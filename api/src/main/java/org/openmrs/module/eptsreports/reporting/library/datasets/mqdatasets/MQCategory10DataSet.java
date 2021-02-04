package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory10CohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory10DataSet extends MQGenericDataSet {

  @Autowired private MQCategory10CohortQueries mqCohortQueries10;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT10_10_3_DENOMINATOR",
        "1.6: Crianças com PCR positivo  para HIV  que iniciaram TARV dentro de 2 semanas "
            + "após o diagnóstico/entrega do resultado ao cuidador Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries10
                    .findPatientsWithPCRTestPositiveForHIVAndStartARTWithinTwoWeeksCategory10_Denominador_10_3(),
                "CAT10_10_3_DENOMINATOR",
                mappings),
            mappings),
        "ageMqNewART=0-18M");

    dataSetDefinition.addColumn(
        "CAT10_10_3_NUMERATOR",
        "1.6: Crianças com PCR positivo  para HIV  que iniciaram TARV dentro de 2 semanas"
            + " após o diagnóstico/entrega do resultado ao cuidador Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueries10
                    .findPatientsWithPCRTestPositiveForHIVAndStartARTWithinTwoWeeksCategory10_Numerador_10_3(),
                "CAT10_10_3_NUMERATOR",
                mappings),
            mappings),
        "ageMqNewART=0-18M");
  }
}
