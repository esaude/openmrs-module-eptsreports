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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.generic.AgeOnArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.StartedArtBeforeDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.StartedArtOnPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenericCohortQueries {

  private HivMetadata hivMetadata;

  @Autowired
  public GenericCohortQueries(HivMetadata hivMetadata) {
    this.hivMetadata = hivMetadata;
  }

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

    List<Program> programs = new ArrayList<Program>();
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
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put(
        "arvAdultInitialEncounterTypeId",
        String.valueOf(hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId()));
    parameters.put(
        "arvPediatriaInitialEncounterTypeId",
        String.valueOf(hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId()));
    parameters.put(
        "hivCareProgramId", String.valueOf(hivMetadata.getHIVCareProgram().getProgramId()));
    parameters.put("artProgramId", String.valueOf(hivMetadata.getARTProgram().getProgramId()));
    return generalSql("baseCohort", BaseQueries.getBaseCohortQuery(parameters));
  }

  /**
   * Get patients states based on program, state and end of reporting period
   *
   * @param program
   * @param state
   * @return
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
   * @param program
   * @param state
   * @return
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
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get deceased patients based on patient states and person object");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "deceased",
        EptsReportUtils.map(
            generalSql(
                "deceased",
                "SELECT patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id AND pe.dead=1 WHERE pe.death_date <=:endDate"),
            "startDate=${startDate},endDate=${endDate}"));
    cd.setCompositionString("dead OR deceased");
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
        EptsReportUtils.map(
            getPatientsBasedOnPatientStatesBeforeDate(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId()),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "deceased",
        EptsReportUtils.map(
            generalSql(
                "deceased",
                "SELECT patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id AND pe.dead=1 WHERE pe.death_date <=:endDate"),
            "endDate=${endDate}"));
    cd.setCompositionString("dead OR deceased");
    return cd;
  }

  public CohortDefinition getAgeOnArtStartDate(
      Integer minAge, Integer maxAge, boolean considerPatientThatStartedBeforeWasBorn) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(AgeOnArtStartDateCalculation.class).get(0));
    cd.setName("Age on ART start date");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addCalculationParameter("minAge", minAge);
    cd.addCalculationParameter("maxAge", maxAge);
    cd.addCalculationParameter(
        "considerPatientThatStartedBeforeWasBorn", considerPatientThatStartedBeforeWasBorn);
    return cd;
  }

  public CohortDefinition getAgeOnArtStartDate(Integer minAge, Integer maxAge) {
    return getAgeOnArtStartDate(minAge, maxAge, false);
  }

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
}
