/** */
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.IMR1CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.IMR1Queries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IMR1Dimensions {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired IMR1CohortQueries iMR1CohortQueries;

  public CohortDefinitionDimension getDimension() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));
    dimension.setName("Get patient states");

    dimension.addCohortDefinition(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoArePregnantInAPeriod",
                IMR1Queries.QUERY.findPatientsWhoArePregnantInAPeriod),
            "endDate=${endDate},location=${location}"));

    dimension.addCohortDefinition(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreBreastfeedindnAPeriod",
                IMR1Queries.QUERY.findPatientsWhoAreBreastfeeding),
            "endDate=${endDate},location=${location}"));

    return dimension;
  }
}
