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
import org.openmrs.module.eptsreports.reporting.library.datasets.LocationDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.resumo.ResumoTrimestralDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupResumoTrimestralReport extends EptsDataExportManager {

  private ResumoTrimestralDataSetDefinition resumoTrimestralDataSetDefinition;

  private GenericCohortQueries genericCohortQueries;

  @Autowired
  public SetupResumoTrimestralReport(
      ResumoTrimestralDataSetDefinition resumoTrimestralDataSetDefinition,
      GenericCohortQueries genericCohortQueries) {
    this.resumoTrimestralDataSetDefinition = resumoTrimestralDataSetDefinition;
    this.genericCohortQueries = genericCohortQueries;
  }

  @Override
  public String getExcelDesignUuid() {
    return "5c04a7b8-371d-445c-b075-5358dc07ef7f";
  }

  @Override
  public String getUuid() {
    return "0c64c016-6b09-4ac4-bb6d-bdacd77988ea";
  }

  @Override
  public String getName() {
    return "Resumo Trimestral das Coortes de Tratamento Antirretroviral";
  }

  @Override
  public String getDescription() {
    return "Resumo Trimestral das Coortes de Tratamento Antirretroviral";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(resumoTrimestralDataSetDefinition.getParameters());
    rd.addDataSetDefinition("HF", mapStraightThrough(new LocationDataSetDefinition()));
    rd.addDataSetDefinition(
        "T",
        mapStraightThrough(resumoTrimestralDataSetDefinition.constructResumoTrimestralDataset()));

    // rd.setBaseCohortDefinition(
    // EptsReportUtils.map(
    // this.genericCohortQueries.generalSql(
    // "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
    // "endDate=${endDate},location=${location}"));
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
              "Relatorio_Trimestral.xls",
              "Relatorio Trimestral Report",
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
