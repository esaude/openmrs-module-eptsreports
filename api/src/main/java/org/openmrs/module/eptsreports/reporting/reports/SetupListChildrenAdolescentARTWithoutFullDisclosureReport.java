package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListChildrenAdolescentARTWithoutFullDisclosureDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.SismaCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.TotalChildrenAdolescentARTWithoutFullDisclosureDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Deprecated
public class SetupListChildrenAdolescentARTWithoutFullDisclosureReport
    extends EptsDataExportManager {

  private final ListChildrenAdolescentARTWithoutFullDisclosureDataset
      listChildrenAdolescentARTWithoutFullDisclosureDataset;
  private final TotalChildrenAdolescentARTWithoutFullDisclosureDataset
      totalChildrenAdolescentARTWithoutFullDisclosureDataset;
  private final ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries
      listChildrenAdolescentARTWithoutFullDisclosureCohortQueries;

  @Autowired
  public SetupListChildrenAdolescentARTWithoutFullDisclosureReport(
      ListChildrenAdolescentARTWithoutFullDisclosureDataset
          listChildrenAdolescentARTWithoutFullDisclosureDataset,
      TotalChildrenAdolescentARTWithoutFullDisclosureDataset
          totalChildrenAdolescentARTWithoutFullDisclosureDataset,
      ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries
          listChildrenAdolescentARTWithoutFullDisclosureCohortQueries) {
    this.listChildrenAdolescentARTWithoutFullDisclosureDataset =
        listChildrenAdolescentARTWithoutFullDisclosureDataset;
    this.totalChildrenAdolescentARTWithoutFullDisclosureDataset =
        totalChildrenAdolescentARTWithoutFullDisclosureDataset;
    this.listChildrenAdolescentARTWithoutFullDisclosureCohortQueries =
        listChildrenAdolescentARTWithoutFullDisclosureCohortQueries;
  }

  @Override
  public String getExcelDesignUuid() {
    return "5F1006D0-73BA-4435-8E5A-649664320D78";
  }

  @Override
  public String getUuid() {
    return "0E6C1595-8539-4E05-B1D1-9BA58ED1CF8F";
  }

  @Override
  public String getName() {
    return "List ofAdolescent Children on ART Without Full Disclosure";
  }

  @Override
  public String getDescription() {
    return "List of Adolescent Children on ART Without Full Disclosure";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getParameters());
    rd.addDataSetDefinition("DT", Mapped.mapStraightThrough(new DatimCodeDatasetDefinition()));
    rd.addDataSetDefinition("SM", Mapped.mapStraightThrough(new SismaCodeDatasetDefinition()));
    rd.addDataSetDefinition(
        "LCA",
        Mapped.mapStraightThrough(
            listChildrenAdolescentARTWithoutFullDisclosureDataset
                .constructListChildrenAdolescentARTWithoutFullDisclosureDataset()));
    rd.addDataSetDefinition(
        "LCAT",
        Mapped.mapStraightThrough(
            totalChildrenAdolescentARTWithoutFullDisclosureDataset
                .constructTotalChildrenAdolescentARTWithoutFullDisclosureDataset()));
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            listChildrenAdolescentARTWithoutFullDisclosureCohortQueries
                .getBaseCohortForAdolescent(),
            "endDate=${endDate},location=${location}"));
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
              "Template_ListChildrenAdolescentARTWithoutFullDisclosure_v0.3.xls",
              "List of Adolescent Children On ART Without Full Disclosure",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:9,dataset:LCA");
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
