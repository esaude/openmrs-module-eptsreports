package org.openmrs.module.eptsreports.reporting.library.cohorts.mi;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.mi.MICategory15QueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MICategory15CohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  @DocumentedDefinition(value = "patientsWhoAreActiveOnArtAndInAtleastOneDSDRF6")
  public CohortDefinition findPatientsWhoAreActiveOnArtAndInAtleastOneDSD() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("patientsWhoAreActiveOnArtAndInAtleastOneDSD");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-2m+1d},endInclusionDate=${endRevisionDate-1m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "GAAC",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreCommunityAdherenceGroups",
                MICategory15QueriesInterface.QUERY.findPatientsWhoAreCommunityAdherenceGroups),
            mappings));

    definition.addSearch(
        "DT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreThreeMonthsDrugDistribution",
                MICategory15QueriesInterface.QUERY.findPatientsWhoAreThreeMonthsDrugDistribution),
            mappings));

    definition.addSearch(
        "FR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreFastTrack",
                MICategory15QueriesInterface.QUERY.findPatientsWhoAreFastTrack),
            mappings));

    definition.addSearch(
        "DC",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreInCommunityDrugsDistribution",
                MICategory15QueriesInterface.QUERY.findPatientsWhoAreInCommunityDrugsDistribution),
            mappings));

    definition.addSearch(
        "DS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoAreSixMonthsDrugsDistribution",
                MICategory15QueriesInterface.QUERY.findPatientsWhoAreSixMonthsDrugsDistribution),
            mappings));

    definition.setCompositionString("GAAC OR DT OR FR OR DC OR DS");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoArePregnantSpecificForCategory15MI")
  public CohortDefinition findPatientsWhoArePregnantSpecificForCategory15MI() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoArePregnantSpecificForCategory15MI");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MICategory15QueriesInterface.QUERY.findPatientsWhoArePregnantSpecificForCategory15MI;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreBreastfeedingSpecificForCategory15MI")
  public CohortDefinition findPatientsWhoAreBreastfeedingSpecificForCategory15MI() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoAreBreastfeedingSpecificForCategory15MI");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MICategory15QueriesInterface.QUERY.findPatientsWhoAreBreastfeedingSpecificForCategory15MI;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientOnARTMarkedPregnantOnTheLastNineMonthsRF8")
  public CohortDefinition findPatientOnARTMarkedPregnantOnTheLastNineMonthsRF8() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREGNANT_LAST_NINE_MONTHS");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantSpecificForCategory15MI(), mappings));

    definition.setCompositionString("PREGNANT");
    return definition;
  }

  @DocumentedDefinition(value = "findPatientOnARTMarkedBreastfeedingOnTheLastEighTeenMonthsRF9")
  public CohortDefinition findPatientOnARTMarkedBreastfeedingOnTheLastEighTeenMonthsRF9() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("BREASTFEEDING_LAST_EIGHTEEN_MONTHS");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.findPatientsWhoAreBreastfeedingSpecificForCategory15MI(), mappings));

    definition.setCompositionString("BREASTFEEDING");
    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithClinicalConsultationAndARTStartDateBiggerThanThreeMonths")
  public CohortDefinition
      findPatientsWithClinicalConsultationDuringRevisionPeriodAndARTStartDateBiggerThanThreeMonths() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithClinicalConsultationAndARTStartDateBiggerThanThreeMonths");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MICategory15QueriesInterface.QUERY
            .findPatientsWithClinicalConsultationAndARTStartDateGreaterThanThreeMonths;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithRegularClinicalConsultationOrRegularArtPickUpInTheLastThreeMonths")
  public CohortDefinition
      findPatientsWithRegularClinicalConsultationOrRegularArtPickUpInTheLastThreeMonths() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithClinicalConsultationOrARTPickUpInTheLastThreeMonths");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MICategory15QueriesInterface.QUERY
            .findPatientsWithClinicalConsultationOrARTPickUpInTheLastThreeMonths;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWithTheLastCargaViralGreaterOrEqualThan1000RegisteredInTheLastClinicalConsultation")
  public CohortDefinition
      findPatientsWithTheLastCargaViralGreaterOrEqualThan1000RegisteredInTheLastClinicalConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWithTheLastCargaViralBiggerOrEqualThan1000InClinicalConsultation");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MICategory15QueriesInterface.QUERY
            .findPatientsWithTheLastCargaViralGreaterOrEqualThan1000RegisteredInTheLastClinicalConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithTheLastCargaViralGreaterOrEqualThan1000InClinicalConsultation")
  public CohortDefinition
      findPatientsWithTheLastCargaViralGreaterOrEqualThan1000InClinicalConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWithTheLastCargaViralGreaterOrEqualThan1000InClinicalConsultation");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MICategory15QueriesInterface.QUERY
            .findPatientsWithTheLastCargaViralGreaterOrEqualThan1000InClinicalConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithTheLastCD4LessThan200OrEqualInClinicalConsultation")
  public CohortDefinition findPatientsWithTheLastCD4LessThan200OrEqualInClinicalConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWithTheLastCargaViralBiggerOrEqualThan1000InClinicalConsultation");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MICategory15QueriesInterface.QUERY
            .findPatientsWithTheLastCD4LessThan200OrEqualInClinicalConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsElegibleForMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriod_Denominator_15_1")
  public CohortDefinition
      findPatientsElegibleForMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriod_Denominator_15_1() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsElegibleForMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriod");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappingsMI =
        "startInclusionDate=${endRevisionDate-2m+1d},endInclusionDate=${endRevisionDate-1m},endRevisionDate=${endRevisionDate},location=${location}";

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "AB1",
        EptsReportUtils.map(
            this
                .findPatientsWithClinicalConsultationDuringRevisionPeriodAndARTStartDateBiggerThanThreeMonths(),
            mappingsMI));

    definition.addSearch(
        "E",
        EptsReportUtils.map(
            this
                .findPatientsWithRegularClinicalConsultationOrRegularArtPickUpInTheLastThreeMonths(),
            mappingsMI));

    definition.addSearch(
        "C",
        EptsReportUtils.map(this.findPatientOnARTMarkedPregnantOnTheLastNineMonthsRF8(), mappings));

    definition.addSearch(
        "D",
        EptsReportUtils.map(
            this.findPatientOnARTMarkedBreastfeedingOnTheLastEighTeenMonthsRF9(), mappings));

    definition.addSearch(
        "F",
        EptsReportUtils.map(
            this.findPatientsWithTheLastCD4LessThan200OrEqualInClinicalConsultation(), mappingsMI));

    definition.addSearch(
        "G",
        EptsReportUtils.map(
            this.findPatientsWithTheLastCargaViralGreaterOrEqualThan1000InClinicalConsultation(),
            mappingsMI));

    definition.addSearch(
        "J",
        EptsReportUtils.map(this.findPatientsWhoAreActiveOnArtAndInAtleastOneDSD(), mappingsMI));

    definition.setCompositionString("(AB1 AND E) NOT (C OR D OR F OR G OR J)");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoHasRegisteredAsIniciarInAtLeastOneMDS")
  public CohortDefinition findPatientsWhoHasRegisteredAsIniciarInAtLeastOneMDS() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPatientsWhoHasRegisteredAsIniciarInAtLeastOneMDS");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-2m+1d},endInclusionDate=${endRevisionDate-1m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "GAAC",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsRegisteredAsIniciarGAAC",
                MICategory15QueriesInterface.QUERY.findPatientsRegisteredAsIniciarGAAC),
            mappings));

    definition.addSearch(
        "DT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsRegisteredAsIniciarDT",
                MICategory15QueriesInterface.QUERY.findPatientsRegisteredAsIniciarDT),
            mappings));

    definition.addSearch(
        "FR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsRegisteredAsIniciarFR",
                MICategory15QueriesInterface.QUERY.findPatientsRegisteredAsIniciarFR),
            mappings));

    definition.addSearch(
        "DC",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsRegisteredAsIniciarDC",
                MICategory15QueriesInterface.QUERY.findPatientsRegisteredAsIniciarDC),
            mappings));

    definition.addSearch(
        "DS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsRegisteredAsIniciarDS",
                MICategory15QueriesInterface.QUERY.findPatientsRegisteredAsIniciarDS),
            mappings));

    definition.setCompositionString("GAAC OR DT OR FR OR DC OR DS");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsElegibleForMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodAndRegisteredAtLeastInOneMDS_Numerator_15_1")
  public CohortDefinition
      findPatientsElegibleForMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodAndRegisteredAtLeastInOneMDS_Numerator_15_1() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsElegibleForMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodAndRegisteredAtLeastInOneMDS");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappingsMI =
        "startInclusionDate=${endRevisionDate-2m+1d},endInclusionDate=${endRevisionDate-1m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR-15-1",
        EptsReportUtils.map(
            this
                .findPatientsElegibleForMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriod_Denominator_15_1(),
            mappingsMI));

    definition.addSearch(
        "K",
        EptsReportUtils.map(
            this.findPatientsWhoHasRegisteredAsIniciarInAtLeastOneMDS(), mappingsMI));

    definition.setCompositionString("(DENOMINATOR-15-1 AND K)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWithClinicalConsultationDuringRevisionPeriodAndAgeGreaterOrEqualTwoYears")
  public CohortDefinition
      findPatientsWithClinicalConsultationDuringRevisionPeriodAndAgeGreaterOrEqualTwoYears() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWithClinicalConsultationDuringRevisionPeriodAndAgeGreaterOrEqualTwoYears");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MICategory15QueriesInterface.QUERY
            .findPatientsWithClinicalConsultationDuringRevisionPeriodAndAgeGreaterOrEqualTwoYears;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodWhoReceivedCargaViralGreaterThan1000_Denominator_15_2")
  public CohortDefinition
      findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodWhoReceivedCargaViralGreaterThan1000_Denominator_15_2() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodWhoReceivedCargaViralGreaterThan1000");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-2m+1d},endInclusionDate=${endRevisionDate-1m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "A",
        EptsReportUtils.map(
            this
                .findPatientsWithClinicalConsultationDuringRevisionPeriodAndAgeGreaterOrEqualTwoYears(),
            mappings));

    definition.addSearch(
        "J", EptsReportUtils.map(this.findPatientsWhoAreActiveOnArtAndInAtleastOneDSD(), mappings));

    definition.addSearch(
        "H",
        EptsReportUtils.map(
            this
                .findPatientsWithTheLastCargaViralGreaterOrEqualThan1000RegisteredInTheLastClinicalConsultation(),
            mappings));

    definition.setCompositionString("(A AND J AND H)");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoHasRegisteredAsFimInAtLeastOneMDS")
  public CohortDefinition findPatientsWhoHasRegisteredAsFimInAtLeastOneMDS() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPatientsWhoHasRegisteredAsFimInAtLeastOneMDS");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-2m+1d},endInclusionDate=${endRevisionDate-1m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "GAAC",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsRegisteredAsFimGAAC",
                MICategory15QueriesInterface.QUERY.findPatientsRegisteredAsFimGAAC),
            mappings));

    definition.addSearch(
        "DT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsRegisteredAsFimDT",
                MICategory15QueriesInterface.QUERY.findPatientsRegisteredAsFimDT),
            mappings));

    definition.addSearch(
        "FR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsRegisteredAsFimFR",
                MICategory15QueriesInterface.QUERY.findPatientsRegisteredAsFimFR),
            mappings));

    definition.addSearch(
        "DC",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsRegisteredAsFimDC",
                MICategory15QueriesInterface.QUERY.findPatientsRegisteredAsFimDC),
            mappings));

    definition.addSearch(
        "DS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsRegisteredAsFimDS",
                MICategory15QueriesInterface.QUERY.findPatientsRegisteredAsFimDS),
            mappings));

    definition.setCompositionString("GAAC OR DT OR FR OR DC OR DS");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodWhoReceivedCargaViralGreaterThan1000AndSuspendedInTheMDS_Numerator_15_2")
  public CohortDefinition
      findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodWhoReceivedCargaViralGreaterThan1000AndSuspendedInTheMDS_Numerator_15_2() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodWhoReceivedCargaViralGreaterThan1000AndSuspendedInTheMDS");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappingsMI =
        "startInclusionDate=${endRevisionDate-2m+1d},endInclusionDate=${endRevisionDate-1m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR-15-2",
        EptsReportUtils.map(
            this
                .findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodWhoReceivedCargaViralGreaterThan1000_Denominator_15_2(),
            mappingsMI));

    definition.addSearch(
        "L",
        EptsReportUtils.map(this.findPatientsWhoHasRegisteredAsFimInAtLeastOneMDS(), mappingsMI));

    definition.setCompositionString("(DENOMINATOR-15-2 AND L)");

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithClinicalConsultationAndARTStartDateGreaterThanTwentyOneMonths")
  public CohortDefinition
      findPatientsWithClinicalConsultationAndARTStartDateGreaterThanTwentyOneMonths() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWithClinicalConsultationAndARTStartDateGreaterThanTwentyOneMonths");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MICategory15QueriesInterface.QUERY
            .findPatientsWithClinicalConsultationAndARTStartDateGreaterThanTwentyOneMonths;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAllPatientsWhoHaveLaboratoryInvestigationsRequestsAndViralChargeInLastConsultationDuringLastThreeMonths")
  public CohortDefinition
      findAllPatientsWhoHaveLaboratoryInvestigationsRequestsAndViralChargeInLastConsultationDuringLastThreeMonths() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findAllPatientsWhoHaveLaboratoryInvestigationsRequestsAndViralChargeInLastConsultationDuringLastThreeMonths");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MICategory15QueriesInterface.QUERY
            .findAllPatientsWhoHaveLaboratoryInvestigationsRequestsAndViralChargeInLastConsultationDuringLastThreeMonths;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodInARTMoreThan21Months_Denominator_15_3")
  public CohortDefinition
      findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodInARTMoreThan21Months_Denominator_15_3() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodInARTMoreThan21Months");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-2m+1d},endInclusionDate=${endRevisionDate-1m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "A",
        EptsReportUtils.map(
            this
                .findPatientsWithClinicalConsultationDuringRevisionPeriodAndAgeGreaterOrEqualTwoYears(),
            mappings));

    definition.addSearch(
        "J", EptsReportUtils.map(this.findPatientsWhoAreActiveOnArtAndInAtleastOneDSD(), mappings));

    definition.addSearch(
        "B2",
        EptsReportUtils.map(
            this.findPatientsWithClinicalConsultationAndARTStartDateGreaterThanTwentyOneMonths(),
            mappings));

    definition.addSearch(
        "P",
        EptsReportUtils.map(
            this
                .findAllPatientsWhoHaveLaboratoryInvestigationsRequestsAndViralChargeInLastConsultationDuringLastThreeMonths(),
            mappings));

    definition.setCompositionString("(A AND J AND B2) NOT P");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsRegisteredInOneMDSForStablePatientsAndHadCVBetweenTwelveAndEigtheenMonthsAfterCVLessThan1000")
  public CohortDefinition
      findPatientsRegisteredInOneMDSForStablePatientsAndHadCVBetweenTwelveAndEigtheenMonthsAfterCVLessThan1000() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsRegisteredInOneMDSForStablePatientsAndHadCVBetweenTwelveAndEigtheenMonthsAfterCVLessThan1000");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MICategory15QueriesInterface.QUERY
            .findPatientsRegisteredInOneMDSForStablePatientsAndHadCVBetweenTwelveAndEigtheenMonthsAfterCVLessThan1000;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsRegisteredInOneMDSForStablePatientsAndHadCVBetweenTwelveAndEigtheenMonthsAfterCVLessThan1000_Numerator_15_3")
  public CohortDefinition
      findPatientsRegisteredInOneMDSForStablePatientsAndHadCVBetweenTwelveAndEigtheenMonthsAfterCVLessThan1000_Numerator_15_3() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsRegisteredInOneMDSForStablePatientsAndHadCVBetweenTwelveAndEigtheenMonthsAfterCVLessThan1000");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    final String mappingsMI =
        "startInclusionDate=${endRevisionDate-2m+1d},endInclusionDate=${endRevisionDate-1m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR-15-3",
        EptsReportUtils.map(
            this
                .findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodInARTMoreThan21Months_Denominator_15_3(),
            mappingsMI));

    definition.addSearch(
        "I",
        EptsReportUtils.map(
            this
                .findPatientsRegisteredInOneMDSForStablePatientsAndHadCVBetweenTwelveAndEigtheenMonthsAfterCVLessThan1000(),
            mappings));

    definition.setCompositionString("(DENOMINATOR-15-3 AND I)");

    return definition;
  }
}
