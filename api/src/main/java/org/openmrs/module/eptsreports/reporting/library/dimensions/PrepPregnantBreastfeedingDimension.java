/** */
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.PrepCtQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrepPregnantBreastfeedingDimension {

  @Autowired private GenericCohortQueries genericCohortQueries;

  public CohortDefinitionDimension getPregnantAndBreastfeedingDimensios() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    dimension.setName("Pregnant and Breastfeeding Dimensions");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "BREASTFEEDING",
                PrepCtQueries.QUERY.findClientsWithBreastfeedingStatusDuringReportingPeriod),
            mappings));
    dimension.addCohortDefinition(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "PREGNANT",
                PrepCtQueries.QUERY.findClientsWithPregnancyStatusDuringReportingPeriod),
            mappings));
    return dimension;
  }
}
