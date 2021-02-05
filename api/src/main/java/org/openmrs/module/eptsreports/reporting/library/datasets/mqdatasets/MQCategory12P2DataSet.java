package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import java.util.Date;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory12P2CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory12P2DataSet {

  @Autowired private MQCategory12P2CohortQueries mQCategory12P2CohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT12ADULTDENOMINATORFIRSTLINE",
        "12.3: Adultos (15/+anos) na 1ª linha que iniciaram TARV há 12 meses e que continuam activos na US Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory12P2CohortQueries
                    .findPatientsInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDDenominator(),
                "CAT12ADULTDENOMINATORFIRSTLINE",
                mappings),
            mappings),
        "ageMqNewARTRevisionDate=15+");

    dataSetDefinition.addColumn(
        "CAT12ADULTNUMERATORFIRSTLINE",
        "12.3: Adultos (15/+anos) na 1ª linha que iniciaram TARV há 12 meses e que continuam activos na US Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory12P2CohortQueries
                    .findPatientsInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDNumerator(),
                "CAT12ADULTNUMERATORFIRSTLINE",
                mappings),
            mappings),
        "ageMqNewARTRevisionDate=15+");

    dataSetDefinition.addColumn(
        "CAT12ADULTDENOMINATORSECONDLINE",
        "12.4: Adultos (15/+anos) na 2ª linha que iniciaram TARV há 12 meses e que continuam activos na US Denominador ",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory12P2CohortQueries
                    .findPatietsInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnEDenominator(),
                "CAT12ADULTDENOMINATORSECONDLINE",
                mappings),
            mappings),
        "ageMqNewARTRevisionDate=15+");

    dataSetDefinition.addColumn(
        "CAT12ADULTNUMERATORSECONDLINE",
        "12.4: Adultos (15/+anos) na 2ª linha que iniciaram TARV há 12 meses e que continuam activos na US Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory12P2CohortQueries
                    .findPatientsInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnDNumerator(),
                "CAT12ADULTNUMERATORSECONDLINE",
                mappings),
            mappings),
        "ageMqNewARTRevisionDate=15+");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENDENOMINATORFIRSTLINE",
        "12.7: Crianças (0-14 anos) na 1ª linha que iniciaram TARV há 12 meses e que continuam activos na US Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory12P2CohortQueries
                    .findPatientsInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDDenominator(),
                "CAT12CHILDRENDENOMINATORFIRSTLINE",
                mappings),
            mappings),
        "ageMqNewARTRevisionDate=15-");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENNUMERATORFIRSTLINE",
        "12.7: Crianças (0-14 anos) na 1ª linha que iniciaram TARV há 12 meses e que continuam activos na US Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory12P2CohortQueries
                    .findPatientsInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDNumerator(),
                "CAT12CHILDRENNUMERATORFIRSTLINE",
                mappings),
            mappings),
        "ageMqNewARTRevisionDate=15-");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENDENOMINATORSECONDLINE",
        "12.8: Crianças (0-14 anos) na 2ª linha que iniciaram TARV há 12 meses e que continuam activos na US Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory12P2CohortQueries
                    .findPatietsInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnEDenominator(),
                "CAT12CHILDRENDENOMINATORSECONDLINE",
                mappings),
            mappings),
        "ageMqNewARTRevisionDate=15-");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENNUMERATORSECONDLINE",
        "12.8: Crianças (0-14 anos) na 2ª linha que iniciaram TARV há 12 meses e que continuam activos na US Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory12P2CohortQueries
                    .findPatientsInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnDNumerator(),
                "CAT12CHILDRENNUMERATORSECONDLINE",
                mappings),
            mappings),
        "ageMqNewARTRevisionDate=15-");

    dataSetDefinition.addColumn(
        "CAT12PREGNANTDENOMINATORFIRSTLINE",
        "12.11: Mulheres gravidas na 1ª linha que iniciaram TARV há 12 meses e que continuam activos na US Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory12P2CohortQueries
                    .findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDDenominator(),
                "CAT12PREGNANTDENOMINATORFIRSTLINE",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12PREGNANTNUMERATORFIRSTLINE",
        "12.11: Mulheres gravidas na 1ª linha que iniciaram TARV há 12 meses e que continuam activos na US Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory12P2CohortQueries
                    .findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDNumerator(),
                "CAT12PREGNANTNUMERATORFIRSTLINE",
                mappings),
            mappings),
        "");
  }

  public CohortIndicator setIndicatorWithAllParameters(
      final CohortDefinition cohortDefinition, final String indicatorName, final String mappings) {
    final CohortIndicator indicator =
        this.eptsGeneralIndicator.getIndicator(
            indicatorName, EptsReportUtils.map(cohortDefinition, mappings));

    indicator.addParameter(new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    indicator.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    indicator.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    indicator.addParameter(new Parameter("location", "location", Date.class));

    return indicator;
  }
}
