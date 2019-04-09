package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.UsMonthlySummaryCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
    addAPDIndicators(mappings, dataSetDefinition);
    addPDFIndicators(mappings, dataSetDefinition);

    return dataSetDefinition;
  }

  private void addAPDIndicators(
      String mappings, CohortIndicatorDataSetDefinition dataSetDefinition) {
    dataSetDefinition.addColumn(
        "APDF1",
        "PDF Format assessment By End Date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getPdfFormatAssetAtFinalDate",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.getPdfFormatAssetAtFinalDate(), mappings)),
            mappings),
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
            mappings),
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

  private void addPDFIndicators(
      String mappings, CohortIndicatorDataSetDefinition dataSetDefinition) {
    Mapped<CohortIndicator> activePatientsToEndDate =
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getActivePatientsToEndDate",
                EptsReportUtils.map(usMonthlySummaryCohortQueries.getActiveToEndDate(), mappings)),
            mappings);
    addRow(
        dataSetDefinition,
        "B1PDF",
        "Active to EndDate - Disaggregated",
        activePatientsToEndDate,
        dissagregations());
    Mapped<CohortIndicator> enrolledInReportingPeriod =
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getEnrolledInReportingPeriod",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.getEnrolledInReportingPeriod(), mappings)),
            mappings);
    addRow(
        dataSetDefinition,
        "B2PDF",
        "Enrolled within Reporting period - Disaggregated",
        enrolledInReportingPeriod,
        dissagregations());
    Mapped<CohortIndicator> returnedToPDFInReportingPeriod =
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getReturnedToPDFInReportingPeriod",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.getReturnedToPDFInReportingPeriod(), mappings)),
            mappings);
    addRow(
        dataSetDefinition,
        "B3PDF",
        "Returned to PDF within Reporting period - Disaggregated",
        returnedToPDFInReportingPeriod,
        dissagregations());
    Mapped<CohortIndicator> inARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDate =
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getInARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDate",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries
                        .getInARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDate(),
                    mappings)),
            mappings);
    addRow(
        dataSetDefinition,
        "B4PDF",
        "In ART Excluding Not Answered Abandoned In 4 Weeks - Disaggregated",
        inARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDate,
        dissagregations());
    Mapped<CohortIndicator> currentlyInARTWithoutPregnancy =
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "currentlyInARTWithoutPregnancy",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.currentlyInARTWithoutPregnancy(), mappings)),
            mappings);
    addRow(
        dataSetDefinition,
        "B4PDF",
        "Currently in ART Without pregnancy - Disaggregated",
        currentlyInARTWithoutPregnancy,
        dissagregations());
    Mapped<CohortIndicator> exitFromPdfTransferredTo =
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getExitFromPdfTransferredTo",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.getExitFromPdfTransferredTo(), mappings)),
            mappings);
    addRow(
        dataSetDefinition,
        "B5PDF",
        "Exit from PDF Transferred to - Disaggregated",
        exitFromPdfTransferredTo,
        dissagregations());
    Mapped<CohortIndicator> comeOutOfPdfRemoved =
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getComeOutOfPdfRemoved",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.getComeOutOfPdfRemoved(), mappings)),
            mappings);
    addRow(
        dataSetDefinition,
        "B6PDF",
        "Come out of PDF Removed - Disaggregated",
        comeOutOfPdfRemoved,
        dissagregations());
    Mapped<CohortIndicator> comeOutOfPdfObitos =
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getComeOutOfPdfObitos",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.getComeOutOfPdfObitos(), mappings)),
            mappings);
    addRow(
        dataSetDefinition,
        "B7PDF",
        "Come out of PDF Obitos - Disaggregated",
        comeOutOfPdfObitos,
        dissagregations());
    Mapped<CohortIndicator> comeOutOfPdfSuspension =
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getComeOutOfPdfSuspension",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.getComeOutOfPdfSuspension(), mappings)),
            mappings);
    addRow(
        dataSetDefinition,
        "B8PDF",
        "Come out of PDF Suspension - Disaggregated",
        comeOutOfPdfSuspension,
        dissagregations());
    Mapped<CohortIndicator> clinicalConsultationWithinReportingPeriod =
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getClinicalConsultationWithinReportingPeriod",
                EptsReportUtils.map(
                    usMonthlySummaryCohortQueries.getClinicalConsultationWithinReportingPeriod(),
                    mappings)),
            mappings);
    addRow(
        dataSetDefinition,
        "C1PDF",
        "Clinical Consultation Within Reporting epriod - Disaggregated",
        clinicalConsultationWithinReportingPeriod,
        dissagregations());
  }

  private List<ColumnParameters> dissagregations() {
    return Arrays.asList(
        new ColumnParameters("<15Females", "<15 anos - Feminino", "gender=F|age=<15", "F14"),
        new ColumnParameters(">=15Females", "15+ anos Feminino", "gender=F|age=15+", "F15"),
        new ColumnParameters("<15Males", "<15 anos - Masculino", "gender=M|age=<15", "M14"),
        new ColumnParameters(">=15Males", "15+ anos Masculino", "gender=M|age=15+", "M15"));
  }
}
