/** */
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.BreastFeedingCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.PregnantQueries;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class Eri4MonthsDimensions {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private BreastFeedingCohortQueries breastFeedingCohortQueries;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  public CohortDefinitionDimension getDimension() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    dimension.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));
    dimension.setName("Get patient states");

    dimension.addCohortDefinition(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoArePregnantInAPeriod",
                PregnantQueries.findPatientsWhoArePregnantInAPeriod()),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));

    dimension.addCohortDefinition(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.breastFeedingCohortQueries
                .findPatientsWhoAreBreastFeedingExcludingPregnantsInAPeriod(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));

    dimension.addCohortDefinition(
        "CHILDREN",
        EptsReportUtils.map(
            this.txNewCohortQueries
                .findPatientsNewlyEnrolledByAgeInAPeriodExcludingBreastFeedingAndPregnant(
                    AgeRange.CHILDREN),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));

    dimension.addCohortDefinition(
        "ADULT",
        EptsReportUtils.map(
            this.txNewCohortQueries
                .findPatientsNewlyEnrolledByAgeInAPeriodExcludingBreastFeedingAndPregnant(
                    AgeRange.ADULT),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));

    return dimension;
  }
}
