package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovementQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovementQueriesInterfaceCategory12;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
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

    String query =
        QualityImprovementQueriesInterface.QUERY.findPatientsWhoAreNewlyEnrolledOnARTRF05;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06")
  public CohortDefinition
      findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoTransferedOutRF07")
  public CohortDefinition findPatientsWhoTransferedOutRF07() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = QualityImprovementQueriesInterface.QUERY.findPatientsWhoTransferedOutRF07;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoArePregnantInclusionDateRF08")
  public CohortDefinition findPatientsWhoArePregnantInclusionDateRF08() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY.findPatientsWhoArePregnantInclusionDateRF08;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreBreastfeedingInclusionDateRF09")
  public CohortDefinition findPatientsWhoAreBreastfeedingInclusionDateRF09() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY.findPatientsWhoAreBreastfeedingInclusionDateRF09;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenBiggerThen2eLess14")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenBiggerThen2eLess14() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenBiggerThen2eLess14;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenWhit9Months")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTChildrenWhit9Months() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAChildrenWhit9Months;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenBiggerThen1neLess14")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenBiggerThen1neLess14() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenBiggerThen1neLess14;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingMonth")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingMonth() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingMonth;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoHasNutritionalAssessmentInLastConsultation")
  public CohortDefinition findPatientsWhoHasNutritionalAssessmentInLastConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoHasNutritionalAssessmentInLastConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriod")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriod() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
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

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriod;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoHasNutritionalAssessmentDAMandDAGInLastConsultation")
  public CohortDefinition findPatientsWhoHasNutritionalAssessmentDAMandDAGInLastConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoHasNutritionalAssessmentDAMandDAGInLastConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoHasNutritionalAssessmentDAMandDAGAndATPUInLastConsultation")
  public CohortDefinition
      findPatientsWhoHasNutritionalAssessmentDAMandDAGAndATPUInLastConsultation() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoHasNutritionalAssessmentDAMandDAGAndATPUInLastConsultation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6")
  public CohortDefinition
      findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6")
  public CohortDefinition findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6;
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7")
  public CohortDefinition findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWithPositiveTBScreeningInDurindPeriodCategory7")
  public CohortDefinition findPatientsWithPositiveTBScreeningInDurindPeriodCategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWithPositiveTBScreeningInDurindPeriodCategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "finPatientHaveTBTreatmentDuringPeriodCategory7")
  public CohortDefinition finPatientHaveTBTreatmentDuringPeriodCategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY.finPatientHaveTBTreatmentDuringPeriodCategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWhoStartTPIDuringPeriodCategory7")
  public CohortDefinition findPatientWhoStartTPIDuringPeriodCategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY.findPatientWhoStartTPIDuringPeriodCategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7")
  public CohortDefinition findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWhoCompleteTPICategory7")
  public CohortDefinition findPatientWhoCompleteTPICategory7() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = QualityImprovementQueriesInterface.QUERY.findPatientWhoCompleteTPICategory7;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultsOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11NumeratorAdult")
  public CohortDefinition
      findAdultsOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11NumeratorAdult() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findAdultsOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11NumeratorAdult;

    definition.setQuery(query);

    return definition;
  }

  //  Aqui Se encontram as Definitions da CATEGORIA 12

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
        QualityImprovementQueriesInterfaceCategory12.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12A;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12AdultA")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12AdultA() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12AdultA");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory12.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12AdultA;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12ChildrenA")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12ChildrenA() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12ChildrenA");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory12.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12ChildrenA;

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
        QualityImprovementQueriesInterfaceCategory12.QUERY
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
        QualityImprovementQueriesInterfaceCategory12.QUERY
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
        QualityImprovementQueriesInterfaceCategory12.QUERY
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
        QualityImprovementQueriesInterfaceCategory12.QUERY
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
        QualityImprovementQueriesInterfaceCategory12.QUERY
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
        QualityImprovementQueriesInterfaceCategory12.QUERY
            .findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findAdultWithCVOver1000CopiesCategory11B2")
  public CohortDefinition findAdultWithCVOver1000CopiesCategory11B2() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("findPatientsWithCVOver1000CopiesCategory11DenominatorAdult");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        QualityImprovementQueriesInterface.QUERY.findAdultWithCVOver1000CopiesCategory11B2;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(value = "findChildrenWithCVOver1000CopiesCategory11B2")
  public CohortDefinition findChildrenWithCVOver1000CopiesCategory11B2() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("findChildrenWithCVOver1000CopiesCategory11B2");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        QualityImprovementQueriesInterface.QUERY.findChildrenWithCVOver1000CopiesCategory11B2;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoHaveLastFirstLineTerapeuticChildrenB1")
  public CohortDefinition findPatientsWhoHaveLastFirstLineTerapeutic() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("findPatientsWhoHaveLastFirstLineTerapeuticChildrenB1");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        QualityImprovementQueriesInterface.QUERY.findPatientsWhoHaveLastFirstLineTerapeutic;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdultH")
  public CohortDefinition
      findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdultH() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName(
        "findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdultH");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdultH;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorChildrenH")
  public CohortDefinition
      findChildrenOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorChildrenH() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName(
        "findChildrenOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorChildrenH");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        QualityImprovementQueriesInterface.QUERY
            .findChildrenOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorChildrenH;
    definition.setQuery(query);
    return definition;
  }

  /*Os Numeradores e Denominadores comecam a ser colectados apartir Daqui
   */
  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultCategory3RF11Denominator")
  public CohortDefinition
      findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultCategory3RF11Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("ADULT NOT TRANSFERED IN");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-ADULT",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.setCompositionString("START-ART-ADULT NOT (TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3RF12Numerator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3RF12Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("First Consult IN Inclusion Period");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF11-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultCategory3RF11Denominator(),
            mappings));

    definition.addSearch(
        "RF12-NUMERATOR",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator(),
            mappings));

    definition.setCompositionString("RF11-DENOMINATOR AND RF12-NUMERATOR");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInChildrenCategory3RF13Denominator")
  public CohortDefinition
      findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInChildrenCategory3RF13Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF13");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation(),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.setCompositionString("START-ART-CHILDREN NOT (TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildrenAndHaveFirstConsultInclusionPeriodCategory3RF14Numerator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildrenAndHaveFirstConsultInclusionPeriodCategory3RF14Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("First Consult IN Inclusion Period");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF13-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInChildrenCategory3RF13Denominator(),
            mappings));

    definition.addSearch(
        "RF14-NUMERATOR",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator(),
            mappings));

    definition.setCompositionString("RF13-DENOMINATOR AND RF14-NUMERATOR");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledByAgeChildrenDesagragationInAPeriodCategory4RF15Denominator")
  public CohortDefinition
      findPatientsNewlyEnrolledByAgeChildrenDesagragationInAPeriodCategory4RF15Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF15");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation(),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.setCompositionString("START-ART-CHILDREN NOT (TRANSFERED-IN OR PREGNANT)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodCategory4RF16Numerator")
  public CohortDefinition
      findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodCategory4RF16Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF16");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF15-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeChildrenDesagragationInAPeriodCategory4RF15Denominator(),
            mappings));

    definition.addSearch(
        "NUTRITIONAL-ASSESSMENT",
        EptsReportUtils.map(
            this.findPatientsWhoHasNutritionalAssessmentInLastConsultation(), mappings));

    definition.setCompositionString("RF15-DENOMINATOR AND NUTRITIONAL-ASSESSMENT");

    return definition;
  }

  @DocumentedDefinition(
      value = "findPregnantHumansNewlyEnrolledOnARTInInclusionPeriodCategory4RF17Denominator")
  public CohortDefinition
      findPregnantHumansNewlyEnrolledOnARTInInclusionPeriodCategory4RF17Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREGNANTS NEWLY ENRROLED IN ART");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF16",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodCategory4RF16Numerator(),
            mappings));
    definition.addSearch(
        "RF8", EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));
    definition.setCompositionString("(RF16 AND RF8) NOT (TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentCategory4RF18")
  public CohortDefinition
      findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentCategory4RF18() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREGNANTS NEWLY ENRROLED IN ART AND HAS NUTRITONAL ASSESSMENT");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF17-DENOMINATOR",
        EptsReportUtils.map(
            this.findPregnantHumansNewlyEnrolledOnARTInInclusionPeriodCategory4RF17Denominator(),
            mappings));

    definition.addSearch(
        "NUTRITIONAL-ASSESSMENT",
        EptsReportUtils.map(
            this.findPatientsWhoHasNutritionalAssessmentInLastConsultation(), mappings));
    definition.setCompositionString("(RF17-DENOMINATOR AND NUTRITIONAL-ASSESSMENT)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriodCategory5RF19Denominator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriodCategory5RF19Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Categoria 5 Pediátrico - Denominador");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation(),
            mappings));

    definition.addSearch(
        "RF19-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriod(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.setCompositionString(
        "START-ART-CHILDREN AND RF19-DENOMINATOR NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriodCategory5FR20Numerator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriodCategory5FR20Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Categoria 5 Pediátrico - Numerador");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF19-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriodCategory5RF19Denominator(),
            mappings));

    definition.addSearch(
        "RF20-NUMERATOR",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriod(),
            mappings));

    definition.setCompositionString("RF19-DENOMINATOR AND RF20-NUMERATOR");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGCategory5RF21Denominator")
  public CohortDefinition
      findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGCategory5RF21Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREGNANTS NEWLY ENRROLED IN ART AND HAS NUTRITIONAL ASSESSMENT DAM-DAG");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF5", EptsReportUtils.map(this.findPatientsWhoAreNewlyEnrolledOnARTRF05(), mappings));

    definition.addSearch(
        "RF8", EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "NUTRITIONAL-ASSESSMENT-DAM-DAG",
        EptsReportUtils.map(
            this.findPatientsWhoHasNutritionalAssessmentDAMandDAGInLastConsultation(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));
    definition.setCompositionString(
        "(RF5 AND RF8 AND NUTRITIONAL-ASSESSMENT-DAM-DAG) NOT (TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGAndATPUCategory5RF22")
  public CohortDefinition
      findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGAndATPUCategory5RF22Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREGNANTS NEWLY ENRROLED IN ART AND HAS NUTRITIONAL ASSESSMENT ATPU");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF21",
        EptsReportUtils.map(
            this
                .findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGCategory5RF21Denominator(),
            mappings));

    definition.addSearch(
        "NUTRITIONAL-ASSESSMENT-ATPU",
        EptsReportUtils.map(
            this.findPatientsWhoHasNutritionalAssessmentDAMandDAGAndATPUInLastConsultation(),
            mappings));

    definition.setCompositionString("RF21 AND NUTRITIONAL-ASSESSMENT-ATPU");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientWhoAreNewlyEnrolledOnARTHaveAreLastClinicalConsultationDuringRevisionPeriodCategory6RF11Denominator")
  public CohortDefinition
      findPatientWhoAreNewlyEnrolledOnARTHaveAreLastClinicalConsultationDuringRevisionPeriodCategory6RF11Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-ADULT",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation(), mappings));

    definition.addSearch(
        "TB-ACTIVE",
        EptsReportUtils.map(
            this.findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6(),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.setCompositionString(
        "START-ART-ADULT NOT (TB-ACTIVE OR TRANSFERED-IN OR PREGNANT OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF12Numerator")
  public CohortDefinition
      findPatientsNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF12Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF12");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF11-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientWhoAreNewlyEnrolledOnARTHaveAreLastClinicalConsultationDuringRevisionPeriodCategory6RF11Denominator(),
            mappings));

    definition.addSearch(
        "TB-SCREENING",
        EptsReportUtils.map(
            this.findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6(), mappings));

    definition.setCompositionString("RF11-DENOMINATOR AND TB-SCREENING");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF13Denominator")
  public CohortDefinition
      findChildrenNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF13Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF13");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation(),
            mappings));

    definition.addSearch(
        "TB-ACTIVE",
        EptsReportUtils.map(
            this.findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6(),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.setCompositionString(
        "START-ART-CHILDREN NOT (TB-ACTIVE OR TRANSFERED-IN OR PREGNANT OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF14Numerator")
  public CohortDefinition
      findChildrenNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF14Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF14");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF13-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findChildrenNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF13Denominator(),
            mappings));

    definition.addSearch(
        "TB-SCREENING",
        EptsReportUtils.map(
            this.findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6(), mappings));

    definition.setCompositionString("RF13-DENOMINATOR AND TB-SCREENING");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF15Denominator")
  public CohortDefinition
      findPregnantNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF15Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF15");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TB-ACTIVE",
        EptsReportUtils.map(
            this.findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6(),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.setCompositionString("PREGNANT NOT (TB-ACTIVE OR TRANSFERED-IN OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "indPregnantNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF16Numerator(")
  public CohortDefinition
      findPregnantNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF16Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF16");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF15-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPregnantNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF15Denominator(),
            mappings));

    definition.addSearch(
        "TB-SCREENING",
        EptsReportUtils.map(
            this.findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6(), mappings));

    definition.setCompositionString("RF15-DENOMINATOR AND TB-SCREENING");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF17Denominator")
  public CohortDefinition
      findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF17Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF17");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TB-ACTIVE",
        EptsReportUtils.map(
            this.findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6(),
            mappings));

    definition.setCompositionString("BREASTFEEDING NOT (TB-ACTIVE OR TRANSFERED-IN OR PREGNANT )");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF18Numerator(")
  public CohortDefinition
      findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF18Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF18");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF17-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF17Denominator(),
            mappings));

    definition.addSearch(
        "TB-SCREENING",
        EptsReportUtils.map(
            this.findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6(), mappings));

    definition.setCompositionString("RF17-DENOMINATOR AND TB-SCREENING");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientWhoAreNewlyEnrolledOnARTDuringRevisionPeriodAndStartTPIAndElegibleTPTCategory7RF19Denominator")
  public CohortDefinition
      findPatientWhoAreNewlyEnrolledOnARTDuringRevisionPeriodAndStartTPIAndElegibleTPTCategory7RF19Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-ADULT",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation(), mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.findPatientsWithPositiveTBScreeningInDurindPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(this.finPatientHaveTBTreatmentDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.setCompositionString(
        "START-ART-ADULT NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR PREGNANT OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledOnARTInInclusionPeriodAndHaveINHDuringPeriodCategory7RF20Numerator(")
  public CohortDefinition
      findPatientsNewlyEnrolledOnARTInInclusionPeriodAndHaveINHDuringPeriodCategory7RF20Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF20");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF19-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientWhoAreNewlyEnrolledOnARTDuringRevisionPeriodAndStartTPIAndElegibleTPTCategory7RF19Denominator(),
            mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(this.findPatientWhoStartTPIDuringPeriodCategory7(), mappings));

    definition.setCompositionString("RF19-DENOMINATOR AND START-TPI");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF21Denominator")
  public CohortDefinition
      findChildenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF21Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation(),
            mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.findPatientsWithPositiveTBScreeningInDurindPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(this.finPatientHaveTBTreatmentDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.setCompositionString(
        "START-ART-CHILDREN NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR PREGNANT OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenNewlyEnrolledOnARTInInclusionPeriodAndStartTPIAndElegibleTPTDuringInclusionPeriodCategory7RF22Numerator(")
  public CohortDefinition
      findChildrenNewlyEnrolledOnARTInInclusionPeriodAndStartTPIAndElegibleTPTDuringInclusionPeriodCategory7RF22Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF22");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF21-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findChildenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF21Denominator(),
            mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(this.findPatientWhoStartTPIDuringPeriodCategory7(), mappings));

    definition.setCompositionString("RF21-DENOMINATOR AND START-TPI");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF23Denominator")
  public CohortDefinition
      findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF23Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.findPatientsWithPositiveTBScreeningInDurindPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(this.finPatientHaveTBTreatmentDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.setCompositionString(
        "PREGNANT NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantNewlyEnrolledOnARTInInclusionPeriodAndStartTPIAndElegibleTPTDuringInclusionPeriodCategory7RF24Numerator(")
  public CohortDefinition
      findPregnantNewlyEnrolledOnARTInInclusionPeriodAndStartTPIAndElegibleTPTDuringInclusionPeriodCategory7RF24Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF24");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF23-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF23Denominator(),
            mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(this.findPatientWhoStartTPIDuringPeriodCategory7(), mappings));

    definition.setCompositionString("RF23-DENOMINATOR AND START-TPI");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF25Denominator")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF25Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-ADULT",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation(), mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.findPatientsWithPositiveTBScreeningInDurindPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(this.finPatientHaveTBTreatmentDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(this.findPatientWhoStartTPIDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "START-TPI4MONTHS",
        EptsReportUtils.map(
            this.findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7(), mappings));

    definition.setCompositionString(
        "START-ART-ADULT NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR PREGNANT OR BREASTFEEDING OR START-TPI OR START-TPI4MONTHS)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF26Numerator(")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF26Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF26");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF25-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF25Denominator(),
            mappings));

    definition.addSearch(
        "END-TPI", EptsReportUtils.map(this.findPatientWhoCompleteTPICategory7(), mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.findPatientsWithPositiveTBScreeningInDurindPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(this.finPatientHaveTBTreatmentDuringPeriodCategory7(), mappings));

    definition.setCompositionString("END-TPI");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF27Denominator")
  public CohortDefinition
      findChildrenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF27Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenBiggerThen1neLess14(),
            mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(this.findPatientWhoStartTPIDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.findPatientsWithPositiveTBScreeningInDurindPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(this.finPatientHaveTBTreatmentDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "START-TPI4MONTHS",
        EptsReportUtils.map(
            this.findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7(), mappings));

    definition.setCompositionString(
        "(START-ART-CHILDREN AND START-TPI) NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR PREGNANT OR BREASTFEEDING OR START-TPI4MONTHS)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF28Numerator(")
  public CohortDefinition
      findChildrenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF28Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF28");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF27-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findChildrenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF27Denominator(),
            mappings));

    definition.addSearch(
        "END-TPI", EptsReportUtils.map(this.findPatientWhoCompleteTPICategory7(), mappings));

    definition.setCompositionString("RF27-DENOMINATOR AND END-TPI ");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPragnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF29Denominator")
  public CohortDefinition
      findPragnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF29Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(this.findPatientWhoStartTPIDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.findPatientsWithPositiveTBScreeningInDurindPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(this.finPatientHaveTBTreatmentDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "START-TPI4MONTHS",
        EptsReportUtils.map(
            this.findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7(), mappings));

    definition.setCompositionString(
        "(PREGNANT AND START-TPI) NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR START-TPI4MONTHS)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF30Numerator(")
  public CohortDefinition
      findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF30Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF30");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF29-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPragnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF29Denominator(),
            mappings));

    definition.addSearch(
        "END-TPI", EptsReportUtils.map(this.findPatientWhoCompleteTPICategory7(), mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.findPatientsWithPositiveTBScreeningInDurindPeriodCategory7(), mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(this.finPatientHaveTBTreatmentDuringPeriodCategory7(), mappings));

    definition.setCompositionString("RF29-DENOMINATOR AND END-TPI");

    return definition;
  }

  //  As definitions da categoria 11 comecam aqui

  @DocumentedDefinition(value = "findAdultWithCVOver1000CopiesCategory11DenominatorAdult")
  public CohortDefinition findAdultWithCVOver1000CopiesCategory11DenominatorAdult() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithCVOver1000CopiesCategory11DenominatorAdult");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findAdultWithCVOver1000CopiesCategory11DenominatorAdult;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdult")
  public CohortDefinition
      findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdult() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdult");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdult;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildernOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInCategory11Denominator(")
  public CohortDefinition
      findChildernOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInCategory11Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenBiggerThen2eLess14(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.setCompositionString("CHILDREN NOT(PREGNANT OR BREASTFEEDING OR TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11NumeratorChildren")
  public CohortDefinition
      findChildrenOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11NumeratorChildren() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdult");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findChildrenOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11NumeratorChildren;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findAdultWithCVOver1000CopiesCategory11DenominatorAdult")
  public CohortDefinition findChildrenWithCVOver1000CopiesCategory11DenominatorChildren() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithCVOver1000CopiesCategory11DenominatorAdult");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findChildrenWithCVOver1000CopiesCategory11DenominatorChildren;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorChildren")
  public CohortDefinition
      findChildrenOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorChildren() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdult");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findChildrenOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorChildren;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "B13")
  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Tx Curr B13");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory12.QUERY
            .findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13;
    definition.setQuery(query);

    return definition;
  }

  //  1. Construcao dos denominadores e Numeradores da CATEGORIA 11 PART 1

  // Desagregacao Adultos

  @DocumentedDefinition(
      value =
          "findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInCategory11Denominator")
  public CohortDefinition
      findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInCategory11Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    definition.addSearch(
        "ADULT",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation(), mappings));
    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));
    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));
    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));
    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));
    definition.setCompositionString(
        "ADULT NOT(PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT)");
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTCategory11NUMERATOR")
  public CohortDefinition
      findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTCategory11NUMERATOR() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    definition.addSearch(
        "ADULT",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation(), mappings));
    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));
    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));
    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));
    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));
    definition.addSearch(
        "APSS-PP",
        EptsReportUtils.map(
            this
                .findAdultsOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11NumeratorAdult(),
            mappings));
    definition.setCompositionString(
        "(ADULT AND APSS-PP) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT)");
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11NUMERATOR")
  public CohortDefinition
      findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11NUMERATOR() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    definition.addSearch(
        "B1", EptsReportUtils.map(this.findPatientsWhoHaveLastFirstLineTerapeutic(), mappings));
    definition.addSearch(
        "B2", EptsReportUtils.map(this.findAdultWithCVOver1000CopiesCategory11B2(), mappings));
    definition.addSearch(
        "H",
        EptsReportUtils.map(
            this
                .findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorAdultH(),
            mappings));
    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));
    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));
    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));
    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));
    definition.setCompositionString(
        "(B1 AND B2 AND H) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT)");
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11Denominator")
  public CohortDefinition
      findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    definition.addSearch(
        "B1", EptsReportUtils.map(this.findPatientsWhoHaveLastFirstLineTerapeutic(), mappings));
    definition.addSearch(
        "B2", EptsReportUtils.map(this.findAdultWithCVOver1000CopiesCategory11B2(), mappings));
    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));
    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));
    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));
    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));
    definition.setCompositionString(
        "(B1 AND B2) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT)");
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildernOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutCategory11Denominator")
  public CohortDefinition
      findChildernOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutCategory11Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    definition.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenBiggerThen2eLess14(),
            mappings));
    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));
    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));
    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));
    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));
    definition.setCompositionString(
        "CHILDREN NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT)");
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildernOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutCategory11Numerator")
  public CohortDefinition
      findChildernOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutCategory11Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    definition.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenBiggerThen2eLess14(),
            mappings));
    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));
    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));
    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));
    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));
    definition.addSearch(
        "APSS-PP-CHILDREN",
        EptsReportUtils.map(
            this
                .findChildrenOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11NumeratorChildren(),
            mappings));
    definition.setCompositionString(
        "(CHILDREN AND APSS-PP-CHILDREN) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT)");
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutWITH1000CVCategory11NUMERATOR")
  public CohortDefinition
      findChildrenOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutWITH1000CVCategory11NUMERATOR() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    definition.addSearch(
        "CHILDREN-B1",
        EptsReportUtils.map(this.findPatientsWhoHaveLastFirstLineTerapeutic(), mappings));
    definition.addSearch(
        "CHILDREN-B2",
        EptsReportUtils.map(this.findChildrenWithCVOver1000CopiesCategory11B2(), mappings));
    definition.addSearch(
        "CHILDREN-H",
        EptsReportUtils.map(
            this
                .findChildrenOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11NumeratorChildrenH(),
            mappings));
    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));
    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));
    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));
    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));
    definition.setCompositionString(
        "(CHILDREN-B1 AND CHILDREN-B2 AND CHILDREN-H) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT)");
    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutWITH1000CVCategory11Denominator")
  public CohortDefinition
      findChildrenOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutWITH1000CVCategory11Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    definition.addSearch(
        "CHILDREN-B1",
        EptsReportUtils.map(this.findPatientsWhoHaveLastFirstLineTerapeutic(), mappings));
    definition.addSearch(
        "CHILDREN-B2",
        EptsReportUtils.map(this.findChildrenWithCVOver1000CopiesCategory11B2(), mappings));
    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));
    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));
    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));
    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));
    definition.setCompositionString(
        "(CHILDREN-B1 AND CHILDREN-B2) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT)");
    return definition;
  }

  //  1. Construcao dos denominadores e Numeradores da CATEGORIA 12 PART 1

  // Desagregacao Adultos

  @DocumentedDefinition(
      value =
          "findAdultsWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut(")
  public CohortDefinition
      findAdultsWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "ADULT",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation(), mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.setCompositionString(
        "ADULT NOT(PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT)");

    return definition;
  }

  // Desagregacao Criancas

  @DocumentedDefinition(
      value =
          "findChildrenWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut(")
  public CohortDefinition
      findChildrenWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.setCompositionString(
        "CHILDREN NOT(PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT)");

    return definition;
  }

  // Denominador Gravidas

  @DocumentedDefinition(
      value =
          "findPregnantrenWhoStartedARTInTheInclusionPeriodCategory12ExcludingBreastfeedingAndTrasferedInOrOut(")
  public CohortDefinition
      findPregnantWhoStartedARTInTheInclusionPeriodCategory12ExcludingBreastfeedingAndTrasferedInOrOut() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF5", EptsReportUtils.map(this.findPatientsWhoAreNewlyEnrolledOnARTRF05(), mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.setCompositionString(
        "RF5 AND PREGNANT  NOT(BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12Line62ColumnDInTheTemplateNumerator1(")
  public CohortDefinition
      findAdultsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12Line62ColumnDInTheTemplateNumerator1() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "ADULT-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findAdultsWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut(),
            mappings));

    definition.addSearch(
        "RET33DAYS",
        EptsReportUtils.map(
            this
                .findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12(),
            mappings));

    definition.setCompositionString("ADULT-DENOMINATOR AND RET33DAYS");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12Line63ColumnDInTheTemplateNumerator2(")
  public CohortDefinition
      findAdultsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12Line63ColumnDInTheTemplateNumerator2() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "ADULT-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findAdultsWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut(),
            mappings));

    definition.addSearch(
        "RET99DAYS",
        EptsReportUtils.map(
            this
                .findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12(),
            mappings));

    definition.setCompositionString("ADULT-DENOMINATOR AND RET99DAYS");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12Line67ColumnDInTheTemplateNumerator3(")
  public CohortDefinition
      findChildrenWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12Line67ColumnDInTheTemplateNumerator3() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "CHILDREN-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findChildrenWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut(),
            mappings));

    definition.addSearch(
        "RET33DAYS",
        EptsReportUtils.map(
            this
                .findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12(),
            mappings));

    definition.setCompositionString("CHILDREN-DENOMINATOR AND RET33DAYS");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12Line63ColumnDInTheTemplateNumerator4(")
  public CohortDefinition
      findChildrenWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12Line63ColumnDInTheTemplateNumerator4() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "CHILDREN-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findChildrenWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut(),
            mappings));

    definition.addSearch(
        "RET99DAYS",
        EptsReportUtils.map(
            this
                .findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12(),
            mappings));

    definition.setCompositionString("CHILDREN-DENOMINATOR AND RET99DAYS");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12Line68ColumnDInTheTemplateNumerator5(")
  public CohortDefinition
      findPregnantWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12Line71ColumnDInTheTemplateNumerator5() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPregnantWhoStartedARTInTheInclusionPeriodCategory12ExcludingBreastfeedingAndTrasferedInOrOut(),
            mappings));

    definition.addSearch(
        "RET33DAYS",
        EptsReportUtils.map(
            this
                .findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12(),
            mappings));

    definition.setCompositionString("PREGNANT-DENOMINATOR AND RET33DAYS");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12Line63ColumnDInTheTemplateNumerator6(")
  public CohortDefinition
      findPregnantWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12Line72ColumnDInTheTemplateNumerator6() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPregnantWhoStartedARTInTheInclusionPeriodCategory12ExcludingBreastfeedingAndTrasferedInOrOut(),
            mappings));

    definition.addSearch(
        "RET99DAYS",
        EptsReportUtils.map(
            this
                .findPatientsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12(),
            mappings));

    definition.setCompositionString("PREGNANT-DENOMINATOR AND RET99DAYS");

    return definition;
  }

  //  1. Construcao dos denominadores e Numeradores da CATEGORIA 12 PART 2

  // Desagregacao Adultos 1 Linha Denominador e Numerador

  @DocumentedDefinition(
      value =
          "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE(")
  public CohortDefinition
      findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-ADULT",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12AdultA(),
            mappings));

    definition.addSearch(
        "B1-FIRSTLINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.setCompositionString(
        "(START-ART-ADULT AND B1-FIRSTLINE) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT )");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE(")
  public CohortDefinition
      findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDNumerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "ADULT-FIRST-LINE",
        EptsReportUtils.map(
            this
                .findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDDenominator(),
            mappings));

    definition.addSearch(
        "B1E-NOTFIRSTLINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNotInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1E(),
            mappings));

    definition.addSearch(
        "B13-RESUMO-MENSAL",
        EptsReportUtils.map(this.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.setCompositionString("ADULT-FIRST-LINE NOT (B1E-NOTFIRSTLINE OR B13-RESUMO-MENSAL)");

    return definition;
  }

  // Desagregacao Adultos 2 Linha Denominador e Numerador

  @DocumentedDefinition(
      value =
          "findAdultInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnEDenominator(")
  public CohortDefinition
      findAdultInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnEDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findAdultInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnEDenominator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-ADULT",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12AdultA(),
            mappings));

    definition.addSearch(
        "B2-SECONDLINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B2(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.setCompositionString(
        "(START-ART-ADULT AND B2-SECONDLINE) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT )");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnDNumerator(")
  public CohortDefinition
      findAdultInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnDNumerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "ADULT-SECOND-LINE",
        EptsReportUtils.map(
            this
                .findAdultInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnEDenominator(),
            mappings));

    definition.addSearch(
        "B2E-NOTSECONDLINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNotInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateB2E(),
            mappings));

    definition.addSearch(
        "B13-RESUMO-MENSAL",
        EptsReportUtils.map(this.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.setCompositionString(
        "ADULT-SECOND-LINE NOT (B2E-NOTSECONDLINE OR B13-RESUMO-MENSAL)");

    return definition;
  }

  // Desagregacao Criancas 1 Linha Denominador e Numerador

  @DocumentedDefinition(
      value =
          "findChildrenInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line69ColumnDDenominator(")
  public CohortDefinition
      findChildrenInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line69ColumnDDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findChildrenInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line69ColumnDDenominator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12ChildrenA(),
            mappings));

    definition.addSearch(
        "B1-FIRSTLINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.setCompositionString(
        "(START-ART-CHILDREN AND B1-FIRSTLINE) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT )");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE(")
  public CohortDefinition
      findAChildrenInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line69ColumnDNumerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "CHILDREN-FIRST-LINE",
        EptsReportUtils.map(
            this
                .findChildrenInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line69ColumnDDenominator(),
            mappings));

    definition.addSearch(
        "B1E-NOTFIRSTLINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNotInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1E(),
            mappings));

    definition.addSearch(
        "B13-RESUMO-MENSAL",
        EptsReportUtils.map(this.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.setCompositionString(
        "CHILDREN-FIRST-LINE NOT (B1E-NOTFIRSTLINE OR B13-RESUMO-MENSAL)");

    return definition;
  }

  // Desagregacao Criancas 2 Linha Denominador e Numerador

  @DocumentedDefinition(
      value =
          "findChildInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line70ColumnEDenominator(")
  public CohortDefinition
      findChildInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line70ColumnEDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findChildInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line70ColumnEDenominator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12ChildrenA(),
            mappings));

    definition.addSearch(
        "B2-SECONDLINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B2(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.setCompositionString(
        "(START-ART-CHILDREN AND B2-SECONDLINE) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT )");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnDNumerator(")
  public CohortDefinition
      findChildrenInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line70ColumnDNumerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findChildrenInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnDNumerator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "ADULT-SECOND-LINE",
        EptsReportUtils.map(
            this
                .findChildInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line70ColumnEDenominator(),
            mappings));

    definition.addSearch(
        "B2E-NOTSECONDLINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNotInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateB2E(),
            mappings));

    definition.addSearch(
        "B13-RESUMO-MENSAL",
        EptsReportUtils.map(this.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.setCompositionString(
        "ADULT-SECOND-LINE NOT (B2E-NOTSECONDLINE OR B13-RESUMO-MENSAL)");

    return definition;
  }

  // Desagregacao Gravida 1 Linha Denominador e Numerador

  @DocumentedDefinition(
      value =
          "findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDDenominator(")
  public CohortDefinition
      findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDDenominator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12AdultA(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(this.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "B1-FIRSTLINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1(),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(this.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.setCompositionString(
        "(START-ART AND  PREGNANT AND B1-FIRSTLINE) NOT (BREASTFEEDING OR TRANSFERED-IN OR TRANSFERED-OUT )");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDNumerator(")
  public CohortDefinition
      findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDNumerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDNumerator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT-FIRST-LINE",
        EptsReportUtils.map(
            this
                .findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDDenominator(),
            mappings));

    definition.addSearch(
        "B1E-NOTFIRSTLINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNotInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1E(),
            mappings));

    definition.addSearch(
        "B13-RESUMO-MENSAL",
        EptsReportUtils.map(this.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.setCompositionString(
        "PREGNANT-FIRST-LINE NOT (B1E-NOTFIRSTLINE OR B13-RESUMO-MENSAL)");

    return definition;
  }
}
