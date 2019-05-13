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
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri2MonthsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri4MonthsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.EriCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TbPrevCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EptsCommonDimension {

  @Autowired private AgeCohortQueries ageCohortQueries;

  @Autowired private GenderCohortQueries genderCohortQueries;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private Eri4MonthsCohortQueries eri4MonthsCohortQueries;

  @Autowired private Eri2MonthsCohortQueries eri2MonthsCohortQueries;

  @Autowired private EriCohortQueries eriCohortQueries;

  @Autowired private TbPrevCohortQueries tbPrevCohortQueries;

  /**
   * Gender dimension
   *
   * @return the {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
   */
  public CohortDefinitionDimension gender() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("gender");
    dim.addCohortDefinition("M", EptsReportUtils.map(genderCohortQueries.maleCohort(), ""));
    dim.addCohortDefinition("F", EptsReportUtils.map(genderCohortQueries.femaleCohort(), ""));
    return dim;
  }

  /**
   * Age range dimension 10-14, 15-19, 20-24, 25-29, 30-34, 35-39, 40-44, 45-49, >=50
   *
   * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
   */
  public CohortDefinitionDimension age(AgeDimensionCohortInterface ageDimensionCohort) {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setParameters(ageDimensionCohort.getParameters());
    dim.setName("age dimension");

    dim.addCohortDefinition("UK", ageDimensionCohort.createUnknownAgeCohort());
    dim.addCohortDefinition(
        "<1", ageDimensionCohort.createXtoYAgeCohort("patients with age bellow 1", 0, 0));
    dim.addCohortDefinition(
        "1-4", ageDimensionCohort.createXtoYAgeCohort("patients with age between 1 and 4", 1, 4));
    dim.addCohortDefinition(
        "5-9", ageDimensionCohort.createXtoYAgeCohort("patients with age between 5 and 9", 5, 9));
    dim.addCohortDefinition(
        "<15", ageDimensionCohort.createXtoYAgeCohort("patients with age below 15", null, 14));
    dim.addCohortDefinition(
        "10-14",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 10 and 14", 10, 14));
    dim.addCohortDefinition(
        "15+", ageDimensionCohort.createXtoYAgeCohort("patients with age over 15", 15, null));
    dim.addCohortDefinition(
        "15-19",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 15 and 19", 15, 19));
    dim.addCohortDefinition(
        "20-24",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 20 and 24", 20, 24));
    dim.addCohortDefinition(
        "25-29",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 25 and 29", 25, 29));
    dim.addCohortDefinition(
        "30-34",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 30 and 34", 30, 34));
    dim.addCohortDefinition(
        "35-39",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 35 and 39", 35, 39));
    dim.addCohortDefinition(
        "40-44",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 40 and 44", 40, 44));
    dim.addCohortDefinition(
        "45-49",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 45 and 49", 45, 49));
    dim.addCohortDefinition(
        "50+", ageDimensionCohort.createXtoYAgeCohort("patients with age over 50", 50, null));

    return dim;
  }

  /** @return CohortDefinitionDimension */
  public CohortDefinitionDimension maternityDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Maternity Dimension");

    dim.addCohortDefinition(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    return dim;
  }

  /**
   * Get the dimensions based on the patient states for ERI-4 months
   *
   * @return CohortDefinitionDimension
   */
  public CohortDefinitionDimension getEri4MonthsDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    dim.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    dim.addParameter(new Parameter("reportingStartDate", "Report Start Date", Date.class));
    dim.addParameter(new Parameter("reportingEndDate", "Report End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Get patient states");

    dim.addCohortDefinition(
        "IART",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));

    dim.addCohortDefinition(
        "AIT",
        EptsReportUtils.map(
            eri4MonthsCohortQueries.getPatientsWhoAreAliveAndOnTreatment(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "DP",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatients(),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "LTFU",
        EptsReportUtils.map(
            eri4MonthsCohortQueries.getAllPatientsWhoAreLostToFollowUpDuringPeriod(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "TOP",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "STP",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "ANIT",
        EptsReportUtils.map(
            eri4MonthsCohortQueries.getPatientsWhoAreAliveAndNotOnTreatment(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    return dim;
  }

  /**
   * Get the dimensions based on the patient states for ERI-4 months
   *
   * @return CohortDefinitionDimension
   */
  public CohortDefinitionDimension getEri2MonthsDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    dim.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    dim.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Get patients dimensions for Eri2Months");
    dim.addCohortDefinition(
        "IART",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
    dim.addCohortDefinition(
        "DNPUD",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoDidNotPickDrugsOnTheirSecondVisit(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    dim.addCohortDefinition(
        "PUD",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoPickedUpDrugsOnTheirSecondVisit(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    dim.addCohortDefinition(
        "DP",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoInitiatedArtAndDead(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    dim.addCohortDefinition(
        "TOP",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoInitiatedArtButTransferredOut(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    dim.addCohortDefinition(
        "STP",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoInitiatedArtButSuspendedTreatment(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    return dim;
  }

  public CohortDefinitionDimension getArtStatusDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    dim.addParameter(new Parameter("onOrBefore", "orOrBefore", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    dim.setName("ART-status Dimension");
    dim.addCohortDefinition(
        "new-on-art",
        EptsReportUtils.map(
            tbPrevCohortQueries.getNewOnArt(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    dim.addCohortDefinition(
        "previously-on-art",
        EptsReportUtils.map(
            tbPrevCohortQueries.getPreviouslyOnArt(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    return dim;
  }

  public CohortDefinitionDimension getUsMonthlySummaryHivAges() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("effectiveDate", "age effective date", Date.class));
    dim.setName("US monthly summary - HIV age dimension");

    dim.addCohortDefinition(
        "0-14",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age between 0 and 14 years", 0, 14),
            "effectiveDate=${effectiveDate}"));

    dim.addCohortDefinition(
        "15+",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age over 15 years", 15, null),
            "effectiveDate=${effectiveDate}"));

    return dim;
  }
}
