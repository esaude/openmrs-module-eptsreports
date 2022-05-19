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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.generic.AgeInMonthsOnArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.AgeOnArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.AgeOnPreArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.AgeOnReportEndDateDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.ArtDateMinusDiagnosisDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.NewlyOrPreviouslyEnrolledOnARTCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.StartedArtBeforeDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.StartedArtBeforeDateCalculationMOH;
import org.openmrs.module.eptsreports.reporting.calculation.generic.StartedArtBeforeDateForTxTbCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.StartedArtOnPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.PrepCtQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.PrepNewQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.ViralLoadQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

@Component
public class GenericCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired private CommonMetadata commonMetadata;

  /**
   * Generic Coded Observation cohort
   *
   * @param question the question concept
   * @param values the answers to include
   * @return the cohort definition
   */
  public CohortDefinition hasCodedObs(
      Concept question,
      TimeModifier timeModifier,
      SetComparator operator,
      List<EncounterType> encounterTypes,
      List<Concept> values) {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("has obs between dates");
    cd.setQuestion(question);
    cd.setOperator(operator);
    cd.setTimeModifier(timeModifier);
    cd.setEncounterTypeList(encounterTypes);
    cd.setValueList(values);

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }

  /**
   * Generic Coded Observation cohort with default parameters defined
   *
   * @param question the question concept
   * @param values the answers to include
   * @return the cohort definition
   */
  public CohortDefinition hasCodedObs(Concept question, List<Concept> values) {
    return hasCodedObs(
        question, BaseObsCohortDefinition.TimeModifier.ANY, SetComparator.IN, null, values);
  }

  /**
   * Generic SQL cohort
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "generalSql")
  public CohortDefinition generalSql(String name, String query) {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName(name);
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Facility", Location.class));
    sql.setQuery(query);
    return sql;
  }

  /**
   * Generic InProgram Cohort
   *
   * @param program the programs
   * @return the cohort definition
   */
  public CohortDefinition createInProgram(String name, Program program) {
    InProgramCohortDefinition inProgram = new InProgramCohortDefinition();
    inProgram.setName(name);

    List<Program> programs = new ArrayList<>();
    programs.add(program);

    inProgram.setPrograms(programs);
    inProgram.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    inProgram.addParameter(new Parameter("locations", "Location", Location.class));
    return inProgram;
  }

  /**
   * Base cohort for the pepfar report
   *
   * @return CohortDefinition
   */
  public CohortDefinition getBaseCohort() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setName("baseCohort");
    cd.addSearch(
        "base",
        EptsReportUtils.map(
            generalSql(
                "baseCohort",
                BaseQueries.getBaseCohortQuery(
                    hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
                    hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
                    hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                    hivMetadata.getHIVCareProgram().getProgramId(),
                    hivMetadata.getARTProgram().getProgramId(),
                    hivMetadata.getDateOfMasterCardFileOpeningConcept().getConceptId())),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "persons",
        EptsReportUtils.map(
            generalSql(
                "persons",
                "SELECT p.patient_id FROM patient p JOIN person pn ON p.patient_id=pn.person_id WHERE p.voided=0 AND pn.voided=0 "),
            ""));
    cd.setCompositionString("base AND persons");
    return cd;
  }

  /**
   * <b>Description:</b> Get patients states based on program, state and end of reporting period
   *
   * @param program - program ID
   * @param state - patient state
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsBasedOnPatientStates(int program, int state) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patient states based on end of reporting period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        "SELECT pg.patient_id"
            + " FROM patient p"
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 "
            + " AND pg.program_id=%s AND ps.state=%s AND ps.end_date is null "
            + " AND ps.start_date BETWEEN :startDate AND :endDate AND location_id=:location";
    cd.setQuery(String.format(query, program, state));
    return cd;
  }

  /**
   * Get patients states based on program, state and end of reporting period
   *
   * @param program - program ID
   * @param state - patient state
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsBasedOnPatientStatesBeforeDate(int program, int state) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patient states based on end of reporting period");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        "SELECT pg.patient_id"
            + " FROM patient p"
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 "
            + " AND pg.program_id=%s AND ps.state=%s AND ps.end_date is null "
            + " AND ps.start_date <= :endDate AND location_id=:location";
    cd.setQuery(String.format(query, program, state));
    return cd;
  }

  /**
   * Get deceased patients, we need to check in the person table and patient states,
   *
   * @return CohortDefinition
   */
  public CohortDefinition getDeceasedPatients() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Get deceased patients based on patient states and person object");
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String sql =
        "SELECT patient_id "
            + "FROM   (SELECT patient_id, "
            + "               Max(death_date) death_date "
            + "        FROM   (SELECT p.patient_id, "
            + "                       ps.start_date death_date "
            + "                FROM   patient p "
            + "                       JOIN patient_program pp "
            + "                         ON p.patient_id = pp.patient_id "
            + "                       JOIN patient_state ps "
            + "                         ON pp.patient_program_id = ps.patient_program_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND pp.voided = 0 "
            + "                       AND pp.program_id = %d "
            + "                       AND pp.location_id = :location "
            + "                       AND ps.voided = 0 "
            + "                       AND ps.state = %d "
            + "                       AND ps.start_date <= :onOrBefore "
            + "                       AND ps.end_date IS NULL "
            + "                UNION "
            + "                SELECT p.person_id, "
            + "                       p.death_date "
            + "                FROM   person p "
            + "                WHERE  p.voided = 0"
            + "                   AND p.dead = 1 "
            + "                   AND p.death_date <= :onOrBefore "
            + "                UNION "
            + "                SELECT e.patient_id, "
            + "                       visit_card.death_date "
            + "                FROM   (SELECT p.patient_id, "
            + "                               Max(e.encounter_datetime) death_date "
            + "                        FROM   patient p "
            + "                               JOIN encounter e "
            + "                                 ON p.patient_id = e.patient_id "
            + "                               JOIN obs notfound "
            + "                                 ON e.encounter_id = notfound.encounter_id "
            + "                               JOIN obs reason "
            + "                                 ON e.encounter_id = reason.encounter_id "
            + "                        WHERE  p.voided = 0 "
            + "                               AND e.voided = 0 "
            + "                               AND e.encounter_type IN ( %d, %d, %d ) "
            + "                               AND e.encounter_datetime <= :onOrBefore "
            + "                               AND notfound.voided = 0 "
            + "                               AND notfound.concept_id = %d "
            + "                               AND notfound.value_coded = %d "
            + "                               AND reason.voided = 0 "
            + "                               AND reason.concept_id = %d "
            + "                               AND reason.value_coded = %d "
            + "                        GROUP  BY p.patient_id) visit_card "
            + "                       JOIN encounter e "
            + "                         ON visit_card.patient_id = e.patient_id "
            + "                            AND visit_card.death_date = e.encounter_datetime "
            + "                UNION "
            + "                SELECT p.patient_id, "
            + "                       e.encounter_datetime death_date "
            + "                FROM   patient p "
            + "                       JOIN encounter e "
            + "                         ON p.patient_id = e.patient_id "
            + "                       JOIN obs o "
            + "                         ON e.encounter_id = o.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type IN ( %d, %d ) "
            + "                       AND e.encounter_datetime <= :onOrBefore "
            + "                       AND o.voided = 0 "
            + "                       AND o.concept_id IN ( %d, %d ) "
            + "                       AND o.value_coded = %d) dead "
            + "        GROUP  BY patient_id) max_dead "
            + "WHERE  patient_id NOT IN (SELECT p.patient_id "
            + "                          FROM   patient p "
            + "                                 JOIN encounter e "
            + "                                   ON p.patient_id = e.patient_id "
            + "                          WHERE  p.voided = 0 "
            + "                                 AND e.voided = 0 "
            + "                                 AND e.encounter_type IN ( %d, %d, %d ) "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_datetime > death_date "
            + "                          UNION "
            + "                          SELECT p.patient_id "
            + "                          FROM   patient p "
            + "                                 JOIN encounter e "
            + "                                   ON p.patient_id = e.patient_id "
            + "                                 JOIN obs o "
            + "                                   ON e.encounter_id = o.encounter_id "
            + "                          WHERE  p.voided = 0 "
            + "                                 AND e.voided = 0 "
            + "                                 AND e.encounter_type = %d "
            + "                                 AND e.location_id = :location "
            + "                                 AND o.concept_id = 23866 "
            + "                                 AND o.value_datetime > death_date); ";
    cd.setQuery(
        String.format(
            sql,
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getPatientFoundConcept().getConceptId(),
            hivMetadata.getNoConcept().getConceptId(),
            hivMetadata.getReasonPatientNotFound().getConceptId(),
            hivMetadata.getPatientIsDead().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfPreArtPatient().getConceptId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getPatientHasDiedConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

    return cd;
  }

  /**
   * Get deceased patients, we need to check in the person table and patient states,
   *
   * @return CohortDefinition
   */
  public CohortDefinition getDeceasedPatientsBeforeDate() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get deceased patients based on patient states and person object");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "dead",
        map(
            getPatientsBasedOnPatientStatesBeforeDate(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId()),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "deceased",
        map(
            generalSql(
                "deceased",
                "SELECT patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id AND pe.dead=1 WHERE pe.death_date <=:endDate"),
            "endDate=${endDate}"));
    cd.setCompositionString("dead OR deceased");
    return cd;
  }

  /**
   * @param minAge minimum age of a patient based on Art Start Date
   * @param maxAge maximum age of a patient based on Art Start Date
   * @param considerPatientThatStartedBeforeWasBorn boolean parameter true for Patient That Started
   *     Before Was Born
   * @return CohortDefinition
   */
  public CohortDefinition getAgeOnArtStartDate(
      Integer minAge, Integer maxAge, boolean considerPatientThatStartedBeforeWasBorn) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(AgeOnArtStartDateCalculation.class).get(0));
    cd.setName("Age on ART start date");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addCalculationParameter("minAge", minAge);
    cd.addCalculationParameter("maxAge", maxAge);
    cd.addCalculationParameter(
        "considerPatientThatStartedBeforeWasBorn", considerPatientThatStartedBeforeWasBorn);
    return cd;
  }

  /**
   * Age should be calculated on Patient ART Start Date (Check Section A for the algorithm to define
   * this date).
   *
   * @param minAge Minimum age of a patient based on ART Start Date
   * @param maxAge Maximum age of a patient based on ART Start Date
   * @param considerPatientThatStartedBeforeWasBorn
   * @return CohortDefinition
   */
  public CohortDefinition getAgeOnMOHArtStartDate(
      Integer minAge, Integer maxAge, boolean considerPatientThatStartedBeforeWasBorn) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Age on MOH ART start date");
    sqlCohortDefinition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("minAge", minAge);
    map.put("maxAge", maxAge);
    String query =
        "SELECT p.person_id "
            + "FROM person p "
            + "     INNER JOIN ( "
            + "           SELECT pp.patient_id, MIN(o.value_datetime) as first_start_drugs "
            + "           FROM patient pp "
            + "                INNER JOIN encounter e ON e.patient_id = pp.patient_id "
            + "                INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "           WHERE pp.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "             AND e.encounter_type = ${53} and o.concept_id = ${1190} "
            + "             AND e.location_id = :location "
            + "             AND o.value_datetime <= :onOrBefore "
            + "           GROUP BY pp.patient_id ) AS A1 ON p.person_id = A1.patient_id "
            + "WHERE A1.first_start_drugs >= :onOrAfter "
            + "  AND A1.first_start_drugs <= :onOrBefore "
            + "  AND ";
    if (minAge != null && maxAge != null) {
      query +=
          "     TIMESTAMPDIFF(YEAR, p.birthdate, A1.first_start_drugs) >= ${minAge}  "
              + "         AND   "
              + "   TIMESTAMPDIFF(YEAR, p.birthdate, A1.first_start_drugs) <= ${maxAge}; ";
    } else if (minAge == null && maxAge != null) {
      query += "   TIMESTAMPDIFF(YEAR, p.birthdate, A1.first_start_drugs) <= ${maxAge}; ";
    } else if (minAge != null && maxAge == null) {
      query += "   TIMESTAMPDIFF(YEAR, p.birthdate, A1.first_start_drugs) >= ${minAge};  ";
    }
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));
    return sqlCohortDefinition;
  }

  /**
   * @param considerTransferredIn
   * @param considerPharmacyEncounter
   * @return CohortDefinition
   */
  public CohortDefinition getStartedArtOnPeriod(
      boolean considerTransferredIn, boolean considerPharmacyEncounter) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(StartedArtOnPeriodCalculation.class).get(0));
    cd.setName("Art start date");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addCalculationParameter("considerTransferredIn", considerTransferredIn);
    cd.addCalculationParameter("considerPharmacyEncounter", considerPharmacyEncounter);
    return cd;
  }

  /**
   * @param considerTransferredIn
   * @return CohortDefinition
   */
  public CohortDefinition getStartedArtBeforeDate(boolean considerTransferredIn) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(StartedArtBeforeDateCalculation.class).get(0));
    cd.setName("Art start date");
    cd.addCalculationParameter("considerTransferredIn", considerTransferredIn);
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    return cd;
  }

  public CohortDefinition getStartedArtBeforeDateTxTb(boolean considerTransferredIn) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(StartedArtBeforeDateForTxTbCalculation.class).get(0));
    cd.setName("Art start date TX TB");
    cd.addCalculationParameter("considerTransferredIn", considerTransferredIn);
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    return cd;
  }

  /**
   * @param considerTransferredIn
   * @return CohortDefinition
   */
  public CohortDefinition getStartedArtBeforeDateMOH(boolean considerTransferredIn) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(StartedArtBeforeDateCalculationMOH.class).get(0));
    cd.setName("Art start date");
    cd.addCalculationParameter("considerTransferredIn", considerTransferredIn);
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));

    return cd;
  }

  /**
   * @param isNewlyEnrolledOnArtSearch
   * @return CohortDefinition
   */
  public CohortDefinition getNewlyOrPreviouslyEnrolledOnART(boolean isNewlyEnrolledOnArtSearch) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(NewlyOrPreviouslyEnrolledOnARTCalculation.class)
                .get(0));
    cd.setName("Newly Or Previously Enrolled On ART");
    cd.addCalculationParameter("isNewlyEnrolledOnArtSearch", isNewlyEnrolledOnArtSearch);
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    return cd;
  }

  /**
   * @param question
   * @param timeModifier
   * @param operator1
   * @param value1
   * @param operator2
   * @param value2
   * @param encounterTypes
   * @return CohortDefinition
   */
  public CohortDefinition hasNumericObs(
      Concept question,
      TimeModifier timeModifier,
      RangeComparator operator1,
      Double value1,
      RangeComparator operator2,
      Double value2,
      List<EncounterType> encounterTypes) {

    NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
    cd.setTimeModifier(timeModifier);
    cd.setQuestion(question);
    cd.setName("has obs with numeric value ranges");
    cd.setEncounterTypeList(encounterTypes);
    cd.setOperator1(operator1);
    cd.setValue1(value1);
    cd.setOperator2(operator2);
    cd.setValue2(value2);

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }

  /**
   * @param numDays number of days
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoToLostToFollowUp(int numDays) {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addSearch(
        "31",
        mapStraightThrough(
            txCurrCohortQueries.getPatientHavingLastScheduledDrugPickupDateDaysBeforeEndDate(
                numDays)));

    definition.addSearch(
        "32",
        mapStraightThrough(
            txCurrCohortQueries.getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.setCompositionString("31 OR  32");

    return definition;
  }

  /**
   * <b>Description:</b> Get patients with any coded obs before end date
   *
   * @return {@link String}
   */
  public static String getPatientsWithCodedObsValueDatetimeBeforeEndDate(
      int encounterType, int conceptId) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + " AND e.location_id = :location AND e.encounter_datetime <= :onOrBefore AND e.encounter_type=${encounterType} "
            + " AND o.concept_id=${conceptId} AND o.value_datetime is NOT NULL";

    Map<String, Integer> map = new HashMap<>();
    map.put("encounterType", encounterType);
    map.put("conceptId", conceptId);

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query);
  }

  /**
   * @param encounterType The encounter Type
   * @param question The concept Id
   * @param answers The value coded
   * @return String
   */
  public String getPatientsWithObsBetweenDates(
      EncounterType encounterType, Concept question, List<Concept> answers) {
    List<Integer> answerIds = new ArrayList<>();
    for (Concept concept : answers) {
      answerIds.add(concept.getConceptId());
    }
    StringBuilder s = new StringBuilder();
    s.append("SELECT p.patient_id FROM patient p INNER JOIN encounter e ");
    s.append("ON p.patient_id = e.patient_id ");
    s.append("INNER JOIN obs o ");
    s.append("ON e.encounter_id = o.encounter_id ");
    s.append("WHERE e.location_id = :location AND e.encounter_type = ${encounterType} ");
    s.append("AND o.concept_id = ${question}  ");
    s.append("AND o.value_coded in (${answers}) ");
    s.append("AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate ");
    s.append("AND p.voided = 0 AND e.voided = 0 AND o.voided = 0");
    Map<String, String> values = new HashMap<>();
    values.put("encounterType", String.valueOf(encounterType.getEncounterTypeId()));
    // Just convert the conceptId to String so it can be added to the map
    values.put("question", String.valueOf(question.getConceptId()));
    values.put("answers", StringUtils.join(answerIds, ","));
    StringSubstitutor sb = new StringSubstitutor(values);
    return sb.replace(s.toString());
  }

  /**
   * <b>Description:</b> Gets last obs with value coded before enDate
   *
   * @param encounterTypes The Obs encounter Type
   * @param question The Obs quetion concept
   * @param answers The third value coded
   * @return String
   */
  public static String getLastCodedObsBeforeDate(
      List<Integer> encounterTypes, Integer question, List<Integer> answers) {

    Map<String, String> map = new HashMap<>();

    map.put("encounterTypes", StringUtils.join(encounterTypes, ","));
    map.put("question", String.valueOf(question));
    map.put("answers", StringUtils.join(answers, ","));

    String query =
        "SELECT patient_id FROM ( "
            + "  SELECT p.patient_id, max(e.encounter_datetime) datetime "
            + "  FROM patient p "
            + "  INNER JOIN encounter e "
            + "  ON p.patient_id = e.patient_id "
            + "  INNER JOIN obs o "
            + "  ON e.encounter_id = o.encounter_id "
            + "  WHERE e.location_id = :location AND e.encounter_type IN (${encounterTypes}) "
            + "  AND o.concept_id = ${question} AND o.value_coded IN (${answers}) "
            + "  AND e.encounter_datetime <= :onOrBefore  "
            + "  AND p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "  GROUP BY p.patient_id ) AS last_encounter";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * <b>Description: </b>Gets last obs with value coded before enDate
   *
   * @param encounterTypes The Obs encounter Type
   * @param question The Obs question concept
   * @param answers The third value coded
   * @return String
   */
  public static String getLastCodedObservationBeforeDate(
      List<Integer> encounterTypes, Integer question, List<Integer> answers) {

    Map<String, String> map = new HashMap<>();

    map.put("encounterTypes", StringUtils.join(encounterTypes, ","));
    map.put("question", String.valueOf(question));
    map.put("answers", StringUtils.join(answers, ","));

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON e.encounter_id = o.encounter_id "
            + "               WHERE"
            + "                   e.location_id = :location "
            + "                  AND e.encounter_type IN ( ${encounterTypes} ) "
            + "                  AND o.concept_id = ${question} "
            + "                  AND e.voided = 0 "
            + "                  AND o.voided = 0 "
            + "                  AND o.value_coded IN ( ${answers} ) "
            + "                  AND e.encounter_datetime = (SELECT ee.encounter_datetime "
            + "                                              FROM   encounter ee "
            + "                      INNER JOIN obs oo "
            + "                              ON ee.encounter_id = "
            + "                                 oo.encounter_id "
            + "                                 AND ee.location_id = :location "
            + "                                 AND ee.encounter_type IN "
            + "                                     ( ${encounterTypes} ) "
            + "                                 AND oo.concept_id = "
            + "                                     ${question} "
            + "                                 AND ee.encounter_datetime <= "
            + "                                     :onOrBefore "
            + "                                 AND ee.voided = 0 "
            + "                                 AND oo.voided = 0 "
            + "                                              WHERE  ee.patient_id = p.patient_id "
            + "                                              ORDER  BY ee.encounter_datetime "
            + "                                                        DESC "
            + "                                              LIMIT  1)"
            + "ORDER BY  p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * @param minAge minimum age of a patient based on reporting end date
   * @param maxAge maximum age of a patient based on reporting end date
   * @return CohortDefinition
   */
  public CohortDefinition getAgeOnReportEndDate(Integer minAge, Integer maxAge) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(AgeOnReportEndDateDateCalculation.class).get(0));
    cd.setName("Age on Report end date");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addCalculationParameter("minAge", minAge);
    cd.addCalculationParameter("maxAge", maxAge);

    return cd;
  }

  /**
   * @param minAge minimum age of a patient based on preArt start date
   * @param maxAge maximum age of a patient based on preArt start date
   * @return CohortDefinition
   */
  public CohortDefinition getAgeOnPreArtDate(Integer minAge, Integer maxAge) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(AgeOnPreArtStartDateCalculation.class).get(0));
    cd.setName("Age on Pre ART start date");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addCalculationParameter("minAge", minAge);
    cd.addCalculationParameter("maxAge", maxAge);

    return cd;
  }

  /**
   * @param minAge minimum age in months
   * @param maxAge maximum age in months
   * @return CohortDefinition
   */
  public CohortDefinition getAgeInMonths(int minAge, int maxAge) {
    AgeCohortDefinition cd = new AgeCohortDefinition();
    cd.setName("Age in months");
    cd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
    cd.setMinAge(minAge);
    cd.setMinAgeUnit(DurationUnit.MONTHS);
    cd.setMaxAge(maxAge);
    cd.setMaxAgeUnit(DurationUnit.MONTHS);
    return cd;
  }

  /**
   * @param minAge minimum age of a patients who initiated ART during the inclusion period
   * @param maxAge maximum age of a patients who initiated ART during the inclusion period
   * @return CohortDefinition
   */
  public CohortDefinition getAgeInMonthsOnArtStartDate(Integer minAge, Integer maxAge) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(AgeInMonthsOnArtStartDateCalculation.class).get(0));
    cd.setName("Age in months based on ART start date");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addCalculationParameter("minAge", minAge);
    cd.addCalculationParameter("maxAge", maxAge);

    return cd;
  }

  /** @return CohortDefinition */
  public CohortDefinition getArtDateMinusDiagnosisDate() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(ArtDateMinusDiagnosisDateCalculation.class).get(0));
    cd.setName("ART date minus diagnosis date");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }

  /**
   * @param minAge minimum age of patient based on first viral load date
   * @param maxAge minimum age of patient based on first viral load date
   * @return CohortDefinition
   */
  public CohortDefinition getPatientAgeBasedOnFirstViralLoadDate(int minAge, int maxAge) {
    return generalSql(
        "getPatientAgeBasedOnFirstViralLoadDate",
        ViralLoadQueries.getPatientAgeBasedOnFirstViralLoadDate(
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            minAge,
            maxAge));
  }

  /**
   * @param minAge minimum age of patient based on PrEP Start date
   * @param maxAge maximum age of patient based on PrEP Start date
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientAgeBasedOnPrepStartDate(int minAge, int maxAge) {
    return generalSql(
        "getPatientAgeBasedOnPrepStartDate",
        PrepNewQueries.getPatientAgeBasedOnPrepStartDate(
            hivMetadata.getInitialStatusPrepUserConcept().getConceptId(),
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getPrepStartDateConcept().getConceptId(),
            minAge,
            maxAge));
  }

  /**
   * @param minAge minimum age of patient based on PrEP End date
   * @param maxAge maximum age of patient based on PrEP End date
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientAgeBasedOnPrepEndDate(int minAge, int maxAge) {
    return generalSql(
        "getPatientAgeBasedOnPrepEndDate",
        PrepCtQueries.patientAgeBasedOnPrepEndDate(minAge, maxAge));
  }

  /**
   * <b>Description:</b> Pregnant patients based on Prep
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPregnantPatientsBasedOnPrep() {
    return generalSql(
        "getPregnantPatientsBasedOnPrep",
        PrepCtQueries.pregnantPatientsBasedOnPrep(
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getPrepSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getCurrentStateOfTheWomanUuidConcept().getConceptId(),
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            commonMetadata.getBreastfeeding().getConceptId()));
  }

  /**
   * <b>Description:</b> Breastfeeding patients based on Prep
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getBreastfeedingPatientsBasedOnPrep() {
    return generalSql(
        "getPregnantPatientsBasedOnPrep",
        PrepCtQueries.breastfeedingPatientsBasedOnPrep(
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getPrepSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getCurrentStateOfTheWomanUuidConcept().getConceptId(),
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            commonMetadata.getBreastfeeding().getConceptId()));
  }
}
