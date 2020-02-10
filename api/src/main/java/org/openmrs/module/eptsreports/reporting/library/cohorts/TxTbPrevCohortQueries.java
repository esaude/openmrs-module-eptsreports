package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.TxTbPrevQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxTbPrevCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  @DocumentedDefinition(value = "findPatientWhoStartedTpi")
  public CohortDefinition findPatientWhoStartedTpi() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientWhoStartedTpi");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(TxTbPrevQueriesInterface.QUERY.findPatientWhoStartedTpi);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoStartedTpi6MonthsAgoAndWhoEndedTpi")
  public CohortDefinition findPatientsWhoStartedTpi6MonthsAgoAndWhoEndedTpi() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientWhoStartedTpi");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TxTbPrevQueriesInterface.QUERY.findPatientsWhoStartedTpi6MonthsAgoAndWhoEndedTpi);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsTransferredOut")
  public CohortDefinition findPatientsTransferredOut() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientWhoStartedTpi");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(TxTbPrevQueriesInterface.QUERY.findPatientsTransferredOut);

    return definition;
  }

  @DocumentedDefinition(value = "PatientsWhoStartedTpi6MonthsAgoExcludingTransferredOutNotEndedTpi")
  public CohortDefinition findPatientsWhoStartedTpi6MonthsAgoExcludingTransferredOutNotEndedTpi() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("PatientsWhoStartedTpi6MonthsAgoExcludingTransferredOutNotEndedTpi");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch("STARTTPI", EptsReportUtils.map(this.findPatientWhoStartedTpi(), mappings));

    dsd.addSearch(
        "ENDTPI",
        EptsReportUtils.map(this.findPatientsWhoStartedTpi6MonthsAgoAndWhoEndedTpi(), mappings));

    dsd.addSearch("TROUT", EptsReportUtils.map(this.findPatientsTransferredOut(), mappings));

    dsd.setCompositionString("STARTTPI NOT(TROUT NOT ENDTPI)");

    return dsd;
  }

  @DocumentedDefinition(value = "PatientsWhoStartedArtAndTpi6Months")
  public CohortDefinition findPatientsWhoStartedArtAndTpiNewDessagragation() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("PatientsWhoStartedArtAndTpi6Months");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "STARTTPIART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientWhoStartedTpiAndArt",
                TxTbPrevQueriesInterface.QUERY.findPatientsWhoStartedArtAndTpiNewDessagragation),
            mappings));

    dsd.addSearch(
        "ENDTPI",
        EptsReportUtils.map(this.findPatientsWhoStartedTpi6MonthsAgoAndWhoEndedTpi(), mappings));

    dsd.addSearch("TROUT", EptsReportUtils.map(this.findPatientsTransferredOut(), mappings));

    dsd.setCompositionString("STARTTPIART NOT(TROUT NOT ENDTPI)");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsWhoStartedArtAndTpiPreviouslyDessagragation")
  public CohortDefinition findPatientsWhoStartedArtAndTpiPreviouslyDessagragation() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("PatientsWhoStartedArtAndTpi6Months");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "STARTTPIART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientWhoStartedTpiAndArt",
                TxTbPrevQueriesInterface.QUERY
                    .findPatientsWhoStartedArtAndTpiPreviouslyDessagragation),
            mappings));

    dsd.addSearch(
        "ENDTPI",
        EptsReportUtils.map(this.findPatientsWhoStartedTpi6MonthsAgoAndWhoEndedTpi(), mappings));

    dsd.addSearch("TROUT", EptsReportUtils.map(this.findPatientsTransferredOut(), mappings));

    dsd.setCompositionString("STARTTPIART NOT(TROUT NOT ENDTPI)");

    return dsd;
  }

  @DocumentedDefinition(value = "findTbTotalDenominator")
  public CohortDefinition findTbTotalDenominator() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("findTbTotalDenominator");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NEW", EptsReportUtils.map(findPatientsWhoStartedArtAndTpiNewDessagragation(), mappings));

    dsd.addSearch(
        "PRIVIUS",
        EptsReportUtils.map(
            this.findPatientsWhoStartedArtAndTpiPreviouslyDessagragation(), mappings));

    dsd.setCompositionString("NEW OR PRIVIUS");

    return dsd;
  }
}
