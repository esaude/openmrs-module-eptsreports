package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXRetCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxRetDimensionCohort {
  @Autowired private TXRetCohortQueries txRetCohortQueries;

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
}
