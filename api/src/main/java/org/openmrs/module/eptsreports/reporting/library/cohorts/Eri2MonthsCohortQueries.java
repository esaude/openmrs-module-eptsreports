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
import org.openmrs.module.eptsreports.reporting.library.queries.Eri2MonthsQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Eri2MonthsCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private EriCohortQueries eriCohortQueries;

  @Autowired private HivCohortQueries hivCohortQueries;

  /**
   * get all patients who returned for 2nd consultation or 2nd drugs pickUp within 33 days
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients who picked up drugs in 33 days");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        Eri2MonthsQueries.getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days(
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
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId()));
    return cd;
  }

  /**
   * A and B and C
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAllPatientsWhoStartedArtAndPickedDrugsOnTheirNextVisit() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who  picked up drugs during their second visit and had initiated ART");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${startDate},cohortEndDate=${endDate},reportingEndDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "pickedDrugs",
        EptsReportUtils.map(
            getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days(),
            "startDate=${startDate},endDate=${endDate},reportingEndDate=${reportingEndDate},location=${location}"));
    cd.setCompositionString("initiatedArt AND pickedDrugs");
    return cd;
  }

  /**
   * Get patients who did not pick up drugs on their second visit Did NOT pick up the drugs A and B
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoDidNotPickDrugsOnTheirSecondVisit() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who did not pick up drugs during their second visit");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "pickedDrugs",
        EptsReportUtils.map(
            getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days(),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatients(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "transfers",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsTransferredOut(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    cd.setCompositionString("initiatedArt AND NOT (pickedDrugs OR dead OR transfers)");
    return cd;
  }

  /**
   * Get patients who picked up drugs on their second visit A and B and C and NOt (dead and
   * tarnsfers)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoPickedUpDrugsOnTheirSecondVisit() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who  picked up drugs during their second visit");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "pickedDrugsAndStartedART",
        EptsReportUtils.map(
            getAllPatientsWhoStartedArtAndPickedDrugsOnTheirNextVisit(),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatients(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "transfers",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsTransferredOut(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    cd.setCompositionString("pickedDrugsAndStartedART AND NOT (dead OR transfers)");
    return cd;
  }

  /**
   * Get all patients who initiated ART(A), less transfer ins(B) intersected with those who picked
   * up drugs in 33 days AND who died within the reporting period
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoInitiatedArtAndDead() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who died during period");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArtAndNotTransferIns",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatients(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    cd.setCompositionString("initiatedArtAndNotTransferIns AND dead");
    return cd;
  }

  /**
   * Get all patients who initiated ART(A), less transfer ins(B) intersected with those who picked
   * up drugs in 33 days AND those who suspended treatment within the reporting period
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoInitiatedArtButSuspendedTreatment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who suspended treatment");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArtAndNotTransferIns",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "suspendedTreatment",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.setCompositionString("initiatedArtAndNotTransferIns AND suspendedTreatment");
    return cd;
  }

  /**
   * Get all patients who initiated ART(A), less transfer ins(B) intersected with those who picked
   * up drugs in 33 days AND those who suspended treatment within the reporting period
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoInitiatedArtButTransferredOut() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who transferred out during period");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArtAndNotTransferIns",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "transferredOut",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsTransferredOut(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    cd.setCompositionString("initiatedArtAndNotTransferIns AND transferredOut");
    return cd;
  }
}
