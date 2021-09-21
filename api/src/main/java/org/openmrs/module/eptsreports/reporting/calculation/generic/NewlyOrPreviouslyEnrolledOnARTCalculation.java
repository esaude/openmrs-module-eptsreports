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
package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EPTSMetadataDatetimeQualifier;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Patients Newly Enrolled on ART: The patients from TB_PREV_DENOMINATOR (2 AND 3) who falls under
 * “Patients Newly Enrolled on ART” are patients with the earliest IPT start Date (obtained based in
 * different set of identified fields/sources defined in 3a) within 6 months of the earliest ART
 * start Date (obtained based in different set of identified fields/sources defined in 2a): (Art
 * Start Date minus IPT Start Date <= 6months)
 *
 * <p>Patients Previously Enrolled on ART: The patients from TB_PREV_DENOMINATOR (2 AND 3) who falls
 * under “Patients Previously Enrolled on ART” are patients with the earliest TPI start Date
 * (obtained based in different set of identified fields/sources defined in 3a) within 6 months of
 * the earliest ART start Date (obtained based in different set of identified fields/sources defined
 * in 2a): (Art Start Date minus TPI Start Date > 6months)
 *
 * @return a CulculationResultMap
 */
@Component
public class NewlyOrPreviouslyEnrolledOnARTCalculation extends AbstractPatientCalculation {

  private static final int MINIMUM_DURATION_IN_MONTHS = 6;

  private static final String ON_OR_AFTER = "onOrAfter";

  private static final String ON_OR_BEFORE = "onOrBefore";

  @Autowired private HivMetadata hivMetadata;

  @Autowired private TbMetadata tbMetadata;

  @Autowired private EPTSCalculationService ePTSCalculationService;

  @SuppressWarnings("unused")
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap map = new CalculationResultMap();
    boolean isNewlyEnrolledOnArtSearch =
        getBooleanParameter(parameterValues, "isNewlyEnrolledOnArtSearch");
    Location location = (Location) context.getFromCache("location");
    Date startDate = (Date) parameterValues.get(ON_OR_AFTER);
    Date endDate = (Date) parameterValues.get(ON_OR_BEFORE);

    if (startDate == null) {
      startDate = (Date) context.getFromCache(ON_OR_AFTER);
    }

    if (endDate == null) {
      endDate = (Date) context.getFromCache(ON_OR_BEFORE);
    }

