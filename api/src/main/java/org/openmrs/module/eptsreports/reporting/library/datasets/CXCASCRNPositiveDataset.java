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
            "TOTAL",
            EptsReportUtils.map(
                cxcascrnCohortQueries.getTotal(CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE),
                mappings));

    dsd.addColumn("TOTAL", "Total", EptsReportUtils.map(total, mappings), "");

    // 1st Time Screened - FTS
    CohortIndicator f1rstTimeScreened =
        eptsGeneralIndicator.getIndicator(
            "FTS",
            EptsReportUtils.map(
                cxcascrnCohortQueries.get1stTimeScreened(CXCASCRNCohortQueries.CXCASCRNResult.ALL),
                mappings));
    dsd.addColumn("FTS", "1st Time Screened", EptsReportUtils.map(f1rstTimeScreened, mappings), "");

    // 1st Time Screened (POSITIVE) - FTSP
    CohortIndicator f1rstTimeScreenedPositive =
        eptsGeneralIndicator.getIndicator(
            "FTSP",
            EptsReportUtils.map(
                cxcascrnCohortQueries.get1stTimeScreened(
                    CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE),
                mappings));
    addRow(
        dsd,
        "FTSP",
        "1st Time Screened POSITIVE",
        EptsReportUtils.map(f1rstTimeScreenedPositive, mappings),
        getColumnsForAge());

    // Rescreened after previous negative - RAPN
    CohortIndicator rescreenedAfterPreviousNegative =
        eptsGeneralIndicator.getIndicator(
            "RAPN",
            EptsReportUtils.map(
                cxcascrnCohortQueries.getRescreenedAfterPreviousNegative(
                    CXCASCRNCohortQueries.CXCASCRNResult.ALL),
                mappings));
    dsd.addColumn(
        "RAPN",
        "Rescreened after previous negative",
        EptsReportUtils.map(rescreenedAfterPreviousNegative, mappings),
        "");

    // Rescreened after previous negative (POSITIVE) - RAPNP
    CohortIndicator rescreenedAfterPreviousNegativePositive =
        eptsGeneralIndicator.getIndicator(
            "RAPNP",
            EptsReportUtils.map(
                cxcascrnCohortQueries.getRescreenedAfterPreviousNegative(
                    CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE),
                mappings));
    addRow(
        dsd,
        "RAPNP",
        "Rescreened after previous negative POSITIVE",
        EptsReportUtils.map(rescreenedAfterPreviousNegativePositive, mappings),
        getColumnsForAge());

    // Post-Treatment follow-up - PTFU
    CohortIndicator postTreatmentFollowUp =
        eptsGeneralIndicator.getIndicator(
            "PTFU",
            EptsReportUtils.map(
                cxcascrnCohortQueries.getPostTreatmentFollowUp(
                    CXCASCRNCohortQueries.CXCASCRNResult.ALL),
                mappings));
    dsd.addColumn(
        "PTFU",
        "Post-Treatment follow-up",
        EptsReportUtils.map(postTreatmentFollowUp, mappings),
        "");

    // Post-Treatment follow-up (POSITIVE) - PTFUP
    CohortIndicator postTreatmentFollowUpPositive =
        eptsGeneralIndicator.getIndicator(
            "PTFUP",
            EptsReportUtils.map(
                cxcascrnCohortQueries.getPostTreatmentFollowUp(
                    CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE),
                mappings));
    addRow(
        dsd,
        "PTFUP",
        "Post-Treatment follow-up POSITIVE",
        EptsReportUtils.map(postTreatmentFollowUpPositive, mappings),
        getColumnsForAge());

    // Rescreened after previous positive - RAPP
    CohortIndicator rescreenedAfterPreviousPositive =
        eptsGeneralIndicator.getIndicator(
            "RAPP",
            EptsReportUtils.map(
                cxcascrnCohortQueries.getRescreenedAfterPreviousPositive(
                    CXCASCRNCohortQueries.CXCASCRNResult.ALL),
                mappings));
    dsd.addColumn(
        "RAPP",
        "Rescreened after previous positive",
        EptsReportUtils.map(rescreenedAfterPreviousPositive, mappings),
        "");

    // Rescreened after previous positive (POSITIVE) - RAPPP
    CohortIndicator rescreenedAfterPreviousPositivePositive =
        eptsGeneralIndicator.getIndicator(
            "RAPPP",
            EptsReportUtils.map(
                cxcascrnCohortQueries.getRescreenedAfterPreviousPositive(
                    CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE),
                mappings));
    addRow(
        dsd,
        "RAPPP",
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
        new ColumnParameters("fifteenTo19", "15 - 19 female", "age=15-19", "01");
    ColumnParameters twentyTo24 =
        new ColumnParameters("twentyTo24", "20 - 24 female", "age=20-24", "02");
    ColumnParameters twenty5To29 =
        new ColumnParameters("twenty4To29", "25 - 29 female", "age=25-29", "03");
    ColumnParameters thirtyTo34 =
        new ColumnParameters("thirtyTo34", "30 - 34 female", "age=30-34", "04");
    ColumnParameters thirty5To39 =
        new ColumnParameters("thirty5To39", "35 - 39 female", "age=35-39", "05");
    ColumnParameters foutyTo44 =
        new ColumnParameters("foutyTo44", "40 - 44 female", "age=40-44", "06");
    ColumnParameters fouty5To49 =
        new ColumnParameters("fouty5To49", "45 - 49 female", "age=45-49", "07");
    ColumnParameters above50 = new ColumnParameters("above50", "50+ female", "age=50+", "08");
    ColumnParameters unknown =
        new ColumnParameters("unknown", "Unknown age female", "age=UK", "09");
    ColumnParameters total = new ColumnParameters("totals", "Totals", "", "10");

    return Arrays.asList(
        fifteenTo19,
        twentyTo24,
        twenty5To29,
        thirtyTo34,
        thirty5To39,
        foutyTo44,
        fouty5To49,
        above50,
        unknown,
        total);
  }
}
