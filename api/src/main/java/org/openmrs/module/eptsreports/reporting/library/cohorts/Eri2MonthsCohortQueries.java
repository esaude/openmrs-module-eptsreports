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

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Autowired private TxPvlsCohortQueries txPvlsCohortQueries;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  /**
   * Get patients who have 2 months ART retention after ART initiation A
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAllPatientsRetainedOnArtFor2MonthsFromArtInitiation() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsRetentionFor2MonthsOnART");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location" + "", "Location", Location.class));
    cd.setQuery(
        Eri2MonthsQueries.allPatientsWhoInitiatedTreatmentDuringReportingPeriod(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getstartDrugsConcept().getConceptId(),
            hivMetadata.gethistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId()));
    return cd;
  }

  /**
   * B
   *
   * @return
   */
  public CohortDefinition getTransferInPatients() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Transfer Ins");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location" + "", "Location", Location.class));
    cd.setQuery(
        Eri2MonthsQueries.getTransferInPatients(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));
    return cd;
  }

  /**
   * C
   *
   * @return
   */
  public CohortDefinition
      getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients who picked up drugs in 33 days");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        Eri2MonthsQueries.getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getstartDrugsConcept().getConceptId(),
            hivMetadata.gethistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId()));
    return cd;
  }

  /**
   * Get all patients who initiated ART 2 months from ART initiation less transfer ins return the
   * patient who initiated ART A and B
   *
   * @retrun CohortDefinition
   */
  public CohortDefinition getAllPatientsWhoInitiatedArt() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("All patients who initiated ART less transfer ins");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "all",
        EptsReportUtils.map(
            getAllPatientsRetainedOnArtFor2MonthsFromArtInitiation(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "transferIns",
        EptsReportUtils.map(getTransferInPatients(), "endDate=${endDate},location=${location}"));
    cd.setCompositionString("all AND NOT transferIns");
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
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            getAllPatientsWhoInitiatedArt(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "pickedDrugs",
        EptsReportUtils.map(
            getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days(),
            "endDate=${endDate},location=${location}"));
    cd.setCompositionString("initiatedArt AND pickedDrugs");
    return cd;
  }

  /**
   * Get pregnant women who have more than 2 months retention on ART
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantWomenRetainedOnArtFor2MonthsFromArtInitiation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Pregnant women retain on ART for more than 2 months from ART initiation date");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location" + "", "Location", Location.class));
    cd.addSearch(
        "all",
        EptsReportUtils.map(
            getAllPatientsWhoInitiatedArt(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-2m},endDate=${endDate-1m},location=${location}"));
    cd.setCompositionString("all AND pregnant");
    return cd;
  }

  /**
   * Get breastfeeding women who have more than 2 months ART retention
   *
   * @return CohortDefinition
   */
  public CohortDefinition getBreastfeedingWomenRetainedOnArtFor2MonthsFromArtInitiation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding women retain on ART for more than 2 months from ART initiation date");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location" + "", "Location", Location.class));
    cd.addSearch(
        "all",
        EptsReportUtils.map(
            getAllPatientsWhoInitiatedArt(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${endDate-2m},onOrBefore=${endDate-1m},location=${location}"));
    cd.setCompositionString("all AND breastfeeding");
    return cd;
  }

  /**
   * Get Children (0-14, excluding pregnant and breastfeeding women)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getChildrenRetainedOnArtFor2MonthsFromArtInitiation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Children having ART retention for than 2 months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location" + "", "Location", Location.class));
    cd.addSearch(
        "initiatedART",
        EptsReportUtils.map(
            getAllPatientsWhoInitiatedArt(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "children",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(0, 14),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-2m},endDate=${endDate-1m},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${endDate-2m},onOrBefore=${endDate-1m},location=${location}"));
    cd.setCompositionString(
        "(initiatedART AND children) AND NOT ((initiatedART AND pregnant) OR (initiatedART AND breastfeeding))");
    return cd;
  }

  /**
   * Get Adults (14+, excluding pregnant and breastfeeding women)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAdultsRetaineOnArtFor2MonthsFromArtInitiation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Adults having ART retention for than 2 months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "all",
        EptsReportUtils.map(
            getAllPatientsWhoInitiatedArt(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "adults",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(15, 200),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-2m},endDate=${endDate-1m},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${endDate-2m},onOrBefore=${endDate-1m},location=${location}"));
    cd.setCompositionString(
        "(initiatedART AND children) AND NOT ((initiatedART AND pregnant) OR (initiatedART AND breastfeeding))");
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
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            getAllPatientsWhoInitiatedArt(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "pickedDrugs",
        EptsReportUtils.map(
            getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId()),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "transfers",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "endDate=${endDate},location=${location}"));
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
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "pickedDrugsAndStartedART",
        EptsReportUtils.map(
            getAllPatientsWhoStartedArtAndPickedDrugsOnTheirNextVisit(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId()),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "transfers",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "endDate=${endDate},location=${location}"));
    cd.setCompositionString("pickedDrugsAndStartedART AND NOT (dead OR transfers)");
    return cd;
  }

  /**
   * Get all patients who initiated ART(A), less transfer ins(B) intersected with those who picked
   * up drugs in 33 days
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoInitiatedArtAndPickedDrugsButSuspendedTreatment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who suspended treatment");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArtAndPickedDrugs",
        EptsReportUtils.map(
            getAllPatientsWhoStartedArtAndPickedDrugsOnTheirNextVisit(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "suspendedTreatment",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId()),
            "endDate=${endDate},location=${location}"));
    return cd;
  }
}
