package org.openmrs.module.eptsreports.reporting.library.cohorts.mq;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.quality.improvment.calculation.QualityImprovmentCategory11SectionICalculation;
import org.openmrs.module.eptsreports.reporting.library.queries.ResumoMensalQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQCategory11P2QueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQCategory12QueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQCategory13P2QueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQCategory13P4QueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class MQCohortQueries {

  @DocumentedDefinition(value = "findPatientsWhoAreNewlyEnrolledOnARTRF05")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTRF05() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientsWhoAreNewlyEnrolledOnARTRF05;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06")
  public CohortDefinition
      findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoTransferedOutRF07")
  public CohortDefinition findPatientsWhoTransferedOutRF07() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoTransferedOutRF07");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientsWhoTransferedOutRF07;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoArePregnantInclusionDateRF08")
  public CohortDefinition findPatientsWhoArePregnantInclusionDateRF08() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoArePregnantInclusionDateRF08");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientsWhoArePregnantInclusionDateRF08;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreBreastfeedingInclusionDateRF09")
  public CohortDefinition findPatientsWhoAreBreastfeedingInclusionDateRF09() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoAreBreastfeedingInclusionDateRF09");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientsWhoAreBreastfeedingInclusionDateRF09;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoHasNutritionalAssessmentInLastConsultation")
  public CohortDefinition findPatientsWhoHasNutritionalAssessmentInLastConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoHasNutritionalAssessmentInLastConsultation");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY.findPatientsWhoHasNutritionalAssessmentInLastConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriod")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriod() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriod");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriod;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriod")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriod() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriod");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriod;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoHasNutritionalAssessmentDAMandDAGInLastConsultation")
  public CohortDefinition findPatientsWhoHasNutritionalAssessmentDAMandDAGInLastConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoHasNutritionalAssessmentDAMandDAGInLastConsultation");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY.findPatientsWhoHasNutritionalAssessmentDAMandDAGInLastConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoHasNutritionalAssessmentDAMandDAGAndATPUInLastConsultation")
  public CohortDefinition
      findPatientsWhoHasNutritionalAssessmentDAMandDAGAndATPUInLastConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoHasNutritionalAssessmentDAMandDAGAndATPUInLastConsultation");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findPatientsWhoHasNutritionalAssessmentDAMandDAGAndATPUInLastConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6")
  public CohortDefinition
      findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6")
  public CohortDefinition findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6;
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7")
  public CohortDefinition findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY.findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWithPositiveTBScreeningInDurindPeriodCategory7")
  public CohortDefinition findPatientsWithPositiveTBScreeningInDurindPeriodCategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithPositiveTBScreeningInDurindPeriodCategory7");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY.findPatientsWithPositiveTBScreeningInDurindPeriodCategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "finPatientHaveTBTreatmentDuringPeriodCategory7")
  public CohortDefinition finPatientHaveTBTreatmentDuringPeriodCategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("finPatientHaveTBTreatmentDuringPeriodCategory7");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.finPatientHaveTBTreatmentDuringPeriodCategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWhoStartTPIDuringPeriodCategory7")
  public CohortDefinition findPatientWhoStartTPIDuringPeriodCategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientWhoStartTPIDuringPeriodCategory7");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientWhoStartTPIDuringPeriodCategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7")
  public CohortDefinition findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY.findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWhoCompleteTPICategory7")
  public CohortDefinition findPatientWhoCompleteTPICategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientWhoCompleteTPICategory7");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientWhoCompleteTPICategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultsOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11NumeratorAdult")
  public CohortDefinition
      findPatientsOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11Numerator() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findAdultsOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11NumeratorAdult");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findPatientsOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11Numerator;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12A")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12A() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12A");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory12QueriesInterface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12A;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1")
  public CohortDefinition
      findPatientsWhoAreInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory12QueriesInterface.QUERY
            .findPatientsWhoAreInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNotInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1E")
  public CohortDefinition
      findPatientsWhoAreNotInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1E() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreNotInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1E");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory12QueriesInterface.QUERY
            .findPatientsWhoAreNotInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1E;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B2")
  public CohortDefinition
      findPatientsWhoAreInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B2() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory12QueriesInterface.QUERY
            .findPatientsWhoAreInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B2;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNotInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateB2E")
  public CohortDefinition
      findPatientsWhoAreNotInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateB2E() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreNotInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateB2E");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory12QueriesInterface.QUERY
            .findPatientsWhoAreNotInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateB2E;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12")
  public CohortDefinition
      findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdult");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory12QueriesInterface.QUERY
            .findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12")
  public CohortDefinition
      findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdult");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory12QueriesInterface.QUERY
            .findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWithCVOver1000CopiesCategory11B2")
  public CohortDefinition findPatientWithCVOver1000CopiesCategory11B2() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findAdultWithCVOver1000CopiesCategory11B2");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientWithCVOver1000CopiesCategory11B2;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoHaveLastFirstLineTerapeutic")
  public CohortDefinition findPatientsWhoHaveLastFirstLineTerapeutic() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoHaveLastFirstLineTerapeutic");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientsWhoHaveLastFirstLineTerapeutic;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdultH")
  public CohortDefinition
      findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11Numerator() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdultH");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        MQQueriesInterface.QUERY
            .findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11Numerator;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11NumeratorChildren")
  public CohortDefinition
      findChildrenOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11Numerator() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdult");

    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findPatientsOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11Numerator;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13")
  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13");

    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = ResumoMensalQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13();

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoHasCVBiggerThan1000AndMarkedAsPregnantInTheSameClinicalConsultation")
  public CohortDefinition
      findPatientsWhoHasCVBiggerThan1000AndMarkedAsPregnantInTheSameClinicalConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoHasCVBiggerThan1000AndMarkedAsPregnantInTheSameClinicalConsultation");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory11P2QueriesInterface.QUERY
            .findPatientsWhoHasCVBiggerThan1000AndMarkedAsPregnantInTheSameClinicalConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoHasCVBiggerThan1000AndMarkedAsBreastFeedingInTheSameClinicalConsultation")
  public CohortDefinition
      findPatientsWhoHasCVBiggerThan1000AndMarkedAsBreastFeedingInTheSameClinicalConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoHasCVBiggerThan1000AndMarkedAsBreastFeedingInTheSameClinicalConsultation");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory11P2QueriesInterface.QUERY
            .findPatientsWhoHasCVBiggerThan1000AndMarkedAsBreastFeedingInTheSameClinicalConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "childrenPatientsWithAtLeast9APSSConsultations")
  public CohortDefinition findChildrenPatientsWithAtLeast9APSSConsultationByInclusionPeriod() {

    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "childrenPatientsWithAtLeast9APSSConsultations",
            Context.getRegisteredComponents(QualityImprovmentCategory11SectionICalculation.class)
                .get(0));
    cd.setName("findChildrenPatientsWithAtLeast9APSSConsultationByInclusionPeriod");

    cd.setName("childrenPatientsWithAtLeast9APSSConsultations");

    cd.addParameter(
        new Parameter(
            EptsReportConstants.START_INCULSION_DATE, "Start Inclusion Date", Date.class));
    cd.addParameter(
        new Parameter(EptsReportConstants.END_INCLUSION_DATE, "End Inclusion Date", Date.class));
    cd.addParameter(
        new Parameter(EptsReportConstants.END_REVISION_DATE, "End Revision Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreRequestForLaboratoryInvestigationAndPregnantInclusionPeriodCAT13DenumeratorP2ByB4")
  public CohortDefinition
      findPatientsWhoAreRequestForLaboratoryInvestigationAndPregnantInclusionPeriodCAT13DenumeratorP2ByB4() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreRequestForLaboratoryInvestigationAndPregnantInclusionPeriodCAT13DenumeratorP2ByB4");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P2QueriesInterface.QUERY
            .findPatientsWhoAreRequestForLaboratoryInvestigationAndPregnantInclusionPeriodByB4;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreRequestForLaboratoryInvestigationsInclusionPeriodCAT13DenumeratorP2ByB3")
  public CohortDefinition
      findPatientsWhoAreRequestForLaboratoryInvestigationsInclusionPeriodCAT13DenumeratorP2ByB3() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreRequestForLaboratoryInvestigationsInclusionPeriodCAT13DenumeratorP2ByB3");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P2QueriesInterface.QUERY
            .findPatientsWhoAreRequestForLaboratoryInvestigationsInclusionPeriodByB3;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoArePregnantInclusionPeriodCAT13DenumeratorP2ByB1")
  public CohortDefinition findPatientsWhoArePregnantInclusionPeriodCAT13DenumeratorP2ByB1() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoArePregnantInclusionPeriodCAT13DenumeratorP2ByB1");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P2QueriesInterface.QUERY.findPatientsWhoArePregnantInclusionPeriodByB1;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoArePregnantInFirstConsultationInclusionPeriodByB2")
  public CohortDefinition findPatientsWhoArePregnantInFirstConsultationInclusionPeriodByB2() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoArePregnantInFirstConsultationInclusionPeriodByB2");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P2QueriesInterface.QUERY
            .findPatientsWhoArePregnantInFirstConsultationInclusionPeriodByB2;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoHaveCVResultAfter33DaysOfRequestByK")
  public CohortDefinition findPatientsWhoHaveCVResultAfter33DaysOfRequestByK() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoHaveCVResultAfter33DaysOfRequestByK");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P2QueriesInterface.QUERY.findPatientsWhoHaveCVResultAfter33DaysOfRequestByK;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoHaveCVResultAfeter33DaysOfRequestForPregnantWishRequestedCVByL")
  public CohortDefinition
      findPatientsWhoHaveCVResultAfeter33DaysOfRequestForPregnantWishRequestedCVByL() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoHaveCVResultAfeter33DaysOfRequestForPregnantWishRequestedCVByL");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P2QueriesInterface.QUERY
            .findPatientsWhoHaveCVResultAfeter33DaysOfRequestForPregnantWishRequestedCVByL;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoHaveLastTerapeutcLineByQueryB1")
  public CohortDefinition findPatientsWhoHaveLastTerapeutcLineByQueryB1() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoHaveLastTerapeutcLineByQueryB1");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P4QueriesInterface.QUERY.findPatientsWhoHaveLastTerapeutcLineByQueryB1;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhohaveCVMoreThan1000CopiesByQueryB2")
  public CohortDefinition findPatientsWhohaveCVMoreThan1000CopiesByQueryB2() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhohaveCVMoreThan1000CopiesByQueryB2");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P4QueriesInterface.QUERY.findPatientsWhohaveCVMoreThan1000CopiesByQueryB2;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoHave3APSSPPConsultationInSameDayOfCVByQueryG")
  public CohortDefinition findPatientsWhoHave3APSSPPConsultationInSameDayOfCVByQueryG() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoHave3APSSPPConsultationInSameDayOfCVByQueryG");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P4QueriesInterface.QUERY
            .findPatientsWhoHave3APSSPPConsultationInSameDayOfCVByQueryG;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoHaveRequestedCV120DaysAfterCVResultByQueryH")
  public CohortDefinition findPatientsWhoHaveRequestedCV120DaysAfterCVResultByQueryH() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoHaveRequestedCV120DaysAfterCVResultByQueryH");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P4QueriesInterface.QUERY
            .findPatientsWhoHaveRequestedCV120DaysAfterCVResultByQueryH;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhithCD4RegistredInClinicalConsultationUnder33DaysFromTheFirstClinicalConsultation")
  public CohortDefinition
      findPatientsWhithCD4RegistredInClinicalConsultationUnder33DaysFromTheFirstClinicalConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhithCD4RegistredInClinicalConsultationUnder33DaysFromTheFirstClinicalConsultation");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findPatientsWhithCD4RegistredInClinicalConsultationUnder33DaysFromTheFirstClinicalConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientHaveTBACTIVEAndTPIDuringPeriodCategory7AsH")
  public CohortDefinition findPatientHaveTBACTIVEAndTPIDuringPeriodCategory7AsH() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientHaveTBACTIVEAndTPIDuringPeriodCategory7AsH");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientHaveTBACTIVEAndTPIDuringPeriodCategory7AsH;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientHaveTBSCREENINGAndTPIDuringPeriodCategory7AsI")
  public CohortDefinition findPatientHaveTBSCREENINGAndTPIDuringPeriodCategory7AsI() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientHaveTBSCREENINGAndTPIDuringPeriodCategory7AsI");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY.findPatientHaveTBSCREENINGAndTPIDuringPeriodCategory7AsI;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientHaveTBTREATMENTAndTPIDuringPeriodCategory7AsJ")
  public CohortDefinition findPatientHaveTBTREATMENTAndTPIDuringPeriodCategory7AsJ() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientHaveTBTREATMENTAndTPIDuringPeriodCategory7AsJ");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY.findPatientHaveTBTREATMENTAndTPIDuringPeriodCategory7AsJ;

    definition.setQuery(query);

    return definition;
  }
}
