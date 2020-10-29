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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.generic.StartedArtOnPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class APSSResumoTrimestralCohortQueries {

  private HivMetadata hivMetadata;
  private TbMetadata tbMetadata;
  private GenericCohortQueries genericCohortQueries;
  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Autowired
  public APSSResumoTrimestralCohortQueries(
      HivMetadata hivMetadata,
      TbMetadata tbMetadata,
      GenericCohortQueries genericCohortQueries,
      ResumoMensalCohortQueries resumoMensalCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
    this.genericCohortQueries = genericCohortQueries;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
  }

  /**
   * <b>Name: A1</b>
   *
   * <p><b>Description:</b> Nº de crianças e adolescente de 8 -14 anos que receberam revelação total
   * do diagnóstico durante o trimestre
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getA1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("A1");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition patientsRegisteredInEncounterFichaAPSSANDPP =
        getAllPatientsRegisteredInEncounterFichaAPSSANDPP(
            hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept(),
            hivMetadata.getRevealdConcept());

    CohortDefinition patientAtAgeBetween8And14 =
        genericCohortQueries.getAgeOnReportEndDate(8, 14, false);

    cd.addSearch(
        "revealded",
        EptsReportUtils.map(
            patientsRegisteredInEncounterFichaAPSSANDPP,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "patientAtAgeBetween8And14",
        EptsReportUtils.map(
            patientAtAgeBetween8And14,
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("revealded AND patientAtAgeBetween8And14");

    return cd;
  }

  /**
   * <b>Name: B1</b>
   *
   * <p><b>Description:</b> Nº de pacientes que iniciou cuidados HIV nesta unidade sanitária durante
   * o trimestre e que receberam aconselhamento Pré-TARV no mesmo período
   *
   * <ul>
   *   <li>Nº de pacientes que iniciou Pré-TARV (Cuidados de HIV) [ {@link
   *       ResumoMensalCohortQueries#getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1}
   *       from Resumo Mensal only changes the period to quarterly)]
   *   <li>And filter all patients registered in encounter “Ficha APSS&PP” (encounter_type = 35) who
   *       have the following conditions:
   *       <ul>
   *         <li>ACONSELHAMENTO PRÉ-TARV” (concept_id = 23886) with value_coded “SIM” (concept_id =
   *             1065)
   *         <li>And “encounter_datetime” Between StartDate and EndDate
   *       </ul>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getB1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setName("B1");
    CohortDefinition a1 =
        resumoMensalCohortQueries.getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1();

    cd.addSearch("A1", map(a1, "startDate=${startDate},location=${location}"));

    Concept preARTCounselingConceptQuestion = hivMetadata.getPreARTCounselingConcept();
    Concept patientFoundYesConceptAnswer = hivMetadata.getPatientFoundYesConcept();
    cd.addSearch(
        "APSSANDPP",
        map(
            getAllPatientsRegisteredInEncounterFichaAPSSANDPP(
                preARTCounselingConceptQuestion, patientFoundYesConceptAnswer),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("A1 AND APSSANDPP");

    return cd;
  }

  /**
   * <b>Name: C1</b>
   *
   * <p><b>Description:</b> Nº total de pacientes activos em TARV que receberam seguimento de adesão
   * durante o trimestre
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getC1() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("C1");

    return sqlCohortDefinition;
  }

  /**
   * <b>Name: D1</b>
   *
   * <p><b>Description:</b> Nº de pacientes que iniciou TARV (15/+ anos) nesta unidade sanitária no
   * trimestre anterior e que receberam o pacote completo de prevenção positiva até ao período de
   * reporte
   *
   * <ul>
   *   <li>Select all patients from who initiated ART during previous quarterly period
   *       (startDate-3months and endDate-3months):
   *       <ul>
   *         <li>Same criterias as B10 indicator from Resumo Mensal only changes the period to
   *             previous quarter)
   *       </ul>
   *   <li>And FILTER patients with age older or equal than 15 years, calculated at reporting
   *       endDate (endDate minus birthdate);
   *       <ul>
   *         <li>Note: patients without birthdate information should not be included.
   *       </ul>
   *   <li>And FILTER all patients registered in encounter “Ficha APSS&PP” (encounter_type = 35) who
   *       have the following conditions:
   *       <ul>
   *         <li>“PP1”(concept_id = 6317) with value_coded “SIM” (concecpt_id = 1065); and
   *         <li>“PP2” (concept_id = 6318) with value_coded “SIM” (concecpt_id = 1065); and
   *         <li>“PP3” (concept_id = 6319) with value_coded “SIM” (concecpt_id = 1065); and
   *         <li>“PP4” (concept_id = 6320) with value_coded “SIM” (concecpt_id = 1065); and
   *         <li>“PLANEAMENTO FAMILIAR” (concept_id = 5271) with value_coded “SIM” (concecpt_id =
   *             1065); and
   *         <li>“PP6” (concept_id = 6321) with value_coded “SIM” (concecpt_id = 1065); and
   *         <li>“PP7” (concept_id = 6322) with value_coded “SIM” (concecpt_id = 1065)
   *         <li>And “encounter_datetime” Between (startDate – 3 months) and endDate
   *       </ul>
   * </ul>
   *
   * <p><b>NOTE - 1: FIELDS “PP1” TO “PP7” CAN BE FILLED IN MORE THAN ONE CONSULTATION SO CONSIDER
   * ALL “FICHA APSS&PP” </b>(encounter_type = 35) WITHIN THE PERIOD [(startDate – 3 months) and
   * endDate]
   *
   * <p><b>NOTE - 2: Only consider patients who have more than 15 years old during StartDate and
   * EndDate</b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getD1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Nº de pacientes que iniciou TARV (15/+ anos) no trimestre anteriaor");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = this.getPatientsWhoStartedArtByEndOfPreviousMonthB10();
    CohortDefinition patientAtAge15OrOlder =
        genericCohortQueries.getAgeOnReportEndDate(15, null, false);
    CohortDefinition registeredInFichaAPSSPP = this.getPatientsRegisteredInFichaAPSSPP();

    cd.addSearch(
        "startedArt",
        EptsReportUtils.map(
            startedART, "startDate=${startDate-3m},endDate=${endDate-3m},location=${location}"));

    cd.addSearch(
        "patientAtAge15OrOlder",
        EptsReportUtils.map(
            patientAtAge15OrOlder,
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "registeredInFichaAPSSPP",
        EptsReportUtils.map(
            registeredInFichaAPSSPP,
            "startDate=${startDate-3m},endDate=${endDate},location=${location}"));

    cd.setCompositionString("startedArt AND patientAtAge15OrOlder AND registeredInFichaAPSSPP");

    return cd;
  }

  /**
   * <b>Name: E1</b>
   *
   * <p><b>Description:</b> Nº pacientes faltosos e abandonos referidos para chamadas e/ou visitas
   * de reintegração durante o trimestre
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getE1() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("E1");

    return sqlCohortDefinition;
  }

  /**
   * <b>Name: E2</b>
   *
   * <p><b>Description:</b> Nº de pacientes faltosos e abandonos contactados e/ou encontrados
   * durante o trimestre, (dos referidos no mesmo período)
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getE2() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("E2");

    return sqlCohortDefinition;
  }

  /**
   * <b>Name: E3</b>
   *
   * <p><b>Description:</b> Nº de pacientes faltosos e abandonos que retornaram a unidade sanitária
   * durante o trimestre, (dos contactados e/ou encontrados no mesmo período)
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getE3() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("E3");

    return sqlCohortDefinition;
  }

  public SqlCohortDefinition getAllPatientsRegisteredInEncounterFichaAPSSANDPP(
      Concept question, Concept answer) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All Patients Registered In Encounter Ficha APSS AND PP");

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "prevencaoPositivaSeguimentoEncounterType",
        hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("question", question.getConceptId());
    map.put("answer", answer.getConceptId());

    String query =
        " SELECT p.patient_id "
            + "FROM patient p "
            + "     INNER JOIN encounter e "
            + "        ON e.patient_id = p.patient_id "
            + "     INNER JOIN obs o "
            + "        ON o.encounter_id=e.encounter_id "
            + "WHERE p.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.encounter_type = ${prevencaoPositivaSeguimentoEncounterType} "
            + "    AND o.concept_id = ${question} "
            + "    AND o.value_coded = ${answer} "
            + "    AND encounter_datetime "
            + "        BETWEEN :startDate AND  :endDate"
            + "    AND e.location_id = :location "
            + "    ";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    cd.setQuery(replacedQuery);

    return cd;
  }

  public CohortDefinition getPatientsWhoStartedArtByEndOfPreviousMonthB10() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of cumulative patients who started ART by end of previous month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "artStartDate",
        map(
            this.getStartedArtBeforeDate(false),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "transferredIn",
        map(
            this.getTransferredInForB10(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("artStartDate AND NOT transferredIn");

    return cd;
  }

  private CohortDefinition getStartedArtBeforeDate(boolean considerTransferredIn) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(StartedArtOnPeriodCalculation.class).get(0));
    cd.setName("Art start date");
    cd.addCalculationParameter("considerTransferredIn", considerTransferredIn);
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  private CohortDefinition getTransferredInForB10() {
    EptsTransferredInCohortDefinition cd = new EptsTransferredInCohortDefinition();
    cd.setName(
        "Number of patients transferred-in from another HF during a period less than startDate B10 ");
    cd.setProgramEnrolled(hivMetadata.getARTProgram());
    cd.setPatientState(hivMetadata.getArtTransferredFromOtherHealthFacilityWorkflowState());
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setB10Flag(true);

    return cd;
  }

  private CohortDefinition getPatientsRegisteredInFichaAPSSPP() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All Patients Registered In Encounter Ficha APSS AND PP");

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "prevencaoPositivaSeguimentoEncounterType",
        hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("preARTCounselingConcept", hivMetadata.getPreARTCounselingConcept().getConceptId());
    map.put("patientFoundYesConcept", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("pp1Concept", hivMetadata.getPP1Concept().getConceptId());
    map.put("pp2Concept", hivMetadata.getPP2Concept().getConceptId());
    map.put("pp3Concept", hivMetadata.getPP3Concept().getConceptId());
    map.put("pp4Concept", hivMetadata.getPP4Concept().getConceptId());
    map.put("familyPlanningConcept", hivMetadata.getfamilyPlanningConcept().getConceptId());
    map.put("pp6Concept", hivMetadata.getPP6Concept().getConceptId());
    map.put("pp7Concept", hivMetadata.getPP7Concept().getConceptId());

    String query =
        ""
            + " SELECT p.patient_id "
            + " FROM patient p "
            + "     INNER JOIN encounter e "
            + "        ON e.patient_id = p.patient_id "
            + "     INNER JOIN obs pp1 "
            + "        ON pp1.encounter_id=e.encounter_id "
            + "     INNER JOIN obs pp2 "
            + "        ON pp2.encounter_id=e.encounter_id "
            + "     INNER JOIN obs pp3 "
            + "        ON pp3.encounter_id=e.encounter_id "
            + "     INNER JOIN obs pp4 "
            + "        ON pp4.encounter_id=e.encounter_id "
            + "     INNER JOIN obs pp5 "
            + "        ON pp5.encounter_id=e.encounter_id "
            + "     INNER JOIN obs pp6 "
            + "        ON pp6.encounter_id=e.encounter_id "
            + "     INNER JOIN obs pp7 "
            + "        ON pp7.encounter_id=e.encounter_id "
            + " WHERE p.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND pp1.voided = 0 "
            + "    AND pp2.voided = 0 "
            + "    AND pp3.voided = 0 "
            + "    AND pp4.voided = 0 "
            + "    AND pp5.voided = 0 "
            + "    AND pp6.voided = 0 "
            + "    AND pp7.voided = 0 "
            + "    AND e.encounter_type = ${prevencaoPositivaSeguimentoEncounterType} "
            + "    AND (pp1.concept_id = ${pp1Concept} AND pp1.value_coded = ${patientFoundYesConcept}) "
            + "    AND (pp2.concept_id = ${pp2Concept} AND pp2.value_coded = ${patientFoundYesConcept}) "
            + "    AND (pp3.concept_id = ${pp3Concept} AND pp3.value_coded = ${patientFoundYesConcept}) "
            + "    AND (pp4.concept_id = ${pp4Concept} AND pp4.value_coded = ${patientFoundYesConcept}) "
            + "    AND (pp5.concept_id = ${familyPlanningConcept} AND pp5.value_coded = ${patientFoundYesConcept}) "
            + "    AND (pp6.concept_id = ${pp6Concept} AND pp6.value_coded = ${patientFoundYesConcept}) "
            + "    AND (pp7.concept_id = ${pp7Concept} AND pp7.value_coded = ${patientFoundYesConcept}) "
            + "    AND encounter_datetime "
            + "        BETWEEN :startDate AND :endDate "
            + "    AND e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    cd.setQuery(replacedQuery);

    return cd;
  }
}
