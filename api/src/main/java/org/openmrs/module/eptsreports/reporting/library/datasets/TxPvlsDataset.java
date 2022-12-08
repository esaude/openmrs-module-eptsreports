/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxPvlsCohortQueries;
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
public class TxPvlsDataset extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TxPvlsCohortQueries txPvls;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  /** @return @{@link DataSetDefinition} */
  public DataSetDefinition constructTxPvlsDatset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    String mappingsKp = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    dsd.setName("TxPvls");
    dsd.addParameters(getParameters());

    // Tie dimensions to this data definition
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "query", EptsReportUtils.map(eptsCommonDimension.maternityDimension(), mappings));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    dsd.addDimension(
        "rt",
        EptsReportUtils.map(
            eptsCommonDimension.getViralLoadRoutineTargetReasonsDimension(), mappings));
    dsd.addDimension(
        "KP", EptsReportUtils.map(eptsCommonDimension.getKeyPopsDimension(), mappingsKp));

    addNumeratorColumns(dsd, mappings);
    addDenominatorColumns(dsd, mappings);

    return dsd;
  }

  private void addDenominatorColumns(CohortIndicatorDataSetDefinition dsd, String mappings) {
    // Denominator -------------------------------------------------------------
    // Totals
    dsd.addColumn(
        "totalDenominator",
        "Total patients with Viral load - Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients with viral load",
                EptsReportUtils.map(
                    txPvls.getPatientsWithViralLoadResultsAndOnArtForMoreThan3Months(), mappings)),
            mappings),
        "");

    // Get patients with viral load and on routine
    addRow(
        dsd,
        "DR",
        "Patients Denominator on Routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children",
                EptsReportUtils.map(
                    txPvls.getPatientsWithViralLoadResultsAndOnRoutine(), mappings)),
            mappings),
        getAdultChildrenColumns());
    // Get patients with viral load and on target
    addRow(
        dsd,
        "DT",
        "Patients Denominator on Target",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on target adults and children",
                EptsReportUtils.map(txPvls.getPatientsWithViralLoadResultsAndOnTarget(), mappings)),
            mappings),
        getAdultChildrenColumns());

    // Breastfeeding & Pregnant
    // Breastfeeding and on ART for more than 3 months and have VL results
    addRow(
        dsd,
        "B",
        "Breast feeding, have vl results and on ART more than 3 months Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breast feeding, have vl results and on ART more than 3 months Denominator",
                EptsReportUtils.map(
                    txPvls.getBreastfeedingWomenWhoHaveViralLoadResults(), mappings)),
            mappings),
        getRoutineTargetedColumns());

    // Pregnant women on ART for more than 3 months and have VL results
    addRow(
        dsd,
        "P",
        "Pregnant, have vl results and on ART more than 3 months Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregnant, have vl results and on ART more than 3 months Denominator",
                EptsReportUtils.map(txPvls.getPregnantWomenWithViralLoadResults(), mappings)),
            mappings),
        getRoutineTargetedColumns());

    // Routine for Adults & Children denominator KP
    addRow(
        dsd,
        "KPD",
        "Key population patients and are on routine and target Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Key population patients and are on routine and target Denominator",
                EptsReportUtils.map(
                    txPvls.getPatientsWithViralLoadResultsAndOnArtForMoreThan3Months(), mappings)),
            mappings),
        getKpRoutineTargetedColumns());
  }

  private void addNumeratorColumns(CohortIndicatorDataSetDefinition dsd, String mappings) {
    // Numerator ------------------------------------------------------------
    // Totals
    dsd.addColumn(
        "totalNumerator",
        "Total patients with suppressed Viral load - Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "suppressed viral load",
                EptsReportUtils.map(
                    txPvls.getPatientsWithViralLoadSuppressionWhoAreOnArtMoreThan3Months(),
                    mappings)),
            mappings),
        "");

    // Breastfeeding & Pregnant
    // Breastfeeding
    addRow(
        dsd,
        "BN",
        "Breast feeding, have vl suppression and on ART more than 3 months numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breast feeding, have vl suppression and on ART more than 3 months numerator",
                EptsReportUtils.map(
                    txPvls.getBreastfeedingWomenWhoHaveViralSuppression(), mappings)),
            mappings),
        getRoutineTargetedColumns());
    // Pregnant
    addRow(
        dsd,
        "PN",
        "Pregnant patients, have vl suppression and on ART more than 3 months numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregnant patients, have vl suppression and on ART more than 3 months numerator",
                EptsReportUtils.map(txPvls.getPregnantWomenWithViralLoadSuppression(), mappings)),
            mappings),
        getRoutineTargetedColumns());

    // Adults & children on Routine
    addRow(
        dsd,
        "NR",
        "Adult and children with viral load suppression, have more than 3 months on ART and on routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Adult and children with viral load suppression, have more than 3 months on ART and on routine",
                EptsReportUtils.map(txPvls.getPatientWithViralSuppressionAndOnRoutine(), mappings)),
            mappings),
        getAdultChildrenColumns());
    // Adults and children on Target
    addRow(
        dsd,
        "NT",
        "Adult and children with viral load suppression, have more than 3 months on ART and on target",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Adult and children with viral load suppression, have more than 3 months on ART and on target",
                EptsReportUtils.map(txPvls.getPatientWithViralSuppressionAndOnTarget(), mappings)),
            mappings),
        getAdultChildrenColumns());

    // Routine for Adults & Children Numerator KP
    addRow(
        dsd,
        "KPN",
        "Key population patients and are on routine and target Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Key population patients and are on routine and target Numerator",
                EptsReportUtils.map(
                    txPvls.getPatientsWithViralLoadSuppressionWhoAreOnArtMoreThan3Months(),
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
        new ColumnParameters("twenty5To29M", "25 - 29 male", "gender=M|age=25-29", "07");
    ColumnParameters thirtyTo34M =
        new ColumnParameters("thirtyTo34M", "30 - 34 male", "gender=M|age=30-34", "08");
    ColumnParameters thirty5To39M =
        new ColumnParameters("thirty5To39M", "35 - 39 male", "gender=M|age=35-39", "09");
    ColumnParameters fortyTo44M =
        new ColumnParameters("fortyTo44M", "40 - 44 male", "gender=M|age=40-44", "10");
    ColumnParameters forty5To49M =
        new ColumnParameters("forty5To49M", "45 - 49 male", "gender=M|age=45-49", "11");
    // 50-54, 55-59, 60-64, 65+ male
    ColumnParameters fiftyTo54M =
        new ColumnParameters("fiftyTo54M", "50 - 54 male", "gender=M|age=50-54", "12");
    ColumnParameters fifty5To59M =
        new ColumnParameters("fifty5To59M", "55 - 59 male", "gender=M|age=55-59", "13");
    ColumnParameters sixtyTo64M =
        new ColumnParameters("sixtyTo64M", "60 - 64 male", "gender=M|age=60-64", "14");
    ColumnParameters above65M =
        new ColumnParameters("above65M", "65+ male", "gender=M|age=65+", "15");
    ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "16");

    // Female
    ColumnParameters under1F =
        new ColumnParameters("under1F", "under 1 year female", "gender=F|age=<1", "17");
    ColumnParameters oneTo4F =
        new ColumnParameters("oneTo4F", "1 - 4 years female", "gender=F|age=1-4", "18");
    ColumnParameters fiveTo9F =
        new ColumnParameters("fiveTo9F", "5 - 9 years female", "gender=F|age=5-9", "19");
    ColumnParameters tenTo14F =
        new ColumnParameters("tenTo14F", "10 - 14 female", "gender=F|age=10-14", "20");
    ColumnParameters fifteenTo19F =
        new ColumnParameters("fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "21");
    ColumnParameters twentyTo24F =
        new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "22");
    ColumnParameters twenty5To29F =
        new ColumnParameters("twenty5To29F", "25 - 29 female", "gender=F|age=25-29", "23");
    ColumnParameters thirtyTo34F =
        new ColumnParameters("thirtyTo34F", "30 - 34 female", "gender=F|age=30-34", "24");
    ColumnParameters thirty5To39F =
        new ColumnParameters("thirty5To39F", "35 - 39 female", "gender=F|age=35-39", "25");
    ColumnParameters fortyTo44F =
        new ColumnParameters("fortyTo44F", "40 - 44 female", "gender=F|age=40-44", "26");
    ColumnParameters forty5To49F =
        new ColumnParameters("forty5To49F", "45 - 49 female", "gender=F|age=45-49", "27");
    // 50-54, 55-59, 60-64, 65+ female
    ColumnParameters fiftyTo54F =
        new ColumnParameters("fiftyTo54F", "50 - 54 female", "gender=F|age=50-54", "28");
    ColumnParameters fifty5To59F =
        new ColumnParameters("fifty5To59F", "55 - 59 female", "gender=F|age=55-59", "29");
    ColumnParameters sixtyTo64F =
        new ColumnParameters("sixtyTo64F", "60 - 64 female", "gender=F|age=60-64", "30");
    ColumnParameters above65F =
        new ColumnParameters("above65F", "65+ female", "gender=F|age=65+", "31");
    ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "32");

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
        fortyTo44M,
        forty5To49M,
        fiftyTo54M,
        fifty5To59M,
        sixtyTo64M,
        above65M,
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
        fortyTo44F,
        forty5To49F,
        fiftyTo54F,
        fifty5To59F,
        sixtyTo64F,
        above65F);
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
