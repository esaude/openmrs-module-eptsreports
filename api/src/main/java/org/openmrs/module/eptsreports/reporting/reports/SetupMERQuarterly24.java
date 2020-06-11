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
package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCurrDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxMlDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxNewDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxPvlsDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxRttDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupMERQuarterly24 extends EptsDataExportManager {

  private TxPvlsDataset txPvlsDataset;

  private TxNewDataset txNewDataset;

  private TxCurrDataset txCurrDataset;

  private TxMlDataset txMlDataset;

  private TxRttDataset txRttDataset;

  private GenericCohortQueries genericCohortQueries;

  @Autowired
  public SetupMERQuarterly24(
      TxPvlsDataset txPvlsDataset,
      TxNewDataset txNewDataset,
      TxCurrDataset txCurrDataset,
      TxMlDataset txMlDataset,
      TxRttDataset txRttDataset,
      GenericCohortQueries genericCohortQueries) {
    this.txPvlsDataset = txPvlsDataset;
    this.txNewDataset = txNewDataset;
    this.txCurrDataset = txCurrDataset;
    this.txMlDataset = txMlDataset;
    this.txRttDataset = txRttDataset;
    this.genericCohortQueries = genericCohortQueries;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "b57c8c7a-27db-11ea-a2eb-cfdcb6608edf";
  }

  @Override
  public String getExcelDesignUuid() {
    return "d8b5de76-27db-11ea-b629-8f1716d378b1";
  }

  @Override
  public String getName() {
    return "PEPFAR MER 2.4 Quarterly";
  }

  @Override
  public String getDescription() {
    return "MER 2.4 Quarterly Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(txPvlsDataset.getParameters());
    rd.addDataSetDefinition("N", Mapped.mapStraightThrough(txNewDataset.constructTxNewDataset()));
    rd.addDataSetDefinition(
        "C", Mapped.mapStraightThrough(txCurrDataset.constructTxCurrDataset(true)));
    rd.addDataSetDefinition("P", Mapped.mapStraightThrough(txPvlsDataset.constructTxPvlsDatset()));
    rd.addDataSetDefinition("TXML", Mapped.mapStraightThrough(txMlDataset.constructtxMlDataset()));
    rd.addDataSetDefinition("R", Mapped.mapStraightThrough(txRttDataset.constructTxRttDataset()));
    //  add a base cohort here to help in calculations running
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));

    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign rd = null;
    try {
      rd =
          createXlsReportDesign(
              reportDefinition,
              "PEPFAR_MER_2.4_Quarterly_sheets.xls",
              "PEPFAR MER 2.4 Quarterly Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      rd.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(rd);
  }
}
