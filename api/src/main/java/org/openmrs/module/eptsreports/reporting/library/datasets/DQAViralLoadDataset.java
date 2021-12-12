package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DQAViralLoadDataset extends BaseDataSet {

  private static final String NUMBER_OF_ACTIVE_PATIENTS_IN_ART_WHO_RECEIVED_VIRAL_LOAD_TEST_RESULT =
      "DQA/NUMBER_OF_ACTIVE_PATIENTS_IN_ART_WHO_RECEIVED_VIRAL_LOAD_TEST_RESULT.sql";

  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;
  @Autowired private EptsCommonDimension eptsCommonDimension;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructDataSetSESP() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    final String mappingsB1M3 = "startDate=${startDate+2m},endDate=${endDate},location=${location}";
    final String mappingsB1M2 =
        "startDate=${startDate+1m},endDate=${startDate+2m-1d},location=${location}";
    final String mappingsB1M1 =
        "startDate=${startDate},endDate=${startDate+1m-1d},location=${location}";

    dataSetDefinition.setName(
        "Número de Activos em TARV no fim do período de revisão (SESP - SISMA)");

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.ageDQA(ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    dataSetDefinition.addParameters(getParameters());

    addRow(
        dataSetDefinition,
        "DAQRM",
        "Numero de Activos em TARV no fim do período de revisao (SESP - SISMA)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Number de Activos em TARV no fim do período de revisao (SESP - SISMA)",
                EptsReportUtils.map(
                    this.resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
                    "endDate=${endDate}")),
            "endDate=${endDate}"),
        getDQAColumns());

    addRow(
        dataSetDefinition,
        "DAQMER",
        "Número de Activos em TARV no fim do período de revisão (SESP - DATIM)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Número de Activos em TARV no fim do período de revisão (SESP - DATIM)",
                EptsReportUtils.map(
                    this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(),
                    "endDate=${endDate}")),
            "endDate=${endDate}"),
        getDQAColumns());

    addRow(
        dataSetDefinition,
        "DAQRM2",
        "Número de Novos Inícios em TARV durante o período de revisão Resumo Mensal B1",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Número de Activos em TARV no fim do período de revisão (SESP - DATIM)",
                EptsReportUtils.map(
                    this.resumoMensalCohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
                    mappingsB1M3)),
            mappingsB1M3),
        getDQAColumns());

    addRow(
        dataSetDefinition,
        "DAQRM3",
        "Número de Novos Inícios em TARV durante o período de revisão Resumo Mensal B1",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Número de Activos em TARV no fim do período de revisão (SESP - DATIM)",
                EptsReportUtils.map(
                    this.resumoMensalCohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
                    mappingsB1M2)),
            mappingsB1M2),
        getDQAColumns());

    addRow(
        dataSetDefinition,
        "DAQRM4",
        "Número de Novos Inícios em TARV durante o período de revisão Resumo Mensal B1",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Número de Activos em TARV no fim do período de revisão (SESP - DATIM)",
                EptsReportUtils.map(
                    this.resumoMensalCohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
                    mappingsB1M1)),
            mappingsB1M1),
        getDQAColumns());

    dataSetDefinition.addColumn(
        "DAQRM5",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominator Total",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2(),
                    mappingsB1M3)),
            mappingsB1M3),
        "age=0-14");

    dataSetDefinition.addColumn(
        "DAQRM6",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominator Total",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2(),
                    mappingsB1M3)),
            mappingsB1M3),
        "age=15+");

    dataSetDefinition.addColumn(
        "DAQRM7",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominator Total",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2(),
                    mappingsB1M2)),
            mappingsB1M2),
        "age=0-14");

    dataSetDefinition.addColumn(
        "DAQRM8",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominator Total",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2(),
                    mappingsB1M2)),
            mappingsB1M2),
        "age=15+");

    dataSetDefinition.addColumn(
        "DAQRM9",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominator Total",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2(),
                    mappingsB1M1)),
            mappingsB1M1),
        "age=0-14");

    dataSetDefinition.addColumn(
        "DAQRM10",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominator Total",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2(),
                    mappingsB1M1)),
            mappingsB1M1),
        "age=15+");

    return dataSetDefinition;
  }

  private List<ColumnParameters> getDQAColumns() {

    ColumnParameters dqa1 = new ColumnParameters("0-4", "0-4", "age=0-4", "01");

    ColumnParameters dqa2 = new ColumnParameters("5-9", "5-9", "age=5-9", "02");

    ColumnParameters dqa3 =
        new ColumnParameters("10-14", "10-14 years female", "gender=F|age=10-14", "03");

    ColumnParameters dqa4 =
        new ColumnParameters("10-14", "10-14 years female", "gender=M|age=10-14", "04");

    ColumnParameters dqa5 =
        new ColumnParameters("15-19", "15-19 years female", "gender=F|age=15-19", "05");

    ColumnParameters dqa6 =
        new ColumnParameters("15-19", "15-19 years female", "gender=M|age=15-19", "06");

    ColumnParameters dqa7 =
        new ColumnParameters("20+", "20+ years female", "gender=F|age=20+", "07");

    ColumnParameters dqa8 =
        new ColumnParameters("20+", "20+ years female", "gender=M|age=20+", "08");

    return Arrays.asList(dqa1, dqa2, dqa3, dqa4, dqa5, dqa6, dqa7, dqa8);
  }

  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }

  public DataSetDefinition constructDataset(List<Parameter> list) {

    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("DQA CV");
    dsd.addParameters(list);
    dsd.setSqlQuery(
        EptsQuerysUtils.loadQuery(
            NUMBER_OF_ACTIVE_PATIENTS_IN_ART_WHO_RECEIVED_VIRAL_LOAD_TEST_RESULT));
    return dsd;
  }
}
