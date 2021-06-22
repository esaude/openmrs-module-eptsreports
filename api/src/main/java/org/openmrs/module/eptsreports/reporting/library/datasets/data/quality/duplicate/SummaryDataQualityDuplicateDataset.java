package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.duplicate;

import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryDataQualityDuplicateDataset extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EC1PatientListDuplicateDataset eC1PatientListDuplicateDataset;

  @Autowired private EC2PatientListDuplicateDataset eC2PatientListDuplicateDataset;

  public DataSetDefinition constructSummaryDataQualityDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("Data Quality Duplicated Summary Dataset");
    final String mappings = "";

    final CohortDefinition summaryCohortQueryEC1 = eC1PatientListDuplicateDataset.getEC1Total();

    final CohortDefinition summaryCohortQueryEC2 = eC2PatientListDuplicateDataset.getEC1Total();

    dsd.addColumn(
        "EC1D-TOTAL",
        "EC1D: patients using same NID",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC1Indicator",
                EptsReportUtils.map(summaryCohortQueryEC1, mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC2D-TOTAL",
        "EC2D: patients with Duplicated NID",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC2Indicator",
                EptsReportUtils.map(summaryCohortQueryEC2, mappings)),
            mappings),
        "");

    return dsd;
  }
}
