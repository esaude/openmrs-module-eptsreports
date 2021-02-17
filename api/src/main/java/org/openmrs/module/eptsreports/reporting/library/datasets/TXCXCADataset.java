package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.calculation.cxcascrn.TreatmentType;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXCXCACohortQueries;
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
public class TXCXCADataset extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TXCXCACohortQueries cxcatxCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructTXCXCASCRNDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("CXCASCRN Dataset");
    dsd.addParameters(getParameters());

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    /** Total */
    CohortIndicator total =
        eptsGeneralIndicator.getIndicator(
            "TOTAL", EptsReportUtils.map(cxcatxCohortQueries.getTotal(), mappings));

    dsd.addColumn("TOTAL", "Total", EptsReportUtils.map(total, mappings), "");

    /** 1st Time Screened - FTS */
    CohortIndicator f1rstTimeScreened =
        eptsGeneralIndicator.getIndicator(
            "FTS", EptsReportUtils.map(cxcatxCohortQueries.getf1srtTimeScreened(), mappings));
    dsd.addColumn("FTS", "1st Time Screened", EptsReportUtils.map(f1rstTimeScreened, mappings), "");

    // 1st Time Screened (B5) - FTSNB5
    CohortIndicator f1rstTimeScreenedB5 =
        eptsGeneralIndicator.getIndicator(
            "FTSNB5",
            EptsReportUtils.map(
                this.cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getf1srtTimeScreened(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B5),
                    "TX 1st Time Screened B5  composition"),
                mappings));
    addRow(
        dsd,
        "FTSNB5",
        "1st Time Screened B5",
        EptsReportUtils.map(f1rstTimeScreenedB5, mappings),
        getColumnsForAge());

    // 1st Time Screened (B6) - FTSB6
    CohortIndicator f1rstTimeScreenedB6 =
        eptsGeneralIndicator.getIndicator(
            "FTSB6",
            EptsReportUtils.map(
                this.cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getf1srtTimeScreened(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B6),
                    "TX 1st Time Screened B6  composition"),
                mappings));
    addRow(
        dsd,
        "FTSB6",
        "1st Time Screened B6",
        EptsReportUtils.map(f1rstTimeScreenedB6, mappings),
        getColumnsForAge());

    // 1st Time Screened (B7) - FTSB7
    CohortIndicator f1rstTimeScreenedB7 =
        eptsGeneralIndicator.getIndicator(
            "FTSB7",
            EptsReportUtils.map(
                this.cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getf1srtTimeScreened(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B7),
                    "TX 1st Time Screened B7  composition"),
                mappings));
    addRow(
        dsd,
        "FTSB7",
        "1st Time Screened B7",
        EptsReportUtils.map(f1rstTimeScreenedB7, mappings),
        getColumnsForAge());

    /** Rescreened after previous negative - RAPN */
    CohortIndicator rescreenedAfterPreviousNegative =
        eptsGeneralIndicator.getIndicator(
            "RAPN",
            EptsReportUtils.map(
                cxcatxCohortQueries.getRescreenedAfterPreviousNegative(), mappings));
    dsd.addColumn(
        "RAPN",
        "Rescreened after previous negative",
        EptsReportUtils.map(rescreenedAfterPreviousNegative, mappings),
        "");

    // Rescreened after previous negative (B5) - RAPNB5
    CohortIndicator rescreenedAfterPreviousNegativeB5 =
        eptsGeneralIndicator.getIndicator(
            "RAPNB5",
            EptsReportUtils.map(
                this.cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getRescreenedAfterPreviousNegative(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B5),
                    "TX Rescreened after previous negative B5 composition"),
                mappings));
    addRow(
        dsd,
        "RAPNB5",
        "Rescreened after previous negative B5",
        EptsReportUtils.map(rescreenedAfterPreviousNegativeB5, mappings),
        getColumnsForAge());

    // Rescreened after previous negative (B6) - RAPNB6
    CohortIndicator rescreenedAfterPreviousNegativeB6 =
        eptsGeneralIndicator.getIndicator(
            "RAPNB6",
            EptsReportUtils.map(
                this.cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getRescreenedAfterPreviousNegative(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B6),
                    "TX Rescreened after previous negative B6 composition"),
                mappings));
    addRow(
        dsd,
        "RAPNB6",
        "Rescreened after previous negative B6",
        EptsReportUtils.map(rescreenedAfterPreviousNegativeB6, mappings),
        getColumnsForAge());

    // Rescreened after previous negative (B7) - RAPNB7
    CohortIndicator rescreenedAfterPreviousNegativeB7 =
        eptsGeneralIndicator.getIndicator(
            "RAPNB7",
            EptsReportUtils.map(
                this.cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getRescreenedAfterPreviousNegative(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B7),
                    "TX Rescreened after previous negative B7 composition"),
                mappings));
    addRow(
        dsd,
        "RAPNB7",
        "Rescreened after previous negative B7",
        EptsReportUtils.map(rescreenedAfterPreviousNegativeB7, mappings),
        getColumnsForAge());

    /** Post-Treatment follow-up - PTFU */
    CohortIndicator postTreatmentFollowUp =
        eptsGeneralIndicator.getIndicator(
            "PTFU", EptsReportUtils.map(cxcatxCohortQueries.getPostTreatmentFollowUp(), mappings));
    dsd.addColumn(
        "PTFU",
        "Post-Treatment follow-up",
        EptsReportUtils.map(postTreatmentFollowUp, mappings),
        "");

    // Post-Treatment follow-up (B5) - PTFUB5
    CohortIndicator postTreatmentFollowUpB5 =
        eptsGeneralIndicator.getIndicator(
            "PTFUB5",
            EptsReportUtils.map(
                this.cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getPostTreatmentFollowUp(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B5),
                    "TX Post-Treatment follow-up B5 composition"),
                mappings));
    addRow(
        dsd,
        "PTFUB5",
        "Post-Treatment follow-up B5",
        EptsReportUtils.map(postTreatmentFollowUpB5, mappings),
        getColumnsForAge());

    // Post-Treatment follow-up (B6) - PTFUB6
    CohortIndicator postTreatmentFollowUpB6 =
        eptsGeneralIndicator.getIndicator(
            "PTFUB6",
            EptsReportUtils.map(
                this.cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getPostTreatmentFollowUp(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B6),
                    "TX Post-Treatment follow-up B6 composition"),
                mappings));
    addRow(
        dsd,
        "PTFUB6",
        "Post-Treatment follow-up B6",
        EptsReportUtils.map(postTreatmentFollowUpB6, mappings),
        getColumnsForAge());

    // Post-Treatment follow-up (B7) - PTFUB7
    CohortIndicator postTreatmentFollowUpB7 =
        eptsGeneralIndicator.getIndicator(
            "PTFUB7",
            EptsReportUtils.map(
                this.cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getPostTreatmentFollowUp(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B7),
                    "TX Post-Treatment follow-up B7 composition"),
                mappings));
    addRow(
        dsd,
        "PTFUB7",
        "Post-Treatment follow-up B7",
        EptsReportUtils.map(postTreatmentFollowUpB7, mappings),
        getColumnsForAge());

    /** Rescreened after previous positive - RAPP */
    CohortIndicator rescreenedAfterPreviousPositive =
        eptsGeneralIndicator.getIndicator(
            "RAPP",
            EptsReportUtils.map(
                cxcatxCohortQueries.getRescreenedAfterPreviousPositive(), mappings));
    dsd.addColumn(
        "RAPP",
        "Rescreened after previous positive",
        EptsReportUtils.map(rescreenedAfterPreviousPositive, mappings),
        "");

    // Rescreened after previous positive (B5) - RAPPB5
    CohortIndicator rescreenedAfterPreviousPositiveB5 =
        eptsGeneralIndicator.getIndicator(
            "RAPPB5",
            EptsReportUtils.map(
                cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getRescreenedAfterPreviousPositive(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B5),
                    "TX Rescreened after previous positive B6"),
                mappings));
    addRow(
        dsd,
        "RAPPB5",
        "Rescreened after previous positive B5",
        EptsReportUtils.map(rescreenedAfterPreviousPositiveB5, mappings),
        getColumnsForAge());

    // Rescreened after previous positive (B6) - RAPPB6
    CohortIndicator rescreenedAfterPreviousPositiveB6 =
        eptsGeneralIndicator.getIndicator(
            "RAPPB6",
            EptsReportUtils.map(
                cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getRescreenedAfterPreviousPositive(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B6),
                    "TX Rescreened after previous positive B6"),
                mappings));
    addRow(
        dsd,
        "RAPPB6",
        "Rescreened after previous positive B6",
        EptsReportUtils.map(rescreenedAfterPreviousPositiveB6, mappings),
        getColumnsForAge());

    // Rescreened after previous positive (B7) - RAPPB7
    CohortIndicator rescreenedAfterPreviousPositiveB7 =
        eptsGeneralIndicator.getIndicator(
            "RAPPB7",
            EptsReportUtils.map(
                cxcatxCohortQueries.getFinalComposition(
                    this.cxcatxCohortQueries.getRescreenedAfterPreviousPositive(),
                    this.cxcatxCohortQueries.getB5OrB6OrB7(TreatmentType.B7),
                    "TX Rescreened after previous positive B7"),
                mappings));
    addRow(
        dsd,
        "RAPPB7",
        "Rescreened after previous positive B7",
        EptsReportUtils.map(rescreenedAfterPreviousPositiveB7, mappings),
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
