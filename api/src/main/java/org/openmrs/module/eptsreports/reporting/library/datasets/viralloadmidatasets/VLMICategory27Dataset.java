package org.openmrs.module.eptsreports.reporting.library.datasets.viralloadmidatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.vlmi.ViralLoadLMICohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VLMICategory27Dataset extends MQAbstractDataSet {

  @Autowired private ViralLoadLMICohortQueries viralLoadLMICohortQueries;

  public void constructTMiDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT27DENOMINATOR",
        "Número de MG na primeira linha de TARV que receberam 3 sessões consecutivas de APSS/PP após primeiro resultado acima de 1000 cópias 9 meses atrás, com pedido de segunda carga viral registado e resultado de segunda carga viral acima de 1000 cópias.\n"
            + "\n"
            + " - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPregnantWomenWithSwitchToSecondLineARTAfterSecondHighViralLoadResult_VL_FR27Denominator(),
                "CAT27DENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT27NUMERATOR",
        "Número de MG na 1a Linha TARV que receberam 3 sessões consecutivas de APSS/PP após o primeiro resultado acima de 1000 cópias 9 meses atrás, com segundo resultado acima de 1000 cópias e que mudaram para a segunda linha de TARV.\n"
            + " - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.viralLoadLMICohortQueries
                    .findPregnantWomenWithSwitchToSecondLineARTAfterSecondHighViralLoadResult_VL_FR27_1_Numerator(),
                "CAT27NUMERATOR",
                mappings),
            mappings),
        "");
  }
}
