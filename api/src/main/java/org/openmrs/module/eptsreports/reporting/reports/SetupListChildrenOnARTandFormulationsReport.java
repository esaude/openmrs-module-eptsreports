package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.*;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListChildrenOnARTandFormulationsDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Deprecated
public class SetupListChildrenOnARTandFormulationsReport extends EptsDataExportManager {

  private ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset;

  @Autowired protected GenericCohortQueries genericCohortQueries;

  @Autowired
  public SetupListChildrenOnARTandFormulationsReport(
      ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset) {
    this.listChildrenOnARTandFormulationsDataset = listChildrenOnARTandFormulationsDataset;
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
    return "List of Children and ARV Formulations Report";
  }

  @Override
  public String getDescription() {
    return "This report provides a line listing of children and ARV Formulations";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getParameters());
    rd.addDataSetDefinition(
        "ALL",
        Mapped.mapStraightThrough(
            listChildrenOnARTandFormulationsDataset.constructDataset(getParameters())));
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
              "Template_ListChildrenonARTandFormulations Specification_v1.0.xlsx",
              "List of Children on ART and Formulations",
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
