/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRttCohortQueries;
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
public class TxRttDataset extends BaseDataSet {

  private TxRttCohortQueries txRttCohortQueries;
  private EptsGeneralIndicator eptsGeneralIndicator;
  private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public TxRttDataset(
      TxRttCohortQueries txRttCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator,
      EptsCommonDimension eptsCommonDimension) {
    this.txRttCohortQueries = txRttCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.eptsCommonDimension = eptsCommonDimension;
  }

  /**
   * Construction of the TX retention dataset
   *
   * @return @{@link DataSetDefinition}
   */
  public DataSetDefinition constructTxRttDataset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    dsd.addDimension(
        "KP",
        EptsReportUtils.map(
            eptsCommonDimension.getKeyPopsDimension(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    dsd.setName("R");
    dsd.addParameters(getParameters());
    addRow(
        dsd,
        "RTT",
        "Patients who missed appointment but later showed up for a visit",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients who missed appointment but later showed up for a visit",
                EptsReportUtils.map(txRttCohortQueries.getRTTComposition(), mappings)),
            mappings),
        dissagChildrenAndAdultsAndKeyPop());

    CohortIndicator RTT90 =
        eptsGeneralIndicator.getIndicator(
            "RTT90",
            EptsReportUtils.map(
                txRttCohortQueries.treatmentInterruptionOfXDays(null, 90), mappings));

    dsd.addColumn(
        "RTT90",
        "Experienced treatment interruption of <3 months (less than 90 days)  before returning to treatment",
        EptsReportUtils.map(RTT90, mappings),
        "");

    CohortIndicator RTT180 =
        eptsGeneralIndicator.getIndicator(
            "RTT180",
            EptsReportUtils.map(
                txRttCohortQueries.treatmentInterruptionOfXDays(180, null), mappings));

    dsd.addColumn(
        "RTT180",
        "Experienced treatment interruption of 6+ months (>=180 days) before returning to treatment",
        EptsReportUtils.map(RTT180, mappings),
        "");

    CohortIndicator RTT90180 =
        eptsGeneralIndicator.getIndicator(
            "RTT90180",
            EptsReportUtils.map(
                txRttCohortQueries.treatmentInterruptionOfXDays(90, 180), mappings));

    dsd.addColumn(
        "RTT90180",
        "Experienced treatment interruption of 3-5 months (>= 90 days and <180 days) before returning to treatment",
        EptsReportUtils.map(RTT90180, mappings),
        "");

    return dsd;
  }

  private List<ColumnParameters> dissagChildrenAndAdultsAndKeyPop() {
    // children Male
    ColumnParameters under1YearM =
        new ColumnParameters("under1YearM", "<1 Male", "gender=M|age=<1", "01");
    ColumnParameters oneTo4YearsM =
        new ColumnParameters("oneTo4YearsM", "1-4 Male", "gender=M|age=1-4", "02");
    ColumnParameters fiveTo9YearsM =
        new ColumnParameters("fiveTo9YearsM", "5-9 Male", "gender=M|age=5-9", "03");
    ColumnParameters tenTo14YearsM =
        new ColumnParameters("tenTo14YearsM", "10-14 Male", "gender=M|age=10-14", "04");

    // Adults Male
    // 15-19, 20-24,25-29,30-34,35-39,40-44,45-49, >=50, Unknown age
    ColumnParameters fifteenTo19YearsM =
        new ColumnParameters("fifteenTo19YearsM", "15-19 Male", "gender=M|age=15-19", "05");
    ColumnParameters twentyTo24YearsM =
        new ColumnParameters("twentyTo24YearsM", "20 - 24 male", "gender=M|age=20-24", "06");
    ColumnParameters twenty5To29M =
        new ColumnParameters("twenty4To29M", "25 - 29 male", "gender=M|age=25-29", "07");
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
    ColumnParameters totalM = new ColumnParameters("totalM", "Total  male", "gender=M", "41");

    // Children Female
    ColumnParameters under1YearF =
        new ColumnParameters("under1YearF", "<1 Female", "gender=F|age=<1", "17");
    ColumnParameters oneTo4YearsF =
        new ColumnParameters("oneTo4YearsF", "1-4 Female", "gender=F|age=1-4", "18");
    ColumnParameters fiveTo9YearsF =
        new ColumnParameters("fiveTo9YearsF", "5-9 Female", "gender=F|age=5-9", "19");
    ColumnParameters tenTo14YearsF =
        new ColumnParameters("tenTo14YearsF", "10-14 Female", "gender=F|age=10-14", "20");

    // Adults Female
    // 15-19, 20-24,25-29,30-34,35-39,40-44,45-49, >=50, Unknown age
    ColumnParameters fifteenTo19YearsF =
        new ColumnParameters("fifteenTo19YearsF", "15-19 female", "gender=F|age=15-19", "21");
    ColumnParameters twentyTo24YearsF =
        new ColumnParameters("twentyTo24YearsF", "20 - 24 female", "gender=F|age=20-24", "22");
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
    ColumnParameters totalF = new ColumnParameters("totalF", "Total  female", "gender=F", "42");

    // Key population
    ColumnParameters pid = new ColumnParameters("pid", "PID", "KP=PID", "33");
    ColumnParameters msm = new ColumnParameters("msm", "MSM", "KP=MSM", "34");
    ColumnParameters csw = new ColumnParameters("csw", "CSW", "KP=CSW", "35");
    ColumnParameters pri = new ColumnParameters("pri", "PRI", "KP=PRI", "36");

    // Getting the totals
    ColumnParameters totals = new ColumnParameters("totals", "Total", "", "40");

    return Arrays.asList(
        under1YearM,
        oneTo4YearsM,
        fiveTo9YearsM,
        tenTo14YearsM,
        under1YearF,
        oneTo4YearsF,
        fiveTo9YearsF,
        tenTo14YearsF,
        fifteenTo19YearsM,
        twentyTo24YearsM,
        twenty5To29M,
        thirtyTo34M,
        thirty5To39M,
        fortyTo44M,
        forty5To49M,
        fiftyTo54M,
        fifty5To59M,
        sixtyTo64M,
        above65M,
        unknownM,
        fifteenTo19YearsF,
        twentyTo24YearsF,
        twenty5To29F,
        thirtyTo34F,
        thirty5To39F,
        fortyTo44F,
        forty5To49F,
        fiftyTo54F,
        fifty5To59F,
        sixtyTo64F,
        above65F,
        unknownF,
        pid,
        msm,
        csw,
        pri,
        totals,
        totalM,
        totalF);
  }
}
