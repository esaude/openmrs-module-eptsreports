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
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXTBCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxTBDataset;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupMERSemiAnnualReport extends SetupMERQuarterly {

  @Autowired private TxTBDataset txTBDataset;
  @Autowired private TXTBCohortQueries txtBCohortQueries;

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "00812f4a-af74-4227-8698-d46cd7a21ef0";
  }

  @Override
  public String getExcelDesignUuid() {
    return "00812f4a-af74-4227-8698-d46cd7a21ef1";
  }

  @Override
  public String getName() {
    return "MER Semi Annual";
  }

  @Override
  public String getDescription() {
    return "MERSemiAnnual - PEPFAR MER 2.1";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition reportDefinition = super.constructReportDefinition();

    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            txtBCohortQueries.getPatientsEnrolledInARTCareAndOnTreatment(),
            "endDate=${endDate},location=${location}"));
    reportDefinition.addDataSetDefinition(
        "T", Mapped.mapStraightThrough(txTBDataset.constructTxTBDataset()));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "PEPFAR_MER_2.1_REPORT.xls",
              "PEPFAR Semi annual Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.getMessage());
    }

    return Arrays.asList(reportDesign);
  }
}
