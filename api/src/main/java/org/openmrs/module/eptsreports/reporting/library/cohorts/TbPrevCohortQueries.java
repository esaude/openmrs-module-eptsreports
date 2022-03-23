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
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.prev.CompletedIsoniazidProphylaticTreatmentCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.TbPrevQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TbPrev we want to expose for EPTS */
@Component
public class TbPrevCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private TbMetadata tbMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * all patients who have in “Patient Clinical Record of ART - Ficha de Seguimento (adults and
   * children)” or “Ficha Resumo” under “Profilaxia com INH – TPI” a Start Date (Data de Início)
   * within previous reporting period
   *
   * <p>Encounter Type Ids = <b>6, 9, 53</b> Isoniazid Prophylaxis start Date <b>(Concept 6128) >=
   * (startDate-6months) and < startDate AND NOT IN Encounter type = 53 RegimeTPT (Concept 23985) =
   * ANY VALUE</b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsThatStartedProfilaxiaIsoniazidaOnPeriod() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Have Última profilaxia TPT with value INH");
    sqlCohortDefinition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());

    String query =
        ""
            + "SELECT distinct p.patient_id "
            + "FROM patient p "
            + "         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         INNER JOIN ( "
            + "             SELECT distinct p.patient_id, e.encounter_datetime AS encounter "
            + "             FROM patient p "
            + "                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "             WHERE e.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND e.encounter_type IN (${6}, ${9}, ${53}) "
            + "               AND o.concept_id = ${6128} "
            + "               AND o.value_datetime >= :onOrAfter "
            + "               AND o.value_datetime < :onOrBefore "
            + "               AND o.location_id = :location "
            + "             ) data_inicio ON data_inicio.patient_id = p.patient_id "
            + "WHERE e.voided = 0 "
            + "  AND o.voided = 0 "
            + "  AND o.location_id = :location "
            + "  AND p.patient_id NOT IN ( "
            + "    SELECT p.patient_id "
            + "    FROM patient p "
            + "             INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "    WHERE e.voided = 0 "
            + "      AND o.voided = 0 "
            + "      AND e.encounter_type = ${53} "
            + "      AND (o.concept_id = ${23985} AND o.value_coded IS NOT NULL) "
            + "      AND e.encounter_datetime = data_inicio.encounter "
            + ")";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  public CohortDefinition getNumerator() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Numerator Query");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${onOrBefore},location=${location}"));
    definition.addSearch(
        "started-isoniazid",
        EptsReportUtils.map(
            getPatientsThatStartedProfilaxiaIsoniazidaOnPeriod(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "last-profilaxia-tpt-inh",
        EptsReportUtils.map(
            getPatientsWhoHaveLastProfilaxiaTPTWithINH(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "initiated-profilaxia",
        EptsReportUtils.map(
            getPatientsThatInitiatedProfilaxia(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "profilaxia-tpt-inh-marked-inicio",
        EptsReportUtils.map(
            getPatientsWhoHaveProfilaxiaTPTWithINHAndAreMarkedAsInicio(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "profilaxia-tpt-3hp-inicio-ficha-resumo",
        EptsReportUtils.map(
            getPatientsWhoHaveProfilaxiaTPTWith3HPAndDataInicioOnFichaResumo(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "profilaxia-tpt-3hp-marked-inicio",
        EptsReportUtils.map(
            getPatientsWhoHaveProfilaxiaTPTWith3HPAndAreMarkedAsInicio(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "outras-prescricoes-with-dt-3hp-on-ficha-clinica",
        EptsReportUtils.map(
            getPatientsWhoHaveOutrasPrescricoesWithDT3HPOnFichaClinica(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "transferred-out",
        EptsReportUtils.map(
            getPatientsTransferredOut(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "completed-isoniazid",
        EptsReportUtils.map(
            getPatientsThatCompletedIsoniazidProphylacticTreatment(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    definition.addSearch(
        "regime-tpt-isoniazid",
        EptsReportUtils.map(
            getPatientWhoHaveRegimeTptIsoniazid(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "outras-prescricoes-3hp",
        EptsReportUtils.map(
            getPatientWhoHaveOutrasPrescricoes(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "regime-tpt-3hp",
        EptsReportUtils.map(
            getPatientWhoHaveRegimeTpt3HPor3HP(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "regime-tpt-INH",
        EptsReportUtils.map(
            getPatientWhoHaveRegimeTptINH(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));

    definition.addSearch(
        "regime-tpt-3hpOrPiridoxina",
        EptsReportUtils.map(
            getPatientWhoHaveRegimeTpt3HP(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));

    definition.setCompositionString(
        "started-by-end-previous-reporting-period AND completed-isoniazid  AND  "
            + "((started-isoniazid OR last-profilaxia-tpt-inh OR initiated-profilaxia "
            + " OR profilaxia-tpt-inh-marked-inicio OR profilaxia-tpt-3hp-inicio-ficha-resumo "
            + " OR profilaxia-tpt-3hp-marked-inicio OR outras-prescricoes-with-dt-3hp-on-ficha-clinica "
            + " OR regime-tpt-isoniazid OR regime-tpt-INH) "
            + " OR (outras-prescricoes-3hp OR regime-tpt-3hp OR regime-tpt-3hpOrPiridoxina) "
            + " ) ");

    return definition;
  }

  public CohortDefinition getNewOnArt() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV New on ART");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "started-on-previous-period",
        EptsReportUtils.map(
            genericCohortQueries.getNewlyOrPreviouslyEnrolledOnART(true),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.setCompositionString("started-on-previous-period");
    return definition;
  }

  public CohortDefinition getPreviouslyOnArt() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Previously on ART");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getNewlyOrPreviouslyEnrolledOnART(false),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.setCompositionString("started-by-end-previous-reporting-period");
    return definition;
  }

  public CohortDefinition getDenominator() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Denominator Query");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${onOrBefore},location=${location}"));
    definition.addSearch(
        "started-isoniazid",
        EptsReportUtils.map(
            getPatientsThatStartedProfilaxiaIsoniazidaOnPeriod(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "last-profilaxia-tpt-inh",
        EptsReportUtils.map(
            getPatientsWhoHaveLastProfilaxiaTPTWithINH(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "initiated-profilaxia",
        EptsReportUtils.map(
            getPatientsThatInitiatedProfilaxia(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "profilaxia-tpt-inh-marked-inicio",
        EptsReportUtils.map(
            getPatientsWhoHaveProfilaxiaTPTWithINHAndAreMarkedAsInicio(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "profilaxia-tpt-3hp-inicio-ficha-resumo",
        EptsReportUtils.map(
            getPatientsWhoHaveProfilaxiaTPTWith3HPAndDataInicioOnFichaResumo(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "profilaxia-tpt-3hp-marked-inicio",
        EptsReportUtils.map(
            getPatientsWhoHaveProfilaxiaTPTWith3HPAndAreMarkedAsInicio(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "outras-prescricoes-with-dt-3hp-on-ficha-clinica",
        EptsReportUtils.map(
            getPatientsWhoHaveOutrasPrescricoesWithDT3HPOnFichaClinica(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "transferred-out",
        EptsReportUtils.map(
            getPatientsTransferredOut(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "completed-isoniazid",
        EptsReportUtils.map(
            getPatientsThatCompletedIsoniazidProphylacticTreatment(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    definition.addSearch(
        "regime-tpt-isoniazid",
        EptsReportUtils.map(
            getPatientWhoHaveRegimeTptIsoniazid(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "outras-prescricoes-3hp",
        EptsReportUtils.map(
            getPatientWhoHaveOutrasPrescricoes(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "regime-tpt-3hp",
        EptsReportUtils.map(
            getPatientWhoHaveRegimeTpt3HPor3HP(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "regime-tpt-INH",
        EptsReportUtils.map(
            getPatientWhoHaveRegimeTptINH(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));

    definition.addSearch(
        "regime-tpt-3hpOrPiridoxina",
        EptsReportUtils.map(
            getPatientWhoHaveRegimeTpt3HP(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));

    definition.setCompositionString(
        "started-by-end-previous-reporting-period  AND "
            + "((started-isoniazid OR last-profilaxia-tpt-inh OR initiated-profilaxia "
            + " OR profilaxia-tpt-inh-marked-inicio OR profilaxia-tpt-3hp-inicio-ficha-resumo "
            + " OR profilaxia-tpt-3hp-marked-inicio OR outras-prescricoes-with-dt-3hp-on-ficha-clinica "
            + " OR regime-tpt-isoniazid OR regime-tpt-INH OR (outras-prescricoes-3hp "
            + " OR regime-tpt-3hp OR regime-tpt-3hpOrPiridoxina) "
            + " AND NOT (transferred-out AND NOT completed-isoniazid)) ");

    return definition;
  }

  public CohortDefinition getPatientsThatCompletedIsoniazidProphylacticTreatment() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(CompletedIsoniazidProphylaticTreatmentCalculation.class)
                .get(0));
    cd.setName("Patients that completed Isoniazid prophylatic treatment");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    return cd;
  }

  /**
   * Patients who on “Ficha Clinica-MasterCard”, mastercard under “ Profilaxia INH” were marked with
   * an “I” (inicio) in a clinical consultation date occurred between ${onOrAfter} and ${onOrBefore}
   *
   * @return the cohort definition
   */
  public CohortDefinition getPatientsThatInitiatedProfilaxia() {
    Concept profilaxiaINH = hivMetadata.getIsoniazidUsageConcept();
    Concept inicio = hivMetadata.getStartDrugs();
    EncounterType adultoSeguimentoEncounterType = hivMetadata.getAdultoSeguimentoEncounterType();

    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("Initiated Profilaxia");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.setEncounterTypeList(Collections.singletonList(adultoSeguimentoEncounterType));
    cd.setTimeModifier(TimeModifier.ANY);
    cd.setQuestion(profilaxiaINH);
    cd.setValueList(Collections.singletonList(inicio));
    cd.setOperator(SetComparator.IN);
    return cd;
  }

  /**
   * 1.All transferred-outs registered in Patient Program State Patient_program.program_id =2 =
   * SERVICO TARV-TRATAMENTO and Patient_State.state = 7 (Transferred-out) or
   * Patient_State.start_date >= (starDate-6months) and <= endDate
   *
   * <p>2.All transferred-outs registered in Ficha Clinica of Master Card Encounter Type ID= 6
   * Estado de Permanencia (Concept Id 6273) = Transferred-out (Concept ID 1706) Encounter_datetime
   * >= (starDate-6months) and <= endDate
   *
   * <p>3.All transferred-outs registered in Ficha Resumo of Master Card Encounter Type ID= 53
   * Estado de Permanencia (Concept Id 6272) = Transferred-out (Concept ID 1706) obs_datetime >=
   * (starDate-6months) and <= endDate
   *
   * <p>4.All transferred-outs registered in Last Home Visit Card Encounter Type ID= 21 Reason
   * Patient Missed Visit (Concept Id 2016) = Transferred-out to another Facility (Concept ID 1706)
   * Or Auto Transfer (Concept id 23863) Encounter_datetime >= (starDate-6months) and <= endDate
   *
   * <p>5.Except all patients who after the most recent date from 1.1 to 1.2 have a drugs pick up or
   * consultation Encounter Type ID= 6, 9, 18 and  encounter_datetime> the most recent date and
   * <=endDate or Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866
   * value_datetime) > the most recent date and <=endDate
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsTransferredOut() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("get Patients With Most Recent Date Have Fila or Consultation ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pharmaciaEncounterType", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put(
        "masterCardDrugPickupEncounterType",
        hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("artDatePickup", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put(
        "stateOfStayOfPreArtPatient", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    map.put("transferredOutConcept", hivMetadata.getTransferredOutConcept().getConceptId());
    map.put("autoTransferConcept", hivMetadata.getAutoTransferConcept().getConceptId());
    map.put("stateOfStayOfArtPatient", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("defaultingMotiveConcept", hivMetadata.getDefaultingMotiveConcept().getConceptId());
    map.put(
        "buscaActivaEncounterType", hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId());
    map.put("artProgram", hivMetadata.getARTProgram().getProgramId());
    map.put(
        "transferredOutToAnotherHealthFacilityWorkflowState",
        hivMetadata
            .getTransferredOutToAnotherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());

    String query =
        "  SELECT mostrecent.patient_id "
            + "FROM ("
            + " SELECT lastest.patient_id ,Max(lastest.last_date) as  last_date "
            + " FROM (  "
            + "    SELECT p.patient_id , Max(ps.start_date) AS last_date  "
            + "    FROM patient p   "
            + "        INNER JOIN patient_program pg   "
            + "            ON p.patient_id=pg.patient_id   "
            + "        INNER JOIN patient_state ps   "
            + "            ON pg.patient_program_id=ps.patient_program_id   "
            + "    WHERE pg.voided=0   "
            + "        AND ps.voided=0   "
            + "        AND p.voided=0   "
            + "        AND pg.program_id= ${artProgram}  "
            + "        AND ps.state = ${transferredOutToAnotherHealthFacilityWorkflowState}   "
            + "        AND ps.end_date is null   "
            + "        AND ps.start_date BETWEEN :startDate AND :endDate    "
            + "        AND pg.location_id= :location   "
            + "    group by p.patient_id  "
            + "  "
            + "    UNION  "
            + "  "
            + "    SELECT  p.patient_id,  Max(o.obs_datetime) AS last_date  "
            + "    FROM patient p    "
            + "        INNER JOIN encounter e   "
            + "            ON e.patient_id=p.patient_id   "
            + "        INNER JOIN obs o   "
            + "            ON o.encounter_id=e.encounter_id   "
            + "    WHERE  p.voided = 0   "
            + "        AND e.voided = 0   "
            + "        AND o.voided = 0   "
            + "        AND e.encounter_type = ${masterCardEncounterType}   "
            + "        AND o.concept_id = ${stateOfStayOfPreArtPatient}  "
            + "        AND o.value_coded =  ${transferredOutConcept}   "
            + "        AND o.obs_datetime BETWEEN :startDate AND :endDate   "
            + "        AND e.location_id =  :location   "
            + "    GROUP BY p.patient_id  "
            + "    UNION   "
            + "    SELECT  p.patient_id , Max(e.encounter_datetime) AS last_date  "
            + "    FROM patient p    "
            + "        INNER JOIN encounter e   "
            + "            ON e.patient_id=p.patient_id   "
            + "        INNER JOIN obs o   "
            + "            ON o.encounter_id=e.encounter_id   "
            + "    WHERE  p.voided = 0   "
            + "        AND e.voided = 0   "
            + "        AND o.voided = 0   "
            + "        AND e.encounter_type = ${adultoSeguimentoEncounterType}  "
            + "        AND o.concept_id = ${stateOfStayOfArtPatient}  "
            + "        AND o.value_coded = ${transferredOutConcept}   "
            + "        AND e.encounter_datetime BETWEEN :startDate AND :endDate   "
            + "        AND e.location_id =  :location  "
            + "    GROUP BY p.patient_id   "
            + "  "
            + "    UNION  "
            + "  "
            + "    SELECT p.patient_id, Max(e.encounter_datetime) last_date   "
            + "    FROM patient p   "
            + "        INNER JOIN encounter e   "
            + "              ON p.patient_id = e.patient_id   "
            + "        INNER JOIN obs o   "
            + "              ON e.encounter_id = o.encounter_id   "
            + "    WHERE o.concept_id = ${defaultingMotiveConcept}  "
            + "    	   AND e.location_id = :location   "
            + "        AND e.encounter_type= ${buscaActivaEncounterType}   "
            + "        AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "		   AND o.value_coded IN (${transferredOutConcept} ,${autoTransferConcept})  "
            + "        AND e.voided=0   "
            + "        AND o.voided=0   "
            + "        AND p.voided=0   "
            + "    GROUP BY p.patient_id "
            + ") lastest   "
            + "WHERE lastest.patient_id NOT  IN("
            + " "
            + "  			     SELECT  p.patient_id    "
            + "	                 FROM patient p      "
            + "	                     INNER JOIN encounter e     "
            + "	                         ON e.patient_id=p.patient_id     "
            + "	                 WHERE  p.voided = 0     "
            + "	                     AND e.voided = 0     "
            + "	                     AND e.encounter_type IN (${adultoSeguimentoEncounterType},"
            + "${pediatriaSeguimentoEncounterType},"
            + "${pharmaciaEncounterType})    "
            + "	                     AND e.encounter_datetime > lastest.last_date "
            + " AND e.encounter_datetime <=  :endDate    "
            + "	                     AND e.location_id =  :location    "
            + "	                 GROUP BY p.patient_id "
            + " UNION "
            + "        			 SELECT  p.patient_id    "
            + "	                 FROM patient p       "
            + "	                      INNER JOIN encounter e      "
            + "	                          ON e.patient_id=p.patient_id      "
            + "	                      INNER JOIN obs o      "
            + "	                          ON o.encounter_id=e.encounter_id      "
            + "	                  WHERE  p.voided = 0      "
            + "	                      AND e.voided = 0      "
            + "	                      AND o.voided = 0      "
            + "	                      AND e.encounter_type = ${masterCardDrugPickupEncounterType}     "
            + "	                      AND o.concept_id = ${artDatePickup}     "
            + "	                      AND o.value_datetime > lastest.last_date  "
            + " AND o.value_datetime <= :endDate      "
            + "	                      AND e.location_id =  :location     "
            + "	                  GROUP BY p.patient_id   "
            + ")  "
            + " GROUP BY lastest.patient_id"
            + " )mostrecent "
            + " GROUP BY mostrecent.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String mappedQuery = stringSubstitutor.replace(query);

    sqlCohortDefinition.setQuery(mappedQuery);

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> Patients who have Regime de TPT with the values (“Isoniazida” or
   * “Isoniazida + Piridoxina”) marked on the first pick-up date on Ficha de Levantamento de TPT
   * (FILT) during the previous reporting period (INH Start Date) and no other INH values
   * (“Isoniazida” or “Isoniazida + Piridoxina”) marked on FILT in the 7 months prior to the INH
   * Start Date or
   *
   * <p><b>Technical Specs</b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoHaveRegimeTPTWithINHMarkedOnFirstPickUpDateOnFILT() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients Who Have Regime TPT With INH Marked On First PickUp Date On FILT");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));
    sqlCohortDefinition.setQuery(
        TbPrevQueries.getRegimeTPTOrOutrasPrescricoes(
            tbMetadata.getRegimeTPTEncounterType(),
            tbMetadata.getRegimeTPTConcept(),
            Arrays.asList(
                tbMetadata.getIsoniazidConcept(), tbMetadata.getIsoniazidePiridoxinaConcept()),
            7));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> Patients who have Outras Prescrições with the value “3HP” marked on Ficha
   * Clínica - Mastercard during the previous reporting period (3HP Start Date) and no other 3HP
   * prescriptions marked on Ficha-Clinica in the 4 months prior to the 3HP Start Date
   *
   * <p><b>Technical Specs</b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoHaveOutrasPrescricoesWith3HPMarkedOnFichaClinica() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients Who Have Outras Prescricoes With 3HP Marked On Ficha Clinica");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));
    sqlCohortDefinition.setQuery(
        TbPrevQueries.getRegimeTPTOrOutrasPrescricoes(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getTreatmentPrescribedConcept(),
            Arrays.asList(tbMetadata.get3HPConcept()),
            4));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> Patients who have Regime de TPT with the values “3HP or 3HP + Piridoxina”
   * marked on the first pick-up date on Ficha de Levantamento de TPT (FILT) during the previous
   * reporting period (3HP Start Date) and no other 3HP pick-ups marked on FILT in the 4 months
   * prior to the 3HP Start Date
   *
   * <p><b>Technical Specs</b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoHaveRegimeTPTWith3HPMarkedOnFirstPickUpDateOnFILT() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients Who Have Regime TPT With 3HP Marked On First PickUp Date On FILT");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));
    sqlCohortDefinition.setQuery(
        TbPrevQueries.getRegimeTPTOrOutrasPrescricoes(
            tbMetadata.getRegimeTPTEncounterType(),
            tbMetadata.getRegimeTPTConcept(),
            Arrays.asList(tbMetadata.get3HPConcept(), tbMetadata.get3HPPiridoxinaConcept()),
            4));

    return sqlCohortDefinition;
  }

  /**
   * Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”) and
   * “Seguimento de tratamento TPT” = (‘Inicio’ or ‘Re-Inicio’) marked on Ficha de Levantamento de
   * TPT (FILT) during the previous reporting period (INH Start Date) or
   *
   * <p>Encounter type = 60 RegimeTPT (Concept 23985) = Isoniazida (Concept 656) or Isoniazida +
   * Piridoxina (Concept 23982) and Seguimento de tratamento TPT (concept ID 23987) =  “inicio” or
   * “re-inicio”(concept ID in [1256, 1705]) and Encounter datetime(First3HPDate) >= startdate – 6
   * months and <= enddate – 6 months
   *
   * @return
   */
  public CohortDefinition getPatientWhoHaveRegimeTptINH() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patient states based on end of reporting period");
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + "FROM patient p "
            + "         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "         INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "WHERE p.voided = 0 "
            + "  AND e.voided = 0 "
            + "  AND o.voided = 0 "
            + "  AND o2.voided = 0 "
            + "  AND e.location_id = :location "
            + "  AND e.encounter_type = ${60} "
            + "  AND (o.concept_id = ${23985} AND o.value_coded IN (${656}, ${23982})) "
            + "  AND (o2.concept_id =${23987} AND o2.value_coded IN (${1256}, ${1705})) "
            + "  AND e.encounter_datetime >= :onOrAfter AND encounter_datetime <= :onOrBefore ";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(substitutor.replace(query));
    return cd;
  }

  /**
   * Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”) and
   * “Seguimento de Tratamento TPT” with values “Continua” or no value marked on the first pick-up
   * date on Ficha de Levantamento de TPT (FILT) during the previous reporting period (INH Start
   * Date) and no other INH values (“Isoniazida” or “Isoniazida + Piridoxina”) marked on FILT and no
   * Última profilaxia Isoniazida (Data Início) registered in Ficha Resumo - Mastercard or
   * Profilaxia (INH) with the value “I” (Início) marked on Ficha Clínica - Mastercard or Profilaxia
   * com INH – TPI (Data Início) marked in Ficha de Seguimento (Adulto e Pediatria) or no Profilaxia
   * TPT = Isoniazida (INH) and Estado da Profilaxia with value Inicio marked on Ficha Clinica and
   * no Última profilaxia TPT with value “Isoniazida (INH)” and Data Inicio da Profilaxia TPT))
   * marked on Ficha Resumo in the 7 months prior to ‘FILT Start Date’
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientWhoHaveRegimeTptIsoniazid() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patient states based on end of reporting period");
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());

    String query =
        " SELECT DISTINCT p.patient_id "
            + " FROM  patient p "
            + "          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "          INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "          INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date "
            + "                      FROM    patient p "
            + "                                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                      WHERE   p.voided = 0 "
            + "                        AND e.voided = 0 "
            + "                        AND o.voided = 0 "
            + "                        AND o2.voided = 0 "
            + "                        AND e.location_id = :location "
            + "                        AND e.encounter_type = ${60} "
            + "                        AND (o.concept_id = ${23985} AND o.value_coded IN (${656},${23982})) "
            + "                        AND ((o2.concept_id =${23987} AND o2.value_coded IN (${1257})) OR o2.concept_id IS NULL) "
            + "                        AND e.encounter_datetime >= :onOrAfter "
            + "                        AND e.encounter_datetime <= :onOrBefore "
            + "                      GROUP BY p.patient_id "
            + "                      UNION "
            + "                      SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date "
            + "                      FROM    patient p "
            + "                                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                      WHERE   p.voided = 0 "
            + "                        AND e.voided = 0 "
            + "                        AND o.voided = 0 "
            + "                        AND o2.voided = 0 "
            + "                        AND e.location_id = :location "
            + "                        AND e.encounter_type = ${60} "
            + "                        AND (o.concept_id = ${23985} AND o.value_coded IN (${656},${23982})) "
            + "                        AND (o2.concept_id NOT IN (SELECT o.concept_id FROM encounter e "
            + "                                                      INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                                   WHERE e.patient_id = p.patient_id AND e.encounter_type = ${60} AND  o.concept_id = ${23987})) "
            + "                        AND e.encounter_datetime >= :onOrAfter "
            + "                        AND e.encounter_datetime <= :onOrBefore "
            + "                      GROUP BY p.patient_id) AS inh  on inh.patient_id = p.patient_id "
            + " WHERE p.voided = 0 "
            + "  AND e.voided = 0 "
            + "  AND o.voided = 0 "
            + "  AND p.patient_id NOT IN ( SELECT pp.patient_id "
            + "                            FROM patient pp "
            + "                                JOIN encounter ee ON ee.patient_id = pp.patient_id "
            + "                                JOIN obs oo ON oo.encounter_id = ee.encounter_id "
            + "                            WHERE    pp.voided = 0 "
            + "                              AND ee.voided = 0 "
            + "                              AND oo.voided = 0 "
            + "                              AND ee.location_id = :location "
            + "                              AND ee.encounter_type = ${60} "
            + "                              AND oo.concept_id = ${23985} "
            + "                              AND oo.value_coded IN (${656},${23982}) "
            + "                              AND ee.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL 7  MONTH) "
            + "                              AND ee.encounter_datetime < inh.first_pickup_date "
            + "                            UNION "
            + "                            SELECT pp.patient_id FROM patient pp "
            + "                                JOIN encounter ee ON ee.patient_id = pp.patient_id "
            + "                                JOIN obs oo ON oo.encounter_id = ee.encounter_id "
            + "                            WHERE oo.voided = 0 AND ee.voided = 0 "
            + "                              AND pp.voided = 0 AND ee.location_id = :location "
            + "                              AND ((ee.encounter_type IN (${6},${9}) AND oo.concept_id =  ${6128} "
            + "                              AND ee.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL 7  MONTH) "
            + "                              AND ee.encounter_datetime < inh.first_pickup_date)"
            + "                               OR  (ee.encounter_type IN (${53})   AND oo.concept_id =  ${6128} "
            + "                              AND oo.value_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL 7  MONTH)"
            + "                              AND oo.value_datetime < inh.first_pickup_date))"
            + "                              UNION "
            + "                            SELECT pp.patient_id FROM patient pp "
            + "                                JOIN encounter ee ON ee.patient_id = pp.patient_id "
            + "                                JOIN obs oo ON oo.encounter_id = ee.encounter_id "
            + "                            WHERE ee.encounter_type =   ${6}   AND oo.concept_id = ${6122} "
            + "                              AND oo.voided = 0 AND ee.voided = 0 "
            + "                              AND pp.voided = 0 AND ee.location_id = :location "
            + "                              AND oo.value_coded =   ${1256} "
            + "                              AND ee.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL 7  MONTH) "
            + "                              AND ee.encounter_datetime < inh.first_pickup_date "
            + "                            UNION "
            + "                            SELECT p.patient_id "
            + "                            FROM patient p "
            + "                               INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                               INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                               INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                            WHERE p.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND o2.voided = 0 "
            + "                              AND e.encounter_type = ${53} "
            + "                              AND (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "                              AND (o2.concept_id = ${6128} "
            + "                                    AND o2.value_datetime  >= DATE_SUB(inh.first_pickup_date, INTERVAL 7 MONTH) "
            + "                                    AND o2.value_datetime  < inh.first_pickup_date) "
            + "                              AND e.location_id = :location "
            + "                            GROUP BY p.patient_id "
            + "                            UNION "
            + "                            SELECT patient_id "
            + "                            FROM ( "
            + "                                 SELECT p.patient_id, MIN(e.encounter_datetime) "
            + "                                 FROM patient p "
            + "                                          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                          INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                          INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                                          INNER JOIN ( "
            + "                                     SELECT p.patient_id, MIN(e.encounter_datetime) first_pickup_date "
            + "                                     FROM patient p "
            + "                                              INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                              INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                                     WHERE p.voided = 0 "
            + "                                       AND e.voided = 0 "
            + "                                       AND o.voided = 0 "
            + "                                       AND o2.voided = 0 "
            + "                                       AND e.location_id = :location "
            + "                                       AND e.encounter_type = ${60} "
            + "                                       AND (o.concept_id = ${23985} AND o.value_coded IN (${656}, ${23982})) "
            + "                                       AND ((o2.concept_id = ${23987} AND o2.value_coded IN (${1257})) OR o2.concept_id IS NULL) "
            + "                                       AND e.encounter_datetime >= :onOrAfter "
            + "                                       AND e.encounter_datetime <= :onOrBefore "
            + "                                     GROUP BY p.patient_id "
            + "                                     UNION "
            + "                                     SELECT p.patient_id, MIN(e.encounter_datetime) first_pickup_date "
            + "                                     FROM patient p "
            + "                                              INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                              INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                                     WHERE p.voided = 0 "
            + "                                       AND e.voided = 0 "
            + "                                       AND o.voided = 0 "
            + "                                       AND o2.voided = 0 "
            + "                                       AND e.location_id = :location "
            + "                                       AND e.encounter_type = ${60} "
            + "                                       AND (o.concept_id = ${23985} AND o.value_coded IN (${656}, ${23982})) "
            + "                                       AND (o2.concept_id NOT IN (SELECT o.concept_id "
            + "                                                                  FROM encounter e "
            + "                                                                           INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                                                  WHERE e.patient_id = p.patient_id "
            + "                                                                    AND e.encounter_type = ${60} "
            + "                                                                    AND o.concept_id = ${23987})) "
            + "                                       AND e.encounter_datetime >= :onOrAfter "
            + "                                       AND e.encounter_datetime <= :onOrBefore "
            + "                                     GROUP BY p.patient_id) AS inh on inh.patient_id = p.patient_id "
            + "                                 WHERE p.voided = 0 "
            + "                                   AND e.voided = 0 "
            + "                                   AND o.voided = 0 "
            + "                                   AND e.location_id = :location "
            + "                                   AND e.encounter_type = ${6} "
            + "                                   AND (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "                                   AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256}) "
            + "                                   AND e.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL 7 MONTH) "
            + "                                   AND e.encounter_datetime < inh.first_pickup_date "
            + "                                 GROUP BY p.patient_id "
            + "                            ) tpt_inh_prophylaxis_start"
            + "  ) ";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(substitutor.replace(query));
    return cd;
  }

  public CohortDefinition getPatientWhoHaveOutrasPrescricoes() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patient states based on end of reporting period");
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());

    String query =
        " SELECT distinct p.patient_id   "
            + "             FROM  patient p    "
            + "             INNER JOIN encounter e ON e.patient_id = p.patient_id    "
            + "             INNER JOIN obs o ON o.encounter_id = e.encounter_id    "
            + "             INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date   "
            + "                         FROM    patient p    "
            + "                         INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                         INNER JOIN obs o ON o.encounter_id = e.encounter_id    "
            + "                         WHERE   p.voided = 0    "
            + "                             AND e.voided = 0    "
            + "                             AND o.voided = 0    "
            + "                             AND e.location_id = :location   "
            + "                             AND e.encounter_type = ${6}   "
            + "                             AND o.concept_id = ${1719}   "
            + "                             AND o.value_coded IN (${23954})   "
            + "                             AND e.encounter_datetime >= :onOrAfter   "
            + "                             AND e.encounter_datetime <= :onOrBefore   "
            + "                         GROUP BY p.patient_id) AS inh  on inh.patient_id = p.patient_id   "
            + "             WHERE p.voided = 0   "
            + "                and e.voided = 0   "
            + "                and o.voided = 0   "
            + "                and p.patient_id NOT IN ( SELECT pp.patient_id    "
            + "                                            FROM patient pp   "
            + "                                                   JOIN encounter ee ON ee.patient_id = pp.patient_id "
            + "                                                   JOIN obs oo ON oo.encounter_id = ee.encounter_id "
            + "                                            WHERE    pp.voided = 0    "
            + "                                                  AND ee.voided = 0    "
            + "                                                  AND oo.voided = 0    "
            + "                                                  AND ee.location_id = :location   "
            + "                                                  AND ee.encounter_type = ${6}   "
            + "                                                  AND oo.concept_id = ${1719}   "
            + "                                                  AND oo.value_coded IN (${23954})   "
            + "                                                  AND ee.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL  4 MONTH)    "
            + "                                                  AND ee.encounter_datetime < inh.first_pickup_date "
            + "                                               "
            + "                                              UNION "
            + "                                               "
            + "                                            SELECT pp.patient_id "
            + "                                                FROM patient pp "
            + "                                                   JOIN encounter ee ON ee.patient_id = pp.patient_id "
            + "                                                   JOIN obs oo ON oo.encounter_id = ee.encounter_id "
            + "                                                WHERE    pp.voided = 0 "
            + "                                                  AND ee.voided = 0 "
            + "                                                  AND oo.voided = 0 "
            + "                                                  AND ee.location_id = :location "
            + "                                                  AND ee.encounter_type = ${60} "
            + "                                                  AND oo.concept_id = ${23985} "
            + "                                                  AND oo.value_coded IN (${23954},${23984}) "
            + "                                                  AND ee.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL  4 MONTH)    "
            + "                                                    AND ee.encounter_datetime < inh.first_pickup_date) ";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(substitutor.replace(query));
    return cd;
  }

  public CohortDefinition getPatientWhoHaveRegimeTpt3HP() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patient states based on end of reporting period");
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        " SELECT p.patient_id "
            + " FROM patient p "
            + "         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "         INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 "
            + "  AND e.voided = 0 "
            + "  AND o.voided = 0 "
            + "  AND o2.voided = 0 "
            + "  AND e.location_id = :location "
            + "  AND e.encounter_type = ${60} "
            + "  AND (o.concept_id = ${23985} AND o.value_coded IN (${23954}, ${23984})) "
            + "  AND (o2.concept_id = ${23987} AND o2.value_coded IN (${1256}, ${1705})) "
            + "  AND e.encounter_datetime >= :onOrAfter AND encounter_datetime <= :onOrBefore ";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(substitutor.replace(query));
    return cd;
  }

  /**
   * Patients who have Regime de TPT with the values “3HP or 3HP + Piridoxina” marked on the first
   * pick-up date on Ficha de Levantamento de TPT (FILT) during the previous reporting period (3HP
   * Start Date) and no Outras Prescrições with the value “3HP” marked on Ficha Clínica - Mastercard
   * in the 4 months prior to this “FILT 3HP Start Date” and no other Regime de TPT with the values
   * “3HP or 3HP + Piridoxina” marked on FILT in the 4 months prior to the 3HP Start Date and no
   * other 3HP Start Dates marked on Ficha Clinica ((Profilaxia TPT with the value “3HP” and Estado
   * da Profilaxia with the value “Inicio (I)”) or (Outras Prescrições with the value “DT-3HP”) in
   * the 4 months prior to this FILT 3HP Start Date and No other 3HP Start Dates marked on Ficha
   * Resumo (Última profilaxia TPT with value “3HP” and Data Inicio da Profilaxia TPT) in the 4
   * months prior to this FILT 3HP Start Date
   */
  public CohortDefinition getPatientWhoHaveRegimeTpt3HPor3HP() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patient states based on end of reporting period");
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());

    String query =
        " SELECT DISTINCT p.patient_id "
            + " FROM  patient p "
            + "          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "          INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "          INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date "
            + "                      FROM    patient p "
            + "                                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                      WHERE   p.voided = 0 "
            + "                        AND e.voided = 0 "
            + "                        AND o.voided = 0 "
            + "                        AND e.location_id = :location "
            + "                        AND e.encounter_type = ${60} "
            + "                        AND o.concept_id = ${23985} "
            + "                        AND o.value_coded IN (${23954},${23984}) "
            + "                        AND e.encounter_datetime >= :onOrAfter "
            + "                        AND e.encounter_datetime <= :onOrBefore "
            + "                      GROUP BY p.patient_id) AS inh  on inh.patient_id = p.patient_id "
            + " WHERE p.voided = 0 "
            + "  and e.voided = 0 "
            + "  and o.voided = 0 "
            + "  and p.patient_id NOT IN (SELECT pp.patient_id "
            + "                           FROM patient pp "
            + "                                 JOIN encounter ee ON ee.patient_id = pp.patient_id "
            + "                                 JOIN obs oo ON oo.encounter_id = ee.encounter_id "
            + "                           WHERE     pp.voided = 0 "
            + "                             AND ee.voided = 0 "
            + "                             AND oo.voided = 0 "
            + "                             AND ee.location_id = :location "
            + "                             AND ee.encounter_type = ${60} "
            + "                             AND oo.concept_id = ${23985} "
            + "                             AND oo.value_coded IN (${23954},${23984}) "
            + "                             AND ee.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL 4 MONTH) "
            + "                             AND ee.encounter_datetime < inh.first_pickup_date "
            + "                           UNION "
            + "                           SELECT pp.patient_id "
            + "                           FROM patient pp "
            + "                                 JOIN encounter ee ON ee.patient_id = pp.patient_id "
            + "                                 JOIN obs oo ON oo.encounter_id = ee.encounter_id "
            + "                           WHERE     pp.voided = 0 "
            + "                             AND ee.voided = 0 "
            + "                             AND oo.voided = 0 "
            + "                             AND ee.location_id = :location "
            + "                             AND ee.encounter_type = ${6} "
            + "                             AND oo.concept_id = ${1719} "
            + "                             AND oo.value_coded IN (${23954}) "
            + "                             AND ee.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL 4 MONTH) "
            + "                             AND ee.encounter_datetime < inh.first_pickup_date) "
            + "                           UNION "
            + "                           SELECT patient_id "
            + "                           FROM ( "
            + "                               SELECT p.patient_id, MIN(e.encounter_datetime) "
            + "                               FROM patient p "
            + "                                   INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                   INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                   INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                                   INNER JOIN ( "
            + "                                       SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date "
            + "                                       FROM    patient p "
            + "                                           INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                           INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                       WHERE   p.voided = 0 "
            + "                                         AND e.voided = 0 "
            + "                                         AND o.voided = 0 "
            + "                                         AND e.location_id = :location "
            + "                                         AND e.encounter_type = ${60} "
            + "                                         AND o.concept_id = ${23985} "
            + "                                         AND o.value_coded IN (${23954},${23984}) "
            + "                                         AND e.encounter_datetime >= :onOrAfter "
            + "                                         AND e.encounter_datetime <= :onOrBefore "
            + "                                       GROUP BY p.patient_id) AS inh  on inh.patient_id = p.patient_id "
            + "                               WHERE p.voided = 0 "
            + "                                 AND e.voided = 0 "
            + "                                 AND o.voided = 0 "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_type = ${6} "
            + "                                AND (o.concept_id = ${23985} AND o.value_coded = ${23954}) "
            + "                                 AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256}) "
            + "                                 AND e.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL 4 MONTH) "
            + "                                 AND e.encounter_datetime < inh.first_pickup_date "
            + "                               GROUP BY p.patient_id "
            + "                           ) tpt_3hp_prophylaxis_start "
            + "                           UNION "
            + "                           SELECT patient_id "
            + "                           FROM ( "
            + "                               SELECT p.patient_id, MIN(e.encounter_datetime) "
            + "                               FROM patient p "
            + "                                        INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                        INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                        INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                                        INNER JOIN ( "
            + "                                   SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date "
            + "                                   FROM    patient p "
            + "                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                               INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                   WHERE   p.voided = 0 "
            + "                                     AND e.voided = 0 "
            + "                                     AND o.voided = 0 "
            + "                                     AND e.location_id = :location "
            + "                                     AND e.encounter_type = ${60} "
            + "                                     AND o.concept_id = ${23985} "
            + "                                     AND o.value_coded IN (${23954},${23984}) "
            + "                                     AND e.encounter_datetime >= :onOrAfter "
            + "                                     AND e.encounter_datetime <= :onOrBefore "
            + "                                   GROUP BY p.patient_id) AS inh  on inh.patient_id = p.patient_id "
            + "                               WHERE p.voided = 0 "
            + "                                 AND e.voided = 0 "
            + "                                 AND o.voided = 0 "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_type = ${6} "
            + "                                 AND (o.concept_id = ${1719} AND o.value_coded = ${165307}) "
            + "                                 AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256})  "
            + "                                 AND e.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL 4 MONTH) "
            + "                                 AND e.encounter_datetime < inh.first_pickup_date "
            + "                               GROUP BY p.patient_id "
            + "                           ) other_prescriptions_dt_3hp "
            + "                           UNION "
            + "                           SELECT patient_id "
            + "                           FROM ( "
            + "                               SELECT p.patient_id, MIN(e.encounter_datetime) "
            + "                               FROM patient p "
            + "                                        INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                        INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                        INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                                        INNER JOIN ( "
            + "                                   SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date "
            + "                                   FROM    patient p "
            + "                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                               INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                   WHERE   p.voided = 0 "
            + "                                     AND e.voided = 0 "
            + "                                     AND o.voided = 0 "
            + "                                     AND e.location_id = :location "
            + "                                     AND e.encounter_type = ${60} "
            + "                                     AND o.concept_id = ${23985} "
            + "                                     AND o.value_coded IN (${23954},${23984}) "
            + "                                     AND e.encounter_datetime >= :onOrAfter "
            + "                                     AND e.encounter_datetime <= :onOrBefore "
            + "                                   GROUP BY p.patient_id) AS inh  on inh.patient_id = p.patient_id "
            + "                               WHERE p.voided = 0 "
            + "                                 AND e.voided = 0 "
            + "                                 AND o.voided = 0 "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_type = ${53} "
            + "                                 AND (o.concept_id = ${23985} AND o.value_coded = ${23954}) "
            + "                                 AND o2.concept_id = ${6128} "
            + "                                 AND e.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL 4 MONTH) "
            + "                                 AND e.encounter_datetime < inh.first_pickup_date "
            + "                               GROUP BY p.patient_id "
            + "                           ) tpt_3hp_start";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(substitutor.replace(query));
    return cd;
  }

  /**
   * Patients who have Última profilaxia TPT with value “Isoniazida (INH)” and Data Inicio da
   * Profilaxia TPT) registered in Ficha Resumo – Mastercard within previous reporting period
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoHaveLastProfilaxiaTPTWithINH() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Última profilaxia TPT with value INH and Data Inicio da profilaxia TPT");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());

    String query =
        ""
            + "SELECT patient_id "
            + "FROM ( "
            + "         SELECT p.patient_id, MAX(e.encounter_datetime) "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                  INNER JOIN ( "
            + "                     SELECT p.patient_id, e.encounter_datetime AS encounter "
            + "                     FROM patient p "
            + "                         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                     WHERE p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.encounter_type = ${53} "
            + "                       AND o.concept_id = ${6128} "
            + "                       AND o.value_datetime BETWEEN :startDate AND :endDate "
            + "                       AND e.location_id = :location "
            + "                 ) data_inicio ON data_inicio.patient_id = p.patient_id "
            + "       WHERE p.voided = 0 "
            + "         AND e.voided = 0 "
            + "         AND o.voided = 0 "
            + "         AND e.encounter_type = ${53} "
            + "         AND o.concept_id = ${23985} "
            + "         AND o.value_coded = ${656} "
            + "         AND e.location_id = :location "
            + "         AND e.encounter_datetime = data_inicio.encounter "
            + "       GROUP BY p.patient_id "
            + " ) last_prophylaxis";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Patients who have Profilaxia TPT with the value “Isoniazida (INH) and Estado da Profilaxia with
   * the value “Inicio (I)”) marked on Ficha Clínica - Mastercard during the previous reporting
   * period
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoHaveProfilaxiaTPTWithINHAndAreMarkedAsInicio() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "TPT with Isoniazida and Estado da Profilaxia with the value Inicio”");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());

    String query =
        ""
            + "SELECT patient_id "
            + "FROM ( "
            + "         SELECT p.patient_id, MIN(e.encounter_datetime) "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                  INNER JOIN ( "
            + "                     SELECT p.patient_id, e.encounter_datetime AS encounter "
            + "                     FROM patient p "
            + "                         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                     WHERE p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND o.concept_id = ${165308} "
            + "                       AND o.value_coded = ${1256} "
            + "                       AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                       AND e.location_id = :location "
            + "                 ) prophylaxis_status ON prophylaxis_status.patient_id = p.patient_id "
            + "         WHERE p.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.encounter_type = ${6} "
            + "           AND o.concept_id = ${23985} "
            + "           AND o.value_coded = ${656} "
            + "           AND e.location_id = :location "
            + "           AND e.encounter_datetime = prophylaxis_status.encounter "
            + "         GROUP BY p.patient_id "
            + " ) first_prophylaxis";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Patients who have Última profilaxia TPT with value “3HP” and Data Inicio da Profilaxia TPT
   * registered in Ficha Resumo - Mastercard during the previous reporting period (3HP Start Date)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoHaveProfilaxiaTPTWith3HPAndDataInicioOnFichaResumo() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Última profilaxia TPT with value 3HP and Data Inicio da Profilaxia TPT in Ficha Resumo");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());

    String query =
        ""
            + "SELECT p.patient_id "
            + "FROM patient p "
            + "         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         INNER JOIN ( "
            + "            SELECT p.patient_id, e.encounter_datetime AS encounter "
            + "            FROM patient p "
            + "                INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "            WHERE p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND o.voided = 0 "
            + "              AND e.encounter_type = ${53} "
            + "              AND o.concept_id = ${6128} "
            + "              AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "              AND e.location_id = :location "
            + "        ) data_inicio ON data_inicio.patient_id = p.patient_id "
            + "WHERE p.voided = 0 "
            + "  AND e.voided = 0 "
            + "  AND o.voided = 0 "
            + "  AND e.encounter_type = ${53} "
            + "  AND o.concept_id = ${23985} "
            + "  AND o.value_coded = ${23954} "
            + "  AND e.location_id = :location "
            + "  AND e.encounter_datetime = data_inicio.encounter "
            + "GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Patients who have Profilaxia TPT with the value “3HP” and Estado da Profilaxia with the value
   * “Inicio (I)” marked on Ficha Clínica – Mastercard during the previous reporting period (3HP
   * Start Date)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoHaveProfilaxiaTPTWith3HPAndAreMarkedAsInicio() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "TPT with 3HP and Estado da Profilaxia with Inicio on Ficha Clínica");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());

    String query =
        ""
            + "SELECT patient_id "
            + "FROM ( "
            + "         SELECT p.patient_id, MIN(e.encounter_datetime) "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                  INNER JOIN ( "
            + "             SELECT p.patient_id, e.encounter_datetime AS encounter "
            + "             FROM patient p "
            + "                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "             WHERE p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND e.encounter_type = ${6} "
            + "               AND o.concept_id = ${165308} "
            + "               AND o.value_coded = ${1256} "
            + "               AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "               AND e.location_id = :location "
            + "         ) data_inicio ON data_inicio.patient_id = p.patient_id "
            + "         WHERE p.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.encounter_type = ${6} "
            + "           AND o.concept_id = ${23985} "
            + "           AND o.value_coded = ${23954} "
            + "           AND e.location_id = :location "
            + "           AND e.encounter_datetime = data_inicio.encounter "
            + "         GROUP BY p.patient_id "
            + ") tpt_3hp_prophylaxis_start";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Patients who have Outras Prescrições with the value “DT-3HP” marked on Ficha Clínica -
   * Mastercard during the previous reporting period (3HP Start Date)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoHaveOutrasPrescricoesWithDT3HPOnFichaClinica() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Outras prescrições with DT-3HP on Ficha Clínica");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());

    String query =
        ""
            + "SELECT patient_id "
            + "FROM ( "
            + "    SELECT p.patient_id, MIN(e.encounter_datetime) "
            + "    FROM patient p "
            + "             INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "    WHERE p.voided = 0 "
            + "      AND e.voided = 0 "
            + "      AND o.voided = 0 "
            + "      AND e.encounter_type = ${6} "
            + "      AND o.concept_id = ${1719} "
            + "      AND o.value_coded = ${165307} "
            + "      AND e.location_id = :location "
            + "    GROUP BY p.patient_id "
            + ") other_prescriptions_dt_3hp";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }
}
