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
import org.openmrs.module.eptsreports.reporting.library.datasets.MISAUKeyPopsDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupMISAUKeyPopsReport extends EptsDataExportManager {

  private MISAUKeyPopsDataSetDefinition mISAUKeyPopsDataSetDefinition;

  private GenericCohortQueries genericCohortQueries;

  @Autowired
  public SetupMISAUKeyPopsReport(
      MISAUKeyPopsDataSetDefinition mISAUKeyPopsDataSetDefinition,
      GenericCohortQueries genericCohortQueries) {
    this.mISAUKeyPopsDataSetDefinition = mISAUKeyPopsDataSetDefinition;
    this.genericCohortQueries = genericCohortQueries;
  }

  @Override
  public String getExcelDesignUuid() {
    return "4e227c85-2270-11eb-b9a1-0242ac120002";
  }

  @Override
  public String getUuid() {
    return "55da64fe-2270-11eb-b9a1-0242ac120002";
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
    rd.addParameters(mISAUKeyPopsDataSetDefinition.getParameters());
    rd.addDataSetDefinition("HF", mapStraightThrough(new LocationDataSetDefinition()));
    rd.addDataSetDefinition(
        "R", mapStraightThrough(mISAUKeyPopsDataSetDefinition.constructMISAUKeyPopsDataset()));
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));
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
              "Relatorio_de_Populacao_Chave_-_MISAUv2.xls",
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
