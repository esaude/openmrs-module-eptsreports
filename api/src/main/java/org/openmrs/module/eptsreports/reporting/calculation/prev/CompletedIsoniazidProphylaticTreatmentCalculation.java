/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.calculation.prev;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Patients that completed isoniazid prophylactic treatment
 *
 * @return a CulculationResultMap
 */
@Component
public class CompletedIsoniazidProphylaticTreatmentCalculation extends AbstractPatientCalculation {

  private static final int COMPLETION_PERIOD_OFFSET = 1;

  private static final int TREATMENT_BEGIN_PERIOD_OFFSET = -6;

  private static final int NUMBER_ISONIAZID_USAGE_TO_CONSIDER_COMPLETED = 5;

  private static final int MONTHS_TO_CHECK_FOR_ISONIAZID_USAGE = 7;

  private static final int MINIMUM_DURATION_IN_DAYS = 173;

  private static final String ON_OR_AFTER = "onOrAfter";

  private static final String ON_OR_BEFORE = "onOrBefore";

  @Autowired private HivMetadata hivMetadata;

  @Autowired private EPTSCalculationService ePTSCalculationService;

  @SuppressWarnings("unused")
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();
    Location location = (Location) context.getFromCache("location");

    Date onOrBefore = (Date) context.getFromCache(ON_OR_BEFORE);
    Date onOrAfter = (Date) context.getFromCache(ON_OR_AFTER);

