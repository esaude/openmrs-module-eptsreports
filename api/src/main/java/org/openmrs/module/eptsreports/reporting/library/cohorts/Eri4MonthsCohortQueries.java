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

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

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

  @Autowired private HivCohortQueries hivCohortQueries;

  /**
   * C Get all patients who initiated treatment and had a drug pick up or had a consultation between
   * 61 and 120 days from the encounter date
   */
  public CohortDefinition
      getAllPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients who had consultation between 61 to 120 days from encounter date");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        Eri4MonthsQueries
            .allPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate(
                hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPlanConcept().getConceptId(),
                hivMetadata.getStartDrugsConcept().getConceptId(),
                hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getArtPickupConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId(),
                hivMetadata.getArtDatePickupMasterCard().getConceptId(),
                hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId()));
    return cd;
  }

  /**
   * Get lost to follow up patients
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreLostToFollowUpWithinPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get lost to follow up patients within period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    int daysThreshold = 60;

    cd.addSearch(
        "drugPickupLTFU",
        EptsReportUtils.map(
            getPatientsLostToFollowUpOnDrugPickup(daysThreshold),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "consultationLTFU",
        EptsReportUtils.map(
            getPatientsLostToFollowUpOnConsultation(daysThreshold),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("drugPickupLTFU AND consultationLTFU");

    return cd;
  }

  private SqlCohortDefinition getPatientsLostToFollowUpOnConsultation(int daysThreshold) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        Eri4MonthsQueries.getPatientsLostToFollowUpOnConsultation(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            daysThreshold));
    return cd;
  }

  private SqlCohortDefinition getPatientsLostToFollowUpOnDrugPickup(int daysThreshold) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        Eri4MonthsQueries.getPatientsLostToFollowUpOnDrugPickup(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
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
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who are a live and on treatment");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArtNOTTransferredInBeforeReportingEndDate(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "consultation",
        EptsReportUtils.map(
            getAllPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate(),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatients(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "transfersOut",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata
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
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Lost to follow up patients");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArtNOTTransferredInBeforeReportingEndDate(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "missedVisit",
        EptsReportUtils.map(
            getPatientsWhoAreLostToFollowUpWithinPeriod(),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatients(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "transfersOut",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata
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
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who are a live and NOT treatment");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArtNOTTransferredInBeforeReportingEndDate(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "consultation",
        EptsReportUtils.map(
            getAllPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate(),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatients(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "transfersOut",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.setCompositionString("initiatedArt AND NOT (consultation OR dead OR transfersOut)");
    return cd;
  }

  /**
   * Get lost to follow up patients who are not dead, transferred out or stopped treatment
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition getPatientsLostToFollowUpAndNotDeadTransferredOrStoppedTreatment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Patients who are lost to follow up and not dead, transferred out or stopped treatment");

    cd.addSearch(
        "ltfu", mapStraightThrough(genericCohortQueries.getPatientsWhoToLostToFollowUp(60)));
    cd.addSearch("dead", mapStraightThrough(genericCohortQueries.getDeceasedPatients()));
    cd.addSearch("transferOut", mapStraightThrough(hivCohortQueries.getPatientsTransferredOut()));
    cd.addSearch(
        "stoppedTreatment", mapStraightThrough(hivCohortQueries.getPatientsWhoStoppedTreatment()));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setCompositionString("ltfu and not (stoppedTreatment or transferOut or dead) ");

    return cd;
  }
}
