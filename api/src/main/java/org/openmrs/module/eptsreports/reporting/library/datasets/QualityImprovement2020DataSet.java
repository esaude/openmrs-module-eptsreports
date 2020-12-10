package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.QualityImprovement2020CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class QualityImprovement2020DataSet extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private QualityImprovement2020CohortQueries qualityImprovement2020CohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructQualityImprovement2020DataSet() {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("Quality Improvement DataSet 2020");
    dataSetDefinition.addParameters(getParameters());

    /* add dimensions */
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    CohortIndicator initiatedART =
        eptsGeneralIndicator.getIndicator(
            "initiatedART",
            EptsReportUtils.map(
                this.qualityImprovement2020CohortQueries.getMQC3D1(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));

    addRow(
        dataSetDefinition,
        "ART",
        "initiatedART",
        EptsReportUtils.map(
            initiatedART, "startDate=${startDate},endDate=${endDate},location=${location}"),
        getDisagregateAdultsAndChildrenSColumn());
    // Category 4 denominator indicators
    dataSetDefinition.addColumn(
        "MCC4D1",
        "Crianças em TARV com estado (grau) da avaliação nutricional registado na última consulta clínica",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MCC4D1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC4D1(),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MCC4D2",
        "MG em TARV com o estado (grau) da avaliação nutricional registado na última consulta clínica",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MCC4D2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC4D2(),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // Category 4 Numerator indicators
    CohortIndicator MQC4N1 =
        eptsGeneralIndicator.getIndicator(
            "MQC4N1",
            EptsReportUtils.map(
                this.qualityImprovement2020CohortQueries.getMQC4N1(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));
    dataSetDefinition.addColumn(
        "MQC4N1",
        "Category 4 numerator 1",
        EptsReportUtils.map(
            MQC4N1, "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    CohortIndicator MQC4N2 =
        eptsGeneralIndicator.getIndicator(
            "MQC4N2",
            EptsReportUtils.map(
                this.qualityImprovement2020CohortQueries.getMQC4N2(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));

    dataSetDefinition.addColumn(
        "MQC4N2",
        "Category 4 numerator 2",
        EptsReportUtils.map(
            MQC4N2, "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // Category 5 denominator indicators
    dataSetDefinition.addColumn(
        "MQ5DEN1",
        "Crianças em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ5DEN1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ5A(true),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=<15");

    dataSetDefinition.addColumn(
        "MQ5DEN2",
        "MG em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ5DEN2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ5B(true),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    // Category 5 nominator indicators
    dataSetDefinition.addColumn(
        "MQ5NOM1",
        "Crianças em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ5NOM1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ5A(false),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=<15");

    dataSetDefinition.addColumn(
        "MQ5NOM2",
        "MG em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ5NOM2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ5B(false),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    // Category 6 denominator indicators
    dataSetDefinition.addColumn(
        "MQ6DEN1",
        "Crianças em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ6DEN1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ6A(1),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    dataSetDefinition.addColumn(
        "MQ6DEN2",
        "",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ6DEN2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ6A(2),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=<15");

    dataSetDefinition.addColumn(
        "MQ6DEN3",
        "% de mulheres grávidas HIV+ rastreadas para TB na última consulta clínica",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ6DEN3",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ6A(3),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ6DEN4",
        "% de mulheres lactantes HIV+ rastreadas para TB  na última consulta",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ6DEN4",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ6A(4),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    return dataSetDefinition;
  }

  private List<ColumnParameters> getDisagregateAdultsAndChildrenSColumn() {
    ColumnParameters ADULTOS = new ColumnParameters("ADULTOS", "Adultos", "age=15+", "ADULTOS");
    ColumnParameters CRIANCAS = new ColumnParameters("CRIANCAS", "Criancas", "age=<15", "CRIANCAS");
    return Arrays.asList(ADULTOS, CRIANCAS);
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("startDate", "Data Inicial Inclusão", Date.class),
        new Parameter("endDate", "Data Final Inclusão", Date.class),
        new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class),
        new Parameter("testStart", "Testar Iniciar", Boolean.class));
  }
}
