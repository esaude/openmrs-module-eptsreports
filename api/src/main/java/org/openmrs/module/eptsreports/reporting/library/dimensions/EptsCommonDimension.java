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
import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri2MonthsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri4MonthsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.EriCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TbPrevCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxTbPrevCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.BreastfeedingQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.DsdQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.Eri2MonthsQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.TxCurrQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.Gender;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EptsCommonDimension {

  @Autowired private GenderCohortQueries genderCohortQueries;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private Eri4MonthsCohortQueries eri4MonthsCohortQueries;

  @Autowired private Eri2MonthsCohortQueries eri2MonthsCohortQueries;

  @Autowired private EriCohortQueries eriCohortQueries;

  @Autowired private TbPrevCohortQueries tbPrevCohortQueries;

  @Autowired private TxTbPrevCohortQueries txTbPrevCohortQueries;

  @Autowired private GenericCohortQueries genericCohorts;

  /**
   * Gender dimension
   *
   * @return the {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
   */
  public CohortDefinitionDimension gender() {
    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("gender");
    dim.addCohortDefinition("M", EptsReportUtils.map(this.genderCohortQueries.maleCohort(), ""));
    dim.addCohortDefinition("F", EptsReportUtils.map(this.genderCohortQueries.femaleCohort(), ""));
    return dim;
  }

  /**
   * Age range dimension 10-14, 15-19, 20-24, 25-29, 30-34, 35-39, 40-44, 45-49, >=50
   *
   * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
   */
  public CohortDefinitionDimension age(final AgeDimensionCohortInterface ageDimensionCohort) {
    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setParameters(ageDimensionCohort.getParameters());
    dim.setName("age dimension");

    dim.addCohortDefinition("UK", ageDimensionCohort.createUnknownAgeCohort());
    dim.addCohortDefinition(
        "<1", ageDimensionCohort.createXtoYAgeCohort("patients with age bellow 1", 0, 0));
    dim.addCohortDefinition(
        "0-4",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 0 and 4 years", 0, 4));
    dim.addCohortDefinition(
        "0-14",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 0 and 14 years", 0, 14));
    dim.addCohortDefinition(
        "0-15",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 0 and 15 years", 0, 15));
    dim.addCohortDefinition(
        "1-4", ageDimensionCohort.createXtoYAgeCohort("patients with age between 1 and 4", 1, 4));
    dim.addCohortDefinition(
        "2-4", ageDimensionCohort.createXtoYAgeCohort("patients with age between 2 and 4", 2, 4));
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
        "10-19",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 10 and 19", 10, 19));

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

    dim.addCohortDefinition(
        "20+", ageDimensionCohort.createXtoYAgeCohort("patients with age over 20 years", 20, null));

    return dim;
  }

  /** @return CohortDefinitionDimension */
  public CohortDefinitionDimension maternityDimension() {
    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Maternity Dimension");

    dim.addCohortDefinition(
        "breastfeeding",
        EptsReportUtils.map(
            this.txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "pregnant",
        EptsReportUtils.map(
            this.txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    return dim;
  }

  /**
   * Get the dimensions based on the patient states for ERI-4 months
   *
   * @return CohortDefinitionDimension
   */
  public CohortDefinitionDimension getEri4MonthsDimension() {
    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    dim.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    dim.addParameter(new Parameter("reportingStartDate", "Report Start Date", Date.class));
    dim.addParameter(new Parameter("reportingEndDate", "Report End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Get patient states");

    dim.addCohortDefinition(
        "IART",
        EptsReportUtils.map(
            this.eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));

    dim.addCohortDefinition(
        "AIT",
        EptsReportUtils.map(
            this.eri4MonthsCohortQueries.getPatientsWhoAreAliveAndOnTreatment(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "DP",
        EptsReportUtils.map(
            this.genericCohortQueries.getDeceasedPatients(),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "LTFU",
        EptsReportUtils.map(
            this.eri4MonthsCohortQueries.getAllPatientsWhoAreLostToFollowUpDuringPeriod(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "TOP",
        EptsReportUtils.map(
            this.genericCohortQueries.getPatientsBasedOnPatientStates(
                this.hivMetadata.getARTProgram().getProgramId(),
                this.hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "STP",
        EptsReportUtils.map(
            this.genericCohortQueries.getPatientsBasedOnPatientStates(
                this.hivMetadata.getARTProgram().getProgramId(),
                this.hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId()),
            "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "ANIT",
        EptsReportUtils.map(
            this.eri4MonthsCohortQueries.getPatientsWhoAreAliveAndNotOnTreatment(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    return dim;
  }

  /**
   * Get the dimensions based on the patient states for ERI-4 months
   *
   * @return CohortDefinitionDimension
   */
  public CohortDefinitionDimension getEri2MonthsDimension() {
    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    dim.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    dim.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Get patients dimensions for Eri2Months");

    dim.addCohortDefinition(
        "IART",
        EptsReportUtils.map(
            this.eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));

    dim.addCohortDefinition(
        "DNPUD",
        EptsReportUtils.map(
            this.eri2MonthsCohortQueries.getPatientsWhoDidNotPickDrugsOnTheirSecondVisit(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "PUD",
        EptsReportUtils.map(
            this.eri2MonthsCohortQueries.getPatientsWhoPickedUpDrugsOnTheirSecondVisit(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "DP",
        EptsReportUtils.map(
            this.eri2MonthsCohortQueries.getPatientsWhoInitiatedArtAndDead(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "TOP",
        EptsReportUtils.map(
            this.eri2MonthsCohortQueries.getPatientsWhoInitiatedArtButTransferredOut(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "STP",
        EptsReportUtils.map(
            this.eri2MonthsCohortQueries.getPatientsWhoInitiatedArtButSuspendedTreatment(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    return dim;
  }

  public CohortDefinitionDimension getEri2MonthsDimension2() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Get patients dimensions for Eri2Months");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "IART",
        EptsReportUtils.map(
            this.txNewCohortQueries.getTxNewCompositionCohort("Eri2Months"),
            "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}"));

    dimension.addCohortDefinition(
        "DNPUD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedArtAtAPeriodAndDidNotHaveASecondPickupsOrClinicalConsultationWithin33DaysAfterArtInitiation",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoStartedArtAtAPeriodAndDidNotHaveASecondPickupsOrClinicalConsultationWithin33DaysAfterArtInitiation),
            mappings));

    dimension.addCohortDefinition(
        "PUD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsStartedArtLastMonthWith2PickupsOrConsultationWithin33DaysExcludingDeadAndTransferedOutAndIn",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsStartedArtLastMonthWith2PickupsOrConsultationWithin33DaysExcludingDeadAndTransferedOutAndIn),
            mappings));

    dimension.addCohortDefinition(
        "DP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedArtInAPeriodAndAreDeath33DaysAfterArtInitiation",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoStartedArtInAPeriodAndAreDeath33DaysAfterArtInitiation),
            mappings));

    dimension.addCohortDefinition(
        "TOP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedArtInAPeriodAndAreTrasferedOut33DaysAfterInitiation",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoStartedArtInAPeriodAndAreTrasferedOut33DaysAfterInitiation),
            mappings));

    dimension.addCohortDefinition(
        "STP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedArtInAPeriodAndSuspendTratement33DaysAfterInitiation",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoStartedArtInAPeriodAndSuspendTratement33DaysAfterInitiation),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension getTbPrevArtStatusDimension() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Get patients dimensions for Eri2Months");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));
    dimension.setName("ART-status Dimension");
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "previously-on-art",
        EptsReportUtils.map(
            txTbPrevCohortQueries.findPatientsWhoStartedArtAndTpiPreviouslyDessagragation(),
            mappings));

    dimension.addCohortDefinition(
        "new-on-art",
        EptsReportUtils.map(
            txTbPrevCohortQueries.findPatientsWhoStartedArtAndTpiNewDessagragation(), mappings));

    return dimension;
  }

  public CohortDefinitionDimension getArtStatusDimension() {
    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    dim.addParameter(new Parameter("onOrBefore", "orOrBefore", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    dim.setName("ART-status Dimension");
    dim.addCohortDefinition(
        "new-on-art",
        EptsReportUtils.map(
            this.tbPrevCohortQueries.getNewOnArt(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    dim.addCohortDefinition(
        "previously-on-art",
        EptsReportUtils.map(
            this.tbPrevCohortQueries.getPreviouslyOnArt(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    return dim;
  }

  public CohortDefinitionDimension findPatientsWhoAreBreastfeeding() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Breastfeedig Dimension");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "breastfeeding",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreBreastfeeding",
                BreastfeedingQueries.findPatientsWhoAreBreastfeeding()),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreNewlyEnrolledOnArtByAgeAndGender(
      final String name, final AgeRange ageRange, final String gender) {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName(name);
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    String query = TxNewQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnArtByAgeAndGender;
    query = String.format(query, ageRange.getMin(), ageRange.getMax(), gender);

    switch (ageRange) {
      case UNDER_ONE:
        query = query.replace("BETWEEN " + ageRange.getMin() + " AND " + ageRange.getMax(), " < 1");
        break;

      case ABOVE_FIFTY:
        query =
            query.replace("BETWEEN " + ageRange.getMin() + " AND " + ageRange.getMax(), " >= 50");

      default:
        break;
    }

    dimension.addCohortDefinition(
        name,
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreNewlyEnrolledOnArtByAgeAndGender", query),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWithUnknownAgeByGender(
      final String name, final Gender gender) {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    dimension.setName(name);

    String query = TxNewQueries.QUERY.findPatientsWithUnknownAgeByGender;
    query = String.format(query, gender.getName());

    dimension.addCohortDefinition(
        name,
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql("patientsWithUnknownAgeByGender", query), ""));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsByGenderAndRange(
      final String name, final AgeRange range, final Gender gender) {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName(name);
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    String query = TxCurrQueries.QUERY.findPatientsByGenderAndRage;
    query = String.format(query, range.getMin(), range.getMax(), gender.getName());

    switch (range) {
      case UNDER_ONE:
        query = query.replace("BETWEEN " + range.getMin() + " AND " + range.getMax(), " < 1");
        break;

      case ABOVE_FIFTY:
        query = query.replace("BETWEEN " + range.getMin() + " AND " + range.getMax(), " >= 50");

      default:
        break;
    }

    dimension.addCohortDefinition(
        name,
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql("findPatientsByGenderAndRage", query),
            "endDate=${endDate}"));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsByRange(final String name, final AgeRange range) {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName(name);
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));

    String query = DsdQueriesInterface.QUERY.findPatientsAgeRange;
    query = String.format(query, range.getMin(), range.getMax());

    switch (range) {
      case UNDER_TWO:
        query =
            query.replace(
                "BETWEEN " + range.getMin() + " AND " + range.getMax(), " < " + range.getMax());
        break;

      case ADULT:
        query =
            query.replace(
                "BETWEEN " + range.getMin() + " AND " + range.getMax(), " >= " + range.getMax());

      default:
        break;
    }

    dimension.addCohortDefinition(
        name,
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql("findPatientsByRange", query),
            "endDate=${endDate}"));

    return dimension;
  }
}
