package org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade;

import java.util.*;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

/**
 * At least one consultation (Encounter_datetime (from encounter type 35)) registered during the
 * period between “1st Consultation Date(from G1)+20days” and “1st Consultation Date(from
 * G1)+33days”
 */
@Component
public class SecondFollowingEncounterAfterOldestARTStartDateCalculation
    extends AbstractPatientCalculation {
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap calculationResultMap = new CalculationResultMap();

    Location location = (Location) context.getFromCache("location");

    Date endDate = (Date) context.getFromCache("onOrBefore");

    Integer lowerBoundary = (Integer) parameterValues.get("lowerBoundary");
    Integer upperBoundary = (Integer) parameterValues.get("upperBoundary");

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EPTSCalculationService eptsCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    PatientCalculation patientCalculation =
        Context.getRegisteredComponents(EncounterAfterOldestARTStartDateCalculation.class).get(0);

    CalculationResultMap previousApssEncountersMap =
        calculate(patientCalculation, cohort, parameterValues, context);

    CalculationResultMap apssEncountersMap =
        eptsCalculationService.allEncounters(
            Arrays.asList(hivMetadata.getPrevencaoPositivaSeguimentoEncounterType()),
            cohort,
            location,
            null,
            endDate,
            context);

    for (Integer patientId : cohort) {

      CalculationResult calculationResult = previousApssEncountersMap.get(patientId);
      if (calculationResult != null) {
        Date previousApssEncounter = (Date) calculationResult.getValue();

        ListResult listResult = (ListResult) apssEncountersMap.get(patientId);
        List<Encounter> appEncounters = EptsCalculationUtils.extractResultValues(listResult);

        if (previousApssEncounter != null) {

          Date lower = EptsCalculationUtils.addDays(previousApssEncounter, lowerBoundary);

          Date upper = EptsCalculationUtils.addDays(previousApssEncounter, upperBoundary);

          Date date = evaluateConsultation(appEncounters, lower, upper);

          if (date != null) {
            calculationResultMap.put(patientId, new SimpleResult(date, this));
          }
        }
      }
    }
    return calculationResultMap;
  }

  private Date evaluateConsultation(List<Encounter> appEncounters, Date lower, Date upper) {

    for (Encounter e : appEncounters) {
      if (e.getEncounterDatetime().compareTo(lower) > 0
          && e.getEncounterDatetime().compareTo(upper) <= 0) {
        return e.getEncounterDatetime();
      }
    }
    return null;
  }
}
