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
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class SetupTxCurr extends EptsDataExportManager {

  @Autowired private TxCurrDataset txCurrDataset;

  @Autowired private GenericCohortQueries genericCohortQueries;

  public SetupTxCurr() {}

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "0c35dabb-a0da-429b-92f8-fcf8e0977be2";
  }

  @Override
  public String getExcelDesignUuid() {
    return "38e7ccad-c199-45b8-91a0-c6a694c52b60";
  }

  @Override
  public String getName() {
    return "TX_CURR Report";
  }

  @Override
  public String getDescription() {
    return "Number of adults and children currently receiving antiretroviral therapy (ART).";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(getUuid());
    reportDefinition.setName(getName());
    reportDefinition.setDescription(getDescription());
    reportDefinition.setParameters(txCurrDataset.getParameters());

    reportDefinition.addDataSetDefinition(
        txCurrDataset.constructTxCurrDataset(true),
        ParameterizableUtil.createParameterMappings(
            "endDate=${endDate},startDate=${startDate},location=${location}"));
    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));
    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition, "TXCURR.xls", "TXCURR.xls_", getExcelDesignUuid(), null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,dataset:TX_CURR Data Set");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return Arrays.asList(reportDesign);
  }
}
