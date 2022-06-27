package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxTbMonthlyCascadeDataset;
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
public class SetupTxTbMonthlyCascadeReport extends EptsDataExportManager {

  @Autowired private TxTbMonthlyCascadeDataset txtbMonthlyCascadeDataset;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Override
  public String getUuid() {
    return "b5637fee-a373-11ec-ab03-3bd7c92b4c14";
  }

  @Override
  public String getName() {
    return "TX TB Monthly Cascade Report";
  }

  @Override
  public String getDescription() {
    return "TX TB Monthly Cascade Report to PEPFAR";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "4692be1c-a374-11ec-a896-bb0968a58d22";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(getUuid());
    reportDefinition.setName(getName());
    reportDefinition.setDescription(getDescription());
    reportDefinition.setParameters(getParameters());

    reportDefinition.addDataSetDefinition(
        "TXTB",
        EptsReportUtils.map(
            txtbMonthlyCascadeDataset.constructTXTBMonthlyDataset(),
            "endDate=${endDate},location=${location}"));

    reportDefinition.addDataSetDefinition(
        "DT", Mapped.mapStraightThrough(new DatimCodeDatasetDefinition()));

    // add a base cohort here to help in calculations running
    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign rd = null;
    try {
      rd =
          createXlsReportDesign(
              reportDefinition,
              "Template_TX_TB_Monthly_Cascade_Report_v1.2.xls",
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
        new Parameter("endDate", "End Date", Date.class),
        new Parameter("location", "Location", Location.class));
  }
}
