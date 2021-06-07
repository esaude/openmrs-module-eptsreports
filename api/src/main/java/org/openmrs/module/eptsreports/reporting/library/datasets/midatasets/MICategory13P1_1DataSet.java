package org.openmrs.module.eptsreports.reporting.library.datasets.midatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mi.MICategory13P1_1CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MICategory13P1_1DataSet extends MQAbstractDataSet {

  @Autowired private MICategory13P1_1CohortQueries mICategory13P1_1CohortQueries;

  public void constructTMiDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSDENOMINATOR",
        "13.1: Adultos (15/+anos) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador ",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findDenominatorCategory13SectionIB(),
                "CAT13CV15PLUSDENOMINATOR",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV04DENOMINATOR",
        "13.6: Crianças (0-4 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findDenominatorCategory13SectionIB(),
                "CAT13CV04DENOMINATOR",
                mappings),
            mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT13CV59DENOMINATOR",
        "13.7: Crianças (5-9 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findDenominatorCategory13SectionIB(),
                "CAT13CV59DENOMINATOR",
                mappings),
            mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT13CV1014DENOMINATOR",
        "13.8: Crianças (10-14 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                mICategory13P1_1CohortQueries.findDenominatorCategory13SectionIB(),
                "CAT13CV1014DENOMINATOR",
                mappings),
            mappings),
        "age=10-14");

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSNUMERATOR",
        "13.1: Adultos (15/+anos) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findFinalNumeratorCategory13SectionIC(),
                "CAT13CV15PLUSNUMERATOR",
                mappings),
            mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV04NUMERATOR",
        "13.6: Crianças (0-4 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findFinalNumeratorCategory13SectionIC(),
                "CAT13CV04NUMERATOR",
                mappings),
            mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT13CV59NUMERATOR",
        "13.7: Crianças (5-9 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findFinalNumeratorCategory13SectionIC(),
                "CAT13CV59NUMERATOR",
                mappings),
            mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT13CV1014NUMERATOR",
        "13.8: Crianças (10-14 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão e que eram elegíveis ao pedido de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory13P1_1CohortQueries.findFinalNumeratorCategory13SectionIC(),
                "CAT13CV1014NUMERATOR",
                mappings),
            mappings),
        "age=10-14");
  }
}
