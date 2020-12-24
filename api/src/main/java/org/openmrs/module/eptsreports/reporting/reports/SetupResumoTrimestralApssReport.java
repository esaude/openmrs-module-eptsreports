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
import org.openmrs.module.eptsreports.reporting.library.datasets.resumo.ResumoTrimestralApssDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupResumoTrimestralApssReport extends EptsDataExportManager {

  private GenericCohortQueries genericCohortQueries;

  private ResumoTrimestralApssDataSetDefinition resumoMensTrimestralApssDataSetDefinition;

  @Autowired
  public SetupResumoTrimestralApssReport(
      GenericCohortQueries genericCohortQueries,
      ResumoTrimestralApssDataSetDefinition resumoMensTrimestralApssDataSetDefinition) {
    this.genericCohortQueries = genericCohortQueries;
    this.resumoMensTrimestralApssDataSetDefinition = resumoMensTrimestralApssDataSetDefinition;
  }

  @Override
  public String getExcelDesignUuid() {
    return "225c3224-a928-4b70-98ac-c71627366c14";
  }

  @Override
  public String getUuid() {
    return "1a50f7ec-0355-4c02-b8ad-2faebd35eb6f";
  }

  @Override
  public String getName() {
    return "Resumo Trimestral das APSS e PP - MISAU";
  }

  @Override
  public String getDescription() {
    return "Resumo Trimestral das APSS e PP - MISAU";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(resumoMensTrimestralApssDataSetDefinition.getParameters());
    rd.addDataSetDefinition("HF", mapStraightThrough(new LocationDataSetDefinition()));
    rd.addDataSetDefinition(
        "R",
        mapStraightThrough(
            resumoMensTrimestralApssDataSetDefinition.constructResumoTrimestralApssDataset()));
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
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
              "Relatorio_trimestral_apss.xls",
              "Relatorio Trimestral APSS Report",
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
