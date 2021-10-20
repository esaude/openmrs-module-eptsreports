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

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.MisauKeyPopReportDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class SetupMisauKeyPopReport extends EptsDataExportManager {

  private MisauKeyPopReportDataSetDefinition misauKeyPopDataSetDefinition;

  @Autowired protected GenericCohortQueries genericCohortQueries;

  @Autowired
  public SetupMisauKeyPopReport(MisauKeyPopReportDataSetDefinition misauKeyPopDataSetDefinition) {
    this.misauKeyPopDataSetDefinition = misauKeyPopDataSetDefinition;
  }

  @Override
  public String getExcelDesignUuid() {
    return "9d9afa2f-d107-48e7-bd0a-c82a24fecdec";
  }

  @Override
  public String getUuid() {
    return "5de5a817-000e-4572-80a1-61ab83a49461";
  }

  @Override
  public String getName() {
    return "Relatorio de Populacao Chave - MISAU";
  }

  @Override
  public String getDescription() {
    return "Relatorio de Populacao Chave - MISAU";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(misauKeyPopDataSetDefinition.getParameters());
    rd.addDataSetDefinition(
        "M", mapStraightThrough(misauKeyPopDataSetDefinition.constructMisauKeyPopDataset()));
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getAdultBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));
    return rd;
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
              "Relatorio_de_Populacao_Chave_MISAU.xls",
              "Relatorio de Populacao Chave - MISAU",
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
