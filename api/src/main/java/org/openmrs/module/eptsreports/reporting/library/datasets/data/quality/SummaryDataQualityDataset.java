package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.DataQualityOverallCohorts;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SummaryDataQualityDataset extends BaseDataSet {

  private EptsCommonDimension eptsCommonDimension;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private DataQualityOverallCohorts DataQualityOverallCohorts;

  private TxNewCohortQueries txNewCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public SummaryDataQualityDataset(
      EptsCommonDimension eptsCommonDimension,
      EptsGeneralIndicator eptsGeneralIndicator,
      DataQualityOverallCohorts DataQualityOverallCohorts,
      TxNewCohortQueries txNewCohortQueries) {
    this.eptsCommonDimension = eptsCommonDimension;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.DataQualityOverallCohorts = DataQualityOverallCohorts;
    this.txNewCohortQueries = txNewCohortQueries;
  }

  public DataSetDefinition constructOveralDataQualityDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("Data Quality Overall Dataset");
    dsd.addParameters(getDataQualityParameters());

    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    dsd.addColumn(
        "EC1",
        "The patient’s sex is male and the patient is pregnant",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient’s sex is male and the patient is pregnant",
                EptsReportUtils.map(
                    txNewCohortQueries.getPatientsPregnantEnrolledOnART(), mappings)),
            mappings),
        "gender=M");
    dsd.addColumn(
        "EC2",
        "The patient’s sex is male and the patient is breastfeeding",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient’s sex is male and the patient is breastfeeding",
                EptsReportUtils.map(
                    txNewCohortQueries.getTxNewBreastfeedingComposition(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        "gender=M");

    return dsd;
  }
}
