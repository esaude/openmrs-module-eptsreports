package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory15CohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory15DataSet extends MQAbstractDataSet {

  @Autowired private MQCategory15CohortQueries mqCohortCategory15Queries;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_1_2_3_4_DENOMINATOR",
        "15.1: Adultos (15/+anos) inscritos há 12 meses em algum MDS  (DT ou GAAC) que continuam activos em TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries
                    .getDenominatorCategory15_Indicator_1_And_2_and_3_And_4(),
                "CAT15INDICATOR_1_2_3_4_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_5_7_9_11_DENOMINATOR",
        "15.5: Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) que continuam activos em TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries
                    .getDenominatorCategory15_Indicator_5_And_7_And_9_And_11(),
                "CAT15INDICATOR_5_7_9_11_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=2-9");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_6_8_10_12_DENOMINATOR",
        "15.6: Crianças (10-14 anos) inscritas há 12 em algum MDS (DT) que continuam activos em TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries
                    .getDenominatorCategory15_Indicator_6_And_8_And_10_And_12(),
                "CAT15INDICATOR_6_8_10_12_DENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=10-14");

    // Categoria 15 NUMERATOR
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_1_NUMERATOR",
        "15.1: Adultos (15/+anos) inscritos há 12 meses em algum MDS  (DT ou GAAC) que continuam activos em TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_1(),
                "CAT15INDICATOR_1_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_2_NUMERATOR",
        "15.2: Adultos (15/+anos) inscritos há pelo menos 12 meses em algum MDS (DT ou GAAC) com pedido de pelo menos uma CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_2(),
                "CAT15INDICATOR_2_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_3_NUMERATOR",
        "15.3: Adultos (15/+anos) inscritos há 12 meses em algum MDS (DT ou GAAC) que receberam pelo menos um resultado de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_3(),
                "CAT15INDICATOR_3_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_4_NUMERATOR",
        "15.4: Adultos (15/+anos) inscritos há 12 meses em algum MDS (DT ou GAAC) com CV <1000 Cópias Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_4(),
                "CAT15INDICATOR_4_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_5_NUMERATOR",
        "15.5: Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) que continuam activos em TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_5(),
                "CAT15INDICATOR_5_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=2-9");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_6_NUMERATOR",
        "15.6: Crianças (10-14 anos) inscritas há 12 em algum MDS (DT) que continuam activos em TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_6(),
                "CAT15INDICATOR_6_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=10-14");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_7_NUMERATOR",
        "15.7: Crianças (2-9 anos de idade) inscritas há 12 meses em algum MDS (DT) com pedido de pelo menos uma CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_7(),
                "CAT15INDICATOR_7_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=2-9");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_8_NUMERATOR",
        "15.8: Crianças (10-14 anos de idade) inscritas há 12 meses em algum MDS (DT) com pedido de pelo menos uma CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_8(),
                "CAT15INDICATOR_8_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=10-14");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_9_NUMERATOR",
        "15.9: Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) que receberam pelo menos um resultado de CV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_9(),
                "CAT15INDICATOR_9_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=2-9");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_10_NUMERATOR",
        "15.10: Crianças (10-14 anos) inscritas há 12 meses em algum MDS (DT) que receberam pelo menos um resultado de CV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_10(),
                "CAT15INDICATOR_10_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=10-14");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_11_NUMERATOR",
        "15.11: Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) com CV <1000 Cópias Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_11(),
                "CAT15INDICATOR_11_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=2-9");

    dataSetDefinition.addColumn(
        "CAT15INDICATOR_12_NUMERATOR",
        "15.12: Crianças (10-14 anos) inscritas há 12 meses em algum MDS (DT) com CV <1000 Cópias Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_12(),
                "CAT15INDICATOR_12_NUMERATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=10-14");
  }
}
