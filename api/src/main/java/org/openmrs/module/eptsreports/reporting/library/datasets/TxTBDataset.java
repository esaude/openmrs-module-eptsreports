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
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXTBCohortQueries;
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
public class TxTBDataset extends BaseDataSet {
  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TXTBCohortQueries txTbCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructTxTBDataset() {
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TX_TB Data Set");
    dataSetDefinition.addParameters(getParameters());

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    addTXTBNumerator(mappings, dataSetDefinition);

    addTXTBDenominator(mappings, dataSetDefinition);

    return dataSetDefinition;
  }

  private void addTXTBNumerator(
      String mappings, CohortIndicatorDataSetDefinition dataSetDefinition) {
    CohortIndicator numerator =
        eptsGeneralIndicator.getIndicator(
            "NUMERATOR", EptsReportUtils.map(txTbCohortQueries.txTbNumerator(), mappings));
    dataSetDefinition.addColumn(
        "TXB_NUM", "TX_TB: Notified TB On ART", EptsReportUtils.map(numerator, mappings), "");
    dataSetDefinition.addColumn(
        "TXB",
        "TX_TB: On ART",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "getNotifiedTBPatientsAtARVService",
                EptsReportUtils.map(
                    txTbCohortQueries.getNotifiedTBPatientsAtARVService(), mappings)),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "TXB_NUM_NEW",
        "TX_TB: Numerator new",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "notifiedTbPatientsOnARVNewStarting",
                EptsReportUtils.map(
                    txTbCohortQueries.notifiedTbPatientsOnARVNewStarting(), mappings)),
            mappings),
        "");
    addRow(
        dataSetDefinition,
        "TXB_NUM",
        "TXB_NUM: Numerator - disaggregated",
        EptsReportUtils.map(numerator, mappings),
        dissagregations());
  }

  private void addTXTBDenominator(
      String mappings, CohortIndicatorDataSetDefinition dataSetDefinition) {
    CohortIndicator denominator =
        eptsGeneralIndicator.getIndicator(
            "DENOMINATOR", EptsReportUtils.map(txTbCohortQueries.txTbDenominator(), mappings));
    dataSetDefinition.addColumn(
        "TXB_DEN", "TX_TB: Denominator", EptsReportUtils.map(denominator, mappings), "");
    dataSetDefinition.addColumn(
        "TXB_DEN_POS",
        "TX_TB: Denominator - Screened Positive",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patientsOnARTWhoScreenedTBPositiveForAPeriod",
                EptsReportUtils.map(
                    txTbCohortQueries.patientsOnARTWhoScreenedTBPositiveForAPeriod(), mappings)),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "TXB_DEN_NEG",
        "TX_TB: Denominator - Screened Negative",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patientsOnARTWhoScreenedTBNegativeForAPeriod",
                EptsReportUtils.map(
                    txTbCohortQueries.patientsOnARTWhoScreenedTBNegativeForAPeriod(), mappings)),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "TXB_DEN_POS_NEW",
        "TX_TB: Denominator - Newly Screened Positive",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patientsWithPositiveTBTrialNotTransferredOut",
                EptsReportUtils.map(
                    txTbCohortQueries.patientsWithPositiveTBTrialNotTransferredOut(), mappings)),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "TXB_DEN_NEG_NEW",
        "TX_TB: Denominator - Newly Screened Negative",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patientsWithNegativeTBTrialNotTransferredOut",
                EptsReportUtils.map(
                    txTbCohortQueries.patientsWithNegativeTBTrialNotTransferredOut(), mappings)),
            mappings),
        "");
    addRow(
        dataSetDefinition,
        "TXB_DEN",
        "TXB_DEN: Denominator - disaggregated",
        EptsReportUtils.map(denominator, mappings),
        dissagregations());
  }

  private List<ColumnParameters> dissagregations() {
    return Arrays.asList(
        new ColumnParameters("<15Females", "<15 anos - Feminino", "gender=F|age=<15", "F1"),
        new ColumnParameters(">=15Females", "15+ anos Feminino", "gender=F|age=15+", "F2"),
        new ColumnParameters("UnknownFemales", "Unknown anos Feminino", "gender=F|age=UK", "UK1"),
        new ColumnParameters("<15Males", "<15 anos - Masculino", "gender=M|age=<15", "M1"),
        new ColumnParameters(">=15Males", "15+ anos Masculino", "gender=M|age=15+", "M2"),
        new ColumnParameters("UnknownMales", "Unknown anos Masculino", "gender=M|age=UK", "UK2"));
  }
}
