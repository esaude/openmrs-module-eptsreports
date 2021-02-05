package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import java.util.Date;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory3CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MQCategory3DataSet {

  @Autowired private MQCategory3CohortQueries mQCohortQueryCategory3;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT3ADULTODENOMINATOR",
        "3.1: Adultos (15/+anos) HIV+ em TARV que tiveram consulta clínica dentro de 7 dias após  diagnóstico Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCohortQueryCategory3
                    .findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInCategory3RF11Denominator(),
                "CAT3ADULTODENOMINATOR",
                mappings),
            mappings),
        "ageMqNewART=15+");

    dataSetDefinition.addColumn(
        "CAT3ADULTONUMERATOR",
        "3.1: Adultos (15/+anos) HIV+ em TARV que tiveram consulta clínica dentro de 7 dias após  diagnóstico Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCohortQueryCategory3
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3RF12Numerator(),
                "CAT3ADULTONUMERATOR",
                mappings),
            mappings),
        "ageMqNewART=15+");

    dataSetDefinition.addColumn(
        "CAT3CHIDRENDENOMINATOR",
        "3.2: Crianças  HIV+ que tiveram consulta clínica dentro de 7 dias após diagnóstico Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCohortQueryCategory3
                    .findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInCategory3RF11Denominator(),
                "CAT3CHIDRENDENOMINATOR",
                mappings),
            mappings),
        "ageMqNewART=15-");

    dataSetDefinition.addColumn(
        "CAT3CHIDRENNUMERATOR",
        "3.2: Crianças  HIV+ que tiveram consulta clínica dentro de 7 dias após diagnóstico Numerador ",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCohortQueryCategory3
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildrenAndHaveFirstConsultInclusionPeriodCategory3RF14Numerator(),
                "CAT3CHIDRENNUMERATOR",
                mappings),
            mappings),
        "ageMqNewART=15-");
  }

  public CohortIndicator setIndicatorWithAllParameters(
      final CohortDefinition cohortDefinition, final String indicatorName, final String mappings) {
    final CohortIndicator indicator =
        this.eptsGeneralIndicator.getIndicator(
            indicatorName, EptsReportUtils.map(cohortDefinition, mappings));

    indicator.addParameter(new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    indicator.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    indicator.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    indicator.addParameter(new Parameter("location", "location", Date.class));

    return indicator;
  }
}
