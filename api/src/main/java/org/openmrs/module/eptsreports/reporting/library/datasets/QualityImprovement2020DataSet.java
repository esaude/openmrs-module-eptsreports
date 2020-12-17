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

    dataSetDefinition.addDimension(
        "ageInMonths",
        EptsReportUtils.map(eptsCommonDimension.ageInMonths(), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension(
        "ageBasedOnArt",
        EptsReportUtils.map(
            eptsCommonDimension.ageBasedOnArtStartDate(), "effectiveDate=${endDate}"));

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

    // Category 5 numerator indicators
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

    // Category 7 denominator indicators
    dataSetDefinition.addColumn(
        "MQ7DEN1",
        "% de adultos HIV+ em TARV elegíveis ao TPT e que iniciaram TPT",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7DEN1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7A(1),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    dataSetDefinition.addColumn(
        "MQ7DEN2",
        "% de adultos HIV+ em TARV elegiveis ao TPT que iniciaram e  completaram TPT",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7DEN2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7A(2),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    dataSetDefinition.addColumn(
        "MQ7DEN3",
        "% de crianças HIV+ em TARV elegiveis ao TPT  e que iniciaram TPT",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7DEN3",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7A(3),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=<15");

    dataSetDefinition.addColumn(
        "MQ7DEN4",
        "% de crianças HIV+ em TARV elegíveis que iniciaram e completaram TPT",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7DEN4",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7A(4),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=<15");

    dataSetDefinition.addColumn(
        "MQ7DEN5",
        "% de mulheres grávidas HIV+ elegíveis ao TPI e que iniciaram TPI",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7DEN5",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7A(5),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ7DEN6",
        "% de MG HIV+ em TARV que iniciou TPI e que terminou TPI",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7DEN6",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7A(6),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // Category 7 numerator indicators
    dataSetDefinition.addColumn(
        "MQ7NUM1",
        "% de adultos HIV+ em TARV elegíveis ao TPT e que iniciaram TPT",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7NUM1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7B(1),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    dataSetDefinition.addColumn(
        "MQ7NUM2",
        "% de adultos HIV+ em TARV elegiveis ao TPT que iniciaram e  completaram TPT",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7NUM2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7B(2),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    dataSetDefinition.addColumn(
        "MQ7NUM3",
        "% de crianças HIV+ em TARV elegiveis ao TPT  e que iniciaram TPT",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7NUM3",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7B(3),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=<15");

    dataSetDefinition.addColumn(
        "MQ7NUM4",
        "% de crianças HIV+ em TARV elegíveis que iniciaram e completaram TPT",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7NUM4",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7B(4),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=<15");

    dataSetDefinition.addColumn(
        "MQ7NUM5",
        "% de mulheres grávidas HIV+ elegíveis ao TPI e que iniciaram TPI",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7NUM5",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7B(5),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ7NUM6",
        "% de MG HIV+ em TARV que iniciou TPI e que terminou TPI",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ7NUM6",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ7B(6),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // Category 11 denominator indicators
    dataSetDefinition.addColumn(
        "MQ11DEN1",
        "Adultos em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de APSS/PP nos primeiros 3 meses após início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11DEN1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC11DEN("A"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=14+");

    dataSetDefinition.addColumn(
        "MQ11DEN2",
        "Pacientes na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas de APSS/PP mensais consecutivas para reforço de adesão",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11DEN2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC11DEN("B"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=14+");

    dataSetDefinition.addColumn(
        "MQ11DEN3",
        " MG em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de APSS/PP nos primeiros 3 meses após início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11DEN3",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC11DEN("C"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ11DEN4",
        "MG na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas de APSS/PP mensais consecutivas para reforço de adesão",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11DEN4",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC11DEN("D"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ11DEN5",
        "Crianças > 2 anos de idade em TARV com registo mensal de seguimento da adesão na ficha de APSS/PP nos primeiros 99 dias de TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11DEN5",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC11DEN("E"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=2-14");

    dataSetDefinition.addColumn(
        "MQ11DEN6",
        "Crianças < 2 anos de idade em TARV com registo mensal de seguimento da adesão na ficha de APSS/PP no primeiro Ano de TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11DEN6",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC11DEN("F"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "ageInMonths=<9m");

    dataSetDefinition.addColumn(
        "MQ11DEN7",
        "Crianças (0-14 anos) na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas mensais consecutivas de APSS/PP para reforço de adesão",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11DEN7",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC11DEN("G"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=<15");

    // Numerator CAT 11

    dataSetDefinition.addColumn(
        "MQ11NUM1",
        "1.1. % de adultos em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de APSS/PP nos primeiros 3 meses após início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11NUM1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC11NumAnotCnotDnotEnotFandGAdultss(),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ11NUM2",
        "11.2. % de pacientes na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas de APSS/PP mensais consecutivas para reforço de adesão ",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11NUM2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries
                        .getMQC11NumB1nB2notCnotDnotEnotEnotFnHandAdultss(),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ11NUM3",
        "11.3. % de crianças >2 anos de idade em TARV com registo mensal de seguimento da adesão na ficha de APSS/PP nos primeiros 99 dias de TARV ",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11NUM3",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC11NumAnB3nCnotDnotEnotEnotFnG(),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ11NUM4",
        "11.4. % de crianças <2 anos de idade em TARV com registo mensal de seguimento da adesão na ficha de APSS/PP no primeiro ano de TARV ",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11NUM4",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC11NumB1nB2nB3nCnotDnotEnotEnotFnH(),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ11NUM5",
        "11.5. % de crianças (0-14 anos) na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas mensais consecutivas de APSS/PP para reforço de adesão",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11NUM5",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC11NumAnotCnotDnotEnotFnotGnChildren(),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ11NUM6",
        "11.6. % de crianças <2 anos de idade em TARV com registo mensal de seguimento da adesão na ficha de APSS/PP no primeiro ano de TARV (Line 61 in the template) Numerador (Column D in the",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11NUM6",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries
                        .getMQC11NumAnotCnotDnotEnotFnotIlessThan9Month(),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ11NUM7",
        "11.7. % de crianças (0-14 anos) na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3  consultas mensais consecutivas de APSS/PP para reforço de adesão",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ11NUM7",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries
                        .getMQC11NumB1nB2notCnotDnotEnotFnHChildren(),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // Category 12 denominator indicators
    dataSetDefinition.addColumn(
        "MQ12DEN1",
        "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12DEN(1),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    dataSetDefinition.addColumn(
        "MQ12DEN2",
        "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12DEN(2),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=<15");

    dataSetDefinition.addColumn(
        "MQ12DEN6",
        "# de crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN6",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12DEN(6),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ12DEN7",
        "# de crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN7",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12DEN(7),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ12DEN10",
        "# de mulheres grávidas HIV+  que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN10",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12DEN(10),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ12DEN11",
        "# de mulheres grávidas HIV+  que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN11",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12DEN(11),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // Category 12 Part 2 denominator indicators
    dataSetDefinition.addColumn(
        "MQ12DEN3",
        "Adultos (15/+anos) na 1ª linha que iniciaram o TARV há 12 meses atrás ",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN3",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC12P2DEN("A"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "ageBasedOnArt=adultsArt");

    dataSetDefinition.addColumn(
        "MQ12DEN4",
        "Adultos (15/+anos) que iniciaram 2ª linha TARV há 12 meses atrás",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN4",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC12P2DEN("B"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "ageBasedOnArt=adultsArt");

    dataSetDefinition.addColumn(
        "MQ12DEN8",
        "Crianças (0-14 anos) na 1ª linha que iniciaram o TARV há 12 meses atrás",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN8",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC12P2DEN("C"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "ageBasedOnArt=childrenArt");

    dataSetDefinition.addColumn(
        "MQ12DEN9",
        "Crianças (0-14 anos)  que iniciaram 2ª linha TARV há 12 meses atrás",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN9",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC12P2DEN("D"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "ageBasedOnArt=childrenArt");

    dataSetDefinition.addColumn(
        "MQ12DEN12",
        "Mulheres grávidas HIV+ 1ª linha que iniciaram o TARV há 12 meses atrás",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12DEN12",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC12P2DEN("E"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // M&Q Report - Categoria 12 Numerador - P2 Indicators
    dataSetDefinition.addColumn(
        "MQ12NUM123",
        "# de adultos (15/+anos) na 1ª linha que iniciaram o TARV há 12 meses atrás sem registo de saidas",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM123",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NumeratorP2(3),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "ageBasedOnArt=adultsArt");
    dataSetDefinition.addColumn(
        "MQ12NUM124",
        "# de adultos (15/+anos) que iniciaram 2ª linha TARV há 12 meses atrás",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM124",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NumeratorP2(4),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "ageBasedOnArt=adultsArt");
    dataSetDefinition.addColumn(
        "MQ12NUM128",
        "# de crianças (0-14 anos) na 1ª linha que iniciaram o TARV há 12 meses atrás",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM128",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NumeratorP2(8),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "ageBasedOnArt=childrenArt");
    dataSetDefinition.addColumn(
        "MQ12NUM129",
        "de crianças (0-14 anos)  que iniciaram 2ª linha TARV há 12 meses atrás",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM129",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NumeratorP2(9),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "ageBasedOnArt=childrenArt");
    dataSetDefinition.addColumn(
        "MQ12NUM1212",
        "# de mulheres grávidas HIV+ 1ª linha que iniciaram o TARV há 12 meses atrás",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM1212",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NumeratorP2(12),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // Category 12 numerator indicators
    dataSetDefinition.addColumn(
        "MQ12NUM1",
        "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs entre 25 a 33 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NUM(1),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ12NUM2",
        "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que tiveram 3 consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NUM(2),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ12NUM6",
        "# de crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM6",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NUM(6),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ12NUM7",
        "# de crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM7",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NUM(7),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ12NUM10",
        "# de mulheres grávidas HIV+  que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM10",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NUM(10),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ12NUM11",
        "# de mulheres grávidas HIV+  que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ12NUM11",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ12NUM(11),
                    "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // Category 13 Denominator indicators
    dataSetDefinition.addColumn(
        "MQ13DEN1",
        "% de adultos (15/+anos) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(true, 1),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ13DEN4",
        "# de adultos (15/+ anos) na 2a linha de TARV elegíveis a CV.",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN4",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(true, 4),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ13DEN6",
        "% de crianças (0-4 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN6",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(true, 6),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ13DEN7",
        "% de crianças (5-9 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN7",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(true, 7),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ13DEN8",
        "% de crianças (10-14 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN8",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(true, 8),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ13DEN13",
        "# de crianças (>2anos) na 2a linha de TARV elegíveis ao pedido de CV.",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN13",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(true, 13),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // Category 13 Numerator indicators
    dataSetDefinition.addColumn(
        "MQ13NUM1",
        "% de adultos (15/+anos) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM1",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(false, 1),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ13NUM4",
        "% de adultos (15/+anos) na 2a linha de TARV elegíveis a CV com registo de pedido de CV feito pelo clínico",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM4",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(false, 4),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ13NUM6",
        "% de crianças (0-4 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM6",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(false, 6),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ13NUM7",
        "% de crianças (5-9 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM7",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(false, 7),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ13NUM8",
        "% de crianças (10-14 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM8",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(false, 8),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "MQ13NUM13",
        "% de crianças na  2ª linha de TARV elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM13",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13(false, 13),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // Category 13 Part 3 Denominator Indicators
    dataSetDefinition.addColumn(
        "MQ13DEN2",
        "Adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3DEN("A"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    dataSetDefinition.addColumn(
        "MQ13DEN9",
        "Crianças  (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN9",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3DEN("B"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=0-4");

    dataSetDefinition.addColumn(
        "MQ13DEN10",
        "Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN10",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3DEN("C"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=5-9");

    dataSetDefinition.addColumn(
        "MQ13DEN11",
        "crianças  (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV ",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN11",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3DEN("D"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=10-14");

    dataSetDefinition.addColumn(
        "MQ13DEN5",
        "Adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após o início da 2a linha de TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN5",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3DEN("E"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    dataSetDefinition.addColumn(
        "MQ13DEN14",
        "Crianças na 2a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início da 2a linha de TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN14",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3DEN("F"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=2-14");

    dataSetDefinition.addColumn(
        "MQ13DEN3",
        "# de adultos na 1a linha de TARV que receberam um resultado de CV acima de 1000 cópias no período de inclusão",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN3",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13P4(true, 3),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    dataSetDefinition.addColumn(
        "MQ13DEN12",
        "# de crianças (>2 anos de idade) na 1a linha de TARV que receberam um resultado de CV acima de 1000 cópias no período de inclusão",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN12",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13P4(true, 12),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=2-14");

    dataSetDefinition.addColumn(
        "MQ13DEN18",
        "# de MG na 1a linha de TARV que receberam um resultado de CV acima de 1000 cópias no período de inclusão",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13DEN18",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQ13P4(true, 18),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    // M&Q Report - Categoria 13 Numerador - P3 Indicators
    dataSetDefinition.addColumn(
        "MQ13NUM2",
        "Adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM2",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3NUM("A"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    dataSetDefinition.addColumn(
        "MQ13NUM9",
        "Crianças  (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM9",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3NUM("B"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=0-4");

    dataSetDefinition.addColumn(
        "MQ13NUM10",
        "Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM10",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3NUM("C"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=5-9");

    dataSetDefinition.addColumn(
        "MQ13NUM11",
        " crianças  (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM11",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3NUM("D"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=10-14");

    dataSetDefinition.addColumn(
        "MQ13NUM5",
        "adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após o início da 2a linha de TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM5",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3NUM("E"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=15+");

    dataSetDefinition.addColumn(
        "MQ13NUM4",
        "Crianças na 2a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início da 2a linha de TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "MQ13NUM14",
                EptsReportUtils.map(
                    qualityImprovement2020CohortQueries.getMQC13P3NUM("F"),
                    "startDate=${startDate},endDate=${endDate},location=${location}")),
            "startDate=${startDate},endDate=${endDate},location=${location}"),
        "age=2-15");

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
        new Parameter("location", "Unidade Sanitária", Location.class));
  }
}
