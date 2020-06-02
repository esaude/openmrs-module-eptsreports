/** */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.BreastfeedingQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class BreastFeedingCohortQueries {

  @Autowired private GenericCohortQueries genericCohortQueries;

  public CohortDefinition findPatientsWhoAreBreastFeedingExcludingPregnantsInAPeriod() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("patientsWhoAreBreastFeedingExcludingPregnantsInAPeriod");

    definition.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    definition.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreBreastfeeding",
                BreastfeedingQueries.findPatientsWhoAreBreastfeeding()),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));

    definition.setCompositionString("BREASTFEEDING");

    return definition;
  }
}
