package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.TPTCompletionCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TPTCompletionDataSet extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TPTCompletionCohortQueries tPTCompletionCohortQueries;

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructTPTCompletionDataSet() {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TPT Completion Cascade Report");
    dataSetDefinition.addParameters(getParameters());
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    CohortDefinition txCurrCompositionCohort =
        txCurrCohortQueries.getTxCurrCompositionCohort("txCurrCompositionTPT", true);

    CohortIndicator txCurrIndicator =
        eptsGeneralIndicator.getIndicator(
            "patientInYearRangeEnrolledInHIVStartedARTIndicator",
            EptsReportUtils.map(
                txCurrCompositionCohort, "onOrBefore=${endDate},location=${location}"));

    CohortIndicator txCurrWithTPTCompIndicator =
        eptsGeneralIndicator.getIndicator(
            "TX_CURRwithTPTCompletion",
            EptsReportUtils.map(tPTCompletionCohortQueries.getTxCurrWithTPTCompletion(), mappings));

    CohortIndicator txCurrWithoutTPTCompIndicator =
        eptsGeneralIndicator.getIndicator(
            "TX_CURRwithoutTPTCompletion",
            EptsReportUtils.map(
                tPTCompletionCohortQueries.getTxCurrWithoutTPTCompletion(), mappings));

    CohortIndicator txCurrWithoutTPTCompAndTxTBIndicator =
        eptsGeneralIndicator.getIndicator(
            "TX_CURRwithoutTPTCompletionWithTB",
            EptsReportUtils.map(
                tPTCompletionCohortQueries.getTxCurrWithoutTPTCompletionWithTB(), mappings));

    CohortIndicator txCurrWithoutTPTCompAndTBPosScreeningIndicator =
        eptsGeneralIndicator.getIndicator(
            "TX_CURRwithoutTPTCompletionWithTBPositiveScreening",
            EptsReportUtils.map(
                tPTCompletionCohortQueries.getTxCurrWithoutTPTCompletionWithPositiveTBScreening(),
                mappings));

    CohortIndicator txCurrEligibleForTPTCompletion =
        eptsGeneralIndicator.getIndicator(
            "TxCurrEligibleForTPTCompletion",
            EptsReportUtils.map(
                tPTCompletionCohortQueries.getTxCurrEligibleForTPTCompletion(), mappings));

    CohortIndicator txCurrWithTPTinLast7Months =
        eptsGeneralIndicator.getIndicator(
            "TxCurrWithTPTInLast7Months",
            EptsReportUtils.map(
                tPTCompletionCohortQueries.getTxCurrWithTPTInLast7Months(), mappings));

    CohortIndicator txCurrEligibleForTPTInitiation =
        eptsGeneralIndicator.getIndicator(
            "TxCurrEligibleForTPTInitiation",
            EptsReportUtils.map(
                tPTCompletionCohortQueries.getTxCurrEligibleForTPTInitiation(), mappings));

    dataSetDefinition.addColumn(
        "TXCURR", "TX_CURR: Currently on ART", EptsReportUtils.map(txCurrIndicator, mappings), "");

    dataSetDefinition.addColumn(
        "TPT0",
        "TX_CURR with TPT Completion",
        EptsReportUtils.map(txCurrWithTPTCompIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "TPT1",
        "TX_CURR without TPT Completion",
        EptsReportUtils.map(txCurrWithoutTPTCompIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "TPT2",
        "TX_CURR without TPT Completion and TxTB",
        EptsReportUtils.map(txCurrWithoutTPTCompAndTxTBIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "TPT3",
        "TX_CURR without TPT Completion and TB Positive Screening",
        EptsReportUtils.map(txCurrWithoutTPTCompAndTBPosScreeningIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "TPT4",
        "TX_CURR eligible for TPT Completion",
        EptsReportUtils.map(txCurrEligibleForTPTCompletion, mappings),
        "");

    dataSetDefinition.addColumn(
        "TPT5",
        "TX_CURR With TPT Completion in Last 7 Months",
        EptsReportUtils.map(txCurrWithTPTinLast7Months, mappings),
        "");

    dataSetDefinition.addColumn(
        "TPT6",
        "TX_CURR eligible for TPT Initiation",
        EptsReportUtils.map(txCurrEligibleForTPTInitiation, mappings),
        "");

    return dataSetDefinition;
  }
}
