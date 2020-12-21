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

import static org.openmrs.module.eptsreports.reporting.library.queries.ResumoMensalQueries.*;
import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.Date;
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
import org.openmrs.module.eptsreports.reporting.cohort.evaluator.ResumoMensalTransferredOutCohortDefinitionEvaluator;
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
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
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

  /**
   * <b>Name: A1</b>
   *
   * <p><b>Description:</b> Number of patients who initiated Pre-TARV at this HF by end of previous
   * month
   *
   * <h4>PT: Nº cumulativo de pacientes que iniciou Pré-TARV (Cuidados de HIV) nesta US até ao fim
   * do mês anterior</h4>
   *
   * <ul>
   *   <li>Select all patients:
   *       <ol>
   *         <li>registered in encounter “Master Card – Ficha Resumo” <b>(encounter id 53)</b> who
   *             have Pre-ART Start Date (PT”: “Data do Inicio Pre-TARV”) <b>Concept ID 23808</b>
   *             <code> < startDate </code>
   *         <li>enrolled in PRE-ART Program <b>program_id=1</b>, from patient program table) with
   *             enrollement date <code>date_enrolled < startDate</code> )
   *         <li>registered in “Abertura de Processo Clínico” <b>(encounter type id 5 or 7)</b> with
   *             “Data de Registo” <code>date_enrolled < startDate</code>
   *       </ol>
   *   <li>Exclude all patients registered in encounter “Master Card – Ficha Resumo” <b>(encounter
   *       id 53)</b> who have the following conditions:
   *       <ol>
   *         <li>Transfer from other HF” (PT:“Transferido de outra US”) <b>(Concept ID 1369)</b> is
   *             equal to <i>“YES”</i> <b>(Concept ID 1065)</b>
   *         <li>Type of Patient Transferred From (PT”: “Tipo de Paciente Transferido”) <b>(Concept
   *             ID 6300)</b> = “Pre-TARV” (Concept ID ????)
   *         <li>Date of Transferred In From (PT”: “Data de Transferência”) <b>(Concept ID 1369
   *             obs_datetime)</b> <code> < startDate</code>
   *       </ol>
   *   <li>Exclude all patients registered as “Transferred-in” in Program Enrollment:
   *       <ol>
   *         Technical Specs:
   *         <li>
   *             <p>Table: patient_program <b>Criterias:</b> <code>
   *             program_id=1, patient_state_id=29 and start_date < startDate </code>
   *       </ol>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who initiated Pre-TARV at this HF by end of previous month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "A1I",
        map(
            getNumberOfPatientsInMasterCardWithArtLessThanStartDateA1(),
            "startDate=${startDate-1d},location=${location}"));
    cd.addSearch(
        "A1II",
        map(
            getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthA1(),
            "onOrAfter=${startDate-1d},location=${location}"));
    cd.addSearch(
        "A1III",
        map(
            getAllPatientsEnrolledInPreArtProgramWithDateEnrolledLessThanStartDateA1(),
            "startDate=${startDate-1d},location=${location}"));
    cd.addSearch(
        "A1IV",
        map(
            getAllPatientsRegisteredInEncounterType5or7WithEncounterDatetimeLessThanStartDateA1(),
            "onOrBefore=${startDate-1d},locationList=${location}"));

    cd.setCompositionString("(A1I OR A1III OR A1IV) AND NOT A1II");

    return cd;
  }

  /**
   * <b>Name: A1I</b>
   *
   * <p><b>Description:</b> Number of patients in Master Card with ART less than Start Date
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNumberOfPatientsInMasterCardWithArtLessThanStartDateA1() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("A1I");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.setQuery(
        getAllPatientsWithPreArtStartDateLessThanReportingStartDate(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getPreArtStartDate().getConceptId()));
    return sqlCohortDefinition;
  }

  /**
   * <b>Name: A2</b>
   *
   * <p><b>Description:</b>Number of patients who initiated Pre-TARV at this HF during the current
   * month month
   *
   * <h4>PT: Nº de pacientes que iniciou Pré-TARV (Cuidados de HIV) nesta US durante o mês</h4>
   *
   * <ul>
   *   <li>Select all patients registered in encounter <i>Master Card – Ficha Resumo</i>
   *       <b>encounter id 53</b> who have the following conditions:
   *       <ol>
   *         <li>Pre-ART Start Date (PT: Data do Inicio Pre-TARV”) <b>Concept ID 23808</b>
   *         <li>enrolled in PRE-ART Program <b>program_id=1</b>, from patient program table) with
   *             enrollement date
   *         <li>registered in “Abertura de Processo Clínico” <b>(encounter type id 5 or 7)</b> with
   *             “Data de Registo” encounter_datetime
   *         <li>with the most earliest date from above criterias in the reporting period <code>
   *              >=startDate and <=endDate</code>
   *       </ol>
   *   <li>Exclude all patients registered in encounter “Master Card – Ficha Resumo” <b>(encounter
   *       id 53)</b> who have the following conditions:
   *       <ol>
   *         <li>Transfer from other HF” (PT:“Transferido de outra US”) <b>(Concept ID 1369)</b> is
   *             equal to <i>“YES”</i> <b>(Concept ID 1065)</b>
   *         <li>Type of Patient Transferred From (PT”: “Tipo de Paciente Transferido”) <b>(Concept
   *             ID 6300)</b> = “Pre-TARV” (Concept ID ????)
   *         <li>Date of Transferred In From (PT”: “Data de Transferência”) <b>(Concept ID 1369
   *             obs_datetime)</b> <code> >=startDate and <=endDate </code>
   *       </ol>
   *   <li>Exclude all patients registered as “Transferred-in” in Program Enrollment:
   *       <ol>
   *         Technical Specs:
   *         <li>
   *             <p>Table: patient_program <b>Criterias:</b> <code>
   *             program_id=1, patient_state_id=29 and start_date >= startDate and <= endDate</code>
   *       </ol>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who initiated Pre-TARV at this HF during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "A2I",
        map(
            getNumberOfPatientInitiedPreArtDuringCurrentMothA2(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "A2II",
        map(
            getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthA2(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("A2I AND NOT A2II");

    return cd;
  }

  /**
   * <b>Name: A2I</b>
   *
   * <p><b>Description:</b> Number of patients who initiated Pre-ART during current month
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNumberOfPatientInitiedPreArtDuringCurrentMothA2() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("A2i");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.setQuery(
        getPatientsWhoInitiatedPreArtDuringCurrentMonthWithConditions(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getPreArtStartDate().getConceptId(),
            hivMetadata.getHIVCareProgram().getProgramId(),
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId()));
    return sqlCohortDefinition;
  }

  /**
   * <b>Name: C1</b>
   *
   * <p><b>Description:</b> Number of patients who initiated Pre-TARV during the current month
   *
   * <p><b>NOTE:</b> The composition returns patients with pre-TARV, excluding patients transferred
   * form other HFs
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthC1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("C1");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String transferBasedOnDateMappings =
        "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${locationList}";
    String inProgramStatesMappings =
        "startDate=${onOrAfter},endDate=${onOrBefore},location=${locationList}";
    cd.addSearch(
        "population",
        map(
            getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "exclusion",
        map(
            getAdditionalExclusionCriteriaForC1(
                transferBasedOnDateMappings, inProgramStatesMappings),
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));
    cd.setCompositionString("population AND NOT exclusion");
    return cd;
  }

  /**
   * <b>Name: A2II</b>
   *
   * <p><b>Description:</b> Number of patients transferred-in from another HFs during the current
   * month
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthA2() {

    EptsTransferredInCohortDefinition cd = new EptsTransferredInCohortDefinition();
    // cd.setTypeOfPatientTransferredFromAnswer(hivMetadata.getPreTarvConcept());
    cd.setProgramEnrolled(hivMetadata.getHIVCareProgram());
    cd.setProgramEnrolled2(hivMetadata.getARTProgram());
    cd.setPatientState(hivMetadata.getArtCareTransferredFromOtherHealthFacilityWorkflowState());
    cd.setPatientState2(hivMetadata.getArtTransferredFromOtherHealthFacilityWorkflowState());
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setB10Flag(new Boolean("false"));
    return cd;
  }

  /**
   * <b>Name: A3 </b>
   *
   * <p><b>Descrption:</b> Number of patients who initiated Pre-TARV at this HF until the end of
   * reporting period
   *
   * <p>PT: Nº cumulativo de pacientes que iniciou Pré-TARV (Cuidados de HIV) nesta unidade
   * sanitária até ao fim do mês;
   *
   * <p><b>Formula: </b><code>A3 = A1 + A2<</code>
   *
   * @return {@link CohortDefinition}
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
   * <b>Name: B1</b>
   *
   * <p><b>Description:</b> Number of patients who initiated TARV at this HF during the current
   * month
   *
   * <p>Excludes All transfered In
   *
   * <ul>
   *   <li>Select all patients with the earliest ART Start Date from the following criterias:
   *       <ol>
   *         <li>All patients who have their first drugs pick-up date (first encounter_datetime) by
   *             reporting end date in Pharmacy form FILA (Encounter Type ID 18): <code>
   * first occurrence of encounter datetime
   *                 Encounter Type Ids = 18
   * *</code> *
   *         <li>All patients who have initiated the ARV drugs [ ARV PLAN (Concept ID 1255) = START
   *             DRUGS (Concept ID 1256) at the pharmacy or clinical visits (Encounter Type IDs
   *             6,9,18) <code>
   * first occurrence of encounter datetime
   *                     Encounter Type Ids = 6,9,18
   *                     ARV PLAN (Concept Id 1255) = START DRUGS (Concept ID 1256)
   * </code>
   *         <li>All patients who have the first historical start drugs date (earliest concept ID
   *             1190) set in pharmacy or in clinical forms (Encounter Type IDs 6, 9, 18, 53)
   *             earliest “historical start date” <code>Encounter Type Ids = 6,9,18,53
   *             The earliest “Historical Start Date” (Concept Id 1190)
   * </code>
   *         <li>
   *             <p>All patients enrolled in ART Program (date_enrolled in program_id 2, from
   *             patient program table)
   *             <p><code>
   * program_enrollment date
   * program_id=2, patient_state_id=29 and date_enrolled
   * </code>
   *         <li>
   *             <p>All patients with first drugs pick up date (earliest concept ID 23866
   *             value_datetime) set in mastercard pharmacy form “Recepção/Levantou ARV” (Encounter
   *             Type ID 52) with Levantou ARV (concept id 23865) = Yes (concept id 1065)
   *             <p><code>
   * earliest “Date of Pick up”
   *                        Encounter Type Ids = 52
   *                        The earliest “Data de Levantamento” (Concept Id 23866 value_datetime) <= endDate
   *                        Levantou ARV (concept id 23865) = SIm (1065)
   *
   * </code>
   *       </ol>
   *   <li>and check if the selected ART Start Date falls in the reporting period (>= startDate and
   *       <= endDate)
   *   <li>Exclude all patients transferred-in
   *       <ol>
   *         <li>Registered in encounter “Master Card – Ficha Resumo” (encounter id 53) with
   *             “Transfer from other HF” (PT:“Transferido de outra US”) (Concept ID 1369) equal to
   *             “YES” (Concept ID 1065) AND Type of Patient Transferred From (PT”: “Tipo de
   *             Paciente Transferido”) (Concept ID 6300) = “TARV” (Concept ID 6276)
   *         <li>AND Date of MasterCard File Opening (PT”: “Data de Abertura da Ficha na US”)
   *             (Concept ID 23891 value_datetime) <= endDate
   *         <li>Registered as Transferred-in in Program Enrollment (1st State) Technical Specs:
   *             Table: patient_program Criterias: program_id=2, patient_state_id=29 and
   *             min(start_date)<=endDate
   *       </ol>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patientes who initiated TARV at this HF during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);

    CohortDefinition transferredIn =
        getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2E();

    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    String mappingsEndDate = "onOrBefore=${endDate},location=${location}";
    cd.addSearch("startedArt", map(startedArt, mappings));

    cd.addSearch("transferredIn", map(transferredIn, mappingsEndDate));

    cd.setCompositionString("startedArt AND NOT transferredIn");
    return cd;
  }

  /**
   * <b>Name: B2</b>
   *
   * <p><b>Description:</b> Number of patients transferred-in from another HFs during the current
   * month
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2E() {
    // TODO maybe we should be re-using
    // HivCohortQueries#getPatientsTransferredFromOtherHealthFacility
    // Waiting for BAs response
    EptsTransferredInCohortDefinition cd = new EptsTransferredInCohortDefinition();

    cd.setName("Number of patients transferred-in from another HFs during the current month");
    cd.setTypeOfPatientTransferredFromAnswer(hivMetadata.getArtStatus());
    cd.setPatientState(hivMetadata.getTransferredFromOtherHealthFacilityWorkflowState());
    cd.setProgramEnrolled(hivMetadata.getARTProgram());
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setB10Flag(new Boolean("true"));
    return cd;
  }

  /**
   * <b>Name: B2</b>
   *
   * <p><b>Description:</b> Number of patients transferred-in from another HFs during the current
   * month. Composition to exclude <b>B5 ({@link #getPatientsTransferredOutB5})</b>
   *
   * <ul>
   *   <li>B2i - Select all patients registered in encounter “Master Card – Ficha Resumo” (encounter
   *       id 53) who have the following conditions:
   *       <ol>
   *         <li>Transfer from other HF” (PT:“Transferido de outra US”) (Concept ID 1369) is equal
   *             to “YES” (Concept ID 1065); AND
   *         <li>Type of Patient Transferred From (PT”: “Tipo de Paciente Transferido”) (Concept ID
   *             6300) = “TARV” (Concept ID 6276) AND
   *         <li>Date of MasterCard File Opening (PT”: “Data de Abertura da Ficha na US”) (Concept
   *             ID 23891 value_datetime) >=startDate and <= endDate
   *       </ol>
   *       OR
   *   <li>B2ii - Select all patients registered as transferred-in in Program Enrollment
   *       <p>Technical Specs: <i>Table: patient_program</i> <b>Criterias:</b> <code>
   *       program_id=2, patient_state_id=29 and start_date >=startDate and <=endDate
   * </code>
   *   <li>Exclude all Transferred-in registered by end of previous month (B2i [< startDate] OR B2ii
   *       [>startDate]) except transferred-out patients by end of previous month (B5 inclusion
   *       criterias for < startDate) according to 13 MOH Transferred-out patients by end of the
   *       period (Any State) defined in the common queries <a
   *       href="https://docs.google.com/document/d/1EtpeIn-6seD5skZJteCdANhxkKXQye9RckGV2eoYj6c/edit?pli=1#">link</a>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2() {
    // TODO maybe we should be re-using
    // HivCohortQueries#getPatientsTransferredFromOtherHealthFacility
    // Waiting for BAs response

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}";

    cd.addSearch("B2", map(getTransferredInPatients(false), mapping));

    cd.addSearch(
        "B2Exlcusion",
        map(getTransferredInPatients(true), "onOrAfter=${onOrAfter-1},location=${location}"));

    CohortDefinition transferredOut = getPatientsTransferredOutB5(false);

    cd.addSearch("B5", map(transferredOut, "onOrBefore=${onOrAfter},location=${location}"));

    cd.setCompositionString("B2 AND NOT ( B2Exlcusion AND NOT B5)");

    return cd;
  }

  /**
   * <b>Name: B3</b>
   *
   * <p><b>Description:</b> Number of patients who restarted the treatment during the current month
   * <br>
   *
   * <p>PT: Nº de reinícios TARV durante o mês <br>
   *
   * <p><code>B3 = B13 + B9 - B12 - B1 - B2</code>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithStartDrugs() {
    EncounterWithCodedObsCohortDefinition cd = getStateOfStayCohort();
    cd.setName("Number of patients who restarted the treatment during the current month");
    cd.addIncludeCodedValue(hivMetadata.getStartDrugs());
    return cd;
  }

  /**
   * <b>Name: B5: Number of patientes transferred out during the current month. (PT: Nº de
   * transferidos para outras US em TARV durante o mês)</b>
   *
   * <p><b>Description:</b> Number of patients transferred out during the current month.
   *
   * <ol>
   *   <li>Select all patients registered in encounter “Ficha Clinica-MasterCard” (encounter id 6)
   *       with LAST “Patient State” (PT:“Estado de Permanência”) (Concept ID 6273) equal to
   *       “Transferred Out” (PT: “Transferido Para”) (Concept ID 1706); AND encounter Date
   *       >=startDate and <=endDate
   *   <li>Select all patients registered in encounter “Ficha Resumo-MasterCard” (encounter id 53)
   *       with LAST “Patient State” (PT:“Estado de Permanência”) (Concept ID 6272) equal to
   *       “Transferred Out” (PT: “Transferido Para”) (Concept ID 1706) AND obs_datetime >=startDate
   *       and <=endDate
   *   <li>Select all patients registered as transferred-out in LAST Patient Program State during
   *       the reporting period <code>Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and
   *          Patient_State.state = 7 (Transferred-out) or
   *          Patient_State.start_date >= startDate and <= endDate
   *          Patient_State.end_date = null -> deve se selecionar a max (start_date)</code>
   *   <li>Except all patients who after the most recent date from 1 to 3 have a drugs pick up or
   *       consultation by reporting endDate: <code>Encounter Type ID= 6, 9, 18 and
   *          encounter_datetime> the most recent date and <=endDate
   *          or
   *          Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866 value_datetime) > the most recent date and <=endDate
   *       </code>
   * </ol>
   *
   * <p>The <b>parameter</b> below checks patients in last date as transfered out See <b>{@link
   * ResumoMensalTransferredOutCohortDefinitionEvaluator}</b>
   *
   * @param
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsTransferredOutB5(boolean maxDates) {

    ResumoMensalTransferredOutCohortDefinition cd =
        new ResumoMensalTransferredOutCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setMaxDates(maxDates);

    return cd;
  }

  /**
   * <b>Name: B6: Number of patientes with ART suspension during the current month. (PT: Nº de
   * suspensos TARV durante o mês)</b>
   *
   * <p><b>Description:</b> Number of patients with ART suspension during the current month.
   *
   * <ol>
   *   <li>Select all patients registered in encounter “Ficha Clinica - MasterCard” (encounter id 6)
   *       with “Patient State” (PT:“Estado de Permanência”) (Concept ID 6273) to “Suspended
   *       Treatment” (PT: “Suspenso”) (Concept ID 1709); AND Encounter Date >=startDate and
   *       <=endDate
   *   <li>Select all patients registered as suspended in Patient Program State during the reporting
   *       period <code>Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and
   *      Patient_State.state = 8 (Suspended treatment) or
   *      Patient_State.start_date >= startDate and <= endDate</code>
   *   <li>3.Select all patients registered in encounter “Ficha Resumo - MasterCard” (encounter id
   *       53) with “Patient State” (PT:“Estado de Permanência”) (Concept ID 6272) to “Suspended
   *       Treatment” (PT: “Suspenso”) (Concept ID 1709) AND OBS Datetime >=startDate and <=endDate
   *   <li>Except all patients who after the most recent date from 1.1 to 1.2, have a drugs pick up
   *       by reporting end date: <code>Encounter Type ID= 18 and
   *        encounter_datetime> the most recent date and <=endDate
   *        or
   *        Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866 value_datetime) > the most recent date and <=endDate
   *       </code>
   * </ol>
   *
   * <p>The <b>parameter</b> below checks the patient's state between the start and end date
   *
   * @param
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoSuspendedTreatmentB6(boolean useBothDates) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Number of patients with ART suspension during the current month");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT patient_id ");
    sql.append("FROM (SELECT patient_id, ");
    sql.append("             Max(suspended_date) suspended_date ");
    sql.append("      FROM (SELECT p.patient_id, ");
    sql.append("                   ps.start_date suspended_date ");
    sql.append("            FROM patient p ");
    sql.append("                     JOIN patient_program pp ");
    sql.append("                          ON p.patient_id = pp.patient_id ");
    sql.append("                     JOIN patient_state ps ");
    sql.append("                          ON pp.patient_program_id = ps.patient_program_id ");
    sql.append("            WHERE p.voided = 0 ");
    sql.append("              AND pp.voided = 0 ");
    sql.append("              AND pp.program_id = ${art} ");
    sql.append("              AND pp.location_id = :location ");
    sql.append("              AND ps.voided = 0 ");
    sql.append("              AND ps.state = ${suspendedState} ");
    if (useBothDates) {
      sql.append("              AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore ");
    } else {
      sql.append("              AND ps.start_date  <= :onOrBefore ");
    }
    sql.append("            UNION ");
    sql.append("            SELECT p.patient_id, ");
    sql.append("                   e.encounter_datetime suspended_date ");
    sql.append("            FROM patient p ");
    sql.append("                     JOIN encounter e ");
    sql.append("                          ON p.patient_id = e.patient_id ");
    sql.append("                     JOIN obs o ");
    sql.append("                          ON e.encounter_id = o.encounter_id ");
    sql.append("            WHERE p.voided = 0 ");
    sql.append("              AND e.voided = 0 ");
    sql.append("              AND e.location_id = :location ");
    sql.append("              AND e.encounter_type = ${adultSeg} ");
    if (useBothDates) {
      sql.append("              AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore ");
    } else {
      sql.append("              AND e.encounter_datetime  <= :onOrBefore ");
    }
    sql.append("              AND o.voided = 0 ");
    sql.append("              AND o.concept_id IN (${artStateOfStay}, ${preArtStateOfStay}) ");
    sql.append("              AND o.value_coded = ${suspendedConcept} ");
    sql.append("            UNION ");
    sql.append("            SELECT p.patient_id, ");
    sql.append("                   o.obs_datetime suspended_date ");
    sql.append("            FROM patient p ");
    sql.append("                     JOIN encounter e ");
    sql.append("                          ON p.patient_id = e.patient_id ");
    sql.append("                     JOIN obs o ");
    sql.append("                          ON e.encounter_id = o.encounter_id ");
    sql.append("            WHERE p.voided = 0 ");
    sql.append("              AND e.voided = 0 ");
    sql.append("              AND e.location_id = :location ");
    sql.append("              AND e.encounter_type = ${masterCard} ");
    if (useBothDates) {
      sql.append("              AND o.obs_datetime BETWEEN :onOrAfter AND :onOrBefore ");
    } else {
      sql.append("              AND o.obs_datetime  <= :onOrBefore ");
    }
    sql.append("              AND o.voided = 0 ");
    sql.append("              AND o.concept_id IN (${artStateOfStay}, ${preArtStateOfStay}) ");
    sql.append("              AND o.value_coded = ${suspendedConcept}) transferout ");
    sql.append("      GROUP BY patient_id) max_transferout ");
    sql.append("WHERE patient_id NOT IN (SELECT p.patient_id ");
    sql.append("                         FROM patient p ");
    sql.append("                                  JOIN encounter e ");
    sql.append("                                       ON p.patient_id = e.patient_id ");
    sql.append("                         WHERE p.voided = 0 ");
    sql.append("                           AND e.voided = 0 ");
    sql.append("                           AND e.encounter_type IN (${fila}) ");
    sql.append("                           AND e.location_id = :location ");
    sql.append("                           AND e.encounter_datetime > suspended_date ");
    sql.append("                           AND e.encounter_datetime <= :onOrBefore ");
    sql.append("                         UNION ");
    sql.append("                         SELECT p.patient_id ");
    sql.append("                         FROM patient p ");
    sql.append("                                  JOIN encounter e ");
    sql.append("                                       ON p.patient_id = e.patient_id ");
    sql.append("                                  JOIN obs o ");
    sql.append("                                       ON e.encounter_id = o.encounter_id ");
    sql.append("                         WHERE p.voided = 0 ");
    sql.append("                           AND e.voided = 0 ");
    sql.append("                           AND e.encounter_type = ${mcDrugPickup} ");
    sql.append("                           AND e.location_id = :location ");
    sql.append("                           AND o.concept_id = ${drugPickup} ");
    sql.append("                           AND o.value_datetime ");
    sql.append("                             > suspended_date");
    sql.append("                           AND o.value_datetime");
    sql.append("                             <= :onOrBefore);");

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
        "childSeg", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("fila", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put(
        "mcDrugPickup", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("drugPickup", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    cd.setQuery(sub.replace(sql));
    return cd;
  }

  /**
   * <b>Name: B8: Number of died patients during the current month. (PT: Nº de óbitos TARV durante o
   * mês)</b>
   *
   * <ul>
   *   <li>Select all patients registered in encounter “Ficha Clinica- Master Card” (encounter id 6)
   *       with LAST “Patient State” (PT:“Estado de Permanência”) (Concept ID 6273) is equal to
   *       “Patient Has Died” (PT: “Obitou”) (Concept ID 1366) and Encounter Date >=startDate and
   *       <=endDate
   *   <li>Select all patients registered in encounter “Ficha Resumo – Master Card” (encounter id
   *       53) with LAST “Patient State” (PT:“Estado de Permanência”) (Concept ID 6272) is equal to
   *       “Patient Has Died” (PT: “Obitou”) (Concept ID 1366) and Obs_date >=startDate and
   *       <=endDate
   *   <li>All patients registered as died in LAST Patient Program State during the reporing period
   *       Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 10
   *       (Died) Patient_State.start_date <= endDate >=startDate Patient_State.end_date = null ->
   *       deve ser selecionar a max (start_date)
   *   <li>All deaths registered in Patient Demographics during reporting period Person.Dead=1 and
   *       death_date <= :endDate and >=startDate
   *   <li>Except all patients who after the most recent date from 1 to 4, have a drugs pick up or
   *       consultation: Encounter Type ID= 6, 9, 18 and encounter_datetime> the most recent date
   *       and <=endDate or Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866
   *       value_datetime) > the most recent date and <= endDate
   * </ul>
   *
   * <p>The <b>parameter</b> below checks the patient's state between the start and end date
   *
   * @param
   * @return {@link CohortDefinition}
   */
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
    if (hasStartDate) {
      sql += "AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore ";
    } else {
      sql += "AND e.encounter_datetime <= :onOrBefore ";
    }
    sql =
        sql
            + "              AND o.voided = 0 "
            + "              AND o.concept_id = ${artStateOfStay}"
            + "              AND o.value_coded = ${patientDeadConcept} "
            + "      GROUP BY p.patient_id"
            + "            UNION"
            + "            SELECT p.patient_id, max(o.obs_datetime) AS death_date "
            + "            FROM patient p "
            + "                     JOIN encounter e "
            + "                          ON p.patient_id = e.patient_id "
            + "                     JOIN obs o "
            + "                          ON e.encounter_id = o.encounter_id "
            + "            WHERE p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND e.location_id = :locationList "
            + "              AND e.encounter_type = ${masterCard} ";
    if (hasStartDate) {
      sql += "AND o.obs_datetime BETWEEN :onOrAfter AND :onOrBefore ";
    } else {
      sql += "AND o.obs_datetime <= :onOrBefore ";
    }
    sql =
        sql
            + "AND o.voided = 0 "
            + "              AND o.concept_id = ${preArtStateOfStay}  "
            + "              AND o.value_coded = ${patientDeadConcept} "
            + "      GROUP BY p.patient_id"
            + "            UNION"
            + "       SELECT pg.patient_id, MAX(ps.start_date) AS death_date"
            + "       FROM patient p"
            + "         INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
            + "         INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "       WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 "
            + "         AND pg.program_id=${arvProgram}  AND ps.state=${deadState}  ";
    if (hasStartDate) {
      sql += "AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore ";
    } else {
      sql += "AND ps.start_date <= :onOrBefore ";
    }
    sql =
        sql
            + "AND location_id = :locationList "
            + "GROUP BY p.patient_id "
            + "            UNION"
            + "        SELECT p.person_id patient_id, p.death_date AS death_date"
            + "      FROM person p "
            + "      WHERE p.dead=1 AND p.voided = 0 ";
    if (hasStartDate) {
      sql += " AND p.death_date BETWEEN :onOrAfter AND :onOrBefore ";
    } else {
      sql += " AND p.death_date <= :onOrBefore ";
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
            + "       AND e.encounter_datetime > all_dead.death_date "
            + "       AND e.encounter_datetime <= :onOrBefore "
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
            + "       AND o.value_datetime > all_dead.death_date"
            + "       AND o.value_datetime <= :onOrBefore )";

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
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("farmacia", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put(
        "arvLevantamento", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("arvLevantamentoDate", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    cd.setQuery(sub.replace(sql));

    return cd;
  }

  /**
   * <b>Name: B10: Number of cumulative patients who started ART by end of previous month</b>
   *
   * <ol>
   *   <li>Select all patients with the earliest ART Start Date from the following criterias:
   *       <ul>
   *         <li>All patients who have their first drugs pick-up date (first encounter_datetime) by
   *             reporting end date in Pharmacy form FILA (Encounter Type ID 18): <code>
   *             first occurrence of encounter datetime
   *            Encounter Type Ids = 18</code>
   *         <li>b.All patients who have initiated the ARV drugs [ ARV PLAN (Concept ID 1255) =
   *             START DRUGS (Concept ID 1256) at the pharmacy or clinical visits (Encounter Type
   *             IDs 6,9,18) <code>first occurrence of encounter datetime
   *                Encounter Type Ids = 6,9,18
   *                ARV PLAN (Concept Id 1255) = START DRUGS (Concept ID 1256)</code>
   *         <li>All patients who have the first historical start drugs date (earliest concept ID
   *             1190) set in pharmacy or in clinical forms (Encounter Type IDs 6, 9, 18, 53)
   *             earliest “historical start date <code>Encounter Type Ids = 6,9,18,53
   *                    The earliest “Historical Start Date” (Concept Id 1190)</code>
   *         <li>All patients who have the first historical start drugs date (earliest concept ID
   *             1190) set in pharmacy or in clinical forms (Encounter Type IDs 6, 9, 18, 53)
   *             earliest “historical start date” <code>Encounter Type Ids = 6,9,18,53
   *                The earliest “Historical Start Date” (Concept Id 1190)</code>
   *         <li>All patients enrolled in ART Program (date_enrolled in program_id 2, from patient
   *             program table) <code> program_enrollment date
   *              program_id=2, patient_state_id=29 and date_enrolled</code>
   *         <li>All patients with first drugs pick up date (earliest concept ID 23866
   *             value_datetime) set in mastercard pharmacy form “Recepção/Levantou ARV” (Encounter
   *             Type ID 52) with Levantou ARV (concept id 23865) = Yes (concept id 1065) <code>
   *                  earliest “Date of Pick up”
   *                  Encounter Type Ids = 52
   *                  The earliest “Data de Levantamento” (Concept Id 23866 value_datetime) <= endDate
   *                  Levantou ARV (concept id 23865) = SIm (1065)
   *              </code>
   *       </ul>
   *   <li>And check if the selected ART Start Date is < startDate
   *   <li>Exclude all patients transferred-in
   *       <ul>
   *         <li>Registered in encounter “Master Card – Ficha Resumo” (encounter id 53) with
   *             “Transfer from other HF” (PT:“Transferido de outra US”) (Concept ID 1369) equal to
   *             “YES” (Concept ID 1065) AND Type of Patient Transferred From (PT”: “Tipo de
   *             Paciente Transferido”) (Concept ID 6300) = “TARV” (Concept ID 6276) AND Date of
   *             Transferred In From (PT”: “Data de Transferência”) (Concept ID 1369 obs_datetime)
   *             <startDate
   *         <li>Registered as Transferred-in in Program Enrollment Technical Specs: <code>
   *             Table: patient_program
   *              Criterias: program_id=2, patient_state_id=29 and start_date <startDate</code>
   *       </ul>
   * </ol>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoStartedArtByEndOfPreviousMonthB10() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of cumulative patients who started ART by end of previous month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "artStartDate",
        map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${startDate-1d},location=${location}"));

    cd.addSearch(
        "transferredIn",
        map(getTransferredInForB10(), "onOrAfter=${startDate-1d},location=${location}"));

    cd.setCompositionString("artStartDate AND NOT transferredIn");

    return cd;
  }

  /**
   * <b>Name:</b> Transferred-in for <b>B10</b>
   *
   * <p><b>Description:</b> Number of patients transferred-in from another HF during a period less
   * than startDate
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getTransferredInForB10() {
    EptsTransferredInCohortDefinition cd = new EptsTransferredInCohortDefinition();
    cd.setName(
        "Number of patients transferred-in from another HF during a period less than startDate B10 ");
    cd.setProgramEnrolled(hivMetadata.getARTProgram());
    cd.setPatientState(hivMetadata.getArtTransferredFromOtherHealthFacilityWorkflowState());
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setB10Flag(true);

    return cd;
  }

  /**
   * <b>Name: B.12 Number of active patients in ART by end of previous month (PT: Nº de pacientes
   * activos em TARV até ao fim do mês anterior (B13 do mês anterior )</b>
   *
   * <ul>
   *   <li>Select all patients from B10
   *   <li>OR
   *   <li>B2A- Select all patients registered in encounter “Master Card – Ficha Resumo” (encounter
   *       id 53) who have the following conditions:
   *       <ul>
   *         <li>“Transfer from other HF” (PT:“Transferido de outra US”) (Concept ID 1369) is equal
   *             to “YES” (Concept ID 1065);
   *         <li>Type of Patient Transferred From (PT”: “Tipo de Paciente Transferido”) (Concept ID
   *             6300) = “TARV” (Concept ID 6276)
   *         <li>Date of Transferred In From (PT”: “Data de Transferência”) (Concept ID 1369
   *             obs.datetime) <startDate
   *       </ul>
   *   <li>OR
   *   <li>Select all patients registered as transferred-in in Program Enrollment
   *       <p>Technical Specs: <code>
   *                Table: patient_program
   *                Criterias: program_id=2, patient_state_id=29 and start_date <startDate and
   *              </code>
   *   <li>Filter patients who had a drug pick up as
   *       <ul>
   *         <li>At least one encounter “Levantamento de ARV Master Card” (encounter id 52) with the
   *             following information: PickUp Date (PT: “Data de Levantamento”) (Concept ID 23866)
   *             <=(startDate-1D)
   *         <li>Patient had a drug pick in FILA (encounter id 18) by reporting start date minus 1
   *             Day (encounter_datetime <=(startDate-1)) and have next scheduled pick up SET
   *             (Concept ID 5096 value_datetime – not empty/null)
   *       </ul>
   *   <li>Except B5
   *   <li>Except B6
   *   <li>Except all patients who after the most recent date from 1.1 to 1.2, have a drugs pick up
   *       by reporting start date:
   *       <p><code>
   *              Encounter Type ID= 18 and
   *              encounter_datetime> the most recent date and <startDate
   *                or
   *                Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866 value_datetime) > the most recent date and <startDate
   *          </code>
   *   <li>
   *       <p>oB7E- All patients having the most recent date between last scheduled drug pickup date
   *       (Fila) or 30 days after last ART pickup date (Recepção – Levantou ARV) and adding 60 days
   *       and this date being less than reporting start Date. Select the most recent date from:
   *       <ul>
   *         <li>a.Last record of Next Drugs Pick Up Appointment (Concept ID 5096 value_datetime –
   *             not empty/null) from encounters of type 18 occurred by reporting start date
   *         <li>b.The most recent “Data de Levantamento” (Concept Id 23866 value_datetime-not
   *             empty/null) <startDate from encounters of type 52 plus(+) 30 days
   *       </ul>
   *       <p>And add 60 days and this date should be < startDate
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoWereActiveByEndOfPreviousMonthB12() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of active patients in ART by end of previous month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "B10",
        map(
            getPatientsWhoStartedArtByEndOfPreviousMonthB10(),
            "startDate=${startDate},location=${location}"));
    cd.addSearch(
        "B2A", map(getTransferredInForB10(), "onOrAfter=${startDate-1d},location=${location}"));

    cd.addSearch(
        "B5A",
        map(getPatientsTransferredOutB5(true), "onOrBefore=${startDate-1d},location=${location}"));

    cd.addSearch(
        "B6A",
        map(
            getPatientsWhoSuspendedTreatmentB6(false),
            "onOrBefore=${startDate-1d},location=${location}"));
    cd.addSearch(
        "B7A",
        map(
            getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB7(),
            "location=${location},date=${startDate-1d}"));
    cd.addSearch(
        "B8A",
        map(getPatientsWhoDied(false), "onOrBefore=${startDate-1d},locationList=${location}"));

    cd.addSearch(
        "drugPick",
        map(
            getPatientsWhoHadAtLeastDrugPickUp(),
            "startDate=${startDate-1d},location=${location}"));
    cd.setCompositionString("((B10 OR B2A) AND drugPick) AND NOT (B5A OR B6A OR B7A OR B8A)");

    return cd;
  }

  /**
   * <b>Description:</b> Patients who had a drug pick up as Levantamento de ARV Master Card and FILA
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoHadAtLeastDrugPickUp() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(" ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    String query =
        "                   select p.patient_id "
            + "                from patient p "
            + "                    inner join encounter e "
            + "                        on p.patient_id = e.patient_id "
            + "                    inner join obs o  "
            + "                        on o.encounter_id=e.encounter_id "
            + "                where p.voided =0    "
            + "                and e.voided = 0 "
            + "                and  o.voided =0 "
            + "                and e.location_id = :location "
            + "                and e.encounter_type = %d "
            + "                and o.concept_id= %d "
            + "                and o.value_datetime <= :startDate "
            + "                group by p.patient_id "
            + "                union "
            + "                select p.patient_id "
            + "                from patient p "
            + "                    inner join encounter e "
            + "                        on p.patient_id = e.patient_id "
            + "                    inner join obs o  "
            + "                        on o.encounter_id=e.encounter_id "
            + "                where p.voided =0    "
            + "                and e.voided = 0 "
            + "                and o.voided =0  "
            + "                and e.encounter_type = %d  "
            + "                and e.location_id = :location  "
            + "                and o.concept_id= %d  "
            + "                and e.encounter_datetime <= :startDate  "
            + "                and o.value_datetime is not  null  "
            + "                group by p.patient_id ";

    cd.setQuery(
        String.format(
            query,
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));
    return cd;
  }

  public CohortDefinition
      getNumberOfPatientsTransferredFromAnotherHealthFacilityByEndOfPreviousMonthB12() {
    SqlCohortDefinition transferredIn = new SqlCohortDefinition();
    transferredIn.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    transferredIn.addParameter(new Parameter("location", "location", Location.class));
    transferredIn.setQuery(
        getPatientsTransferredFromAnotherHealthFacilityByEndOfPreviousMonth(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getArtTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));
    return transferredIn;
  }

  /**
   * <b>Name: C1</b>
   *
   * <p><b>Description:</b> Number of patients who initiated Pre-TARV during the current month and
   * was screened for TB
   *
   * <p><b>NOTE:</b> The composition returns patients with pre-TARV, filtering patients who were
   * screened for TB
   *
   * @return {@link CohortDefinition}
   */

  // TODO: Method is only being used for tests
  // TODO: Method is duplicated in ResumoMensalCohortQueriesTest
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
   * <b>Name: C1</b>
   *
   * <p><b>Description:</b> Number of patients who initiated Pre-TARV during the current month and
   * was screened for TB
   *
   * <p><b>NOTE:</b> The composition returns patients with pre-TARV, filtering patients who were
   * screened for TB
   *
   * <ul>
   *   <li>A2: {@link
   *       ResumoMensalCohortQueries#getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2()}
   *       <b>AND</b>
   *   <li>
   *       <p>Filter all patients with following information in their FIRST visit “S.TARV – Adulto
   *       Seguimento or Ficha Clinica Master Card” (encounter id 6)
   *       <ol>
   *         <li>“Has TB Symptoms” (PT”: “Tem Sintomas TB?”) (Concept ID 23758) = (Concept ID 1065)
   *             OR (Concept ID 1066) <b>AND</b>
   *         <li>Encounter Date >=startDate and <=endDate (ONLY CONSIDER THE FIRST OCCURRENCE EVER)
   *       </ol>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndScreenedTbC1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Patients who initiated Pre-TARV during the current month and was screened for TB C1");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "A2", map(getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(), mappings));
    cd.addSearch("TB", map(getNumberOfPatientTbScreenedInFirstEncounter(), mappings));

    cd.setCompositionString("A2 AND TB");

    return cd;
  }

  /**
   * <b>Description:</b> Number of patients screened for TB in first encounter
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNumberOfPatientTbScreenedInFirstEncounter() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.addParameter(new Parameter("startDate", "startDate", Date.class));
    definition.addParameter(new Parameter("endDate", "endDate", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("tbSymptomsConcept", tbMetadata.getHasTbSymptomsConcept().getConceptId());
    map.put("yesConcept", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("noConcept", hivMetadata.getNoConcept().getConceptId());
    String query =
        "SELECT form.patient_id FROM "
            + "(SELECT p.patient_id,e.encounter_id "
            + "                                 FROM patient p "
            + "                                 INNER JOIN encounter e "
            + "                                 ON p.patient_id = e.patient_id "
            + "                                 INNER JOIN obs o "
            + "                   ON o.encounter_id = e.encounter_id "
            + "                 WHERE e.encounter_type = ${adultoSeguimentoEncounterType} "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.voided = 0 "
            + "                                 AND p.voided = 0 "
            + "                 AND o.voided = 0 "
            + "                         AND o.concept_id  = ${tbSymptomsConcept} "
            + "                         AND o.value_coded IN (${yesConcept},${noConcept}) "
            + "                         AND e.encounter_datetime BETWEEN :startDate AND :endDate) AS form "
            + "                                 RIGHT JOIN "
            + "                                 (SELECT minDate.patient_id, encounter_id, encounter_datetime,min_date FROM  "
            + "  (SELECT p.patient_id,MIN(e.encounter_datetime) AS min_date "
            + "               FROM encounter e "
            + "                                   INNER JOIN patient p on p.patient_id = e.patient_id "
            + "                                   INNER JOIN obs o "
            + "                                     ON o.encounter_id = e.encounter_id "
            + "                               WHERE e.encounter_type = ${adultoSeguimentoEncounterType} "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.voided = 0 "
            + "                                 AND p.voided = 0 "
            + "                                  AND o.voided = 0 "
            + "                            AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "                           GROUP BY p.patient_id) minDate "
            + "                          INNER JOIN encounter enc ON minDate.patient_id = enc.patient_id  "
            + "                          WHERE minDate.min_date = enc.encounter_datetime) AS encounters "
            + "                         ON encounters.encounter_id = form.encounter_id "
            + "                         WHERE form.patient_id IS NOT NULL"
            + "                         GROUP BY form.patient_id;";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    definition.setQuery(replacedQuery);
    return definition;
  }

  /**
   * <b>Name: C2</b>
   *
   * <p><b>Description:</b> Number of patients who initiated Pre-TARV during the current month and
   * started TPI
   *
   * <ul>
   *   <li>A2:{@link
   *       ResumoMensalCohortQueries#getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2()}
   *       <b>AND</b>
   *   <li><b>Profilaxia com Isoniazida (TPI)</b> {@link
   *       ResumoMensalCohortQueries#getPatientsWhoStartedTPI()}
   *       <h4>Exclusions</h4>
   *       <ul>
   *         <li>
   *             <p>Exclude all patients registered in encounter “Master Card – Ficha Resumo”
   *             (encounter id 53) who have the following conditions:
   *             <ul>
   *               <li>“Transfer from other HF” (PT:“Transferido de outra US”) (Concept ID 1369) is
   *                   equal to “YES” (Concept ID 1065);
   *               <li>Type of Patient Transferred From (PT”: “Tipo de Paciente Transferido”)
   *                   (Concept ID 6300) = “Pre-TARV” (Concept ID 6275)
   *               <li>Date of Transferred In From (PT”: “Data de Transferência”) (Concept ID 1369
   *                   obs_datetime) >=startDate – 1month and <=endDate
   *             </ul>
   *         <li>
   *             <p>Exclude all patients registered as “Transferred-in” in Program Enrollment:
   *             <i>Technical Specs:</i> <br>
   *             <code>
   *                     Table: patient_program<br />
   *                     Criterias: program_id=1, patient_state_id=29 and start_date >= startDate-1month and <= endDate
   *                 </code>
   *       </ul>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndStartedTpiC2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who initiated Pre-TARV during the current month and started TPI");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition a2 = getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2();
    CohortDefinition tpi = getPatientsWhoStartedTPI();

    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    String mappings1 = "startDate=${startDate-1m},endDate=${endDate},location=${location}";
    String transferBasedOnDateMappings =
        "onOrAfter=${onOrAfter-1m},onOrBefore=${onOrBefore},locationList=${locationList}";
    String inProgramStatesMappings =
        "startDate=${onOrAfter-1m},endDate=${onOrBefore},location=${locationList}";
    cd.addSearch("A2", map(a2, mappings1));
    cd.addSearch("TPI", map(tpi, mappings));
    cd.addSearch(
        "exclusions",
        map(
            getAdditionalExclusionCriteriaForC1(
                transferBasedOnDateMappings, inProgramStatesMappings),
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));
    cd.setCompositionString("(A2 AND TPI) AND NOT exclusions");

    return cd;
  }

  /**
   * <b>Name: C3</b>
   *
   * <p><b>Description:</b> Number of patients who initiated Pre-TARV during the current month and
   * was diagnosed for active TB
   *
   * <ul>
   *   <li>
   *   <li>A2:{@link
   *       ResumoMensalCohortQueries#getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2()}
   *       <b>AND</b>
   *   <li>TB: {@link
   *       ResumoMensalCohortQueries#getNumberOfPatientActiveTBInFirstAndSecondEncounter()}
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndDiagnosedForActiveTBC3() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Patients who initiated Pre-TARV during the current month and started TPI - it has getPatientsDiagnosedForActiveTB with - CodedObsOnFirstOrSecondEncounterCalculation");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    CohortDefinition tb = getNumberOfPatientActiveTBInFirstAndSecondEncounter();

    cd.addSearch(
        "A2",
        map(
            getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
            "startDate=${startDate-1m},endDate=${endDate},location=${location}"));

    cd.addSearch("TB", map(tb, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("A2 AND TB");
    return cd;
  }

  /**
   * <b>Description:</b> Number of patients diagnosed for active TB in first or second encounter
   *
   * <ul>
   *   <li>Filter all patients with following information in their FIRST OR SECOND visits “S.TARV –
   *       Adulto Seguimento” (encounter id 6)
   *       <ul>
   *         <li>“Active TB Diagnosis” (PT”: “Diagnóstico TB Activa”) (Concept ID 23761) = (Concept
   *             ID 1065)
   *         <li>Encounter Date >=startDate and <= endDate (ONLY CONSIDER THE FIRST AND SECOND
   *             OCCURRENCE EVER)
   *       </ul>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNumberOfPatientActiveTBInFirstAndSecondEncounter() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.addParameter(new Parameter("startDate", "startDate", Date.class));
    definition.addParameter(new Parameter("endDate", "endDate", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("activeTBConcept", tbMetadata.getActiveTBConcept().getConceptId());
    map.put("yesConcept", hivMetadata.getPatientFoundYesConcept().getConceptId());
    String query =
        " SELECT pt.patient_id "
            + "FROM patient pt  "
            + "    INNER JOIN  "
            + "    (SELECT p.patient_id, MIN( e.encounter_datetime) "
            + "    FROM  patient p "
            + "        INNER JOIN encounter e  "
            + "            ON e.patient_id = p.patient_id "
            + "    WHERE  e.encounter_type = ${adultoSeguimentoEncounterType} "
            + "        AND e.location_id = :location "
            + "        AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "        AND p.voided = 0 "
            + "        AND e.voided = 0 "
            + "    GROUP BY p.patient_id  "
            + " UNION "
            + " SELECT ee.patient_id,MIN( ee.encounter_datetime)  "
            + " FROM encounter ee"
            + "                     INNER JOIN ("
            + "                     SELECT p.patient_id, MIN( e.encounter_datetime)  minn_encounter_date "
            + "                         FROM  patient p "
            + "                             INNER JOIN encounter e  "
            + "                                 ON e.patient_id = p.patient_id "
            + "                         WHERE  e.encounter_type = ${adultoSeguimentoEncounterType}  "
            + "                             AND e.location_id = :location "
            + "                             AND e.encounter_datetime BETWEEN :startDate AND :endDate   "
            + "                             AND p.voided = 0 "
            + "                             AND e.voided = 0 "
            + "                         GROUP BY p.patient_id)  minn_encounter "
            + "                            ON minn_encounter.patient_id = ee.patient_id "
            + "                            WHERE ee.voided =0 "
            + "                            AND ee.encounter_type = ${adultoSeguimentoEncounterType} "
            + "                              AND ee.encounter_datetime  "
            + "                                   > minn_encounter.minn_encounter_date AND"
            + "									ee.encounter_datetime >= :endDate   "
            + "                  GROUP BY ee.patient_id"
            + ") min_encounter "
            + " ON pt.patient_id = min_encounter.patient_id "
            + "                             INNER JOIN encounter  enc "
            + "                               ON enc.patient_id = pt.patient_id "
            + "                       INNER JOIN obs o   "
            + "                               ON o.encounter_id = enc.encounter_id  "
            + ""
            + "                   WHERE o.voided = 0  "
            + "                        AND enc.voided = 0 "
            + "                  AND enc.encounter_datetime BETWEEN :startDate AND :endDate    "
            + "                  AND enc.encounter_type = ${adultoSeguimentoEncounterType}  "
            + "                  AND pt.voided = 0  "
            + "                  AND o.concept_id   = ${activeTBConcept}  "
            + "                  AND o.value_coded  =  ${yesConcept} "
            + "                  GROUP BY pt.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    definition.setQuery(replacedQuery);
    return definition;
  }

  private CohortDefinition getPatientsWhoInitiatedPreTARVDuringTheCurrentMonth() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("A2i");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.setQuery(
        getPatientsWhoInitiatedPreArtDuringCurrentMonthWithConditions(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getPreArtStartDate().getConceptId(),
            hivMetadata.getHIVCareProgram().getProgramId(),
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId()));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:<b> Number of patients that have ACTIVE TB = YES in their FIRST or SECOND S.TARV
   * – Adulto Seguimento encounter
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> Patients that have ISONIAZID USE = START DRUGS in their FIRST or SECOND
   * S.TARV – Adulto Seguimento (encounter id 6)
   *
   * <ul>
   *   <li>“ISONIAZID PROPYLAXIS” (PT”: “Profilaxia com Isoniazida”) (Concept ID 6122) = (Concept ID
   *       1256) <b>AND</b>
   *   <li>Encounter Date >=startDate and <= endDate (ONLY CONSIDER THE FIRST OR SECOND OCCURRENCE
   *       EVER)
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWhoStartedTPI() {
    CodedObsOnFirstOrSecondEncounterCalculation calculation =
        Context.getRegisteredComponents(CodedObsOnFirstOrSecondEncounterCalculation.class).get(0);
    CalculationCohortDefinition cd = new CalculationCohortDefinition(calculation);
    cd.setName("Patients who starte TPI");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter("concept", hivMetadata.getIsoniazidUsageConcept());
    cd.addCalculationParameter("valueCoded", hivMetadata.getStartDrugs());
    return cd;
  }

  /**
   * <b>Description:</b> Patients that have TB Symptoms = Yes in their FIRST S.TARV – Adulto
   * Seguimento
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> Number of patients transferred from other HF marked in master-card
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getTypeOfPatientTransferredFrom() {
    EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addEncounterType(hivMetadata.getMasterCardEncounterType());
    cd.addIncludeCodedValue(hivMetadata.getPreTarvConcept());
    return cd;
  }

  /**
   * <b>Description:</b> Number of Patients transferred from other HF = YES
   *
   * @return {@link CohortDefinition} TODO : method is never called
   */
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

  /**
   * <b>Description:</b> Additional exclusion criteria for <b>C1</b>
   *
   * <p>The <b>parameters</b> return mappings for <b>transferBasedOnObsDate</b> and
   * <b>programState</b>
   *
   * @param
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getAdditionalExclusionCriteriaForC1(
      String transferBasedOnDateMappings, String inProgramStatesMappings) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("All patients to be excluded for the C1 definition");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addSearch(
        "transferBasedOnObsDate",
        map(getPatientsTransferBasedOnObsDate(), transferBasedOnDateMappings));
    cd.addSearch(
        "getTypeOfPatientTransferredFrom",
        map(
            getTypeOfPatientTransferredFrom(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${locationList}"));
    cd.addSearch(
        "inProgramState",
        map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getHIVCareProgram().getProgramId(),
                hivMetadata
                    .getPateintTransferedFromOtherFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            inProgramStatesMappings));
    cd.setCompositionString(
        "transferBasedOnObsDate OR getTypeOfPatientTransferredFrom OR inProgramState");
    return cd;
  }

  /**
   * <b>Description:</b> Additional exclusion criteria for <b>C2</b>
   *
   * @return {@link CohortDefinition}
   *     <p>TODO : method is never called
   */
  public CohortDefinition getAdditionalExclusionCriteriaForC2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("All patients to be excluded for the C2 definition");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addSearch(
        "transferBasedOnObsDate",
        map(
            getPatientsTransferBasedOnObsDate(),
            "onOrAfter=${onOrAfter-1m},onOrBefore=${onOrBefore},locationList=${locationList}"));
    cd.addSearch(
        "getTypeOfPatientTransferredFrom",
        map(
            getTypeOfPatientTransferredFrom(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${locationList}"));
    cd.addSearch(
        "inProgramState",
        map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getHIVCareProgram().getProgramId(),
                hivMetadata
                    .getPateintTransferedFromOtherFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${onOrAfter-1m},endDate=${onOrBefore},location=${locationList}"));
    cd.setCompositionString(
        "transferBasedOnObsDate OR getTypeOfPatientTransferredFrom OR inProgramState");
    return cd;
  }

  /**
   * <b>Description:</b> Number of patients transfer based on obs date
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsTransferBasedOnObsDate() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
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

  /**
   * <b>Description:</b> Number of patients with Type of Patient Transferred From = 'Pre-TARV'
   *
   * @return {@link CohortDefinition}
   */
  // TODO: method is never used
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

  /**
   * <b>Description:<b> Number of patients who initiated TARV at a facility
   *
   * @return {@link CohortDefinition}
   */
  // TODO: method is never used
  private CohortDefinition getPatientsWhoInitiatedTarvAtAfacility() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.addParameter(new Parameter("value1", "Value 1", Date.class));
    cd.addParameter(new Parameter("value2", "Value 1", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getMasterCardEncounterType());
    cd.setQuestion(hivMetadata.getARVStartDateConcept());
    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);
    return cd;
  }

  /**
   * <b>Description:</b> Patients with Adulto Seguimento encounter with State of Stay question
   *
   * @return {@link CohortDefinition}
   */
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
   * <b>Description:<> Number of patients with master card drug pickup date
   *
   * @return {@link CohortDefinition}
   */
  private DateObsCohortDefinition getPatientsWithMasterCardDrugPickUpDate() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.addParameter(new Parameter("value1", "Value 1", Date.class));
    cd.addParameter(new Parameter("value2", "Value 1", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getMasterCardDrugPickupEncounterType());
    cd.setQuestion(hivMetadata.getArtDatePickupMasterCard());
    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);
    return cd;
  }

  /**
   * <b>Description:</b> All patients who have picked up drugs (Recepção Levantou ARV) – Master Card
   * by end of reporting period The earliest “Data de Levantamento”
   *
   * @return {@link CohortDefinition}
   */
  @DocumentedDefinition(value = "patientsWhoHavePickedUpDrugsMasterCardByEndReporingPeriod")
  public CohortDefinition getPatientsWhoHavePickedUpDrugsMasterCardByEndReporingPeriod() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsWhoHavePickedUpDrugsMasterCardByEndReporingPeriod");

    String query =
        "select p.patient_id "
            + " from patient p "
            + " inner join encounter e on  e.patient_id=p.patient_id "
            + " inner join obs o on  o.encounter_id=e.encounter_id "
            + " where  e.encounter_type = %s and o.concept_id = %s "
            + " and o.value_datetime <= :onOrBefore and e.location_id = :location "
            + " and p.voided =0 and e.voided=0  and o.voided = 0 group by p.patient_id";

    definition.setQuery(
        String.format(
            query,
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * <b>Description:</b> Patients with last Drug Pickup Date between boundaries
   *
   * @return {@link CohortDefinition}
   */
  private DateObsCohortDefinition getLastArvPickupDateCohort() {
    DateObsCohortDefinition cd = getPatientsWithMasterCardDrugPickUpDate();
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    return cd;
  }

  /**
   * <b>Description:</b> Patients with Drug pickup and Next Scheduled Pickup Date
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithFILAEncounterAndNextPickupDate() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients with FILA drug pickup and Scheduled Next Pickup Date");
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        GenericCohortQueries.getPatientsWithCodedObsValueDatetimeBeforeEndDate(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));
    return cd;
  }

  /**
   * <b>Name: E1</b>
   *
   * <p><b>Description:</b> Number of active patients in ART at the end of current month who
   * performed Viral Load Test (Annual Notification) B12 AND NOT (B5 OR B6 OR B7 OR B8)
   *
   * <ul>
   *   <li>B13: {@link ResumoMensalCohortQueries#getActivePatientsInARTByEndOfCurrentMonth()}
   *       <b>AND</b>
   *   <li>Filter all patients registered in encounter “S.TARV – Adulto Seguimento” (encounter id 6)
   *       with the following information:
   *       <ul>
   *         <li>Lab Requests (PT”: “Pedido Exames Laboratoriais”) (Concept ID 23722) = Viral Load
   *             (PT:”Carga Viral”) (Concept ID 856)
   *         <li>Encounter Date >=startDate and <= endDate as “LAB REQUEST DATE”
   *       </ul>
   *       <b>AND NOT</b>
   *   <li>oExclude all patients registered encounter “S.TARV – Adulto Seguimento” (encounter id 6)
   *       with the following information:
   *       <ul>
   *         <li>Lab Requests (PT”: “Pedido Exames Laboratoriais”) (Concept ID 23722) = (Concept ID
   *             856)
   *         <li>Encounter Date >=newStartDate and <= (startDate-1d)
   *       </ul>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNumberOfActivePatientsInArtAtEndOfCurrentMonthWithVlPerformed(
      boolean isMOH) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Number of active patients in ART at the end of current month who performed Viral Load Test (Annual Notification)");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "common",
        map(
            getActivePatientsInARTByEndOfCurrentMonth(isMOH),
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
                getE1ExclusionCriteria(
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getApplicationForLaboratoryResearch().getConceptId(),
                    hivMetadata.getHivViralLoadConcept().getConceptId())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(common AND F) AND NOT E1x");
    return cd;
  }

  /**
   * <b>Description:</b> Standard columns for <b>E1, E2 and E3</b> implementation
   *
   * <p><b>NOTE:</b> B12 = B13 AND NOT (B4 OR B9), this common for the 3 E columns B4 = B1+B2+B3 B9
   * = B5+B6+B7+B8 This just implementation of <b>B12</b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getStandardDefinitionForEcolumns() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Standard columns for E1, E2 and E3 implementation");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "B13",
        map(
            getActivePatientsInARTByEndOfCurrentMonth(false),
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
            getPatientsTransferredOutB5(false),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "B6",
        map(
            getPatientsWhoSuspendedTreatmentB6(true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "B7",
        map(
            getNumberOfPatientsWhoAbandonedArtDuringCurrentMonthForB7(),
            "location=${location},onOrBefore=${endDate}"));
    cd.addSearch(
        "B8",
        map(
            getPatientsWhoDied(true),
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));
    cd.setCompositionString("B13 AND NOT(B1 OR B2 OR B3 OR B5 OR B6 OR B7 OR B8)");
    return cd;
  }

  /**
   * <b>Description:</b> Number of patients with lab requests
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithCodedObsAndAnswers(Concept question, Concept answer) {
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
   * <b>Description:</b> Number of patients with TB Screening
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithTBScreening() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients with TB Screening");
    cd.addParameter(new Parameter("startDate", "After Date", Date.class));
    cd.addParameter(new Parameter("endDate", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsWithTBScreening(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            tbMetadata.getHasTbSymptomsConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getNoConcept().getConceptId(),
            tbMetadata.getTBTreatmentPlanConcept().getConceptId()));
    return cd;
  }

  /**
   * <b>Description:</b> Number of patients who have viral load test done
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getViralLoadTestDone() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Viral load test");
    cd.addParameter(new Parameter("startDate", "After Date", Date.class));
    cd.addParameter(new Parameter("endDate", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        getPatientsHavingViralLoadResults(
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()));
    return cd;
  }

  /**
   * <b>Description:</b> Number of patients with coded observation
   *
   * @param
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> Combined viral load and viral load qualitative
   *
   * @return {@link CohortDefinition}
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
   *
   *
   * <h4>Name: E2</h4>
   *
   * <p>Number of active patients in ART at the end of current month who received a Viral Load Test
   * Result (Annual Notification) (PT: Dos activos em TARV no fim do mês (B.13), subgrupo que
   * recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual) B13=B12+B4-B9)
   *
   * <ul>
   *   <li>B13: {@link ResumoMensalCohortQueries#getActivePatientsInARTByEndOfCurrentMonth()}
   *       <b>AND</b>
   *   <li>Filter all patients registered in encounter “S.TARV – Adulto Seguimento” (encounter id 6)
   *       with the following information:
   *       <ul>
   *         <li>Viral Load (PT”: “Carga Viral”) (Concept ID 856) = any value Or Viral load Qual
   *             (PT: “Carga Viral – Qualitativa) (Concept ID 1305) = any value <b>AND</b>
   *         <li>Encounter Date >=startDate and <= endDate as “LAB RESULT DATE”
   *       </ul>
   *       <b>AND NOT</b>
   *   <li>Exclude all patients registered encounter “S.TARV – Adulto Seguimento” (encounter id 6)
   *       with the following information:
   *       <ul>
   *         <li>Viral Load (PT”: “Carga Viral”) (Concept ID 856) = any value Or Viral load Qual
   *             (PT: “Carga Viral – Qualitativa) (Concept ID 1305) = any value
   *         <li>Encounter Date >=newStartDate and <= (startDate-1d)
   *       </ul>
   * </ul>
   *
   * <p><b><Note:/b>If startDate=”21-Dez-yyyy” where “yyyy” is any year, then newStartDate =
   * startDate Else newStartDate = “21-Dez-(yyyy-1) where “yyyy” is the year from startDate
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "E2: Number of active patients in ART at the end of current month who received a Viral Load Test Result (Annual Notification)");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "C",
        map(
            getActivePatientsInARTByEndOfCurrentMonth(false),
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
                getE2ExclusionCriteria(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(C AND VL) AND NOT Ex2");
    return cd;
  }

  /**
   * <b>Name: E3</b>
   *
   * <p><b>Description:</b> Number of active patients in ART at the end of current month who
   * received supressed Viral Load Result (Annual Notification)
   *
   * <p> Number of active patients in ART at the end of current month who received supressed Viral
   * Load Result (Annual Notification) (PT: Dos activos em TARV no fim do mês (B.13), subgrupo que
   * recebeu resultado de CV com supressão virológica durante o mês (<1000 cópias/mL) (Notificação
   * anual!) B13=B12+B4-B9)
   *
   * <ul>
   *   <li>B13: {@link ResumoMensalCohortQueries#getActivePatientsInARTByEndOfCurrentMonth()}
   *       <b>AND</b>
   *   <li>Filter all patients registered in encounter “S.TARV – Adulto Seguimento” (encounter id 6)
   *       with the following information:
   *       <ul>
   *         <li>Viral Load (PT”: “Carga Viral”) (Concept ID 856) < 1000 Or Viral load Qual (PT:
   *             “Carga Viral – Qualitativa) (Concept ID 1305) = any value
   *         <li>Encounter Date >=startDate and <= endDate as “LAB RESULT DATE”
   *       </ul>
   *       <b>AND NOT</b>
   *   <li>Exclude all patients registered encounter “S.TARV – Adulto Seguimento” (encounter id 6)
   *       with the following information:
   *       <ul>
   *         <li>Viral Load (PT”: “Carga Viral”) (Concept ID 856) < 1000 Or Viral load Qual (PT:
   *             “Carga Viral – Qualitativa) (Concept ID 1305) = any value
   *         <li>Encounter Date >=newStartDate and <= (startDate-1d)
   *       </ul>
   * </ul>
   *
   * <p>If startDate=”21-Dez-yyyy” where “yyyy” is any year, then newStartDate = startDate Else
   * newStartDate = “21-Dez-(yyyy-1) where “yyyy” is the year from startDate
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getActivePatientsOnArtWhoReceivedVldSuppressionResults() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Number of active patients in ART at the end of current month who received supressed Viral Load Result (Annual Notification)");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "C",
        map(
            getActivePatientsInARTByEndOfCurrentMonth(false),
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
                getE3ExclusionCriteria(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(C AND (SUPP OR QUAL)) AND NOT Ex3");
    return cd;
  }

  /**
   * <b>Description:</b> Viral load suprresion
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getViralLoadSuppression() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Viral load suppression");
    cd.addParameter(new Parameter("startDate", "After Date", Date.class));
    cd.addParameter(new Parameter("endDate", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        getPatientsHavingViralLoadSuppression(
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()));
    return cd;
  }

  /**
   * <b>Name: F1</b>
   *
   * <p><b>Description:</b> Number of patients who had clinical appointment during the reporting
   * month <code>
   *     <ul>
   *        <li>Select all patients from encounter “Master Card  – Ficha Clinica” (encounter type id 6) that occurred between startDate and endDate: <br />
   *        Encounter_Datetime >=startDate and <= endDate</li>
   *      </ul>
   * </code>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthF1() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("F1: Number of patients who had clinical appointment during the reporting month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        getPatientsWithGivenEncounterType(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()));
    return cd;
  }

  /**
   * <b>Name: F2</b>
   *
   * <p><b>Description:</b> F2: Number of patients who had clinical appointment during the
   * reporting month and were screened for TB (PT: Dos pacientes que vieram à consulta durante o
   * mês, subgrupo que foi rastreado para TB)
   *
   * <ul>
   *   <ul>
   *     <li>Select all patients from encounter “Master Card – Ficha Clinica ” (encounter type id 6)
   *         that occurred between startDate and endDate: <code>
   *         Encounter Date >=startDate and <= endDate</code>
   *     <li>Filter all patients registered in encounter “Master Card – Ficha Clinica ” (encounter
   *         id 6) with the following information:
   *         <ul>
   *           <li>TB Screening (PT”: “Tuberculose – Tem Sintomas?”) (Concept ID 23758) = Yes (PT:
   *               “SIM”) (Concept ID 1065) or (Concept ID 23758) = No (PT: “NAO”) (Concept ID 1066)
   *           <li>Encounter Date >=startDate and <= endDate as “SCREENING DATE”
   *         </ul>
   *     <li>oExclude all patients registered encounter “Master Card – Ficha Clinica ” (encounter id
   *         6) with the following information:
   *         <ul>
   *           <li>TB Treatment (PT”: “Tratamento TB”) (Concept ID 1268) = any value
   *           <li>Encounter Date = ”SCREENING DATE”
   *         </ul>
   *   </ul>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthAndScreenedForTbF2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Number of patients who had clinical appointment during the reporting month and were screened for TB");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "F2F",
        map(
            getPatientsWithTBScreening(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("F2F");
    return cd;
  }

  /**
   * <b>Name: F3</b>
   *
   * <p>F3: Number of patients who had at least one clinical appointment during the year (PT: Nº de
   * pacientes que vieram, pelo menos, a uma consulta durante o ANO (Notificação anual!)
   * (Notificação anual!))
   *
   * <ul>
   *   <li>Select all patients from encounter “Master Card – Ficha Clinica ” (encounter id 6) that
   *       occurred between startDate and endDate: <br>
   *       <code>Encounter Date >=startDate and <= endDate AS “VISIT DATE”</code>
   *   <li>Exclude all patients registered encounter “Master Card – Ficha Clinica ” (encounter id 6)
   *       with the following information: <br>
   *       <code>
   *             Encounter Date >=newStartDate and < startDate
   *         </code> <br>
   *       <p><b> If startDate=”21-Dez-yyyy” where “yyyy” is any year, then newStartDate = startDate
   *       Else newStartDate = “21-Dez-(yyyy-1) where “yyyy” is the year from startDate</b>
   * </ul>
   *
   * <ul>
   *   <li>Exclude all patients registered as Transferred In during the statistical year:
   *       <ul>
   *         <li>B2i - Select all patients registered in encounter “Master Card – Ficha Resumo”
   *             (encounter id 53) who have the following conditions:
   *             <ul>
   *               <li>“Transfer from other HF” (PT:“Transferido de outra US”) (Concept ID 1369) is
   *                   equal to “YES” (Concept ID 1065) AND
   *               <li>Type of Patient Transferred From (PT”: “Tipo de Paciente Transferido”)
   *                   (Concept ID 6300) = “TARV” (Concept ID 6276) AND
   *               <li>Date of MasterCard File Opening (PT”: “Data de Abertura da Ficha na US”)
   *                   (Concept ID 23891 value_datetime) >=newStartDate and <= endDate
   *             </ul>
   *         <li>B2ii - Select all patients registered as transferred-in in Program Enrollment
   *             <h4>Technical Specs:</h4>
   *             <p><b>Table: patient_program</b><br>
   *             <code>
   *             Criterias: program_id=2, patient_state_id=29 and start_date >=newStartDate and <=endDate
   *             </code>
   *       </ul>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNumberOfPatientsWithAtLeastOneClinicalAppointmentDuringTheYearF3() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who had at least one clinical appointment during the year");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "F1",
        map(getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthF1(), mappings));

    cd.addSearch("Fx3", map(getExclusionForF3(), mappings));

    cd.addSearch(
        "TransferredInStaticalYear",
        map(
            getPatientsRegisteredAsTransferredInDuringTheStatisticalYear(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("F1 AND NOT Fx3 AND NOT TransferredInStaticalYear");
    return cd;
  }

  /**
   * <b>Description:</b> Number of patients registered as transferred in during the statistical year
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsRegisteredAsTransferredInDuringTheStatisticalYear() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients Registered As TransferredIn During The Statistical Year");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "Locaton", Location.class));

    cd.setQuery(
        ResumoMensalQueries.getPatientsRegisteredAsTransferredInDuringTheStatisticalYear(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId(),
            hivMetadata.getDateOfMasterCardFileOpeningConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getArtTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));

    return cd;
  }

  /**
   * <b>Description: F3</b> exclusion criteria
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getExclusionForF3() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Exclusions");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        getF3Exclusion(hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()));

    return sql;
  }

  /**
   * <b>Description:<b> Number of patients who have a consultation on the same date as that of
   * pre-ART
   *
   * @return {@link CohortDefinition}
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
   * <b>Name:B7: Number of patientes who Abandoned the ART during the current month. (PT: Nº de
   * abandonos TARV durante o mês)</b>
   *
   * <ul>
   *   <li>All patients having the most recent date between last scheduled drug pickup date (Fila)
   *       or 30 days after last ART pickup date (Recepção – Levantou ARV) and adding 60 days and
   *       this date being less than reporting end Date.
   *       <ol>
   *         <li>Select the most recent date from:
   *             <ul>
   *               <li>a.Last record of Next Drugs Pick Up Appointment (Concept ID 5096
   *                   value_datetime – not empty/null) from encounters of type 18 registered by
   *                   endDate
   *               <li>The most recent “Data de Levantamento” (Concept Id 23866 value_datetime-not
   *                   empty/null) occurred by endDate from encounters of type 52 , plus(+) 30 days
   *             </ul>
   *         <li>And add 60 days and this date should be < endDate
   *       </ol>
   *   <li>Except all patients who abandoned the ART by previous month:
   *       <ol>
   *         <li>Select the most recent date from:
   *             <ul>
   *               <li>Last record of Next Drugs Pick Up Appointment (Concept ID 5096 value_datetime
   *                   – not empty/null) from encounters of type 18 registered by startDate
   *               <li>d.The most recent “Data de Levantamento” (Concept Id 23866 value_datetime-not
   *                   empty/null) occurred by startDate from encounters of type 52 , plus(+) 30
   *                   days
   *             </ul>
   *         <li>And add 60 days and this date should be < startDate
   *       </ol>
   *   <li>Except all patients who were transferred-out by reporting endDate: Same criterias as
   *       defined in B5, but instead of during the period (>=startDate and <=endDate), it should be
   *       by reporting endDate (<=endDate)
   *   <li>Except all patients who were suspended by reporting endDate: Same criterias as defined in
   *       B6, but instead of during the period (>=startDate and <=endDate), it should be by
   *       reporting endDate (<=endDate)
   *   <li>Except all patients who died by reporting endDate: Same criterias as defined in B8, but
   *       instead of during the period (>=startDate and <=endDate), it should be by reporting
   *       endDate (<=endDate)
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNumberOfPatientsWhoAbandonedArtDuringCurrentMonthForB7() {

    CompositionCohortDefinition ccd = new CompositionCohortDefinition();
    ccd.setName("Number of patients who Abandoned the ART during the current month");
    ccd.addParameter(new Parameter("location", "Location", Location.class));
    ccd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    ccd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));

    ccd.addSearch(
        "B7I",
        map(
            getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB7(),
            "date=${onOrBefore},location=${location}"));
    ccd.addSearch(
        "B7II",
        map(
            getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB7(),
            "date=${onOrAfter-1},location=${location}"));

    ccd.addSearch(
        "B7III",
        map(getPatientsTransferredOutB5(true), "onOrBefore=${onOrBefore},location=${location}"));
    ccd.addSearch(
        "B7IV",
        map(
            getPatientsWhoSuspendedTreatmentB6(false),
            "onOrBefore=${onOrBefore},location=${location}"));
    ccd.addSearch(
        "B7V", map(getPatientsWhoDied(false), "onOrBefore=${onOrBefore},locationList=${location}"));
    ccd.setCompositionString("B7I AND NOT (B7II OR B7III OR B7IV OR B7V)");

    return ccd;
  }

  /**
   * <b>Name: B7</b>
   *
   * <p><b>Description:</b> Number of patients who abandoned ART during previous month
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB7() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Number of patients who Abandoned the ART during the current month");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("date", "specifiedDate", Date.class));

    cd.setQuery(
        getNumberOfPatientsWhoAbandonedArtBySpecifiedDateB7(
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId()));
    return cd;
  }

  /**
   * <b>Name: A1III</b>
   *
   * <p><b>Descrption:</b> Number of patients enrolled in PRE-ART program id 1, with date enrolled
   * less than startDate
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getAllPatientsEnrolledInPreArtProgramWithDateEnrolledLessThanStartDateA1() {
    SqlCohortDefinition sqlPatientsEnrolledInPreART = new SqlCohortDefinition();
    sqlPatientsEnrolledInPreART.setName("patientsEnrolledInPreART");
    sqlPatientsEnrolledInPreART.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlPatientsEnrolledInPreART.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlPatientsEnrolledInPreART.addParameter(new Parameter("location", "Location", Location.class));
    sqlPatientsEnrolledInPreART.setQuery(
        getAllPatientsEnrolledInPreArtProgramWithDateEnrolledLessThanStartDate(
            hivMetadata.getHIVCareProgram().getProgramId()));

    return sqlPatientsEnrolledInPreART;
  }

  /**
   * <b>Name: A1IV</b>
   *
   * <p><b>Descrption:</b> Number of patients registered in <b>encounterType 5 or 7</b>, with date
   * enrolled less than startDate
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getAllPatientsRegisteredInEncounterType5or7WithEncounterDatetimeLessThanStartDateA1() {
    EncounterCohortDefinition cd = new EncounterCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Start Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getARVAdultInitialEncounterType());
    cd.addEncounterType(hivMetadata.getARVPediatriaInitialEncounterType());
    return cd;
  }

  /**
   * <b>Name: A1II</b>
   *
   * <p><b>Descrption:</b> Number of patients transferred-in from another HFs during a period less
   * than startDate
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthA1() {
    EptsTransferredInCohortDefinition cd = new EptsTransferredInCohortDefinition();
    cd.setName(
        "Number of patients transferred-in from another HF during a period less than startDate");
    cd.setProgramEnrolled(hivMetadata.getHIVCareProgram());
    cd.setProgramEnrolled2(hivMetadata.getARTProgram());
    cd.setPatientState(hivMetadata.getArtCareTransferredFromOtherHealthFacilityWorkflowState());
    cd.setPatientState2(hivMetadata.getArtTransferredFromOtherHealthFacilityWorkflowState());
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setB10Flag(new Boolean("false"));

    return cd;
  }

  /**
   * <b>Name: B13</b>
   *
   * <p><b>Description:</b> Number of active patients in ART by end of current month (PT: Nº activo
   * em TARV no fim do mês automaticamente calculado através da seguinte formula)
   *
   * <ol>
   *   <li>Select all patients with the earliest ART Start Date from the following criterias
   *       <ul>
   *         <li>
   *             <p>All patients who have their first drugs pick-up date (first encounter_datetime)
   *             by reporting end date in Pharmacy form FILA (Encounter Type ID 18): <code>
   *                      first occurrence of encounter datetime
   *                      Encounter Type Ids = 18
   *                   </code>
   *         <li>
   *             <p>All patients who have initiated the ARV drugs [ ARV PLAN (Concept ID 1255) =
   *             START DRUGS (Concept ID 1256) at the pharmacy or clinical visits (Encounter Type
   *             IDs 6,9,18) <code>first occurrence of encounter datetime
   *                     Encounter Type Ids = 6,9,18
   *                      ARV PLAN (Concept Id 1255) = START DRUGS (Concept ID 1256)
   *                      </code>
   *         <li>
   *             <p>All patients who have the first historical start drugs date (earliest concept ID
   *             1190) set in pharmacy or in clinical forms (Encounter Type IDs 6, 9, 18, 53)
   *             earliest “historical start date” <code>
   *                          Encounter Type Ids = 6,9,18,53
   *                          The earliest “Historical Start Date” (Concept Id 1190)
   *                    </code>
   *         <li>
   *             <p>All patients enrolled in ART Program (date_enrolled in program_id 2, from
   *             patient program table) <code>
   *                    program_enrollment date
   *                      program_id=2, patient_state_id=29 and date_enrolled
   *                </code>
   *         <li>
   *             <p>f.All patients with first drugs pick up date (earliest concept ID 23866
   *             value_datetime) set in mastercard pharmacy form “Recepção/Levantou ARV” (Encounter
   *             Type ID 52) with Levantou ARV (concept id 23865) = Yes (concept id 1065) <code>
   *                      earliest “Date of Pick up”
   *                      Encounter Type Ids = 52
   *                      The earliest “Data de Levantamento” (Concept Id 23866 value_datetime) <= endDate
   *                      Levantou ARV (concept id 23865) = SIm (1065)
   *                </code>
   *       </ul>
   *   <li>And check if the selected ART Start Date is <= endDate
   *   <li>
   *       <p>Filter patients who had a drug pick up as <code>
   *             At least one encounter “Levantamento de ARV Master Card” (encounter id 52) with the following information:
   *              PickUp Date (PT: “Data de Levantamento”) (Concept ID 23866) <=endDate
   *         </code> <code>
   *             Patient had a drug pick in FILA (encounter id 18) by reporting end date (encounter_datetime <=endDate) and have next
   *             scheduled pick up SET (Concept ID 5096 value_datetime – not empty/null)
   *         </code>
   * </ol>
   *
   * <p>EXCEPT (NOT)
   *
   * <ol>
   *   <li>B5: {@link ResumoMensalCohortQueries#getPatientsTransferredOutB5(boolean)}
   *   <li>B6: {@link ResumoMensalCohortQueries#getPatientsWhoSuspendedTreatmentB6(boolean)}
   *   <li>B7: {@link
   *       ResumoMensalCohortQueries#getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB7()}
   *   <li>B8: {@link ResumoMensalCohortQueries#getPatientsWhoDied(Boolean)}
   * </ol>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getActivePatientsInARTByEndOfCurrentMonth(boolean isMOH) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Number of active patients in ART by end of current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition startedArt = null;
    if (isMOH) {
      startedArt = genericCohortQueries.getStartedArtBeforeDateMOH(false);

    } else {
      startedArt = genericCohortQueries.getStartedArtBeforeDate(false);
    }

    CohortDefinition fila = getPatientsWithFILAEncounterAndNextPickupDate();

    CohortDefinition masterCardPickup =
        getPatientsWhoHavePickedUpDrugsMasterCardByEndReporingPeriod();

    CohortDefinition B5E = getPatientsTransferredOutB5(true);

    CohortDefinition B6E = getPatientsWhoSuspendedTreatmentB6(false);

    CohortDefinition B7E = getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB7();

    CohortDefinition B8E = getPatientsWhoDied(false);

    String mappingsOnDate = "onOrBefore=${endDate},location=${location}";
    String mappingsOnOrBeforeLocationList = "onOrBefore=${endDate},locationList=${location}";

    if (isMOH) {
      cd.addSearch(
          "startedArt",
          map(startedArt, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    } else {
      cd.addSearch("startedArt", map(startedArt, mappingsOnDate));
    }
    cd.addSearch("fila", map(fila, mappingsOnDate));
    cd.addSearch("masterCardPickup", map(masterCardPickup, mappingsOnDate));
    cd.addSearch("B5E", map(B5E, mappingsOnDate));
    cd.addSearch("B6E", map(B6E, mappingsOnDate));
    cd.addSearch("B7E", map(B7E, "date=${endDate},location=${location}"));
    cd.addSearch("B8E", map(B8E, mappingsOnOrBeforeLocationList));

    cd.setCompositionString(
        "startedArt AND (fila OR masterCardPickup) AND NOT (B5E OR B6E  OR B7E OR B8E )");

    return cd;
  }

  /**
   * <b>Description:</b> Number of patients transferred-In
   *
   * @param
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getTransferredInPatients(boolean isExclusion) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Transferred-in patients");
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getTransferredIn(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getDateOfMasterCardFileOpeningConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            isExclusion));
    return cd;
  }
}