    if (onOrAfter != null && onOrBefore != null) {
      Date beginPeriodStartDate =
          EptsCalculationUtils.addMonths(onOrAfter, TREATMENT_BEGIN_PERIOD_OFFSET);
      Date beginPeriodEndDate =
          EptsCalculationUtils.addMonths(onOrBefore, TREATMENT_BEGIN_PERIOD_OFFSET);
      Date completionPeriodStartDate =
          EptsCalculationUtils.addMonths(onOrAfter, TREATMENT_BEGIN_PERIOD_OFFSET);
      Date completionPeriodEndDate =
          EptsCalculationUtils.addMonths(onOrBefore, COMPLETION_PERIOD_OFFSET);

      final List<EncounterType> consultationEncounterTypes =
          Arrays.asList(
              hivMetadata.getAdultoSeguimentoEncounterType(),
              hivMetadata.getPediatriaSeguimentoEncounterType(),
              hivMetadata.getMasterCardEncounterType());
      CalculationResultMap startProfilaxiaObservations =
          ePTSCalculationService.firstObs(
              hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept(),
              null,
              location,
              false,
              beginPeriodStartDate,
              beginPeriodEndDate,
              null,
              cohort,
              context);
      CalculationResultMap startDrugsObservations =
          ePTSCalculationService.getObs(
              hivMetadata.getIsoniazidUsageConcept(),
              Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
              cohort,
              Arrays.asList(location),
              Arrays.asList(hivMetadata.getStartDrugs()),
              TimeQualifier.FIRST,
              beginPeriodStartDate,
              onOrAfter,
              context);
      CalculationResultMap endProfilaxiaObservations =
          ePTSCalculationService.lastObs(
              hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept(),
              null,
              location,
              false,
              completionPeriodStartDate,
              completionPeriodEndDate,
              cohort,
              context);
      CalculationResultMap completedDrugsObservations =
          ePTSCalculationService.getObs(
              hivMetadata.getIsoniazidUsageConcept(),
              Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
              cohort,
              Arrays.asList(location),
              Arrays.asList(hivMetadata.getCompletedConcept()),
              TimeQualifier.LAST,
              onOrAfter,
              completionPeriodEndDate,
              context);
      CalculationResultMap isoniazidUsageObservationsList =
          ePTSCalculationService.allObservations(
              hivMetadata.getIsoniazidUsageConcept(),
              Arrays.asList(
                  hivMetadata.getStartDrugs(),
                  hivMetadata.getPatientFoundYesConcept(),
                  hivMetadata.getContinueRegimen()),
              consultationEncounterTypes,
              location,
              cohort,
              context);

      for (Integer patientId : cohort) {
        Obs startProfilaxiaObs =
            EptsCalculationUtils.resultForPatient(startProfilaxiaObservations, patientId);
        Obs startDrugsObs =
            EptsCalculationUtils.resultForPatient(startDrugsObservations, patientId);
        Obs endProfilaxiaObs =
            EptsCalculationUtils.resultForPatient(endProfilaxiaObservations, patientId);
        Obs endDrugsObs =
            EptsCalculationUtils.resultForPatient(completedDrugsObservations, patientId);
        Date startDate = getDateFromObs(startProfilaxiaObs);
        Date endDate = getDateFromObs(endProfilaxiaObs);

        // this is because user can type startdate after enddate
        boolean inconsistent =
            (startDate != null && endDate != null && startDate.compareTo(endDate) > 0)
                || (startDate == null && endDate != null);

        Date[] dateBoundaries =
            evaluateEarliestDateAndLatestDate(startDate, startDrugsObs, endDate, endDrugsObs);

        // if no  earliestDate and  latestDate is picked the the patient is skipped
        if (dateBoundaries[0] == null && dateBoundaries[1] == null) {
          continue;
        }
        // if the  patient has startdate and not  enddate
        if (dateBoundaries[0] != null && dateBoundaries[1] == null) {
          int yesAnswers =
              calculateNumberOfYesAnswers(isoniazidUsageObservationsList, patientId, startDate);
          if (yesAnswers >= NUMBER_ISONIAZID_USAGE_TO_CONSIDER_COMPLETED) {
            map.put(patientId, new BooleanResult(true, this));
          }
          continue;
        }

        int profilaxiaDuration12 =
            Days.daysIn(new Interval(dateBoundaries[0].getTime(), dateBoundaries[1].getTime()))
                .getDays();
        if (profilaxiaDuration12 >= MINIMUM_DURATION_IN_DAYS) {
          map.put(patientId, new BooleanResult(true, this));
          continue;
        } else {
          int yesAnswers =
              calculateNumberOfYesAnswers(isoniazidUsageObservationsList, patientId, startDate);
          if (yesAnswers >= NUMBER_ISONIAZID_USAGE_TO_CONSIDER_COMPLETED) {
            map.put(patientId, new BooleanResult(true, this));
          }
        }
      }
    }
    return map;
  }

  private int calculateNumberOfYesAnswers(
      CalculationResultMap isoniazidUsageObservationsList, Integer patientId, Date startDate) {
    List<Obs> isoniazidUsageObservations =
        EptsCalculationUtils.extractResultValues(
            (ListResult) isoniazidUsageObservationsList.get(patientId));
    int count = 0;
    Date isoniazidUsageEndDate = getIsoniazidUsageEndDate(startDate);
    for (Obs obs : isoniazidUsageObservations) {
      Date date = obs.getObsDatetime();
      if (date.compareTo(startDate) > 0 && date.compareTo(isoniazidUsageEndDate) <= 0) {
        count++;
      }
    }
    return count;
  }

  private Date getIsoniazidUsageEndDate(Date startDate) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    calendar.add(Calendar.MONTH, MONTHS_TO_CHECK_FOR_ISONIAZID_USAGE);
    return calendar.getTime();
  }

  private Date getDateFromObs(Obs obs) {
    if (obs != null) {
      return obs.getValueDatetime();
    }
    return null;
  }

  private Date[] evaluateEarliestDateAndLatestDate(
      Date startDate, Obs startDrugsObs, Date endDate, Obs endDrugsObs) {

    Date earliestDate = null;
    Date latestDate = null;
    /*
     * Assuming that the startdate of the old spec is not null and the startdate of
     * the new spec is null and enddate of the old spec is not null and the enddate
     * of the new spec is null
     */

    if ((startDate != null && startDrugsObs == null) && (endDate != null && endDrugsObs == null)) {
      earliestDate = startDate;
      latestDate = endDate;
    }

    /*
     * Assuming that the startdate of the old spec is not null and the startdate of
     * the new spec is null and enddate of the old spec is null and the enddate of
     * the new spec is not null
     */

    if ((startDate != null && startDrugsObs == null) && (endDate == null && endDrugsObs != null)) {
      earliestDate = startDate;
      latestDate = endDrugsObs.getEncounter().getEncounterDatetime();
    }

    /*
     * Assuming that the startdate of the old spec is null and the startdate of the
     * new spec is not null and enddate of the old spec is not null and the enddate
     * of the new spec is null
     */
    if ((startDate == null && startDrugsObs != null) && (endDate != null && endDrugsObs == null)) {
      earliestDate = startDrugsObs.getEncounter().getEncounterDatetime();
      latestDate = endDate;
    }

    /*
     * Assuming that the startdate of the old spec is null and the startdate of the
     * new spec is not null and enddate of the old spec is null and the enddate of
     * the new spec is not null
     */

    if ((startDate == null && startDrugsObs != null) && (endDate == null && endDrugsObs != null)) {
      earliestDate = startDrugsObs.getEncounter().getEncounterDatetime();
      latestDate = endDrugsObs.getEncounter().getEncounterDatetime();
    }
    /*
     * Assuming that the 2 startdates are not null and the old spec enddate is not
     * null for the startdates we pick the earliest
     */
    if ((startDate != null && startDrugsObs != null) && (endDate != null && endDrugsObs == null)) {
      if (startDate.compareTo(startDrugsObs.getEncounter().getEncounterDatetime()) > 0) {
        earliestDate = startDrugsObs.getEncounter().getEncounterDatetime();
      } else {
        earliestDate = startDate;
      }
      latestDate = endDate;
    }
    /*
     * Assuming that the 2 startdates are not null and the new spec enddate is not
     * null for the startdates we pick the earliest
     */
    if ((startDate != null && startDrugsObs != null) && (endDate == null && endDrugsObs != null)) {
      if (startDate.compareTo(startDrugsObs.getEncounter().getEncounterDatetime()) > 0) {
        earliestDate = startDrugsObs.getEncounter().getEncounterDatetime();
      } else {
        earliestDate = startDate;
      }
      latestDate = endDrugsObs.getEncounter().getEncounterDatetime();
    }
    /*
     * Assuming that the 2 enddates are not null and the old spec startdate is not
     * null for the enddates we pick the latest
     */
    if ((startDate != null && startDrugsObs == null) && (endDate != null && endDrugsObs != null)) {
      if (endDate.compareTo(endDrugsObs.getEncounter().getEncounterDatetime()) > 0) {
        latestDate = endDate;
      } else {
        latestDate = endDrugsObs.getEncounter().getEncounterDatetime();
      }
      earliestDate = startDate;
    }
    /*
     * Assuming that the 2 enddates are not null and the new spec startdate is not
     * null for the enddates we pick the latest
     */
    if ((startDate == null && startDrugsObs != null) && (endDate != null && endDrugsObs != null)) {
      if (endDate.compareTo(endDrugsObs.getEncounter().getEncounterDatetime()) > 0) {
        latestDate = endDate;
      } else {
        latestDate = endDrugsObs.getEncounter().getEncounterDatetime();
      }
      earliestDate = startDrugsObs.getEncounter().getEncounterDatetime();
    }

    /*
     * Assuming that the 2 startdate and the 2 and dates are not null for the
     * startdate we pick the earliest and for the last date we pick the latest
     */
    if ((startDate != null && startDrugsObs != null) && (endDate != null && endDrugsObs != null)) {
      // get the earliest
      if (startDate.compareTo(startDrugsObs.getEncounter().getEncounterDatetime()) > 0) {
        earliestDate = startDrugsObs.getEncounter().getEncounterDatetime();
      } else {
        earliestDate = startDate;
      }
      // //get the latest

      if (endDate.compareTo(endDrugsObs.getEncounter().getEncounterDatetime()) > 0) {
        latestDate = endDate;
      } else {
        latestDate = endDrugsObs.getEncounter().getEncounterDatetime();
      }
    }

    if ((startDate != null && startDrugsObs == null) && (endDate == null && endDrugsObs == null)) {

      return new Date[] {startDate, null};
    }
    if ((startDate == null && startDrugsObs != null) && (endDate == null && endDrugsObs == null)) {

      return new Date[] {startDrugsObs.getEncounter().getEncounterDatetime(), null};
    }

    return new Date[] {earliestDate, latestDate};
  }
}
