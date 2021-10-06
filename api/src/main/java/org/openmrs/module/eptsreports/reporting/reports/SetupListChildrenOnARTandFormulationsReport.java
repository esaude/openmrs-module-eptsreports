package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.*;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListChildrenOnARTandFormulationsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListChildrenOnARTandFormulationsDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupListChildrenOnARTandFormulationsReport extends EptsDataExportManager {

  private ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset;

  private ListChildrenOnARTandFormulationsCohortQueries
      listChildrenOnARTandFormulationsCohortQueries;

  @Autowired
  public SetupListChildrenOnARTandFormulationsReport(
      ListChildrenOnARTandFormulationsCohortQueries listChildrenOnARTandFormulationsCohortQueries,
      ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset) {
    this.listChildrenOnARTandFormulationsDataset = listChildrenOnARTandFormulationsDataset;
    this.listChildrenOnARTandFormulationsCohortQueries =
        listChildrenOnARTandFormulationsCohortQueries;
  }

  @Override
  public String getExcelDesignUuid() {
    return "9a832386-e290-11eb-a73e-4f4bdad3d8fd";
  }

  @Override
  public String getUuid() {
    return "ac5ed794-e290-11eb-8648-c3783d587203";
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
        Mapped.mapStraightThrough(listChildrenOnARTandFormulationsDataset.constructDataset()));
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
              "list_children_art_formulation.xls",
              "List of Children on ART and Formulations",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:ALL");
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
