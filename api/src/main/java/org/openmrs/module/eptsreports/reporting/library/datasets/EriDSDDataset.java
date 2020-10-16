package org.openmrs.module.eptsreports.reporting.library.datasets;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.EriDSDCohortQueries;
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
public class EriDSDDataset extends BaseDataSet {

  private static final String N1 =
      "N1 - Number of active on ART whose next ART pick-up is schedule for 83-97 days after the date of their last ART drug pick-up (DT)";

  private static final String N2 =
      "N2 - Number of active patients on ART whose next clinical consultation is scheduled 175-190 days after the date of the last clinical consultation";

  private static final String N3 =
      "N3 - Number of active patients on ART that are participating in GAAC at the end of the month prior to month of results submission deadline.";

  private static final String N4 =
      "N4 - Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in AF";

  private static final String N5 =
      "N5  - Number of all patients marked in last “Clubes de Adesão (CA)”";

  private static final String N7 =
      "N7 Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in DC";

  private static final String N8 =
      "N8 - Number of active patients on ART who participate in at least one DSD";

  private static final String N9 = "N9 : Number of active patients on ART who are on DS";

  @Autowired private EriDSDCohortQueries eriDSDCohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructEriDSDDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.setName("DSD Data Set");
    dsd.addParameters(getParameters());
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    dsd.addDimension("eligible", mapStraightThrough(eptsCommonDimension.getDSDEligibleDimension()));
    dsd.addDimension(
        "pregnantBreastfeedingTb",
        mapStraightThrough(
            eptsCommonDimension.getDSDNonPregnantNonBreastfeedingAndNotOnTbDimension()));

    dsd.setName("total");
    dsd.addColumn(
        "D1T",
        "DSD D1 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D1 Total", EptsReportUtils.map(eriDSDCohortQueries.getD1(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D1SNPNB",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D1SNPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD1(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "D1SNPNBC",
        "Non-pregnant and Non-Breastfeeding Children By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D1SNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD1(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "D2T",
        "DSD D2 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D2 Total", EptsReportUtils.map(eriDSDCohortQueries.getD2(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D2NPNB",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2NPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD2(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "D2NPNBC",
        "Non-pregnant and Non-Breastfeeding Children  By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2NPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD2(),
                    mappings)),
            mappings),
        getChildrenColumn());

    addRow(dsd, "N1", N1, mapStraightThrough(getN1()), getDisags());
    addRow(dsd, "N2", N2, mapStraightThrough(getN2()), getDisags());
    addRow(dsd, "N3", N3, mapStraightThrough(getN3()), getDisags());
    addRow(dsd, "N4", N4, mapStraightThrough(getN4()), getDisags());
    addRow(dsd, "N5", N5, mapStraightThrough(getN5()), getDisags());
    addRow(dsd, "N7", N7, mapStraightThrough(getN7()), getDisags());
    addRow(dsd, "N8", N8, mapStraightThrough(getN8()), getDisags());
    addRow(dsd, "N9", N9, mapStraightThrough(getN9()), getDisags());

    return dsd;
  }

  /**
   * 2-14 years old children List
   *
   * @return java.util.List
   */
  private List<ColumnParameters> getChildrenColumn() {
    ColumnParameters twoTo4 = new ColumnParameters("twoTo4", "2-4", "age=2-4", "01");
    ColumnParameters fiveTo9 = new ColumnParameters("fiveTo9", "5-9", "age=5-9", "02");
    ColumnParameters tenTo14 = new ColumnParameters("tenTo14", "10-14", "age=10-14", "03");
    ColumnParameters lesThan2 = new ColumnParameters("lesThan2", "<2", "age=<2", "04");

    return Arrays.asList(lesThan2, twoTo4, fiveTo9, tenTo14);
  }

  private CohortIndicator getN1() {
    return eptsGeneralIndicator.getIndicator("N1", mapStraightThrough(eriDSDCohortQueries.getN1()));
  }

  private CohortIndicator getN2() {
    return eptsGeneralIndicator.getIndicator("N2", mapStraightThrough(eriDSDCohortQueries.getN2()));
  }

  private CohortIndicator getN3() {
    return eptsGeneralIndicator.getIndicator("N3", mapStraightThrough(eriDSDCohortQueries.getN3()));
  }

  private CohortIndicator getN4() {
    return eptsGeneralIndicator.getIndicator("N4", mapStraightThrough(eriDSDCohortQueries.getN4()));
  }

  private CohortIndicator getN5() {
    return eptsGeneralIndicator.getIndicator("N5", mapStraightThrough(eriDSDCohortQueries.getN5()));
  }

  private CohortIndicator getN7() {
    return eptsGeneralIndicator.getIndicator("N7", mapStraightThrough(eriDSDCohortQueries.getN7()));
  }

  private CohortIndicator getN8() {
    return eptsGeneralIndicator.getIndicator("N8", mapStraightThrough(eriDSDCohortQueries.getN8()));
  }

  private CohortIndicator getN9() {
    return eptsGeneralIndicator.getIndicator("N9", mapStraightThrough(eriDSDCohortQueries.getN9()));
  }

  private List<ColumnParameters> getDisags() {
    return Arrays.asList(
        new ColumnParameters("Total", "Total", "pregnantBreastfeedingTb=NPNBNTB", "01"),
        new ColumnParameters("Eligible Sub-Total", "Eligible Sub-Total", "eligible=E", "02"),
        new ColumnParameters(
            "Eligible Adults",
            "Eligible Adults",
            "eligible=E|pregnantBreastfeedingTb=NPNBNTB|age=15+",
            "03"),
        new ColumnParameters(
            "Eligible 2-4",
            "Eligible 2-4",
            "eligible=E|pregnantBreastfeedingTb=NPNBNTB|age=2-4",
            "04"),
        new ColumnParameters(
            "Eligible 5-9",
            "Eligible 5-9",
            "eligible=E|pregnantBreastfeedingTb=NPNBNTB|age=5-9",
            "05"),
        new ColumnParameters(
            "Eligible 10-14",
            "Eligible 10-14",
            "eligible=E|pregnantBreastfeedingTb=NPNBNTB|age=10-14",
            "06"),
        new ColumnParameters(
            "Not Eligible Sub-Total", "Not Eligible Sub-Total", "eligible=NE", "07"),
        new ColumnParameters(
            "Not Eligible Adults",
            "Not Eligible Adults",
            "eligible=NE|pregnantBreastfeedingTb=NPNBNTB|age=15+",
            "08"),
        new ColumnParameters(
            "Not Eligible <2",
            "Not Eligible <2",
            "eligible=NE|pregnantBreastfeedingTb=NPNBNTB|age=<2",
            "09"),
        new ColumnParameters(
            "Not Eligible 2-4",
            "Not Eligible 2-4",
            "eligible=NE|pregnantBreastfeedingTb=NPNBNTB|age=2-4",
            "10"),
        new ColumnParameters(
            "Not Eligible 5-9",
            "Not Eligible 5-9",
            "eligible=NE|pregnantBreastfeedingTb=NPNBNTB|age=5-9",
            "11"),
        new ColumnParameters(
            "Not Eligible 10-14",
            "Not Eligible 10-14",
            "eligible=NE|pregnantBreastfeedingTb=NPNBNTB|age=10-14",
            "12"));
  }
}
