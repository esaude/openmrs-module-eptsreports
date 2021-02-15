package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CXCASCRNCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CXCASCRNDimensions {
  @Autowired private CXCASCRNCohortQueries cXCASCRNCohortQueries;

  public CohortDefinitionDimension getDimension() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    dimension.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));
    dimension.setName("Get patient states");

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "NEGATIVE",
        EptsReportUtils.map(
            cXCASCRNCohortQueries
                .findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodNegative(),
            mappings));

    return dimension;
  }
}
