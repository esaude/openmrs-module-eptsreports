package org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade;

import java.util.*;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

/**
 * Select all patients who have monthly APSS&PP(encounter type 35) consultation (until the end of
 * the revision period) after Starting ART(The oldest date from A) as following pseudo-code:
 *
 * <ul>
 *   <li>For ( i=0; i<(days between “ART Start Date” and endDateRevision; i++)
 *       <ul>
 *         <li>Existence of consultation (Encounter_datetime (from encounter type 35)) > [“ART Start
 *             Date” (oldest date from A)+i] and <= “ART Start Date” (oldest date from A)+i+99days
 *         <li>i= i+99days
 *       </ul>
 * </ul>
 */
@Component
public class ConsultationUntilEndDateAfterStartingART extends AbstractPatientCalculation {

  private final int DAYS_FROM_ART_START_DATE_TO_APSS_ENCOUNTER = 99;
  private final int MIN_NUMBER_OF_APSS_CONSULTATIONS = 3;

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap calculationResultMap = new CalculationResultMap();

    Location location = (Location) context.getFromCache("location");

    Date endDate = (Date) context.getFromCache("onOrBefore");

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

      ListResult listResult = (ListResult) apssEncountersMap.get(patientId);
      List<Encounter> appEncounters = EptsCalculationUtils.extractResultValues(listResult);

      Date artStartDate = InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
      if (artStartDate != null) {
        boolean atLeast3Encounters =
            checkApssEncountersBetweenARTStartDateAndEndDate(appEncounters, artStartDate, endDate);

        calculationResultMap.put(patientId, new BooleanResult(atLeast3Encounters, this));
      }
    }

    return calculationResultMap;
  }

  Boolean checkApssEncountersBetweenARTStartDateAndEndDate(
      List<Encounter> appEncounters, Date artStartDate, Date endDate) {
    boolean atLeast3Encounters = false;
    int count = 0;

    for (Encounter e : appEncounters) {
      if (e.getEncounterDatetime().compareTo(artStartDate) > 0) {
        if (e.getEncounterDatetime().compareTo(endDate) < 0) {
          int daysAfterArt =
              Days.daysIn(new Interval(artStartDate.getTime(), e.getEncounterDatetime().getTime()))
                  .getDays();

          if (daysAfterArt <= DAYS_FROM_ART_START_DATE_TO_APSS_ENCOUNTER) {
            count += 1;

            if (count == MIN_NUMBER_OF_APSS_CONSULTATIONS) {
              atLeast3Encounters = true;
              break;
            }
          }
        }
      }
    }

    return atLeast3Encounters;
  }
}
