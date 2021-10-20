package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.ListOfPatientsEligileToViralLoadQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ListPatientsEligibleViralLoadCohortQueries {

  @DocumentedDefinition(value = "findPatientsEligibleToViralLoad")
  public CohortDefinition findPatientsEligibleToViralLoad() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsEligibleToViralLoad");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        ListOfPatientsEligileToViralLoadQueries.QUERY.findPatientsEligibleToViralLoad);

    return definition;
  }
}
