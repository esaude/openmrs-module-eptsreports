package org.openmrs.module.eptsreports.reporting.reports;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.TXTBMonthlyDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
public class SetupTXTBMonthlyReport extends EptsDataExportManager {

  private GenericCohortQueries genericCohortQueries;

  private TXTBMonthlyDataset txtbMonthlyDataset;

  @Autowired
  public SetupTXTBMonthlyReport(
      GenericCohortQueries genericCohortQueries, TXTBMonthlyDataset txtbMonthlyDataset) {
    this.genericCohortQueries = genericCohortQueries;
    this.txtbMonthlyDataset = txtbMonthlyDataset;
  }

  /** @return the uuid for the report design for exporting to Excel */
  @Override
  public String getExcelDesignUuid() {
    return "6a76b0e8-a0a1-11ec-93e6-e318a61c8176";
  }

  /** @return the uuid of the Report */
  @Override
  public String getUuid() {
    return "7a867a9a-a0a1-11ec-8df5-cfe5d86ba2a3";
  }

  /** @return the name of the Report */
  @Override
  public String getName() {
    return "TX TB Monthly Cascade Report";
  }

  /** @return the description of the Report */
  @Override
  public String getDescription() {
    return "TB4: TX_TB Monthly Cascade Report (Relatorio Cascata Mensal de TX_TB)";
  }

  /** @return a ReportDefinition that may be persisted or run */
  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getParameters());
    rd.addDataSetDefinition("DT", Mapped.mapStraightThrough(new DatimCodeDatasetDefinition()));
    rd.addDataSetDefinition(
        "TXM", Mapped.mapStraightThrough(txtbMonthlyDataset.constructTXTBMonthlyDataset()));

    // add a base cohort here to help in calculations running
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));
    return rd;
  }

  /**
   * This is used to determine whether to build/save the report definition on module startup.
   * Version should be something like "1.0" or "1.1-SNAPSHOT". (Any version with "-SNAPSHOT"
   * indicates it is under active development and will be built/saved every time the module is
   * started.)
   *
   * @return what version of this report we are at
   */
  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign rd = null;
    try {
      rd =
          createXlsReportDesign(
              reportDefinition,
              "Template_TX_TB_ Monthly_Cascade_Report_v1.2.xlsx",
              "TX TB Monthly Cascade Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      rd.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(rd);
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("endDate", "End date", Date.class),
        new Parameter("location", "Location", Location.class));
  }
}
