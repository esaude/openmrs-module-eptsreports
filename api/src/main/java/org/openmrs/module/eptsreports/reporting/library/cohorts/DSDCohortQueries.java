package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.DsdQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.TxCurrQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DSDCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  @DocumentedDefinition(value = "patientsActiveOnArtExcludingPregnantBreastfeedingAndTb")
  public CohortDefinition getPatientsActiveOnArtExcludingPregnantBreastfeedingAndTb(
      final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "IN-ART", TxCurrQueries.QUERY.findPatientsWhoAreCurrentlyEnrolledOnART),
            mappings));

    dsd.addSearch(
        "PREGNANT-BRESTFEETING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT-BRESTFEETING",
                DsdQueriesInterface.QUERY
                    .findPregnantWomenRegisteredInTheLast9MonthsOrBrestfeetingWomenRegisteredInTheLast18Months),
            mappings));

    dsd.addSearch(
        "TB",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB", DsdQueriesInterface.QUERY.findPatientsBeingOnTuberculosisTreatmentEndPeriod),
            mappings));

    dsd.setCompositionString("IN-ART NOT (PREGNANT-BRESTFEETING OR TB)");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveInArtEligibleForDsd")
  public CohortDefinition getPatientsActiveOnArtEligibleForDsd(final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            getPatientsActiveOnArtExcludingPregnantBreastfeedingAndTb(""), mappings));

    dsd.addSearch(
        "STABLE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsInArtWhoAreStable",
                DsdQueriesInterface.QUERY.findPatientsInArtWhoAreStable),
            mappings));

    dsd.addSearch(
        "SARCOMA-KAPOSI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "SARCOMA-KAPOSI",
                DsdQueriesInterface.QUERY.findPatientsWhoHaveBeenNotifiedOfKaposiSarcoma),
            mappings));

    dsd.addSearch(
        "ADVERSASE-REACTIONS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "ADVERSASE-REACTIONS",
                DsdQueriesInterface.QUERY
                    .findPatientsWithAdverseDrugReactionsRequiringRegularMonitoringNotifiedInLast6Months),
            mappings));

    dsd.setCompositionString("(IN-ART AND STABLE) NOT(ADVERSASE-REACTIONS OR SARCOMA-KAPOSI)");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsWhoNotElegibleDSD")
  public CohortDefinition getPatientsActiveOnArtNotEligibleForDsd(final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            getPatientsActiveOnArtExcludingPregnantBreastfeedingAndTb(""), mappings));

    dsd.addSearch(
        "STABLE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsInArtWhoAreStable",
                DsdQueriesInterface.QUERY.findPatientsInArtWhoAreStable),
            mappings));

    dsd.addSearch(
        "SARCOMA-KAPOSI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "SARCOMA-KAPOSI",
                DsdQueriesInterface.QUERY.findPatientsWhoHaveBeenNotifiedOfKaposiSarcoma),
            mappings));

    dsd.addSearch(
        "ADVERSASE-REACTIONS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "ADVERSASE-REACTIONS",
                DsdQueriesInterface.QUERY
                    .findPatientsWithAdverseDrugReactionsRequiringRegularMonitoringNotifiedInLast6Months),
            mappings));

    dsd.setCompositionString(
        "IN-ART NOT((STABLE AND IN-ART) NOT(ADVERSASE-REACTIONS OR SARCOMA-KAPOSI))");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtWhoInDt")
  public CohortDefinition getPatientsActiveOnArtWhoInDt(final String cohortName) {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            getPatientsActiveOnArtExcludingPregnantBreastfeedingAndTb(""), mappings));

    dsd.addSearch(
        "QUARTERLY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "QUARTERLY-DISPENSATION",
                DsdQueriesInterface.QUERY.findPatientWhoAreMdcQuarterlyDispensation),
            mappings));

    dsd.setCompositionString("(IN-ART AND QUARTERLY-DISPENSATION)");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtWhoInDt")
  public CohortDefinition getPatientsActiveOnArtElegibleDsdWhoInDt(final String cohortName) {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE", EptsReportUtils.map(getPatientsActiveOnArtEligibleForDsd(""), mappings));

    dsd.addSearch(
        "QUARTERLY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "QUARTERLY-DISPENSATION",
                DsdQueriesInterface.QUERY.findPatientWhoAreMdcQuarterlyDispensation),
            mappings));

    dsd.setCompositionString("(ELEGIBLE AND QUARTERLY-DISPENSATION)");

    return dsd;
  }
}
