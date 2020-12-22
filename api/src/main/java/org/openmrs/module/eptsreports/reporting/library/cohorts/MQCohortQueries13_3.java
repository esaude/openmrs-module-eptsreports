package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovementQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovementQueriesInterfaceCategory13_3;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class MQCohortQueries13_3 {

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
      value = "findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween5And9")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween5And9() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13_3.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween5And9;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween10And14")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween10And14() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13_3.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween10And14;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween0To4")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween0To4() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13_3.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween0To4;

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

  @DocumentedDefinition(
      value =
          "findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1")
  public CohortDefinition
      findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("B1");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13_3.QUERY
            .findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_Denominador_B1;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsFromClinicalConsultationWhoHaveLaboratoriesInvestigationRequestsCategory13_3_B2E")
  public CohortDefinition
      findPatientsFromClinicalConsultationWhoHaveLaboratoriesInvestigationRequestsCategory13_3_B2E() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("Category13_3_B2E");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13_3.QUERY
            .findPatientsWhoHasTherapeuthicLineDiferentThanFirstLineFromConsultationClinicalCategory13_3_B1E_Denominator;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsGreaterThan15ExcludeAllHaveLaboratoryInvestigationRequestsAndViralChargeCategory13_3_B2")
  public CohortDefinition
      findPatientsGreaterThan15ExcludeAllHaveLaboratoryInvestigationRequestsAndViralChargeCategory13_3_B2() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("B2_GREATER_THAN_15");

    definition.setName("CAT_13_3_BI2_GREATER_THAN_15");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13_3.QUERY
            .findAllPatientsGreaterThan15WhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13_3_B2_Denominator;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsGreaterThan2LessThan15ExcludeAllHaveLaboratoryInvestigationRequestsAndViralChargeCategory13_3_B2")
  public CohortDefinition
      findPatientsGreaterThan2LessThan15ExcludeAllHaveLaboratoryInvestigationRequestsAndViralChargeCategory13_3_B2() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("B2_GREATER_THAN_2_LESS_THAN_15");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13_3.QUERY
            .findAllPatientsGreaterThan2LessThan15WhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13_3_B2_Denominator;

    definition.setQuery(query);

    return definition;
  }

  /**
   * G - Select all patients with a clinical consultation (​ encounter type 6​ ) with concept “Carga
   * Viral” (​ Concept id 856​ ) with value_numeric not null ​ and Encounter_datetime between
   * “Patient ART Start Date” (​ the oldest date from A​ )+6months and “Patient ART Start Date” (​
   * the oldest date from query A​ )+9months
   */
  @DocumentedDefinition(
      value = "findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_G")
  public CohortDefinition findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_G() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("Category13_3_G");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13_3.QUERY
            .findAllPatientsWhoHaveClinicalConsultationWithViralChargeBetweenSixAndNineMonthsAfterARTStartDateCategory13_3_G_Numerator;

    definition.setQuery(query);

    return definition;
  }

  /**
   * H - Select all patients with a clinical consultation (​ encounter type 6​ ) with concept “Carga
   * Viral” (​ Concept id 856​ ) with value_numeric not null ​ and Encounter_datetime between
   * “ALTERNATIVA A LINHA - 1a LINHA Date” (​ the most recent date from B1​ )+6months and
   * “ALTERNATIVA A LINHA - 1a LINHA Date” (​ the most recent date from B1​ )+9months.
   */
  @DocumentedDefinition(
      value = "findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_H")
  public CohortDefinition findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_H() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("Category13_3_H");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13_3.QUERY
            .findAllPatientsWhoHaveClinicalConsultationAndEncounterDateTimeBetweenAlternativeFirstLineDateCategory13_3_H_Numerator;

    definition.setQuery(query);

    return definition;
  }

  /**
   * I - Select all patients with a clinical consultation (encounter type 6) with concept “Carga
   * Viral” (Concept id 856) with value_numeric not null OR concept “Carga Viral Qualitative”
   * (Concept id 1305) with value_coded not null and Encounter_datetime between “Segunda Linha Date”
   * (the most recent date from B2)+6months and “Segunda Linha Date” (the most recent date from
   * B2)+9months.
   */
  @DocumentedDefinition(
      value = "findPatientsFromClinicalConsultationWhoHaveViralChargeSecondLineDateCategory13_3_I")
  public CohortDefinition
      findPatientsFromClinicalConsultationWhoHaveViralChargeSecondLineDateCategory13_3_I() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("Category13_3_I");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13_3.QUERY
            .findAllPatientsWhoHaveClinicalConsultationAndEncounterDateTimeBetweenSecondTherapheuticLineDateCategory13_3_I_Numerator;

    definition.setQuery(query);

    return definition;
  }

  // Implementação de DENOMINADORES Categoria13.3
  // ------------------------------------------------------------------------------------------------------------------------------------------------------------

  /**
   * 13.2. % de adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto
   * e o nono mês após início do TARV Denominator: # de adultos que iniciaram TARV e novo regime no
   * período de inclusão (Line 77, Column F in the Template)
   */
  @DocumentedDefinition(
      value =
          "findAdultsPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Denominador_13_2")
  public CohortDefinition
      findAdultsPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Denominador_13_2() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3 DENOMINATOR_13_2");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "START-TARV",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation(), mappings));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            this
                .findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1(),
            mappings));

    definition.setCompositionString(
        "((START-TARV NOT TRANSFERED-IN) OR B1) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.9. % de crianças (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da Carga
   * Viral entre o sexto e o nono mês após o início do TARV Denominator: # de crianças (0-4 anos de
   * idade) que iniciaram TARV no período de inclusão (Line 84, Column F in the Template)
   */
  @DocumentedDefinition(
      value =
          "findChildrenZeroToFourPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Denominador_13_9")
  public CohortDefinition
      findChildrenZeroToFourPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Denominador_13_9() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3 DENOMINATOR_13_9");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "START-TARV",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween0To4(),
            mappings));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            this
                .findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1(),
            mappings));

    definition.setCompositionString(
        "((START-TARV NOT TRANSFERED-IN) OR B1) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.10.% de crianças (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da Carga
   * Viral entre o sexto e o nono mês após o início do TARV Denominator:# de crianças (5-9 anos de
   * idade) que iniciaram TARV no período de inclusão (Line 85, Column F in the Template)
   */
  @DocumentedDefinition(
      value =
          "findChildrenFifeNinePatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Denominador_13_10")
  public CohortDefinition
      findChildrenFifeNinePatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Denominador_13_10() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3 DENOMINATOR_13_10");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "START-TARV",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween5And9(),
            mappings));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            this
                .findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1(),
            mappings));

    definition.setCompositionString(
        "((START-TARV NOT TRANSFERED-IN) OR B1) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.11.% de crianças (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da
   * Carga Viral entre o sexto e o nono mês após o início do TARV Denominator: # de crianças (10-14
   * anos de idade) que iniciaram TARV no período de inclusão (Line 86, Column F in the Template)
   */
  @DocumentedDefinition(
      value =
          "findChildrenFifeNinePatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Denominador_13_11")
  public CohortDefinition
      findChildrenFifeNinePatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Denominador_13_11() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3 DENOMINATOR_13_11");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "START-TARV",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween10And14(),
            mappings));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            this
                .findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1(),
            mappings));

    definition.setCompositionString(
        "((START-TARV NOT TRANSFERED-IN) OR B1) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.5.% de adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto
   * e o nono mês após o início da 2a linha de TARV Denominator: # de adultos (15/+ anos) que
   * iniciaram a 2a linha do TARV no período de inclusão (Line 80, Column F in the Template)
   */
  @DocumentedDefinition(
      value =
          "findAdultPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Denominador_13_5")
  public CohortDefinition
      findAdultPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Denominador_13_5() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Category13_3_Denominador_13_5");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "B2",
        EptsReportUtils.map(
            this
                .findPatientsGreaterThan15ExcludeAllHaveLaboratoryInvestigationRequestsAndViralChargeCategory13_3_B2(),
            mappings));

    definition.setCompositionString("B2 NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.14. % de crianças na 2a linha de TARV que receberam o resultado da Carga Viral entre o sexto
   * e o nono mês após o início da 2a linha de TARV Denominator:# de crianças > 2 anos que iniciaram
   * a 2a linha do TARV no período de inclusão (Line 88, Column F in the Template)
   */
  @DocumentedDefinition(
      value =
          "findChildrenPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartAgeBiggerThanTwoCategory13_3_Denominador_13_14")
  public CohortDefinition
      findChildrenPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartAgeBiggerThanTwoCategory13_3_Denominador_13_14() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Category13_3_Denominador_13_14");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "B2",
        EptsReportUtils.map(
            this
                .findPatientsGreaterThan2LessThan15ExcludeAllHaveLaboratoryInvestigationRequestsAndViralChargeCategory13_3_B2(),
            mappings));

    definition.setCompositionString("B2 NOT (PREGNANT or BREASTFEEDING or TRANSFERED-OUT)");

    return definition;
  }

  // Implementação de NUMERADORES Categoria13.3
  // ------------------------------------------------------------------------------------------------------------------------------------------------------------

  /**
   * 13.2. % de adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto
   * e o nono mês após início do TARV (Line 77 in the template) Numerator (Column E in the Template)
   */
  @DocumentedDefinition(
      value =
          "findAdultsPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Numerador_13_2")
  public CohortDefinition
      findAdultsPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Numerador_13_2() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3_NUMERADOR_13_2");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "START-TARV",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation(), mappings));

    definition.addSearch(
        "G",
        EptsReportUtils.map(
            this.findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_G(), mappings));

    definition.addSearch(
        "H",
        EptsReportUtils.map(
            this.findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_H(), mappings));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            this
                .findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1(),
            mappings));

    definition.setCompositionString(
        "(((START-TARV NOT TRANSFERED-IN) AND G) OR (B1 AND H)) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.9. % de crianças (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da Carga
   * Viral entre o sexto e o nono mês após o início do TARV (Line 84 in the template) Numerator
   * (Column E in the Template)
   */
  @DocumentedDefinition(
      value =
          "findChildrenPatientsFromZeroToFourInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Numerador_13_9")
  public CohortDefinition
      findChildrenPatientsFromZeroToFourInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Numerador_13_9() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3_NUMERADOR_13_9");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "START-TARV",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween0To4(),
            mappings));

    definition.addSearch(
        "G",
        EptsReportUtils.map(
            this.findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_G(), mappings));

    definition.addSearch(
        "H",
        EptsReportUtils.map(
            this.findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_H(), mappings));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            this
                .findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1(),
            mappings));

    definition.setCompositionString(
        "(((START-TARV NOT TRANSFERED-IN) AND G) OR (B1 AND H)) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.10.% de crianças (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da Carga
   * Viral entre o sexto e o nono mês após o início do TARV (Line 85 in the template) Numerator
   * (Column E in the Template)
   */
  @DocumentedDefinition(
      value =
          "findChildrenPatientsFromFiveToNineInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Numerador_13_10")
  public CohortDefinition
      findChildrenPatientsFromFiveToNineInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Numerador_13_10() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3_NUMERADOR_13_10");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "START-TARV",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween5And9(),
            mappings));

    definition.addSearch(
        "G",
        EptsReportUtils.map(
            this.findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_G(), mappings));

    definition.addSearch(
        "H",
        EptsReportUtils.map(
            this.findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_H(), mappings));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            this
                .findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1(),
            mappings));

    definition.setCompositionString(
        "(((START-TARV NOT TRANSFERED-IN) AND G) OR (B1 AND H)) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.11.% de crianças (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da
   * Carga Viral entre o sexto e o nono mês após o início do TARV (Line 86 in the template)
   * Numerator (Column E in the Template)
   */
  @DocumentedDefinition(
      value =
          "findChildrenPatientsFrom14To10InFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Numerador_13_11")
  public CohortDefinition
      findChildrenPatientsFrom14To10InFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_Numerador_13_11() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3_NUMERADOR_13_11");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "START-TARV",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragationBetween10And14(),
            mappings));

    definition.addSearch(
        "G",
        EptsReportUtils.map(
            this.findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_G(), mappings));

    definition.addSearch(
        "H",
        EptsReportUtils.map(
            this.findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_H(), mappings));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            this
                .findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1(),
            mappings));

    definition.setCompositionString(
        "(((START-TARV NOT TRANSFERED-IN) AND G) OR (B1 AND H)) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.5.% de adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto
   * e o nono mês após o início da 2a linha de TARV (Line 80 in the template) Numerator (Column E in
   * the Template)
   */
  @DocumentedDefinition(
      value =
          "findAdultPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Numerador_13_5")
  public CohortDefinition
      findAdultPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Numerador_13_5() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Category13_3_Numerador_13_5");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "B2",
        EptsReportUtils.map(
            this
                .findPatientsGreaterThan15ExcludeAllHaveLaboratoryInvestigationRequestsAndViralChargeCategory13_3_B2(),
            mappings));

    definition.addSearch(
        "I",
        EptsReportUtils.map(
            this
                .findPatientsFromClinicalConsultationWhoHaveViralChargeSecondLineDateCategory13_3_I(),
            mappings));

    definition.setCompositionString("(B2 AND I) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.14. % de crianças na 2a linha de TARV que receberam o resultado da Carga Viral entre o sexto
   * e o nono mês após o início da 2a linha de TARV (Line 88 in the template) Numerator (Column E in
   * the Template)
   */
  @DocumentedDefinition(
      value =
          "findChildrenPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Numerador_13_14")
  public CohortDefinition
      findChildrenPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Numerador_13_14() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Category13_3_Numerador_13_14");
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
        "TRANSFERED-OUT", EptsReportUtils.map(this.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "B2",
        EptsReportUtils.map(
            this
                .findPatientsGreaterThan15ExcludeAllHaveLaboratoryInvestigationRequestsAndViralChargeCategory13_3_B2(),
            mappings));

    definition.addSearch(
        "I",
        EptsReportUtils.map(
            this
                .findPatientsFromClinicalConsultationWhoHaveViralChargeSecondLineDateCategory13_3_I(),
            mappings));

    definition.setCompositionString("(B2 AND I) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }
}
