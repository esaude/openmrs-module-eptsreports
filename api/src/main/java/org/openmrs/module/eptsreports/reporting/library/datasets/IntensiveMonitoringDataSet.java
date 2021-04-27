package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.IntensiveMonitoringCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.QualityImprovement2020CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class IntensiveMonitoringDataSet extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries;

  @Autowired private QualityImprovement2020CohortQueries qualityImprovement2020CohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructIntensiveMonitoringDataSet() {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("Intensive Monitoring DataSet");
    dataSetDefinition.addParameters(getParameters());

    /* add dimensions */
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension(
        "ageInMonths",
        EptsReportUtils.map(eptsCommonDimension.ageInMonths(), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension(
        "ageBasedOnArt",
        EptsReportUtils.map(
            eptsCommonDimension.ageBasedOnArtStartDateMOH(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    dataSetDefinition.addDimension(
        "mqAge",
        EptsReportUtils.map(
            eptsCommonDimension.getPatientAgeBasedOnFirstViralLoadDate(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    // Category 3 Denominator
    CohortIndicator MQC3D1 =
        eptsGeneralIndicator.getIndicator(
            "MQC3D1",
            EptsReportUtils.map(
                this.qualityImprovement2020CohortQueries.getMQC3D1(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));

    addRow(
        dataSetDefinition,
        "MQC3D1",
        "Category 3 Denominator",
        EptsReportUtils.map(
            MQC3D1, "startDate=${startDate},endDate=${endDate},location=${location}"),
        getDisagregateAdultsAndChildrenSColumn());

    // Category 3 Numerator
    CohortIndicator MQC3N1 =
        eptsGeneralIndicator.getIndicator(
            "MQC3N1",
            EptsReportUtils.map(
                this.qualityImprovement2020CohortQueries.getMQC3N1(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));

    addRow(
        dataSetDefinition,
        "MQC3N1",
        "Category 3 Numerator",
        EptsReportUtils.map(
            MQC3N1, "startDate=${startDate},endDate=${endDate},location=${location}"),
        getDisagregateAdultsAndChildrenSColumn());

    // Category 4 denominator indicators

    CohortIndicator MCC4D1 =
        eptsGeneralIndicator.getIndicator(
            "MCC4D1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC4D1(),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MCC4D1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MCC4D1",
        "Crianças em TARV com estado (grau) da avaliação nutricional registado na última consulta clínica",
        EptsReportUtils.map(
            MCC4D1,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MCC4D2 =
        eptsGeneralIndicator.getIndicator(
            "MCC4D2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC4D2(),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));
    MCC4D2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    dataSetDefinition.addColumn(
        "MCC4D2",
        "MG em TARV com o estado (grau) da avaliação nutricional registado na última consulta clínica",
        EptsReportUtils.map(
            MCC4D2,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Category 4 Numerator indicators
    dataSetDefinition.addColumn(
        "MQC4N1",
        "Category 4 numerator 1",
        EptsReportUtils.map(
            customCohortIndicator(
                qualityImprovement2020CohortQueries.getMQC4N1(),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MQC4N2",
        "Category 4 numerator 2",
        EptsReportUtils.map(
            customCohortIndicator(
                qualityImprovement2020CohortQueries.getMQC4N2(),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Category 5 denominator indicators
    CohortIndicator MQ5DEN1 =
        eptsGeneralIndicator.getIndicator(
            "MQ5DEN1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ5A(true),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ5DEN1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ5DEN1",
        "Crianças em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional",
        EptsReportUtils.map(
            MQ5DEN1,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=<15");

    CohortIndicator MQ5DEN2 =
        eptsGeneralIndicator.getIndicator(
            "MQ5DEN2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ5B(true),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ5DEN2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ5DEN2",
        "MG em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional",
        EptsReportUtils.map(
            MQ5DEN2,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    // Category 5 numerator indicators
    CohortIndicator MQ5NOM1 =
        eptsGeneralIndicator.getIndicator(
            "MQ5NOM1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ5A(false),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ5NOM1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ5NOM1",
        "Crianças em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional",
        EptsReportUtils.map(
            MQ5NOM1,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=<15");

    CohortIndicator MQ5NOM2 =
        eptsGeneralIndicator.getIndicator(
            "MQ5NOM2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ5B(false),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ5NOM2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ5NOM2",
        "MG em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional",
        EptsReportUtils.map(
            MQ5NOM2,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    // Category 6 denominator indicators

    CohortIndicator MQ6DEN1 =
        eptsGeneralIndicator.getIndicator(
            "MQ6DEN1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ6A(1),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ6DEN1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ6DEN1",
        "Crianças em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional",
        EptsReportUtils.map(
            MQ6DEN1,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    CohortIndicator MQ6DEN2 =
        eptsGeneralIndicator.getIndicator(
            "MQ6DEN2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ6A(2),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ6DEN2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ6DEN2",
        "",
        EptsReportUtils.map(
            MQ6DEN2,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=<15");

    CohortIndicator MQ6DEN3 =
        eptsGeneralIndicator.getIndicator(
            "MQ6DEN3",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ6A(3),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ6DEN3.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ6DEN3",
        "% de mulheres grávidas HIV+ rastreadas para TB na última consulta clínica",
        EptsReportUtils.map(
            MQ6DEN3,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ6DEN4 =
        eptsGeneralIndicator.getIndicator(
            "MQ6DEN4",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ6A(4),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ6DEN4.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ6DEN4",
        "% de mulheres lactantes HIV+ rastreadas para TB  na última consulta",
        EptsReportUtils.map(
            MQ6DEN4,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Category 6 numerator indicators

    CohortIndicator MQ6NUM1 =
        eptsGeneralIndicator.getIndicator(
            "MQ6NUM1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ6NUM(1),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ6NUM1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ6NUM1",
        "Crianças em TARV com desnutrição (DAM ou DAG) e  com registo de prescrição de suplementação ou tratamento nutricional",
        EptsReportUtils.map(
            MQ6NUM1,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    CohortIndicator MQ6NUM2 =
        eptsGeneralIndicator.getIndicator(
            "MQ6NUM2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ6NUM(2),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ6NUM2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ6NUM2",
        "",
        EptsReportUtils.map(
            MQ6NUM2,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=<15");

    CohortIndicator MQ6NUM3 =
        eptsGeneralIndicator.getIndicator(
            "MQ6NUM3",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ6NUM(3),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ6NUM3.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ6NUM3",
        "% de mulheres grávidas HIV+ rastreadas para TB na última consulta clínica",
        EptsReportUtils.map(
            MQ6NUM3,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ6NUM4 =
        eptsGeneralIndicator.getIndicator(
            "MQ6NUM4",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ6NUM(4),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ6NUM4.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ6NUM4",
        "% de mulheres lactantes HIV+ rastreadas para TB  na última consulta",
        EptsReportUtils.map(
            MQ6NUM4,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    //  CATEGORY 7 from report named “Monitoria Intensiva MQHIV 2021 denominator
    // 7.1
    dataSetDefinition.addColumn(
        "MI7DEN1",
        "% de adultos HIV+ em TARV elegíveis ao TPT e que iniciaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202171Definition("DEN"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt=adultsArt");
    // 7.2
    dataSetDefinition.addColumn(
        "MI7DEN2",
        "% de adultos HIV+ em TARV elegiveis ao TPT que iniciaram e  completaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202172Definition("DEN"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt=adultsArt");
    // 7.3
    dataSetDefinition.addColumn(
        "MI7DEN3",
        "% de crianças HIV+ em TARV elegiveis ao TPT  e que iniciaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202173Definition("DEN"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt=childrenArt");
    // 7.4
    dataSetDefinition.addColumn(
        "MI7DEN4",
        "% de crianças HIV+ em TARV elegíveis que iniciaram e completaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202174Definition("DEN"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt=childrenArt");
    // 7.5
    dataSetDefinition.addColumn(
        "MI7DEN5",
        "% de mulheres grávidas HIV+ elegíveis ao TPI e que iniciaram TPI",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202175Definition("DEN"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 7.6
    dataSetDefinition.addColumn(
        "MI7DEN6",
        "% de MG HIV+ em TARV que iniciou TPI e que terminou TPI",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202176Definition("DEN"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // CATEGORY 7 from report named “Monitoria Intensiva MQHIV 2021 numerator
    // 7.1
    dataSetDefinition.addColumn(
        "MI7NUM1",
        "% de adultos HIV+ em TARV elegíveis ao TPT e que iniciaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202171Definition("NUM"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt=adultsArt");
    // 7.2
    dataSetDefinition.addColumn(
        "MI7NUM2",
        "% de adultos HIV+ em TARV elegiveis ao TPT que iniciaram e  completaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202172Definition("NUM"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt=adultsArt");
    // 7.3
    dataSetDefinition.addColumn(
        "MI7NUM3",
        "% de crianças HIV+ em TARV elegiveis ao TPT  e que iniciaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202173Definition("NUM"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt=childrenArt");
    // 7.4
    dataSetDefinition.addColumn(
        "MI7NUM4",
        "% de crianças HIV+ em TARV elegíveis que iniciaram e completaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202174Definition("NUM"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt=childrenArt");
    // 7.5
    dataSetDefinition.addColumn(
        "MI7NUM5",
        "% de mulheres grávidas HIV+ elegíveis ao TPI e que iniciaram TPI",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202175Definition("NUM"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 7.6
    dataSetDefinition.addColumn(
        "MI7NUM6",
        "% de MG HIV+ em TARV que iniciou TPI e que terminou TPI",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7MOHIV202176Definition("NUM"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Category 11 denominator indicators

    CohortIndicator MI11DEN1 =
        eptsGeneralIndicator.getIndicator(
            "MI11DEN1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11DEN(1),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11DEN1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11DEN1",
        "Adultos em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de APSS/PP nos primeiros 3 meses após início do TARV",
        EptsReportUtils.map(
            MI11DEN1,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt=adultsArt");

    CohortIndicator MI11DEN2 =
        eptsGeneralIndicator.getIndicator(
            "MI11DEN2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11DEN(2),
                "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11DEN2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11DEN2",
        "Pacientes na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas de APSS/PP mensais consecutivas para reforço de adesão",
        EptsReportUtils.map(
            MI11DEN2,
            "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"),
        "mqAge=MqAdults");

    CohortIndicator MI11DEN3 =
        eptsGeneralIndicator.getIndicator(
            "MI11DEN3",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11DEN(3),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11DEN3.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11DEN3",
        " MG em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de APSS/PP nos primeiros 3 meses após início do TARV",
        EptsReportUtils.map(
            MI11DEN3,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI11DEN4 =
        eptsGeneralIndicator.getIndicator(
            "MI11DEN4",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11DEN(4),
                "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11DEN4.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11DEN4",
        "MG na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas de APSS/PP mensais consecutivas para reforço de adesão",
        EptsReportUtils.map(
            MI11DEN4,
            "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI11DEN5 =
        eptsGeneralIndicator.getIndicator(
            "MI11DEN5",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11DEN(5),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11DEN5.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11DEN5",
        "Crianças > 2 anos de idade em TARV com registo mensal de seguimento da adesão na ficha de APSS/PP nos primeiros 99 dias de TARV",
        EptsReportUtils.map(
            MI11DEN5,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt=2-14");

    CohortIndicator MI11DEN6 =
        eptsGeneralIndicator.getIndicator(
            "MI11DEN6",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11DEN(6),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11DEN6.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11DEN6",
        "Crianças < 2 anos de idade em TARV com registo mensal de seguimento da adesão na ficha de APSS/PP no primeiro Ano de TARV",
        EptsReportUtils.map(
            MI11DEN6,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageInMonths=<9m");

    CohortIndicator MI11DEN7 =
        eptsGeneralIndicator.getIndicator(
            "MI11DEN7",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11DEN(7),
                "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11DEN7.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11DEN7",
        "Crianças (0-14 anos) na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas mensais consecutivas de APSS/PP para reforço de adesão",
        EptsReportUtils.map(
            MI11DEN7,
            "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"),
        "mqAge=MqChildren");

    // Numerator CAT 11
    CohortIndicator MI11NUM1 =
        eptsGeneralIndicator.getIndicator(
            "MI11NUM1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11NumAnotCnotDnotEnotFandGAdultss(),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11NUM1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11NUM1",
        "1.1. % de adultos em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de APSS/PP nos primeiros 3 meses após início do TARV",
        EptsReportUtils.map(
            MI11NUM1,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    CohortIndicator MI11NUM2 =
        eptsGeneralIndicator.getIndicator(
            "MI11NUM2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries
                    .getMQC11NumB1nB2notCnotDnotEnotEnotFnHandAdultss(),
                "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11NUM2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11NUM2",
        "11.2. % de pacientes na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas de APSS/PP mensais consecutivas para reforço de adesão ",
        EptsReportUtils.map(
            MI11NUM2,
            "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"),
        "mqAge=MqAdults");

    CohortIndicator MI11NUM3 =
        eptsGeneralIndicator.getIndicator(
            "MI11NUM3",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11NumAnB3nCnotDnotEnotEnotFnG(),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11NUM3.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11NUM3",
        "11.3. % de crianças >2 anos de idade em TARV com registo mensal de seguimento da adesão na ficha de APSS/PP nos primeiros 99 dias de TARV ",
        EptsReportUtils.map(
            MI11NUM3,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI11NUM4 =
        eptsGeneralIndicator.getIndicator(
            "MI11NUM4",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11NumB1nB2nB3nCnotDnotEnotEnotFnH(),
                "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11NUM4.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11NUM4",
        "11.4. % de crianças <2 anos de idade em TARV com registo mensal de seguimento da adesão na ficha de APSS/PP no primeiro ano de TARV ",
        EptsReportUtils.map(
            MI11NUM4,
            "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI11NUM5 =
        eptsGeneralIndicator.getIndicator(
            "MI11NUM5",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11NumAnotCnotDnotEnotFnotGnChildren(),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11NUM5.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11NUM5",
        "11.5. % de crianças (0-14 anos) na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas mensais consecutivas de APSS/PP para reforço de adesão",
        EptsReportUtils.map(
            MI11NUM5,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=2-14");

    CohortIndicator MI11NUM6 =
        eptsGeneralIndicator.getIndicator(
            "MI11NUM6",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries
                    .getMQC11NumAnotCnotDnotEnotFnotIlessThan9Month(),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11NUM6.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11NUM6",
        "11.6. % de crianças <2 anos de idade em TARV com registo mensal de seguimento da adesão na ficha de APSS/PP no primeiro ano de TARV (Line 61 in the template) Numerador (Column D in the",
        EptsReportUtils.map(
            MI11NUM6,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI11NUM7 =
        eptsGeneralIndicator.getIndicator(
            "MI11NUM7",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC11NumB1nB2notCnotDnotEnotFnHChildren(),
                "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI11NUM7.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI11NUM7",
        "11.7. % de crianças (0-14 anos) na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3  consultas mensais consecutivas de APSS/PP para reforço de adesão",
        EptsReportUtils.map(
            MI11NUM7,
            "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"),
        "mqAge=MqChildren");

    // Category 12 denominator indicators Part 1
    CohortIndicator MI12DEN1 =
        eptsGeneralIndicator.getIndicator(
            "MI12DEN1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12DEN(1),
                "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12DEN1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12DEN1",
        "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            MI12DEN1,
            "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI12DEN2 =
        eptsGeneralIndicator.getIndicator(
            "MI12DEN2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12DEN(2),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12DEN2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12DEN2",
        "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV",
        EptsReportUtils.map(
            MI12DEN2,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI12DEN5 =
        eptsGeneralIndicator.getIndicator(
            "MI12DEN5",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12DEN(5),
                "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12DEN5.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12DEN5",
        "Crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            MI12DEN5,
            "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt=childrenArt");

    CohortIndicator MI12DEN6 =
        eptsGeneralIndicator.getIndicator(
            "MI12DEN6",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12DEN(6),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12DEN6.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12DEN6",
        "# de crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            MI12DEN6,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI12DEN9 =
        eptsGeneralIndicator.getIndicator(
            "MI12DEN9",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12DEN(9),
                "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12DEN9.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12DEN9",
        "# de mulheres grávidas HIV+  que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            MI12DEN9,
            "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI12DEN10 =
        eptsGeneralIndicator.getIndicator(
            "MI12DEN10",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12DEN(10),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12DEN10.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12DEN10",
        "# de mulheres grávidas HIV+  que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            MI12DEN10,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Category 12 denominator indicators Part 2
    CohortIndicator MQ12DEN3 =
        eptsGeneralIndicator.getIndicator(
            "MQ12DEN3",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC12P2DEN(3),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ12DEN3.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ12DEN3",
        "Adultos (15/+anos) na 1ª linha que iniciaram o TARV há 12 meses atrás",
        EptsReportUtils.map(
            MQ12DEN3,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ12DEN4 =
        eptsGeneralIndicator.getIndicator(
            "MQ12DEN4",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC12P2DEN(4),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ12DEN4.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ12DEN4",
        "Adultos (15/+anos) que iniciaram 2ª linha TARV há 12 meses atrás",
        EptsReportUtils.map(
            MQ12DEN4,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ12DEN7 =
        eptsGeneralIndicator.getIndicator(
            "MQ12DEN7",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC12P2DEN(7),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ12DEN7.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ12DEN7",
        "Crianças (0-14 anos) na 1ª linha que iniciaram o TARV há 12 meses atrás",
        EptsReportUtils.map(
            MQ12DEN7,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ12DEN8 =
        eptsGeneralIndicator.getIndicator(
            "MQ12DEN8",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC12P2DEN(8),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ12DEN8.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ12DEN8",
        "Crianças (0-14 anos) que iniciaram 2ª linha TARV há 12 meses atrás",
        EptsReportUtils.map(
            MQ12DEN8,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ12DEN9 =
        eptsGeneralIndicator.getIndicator(
            "MQ12DEN9",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12DEN(9),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ12DEN9.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ12DEN9",
        "Crianças (0-14 anos)  que iniciaram 2ª linha TARV há 12 meses atrás",
        EptsReportUtils.map(
            MQ12DEN9,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ12DEN11 =
        eptsGeneralIndicator.getIndicator(
            "MQ12DEN11",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC12P2DEN(11),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ12DEN11.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ12DEN11",
        "Mulheres grávidas HIV+ 1ª linha que iniciaram o TARV há 12 meses atrás",
        EptsReportUtils.map(
            MQ12DEN11,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    /* not specified in the newer version of the spec...
        CohortIndicator MQ12DEN12 =
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN12",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.,
                    "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

        MQ12DEN12.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

        dataSetDefinition.addColumn(
            "MQ12DEN12",
            "Mulheres grávidas HIV+ 1ª linha que iniciaram o TARV há 12 meses atrás",
            EptsReportUtils.map(
                MQ12DEN12,
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "");
    */
    // Category 12 numerator - P2 Indicators
    CohortIndicator MQ12NUM3 =
        eptsGeneralIndicator.getIndicator(
            "MQ12NUM3",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12NumeratorP2(3),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ12NUM3.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ12NUM3",
        "Adultos (15/+anos) na 1ª linha que iniciaram o TARV há 12 meses atrás sem registo de saídas",
        EptsReportUtils.map(
            MQ12NUM3,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ12NUM4 =
        eptsGeneralIndicator.getIndicator(
            "MQ12NUM4",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12NumeratorP2(4),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ12NUM4.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ12NUM4",
        "Adultos (15/+anos) que iniciaram 2ª linha TARV há 12 meses atrás",
        EptsReportUtils.map(
            MQ12NUM4,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ12NUM7 =
        eptsGeneralIndicator.getIndicator(
            "MQ12NUM7",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12NumeratorP2(7),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ12NUM7.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ12NUM7",
        "Crianças (0-14 anos) na 1ª linha que iniciaram o TARV há 12 meses atrás",
        EptsReportUtils.map(
            MQ12NUM7,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ12NUM8 =
        eptsGeneralIndicator.getIndicator(
            "MQ12NUM8",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12NumeratorP2(8),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ12NUM8.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ12NUM8",
        "Crianças (0-14 anos) que iniciaram 2ª linha TARV há 12 meses atrás",
        EptsReportUtils.map(
            MQ12NUM8,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ12NUM11 =
        eptsGeneralIndicator.getIndicator(
            "MQ12NUM11",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12NumeratorP2(11),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ12NUM11.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ12NUM11",
        "Mulheres grávidas HIV+ 1ª linha que iniciaram o TARV há 12 meses atrás",
        EptsReportUtils.map(
            MQ12NUM11,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    /*
        CohortIndicator MQ12NUM12 =
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM12",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NumeratorP2(12),
                    "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

        MQ12NUM12.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

        dataSetDefinition.addColumn(
            "MQ12NUM12",
            "# de mulheres grávidas HIV+ 1ª linha que iniciaram o TARV há 12 meses atrás",
            EptsReportUtils.map(
                MQ12NUM12,
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "");
    */
    // Category 12 numerator indicators
    CohortIndicator MI12NUM1 =
        eptsGeneralIndicator.getIndicator(
            "MI12NUM1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12NUM(1),
                "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12NUM1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12NUM1",
        "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs entre 25 a 33 dias após o início do TARV",
        EptsReportUtils.map(
            MI12NUM1,
            "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI12NUM2 =
        eptsGeneralIndicator.getIndicator(
            "MI12NUM2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12NUM(2),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12NUM2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12NUM2",
        "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que tiveram 3 consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV",
        EptsReportUtils.map(
            MI12NUM2,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI12NUM5 =
        eptsGeneralIndicator.getIndicator(
            "MI12NUM5",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12NUM(5),
                "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12NUM5.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12NUM5",
        "# # de crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            MI12NUM5,
            "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI12NUM6 =
        eptsGeneralIndicator.getIndicator(
            "MI12NUM6",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12NUM(6),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12NUM6.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12NUM6",
        "# de crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            MI12NUM6,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI12NUM9 =
        eptsGeneralIndicator.getIndicator(
            "MI12NUM9",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12NUM(9),
                "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12NUM9.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12NUM9",
        "No de crianças (0-14 anos)  que iniciaram 2ª linha TARV há 12 meses atrás",
        EptsReportUtils.map(
            MI12NUM9,
            "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI12NUM10 =
        eptsGeneralIndicator.getIndicator(
            "MQ12NUM10",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ12NUM(10),
                "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"));

    MI12NUM10.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI12NUM10",
        "# de mulheres grávidas HIV+  que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            MI12NUM10,
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Category 13 Denominator indicators
    CohortIndicator MQ13DEN1 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(true, 1),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN1",
        "% de adultos (15/+anos) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico",
        EptsReportUtils.map(
            MQ13DEN1,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN4 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN4",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(true, 4),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN4.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN4",
        "# de adultos (15/+ anos) na 2a linha de TARV elegíveis a CV.",
        EptsReportUtils.map(
            MQ13DEN4,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN6 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN6",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(true, 6),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN6.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN6",
        "% de crianças (0-4 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            MQ13DEN6,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN7 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN7",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(true, 7),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN7.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN7",
        "% de crianças (5-9 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            MQ13DEN7,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN8 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN8",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(true, 8),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN8.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN8",
        "% de crianças (10-14 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            MQ13DEN8,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN13 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN13",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(true, 13),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN13.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN13",
        "# de crianças (>2anos) na 2a linha de TARV elegíveis ao pedido de CV.",
        EptsReportUtils.map(
            MQ13DEN13,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Category 13 Numerator indicators
    CohortIndicator MQ13NUM1 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(false, 1),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM1",
        "% de adultos (15/+anos) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico",
        EptsReportUtils.map(
            MQ13NUM1,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM4 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM4",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(false, 4),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM4.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM4",
        "% de adultos (15/+anos) na 2a linha de TARV elegíveis a CV com registo de pedido de CV feito pelo clínico",
        EptsReportUtils.map(
            MQ13NUM4,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM6 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM6",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(false, 6),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM6.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM6",
        "% de crianças (0-4 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            MQ13NUM6,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM7 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM7",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(false, 7),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM7.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM7",
        "% de crianças (5-9 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            MQ13NUM7,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM8 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM8",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(false, 8),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM8.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM8",
        "% de crianças (10-14 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            MQ13NUM8,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM13 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM13",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13(false, 13),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM13.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM13",
        "% de crianças na  2ª linha de TARV elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico",
        EptsReportUtils.map(
            MQ13NUM13,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Category 13 Part 3 Denominator Indicators

    CohortIndicator MQ13DEN2 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3DEN(2),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));
    MQ13DEN2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    dataSetDefinition.addColumn(
        "MQ13DEN2",
        "Adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início do TARV",
        EptsReportUtils.map(
            MQ13DEN2,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN5 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN5",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3DEN(5),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN5.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN5",
        "Adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após o início da 2a linha de TARV",
        EptsReportUtils.map(
            MQ13DEN5,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN9 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN9",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3DEN(9),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN9.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN9",
        "Crianças  (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            MQ13DEN9,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN10 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN10",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3DEN(10),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN10.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN10",
        "Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            MQ13DEN10,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN11 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN11",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3DEN(11),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN11.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN11",
        "crianças  (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV ",
        EptsReportUtils.map(
            MQ13DEN11,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN14 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN14",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3DEN(14),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN14.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN14",
        "Crianças na 2a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início da 2a linha de TARV",
        EptsReportUtils.map(
            MQ13DEN14,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN3 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN3",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13P4(true, 3),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));
    MQ13DEN3.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    dataSetDefinition.addColumn(
        "MQ13DEN3",
        "# de adultos na 1a linha de TARV que receberam um resultado de CV acima de 1000 cópias no período de inclusão",
        EptsReportUtils.map(
            MQ13DEN3,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "mqAge=MqAdults");

    CohortIndicator MQ13DEN12 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN12",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13P4(true, 12),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN12.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN12",
        "# de crianças (>2 anos de idade) na 1a linha de TARV que receberam um resultado de CV acima de 1000 cópias no período de inclusão",
        EptsReportUtils.map(
            MQ13DEN12,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN18 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN18",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13P4(true, 18),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN18.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN18",
        "# de MG na 1a linha de TARV que receberam um resultado de CV acima de 1000 cópias no período de inclusão",
        EptsReportUtils.map(
            MQ13DEN18,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // M&Q Report - Categoria 13 Numerador - P3 Indicators

    CohortIndicator MQ13NUM2 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3NUM(2),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM2",
        "Adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início do TARV",
        EptsReportUtils.map(
            MQ13NUM2,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM9 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM9",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3NUM(9),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM9.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM9",
        "Crianças  (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            MQ13NUM9,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM10 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM10",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3NUM(10),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM10.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM10",
        "Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            MQ13NUM10,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM11 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM11",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3NUM(11),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM11.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM11",
        " crianças  (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            MQ13NUM11,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM5 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM5",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3NUM(5),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM5.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM5",
        "adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após o início da 2a linha de TARV",
        EptsReportUtils.map(
            MQ13NUM5,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM14 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM14",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P3NUM(14),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM14.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM14",
        "Crianças na 2a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início da 2a linha de TARV",
        EptsReportUtils.map(
            MQ13NUM14,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM3 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM3",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13P4(false, 3),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM3.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM3",
        "% de Adultos (15/+anos) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês após terem recebido o último resultado de CV acima de 1000 e terem  3 sessões consecutivas de APSS/PP",
        EptsReportUtils.map(
            MQ13NUM3,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "mqAge=MqAdults");

    CohortIndicator MQ13NUM12 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM12",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13P4(false, 12),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM12.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM12",
        "% de crianças (>2 anos de idade) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês após terem recebido o último resultado de CV acima de 1000 cópia e terem  3 sessões consecutivas de APSS/PP",
        EptsReportUtils.map(
            MQ13NUM12,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=2-14");

    CohortIndicator MQ13NUM18 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM18",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ13P4(false, 18),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM18.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM18",
        "% de MG na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês após terem recebido o último resultado de CV acima de 1000 cópia e terem 3 sessões consecutivas de APSS/PP",
        EptsReportUtils.map(
            MQ13NUM18,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Categoria  13 part 2  Denominator

    CohortIndicator MQ13DEN15 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN15",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getgetMQC13P2DenMGInIncluisionPeriod(),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN15.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN15",
        "13.15. % de MG elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram TARV na CPN) Denominator: # de MG com registo de início do TARV na CPN dentro do período de inclusão. (Line 90,Column F in the Template) as following",
        EptsReportUtils.map(
            MQ13DEN15,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN16 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN16",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getgetMQC13P2DenMGInIncluisionPeriod33Month(),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN16.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN16",
        "13.16. % de MG elegíveis a CV com registo de pedido de CV feito pelo clínico na primeira CPN (MG que entraram em TARV na CPN) Denominator:# de MG que tiveram a primeira CPN no período de inclusão, e que já estavam em TARV há mais de 3 meses  (Line 91,Column F in the Template) as following:",
        EptsReportUtils.map(
            MQ13DEN16,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13DEN17 =
        eptsGeneralIndicator.getIndicator(
            "MQ13DEN17",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P2DenMGInIncluisionPeriod33Days(),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13DEN17.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13DEN17",
        "13.17. % de MG que receberam o resultado da Carga Viral dentro de 33 dias após pedido Denominator: # de MG com registo de pedido de CV no período de revisão (Line 92,Column F in the Template) as following:<",
        EptsReportUtils.map(
            MQ13DEN17,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // M&Q Report - Cat 10 Indicator 10.3 - Numerator and Denominator

    CohortIndicator MQ10NUMDEN1031 =
        eptsGeneralIndicator.getIndicator(
            "MQ10NUMDEN1031",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ10NUMDEN103("den"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ10NUMDEN1031.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ10NUMDEN1031",
        "% de crianças com PCR positivo para HIV  que iniciaram TARV dentro de 2 semanas após o diagnóstico/entrega do resultado ao cuidador - denominator",
        EptsReportUtils.map(
            MQ10NUMDEN1031,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ10NUMDEN1032 =
        eptsGeneralIndicator.getIndicator(
            "MQ10NUMDEN1032",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ10NUMDEN103("num"),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));
    MQ10NUMDEN1032.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ10NUMDEN1032",
        "% de crianças com PCR positivo para HIV  que iniciaram TARV dentro de 2 semanas após o diagnóstico/entrega do resultado ao cuidador - numerator",
        EptsReportUtils.map(
            MQ10NUMDEN1032,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Categoria  13 part 2  Numerator

    CohortIndicator MQ13NUM15 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM15",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P2Num1(),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM15.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM15",
        "13.15. % de MG elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram TARV na CPN) Denominator: # de MG com registo de início do TARV na CPN dentro do período de inclusão. (Line 90,Column F in the Template) as following",
        EptsReportUtils.map(
            MQ13NUM15,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM16 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM16",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P2Num2(),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM16.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM16",
        "13.16. % de MG elegíveis a CV com registo de pedido de CV feito pelo clínico na primeira CPN (MG que entraram em TARV na CPN) Denominator:# de MG que tiveram a primeira CPN no período de inclusão, e que já estavam em TARV há mais de 3 meses  (Line 91,Column F in the Template) as following:",
        EptsReportUtils.map(
            MQ13NUM16,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MQ13NUM17 =
        eptsGeneralIndicator.getIndicator(
            "MQ13NUM17",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQC13P2Num3(),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ13NUM17.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ13NUM17",
        "13.17. % de MG que receberam o resultado da Carga Viral dentro de 33 dias após pedido Denominator: # de MG com registo de pedido de CV no período de revisão (Line 92,Column F in the Template) as following:<",
        EptsReportUtils.map(
            MQ13NUM17,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Category 15 denominator indicators
    CohortIndicator MQ15DEN1 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(1),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15DEN1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN1",
        "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS  (DT ou GAAC) que continuam activos em TARV",
        EptsReportUtils.map(
            MQ15DEN1,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    CohortIndicator MQ15DEN2 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(2),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15DEN2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN2",
        "% de Adultos (15/+anos) inscritos há pelo menos 12 meses em algum MDS (DT ou GAAC) com pedido de pelo menos uma CV",
        EptsReportUtils.map(
            MQ15DEN2,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    CohortIndicator MQ15DEN3 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN3",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(3),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15DEN3.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN3",
        "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS (DT ou GAAC) que receberam pelo menos um resultado de CV",
        EptsReportUtils.map(
            MQ15DEN3,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    CohortIndicator MQ15DEN4 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN4",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(4),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15DEN4.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN4",
        "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS (DT ou GAAC) com CV <1000 Cópias",
        EptsReportUtils.map(
            MQ15DEN4,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    CohortIndicator MQ15DEN5 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN5",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(5),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15DEN5.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN5",
        "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) que continuam activos em TARV",
        EptsReportUtils.map(
            MQ15DEN5,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=2-9");

    CohortIndicator MQ15DEN6 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN6",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(6),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15DEN6.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN6",
        "% de Crianças (10-14 anos) inscritas há 12 em algum MDS (DT) que continuam activos em TARV",
        EptsReportUtils.map(
            MQ15DEN6,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=10-14");

    CohortIndicator MQ15DEN7 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN7",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(7),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15DEN7.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN7",
        "% de Crianças (2-9 anos de idade) inscritas há 12 meses em algum MDS (DT) com pedido de pelo menos uma CV",
        EptsReportUtils.map(
            MQ15DEN7,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=2-9");

    CohortIndicator MQ15DEN8 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN8",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(8),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15DEN8.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN8",
        "% de Crianças (10-14 anos de idade) inscritas há 12 meses em algum MDS (DT) com pedido de pelo menos uma CV",
        EptsReportUtils.map(
            MQ15DEN8,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=10-14");

    CohortIndicator MQ15DEN9 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN9",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(9),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15DEN9.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN9",
        "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) que receberam pelo menos um resultado de CV",
        EptsReportUtils.map(
            MQ15DEN9,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=2-9");

    CohortIndicator MQ15DEN10 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN10",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(10),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));
    MQ15DEN10.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN10",
        "% de Crianças (10-14 anos) inscritas há 12 meses em algum MDS (DT) que receberam pelo menos um resultado de CV",
        EptsReportUtils.map(
            MQ15DEN10,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=10-14");

    CohortIndicator MQ15DEN11 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN11",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(11),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15DEN11.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN11",
        "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) com CV <1000 Cópias",
        EptsReportUtils.map(
            MQ15DEN11,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=2-9");

    CohortIndicator MQ15DEN12 =
        eptsGeneralIndicator.getIndicator(
            "MQ15DEN12",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15DEN(12),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15DEN12.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15DEN12",
        "% de Crianças (10-14 anos) inscritas há 12 meses em algum MDS (DT) com CV <1000 Cópias",
        EptsReportUtils.map(
            MQ15DEN12,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=10-14");

    // Category 15 Numerator indicators
    CohortIndicator MQ15NUM1 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM1",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(1),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM1.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM1",
        "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS  (DT ou GAAC) que continuam activos em TARV",
        EptsReportUtils.map(
            MQ15NUM1,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    CohortIndicator MQ15NUM2 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM2",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(2),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM2.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM2",
        "% de Adultos (15/+anos) inscritos há pelo menos 12 meses em algum MDS (DT ou GAAC) com pedido de pelo menos uma CV",
        EptsReportUtils.map(
            MQ15NUM2,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    CohortIndicator MQ15NUM3 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM3",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(3),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM3.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM3",
        "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS (DT ou GAAC) que receberam pelo menos um resultado de CV",
        EptsReportUtils.map(
            MQ15NUM3,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    CohortIndicator MQ15NUM4 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM4",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(4),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM4.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM4",
        "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS (DT ou GAAC) com CV <1000 Cópias",
        EptsReportUtils.map(
            MQ15NUM4,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=15+");

    CohortIndicator MQ15NUM5 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM5",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(5),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM5.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM5",
        "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) que continuam activos em TARV",
        EptsReportUtils.map(
            MQ15NUM5,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=2-9");

    CohortIndicator MQ15NUM6 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM6",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(6),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM6.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM6",
        "% de Crianças (10-14 anos) inscritas há 12 em algum MDS (DT) que continuam activos em TARV",
        EptsReportUtils.map(
            MQ15NUM6,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=10-14");

    CohortIndicator MQ15NUM7 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM7",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(7),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM7.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM7",
        "% de Crianças (2-9 anos de idade) inscritas há 12 meses em algum MDS (DT) com pedido de pelo menos uma CV",
        EptsReportUtils.map(
            MQ15NUM7,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=2-9");

    CohortIndicator MQ15NUM8 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM8",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(8),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM8.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM8",
        "% de Crianças (10-14 anos de idade) inscritas há 12 meses em algum MDS (DT) com pedido de pelo menos uma CV",
        EptsReportUtils.map(
            MQ15NUM8,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=10-14");

    CohortIndicator MQ15NUM9 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM9",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(9),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM9.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM9",
        "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) que receberam pelo menos um resultado de CV",
        EptsReportUtils.map(
            MQ15NUM9,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=2-9");

    CohortIndicator MQ15NUM10 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM10",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(10),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM10.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM10",
        "% de Crianças (10-14 anos) inscritas há 12 meses em algum MDS (DT) que receberam pelo menos um resultado de CV",
        EptsReportUtils.map(
            MQ15NUM10,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=10-14");

    CohortIndicator MQ15NUM11 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM11",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(11),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM11.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM11",
        "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) com CV <1000 Cópias",
        EptsReportUtils.map(
            MQ15NUM11,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=2-9");

    CohortIndicator MQ15NUM12 =
        eptsGeneralIndicator.getIndicator(
            "MQ15NUM12",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15NUM(12),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    MQ15NUM12.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MQ15NUM12",
        "% de Crianças (10-14 anos) inscritas há 12 meses em algum MDS (DT) com CV <1000 Cópias",
        EptsReportUtils.map(
            MQ15NUM12,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "age=10-14");

    // M&Q Report - Categoria 14 Denominador
    dataSetDefinition.addColumn(
        "MQ14DEN1",
        "% de adultos (15/+anos) em TARV com supressão viral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ14DEN1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ14DEN(1),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"),
        "age=15+");
    dataSetDefinition.addColumn(
        "MQ14DEN2",
        "% de crianças (0-14 anos) em TARV com supressão viral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ14DEN2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ14DEN(1),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"),
        "age=0-14");

    dataSetDefinition.addColumn(
        "MQ14DEN3",
        "% de MG em TARV com supressão viral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ14DEN3",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ14DEN(3),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ14DEN4",
        "% de ML em TARV com supressão viral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ14DEN4",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ14DEN(4),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"),
        "");
    // M&Q Report - Categoria 14 Numerator
    dataSetDefinition.addColumn(
        "MQ14NUM1",
        "% de adultos (15/+anos) em TARV com supressão viral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ14NUM1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ14NUM(1),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"),
        "age=15+");
    dataSetDefinition.addColumn(
        "MQ14NUM2",
        "% de crianças (0-14 anos) em TARV com supressão viral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ14NUM2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ14NUM(2),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"),
        "age=0-14");

    dataSetDefinition.addColumn(
        "MQ14NUM3",
        "% de MG em TARV com supressão viral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ14NUM3",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ14NUM(3),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ14NUM4",
        "% de ML em TARV com supressão viral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ14NUM4",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ14NUM(4),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"),
        "");

    // MQ indicators category 9 denominator

    dataSetDefinition.addColumn(
        "MQ9DEN1",
        "% de adultos  HIV+ em TARV que tiveram conhecimento do resultado do primeiro CD4 dentro de 33 dias após a inscrição",
        EptsReportUtils.map(
            customCohortIndicator(
                qualityImprovement2020CohortQueries.getMQ9Den(1),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MQ9DEN2",
        "% de crianças HIV+ em TARV que tiveram conhecimento do resultado do primeiro CD4 dentro de 33 dias após a inscrição",
        EptsReportUtils.map(
            customCohortIndicator(
                qualityImprovement2020CohortQueries.getMQ9Den(2),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // MQ indicators category 10 denominator

    CohortIndicator MQ10DEN1A =
        eptsGeneralIndicator.getIndicator(
            "",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ10Den(true),
                "startDate=${startDate},endDate=${endDate},location=${location}"));

    dataSetDefinition.addColumn(
        "MQ10DEN1A",
        "%  % de adultos que iniciaram o TARV dentro de 15 dias após diagnóstico",
        EptsReportUtils.map(
            MQ10DEN1A, "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    CohortIndicator MQ10DEN1C =
        eptsGeneralIndicator.getIndicator(
            "",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ10Den(false),
                "startDate=${startDate},endDate=${endDate},location=${location}"));

    dataSetDefinition.addColumn(
        "MQ10DEN1C",
        "% de crianças HIV+ que iniciaram TARV dentro de 15 dias após diagnóstico",
        EptsReportUtils.map(
            MQ10DEN1C, "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");
    // Category 10 numerator results dataset
    dataSetDefinition.addColumn(
        "MQ10NUM1",
        "% de adultos (15/+anos) que iniciaram o TARV dentro de 15 dias após diagnóstico",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ10NUM1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ10NUM(1),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MQ10NUM3",
        "% de crianças (0-14 anos) HIV+ que iniciaram TARV dentro de 15 dias após diagnóstico",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ10NUM3",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ10NUM(3),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"),
        "");

    // MQ indicators category 9 numerator
    dataSetDefinition.addColumn(
        "MQ9NUM1",
        "% de adultos  HIV+ em TARV que tiveram conhecimento do resultado do primeiro CD4 dentro de 33 dias após a inscrição",
        EptsReportUtils.map(
            customCohortIndicator(
                qualityImprovement2020CohortQueries.getMQ9Num(1),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MQ9NUM2",
        "% de crianças HIV+ em TARV que tiveram conhecimento do resultado do primeiro CD4 dentro de 33 dias após a inscrição",
        EptsReportUtils.map(
            customCohortIndicator(
                qualityImprovement2020CohortQueries.getMQ9Num(2),
                "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"),
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
        new Parameter("revisionEndDate", "Data Final Revisão", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class));
  }

  private CohortIndicator customCohortIndicator(CohortDefinition cd, String mapping) {
    CohortIndicator cohortIndicator =
        eptsGeneralIndicator.getIndicator(cd.getName(), EptsReportUtils.map(cd, mapping));
    cohortIndicator.addParameter(new Parameter("revisionEndDate", "Revision Date", Date.class));
    return cohortIndicator;
  }
}
