package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsEligibleForVLCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListOfPatientsEligibleForVLDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.TotalOfPatientsEligibleForVLDataSet;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupListOfPatientsEligibleForVLReport extends EptsDataExportManager {

  private ListOfPatientsEligibleForVLDataSet listOfPatientsEligibleForVLDataset;
  private TotalOfPatientsEligibleForVLDataSet totalOfPatientsEligibleForVLDataSet;
  private ListOfPatientsEligibleForVLCohortQueries listOfPatientsEligibleForVLCohortQueries;

  @Autowired
  public SetupListOfPatientsEligibleForVLReport(
      ListOfPatientsEligibleForVLDataSet listOfPatientsEligibleForVLDataset,
      TotalOfPatientsEligibleForVLDataSet totalOfPatientsEligibleForVLDataSet,
      ListOfPatientsEligibleForVLCohortQueries listOfPatientsEligibleForVLCohortQueries) {

    this.listOfPatientsEligibleForVLDataset = listOfPatientsEligibleForVLDataset;
    this.totalOfPatientsEligibleForVLDataSet = totalOfPatientsEligibleForVLDataSet;
    this.listOfPatientsEligibleForVLCohortQueries = listOfPatientsEligibleForVLCohortQueries;
  }

  @Override
  public String getExcelDesignUuid() {
    return "5560708a-058e-11ec-8f57-ef5df3aedb96";
  }

  @Override
  public String getUuid() {
    return "5ff8d9ec-058e-11ec-8dee-e7f449a993e7";
  }

  @Override
  public String getName() {
    return "List of Patients Eligible for VL";
  }

  @Override
  public String getDescription() {
    return "This report provide a line listing of patients eligible for VL";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getParameters());
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            listOfPatientsEligibleForVLCohortQueries.getBaseCohort(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    rd.addDataSetDefinition(
        "TPEVL", Mapped.mapStraightThrough(totalOfPatientsEligibleForVLDataSet.constructDataSet()));

    rd.addDataSetDefinition(
        "LPEVL", Mapped.mapStraightThrough(listOfPatientsEligibleForVLDataset.constructDataSet()));

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
              "Template_List_Patients_EligbleVL_v1.1.xls",
              "List of Patients Eligible for VL",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:LPEVL");
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
        new Parameter("startDate", "Start date", Date.class),
        new Parameter("endDate", "End date", Date.class),
        new Parameter("location", "Location", Location.class));
  }
}
