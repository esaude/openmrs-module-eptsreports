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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
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
        getFirstINHDateOrFirst3HPDate(
            tbMetadata.getRegimeTPTEncounterType(),
            tbMetadata.getRegimeTPTConcept(),
            Arrays.asList(
                tbMetadata.getIsoniazidConcept(), tbMetadata.getIsoniazidePiridoxinaConcept()),
            false,
            cohort,
            context);
    CalculationResultMap notInFirstINHDateMap =
        getFirstINHDateOrFirst3HPDate(
            tbMetadata.getRegimeTPTEncounterType(),
            tbMetadata.getRegimeTPTConcept(),
            Arrays.asList(
                tbMetadata.getIsoniazidConcept(), tbMetadata.getIsoniazidePiridoxinaConcept()),
            true,
            cohort,
            context);
    CalculationResultMap firstDtINHDateMap =
        getFirstINHDateOrFirst3HPDate(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getTreatmentPrescribedConcept(),
            Arrays.asList(tbMetadata.getDtINHConcept()),
            false,
            cohort,
            context);
    CalculationResultMap notInDtINHDateMap =
        getFirstINHDateOrFirst3HPDate(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getTreatmentPrescribedConcept(),
            Arrays.asList(tbMetadata.getDtINHConcept()),
            true,
            cohort,
            context);
    CalculationResultMap first3HPDateMap =
        getFirstINHDateOrFirst3HPDate(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getTreatmentPrescribedConcept(),
            Arrays.asList(tbMetadata.get3HPConcept()),
            false,
            cohort,
            context);
    CalculationResultMap notIn3HPDateMap =
        getFirstINHDateOrFirst3HPDate(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getTreatmentPrescribedConcept(),
            Arrays.asList(tbMetadata.get3HPConcept()),
            true,
            cohort,
            context);
    CalculationResultMap first3HPOr3HPPlusPiridoxinaDateMap =
        getFirstINHDateOrFirst3HPDate(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getTreatmentPrescribedConcept(),
            Arrays.asList(tbMetadata.get3HPConcept()),
            false,
            cohort,
            context);
    CalculationResultMap notIn3HPOr3HPPlusPiridoxinaDateMap =
        getFirstINHDateOrFirst3HPDate(
            tbMetadata.getRegimeTPTEncounterType(),
            tbMetadata.getRegimeTPTConcept(),
            Arrays.asList(tbMetadata.get3HPConcept(), tbMetadata.get3HPPiridoxinaConcept()),
            true,
            cohort,
            context);

    if (endDate != null) {
      for (Integer patientId : cohort) {
        Date artStartDate =
            InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
        Obs seguimentoOrFichaResumo =
            EptsCalculationUtils.resultForPatient(startProfilaxiaObservations, patientId);
        Obs fichaClinicaMasterCardStartDrugsObs =
            EptsCalculationUtils.resultForPatient(startDrugsObservations, patientId);
        SimpleResult firstINHDateResult = (SimpleResult) firstINHDateMap.get(patientId);
        SimpleResult notInINHDateResult = (SimpleResult) notInFirstINHDateMap.get(patientId);
        SimpleResult firstDtINHDateResult = (SimpleResult) firstDtINHDateMap.get(patientId);
        SimpleResult notInDtINHDateResult = (SimpleResult) notInDtINHDateMap.get(patientId);
        SimpleResult first3HPDateResult = (SimpleResult) first3HPDateMap.get(patientId);
        SimpleResult notIn3HPDateResult = (SimpleResult) notIn3HPDateMap.get(patientId);
        SimpleResult first3HPOr3HPPlusPiridoxinaDateResult =
            (SimpleResult) first3HPOr3HPPlusPiridoxinaDateMap.get(patientId);
        SimpleResult notIn3HPOr3HPPlusPiridoxinaDateResult =
            (SimpleResult) notIn3HPOr3HPPlusPiridoxinaDateMap.get(patientId);

        Date firstINHDate = addOrSubtractMonths(firstINHDateResult, notInINHDateResult, -7);
        Date firstDtINHDate = addOrSubtractMonths(firstDtINHDateResult, notInDtINHDateResult, -7);
        Date first3HPDate = addOrSubtractMonths(first3HPDateResult, notIn3HPDateResult, -4);
        Date first3HPOr3HPPlusPiridoxinaDate =
            addOrSubtractMonths(
                first3HPOr3HPPlusPiridoxinaDateResult, notIn3HPOr3HPPlusPiridoxinaDateResult, -4);

        if ((seguimentoOrFichaResumo == null
                && fichaClinicaMasterCardStartDrugsObs == null
                && firstINHDate == null
                && firstDtINHDate == null
                && first3HPDate == null
                && first3HPOr3HPPlusPiridoxinaDate == null)
            || artStartDate == null) {
          continue;
        }

        DateTime artStartDateTime = new DateTime(artStartDate.getTime());
        DateTime iptStartDateTime =
            new DateTime(
                getEarliestIptStartDate(
                        seguimentoOrFichaResumo,
                        fichaClinicaMasterCardStartDrugsObs,
                        firstINHDate,
                        firstDtINHDate,
                        first3HPDate,
                        first3HPOr3HPPlusPiridoxinaDate)
                    .getTime());
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
      return map;
    } else {
      throw new IllegalArgumentException(String.format("Parameter %s must be set", ON_OR_BEFORE));
    }
  }

  private Date getDateFromObs(Obs obs) {
    if (obs != null) {
      return obs.getValueDatetime();
    }
    return null;
  }
  /**
   * Gets the earliest treatment start date by comparing the drugs start date obs from Seguimento
   * (adults and children)” or “Ficha Resumo” and “Ficha Clinica-MasterCard”
   *
   * @param seguimentoOrFichaResumoDate The earliest drug start date obs from Seguimento (adults and
   *     children)” or “Ficha Resumo”
   * @param fichaClinicaMasterCardDate The earliest drug start date obs from Ficha
   *     Clinica-MasterCard
   * @return
   */
  private Date getEarliestIptStartDate(
      Obs seguimentoOrFichaResumoDate,
      Obs fichaClinicaMasterCardDate,
      Date firstINHDate,
      Date firstDtINHDate,
      Date first3HPDate,
      Date first3HPOr3HPPlusPiridoxinaDate) {
    if (seguimentoOrFichaResumoDate != null
        && fichaClinicaMasterCardDate != null
        && firstINHDate != null
        && firstDtINHDate != null
        && first3HPDate != null
        && first3HPOr3HPPlusPiridoxinaDate != null) {
      List<Date> dates =
          Arrays.asList(
              getDateFromObs(seguimentoOrFichaResumoDate),
              fichaClinicaMasterCardDate.getObsDatetime(),
              firstINHDate,
              firstDtINHDate,
              first3HPDate,
              first3HPOr3HPPlusPiridoxinaDate);
      return Collections.min(dates);
    } else {
      if (getDateFromObs(seguimentoOrFichaResumoDate) != null) {
        return getDateFromObs(seguimentoOrFichaResumoDate);
      } else if (firstINHDate != null) {
        return firstINHDate;
      } else if (firstDtINHDate != null) {
        return firstDtINHDate;
      } else if (first3HPDate != null) {
        return first3HPDate;
      } else if (first3HPOr3HPPlusPiridoxinaDate != null) {
        return first3HPOr3HPPlusPiridoxinaDate;
      } else {
        return fichaClinicaMasterCardDate.getObsDatetime();
      }
    }
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

  private Date addOrSubtractMonths(
      SimpleResult firstDateResult, SimpleResult notInDateResult, int months) {
    if (firstDateResult != null && notInDateResult != null) {
      Date firstDate = (Date) firstDateResult.getValue();
      Date notInDate = (Date) notInDateResult.getValue();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(firstDate);
      calendar.add(Calendar.MONTH, months);
      Date addedOrSubtractedMonths = calendar.getTime();

      if (notInDate.getTime() >= addedOrSubtractedMonths.getTime()
          && notInDate.getTime() < firstDate.getTime()) {
        return firstDate;
      }
    }
    return null;
  }

  private CalculationResultMap getFirstINHDateOrFirst3HPDate(
      EncounterType encounterType,
      Concept question,
      List<Concept> answers,
      Boolean notInQuery,
      Collection<Integer> cohort,
      PatientCalculationContext context) {

    SqlPatientDataDefinition def = new SqlPatientDataDefinition();
    def.addParameter(new Parameter("startDate", "onOrAfter", Date.class));
    def.addParameter(new Parameter("endDate", "onOrBefore", Date.class));
    def.addParameter(new Parameter("location", "location", Location.class));

    List<Integer> answerIds = new ArrayList<>();

    for (Concept concept : answers) {
      answerIds.add(concept.getConceptId());
    }

    String sql =
        "   SELECT  p.patient_id, MIN(e.encounter_datetime) AS value_datetime "
            + " FROM    patient p  "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + " WHERE   p.voided = 0  "
            + "     AND e.voided = 0  "
            + "     AND o.voided = 0  "
            + "     AND e.location_id = :location "
            + "     AND e.encounter_type = %d "
            + "     AND o.concept_id = %d ";
    if (answerIds.size() == 1) {
      sql += "      AND o.value_coded IN (%d) ";
    } else if (answerIds.size() == 2) {
      sql += "      AND o.value_coded IN (%d, %d) ";
    }
    if (!notInQuery) {
      sql +=
          "           AND e.encounter_datetime >= :startDate "
              + "     AND e.encounter_datetime <= :endDate ";
    }
    sql += "    GROUP BY p.patient_id";

    if (answerIds.size() == 1) {
      def.setSql(
          String.format(
              sql, encounterType.getEncounterTypeId(), question.getConceptId(), answerIds.get(0)));
    } else if (answerIds.size() == 2) {
      def.setSql(
          String.format(
              sql,
              encounterType.getEncounterTypeId(),
              question.getConceptId(),
              answerIds.get(0),
              answerIds.get(1)));
    }

    Map<String, Object> params = new HashMap<>();
    params.put("location", context.getFromCache("location"));
    params.put("onOrBefore", context.getFromCache("onOrBefore"));
    params.put("onOrAfter", context.getFromCache("onOrAfter"));
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, params, null, context);
  }
}
