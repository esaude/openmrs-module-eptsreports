package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxPvlsBySourceClinicalOrFichaResumoCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxPvlsBySourceLabOrFsrCohortQueries;
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
public class TxPvlsBySourceDataset extends BaseDataSet {

  private TxPvlsBySourceLabOrFsrCohortQueries txPvlsBySourceLabOrFsrCohortQueries;
  private EptsCommonDimension eptsCommonDimension;
  private EptsGeneralIndicator eptsGeneralIndicator;
  private AgeDimensionCohortInterface ageDimensionCohortInterface;

  private TxPvlsBySourceClinicalOrFichaResumoCohortQueries
      txPvlsBySourceClinicalOrFichaResumoCohortQueries;

  @Autowired
  public TxPvlsBySourceDataset(
      TxPvlsBySourceLabOrFsrCohortQueries txPvlsBySourceLabOrFsrCohortQueries,
      TxPvlsBySourceClinicalOrFichaResumoCohortQueries
          txPvlsBySourceClinicalOrFichaResumoCohortQueries,
      EptsCommonDimension eptsCommonDimension,
      EptsGeneralIndicator eptsGeneralIndicator,
      @Qualifier("commonAgeDimensionCohort")
          AgeDimensionCohortInterface ageDimensionCohortInterface) {
    this.txPvlsBySourceLabOrFsrCohortQueries = txPvlsBySourceLabOrFsrCohortQueries;
    this.txPvlsBySourceClinicalOrFichaResumoCohortQueries =
        txPvlsBySourceClinicalOrFichaResumoCohortQueries;
    this.eptsCommonDimension = eptsCommonDimension;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.ageDimensionCohortInterface = ageDimensionCohortInterface;
  }

  String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
  String mappingsKp = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";

