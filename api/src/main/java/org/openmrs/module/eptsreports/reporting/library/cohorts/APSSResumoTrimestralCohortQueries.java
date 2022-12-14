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
   * <p><b>Normal Flow of Events:</b>
   *
   * <ul>
   *   <li>Select all patients registered in encounter “Ficha APSS&PP” (encounter_type = 35) who
   *       have the following conditions:
   *       <ul>
   *         <li>“ESTADO DA REVELAÇÃO DO DIAGNÓSTICO a criança/adolescente” (concept_id = 6340) with
   *             value_coded “REVELADO”(concept_id= 6337)
   *         <li>And “encounter_datetime” Between StartDate and EndDate
   *       </ul>
   *   <li>Filter patients with age between 8 and 14 years, calculated at reporting endDate (endDate
   *       minus birthdate);
   * </ul>
   *
   * <p><b>Note 1:</b> <i> Exclude all patients who have “ESTADO DA REVELAÇÃO DO DIAGNÓSTICO a
   * criança/adolescente” (concept_id = 6340) with value_coded “REVELADO” (concept_id= 6337) before
   * startDate </i>
   *
   * <p><b>Note 2:</b><i> patients without birthdate information should not be included.</i>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getA1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("A1");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition reveladoInReportingPeriod =
        getAllPatientsRegisteredInEncounterFichaAPSSANDPP(
            hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept(),
            hivMetadata.getRevealdConcept(),
            false);

    CohortDefinition patientAtAgeBetween8And14 = genericCohortQueries.getAgeOnReportEndDate(8, 14);

    CohortDefinition reveladoBeforeStartDate =
        getAllPatientsRegisteredInEncounterFichaAPSSANDPP(
            hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept(),
            hivMetadata.getRevealdConcept(),
            true);

    cd.addSearch(
        "revealded",
        EptsReportUtils.map(
            reveladoInReportingPeriod,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "patientAtAgeBetween8And14",
        EptsReportUtils.map(
            patientAtAgeBetween8And14,
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "reveladoBeforeStartDate",
        EptsReportUtils.map(
            reveladoBeforeStartDate, "startDate=${startDate},location=${location}"));

    cd.setCompositionString(
        "(revealded AND patientAtAgeBetween8And14) AND NOT reveladoBeforeStartDate");

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

    CohortDefinition resumoMensalA2 =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2();

    cd.addSearch(
        "resumoMensalA2",
        map(resumoMensalA2, "startDate=${startDate},endDate=${endDate},location=${location}"));

    Concept preARTCounselingConceptQuestion = hivMetadata.getPreARTCounselingConcept();
    Concept patientFoundYesConceptAnswer = hivMetadata.getPatientFoundYesConcept();
    cd.addSearch(
        "APSSANDPP",
        map(
            getAllPatientsRegisteredInEncounterFichaAPSSANDPP(
                preARTCounselingConceptQuestion, patientFoundYesConceptAnswer, false),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("resumoMensalA2 AND APSSANDPP");

    return cd;
  }

  /**
   * <b>Name: C1</b>
   *
   * <p><b>Description:</b> Nº total de pacientes activos em TARV que receberam seguimento de adesão
   * durante o trimestre
   *
   * <p>Normal Flow of Events:
   *
   * <ul>
   *   <li>Select all active patients in TARV at end of reporting period ( endDate) from:
   *       <ul>
   *         <li>Pacientes activos em TARV no fim do mês (B13 indicator from Resumo Mensal only
   *             changes the period to quarterly)
   *       </ul>
   *   <li>And FILTER all patients registered in encounter “Ficha APSS&PP” (encounter_type = 35) who
   *       have the following conditions:
   *       <ul>
   *         <li>“PLANO DE ADESÃO - Horário; Esquecimento da dose; viagem” (concept_id =23716) with
   *             value_coded “SIM”/”NAO” [concept_id IN (1065, 1066)] OR
   *         <li>“EFEITOS SECUNDÁRIOS - O que pode ocorrer; Como manejar efeitos secundários”
   *             (concept_id =23887) with value_coded “SIM”/”NAO” [concept_id IN (1065, 1066)] OR
   *         <li>“ADESÃO ao TARV - Boa, Risco, Má, Dias de atraso na toma ARVs) = “B” ou “R” ou “M””
   *             (concept_id=6223) with value_coded “BOM” or “RISCO” or “MAU” [concept_id IN (1383,
   *             1749, 1385)]
   *       </ul>
   *   <li>AND “encounter_datetime” between (Patient ARTStartDate+30Days) and reporting endDate
   * </ul>
   *
   * <p><b>Note:</b> Patient ARTStartDate is the oldest date from the following dates: <br>
   * <i>Inclusion criteria of B10 Indicator from Resumo Mensal</i>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getC1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setName("C1");

    String mapping = "endDate=${endDate},location=${location}";

    CohortDefinition activeInART =
        this.resumoMensalCohortQueries.getPatientsWhoWereActiveByEndOfMonthB13();

    cd.addSearch("activeInART", EptsReportUtils.map(activeInART, mapping));

    cd.addSearch(
        "minArtStartDate",
        map(
            getFichaAPSSAndMinArtStartDate(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeInART AND minArtStartDate");

    return cd;
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

    CohortDefinition startedART =
        this.resumoMensalCohortQueries.getPatientsWhoStartedArtByEndOfPreviousMonthB10();
    CohortDefinition patientAtAge15OrOlder = genericCohortQueries.getAgeOnReportEndDate(15, null);
    CohortDefinition registeredInFichaAPSSPP = this.getPatientsRegisteredInFichaAPSSPP();

    cd.addSearch(
        "startedArt",
        EptsReportUtils.map(startedART, "startDate=${startDate-3m},location=${location}"));

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
   * <b>Name: E1-C</b>
   *
   * <p>Select all patients registered in encounter “LIVRO DE REGISTO DIÁRIO DE CHAMADAS E VISITAS
   * DOMICILIARES” (encounter_type = 61) with following conditions:
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getC() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(
        "E1: Número de pacientes faltosos e abandonos referidos para chamadas e/ou visitas de reintegração durante o trimestre");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "61",
        hivMetadata
            .getLivroRegistoChamadasVisistasDomiciliaresEncounterType()
            .getEncounterTypeId());
    map.put("23996", hivMetadata.getPatientElegibilityConcept().getConceptId());
    map.put("23993", hivMetadata.getReintegrationConcept().getConceptId());
    map.put("23997", hivMetadata.getElegibilityDateConcept().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e  "
            + "        ON p.patient_id = e.patient_id "
            + "    INNER JOIN obs elegivel  "
            + "        ON elegivel.encounter_id = e.encounter_id "
            + "    INNER JOIN obs data_elegibilidade  "
            + "        ON data_elegibilidade.encounter_id = e.encounter_id "
            + "WHERE "
            + "    p.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND elegivel.voided = 0 "
            + "    AND data_elegibilidade.voided = 0 "
            + "    AND e.encounter_type = ${61} "
            + "    AND elegivel.concept_id = ${23996} AND elegivel.value_coded = ${23993} "
            + "    AND data_elegibilidade.concept_id = ${23997} "
            + "    AND data_elegibilidade.value_datetime BETWEEN :startDate AND :endDate "
            + "    AND e.location_id = :location";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * <b>Name: A1</b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getA() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("A: ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    Map<String, Integer> map = new HashMap<>();
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        "SELECT "
            + "    final.patient_id "
            + " FROM "
            + "    (SELECT  "
            + "        most_recent.patient_id, "
            + "            DATE_ADD(MAX(most_recent.value_datetime1), INTERVAL 5 DAY) final_encounter_date "
            + "    FROM "
            + "        (SELECT  "
            + "        fila.patient_id, fila.value_datetime1 "
            + "    FROM "
            + "        (SELECT  "
            + "        pa.patient_id, MAX(obs.value_datetime) value_datetime1 "
            + "    FROM "
            + "        patient pa "
            + "    INNER JOIN encounter enc ON enc.patient_id = pa.patient_id "
            + "    INNER JOIN obs obs ON obs.encounter_id = enc.encounter_id "
            + "    WHERE "
            + "        pa.voided = 0 AND enc.voided = 0 "
            + "            AND obs.voided = 0 "
            + "            AND enc.encounter_type = ${18} "
            + "            AND obs.value_datetime IS NOT NULL "
            + "            AND obs.concept_id = ${5096} "
            + "            AND enc.location_id = :location "
            + "            AND obs.value_datetime <= :endDate "
            + "    GROUP BY pa.patient_id) AS fila UNION SELECT "
            + "        pa.patient_id, "
            + "            DATE_ADD(MAX(obs.value_datetime), INTERVAL 30 DAY) value_datetime2 "
            + "    FROM "
            + "        patient pa "
            + "    INNER JOIN encounter enc ON enc.patient_id = pa.patient_id "
            + "    INNER JOIN obs obs ON obs.encounter_id = enc.encounter_id "
            + "    INNER JOIN obs obs2 ON obs2.encounter_id = enc.encounter_id "
            + "    WHERE "
            + "        pa.voided = 0 AND enc.voided = 0 "
            + "            AND obs.voided = 0 "
            + "            AND obs2.voided = 0 "
            + "            AND obs.concept_id = ${23866} "
            + "            AND obs.value_datetime IS NOT NULL "
            + "            AND obs2.concept_id = ${23865} "
            + "            AND obs2.value_coded = ${1065} "
            + "            AND enc.encounter_type = ${52} "
            + "            AND enc.location_id = :location "
            + "            AND obs.value_datetime <= :endDate "
            + "    GROUP BY pa.patient_id) most_recent "
            + "    GROUP BY most_recent.patient_id "
            + "    HAVING final_encounter_date <= :endDate "
            + "        AND final_encounter_date >= :startDate) final "
            + "GROUP BY final.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * <b>Name: E1-B</b>
   *
   * <p><b>Description:</b> Number of patients who abandoned ART during previous month
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getB() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("B: ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    Map<String, Integer> map = new HashMap<>();
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        "SELECT "
            + "    final.patient_id "
            + " FROM "
            + "    (SELECT  "
            + "        most_recent.patient_id, "
            + "            DATE_ADD(MAX(most_recent.value_datetime1), INTERVAL 60 DAY) final_encounter_date "
            + "    FROM "
            + "        (SELECT  "
            + "        fila.patient_id, fila.value_datetime1 "
            + "    FROM "
            + "        (SELECT  "
            + "        pa.patient_id, MAX(obs.value_datetime) value_datetime1 "
            + "    FROM "
            + "        patient pa "
            + "    INNER JOIN encounter enc ON enc.patient_id = pa.patient_id "
            + "    INNER JOIN obs obs ON obs.encounter_id = enc.encounter_id "
            + "    WHERE "
            + "        pa.voided = 0 AND enc.voided = 0 "
            + "            AND obs.voided = 0 "
            + "            AND enc.encounter_type = ${18} "
            + "            AND obs.value_datetime IS NOT NULL "
            + "            AND obs.concept_id = ${5096} "
            + "            AND enc.location_id = :location "
            + "            AND obs.value_datetime <= :endDate "
            + "    GROUP BY pa.patient_id) AS fila UNION SELECT "
            + "        pa.patient_id, "
            + "            DATE_ADD(MAX(obs.value_datetime), INTERVAL 30 DAY) value_datetime2 "
            + "    FROM "
            + "        patient pa "
            + "    INNER JOIN encounter enc ON enc.patient_id = pa.patient_id "
            + "    INNER JOIN obs obs ON obs.encounter_id = enc.encounter_id "
            + "    INNER JOIN obs obs2 ON obs2.encounter_id = enc.encounter_id "
            + "    WHERE "
            + "        pa.voided = 0 AND enc.voided = 0 "
            + "            AND obs.voided = 0 "
            + "            AND obs2.voided = 0 "
            + "            AND obs.concept_id = ${23866} "
            + "            AND obs.value_datetime IS NOT NULL "
            + "            AND obs2.concept_id = ${23865} "
            + "            AND obs2.value_coded = ${1065} "
            + "            AND enc.encounter_type = ${52} "
            + "            AND enc.location_id = :location "
            + "            AND obs.value_datetime <= :endDate "
            + "    GROUP BY pa.patient_id) most_recent "
            + "    GROUP BY most_recent.patient_id "
            + "    HAVING final_encounter_date <= :endDate "
            + "        AND final_encounter_date >= :startDate) final "
            + "GROUP BY final.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(query));

    return cd;
  }

  public CohortDefinition getE1() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";

    CohortDefinition A = getA();
    CohortDefinition B = getB();
    CohortDefinition C = getC();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(A, mapping));

    compositionCohortDefinition.addSearch("B", EptsReportUtils.map(B, mapping));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(C, mapping));

    compositionCohortDefinition.setCompositionString("(A OR B) AND C");

    return compositionCohortDefinition;
  }

  /**
   * <b>Name: E2</b>
   *
   * <p><b>Description:</b> E1 AND D
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getE2() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";

    CohortDefinition D = getD();
    CohortDefinition E1 = getE1();

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(D, mapping));

    compositionCohortDefinition.addSearch("E1", EptsReportUtils.map(E1, mapping));

    compositionCohortDefinition.setCompositionString("(D OR E1)");

    return compositionCohortDefinition;
  }

  /**
   * <b>Name: E3</b>
   *
   * <p><b>Description:</b> Nº de pacientes faltosos e abandonos que retornaram a unidade sanitária
   * durante o trimestre, (dos contactados e/ou encontrados no mesmo período)
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNumOfMissingAndAbandonedWhoReturnedUsDuringQuarter() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(
        "E3: Número de pacientes faltosos e abandonos que retornaram a unidade sanitária durante o trimestre, (dos contactados e/ou encontrados no mesmo período");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "61",
        hivMetadata
            .getLivroRegistoChamadasVisistasDomiciliaresEncounterType()
            .getEncounterTypeId());
    map.put("24005", hivMetadata.getPatientReturnedConcept().getConceptId());
    map.put("24006", hivMetadata.getPatientIsBedriddenAtHomeConcept().getConceptId());
    map.put("24011", hivMetadata.getPatientReturnedAfterVisitConcept().getConceptId());
    map.put("24012", hivMetadata.getDatePatientReturnedAfterVisitConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT "
            + "  p.patient_id"
            + " FROM "
            + " patient p"
            + "   INNER JOIN "
            + "  (SELECT "
            + "    p.patient_id, e.encounter_datetime"
            + " FROM "
            + "  patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id"
            + "  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id"
            + " WHERE"
            + "   p.patient_id = e.patient_id"
            + "    AND p.voided = 0"
            + "    AND e.voided = 0"
            + "    AND o.voided = 0"
            + "    AND e.encounter_type = ${61}"
            + "   AND e.location_id = :location"
            + "   AND o.concept_id = ${24005}"
            + "     AND o.value_coded = ${1065}"
            + "    AND o2.concept_id = ${24006}"
            + "   AND o2.obs_datetime BETWEEN :startDate AND :endDate) AS returnedAfVisitX ON p.patient_id = returnedAfVisitX.patient_id"
            + "   INNER JOIN"
            + "   (SELECT "
            + "   p.patient_id, e.encounter_datetime"
            + "   FROM  "
            + "   patient p"
            + "   INNER JOIN encounter e ON p.patient_id = e.patient_id"
            + "  WHERE"
            + "    p.patient_id = e.patient_id"
            + "       AND p.voided = 0"
            + "      AND e.voided = 0"
            + "      AND e.encounter_type = ${35}"
            + "      AND e.location_id = :location) AS consultationX ON p.patient_id = consultationX.patient_id "
            + " WHERE "
            + " p.voided = 0 "
            + " AND returnedAfVisitX.encounter_datetime = consultationX.encounter_datetime"
            + " UNION SELECT "
            + "    p.patient_id"
            + " FROM"
            + "  patient p"
            + "       INNER JOIN"
            + "    (SELECT "
            + "      p.patient_id, e.encounter_datetime"
            + "  FROM"
            + "       patient p"
            + "  INNER JOIN encounter e ON e.patient_id = p.patient_id"
            + "  INNER JOIN obs o ON e.encounter_id = o.encounter_id"
            + "  INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id"
            + "  WHERE"
            + "   p.patient_id = e.patient_id"
            + "     AND p.voided = 0"
            + "      AND e.voided = 0"
            + "      AND o.voided = 0"
            + "       AND e.encounter_type = ${61}"
            + "      AND e.location_id = :location"
            + "      AND o.concept_id = ${24011}"
            + "      AND o.value_coded = ${1065}"
            + "     AND o2.concept_id = ${24012}"
            + "      AND o2.obs_datetime BETWEEN :startDate AND :endDate) AS returnedAfVisitY ON p.patient_id = returnedAfVisitY.patient_id"
            + "   INNER JOIN"
            + "  (SELECT "
            + "   p.patient_id, e.encounter_datetime "
            + " FROM"
            + "   patient p"
            + "  INNER JOIN encounter e ON p.patient_id = e.patient_id"
            + "   WHERE"
            + "      p.patient_id = e.patient_id"
            + "        AND p.voided = 0"
            + "        AND e.voided = 0"
            + "      AND e.encounter_type = ${35}"
            + "       AND e.location_id = :location) AS consultationAfVisitY ON p.patient_id = consultationAfVisitY.patient_id"
            + " WHERE"
            + "  p.voided = 0"
            + "     AND returnedAfVisitY.encounter_datetime = consultationAfVisitY.encounter_datetime;";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(query));

    return cd;
  }

  public CohortDefinition getE3() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String MAPPING = "startDate=${startDate},endDate=${endDate},location=${location}";

    CohortDefinition E2 = getE2();
    CohortDefinition E = getNumOfMissingAndAbandonedWhoReturnedUsDuringQuarter();
    compositionCohortDefinition.addSearch("E2", EptsReportUtils.map(E2, MAPPING));
    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(E, MAPPING));
    compositionCohortDefinition.setCompositionString("E2 AND E");
    return compositionCohortDefinition;
  }

  public SqlCohortDefinition getAllPatientsRegisteredInEncounterFichaAPSSANDPP(
      Concept question, Concept answer, boolean beforeStartDate) {
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
    StringBuilder query = new StringBuilder();

    query.append(" SELECT p.patient_id ");
    query.append(" FROM patient p ");
    query.append("     INNER JOIN encounter e ");
    query.append("        ON e.patient_id = p.patient_id ");
    query.append("     INNER JOIN obs o ");
    query.append("        ON o.encounter_id=e.encounter_id ");
    query.append(" WHERE p.voided = 0 ");
    query.append("    AND e.voided = 0 ");
    query.append("    AND o.voided = 0 ");
    query.append("    AND e.encounter_type = ${prevencaoPositivaSeguimentoEncounterType} ");
    query.append("    AND o.concept_id = ${question} ");
    query.append("    AND o.value_coded = ${answer} ");
    query.append("    AND encounter_datetime ");
    if (beforeStartDate) {
      query.append(" < :startDate ");
    } else {
      query.append("  BETWEEN :startDate AND  :endDate");
    }
    query.append(" AND e.location_id = :location ");

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

  /**
   * <b>Name: Indicator ID</b>
   *
   * <p><b>Description:</b> Filter all patients registered in encounter “LIVRO DE REGISTO DIÁRIO DE
   * CHAMADAS E VISITAS DOMICILIARES” (encounter_type = 61) with following conditions: /** <b>Name:
   * PatientsRegisteredInLogBook - D </b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsRegisteredInLogBook(String indicatorFlag) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(
        "D: patients registered in encounter “LIVRO DE REGISTO DIÁRIO DE CHAMADAS E VISITAS DOMICILIARES");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "61",
        hivMetadata
            .getLivroRegistoChamadasVisistasDomiciliaresEncounterType()
            .getEncounterTypeId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("23998", hivMetadata.getPatientContactedOnFirstAttemptConcept().getConceptId());
    map.put("23999", hivMetadata.getPatientContactedOnSecondAttemptConcept().getConceptId());
    map.put("24000", hivMetadata.getPatientContactedOnThirdAttemptConcept().getConceptId());
    map.put("24001", hivMetadata.getDateAgreedForReturnOnFirstCallConcept().getConceptId());
    map.put("24002", hivMetadata.getDateAgreedForReturnOnSecondCallConcept().getConceptId());
    map.put("24003", hivMetadata.getDateAgreedForReturnOnThirdCallConcept().getConceptId());
    map.put("24008", hivMetadata.getPatientFoundOnFirstAttemptConcept().getConceptId());
    map.put("24009", hivMetadata.getPatientFoundOnSecondAttemptConcept().getConceptId());
    map.put("24010", hivMetadata.getPatientFoundOnThirdAttemptConcept().getConceptId());
    map.put("23933", hivMetadata.getReturnDateOnFirstAttemptConcept().getConceptId());
    map.put("23934", hivMetadata.getReturnDateOnSecondAttemptConcept().getConceptId());
    map.put("23935", hivMetadata.getReturnDateOnThirdAttemptConcept().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.encounter_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN obs o1 "
            + "               ON o1.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_id = ${61} "
            + "       AND e.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND o.voided = 0 "
            + "       AND p.voided = 0 ";

    if (indicatorFlag.equalsIgnoreCase("D1")) {
      query +=
          " AND ((o.concept_id = ${23998}  AND o.value_coded = ${1065}) OR "
              + " (o.concept_id = ${23999}  AND o.value_coded = ${1065}) OR "
              + " (o.concept_id = ${24000}  AND o.value_coded = ${1065}))";
    }

    if (indicatorFlag.equalsIgnoreCase("D2")) {
      query +=
          " AND ((o.concept_id = ${24001}  AND o.value_datetime IS NOT NULL) OR "
              + " (o.concept_id = ${24002}  AND o.value_datetime IS NOT NULL) OR "
              + " (o.concept_id = ${24003}  AND o.value_datetime IS NOT NULL))";
    }

    if (indicatorFlag.equalsIgnoreCase("D3")) {
      query +=
          " AND ((o.concept_id = ${24008}  AND o.value_coded = ${1065}) OR "
              + " (o.concept_id = ${24009}  AND o.value_coded = ${1065}) OR "
              + " (o.concept_id = ${24010}  AND o.value_coded = ${1065}))";
    }

    if (indicatorFlag.equalsIgnoreCase("D4")) {
      query +=
          " AND ((o.concept_id = ${23933}  AND o.value_datetime IS NOT NULL) OR "
              + " (o.concept_id = ${23934}  AND o.value_datetime IS NOT NULL) OR "
              + " (o.concept_id = ${23935}  AND o.value_datetime IS NOT NULL))";
    }

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * D1- ‘Contactado na 1ª Tentativa ’(concept_id = 23998) value coded ‘SIM’ (concept_id =1065 ) or
   * ‘Contactado na 2ª Tentativa ’(concept_id = 23999) value coded ‘SIM’ (concept_id =1065 ) or
   * ‘Contactado na 3ª Tentativa ’(concept_id = 24000) value coded ‘SIM’ (concept_id =1065 )
   *
   * <p>D2- And ‘Data Combinada de Retorno na 1ª Tentativa’ (concept_id=24001) not null or ‘Data
   * Combinada de Retorno na 2ª Tentativa’ (concept_id = 24002) not null or ‘Data Combinada de
   * Retorno na 3ª Tentativa’ (concept_id=24003) not null; OR
   *
   * <p>D3- ‘Encontrado na 1ª Tentativa ’(concept_id = 24008) value coded ‘SIM’ (concept_id =1065 )
   * or ‘Encontrado na 2ª Tentativa ’(concept_id = 24009) value coded ‘SIM’ (concept_id =1065 ) or
   * ‘Encontrado na 3ª Tentativa ’(concept_id = 24010) value coded ‘SIM’ (concept_id =1065 )
   *
   * <p>D4- And ‘Data Combinada de Retorno na 1ª Tentativa’ (concept_id=23933) not null or ‘Data
   * Combinada de Retorno na 2ª Tentativa’ (concept_id = 23934) not null or ‘Data Combinada de
   * Retorno na 3ª Tentativa’ (concept_id=23935) not null;
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getD() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Patients registered in encounter “LIVRO DE REGISTO DIÁRIO DE CHAMADAS E VISITAS DOMICILIARES”");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";

    cd.addSearch("D1", map(this.getPatientsRegisteredInLogBook("D1"), mapping));

    cd.addSearch("D2", map(this.getPatientsRegisteredInLogBook("D2"), mapping));

    cd.addSearch("D3", map(this.getPatientsRegisteredInLogBook("D3"), mapping));

    cd.addSearch("D4", map(this.getPatientsRegisteredInLogBook("D4"), mapping));

    cd.setCompositionString("(D1 AND D2) OR (D3 AND D4)");

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

  public CohortDefinition getPatientsRegisteredInFichaAPSSPP() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All Patients Registered In Encounter Ficha APSS AND PP");

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "prevencaoPositivaSeguimentoEncounterType",
        hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("patientFoundYesConcept", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("pp1Concept", hivMetadata.getPP1Concept().getConceptId());
    map.put("pp2Concept", hivMetadata.getPP2Concept().getConceptId());
    map.put("pp3Concept", hivMetadata.getPP3Concept().getConceptId());
    map.put("pp4Concept", hivMetadata.getPP4Concept().getConceptId());
    map.put("familyPlanningConcept", hivMetadata.getfamilyPlanningConcept().getConceptId());
    map.put("pp6Concept", hivMetadata.getPP6Concept().getConceptId());
    map.put("pp7Concept", hivMetadata.getPP7Concept().getConceptId());

    String query =
        " SELECT p.patient_id "
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
            + "    AND e.location_id = :location "
            + " UNION "
            + "  SELECT  p.patient_id "
            + " FROM patient p "
            + " WHERE p.voided = 0"
            + "    AND p.patient_id IN ("
            + "                        SELECT e.patient_id "
            + "                        FROM encounter  e"
            + "                            INNER JOIN obs o"
            + "                                ON o.encounter_id = e.encounter_id"
            + "                        WHERE e.voided = 0"
            + "                            AND o.voided = 0"
            + "                            AND e.encounter_type = ${prevencaoPositivaSeguimentoEncounterType}"
            + "                            AND e.encounter_datetime"
            + "                                BETWEEN :startDate AND :endDate "
            + "                            AND e.location_id = :location"
            + "                            AND o.concept_id = ${pp1Concept} AND o.value_coded = ${patientFoundYesConcept} "
            + "                        )"
            + "    AND p.patient_id IN("
            + "                        SELECT e.patient_id "
            + "                        FROM encounter  e"
            + "                            INNER JOIN obs o"
            + "                                ON o.encounter_id = e.encounter_id"
            + "                        WHERE e.voided = 0"
            + "                            AND o.voided = 0"
            + "                            AND e.encounter_type = ${prevencaoPositivaSeguimentoEncounterType}"
            + "                            AND e.encounter_datetime"
            + "                                BETWEEN :startDate AND :endDate "
            + "                            AND e.location_id = :location"
            + "                            AND o.concept_id = ${pp2Concept} AND o.value_coded = ${patientFoundYesConcept}"
            + "                        )"
            + "    AND p.patient_id IN("
            + "                        SELECT e.patient_id "
            + "                        FROM encounter  e"
            + "                            INNER JOIN obs o"
            + "                                ON o.encounter_id = e.encounter_id"
            + "                        WHERE e.voided = 0"
            + "                            AND o.voided = 0"
            + "                            AND e.encounter_type = ${prevencaoPositivaSeguimentoEncounterType}"
            + "                            AND e.encounter_datetime"
            + "                                BETWEEN :startDate AND :endDate "
            + "                            AND e.location_id = :location"
            + "                            AND o.concept_id = ${pp3Concept} AND o.value_coded = ${patientFoundYesConcept}"
            + "                        )"
            + "    AND p.patient_id IN("
            + "                        SELECT e.patient_id "
            + "                        FROM encounter  e"
            + "                            INNER JOIN obs o"
            + "                                ON o.encounter_id = e.encounter_id"
            + "                        WHERE e.voided = 0"
            + "                            AND o.voided = 0"
            + "                            AND e.encounter_type = ${prevencaoPositivaSeguimentoEncounterType}"
            + "                            AND e.encounter_datetime"
            + "                                BETWEEN :startDate AND :endDate "
            + "                            AND e.location_id = :location"
            + "                            AND o.concept_id = ${pp4Concept} AND o.value_coded = ${patientFoundYesConcept}"
            + "                        )"
            + "    AND p.patient_id IN("
            + "                        SELECT e.patient_id "
            + "                        FROM encounter  e"
            + "                            INNER JOIN obs o"
            + "                                ON o.encounter_id = e.encounter_id"
            + "                        WHERE e.voided = 0"
            + "                            AND o.voided = 0"
            + "                            AND e.encounter_type = ${prevencaoPositivaSeguimentoEncounterType}"
            + "                            AND e.encounter_datetime"
            + "                                BETWEEN :startDate AND :endDate "
            + "                            AND e.location_id = :location"
            + "                            AND o.concept_id = ${familyPlanningConcept} AND o.value_coded = ${patientFoundYesConcept} "
            + "                        )"
            + "    AND p.patient_id IN("
            + "                        SELECT e.patient_id "
            + "                        FROM encounter  e"
            + "                            INNER JOIN obs o"
            + "                                ON o.encounter_id = e.encounter_id"
            + "                        WHERE e.voided = 0"
            + "                            AND o.voided = 0"
            + "                            AND e.encounter_type = ${prevencaoPositivaSeguimentoEncounterType} "
            + "                            AND e.encounter_datetime"
            + "                                BETWEEN :startDate AND :endDate "
            + "                            AND e.location_id = :location"
            + "                            AND o.concept_id = ${pp6Concept} AND o.value_coded = ${patientFoundYesConcept} "
            + "                        ) "
            + "                        AND p.patient_id IN("
            + "                        SELECT e.patient_id "
            + "                        FROM encounter  e"
            + "                            INNER JOIN obs o"
            + "                                ON o.encounter_id = e.encounter_id"
            + "                        WHERE e.voided = 0"
            + "                            AND o.voided = 0"
            + "                            AND e.encounter_type = ${prevencaoPositivaSeguimentoEncounterType} "
            + "                            AND e.encounter_datetime"
            + "                                BETWEEN :startDate AND :endDate "
            + "                            AND e.location_id = :location"
            + "                            AND o.concept_id = ${pp7Concept} AND o.value_coded = ${patientFoundYesConcept}"
            + "                        ) ";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    cd.setQuery(replacedQuery);

    return cd;
  }

  private CohortDefinition getFichaAPSSAndMinArtStartDate() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All Patients Registered In Encounter Ficha APSS AND PP");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "prevencaoPositivaSeguimentoEncounterType",
        hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("yesConcept", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("noConcept", hivMetadata.getNoConcept().getConceptId());
    map.put("memberShipPlanConcept", hivMetadata.getMemberShipPlanConcept().getConceptId());
    map.put(
        "counseledOnSideEffectsOfArtConcept",
        hivMetadata.getCounseledOnSideEffectsOfArtConcept().getConceptId());
    map.put(
        "adherenceEvaluationConcept", hivMetadata.getAdherenceEvaluationConcept().getConceptId());
    map.put("goodConcept", hivMetadata.getPatientIsDead().getConceptId());
    map.put("arvAdherenceRiskConcept", hivMetadata.getArvAdherenceRiskConcept().getConceptId());
    map.put("badConcept", hivMetadata.getBadConcept().getConceptId());
    map.put("artProgram", hivMetadata.getARTProgram().getProgramId());
    map.put(
        "transferredFromOtherHealthFacilityWorkflowState",
        hivMetadata
            .getTransferredFromOtherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "arvPharmaciaEncounterType",
        hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("arvStartDateConcept", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("arvPlanConcept", hivMetadata.getARVPlanConcept().getConceptId());
    map.put("startDrugs", hivMetadata.getStartDrugs().getConceptId());
    map.put(
        "masterCardDrugPickupEncounterType",
        hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("artPickupConcept", hivMetadata.getArtPickupConcept().getConceptId());
    map.put("artDatePickupMasterCard", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        ""
            + "SELECT external.patient_id "
            + "FROM patient external "
            + "     INNER JOIN encounter e "
            + "        ON e.patient_id = external.patient_id "
            + "     INNER JOIN obs o "
            + "        ON o.encounter_id=e.encounter_id "
            + "WHERE external.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.encounter_type = ${prevencaoPositivaSeguimentoEncounterType} "
            + "    AND (o.concept_id = ${memberShipPlanConcept} AND o.value_coded IN (${yesConcept},${noConcept}) "
            + "        OR "
            + "        o.concept_id = ${counseledOnSideEffectsOfArtConcept} AND o.value_coded IN (${yesConcept},${noConcept}) "
            + "        OR "
            + "        o.concept_id = ${adherenceEvaluationConcept} AND o.value_coded IN (${goodConcept}, ${arvAdherenceRiskConcept}, ${badConcept}) "
            + "        ) "
            + "    AND encounter_datetime "
            + "        > (SELECT DATE_ADD(min(art_startdate.min_date), INTERVAL 30 DAY) as min_min_date  "
            + "FROM  "
            + "(  "
            + "    SELECT  p.patient_id as patient_id, min(pp.date_enrolled) AS min_date  "
            + "    FROM patient p  "
            + "        INNER JOIN patient_program pp  "
            + "            ON pp.patient_id = p.patient_id  "
            + "        INNER JOIN program pgr  "
            + "            ON pgr.program_id=pp.program_id  "
            + "        INNER JOIN patient_state ps  "
            + "            ON  ps.patient_program_id = pp.patient_program_id  "
            + "    WHERE   "
            + "        p.voided = 0  "
            + "        AND ps.voided = 0  "
            + "        AND pp.voided = 0  "
            + "        AND pgr.program_id = ${artProgram}   "
            + "        AND ps.state = ${transferredFromOtherHealthFacilityWorkflowState}   "
            + "    GROUP BY p.patient_id  "
            + "    UNION  "
            + "    SELECT p.patient_id as patient_id, min(o.value_datetime)  AS min_date  "
            + "    FROM patient p  "
            + "        INNER JOIN encounter e  "
            + "            ON e.patient_id = p.patient_id  "
            + "        INNER JOIN obs o  "
            + "            ON o.encounter_id=e.encounter_id  "
            + "    WHERE p.voided = 0  "
            + "        AND e.voided = 0  "
            + "        AND o.voided = 0  "
            + "        AND e.encounter_type IN (${adultoSeguimentoEncounterType}, ${pediatriaSeguimentoEncounterType}, ${arvPharmaciaEncounterType}, ${masterCardEncounterType})  "
            + "        AND o.concept_id = ${arvStartDateConcept}  "
            + "        GROUP BY p.patient_id  "
            + "    UNION  "
            + "    SELECT p.patient_id as patient_id,  min(e.encounter_datetime) AS min_date  "
            + "    FROM patient p  "
            + "        INNER JOIN encounter e  "
            + "            ON e.patient_id = p.patient_id  "
            + "    WHERE p.voided = 0  "
            + "        AND e.voided = 0  "
            + "        AND e.encounter_type  = ${arvPharmaciaEncounterType}  "
            + "        AND e.encounter_datetime <= :endDate  "
            + "        GROUP BY p.patient_id  "
            + "    UNION  "
            + "    SELECT p.patient_id as patient_id, min(e.encounter_datetime)   "
            + "    FROM patient p  "
            + "        INNER JOIN encounter e  "
            + "            ON e.patient_id = p.patient_id  "
            + "        INNER JOIN obs o  "
            + "            ON o.encounter_id=e.encounter_id  "
            + "    WHERE p.voided = 0  "
            + "        AND e.voided = 0  "
            + "        AND o.voided = 0  "
            + "        AND e.encounter_type  IN (${adultoSeguimentoEncounterType},${pediatriaSeguimentoEncounterType},${arvPharmaciaEncounterType})  "
            + "        AND e.encounter_datetime <= :endDate  "
            + "        AND o.concept_id = ${arvPlanConcept} AND o.value_coded = ${startDrugs}  "
            + "        GROUP BY p.patient_id  "
            + "    UNION  "
            + "    SELECT p.patient_id as patient_id, min(e.encounter_datetime) AS min_date  "
            + "    FROM patient p  "
            + "        INNER JOIN encounter e  "
            + "            ON e.patient_id = p.patient_id  "
            + "        INNER JOIN obs o  "
            + "            ON o.encounter_id=e.encounter_id  "
            + "    WHERE p.voided = 0  "
            + "        AND e.voided = 0  "
            + "        AND o.voided = 0  "
            + "        AND e.encounter_type  IN (${adultoSeguimentoEncounterType},${pediatriaSeguimentoEncounterType},${arvPharmaciaEncounterType})  "
            + "        AND e.encounter_datetime <= :endDate  "
            + "        AND o.concept_id = ${arvPlanConcept} AND o.value_coded = ${startDrugs}  "
            + "        GROUP BY p.patient_id  "
            + "    UNION  "
            + "    SELECT p.patient_id as patient_id,  min(o2.value_datetime) AS min_date  "
            + "    FROM patient p  "
            + "        INNER JOIN encounter e  "
            + "            ON e.patient_id = p.patient_id  "
            + "        INNER JOIN obs o1  "
            + "            ON o1.encounter_id=e.encounter_id  "
            + "        INNER JOIN obs o2  "
            + "            ON o2.encounter_id=e.encounter_id  "
            + "    WHERE p.voided = 0  "
            + "        AND e.voided = 0  "
            + "        AND o1.voided = 0  "
            + "        AND o2.voided = 0  "
            + "        AND e.encounter_type = ${masterCardDrugPickupEncounterType}  "
            + "        AND o1.concept_id = ${artPickupConcept} AND o1.value_coded = ${yesConcept}  "
            + "        AND o2.concept_id = ${artDatePickupMasterCard} AND o2.value_datetime <= :endDate  "
            + "        GROUP BY p.patient_id  "
            + ") art_startdate   "
            + "    WHERE art_startdate.patient_id=external.patient_id ) AND encounter_datetime <=:endDate "
            + "    AND e.location_id = :location";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    cd.setQuery(replacedQuery);

    return cd;
  }
}
