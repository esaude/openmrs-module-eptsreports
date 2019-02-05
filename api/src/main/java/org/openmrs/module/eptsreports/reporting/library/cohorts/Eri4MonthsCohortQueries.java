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

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Autowired private TxPvlsCohortQueries txPvlsCohortQueries;

  @Autowired private GenericCohortQueries genericCohortQueries;

  /**
   * Get all patients who have 4 months ART retention after initiation
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsRetainedOnArtFor4MonthsFromArtInitiation() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsRetentionFor4MonthsOnART");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location" + "", "Location", Location.class));
    cd.setQuery(
        Eri4MonthsQueries.getPatientsRetainedOnArt4MonthsAfterArtInitiation(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getstartDrugsConcept().getConceptId(),
            hivMetadata.gethistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));
    return cd;
  }

  /**
   * Get pregnant women who have more than 4 months retention on ART
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantWomenRetainedOnArtFor4MonthsFromArtInitiation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Pregnant women retain on ART for more than 4 months from ART initiation date");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location" + "", "Location", Location.class));
    cd.addSearch(
        "all",
        EptsReportUtils.map(
            getPatientsRetainedOnArtFor4MonthsFromArtInitiation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-4m},endDate=${endDate-3m},location=${location}"));
    cd.setCompositionString("all AND pregnant");
    return cd;
  }

  /**
   * Get breastfeeding women who have more than 4 months ART retention
   *
   * @return CohortDefinition
   */
  public CohortDefinition getBreastfeedingWomenRetainedOnArtFor4MonthsFromArtInitiation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding women retain on ART for more than 4 months from ART initiation date");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location" + "", "Location", Location.class));
    cd.addSearch(
        "all",
        EptsReportUtils.map(
            getPatientsRetainedOnArtFor4MonthsFromArtInitiation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${endDate-4m},onOrBefore=${endDate-3m},location=${location}"));
    cd.setCompositionString("all AND breastfeeding");
    return cd;
  }

  /**
   * Get Children (0-14, excluding pregnant and breastfeeding women)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getChildrenRetainedOnArtFor4MonthsFromArtInitiation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Children having ART retention for than 3 months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location" + "", "Location", Location.class));
    cd.addSearch(
        "all",
        EptsReportUtils.map(
            getPatientsRetainedOnArtFor4MonthsFromArtInitiation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "children",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(0, 14),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            getPregnantWomenRetainedOnArtFor4MonthsFromArtInitiation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getBreastfeedingWomenRetainedOnArtFor4MonthsFromArtInitiation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("(all AND children) AND NOT(pregnant OR breastfeeding)");
    return cd;
  }

  /**
   * Get Adults (14+, excluding pregnant and breastfeeding women)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAdultsRetaineOnArtFor4MonthsFromArtInitiation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Adults having ART retention for than 3 months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "all",
        EptsReportUtils.map(
            getPatientsRetainedOnArtFor4MonthsFromArtInitiation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "adults",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(15, 200),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            getPregnantWomenRetainedOnArtFor4MonthsFromArtInitiation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getBreastfeedingWomenRetainedOnArtFor4MonthsFromArtInitiation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("(all AND adults) AND NOT(pregnant OR breastfeeding)");
    return cd;
  }

  /**
   * Get patients who are alive and on treatment - probably all those who have been on ART for more
   * than 3 months excluding the dead, transfers or suspended
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreAliveAndOnTreatment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who are a live and on treatment");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "initiatedArt",
        EptsReportUtils.map(
            getPatientsRetainedOnArtFor4MonthsFromArtInitiation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId()),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "transfersOut",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "suspended",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId()),
            "endDate=${endDate},location=${location}"));
    cd.setCompositionString("initiatedArt AND NOT (dead OR transfersOut OR suspended)");
    return cd;
  }
}
