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
