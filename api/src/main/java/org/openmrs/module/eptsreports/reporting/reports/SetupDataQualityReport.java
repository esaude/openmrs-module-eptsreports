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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.*;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Deprecated
public class SetupDataQualityReport extends EptsDataExportManager {

  private SummaryDataQualityDataset summaryDataQualityDataset;

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  private Ec1PatientListDataset ec1PatientListDataset;

  private Ec2PatientListDataset ec2PatientListDataset;

  private Ec3PatientListDataset ec3PatientListDataset;

  private Ec4PatientListDataset ec4PatientListDataset;

  private Ec5PatientListDataset ec5PatientListDataset;

  private Ec6PatientListDataset ec6PatientListDataset;

  private Ec7PatientListDataset ec7PatientListDataset;

  private Ec8PatientListDataset ec8PatientListDataset;

  private Ec9PatientListDataset ec9PatientListDataset;

  private Ec10PatientListDataset ec10PatientListDataset;

  private Ec11PatientListDataset ec11PatientListDataset;

  private Ec12PatientListDataset ec12PatientListDataset;

  private Ec13PatientListDataset ec13PatientListDataset;

  private Ec14PatientListDataset ec14PatientListDataset;

  private Ec15PatientListDataset ec15PatientListDataset;

  private Ec16PatientListDataset ec16PatientListDataset;

  private Ec17PatientListDataset ec17PatientListDataset;

  private Ec18PatientListDataset ec18PatientListDataset;

  private Ec19PatientListDataset ec19PatientListDataset;

  private GetCustomConfigurationDataset getCustomConfigurationDataset;

  private final HivMetadata hivMetadata;

  @Autowired
  public SetupDataQualityReport(
      SummaryDataQualityDataset summaryDataQualityDataset,
      SummaryDataQualityCohorts summaryDataQualityCohorts,
      Ec1PatientListDataset ec1PatientListDataset,
      Ec2PatientListDataset ec2PatientListDataset,
      Ec3PatientListDataset ec3PatientListDataset,
      Ec4PatientListDataset ec4PatientListDataset,
      Ec5PatientListDataset ec5PatientListDataset,
      Ec6PatientListDataset ec6PatientListDataset,
      Ec7PatientListDataset ec7PatientListDataset,
      Ec8PatientListDataset ec8PatientListDataset,
      Ec9PatientListDataset ec9PatientListDataset,
      Ec10PatientListDataset ec10PatientListDataset,
      Ec11PatientListDataset ec11PatientListDataset,
      Ec12PatientListDataset ec12PatientListDataset,
      Ec13PatientListDataset ec13PatientListDataset,
      Ec14PatientListDataset ec14PatientListDataset,
      Ec15PatientListDataset ec15PatientListDataset,
      Ec16PatientListDataset ec16PatientListDataset,
      Ec17PatientListDataset ec17PatientListDataset,
      Ec18PatientListDataset ec18PatientListDataset,
      Ec19PatientListDataset ec19PatientListDataset,
      GetCustomConfigurationDataset getCustomConfigurationDataset,
      HivMetadata hivMetadata) {
    this.summaryDataQualityDataset = summaryDataQualityDataset;
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.ec1PatientListDataset = ec1PatientListDataset;
    this.ec2PatientListDataset = ec2PatientListDataset;
    this.ec3PatientListDataset = ec3PatientListDataset;
    this.ec4PatientListDataset = ec4PatientListDataset;
    this.ec5PatientListDataset = ec5PatientListDataset;
    this.ec6PatientListDataset = ec6PatientListDataset;
    this.ec7PatientListDataset = ec7PatientListDataset;
    this.ec8PatientListDataset = ec8PatientListDataset;
    this.ec9PatientListDataset = ec9PatientListDataset;
    this.ec10PatientListDataset = ec10PatientListDataset;
    this.ec11PatientListDataset = ec11PatientListDataset;
    this.ec12PatientListDataset = ec12PatientListDataset;
    this.ec13PatientListDataset = ec13PatientListDataset;
    this.ec14PatientListDataset = ec14PatientListDataset;
    this.ec15PatientListDataset = ec15PatientListDataset;
    this.ec16PatientListDataset = ec16PatientListDataset;
    this.ec17PatientListDataset = ec17PatientListDataset;
    this.ec18PatientListDataset = ec18PatientListDataset;
    this.ec19PatientListDataset = ec19PatientListDataset;
    this.hivMetadata = hivMetadata;
    this.getCustomConfigurationDataset = getCustomConfigurationDataset;
  }

