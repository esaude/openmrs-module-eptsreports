package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory5CohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory5DataSet extends MQGenericDataSet {

  @Autowired private MQCategory5CohortQueries mQCategory5CohortQueries;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT5CHIDRENDENOMINATOR",
        "5.1: Crianças em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory5CohortQueries
                    .findPatientsWhoAreNewEnrolledOnArtAndHaveFirstConsultMarkedDAMDAGInclusionPeriodCategory5RF19Denominator(),
                "CAT5CHIDRENDENOMINATOR",
                mappings),
            mappings),
        "ageMqNewART=15-");

    dataSetDefinition.addColumn(
        "CAT5CHIDRENNUMERATOR",
        "5.1: Crianças em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory5CohortQueries
                    .findPatientsWhoAreNewEnrolledOnArtAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriodCategory5FR20Numerator(),
                "CAT5CHIDRENNUMERATOR",
                mappings),
            mappings),
        "ageMqNewART=15-");

    dataSetDefinition.addColumn(
        "CAT5PregnantDENOMINATOR",
        "5.2: MG em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory5CohortQueries
                    .findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGCategory5RF21Denominator(),
                "CAT5PregnantDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT5PregnantNUMERATOR",
        "5.2: MG em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory5CohortQueries
                    .findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGAndATPUCategory5RF22Denominator(),
                "CAT5PregnantNUMERATOR",
                mappings),
            mappings),
        "");
  }
}
