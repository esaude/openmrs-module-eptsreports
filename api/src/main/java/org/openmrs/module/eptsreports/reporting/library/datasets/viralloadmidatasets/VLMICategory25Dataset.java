package org.openmrs.module.eptsreports.reporting.library.datasets.viralloadmidatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.vlmi.ViralLoadLMICohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VLMICategory25Dataset extends MQAbstractDataSet {

  @Autowired private ViralLoadLMICohortQueries viralLoadLMICohortQueries;

  public void constructTMiDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT25DENOMINATOR",
        "Número de MG na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS / PP após primeiro resultado acima de 1000 cópias 7 meses atrás e com pedido de segunda carga viral registado.\n"
            + " - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPregnantWomenWithSecondHighViralLoadResultAfterAPSSSessionsVL_FR25Denominator(),
                "CAT25DENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT25NUMERATOR",
        "Número de MG no na 1a Linha de TARV que receberam 3 sessões consecutivas de APSS/PP após primeiro resultado acima de 1000 cópias 7 meses atrás, com pedido de segunda carga viral registado e segundo resultado de carga viral acima de 1000 cópias.\n"
            + " - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPregnantWomenWithSecondHighViralLoadResultAfterAPSSSessionsVL_FR25_1_Numerator(),
                "CAT25NUMERATOR",
                mappings),
            mappings),
        "");
  }
}
