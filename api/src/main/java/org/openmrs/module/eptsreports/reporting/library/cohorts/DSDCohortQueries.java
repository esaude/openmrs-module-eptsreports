package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.BreastfeedingQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.DsdQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.PregnantQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TbQueries;
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

  @DocumentedDefinition(value = "patientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb")
  public CohortDefinition findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb() {

    final CompositionCohortDefinition dataSetDefinitio = new CompositionCohortDefinition();

    dataSetDefinitio.setName("patientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb");
    dataSetDefinitio.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dataSetDefinitio.addParameter(new Parameter("endDate", "End Date", Date.class));
    dataSetDefinitio.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dataSetDefinitio.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "IN-ART", TxCurrQueries.QUERY.findPatientsWhoAreCurrentlyEnrolledOnART),
            mappings));

    dataSetDefinitio.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoArePregnantInAPeriod",
                PregnantQueries.findPatientsWhoArePregnantInAPeriodNotCheckingBreastfeeding()),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));

    dataSetDefinitio.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreBreastfeeding",
                BreastfeedingQueries.findPatientsWhoAreBreastfeedingNotCheckingPregnant()),
            "startDate=${endDate-18m},endDate=${endDate},location=${location}"));

    dataSetDefinitio.addSearch(
        "TB",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreInTbTreatment", TbQueries.QUERY.findPatientsWhoAreInTbTreatment),
            mappings));

    dataSetDefinitio.setCompositionString("IN-ART NOT (PREGNANT OR BREASTFEEDING OR TB)");

    return dataSetDefinitio;
  }

  @DocumentedDefinition(value = "patientsActiveInArtEligibleForDsd")
  public CohortDefinition findPatientsActiveOnArtEligibleForDsd() {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtEligibleForDsd");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb(), mappings));

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

    dsd.setCompositionString("(IN-ART AND STABLE) NOT (ADVERSASE-REACTIONS OR SARCOMA-KAPOSI)");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsWhoNotElegibleDSD")
  public CohortDefinition findPatientsActiveOnArtNotEligibleForDsd() {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsWhoNotElegibleDSD");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb(), mappings));

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
        "IN-ART NOT ((STABLE AND IN-ART) NOT (ADVERSASE-REACTIONS OR SARCOMA-KAPOSI))");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsWhoAreActiveOnArtAndInAtleastOneDSD")
  public CohortDefinition findPatientsWhoAreActiveOnArtAndInAtleastOneDSD() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("patientsWhoAreActiveOnArtAndInAtleastOneDSD");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb(), mappings));

    definition.addSearch(
        "GAAC",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreCommunityAdherenceGroups",
                DsdQueriesInterface.QUERY.findPatientsWhoAreCommunityAdherenceGroups),
            mappings));

    definition.addSearch(
        "AF",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreFamilyAproach",
                DsdQueriesInterface.QUERY.findPatientsWhoAreFamilyAproach),
            mappings));

    definition.addSearch(
        "CA",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patietsWhoAreOnAdherenceClubs",
                DsdQueriesInterface.QUERY.findPatietsWhoAreOnAdherenceClubs),
            mappings));

    definition.addSearch(
        "DT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreThreeMonthsDrugDistribution",
                DsdQueriesInterface.QUERY.findPatientsWhoAreThreeMonthsDrugDistribution),
            mappings));

    definition.addSearch(
        "FR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreFastTrack", DsdQueriesInterface.QUERY.findPatientsWhoAreFastTrack),
            mappings));

    definition.addSearch(
        "DC",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreInCommunityDrugsDistribution",
                DsdQueriesInterface.QUERY.findPatientsWhoAreInCommunityDrugsDistribution),
            mappings));

    definition.addSearch(
        "DS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreSixMonthsDrugsDistribution",
                DsdQueriesInterface.QUERY.findPatientsWhoAreSixMonthsDrugsDistribution),
            mappings));

    definition.setCompositionString("IART AND (GAAC OR AF OR CA OR DT FR OR DC OR DS)");

    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreActiveOnArtAndInAtleastOneDSDAndAreStable")
  public CohortDefinition findPatientsWhoAreActiveOnArtAndInAtleastOneDSDAndAreStable() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("patientsWhoAreActiveOnArtAndInAtleastOneDSD");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART-DSD",
        EptsReportUtils.map(this.findPatientsWhoAreActiveOnArtAndInAtleastOneDSD(), mappings));

    definition.addSearch(
        "STABLE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsInArtWhoAreStable",
                DsdQueriesInterface.QUERY.findPatientsInArtWhoAreStable),
            mappings));

    definition.addSearch(
        "SARCOMA-KAPOSI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoHaveBeenNotifiedOfKaposiSarcoma",
                DsdQueriesInterface.QUERY.findPatientsWhoHaveBeenNotifiedOfKaposiSarcoma),
            mappings));

    definition.addSearch(
        "ADVERSASE-REACTIONS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWithAdverseDrugReactionsRequiringRegularMonitoringNotifiedInLast6Months",
                DsdQueriesInterface.QUERY
                    .findPatientsWithAdverseDrugReactionsRequiringRegularMonitoringNotifiedInLast6Months),
            mappings));

    definition.setCompositionString(
        "(STABLE AND IART-DSD) NOT (ADVERSASE-REACTIONS OR SARCOMA-KAPOSI)");

    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreActiveOnArtAndInAtleastOneDSDAndAreUnstable")
  public CohortDefinition findPatientsWhoAreActiveOnArtAndInAtleastOneDSDAndAreUnstable() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("patientsWhoAreActiveOnArtAndInAtleastOneDSDAndAreUnstable");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART-DSD",
        EptsReportUtils.map(this.findPatientsWhoAreActiveOnArtAndInAtleastOneDSD(), mappings));

    definition.addSearch(
        "STABLE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsInArtWhoAreStable",
                DsdQueriesInterface.QUERY.findPatientsInArtWhoAreStable),
            mappings));

    definition.addSearch(
        "SARCOMA-KAPOSI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoHaveBeenNotifiedOfKaposiSarcoma",
                DsdQueriesInterface.QUERY.findPatientsWhoHaveBeenNotifiedOfKaposiSarcoma),
            mappings));

    definition.addSearch(
        "ADVERSASE-REACTIONS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWithAdverseDrugReactionsRequiringRegularMonitoringNotifiedInLast6Months",
                DsdQueriesInterface.QUERY
                    .findPatientsWithAdverseDrugReactionsRequiringRegularMonitoringNotifiedInLast6Months),
            mappings));

    definition.setCompositionString(
        "IART-DSD NOT ((STABLE AND IART-DSD) NOT (ADVERSASE-REACTIONS OR SARCOMA-KAPOSI))");

    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreActiveOnArtAndInThreeMonthsDrugsDistribution")
  public CohortDefinition findPatientsWhoAreActiveOnArtAndInThreeMonthsDrugsDistribution() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsWhoAreActiveOnArtAndInThreeMonthsDrugsDistribution");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb(), mappings));

    dsd.addSearch(
        "DT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreThreeMonthsDrugDistribution",
                DsdQueriesInterface.QUERY.findPatientsWhoAreThreeMonthsDrugDistribution),
            mappings));

    dsd.setCompositionString("IN-ART AND DT");

    return dsd;
  }

  @DocumentedDefinition(
      value = "patientsWhoAreActiveOnArtAndEligibleToThreeMonthsDrugsDistribution")
  public CohortDefinition findPatientsWhoAreActiveOnArtAndEligibleToThreeMonthsDrugsDistribution() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsWhoAreActiveOnArtAndEligibleToThreeMonthsDrugsDistribution");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE", EptsReportUtils.map(this.findPatientsActiveOnArtEligibleForDsd(), mappings));

    dsd.addSearch(
        "DT",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtAndInThreeMonthsDrugsDistribution(), mappings));

    dsd.setCompositionString("ELEGIBLE AND DT");

    return dsd;
  }

  @DocumentedDefinition(
      value = "patientsWhoAreActiveOnArtAndNotEligibleToThreeMonthsDrugsDistribution")
  public CohortDefinition
      findPatientsWhoAreActiveOnArtAndNotEligibleToThreeMonthsDrugsDistribution() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsWhoAreActiveOnArtAndNotEligibleToThreeMonthsDrugsDistribution");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(this.findPatientsActiveOnArtNotEligibleForDsd(), mappings));

    dsd.addSearch(
        "DT",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtAndInThreeMonthsDrugsDistribution(), mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND DT");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtInFastTrack")
  public CohortDefinition findPatientsActiveOnArtInFastTrack() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtInFastTrack");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb(), mappings));

    dsd.addSearch(
        "FR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreFastTrack", DsdQueriesInterface.QUERY.findPatientsWhoAreFastTrack),
            mappings));

    dsd.setCompositionString("IN-ART AND FR");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtEligibleToFastTrack")
  public CohortDefinition findPatientsActiveOnArtEligibleToFastTrack() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtEligibleToFastTrack");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE", EptsReportUtils.map(this.findPatientsActiveOnArtEligibleForDsd(), mappings));

    dsd.addSearch("FR", EptsReportUtils.map(this.findPatientsActiveOnArtInFastTrack(), mappings));

    dsd.setCompositionString("ELEGIBLE AND FR");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtNotElegibleToFastTrack")
  public CohortDefinition findPatientsActiveOnArtNotElegibleToFastTrack() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtNotElegibleToFastTrack");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(this.findPatientsActiveOnArtNotEligibleForDsd(), mappings));

    dsd.addSearch("FR", EptsReportUtils.map(this.findPatientsActiveOnArtInFastTrack(), mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND FR");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtInCommunityAdherennceGroups")
  public CohortDefinition findPatientsActiveOnArtInCommunityAdherennceGroups() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtInCommunityAdherennceGroups");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb(), mappings));

    dsd.addSearch(
        "GAAC",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreCommunityAdherenceGroups",
                DsdQueriesInterface.QUERY.findPatientsWhoAreCommunityAdherenceGroups),
            mappings));

    dsd.setCompositionString("IN-ART AND GAAC");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtEligibleToCommunityAdherennceGroups")
  public CohortDefinition findPatientsActiveOnArtEligibleToCommunityAdherennceGroups() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtEligibleToCommunityAdherennceGroups");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE", EptsReportUtils.map(this.findPatientsActiveOnArtEligibleForDsd(), mappings));

    dsd.addSearch(
        "GAAC",
        EptsReportUtils.map(this.findPatientsActiveOnArtInCommunityAdherennceGroups(), mappings));

    dsd.setCompositionString("ELEGIBLE AND GAAC");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtNotEligibleToCommunityAdherennceGroups")
  public CohortDefinition findPatientsActiveOnArtNotEligibleToCommunityAdherennceGroups() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtNotEligibleToCommunityAdherennceGroups");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(this.findPatientsActiveOnArtNotEligibleForDsd(), mappings));

    dsd.addSearch(
        "GAAC",
        EptsReportUtils.map(this.findPatientsActiveOnArtInCommunityAdherennceGroups(), mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND GAAC");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtInFamilyApproach")
  public CohortDefinition findPatientsActiveOnArtInFamilyApproach() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtInFamilyApproach");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb(), mappings));

    dsd.addSearch(
        "AF",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreFamilyAproach",
                DsdQueriesInterface.QUERY.findPatientsWhoAreFamilyAproach),
            mappings));

    dsd.setCompositionString("IN-ART AND AF");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtEligibleToFamilyApproach")
  public CohortDefinition findPatientsActiveOnArtEligibleToFamilyApproach() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtEligibleToFamilyApproach");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE", EptsReportUtils.map(this.findPatientsActiveOnArtEligibleForDsd(), mappings));

    dsd.addSearch(
        "AF", EptsReportUtils.map(this.findPatientsActiveOnArtInFamilyApproach(), mappings));

    dsd.setCompositionString("ELEGIBLE AND AF");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtNotEligibleToFamilyApproach")
  public CohortDefinition findPatientsActiveOnArtNotEligibleToFamilyApproach() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtNotEligibleToFamilyApproach");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(this.findPatientsActiveOnArtNotEligibleForDsd(), mappings));

    dsd.addSearch(
        "AF", EptsReportUtils.map(this.findPatientsActiveOnArtInFamilyApproach(), mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND AF");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtInAdherenceClubs")
  public CohortDefinition findPatientsActiveOnArtInAdherenceClubs() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtInAdherenceClubs");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb(), mappings));

    dsd.addSearch(
        "CA",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patietsWhoAreOnAdherenceClubs",
                DsdQueriesInterface.QUERY.findPatietsWhoAreOnAdherenceClubs),
            mappings));

    dsd.setCompositionString("IN-ART AND CA");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtEligibleToAdherenceClubs")
  public CohortDefinition findPatientsActiveOnArtEligibleToAdherenceClubs() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtEligibleToAdherenceClubs");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE", EptsReportUtils.map(this.findPatientsActiveOnArtEligibleForDsd(), mappings));

    dsd.addSearch(
        "CA", EptsReportUtils.map(this.findPatientsActiveOnArtInAdherenceClubs(), mappings));

    dsd.setCompositionString("ELEGIBLE AND CA");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtNotEligibleToAdherenceClubs")
  public CohortDefinition findPatientsActiveOnArtNotEligibleToAdherenceClubs() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtNotEligibleToAdherenceClubs");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(this.findPatientsActiveOnArtNotEligibleForDsd(), mappings));

    dsd.addSearch(
        "CA", EptsReportUtils.map(this.findPatientsActiveOnArtInAdherenceClubs(), mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND CA");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtInSixMonthsDrugsDistribution")
  public CohortDefinition findPatientsActiveOnArtInSixMonthsDrugsDistribution() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtInSixMonthsDrugsDistribution");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb(), mappings));

    dsd.addSearch(
        "DS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreSixMonthsDrugsDistribution",
                DsdQueriesInterface.QUERY.findPatientsWhoAreSixMonthsDrugsDistribution),
            mappings));

    dsd.setCompositionString("IN-ART AND DS");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtEligibleToSixMonthsDrugsDistribution")
  public CohortDefinition findPatientsActiveOnArtEligibleToSixMonthsDrugsDistribution() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtEligibleToSixMonthsDrugsDistribution");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE", EptsReportUtils.map(this.findPatientsActiveOnArtEligibleForDsd(), mappings));

    dsd.addSearch(
        "DS",
        EptsReportUtils.map(this.findPatientsActiveOnArtInSixMonthsDrugsDistribution(), mappings));

    dsd.setCompositionString("ELEGIBLE AND DS");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtNotEligibleToSixMonthsDrugsDistribution")
  public CohortDefinition findPatientsActiveOnArtNotEligibleToSixMonthsDrugsDistribution() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtNotEligibleToSixMonthsDrugsDistribution");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(this.findPatientsActiveOnArtNotEligibleForDsd(), mappings));

    dsd.addSearch(
        "DS",
        EptsReportUtils.map(this.findPatientsActiveOnArtInSixMonthsDrugsDistribution(), mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND DS");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtInCommunityDrugsDistribution")
  public CohortDefinition findPatientsActiveOnArtInCommunityDrugsDistribution() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtInCommunityDrugsDistribution");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "IN-ART",
        EptsReportUtils.map(
            this.findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb(), mappings));

    dsd.addSearch(
        "DC",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreInCommunityDrugsDistribution",
                DsdQueriesInterface.QUERY.findPatientsWhoAreInCommunityDrugsDistribution),
            mappings));

    dsd.setCompositionString("IN-ART AND DC");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtEligibleToCommunityDrugsDistribution")
  public CohortDefinition findPatientsActiveOnArtEligibleToCommunityDrugsDistribution() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtEligibleToCommunityDrugsDistribution");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE", EptsReportUtils.map(this.findPatientsActiveOnArtEligibleForDsd(), mappings));

    dsd.addSearch(
        "DC",
        EptsReportUtils.map(this.findPatientsActiveOnArtInCommunityDrugsDistribution(), mappings));

    dsd.setCompositionString("ELEGIBLE AND DC");

    return dsd;
  }

  @DocumentedDefinition(value = "patientsActiveOnArtNotEligibleToCommunityDrugsDistribution")
  public CohortDefinition findPatientsActiveOnArtNotEligibleToCommunityDrugsDistribution() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("patientsActiveOnArtNotEligibleToCommunityDrugsDistribution");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(this.findPatientsActiveOnArtNotEligibleForDsd(), mappings));

    dsd.addSearch(
        "DC",
        EptsReportUtils.map(this.findPatientsActiveOnArtInCommunityDrugsDistribution(), mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND DC");

    return dsd;
  }
}
