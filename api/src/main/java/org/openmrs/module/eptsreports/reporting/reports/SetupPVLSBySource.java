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
import org.openmrs.module.eptsreports.reporting.library.datasets.TxPvlsByLabAndFSRSourceDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxPvlsByMasterCardSourceDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxRttDataset;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupPVLSBySource extends EptsDataExportManager {

  @Autowired private TxPvlsByLabAndFSRSourceDataset txPvlsByLabAndFSRSourceDataset;

  @Autowired private TxPvlsByMasterCardSourceDataset txPvlsByMasterCardSourceDataset;

  @Autowired private TxRttDataset txRttDataset;

  @Autowired private DatinCodeDataSet DatinCodeDataSet;

  @Autowired protected GenericCohortQueries genericCohortQueries;

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "1461611a-1ad7-4a03-b042-8646c0ac5ea3";
  }

  @Override
  public String getExcelDesignUuid() {
    return "1461611a-1ad7-4a03-b042-8646c0ac5ea3";
  }

  @Override
  public String getName() {
    return "TX_PVLS by Sources Report";
  }

  @Override
  public String getDescription() {
    return "This report generates the aggregate numbers of ART patients with suppressed viral load result documented only in the medical record sources and the aggregate numbers of ART patients with suppressed viral load result documented document only in the Laboratory Record sources ";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.txRttDataset.getParameters());

    reportDefinition.addDataSetDefinition(
        "PS1",
        Mapped.mapStraightThrough(this.txPvlsByLabAndFSRSourceDataset.constructTxPvlsDatset()));

    reportDefinition.addDataSetDefinition(
        "PS2",
        Mapped.mapStraightThrough(this.txPvlsByMasterCardSourceDataset.constructTxPvlsDatset()));

    reportDefinition.addDataSetDefinition(
        "D",
        Mapped.mapStraightThrough(this.DatinCodeDataSet.constructDataset(this.getParameters())));

    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "TX_PVLS_by_Source.xls",
              "TX_PVLS by Sources Report",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
