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
import org.openmrs.module.eptsreports.reporting.library.datasets.TxRttDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Deprecated
public class SetupRTTQuarterlyReport extends EptsDataExportManager {

  private TxRttDataset txRttDataset;
  private GenericCohortQueries genericCohortQueries;

  @Autowired
  public SetupRTTQuarterlyReport(
      TxRttDataset txRttDataset, GenericCohortQueries genericCohortQueries) {
    this.txRttDataset = txRttDataset;
    this.genericCohortQueries = genericCohortQueries;
  }

  @Override
  public String getExcelDesignUuid() {
    return "75e7bcf2-1682-11ea-8854-6bfca6323c75";
  }

  @Override
  public String getUuid() {
    return "894f63e4-1682-11ea-ad73-37edadd46854";
  }

  @Override
  public String getName() {
    return "PEPFAR MER TX_RTT Numerator";
  }

  @Override
  public String getDescription() {
    return "PEPFAR MER TX_RTT Quarterly Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(getUuid());
    reportDefinition.setName(getName());
    reportDefinition.setDescription(getDescription());
    reportDefinition.setParameters(txRttDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "R", Mapped.mapStraightThrough(txRttDataset.constructTxRttDataset()));
    // add a base cohort here to help in calculations running
    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));
    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "PEPFAR_MER_2.4_Quarterly_RTT.xls",
              "PEPFAR_MER_2.4 Quarterly Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
