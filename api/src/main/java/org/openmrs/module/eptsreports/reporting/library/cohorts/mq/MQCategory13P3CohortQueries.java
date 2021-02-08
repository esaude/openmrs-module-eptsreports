package org.openmrs.module.eptsreports.reporting.library.cohorts.mq;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQCategory13P3QueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory13P3CohortQueries {

  @Autowired private MQCohortQueries mQCohortQueries;

  @DocumentedDefinition(
      value =
          "findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1")
  public CohortDefinition
      findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P3QueriesInterface.QUERY
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

    definition.setName(
        "findPatientsFromClinicalConsultationWhoHaveLaboratoriesInvestigationRequestsCategory13_3_B2E");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P3QueriesInterface.QUERY
            .findPatientsWhoHasTherapeuthicLineDiferentThanFirstLineFromConsultationClinicalCategory13_3_B1E_Denominator;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsGreaterThan15ExcludeAllHaveLaboratoryInvestigationRequestsAndViralChargeCategory13_3_B2")
  public CohortDefinition
      findPatientsExcludeAllHaveLaboratoryInvestigationRequestsAndViralChargeCategory13_3_B2() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("B2_GREATER_THAN_15");

    definition.setName("CAT_13_3_BI2_GREATER_THAN_15");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory13P3QueriesInterface.QUERY
            .findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2Denominator;

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
        MQCategory13P3QueriesInterface.QUERY
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
        MQCategory13P3QueriesInterface.QUERY
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
        MQCategory13P3QueriesInterface.QUERY
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
          "findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Denominador")
  public CohortDefinition
      findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Denominador() {
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
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            mQCohortQueries
                .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(mQCohortQueries.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(mQCohortQueries.findPatientsWhoAreNewlyEnrolledOnARTRF05(), mappings));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            this
                .findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_B1(),
            mappings));

    definition.setCompositionString(
        "((START-ART NOT TRANSFERED-IN) OR B1) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.5.% de adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto
   * e o nono mês após o início da 2a linha de TARV Denominator: # de adultos (15/+ anos) que
   * iniciaram a 2a linha do TARV no período de inclusão (Line 80, Column F in the Template)
   */
  @DocumentedDefinition(
      value =
          "findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Denominador_13_5")
  public CohortDefinition
      findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Denominador_13_5() {
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
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(mQCohortQueries.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "B2",
        EptsReportUtils.map(
            this
                .findPatientsExcludeAllHaveLaboratoryInvestigationRequestsAndViralChargeCategory13_3_B2(),
            mappings));

    definition.setCompositionString("B2 NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

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
          "findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Numerador")
  public CohortDefinition
      findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Numerador() {
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
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            mQCohortQueries
                .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(mQCohortQueries.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(mQCohortQueries.findPatientsWhoAreNewlyEnrolledOnARTRF05(), mappings));

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
        "(((START-ART NOT TRANSFERED-IN) AND G) OR (B1 AND H)) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-OUT)");

    return definition;
  }

  /**
   * 13.5.% de adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto
   * e o nono mês após o início da 2a linha de TARV (Line 80 in the template) Numerator (Column E in
   * the Template)
   */
  @DocumentedDefinition(
      value =
          "findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Numerador_13_5")
  public CohortDefinition
      findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Numerador_13_5() {
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
        "DENOMINATOR-B2",
        EptsReportUtils.map(
            this
                .findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Denominador_13_5(),
            mappings));

    definition.addSearch(
        "I",
        EptsReportUtils.map(
            this
                .findPatientsFromClinicalConsultationWhoHaveViralChargeSecondLineDateCategory13_3_I(),
            mappings));

    definition.setCompositionString("DENOMINATOR-B2 AND I");

    return definition;
  }
}
