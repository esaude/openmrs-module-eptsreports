package org.openmrs.module.eptsreports.reporting.library.datasets.viralloadmidatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.vlmi.ViralLoadLMICohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VLMICategory22Dataset extends MQAbstractDataSet {

  @Autowired private ViralLoadLMICohortQueries viralLoadLMICohortQueries;

  public void constructTMiDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT2204DENOMINATOR",
        "Número de pacientes (0-4 anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado. - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22Denominator(),
                "CAT2204DENOMINATOR",
                mappings),
            mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT2259DENOMINATOR",
        "Número de pacientes (5-9 anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado. - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22Denominator(),
                "CAT2259DENOMINATOR",
                mappings),
            mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT221014DENOMINATOR",
        "Número de pacientes (10-14 anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado. - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                viralLoadLMICohortQueries
                    .findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22Denominator(),
                "CAT221014DENOMINATOR",
                mappings),
            mappings),
        "age=10-14");

    dataSetDefinition.addColumn(
        "CAT2215PLUSDENOMINATOR",
        "Número de pacientes (15+ anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado. - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22Denominator(),
                "CAT2215PLUSDENOMINATOR",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT2204NUMERATOR",
        "Número de pacientes (0-4 anos de idade) no na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS/PP após primeiro resultado acima de 1000 cópias 7 meses atrás, com pedido de segunda carga viral registado e segundo resultado de carga viral acima de 1000 cópias - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22_1Numerator(),
                "CAT2204NUMERATOR",
                mappings),
            mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT2259NUMERATOR",
        "Número de pacientes (5-9 anos de idade) no na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS/PP após primeiro resultado acima de 1000 cópias 7 meses atrás, com pedido de segunda carga viral registado e segundo resultado de carga viral acima de 1000 cópias - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22_1Numerator(),
                "CAT2259NUMERATOR",
                mappings),
            mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT221014NUMERATOR",
        "Número de pacientes (10-14 anos de idade) no na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS/PP após primeiro resultado acima de 1000 cópias 7 meses atrás, com pedido de segunda carga viral registado e segundo resultado de carga viral acima de 1000 cópias - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                viralLoadLMICohortQueries
                    .findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22_1Numerator(),
                "CAT221014NUMERATOR",
                mappings),
            mappings),
        "age=10-14");

    dataSetDefinition.addColumn(
        "CAT2215PLUSNUMERATOR",
        "Número de pacientes (15+ anos de idade) no na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS/PP após primeiro resultado acima de 1000 cópias 7 meses atrás, com pedido de segunda carga viral registado e segundo resultado de carga viral acima de 1000 cópias - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithSecondHighViralLoadResulAfterAPSSSessionsVLFR22_1Numerator(),
                "CAT2215PLUSNUMERATOR",
                mappings),
            mappings),
        "age=15+");
  }
}
