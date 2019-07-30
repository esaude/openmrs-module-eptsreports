package org.openmrs.module.eptsreports.reporting.calculation.dsd;

import java.util.*;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
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

    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    Location location = (Location) context.getFromCache("location");
    CalculationResultMap map = new CalculationResultMap();
    Concept concept = (Concept) parameterValues.get("conceptId");
    Integer lowerBound = (Integer) parameterValues.get("lowerBound");
    Integer upperBound = (Integer) parameterValues.get("upperBound");
    List<EncounterType> encounterTypes =
        (List<EncounterType>) parameterValues.get("encounterTypes");

    // Step 1: Search for next drug pick up from list of allObs
    CalculationResultMap lastEncounterMap =
        ePTSCalculationService.getEncounter(
            encounterTypes, TimeQualifier.LAST, cohort, location, onOrBefore, context);

    // Step 2: Search for last drug pick from list of allObs
    CalculationResultMap lastReturnVisitObs =
        ePTSCalculationService.getObs(
            concept,
            encounterTypes,
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.LAST,
            null,
            context);

    for (Integer pId : cohort) {
      boolean scheduled = false;
      Obs lastReturnVisit = EptsCalculationUtils.resultForPatient(lastReturnVisitObs, pId);
      Encounter lastEncounter = EptsCalculationUtils.resultForPatient(lastEncounterMap, pId);

      // Step 3: compare against boundaries
      if (lastEncounter != null && lastReturnVisit != null) {

        Date lowerBoundary =
            EptsCalculationUtils.addDays(lastEncounter.getEncounterDatetime(), lowerBound);
        Date upperBoundary =
            EptsCalculationUtils.addDays(lastEncounter.getEncounterDatetime(), upperBound);

        if (lastReturnVisit.getValueDate().compareTo(lowerBoundary) >= 0
            && lastReturnVisit.getValueDate().compareTo(upperBoundary) <= 0) {
          scheduled = true;
        }
      }

      map.put(pId, new BooleanResult(scheduled, this));
    }
    return map;
  }
}
