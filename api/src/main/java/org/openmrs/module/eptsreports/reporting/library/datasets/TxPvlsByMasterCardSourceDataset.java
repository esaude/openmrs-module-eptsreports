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
import org.openmrs.module.eptsreports.reporting.library.cohorts.PvlsBySourceCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.KeyPopulationDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.library.queries.TxPvlsBySourceQueriesInterface.QUERY.SourceType;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TxPvlsByMasterCardSourceDataset extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private PvlsBySourceCohortQueries pvlsBySourceCohortQueries;

  @Autowired private KeyPopulationDimension keyPopulationDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructTxPvlsDatset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dataSetDefinition.setName("TX_PVLS Data Set");

    dataSetDefinition.addParameters(this.getParameters());

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addColumn(
        "PVLSTOTAL-S2",
        "Total patients with Viral load - Denominator (MAstercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients with viral load",
                EptsReportUtils.map(
                    this.pvlsBySourceCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsBySource(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    addRow(
        dataSetDefinition,
        "DR-S2",
        "Adults & Children Denominator Routine (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children",
                EptsReportUtils.map(
                    this.pvlsBySourceCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    addRow(
        dataSetDefinition,
        "DT-S2",
        "Adults & Children Denominator Routine (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children",
                EptsReportUtils.map(
                    this.pvlsBySourceCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    dataSetDefinition.addColumn(
        "DPREGROTINE-S2",
        "Pregant routine (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant routine",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "DPREGTARGET-S2",
        "pregnant target (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant routine",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "DBREASROTINE-S2",
        "Breastfeeding routine (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breastfeeding routine",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "DBREASTARGET-S2",
        "Breastfeeding routine (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant routine",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NRTOTAL-S2",
        "Total patients with Viral load - numerator (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients with viral load",
                EptsReportUtils.map(
                    this.pvlsBySourceCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    addRow(
        dataSetDefinition,
        "NR-S2",
        "Adults & Children Numerator Routine (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children",
                EptsReportUtils.map(
                    this.pvlsBySourceCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    addRow(
        dataSetDefinition,
        "NT-S2",
        "Adults & Children Numerator target (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children",
                EptsReportUtils.map(
                    this.pvlsBySourceCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    dataSetDefinition.addColumn(
        "NPREGROTINE-S2",
        "Pregant routine (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant routine",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Rotine(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NPREGTARGET-S2",
        "Pregant routine (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant routine",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Target(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NBREASROTINE-S2",
        "Pregant routine (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant routine",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findPregnantBreatsFeedingWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Rotine(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NBREASTARGET-S2",
        "Pregant target (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant routine",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findPregnantBreatsFeedingWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Target(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    // Add SubTotal Denominator

    dataSetDefinition.addColumn(
        "DRSUBTOTAL-S2",
        "Rotine Sub Total (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Rotine Sub Total",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "DTSUBTOTAL-S2",
        "Target Sub Total (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Target Sub Total",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    // Add SubTotal Numerator

    dataSetDefinition.addColumn(
        "NRSUBTOTAL-S2",
        "Rotine Numerator Sub Total (Mastercard Source) ",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Rotine Numerator Sub Total",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NTSUBTOTAL-S2",
        "Target Numerator Sub Total (Mastercard Source)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Target Numerator Sub Total",
                EptsReportUtils.map(
                    pvlsBySourceCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(
                            SourceType.MASTERCARD),
                    mappings)),
            mappings),
        "");

    // Key Population dimension

    dataSetDefinition.addDimension(
        "homosexual",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreHomosexual(), mappings));

    dataSetDefinition.addDimension(
        "drug-user",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoUseDrugs(), mappings));

    dataSetDefinition.addDimension(
        "prisioner",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreInPrison(), mappings));

    dataSetDefinition.addDimension(
        "sex-worker",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreSexWorker(), mappings));

    // Key population collumn denominator

    final CohortIndicator rotineDenominator =
        this.eptsGeneralIndicator.getIndicator(
            "rotine",
            EptsReportUtils.map(
                pvlsBySourceCohortQueries
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(
                        SourceType.MASTERCARD),
                mappings));

    final CohortIndicator targetDenominator =
        this.eptsGeneralIndicator.getIndicator(
            "target",
            EptsReportUtils.map(
                pvlsBySourceCohortQueries
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
                        SourceType.MASTERCARD),
                mappings));

    dataSetDefinition.addColumn(
        "DRMSM-S2",
        "Homosexual (MasterCard Source)",
        EptsReportUtils.map(rotineDenominator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "DTMSM-S2",
        "Homosexual (Mastercard Source)",
        EptsReportUtils.map(targetDenominator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "DRPWID-S2",
        "Drugs User (Mastercard Source) ",
        EptsReportUtils.map(rotineDenominator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "DTPWID-S2",
        "Drugs User (Mastercard Source)",
        EptsReportUtils.map(targetDenominator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "DRPRI-S2",
        "Prisioners (Mastercard Source)",
        EptsReportUtils.map(rotineDenominator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "DTPRI-S2",
        "Prisioners (Mastercard Source)",
        EptsReportUtils.map(targetDenominator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "DRFSW-S2",
        "Sex Worker (Mastercard Source)",
        EptsReportUtils.map(rotineDenominator, mappings),
        "gender=F|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "DTFSW-S2",
        "Sex Worker (Mastercard Source)",
        EptsReportUtils.map(targetDenominator, mappings),
        "gender=F|sex-worker=sex-worker");

    // Key population collumn Numerator

    final CohortIndicator rotineNumerator =
        this.eptsGeneralIndicator.getIndicator(
            "rotine",
            EptsReportUtils.map(
                pvlsBySourceCohortQueries
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine(
                        SourceType.MASTERCARD),
                mappings));

    final CohortIndicator targetNumerator =
        this.eptsGeneralIndicator.getIndicator(
            "target",
            EptsReportUtils.map(
                pvlsBySourceCohortQueries
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(
                        SourceType.MASTERCARD),
                mappings));

    dataSetDefinition.addColumn(
        "NRPWID-S2",
        "Drugs User (Mastercard Source)",
        EptsReportUtils.map(rotineNumerator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "NTPWID-S2",
        "Drugs User (Mastercard Source)",
        EptsReportUtils.map(targetNumerator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "NRMSM-S2",
        "Homosexual (Mastercard Source)",
        EptsReportUtils.map(rotineNumerator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "NTMSM-S2",
        "Homosexual (Mastercard Source)",
        EptsReportUtils.map(targetNumerator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "NRFSW-S2",
        "Sex Worker (Mastercard Source)",
        EptsReportUtils.map(rotineNumerator, mappings),
        "gender=F|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "NTFSW-S2",
        "Sex Worker (Mastercard Source)",
        EptsReportUtils.map(targetNumerator, mappings),
        "gender=F|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "NRPRI-S2",
        "Prisioners (Mastercard Source)",
        EptsReportUtils.map(rotineNumerator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "NTPRI-S2",
        "Prisioners (Mastercard Source)",
        EptsReportUtils.map(targetNumerator, mappings),
        "prisioner=prisioner");

    return dataSetDefinition;
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
