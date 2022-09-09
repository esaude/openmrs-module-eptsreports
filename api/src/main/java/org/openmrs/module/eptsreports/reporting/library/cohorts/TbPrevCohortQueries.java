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

import java.util.*;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TbPrevQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsQueriesUtil;
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

  @Autowired private TbPrevQueries tbPrevQueries;

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
        "A",
        EptsReportUtils.map(
            getPatientsThatCompletedIsoniazidProphylacticTreatment(),
            "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}"));

    definition.addSearch(
        "B",
        EptsReportUtils.map(
            getDenominator(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));

    definition.setCompositionString("A AND B");

    return definition;
  }

  public CohortDefinition getPatientsStartedTpt() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("TB-PREV Previously on ART");
    definition.addParameter(new Parameter("startDate", "startDate Date", Date.class));
    definition.addParameter(new Parameter("endDate", "endDate Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        new EptsQueriesUtil()
            .patientIdQueryBuilder(tbPrevQueries.getTPTStartDateQuery())
            .getQuery();
    definition.setQuery(query);
    return definition;
  }

  public CohortDefinition getDenominator() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Denominator Query");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "A",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${onOrBefore},location=${location}"));

    definition.addSearch(
        "B",
        EptsReportUtils.map(
            getPatientsTransferredOut(),
            "startDate=${onOrAfter-6m},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "C",
        EptsReportUtils.map(
            getPatientsThatCompletedIsoniazidProphylacticTreatment(),
            "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}"));

    definition.addSearch(
        "D",
        EptsReportUtils.map(
            getPatientsStartedTpt(),
            "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}"));
    definition.setCompositionString("A AND D AND NOT (B AND NOT C)");

    return definition;
  }

  public CohortDefinition getPatientsThatCompletedIsoniazidProphylacticTreatment() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients that completed Isoniazid prophylatic treatment");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "Before Date", Date.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    cd.addSearch(
        "A",
        EptsReportUtils.map(tbPrevQueries.getPatientsWhoCompleted3HPAtLeast86Days(), mappings));
    cd.addSearch(
        "B", EptsReportUtils.map(tbPrevQueries.getAtLeast3ConsultationOnFichaClinica(), mappings));
    cd.addSearch("C", EptsReportUtils.map(tbPrevQueries.getAtLeastOne3HPWithDTOnFilt(), mappings));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            tbPrevQueries.getAtLeast3ConsultarionWithDispensaMensalOnFilt(), mappings));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            tbPrevQueries.getAtLeast1ConsultarionWithDT3HPOnFichaClinica(), mappings));
    cd.addSearch(
        "F",
        EptsReportUtils.map(tbPrevQueries.getPatientsWhoCompletedINHAtLeast173Days(), mappings));
    cd.addSearch(
        "G",
        EptsReportUtils.map(tbPrevQueries.getAtLeast5ConsultarionINHOnFichaClinica(), mappings));
    cd.addSearch(
        "H",
        EptsReportUtils.map(
            tbPrevQueries.getAtLeast6ConsultarionWithINHDispensaMensalOnFilt(), mappings));
    cd.addSearch(
        "I",
        EptsReportUtils.map(
            tbPrevQueries.getAtLeast2ConsultarionOfDTINHOnFichaClinica(), mappings));
    cd.addSearch(
        "J",
        EptsReportUtils.map(
            tbPrevQueries.getAtLeast2ConsultarionWithINHDispensaTrimestralOnFilt(), mappings));
    cd.addSearch(
        "K",
        EptsReportUtils.map(tbPrevQueries.getAtLeast3ConsultarionOfINHOnFichaClinica(), mappings));
    cd.addSearch(
        "L",
        EptsReportUtils.map(
            tbPrevQueries.getAtLeast1ConsultarionWithDTINHOnFichaClinica(), mappings));
    cd.addSearch(
        "M",
        EptsReportUtils.map(
            tbPrevQueries.getAtLeast3ConsultarionWithINHDispensaMensalOnFilt(), mappings));
    cd.addSearch(
        "N",
        EptsReportUtils.map(
            tbPrevQueries.getAtLeast1ConsultarionWithDTINHDispensaTrimestralOnFilt(), mappings));

    cd.setCompositionString(
        "A OR B OR C OR D OR E OR F OR G OR H OR I OR J OR (K AND L) OR (M AND N)");

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
}
