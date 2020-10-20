package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryEc20DataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.Ec20PatientListDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.SummaryEc20DataQualityDataset;
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

@Deprecated
@Component
public class SetupEc20DataQualityReport extends EptsDataExportManager {

  private SummaryEc20DataQualityCohorts summaryEc20DataQualityCohorts;

  private SummaryEc20DataQualityDataset summaryEc20DataQualityDataset;

  private Ec20PatientListDataset ec20PatientListDataset;

  @Autowired
  public SetupEc20DataQualityReport(
      Ec20PatientListDataset ec20PatientListDataset,
      SummaryEc20DataQualityDataset summaryEc20DataQualityDataset,
      SummaryEc20DataQualityCohorts summaryEc20DataQualityCohorts) {
    this.summaryEc20DataQualityDataset = summaryEc20DataQualityDataset;
    this.summaryEc20DataQualityCohorts = summaryEc20DataQualityCohorts;
    this.ec20PatientListDataset = ec20PatientListDataset;
  }

  @Override
  public String getExcelDesignUuid() {
    return "0527ab60-bf2c-11e9-b8ed-7b0ec2ec93ad";
  }

  @Override
  public String getUuid() {
    return "10b6128c-bf2c-11e9-a2e5-63fb38259292";
  }

  @Override
  public String getName() {
    return "EC20 Data Quality Report";
  }

  @Override
  public String getDescription() {
    return "This report provides a line listing of patient records failing to meet EC20 edit checks and allows the user to review the information so the patient’s information can be corrected";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getDataParameters());

    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            summaryEc20DataQualityCohorts.getEc20DataQualityReportBaseCohort(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    rd.addDataSetDefinition(
        "S20",
        Mapped.mapStraightThrough(
            summaryEc20DataQualityDataset.constructSummaryEc20DataQualityDatset(
                getDataParameters())));
    rd.addDataSetDefinition(
        "EC20",
        Mapped.mapStraightThrough(
            ec20PatientListDataset.ec20PatientListDatset(getDataParameters())));
    //    rd.addDataSetDefinition(
    //        "EC01",
    //        Mapped.mapStraightThrough(
    //            getCustomConfigurationDataset.configDataSetDefinition(getDataParameters())));

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
              "EC20_Data_Quality_Report.xls",
              "EC20 Data Quality Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:2,row:7,dataset:EC20");
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
    return parameters;
  }
}
