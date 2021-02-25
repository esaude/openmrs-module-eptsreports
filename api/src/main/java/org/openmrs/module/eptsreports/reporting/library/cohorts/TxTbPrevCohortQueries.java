package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.tbprev.TxTbEndINHCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.tbprev.TxTbPrev3HPCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.tbprev.TxTbPrevINHCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
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

  @DocumentedDefinition(value = "findPatientsWhoCompleted3HPPreventiveTreatment")
  public CohortDefinition findPatientsWhoCompleted3HPPreventiveTreatment() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientWhoStartedTpi");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TxTbPrevQueriesInterface.QUERY.findPatientsWhoCompleted3HPPreventiveTreatment);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoStartedArtAndTpiNewDessagragation")
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

    dsd.addSearch("TROUT", EptsReportUtils.map(this.findPatientsTransferredOut(), mappings));

    dsd.setCompositionString("STARTTPIART NOT TROUT ");

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

    dsd.addSearch("TROUT", EptsReportUtils.map(this.findPatientsTransferredOut(), mappings));

    dsd.setCompositionString("STARTTPIART NOT TROUT");

    return dsd;
  }

  @DocumentedDefinition(value = "getPatientsWhoAre3HPCalculation")
  public CohortDefinition getPatientsWhoAre3HPCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "getPatientsWhoAre3HPCalculation",
            Context.getRegisteredComponents(TxTbPrev3HPCalculation.class).get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }

  @DocumentedDefinition(value = "getPatientsWhoAreINHCalculation")
  public CohortDefinition getPatientsWhoAreINHCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "getPatientsWhoAre3HPCalculation",
            Context.getRegisteredComponents(TxTbPrevINHCalculation.class).get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }

  @DocumentedDefinition(value = "getPatientsWhoEndINHCalculation")
  public CohortDefinition getPatientsWhoEndINHCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "getPatientsWhoAre3HPCalculation",
            Context.getRegisteredComponents(TxTbEndINHCalculation.class).get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }

  @DocumentedDefinition(value = "getTbPrevTotalDenominator")
  public CohortDefinition getTbPrevTotalDenominator() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("findTbPrevTotalNumerator");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch("HP", EptsReportUtils.map(getPatientsWhoAre3HPCalculation(), mappings));

    dsd.addSearch("TPI", EptsReportUtils.map(this.getPatientsWhoAreINHCalculation(), mappings));

    dsd.addSearch("TRF-OUT", EptsReportUtils.map(this.findPatientsTransferredOut(), mappings));

    dsd.setCompositionString("(HP OR TPI) NOT (TRF-OUT )");

    return dsd;
  }

  @DocumentedDefinition(value = "getTbPrevTotalNumerator")
  public CohortDefinition getTbPrevTotalNumerator() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("findTbPrevTotalNumerator");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "HP", EptsReportUtils.map(this.findPatientsWhoCompleted3HPPreventiveTreatment(), mappings));

    dsd.addSearch("INH", EptsReportUtils.map(this.getPatientsWhoEndINHCalculation(), mappings));

    dsd.setCompositionString("HP OR INH");

    return dsd;
  }
}
