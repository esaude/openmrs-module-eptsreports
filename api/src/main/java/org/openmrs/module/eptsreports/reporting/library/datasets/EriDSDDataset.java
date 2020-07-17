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

  private static final String N7 =
      "N7 Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in DC";

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
        "pregnantBreastfeeding",
        mapStraightThrough(eptsCommonDimension.getDSDNonPregnantAndNonBreastfeedingDimension()));

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

    dsd.addColumn(
        "DTT",
        "DSD DT Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DTT", EptsReportUtils.map(eriDSDCohortQueries.getN1(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "DTSST",
        "DSD DT Stable Subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DTSST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingDTStable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "DTSNPNBA",
        "DT Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DTSNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingDTStable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "DTSNPNBC",
        "DT Non-pregnant and Non-Breastfeeding Children (<15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DTSNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingDTStable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "DTUST",
        "DSD DT Unstable Subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DTUST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingDTUnstable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "DTUNPNBA",
        "DT Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DTUNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingDTUnstable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "DTUNPNBC",
        "DT Non-pregnant and Non-Breastfeeding Children (<15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DTUNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingDTUnstable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "FRT",
        "DSD FR Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "FRT", EptsReportUtils.map(eriDSDCohortQueries.getN2(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "FRSST",
        "DSD FR Stable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "FRSST", EptsReportUtils.map(eriDSDCohortQueries.getN2Stable(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "FRSNPNBA",
        "DSD FR Stable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "FRSNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingFRStable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "FRSNPNBC",
        " DSD FR Stable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "FRSNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingFRStable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "FRUST",
        "DSD FR Unstable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "FRUST", EptsReportUtils.map(eriDSDCohortQueries.getN2Unstable(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "FRUNPNBA",
        "DSD FR Unstable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "FRUNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingFRUnstable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "FRUNPNBC",
        " DSD FR Unstable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "FRUNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingFRUnstable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "GAACT",
        "DSD GAAC Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "GAACT", EptsReportUtils.map(eriDSDCohortQueries.getN3(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "GAACSST",
        "DSD GAAC Stable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "GAACSST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingGAACStable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "GAACSNPNBA",
        "DSD GAAC Stable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "GAACSNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingGAACStable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "GAACSNPNBC",
        " DSD GAAC Stable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "GAACSNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingGAACStable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "GAACUST",
        "DSD GAAC Unstable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "GAACUST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticpatingInGaacUnstable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "GAACUNPNBA",
        "DSD GAAC Unstable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "GAACUNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoAreNotPregnantAndNotBreastfeedingGAACUnstable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "GAACUNPNBC",
        " DSD GAAC Unstable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "GAACUNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoAreNotPregnantAndNotBreastfeedingGAACUnstable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    // N4
    dsd.addColumn(
        "AFT",
        "AF: Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AFT", EptsReportUtils.map(eriDSDCohortQueries.getN4(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "AFEST",
        "AF: Eligible subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AFEST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreEligible(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "AFENPNBA",
        "AF: Eligible Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AFSNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreEligible(), mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "AFENPNBC",
        "AF: Eligible Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AFENPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreEligible(), mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "AFNEST",
        "AF: Not Eligible subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AFNEST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreNotEligible(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "AFNENPNBA",
        "AF: Not Eligible Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AFNENPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreNotEligible(), mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "AFNENPNBC",
        "AF: Not Eligible Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AFNENPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreNotEligible(), mappings)),
            mappings),
        getChildrenColumn());

    addRow(dsd, "N7", N7, mapStraightThrough(getN7()), getDisags());
    // N8 data columns starts here
    dsd.addColumn(
        "AnyDSDModel-T",
        "Active patients on ART who participate in at least one measured DSD model N8)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AnyDSDModelT", EptsReportUtils.map(eriDSDCohortQueries.getN8(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "AnyDSDModelE-ST",
        "Active patients on ART who participate in at least one measured DSD model - Eligible(Stable) N8",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AnyDSDModelE-ST",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndStable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "AnyDSDModelE-05",
        "Adult Active patients on ART who participate in at least one measured DSD model - Eligible(Stable) N8",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AnyDSDModelE-05",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndStable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "AnyDSDModelE",
        "Active patients on ART who participate in at least one measured DSD model - Eligible(Stable) N8",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AnyDSDModelE",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndStable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "AnyDSDModelNE-ST",
        "Active patients on ART who participate in at least one measured DSD model - Not-Eligible(UnStable) N8",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AnyDSDModelNE-ST",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndUnStable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "AnyDSDModelNE-05",
        "Adult Active patients on ART who participate in at least one measured DSD model - Not-Eligible(UnStable) N8",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AnyDSDModelNE-05",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndUnStable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "AnyDSDModelNE",
        "Active patients on ART who participate in at least one measured DSD model - Not-Eligible(UnStable) N8",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "AnyDSDModelNE",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndUnStable(),
                    mappings)),
            mappings),
        getChildrenColumn());

    dsd.addColumn(
        "CAT",
        "DSD CA Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CAT", EptsReportUtils.map(eriDSDCohortQueries.getN5(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "CASST",
        "DSD CA Stable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CASST",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoAreActiveParticipatingInAccessionClubsAndStable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "CASNPNBA",
        "DSD CA Stable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CASNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoAreActiveParticipatingInAccessionClubsAndStable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "CASNPNBC",
        " DSD CA Stable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CASNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoAreActiveParticipatingInAccessionClubsAndStable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "CAUST",
        "DSD CA Unstable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CAUST",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoAreActiveParticipatingInAccessionClubsAndUnstable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "CAUNPNBA",
        "DSD CA Unstable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CAUNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoAreActiveParticipatingInAccessionClubsAndUnstable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "CAUNPNBC",
        " DSD CA Unstable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CAUNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoAreActiveParticipatingInAccessionClubsAndUnstable(),
                    mappings)),
            mappings),
        getChildrenColumn());

    dsd.addColumn(
        "DST",
        "DSD DS Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DST", EptsReportUtils.map(eriDSDCohortQueries.getN9(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "DSSST",
        "DSD DS Stable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSSST", EptsReportUtils.map(eriDSDCohortQueries.getN9Stable(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "DSSNPNBA",
        "DSD DS Stable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSSNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingDSStable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "DSSNPNBC",
        " DSD DS Stable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSSNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingDSStable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "DSUST",
        "DSD DS Unstable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSUST", EptsReportUtils.map(eriDSDCohortQueries.getN9Unstable(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "DSUNPNBA",
        "DSD DS Unstable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSUNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingDSUnstable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "DSUNPNBC",
        " DSD DS Unstable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSUNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingDSUnstable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    return dsd;
  }

  /**
   * 2-14 years old children List
   *
   * @return
   */
  private List<ColumnParameters> getChildrenColumn() {
    ColumnParameters twoTo4 = new ColumnParameters("twoTo4", "2-4", "age=2-4", "01");
    ColumnParameters fiveTo9 = new ColumnParameters("fiveTo9", "5-9", "age=5-9", "02");
    ColumnParameters tenTo14 = new ColumnParameters("tenTo14", "10-14", "age=10-14", "03");
    ColumnParameters lesThan2 = new ColumnParameters("lesThan2", "<2", "age=<2", "04");

    return Arrays.asList(lesThan2, twoTo4, fiveTo9, tenTo14);
  }

  private CohortIndicator getN7() {
    return eptsGeneralIndicator.getIndicator("N7", mapStraightThrough(eriDSDCohortQueries.getN7()));
  }

  private List<ColumnParameters> getDisags() {
    return Arrays.asList(
        new ColumnParameters("Total", "Total", "", "01"),
        new ColumnParameters("Eligible Sub-Total", "Eligible Sub-Total", "eligible=E", "02"),
        new ColumnParameters(
            "Eligible Adults",
            "Eligible Adults",
            "eligible=E|pregnantBreastfeeding=NPNB|age=15+",
            "03"),
        new ColumnParameters(
            "Eligible 2-4", "Eligible 2-4", "eligible=E|pregnantBreastfeeding=NPNB|age=2-4", "04"),
        new ColumnParameters(
            "Eligible 5-9", "Eligible 5-9", "eligible=E|pregnantBreastfeeding=NPNB|age=5-9", "05"),
        new ColumnParameters(
            "Eligible 10-14",
            "Eligible 10-14",
            "eligible=E|pregnantBreastfeeding=NPNB|age=10-14",
            "06"),
        new ColumnParameters(
            "Not Eligible Sub-Total", "Not Eligible Sub-Total", "eligible=NE", "07"),
        new ColumnParameters(
            "Not Eligible Adults",
            "Not Eligible Adults",
            "eligible=NE|pregnantBreastfeeding=NPNB|age=15+",
            "08"),
        new ColumnParameters(
            "Not Eligible <2",
            "Not Eligible <2",
            "eligible=NE|pregnantBreastfeeding=NPNB|age=<2",
            "09"),
        new ColumnParameters(
            "Not Eligible 2-4",
            "Not Eligible 2-4",
            "eligible=NE|pregnantBreastfeeding=NPNB|age=2-4",
            "10"),
        new ColumnParameters(
            "Not Eligible 5-9",
            "Not Eligible 5-9",
            "eligible=NE|pregnantBreastfeeding=NPNB|age=5-9",
            "11"),
        new ColumnParameters(
            "Not Eligible 10-14",
            "Not Eligible 10-14",
            "eligible=NE|pregnantBreastfeeding=NPNB|age=10-14",
            "12"));
  }
}
