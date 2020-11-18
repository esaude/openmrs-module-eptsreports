package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRttCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxRTTPLHIVDateset extends BaseDataSet {

  private TxRttCohortQueries txRttCohortQueries;

  private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  public TxRTTPLHIVDateset(
      TxRttCohortQueries txRttCohortQueries, EptsGeneralIndicator eptsGeneralIndicator) {
    this.txRttCohortQueries = txRttCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
  }

  public DataSetDefinition constructTxRTTPLHIVDateset() {

    CohortIndicatorDataSetDefinition cohortIndicatorDataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    cohortIndicatorDataSetDefinition.addColumn(
        "RTT",
        "Total of RTT",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "All patient in RTT",
                EptsReportUtils.map(this.txRttCohortQueries.getRTTComposition(), "")),
            ""),
        "");

    // addRow();
    return cohortIndicatorDataSetDefinition;
  }
}
