package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory13P3CohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory13P3DataSet extends MQAbstractDataSet {

  @Autowired private MQCategory13P3CohortQueries mqCohortQueries13_3;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_2_DENOMINATOR",
        "13.2: Adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início do TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Denominador(),
                "CAT13_PART_3_13_2_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_2_NUMERATOR",
        "13.2: Adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início do TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Numerador(),
                "CAT13_PART_3_13_2_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_9_DENOMINATOR",
        "13.9: Crianças  (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da "
            + "Carga Viral entre o sexto e o nono mês após o início do TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Denominador(),
                "CAT13_PART_3_13_9_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=0-4");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_9_NUMERATOR",
        "13.9: Crianças  (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da "
            + "Carga Viral entre o sexto e o nono mês após o início do TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Numerador(),
                "CAT13_PART_3_13_9_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=0-4");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_10_DENOMINATOR",
        "13.10: Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da "
            + "Carga Viral entre o sexto e o nono mês após o início do TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Denominador(),
                "CAT13_PART_3_13_10_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=5-9");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_10_NUMERATOR",
        "13.10: Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da "
            + "Carga Viral entre o sexto e o nono mês após o início do TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Numerador(),
                "CAT13_PART_3_13_10_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=5-9");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_11_DENOMINATOR",
        "13.11: Crianças  (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da "
            + "Carga Viral entre o sexto e o nono mês após o início do TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Denominador(),
                "CAT13_PART_3_13_11_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=10-14");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_11_NUMERATOR",
        "13.11: Crianças  (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da"
            + " Carga Viral entre o sexto e o nono mês após o início do TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInFirstLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13Numerador(),
                "CAT13_PART_3_13_11_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=10-14");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_5_DENOMINATOR",
        "13.5: Adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o "
            + "sexto e o nono mês após o início da 2a linha de TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Denominador_13_5(),
                "CAT13_PART_3_13_5_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_5_NUMERATOR",
        "13.5: Adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre "
            + "o sexto e o nono mês após o início da 2a linha de TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Numerador_13_5(),
                "CAT13_PART_3_13_5_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_14_DENOMINATOR",
        "13.14: Crianças  na 2a linha de TARV que receberam o resultado da Carga Viral entre o "
            + "sexto e o nono mês após o início da 2a linha de TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Denominador_13_5(),
                "CAT13_PART_3_13_14_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=2-14");

    dataSetDefinition.addColumn(
        "CAT13_PART_3_13_14_NUMERATOR",
        "13.14: Crianças  na 2a linha de TARV que receberam o resultado da Carga Viral entre o "
            + "sexto e o nono mês após o início da 2a linha de TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries13_3
                    .findPatientsInSecondLineTherapheuticWhoReceivedViralChargeBetweenSixthAndNinthMonthAfterARTStartCategory13_3_Numerador_13_5(),
                "CAT13_PART_3_13_14_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=2-14");
  }
}