  public DataSetDefinition getPvlsLabFsr() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("PLF");
    dsd.setDescription("PVLS_LAB_FSR");
    dsd.addParameters(getParameters());
    // Tie dimensions to this data definition
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "query", EptsReportUtils.map(eptsCommonDimension.maternityDimension(), mappings));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohortInterface), "effectiveDate=${endDate}"));
    dsd.addDimension(
        "rt",
        EptsReportUtils.map(
            eptsCommonDimension.getViralLoadRoutineTargetReasonsDimension(), mappings));
    dsd.addDimension(
        "KP", EptsReportUtils.map(eptsCommonDimension.getKeyPopsDimension(), mappingsKp));

    addNumeratorColumnsForLabAndFsr(dsd, mappings);
    addDenominatorColumnsForLabAndFsr(dsd, mappings);
    return dsd;
  }

  public DataSetDefinition getPvlFichaMestre() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("PFM");
    dsd.setDescription("PVLS_Ficha_Mestre");
    dsd.addParameters(getParameters());
    // Tie dimensions to this data definition
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "query", EptsReportUtils.map(eptsCommonDimension.maternityDimension(), mappings));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohortInterface), "effectiveDate=${endDate}"));
    dsd.addDimension(
        "rt",
        EptsReportUtils.map(
            eptsCommonDimension.getViralLoadRoutineTargetReasonsDimension(), mappings));
    dsd.addDimension(
        "KP", EptsReportUtils.map(eptsCommonDimension.getKeyPopsDimension(), mappingsKp));
    addDenominatorColumnsForClinicalForms(dsd, mappings);
    addNumeratorColumnsForClinicalForms(dsd, mappings);
    return dsd;
  }

  private void addDenominatorColumnsForLabAndFsr(
      CohortIndicatorDataSetDefinition dsd, String mappings) {
    dsd.addColumn(
        "DTT",
        "Total patients with Viral load - Denominator based on Lab or FSR",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients with viral load",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getPatientsViralLoadWithin12MonthsForLabAndFsrDenominatorAndOnArtForMoreThan3Months(),
                    mappings)),
            mappings),
        "");

    // Get patients with viral load and on routine
    addRow(
        dsd,
        "DR",
        "Patients Denominator on Routine based on Lab or FSR",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children based on Lab or FSR",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getPatientsWithViralLoadResultsAndOnRoutineForLabAndFsrDenominator(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());
    // Get patients with viral load and on target
    addRow(
        dsd,
        "DT",
        "Patients Denominator on Target based on Lab or FSR",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on target adults and children based on Lab or FSR",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getPatientsWhoAreOnTargetForLabAndFsrDenominator(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    // Breastfeeding & Pregnant
    // Breastfeeding and on ART for more than 3 months and have VL results
    addRow(
        dsd,
        "BD",
        "Breast feeding, have vl results and on ART more than 3 months Denominator with Lab or FSR",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breast feeding, have vl results and on ART more than 3 months Denominator with Lab or FSR",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getBreastfeedingWomenWhoHaveViralLoadResultsForLabAndFsrDenominator(),
                    mappings)),
            mappings),
        getRoutineTargetedColumns());

    // Pregnant women on ART for more than 3 months and have VL results
    addRow(
        dsd,
        "PD",
        "Pregnant, have vl results and on ART more than 3 months Denominator with Lab or FSR",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregnant, have vl results and on ART more than 3 months Denominator with Lab or FSR",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getPregnantWomenWithViralLoadResultsForLabAndFsrDenominator(),
                    mappings)),
            mappings),
        getRoutineTargetedColumns());

    // Routine for Adults & Children denominator KP
    addRow(
        dsd,
        "KPD",
        "Key population patients and are on routine and target Denominator with Lab or FSR",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Key population patients and are on routine and target Denominator with Lab or FSR",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getPatientsViralLoadWithin12MonthsForLabAndFsrDenominatorAndOnArtForMoreThan3Months(),
                    mappings)),
            mappings),
        getKpRoutineTargetedColumns());
  }

  private void addNumeratorColumnsForLabAndFsr(
      CohortIndicatorDataSetDefinition dsd, String mappings) {
    dsd.addColumn(
        "NTT",
        "Total patients with suppressed Viral load based on Lab or FSR - Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "suppressed viral load",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getPatientsWithViralLoadSuppressionForLabAndFsrNumeratorWhoAreOnArtMoreThan3Months(),
                    mappings)),
            mappings),
        "");

    // Breastfeeding & Pregnant
    // Breastfeeding
    addRow(
        dsd,
        "BN",
        "Breast feeding, have vl suppression and on ART more than 3 months numerator based on Lab or FSR",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breast feeding, have vl suppression and on ART more than 3 months numerator based on FSR",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getBreastfeedingWomenWithViralSuppressionForLabAndFsrNumerator(),
                    mappings)),
            mappings),
        getRoutineTargetedColumns());
    // Pregnant
    addRow(
        dsd,
        "PN",
        "Pregnant patients, have vl suppression and on ART more than 3 months numerator based on Lab or FSR",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregnant patients, have vl suppression and on ART more than 3 months numerator based on Lab or FSR",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getPregnantWomenWithViralLoadSuppressionForLabAndFsrNumerator(),
                    mappings)),
            mappings),
        getRoutineTargetedColumns());

    // Adults & children on Routine
    addRow(
        dsd,
        "NR",
        "Adult and children with viral load suppression, have more than 3 months on ART and on routine based on Lab or FSR",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Adult and children with viral load suppression, have more than 3 months on ART and on routine based on Lab or FSR",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getPatientWithViralSuppressionAndOnRoutineForLabAndFsrNumerator(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());
    // Adults and children on Target
    addRow(
        dsd,
        "NT",
        "Adult and children with viral load suppression, have more than 3 months on ART and on target based on Lab or FSR",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Adult and children with viral load suppression, have more than 3 months on ART and on target based on Lab or FSR",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getPatientWithViralSuppressionAndOnTargetForLabAndFsrNumerator(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    // Routine for Adults & Children Numerator KP
    addRow(
        dsd,
        "KPN",
        "Key population patients and are on routine and target Numerator based on Lab or FSR",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Key population patients and are on routine and target Numerator based on Lab or FSR",
                EptsReportUtils.map(
                    txPvlsBySourceLabOrFsrCohortQueries
                        .getPatientsWithViralLoadSuppressionForLabAndFsrNumeratorWhoAreOnArtMoreThan3Months(),
                    mappings)),
            mappings),
        getKpRoutineTargetedColumns());
  }

  private void addDenominatorColumnsForClinicalForms(
      CohortIndicatorDataSetDefinition dsd, String mappings) {
    dsd.addColumn(
        "DTC",
        "Total patients with Viral load - Denominator based on Clinical forms",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients with viral load based on clinical forms",
                EptsReportUtils.map(
                    txPvlsBySourceClinicalOrFichaResumoCohortQueries
                        .getPatientsViralLoadWithin12MonthsForFichaMestreDenominatorAndOnArtForMoreThan3Months(),
                    mappings)),
            mappings),
        "");

    // Get patients with viral load and on routine
    addRow(
        dsd,
        "DRC",
        "Patients Denominator on Routine based on Clinical forms",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children based on clinical forms",
                EptsReportUtils.map(
                    txPvlsBySourceClinicalOrFichaResumoCohortQueries
                        .getPatientsWithViralLoadResultsAndOnRoutineForFichaMestreDenominator(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    // Breastfeeding & Pregnant
    // Breastfeeding and on ART for more than 3 months and have VL results
    addRow(
        dsd,
        "DBC",
        "Breast feeding, have vl results and on ART more than 3 months Denominator with Clinical forms",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breast feeding, have vl results and on ART more than 3 months Denominator with clinical forms",
                EptsReportUtils.map(
                    txPvlsBySourceClinicalOrFichaResumoCohortQueries
                        .getBreastfeedingWomenWhoHaveViralLoadResultsForFichaMestreDenominator(),
                    mappings)),
            mappings),
        getRoutineTargetedColumns());

    // Pregnant women on ART for more than 3 months and have VL results
    addRow(
        dsd,
        "DPC",
        "Pregnant, have vl results and on ART more than 3 months Denominator with Clinical forms",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregnant, have vl results and on ART more than 3 months Denominator with Clinical forms",
                EptsReportUtils.map(
                    txPvlsBySourceClinicalOrFichaResumoCohortQueries
                        .getPregnantWomenWithViralLoadResultsForFichaMestreDenominator(),
                    mappings)),
            mappings),
        getRoutineTargetedColumns());

    // Routine for Adults & Children denominator KP
    addRow(
        dsd,
        "KPDC",
        "Key population patients and are on routine and target Denominator with Clinical forms",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Key population patients and are on routine and target Denominator with Clinical forms",
                EptsReportUtils.map(
                    txPvlsBySourceClinicalOrFichaResumoCohortQueries
                        .getPatientsViralLoadWithin12MonthsForFichaMestreDenominatorAndOnArtForMoreThan3Months(),
                    mappings)),
            mappings),
        getKpRoutineTargetedColumns());
  }

  private void addNumeratorColumnsForClinicalForms(
      CohortIndicatorDataSetDefinition dsd, String mappings) {
    dsd.addColumn(
        "NTC",
        "Total patients with suppressed Viral load based on Clinical forms- Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "suppressed viral load based on clinical forms",
                EptsReportUtils.map(
                    txPvlsBySourceClinicalOrFichaResumoCohortQueries
                        .getPatientsWithViralLoadSuppressionForFichaMestreNumeratorWhoAreOnArtMoreThan3Months(),
                    mappings)),
            mappings),
        "");

    // Breastfeeding & Pregnant
    // Breastfeeding
    addRow(
        dsd,
        "NBC",
        "Breast feeding, have vl suppression and on ART more than 3 months numerator based on Clinical forms",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breast feeding, have vl suppression and on ART more than 3 months numerator based on clinical forms",
                EptsReportUtils.map(
                    txPvlsBySourceClinicalOrFichaResumoCohortQueries
                        .getBreastfeedingWomenWithViralSuppressionForFichaMestreNumerator(),
                    mappings)),
            mappings),
        getRoutineTargetedColumns());
    // Pregnant
    addRow(
        dsd,
        "NPC",
        "Pregnant patients, have vl suppression and on ART more than 3 months numerator based on Clinical form",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregnant patients, have vl suppression and on ART more than 3 months numerator based on Clinical forms",
                EptsReportUtils.map(
                    txPvlsBySourceClinicalOrFichaResumoCohortQueries
                        .getPregnantWomenWithViralLoadSuppressionForLabAndFsrNumerator(),
                    mappings)),
            mappings),
        getRoutineTargetedColumns());

    // Adults & children on Routine
    addRow(
        dsd,
        "NRC",
        "Adult and children with viral load suppression, have more than 3 months on ART and on routine based on Clinical forms",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Adult and children with viral load suppression, have more than 3 months on ART and on routine based on Clinical forms",
                EptsReportUtils.map(
                    txPvlsBySourceClinicalOrFichaResumoCohortQueries
                        .getPatientWithViralSuppressionAndOnRoutineForFichaMestreNumerator(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    // Routine for Adults & Children Numerator KP
    addRow(
        dsd,
        "NKPC",
        "Key population patients and are on routine and target Numerator based on Clinical forms",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Key population patients and are on routine and target Numerator based on Clinical forms",
                EptsReportUtils.map(
                    txPvlsBySourceClinicalOrFichaResumoCohortQueries
                        .getPatientsWithViralLoadSuppressionForFichaMestreNumeratorWhoAreOnArtMoreThan3Months(),
                    mappings)),
            mappings),
        getKpRoutineTargetedColumns());
  }

  private List<ColumnParameters> getAdultChildrenColumns() {
    // Male
    ColumnParameters under1M =
        new ColumnParameters("under1M", "under 1 year male", "gender=M|age=<1", "01");
    ColumnParameters oneTo4M =
        new ColumnParameters("oneTo4M", "1 - 4 years male", "gender=M|age=1-4", "02");
    ColumnParameters fiveTo9M =
        new ColumnParameters("fiveTo9M", "5 - 9 years male", "gender=M|age=5-9", "03");
    ColumnParameters tenTo14M =
        new ColumnParameters("tenTo14M", "10 - 14 male", "gender=M|age=10-14", "04");
    ColumnParameters fifteenTo19M =
        new ColumnParameters("fifteenTo19M", "15 - 19 male", "gender=M|age=15-19", "05");
    ColumnParameters twentyTo24M =
        new ColumnParameters("twentyTo24M", "20 - 24 male", "gender=M|age=20-24", "06");
    ColumnParameters twenty5To29M =
        new ColumnParameters("twenty4To29M", "25 - 29 male", "gender=M|age=25-29", "07");
    ColumnParameters thirtyTo34M =
        new ColumnParameters("thirtyTo34M", "30 - 34 male", "gender=M|age=30-34", "08");
    ColumnParameters thirty5To39M =
        new ColumnParameters("thirty5To39M", "35 - 39 male", "gender=M|age=35-39", "09");
    ColumnParameters foutyTo44M =
        new ColumnParameters("foutyTo44M", "40 - 44 male", "gender=M|age=40-44", "10");
    ColumnParameters fouty5To49M =
        new ColumnParameters("fouty5To49M", "45 - 49 male", "gender=M|age=45-49", "11");
    ColumnParameters above50M =
        new ColumnParameters("above50M", "50+ male", "gender=M|age=50+", "12");
    ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "13");

    // Female
    ColumnParameters under1F =
        new ColumnParameters("under1F", "under 1 year female", "gender=F|age=<1", "14");
    ColumnParameters oneTo4F =
        new ColumnParameters("oneTo4F", "1 - 4 years female", "gender=F|age=1-4", "15");
    ColumnParameters fiveTo9F =
        new ColumnParameters("fiveTo9F", "5 - 9 years female", "gender=F|age=5-9", "16");
    ColumnParameters tenTo14F =
        new ColumnParameters("tenTo14F", "10 - 14 female", "gender=F|age=10-14", "17");
    ColumnParameters fifteenTo19F =
        new ColumnParameters("fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "18");
    ColumnParameters twentyTo24F =
        new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "19");
    ColumnParameters twenty5To29F =
        new ColumnParameters("twenty4To29F", "25 - 29 female", "gender=F|age=25-29", "20");
    ColumnParameters thirtyTo34F =
        new ColumnParameters("thirtyTo34F", "30 - 34 female", "gender=F|age=30-34", "21");
    ColumnParameters thirty5To39F =
        new ColumnParameters("thirty5To39F", "35 - 39 female", "gender=F|age=35-39", "22");
    ColumnParameters foutyTo44F =
        new ColumnParameters("foutyTo44F", "40 - 44 female", "gender=F|age=40-44", "23");
    ColumnParameters fouty5To49F =
        new ColumnParameters("fouty5To49F", "45 - 49 female", "gender=F|age=45-49", "24");
    ColumnParameters above50F =
        new ColumnParameters("above50F", "50+ female", "gender=F|age=50+", "25");
    ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "26");

    return Arrays.asList(
        unknownM,
        under1M,
        oneTo4M,
        fiveTo9M,
        tenTo14M,
        fifteenTo19M,
        twentyTo24M,
        twenty5To29M,
        thirtyTo34M,
        thirty5To39M,
        foutyTo44M,
        fouty5To49M,
        above50M,
        unknownF,
        under1F,
        oneTo4F,
        fiveTo9F,
        tenTo14F,
        fifteenTo19F,
        twentyTo24F,
        twenty5To29F,
        thirtyTo34F,
        thirty5To39F,
        foutyTo44F,
        fouty5To49F,
        above50F);
  }

  private List<ColumnParameters> getRoutineTargetedColumns() {
    ColumnParameters routine = new ColumnParameters("routine", "R", "rt=VLR", "01");
    ColumnParameters target = new ColumnParameters("target", "T", "rt=VLT", "02");
    return Arrays.asList(routine, target);
  }

  private List<ColumnParameters> getKpRoutineTargetedColumns() {
    // PID
    ColumnParameters piwdR = new ColumnParameters("piwdR", "PIWD-R", "KP=PID|rt=VLR", "01");
    ColumnParameters piwdT = new ColumnParameters("piwdT", "PIWD-T", "KP=PID|rt=VLT", "02");

    // MSM
    ColumnParameters msnR = new ColumnParameters("msnR", "MSN-R", "KP=MSM|rt=VLR", "04");
    ColumnParameters msnT = new ColumnParameters("msnT", "MSN-T", "KP=MSM|rt=VLT", "05");

    // CSW
    ColumnParameters cswR = new ColumnParameters("cswR", "CSW-R", "KP=CSW|rt=VLR", "07");
    ColumnParameters cswT = new ColumnParameters("cswT", "CSW-T", "KP=CSW|rt=VLT", "08");

    // PRI
    ColumnParameters priR = new ColumnParameters("priR", "PRI-R", "KP=PRI|rt=VLR", "10");
    ColumnParameters priT = new ColumnParameters("priT", "PRI-T", "KP=PRI|rt=VLT", "11");

    return Arrays.asList(piwdR, piwdT, msnR, msnT, cswR, cswT, priR, priT);
  }
}
