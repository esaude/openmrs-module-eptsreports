package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.IntensiveMonitoringCohortQueries;
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

  private EptsGeneralIndicator eptsGeneralIndicator;

  private IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  private EptsCommonDimension eptsCommonDimension;

  @Autowired
  public IntensiveMonitoringDataSet(
      EptsGeneralIndicator eptsGeneralIndicator,
      IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries,
      EptsCommonDimension eptsCommonDimension) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.intensiveMonitoringCohortQueries = intensiveMonitoringCohortQueries;
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
    // CAT 7 Denominator

    // 7.1
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
        "Adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início do TARV",
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
        "Adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após o início da 2a linha de TARV",
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
        "Crianças  (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
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
        "Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
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
        "Crianças (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
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
        "Crianças na 2a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início da 2a linha de TARV",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getMI13DEN14(14),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 13.141 NUM
    dataSetDefinition.addColumn(
        "MI13NUM14",
        "Crianças na 2a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início da 2a linha de TARV",
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
        "ageBasedOnArt135=childrenArt");
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
        "miAge=MqAdults");

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
