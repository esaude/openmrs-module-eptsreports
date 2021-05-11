package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.*;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.datasets.TPTListOfPatientsEligibleDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.TPTTotalListOfPatientsEligibleDataSet;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupTPTListOfPatientsEligibleReport extends EptsDataExportManager {

  private TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet;

  private TPTTotalListOfPatientsEligibleDataSet tptTotalListOfPatientsEligibleDataSet;

  @Autowired
  public SetupTPTListOfPatientsEligibleReport(
      TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet,
      TPTTotalListOfPatientsEligibleDataSet tptTotalListOfPatientsEligibleDataSet) {

    this.tptListOfPatientsEligibleDataSet = tptListOfPatientsEligibleDataSet;
    this.tptTotalListOfPatientsEligibleDataSet = tptTotalListOfPatientsEligibleDataSet;
  }

  @Override
  public String getExcelDesignUuid() {
    return "5e17f214-af0f-11eb-852c-ef10820bc4bd";
  }

  @Override
  public String getUuid() {
    return "6ee04286-af0f-11eb-afea-e3389adce11f";
  }

  @Override
  public String getName() {
    return "TPT List of Patients Elegible  Report";
  }

  @Override
  public String getDescription() {
    return "This report provides a line listing of patient records who eligible TPT";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getParameters());
    rd.addDataSetDefinition(
        "TOTAL",
        Mapped.mapStraightThrough(
            tptTotalListOfPatientsEligibleDataSet.constructDataset(getParameters())));
    rd.addDataSetDefinition(
        "TPT",
        Mapped.mapStraightThrough(
            tptListOfPatientsEligibleDataSet.constructDataset(getParameters())));
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
              "TPT_List_Patients_Eligibles_Report.xls",
              "TPT List Patients Eligibles Report",
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

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("endDate", "End date", Date.class),
        new Parameter("location", "Location", Location.class));
  }
}
