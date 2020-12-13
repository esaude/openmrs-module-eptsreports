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
 *             Date” (oldest date from A)+i] and <= “ART Start Date” (oldest date from A)+i+33days
 *         <li>i= i+33days
 *       </ul>
 * </ul>
 */
@Component
public class ConsultationUntilEndDateAfterStartingART extends AbstractPatientCalculation {

  private final int INTERVAL_BETWEEN_APSS_ENCOUNTERS = 33;

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
        Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0);

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
        List<Encounter> wantedEncounters =
            getApssEncounterAfterARTStartDate(appEncounters, artStartDate);

        boolean rangeResult = evaluateEncounterDates(wantedEncounters, endDate);

        if (rangeResult) {
          calculationResultMap.put(patientId, new BooleanResult(rangeResult, this));
        }
      }
    }

    return calculationResultMap;
  }

  List<Encounter> getApssEncounterAfterARTStartDate(
      List<Encounter> appEncounters, Date artStartDate) {
    List<Encounter> wantedEncounters = new ArrayList<>();

    for (Encounter e : appEncounters) {
      if (e.getEncounterDatetime().compareTo(artStartDate) > 0) {
        wantedEncounters.add(e);
      }
    }

    Collections.sort(
        wantedEncounters,
        new Comparator<Encounter>() {
          @Override
          public int compare(Encounter a, Encounter b) {
            return a.getEncounterDatetime().compareTo(b.getEncounterDatetime());
          }
        });

    return wantedEncounters;
  }

  public boolean evaluateEncounterDates(List<Encounter> wantedEncounters, Date endDate) {

    boolean isInterval33Days = false;

    Encounter previous = null;
    for (Encounter current : wantedEncounters) {

      if (current.getEncounterDatetime().compareTo(endDate) <= 0) {

        if (previous == null) {
          previous = current;
          continue;
        }

        int days =
            Days.daysIn(
                    new Interval(
                        previous.getEncounterDatetime().getTime(),
                        current.getEncounterDatetime().getTime()))
                .getDays();

        if (days == INTERVAL_BETWEEN_APSS_ENCOUNTERS) {
          isInterval33Days = true;
          previous = current;
        } else {
          isInterval33Days = false;
          break;
        }
      }
    }
    return isInterval33Days;
  }
}
