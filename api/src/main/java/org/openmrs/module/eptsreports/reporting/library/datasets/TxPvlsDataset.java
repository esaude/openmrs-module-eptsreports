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
import org.openmrs.module.eptsreports.reporting.library.cohorts.PvlsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.KeyPopulationDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TxPvlsDataset extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private PvlsCohortQueries pvlsCohortQueries;

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
        "PVLSTOTAL",
        "Total patients with Viral load - Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients with viral load",
                EptsReportUtils.map(
                    this.pvlsCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
                    mappings)),
            mappings),
        "");

    addRow(
        dataSetDefinition,
        "DR",
        "Adults & Children Denominator Routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children",
                EptsReportUtils.map(
                    this.pvlsCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    addRow(
        dataSetDefinition,
        "DT",
        "Adults & Children Denominator Routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children",
                EptsReportUtils.map(
                    this.pvlsCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    dataSetDefinition.addColumn(
        "DPREGROTINE",
        "Pregant routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant routine",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "DPREGTARGET",
        "pregnant target",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant target",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "DBREASROTINE",
        "Breastfeeding routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breastfeeding routine",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "DBREASTARGET",
        "Breastfeeding target",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breastfeeding target",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NRTOTAL",
        "Total patients with Viral load - numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients with viral load",
                EptsReportUtils.map(
                    this.pvlsCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(),
                    mappings)),
            mappings),
        "");

    addRow(
        dataSetDefinition,
        "NR",
        "Adults & Children Numerator Routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children",
                EptsReportUtils.map(
                    this.pvlsCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    addRow(
        dataSetDefinition,
        "NT",
        "Adults & Children Numerator target",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "viral load results on routine adults and children",
                EptsReportUtils.map(
                    this.pvlsCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(),
                    mappings)),
            mappings),
        getAdultChildrenColumns());

    dataSetDefinition.addColumn(
        "NPREGROTINE",
        "Pregant routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant routine",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Rotine(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NPREGTARGET",
        "Pregant target",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant target",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Target(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NBREASROTINE",
        "Breastfeeding routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breastfeeding routine",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPregnantBreatsFeedingWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Rotine(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NBREASTARGET",
        "Breastfeeding target",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breastfeeding target",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPregnantBreatsFeedingWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Target(),
                    mappings)),
            mappings),
        "");

    // Add SubTotal Denominator

    dataSetDefinition.addColumn(
        "DRSUBTOTAL",
        "Rotine Sub Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Rotine Sub Total",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "DTSUBTOTAL",
        "Target Sub Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Target Sub Total",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(),
                    mappings)),
            mappings),
        "");

    // Add SubTotal Numerator

    dataSetDefinition.addColumn(
        "NRSUBTOTAL",
        "Rotine Numerator Sub Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Rotine Numerator Sub Total",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NTSUBTOTAL",
        "Target Numerator Sub Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Target Numerator Sub Total",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(),
                    mappings)),
            mappings),
        "");

    // Kay Population dimension

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
                pvlsCohortQueries
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(),
                mappings));

    final CohortIndicator targetDenominator =
        this.eptsGeneralIndicator.getIndicator(
            "target",
            EptsReportUtils.map(
                pvlsCohortQueries
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(),
                mappings));

    dataSetDefinition.addColumn(
        "DRMSM",
        "Homosexual",
        EptsReportUtils.map(rotineDenominator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "DTMSM",
        "Homosexual",
        EptsReportUtils.map(targetDenominator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "DRPWID",
        "Drugs User",
        EptsReportUtils.map(rotineDenominator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "DTPWID",
        "Drugs User",
        EptsReportUtils.map(targetDenominator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "DRPRI",
        "Prisioners",
        EptsReportUtils.map(rotineDenominator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "DTPRI",
        "Prisioners",
        EptsReportUtils.map(targetDenominator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "DRFSW",
        "Sex Worker",
        EptsReportUtils.map(rotineDenominator, mappings),
        "gender=F|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "DTFSW",
        "Sex Worker",
        EptsReportUtils.map(targetDenominator, mappings),
        "gender=F|sex-worker=sex-worker");

    // Key population collumn Numerator

    final CohortIndicator rotineNumerator =
        this.eptsGeneralIndicator.getIndicator(
            "rotine",
            EptsReportUtils.map(
                pvlsCohortQueries
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine(),
                mappings));

    final CohortIndicator targetNumerator =
        this.eptsGeneralIndicator.getIndicator(
            "target",
            EptsReportUtils.map(
                pvlsCohortQueries
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(),
                mappings));

    dataSetDefinition.addColumn(
        "NRPWID",
        "Drugs User",
        EptsReportUtils.map(rotineNumerator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "NTPWID",
        "Drugs User",
        EptsReportUtils.map(targetNumerator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "NRMSM",
        "Homosexual",
        EptsReportUtils.map(rotineNumerator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "NTMSM",
        "Homosexual",
        EptsReportUtils.map(targetNumerator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "NRFSW",
        "Sex Worker",
        EptsReportUtils.map(rotineNumerator, mappings),
        "gender=F|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "NTFSW",
        "Sex Worker",
        EptsReportUtils.map(targetNumerator, mappings),
        "gender=F|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "NRPRI",
        "Prisioners",
        EptsReportUtils.map(rotineNumerator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "NTPRI",
        "Prisioners",
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
