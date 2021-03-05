package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory11CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory13P4CohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory13P4DataSet extends MQAbstractDataSet {

  @Autowired private MQCategory13P4CohortQueries mQCategory13P4CohortQueries;
  @Autowired private MQCategory11CohortQueries mQCategory11CohortQueries;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT13P4AdultDENUMINATOR",
        "13.3: Adultos (15/+anos) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º "
            + "mês após terem recebido  o último resultado de CV acima de 1000 e terem  3 sessões consecutivas de APSS/PP (AMA) Denominador ",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11CohortQueries
                    .findPatietsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11Denominator(),
                "CAT13P4AdultDENUMINATOR",
                mappings),
            mappings),
        "ageOnCV=15+");

    dataSetDefinition.addColumn(
        "CAT13P4AdultNUMINATOR",
        "13.3: Adultos (15/+anos) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês "
            + "após terem recebido  o último resultado de CV acima de 1000 e terem  3 sessões consecutivas de APSS/PP (AMA) Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory13P4CohortQueries
                    .findPatientsWhoReceivedResultMoreThan1000CVCategory13P4Numerator(),
                "CAT13P4AdultNUMINATOR",
                mappings),
            mappings),
        "ageOnCV=15+");

    dataSetDefinition.addColumn(
        "CAT13P4ChildrenDENUMINATOR",
        "13.12: Crianças (>2 anos de idade) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês "
            + "após terem recebido  o último resultado de CV acima de 1000 cópia e terem  3 sessões consecutivas de APSS/PP (AMA) Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11CohortQueries
                    .findPatietsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11Denominator(),
                "CAT13P4ChildrenDENUMINATOR",
                mappings),
            mappings),
        "ageMqNewART=3-14");

    dataSetDefinition.addColumn(
        "CAT13P4ChildrenNUMINATOR",
        "13.12: Crianças (>2 anos de idade) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês "
            + "após terem recebido  o último resultado de CV acima de 1000 cópia e terem  3 sessões consecutivas de APSS/PP (AMA) Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory13P4CohortQueries
                    .findPatientsWhoReceivedResultMoreThan1000CVCategory13P4Numerator(),
                "CAT13P4ChildrenNUMINATOR",
                mappings),
            mappings),
        "ageMqNewART=3-14");

    dataSetDefinition.addColumn(
        "CAT13P4PregnantDENUMINATOR",
        "13.18: Mulheres Gravidas na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º "
            + "mês após terem recebido  o último resultado de CV acima de 1000 cópia e terem  3 sessões consecutivas de APSS/PP (AMA) Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory13P4CohortQueries
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
                this.mQCategory13P4CohortQueries
                    .findPatientsWhoPregnantReceivedResultMoreThan1000CVCategory13P4Numerator(),
                "CAT13P4PregnantNUMINATOR",
                mappings),
            mappings),
        "");
  }
}
