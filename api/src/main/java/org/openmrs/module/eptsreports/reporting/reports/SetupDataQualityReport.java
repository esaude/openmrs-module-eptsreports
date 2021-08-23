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
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryEc20DataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec10PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec11PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec12PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec13PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec14PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec15PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec16PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec17PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec18PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec19PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec1PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec20PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec21PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec22PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec23PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec2PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec3PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec4PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec5PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec6PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec7PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec8PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec9PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.GetCustomConfigurationDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.SummaryDataQualityDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.SummaryEc20DataQualityDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupDataQualityReport extends EptsDataExportManager {

  private SummaryDataQualityDataset summaryDataQualityDataset;

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

  private SummaryEc20DataQualityDataset summaryEc20DataQualityDataset;

  private Ec20PatientListDataset ec20PatientListDataset;

  private Ec23PatientListDataset ec23PatientListDataset;
  private Ec21PatientListDataset ec21PatientListDataset;

  private Ec22PatientListDataset ec22PatientListDataset;

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
      Ec20PatientListDataset ec20PatientListDataset,
      Ec21PatientListDataset ec21PatientListDataset,
      Ec22PatientListDataset ec22PatientListDataset,
      SummaryEc20DataQualityDataset summaryEc20DataQualityDataset,
      SummaryEc20DataQualityCohorts summaryEc20DataQualityCohorts,
      GetCustomConfigurationDataset getCustomConfigurationDataset,
      HivMetadata hivMetadata,
      Ec23PatientListDataset ec23PatientListDataset) {
    this.summaryDataQualityDataset = summaryDataQualityDataset;
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
    this.ec20PatientListDataset = ec20PatientListDataset;
    this.ec23PatientListDataset = ec23PatientListDataset;
    this.ec21PatientListDataset = ec21PatientListDataset;
    this.ec22PatientListDataset = ec22PatientListDataset;
    this.summaryEc20DataQualityDataset = summaryEc20DataQualityDataset;
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

    rd.addDataSetDefinition(
        "S",
        Mapped.mapStraightThrough(
            summaryDataQualityDataset.constructSummaryDataQualityDatset(getDataParameters())));

    rd.addDataSetDefinition(
        "S20",
        Mapped.mapStraightThrough(
            summaryEc20DataQualityDataset.constructSummaryEc20DataQualityDatset(
                getDataParameters())));

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
        "EC20",
        Mapped.mapStraightThrough(
            ec20PatientListDataset.ec20PatientListDatset(getDataParameters())));
    rd.addDataSetDefinition(
        "EC23",
        Mapped.mapStraightThrough(
            ec23PatientListDataset.ec23PatientListDatset(getDataParameters())));

    rd.addDataSetDefinition(
        "EC21",
        Mapped.mapStraightThrough(
            ec21PatientListDataset.ec21PatientListDataset(getDataParameters())));

    rd.addDataSetDefinition(
        "EC22",
        Mapped.mapStraightThrough(
            ec22PatientListDataset.ec22PatientListDataset(getDataParameters())));

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
          "sheet:2,row:7,dataset:EC1 | sheet:3,row:7,dataset:EC2 | sheet:4,row:7,dataset:EC3 | sheet:5,row:7,dataset:EC4 | sheet:6,row:7,dataset:EC5 | sheet:7,row:7,dataset:EC6 | sheet:8,row:7,dataset:EC7 | sheet:9,row:7,dataset:EC8 | sheet:10,row:7,dataset:EC9 | sheet:11,row:7,dataset:EC10 | sheet:12,row:7,dataset:EC11 | sheet:13,row:7,dataset:EC12 | sheet:14,row:7,dataset:EC13 | sheet:15,row:7,dataset:EC14 | sheet:16,row:7,dataset:EC15 | sheet:17,row:7,dataset:EC16 | sheet:18,row:7,dataset:EC17 | sheet:19,row:7,dataset:EC18 | sheet:20,row:7,dataset:EC19 |sheet:21,row:7,dataset:EC20 |sheet:22,row:7,dataset:EC21 |sheet:23,row:7,dataset:EC22 |sheet:24,row:7,dataset:EC23");
      props.put("sortWeight", "5000");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }

  private List<Parameter> getDataParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);

    parameters.add(
        new Parameter(
            "location", "reporting.parameter.locationList", Location.class, List.class, null));
    return parameters;
  }
}
