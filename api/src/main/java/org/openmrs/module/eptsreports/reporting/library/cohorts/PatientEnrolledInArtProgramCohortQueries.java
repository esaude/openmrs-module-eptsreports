package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.PatientEnrolledInArtProgramQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class PatientEnrolledInArtProgramCohortQueries {
  public CohortDefinition findPatientEnrolledInArtProgramOnReportingPeriod() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsEnrolledInArtProgram");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(
        PatientEnrolledInArtProgramQueries.QUERY.findPatientsEnrolledInArtProgramOnReportingPeriod);
    return definition;
  }
}
