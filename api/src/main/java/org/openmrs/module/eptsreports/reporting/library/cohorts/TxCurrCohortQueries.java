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
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXCurrQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TxCurrCohortQueries we want to expose for EPTS */
@Component
public class TxCurrCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private HivCohortQueries hivCohortQueries;

  /**
   * @param cohortName Cohort name
   * @param currentSpec
   * @return TxCurr composition cohort definition
   */
  @DocumentedDefinition(value = "getTxCurrCompositionCohort")
  public CohortDefinition getTxCurrCompositionCohort(String cohortName, boolean currentSpec) {

    CompositionCohortDefinition txCurrComposition = new CompositionCohortDefinition();
    txCurrComposition.setName(cohortName);

    txCurrComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    txCurrComposition.addParameter(new Parameter("location", "location", Location.class));

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
            "5",
            EptsReportUtils.map(
                getPatientsWhoHavePickedUpDrugsMasterCardByEndReporingPeriod(),
                "onOrBefore=${onOrBefore},location=${location}"));
    // section 2
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
            "7",
            EptsReportUtils.map(
                getDeadPatientsInDemographiscByReportingEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
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
                getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultation(),
                "location=${location}"));
    // section 3
    txCurrComposition
        .getSearches()
        .put(
            "13",
            EptsReportUtils.map(
                getPatientHavingLastScheduledDrugPickupDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "14",
            EptsReportUtils.map(
                getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup(),
                "location=${location}"));

    String compositionString;
    if (currentSpec) {
      compositionString =
          "(1 OR 2 OR 3 OR 4 OR 5) AND NOT ((6 OR 7 OR 8 OR 9 OR 10 OR 11) AND NOT 12) AND NOT (13 OR 14)";
    } else {
      compositionString = "(1 OR 2 OR 3 OR 4) AND (NOT (5 OR (6 AND (NOT (7 OR 8)))))";
    }

    txCurrComposition.setCompositionString(compositionString);
    return txCurrComposition;
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
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getStartDrugsConcept().getConceptId()));
    patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    patientWithSTARTDRUGSObs.addParameter(new Parameter("location", "location", Location.class));
    return patientWithSTARTDRUGSObs;
  }

  /**
   * 3.All patients enrolled in ART Program by end of reporting period. (3) Table: patient_program
   * Criterias: program_id=2, patient_state_id=29 and date_enrolled <= endDate
   */
  @DocumentedDefinition(value = "patientEnrolledInArtProgramByEndReportingPeriod")
  public CohortDefinition getPatientEnrolledInArtProgramByEndReportingPeriod() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientEnrolledInArtProgramByEndReportingPeriod");

    definition.setQuery(
        TXCurrQueries.getPatientEnrolledInArtProgramByEndReportingPeriod(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getArtTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));

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
        " select p.patient_id "
            + " from patient p "
            + " inner join encounter e on  e.patient_id=p.patient_id "
            + " inner join obs o on  o.encounter_id=e.encounter_id "
            + " where  e.encounter_type = %s and o.concept_id = %s "
            + " and e.encounter_datetime <= :onOrBefore and e.location_id = :location "
            + " and p.voided =0 and e.voided=0  and o.voided = 0 group by p.patient_id";

    definition.setQuery(
        String.format(
            query,
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickup().getConceptId()));

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
    definition.addParameter(new Parameter("location", "location", Location.class));

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

    String encounterTypes =
        StringUtils.join(
            Arrays.asList(
                hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
                hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
                hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId()),
            ',');

    definition.setQuery(
        TXCurrQueries.getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate(
            encounterTypes,
            hivMetadata.getPatientFoundConcept().getConceptId(),
            hivMetadata.getReasonPatientNotFound().getConceptId(),
            hivMetadata.getNoConcept().getConceptId(),
            hivMetadata.getPatientIsDead().getConceptId()));

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
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId(),
            hivMetadata.getSuspendedTreatmentConcept().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 12. Except all patients who after the most recent date from 2.1 to 2.6, have a drugs pick up or
   * consultation: Encounter Type ID= 6, 9, 18 and encounter_datetime> the most recent date or
   * Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866 value_datetime) > the most
   * recent date.
   */
  @DocumentedDefinition(value = "patientWhoAfterMostRecentDateHaveDrusPickupOrConsultation")
  public CohortDefinition getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultation() {
    SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientWhoAfterMostRecentDateHaveDrusPickupOrConsultation");

    definition.setQuery(
        TXCurrQueries.getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultation(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickup().getConceptId()));

    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 13..All patients having the most recent date between last scheduled drug pickup date (Fila) or
   * last scheduled consultation date (Ficha Seguimento or Ficha Clínica) or 30 days after last ART
   * pickup date (Recepção – Levantou ARV) and adding 30 days and this date being less than
   * reporting end Date. (For more clarifications refer to scenario Table 1)
   */
  @DocumentedDefinition(value = "patientHavingLastScheduledDrugPickupDate")
  public CohortDefinition getPatientHavingLastScheduledDrugPickupDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientHavingLastScheduledDrugPickupDate");

    definition.setQuery(
        TXCurrQueries.getPatientHavingLastScheduledDrugPickupDate(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            commonMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getArtDatePickup().getConceptId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId()));

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
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));

    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }
}
