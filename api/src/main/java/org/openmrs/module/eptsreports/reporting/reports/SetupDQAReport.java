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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.LocationDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.dqa.DQAViralLoadDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.dqa.DQAViralLoadSummuryDataset;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupDQAReport extends EptsDataExportManager {

  @Autowired private DQAViralLoadDataset dQAViralLoadDataset;
  @Autowired private DQAViralLoadSummuryDataset viralLoadSummuryDataset;

  @Autowired protected GenericCohortQueries genericCohortQueries;

  @Autowired private DatinCodeDataSet datimCodeDataSet;

  @Override
  public String getExcelDesignUuid() {
    return "54981b6c-5be2-11ec-8115-ebcabc659bdf";
  }

  @Override
  public String getUuid() {
    return "5d537ae4-5be2-11ec-8a14-db09b04bb861";
  }

  @Override
  public String getName() {
    return "DQA Carga Viral - MISAU";
  }

  @Override
  public String getDescription() {
    return "DQA Carga Viral - MISAU";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(dQAViralLoadDataset.getParameters());
    rd.addDataSetDefinition("HF", mapStraightThrough(new LocationDataSetDefinition()));

    rd.addDataSetDefinition(
        "DQA", mapStraightThrough(dQAViralLoadDataset.constructDataset(getParameters())));

    rd.addDataSetDefinition("DQ", mapStraightThrough(viralLoadSummuryDataset.constructDatset()));

    rd.addDataSetDefinition(
        "D",
        Mapped.mapStraightThrough(this.datimCodeDataSet.constructDataset(this.getParameters())));

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
              "DQA_Carga_Viral_MISAU.xls",
              "DQA CARGA VIRAL MISAU",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      props.put("repeatingSections", "sheet:1,row:8,dataset:DQA");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }

  @Override
  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }
}
