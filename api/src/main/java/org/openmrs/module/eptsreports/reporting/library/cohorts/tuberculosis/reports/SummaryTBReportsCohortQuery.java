package org.openmrs.module.eptsreports.reporting.library.cohorts.tuberculosis.reports;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.tuberculosis.reports.PatientsWithPositiveTuberculosisWhoStartdTPIDataSetQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class SummaryTBReportsCohortQuery {

  public CohortDefinition getPatientsWithTBWhoStartedTPITotal() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("INICIO");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = PatientsWithPositiveTuberculosisWhoStartdTPIDataSetQueries.getTotal();

    definition.setQuery(query);

    return definition;
  }
}
