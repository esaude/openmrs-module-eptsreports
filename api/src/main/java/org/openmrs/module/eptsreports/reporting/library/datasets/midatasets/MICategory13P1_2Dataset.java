package org.openmrs.module.eptsreports.reporting.library.datasets.midatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mi.MICategory13P1_2CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MICategory13P1_2Dataset extends MQAbstractDataSet {

  @Autowired private MICategory13P1_2CohortQueries mICategory13P1_2CohortQueries;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSDENOMINATOR_SECTION1_2",
        "13.4: Adultos (15/+anos) na 2a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_2CohortQueries.findDenominatorCategory13SectionIIB(),
                "CAT13CV15PLUSDENOMINATOR_SECTION1_2",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV24DENOMINATOR_SECTION1_2",
        "13.13: Crianças na 2a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_2CohortQueries.findDenominatorCategory13SectionIIB(),
                "CAT13CV24DENOMINATOR_SECTION1_2",
                mappings),
            mappings),
        "age=2-14");

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSNUMERATOR_SECTION1_2",
        "13.4: Adultos (15/+anos) na 2a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_2CohortQueries.findFinalNumeratorCategory13SectionIIC(),
                "CAT13CV15PLUSNUMERATOR_SECTION1_2",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV24NUMERATOR_SECTION1_2",
        "13.13: Crianças na 2a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerator",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_2CohortQueries.findFinalNumeratorCategory13SectionIIC(),
                "CAT13CV24NUMERATOR_SECTION1_2",
                mappings),
            mappings),
        "age=2-14");
  }
}
