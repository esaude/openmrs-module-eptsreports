package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec1PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec2PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.SummaryDataQualityDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupDataQualityReport extends EptsDataExportManager {

  private SummaryDataQualityDataset summaryDataQualityDataset;

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  private Ec1PatientListDataset ec1PatientListDataset;

  private Ec2PatientListDataset ec2PatientListDataset;

  @Autowired
  public SetupDataQualityReport(
      SummaryDataQualityDataset summaryDataQualityDataset,
      SummaryDataQualityCohorts summaryDataQualityCohorts,
      Ec1PatientListDataset ec1PatientListDataset,
      Ec2PatientListDataset ec2PatientListDataset) {
    this.summaryDataQualityDataset = summaryDataQualityDataset;
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.ec1PatientListDataset = ec1PatientListDataset;
    this.ec2PatientListDataset = ec2PatientListDataset;
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
    return "Data Quality Report v1";
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
    rd.addParameters(summaryDataQualityDataset.getDataQualityParameters());

    // add a base cohort here to help in calculations running
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            summaryDataQualityCohorts.getQualityDataReportBaseCohort(),
            "startDate=${startDate},endDate=${endDate},location=${location},state=${state}"));
    // adding respective data sets
    rd.addDataSetDefinition(
        "S",
        Mapped.mapStraightThrough(summaryDataQualityDataset.constructSummaryDataQualityDatset()));
    rd.addDataSetDefinition(
        "EC1", Mapped.mapStraightThrough(ec1PatientListDataset.ec1DataSetDefinition()));
    rd.addDataSetDefinition(
        "EC2", Mapped.mapStraightThrough(ec2PatientListDataset.ec2DataSetDefinition()));

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
      props.put("repeatingSections", "sheet:2,row:7,dataset:EC1 | sheet:3,row:7,dataset:EC2");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportDesignConstructionException(e);
    }

    return Arrays.asList(reportDesign);
  }
}
