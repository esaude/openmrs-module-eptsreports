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
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.datasets.DQACargaViralDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.DQASESPDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.LocationDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupDQACargaViral extends EptsDataExportManager {

  private DQACargaViralDataset dqaCargaViralDataset;

  private DQASESPDataset dqaSESPDataset;

  @Autowired
  public SetupDQACargaViral(
      DQACargaViralDataset dqaCargaViralDataset, DQASESPDataset dqaSESPDataset) {
    this.dqaCargaViralDataset = dqaCargaViralDataset;
    this.dqaSESPDataset = dqaSESPDataset;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "8a76bb5c-4c57-11ec-81d3-0242ac130003";
  }

  @Override
  public String getExcelDesignUuid() {
    return "95901d1c-4c57-11ec-81d3-0242ac130003";
  }

  @Override
  public String getName() {
    return "DQA Carga Viral - MISAU";
  }

  @Override
  public String getDescription() {
    return "DQA Carga Viral - MISAU Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(getParameters());
    rd.addDataSetDefinition("HF", Mapped.mapStraightThrough(new LocationDataSetDefinition()));
    rd.addDataSetDefinition("DT", Mapped.mapStraightThrough(new DatimCodeDatasetDefinition()));
    rd.addDataSetDefinition(
        "CV", Mapped.mapStraightThrough(dqaCargaViralDataset.constructDQACargaViralDataset()));
    rd.addDataSetDefinition(
        "SESP", Mapped.mapStraightThrough(dqaSESPDataset.constructDQASESPDataset(true)));

    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign rd = null;
    try {
      rd =
          createXlsReportDesign(
              reportDefinition,
              "DQA_Viral_Load_v1.1.xls",
              "DQA Carga Viral - MISAU",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:CV");
      props.put("sortWeight", "5000");
      rd.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(rd);
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("startDate", "Data Inicial", Date.class),
        new Parameter("endDate", "Data Final", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class));
  }
}
