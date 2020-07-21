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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXCurrQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TxCurrCohortQueries we want to expose for EPTS */
@Component
public class TxCurrCohortQueries {

  private static final String HAS_NEXT_APPOINTMENT_QUERY =
      "select distinct obs.person_id from obs "
          + "where obs.obs_datetime <= :onOrBefore and obs.location_id = :location and obs.concept_id = %s and obs.voided = false and obs.value_datetime is not null "
          + "and obs.obs_datetime = (select max(encounter.encounter_datetime) from encounter "
          + "where encounter.encounter_type in (%s) and encounter.patient_id = obs.person_id and encounter.location_id = obs.location_id and encounter.voided = false and encounter.encounter_datetime <= :onOrBefore) ";

  private static final int OLD_SPEC_ABANDONMENT_DAYS = 60;

  private static final int CURRENT_SPEC_ABANDONMENT_DAYS = 31;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private HivCohortQueries hivCohortQueries;

  @Autowired private GenericCohortQueries genericCohortQueries;

  /**
   * @param cohortName Cohort name
   * @param currentSpec
   * @return TxCurr composition cohort definition
   */
  @DocumentedDefinition(value = "getTxCurrCompositionCohort")
  public CohortDefinition getTxCurrCompositionCohort(String cohortName, boolean currentSpec) {

    final int abandonmentDays =
        currentSpec ? CURRENT_SPEC_ABANDONMENT_DAYS : OLD_SPEC_ABANDONMENT_DAYS;

    CompositionCohortDefinition txCurrComposition = new CompositionCohortDefinition();
    txCurrComposition.setName(cohortName);

    txCurrComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    txCurrComposition.addParameter(new Parameter("location", "location", Location.class));
    txCurrComposition.addParameter(new Parameter("locations", "location", Location.class));

    CohortDefinition inARTProgramAtEndDate =
        genericCohortQueries.createInProgram("InARTProgram", hivMetadata.getARTProgram());
    txCurrComposition
        .getSearches()
        .put(
            "111",
            EptsReportUtils.map(
                inARTProgramAtEndDate, "onOrBefore=${onOrBefore},locations=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "1",
            EptsReportUtils.map(
                getPatientWithSTARTDRUGSObsBeforeOrOnEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));

    txCurrComposition
        .getSearches()
        .put(
            "2",
            EptsReportUtils.map(
                hivCohortQueries.getPatientWithHistoricalDrugStartDateObsBeforeOrOnEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));

    txCurrComposition
        .getSearches()
        .put(
            "3",
            EptsReportUtils.map(
                getPatientEnrolledInArtProgramByEndReportingPeriod(),
                "onOrBefore=${onOrBefore},location=${location}"));

    txCurrComposition
        .getSearches()
        .put(
            "4",
            EptsReportUtils.map(
                getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));

    txCurrComposition
        .getSearches()
        .put(
            "555",
            EptsReportUtils.map(
                getPatientsWhoLeftARTProgramBeforeOrOnEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "5",
            EptsReportUtils.map(
                getPatientsWhoHavePickedUpDrugsMasterCardByEndReporingPeriod(),
                "onOrBefore=${onOrBefore},location=${location}"));
    // section 2
    txCurrComposition
        .getSearches()
        .put(
            "666",
            EptsReportUtils.map(
                getPatientsThatMissedNexPickup(),
                String.format(
                    "onOrBefore=${onOrBefore},location=${location},abandonmentDays=%s",
                    abandonmentDays)));
    txCurrComposition
        .getSearches()
        .put(
            "6",
            EptsReportUtils.map(
                getPatientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "777",
            EptsReportUtils.map(
                getPatientsThatDidNotMissNextConsultation(),
                String.format(
                    "onOrBefore=${onOrBefore},location=${location},abandonmentDays=%s",
                    abandonmentDays)));
    txCurrComposition
        .getSearches()
        .put(
            "7",
            EptsReportUtils.map(
                getDeadPatientsInDemographiscByReportingEndDate(), "onOrBefore=${onOrBefore}"));
    txCurrComposition
        .getSearches()
        .put(
            "888",
            EptsReportUtils.map(
                getPatientsReportedAsAbandonmentButStillInPeriod(),
                String.format(
                    "onOrBefore=${onOrBefore},location=${location},abandonmentDays=%s",
                    abandonmentDays)));
    txCurrComposition
        .getSearches()
        .put(
            "8",
            EptsReportUtils.map(
                getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "9",
            EptsReportUtils.map(
                getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "10",
            EptsReportUtils.map(
                getTransferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "11",
            EptsReportUtils.map(
                getPatientSuspendedInFichaResumeAndClinicaOfMasterCardByReportEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "12",
            EptsReportUtils.map(
                getPatientWhoAfterMostRecentDateHaveDrugPickupOrConsultationComposition(),
                "onOrBefore=${onOrBefore},location=${location}"));
    // section 3
    txCurrComposition
        .getSearches()
        .put(
            "13",
            EptsReportUtils.map(
                getPatientHavingLastScheduledDrugPickupDateDaysBeforeEndDate(28),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "14",
            EptsReportUtils.map(
                getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup(),
                "onOrBefore=${onOrBefore},location=${location}"));

    txCurrComposition
        .getSearches()
        .put(
            "15",
            EptsReportUtils.map(
                getPatientsTransferedOutInLastHomeVisitCard(),
                "onOrBefore=${onOrBefore},location=${location}"));

    String compositionString;
    if (currentSpec) {
      compositionString =
          "(1 OR 2 OR 3 OR 4 OR 5) AND NOT ((6 OR 7 OR 8 OR 9 OR 10 OR 11 OR 15) AND NOT 12) AND NOT (13 OR 14)";
    } else {
      compositionString = "(111 OR 2 OR 3 OR 4) AND (NOT (555 OR (666 AND (NOT (777 OR 888)))))";
    }

    txCurrComposition.setCompositionString(compositionString);
    return txCurrComposition;
  }

  @DocumentedDefinition("TX_CURR base cohort")
  public CohortDefinition getTxCurrBaseCohort() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Facility", Location.class));
    cd.setName("TX_CURR base cohort");
    CohortDefinition baseCohort = genericCohortQueries.getBaseCohort();
    CohortDefinition txCurr = getTxCurrCompositionCohort("tx_curr", true);

    cd.addSearch("baseCohort", Mapped.mapStraightThrough(baseCohort));

    String txCurrMappings = "onOrBefore=${endDate},location=${location},locations=${location}";
    cd.addSearch("txCurr", EptsReportUtils.map(txCurr, txCurrMappings));

    cd.setCompositionString("baseCohort AND txCurr");
    return cd;
  }

  /**
   * 1. Cohort of patients registered as START DRUGS (answer to question 1255 = ARV PLAN is 1256 =
   * START DRUGS) in the first drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up
   * consultation for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV:
   * PEDIATRIA SEGUIMENTO) before or on end date
   */
  @DocumentedDefinition(value = "patientWithSTARTDRUGSObs")
  public CohortDefinition getPatientWithSTARTDRUGSObsBeforeOrOnEndDate() {
    SqlCohortDefinition patientWithSTARTDRUGSObs = new SqlCohortDefinition();
    patientWithSTARTDRUGSObs.setName("patientWithSTARTDRUGSObs");

    patientWithSTARTDRUGSObs.setQuery(
        TXCurrQueries.getPatientWithSTARTDRUGSObsBeforeOrOnEndDate(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getStartDrugsConcept().getConceptId()));
    patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    patientWithSTARTDRUGSObs.addParameter(new Parameter("location", "location", Location.class));
    return patientWithSTARTDRUGSObs;
  }

  /**
   * 3.All patients enrolled in ART Program by end of reporting period. (3) Table: patient_program
   * Criterias: program_id=2, and date_enrolled <= endDate
   */
  @DocumentedDefinition(value = "patientEnrolledInArtProgramByEndReportingPeriod")
  public CohortDefinition getPatientEnrolledInArtProgramByEndReportingPeriod() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientEnrolledInArtProgramByEndReportingPeriod");

    definition.setQuery(
        TXCurrQueries.getPatientEnrolledInArtProgramByEndReportingPeriod(
            hivMetadata.getARTProgram().getProgramId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 4.
   *
   * @return Cohort of patients with first drug pickup (encounter type 18=S.TARV: FARMACIA) before
   *     or on end date
   */
  @DocumentedDefinition(value = "patientWithFirstDrugPickupEncounter")
  public CohortDefinition getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate() {
    SqlCohortDefinition patientWithFirstDrugPickupEncounter = new SqlCohortDefinition();
    patientWithFirstDrugPickupEncounter.setName("patientWithFirstDrugPickupEncounter");

    patientWithFirstDrugPickupEncounter.setQuery(
        TXCurrQueries.getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()));
    patientWithFirstDrugPickupEncounter.addParameter(
        new Parameter("onOrBefore", "onOrBefore", Date.class));
    patientWithFirstDrugPickupEncounter.addParameter(
        new Parameter("location", "location", Location.class));
    return patientWithFirstDrugPickupEncounter;
  }

  /**
   * 5. All patients who have picked up drugs (Recepção Levantou ARV) – Master Card by end of
   * reporting period Encounter Type Ids = 52 The earliest “Data de Levantamento” (Concept Id 23866
   * value_datetime) <= endDate
   *
   * @return
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
   * 6. All deaths, Transferred-out and Suspensions registered in Patient Program State by reporting
   * end date Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 10
   * (Died) or Patient_State.state = 7 (Transferred-out) or Patient_State.state = 8 (Suspended) and
   * Patient_State.start_date <= endDate Patient_state.end_date is null
   */
  @DocumentedDefinition(
      value = "patientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate")
  public CohortDefinition
      getPatientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate() {

    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate");

    definition.setQuery(
        TXCurrQueries.getPatientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 7. All deaths registered in Patient Demographics by reporting end date Person.Dead=1 and
   * death_date <= :endDate
   */
  @DocumentedDefinition(value = "deadPatientsInDemographiscByReportingEndDate")
  public CohortDefinition getDeadPatientsInDemographiscByReportingEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("deadPatientsInDemographiscByReportingEndDate");

    definition.setQuery(
        TXCurrQueries.getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate());
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));

    return definition;
  }

  /**
   * 8. All deaths registered in Last Home Visit Card by reporting end date Last Home Visit Card
   * (Encounter Type 21, 36, 37) Patient not found (Concept ID 2003) = NO (Concept ID 1066) Reason
   * of Not Finding (Concept ID 2031) = Died (COncpet Id 1383) Last Encounter_datetime <= endDate
   *
   * @return
   */
  @DocumentedDefinition(value = "patientDeathRegisteredInLastHomeVisitCardByReportingEndDate")
  public CohortDefinition getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientDeathRegisteredInLastHomeVisitCardByReportingEndDate");

    definition.setQuery(
        TXCurrQueries.getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate(
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getReasonPatientNotFound().getConceptId(),
            hivMetadata.getReasonPatientNotFoundByActivist2ndVisitConcept().getConceptId(),
            hivMetadata.getReasonPatientNotFoundByActivist3rdVisitConcept().getConceptId(),
            hivMetadata.getPatientHasDiedConcept().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 9. All deaths registered in Ficha Resumo and Ficha Clinica of Master Card by reporting end date
   * Encounter Type ID= 6 or 53 Estado de Permanencia (Concept Id 6272) = Dead (Concept ID 1366)
   * Encounter_datetime <= endDate
   */
  @DocumentedDefinition(value = "deadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate")
  public CohortDefinition getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("deadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate");

    definition.setQuery(
        TXCurrQueries.getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getPatientHasDiedConcept().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 10. All tranferred-outs registered in Ficha Resumo and Ficha Clinica of Master Card by
   * reporting end date Encounter Type ID= 6 or 53 Estado de Permanencia (Concept Id 6272) =
   * Transferred-out (Concept ID 1706) Encounter_datetime <= endDate
   */
  @DocumentedDefinition(
      value = "transferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate")
  public CohortDefinition
      getTransferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("transferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate");

    definition.setQuery(
        TXCurrQueries.getTransferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId(),
            hivMetadata.getTransferredOutConcept().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 11. All suspensions registered in Ficha Resumo and Ficha Clinica of Master Card by reporting
   * end date Encounter Type ID= 6 or 53 Estado de Permanencia (Concept Id 6272) = Suspended
   * (Concept ID 1709) Encounter_datetime <= endDate
   */
  @DocumentedDefinition(
      value = "patientSuspendedInFichaResumeAndClinicaOfMasterCardByReportEndDate")
  public CohortDefinition getPatientSuspendedInFichaResumeAndClinicaOfMasterCardByReportEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientSuspendedInFichaResumeAndClinicaOfMasterCardByReportEndDate");

    definition.setQuery(
        TXCurrQueries.getPatientSuspendedInFichaResumeAndClinicaOfMasterCardByReportEndDate(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId(),
            hivMetadata.getSuspendedTreatmentConcept().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 13..All patients having the most recent date between last scheduled drug pickup date (Fila) or
   * last scheduled consultation date (Ficha Seguimento or Ficha Clínica) or 30 days after last ART
   * pickup date (Recepção – Levantou ARV) and adding {@code numDays} days and this date being less
   * than reporting end Date. (For more clarifications refer to scenario Table 1)
   *
   * @param numDays
   */
  @DocumentedDefinition(value = "patientHavingLastScheduledDrugPickupDate")
  public CohortDefinition getPatientHavingLastScheduledDrugPickupDateDaysBeforeEndDate(
      int numDays) {
    SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientHavingLastScheduledDrugPickupDate");

    definition.setQuery(
        TXCurrQueries.getPatientHavingLastScheduledDrugPickupDate(
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            commonMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            numDays));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 14. All patients who do not have the next scheduled drug pick up date (Fila) and next scheduled
   * consultation date (Ficha de Seguimento or Ficha Clinica – Master Card) and ART Pickup date
   * (Recepção – Levantou ARV).
   *
   * @return
   */
  @DocumentedDefinition(value = "patientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup")
  public CohortDefinition getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup");

    definition.setQuery(
        TXCurrQueries.getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 555
   *
   * @return Cohort of patients who left ART program before or on end date(4). Includes: dead,
   *     transferred to, stopped and abandoned (patient state 10, 7, 8 or 9)
   */
  @DocumentedDefinition(value = "leftARTProgramBeforeOrOnEndDate")
  public CohortDefinition getPatientsWhoLeftARTProgramBeforeOrOnEndDate() {
    SqlCohortDefinition leftARTProgramBeforeOrOnEndDate = new SqlCohortDefinition();
    leftARTProgramBeforeOrOnEndDate.setName("leftARTProgramBeforeOrOnEndDate");

    String leftARTProgramQueryString =
        "select p.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%s"
            + " and ps.state in (%s) and ps.end_date is null and ps.start_date<=:onOrBefore and pg.location_id=:location group by p.patient_id";

    String abandonStates =
        StringUtils.join(
            Arrays.asList(
                hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId(),
                hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId(),
                hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId()),
            ',');

    leftARTProgramBeforeOrOnEndDate.setQuery(
        String.format(
            leftARTProgramQueryString, hivMetadata.getARTProgram().getProgramId(), abandonStates));

    leftARTProgramBeforeOrOnEndDate.addParameter(
        new Parameter("onOrBefore", "onOrBefore", Date.class));
    leftARTProgramBeforeOrOnEndDate.addParameter(
        new Parameter("location", "location", Location.class));
    return leftARTProgramBeforeOrOnEndDate;
  }

  /**
   * 666
   *
   * @return Cohort of patients that from the date scheduled for next drug pickup (concept
   *     5096=RETURN VISIT DATE FOR ARV DRUG) until end date have completed 28 days and have not
   *     returned
   */
  @DocumentedDefinition(value = "patientsThatMissedNexPickup")
  private CohortDefinition getPatientsThatMissedNexPickup() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsThatMissedNexPickup");
    String query =
        "SELECT patient_id FROM (SELECT p.patient_id,max(encounter_datetime) encounter_datetime FROM patient p INNER JOIN encounter e on e.patient_id=p.patient_id WHERE p.voided=0 AND e.voided=0 AND e.encounter_type=%s"
            + " AND e.location_id=:location AND e.encounter_datetime<=:onOrBefore group by p.patient_id ) max_frida INNER JOIN obs o on o.person_id=max_frida.patient_id WHERE max_frida.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id=%s"
            + " AND o.location_id=:location AND datediff(:onOrBefore,o.value_datetime)>=:abandonmentDays";
    definition.setQuery(
        String.format(
            query,
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.addParameter(new Parameter("abandonmentDays", "abandonmentDays", Integer.class));
    return definition;
  }

  /**
   * 777
   *
   * @return Cohort of patients that from the date scheduled for next follow up consultation
   *     (concept 1410=RETURN VISIT DATE) until the end date have not completed 28 days
   */
  @DocumentedDefinition(value = "patientsThatDidNotMissNextConsultation")
  private CohortDefinition getPatientsThatDidNotMissNextConsultation() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsThatDidNotMissNextConsultation");
    String query =
        "SELECT patient_id FROM "
            + "(SELECT p.patient_id,max(encounter_datetime) encounter_datetime "
            + "FROM patient p INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + "WHERE p.voided=0 AND e.voided=0 AND e.encounter_type in (%d, %d) "
            + "AND e.location_id=:location AND e.encounter_datetime<=:onOrBefore group by p.patient_id ) max_mov "
            + "INNER JOIN obs o ON o.person_id=max_mov.patient_id "
            + "WHERE max_mov.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id=%d "
            + "AND o.location_id=:location AND DATEDIFF(:onOrBefore,o.value_datetime)<:abandonmentDays";
    definition.setQuery(
        String.format(
            query,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId()));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.addParameter(new Parameter("abandonmentDays", "abandonmentDays", Integer.class));
    return definition;
  }

  /**
   * 888
   *
   * @return Cohort of patients that were registered as abandonment (program workflow state is
   *     9=ABANDONED) but from the date scheduled for next drug pick up (concept 5096=RETURN VISIT
   *     DATE FOR ARV DRUG) until the end date have not completed 28 days
   */
  @DocumentedDefinition(value = "patientsReportedAsAbandonmentButStillInPeriod")
  private CohortDefinition getPatientsReportedAsAbandonmentButStillInPeriod() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsReportedAsAbandonmentButStillInPeriod");
    String query =
        "SELECT abandono.patient_id FROM (SELECT pg.patient_id FROM patient p INNER JOIN patient_program pg ON p.patient_id=pg.patient_id INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id=%d "
            + "AND ps.state=%d "
            + "AND ps.end_date is null AND ps.start_date<=:onOrBefore AND location_id=:location )abandono INNER JOIN ( SELECT max_frida.patient_id,max_frida.encounter_datetime,o.value_datetime FROM ( SELECT p.patient_id,max(encounter_datetime) encounter_datetime FROM patient p INNER JOIN encounter e ON e.patient_id=p.patient_id WHERE p.voided=0 AND e.voided=0 AND e.encounter_type=%d "
            + "AND e.location_id=:location AND e.encounter_datetime<=:onOrBefore group by p.patient_id ) max_frida INNER JOIN obs o ON o.person_id=max_frida.patient_id WHERE max_frida.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id=%d "
            + "AND o.location_id=:location ) ultimo_fila ON abandono.patient_id=ultimo_fila.patient_id WHERE datediff(:onOrBefore,ultimo_fila.value_datetime)<:abandonmentDays";
    definition.setQuery(
        String.format(
            query,
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.addParameter(new Parameter("abandonmentDays", "abandonmentDays", Integer.class));
    return definition;
  }

  @DocumentedDefinition(value = "patientWhoAfterMostRecentDateHaveDrusPickupOrConsultation")
  public CohortDefinition getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultation() {
    SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientWhoAfterMostRecentDateHaveDrusPickupOrConsultation");

    definition.setQuery(
        TXCurrQueries.getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultation(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 12. Except all patients who after the most recent date from 2.1 to 2.6, have a drugs pick up or
   * consultation: Encounter Type ID= 6, 9, 18 and encounter_datetime> the most recent date or
   * Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866 value_datetime) > the most
   * recent date.
   */
  @DocumentedDefinition(
      value = "patientWhoAfterMostRecentDateHaveDrusPickupOrConsultationComposition")
  public CohortDefinition
      getPatientWhoAfterMostRecentDateHaveDrugPickupOrConsultationComposition() {
    SqlCohortDefinition defintion = new SqlCohortDefinition();

    defintion.setName("patientWhoAfterMostRecentDateHaveDrusPickupOrConsultationComposition");

    defintion.setQuery(
        TXCurrQueries.getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultationComposition(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getReasonPatientNotFound().getConceptId(),
            hivMetadata.getReasonPatientNotFoundByActivist2ndVisitConcept().getConceptId(),
            hivMetadata.getReasonPatientNotFoundByActivist3rdVisitConcept().getConceptId(),
            hivMetadata.getPatientHasDiedConcept().getConceptId(),
            hivMetadata.getStateOfStayOfPreArtPatient().getConceptId(),
            hivMetadata.getTransferredOutConcept().getConceptId(),
            hivMetadata.getSuspendedTreatmentConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getDefaultingMotiveConcept().getConceptId(),
            hivMetadata.getAutoTransferConcept().getConceptId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId()));

    defintion.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    defintion.addParameter(new Parameter("location", "location", Location.class));

    return defintion;
  }

  @DocumentedDefinition("<3 month of ARVs Dispensed")
  public CohortDefinition getPatientsWithLessThan3MonthsDispensationQuantity() {
    return getPatientsWithMonthlyTypeOfDispensation();
  }

  @DocumentedDefinition("3-5 months of ARVs dispensed")
  public CohortDefinition getPatientsWith3to5MonthsOfDispensationQuantity() {
    return getPatientsWithQuarterlyTypeOfDispensation();
  }

  @DocumentedDefinition("6 or more months of ARV dispensed")
  public CohortDefinition getPatientsWithMoreThan6MonthsOfDispensationQuantity() {
    return getPatientsWithSemiAnnualTypeOfDispensation();
  }

  @DocumentedDefinition(
      "patients whose next ART pick-up is scheduled for >173 days after the date of their last ART drug pick-up")
  private CohortDefinition getPatientsWithNextPickupMoreThan173Days() {
    return getPatientsWithNextPickupBetweenDaysAfterLastPharmacyEncounter(173, null);
  }

  @DocumentedDefinition(
      "Patients whose next ART pick-up is scheduled for 83-173 days after the date of their last ART drug pick-up")
  private CohortDefinition getPatientsWithNextPickup83to173Days() {
    return getPatientsWithNextPickupBetweenDaysAfterLastPharmacyEncounter(83, 173);
  }

  @DocumentedDefinition(
      "Patients whose next ART pick-up is scheduled for <83 days after the date of their last ART drug pick-up")
  private CohortDefinition getPatientsWithNextPickupLessThan83days() {
    return getPatientsWithNextPickupBetweenDaysAfterLastPharmacyEncounter(null, 83);
  }

  @DocumentedDefinition(
      "Patients marked as DM on Ficha Clinica Mastercard on last Tipo de Levantamento")
  public SqlCohortDefinition getPatientsWithMonthlyTypeOfDispensation() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    String sqlQuery =
        "SELECT pp.patient_id "
            + "FROM   ( "
            + "              SELECT lst.patient_id, "
            + "                     lst.encounter_datetime "
            + "              FROM   ( "
            + "                              SELECT   last_encounter.patient_id, "
            + "                                       last_encounter.encounter_id, "
            + "                                       last_encounter.encounter_datetime "
            + "                              FROM     ( "
            + "                                                SELECT   e.patient_id, "
            + "                                                         e.encounter_datetime, "
            + "                                                         e.encounter_id "
            + "                                                FROM     encounter e "
            + "                                                WHERE    e.encounter_type IN (${aRVPharmaciaEncounterType}, "
            + "                                                                              ${adultoSeguimentoEncounterType})"
            + "                                                AND      e.encounter_datetime <= :onOrBefore "
            + "                                                AND      e.location_id = :location "
            + "                                                AND      e.voided=0 "
            + "                                                ORDER BY e.encounter_type DESC, "
            + "                                                         e.encounter_datetime DESC ) AS last_encounter"
            + "                              GROUP BY last_encounter.patient_id) AS lst, "
            + "                     obs o "
            + "              WHERE  lst.encounter_id=o.encounter_id "
            + "              AND    o.voided=0 "
            + "              AND    (( "
            + "                                   o.concept_id= ${returnVisitDateForArvDrugConcept} "
            + "                            AND    timestampdiff(day, lst.encounter_datetime, o.value_datetime) < ${maxDays} )"
            + "                     OR     ( "
            + "                                   o.concept_id= ${typeOfDispensationConcept} "
            + "                            AND    o.value_coded = ${monthlyConcept}))) AS pp";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put(
        "aRVPharmaciaEncounterType",
        hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put(
        "returnVisitDateForArvDrugConcept",
        hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    valuesMap.put("maxDays", 83);
    valuesMap.put(
        "typeOfDispensationConcept", hivMetadata.getTypeOfDispensationConcept().getConceptId());
    valuesMap.put("monthlyConcept", hivMetadata.getMonthlyConcept().getConceptId());

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    cd.setQuery(sub.replace(sqlQuery));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }

  @DocumentedDefinition(
      "Patients marked as DT on Ficha Clinica Mastercard on last Tipo de Levantamento")
  public SqlCohortDefinition getPatientsWithQuarterlyTypeOfDispensation() {
    SqlCohortDefinition patientsWithQuarterlyTypeOfDispensation = new SqlCohortDefinition();
    String sqlQuery =
        "SELECT pp.patient_id "
            + "FROM   ( "
            + "              SELECT lst.patient_id, "
            + "                     lst.encounter_datetime "
            + "              FROM   ( "
            + "                              SELECT   last_encounter.patient_id, "
            + "                                       last_encounter.encounter_id, "
            + "                                       last_encounter.encounter_datetime "
            + "                              FROM     ( "
            + "                                                SELECT   e.patient_id, "
            + "                                                         e.encounter_datetime, "
            + "                                                         e.encounter_id "
            + "                                                FROM     encounter e "
            + "                                                WHERE    e.encounter_type IN(${aRVPharmaciaEncounterType},"
            + "                                                                             ${adultoSeguimentoEncounterType})"
            + "                                                AND      e.encounter_datetime <= :onOrBefore "
            + "                                                AND      e.location_id = :location "
            + "                                                AND      e.voided=0 "
            + "                                                ORDER BY e.encounter_type DESC, e.encounter_datetime DESC ) AS last_encounter "
            + "                              GROUP BY last_encounter.patient_id) AS lst, "
            + "                     obs o "
            + "              WHERE  lst.encounter_id=o.encounter_id "
            + "              AND    o.voided=0 "
            + "              AND    (( "
            + "                                   o.concept_id= ${returnVisitDateForArvDrugConcept} "
            + "                            AND    timestampdiff(day, lst.encounter_datetime, o.value_datetime) BETWEEN ${minDays} AND    ${maxDays} )"
            + "                     OR     ( "
            + "                                   o.concept_id= ${typeOfDispensationConcept} "
            + "                            AND    o.value_coded = ${quarterlyConcept}) "
            + "                     OR     ( "
            + "                                   o.concept_id= ${quarterlyConcept} "
            + "                            AND    o.value_coded IN (${startDrugsConcept}, "
            + "                                                     ${continueRegimen})))) AS pp";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put(
        "aRVPharmaciaEncounterType",
        hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put(
        "returnVisitDateForArvDrugConcept",
        hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    valuesMap.put("minDays", 83);
    valuesMap.put("maxDays", 173);
    valuesMap.put(
        "typeOfDispensationConcept", hivMetadata.getTypeOfDispensationConcept().getConceptId());
    valuesMap.put("quarterlyConcept", hivMetadata.getQuarterlyConcept().getConceptId());
    valuesMap.put("startDrugsConcept", hivMetadata.getStartDrugsConcept().getConceptId());
    valuesMap.put("continueRegimen", hivMetadata.getContinueRegimenConcept().getConceptId());

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    patientsWithQuarterlyTypeOfDispensation.setQuery(sub.replace(sqlQuery));

    patientsWithQuarterlyTypeOfDispensation.addParameter(
        new Parameter("onOrBefore", "Before Date", Date.class));
    patientsWithQuarterlyTypeOfDispensation.addParameter(
        new Parameter("onOrAfter", "After Date", Date.class));
    patientsWithQuarterlyTypeOfDispensation.addParameter(
        new Parameter("location", "Location", Location.class));
    return patientsWithQuarterlyTypeOfDispensation;
  }

  @DocumentedDefinition(
      "Patients marked as DS on Ficha Clinica Mastercard on last Tipo de Levantamento")
  public SqlCohortDefinition getPatientsWithSemiAnnualTypeOfDispensation() {
    SqlCohortDefinition patientsWithSemiAnnualTypeOfDispensation = new SqlCohortDefinition();
    String sqlQuery =
        "SELECT pp.patient_id "
            + "FROM   ( "
            + "              SELECT lst.patient_id, "
            + "                     lst.encounter_datetime "
            + "              FROM   ( "
            + "                              SELECT   last_encounter.patient_id, "
            + "                                       last_encounter.encounter_id, "
            + "                                       last_encounter.encounter_datetime "
            + "                              FROM     ( "
            + "                                                SELECT   e.patient_id, "
            + "                                                         e.encounter_datetime, "
            + "                                                         e.encounter_id "
            + "                                                FROM     encounter e "
            + "                                                WHERE    e.encounter_type IN(${aRVPharmaciaEncounterType},"
            + "                                                                             ${adultoSeguimentoEncounterType})"
            + "                                                AND      e.encounter_datetime <= :onOrBefore "
            + "                                                AND      e.location_id = :location "
            + "                                                AND      e.voided=0 "
            + "                                                ORDER BY e.encounter_type DESC, e.encounter_datetime DESC) AS last_encounter "
            + "                              GROUP BY last_encounter.patient_id) AS lst, "
            + "                     obs o "
            + "              WHERE  lst.encounter_id=o.encounter_id "
            + "              AND    o.voided=0 "
            + "              AND    (( "
            + "                                   o.concept_id= ${returnVisitDateForArvDrugConcept} "
            + "                            AND    timestampdiff(day, lst.encounter_datetime, o.value_datetime) > ${minDays} )"
            + "                     OR     ( "
            + "                                   o.concept_id= ${typeOfDispensationConcept} "
            + "                            AND    o.value_coded = ${semiannualDispensation}) "
            + "                     OR     ( "
            + "                                   o.concept_id= ${semiannualDispensation} "
            + "                            AND    o.value_coded IN (${startDrugsConcept}, "
            + "                                                     ${continueRegimen})))) AS pp";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put(
        "aRVPharmaciaEncounterType",
        hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put(
        "returnVisitDateForArvDrugConcept",
        hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    valuesMap.put("minDays", 173);
    valuesMap.put(
        "typeOfDispensationConcept", hivMetadata.getTypeOfDispensationConcept().getConceptId());
    valuesMap.put("semiannualDispensation", hivMetadata.getSemiannualDispensation().getConceptId());
    valuesMap.put("startDrugsConcept", hivMetadata.getStartDrugsConcept().getConceptId());
    valuesMap.put("continueRegimen", hivMetadata.getContinueRegimenConcept().getConceptId());

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    patientsWithSemiAnnualTypeOfDispensation.setQuery(sub.replace(sqlQuery));

    patientsWithSemiAnnualTypeOfDispensation.addParameter(
        new Parameter("onOrBefore", "Before Date", Date.class));
    patientsWithSemiAnnualTypeOfDispensation.addParameter(
        new Parameter("onOrAfter", "After Date", Date.class));
    patientsWithSemiAnnualTypeOfDispensation.addParameter(
        new Parameter("location", "Location", Location.class));
    return patientsWithSemiAnnualTypeOfDispensation;
  }

  @DocumentedDefinition(
      "Patients with last “Dispensa Trimestral (DT)” as Iniciar (I) or Manter (C)")
  private CohortDefinition getPatientsWithStartOrContinueOnQuarterlyDispensation() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getQuarterlyDispensation());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getStartDrugsConcept());
    cd.addValue(hivMetadata.getContinueRegimenConcept());
    return cd;
  }

  @DocumentedDefinition("Patients with last “Dispensa Semestral (DS)” as Iniciar (I) or Manter (C)")
  private CohortDefinition getPatientsWithStartOrContinueOnSemiannualDispensation() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getSemiannualDispensation());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getStartDrugsConcept());
    cd.addValue(hivMetadata.getContinueRegimenConcept());
    return cd;
  }

  @DocumentedDefinition(
      "Patients who are marked Completed for their last “Dispensa Trimestral (DT)")
  private CohortDefinition getPatientsWithCompletedOnQuarterlyDispensation() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getQuarterlyDispensation());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getCompletedConcept());
    return cd;
  }

  @DocumentedDefinition("Patients who are marked Completed for their last “Dispensa Semestral (DS)")
  private CohortDefinition getPatientsWithCompletedOnSemiannualDispensation() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getSemiannualDispensation());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getCompletedConcept());
    return cd;
  }

  private CohortDefinition getPatientsWithNextPickupBetweenDaysAfterLastPharmacyEncounter(
      Integer minDays, Integer maxDays) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    StringBuilder sb =
        new StringBuilder()
            .append("select p.patient_id ")
            .append("from patient p ")
            .append(
                "         join (select e.patient_id, e.encounter_datetime, max(o.value_datetime) value_datetime ")
            .append("               from encounter e ")
            .append(
                "                        join (select patient_id, max(encounter_datetime) encounter_datetime ")
            .append("                              from encounter e ")
            .append("                              where e.voided = 0 ")
            .append("                                and e.encounter_type = %d ")
            .append("                                and e.encounter_datetime <= :onOrBefore ")
            .append("                                and e.location_id = :location ")
            .append("                              group by patient_id) last ")
            .append("                             on e.patient_id = last.patient_id ")
            .append(
                "                                 and e.encounter_datetime = last.encounter_datetime ")
            .append("                        join obs o on e.encounter_id = o.encounter_id ")
            .append("               where e.voided = 0 ")
            .append("                 and o.voided = 0 ")
            .append("                 and o.concept_id = %d ")
            .append("                 and e.location_id = :location ")
            .append("                 and e.encounter_type = %d ")
            .append("               group by e.patient_id, e.encounter_datetime) last_obs ")
            .append("              on p.patient_id = last_obs.patient_id ");

    if (minDays != null && maxDays != null) {
      sb.append(
              "  where timestampdiff(DAY, last_obs.encounter_datetime, last_obs.value_datetime) BETWEEN ")
          .append(minDays)
          .append(" AND ")
          .append(maxDays);
    } else if (minDays == null) {
      sb.append(
              "  where timestampdiff(DAY, last_obs.encounter_datetime, last_obs.value_datetime) < ")
          .append(maxDays);
    } else {
      sb.append(
              "  where timestampdiff(DAY, last_obs.encounter_datetime, last_obs.value_datetime) > ")
          .append(minDays);
    }
    sb.append(" and p.voided = 0");

    cd.setQuery(
        String.format(
            sb.toString(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()));

    return cd;
  }

  @DocumentedDefinition("Patients with S.TARV FARMACIA encounter type")
  private CohortDefinition getPatientsWithArvFarmaciaEncounterType() {
    EncounterCohortDefinition cd = new EncounterCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getARVPharmaciaEncounterType());
    return cd;
  }

  /** 15. 2.7 = 15 since version 4 . All transferred-outs registered in Last Home Visit Card */
  @DocumentedDefinition(value = "Patients Transfered Out In Last Home Visit Card")
  public CohortDefinition getPatientsTransferedOutInLastHomeVisitCard() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Patients Transfered Out In Last Home Visit Card");

    definition.setQuery(
        TXCurrQueries.getPatientsTransferedOutInLastHomeVisitCard(
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getDefaultingMotiveConcept().getConceptId(),
            hivMetadata.getTransferredOutConcept().getConceptId(),
            hivMetadata.getAutoTransferConcept().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }
}
