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

  public DataSetDefinition constructTxRttDataset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings =
        "startDate=${startDate},endDate=${endDate},location=${location},months=${months}";
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    dsd.setName("TX_RTT Dataset");
    dsd.addParameters(getParameters());
    return dsd;
  }

  private List<ColumnParameters> dissagChildren() {
    // children Male
    ColumnParameters under1YearM =
        new ColumnParameters("under1YearM", "<1 Male", "gender=M|age=<1", "01");
    ColumnParameters oneT04YearsM =
        new ColumnParameters("oneT04YearsM", "1-4 Male", "gender=M|age=1-4", "02");
    ColumnParameters fiveT09YearsM =
        new ColumnParameters("fiveT09YearsM", "5-9 Male", "gender=M|age=5-9", "03");
    ColumnParameters tenT014YearsM =
        new ColumnParameters("tenT014YearsM", "10-14 Male", "gender=M|age=10-14", "04");

    // Children Female
    ColumnParameters under1YearF =
        new ColumnParameters("under1YearF", "<1 Female", "gender=F|age=<1", "05");
    ColumnParameters oneT04YearsF =
        new ColumnParameters("oneT04YearsF", "1-4 Female", "gender=F|age=1-4", "06");
    ColumnParameters fiveT09YearsF =
        new ColumnParameters("fiveT09YearsF", "5-9 Female", "gender=F|age=5-9", "07");
    ColumnParameters tenT014YearsF =
        new ColumnParameters("tenT014YearsF", "10-14 Female", "gender=F|age=10-14", "08");

    // Adults Male

    // Adults Female

    return Arrays.asList(
        under1YearM,
        oneT04YearsM,
        fiveT09YearsM,
        tenT014YearsM,
        under1YearF,
        oneT04YearsF,
        fiveT09YearsF,
        tenT014YearsF);
  }
}
