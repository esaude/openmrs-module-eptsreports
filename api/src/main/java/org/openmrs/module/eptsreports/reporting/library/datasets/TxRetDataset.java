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
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXRetCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.TxRetDimensionCohort;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TxRetDataset extends BaseDataSet {
  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TXRetCohortQueries txRetCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired private TxRetDimensionCohort txRetDimensionCohort;

  public DataSetDefinition constructTxRetDataset() {
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TX_Ret Data Set");
    dataSetDefinition.addParameters(getParameters());
    CohortIndicator numerator =
        eptsGeneralIndicator.getIndicator(
            "NUMERATOR",
            EptsReportUtils.map(txRetCohortQueries.inCourtForTwelveMonths(), mappings));
    CohortIndicator denominator =
        eptsGeneralIndicator.getIndicator(
            "DENOMINATOR",
            EptsReportUtils.map(txRetCohortQueries.courtNotTransferredTwelveMonths(), mappings));
    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    dataSetDefinition.addDimension(
        "0009",
        EptsReportUtils.map(
            txRetDimensionCohort.startedTargetAtARTInitiation(),
            "endDate=${endDate},location=${location}"));
    dataSetDefinition.addColumn(
        "T04SA-ALL", "TX_RET: Numerator total", EptsReportUtils.map(numerator, mappings), "");
    dataSetDefinition.addColumn(
        "T04SI-ALL", "TX_RET: Denominator total", EptsReportUtils.map(denominator, mappings), "");
    addRow(
        dataSetDefinition,
        "T04SA",
        "TX_RET Numerator (inCourtForTwelveMonths)",
        EptsReportUtils.map(numerator, mappings),
        dissagregations());
    addRow(
        dataSetDefinition,
        "T04SI",
        "TX_RET Denominator (courtNotTransferredTwelveMonths)",
        EptsReportUtils.map(numerator, mappings),
        dissagregations());
    return dataSetDefinition;
  }

  private List<ColumnParameters> dissagregations() {
    return Arrays.asList(
        new ColumnParameters("<1", "Children <1 anos", "0009=0001", "0001"),
        new ColumnParameters("1–9", "Children 1-9 anos", "0009=0109", "0109"),
        new ColumnParameters("10-14Males", "10-14 anos - Masculino", "gender=M|age=10-14", "1014M"),
        new ColumnParameters(
            "10-14Females", "10-14 anos - Feminino", "gender=F|age=10-14", "1014F"),
        new ColumnParameters("15-19Males", "15-19 anos - Masculino", "gender=M|age=15-19", "1519M"),
        new ColumnParameters(
            "15-19Females", "15-19 anos - Feminino", "gender=F|age=15-19", "1519F"),
        new ColumnParameters("20-24Males", "20-24 anos - Masculino", "gender=M|age=20-24", "2024M"),
        new ColumnParameters(
            "20-24Females", "20-24 anos - Feminino", "gender=F|age=20-24", "2024F"),
        new ColumnParameters("25-29Males", "25-29 anos - Masculino", "gender=M|age=25-29", "2529M"),
        new ColumnParameters(
            "25-29Females", "25-29 anos - Feminino", "gender=F|age=25-29", "2529F"),
        new ColumnParameters("30-34Males", "30-34 anos - Masculino", "gender=M|age=30-34", "3034M"),
        new ColumnParameters(
            "30-34Females", "30-34 anos - Feminino", "gender=F|age=30-34", "3034F"),
        new ColumnParameters("35-39Males", "35-39 anos - Masculino", "gender=M|age=35-39", "3539M"),
        new ColumnParameters(
            "35-39Females", "35-39 anos - Feminino", "gender=F|age=35-39", "3539F"),
        new ColumnParameters("40-49Males", "40-49 anos - Masculino", "gender=M|age=40-49", "4049M"),
        new ColumnParameters(
            "40-49Females", "40-49 anos - Feminino", "gender=F|age=40-49", "4049F"),
        new ColumnParameters("50+Males", "50+ anos - Masculino", "gender=M|age=50+", "50M"),
        new ColumnParameters("50+Females", "50+ anos - Feminino", "gender=F|age=50+", "50F"));
  }
}
