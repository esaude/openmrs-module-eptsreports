package org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade;

import java.util.*;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

/**
 * - FIRST consultation (Encounter_datetime (from encounter type 35)) > “ART Start Date” (oldest
 * date from A)+20days and <= “ART Start Date” (oldest date from A)+33days
 */
@Component
public class EncounterAfterOldestARTStartDateCalculation extends AbstractPatientCalculation {
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
        Context.getRegisteredComponents(MohMQInitiatedARTDuringTheInclusionPeriodCalculation.class)
            .get(0);

    CalculationResultMap artStartDates =
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

      Date artStartDate = InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
      ListResult listResult = (ListResult) apssEncountersMap.get(patientId);

      List<Encounter> appEncounters = EptsCalculationUtils.extractResultValues(listResult);

      List<Encounter> orderedApssList = orderEncounterByEncounterDateTime(appEncounters);
      if (orderedApssList.size() > 0) {

        List<Encounter> firstApss = new ArrayList<>();

        firstApss.add(orderedApssList.get(0));

        if (artStartDate != null) {

          Date lower = EptsCalculationUtils.addDays(artStartDate, lowerBoundary);

          Date upper = EptsCalculationUtils.addDays(artStartDate, upperBoundary);

          Date date = evaluateConsultation(firstApss, lower, upper, artStartDate);

          if (date != null) {
            calculationResultMap.put(patientId, new SimpleResult(date, this));
          }
        }
      }
    }
    return calculationResultMap;
  }

  private List<Encounter> orderEncounterByEncounterDateTime(List<Encounter> encounters) {
    Collections.sort(
        encounters,
        new Comparator<Encounter>() {
          @Override
          public int compare(Encounter a, Encounter b) {
            return a.getEncounterDatetime().compareTo(b.getEncounterDatetime());
          }
        });
    return encounters;
  }

  private Date evaluateConsultation(
      List<Encounter> appEncounters, Date lower, Date upper, Date artStartDate) {

    for (Encounter e : appEncounters) {
      if (e.getEncounterDatetime().compareTo(artStartDate) >= 0) {
        if (e.getEncounterDatetime().compareTo(lower) >= 0
            && e.getEncounterDatetime().compareTo(upper) <= 0) {
          return e.getEncounterDatetime();
        }
      }
    }
    return null;
  }
}
