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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransferredInCohortQueries {

  private ResumoMensalCohortQueries resumoMensalCohortQueries;
  private TxCurrCohortQueries txCurrCohortQueries;
  private CommonCohortQueries commonCohortQueries;
  private TxRttCohortQueries txRttCohortQueries;
  private TXTBCohortQueries txtbCohortQueries;
  private HivMetadata hivMetadata;

  @Autowired
  public TransferredInCohortQueries(
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      TxCurrCohortQueries txCurrCohortQueries,
      CommonCohortQueries commonCohortQueries,
      TxRttCohortQueries txRttCohortQueries,
      TXTBCohortQueries txtbCohortQueries,
      HivMetadata hivMetadata) {
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.commonCohortQueries = commonCohortQueries;
    this.txRttCohortQueries = txRttCohortQueries;
    this.txtbCohortQueries = txtbCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  private void addGeneralParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Number Patients who were transferred in from other facility defined as following: - All
   * patients enrolled in ART Program and who have been registered with the following state
   * TRANSFERRED IN FROM OTHER FACILITY - All patients who have filled “Transferido de outra US” and
   * checked “Em TARV” in Ficha Resumo with MasterCard file opening Date during reporting period -
   * But excluding patients who were included in Tx CURR of previous reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getTransferredInPatients() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Number of Transferred In patients by end of current period");
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition transferredIn = resumoMensalCohortQueries.getTransferredInPatients(false);

    CohortDefinition txCurr = txCurrCohortQueries.getTxCurrCompositionCohort("txCurr", true);

    CohortDefinition transferredOut = getTrfOut();

    CohortDefinition homeVisitTrfOut =
        txCurrCohortQueries.getPatientsTransferedOutInLastHomeVisitCard();

    CohortDefinition clinicalVisit =
        txRttCohortQueries.getPatientsReturnedTreatmentDuringReportingPeriod();

    String mappingsTrfIn = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}";
    String mappingsCurr = "onOrBefore=${onOrBefore-3m},location=${location}";
    String mappingsTrfOut = "startDate=${onOrAfter},location=${location}";
    String mappingsHomeVisitTrfOut = "onOrBefore=${onOrAfter-1d},location=${location}";
    String mappingsClinicalVisit =
        "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}";

    cd.addSearch("transferredIn", EptsReportUtils.map(transferredIn, mappingsTrfIn));
    cd.addSearch("txCurr", EptsReportUtils.map(txCurr, mappingsCurr));
    cd.addSearch("transferredOut", EptsReportUtils.map(transferredOut, mappingsTrfOut));
    cd.addSearch("homeVisitTrfOut", EptsReportUtils.map(homeVisitTrfOut, mappingsHomeVisitTrfOut));
    cd.addSearch("clinicalVisit", EptsReportUtils.map(clinicalVisit, mappingsClinicalVisit));

    cd.setCompositionString("(transferredIn OR transferredOut) AND clinicalVisit AND NOT txCurr");

    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The system will identify patients who were transferred out by end of previous reporting
   * period as follows:
   *
   * <ul>
   *   <li>Patients enrolled on ART Program (Service TARV- Tratamento) with the following last
   *       status: Transferred Out or
   *   <li>Patients who have “Mudança no Estado de Permanência TARV” filled out in Ficha Resumo or
   *       Ficha Clinica – Master Card for the following reasons that are specified in the patient
   *       chart: patient Transferred Out or
   *   <li>Patients who have REASON PATIENT MISSED VISIT (MOTIVOS DA FALTA) as “Transferido para
   *       outra US” or “Auto-transferência” marked in the last Home Visit Card by reporting end
   *       date. Use the “data da visita” when the patient reason was marked on the home visit card
   *       as the reference date.
   * </ul>
   *
   * <br>
   *
   * <p>The system will identify the most recent date from the different sources as the date of
   * Transferred Out. Patients who are “marked” as transferred out who have an ARV pick-up
   * registered in FILA or have a clinical consultation after the date the patient was “marked” as
   * transferred out will not be considered as Transferred Out.<br>
   *
   * <p>The system will consider patient as transferred out as above defined only if the most recent
   * date between (next scheduled ART pick-up on FILA + 1 day) and (the most recent ART pickup date
   * on Ficha Recepção – Levantou ARVs + 31 days) falls during the reporting period.
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getTrfOut() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    addGeneralParameters(definition);
    definition.setName("TxTB - Transferred Out");
    definition.addSearch(
        "transferred-out",
        EptsReportUtils.map(getPatientsTrfOut(), "startDate=${startDate},location=${location}"));
    definition.addSearch(
        "transferred-out-fila-arv",
        EptsReportUtils.map(
            getTrfOutBetweenNextPickupDateFilaAndRecepcaoLevantou(),
            "startDate=${startDate},location=${location}"));
    definition.setCompositionString("transferred-out AND transferred-out-fila-arv");

    return definition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The system will identify patients who are Transferred Out as follows:
   *
   * <ul>
   *   <li>Patients enrolled on ART Program (Service TARV- Tratamento) with the following last
   *       status: Transferred Out or
   *   <li>Patients who have “Mudança no Estado de Permanência TARV” filled out in Ficha Resumo or
   *       Ficha Clinica – Master Card for the following reasons that are specified in the patient
   *       chart: patient Transferred Out or
   *   <li>Patients who have REASON PATIENT MISSED VISIT (MOTIVOS DA FALTA) as “Transferido para
   *       outra US” or “Auto-transferência” marked in the last Home Visit Card by reporting end
   *       date. Use the “data da visita” when the patient reason was marked on the home visit card
   *       as the reference date.
   * </ul>
   *
   * <br>
   *
   * <p>The system will identify the most recent date from the different sources as the date of
   * Transferred Out. Patients who are “marked” as transferred out who have an ARV pick-up
   * registered in FILA or have a clinical consultation after the date the patient was “marked” as
   * transferred out will not be considered as Transferred Out.<br>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsTrfOut() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(
        "Patient Transferred Out With No Drug Pick After The Transferred out Date ");
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
        "returnVisitDateForArvDrugConcept",
        hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    map.put(
        "buscaActivaEncounterType", hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId());
    map.put("artProgram", hivMetadata.getARTProgram().getProgramId());
    map.put(
        "transferredOutToAnotherHealthFacilityWorkflowState",
        hivMetadata
            .getTransferredOutToAnotherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());

    String query =
        "SELECT   transferred_out.patient_id "
            + "             FROM     ( "
            + "                                 SELECT     latest.patient_id , "
            + "                                            Max(latest.last_date) AS last_date "
            + "                                 FROM       ( "
            + "                                                       SELECT     p.patient_id , "
            + "                                                                  Max(ps.start_date) AS last_date "
            + "                                                       FROM       patient p "
            + "                                                       INNER JOIN patient_program pg "
            + "                                                       ON         p.patient_id=pg.patient_id "
            + "                                                       INNER JOIN patient_state ps "
            + "                                                       ON         pg.patient_program_id=ps.patient_program_id "
            + "                                                       WHERE      pg.voided=0 "
            + "                                                       AND        ps.voided=0 "
            + "                                                       AND        p.voided=0 "
            + "                                                       AND        pg.program_id= ${artProgram} "
            + "                                                       AND        ps.state = ${transferredOutToAnotherHealthFacilityWorkflowState} "
            + "                                                       AND        ps.start_date < :startDate "
            + "                                                       AND        pg.location_id= :location "
            + "                                                       GROUP BY   p.patient_id "
            + "                                                       UNION "
            + "                                                       SELECT     p.patient_id, "
            + "                                                                  max(o.obs_datetime) AS last_date "
            + "                                                       FROM       patient p "
            + "                                                       INNER JOIN encounter e "
            + "                                                       ON         e.patient_id=p.patient_id "
            + "                                                       INNER JOIN obs o "
            + "                                                       ON         o.encounter_id=e.encounter_id "
            + "                                                       WHERE      p.voided = 0 "
            + "                                                       AND        e.voided = 0 "
            + "                                                       AND        o.voided = 0 "
            + "                                                       AND        e.encounter_type = ${masterCardEncounterType} "
            + "                                                       AND        o.concept_id = ${stateOfStayOfPreArtPatient} "
            + "                                                       AND        o.value_coded = ${transferredOutConcept} "
            + "                                                       AND        o.obs_datetime < :startDate "
            + "                                                       AND        e.location_id = :location "
            + "                                                       GROUP BY   p.patient_id "
            + "                                                       UNION "
            + "                                                       SELECT     p.patient_id , "
            + "                                                                  max(e.encounter_datetime) AS last_date "
            + "                                                       FROM       patient p "
            + "                                                       INNER JOIN encounter e "
            + "                                                       ON         e.patient_id=p.patient_id "
            + "                                                       INNER JOIN obs o "
            + "                                                       ON         o.encounter_id=e.encounter_id "
            + "                                                       WHERE      p.voided = 0 "
            + "                                                       AND        e.voided = 0 "
            + "                                                       AND        o.voided = 0 "
            + "                                                       AND        e.encounter_type = ${adultoSeguimentoEncounterType} "
            + "                                                       AND        o.concept_id = ${stateOfStayOfArtPatient} "
            + "                                                       AND        o.value_coded = ${transferredOutConcept} "
            + "                                                       AND        e.encounter_datetime < :startDate "
            + "                                                       AND        e.location_id = :location "
            + "                                                       GROUP BY   p.patient_id "
            + "                                                       UNION "
            + "                                                       SELECT     p.patient_id, "
            + "                                                                  max(e.encounter_datetime) last_date "
            + "                                                       FROM       patient p "
            + "                                                       INNER JOIN encounter e "
            + "                                                       ON         p.patient_id = e.patient_id "
            + "                                                       INNER JOIN obs o "
            + "                                                       ON         e.encounter_id = o.encounter_id "
            + "                                                       WHERE      o.concept_id = ${defaultingMotiveConcept} "
            + "                                                       AND        e.location_id = :location "
            + "                                                       AND        e.encounter_type= ${buscaActivaEncounterType} "
            + "                                                       AND        e.encounter_datetime < :startDate "
            + "                                                       AND        o.value_coded IN (${transferredOutConcept}, "
            + "                                                                                    ${autoTransferConcept}) "
            + "                                                       AND        e.voided=0 "
            + "                                                       AND        o.voided=0 "
            + "                                                       AND        p.voided=0 "
            + "                                                       GROUP BY   p.patient_id ) latest "
            + "                                                       WHERE      latest.patient_id NOT IN "
            + "                                                                         ( "
            + "                                                                         SELECT     p.patient_id "
            + "                                                                         FROM       patient p "
            + "                                                                         INNER JOIN encounter e "
            + "                                                                         ON         e.patient_id=p.patient_id "
            + "                                                                         WHERE      p.voided = 0 "
            + "                                                                         AND        e.voided = 0 "
            + "                                                                         AND        e.encounter_type IN (${adultoSeguimentoEncounterType}, "
            + "                                                       ${pediatriaSeguimentoEncounterType}, "
            + "                                                       ${pharmaciaEncounterType}) "
            + "                                                       AND        e.encounter_datetime > last_date "
            + "                                                       AND        e.encounter_datetime < :startDate "
            + "                                                       AND        e.location_id = :location "
            + "                                                       GROUP BY   p.patient_id ) "
            + "                                                       GROUP BY   latest.patient_id "
            + "                                            ) transferred_out "
            + "             GROUP BY transferred_out.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String mappedQuery = stringSubstitutor.replace(query);

    sqlCohortDefinition.setQuery(mappedQuery);

    return sqlCohortDefinition;
  }

  /**
   * The system will consider patient as transferred out as above defined only if the most recent
   * date between (next scheduled ART pick-up on FILA + 1 day) and (the most recent ART pickup date
   * on Ficha Recepção – Levantou ARVs + 31 days) falls by end of previous reporting period.
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getTrfOutBetweenNextPickupDateFilaAndRecepcaoLevantou() {

    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName(
        "Patients Transfered Out between (next scheduled ART pick-up on FILA + 1 day) "
            + "and (the most recent ART pickup date on Ficha Recepção – Levantou ARVs + 31 days");

    definition.addParameter(new Parameter("startDate", "startDate", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        "SELECT final.patient_id FROM  ( "
            + "SELECT considered_transferred.patient_id, max(considered_transferred.value_datetime) as max_date "
            + "FROM ( "
            + "               SELECT     p.patient_id, "
            + "                          date_add(max(o.value_datetime), interval 1 day) AS value_datetime "
            + "               FROM       patient p "
            + "                              INNER JOIN encounter e "
            + "                                         ON         e.patient_id=p.patient_id "
            + "                              INNER JOIN obs o "
            + "                                         ON         o.encounter_id=e.encounter_id "
            + "               WHERE      p.voided = 0 "
            + "                 AND        e.voided = 0 "
            + "                 AND        o.voided = 0 "
            + "                 AND        e.encounter_type = ${18} "
            + "                 AND        o.concept_id = ${5096} "
            + "                 AND        e.encounter_datetime < :startDate "
            + "                 AND        e.location_id = :location "
            + "               GROUP BY   p.patient_id "
            + " UNION "
            + "               SELECT     p.patient_id, "
            + "                          date_add(max(o.value_datetime), interval 31 day)  AS value_datetime "
            + "               FROM       patient p "
            + "                              INNER JOIN encounter e "
            + "                                         ON         e.patient_id=p.patient_id "
            + "                              INNER JOIN obs o "
            + "                                         ON         o.encounter_id=e.encounter_id "
            + "               WHERE      p.voided = 0 "
            + "                 AND        e.voided = 0 "
            + "                 AND        o.voided = 0 "
            + "                 AND        e.encounter_type = ${52} "
            + "                 AND        o.concept_id = ${23866} "
            + "                 AND        o.value_datetime < :startDate "
            + "                 AND        e.location_id = :location "
            + "               GROUP BY   p.patient_id "
            + " )  considered_transferred "
            + " GROUP BY considered_transferred.patient_id "
            + " ) final "
            + " WHERE final.max_date < :startDate  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    definition.setQuery(stringSubstitutor.replace(query));

    return definition;
  }
}
