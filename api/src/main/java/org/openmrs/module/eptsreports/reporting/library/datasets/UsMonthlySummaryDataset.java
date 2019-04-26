package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.UsMonthlySummaryCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UsMonthlySummaryDataset extends BaseDataSet {
  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private UsMonthlySummaryCohortQueries usMonthlySummaryCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  private String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

  public DataSetDefinition constructUsMonthlySummaryDataset() {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("US Monthly Summary Data Set");
    dataSetDefinition.addParameters(getParameters());

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    addAPDIndicatorsWithoutDisaggregations(dataSetDefinition);
    addPDFIndicatorsWithDisaggregations(dataSetDefinition);

    return dataSetDefinition;
  }

  private void addAPDIndicatorsWithoutDisaggregations(
      CohortIndicatorDataSetDefinition dataSetDefinition) {
    dataSetDefinition.addColumn(
        "APDF1",
        "PDF Format assessment By End Date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getPdfFormatAssetAtFinalDate",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.getPdfFormatAssetAtFinalDate(), mappings)),
            "endDate=${startDate},location=${location}"),
        "");

    dataSetDefinition.addColumn(
        "APDF2",
        "PDF Format assessment Within Reporting Date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getPdfFormatAssetWithinReportingPeriod",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.getPdfFormatAssetWithinReportingPeriod(),
                    mappings)),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "APDF3",
        "Disaggregated PDF Format assesment Within Reporting Date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getDisaggregatedPdfFormatAssetWithinReportingPeriod",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries
                        .getDisaggregatedPdfFormatAssetWithinReportingPeriod(),
                    mappings)),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "APDF4",
        "PDF Format assesment By End Date - Children",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getPdfFormatAssetAtFinalDateForChildren",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.getPdfFormatAssetAtFinalDateForChildren(),
                    mappings)),
            "endDate=${startDate},location=${location}"),
        "");
    dataSetDefinition.addColumn(
        "APDF5",
        "PDF Format assesment Within Reporting Period - Children",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getPdfFormatAssetWithinReportingPeriodForChildren",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries
                        .getPdfFormatAssetWithinReportingPeriodForChildren(),
                    mappings)),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "APDF6",
        "Disaggregated PDF Format assesment Within Reporting Period - Children",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getDisaggregatedPdfFormatAssetWithinReportingPeriodForChildren",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries
                        .getDisaggregatedPdfFormatAssetWithinReportingPeriodForChildren(),
                    mappings)),
            mappings),
        "");
  }

  private void addRow(
      CohortIndicatorDataSetDefinition dataSetDefinition,
      String baseName,
      String baseLabel,
      String name,
      CohortDefinition cohortDefinition,
      String mapping) {
    addRow(
        dataSetDefinition,
        baseName,
        baseLabel,
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(name, EptsReportUtils.map(cohortDefinition, mapping)),
            mapping),
        dissagregations());
  }

  private void addPDFIndicatorsWithDisaggregations(
      CohortIndicatorDataSetDefinition dataSetDefinition) {
    addRow(
        dataSetDefinition,
        "B1PDF",
        "Active to EndDate - Disaggregated",
        "getActiveToEndDate",
        usMonthlySummaryCohortQueries.getActiveToEndDate(),
        "endDate=${startDate},location=${location}");
    addRow(
        dataSetDefinition,
        "B2PDF",
        "Enrolled within Reporting period - Disaggregated",
        "getEnrolledInReportingPeriod",
        usMonthlySummaryCohortQueries.getEnrolledInReportingPeriod(),
        mappings);
    addRow(
        dataSetDefinition,
        "B3PDF",
        "Returned to PDF within Reporting period - Disaggregated",
        "getReturnedToPDFInReportingPeriod",
        usMonthlySummaryCohortQueries.getReturnedToPDFInReportingPeriod(),
        mappings);
    addRow(
        dataSetDefinition,
        "B4PDF",
        "Currently in ART Without pregnancy - Disaggregated",
        "currentlyInARTWithoutPregnancy",
        usMonthlySummaryCohortQueries.currentlyInARTWithoutPregnancy(),
        "endDate=${endDate},location=${location}");
    addRow(
        dataSetDefinition,
        "B5PDF",
        "Exit from PDF Transferred to - Disaggregated",
        "getExitFromPdfTransferredTo",
        usMonthlySummaryCohortQueries.getExitFromPdfTransferredTo(),
        mappings);
    addRow(
        dataSetDefinition,
        "B6PDF",
        "Come out of PDF Removed - Disaggregated",
        "getComeOutOfPdfRemoved",
        usMonthlySummaryCohortQueries.getComeOutOfPdfRemoved(),
        mappings);
    addRow(
        dataSetDefinition,
        "B7PDF",
        "Come out of PDF Obitos - Disaggregated",
        "getComeOutOfPdfObitos",
        usMonthlySummaryCohortQueries.getComeOutOfPdfObitos(),
        mappings);
    addRow(
        dataSetDefinition,
        "B8PDF",
        "Come out of PDF Suspension - Disaggregated",
        "getComeOutOfPdfSuspension",
        usMonthlySummaryCohortQueries.getComeOutOfPdfSuspension(),
        mappings);
    addRow(
        dataSetDefinition,
        "C1PDF",
        "Clinical Consultation Within Reporting epriod - Disaggregated",
        "getClinicalConsultationWithinReportingPeriod",
        usMonthlySummaryCohortQueries.getClinicalConsultationWithinReportingPeriod(),
        mappings);
  }

  private List<ColumnParameters> dissagregations() {
    return Arrays.asList(
        new ColumnParameters("<15Females", "<15 anos - Feminino", "gender=F|age=<15", "F14"),
        new ColumnParameters(">=15Females", "15+ anos Feminino", "gender=F|age=15+", "F15"),
        new ColumnParameters("<15Males", "<15 anos - Masculino", "gender=M|age=<15", "M14"),
        new ColumnParameters(">=15Males", "15+ anos Masculino", "gender=M|age=15+", "M15"));
  }
}
