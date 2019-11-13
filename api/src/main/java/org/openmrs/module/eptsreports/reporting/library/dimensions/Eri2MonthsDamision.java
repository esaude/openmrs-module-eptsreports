package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri2MonthsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.Eri2MonthsQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Eri2MonthsDamision {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private Eri2MonthsCohortQueries eri2MonthsCohortQueries;

  public CohortDefinitionDimension findPatientsWhoStartArtOneMonth() {

    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dim.addCohortDefinition(
        "1All",
        EptsReportUtils.map(
            this.eri2MonthsCohortQueries.getEri2MonthsCompositionCohort("1All"), mappings));

    return dim;
  }

  public CohortDefinitionDimension
      findPatientsStartedArtLastMonthWith2PickupsOrConsultationWithin33DaysExcludingDeadAndTransferedOutAndIn() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName(
        "PatientsStartedArtLastMonthWith2PickupsOrConsultationWithin33DaysExcludingDeadAndTransferedOutAndIn");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "ERI2.R21-03",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsStartedArtLastMonthWith2PickupsOrConsultationWithin33DaysExcludingDeadAndTransferedOutAndIn",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsStartedArtLastMonthWith2PickupsOrConsultationWithin33DaysExcludingDeadAndTransferedOutAndIn),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension
      findPatientsWhoStartedArtAtAPeriodAndDidNotHaveASecondPickupsOrClinicalConsultationWithin33DaysAfterArtInitiation() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName(
        "findPatientsWhoStartedArtAtAPeriodAndDidNotHaveASecondPickupsOrClinicalConsultationWithin33DaysAfterArtInitiation");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "ERI2.R21-02",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoStartedArtAtAPeriodAndDidNotHaveASecondPickupsOrClinicalConsultationWithin33DaysAfterArtInitiation",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoStartedArtAtAPeriodAndDidNotHaveASecondPickupsOrClinicalConsultationWithin33DaysAfterArtInitiation),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension
      findPatientsWhoStartedArtInAPeriodAndAreDeath33DaysAfterArtInitiation() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("findPatientsWhoStartedArtInAPeriodAndAreDeath33DaysAfterArtInitiation");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "ERI2.R21-04",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoStartedArtInAPeriodAndAreDeath33DaysAfterArtInitiation",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoStartedArtInAPeriodAndAreDeath33DaysAfterArtInitiation),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension
      findPatientsWhoStartedArtInAPeriodAndSuspendTratement33DaysAfterInitiation() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("findPatientsWhoStartedArtInAPeriodAndSuspendTratement33DaysAfterInitiation");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "ERI2.R21-06",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoStartedArtInAPeriodAndSuspendTratement33DaysAfterInitiation",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoStartedArtInAPeriodAndSuspendTratement33DaysAfterInitiation),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension
      findPatientsWhoStartedArtInAPeriodAndAreTrasferedOut33DaysAfterInitiation() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("findPatientsWhoStartedArtInAPeriodAndAreTrasferedOut33DaysAfterInitiation");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "ERI2.R21-03",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoStartedArtInAPeriodAndAreTrasferedOut33DaysAfterInitiation",
                Eri2MonthsQueriesInterface.QUERY
                    .findPatientsWhoStartedArtInAPeriodAndAreTrasferedOut33DaysAfterInitiation),
            mappings));

    return dimension;
  }
}
