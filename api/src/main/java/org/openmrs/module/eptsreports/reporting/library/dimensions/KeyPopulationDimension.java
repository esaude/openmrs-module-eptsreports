/** */
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.KeyPopulationQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class KeyPopulationDimension {

  @Autowired GenericCohortQueries genericCohortQueries;

  public CohortDefinitionDimension findPatientsWhoAreHomosexual() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Homosexual Patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "homosexual",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreHomosexual",
                KeyPopulationQueries.QUERY.findPatientsWhoAreHomosexual),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoUseDrugs() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Drugs User Patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "drug-user",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoUseDrugs", KeyPopulationQueries.QUERY.findPatientsWhoUseDrugs),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreInPrison() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Patients in prision");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "prisioner",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreInPrison", KeyPopulationQueries.QUERY.findPatientsWhoAreInPrison),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreSexWorker() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Sex worker patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "sex-worker",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreSexWorker", KeyPopulationQueries.QUERY.findPatientsWhoAreSexWorker),
            mappings));

    return dimension;
  }
}
