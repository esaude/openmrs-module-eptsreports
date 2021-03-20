package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.ListOfPatientsWhoInitiatedTPTQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class TptCohortQueries {

  @DocumentedDefinition(value = "finPatientsWhoInitieted3hp")
  public CohortDefinition finPatientsWhoInitieted3hp() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("finPatientsWhoInitieted3hp");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(ListOfPatientsWhoInitiatedTPTQueries.QUERY.findPatientsWhoInitiatedTPT);

    return definition;
  }
}
