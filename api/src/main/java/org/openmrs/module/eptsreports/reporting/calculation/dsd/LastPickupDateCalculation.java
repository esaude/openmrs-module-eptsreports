package org.openmrs.module.eptsreports.reporting.calculation.dsd;

import java.util.*;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

@Component
public class LastPickupDateCalculation extends AbstractPatientCalculation {
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EncounterType phamarcyEncounterType = hivMetadata.getARVPharmaciaEncounterType();
    Location location = (Location) context.getFromCache("location");
    CalculationResultMap map = new CalculationResultMap();

    CalculationResultMap allObs =
        ePTSCalculationService.getObs(
            hivMetadata.getReturnVisitDateForArvDrugConcept(),
            Arrays.asList(phamarcyEncounterType),
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
      if (obsList.size() >= 2) {
        Obs lastObs = obsList.get(obsList.size());
        Obs prevObs = obsList.get(obsList.size() - 1);

        if (lastObs.getValueDatetime() != null) {
          nextAppointmentDate = lastObs.getObsDatetime();
        }
        if (prevObs.getValueDatetime() != null) {
          previousAppointmentDate = prevObs.getObsDatetime();
        }

        if (nextAppointmentDate != null && previousAppointmentDate != null) {
          Date previousPlus83Days = EptsCalculationUtils.addDays(previousAppointmentDate, 83);
          Date previousPlus97Days = EptsCalculationUtils.addDays(previousAppointmentDate, 97);

          if (nextAppointmentDate.compareTo(previousPlus83Days) >= 0
              && nextAppointmentDate.compareTo(previousPlus97Days) <= 0) {
            scheduled = true;
          }
        }
      }
      map.put(pId, new BooleanResult(scheduled, this));
    }
    return null;
  }
}
