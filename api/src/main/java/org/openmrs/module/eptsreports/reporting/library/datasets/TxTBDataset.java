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
  @Qualifier("txNewAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructTxTBDataset() {
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TX_TB Data Set");
    dataSetDefinition.addParameters(getParameters());

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "query", EptsReportUtils.map(eptsCommonDimension.maternityDimension(), mappings));
    dataSetDefinition.addDimension(
        "age", EptsReportUtils.map(eptsCommonDimension.age(ageDimensionCohort), mappings));
    CohortIndicator notifiedTbPatientsOnARTService =
        eptsGeneralIndicator.getIndicator(
            "notifiedTbPatientsOnARTService",
            EptsReportUtils.map(txTbCohortQueries.notifiedTbPatientsOnARTService(), mappings));
    dataSetDefinition.addColumn(
        "TXB",
        "TX_TB_NUM: Notified TB On ART",
        EptsReportUtils.map(notifiedTbPatientsOnARTService, mappings),
        "");
    addRow(
        dataSetDefinition,
        "TXBD",
        "TX_TB_NUM - disaggregated",
        EptsReportUtils.map(notifiedTbPatientsOnARTService, mappings),
        dissagregations());

    return dataSetDefinition;
  }

  private List<ColumnParameters> dissagregations() {
    return Arrays.asList(
        new ColumnParameters("0-14Females", "0-14 anos - Feminino", "gender=F|age=0-14", "F1"),
        new ColumnParameters(">=15Females", "15+ anos Feminino", "gender=F|age=15+", "F2"),
        new ColumnParameters("0-14Males", "0-14 anos - Masculino", "gender=M|age=0-14", "M1"),
        new ColumnParameters(">=15Males", "15+ anos Masculino", "gender=M|age=15+", "M2"));
  }
}
