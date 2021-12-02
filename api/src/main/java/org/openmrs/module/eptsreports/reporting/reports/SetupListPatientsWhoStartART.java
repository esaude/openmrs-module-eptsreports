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
import org.openmrs.module.eptsreports.reporting.library.datasets.txnew.ListOfPatientWhoStartArtDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.txnew.SummaryPatientWhoStartArtDataSet;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupListPatientsWhoStartART extends EptsDataExportManager {

  @Autowired private ListOfPatientWhoStartArtDataSet txNew;
  @Autowired SummaryPatientWhoStartArtDataSet summaryPatientWhoStartArtDataSet;

  @Override
  public String getExcelDesignUuid() {
    return "d34d80ae-fbad-11eb-9a03-0242ac130007";
  }

  @Override
  public String getUuid() {
    return "dce1c0b2-fbad-11eb-9a03-0242ac130009";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES QUE INICIARAM TARV";
  }

  @Override
  public String getDescription() {
    return "LISTA DE PACIENTES QUE INICIARAM TARV";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(this.getParameters());

    rd.addDataSetDefinition(
        "NR", Mapped.mapStraightThrough(txNew.constructDataset(getParameters())));

    rd.addDataSetDefinition(
        "RT", Mapped.mapStraightThrough(summaryPatientWhoStartArtDataSet.getTotaStartARTDataset()));

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
              "LISTA_DE_PACIENTES_QUE_INICIARAM_TARV.xls",
              "LISTA_DE_PACIENTES_QUE_INICIARAM_TARV",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:NR");
      props.put("sortWeight", "5000");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }

  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("startDate", "Cohort Start Date", Date.class),
        new Parameter("endDate", "  Cohort End Date", Date.class),
        new Parameter("evaluationDate", "Evaluation Date", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class));
  }
}
