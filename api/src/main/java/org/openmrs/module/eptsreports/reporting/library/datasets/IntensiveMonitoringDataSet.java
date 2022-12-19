package org.openmrs.module.eptsreports.reporting.library.datasets;

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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class IntensiveMonitoringDataSet extends BaseDataSet {

  private EptsGeneralIndicator eptsGeneralIndicator;

  private IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries;

  private final QualityImprovement2020CohortQueries qualityImprovement2020CohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  private EptsCommonDimension eptsCommonDimension;

  @Autowired
  public IntensiveMonitoringDataSet(
      EptsGeneralIndicator eptsGeneralIndicator,
      IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries,
      QualityImprovement2020CohortQueries qualityImprovement2020CohortQueries,
      EptsCommonDimension eptsCommonDimension) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.intensiveMonitoringCohortQueries = intensiveMonitoringCohortQueries;
    this.qualityImprovement2020CohortQueries = qualityImprovement2020CohortQueries;
    this.eptsCommonDimension = eptsCommonDimension;
  }

  public DataSetDefinition constructIntensiveMonitoringDataSet() {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("Intensive Monitoring DataSet");
    dataSetDefinition.addParameters(getParameters());

    /**
     * ******** DIMENSIONS will be added here based on individual indicators required
     * *****************************
     */
    dataSetDefinition.addDimension(
        "miAge",
        EptsReportUtils.map(
            eptsCommonDimension.getPatientAgeBasedOnFirstViralLoadDate(),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));

    dataSetDefinition.addDimension(
        "miAge11",
        EptsReportUtils.map(
            eptsCommonDimension.getPatientAgeBasedOnFirstViralLoadDate(),
            "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},location=${location}"));

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${revisionEndDate-4m}"));

    dataSetDefinition.addDimension(
        "ageByEndDateRevision",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${revisionEndDate}"));
    dataSetDefinition.addDimension(
        "ageByEvaluationEndDate",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${revisionEndDate-1m}"));

    // dimensions to be added here
    dataSetDefinition.addDimension(
        "ageBasedOnArt135",
        EptsReportUtils.map(
            eptsCommonDimension.ageBasedOnArtStartDateMOH(),
            "onOrAfter=${revisionEndDate-2m+1d},onOrBefore=${revisionEndDate},location=${location}"));
    dataSetDefinition.addDimension(
        "ageBasedOnArt246",
        EptsReportUtils.map(
            eptsCommonDimension.ageBasedOnArtStartDateMOH(),
            "onOrAfter=${revisionEndDate-8m+1d},onOrBefore=${revisionEndDate},location=${location}"));
    dataSetDefinition.addDimension(
        "ageBasedOnArt54",
        EptsReportUtils.map(
            eptsCommonDimension.ageBasedOnArtStartDateMOH(),
            "onOrAfter=${revisionEndDate-5m+1d},onOrBefore=${revisionEndDate-4m},location=${location}"));
    dataSetDefinition.addDimension(
        "ageBasedOnArt43",
        EptsReportUtils.map(
            eptsCommonDimension.ageBasedOnArtStartDateMOH(),
            "onOrAfter=${revisionEndDate-4m+1d},onOrBefore=${revisionEndDate-3m},location=${location}"));
    dataSetDefinition.addDimension(
        "ageInMonths54",
        EptsReportUtils.map(
            eptsCommonDimension.ageInMonths(), "effectiveDate=${revisionEndDate-4m}"));
    dataSetDefinition.addDimension(
        "ageBasedOnArt32",
        EptsReportUtils.map(
            eptsCommonDimension.ageBasedOnArtStartDateMOH(),
            "onOrAfter=${revisionEndDate-3m+1d},onOrBefore=${revisionEndDate-2m},location=${location}"));
    dataSetDefinition.addDimension(
        "ageBasedOnArt73",
        EptsReportUtils.map(
            eptsCommonDimension.ageBasedOnArtStartDateMOH(),
            "onOrAfter=${revisionEndDate-2m+1d},onOrBefore=${revisionEndDate-1m},location=${location}"));
    /**
     * *********************************** CATEGORY 7 ********************* //*********************
     * Denominator CAT7 **************
     */
    dataSetDefinition.addColumn(
        "MI7DEN1",
        "% de adultos HIV+ em TARV elegíveis ao TPT e que iniciaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMI2021Part135Definition(1),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt135=adultsArt");
    // 7.2
    dataSetDefinition.addColumn(
        "MI7DEN2",
        "% de adultos HIV+ em TARV elegiveis ao TPT que iniciaram e  completaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMI2021Part246Definition(2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt246=adultsArt");
    // 7.3
    dataSetDefinition.addColumn(
        "MI7DEN3",
        "% de crianças HIV+ em TARV elegiveis ao TPT  e que iniciaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMI2021Part135Definition(3),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt73=1-14");
    // 7.4
    dataSetDefinition.addColumn(
        "MI7DEN4",
        "% de crianças HIV+ em TARV elegíveis que iniciaram e completaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMI2021Part246Definition(4),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt246=childrenArt");
    // 7.5
    dataSetDefinition.addColumn(
        "MI7DEN5",
        "% de mulheres grávidas HIV+ elegíveis ao TPI e que iniciaram TPI",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMI2021Part135Definition(5),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 7.6
    dataSetDefinition.addColumn(
        "MI7DEN6",
        "% de MG HIV+ em TARV que iniciou TPI e que terminou TPI",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMI2021Part246Definition(6),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // ********************* NUMERATOR CAT7 **************
    // 7.1

    dataSetDefinition.addColumn(
        "MI7NUM1",
        "% de adultos HIV+ em TARV elegíveis ao TPT e que iniciaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7NumMI2021Part135Definition(1),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt135=adultsArt");
    // 7.2
    dataSetDefinition.addColumn(
        "MI7NUM2",
        "% de adultos HIV+ em TARV elegiveis ao TPT que iniciaram e  completaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7NumMI2021Part246Definition(2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt246=adultsArt");
    // 7.3
    dataSetDefinition.addColumn(
        "MI7NUM3",
        "% de crianças HIV+ em TARV elegiveis ao TPT  e que iniciaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7NumMI2021Part135Definition(3),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt73=1-14");
    // 7.4
    dataSetDefinition.addColumn(
        "MI7NUM4",
        "% de crianças HIV+ em TARV elegíveis que iniciaram e completaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7NumMI2021Part246Definition(4),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt246=childrenArt");
    // 7.5
    dataSetDefinition.addColumn(
        "MI7NUM5",
        "% de mulheres grávidas HIV+ elegíveis ao TPI e que iniciaram TPI",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7NumMI2021Part135Definition(5),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 7.6
    dataSetDefinition.addColumn(
        "MI7NUM6",
        "% de MG HIV+ em TARV que iniciou TPI e que terminou TPI",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7NumMI2021Part246Definition(6),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    /**
     * *********************************** CATEGORY 13 ********************* //*********************
     * PART 1 **************
     */

    // CAT 13 P2 DENOMINATOR
    // 13.15
    dataSetDefinition.addColumn(
        "MI13DEN15",
        "% de MG elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram TARV na CPN) Denominator: # de MG com registo de início do TARV na CPN dentro do período de inclusão. (Line 90,Column F in the Template) as following",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part2(15, "DEN15"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // 13.16
    dataSetDefinition.addColumn(
        "MI13DEN16",
        "% de MG elegíveis a CV com registo de pedido de CV feito pelo clínico na primeira CPN (MG que entraram em TARV na CPN) Denominator:# de MG que tiveram a primeira CPN no período de inclusão, e que já estavam em TARV há mais de 3 meses",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part2(16, "DEN16"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // 13.17
    dataSetDefinition.addColumn(
        "MI13DEN17",
        "% de MG que receberam o resultado da Carga Viral dentro de 33 dias após pedido Denominator: # de MG com registo de pedido de CV no período de revisão",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part2(17, "DEN17"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // CAT 13 P2 NUMERATOR
    // 13.15
    dataSetDefinition.addColumn(
        "MI13NUM15",
        "% de MG elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram TARV na CPN) Denominator: # de MG com registo de início do TARV na CPN dentro do período de inclusão. (Line 90,Column F in the Template) as following",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part2(15, "NUM15"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // 13.16
    dataSetDefinition.addColumn(
        "MI13NUM16",
        "% de MG elegíveis a CV com registo de pedido de CV feito pelo clínico na primeira CPN (MG que entraram em TARV na CPN) Denominator:# de MG que tiveram a primeira CPN no período de inclusão, e que já estavam em TARV há mais de 3 meses",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part2(16, "NUM16"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // 13.17
    dataSetDefinition.addColumn(
        "MI13NUM17",
        "% de MG que receberam o resultado da Carga Viral dentro de 33 dias após pedido Denominator: # de MG com registo de pedido de CV no período de revisão",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part2(17, "NUM17"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // Category 13 Part-1

    // 13.1
    dataSetDefinition.addColumn(
        "MI13DEN1",
        "% de adultos (15/+anos) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(1, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.6
    dataSetDefinition.addColumn(
        "MI13DEN6",
        "% de crianças (0-4 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(6, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.7
    dataSetDefinition.addColumn(
        "MI13DEN7",
        "% de crianças (5-9 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(7, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.8
    dataSetDefinition.addColumn(
        "MI13DEN8",
        "% de crianças (10-14 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(8, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.1
    dataSetDefinition.addColumn(
        "MI13NUM1",
        "% de adultos (15/+anos) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(1, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.6
    dataSetDefinition.addColumn(
        "MI13NUM6",
        "% de crianças (0-4 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(6, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.7
    dataSetDefinition.addColumn(
        "MI13NUM7",
        "% de crianças (5-9 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(7, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.8
    dataSetDefinition.addColumn(
        "MI13NUM8",
        "% de crianças (10-14 anos de idade) na 1a linha de TARV que tiveram consulta clínica no período de revisão, eram elegíveis ao pedido de CV e com registo de pedido de CV feito pelo clínico.",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(8, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.2 DEN
    dataSetDefinition.addColumn(
        "MI13DEN2",
        "Adultos (15/+anos) que iniciaram a 1a linha de TARV ou novo regime da 1ª linha há 9 meses atrás",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13DEN2(2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.2 NUM
    dataSetDefinition.addColumn(
        "MI13NUM2",
        "Adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início do TARV",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13NUM2(2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.5 DEN
    dataSetDefinition.addColumn(
        "MI13DEN5",
        "Adultos (15/+anos) com registo de início da 2a linha de TARV há 9 meses",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13DEN5(5),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.5 NUM
    dataSetDefinition.addColumn(
        "MI13NUM5",
        "Adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após o início da 2a linha de TARV",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13NUM5(5),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.9 DEN
    dataSetDefinition.addColumn(
        "MI13DEN9",
        "Crianças (0-4 anos de idade) com registo de início da 1a linha de TARV há 9 meses",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13DEN9(9),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.9 NUM
    dataSetDefinition.addColumn(
        "MI13NUM9",
        "Crianças  (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13NUM9(9),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.10 DEN
    dataSetDefinition.addColumn(
        "MI13DEN10",
        "Crianças  (5-9 anos de idade) com registo de início da 1a linha de TARV ou novo regime de TARV há 9 meses",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13DEN10(10),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.10 NUM
    dataSetDefinition.addColumn(
        "MI13NUM10",
        "Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13NUM10(10),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.11 DEN
    dataSetDefinition.addColumn(
        "MI13DEN11",
        "Crianças  (10-14 anos de idade) com registo de início da 1a linha de TARV ou novo regime da 1ª linha de TARV no mês de avaliação",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13DEN11(11),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.11 NUM
    dataSetDefinition.addColumn(
        "MI13NUM11",
        "Crianças (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13NUM11(11),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.14 DEN
    dataSetDefinition.addColumn(
        "MI13DEN14",
        "Crianças com registo de início da 2a linha de TARV no mês de avaliação",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13DEN14(14),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.141 NUM
    dataSetDefinition.addColumn(
        "MI13NUM14",
        "Crianças na 2a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início da 2a linha de TARV no mês de avaliação",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13NUM14(14),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // ********************* PART 2 **************

    // 13.4
    dataSetDefinition.addColumn(
        "MI13DEN4",
        "# de adultos (15/+ anos) na 2a linha de TARV elegíveis a CV.",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(4, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.13
    dataSetDefinition.addColumn(
        "MI13DEN13",
        "# de crianças (>2anos) na 2a linha de TARV elegíveis ao pedido de CV.",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(13, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.4
    dataSetDefinition.addColumn(
        "MI13NUM4",
        "# de adultos (15/+ anos) na 2a linha de TARV elegíveis a CV.",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(4, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.13
    dataSetDefinition.addColumn(
        "MI13NUM13",
        "# de crianças (>2anos) na 2a linha de TARV elegíveis ao pedido de CV.",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat13Den(13, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    /**
     * *********************************** CATEGORY 11 ********************* //*********************
     * Denominator **************
     */
    // 11.1
    dataSetDefinition.addColumn(
        "MI11DEN1",
        "MI DEN 11.1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(1),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=adultsArt");
    // 11.2
    dataSetDefinition.addColumn(
        "MI11DEN2",
        "MI DEN 11.2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "miAge11=MqAdults");
    // 11.3
    dataSetDefinition.addColumn(
        "MI11DEN3",
        "MI DEN 11.3",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(3),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 11.4
    dataSetDefinition.addColumn(
        "MI11DEN4",
        "MI DEN 11.4",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(4),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 11.5
    dataSetDefinition.addColumn(
        "MI11DEN5",
        "MI DEN 11.5",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(5),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=2-14"); // porque isto inclui intervalos
    // 11.6
    dataSetDefinition.addColumn(
        "MI11DEN6",
        "MI DEN 11.6",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(6),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageInMonths54=9m-");
    // 11.7
    dataSetDefinition.addColumn(
        "MI11DEN7",
        "MI DEN 11.7",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(7),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "miAge11=MqChildren");

    // ********************* Numerator **************
    // 11.1
    dataSetDefinition.addColumn(
        "MI11NUM1",
        "MI NUM 11.1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(1),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=adultsArt");
    // 11.2
    dataSetDefinition.addColumn(
        "MI11NUM2",
        "MI NUM 11.2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "miAge11=MqAdults");
    // 11.3
    dataSetDefinition.addColumn(
        "MI11NUM3",
        "MI NUM 11.3",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(3),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 11.4
    dataSetDefinition.addColumn(
        "MI11NUM4",
        "MI NUM 11.4",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(4),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt246=childrenArt");
    // 11.5
    dataSetDefinition.addColumn(
        "MI11NUM5",
        "MI NUM 11.5",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(5),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=2-14");
    // 11.6
    dataSetDefinition.addColumn(
        "MI11NUM6",
        "MI NUM 11.6",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(6),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageInMonths54=9m-");
    // 11.7
    dataSetDefinition.addColumn(
        "MI11NUM7",
        "MI NUM 11.7",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(7),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "miAge11=MqChildren");

    /**
     * *********************************** CATEGORY 12 ******************************************
     * //* Part 1 Denominator **************
     */
    // 12.1
    dataSetDefinition.addColumn(
        "MI12P1DEN1",
        "MI DEN 12.1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(1, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt32=adultsArt");
    // 12.2
    dataSetDefinition.addColumn(
        "MI12P1DEN2",
        "MI DEN 12.2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(2, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=adultsArt");
    // 12.5
    dataSetDefinition.addColumn(
        "MI12P1DEN5",
        "MI DEN 12.5",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(5, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt32=childrenArt");
    // 12.6
    dataSetDefinition.addColumn(
        "MI12P1DEN6",
        "MI DEN 12.6",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(6, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=childrenArt");
    // 12.9
    dataSetDefinition.addColumn(
        "MI12P1DEN9",
        "MI DEN 12.9",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(9, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 12.10
    dataSetDefinition.addColumn(
        "MI12P1DEN10",
        "MI DEN 12.10",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(10, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // ******* */ Part 1 Numerator **************
    // 12.1
    dataSetDefinition.addColumn(
        "MI12P1NUM1",
        "MI NUM 12.1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(1, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt32=adultsArt");
    // 12.2
    dataSetDefinition.addColumn(
        "MI12P1NUM2",
        "MI NUM 12.2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(2, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=adultsArt");
    // 12.5
    dataSetDefinition.addColumn(
        "MI12P1NUM5",
        "MI NUM 12.5",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(5, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt32=childrenArt");
    // 12.6
    dataSetDefinition.addColumn(
        "MI12P1NUM6",
        "MI NUM 12.6",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(6, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=childrenArt");
    // 12.9
    dataSetDefinition.addColumn(
        "MI12P1NUM9",
        "MI NUM 12.9",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(9, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 12.10
    dataSetDefinition.addColumn(
        "MI12P1NUM10",
        "MI NUM 12.10",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(10, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    /* Category 15*/

    // Den 15
    dataSetDefinition.addColumn(
        "MI15DEN1",
        "MI CAT 15 DEN 1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(true, 1),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MI15DEN2",
        "MI CAT 15 DEN 2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(true, 2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MI15DEN3",
        "MI CAT 15 DEN 3",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(true, 3),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // NUM 15
    dataSetDefinition.addColumn(
        "MI15NUM1",
        "MI CAT 15 NUM 1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(false, 1),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MI15NUM2",
        "MI CAT 15 NUM 2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(false, 2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MI15NUM3",
        "MI CAT 15 NUM 3",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(false, 3),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    /**
     * *********************************** CATEGORY 11 ********************* //*********************
     * Denominator **************
     */
    // 11.1
    dataSetDefinition.addColumn(
        "MI11DEN1",
        "MI DEN 11.1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(1),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=adultsArt");
    // 11.2
    dataSetDefinition.addColumn(
        "MI11DEN2",
        "MI DEN 11.2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "miAge11=MqAdults");
    // 11.3
    dataSetDefinition.addColumn(
        "MI11DEN3",
        "MI DEN 11.3",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(3),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 11.4
    dataSetDefinition.addColumn(
        "MI11DEN4",
        "MI DEN 11.4",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(4),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 11.5
    dataSetDefinition.addColumn(
        "MI11DEN5",
        "MI DEN 11.5",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(5),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=2-14"); // porque isto inclui intervalos
    // 11.6
    dataSetDefinition.addColumn(
        "MI11DEN6",
        "MI DEN 11.6",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(6),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageInMonths54=9m-");
    // 11.7
    dataSetDefinition.addColumn(
        "MI11DEN7",
        "MI DEN 11.7",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11DEN(7),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "miAge11=MqChildren");

    // ********************* Numerator **************
    // 11.1
    dataSetDefinition.addColumn(
        "MI11NUM1",
        "MI NUM 11.1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(1),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=adultsArt");
    // 11.2
    dataSetDefinition.addColumn(
        "MI11NUM2",
        "MI NUM 11.2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "miAge11=MqAdults");
    // 11.3
    dataSetDefinition.addColumn(
        "MI11NUM3",
        "MI NUM 11.3",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(3),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 11.4
    dataSetDefinition.addColumn(
        "MI11NUM4",
        "MI NUM 11.4",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(4),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt246=childrenArt");
    // 11.5
    dataSetDefinition.addColumn(
        "MI11NUM5",
        "MI NUM 11.5",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(5),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=2-14");
    // 11.6
    dataSetDefinition.addColumn(
        "MI11NUM6",
        "MI NUM 11.6",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(6),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageInMonths54=9m-");
    // 11.7
    dataSetDefinition.addColumn(
        "MI11NUM7",
        "MI NUM 11.7",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMIC11NUM(7),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "miAge11=MqChildren");

    /**
     * *********************************** CATEGORY 12 *******************************************
     * //* Part 1 Denominator **************
     */
    // 12.1
    dataSetDefinition.addColumn(
        "MI12P1DEN1",
        "MI DEN 12.1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(1, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt32=adultsArt");
    // 12.2
    dataSetDefinition.addColumn(
        "MI12P1DEN2",
        "MI DEN 12.2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(2, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=adultsArt");
    // 12.5
    dataSetDefinition.addColumn(
        "MI12P1DEN5",
        "MI DEN 12.5",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(5, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt32=childrenArt");
    // 12.6
    dataSetDefinition.addColumn(
        "MI12P1DEN6",
        "MI DEN 12.6",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(6, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=childrenArt");
    // 12.9
    dataSetDefinition.addColumn(
        "MI12P1DEN9",
        "MI DEN 12.9",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(9, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 12.10
    dataSetDefinition.addColumn(
        "MI12P1DEN10",
        "MI DEN 12.10",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(10, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // ******* */ Part 1 Numerator **************
    // 12.1
    dataSetDefinition.addColumn(
        "MI12P1NUM1",
        "MI NUM 12.1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(1, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt32=adultsArt");
    // 12.2
    dataSetDefinition.addColumn(
        "MI12P1NUM2",
        "MI NUM 12.2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(2, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=adultsArt");
    // 12.5
    dataSetDefinition.addColumn(
        "MI12P1NUM5",
        "MI NUM 12.5",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(5, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt32=childrenArt");
    // 12.6
    dataSetDefinition.addColumn(
        "MI12P1NUM6",
        "MI NUM 12.6",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(6, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageBasedOnArt54=childrenArt");
    // 12.9
    dataSetDefinition.addColumn(
        "MI12P1NUM9",
        "MI NUM 12.9",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(9, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 12.10
    dataSetDefinition.addColumn(
        "MI12P1NUM10",
        "MI NUM 12.10",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat12P1DenNum(10, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    /* Category 15*/
    // Den 15
    dataSetDefinition.addColumn(
        "MI15DEN1",
        "MI CAT 15 DEN 1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(true, 1),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MI15DEN2",
        "MI CAT 15 DEN 2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(true, 2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MI15DEN3",
        "MI CAT 15 DEN 3",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(true, 3),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // NUM 15
    dataSetDefinition.addColumn(
        "MI15NUM1",
        "MI CAT 15 NUM 1",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(false, 1),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MI15NUM2",
        "MI CAT 15 NUM 2",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(false, 2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "MI15NUM3",
        "MI CAT 15 NUM 3",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat15P1DenNum(false, 3),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // 13.3 P4 Den
    dataSetDefinition.addColumn(
        "MI13DEN3",
        "# de adultos na 1a linha de TARV que receberam um resultado de CV acima de 1000 cópias no período de inclusão",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part4(3, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=15+");

    // 13.12 P4 Den
    dataSetDefinition.addColumn(
        "MI13DEN12",
        "# de crianças (>2 anos de idade) na 1a linha de TARV que receberam um resultado de CV acima de 1000 cópias no período de inclusão",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part4(12, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // 13.18 P4 Den
    dataSetDefinition.addColumn(
        "MI13DEN18",
        "# de MG na 1a linha de TARV que receberam um resultado de CV acima de 1000 cópias no período de inclusão",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part4(18, false),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // 13.3 P4 Num
    dataSetDefinition.addColumn(
        "MI13NUM3",
        "% de Adultos (15/+anos) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês após terem recebido o último resultado de CV acima de 1000 e terem  3 sessões consecutivas de APSS/PP",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part4(3, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "miAge=MqAdults");

    // 13.12 P4 Num
    dataSetDefinition.addColumn(
        "MI13NUM12",
        "% de crianças (>2 anos de idade) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês após terem recebido o último resultado de CV acima de 1000 cópia e terem  3 sessões consecutivas de APSS/PP",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part4(12, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "age=2-14");

    // 13.18 P4 Num
    dataSetDefinition.addColumn(
        "MI13NUM18",
        "% de MG na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês após terem recebido o último resultado de CV acima de 1000 cópia e terem 3 sessões consecutivas de APSS/PP",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICat13Part4(18, true),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    /**
     * *********************************** MI CATEGORY 14 *********************
     * //********************* MI CAT14 Inherited from the MQCAT14 with adjusted date parameters
     * **************
     */
    // MI CAT 14 Denominator
    // CAT 14 DEN 1
    dataSetDefinition.addColumn(
        "MI14DEN1",
        "14.1. % de utentes (<1 ano) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=<1");
    // CAT 14 DEN 2
    dataSetDefinition.addColumn(
        "MI14DEN2",
        "14.2.% de utentes (1- 4 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=1-4");
    // CAT 14 DEN 3
    dataSetDefinition.addColumn(
        "MI14DEN3",
        "14.3.% de utentes (5 - 9 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=5-9");
    // CAT 14 DEN 4
    dataSetDefinition.addColumn(
        "MI14DEN4",
        "14.4. % de utentes (10 - 14 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=10-14");
    // CAT 14 DEN 5
    dataSetDefinition.addColumn(
        "MI14DEN5",
        "14.5. % de utentes (15 -19 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=15-19");
    // CAT 14 DEN 6
    dataSetDefinition.addColumn(
        "MI14DEN6",
        "14.6. % de utentes (20+ anos) em TARV com supressão viral (CV<1000 Cps/ml",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=20+");
    // CAT 14 DEN 7
    dataSetDefinition.addColumn(
        "MI14DEN7",
        "14.7. % de MG em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A_AND_A1, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // CAT 14 DEN 8
    dataSetDefinition.addColumn(
        "MI14DEN8",
        "14.8. % de ML em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A_AND_A2, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // MI CAT 14 Numerator
    // CAT 14 NUM 1
    dataSetDefinition.addColumn(
        "MI14NUM1",
        "14.1. % de utentes (<1 ano) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=<1");
    // CAT 14 NUM 2
    dataSetDefinition.addColumn(
        "MI14NUM2",
        "14.2.% de utentes (1- 4 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=1-4");
    // CAT 14 NUM 3
    dataSetDefinition.addColumn(
        "MI14NUM3",
        "14.3.% de utentes (5 - 9 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=5-9");

    // CAT 14 NUM 4
    dataSetDefinition.addColumn(
        "MI14NUM4",
        "14.4. % de utentes (10 - 14 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=10-14");

    // CAT 14 NUM 5
    dataSetDefinition.addColumn(
        "MI14NUM5",
        "14.5. % de utentes (15 -19 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=15-19");
    // CAT 14 NUM 6
    dataSetDefinition.addColumn(
        "MI14NUM6",
        "14.6. % de utentes (20+ anos) em TARV com supressão viral (CV<1000 Cps/ml",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=20+");
    // CAT 14 NUM 7
    dataSetDefinition.addColumn(
        "MI14NUM7",
        "14.7. % de MG em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B_AND_B1, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // CAT 14 NUM 8
    dataSetDefinition.addColumn(
        "MI14NUM8",
        "14.8. % de ML em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B_AND_B2, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    /**
     * *********************************** MI CATEGORY 14 *********************
     * //********************* MI CAT14 Inherited from the MQCAT14 with adjusted date parameters
     * **************
     */
    // MI CAT 14 Denominator
    // CAT 14 DEN 1
    dataSetDefinition.addColumn(
        "MI14DEN1",
        "14.1. % de utentes (<1 ano) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=<1");
    // CAT 14 DEN 2
    dataSetDefinition.addColumn(
        "MI14DEN2",
        "14.2.% de utentes (1- 4 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=1-4");
    // CAT 14 DEN 3
    dataSetDefinition.addColumn(
        "MI14DEN3",
        "14.3.% de utentes (5 - 9 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=5-9");
    // CAT 14 DEN 4
    dataSetDefinition.addColumn(
        "MI14DEN4",
        "14.4. % de utentes (10 - 14 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=10-14");
    // CAT 14 DEN 5
    dataSetDefinition.addColumn(
        "MI14DEN5",
        "14.5. % de utentes (15 -19 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=15-19");
    // CAT 14 DEN 6
    dataSetDefinition.addColumn(
        "MI14DEN6",
        "14.6. % de utentes (20+ anos) em TARV com supressão viral (CV<1000 Cps/ml",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=20+");
    // CAT 14 DEN 7
    dataSetDefinition.addColumn(
        "MI14DEN7",
        "14.7. % de MG em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A_AND_A1, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // CAT 14 DEN 8
    dataSetDefinition.addColumn(
        "MI14DEN8",
        "14.8. % de ML em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.A_AND_A2, "DEN"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // MI CAT 14 Numerator
    // CAT 14 NUM 1
    dataSetDefinition.addColumn(
        "MI14NUM1",
        "14.1. % de utentes (<1 ano) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=<1");
    // CAT 14 NUM 2
    dataSetDefinition.addColumn(
        "MI14NUM2",
        "14.2.% de utentes (1- 4 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=1-4");
    // CAT 14 NUM 3
    dataSetDefinition.addColumn(
        "MI14NUM3",
        "14.3.% de utentes (5 - 9 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=5-9");

    // CAT 14 NUM 4
    dataSetDefinition.addColumn(
        "MI14NUM4",
        "14.4. % de utentes (10 - 14 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=10-14");

    // CAT 14 NUM 5
    dataSetDefinition.addColumn(
        "MI14NUM5",
        "14.5. % de utentes (15 -19 anos) em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=15-19");
    // CAT 14 NUM 6
    dataSetDefinition.addColumn(
        "MI14NUM6",
        "14.6. % de utentes (20+ anos) em TARV com supressão viral (CV<1000 Cps/ml",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "ageByEndDateRevision=20+");
    // CAT 14 NUM 7
    dataSetDefinition.addColumn(
        "MI14NUM7",
        "14.7. % de MG em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B_AND_B1, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // CAT 14 NUM 8
    dataSetDefinition.addColumn(
        "MI14NUM8",
        "14.8. % de ML em TARV com supressão viral (CV<1000 Cps/ml)",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMICAT14(
                    QualityImprovement2020CohortQueries.MQCat14Preposition.B_AND_B2, "NUM"),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    // ************************* MI CATEGORY 15 MDS INDICATORS **********************

    CohortIndicator MI15DEN13 =
        eptsGeneralIndicator.getIndicator(
            "MI15DEN13",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMI15Den13(),
                "startDate=${startDate},revisionEndDate=${endDate},location=${location}"));

    MI15DEN13.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI15DEN13",
        "15.13 - % de pacientes elegíveis a MDS, que foram inscritos em MDS",
        EptsReportUtils.map(
            MI15DEN13,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"),
        "ageByEvaluationEndDate=2+");

    CohortIndicator MI15NUM13 =
        eptsGeneralIndicator.getIndicator(
            "MI15NUM13",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMI15Nume13(),
                "startDate=${startDate},revisionEndDate=${endDate},location=${location}"));

    MI15NUM13.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI15NUM13",
        "Numerator:  “# de pacientes elegíveis a MDS ",
        EptsReportUtils.map(
            MI15NUM13,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"),
        "ageByEvaluationEndDate=2+");

    CohortIndicator MI15DEN14 =
        eptsGeneralIndicator.getIndicator(
            "MI15DEN14",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15MdsDen14(),
                "startDate=${startDate},revisionEndDate=${endDate},location=${location}"));

    MI15DEN14.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI15DEN14",
        "15.14 - % de inscritos em MDS que receberam CV acima de 1000 cópias  ",
        EptsReportUtils.map(
            MI15DEN14,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"),
        "ageByEvaluationEndDate=2+");

    CohortIndicator MI15NUM14 =
        eptsGeneralIndicator.getIndicator(
            "MI15NUM14",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQ15MdsNum14(),
                "startDate=${startDate},revisionEndDate=${endDate},location=${location}"));

    MI15NUM14.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI15NUM14",
        "Numerator: # de pacientes inscritos em MDS para pacientes estáveis ",
        EptsReportUtils.map(
            MI15NUM14,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"),
        "ageByEvaluationEndDate=2+");

    CohortIndicator MI15DEN15 =
        eptsGeneralIndicator.getIndicator(
            "MI15DEN15",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMI15Den15(),
                "startDate=${startDate},revisionEndDate=${endDate},location=${location}"));

    MI15DEN15.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI15DEN15",
        "Numerator 15.15 : # de pacientes inscritos em MDS para pacientes estáveis - 21 meses",
        EptsReportUtils.map(
            MI15DEN15,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"),
        "ageByEvaluationEndDate=2+");

    CohortIndicator MI15NUM15 =
        eptsGeneralIndicator.getIndicator(
            "MI15NUM15",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMI15Num15(),
                "startDate=${startDate},revisionEndDate=${endDate},location=${location}"));

    MI15NUM15.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI15NUM15",
        "Numerator: # de pacientes inscritos em MDS para pacientes estáveis ",
        EptsReportUtils.map(
            MI15NUM15,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"),
        "ageByEvaluationEndDate=2+");

    CohortIndicator MI15DEN16 =
        eptsGeneralIndicator.getIndicator(
            "MI15DEN16",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQDen15Dot16(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));
    MI15DEN16.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI15DEN16",
        "15.16. % de utentes inscritos em MDS (para pacientes estáveis) com supressão viral",
        EptsReportUtils.map(
            MI15DEN16,
            "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate},location=${location}"),
        "");

    CohortIndicator MI15NUM16 =
        eptsGeneralIndicator.getIndicator(
            "MI15NUM16",
            EptsReportUtils.map(
                qualityImprovement2020CohortQueries.getMQNum15Dot16(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));
    MI15NUM16.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    dataSetDefinition.addColumn(
        "MI15NUM16",
        "15.16. % de utentes inscritos em MDS (para pacientes estáveis) com supressão viral",
        EptsReportUtils.map(
            MI15NUM16,
            "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate},location=${location}"),
        "");

    return dataSetDefinition;
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
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
