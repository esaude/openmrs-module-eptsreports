/** */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.MisauResumoMensalPRepQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class MisauResumoMensalPrepReportCohortQueries {

  public static final int DRUG_USERS_TARGET_GROUP = 20454;
  public static final int SEX_WORKER_TARGET_GROUP = 1901;
  public static final int TRANSGENDER_TARGET_GROUP = 165205;
  public static final int HOMOSEXUAL_TARGET_GROUP = 1377;

  @DocumentedDefinition(value = "A1 - NumberOfUsersEligibleToPREP")
  public CohortDefinition getIndicatorA1() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("A1 - NumberOfUsersEligibleToPREP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(MisauResumoMensalPRepQueries.QUERY.findNumberOfUsersEligibleToPREP);
    return definition;
  }

  @DocumentedDefinition(value = "B1 - NumberOfUsersWhoseInitiatedPrepForTheFirstTime")
  public CohortDefinition getIndicatorB1() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("B1 - NumberOfUsersWhoseInitiatedPrepForTheFirstTime");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(
        MisauResumoMensalPRepQueries.QUERY.findNumberOfUsersWhoseInitiatedPrepForTheFirstTime);
    return definition;
  }

  @DocumentedDefinition(value = "B2 - NumberOfUsersWhoseRestartedPREP")
  public CohortDefinition getIndicatorB2() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName(" B2 - NumberOfUsersWhoseRestartedPREP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(MisauResumoMensalPRepQueries.QUERY.findNumberOfUsersWhoseRestartedPREP);
    return definition;
  }

  @DocumentedDefinition(value = "C1 - NumberOfUsersWhoReceivePrepREgimen")
  public CohortDefinition getIndicatorC1() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("C1 - NumberOfUsersWhoReceivePrepREgimen");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(MisauResumoMensalPRepQueries.QUERY.findNumberOfUsersWhoReceivePrepREgimen);
    return definition;
  }

  @DocumentedDefinition(value = " D1 - NumberOfUseresCurrentlyOnPrep")
  public CohortDefinition getIndicatorD1() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName(" D1 - NumberOfUseresCurrentlyOnPrep");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(MisauResumoMensalPRepQueries.QUERY.findNumberOfUseresCurrentlyOnPrep);
    return definition;
  }
}
