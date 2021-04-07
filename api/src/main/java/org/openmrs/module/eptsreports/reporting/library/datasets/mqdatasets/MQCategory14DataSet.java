package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory14CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MQCategory14DataSet extends MQAbstractDataSet {

  @Autowired private MQCategory14CohortQueries mqCohortQueryCategory14;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT14_15PLUS_INDICATOR_1_DENOMINATOR",
        "14.1: Adultos (15/+anos) em TARV com supressão viral Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueryCategory14.getDenominatorCategory14Indicator(),
                "CAT14_15PLUS_INDICATOR_1_DENOMINATOR",
                mappings),
            mappings),
        "ageMq=15+");

    dataSetDefinition.addColumn(
        "CAT14_014_INDICATOR_1_DENOMINATOR",
        "14.2: Crianças (0-14 anos) em TARV com supressão viral Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueryCategory14.getDenominatorCategory14Indicator(),
                "CAT14_014_INDICATOR_1_DENOMINATOR",
                mappings),
            mappings),
        "ageMq=0-14");

    dataSetDefinition.addColumn(
        "CAT14_MG_INDICATOR_1_DENOMINATOR",
        "14.13:Mulher Gravida em TARV com supressão viral Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueryCategory14.getPregnantDenominatorCategory14Indicator(),
                "CAT14_MG_INDICATOR_1_DENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT14_ML_INDICATOR_1_DENOMINATOR",
        "14.4: Mulher Lactante em TARV com supressão viral Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueryCategory14.getBreastfeedingDenominatorCategory14Indicator(),
                "CAT14_ML_INDICATOR_1_DENOMINATOR",
                mappings),
            mappings),
        "");

    // Numerator

    dataSetDefinition.addColumn(
        "CAT14_15PLUS_INDICATOR_1_NUMERATOR",
        "14.1: Adultos (15/+anos) em TARV com supressão viral Numerator",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueryCategory14.getNumeratorCategory14Indicator(),
                "CAT14_15PLUS_INDICATOR_1_NUMERATOR",
                mappings),
            mappings),
        "ageMq=15+");

    dataSetDefinition.addColumn(
        "CAT14_014_INDICATOR_1_NUMERATOR",
        "14.2: Crianças (0-14 anos) em TARV com supressão viral Numerator",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueryCategory14.getNumeratorCategory14Indicator(),
                ".CAT14_014_INDICATOR_1_NUMERATOR",
                mappings),
            mappings),
        "ageMq=0-14");

    dataSetDefinition.addColumn(
        "CAT14_MG_INDICATOR_1_NUMERATOR",
        "14.13:Mulher Gravida em TARV com supressão viral Numerator",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueryCategory14.getPregnantNumeratorCategory14Indicator(),
                "CAT14_MG_INDICATOR_1_NUMERATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT14_ML_INDICATOR_1_NUMERATOR",
        "14.4: Mulher Lactante em TARV com supressão viral Numerator",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortQueryCategory14.getBreastfeedingNumeratorCategory14Indicator(),
                "CAT14_ML_INDICATOR_1_NUMERATOR",
                mappings),
            mappings),
        "");
  }
}
