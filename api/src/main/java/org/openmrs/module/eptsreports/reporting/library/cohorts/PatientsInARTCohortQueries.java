package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class PatientsInARTCohortQueries {

  private static final String FIND_PATIENTS_IN_ART_TOTAL =
      "ART-INITIATION/LIST_OF_PATIENTS_IN_ART_COHORT_TOTAL.sql";

  @DocumentedDefinition(value = "findPatientsWhoInARTTotal")
  public CohortDefinition findPatientsWhoInARTTotal() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoInARTTotal");
    definition.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    definition.addParameter(new Parameter("cohorEndDate", "Cohort End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(EptsQuerysUtils.loadQuery(FIND_PATIENTS_IN_ART_TOTAL));

    return definition;
  }
}
