package org.openmrs.module.eptsreports.reporting.library.cohorts.vlmi;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.vlmi.VLMIQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ViralLoadLMICohortQueries {

  @DocumentedDefinition(
      value =
          "findPatientsInFirstLineARTWithFirstVLAbove1000ExceptPregnantOrBreastfeedingInTheSameConsultationDuringEvaluationPeriod")
  private CohortDefinition
      findPatientsInFirstLineARTWithFirstVLAbove1000ExceptPregnantOrBreastfeedingInTheSameConsultationDuringEvaluationPeriod() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsInFirstLineARTWithFirstVLAbove1000ExceptPregnantOrBreastfeedingInTheSameConsultationDuringEvaluationPeriod");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        VLMIQueriesInterface.QUERY
            .findPatientsInFirstLineARTWithFirstVLAbove1000ExceptPregnantOrBreastfeedingInTheSameConsultationDuringEvaluationPeriod;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardVLFR4")
  private CohortDefinition
      findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardVLFR4() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardVLFR4");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        VLMIQueriesInterface.QUERY
            .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardVLFR4;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoTransferedOutVLFR5")
  private CohortDefinition findPatientsWhoTransferedOutVLFR5() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardVLFR4");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = VLMIQueriesInterface.QUERY.findPatientsWhoTransferedOutVLFR5;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(value = "findAllPatientWhoAreDeadByEndOfRevisonPeriodVLFR6")
  private CohortDefinition findAllPatientWhoAreDeadByEndOfRevisonPeriodVLFR6() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findAllPatientWhoAreDeadByEndOfRevisonPeriodVLFR6");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = VLMIQueriesInterface.QUERY.findAllPatientWhoAreDeadByEndOfRevisonPeriodVLFR6;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoArePregnantInclusionDateVLFR7")
  private CohortDefinition findPatientsWhoArePregnantInclusionDateVLFR7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoArePregnantInclusionDateVLFR7");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = VLMIQueriesInterface.QUERY.findPatientsWhoArePregnantInclusionDateVLFR7;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreBreastfeedingVLFR8")
  private CohortDefinition findPatientsWhoAreBreastfeedingVLFR8() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoAreBreastfeedingVLFR8");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = VLMIQueriesInterface.QUERY.findPatientsWhoAreBreastfeedingVLFR8;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithCVAbove1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultations")
  private CohortDefinition
      findPatientsWithCVAbove1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultations() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWithCVAbove1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultations");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        VLMIQueriesInterface.QUERY
            .findPatientsWithCVAbove1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultations;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoHaveVLLaboratoryRequestOnClinicalConsultationBetween80And130OfFirstVLDate")
  private CohortDefinition
      findPatientsWhoHaveVLLaboratoryRequestOnClinicalConsultationBetween80And130OfFirstVLDate() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoHaveVLLaboratoryRequestOnClinicalConsultationBetween80And130OfFirstVLDate");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        VLMIQueriesInterface.QUERY
            .findPatientsWhoHaveVLLaboratoryRequestOnClinicalConsultationBetween80And130OfFirstVLDate;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoHadRecordOfSecondViralLoadResultAbove1000InClinicalConsultationBetween110And160DaysOfFirstVL")
  private CohortDefinition
      findPatientsWhoHadRecordOfSecondViralLoadResultAbove1000InClinicalConsultationBetween110And160DaysOfFirstVL() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoHadRecordOfSecondViralLoadResultAbove1000InClinicalConsultationBetween110And160DaysOfFirstVL");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        VLMIQueriesInterface.QUERY
            .findPatientsWhoHadRecordOfSecondViralLoadResultAbove1000InClinicalConsultationBetween110And160DaysOfFirstVL;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhithRecordOfLastViralLoadResultAbove1000InFichaResumoBetween110And160DaysOfFirstVL")
  private CohortDefinition
      findPatientsWhithRecordOfLastViralLoadResultAbove1000InFichaResumoBetween110And160DaysOfFirstVL() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhithRecordOfLastViralLoadResultAbove1000InFichaResumoBetween110And160DaysOfFirstVL");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        VLMIQueriesInterface.QUERY
            .findPatientsWhithRecordOfLastViralLoadResultAbove1000InFichaResumoBetween110And160DaysOfFirstVL;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWithSecondVLQtQlInClinicalConsultationRegisteredBetween110And160OfFirstVL")
  private CohortDefinition
      findPatientsWithSecondVLQtQlInClinicalConsultationRegisteredBetween110And160OfFirstVL() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWithSecondVLQtQlInClinicalConsultationRegisteredBetween110And160OfFirstVL");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        VLMIQueriesInterface.QUERY
            .findPatientsWithSecondVLQtQlBelow1000InClinicalConsultationRegisteredBetween110And160OfFirstVL;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithLastVLBelow1000InFichaResumoRegisteredBetween110And160DaysOfFirstVL")
  private CohortDefinition
      findPatientsWithLastVLBelow1000InFichaResumoRegisteredBetween110And160DaysOfFirstVL() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWithLastVLBelow1000InFichaResumoRegisteredBetween110And160DaysOfFirstVL");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        VLMIQueriesInterface.QUERY
            .findPatientsWithLastVLBelow1000InFichaResumoRegisteredBetween110And160DaysOfFirstVL;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(value = "findPatientsInFirstLineARTInTheEvaluationPeriod")
  private CohortDefinition findPatientsInFirstLineARTInTheEvaluationPeriod() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsInFirstLineARTInTheEvaluationPeriod");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = VLMIQueriesInterface.QUERY.findPatientsInFirstLineARTInTheEvaluationPeriod;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsMarkedWithSecondLineFieldOnFichaFichaResumoWithTheMostRecentDate")
  private CohortDefinition
      findPatientsMarkedWithSecondLineFieldOnFichaFichaResumoWithTheMostRecentDate() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsMarkedWithSecondLineFieldOnFichaFichaResumoWithTheMostRecentDate");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        VLMIQueriesInterface.QUERY
            .findPatientsMarkedWithSecondLineFieldOnFichaFichaResumoWithTheMostRecentDate;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithViralLoadResultInFichaClinicaFichaResumoPregnantNotBreastFeeding")
  private CohortDefinition
      findPatientsWithViralLoadResultInFichaClinicaFichaResumoPregnantNotBreastFeeding() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWithViralLoadResultInFichaClinicaFichaResumoPregnantNotBreastFeeding");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        VLMIQueriesInterface.QUERY
            .findPatientsWithViralLoadResultInFichaClinicaFichaResumoPregnantNotBreastFeeding;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22Denominator")
  public CohortDefinition
      findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22Denominator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "FIRST-LINE",
        EptsReportUtils.map(
            this
                .findPatientsInFirstLineARTWithFirstVLAbove1000InSevenMonthsDuringEvaluationPeriod_FR22_2(),
            mappings));

    definition.addSearch(
        "APSS",
        EptsReportUtils.map(
            this.findPatientsWithCVAbove1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultations(),
            mappings));

    definition.addSearch(
        "LABORATORY-REQUEST",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveVLLaboratoryRequestOnClinicalConsultationBetween80And130OfFirstVLDate(),
            mappings));

    definition.setCompositionString("FIRST-LINE AND APSS AND LABORATORY-REQUEST");

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22_1Numerator")
  public CohortDefinition
      findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22_1Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22_1Numerator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR-FR22",
        EptsReportUtils.map(
            this.findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22Denominator(),
            mappings));

    definition.addSearch(
        "VL-FICHACLINICA",
        EptsReportUtils.map(
            this
                .findPatientsWhoHadRecordOfSecondViralLoadResultAbove1000InClinicalConsultationBetween110And160DaysOfFirstVL(),
            mappings));

    definition.addSearch(
        "VL-FICHARESUMO",
        EptsReportUtils.map(
            this
                .findPatientsWhithRecordOfLastViralLoadResultAbove1000InFichaResumoBetween110And160DaysOfFirstVL(),
            mappings));

    definition.setCompositionString("DENOMINATOR-FR22 AND VL-FICHACLINICA AND VL-FICHARESUMO");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsInFirstLineARTWithFirstVLAbove1000InSevenMonthsDuringEvaluationPeriod_FR22_2")
  public CohortDefinition
      findPatientsInFirstLineARTWithFirstVLAbove1000InSevenMonthsDuringEvaluationPeriod_FR22_2() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsInFirstLineARTWithFirstVLAbove1000InSevenMonthsDuringEvaluationPeriod_FR22_2");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "FIRST-LINE",
        EptsReportUtils.map(
            this
                .findPatientsInFirstLineARTWithFirstVLAbove1000ExceptPregnantOrBreastfeedingInTheSameConsultationDuringEvaluationPeriod(),
            mappings));

    definition.addSearch(
        "TRANSFERRED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardVLFR4(),
            mappings));

    definition.addSearch(
        "TRANSFERRED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutVLFR5(), mappings));

    definition.addSearch(
        "DEAD",
        EptsReportUtils.map(this.findAllPatientWhoAreDeadByEndOfRevisonPeriodVLFR6(), mappings));

    definition.setCompositionString("FIRST-LINE NOT (TRANSFERRED-IN OR TRANSFERRED-OUT OR DEAD)");

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23Denominator")
  public CohortDefinition
      findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23Denominator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "FIRST-LINE-APSS",
        EptsReportUtils.map(
            this.findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22Denominator(),
            mappings));

    definition.setCompositionString("FIRST-LINE-APSS");

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23_1Numerator")
  public CohortDefinition
      findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23_1Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR231Numerator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR-VLFR23",
        EptsReportUtils.map(
            this.findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23Denominator(),
            mappings));

    definition.addSearch(
        "SECONDVL-FICHACLINICA",
        EptsReportUtils.map(
            this
                .findPatientsWithSecondVLQtQlInClinicalConsultationRegisteredBetween110And160OfFirstVL(),
            mappings));

    definition.addSearch(
        "SECONDVL-FICHARESUMO",
        EptsReportUtils.map(
            this
                .findPatientsWithLastVLBelow1000InFichaResumoRegisteredBetween110And160DaysOfFirstVL(),
            mappings));

    definition.setCompositionString(
        "DENOMINATOR-VLFR23 AND SECONDVL-FICHACLINICA AND SECONDVL-FICHARESUMO");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24Denominator")
  public CohortDefinition
      findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24Denominator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-12m+1d},endInclusionDate=${endRevisionDate-11m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "VL-FR24-2",
        EptsReportUtils.map(
            this
                .findPatientsOnFirstLineARTRegimenWhoReceived3APSSAfterFirstVLAbove1000NineMonthsAgoWithRequestSecondVL_VL24_2(),
            mappings));

    definition.addSearch(
        "SECONDVL-FICHACLINICA",
        EptsReportUtils.map(
            this
                .findPatientsWhoHadRecordOfSecondViralLoadResultAbove1000InClinicalConsultationBetween110And160DaysOfFirstVL(),
            mappings));

    definition.addSearch(
        "SECONDVL-FICHARESUMO",
        EptsReportUtils.map(
            this
                .findPatientsWhithRecordOfLastViralLoadResultAbove1000InFichaResumoBetween110And160DaysOfFirstVL(),
            mappings));

    definition.setCompositionString("VL-FR24-2 AND SECONDVL-FICHACLINICA AND SECONDVL-FICHARESUMO");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24_1Numerator")
  public CohortDefinition
      findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24_1Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24_1Numerator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-12m+1d},endInclusionDate=${endRevisionDate-11m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR-VLFR24",
        EptsReportUtils.map(
            this
                .findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24Denominator(),
            mappings));

    definition.addSearch(
        "SECOND-LINE",
        EptsReportUtils.map(
            this.findPatientsMarkedWithSecondLineFieldOnFichaFichaResumoWithTheMostRecentDate(),
            mappings));

    definition.setCompositionString("DENOMINATOR-VLFR24 AND SECOND-LINE");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsOnFirstLineARTRegimenWhoReceived3APSSAfterFirstVLAbove1000NineMonthsAgoWithRequestSecondVL_VL24_2")
  public CohortDefinition
      findPatientsOnFirstLineARTRegimenWhoReceived3APSSAfterFirstVLAbove1000NineMonthsAgoWithRequestSecondVL_VL24_2() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsOnFirstLineARTRegimenWhoReceived3APSSAfterFirstVLAbove1000NineMonthsAgoWithRequestSecondVL_VL24_2");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-12m+1d},endInclusionDate=${endRevisionDate-11m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "VL-FR24-3",
        EptsReportUtils.map(
            this.findPatientsInFirstLineARTWithVLAbove1000NineMonthsBackEvaluationPeriodVL_FR24_3(),
            mappings));

    definition.addSearch(
        "APSS",
        EptsReportUtils.map(
            this.findPatientsWithCVAbove1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultations(),
            mappings));

    definition.addSearch(
        "LABORATORY-REQUEST",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveVLLaboratoryRequestOnClinicalConsultationBetween80And130OfFirstVLDate(),
            mappings));

    definition.setCompositionString("VL-FR24-3 AND APSS AND LABORATORY-REQUEST");

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsInFirstLineARTWithVLAbove1000NineMonthsBackEvaluationPeriodVL_FR24_3")
  public CohortDefinition
      findPatientsInFirstLineARTWithVLAbove1000NineMonthsBackEvaluationPeriodVL_FR24_3() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPatientsInFirstLineARTWithVLAbove1000NineMonthsBackEvaluationPeriodVL_FR24_3");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-12m+1d},endInclusionDate=${endRevisionDate-11m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "FIRST-LINE-ART",
        EptsReportUtils.map(
            this
                .findPatientsInFirstLineARTWithFirstVLAbove1000ExceptPregnantOrBreastfeedingInTheSameConsultationDuringEvaluationPeriod(),
            mappings));

    definition.addSearch(
        "TRANSFERRED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardVLFR4(),
            mappings));

    definition.addSearch(
        "TRANSFERRED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutVLFR5(), mappings));

    definition.addSearch(
        "DEAD",
        EptsReportUtils.map(this.findAllPatientWhoAreDeadByEndOfRevisonPeriodVLFR6(), mappings));

    definition.setCompositionString(
        "FIRST-LINE-ART NOT (TRANSFERRED-IN OR TRANSFERRED-OUT OR DEAD)");

    return definition;
  }

  @DocumentedDefinition(
      value = "findPregnantWomenWithSecondHighViralLoadResultAfterAPSSSessionsVL_FR25Denominator")
  public CohortDefinition
      findPregnantWomenWithSecondHighViralLoadResultAfterAPSSSessionsVL_FR25Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantWomenWithSecondHighViralLoadResultAfterAPSSSessionsVL_FR25Denominator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "VL-FR25-2",
        EptsReportUtils.map(
            this.findPregnantWomenOnTHeFirstLineARTWithVLResultAbove1000_VL_25_2(), mappings));

    definition.addSearch(
        "APSS",
        EptsReportUtils.map(
            this.findPatientsWithCVAbove1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultations(),
            mappings));

    definition.addSearch(
        "LABORATORY-REQUEST",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveVLLaboratoryRequestOnClinicalConsultationBetween80And130OfFirstVLDate(),
            mappings));

    definition.setCompositionString("VL-FR25-2 AND APSS AND LABORATORY-REQUEST");

    return definition;
  }

  @DocumentedDefinition(
      value = "findPregnantWomenWithSecondHighViralLoadResultAfterAPSSSessionsVL_FR25_1_Numerator")
  public CohortDefinition
      findPregnantWomenWithSecondHighViralLoadResultAfterAPSSSessionsVL_FR25_1_Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantWomenWithSecondHighViralLoadResultAfterAPSSSessionsVL_FR25_1_Numerator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "VL-FR25",
        EptsReportUtils.map(
            this
                .findPregnantWomenWithSecondHighViralLoadResultAfterAPSSSessionsVL_FR25Denominator(),
            mappings));

    definition.addSearch(
        "SECONDVL-FICHACLINICA",
        EptsReportUtils.map(
            this
                .findPatientsWhoHadRecordOfSecondViralLoadResultAbove1000InClinicalConsultationBetween110And160DaysOfFirstVL(),
            mappings));

    definition.addSearch(
        "SECONDVL-FICHARESUMO",
        EptsReportUtils.map(
            this
                .findPatientsWhithRecordOfLastViralLoadResultAbove1000InFichaResumoBetween110And160DaysOfFirstVL(),
            mappings));

    definition.setCompositionString("VL-FR25 AND SECONDVL-FICHACLINICA AND SECONDVL-FICHARESUMO");

    return definition;
  }

  @DocumentedDefinition(value = "findPregnantWomenOnTHeFirstLineARTWithVLResultAbove1000_VL_25_2")
  public CohortDefinition findPregnantWomenOnTHeFirstLineARTWithVLResultAbove1000_VL_25_2() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPregnantWomenOnTHeFirstLineARTWithVLResultAbove1000_VL_25_2");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT-NOT-BREASTFEEDING",
        EptsReportUtils.map(
            this.findPatientsWithViralLoadResultInFichaClinicaFichaResumoPregnantNotBreastFeeding(),
            mappings));

    definition.addSearch(
        "FIRST-LINE-ART",
        EptsReportUtils.map(this.findPatientsInFirstLineARTInTheEvaluationPeriod(), mappings));

    definition.addSearch(
        "TRANSFERRED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardVLFR4(),
            mappings));

    definition.addSearch(
        "TRANSFERRED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutVLFR5(), mappings));

    definition.addSearch(
        "DEAD",
        EptsReportUtils.map(this.findAllPatientWhoAreDeadByEndOfRevisonPeriodVLFR6(), mappings));

    definition.setCompositionString(
        "(PREGNANT-NOT-BREASTFEEDING AND FIRST-LINE-ART) NOT (TRANSFERRED-IN OR TRANSFERRED-OUT OR DEAD)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWomenWithLowSecondHighViralLoadResultAfterAPSSSessionsVL_FR26_Denominator")
  public CohortDefinition
      findPregnantWomenWithLowSecondHighViralLoadResultAfterAPSSSessionsVL_FR26_Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantWomenWithLowSecondHighViralLoadResultAfterAPSSSessionsVL_FR26_Denominator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "VL-FR25",
        EptsReportUtils.map(
            this
                .findPregnantWomenWithSecondHighViralLoadResultAfterAPSSSessionsVL_FR25Denominator(),
            mappings));

    definition.setCompositionString("VL-FR25");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWomenWithLowSecondHighViralLoadResultAfterAPSSSessionsVL_FR26_1_Numerator")
  public CohortDefinition
      findPregnantWomenWithLowSecondHighViralLoadResultAfterAPSSSessionsVL_FR26_1_Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantWomenWithLowSecondHighViralLoadResultAfterAPSSSessionsVL_FR26_1_Numerator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR-VL-FR26",
        EptsReportUtils.map(
            this
                .findPregnantWomenWithSecondHighViralLoadResultAfterAPSSSessionsVL_FR25Denominator(),
            mappings));

    definition.addSearch(
        "SECONDVL-FICHACLINICA",
        EptsReportUtils.map(
            this
                .findPatientsWithSecondVLQtQlInClinicalConsultationRegisteredBetween110And160OfFirstVL(),
            mappings));

    definition.addSearch(
        "SECONDVL-FICHARESUMO",
        EptsReportUtils.map(
            this
                .findPatientsWithLastVLBelow1000InFichaResumoRegisteredBetween110And160DaysOfFirstVL(),
            mappings));

    definition.setCompositionString(
        "DENOMINATOR-VL-FR26 AND SECONDVL-FICHACLINICA AND SECONDVL-FICHARESUMO");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWomenWithSwitchToSecondLineARTAfterSecondHighViralLoadResult_VL_FR27Denominator")
  public CohortDefinition
      findPregnantWomenWithSwitchToSecondLineARTAfterSecondHighViralLoadResult_VL_FR27Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantWomenWithSwitchToSecondLineARTAfterSecondHighViralLoadResult_VL_FR27Denominator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-12m+1d},endInclusionDate=${endRevisionDate-11m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "VL-FR27-2",
        EptsReportUtils.map(
            this
                .findPregnantWomenInFirstLineARTRegimenWithAPSSAfterVLResultAbove1000NineMonthsBack_VL27_2(),
            mappings));

    definition.addSearch(
        "SECONDVL-FICHACLINICA",
        EptsReportUtils.map(
            this
                .findPatientsWhoHadRecordOfSecondViralLoadResultAbove1000InClinicalConsultationBetween110And160DaysOfFirstVL(),
            mappings));

    definition.addSearch(
        "SECONDVL-FICHARESUMO",
        EptsReportUtils.map(
            this
                .findPatientsWhithRecordOfLastViralLoadResultAbove1000InFichaResumoBetween110And160DaysOfFirstVL(),
            mappings));

    definition.setCompositionString("VL-FR27-2 AND SECONDVL-FICHACLINICA AND SECONDVL-FICHARESUMO");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWomenWithSwitchToSecondLineARTAfterSecondHighViralLoadResult_VL_FR27_1_Numerator")
  public CohortDefinition
      findPregnantWomenWithSwitchToSecondLineARTAfterSecondHighViralLoadResult_VL_FR27_1_Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantWomenWithSwitchToSecondLineARTAfterSecondHighViralLoadResult_VL_1_FR27_Numerator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-12m+1d},endInclusionDate=${endRevisionDate-11m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "VL-FR27",
        EptsReportUtils.map(
            this
                .findPregnantWomenWithSwitchToSecondLineARTAfterSecondHighViralLoadResult_VL_FR27Denominator(),
            mappings));

    definition.addSearch(
        "SEGUNDA-LINHA",
        EptsReportUtils.map(
            this.findPatientsMarkedWithSecondLineFieldOnFichaFichaResumoWithTheMostRecentDate(),
            mappings));

    definition.setCompositionString("VL-FR27 AND SEGUNDA-LINHA");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWomenInFirstLineARTRegimenWithAPSSAfterVLResultAbove1000NineMonthsBack_VL27_2")
  public CohortDefinition
      findPregnantWomenInFirstLineARTRegimenWithAPSSAfterVLResultAbove1000NineMonthsBack_VL27_2() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantWomenInFirstLineARTRegimenWithAPSSAfterVLResultAbove1000NineMonthsBack_VL27_2");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-12m+1d},endInclusionDate=${endRevisionDate-11m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "VL-FR27-3",
        EptsReportUtils.map(
            this.findPregnantWomenInTHeFirstLineARTWithVLResultAbove1000_NineMonthsBackVL_27_3(),
            mappings));

    definition.addSearch(
        "APSS",
        EptsReportUtils.map(
            this.findPatientsWithCVAbove1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultations(),
            mappings));

    definition.addSearch(
        "LABORATORY-REQUEST",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveVLLaboratoryRequestOnClinicalConsultationBetween80And130OfFirstVLDate(),
            mappings));

    definition.setCompositionString("VL-FR27-3 AND APSS AND LABORATORY-REQUEST");

    return definition;
  }

  @DocumentedDefinition(
      value = "findPregnantWomenInTHeFirstLineARTWithVLResultAbove1000_NineMonthsBackVL_27_3")
  public CohortDefinition
      findPregnantWomenInTHeFirstLineARTWithVLResultAbove1000_NineMonthsBackVL_27_3() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantWomenInTHeFirstLineARTWithVLResultAbove1000_NineMonthsBackVL_27_3");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-12m+1d},endInclusionDate=${endRevisionDate-11m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT-NOT-BREASTFEEDING",
        EptsReportUtils.map(
            this.findPatientsWithViralLoadResultInFichaClinicaFichaResumoPregnantNotBreastFeeding(),
            mappings));

    definition.addSearch(
        "FIRST-LINE-ART",
        EptsReportUtils.map(this.findPatientsInFirstLineARTInTheEvaluationPeriod(), mappings));

    definition.addSearch(
        "TRANSFERRED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardVLFR4(),
            mappings));

    definition.addSearch(
        "TRANSFERRED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutVLFR5(), mappings));

    definition.addSearch(
        "DEAD",
        EptsReportUtils.map(this.findAllPatientWhoAreDeadByEndOfRevisonPeriodVLFR6(), mappings));

    definition.setCompositionString(
        "(PREGNANT-NOT-BREASTFEEDING AND FIRST-LINE-ART) NOT (TRANSFERRED-IN OR TRANSFERRED-OUT OR DEAD)");

    return definition;
  }
}
