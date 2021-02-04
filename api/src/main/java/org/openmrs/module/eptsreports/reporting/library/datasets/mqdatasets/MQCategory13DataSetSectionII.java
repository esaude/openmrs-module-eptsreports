package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory13Section2CohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory13DataSetSectionII extends MQGenericDataSet {

  @Autowired private MQCategory13Section2CohortQueries mqCategory13Section2CohortQueries;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSDENOMINATOR_SECTION1_2",
        "13.4: Adultos (15/+anos) na  2a linha de TARV elegíveis a CV  com registo de pedido de CV feito pelo clínico Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCategory13Section2CohortQueries.findDenominatorCategory13SectionIIB(),
                "CAT13CV15PLUSDENOMINATOR_SECTION1_2",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV24DENOMINATOR_SECTION1_2",
        "13.13: Crianças (2-14) na  2ª linha de TARV elegíveis ao pedido de CV  e com registo de pedido de CV feito pelo clínico Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCategory13Section2CohortQueries.findDenominatorCategory13SectionIIB(),
                "CAT13CV24DENOMINATOR_SECTION1_2",
                mappings),
            mappings),
        "age=2-14");

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSNUMERATOR_SECTION1_2",
        "13.4: Adultos (15/+anos) na 2a linha de TARV elegíveis a CV com registo de pedido de CV feito pelo clínico Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCategory13Section2CohortQueries.findFinalNumeratorCategory13SectionIIC(),
                "CAT13CV15PLUSNUMERATOR_SECTION1_2",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV24NUMERATOR_SECTION1_2",
        "13.13: Crianças na 2ª linha de TARV elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico Numerator",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCategory13Section2CohortQueries.findFinalNumeratorCategory13SectionIIC(),
                "CAT13CV24NUMERATOR_SECTION1_2",
                mappings),
            mappings),
        "age=2-14");
  }
}
