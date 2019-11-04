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

  private static final String HAS_NEXT_APPOINTMENT_QUERY =
      "select distinct obs.person_id from obs "
          + "where obs.obs_datetime <= :onOrBefore and obs.location_id = :location and obs.concept_id = %s and obs.voided = false and obs.value_datetime is not null "
          + "and obs.obs_datetime = (select max(encounter.encounter_datetime) from encounter "
          + "where encounter.encounter_type in (%s) and encounter.patient_id = obs.person_id and encounter.location_id = obs.location_id and encounter.voided = false and encounter.encounter_datetime <= :onOrBefore) ";

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

    String compositionString =
        "(1 OR 2 OR 3 OR 4 OR 5) AND NOT ((6 OR 7 OR 8 OR 9 OR 10 OR 11) AND NOT 12) AND NOT (13 OR 14)";
    //    if (currentSpec) {
    //      compositionString =
    //          "(1.a OR 1.b OR 1.c OR 1.d OR 1.e) AND NOT ((2.1 OR 2.2 OR 2.3 OR 2.4 OR 2.5 OR 2.6)
    // AND NOT 2.7 ) AND NOT (3.1 OR 3.2)";
    //
    //    } else {
    //      compositionString = "(1 OR 2 OR 3 OR 4) AND (NOT (5 OR (6 AND (NOT (7 OR 8)))))";
    //    }

    txCurrComposition.setCompositionString(compositionString);
    return txCurrComposition;
  }

  /**
   * 1.d
   *
   * @return Cohort of patients with first drug pickup (encounter type 18=S.TARV: FARMACIA) before
   *     or on end date
   */
  @DocumentedDefinition(value = "patientWithFirstDrugPickupEncounter")
  private CohortDefinition getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate() {
    SqlCohortDefinition patientWithFirstDrugPickupEncounter = new SqlCohortDefinition();
    patientWithFirstDrugPickupEncounter.setName("patientWithFirstDrugPickupEncounter");
    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "WHERE p.voided=0 AND e.encounter_type=%d "
            + "AND e.voided=0 AND e.encounter_datetime <= :onOrBefore AND e.location_id=:location GROUP BY p.patient_id";
    patientWithFirstDrugPickupEncounter.setQuery(
        String.format(query, hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()));
    patientWithFirstDrugPickupEncounter.addParameter(
        new Parameter("onOrBefore", "onOrBefore", Date.class));
    patientWithFirstDrugPickupEncounter.addParameter(
        new Parameter("location", "location", Location.class));
    return patientWithFirstDrugPickupEncounter;
  }

  /**
   * @return Cohort of patients registered as START DRUGS (answer to question 1255 = ARV PLAN is
   *     1256 = START DRUGS) in the first drug pickup (encounter type 18=S.TARV: FARMACIA) or follow
   *     up consultation for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
   *     9=S.TARV: PEDIATRIA SEGUIMENTO) before or on end date
   */
  @DocumentedDefinition(value = "patientWithSTARTDRUGSObs")
  public CohortDefinition getPatientWithSTARTDRUGSObsBeforeOrOnEndDate() {
    SqlCohortDefinition patientWithSTARTDRUGSObs = new SqlCohortDefinition();
    patientWithSTARTDRUGSObs.setName("patientWithSTARTDRUGSObs");
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "WHERE e.voided=0 AND o.voided=0 AND p.voided=0 AND e.encounter_type in (%d, %d, %d) "
            + "AND o.concept_id=%d AND o.value_coded in (%d) "
            + "AND e.encounter_datetime <= :onOrBefore AND e.location_id=:location GROUP BY p.patient_id";
    patientWithSTARTDRUGSObs.setQuery(
        String.format(
            query,
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
   * @return Cohort of patients who left ART program before or on end date(4). Includes: dead,
   *     transferred to, stopped and abandoned (patient state 10, 7, 8 or 9)
   */
  @DocumentedDefinition(value = "leftARTProgramBeforeOrOnEndDate")
  private CohortDefinition getPatientsWhoLeftARTProgramBeforeOrOnEndDate() {
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
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId()));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.addParameter(new Parameter("abandonmentDays", "abandonmentDays", Integer.class));
    return definition;
  }

  /**
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

  /**
   * Patients that have next pickup date set on their most recent encounter
   *
   * @return
   */
  @DocumentedDefinition(value = "patientsWithNextPickupDate")
  private CohortDefinition getPatientsWithNextPickupDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsWithNextPickupDate");
    String encounterTypes =
        StringUtils.join(
            Arrays.asList(hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()), ',');
    definition.setQuery(
        String.format(
            HAS_NEXT_APPOINTMENT_QUERY,
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            encounterTypes));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    return definition;
  }

  /**
   * Patients that have next consultation date set on their most recent encounter
   *
   * @return
   */
  @DocumentedDefinition(value = "patientsWithNextConsultationDate")
  private CohortDefinition getPatientsWithNextConsultationDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsWithNextConsultationDate");
    String encounterTypes =
        StringUtils.join(
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId()),
            ',');
    definition.setQuery(
        String.format(
            HAS_NEXT_APPOINTMENT_QUERY,
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            encounterTypes));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    return definition;
  }

  /**
   * 1.e All patients who have picked up drugs (Recepção Levantou ARV) – Master Card by end of
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
   * 2.1 All deaths, Transferred-out and Suspensions registered in Patient Program State by
   * reporting end date Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and
   * Patient_State.state = 10 (Died) or Patient_State.state = 7 (Transferred-out) or
   * Patient_State.state = 8 (Suspended) and Patient_State.start_date <= endDate
   * Patient_state.end_date is null
   */
  @DocumentedDefinition(
      value = "patientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate")
  private CohortDefinition
      getPatientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate() {

    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate");

    String states =
        StringUtils.join(
            Arrays.asList(
                hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId(),
                hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId()),
            ',');

    String query =
        " select p.patient_id from patient p "
            + " inner join patient_program pg on p.patient_id=pg.patient_id "
            + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + " where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%s "
            + " and ps.state in (%s) and ps.end_date is null and ps.start_date<=:onOrBefore "
            + "and pg.location_id=:location group by p.patient_id  ";

    definition.setQuery(String.format(query, hivMetadata.getARTProgram().getProgramId(), states));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 2.2 All deaths registered in Patient Demographics by reporting end date Person.Dead=1 and
   * death_date <= :endDate
   */
  @DocumentedDefinition(value = "deadPatientsInDemographiscByReportingEndDate")
  public CohortDefinition getDeadPatientsInDemographiscByReportingEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("deadPatientsInDemographiscByReportingEndDate");
    String query =
        "select p.patient_id  "
            + " from patient p "
            + " inner join person prs on prs.person_id=p.patient_id "
            + " inner join  encounter e on  e.patient_id=p.patient_id "
            + " where prs.dead=1 and prs.death_date <= :onOrBefore and p.voided=0 and prs.voided=0 "
            + " and e.location_id = :location group by p.patient_id ";

    definition.setQuery(query);
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 2.3 All deaths registered in Last Home Visit Card by reporting end date Last Home Visit Card
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

    String query =
        "select  p.patient_id"
            + " from patient p "
            + " inner join encounter e on e.patient_id=p.patient_id "
            + " inner join obs o  on o.encounter_id=e.encounter_id "
            + " where  p.voided=0  and e.voided=0 and o.voided=0 "
            + " and e.encounter_type in (%s) and o.concept_id in (%s,%s) and o.value_coded in (%s,%s) "
            + " and e.encounter_datetime <= :onOrBefore and  e.location_id = :location group by p.patient_id ";

    definition.setQuery(
        String.format(
            query,
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
   * 2.4 All deaths registered in Ficha Resumo and Ficha Clinica of Master Card by reporting end
   * date Encounter Type ID= 6 or 53 Estado de Permanencia (Concept Id 6272) = Dead (Concept ID
   * 1366) Encounter_datetime <= endDate
   */
  @DocumentedDefinition(value = "deadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate")
  public CohortDefinition getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("deadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate");

    String query =
        "select  p.patient_id "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + "and o.concept_id=%s and   o.value_coded=%s "
            + "and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + "group by p.patient_id";

    definition.setQuery(
        String.format(
            query,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId(),
            hivMetadata.getPatientHasDiedConcept().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 2.5 All tranferred-outs registered in Ficha Resumo and Ficha Clinica of Master Card by
   * reporting end date Encounter Type ID= 6 or 53 Estado de Permanencia (Concept Id 6272) =
   * Transferred-out (Concept ID 1706) Encounter_datetime <= endDate
   */
  @DocumentedDefinition(
      value = "transferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate")
  public CohortDefinition
      getTransferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("transferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate");

    String query =
        "select  p.patient_id "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + "and o.concept_id=%s and   o.value_coded=%s "
            + "and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + "group by p.patient_id ";

    definition.setQuery(
        String.format(
            query,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId(),
            hivMetadata.getTransferredOutConcept().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 2.6 All suspensions registered in Ficha Resumo and Ficha Clinica of Master Card by reporting
   * end date Encounter Type ID= 6 or 53 Estado de Permanencia (Concept Id 6272) = Suspended
   * (Concept ID 1709) Encounter_datetime <= endDate
   */
  @DocumentedDefinition(
      value = "patientSuspendedInFichaResumeAndClinicaOfMasterCardByReportEndDate")
  public CohortDefinition getPatientSuspendedInFichaResumeAndClinicaOfMasterCardByReportEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientSuspendedInFichaResumeAndClinicaOfMasterCardByReportEndDate");

    String query =
        "select  p.patient_id "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + "and o.concept_id=%s and   o.value_coded=%s "
            + "and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + "group by p.patient_id ";

    definition.setQuery(
        String.format(
            query,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId(),
            hivMetadata.getSuspendedTreatmentConcept().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 2.7 Except all patients who after the most recent date from 2.1 to 2.6, have a drugs pick up or
   * consultation: Encounter Type ID= 6, 9, 18 and encounter_datetime> the most recent date or
   * Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866 value_datetime) > the most
   * recent date.
   */
  @DocumentedDefinition(value = "patientWhoAfterMostRecentDateHaveDrusPickupOrConsultation")
  public CohortDefinition getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultation() {
    SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientWhoAfterMostRecentDateHaveDrusPickupOrConsultation");

    String query =
        " select p.patient_id "
            + " from patient p "
            + " inner join encounter e on  e.patient_id = p.patient_id "
            + " where  p.voided=0  and e.voided=0 "
            + " and e.encounter_type in (%s,%s,%s)  "
            + " and e.encounter_datetime = (select  max(encounter_datetime) from encounter where  patient_id= p.patient_id) "
            + " and e.location_id= :location group by p.patient_id "
            + " union "
            + " select p.patient_id "
            + " from patient p "
            + " inner join encounter e on e.patient_id=p.patient_id "
            + " inner join obs o on o.encounter_id=e.encounter_id "
            + " where e.encounter_type = %s and p.voided=0  and e.voided=0 and o.voided=0 "
            + " and o.concept_id=%s  "
            + " and e.encounter_datetime = (select  max(encounter_datetime) from encounter where  patient_id= p.patient_id) "
            + " and e.location_id= 6 group by p.patient_id ";

    definition.setQuery(
        String.format(
            query,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickup().getConceptId()));

    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * 3.1.All patients having the most recent date between last scheduled drug pickup date (Fila) or
   * last scheduled consultation date (Ficha Seguimento or Ficha Clínica) or 30 days after last ART
   * pickup date (Recepção – Levantou ARV) and adding 30 days and this date being less than
   * reporting end Date. (For more clarifications refer to scenario Table 1)
   */
  @DocumentedDefinition(value = "patientHavingLastScheduledDrugPickupDate")
  public CohortDefinition getPatientHavingLastScheduledDrugPickupDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientHavingLastScheduledDrugPickupDate");

    String query =
        "select p.patient_id   "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 "
            + "and e.encounter_type= %s and o.value_datetime is not null "
            + "and o.concept_id= %s  "
            + "and e.encounter_datetime = (select  max(encounter_datetime) from encounter where  patient_id= p.patient_id)   "
            + "and e.encounter_datetime <  date_add(:onOrBefore, interval 30 day) and e.location_id = :location "
            + "union  "
            + "select p.patient_id   "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 "
            + "and e.encounter_type in (%s,%s) and o.concept_id= %s  "
            + "and e.encounter_datetime = (select  max(encounter_datetime) from encounter where  patient_id= p.patient_id) "
            + "and e.encounter_datetime <  date_add(:onOrBefore, interval 30 day) and e.location_id = :location "
            + "union "
            + "select p.patient_id   "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 "
            + "and o.concept_id=%s and o.value_datetime is not null and e.encounter_type = %s "
            + "and e.encounter_datetime = (select  max(encounter_datetime) from encounter where  patient_id= p.patient_id) "
            + "and e.encounter_datetime <  date_add(:onOrBefore, interval 30 day) and e.location_id = :location";

    definition.setQuery(
        String.format(
            query,
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
   * 3.2 All patients who do not have the next scheduled drug pick up date (Fila) and next scheduled
   * consultation date (Ficha de Seguimento or Ficha Clinica – Master Card) and ART Pickup date
   * (Recepção – Levantou ARV).
   *
   * @return
   */
  @DocumentedDefinition(value = "patientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup")
  public CohortDefinition getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup");
    String query =
        "select p.patient_id   "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 "
            + "and e.encounter_type not in (%s,%s,%s,%s)  "
            + "and  e.location_id= :location "
            + "group by p.patient_id "
            + "union "
            + "select p.patient_id   "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 "
            + "and  e.encounter_datetime =  (select  max(encounter_datetime) from encounter where  patient_id= p.patient_id) "
            + "and e.encounter_type in (%s,%s,%s)    "
            + "and (o.concept_id not in (%s, %s) or o.encounter_id is  null) "
            + "and  e.location_id= :location "
            + "group by p.patient_id";

    definition.setQuery(
        String.format(
            query,
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
  /**
   * 1.c.All patients enrolled in ART Program by end of reporting period. (3) Table: patient_program
   * Criterias: program_id=2, patient_state_id=29 and date_enrolled <= endDate
   */
  @DocumentedDefinition(value = "patientEnrolledInArtProgramByEndReportingPeriod")
  public CohortDefinition getPatientEnrolledInArtProgramByEndReportingPeriod() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientEnrolledInArtProgramByEndReportingPeriod");

    String query =
        "select p.patient_id  "
            + "from patient p "
            + "inner join patient_program pg on pg.patient_id=p.patient_id "
            + "inner  join patient_state ps on ps.patient_program_id=pg.patient_program_id "
            + "where p.voided=0 and  pg.voided=0  and ps.voided=0 "
            + "and pg.program_id=%s and  ps.state=%s and pg.date_enrolled <= :onOrBefore  "
            + "and pg.location_id= :location group by p.patient_id ";

    definition.setQuery(
        String.format(
            query,
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getArtTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }
}
