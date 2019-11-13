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

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.Eri4MonthsQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition} for pepfar early
 * indicator report
 */
@Component
public class Eri4MonthsCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private EriCohortQueries eriCohortQueries;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  /**
   * C Get all patients who initiated treatment and had a drug pick up or had a consultation between
   * 61 and 120 days from the encounter date
   */
  public CohortDefinition
      getAllPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate() {
    final SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients who had consultation between 61 to 120 days from encounter date");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        Eri4MonthsQueries
            .allPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate(
                this.hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
                this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                this.hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                this.hivMetadata.getARVPlanConcept().getConceptId(),
                this.hivMetadata.getStartDrugsConcept().getConceptId(),
                this.hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
                this.hivMetadata.getARTProgram().getProgramId(),
                this.hivMetadata
                    .getTransferredFromOtherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()));
    return cd;
  }

  /**
   * Get lost to follow up patients
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreLostToFollowUpWithinPeriod() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get lost to follow up patients within period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    final int daysThreshold = 60;

    cd.addSearch(
        "drugPickupLTFU",
        EptsReportUtils.map(
            this.getPatientsLostToFollowUpOnDrugPickup(daysThreshold),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "consultationLTFU",
        EptsReportUtils.map(
            this.getPatientsLostToFollowUpOnConsultation(daysThreshold),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("drugPickupLTFU AND consultationLTFU");

    return cd;
  }

  private SqlCohortDefinition getPatientsLostToFollowUpOnConsultation(final int daysThreshold) {
    final SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        Eri4MonthsQueries.getPatientsLostToFollowUpOnConsultation(
            this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            this.hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            this.hivMetadata.getReturnVisitDateConcept().getConceptId(),
            daysThreshold));
    return cd;
  }

  private SqlCohortDefinition getPatientsLostToFollowUpOnDrugPickup(final int daysThreshold) {
    final SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        Eri4MonthsQueries.getPatientsLostToFollowUpOnDrugPickup(
            this.hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            this.hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            daysThreshold));
    return cd;
  }

  /**
   * Get patients who are alive and on treatment - probably all those who have been on ART for more
   * than 3 months excluding the dead, transfers or suspended (A AND NOT B) AND C
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreAliveAndOnTreatment() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who are a live and on treatment");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            this.eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "consultation",
        EptsReportUtils.map(
            this
                .getAllPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate(),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            this.genericCohortQueries.getDeceasedPatients(),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "transfersOut",
        EptsReportUtils.map(
            this.genericCohortQueries.getPatientsBasedOnPatientStates(
                this.hivMetadata.getARTProgram().getProgramId(),
                this.hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.setCompositionString("(initiatedArt AND consultation) AND NOT (dead OR transfersOut)");
    return cd;
  }

  /**
   * Get lost to follow up patients within 4 months from ART initiation
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAllPatientsWhoAreLostToFollowUpDuringPeriod() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Lost to follow up patients");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            this.eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "missedVisit",
        EptsReportUtils.map(
            this.getPatientsWhoAreLostToFollowUpWithinPeriod(),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            this.genericCohortQueries.getDeceasedPatients(),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "transfersOut",
        EptsReportUtils.map(
            this.genericCohortQueries.getPatientsBasedOnPatientStates(
                this.hivMetadata.getARTProgram().getProgramId(),
                this.hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.setCompositionString("initiatedArt AND missedVisit AND NOT (dead OR transfersOut)");
    return cd;
  }

  /**
   * Get patients who are alive and on treatment - probably all those who have been on ART for more
   * than 3 months excluding the dead, transfers or suspended (A AND NOT B) AND C
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreAliveAndNotOnTreatment() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who are a live and NOT treatment");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            this.eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "consultation",
        EptsReportUtils.map(
            this
                .getAllPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate(),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            this.genericCohortQueries.getDeceasedPatients(),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "transfersOut",
        EptsReportUtils.map(
            this.genericCohortQueries.getPatientsBasedOnPatientStates(
                this.hivMetadata.getARTProgram().getProgramId(),
                this.hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.setCompositionString("initiatedArt AND NOT (consultation OR dead OR transfersOut)");
    return cd;
  }

  public CohortDefinition findPatientsWhoAreLostFollowUp() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("Patients who are a Lost Follow Up");

    definition.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    definition.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    definition.addParameter(new Parameter("reportingEndDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.addSearch(
        "IART",
        EptsReportUtils.map(
            this.txNewCohortQueries.getTxNewCompositionCohort("patientNewlyEnrolledInART"),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));

    definition.addSearch(
        "LFU",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreLostFollowUp", Eri4MonthsQueries.findPatientsWhoAreLostFollowUp()),
            "endDate=${reportingEndDate},location=${location}"));

    definition.setCompositionString("IART AND LFU");

    return definition;
  }
}
