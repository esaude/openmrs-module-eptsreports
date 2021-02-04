package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory4CohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory4Dataset extends MQGenericDataSet {

  @Autowired private MQCategory4CohortQueries mQCategory4CohortQueries;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT4CHIDRENDENOMINATOR",
        "4.1: Crianças em TARV com o estado (grau) da avaliação nutricional registado na última consulta clínica Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory4CohortQueries
                    .findPatientsNewlyEnrolledInAPeriodCategory4RF15Denominator(),
                "CAT4CHIDRENDENOMINATOR",
                mappings),
            mappings),
        "ageMqNewART=15-");

    dataSetDefinition.addColumn(
        "CAT4CHIDRENNUMERATOR",
        "4.1: Crianças em TARV com o estado (grau) da avaliação nutricional registado na última consulta clínica Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory4CohortQueries
                    .findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodCategory4RF16Numerator(),
                "CAT4CHIDRENNUMERATOR",
                mappings),
            mappings),
        "ageMqNewART=15-");

    dataSetDefinition.addColumn(
        "CAT4PregnantDENOMINATOR",
        "4.2: MG em TARV com o estado (grau) da avaliação nutricional registado na última consulta clínica Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory4CohortQueries
                    .findPregnantHumansNewlyEnrolledOnARTInInclusionPeriodCategory4RF17Denominator(),
                "CAT4PregnantDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT4PregnantNUMERATOR",
        "4.2: MG em TARV com o estado (grau) da avaliação nutricional registado na última consulta clínica Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory4CohortQueries
                    .findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentCategory4RF18(),
                "CAT4PregnantNUMERATOR",
                mappings),
            mappings),
        "");
  }
}
