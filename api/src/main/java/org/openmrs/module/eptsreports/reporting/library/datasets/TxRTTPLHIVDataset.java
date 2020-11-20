package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRttCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.TxRTTDimenstion;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxRTTPLHIVDataset extends BaseDataSet {

  private TxRttCohortQueries txRttCohortQueries;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private TxRTTDimenstion txRTTDimenstion;

  @Autowired
  public TxRTTPLHIVDataset(
      TxRttCohortQueries txRttCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator,
      TxRTTDimenstion txRTTDimenstion) {
    this.txRttCohortQueries = txRttCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.txRTTDimenstion = txRTTDimenstion;
  }

  public DataSetDefinition constructTxRTTPLHIVDateset() {

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    CohortIndicatorDataSetDefinition cohortIndicatorDataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    cohortIndicatorDataSetDefinition.setName(
        "Patients who returned to treatment and remained on ARV,");
    cohortIndicatorDataSetDefinition.setParameters(getParameters());

    cohortIndicatorDataSetDefinition.addDimension(
        "days", EptsReportUtils.map(txRTTDimenstion.getAnBFromRTT(), mappings));

    CohortIndicator plhiv =
        eptsGeneralIndicator.getIndicator(
            "PLHIV", EptsReportUtils.map(txRttCohortQueries.getRTTComposition(), mappings));

    Mapped<CohortIndicator> mappedPLHIVIndicator = EptsReportUtils.map(plhiv, mappings);

    addRow(
        cohortIndicatorDataSetDefinition,
        "PLHIV",
        "Patients who returned to treatment and remained on ARV",
        mappedPLHIVIndicator,
        getDiffInDaysFromAnBDisagragation());

    return cohortIndicatorDataSetDefinition;
  }

  private List<ColumnParameters> getDiffInDaysFromAnBDisagragation() {
    ColumnParameters lessThan365 =
        new ColumnParameters("less365", "Less than 365", "days=<365", "01");
    ColumnParameters moreThan365 =
        new ColumnParameters("more365", "more than 365", "days=365+", "02");
    ColumnParameters total = new ColumnParameters("total", "Total PLHIV", "", "03");

    return Arrays.asList(lessThan365, moreThan365, total);
  }
}
