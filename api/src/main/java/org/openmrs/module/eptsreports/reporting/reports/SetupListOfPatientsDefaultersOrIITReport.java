package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.*;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsDefaultersOrIITCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListOfPatientsDefaultersOrIITTemplateDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.TotalListOfPatientsDefaultersOrIITTemplateDataSet;
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
public class SetupListOfPatientsDefaultersOrIITReport extends EptsDataExportManager {

  @Autowired
  protected ListOfPatientsDefaultersOrIITCohortQueries listOfPatientsDefaultersOrIITCohortQueries;

  @Autowired private ListOfPatientsDefaultersOrIITTemplateDataSet initListOfPatDefIITDataSet;

  @Autowired
  private TotalListOfPatientsDefaultersOrIITTemplateDataSet iniTotalLListOfPatDefIITDataSet;

  @Override
  public String getExcelDesignUuid() {
    return "6f594060-f45a-11eb-ab1f-8f99340d0b56";
  }

  @Override
  public String getUuid() {
    return "78126b50-f45a-11eb-9524-6b4b6161a8a7";
  }

  @Override
  public String getName() {
    return "List of Patients Defaulters or IIT ";
  }

  @Override
  public String getDescription() {
    return "This report provides list of patients defaulters or IIT ";
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
            listOfPatientsDefaultersOrIITCohortQueries.getBaseCohort(),
            "endDate=${endDate},minDay=${minDay},maxDay=${maxDay},location=${location}"));

    rd.addDataSetDefinition(
        "FATS", Mapped.mapStraightThrough(iniTotalLListOfPatDefIITDataSet.constructDataSet()));
    rd.addDataSetDefinition(
        "FATL", Mapped.mapStraightThrough(initListOfPatDefIITDataSet.constructDataSet()));
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
              "Template_List_Patients_Defaulters_IIT_TARV_v1.3.1.xls",
              "List Patients Defaulters IIT TARV Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:8,dataset:FATL");
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
        new Parameter("minDay", "Minimum number of days", Integer.class),
        new Parameter("maxDay", "Maximum number of days", Integer.class),
        new Parameter("location", "Location", Location.class));
  }
}
