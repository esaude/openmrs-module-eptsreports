package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.Arrays;
import java.util.List;
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
public class DataQualityOverallDataset extends BaseDataSet {

  private EptsCommonDimension eptsCommonDimension;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private DataQualityOverallCohorts DataQualityOverallCohorts;

  private TxNewCohortQueries txNewCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public DataQualityOverallDataset(
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
    dsd.addParameters(getParameters());

    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    // adding columns
    dsd.addColumn(
        "All",
        "Total patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total Patients",
                EptsReportUtils.map(DataQualityOverallCohorts.getAllPatients(), mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "NPNB1",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Non-pregnant and Non-Breastfeeding Adults (>=15)",
                EptsReportUtils.map(
                    DataQualityOverallCohorts.getNonPreganatAndNonBreastfeedingPatients(),
                    mappings)),
            mappings),
        getAdultColumns());

    addRow(
        dsd,
        "NPNB2",
        "Non-pregnant and Non-Breastfeeding Children",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Non-pregnant and Non-Breastfeeding Children",
                EptsReportUtils.map(
                    DataQualityOverallCohorts.getNonPreganatAndNonBreastfeedingPatients(),
                    mappings)),
            mappings),
        getChildrenColumns());

    dsd.addColumn(
        "Breastfeeding",
        "Breastfeeding (exclude pregnant)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breastfeeding (exclude pregnant)",
                EptsReportUtils.map(
                    txNewCohortQueries.getTxNewBreastfeedingComposition(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        "");

    dsd.addColumn(
        "Pregnant",
        "Pregnant (include breastfeeding)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregnant (include breastfeeding)",
                EptsReportUtils.map(
                    txNewCohortQueries.getPatientsPregnantEnrolledOnART(), mappings)),
            mappings),
        "");

    return dsd;
  }

  private List<ColumnParameters> getChildrenColumns() {
    ColumnParameters twoTo4 = new ColumnParameters("twoTo4", "2 - 4 years", "age=2-4", "01");
    ColumnParameters fiveTo9 = new ColumnParameters("fiveTo9", "5 - 9 years", "age=5-9", "02");
    ColumnParameters tenTo14 = new ColumnParameters("tenTo14", "10 - 14 years", "age=10-14", "03");

    return Arrays.asList(twoTo4, fiveTo9, tenTo14);
  }

  private List<ColumnParameters> getAdultColumns() {
    return Arrays.asList(new ColumnParameters("15Plus", "15+ years", "age=50+", "01"));
  }
}