  @Override
  public String getExcelDesignUuid() {
    return "65e364d6-9be0-11e9-a7a2-cb23750cc2df";
  }

  @Override
  public String getUuid() {
    return "82ea2326-9be0-11e9-9cee-5384d0247641";
  }

  @Override
  public String getName() {
    return "Data Quality Report";
  }

  @Override
  public String getDescription() {
    return "This report provides a line listing of patient records failing to meet certain edit checks and allows the user to review the information so the patient’s information can be corrected";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getDataParameters());

    // add a base cohort here to help in calculations running
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            summaryDataQualityCohorts.getQualityDataReportBaseCohort(),
            "startDate=${startDate},endDate=${endDate},location=${location},state=${state}"));
    // adding a data set to help us get configuration parameters
    // adding respective data sets for the reports
    rd.addDataSetDefinition(
        "S",
        Mapped.mapStraightThrough(
            summaryDataQualityDataset.constructSummaryDataQualityDatset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC1",
        Mapped.mapStraightThrough(ec1PatientListDataset.ec1DataSetDefinition(getDataParameters())));
    rd.addDataSetDefinition(
        "EC2",
        Mapped.mapStraightThrough(ec2PatientListDataset.ec2DataSetDefinition(getDataParameters())));
    rd.addDataSetDefinition(
        "EC3",
        Mapped.mapStraightThrough(
            ec3PatientListDataset.ec3PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC4",
        Mapped.mapStraightThrough(
            ec4PatientListDataset.ec4PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC5",
        Mapped.mapStraightThrough(
            ec5PatientListDataset.ec5PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC6",
        Mapped.mapStraightThrough(
            ec6PatientListDataset.ec6PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC7",
        Mapped.mapStraightThrough(
            ec7PatientListDataset.ec7PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC8",
        Mapped.mapStraightThrough(
            ec8PatientListDataset.ec8PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC9",
        Mapped.mapStraightThrough(
            ec9PatientListDataset.ec9PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC10",
        Mapped.mapStraightThrough(
            ec10PatientListDataset.ec10PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC11",
        Mapped.mapStraightThrough(
            ec11PatientListDataset.ec11PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC12",
        Mapped.mapStraightThrough(
            ec12PatientListDataset.ec12PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC13",
        Mapped.mapStraightThrough(
            ec13PatientListDataset.ec13PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC14",
        Mapped.mapStraightThrough(
            ec14PatientListDataset.ec14PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC15",
        Mapped.mapStraightThrough(
            ec15PatientListDataset.ec15PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC16",
        Mapped.mapStraightThrough(
            ec16PatientListDataset.ec16PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC17",
        Mapped.mapStraightThrough(
            ec17PatientListDataset.ec17PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC18",
        Mapped.mapStraightThrough(
            ec18PatientListDataset.ec18PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC19",
        Mapped.mapStraightThrough(
            ec19PatientListDataset.ec19PatientListDataset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC01",
        Mapped.mapStraightThrough(
            getCustomConfigurationDataset.configDataSetDefinition(getDataParameters())));

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
              "Data_Quality_Report.xls",
              "Data Quality Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put(
          "repeatingSections",
          "sheet:2,row:7,dataset:EC1 | sheet:3,row:7,dataset:EC2 | sheet:4,row:7,dataset:EC3 | sheet:5,row:7,dataset:EC4 | sheet:6,row:7,dataset:EC5 | sheet:7,row:7,dataset:EC6 | sheet:8,row:7,dataset:EC7 | sheet:9,row:7,dataset:EC8 | sheet:10,row:7,dataset:EC9 | sheet:11,row:7,dataset:EC10 | sheet:12,row:7,dataset:EC11 | sheet:13,row:7,dataset:EC12 | sheet:14,row:7,dataset:EC13 | sheet:15,row:7,dataset:EC14 | sheet:16,row:7,dataset:EC15 | sheet:17,row:7,dataset:EC16 | sheet:18,row:7,dataset:EC17 | sheet:19,row:7,dataset:EC18 | sheet:20,row:7,dataset:EC19");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }

  private List<Parameter> getDataParameters() {
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(new Parameter("location", "Facilities", Location.class, List.class, null));
    parameters.add(EptsReportUtils.getProgramConfigurableParameter(hivMetadata.getARTProgram()));
    return parameters;
  }
}
