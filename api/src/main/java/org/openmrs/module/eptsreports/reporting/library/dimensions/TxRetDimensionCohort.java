package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXRetCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxRetDimensionCohort {
  @Autowired private TXRetCohortQueries txRetCohortQueries;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  public CohortDefinitionDimension startedTargetAtARTInitiation() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    dim.setName("startedTargetAtARTInitiation");
    dim.addCohortDefinition(
        "0001",
        EptsReportUtils.map(
            txRetCohortQueries.under1YearIncreasedHARTAtARTStartDate(),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "0109",
        EptsReportUtils.map(
            txRetCohortQueries.oneTo19WhoStartedTargetAtARTInitiation(),
            "endDate=${endDate},location=${location}"));
    return dim;
  }

  public CohortDefinitionDimension pregnantOrBreastFeeding() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    dim.setName("pregnantOrBreastFeeding");
    dim.addCohortDefinition(
        "LACTANTE",
        EptsReportUtils.map(
            txRetCohortQueries.possibleRegisteredClinicalProcedureAndFollowupForm(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "GRAVIDAS",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    return dim;
  }

  public CohortDefinitionDimension genderOnArtByAge() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    dim.setName("on ART By gender and ages");
    String mappings = "endDate=${endDate},location=${location}";
    dim.addCohortDefinition(
        "10-14M", EptsReportUtils.map(txRetCohortQueries.menOnArt10To14(), mappings));
    dim.addCohortDefinition(
        "10-14F", EptsReportUtils.map(txRetCohortQueries.womenOnArt10To14(), mappings));
    dim.addCohortDefinition(
        "15-19M", EptsReportUtils.map(txRetCohortQueries.menOnArt15To19(), mappings));
    dim.addCohortDefinition(
        "15-19F", EptsReportUtils.map(txRetCohortQueries.womenOnArt15To19(), mappings));
    dim.addCohortDefinition(
        "20-24M", EptsReportUtils.map(txRetCohortQueries.menOnArt20To24(), mappings));
    dim.addCohortDefinition(
        "20-24F", EptsReportUtils.map(txRetCohortQueries.womenOnArt20To24(), mappings));
    dim.addCohortDefinition(
        "25-29M", EptsReportUtils.map(txRetCohortQueries.menOnArt25To29(), mappings));
    dim.addCohortDefinition(
        "25-29F", EptsReportUtils.map(txRetCohortQueries.womenOnArt25To29(), mappings));
    dim.addCohortDefinition(
        "30-34M", EptsReportUtils.map(txRetCohortQueries.menOnArt30To34(), mappings));
    dim.addCohortDefinition(
        "30-34F", EptsReportUtils.map(txRetCohortQueries.womenOnArt30To34(), mappings));
    dim.addCohortDefinition(
        "35-39M", EptsReportUtils.map(txRetCohortQueries.menOnArt35To39(), mappings));
    dim.addCohortDefinition(
        "35-39F", EptsReportUtils.map(txRetCohortQueries.womenOnArt35To39(), mappings));
    dim.addCohortDefinition(
        "40-49M", EptsReportUtils.map(txRetCohortQueries.menOnArt40To49(), mappings));
    dim.addCohortDefinition(
        "40-49F", EptsReportUtils.map(txRetCohortQueries.womenOnArt40To49(), mappings));
    dim.addCohortDefinition(
        "50+M", EptsReportUtils.map(txRetCohortQueries.menOnArtAbove50(), mappings));
    dim.addCohortDefinition(
        "50+F", EptsReportUtils.map(txRetCohortQueries.womenOnArtAbove50(), mappings));

    return dim;
  }
}
