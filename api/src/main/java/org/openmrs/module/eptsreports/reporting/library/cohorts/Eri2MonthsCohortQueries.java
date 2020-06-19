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
import org.openmrs.module.eptsreports.reporting.library.queries.BreastfeedingQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.Eri2MonthsQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.Eri2MonthsQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.ErimType;
import org.openmrs.module.eptsreports.reporting.library.queries.PregnantQueries;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
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

  @Autowired private GenericCohortQueries genericCohorts;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  /**
   * C
   *
   * @return
   */
  public CohortDefinition
      getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days() {
    final SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients who picked up drugs in 33 days");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        Eri2MonthsQueries.getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days(
            this.hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            this.hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            this.hivMetadata.getARVPlanConcept().getConceptId(),
            this.hivMetadata.getStartDrugsConcept().getConceptId(),
            this.hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            this.hivMetadata.getARTProgram().getProgramId()));
    return cd;
  }

  /**
   * A and B and C
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAllPatientsWhoStartedArtAndPickedDrugsOnTheirNextVisit() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who  picked up drugs during their second visit and had initiated ART");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            this.eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${startDate},cohortEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "pickedDrugs",
        EptsReportUtils.map(
            this.getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("initiatedArt AND pickedDrugs");
    return cd;
  }

  /**
   * Get patients who did not pick up drugs on their second visit Did NOT pick up the drugs A and B
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoDidNotPickDrugsOnTheirSecondVisit() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who did not pick up drugs during their second visit");
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
        "pickedDrugs",
        EptsReportUtils.map(
            this.getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days(),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            this.genericCohortQueries.getDeceasedPatients(),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "transfers",
        EptsReportUtils.map(
            this.genericCohortQueries.getPatientsBasedOnPatientStates(
                this.hivMetadata.getARTProgram().getProgramId(),
                this.hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.setCompositionString("initiatedArt AND NOT (pickedDrugs OR dead OR transfers)");
    return cd;
  }

  public CohortDefinition
      getPatientsStartedArtLastMonthWith2PickupsOrConsultationWithin33DaysExcludingDeadAndTransferedOutAndIn() {
    return null;
  }

  /**
   * Get patients who picked up drugs on their second visit A and B and C and NOt (dead and
   * tarnsfers)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoPickedUpDrugsOnTheirSecondVisit() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who  picked up drugs during their second visit");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "pickedDrugsAndStartedART",
        EptsReportUtils.map(
            this.getAllPatientsWhoStartedArtAndPickedDrugsOnTheirNextVisit(),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            this.genericCohortQueries.getDeceasedPatients(),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.addSearch(
        "transfers",
        EptsReportUtils.map(
            this.genericCohortQueries.getPatientsBasedOnPatientStates(
                this.hivMetadata.getARTProgram().getProgramId(),
                this.hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
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
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who died during period");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArtAndNotTransferIns",
        EptsReportUtils.map(
            this.eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            this.genericCohortQueries.getDeceasedPatients(),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
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
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who suspended treatment");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArtAndNotTransferIns",
        EptsReportUtils.map(
            this.eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "suspendedTreatment",
        EptsReportUtils.map(
            this.genericCohortQueries.getPatientsBasedOnPatientStates(
                this.hivMetadata.getARTProgram().getProgramId(),
                this.hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId()),
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
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who transferred out during period");
    cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "initiatedArtAndNotTransferIns",
        EptsReportUtils.map(
            this.eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
    cd.addSearch(
        "transferredOut",
        EptsReportUtils.map(
            this.genericCohortQueries.getPatientsBasedOnPatientStates(
                this.hivMetadata.getARTProgram().getProgramId(),
                this.hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
    cd.setCompositionString("initiatedArtAndNotTransferIns AND transferredOut");
    return cd;
  }

  public CohortDefinition getEri2MonthsPregnatCompositionCohort(final String cohortName) {
    final CompositionCohortDefinition eri2MonthsCompositionCohort =
        new CompositionCohortDefinition();

    eri2MonthsCompositionCohort.setName(cohortName);
    eri2MonthsCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    eri2MonthsCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    eri2MonthsCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    final String cohortMappings =
        "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}";

    eri2MonthsCompositionCohort.addSearch(
        "START-ART-2-MONTHS",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "All patients",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickup33DaysForASpecificPatientType(
                        ErimType.TOTAL)),
            mappings));

    eri2MonthsCompositionCohort.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsThatAreFemaleAndWereMarkedAsPregnantInTheInitialConsultationOrFollowUpConsultationAndMasterCard",
                PregnantQueries.findPatientsWhoArePregnantInAPeriod()),
            cohortMappings));

    eri2MonthsCompositionCohort.setCompositionString("START-ART-2-MONTHS AND PREGNANT");

    return eri2MonthsCompositionCohort;
  }

  public CohortDefinition getEri2MonthsBrestfeetingCompositionCohort(final String cohortName) {
    final CompositionCohortDefinition eri2MonthsCompositionCohort =
        new CompositionCohortDefinition();

    eri2MonthsCompositionCohort.setName(cohortName);
    eri2MonthsCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    eri2MonthsCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    eri2MonthsCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final String cohortMappings =
        "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}";

    eri2MonthsCompositionCohort.addSearch(
        "START-ART-2-MONTHS",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "All patients",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickup33DaysForASpecificPatientType(
                        ErimType.TOTAL)),
            mappings));

    eri2MonthsCompositionCohort.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreBreasfeedingInAPeriod",
                BreastfeedingQueries.findPatientsWhoAreBreastfeeding()),
            cohortMappings));

    eri2MonthsCompositionCohort.setCompositionString("START-ART-2-MONTHS AND BREASTFEEDING");

    return eri2MonthsCompositionCohort;
  }

  public CohortDefinition getEri2MonthsAdultCompositionCohort(final String cohortName) {
    final CompositionCohortDefinition eri2MonthsCompositionCohort =
        new CompositionCohortDefinition();

    eri2MonthsCompositionCohort.setName(cohortName);
    eri2MonthsCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    eri2MonthsCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    eri2MonthsCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final String cohortMappings =
        "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}";

    eri2MonthsCompositionCohort.addSearch(
        "START-ART-2-MONTHS",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "All patients",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickup33DaysForASpecificPatientType(
                        ErimType.TOTAL)),
            mappings));

    eri2MonthsCompositionCohort.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreBreasfeedingInAPeriod",
                BreastfeedingQueries.findPatientsWhoAreBreastfeeding()),
            cohortMappings));

    eri2MonthsCompositionCohort.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoArePregnantInAPeriod",
                PregnantQueries.findPatientsWhoArePregnantInAPeriod()),
            cohortMappings));

    eri2MonthsCompositionCohort.addSearch(
        "ADULT",
        EptsReportUtils.map(
            this.txNewCohortQueries.findPatientsWhoAreNewlyEnrolledOnArtByAgeRange(AgeRange.ADULT),
            cohortMappings));

    eri2MonthsCompositionCohort.setCompositionString(
        "(START-ART-2-MONTHS AND ADULT) NOT (PREGNANT OR BREASTFEEDING)");

    return eri2MonthsCompositionCohort;
  }

  public CohortDefinition getEri2MonthsChildCompositionCohort(final String cohortName) {
    final CompositionCohortDefinition eri2MonthsCompositionCohort =
        new CompositionCohortDefinition();

    eri2MonthsCompositionCohort.setName(cohortName);
    eri2MonthsCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    eri2MonthsCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    eri2MonthsCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final String cohortMappings =
        "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}";

    eri2MonthsCompositionCohort.addSearch(
        "START-ART-2-MONTHS",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "All patients",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickup33DaysForASpecificPatientType(
                        ErimType.TOTAL)),
            mappings));

    eri2MonthsCompositionCohort.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreBreasfeedingInAPeriod",
                BreastfeedingQueries.findPatientsWhoAreBreastfeeding()),
            cohortMappings));

    eri2MonthsCompositionCohort.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoArePregnantInAPeriod",
                PregnantQueries.findPatientsWhoArePregnantInAPeriod()),
            cohortMappings));

    eri2MonthsCompositionCohort.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            this.txNewCohortQueries.findPatientsWhoAreNewlyEnrolledOnArtByAgeRange(
                AgeRange.CHILDREN),
            cohortMappings));

    eri2MonthsCompositionCohort.setCompositionString(
        "(START-ART-2-MONTHS AND CHILDREN) NOT (PREGNANT OR BREASTFEEDING)");

    return eri2MonthsCompositionCohort;
  }
}