    // Start ART date is always checked against endDate, not endDate - 6m
    parameterValues.put("onOrBefore", addMonths(endDate, 6));
    CalculationResultMap artStartDates =
        calculate(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0),
            cohort,
            parameterValues,
            context);
    CalculationResultMap startProfilaxiaObservations =
        ePTSCalculationService.firstObs(
            hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept(),
            null,
            location,
            false,
            startDate,
            endDate,
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
            startDate,
            endDate,
            context);
    CalculationResultMap firstINHDateMap =
        ePTSCalculationService.getObs(
            tbMetadata.getRegimeTPTConcept(),
            tbMetadata.getRegimeTPTEncounterType(),
            cohort,
            location,
            Arrays.asList(
                tbMetadata.getIsoniazidConcept(), tbMetadata.getIsoniazidePiridoxinaConcept()),
            TimeQualifier.FIRST,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);

    CalculationResultMap anyIsoniazidaPiridoxina =
        ePTSCalculationService.getObs(
            tbMetadata.getRegimeTPTConcept(),
            tbMetadata.getRegimeTPTEncounterType(),
            cohort,
            location,
            Arrays.asList(
                tbMetadata.getIsoniazidConcept(), tbMetadata.getIsoniazidePiridoxinaConcept()),
            TimeQualifier.ANY,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);

    CalculationResultMap anyIsoniazidaPiridoxina2 =
        ePTSCalculationService.getObs(
            hivMetadata.getPatientTreatmentFollowUp(),
            tbMetadata.getRegimeTPTEncounterType(),
            cohort,
            location,
            Arrays.asList(hivMetadata.getStartDrugs(), hivMetadata.getRestartConcept()),
            TimeQualifier.ANY,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);

    CalculationResultMap firstINHDateMap2 =
        ePTSCalculationService.getObs(
            hivMetadata.getPatientTreatmentFollowUp(),
            tbMetadata.getRegimeTPTEncounterType(),
            cohort,
            location,
            Arrays.asList(hivMetadata.getContinueRegimenConcept()),
            TimeQualifier.FIRST,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);
    CalculationResultMap notInINHDateMap =
        ePTSCalculationService.getObs(
            tbMetadata.getRegimeTPTConcept(),
            tbMetadata.getRegimeTPTEncounterType(),
            cohort,
            location,
            Arrays.asList(
                tbMetadata.getIsoniazidConcept(), tbMetadata.getIsoniazidePiridoxinaConcept()),
            TimeQualifier.ANY,
            DateUtils.addMonths(startDate, -7),
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);
    CalculationResultMap notInINHDateMap2 =
        ePTSCalculationService.getObs(
            tbMetadata.getRegimeTPTConcept(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            cohort,
            location,
            Arrays.asList(hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept()),
            TimeQualifier.ANY,
            DateUtils.addMonths(startDate, -7),
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);
    CalculationResultMap notInINHDateMap3 =
        ePTSCalculationService.getObs(
            tbMetadata.getRegimeTPTConcept(),
            hivMetadata.getPediatriaSeguimentoEncounterType(),
            cohort,
            location,
            Arrays.asList(hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept()),
            TimeQualifier.ANY,
            DateUtils.addMonths(startDate, -7),
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);

    CalculationResultMap notInINHDateMap4 =
        ePTSCalculationService.getObs(
            tbMetadata.getRegimeTPTConcept(),
            hivMetadata.getMasterCardEncounterType(),
            cohort,
            location,
            Arrays.asList(hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept()),
            TimeQualifier.ANY,
            DateUtils.addMonths(startDate, -7),
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);

    CalculationResultMap first3HPDateMap =
        ePTSCalculationService.getObs(
            tbMetadata.getTreatmentPrescribedConcept(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            cohort,
            location,
            Arrays.asList(tbMetadata.get3HPConcept()),
            TimeQualifier.FIRST,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);
    CalculationResultMap notIn3HPDateMap =
        ePTSCalculationService.getObs(
            tbMetadata.getTreatmentPrescribedConcept(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            cohort,
            location,
            Arrays.asList(tbMetadata.get3HPConcept()),
            TimeQualifier.ANY,
            DateUtils.addMonths(startDate, -4),
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);

    CalculationResultMap notIn3HPDateMap2 =
        ePTSCalculationService.getObs(
            tbMetadata.getRegimeTPTConcept(),
            tbMetadata.getRegimeTPTEncounterType(),
            cohort,
            location,
            Arrays.asList(tbMetadata.get3HPConcept(), tbMetadata.get3HPPiridoxinaConcept()),
            TimeQualifier.ANY,
            DateUtils.addMonths(startDate, -4),
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);
    CalculationResultMap in3HPor3HPPiridoxinaMap =
        ePTSCalculationService.getObs(
            tbMetadata.getRegimeTPTConcept(),
            tbMetadata.getRegimeTPTEncounterType(),
            cohort,
            location,
            Arrays.asList(tbMetadata.get3HPConcept(), tbMetadata.get3HPPiridoxinaConcept()),
            TimeQualifier.ANY,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);
    CalculationResultMap first3HPOr3HPPlusPiridoxinaDateMap =
        ePTSCalculationService.getObs(
            tbMetadata.getTreatmentPrescribedConcept(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            cohort,
            location,
            Arrays.asList(tbMetadata.get3HPConcept(), tbMetadata.get3HPPiridoxinaConcept()),
            TimeQualifier.FIRST,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);
    CalculationResultMap notIn3HPOr3HPPlusPiridoxinaDateMap =
        ePTSCalculationService.getObs(
            tbMetadata.getTreatmentPrescribedConcept(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            cohort,
            location,
            Arrays.asList(tbMetadata.get3HPConcept(), tbMetadata.get3HPPiridoxinaConcept()),
            TimeQualifier.ANY,
            DateUtils.addMonths(startDate, -7),
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);

    if (endDate != null) {
      for (Integer patientId : cohort) {
        Date artStartDate =
            InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
        Obs seguimentoOrFichaResumo =
            EptsCalculationUtils.resultForPatient(startProfilaxiaObservations, patientId);
        Obs fichaClinicaMasterCardStartDrugsObs =
            EptsCalculationUtils.resultForPatient(startDrugsObservations, patientId);
        Obs firstINHDateObs = EptsCalculationUtils.obsResultForPatient(firstINHDateMap, patientId);
        Obs firstINHDateObs2 =
            EptsCalculationUtils.obsResultForPatient(firstINHDateMap2, patientId);
        List<Obs> anyIsoniazida = getObsListFromResultMap(anyIsoniazidaPiridoxina, patientId);
        List<Obs> anyIsoniazida2 = getObsListFromResultMap(anyIsoniazidaPiridoxina2, patientId);
        List<Obs> in3HPor3HPPiridoxina =
            getObsListFromResultMap(in3HPor3HPPiridoxinaMap, patientId);
        List<Obs> notInINHDateObs = getObsListFromResultMap(notInINHDateMap, patientId);
        List<Obs> notInINHDateObs2 = getObsListFromResultMap(notInINHDateMap2, patientId);
        List<Obs> notInINHDateObs3 = getObsListFromResultMap(notInINHDateMap3, patientId);
        List<Obs> notInINHDateObs4 = getObsListFromResultMap(notInINHDateMap4, patientId);
        Obs first3HPDateObs = EptsCalculationUtils.obsResultForPatient(first3HPDateMap, patientId);
        List<Obs> notIn3HPDateObs = getObsListFromResultMap(notIn3HPDateMap, patientId);
        List<Obs> notIn3HPDateObs2 = getObsListFromResultMap(notIn3HPDateMap2, patientId);
        Obs first3HPOr3HPPlusPiridoxinaDateObs =
            EptsCalculationUtils.obsResultForPatient(first3HPOr3HPPlusPiridoxinaDateMap, patientId);
        List<Obs> notIn3HPOr3HPPlusPiridoxinaDateObs =
            getObsListFromResultMap(notIn3HPOr3HPPlusPiridoxinaDateMap, patientId);

        if ((seguimentoOrFichaResumo == null
                && fichaClinicaMasterCardStartDrugsObs == null
                && firstINHDateObs == null
                && anyIsoniazida == null
                && anyIsoniazida2 == null
                && !anyIsoniazida2.isEmpty()
                && firstINHDateObs2 == null
                && first3HPDateObs == null
                && in3HPor3HPPiridoxina == null
                && first3HPOr3HPPlusPiridoxinaDateObs == null)
            || artStartDate == null) {
          continue;
        }
        Obs anyIsoniazida2a = null;
        if (anyIsoniazida2 != null && !anyIsoniazida2.isEmpty() && anyIsoniazida2.size() > 0) {
          anyIsoniazida2a = anyIsoniazida2.get(0);
        }
        Obs anyIsoniazidaaa = null;
        if (anyIsoniazida != null && !anyIsoniazida.isEmpty() && anyIsoniazida.size() > 0) {
          anyIsoniazidaaa = anyIsoniazida.get(0);
        }

        DateTime artStartDateTime = new DateTime(artStartDate.getTime());
        Date EarliestIptStartDate =
            getEarliestIptStartDate(
                Arrays.asList(
                    seguimentoOrFichaResumo,
                    fichaClinicaMasterCardStartDrugsObs,
                    anyIsoniazidaaa,
                    anyIsoniazida2a,
                    this.getObsNotInMonthsPriorTo(firstINHDateObs, notInINHDateObs, -7),
                    this.getObsNotInMonthsPriorTo(firstINHDateObs, notInINHDateObs2, -7),
                    this.getObsNotInMonthsPriorTo(firstINHDateObs, notInINHDateObs3, -7),
                    this.getObsNotInMonthsPriorTo(firstINHDateObs, notInINHDateObs4, -7),
                    this.getObsNotInMonthsPriorTo(firstINHDateObs2, notInINHDateObs, -7),
                    this.getObsNotInMonthsPriorTo(firstINHDateObs2, notInINHDateObs2, -7),
                    this.getObsNotInMonthsPriorTo(firstINHDateObs2, notInINHDateObs3, -7),
                    this.getObsNotInMonthsPriorTo(firstINHDateObs2, notInINHDateObs4, -7),
                    this.getObsNotInMonthsPriorTo(first3HPDateObs, notIn3HPDateObs, -4),
                    this.getObsNotInMonthsPriorTo(first3HPDateObs, notIn3HPDateObs2, -4),
                    this.getObsNotInMonthsPriorTo(
                        first3HPOr3HPPlusPiridoxinaDateObs, notIn3HPDateObs, -4),
                    this.getObsNotInMonthsPriorTo(
                        first3HPOr3HPPlusPiridoxinaDateObs,
                        notIn3HPOr3HPPlusPiridoxinaDateObs,
                        -4)));
        if (EarliestIptStartDate != null) {
          DateTime iptStartDateTime = new DateTime(EarliestIptStartDate.getTime());
          boolean isDiffMoreThanSix =
              isDateDiffGreaterThanSixMonths(artStartDateTime, iptStartDateTime);
          if (artStartDate != null
              && artStartDate.compareTo(endDate) <= 0
              && isDiffMoreThanSix == false
              && isNewlyEnrolledOnArtSearch == true) {
            map.put(patientId, new BooleanResult(true, this));
          }
          if (artStartDate != null
              && artStartDate.compareTo(endDate) <= 0
              && isDiffMoreThanSix == true
              && isNewlyEnrolledOnArtSearch == false) {
            map.put(patientId, new BooleanResult(true, this));
          }
        }
        if (firstINHDateObs != null
            && firstINHDateObs2 != null
            && firstINHDateObs.getValueDatetime() != null
            && firstINHDateObs.getValueDatetime().compareTo(DateUtils.addMonths(startDate, -6)) <= 0
            && firstINHDateObs.getValueDatetime().compareTo(DateUtils.addMonths(endDate, -6))
                <= 0) {
          map.put(patientId, new BooleanResult(true, this));
        }

        if (anyIsoniazidaaa != null
            && anyIsoniazida2 != null
            && !anyIsoniazida2.isEmpty()
            && anyIsoniazidaaa.getValueDatetime() != null
            && anyIsoniazidaaa.getValueDatetime().compareTo(DateUtils.addMonths(startDate, -6)) <= 0
            && anyIsoniazidaaa.getValueDatetime().compareTo(DateUtils.addMonths(endDate, -6))
                <= 0) {
          map.put(patientId, new BooleanResult(true, this));
        }
      }
      return map;
    } else {
      throw new IllegalArgumentException(String.format("Parameter %s must be set", ON_OR_BEFORE));
    }
  }

  /**
   * Gets the earliest treatment start date by comparing the Obs in the given list
   *
   * @param List<Obs>
   * @return Date
   */
  private Date getEarliestIptStartDate(List<Obs> obs) {

    int i = 0;
    Date date = null;
    List<Date> dates = new ArrayList<>();

    for (Obs o : obs) {
      if (i == 0 && o != null) dates.add(o.getValueDatetime());
      else if (i > 0 && o != null) dates.add(o.getEncounter().getEncounterDatetime());
      i++;
    }
    if (!dates.isEmpty()) {
      date = Collections.min(dates);
    }

    return date;
  }
  /**
   * Checks if the difference between ART start date and IPT start date is greater than six months,
   * considering days if the difference in months is equal to 6 months
   *
   * @param artStartDateTime The ART start date
   * @param iptStartDateTime The IPT start date
   * @return true if the difference is greater to six months, false otherwise.
   */
  public boolean isDateDiffGreaterThanSixMonths(
      DateTime artStartDateTime, DateTime iptStartDateTime) {
    int artMinusIptStartDate =
        Months.monthsBetween(new DateTime(artStartDateTime), new DateTime(iptStartDateTime))
            .getMonths();
    if (artMinusIptStartDate > MINIMUM_DURATION_IN_MONTHS) {
      return true;
    }
    if (artMinusIptStartDate
        == MINIMUM_DURATION_IN_MONTHS) { // Check if there are some days after the six months (eg. 6
      // Months and 4 days)
      DateTime newEnd = iptStartDateTime.minusMonths(artMinusIptStartDate);
      int days = Days.daysBetween(artStartDateTime, newEnd).getDays();
      if (days > 0) {
        return true;
      }
    }
    return false;
  }
  /**
   * Adds a number of months to the passed-in date
   *
   * @param date the date to increment
   * @param monthsToAdd the number of months to add
   * @return date incremented by {monthsToAdd} months
   */
  public static Date addMonths(Date date, int monthsToAdd) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.MONTH, monthsToAdd);
    return cal.getTime();
  }

  private boolean getBooleanParameter(Map<String, Object> parameterValues, String parameterName) {
    Boolean parameterValue = null;
    if (parameterValues != null) {
      parameterValue = (Boolean) parameterValues.get(parameterName);
    }
    if (parameterValue == null) {
      parameterValue = true;
    }
    return parameterValue;
  }

  private List<Obs> getObsListFromResultMap(CalculationResultMap map, Integer pid) {
    ListResult listResult = (ListResult) map.get(pid);
    List<Obs> obs = EptsCalculationUtils.extractResultValues(listResult);
    obs.removeAll(Collections.singleton(null));
    return obs;
  }

  private Obs getObsNotInMonthsPriorTo(Obs firstDate, List<Obs> notIn, int month) {
    Obs obs = firstDate;

    if (firstDate != null && notIn != null) {
      for (Obs o : notIn) {
        if (o.getEncounter()
                    .getEncounterDatetime()
                    .compareTo(
                        DateUtils.addMonths(firstDate.getEncounter().getEncounterDatetime(), month))
                >= 0
            && o.getEncounter()
                    .getEncounterDatetime()
                    .compareTo(firstDate.getEncounter().getEncounterDatetime())
                < 0) {
          obs = null;
          break;
        }
      }
    }

    return obs;
  }
}
