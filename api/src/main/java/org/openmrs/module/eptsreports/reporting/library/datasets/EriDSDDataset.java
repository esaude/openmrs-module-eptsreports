package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.EriDSDCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
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
public class EriDSDDataset extends BaseDataSet {

  @Autowired private EriDSDCohortQueries eriDSDCohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private TxCurrCohortQueries txCurrCohortQueries;
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

    dsd.setName("total");
    dsd.addColumn(
        "D1T",
        "DSD D1 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D1 Total",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getAllPatientsWhoAreActiveAndStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "D1NPNB",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D1NPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getAllPatientsWhoAreActiveAndStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "D1NPNBC",
        "Non-pregnant and Non-Breastfeeding Children By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D1NPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getAllPatientsWhoAreActiveAndStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "D2T",
        "DSD D2 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D2 Total",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "D2NPNB",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2NPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndUnstable(),
                    "endDate=${endDate},location=${location}")),
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
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "BNP",
        "Breastfeeding (exclude pregnant)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "BNP",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastFeedingAndNotPregnant(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "PNB",
        "Pregnant (exclude breastfeeding)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "PNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoArePregnantAndNotBreastFeeding(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N1T",
        "DSD N1 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1T",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModel(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N1SST",
        "DSD N1 Stable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1SST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N1SNPNB",
        "Stable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1SNPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N1SNPNBC",
        "Stable Non-pregnant and Non-Breastfeeding Children  By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1SNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N1UST",
        "DSD N1 Unstable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1UST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N1UNPNB",
        "Unstable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1UNPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N1UNPNBC",
        "Unstable Non-pregnant and Non-Breastfeeding Children  By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1UNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N1UBNP",
        "N1 Unstable Breastfeeding (exclude pregnant)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1UBNP",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoAreBreastFeedingAndNotPregnantAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N1UPNB",
        "N1 Unstable Pregnant (include breastfeeding)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1UPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoArePregnantAndNotBreastFeedingAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N2T",
        "DSD N2 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2T",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveWithNextPickupAs3Months(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");

    return dsd;
  }

  private List<ColumnParameters> getChildrenColumn() {
    ColumnParameters twoTo4 = new ColumnParameters("twoTo4", "2-4", "age=2-4", "01");
    ColumnParameters fiveTo9 = new ColumnParameters("fiveTo9", "5-9", "age=5-9", "02");
    ColumnParameters tenTo14 = new ColumnParameters("tenTo14", "10-14", "age=10-14", "03");

    return Arrays.asList(twoTo4, fiveTo9, tenTo14);
  }
}
