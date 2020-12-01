package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovementQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnArt() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYear;

    definition.setQuery(query);

    return definition;
  }

  public CohortDefinition findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultRF11() {
    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("Tx NEW");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},location=${location}";

    defeinition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdult),
            mappings));

    defeinition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    defeinition.setCompositionString("START-ART NOT (TRANSFERED-IN)");

    return defeinition;
  }
}
