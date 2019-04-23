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

  public DataSetDefinition constructTxPvlsDatset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("Tx_Pvls Data Set");
    dsd.addParameters(getParameters());

    // Tie dimensions to this data definition
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "query", EptsReportUtils.map(eptsCommonDimension.maternityDimension(), mappings));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    // Numerator ------------------------------------------------------------
    // Totals
    dsd.addColumn(
        "0N",
        "Total patients with suppressed Viral load - Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "suppressed viral load",
                EptsReportUtils.map(txPvls.getPatientsWithViralLoadSuppression(), mappings)),
            mappings),
        "");

    // Breastfeeding & Pregnant
    dsd.addColumn(
        "B01",
        "Breast feeding and on routine Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "breastfeedingWomenWithViralLoadSuppression routine numerator",
                EptsReportUtils.map(txPvls.getBreastFeedingWomenOnRoutineNumerator(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "B02",
        "Breast feeding and NOT documented Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "breastfeedingWomenWithViralLoadSuppression not documented numerator",
                EptsReportUtils.map(
                    txPvls.getBreastFeedingWomenNotDocumentedNumerator(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "B03",
        "Pregnant and on routine Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "pregnantWomenWithViralLoadSuppression and on routine",
                EptsReportUtils.map(txPvls.getPregnantAndOnRoutineNumerator(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "B04",
        "Pregnant and NOT documented Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "pregnantWomenWithViralLoadSuppression and NOT documented",
                EptsReportUtils.map(txPvls.getPregnantAndNotDocumentedNumerator(), mappings)),
            mappings),
        "");

    // Adults & children Routine
    addRow(
        dsd,
        "NR",
        "Children numerator routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load suppression on routine adults and children",
                EptsReportUtils.map(
                    txPvls.getPatientWithViralSuppressionAndOnRoutineAdultsAndChildren(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    // NOT documented Adults & Children
    addRow(
        dsd,
        "NND",
        "Children numerator NOT documented",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load suppression not documents adults and children",
                EptsReportUtils.map(
                    txPvls.getPatientWithViralSuppressionAndNotDocumentedForAdultsAndChildren(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    // Denominator -------------------------------------------------------------
    // Totals
    dsd.addColumn(
        "0D",
        "Total patients with Viral load - Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients with viral load",
                EptsReportUtils.map(txPvls.getPatientsWithViralLoadResults(), mappings)),
            mappings),
        "");

    // Breastfeeding & Pregnant
    dsd.addColumn(
        "B05",
        "Breast feeding and on routine Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "breastfeedingWomenWithViralLoad-routine denominator",
                EptsReportUtils.map(
                    txPvls.getBreastfeedingWomenOnRoutineWithViralLoadResultsDenominator(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "B06",
        "Breast feeding and NOT documented Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "breastfeedingWomenWithViralLoad-not documented denominator",
                EptsReportUtils.map(
                    txPvls.getBreastfeedingWomenAndNotDocumentedWithViralLoadResultsDenominator(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "B07",
        "Pregnant and on routine Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "pregnantWomenWithViralLoad and not documented denominator",
                EptsReportUtils.map(
                    txPvls.getPregnantWomenAndOnRoutineWithViralLoadResultsDenominator(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "B08",
        "Pregnant and NOT documented Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "pregnantWomenWithViralLoad results not documented denominator",
                EptsReportUtils.map(
                    txPvls.getPregnantWomenAndNotDocumentedWithViralLoadResultsDenominator(),
                    mappings)),
            mappings),
        "");

    // Routine for Adults & Children
    addRow(
        dsd,
        "DR",
        "Adults & Children Denominator Routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children",
                EptsReportUtils.map(
                    txPvls.getPatientsWithViralLoadREsultsAndOnRoutineForChildrenAndAdults(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    // NOT documented for Adults & Children
    addRow(
        dsd,
        "DND",
        "Adults & Children Denominator NOT documented",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results not documented adults & children",
                EptsReportUtils.map(
                    txPvls.getPatientsWithViralLoadREsultsAndNotDocumenetdForChildrenAndAdults(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    return dsd;
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
}
