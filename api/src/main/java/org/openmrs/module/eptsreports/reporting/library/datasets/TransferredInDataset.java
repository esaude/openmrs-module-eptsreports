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
import org.openmrs.module.eptsreports.reporting.library.cohorts.TransferredInCohortQueries;
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
public class TransferredInDataset extends BaseDataSet {

  private TransferredInCohortQueries transferredInCohortQueries;
  private EptsGeneralIndicator eptsGeneralIndicator;
  private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public TransferredInDataset(
      TransferredInCohortQueries transferredInCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator,
      EptsCommonDimension eptsCommonDimension) {
    this.transferredInCohortQueries = transferredInCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.eptsCommonDimension = eptsCommonDimension;
  }

  /**
   * Construction of the Transferred In dataset
   *
   * @return @{@link DataSetDefinition}
   */
  public DataSetDefinition constructTransferInDataset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    dsd.setName("R");
    dsd.addParameters(getParameters());
    addRow(
        dsd,
        "TRFIN",
        "Patients who were Transferred In",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients who were Transferred In",
                EptsReportUtils.map(
                    transferredInCohortQueries.getTransferredInPatients(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        dissagChildrenAndAdultsAndKeyPop());
    return dsd;
  }

  private List<ColumnParameters> dissagChildrenAndAdultsAndKeyPop() {
    // children Male
    ColumnParameters under1YearM =
        new ColumnParameters("under1YearM", "<1 Male", "gender=M|age=<1", "01");
    ColumnParameters oneT04YearsM =
        new ColumnParameters("oneT04YearsM", "1-4 Male", "gender=M|age=1-4", "02");
    ColumnParameters fiveT09YearsM =
        new ColumnParameters("fiveT09YearsM", "5-9 Male", "gender=M|age=5-9", "03");
    ColumnParameters tenT014YearsM =
        new ColumnParameters("tenT014YearsM", "10-14 Male", "gender=M|age=10-14", "04");

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
    ColumnParameters foutyTo44M =
        new ColumnParameters("foutyTo44M", "40 - 44 male", "gender=M|age=40-44", "10");
    ColumnParameters fouty5To49M =
        new ColumnParameters("fouty5To49M", "45 - 49 male", "gender=M|age=45-49", "11");
    ColumnParameters above50M =
        new ColumnParameters("above50M", "50+ male", "gender=M|age=50+", "12");
    ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "13");

    // Children Female
    ColumnParameters under1YearF =
        new ColumnParameters("under1YearF", "<1 Female", "gender=F|age=<1", "14");
    ColumnParameters oneT04YearsF =
        new ColumnParameters("oneT04YearsF", "1-4 Female", "gender=F|age=1-4", "15");
    ColumnParameters fiveT09YearsF =
        new ColumnParameters("fiveT09YearsF", "5-9 Female", "gender=F|age=5-9", "16");
    ColumnParameters tenT014YearsF =
        new ColumnParameters("tenT014YearsF", "10-14 Female", "gender=F|age=10-14", "17");

    // Adults Female
    // 15-19, 20-24,25-29,30-34,35-39,40-44,45-49, >=50, Unknown age
    ColumnParameters fifteenTo19YearsF =
        new ColumnParameters("fifteenTo19YearsF", "15-19 female", "gender=F|age=15-19", "18");
    ColumnParameters twentyTo24YearsF =
        new ColumnParameters("twentyTo24YearsF", "20 - 24 female", "gender=F|age=20-24", "19");
    ColumnParameters twenty5To29F =
        new ColumnParameters("twenty5To29F", "25 - 29 female", "gender=F|age=25-29", "20");
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

    // Getting subtotals
    ColumnParameters subTotalF =
        new ColumnParameters("subTotalF", "Sub Total female", "gender=F", "27");

    ColumnParameters subTotalM =
        new ColumnParameters("subTotalM", "Sub Total male", "gender=M", "28");

    // Getting the totals
    ColumnParameters totals = new ColumnParameters("totals", "Total", "", "29");

    return Arrays.asList(
        under1YearM,
        oneT04YearsM,
        fiveT09YearsM,
        tenT014YearsM,
        under1YearF,
        oneT04YearsF,
        fiveT09YearsF,
        tenT014YearsF,
        fifteenTo19YearsM,
        twentyTo24YearsM,
        twenty5To29M,
        thirtyTo34M,
        thirty5To39M,
        foutyTo44M,
        fouty5To49M,
        above50M,
        unknownM,
        fifteenTo19YearsF,
        twentyTo24YearsF,
        twenty5To29F,
        thirtyTo34F,
        thirty5To39F,
        foutyTo44F,
        fouty5To49F,
        above50F,
        unknownF,
        subTotalF,
        subTotalM,
        totals);
  }
}
