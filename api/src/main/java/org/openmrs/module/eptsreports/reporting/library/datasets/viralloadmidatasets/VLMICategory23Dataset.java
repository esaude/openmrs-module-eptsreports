package org.openmrs.module.eptsreports.reporting.library.datasets.viralloadmidatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.vlmi.ViralLoadLMICohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VLMICategory23Dataset extends MQAbstractDataSet {

  @Autowired private ViralLoadLMICohortQueries viralLoadLMICohortQueries;

  public void constructTMiDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT2304DENOMINATOR",
        "Número de pacientes (0-4 anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23Denominator(),
                "CAT2304DENOMINATOR",
                mappings),
            mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT2359DENOMINATOR",
        "Número de pacientes (5-9 anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23Denominator(),
                "CAT2359DENOMINATOR",
                mappings),
            mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT231014DENOMINATOR",
        "Número de pacientes (10-14 anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                viralLoadLMICohortQueries
                    .findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23Denominator(),
                "CAT231014DENOMINATOR",
                mappings),
            mappings),
        "age=10-14");

    dataSetDefinition.addColumn(
        "CAT2315PLUSDENOMINATOR",
        "Número de pacientes (15+ anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23Denominator(),
                "CAT2315PLUSDENOMINATOR",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT2304NUMERATOR",
        "Número de pacientes (0-4 anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23_1Numerator(),
                "CAT2304NUMERATOR",
                mappings),
            mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT2359NUMERATOR",
        "Número de pacientes (5-9 anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23_1Numerator(),
                "CAT2359NUMERATOR",
                mappings),
            mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT231014NUMERATOR",
        "Número de pacientes (10-14 anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                viralLoadLMICohortQueries
                    .findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23_1Numerator(),
                "CAT231014NUMERATOR",
                mappings),
            mappings),
        "age=10-14");

    dataSetDefinition.addColumn(
        "CAT2315PLUSNUMERATOR",
        "Número de pacientes (15+ anos de idade) na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPatientsWithlOWSecondViralLoadResulAfterAPSSSessionsVLFR23_1Numerator(),
                "CAT2315PLUSNUMERATOR",
                mappings),
            mappings),
        "age=15+");
  }
}
