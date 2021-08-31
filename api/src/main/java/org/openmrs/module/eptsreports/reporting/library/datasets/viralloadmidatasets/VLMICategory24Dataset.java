package org.openmrs.module.eptsreports.reporting.library.datasets.viralloadmidatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.vlmi.ViralLoadLMICohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VLMICategory24Dataset extends MQAbstractDataSet {

  @Autowired private ViralLoadLMICohortQueries viralLoadLMICohortQueries;

  public void constructTMiDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT2404DENOMINATOR",
        "Número de pacientes (0-4 anos de idade) na primeira linha de TARV que receberam 3 sessões consecutivas de APSS/PP após primeiro resultado acima de 1000 cópias 9 meses atrás, com pedido de segunda carga viral registado e resultado de segunda carga viral acima de 1000 cópias.\n"
            + " - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24Denominator(),
                "CAT2404DENOMINATOR",
                mappings),
            mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT2459DENOMINATOR",
        "Número de pacientes (5-9 anos de idade) na primeira linha de TARV que receberam 3 sessões consecutivas de APSS/PP após primeiro resultado acima de 1000 cópias 9 meses atrás, com pedido de segunda carga viral registado e resultado de segunda carga viral acima de 1000 cópias.\n"
            + " - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24Denominator(),
                "CAT2459DENOMINATOR",
                mappings),
            mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT241014DENOMINATOR",
        "Número de pacientes (10-14 anos de idade) na primeira linha de TARV que receberam 3 sessões consecutivas de APSS/PP após primeiro resultado acima de 1000 cópias 9 meses atrás, com pedido de segunda carga viral registado e resultado de segunda carga viral acima de 1000 cópias.\n"
            + " - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                viralLoadLMICohortQueries
                    .findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24Denominator(),
                "CAT241014DENOMINATOR",
                mappings),
            mappings),
        "age=10-14");

    dataSetDefinition.addColumn(
        "CAT2415PLUSDENOMINATOR",
        "Número de pacientes (15+ anos de idade) na primeira linha de TARV que receberam 3 sessões consecutivas de APSS/PP após primeiro resultado acima de 1000 cópias 9 meses atrás, com pedido de segunda carga viral registado e resultado de segunda carga viral acima de 1000 cópias.\n"
            + " - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24Denominator(),
                "CAT2415PLUSDENOMINATOR",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT2404NUMERATOR",
        "Número de pacientes (0-4 anos de idade) na 1a Linha TARV que receberam 3 sessões consecutivas de APSS/PP após o primeiro resultado acima de 1000 cópias 9 meses atrás, com segundo resultado acima de 1000 cópias e que mudaram para a segunda linha de TARV - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24_1Numerator(),
                "CAT2404NUMERATOR",
                mappings),
            mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT2459NUMERATOR",
        "Número de pacientes (5-9 anos de idade) na 1a Linha TARV que receberam 3 sessões consecutivas de APSS/PP após o primeiro resultado acima de 1000 cópias 9 meses atrás, com segundo resultado acima de 1000 cópias e que mudaram para a segunda linha de TARV - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24_1Numerator(),
                "CAT2459NUMERATOR",
                mappings),
            mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT241014NUMERATOR",
        "Número de pacientes (10-14 anos de idade) na 1a Linha TARV que receberam 3 sessões consecutivas de APSS/PP após o primeiro resultado acima de 1000 cópias 9 meses atrás, com segundo resultado acima de 1000 cópias e que mudaram para a segunda linha de TARV - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                viralLoadLMICohortQueries
                    .findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24_1Numerator(),
                "CAT241014NUMERATOR",
                mappings),
            mappings),
        "age=10-14");

    dataSetDefinition.addColumn(
        "CAT2415PLUSNUMERATOR",
        "Número de pacientes (15+ anos de idade) na 1a Linha TARV que receberam 3 sessões consecutivas de APSS/PP após o primeiro resultado acima de 1000 cópias 9 meses atrás, com segundo resultado acima de 1000 cópias e que mudaram para a segunda linha de TARV - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSwitchToSecondLineARTAfterSecondHighViralLoadResultVL_FR24_1Numerator(),
                "CAT2415PLUSNUMERATOR",
                mappings),
            mappings),
        "age=15+");
  }
}
