package org.openmrs.module.eptsreports.reporting.library.cohorts.mi;

import java.util.Date;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory13P3CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MICategory13P3CohortQueries extends MQAbstractDataSet {

  @Autowired private MQCohortQueries mQCohortQueries;
  @Autowired private MQCategory13P3CohortQueries mQCategory13P3CohortQueries;

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
        "startInclusionDate=${endRevisionDate-10m+1d},endInclusionDate=${endRevisionDate-9m},endRevisionDate=${endRevisionDate},location=${location}";

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
        "BI1",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoAreInAlternativeLineFirstLineCategory13_3_BI1_Denominator(),
            mappings));

    definition.addSearch(
        "B1E",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoHasTherapeuthicLineDiferentThanFirstLineFromConsultationClinicalCategory13_3_B1E_Denominator(),
            mappings));

    definition.addSearch(
        "DEAD",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries.findAllPatientWhoAreDeadByEndOfRevisonPeriod(), mappings));

    definition.setCompositionString(
        "((START-ART NOT PREGNANT NOT BREASTFEEDING) OR (BI1 NOT B1E)) NOT (TRANSFERED-IN OR TRANSFERED-OUT OR DEAD)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_11Denominador")
  public CohortDefinition
      findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_11Denominador() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3 DENOMINATOR_13_2");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

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
        "BI1",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoAreInAlternativeLineFirstLineCategory13_3_BI1_Denominator(),
            mappings));

    definition.addSearch(
        "B1E",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoHasTherapeuthicLineDiferentThanFirstLineFromConsultationClinicalCategory13_3_B1E_Denominator(),
            mappings));

    definition.addSearch(
        "DEAD",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries.findAllPatientWhoAreDeadByEndOfRevisonPeriod(), mappings));

    definition.setCompositionString(
        "((START-ART NOT PREGNANT NOT BREASTFEEDING) OR (BI1 NOT B1E)) NOT (TRANSFERED-IN OR TRANSFERED-OUT OR DEAD)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_10Denominador")
  public CohortDefinition
      findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_10Denominador() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3 DENOMINATOR_13_2");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

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
        "BI1",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoAreInAlternativeLineFirstLineCategory13_3_BI1_Denominator(),
            mappings));

    definition.addSearch(
        "B1E",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoHasTherapeuthicLineDiferentThanFirstLineFromConsultationClinicalCategory13_3_B1E_Denominator(),
            mappings));

    definition.addSearch(
        "DEAD",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries.findAllPatientWhoAreDeadByEndOfRevisonPeriod(), mappings));

    definition.setCompositionString(
        "((START-ART NOT PREGNANT NOT BREASTFEEDING) OR (BI1 NOT B1E)) NOT (TRANSFERED-IN OR TRANSFERED-OUT OR DEAD)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_9Denominador")
  public CohortDefinition
      findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_9Denominador() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3 DENOMINATOR_13_2");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

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
        "BI1",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoAreInAlternativeLineFirstLineCategory13_3_BI1_Denominator(),
            mappings));

    definition.addSearch(
        "B1E",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoHasTherapeuthicLineDiferentThanFirstLineFromConsultationClinicalCategory13_3_B1E_Denominator(),
            mappings));

    definition.addSearch(
        "DEAD",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries.findAllPatientWhoAreDeadByEndOfRevisonPeriod(), mappings));

    definition.setCompositionString(
        "((START-ART NOT PREGNANT NOT BREASTFEEDING) OR (BI1 NOT B1E)) NOT (TRANSFERED-IN OR TRANSFERED-OUT OR DEAD)");

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
        "startInclusionDate=${endRevisionDate-10m+1d},endInclusionDate=${endRevisionDate-9m},endRevisionDate=${endRevisionDate},location=${location}";

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
        "B2",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2NEWDenominator(),
            mappings));

    definition.addSearch(
        "DEAD",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries.findAllPatientWhoAreDeadByEndOfRevisonPeriod(), mappings));

    definition.setCompositionString("B2 NOT (TRANSFERED-IN OR TRANSFERED-OUT OR DEAD)");

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
        "startInclusionDate=${endRevisionDate-10m+1d},endInclusionDate=${endRevisionDate-9m},endRevisionDate=${endRevisionDate},location=${location}";

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
            mQCategory13P3CohortQueries
                .findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_G(),
            mappings));

    definition.addSearch(
        "H",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_H(),
            mappings));

    definition.addSearch(
        "J",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findAllPatientsWhoHaveClinicalConsultationWithViralChargeBetweenSixAndNineMonthsAfterARTStartDateCategory13_3_J_Numerator(),
            mappings));

    definition.addSearch(
        "K",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findAllPatientsWhoHaveClinicalConsultationAndEncounterDateTimeBetweenAlternativeFirstLineDateCategory13_3_K_Numerator(),
            mappings));

    definition.addSearch(
        "DD",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries.findAllPatientWhoAreDeadByEndOfRevisonPeriod(), mappings));

    definition.addSearch(
        "BI1",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoAreInAlternativeLineFirstLineCategory13_3_BI1_Denominator(),
            mappings));

    definition.addSearch(
        "B1E",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoHasTherapeuthicLineDiferentThanFirstLineFromConsultationClinicalCategory13_3_B1E_Denominator(),
            mappings));

    definition.setCompositionString(
        "(((START-ART AND (G OR J)) NOT PREGNANT NOT BREASTFEEDING NOT DD) OR ((BI1 NOT B1E) AND (H OR K ))) NOT (TRANSFERED-IN OR TRANSFERED-OUT OR DD)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_11Numerador")
  public CohortDefinition
      findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_11Numerador() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3_NUMERADOR_13_2");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

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
            mQCategory13P3CohortQueries
                .findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_G(),
            mappings));

    definition.addSearch(
        "H",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_H(),
            mappings));

    definition.addSearch(
        "J",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findAllPatientsWhoHaveClinicalConsultationWithViralChargeBetweenSixAndNineMonthsAfterARTStartDateCategory13_3_J_Numerator(),
            mappings));

    definition.addSearch(
        "K",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findAllPatientsWhoHaveClinicalConsultationAndEncounterDateTimeBetweenAlternativeFirstLineDateCategory13_3_K_Numerator(),
            mappings));

    definition.addSearch(
        "DD",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries.findAllPatientWhoAreDeadByEndOfRevisonPeriod(), mappings));

    definition.addSearch(
        "BI1",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoAreInAlternativeLineFirstLineCategory13_3_BI1_Denominator(),
            mappings));

    definition.addSearch(
        "B1E",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoHasTherapeuthicLineDiferentThanFirstLineFromConsultationClinicalCategory13_3_B1E_Denominator(),
            mappings));

    definition.setCompositionString(
        "(((START-ART AND (G OR J)) NOT PREGNANT NOT BREASTFEEDING NOT DD) OR ((BI1 NOT B1E) AND (H OR K ))) NOT (TRANSFERED-IN OR TRANSFERED-OUT OR DD)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_10Numerador")
  public CohortDefinition
      findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_10Numerador() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3_NUMERADOR_13_2");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

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
            mQCategory13P3CohortQueries
                .findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_G(),
            mappings));

    definition.addSearch(
        "H",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_H(),
            mappings));

    definition.addSearch(
        "J",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findAllPatientsWhoHaveClinicalConsultationWithViralChargeBetweenSixAndNineMonthsAfterARTStartDateCategory13_3_J_Numerator(),
            mappings));

    definition.addSearch(
        "K",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findAllPatientsWhoHaveClinicalConsultationAndEncounterDateTimeBetweenAlternativeFirstLineDateCategory13_3_K_Numerator(),
            mappings));

    definition.addSearch(
        "DD",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries.findAllPatientWhoAreDeadByEndOfRevisonPeriod(), mappings));

    definition.addSearch(
        "BI1",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoAreInAlternativeLineFirstLineCategory13_3_BI1_Denominator(),
            mappings));

    definition.addSearch(
        "B1E",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoHasTherapeuthicLineDiferentThanFirstLineFromConsultationClinicalCategory13_3_B1E_Denominator(),
            mappings));

    definition.setCompositionString(
        "(((START-ART AND (G OR J)) NOT PREGNANT NOT BREASTFEEDING NOT DD) OR ((BI1 NOT B1E) AND (H OR K ))) NOT (TRANSFERED-IN OR TRANSFERED-OUT OR DD)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_9Numerador")
  public CohortDefinition
      findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_9Numerador() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CAT13_3_NUMERADOR_13_2");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

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
            mQCategory13P3CohortQueries
                .findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_G(),
            mappings));

    definition.addSearch(
        "H",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsFromClinicalConsultationWhoHaveViralChargeCategory13_3_H(),
            mappings));

    definition.addSearch(
        "J",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findAllPatientsWhoHaveClinicalConsultationWithViralChargeBetweenSixAndNineMonthsAfterARTStartDateCategory13_3_J_Numerator(),
            mappings));

    definition.addSearch(
        "K",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findAllPatientsWhoHaveClinicalConsultationAndEncounterDateTimeBetweenAlternativeFirstLineDateCategory13_3_K_Numerator(),
            mappings));

    definition.addSearch(
        "DD",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries.findAllPatientWhoAreDeadByEndOfRevisonPeriod(), mappings));

    definition.addSearch(
        "BI1",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoAreInAlternativeLineFirstLineCategory13_3_BI1_Denominator(),
            mappings));

    definition.addSearch(
        "B1E",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsWhoHasTherapeuthicLineDiferentThanFirstLineFromConsultationClinicalCategory13_3_B1E_Denominator(),
            mappings));

    definition.setCompositionString(
        "(((START-ART AND (G OR J)) NOT PREGNANT NOT BREASTFEEDING NOT DD) OR ((BI1 NOT B1E) AND (H OR K ))) NOT (TRANSFERED-IN OR TRANSFERED-OUT OR DD)");

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
        "startInclusionDate=${endRevisionDate-10m+1d},endInclusionDate=${endRevisionDate-9m},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR-B2",
        EptsReportUtils.map(
            this
                .findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Denominador_13_5(),
            mappings));

    definition.addSearch(
        "I",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findPatientsFromClinicalConsultationWhoHaveViralChargeSecondLineDateCategory13_3_I(),
            mappings));

    definition.addSearch(
        "L",
        EptsReportUtils.map(
            mQCategory13P3CohortQueries
                .findAllPatientsWhoHaveClinicalConsultationAndEncounterDateTimeBetweenSecondTherapheuticLineDateCategory13_3_L_Numerator(),
            mappings));

    definition.setCompositionString("DENOMINATOR-B2 AND (I OR L)");

    return definition;
  }
}
