package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.TxTbPrevQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.TxTbPrevQueriesInterface.QUERY.DisaggregationTypes;
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

  @DocumentedDefinition(value = "getTbPrevTotalDenominator")
  public CohortDefinition getTbPrevTotalDenominator() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("Patients Who Started TPT");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "STARTED-TPT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Finding Patients Who have Started TPT During Previous Reporting Period",
                TxTbPrevQueriesInterface.QUERY
                    .findPatientsWhoStartedTbPrevPreventiveTreatmentDuringPreviousReportingPeriod),
            mappings));
    dsd.addSearch("TRF-OUT", EptsReportUtils.map(this.findPatientsTransferredOut(), mappings));

    dsd.addSearch(
        "ENDED-TPT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Finding Patients Who have Completed TPT",
                TxTbPrevQueriesInterface.QUERY
                    .findPatientsWhoCompletedTbPrevPreventiveTreatmentDuringReportingPeriod),
            mappings));

    dsd.addSearch(
        "NEWLY-ART",
        EptsReportUtils.map(this.findPatientsWhoStartedArtAndTpiNewDessagragation(), mappings));

    dsd.addSearch(
        "PREVIOUS-ART",
        EptsReportUtils.map(
            this.findPatientsWhoStartedArtAndTpiPreviouslyDessagragation(), mappings));

    dsd.setCompositionString(
        "(STARTED-TPT AND (NEWLY-ART OR PREVIOUS-ART)) NOT (TRF-OUT NOT ENDED-TPT) ");

    return dsd;
  }

  @DocumentedDefinition(value = "getTbPrevTotalNumerator")
  public CohortDefinition getTbPrevTotalNumerator() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("get Patients Who Completed TPT");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ENDED-TPT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Finding Patients Who have Completed TPT",
                TxTbPrevQueriesInterface.QUERY
                    .findPatientsWhoCompletedTbPrevPreventiveTreatmentDuringReportingPeriod),
            mappings));
    dsd.addSearch("DENOMINATOR", EptsReportUtils.map(this.getTbPrevTotalDenominator(), mappings));

    dsd.setCompositionString("ENDED-TPT AND DENOMINATOR");

    return dsd;
  }

  @DocumentedDefinition(value = "findPatientsWhoStartedArtAndTpiNewDessagragation")
  public CohortDefinition findPatientsWhoStartedArtAndTpiNewDessagragation() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("get Patients Who Started Art And TPT New - Dessagragation");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "STARTED-TPT-AND-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Finding Patients New on ART Who Have Started TPT",
                TxTbPrevQueriesInterface.QUERY
                    .findPatientsWhoStartedArtAndTbPrevPreventiveTreatmentInDisaggregation(
                        DisaggregationTypes.NEWLY_ENROLLED)),
            mappings));
    dsd.setCompositionString("STARTED-TPT-AND-ART");

    return dsd;
  }

  @DocumentedDefinition(value = "findPatientsWhoStartedArtAndTpiPreviouslyDessagragation")
  public CohortDefinition findPatientsWhoStartedArtAndTpiPreviouslyDessagragation() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("get Patients Who Started Art And TPT Previows - Dessagragation");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "STARTED-TPT-AND-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Finding Patients Previously on ART Who Have Started TPT",
                TxTbPrevQueriesInterface.QUERY
                    .findPatientsWhoStartedArtAndTbPrevPreventiveTreatmentInDisaggregation(
                        DisaggregationTypes.PREVIOUSLY_ENROLLED)),
            mappings));
    dsd.setCompositionString("STARTED-TPT-AND-ART");
    return dsd;
  }

  @DocumentedDefinition(value = "findPatientsTransferredOut")
  private CohortDefinition findPatientsTransferredOut() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("get Patients Who were Transferred Out");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(TxTbPrevQueriesInterface.QUERY.findPatientsTransferredOut);

    return definition;
  }
}
