package org.openmrs.module.eptsreports.reporting.library.datasets.viralloadmidatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mi.MICategory13P1_1CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mi.MICategory13P1_2CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mi.MICategory13P2CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mi.MICategory13P3CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mi.MICategory13P4CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VLMICategory13Dataset extends MQAbstractDataSet {

  @Autowired private MICategory13P1_1CohortQueries mICategory13P1_1CohortQueries;

  @Autowired private MICategory13P3CohortQueries mICategory13P3CohortQueries;

  @Autowired private MICategory13P4CohortQueries miCategory13P4CohortQueries;

  @Autowired private MICategory13P1_2CohortQueries mICategory13P1_2CohortQueries;

  @Autowired private MICategory13P2CohortQueries mICategory13P2CohortQueries;

  public void constructTMiDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSDENOMINATOR",
        "13.1: Adultos (15/+anos) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador ",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findDenominatorCategory13SectionIB(),
                "CAT13CV15PLUSDENOMINATOR",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV04DENOMINATOR",
        "13.6: Crianças (0-4 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findDenominatorCategory13SectionIB(),
                "CAT13CV04DENOMINATOR",
                mappings),
            mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT13CV59DENOMINATOR",
        "13.7: Crianças (5-9 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findDenominatorCategory13SectionIB(),
                "CAT13CV59DENOMINATOR",
                mappings),
            mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT13CV1014DENOMINATOR",
        "13.8: Crianças (10-14 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                mICategory13P1_1CohortQueries.findDenominatorCategory13SectionIB(),
                "CAT13CV1014DENOMINATOR",
                mappings),
            mappings),
        "age=10-14");

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSNUMERATOR",
        "13.1: Adultos (15/+anos) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findFinalNumeratorCategory13SectionIC(),
                "CAT13CV15PLUSNUMERATOR",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV04NUMERATOR",
        "13.6: Crianças (0-4 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findFinalNumeratorCategory13SectionIC(),
                "CAT13CV04NUMERATOR",
                mappings),
            mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT13CV59NUMERATOR",
        "13.7: Crianças (5-9 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findFinalNumeratorCategory13SectionIC(),
                "CAT13CV59NUMERATOR",
                mappings),
            mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT13CV1014NUMERATOR",
        "13.8: Crianças (10-14 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findFinalNumeratorCategory13SectionIC(),
                "CAT13CV1014NUMERATOR",
                mappings),
            mappings),
        "age=10-14");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_2_DENOMINATOR",
        "13.2: Adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início do TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Denominador(),
                "CAT13_PART_3_13_2_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+10MONTHS");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_2_NUMERATOR",
        "13.2: Adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início do TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Numerador(),
                "CAT13_PART_3_13_2_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+10MONTHS");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_9_DENOMINATOR",
        "13.9: Crianças  (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da "
            + "Carga Viral entre o sexto e o nono mês após o início do TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_9Denominador(),
                "CAT13_PART_3_13_9_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=0-4-5MONTHS");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_9_NUMERATOR",
        "13.9: Crianças  (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da "
            + "Carga Viral entre o sexto e o nono mês após o início do TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_9Numerador(),
                "CAT13_PART_3_13_9_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=0-4-5MONTHS");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_10_DENOMINATOR",
        "13.10: Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da "
            + "Carga Viral entre o sexto e o nono mês após o início do TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_10Denominador(),
                "CAT13_PART_3_13_10_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=5-9-5MONTHS");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_10_NUMERATOR",
        "13.10: Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da "
            + "Carga Viral entre o sexto e o nono mês após o início do TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_10Numerador(),
                "CAT13_PART_3_13_10_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=5-9-5MONTHS");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_11_DENOMINATOR",
        "13.11: Crianças  (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da "
            + "Carga Viral entre o sexto e o nono mês após o início do TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_11Denominador(),
                "CAT13_PART_3_13_11_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=10-14-5MONTHS");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_11_NUMERATOR",
        "13.11: Crianças  (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da"
            + " Carga Viral entre o sexto e o nono mês após o início do TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_11Numerador(),
                "CAT13_PART_3_13_11_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=10-14-5MONTHS");

    dataSetDefinition.addColumn(
        "CAT13P4AdultDENUMINATOR",
        "13.3: Adultos (15/+anos) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º "
            + "mês após terem recebido  o último resultado de CV acima de 1000 e terem  3 sessões consecutivas de APSS/PP (AMA) Denominador ",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory13P4CohortQueries
                    .findPatietsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11Denominator(),
                "CAT13P4AdultDENUMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+5MONTHS");

    dataSetDefinition.addColumn(
        "CAT13P4AdultNUMINATOR",
        "13.3: Adultos (15/+anos) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês "
            + "após terem recebido  o último resultado de CV acima de 1000 e terem  3 sessões consecutivas de APSS/PP (AMA) Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory13P4CohortQueries
                    .findPatientsWhoReceivedResultMoreThan1000CVCategory13P4Numerator(),
                "CAT13P4AdultNUMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+5MONTHS");

    dataSetDefinition.addColumn(
        "CAT13P4ChildrenDENUMINATOR",
        "13.12: Crianças (>2 anos de idade) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês "
            + "após terem recebido  o último resultado de CV acima de 1000 cópia e terem  3 sessões consecutivas de APSS/PP (AMA) Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory13P4CohortQueries
                    .findPatietsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11Denominator(),
                "CAT13P4ChildrenDENUMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=2-14-5MONTHS");

    dataSetDefinition.addColumn(
        "CAT13P4ChildrenNUMINATOR",
        "13.12: Crianças (>2 anos de idade) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês "
            + "após terem recebido  o último resultado de CV acima de 1000 cópia e terem  3 sessões consecutivas de APSS/PP (AMA) Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory13P4CohortQueries
                    .findPatientsWhoReceivedResultMoreThan1000CVCategory13P4Numerator(),
                "CAT13P4ChildrenNUMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=2-14-5MONTHS");

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSDENOMINATOR_SECTION1_2",
        "13.4: Adultos (15/+anos) na 2a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_2CohortQueries.findDenominatorCategory13SectionIIB(),
                "CAT13CV15PLUSDENOMINATOR_SECTION1_2",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV24DENOMINATOR_SECTION1_2",
        "13.13: Crianças na 2a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_2CohortQueries.findDenominatorCategory13SectionIIB(),
                "CAT13CV24DENOMINATOR_SECTION1_2",
                mappings),
            mappings),
        "age=2-14");

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSNUMERATOR_SECTION1_2",
        "13.4: Adultos (15/+anos) na 2a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_2CohortQueries.findFinalNumeratorCategory13SectionIIC(),
                "CAT13CV15PLUSNUMERATOR_SECTION1_2",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV24NUMERATOR_SECTION1_2",
        "13.13: Crianças na 2a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerator",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_2CohortQueries.findFinalNumeratorCategory13SectionIIC(),
                "CAT13CV24NUMERATOR_SECTION1_2",
                mappings),
            mappings),
        "age=2-14");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_5_DENOMINATOR",
        "13.5: Adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o "
            + "sexto e o nono mês após o início da 2a linha de TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Denominador_13_5(),
                "CAT13_PART_3_13_5_DENOMINATOR",
                mappings),
            mappings),
        "ageOnB2NEW=15+10MONTHS");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_5_NUMERATOR",
        "13.5: Adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre "
            + "o sexto e o nono mês após o início da 2a linha de TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Numerador_13_5(),
                "CAT13_PART_3_13_5_NUMERATOR",
                mappings),
            mappings),
        "ageOnB2NEW=15+10MONTHS");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_14_DENOMINATOR",
        "13.14: Crianças  na 2a linha de TARV que receberam o resultado da Carga Viral entre o "
            + "sexto e o nono mês após o início da 2a linha de TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Denominador_13_5(),
                "CAT13_PART_3_13_14_DENOMINATOR",
                mappings),
            mappings),
        "ageOnB2NEW=2-14-10MONTHS");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_14_NUMERATOR",
        "13.14: Crianças  na 2a linha de TARV que receberam o resultado da Carga Viral entre o "
            + "sexto e o nono mês após o início da 2a linha de TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P3CohortQueries
                    .findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Numerador_13_5(),
                "CAT13_PART_3_13_14_NUMERATOR",
                mappings),
            mappings),
        "ageOnB2NEW=2-14-10MONTHS");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInTARVDENOMINATOR",
        "13.15: Mulheres Gravidas elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram TARV na CPN) Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P2CohortQueries
                    .findPatientsWhoArePregnantWithCVInTARVCategory13P2Denumerator(),
                "CAT13P2PregnantWithCVInTARVDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInTARVNUMINATOR",
        "13.15: Mulheres Gravidas elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram TARV na CPN) Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P2CohortQueries
                    .findPatientsWhoArePregnantWithCVInTARVCategory13P2Numerator(),
                "CAT13P2PregnantWithCVInTARVNUMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInFirstConsultationTARVDENOMINATOR",
        "13.16: Mulheres Gravidas elegíveis a CV com registo de pedido de "
            + "CV feito pelo clínico na primeira CPN (MG que entraram em TARV na CPN) Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P2CohortQueries
                    .findPatientsWhoArePregnantWithCVInFirstConsultationTARVCategory13P2Denumerator(),
                "CAT13P2PregnantWithCVInFirstConsultationTARVDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInFirstConsultationTARVNUMINATOR",
        "13.16: Mulheres elegíveis a CV com registo de pedido de CV feito pelo clínico na primeira CPN (MG que entraram em TARV na CPN) Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P2CohortQueries
                    .findPatientsWhoArePregnantWithCVInFirstConsultationTARVCategory13P2Numerator(),
                "CAT13P2PregnantWithCVInFirstConsultationTARVNUMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVDENOMINATOR",
        "13.17: Mulheres Gravidas que receberam o resultado da Carga Viral dentro de 33 dias após pedido Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P2CohortQueries
                    .findPatientsWhoArePregnantWithCVIn33DaysAfterInclusionDateTARVCategory13P2Denumerator(),
                "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVNUMINATOR",
        "13.17: Mulheres Gravidas que receberam o resultado da Carga Viral dentro de 33 dias após pedido Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P2CohortQueries
                    .findPatientsWhoArePregnantWithCVIn33DaysAfterInclusionDateTARVCategory13P2Numerator(),
                "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVNUMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P4PregnantDENUMINATOR",
        "13.18: Mulheres Gravidas na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º "
            + "mês após terem recebido  o último resultado de CV acima de 1000 cópia e terem  3 sessões consecutivas de APSS/PP (AMA) Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory13P4CohortQueries
                    .findPregnantWhoHaveRequestedCVCategory13P4Denumerator(),
                "CAT13P4PregnantDENUMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P4PregnantNUMINATOR",
        "13.18: Mulheres gravidas na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º "
            + "mês após terem recebido  o último resultado de CV acima de 1000 cópia e terem  3 sessões consecutivas de APSS/PP (AMA)",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory13P4CohortQueries
                    .findPatientsWhoPregnantReceivedResultMoreThan1000CVCategory13P4Numerator(),
                "CAT13P4PregnantNUMINATOR",
                mappings),
            mappings),
        "");
  }
}
