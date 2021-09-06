package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.*;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.TPTInitiationDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TPTTotalsDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupTPTInitiationReport extends EptsDataExportManager {

  private TPTInitiationDataset tptInitiationDataset;
  private TPTTotalsDataset tptTotalsDataset;

  @Autowired protected GenericCohortQueries genericCohortQueries;

  @Autowired
  public SetupTPTInitiationReport(
      TPTInitiationDataset tptInitiationDataset, TPTTotalsDataset tptTotalsDataset) {
    this.tptInitiationDataset = tptInitiationDataset;
    this.tptTotalsDataset = tptTotalsDataset;
  }

  @Override
  public String getExcelDesignUuid() {
    return "39571f02-8626-11eb-84e8-d3239fe29d04";
  }

  @Override
  public String getUuid() {
    return "5516d872-8626-11eb-9e73-335a85ba4e67";
  }

  @Override
  public String getName() {
    return "TPT Initiation Report";
  }

  @Override
  public String getDescription() {
    return "This report provides a line listing of patient records who initiated TPT";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getParameters());
    // Base Cohort embeded in TPT Initiation query
    rd.addDataSetDefinition(
        "TOTAL", Mapped.mapStraightThrough(tptTotalsDataset.constructDataset(getParameters())));

    rd.addDataSetDefinition(
        "TPT", Mapped.mapStraightThrough(tptInitiationDataset.constructDataset(getParameters())));
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
              "Template_List_Patients_Initiated.xls",
              "TPT Initiation Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:5,dataset:TPT");
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
