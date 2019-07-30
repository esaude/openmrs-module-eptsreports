package org.openmrs.module.eptsreports.reporting.calculation.dsd;

import java.util.*;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

@Component
public class NextAndPrevDatesCalculation extends AbstractPatientCalculation {
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    //    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    Location location = (Location) context.getFromCache("location");
    CalculationResultMap map = new CalculationResultMap();
    Concept concept = (Concept) parameterValues.get("conceptId");
    Integer lowerBound = (Integer) parameterValues.get("lowerBound");
    Integer upperBound = (Integer) parameterValues.get("upperBound");

    List<EncounterType> encounterTypes =
        (List<EncounterType>) parameterValues.get("encounterTypes");

    CalculationResultMap allObs =
        ePTSCalculationService.getObs(
            concept,
            encounterTypes,
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    for (Integer pId : cohort) {
      Date nextAppointmentDate = null;
      Date previousAppointmentDate = null;
      boolean scheduled = false;

      ListResult results = (ListResult) allObs.get(pId);

      List<Obs> obsList = EptsCalculationUtils.extractResultValues(results);
      if (results != null && obsList.size() >= 2) {
        Obs lastObs = obsList.get(obsList.size() - 1);
        Obs prevObs = obsList.get(obsList.size() - 2);

        if (lastObs != null && lastObs.getValueDatetime() != null) {
          nextAppointmentDate = lastObs.getObsDatetime();
        }
        if (prevObs != null && prevObs.getValueDatetime() != null) {
          previousAppointmentDate = prevObs.getObsDatetime();
        }
        Date previousPlusLowerBoundDays = null;
        Date previousPlusUpperBoundDays = null;

        if (nextAppointmentDate != null && previousAppointmentDate != null) {

          previousPlusLowerBoundDays =
              EptsCalculationUtils.addDays(previousAppointmentDate, lowerBound);
          previousPlusUpperBoundDays =
              EptsCalculationUtils.addDays(previousAppointmentDate, upperBound);
        }
        if (nextAppointmentDate != null
            && previousPlusLowerBoundDays != null
            && nextAppointmentDate.compareTo(previousPlusLowerBoundDays) >= 0
            && nextAppointmentDate.compareTo(previousPlusUpperBoundDays) <= 0) {
          scheduled = true;
        }
      }
      map.put(pId, new BooleanResult(scheduled, this));
    }
    return map;
  }
}
