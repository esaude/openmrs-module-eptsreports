package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.ListOfPatientsEligileToTPTQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ListPatientsEligibleTPTCohortQueries {

  @DocumentedDefinition(value = "findPatientsEligibleTPT")
  public CohortDefinition findPatientsEligibleTPT() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsEligibleTPT");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(ListOfPatientsEligileToTPTQueries.QUERY.findPatientsEligibleToTPT);

    return definition;
  }
}
