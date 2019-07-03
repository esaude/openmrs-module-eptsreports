package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryDataQualityDataset extends BaseDataSet {

  private EptsGeneralIndicator eptsGeneralIndicator;

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  @Autowired
  public SummaryDataQualityDataset(
      EptsGeneralIndicator eptsGeneralIndicator,
      SummaryDataQualityCohorts summaryDataQualityCohorts) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
  }

  public DataSetDefinition constructSummaryDataQualityDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings =
        "startDate=${startDate},endDate=${endDate},location=${location},state=${state}";
    dsd.setName("Data Quality Summary Dataset");
    dsd.addParameters(getDataQualityParameters());

    dsd.addColumn(
        "EC1",
        "Total Male Patients who are pregnant",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicatorsForDataQuality(
                "Total Male Patients who are pregnant",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPregnantMalePatients(), "location=${location}")),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC2",
        "Total Male Patients who are breastfeeding",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicatorsForDataQuality(
                "Total Male Patients who are breastfeeding",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getBreastfeedingMalePatients(),
                    "location=${location}")),
            "location=${location}"),
        "");

    return dsd;
  }
}
