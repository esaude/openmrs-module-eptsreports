/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.CodedObsOnFirstOrSecondEncounterCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.definition.ResumoMensalTransferredOutCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.ResumoMensalQueries;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterWithCodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumoMensalCohortQueries {

  private HivMetadata hivMetadata;
  private TbMetadata tbMetadata;
  private GenericCohortQueries genericCohortQueries;

  @Autowired
  public ResumoMensalCohortQueries(
      HivMetadata hivMetadata, TbMetadata tbMetadata, GenericCohortQueries genericCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
    this.genericCohortQueries = genericCohortQueries;
  }

  /** A1 Number of patients who initiated Pre-TARV at this HF by end of previous month */
  public CohortDefinition getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who initiated Pre-TARV at this HF by end of previous month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("A1I");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.setQuery(
        ResumoMensalQueries.getAllPatientsWithPreArtStartDateLessThanReportingStartDate(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getPreArtStartDate().getConceptId()));

    cd.addSearch("A1I", map(sqlCohortDefinition, "startDate=${startDate},location=${location}"));
    cd.addSearch(
        "A1II",
        map(
            getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthA1(),
            "onOrBefore=${startDate},location=${location}"));
    cd.addSearch(
        "A1III",
        map(
            getAllPatientsEnrolledInPreArtProgramWithDateEnrolledLessThanStartDateA1(),
            "startDate=${startDate},location=${location}"));
    cd.addSearch(
        "A1IV",
        map(
            getAllPatientsRegisteredInEncounterType5or7WithEncounterDatetimeLessThanStartDateA1(),
            "onOrBefore=${startDate},locationList=${location}"));

    cd.setCompositionString("(A1I OR A1III OR A1IV) AND NOT A1II");

    return cd;
  }

  /**
   * A2 Number of patients who initiated Pre-TARV at this HF during the current month
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who initiated Pre-TARV at this HF during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("A2i");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.setQuery(
            ResumoMensalQueries.getPatientsWhoInitiatedPreArtDuringCurrentMonthWithConditions(
                    hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                    hivMetadata.getPreArtStartDate().getConceptId(),
                    hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
                    hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
                    hivMetadata.getHIVCareProgram().getProgramId()));

    cd.addSearch(
            "A2I",
            map(sqlCohortDefinition, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
            "A2II",
            map(
                    getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthA2(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("(A2I) AND NOT A2II");

    return cd;
  }

  public CohortDefinition getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthC1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("C1");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch("population", map(getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(), "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch("exclusion", map(getAdditionalExclusionCriteriaForC1(), "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));
    cd.setCompositionString("population AND NOT exclusion");
    return cd;
  }


  /**
   * A.2: Number of patients transferred-in from another HFs during the current month
   *
   * @return Cohort
   * @return CohortDefinition
   */
  public CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthA2() {
    // TODO maybe we should be re-using
    // HivCohortQueries#getPatientsTransferredFromOtherHealthFacility
    EptsTransferredInCohortDefinition cd = new EptsTransferredInCohortDefinition();
    cd.setName("Number of patients transferred-in from another HFs during the current month");
    cd.setTypeOfPatientTransferredFromAnswer(hivMetadata.getPreTarvConcept());
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }

  /**
   * A3 = A.1 + A.2
   *
   * @return CohortDefinition
   */
  public CohortDefinition getSumOfA1AndA2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Sum of A1 and A2");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "A1",
        map(
            getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(),
            "startDate=${startDate},location=${location}"));
    cd.addSearch(
        "A2",
        map(
            getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("A1 OR A2");
    return cd;
  }

  // Start of the B queries
  /**
   * B1 Number of patientes who initiated TARV at this HF during the current month
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patientes who initiated TARV at this HF during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);

    CohortDefinition transferredIn =
        getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2();

    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    cd.addSearch("startedArt", map(startedArt, mappings));

    cd.addSearch("transferredIn", map(transferredIn, mappings));

    cd.setCompositionString("startedArt AND NOT transferredIn");
    return cd;
  }

  /**
   * B.2: Number of patients transferred-in from another HFs during the current month
   *
   * @return Cohort
   * @return CohortDefinition
   */
  public CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2() {
    // TODO maybe we should be re-using
    // HivCohortQueries#getPatientsTransferredFromOtherHealthFacility
    // Waiting for BAs response
    EptsTransferredInCohortDefinition cd = new EptsTransferredInCohortDefinition();
    cd.setName("Number of patients transferred-in from another HFs during the current month");
    cd.setTypeOfPatientTransferredFromAnswer(hivMetadata.getArtStatus());
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }

  /**
   * B.3 Number of patients who restarted the treatment during the current month
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithStartDrugs() {
    EncounterWithCodedObsCohortDefinition cd = getStateOfStayCohort();
    cd.setName("Number of patients who restarted the treatment during the current month");
    cd.addIncludeCodedValue(hivMetadata.getStartDrugs());
    return cd;
  }

  /**
   * See {@link HivCohortQueries#getPatientsTransferredOut}
   *
   * @return B.5 Number of patients transferred out during the current month
   */
  public CohortDefinition getPatientsTransferredOutB5() {
    ResumoMensalTransferredOutCohortDefinition cd =
        new ResumoMensalTransferredOutCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    return cd;
  }

  /** @return B6: Number of patients with ART suspension during the current month */
  public CohortDefinition getPatientsWhoSuspendedTreatmentB6() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Number of patients with ART suspension during the current month");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    String sql =
        "SELECT patient_id "
            + "FROM (SELECT patient_id, "
            + "             Max(suspended_date) suspended_date "
            + "      FROM (SELECT p.patient_id, "
            + "                   ps.start_date suspended_date "
            + "            FROM patient p "
            + "                     JOIN patient_program pp "
            + "                          ON p.patient_id = pp.patient_id "
            + "                     JOIN patient_state ps "
            + "                          ON pp.patient_program_id = ps.patient_program_id "
            + "            WHERE p.voided = 0 "
            + "              AND pp.voided = 0 "
            + "              AND pp.program_id = ${art} "
            + "              AND pp.location_id = :location "
            + "              AND ps.voided = 0 "
            + "              AND ps.state = ${suspendedState} "
            + "              AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore "
            + "            UNION "
            + "            SELECT p.patient_id, "
            + "                   e.encounter_datetime suspended_date "
            + "            FROM patient p "
            + "                     JOIN encounter e "
            + "                          ON p.patient_id = e.patient_id "
            + "                     JOIN obs o "
            + "                          ON e.encounter_id = o.encounter_id "
            + "            WHERE p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND e.location_id = :location "
            + "              AND e.encounter_type IN (${adultSeg}, ${masterCard}) "
            + "              AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore "
            + "              AND o.voided = 0 "
            + "              AND o.concept_id IN (${artStateOfStay}, ${preArtStateOfStay}) "
            + "              AND o.value_coded = ${suspendedConcept}) transferout "
            + "      GROUP BY patient_id) max_transferout "
            + "WHERE patient_id NOT IN (SELECT p.patient_id "
            + "                         FROM patient p "
            + "                                  JOIN encounter e "
            + "                                       ON p.patient_id = e.patient_id "
            + "                         WHERE p.voided = 0 "
            + "                           AND e.voided = 0 "
            + "                           AND e.encounter_type IN (${fila}) "
            + "                           AND e.location_id = :location "
            + "                           AND e.encounter_datetime > suspended_date "
            + "                           AND e.encounter_datetime <= :onOrBefore "
            + "                         UNION "
            + "                         SELECT p.patient_id "
            + "                         FROM patient p "
            + "                                  JOIN encounter e "
            + "                                       ON p.patient_id = e.patient_id "
            + "                                  JOIN obs o "
            + "                                       ON e.encounter_id = o.encounter_id "
            + "                         WHERE p.voided = 0 "
            + "                           AND e.voided = 0 "
            + "                           AND e.encounter_type = ${mcDrugPickup} "
            + "                           AND e.location_id = :location "
            + "                           AND o.concept_id = ${drugPickup} "
            + "                           AND o.value_datetime "
            + "                             > suspended_date"
            + "                           AND o.value_datetime"
            + "                             <= :onOrBefore);";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("art", hivMetadata.getARTProgram().getProgramId());
    valuesMap.put(
        "suspendedState",
        hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId());
    valuesMap.put("adultSeg", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("masterCard", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("artStateOfStay", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    valuesMap.put("preArtStateOfStay", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    valuesMap.put("suspendedConcept", hivMetadata.getSuspendedTreatmentConcept().getConceptId());
    valuesMap.put(
        "childSeg", hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("fila", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put(
        "mcDrugPickup", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("drugPickup", hivMetadata.getArtDatePickup().getConceptId());
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    cd.setQuery(sub.replace(sql));
    return cd;
  }

  /** @return B8: Number of dead patients during the current month */
  public CohortDefinition getPatientsWhoDied(Boolean hasStartDate) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    String sql =
        "SELECT patient_id"
            + "            FROM (SELECT patient_id, Max(death_date) AS death_date"
            + "            FROM (SELECT p.patient_id, Max(e.encounter_datetime) AS death_date"
            + "            FROM patient p "
            + "                     JOIN encounter e "
            + "                          ON p.patient_id = e.patient_id "
            + "                     JOIN obs o "
            + "                          ON e.encounter_id = o.encounter_id "
            + "            WHERE p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND e.location_id = :locationList "
            + "              AND e.encounter_type = ${adultoSeguimento} ";
    if (hasStartDate == true) {
      sql = sql + "AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore ";
    } else {
      sql = sql + "AND e.encounter_datetime <= :onOrBefore ";
    }
    sql =
        sql
            + "              AND o.voided = 0 "
            + "              AND o.concept_id = ${artStateOfStay}"
            + "              AND o.value_coded = ${patientDeadConcept} "
            + "      GROUP BY p.patient_id"
            + "            UNION"
            + "            SELECT p.patient_id, max(e.encounter_datetime) AS death_date "
            + "            FROM patient p "
            + "                     JOIN encounter e "
            + "                          ON p.patient_id = e.patient_id "
            + "                     JOIN obs o "
            + "                          ON e.encounter_id = o.encounter_id "
            + "            WHERE p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND e.location_id = :locationList "
            + "              AND e.encounter_type = ${masterCard} ";
    if (hasStartDate == true) {
      sql = sql + "AND o.obs_datetime BETWEEN :onOrAfter AND :onOrBefore ";
    } else {
      sql = sql + "AND o.obs_datetime <= :onOrBefore ";
    }
    sql =
        sql
            + "AND o.voided = 0 "
            + "              AND o.concept_id = ${preArtStateOfStay}  "
            + "              AND o.value_coded = ${patientDeadConcept} "
            + "      GROUP BY p.patient_id"
            + "            UNION"
            + "       SELECT pg.patient_id, ps.start_date"
            + "       FROM patient p"
            + "         INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
            + "         INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "       WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 "
            + "         AND pg.program_id=${arvProgram}  AND ps.state=${deadState} AND ps.end_date is null ";
    if (hasStartDate == true) {
      sql = sql + "AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore ";
    } else {
      sql = sql + "AND ps.start_date <= :onOrBefore ";
    }
    sql =
        sql
            + "AND location_id=:locationList"
            + "            UNION"
            + "        SELECT p.person_id patient_id, p.death_date "
            + "      FROM person p "
            + "      WHERE p.dead=1 ";
    if (hasStartDate == true) {
      sql = sql + "AND p.death_date BETWEEN :onOrAfter AND :onOrBefore ";
    } else {
      sql = sql + "AND p.death_date <= :onOrBefore ";
    }
    sql =
        sql
            + "AND p.voided=0) dead"
            + "       GROUP  BY patient_id) all_dead"
            + "      WHERE patient_id NOT IN (SELECT p.patient_id "
            + "         FROM patient p "
            + "         JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.encounter_type IN ( ${adultoSeguimento}, ${pediatriaSeguimento}, ${farmacia} )  "
            + "       AND e.location_id = :locationList "
            + "       AND e.encounter_datetime > death_date "
            + "       UNION "
            + "       SELECT p.patient_id "
            + "       FROM   patient p "
            + "       JOIN encounter e "
            + "       ON p.patient_id = e.patient_id "
            + "       JOIN obs o "
            + "       ON e.encounter_id = o.encounter_id "
            + "       WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.encounter_type = ${arvLevantamento} "
            + "       AND e.location_id = :locationList "
            + "       AND o.concept_id = ${arvLevantamentoDate} "
            + "       AND o.value_datetime > death_date)";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put(
        "adultoSeguimento", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("masterCard", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("preArtStateOfStay", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    valuesMap.put("artStateOfStay", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    valuesMap.put("patientDeadConcept", hivMetadata.getPatientHasDiedConcept().getConceptId());
    valuesMap.put("arvProgram", hivMetadata.getARTProgram().getProgramId());
    valuesMap.put("deadState", hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId());
    valuesMap.put(
        "pediatriaSeguimento",
        hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("farmacia", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put(
        "arvLevantamento", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("arvLevantamentoDate", hivMetadata.getArtDatePickup().getConceptId());

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    cd.setQuery(sub.replace(sql));

    return cd;
  }

  /** @return Number of cumulative patients who started ART by end of previous month */
  public CohortDefinition getPatientsWhoStartedArtByEndOfPreviousMonthB10() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of cumulative patients who started ART by end of previous month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition patientsWithArtStartDate = genericCohortQueries.getStartedArtBeforeDate(false);
    CohortDefinition patientsWithDrugPickup = getPatientsWithMasterCardDrugPickUpDate();
    CohortDefinition fila =
        genericCohortQueries.hasEncounter(hivMetadata.getARVPharmaciaEncounterType());
    CohortDefinition transferredIn =
        getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2();

    cd.addSearch(
        "artStartDate",
        map(patientsWithArtStartDate, "onOrBefore=${startDate},location=${location}"));

    String mappings = "value2=${startDate},locationList=${location}";
    cd.addSearch("drugPickup", map(patientsWithDrugPickup, mappings));

    String transferMappings = "onOrBefore=${startDate},location=${location}";
    cd.addSearch("transferredIn", map(transferredIn, transferMappings));

    cd.addSearch("fila", map(fila, "locationList=${location},onOrBefore=${startDate-1d}"));

    cd.setCompositionString("(artStartDate AND (drugPickup OR fila)) AND NOT transferredIn");

    return cd;
  }

  /** @return Number of active patients in ART by end of previous month */
  public CohortDefinition getPatientsWhoWereActiveByEndOfPreviousMonthB12() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of active patients in ART by end of previous month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    SqlCohortDefinition transferredIn = new SqlCohortDefinition();
    transferredIn.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    transferredIn.addParameter(new Parameter("location", "location", Location.class));
    transferredIn.setQuery(
        ResumoMensalQueries.getPatientsTransferredFromAnotherHealthFacilityByEndOfPreviousMonth(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId()));

    CohortDefinition startDrugs = getPatientsWithStartDrugs();
    CohortDefinition transferredOut = getPatientsTransferredOutB5();
    CohortDefinition suspended = getPatientsWhoSuspendedTreatmentB6();
    CohortDefinition missedDrugPickup = getLastArvPickupDateCohort();
    CohortDefinition died = getPatientsWhoDied(false);

    String encounterWithCodedObsMappings = "onOrBefore=${startDate},locationList=${location}";
    String drugPickupMappings =
        "value1=${endDate-90d},value2=${endDate},onOrBefore=${startDate},locationList=${location}";

    cd.addSearch("B10", mapStraightThrough(getPatientsWhoStartedArtByEndOfPreviousMonthB10()));
    cd.addSearch("B2A", map(transferredIn, "onOrBefore=${startDate},location=${location}"));
    cd.addSearch("B3A", map(startDrugs, encounterWithCodedObsMappings));
    cd.addSearch("B5A", map(transferredOut, "onOrBefore=${startDate},location=${location}"));
    cd.addSearch("B6A", map(suspended, encounterWithCodedObsMappings));
    cd.addSearch(
        "B7A",
        map(
            getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB127A(),
            "location=${location},onDate=${startDate}"));
    cd.addSearch("B8A", map(died, encounterWithCodedObsMappings));

    cd.setCompositionString("B10 OR B2A OR B3A AND NOT (B5A OR B6A OR B7A OR B8A)");

    return cd;
  }

  /** @return Patients who initiated Pre-TARV during the current month and was screened for TB. */
  public CohortDefinition getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndScreenedTB() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who initiated Pre-TARV during the current month and was screened for TB.");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition a2 = getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2();
    CohortDefinition tb = getPatientScreenedForTb();

    String mappings = "onOrAfter=${startDate},locationList=${location}";
    cd.addSearch("A2", mapStraightThrough(a2));
    cd.addSearch("TB", map(tb, mappings));

    cd.setCompositionString("A2 AND TB");

    return cd;
  }

  /**
   * C1: Number of patients who initiated Pre-TARV during the current month and was
   * screened for TB C1
   * @return
   */
  public CohortDefinition getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndScreenedTbC1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who initiated Pre-TARV during the current month and was screened for TB C1");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition c1 = getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2();
    CohortDefinition tb = getPatientScreenedForTb();

    String mappings = "onOrAfter=${startDate},locationList=${location}";
    cd.addSearch("Pop", map(getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthC1(), "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch("TB", map(tb, mappings));

    cd.setCompositionString("Pop AND TB");

    return cd;
  }

  /** @return Patients who initiated Pre-TARV during the current month and started TPI. */
  public CohortDefinition getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndStartedTPI() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who initiated Pre-TARV during the current month and started TPI");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition a2 = getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2();
    CohortDefinition tpi = getPatientsWhoStartedTPI();

    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    cd.addSearch("A2", mapStraightThrough(a2));
    cd.addSearch("TPI", map(tpi, mappings));

    cd.setCompositionString("A2 AND TPI");

    return cd;
  }

  /**
   * @return Patientes who initiated Pre-TARV during the current month and was diagnosed for active
   *     TB
   */
  public CohortDefinition
      getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndDiagnosedForActiveTB() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who initiated Pre-TARV during the current month and started TPI");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition a2 = getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2();
    CohortDefinition tb = getPatientsDiagnosedForActiveTB();

    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    cd.addSearch("A2", mapStraightThrough(a2));
    cd.addSearch("TB", map(tb, mappings));

    cd.setCompositionString("A2 AND TB");
    return cd;
  }

  /**
   * @return Patients that have ACTIVE TB = YES in their FIRST or SECOND S.TARV – Adulto *
   *     Seguimento encounter
   */
  private CohortDefinition getPatientsDiagnosedForActiveTB() {
    CodedObsOnFirstOrSecondEncounterCalculation calculation =
        Context.getRegisteredComponents(CodedObsOnFirstOrSecondEncounterCalculation.class).get(0);
    CalculationCohortDefinition cd = new CalculationCohortDefinition(calculation);
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter("concept", tbMetadata.getActiveTBConcept());
    cd.addCalculationParameter("valueCoded", hivMetadata.getYesConcept());
    return cd;
  }

  /**
   * @return Patients that have ISONIAZID USE = START DRUGS in their FIRST or SECOND S.TARV – Adulto
   *     Seguimento encounter
   */
  private CohortDefinition getPatientsWhoStartedTPI() {
    CodedObsOnFirstOrSecondEncounterCalculation calculation =
        Context.getRegisteredComponents(CodedObsOnFirstOrSecondEncounterCalculation.class).get(0);
    CalculationCohortDefinition cd = new CalculationCohortDefinition(calculation);
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter("concept", hivMetadata.getIsoniazidUsageConcept());
    cd.addCalculationParameter("valueCoded", hivMetadata.getStartDrugs());
    return cd;
  }

  /**
   * @return Patients that have TB Symptoms = Yes in their FIRST S.TARV – Adulto Seguimento
   *     encounter
   */
  private CohortDefinition getPatientScreenedForTb() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.FIRST);
    cd.setQuestion(tbMetadata.getHasTbSymptomsConcept());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getYesConcept());
    return cd;
  }

  /**
   * Get number of patients transferred from other health facility marked in master card
   *
   * @return CohortDefinition
   */
  private CohortDefinition getTypeOfPatientTransferredFrom() {
    EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addEncounterType(hivMetadata.getMasterCardEncounterType());
    cd.setConcept(hivMetadata.getTypeOfPatientTransferredFrom());
    cd.addIncludeCodedValue(hivMetadata.getPreTarvConcept());
    return cd;
  }

  /** @return CohortDefinition Patients with transfer from other HF = YES */
  private CohortDefinition getPatientsWithTransferFromOtherHF() {
    EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addEncounterType(hivMetadata.getMasterCardEncounterType());
    cd.setConcept(hivMetadata.getTransferFromOtherFacilityConcept());
    cd.addIncludeCodedValue(hivMetadata.getYesConcept());
    return cd;
  }

  public CohortDefinition getAdditionalExclusionCriteriaForC1() {
    CompositionCohortDefinition cd  = new CompositionCohortDefinition();
    cd.setName("All patients to be excluded for the C1 definition");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    List<Concept> concepts = new ArrayList<>();
    cd.addSearch("transferBasedOnObsDate", map(getPatientsTransferBasedOnObsDate(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${locationList}"));
    cd.addSearch("getTypeOfPatientTransferredFrom", map(getTypeOfPatientTransferredFrom(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${locationList}"));
    cd.addSearch("inProgramState", map(genericCohortQueries.getPatientsBasedOnPatientStates(hivMetadata.getHIVCareProgram().getProgramId(),
            hivMetadata.getPateintTransferedFromOtherFacilityWorkflowState().getProgramWorkflowStateId()), "startDate=${onOrAfter},endDate=${onOrBefore},location=${locationList}"));
    cd.setCompositionString("transferBasedOnObsDate OR getTypeOfPatientTransferredFrom OR inProgramState");
    return cd;
  }

  private CohortDefinition getPatientsTransferBasedOnObsDate() {
    CodedObsCohortDefinition cd  = new CodedObsCohortDefinition();
    cd.setName("Get patients with obs date based on a concept");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.setQuestion(hivMetadata.getTransferFromOtherFacilityConcept());
    cd.setOperator(SetComparator.IN);
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.ANY);
    cd.setEncounterTypeList(Arrays.asList(hivMetadata.getMasterCardEncounterType()));
    return cd;

  }

  /** @return CohortDefinition Patients with Type of Patient Transferred From = 'Pre-TARV' */
  private CohortDefinition getPatientsWithTransferFrom() {
    EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addEncounterType(hivMetadata.getMasterCardEncounterType());
    cd.setConcept(hivMetadata.getTypeOfPatientTransferredFrom());
    cd.addIncludeCodedValue(hivMetadata.getPreTarvConcept());
    return cd;
  }

  private CohortDefinition getPatientsWhoInitiatedTarvAtAfacility() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.addParameter(new Parameter("value1", "Value 1", Date.class));
    cd.addParameter(new Parameter("value2", "Value 1", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getMasterCardEncounterType());
    cd.setQuestion(hivMetadata.getARVStartDate());
    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);
    return cd;
  }

  /** @return Patients with Adulto Seguimento encounter with State of Stay question */
  private EncounterWithCodedObsCohortDefinition getStateOfStayCohort() {
    EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setConcept(hivMetadata.getStateOfStayOfArtPatient());
    return cd;
  }

  /**
   * Patients with master card drug pickup date
   *
   * @return CohortDefinition
   */
  private DateObsCohortDefinition getPatientsWithMasterCardDrugPickUpDate() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.addParameter(new Parameter("value1", "Value 1", Date.class));
    cd.addParameter(new Parameter("value2", "Value 1", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getMasterCardDrugPickupEncounterType());
    cd.setQuestion(hivMetadata.getArtDatePickup());
    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);
    return cd;
  }

  /** @return Patients with last Drug Pickup Date between boundaries */
  private DateObsCohortDefinition getLastArvPickupDateCohort() {
    DateObsCohortDefinition cd = getPatientsWithMasterCardDrugPickUpDate();
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    return cd;
  }

  /**
   * E1: Number of active patients in ART at the end of current month who performed Viral Load Test
   * (Annual Notification) B12 OR (B1 OR B2 OR B3) AND NOT (B5 OR B6 OR B7 OR B8)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getNumberOfActivePatientsInArtAtEndOfCurrentMonthWithVlPerformed() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Number of active patients in ART at the end of current month who performed Viral Load Test (Annual Notification)");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "common",
        map(
            getStandardDefinitionForEcolumns(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "F",
        map(
            getPatientsWithCodedObsAndAnswers(
                hivMetadata.getApplicationForLaboratoryResearch(),
                hivMetadata.getHivViralLoadConcept()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "E1x",
        map(
            genericCohortQueries.generalSql(
                "E1x",
                ResumoMensalQueries.getE1ExclusionCriteria(
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getApplicationForLaboratoryResearch().getConceptId(),
                    hivMetadata.getHivViralLoadConcept().getConceptId())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(common AND F) AND NOT E1x");
    return cd;
  }

  public CohortDefinition getStandardDefinitionForEcolumns() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Standard columns for E1, E2 and E3");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "B12",
        map(
            getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndScreenedTB(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B1",
        map(
            getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B2",
        map(
            getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "B3",
        map(
            getPatientsWithStartDrugs(),
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "B5",
        map(
            getPatientsTransferredOutB5(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "B6",
        map(
            getPatientsWhoSuspendedTreatmentB6(),
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "B7",
        map(
            getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB127A(),
            "location=${location},onDate=${endDate}"));
    cd.addSearch(
        "B8",
        map(
            getPatientsWhoDied(true),
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));
    cd.setCompositionString("(B12 OR (B1 OR B2 OR B3)) AND NOT (B5 OR B6 OR B7 OR B8)");
    return cd;
  }

  /** Filter only those patients */
  private CohortDefinition getPatientsWithCodedObsAndAnswers(Concept question, Concept answer) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(
        "Patients with lab request having question and answer - encounter date within boundaries");
    cd.addParameter(new Parameter("startDate", "After Date", Date.class));
    cd.addParameter(new Parameter("endDate", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsWithCodedObsAndAnswers(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            question.getConceptId(),
            answer.getConceptId()));
    return cd;
  }

  /**
   * get patients who have viral load test done
   *
   * @return CohortDefinition
   */
  private CohortDefinition getViralLoadTestDone() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Viral load test");
    cd.addParameter(new Parameter("startDate", "After Date", Date.class));
    cd.addParameter(new Parameter("endDate", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsHavingViralLoadResults(
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()));
    return cd;
  }

  /**
   * Get patients with coded observation
   *
   * @param question
   * @return
   */
  private CohortDefinition gePatientsWithCodedObs(Concept question) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients with Viral load qualitative done");
    cd.addParameter(new Parameter("startDate", "After Date", Date.class));
    cd.addParameter(new Parameter("endDate", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.gePatientsWithCodedObs(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            question.getConceptId()));
    return cd;
  }

  /**
   * Combine the viral load and viral load qualitative
   *
   * @return CohortDefinition
   */
  private CohortDefinition getViralLoadOrQualitative() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Combined viral load and its qualitative patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "VL",
        map(
            getViralLoadTestDone(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "VLQ",
        map(
            gePatientsWithCodedObs(hivMetadata.getHivViralLoadQualitative()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("VL OR VLQ");
    return cd;
  }

  /**
   * E2 Get the combinations together E2: Number of active patients in ART at the end of current
   * month who received a Viral Load Test Result (Annual Notification
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "E2: Number of active patients in ART at the end of current month who received a Viral Load Test Result (Annual Notification");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "C",
        map(
            getStandardDefinitionForEcolumns(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "VL",
        map(
            getViralLoadOrQualitative(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "Ex2",
        map(
            genericCohortQueries.generalSql(
                "Ex2",
                ResumoMensalQueries.getE2ExclusionCriteria(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(C AND VL) AND NOT Ex2");
    return cd;
  }

  /**
   * E3: Number of active patients in ART at the end of current month who received supressed Viral
   * Load Result (Annual Notification)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getActivePatientsOnArtWhoRecievedVldSuppressionResults() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Number of active patients in ART at the end of current month who received supressed Viral Load Result (Annual Notification)");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "C",
        map(
            getStandardDefinitionForEcolumns(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "SUPP",
        map(
            getViralLoadSuppression(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "QUAL",
        map(
            gePatientsWithCodedObs(hivMetadata.getHivViralLoadQualitative()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "Ex3",
        map(
            genericCohortQueries.generalSql(
                "Ex3",
                ResumoMensalQueries.getE3ExclusionCriteria(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(C AND (SUPP OR QUAL)) AND NOT Ex3");
    return cd;
  }

  private CohortDefinition getViralLoadSuppression() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Viral load suppression");
    cd.addParameter(new Parameter("startDate", "After Date", Date.class));
    cd.addParameter(new Parameter("endDate", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsHavingViralLoadSuppression(
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()));
    return cd;
  }

  /**
   * F1: Number of patients who had clinical appointment during the reporting month
   *
   * @return CohortDefinition
   */
  public CohortDefinition getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonth() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("F1: Number of patients who had clinical appointment during the reporting month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsWithGivenEncounterType(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()));
    return cd;
  }

  /**
   * F2: Number of patients who had clinical appointment during the reporting month and were
   * screened for TB
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthAndScreenedFoTb() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Exclusions");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.setQuery(
        ResumoMensalQueries.getPatientsForF2ForExclusionFromMainQuery(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            tbMetadata.getHasTbSymptomsConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getNoConcept().getConceptId(),
            tbMetadata.getTBTreatmentPlanConcept().getConceptId()));

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Number of patients who had clinical appointment during the reporting month and were screened for TB");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "F2F",
        map(
            getPatientsWithCodedObsAndAnswers(
                tbMetadata.getHasTbSymptomsConcept(), hivMetadata.getYesConcept()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "F2x",
        map(sqlCohortDefinition, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("F2F AND NOT F2x");
    return cd;
  }

  /**
   * F3: Number of patients who had at least one clinical appointment during the year
   *
   * @return CohortDefinition
   */
  public CohortDefinition getNumberOfPatientsWithAtLeastOneClinicalAppointmentDuringTheYear() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who had at least one clinical appointment during the year");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "F1",
        map(
            getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonth(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "Fx3",
        map(
            genericCohortQueries.generalSql(
                "Fx3",
                ResumoMensalQueries.getF3Exclusion(
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("F1 AND NOT Fx3");
    return cd;
  }

  /**
   * Get number of patients who have a consultation on the same date as that of pre art
   *
   * @return CohortDefinition
   */
  private CohortDefinition
      getPatientsWithFirstClinicalConsultationOnTheSameDateAsPreArtStartDate() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(
        "Get number of patients who have a consultation on the same date as that of pre art");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsWithFirstClinicalConsultationOnTheSameDateAsPreArtStartDate(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPreArtStartDate().getConceptId()));
    return cd;
  }

  /**
   * B12-B7A: Number of active patients in ART by end of previous/current month
   *
   * @retrun CohortDefinition
   */
  public CohortDefinition getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB127A() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Number of patients who Abandoned the ART during the current month");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onDate", "On Date", Date.class));
    cd.setQuery(
        ResumoMensalQueries.getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthB127A(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickup().getConceptId()));
    return cd;
  }

  /**
   * Get all patients enrolled in PRE-ART program id 1, with date enrolled less than startDate
   *
   * @return CohortDefinition
   */
  private CohortDefinition
      getAllPatientsEnrolledInPreArtProgramWithDateEnrolledLessThanStartDateA1() {
    SqlCohortDefinition sqlPatientsEnrolledInPreART = new SqlCohortDefinition();
    sqlPatientsEnrolledInPreART.setName("patientsEnrolledInPreART");
    sqlPatientsEnrolledInPreART.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlPatientsEnrolledInPreART.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlPatientsEnrolledInPreART.addParameter(new Parameter("location", "Location", Location.class));
    sqlPatientsEnrolledInPreART.setQuery(
        ResumoMensalQueries.getAllPatientsEnrolledInPreArtProgramWithDateEnrolledLessThanStartDate(
            hivMetadata.getHIVCareProgram().getProgramId()));

    return sqlPatientsEnrolledInPreART;
  }

  /**
   * Get all patients registered in encounterType 5 or 7, with date enrolled less than startDate
   *
   * @return CohortDefinition
   */
  private CohortDefinition
      getAllPatientsRegisteredInEncounterType5or7WithEncounterDatetimeLessThanStartDateA1() {
    EncounterCohortDefinition cd = new EncounterCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Start Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getARVPharmaciaEncounterType());
    cd.addEncounterType(hivMetadata.getARVPediatriaInitialEncounterType());
    return cd;
  }

  /**
   * Number of patients transferred-in from another HF during a period less than startDate
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthA1() {
    EptsTransferredInCohortDefinition cd = new EptsTransferredInCohortDefinition();
    cd.setName(
        "Number of patients transferred-in from another HF during a period less than startDate");
    cd.setTypeOfPatientTransferredFromAnswer(hivMetadata.getPreTARVConcept());
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }
}
