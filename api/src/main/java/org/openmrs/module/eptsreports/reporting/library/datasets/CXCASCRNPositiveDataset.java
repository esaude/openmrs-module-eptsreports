package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CXCASCRNCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CXCASCRNPositiveDataset extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private CXCASCRNCohortQueries cxcascrnCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructCXCASCRNPositiveDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("CXCASCRN Positive Dataset");
    dsd.addParameters(getParameters());

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    // Total
    CohortIndicator total =
        eptsGeneralIndicator.getIndicator(
            "TOTALPP",
            EptsReportUtils.map(
                cxcascrnCohortQueries.getTotal(CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE),
                mappings));

    dsd.addColumn("TOTALPP", "Total Positive", EptsReportUtils.map(total, mappings), "");

    // 1st Time Screened (POSITIVE) - FTSP
    CohortIndicator f1rstTimeScreenedPositive =
        eptsGeneralIndicator.getIndicator(
            "FTSPPP",
            EptsReportUtils.map(
                cxcascrnCohortQueries.get1stTimeScreened(
                    CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE),
                mappings));

    addRow(
        dsd,
        "FTSPPP",
        "1st Time Screened POSITIVE",
        EptsReportUtils.map(f1rstTimeScreenedPositive, mappings),
        getColumnsForAge());

    // Rescreened after previous negative (POSITIVE) - RAPNP
    CohortIndicator rescreenedAfterPreviousNegativePositive =
        eptsGeneralIndicator.getIndicator(
            "RAPNPPP",
            EptsReportUtils.map(
                cxcascrnCohortQueries.getRescreenedAfterPreviousNegative(
                    CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE),
                mappings));
    addRow(
        dsd,
        "RAPNPPP",
        "Rescreened after previous negative POSITIVE",
        EptsReportUtils.map(rescreenedAfterPreviousNegativePositive, mappings),
        getColumnsForAge());

    // Post-Treatment follow-up (POSITIVE) - PTFUP
    CohortIndicator postTreatmentFollowUpPositive =
        eptsGeneralIndicator.getIndicator(
            "PTFUPPP",
            EptsReportUtils.map(
                cxcascrnCohortQueries.getPostTreatmentFollowUp(
                    CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE),
                mappings));
    addRow(
        dsd,
        "PTFUPPP",
        "Post-Treatment follow-up POSITIVE",
        EptsReportUtils.map(postTreatmentFollowUpPositive, mappings),
        getColumnsForAge());

    // Rescreened after previous positive (POSITIVE) - RAPPP
    CohortIndicator rescreenedAfterPreviousPositivePositive =
        eptsGeneralIndicator.getIndicator(
            "RAPPPPP",
            EptsReportUtils.map(
                cxcascrnCohortQueries.getRescreenedAfterPreviousPositive(
                    CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE),
                mappings));
    addRow(
        dsd,
        "RAPPPPP",
        "Rescreened after previous positive POSITIVE",
        EptsReportUtils.map(rescreenedAfterPreviousPositivePositive, mappings),
        getColumnsForAge());

    return dsd;
  }

  /**
   * <b>Description:</b> Creates Desagregation based on Age and Gender
   *
   * @return
   */
  private List<ColumnParameters> getColumnsForAge() {

    ColumnParameters fifteenTo19 =
        new ColumnParameters("fifteenTo19", "15 - 19", "age=15-19", "01");
    ColumnParameters twentyTo24 = new ColumnParameters("twentyTo24", "20 - 24", "age=20-24", "02");
    ColumnParameters twenty5To29 =
        new ColumnParameters("twenty4To29", "25 - 29", "age=25-29", "03");
    ColumnParameters thirtyTo34 = new ColumnParameters("thirtyTo34", "30 - 34", "age=30-34", "04");
    ColumnParameters thirty5To39 =
        new ColumnParameters("thirty5To39", "35 - 39", "age=35-39", "05");
    ColumnParameters foutyTo44 = new ColumnParameters("foutyTo44", "40 - 44", "age=40-44", "06");
    ColumnParameters fouty5To49 = new ColumnParameters("fouty5To49", "45 - 49", "age=45-49", "07");
    ColumnParameters fiftyTo54 = new ColumnParameters("fiftyTo54", "50 - 54", "age=50-54", "08");
    ColumnParameters fifty5To59 = new ColumnParameters("fifty5To59", "55 - 59", "age=55-59", "09");
    ColumnParameters sixtyTo64 = new ColumnParameters("sixtyTo64", "60 - 64", "age=60-64", "10");
    ColumnParameters above65 = new ColumnParameters("above65", "65+", "age=65+", "11");
    ColumnParameters unknown = new ColumnParameters("unknown", "Unknown age", "age=UK", "12");
    ColumnParameters total = new ColumnParameters("totals", "Totals", "", "13");

    return Arrays.asList(
        fifteenTo19,
        twentyTo24,
        twenty5To29,
        thirtyTo34,
        thirty5To39,
        foutyTo44,
        fouty5To49,
        fiftyTo54,
        fifty5To59,
        sixtyTo64,
        above65,
        unknown,
        total);
  }
}
