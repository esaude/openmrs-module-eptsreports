package org.openmrs.module.eptsreports.reporting.reports.manager;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxRetDataset;
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
public class SetupTxRetReport extends EptsDataExportManager {

  @Autowired private TxRetDataset txRetDataset;

  @Override
  public String getExcelDesignUuid() {
    return "7f8e53cc-765c-11e9-8f9e-2a86e4085a59";
  }

  @Override
  public String getUuid() {
    return "7f8e5778-765c-11e9-8f9e-2a86e4085a59";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "TX_RET Report";
  }

  @Override
  public String getDescription() {
    return "TX RET 2.1 Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    rd.addParameter(new Parameter("endDate", "End Date", Date.class));
    rd.addParameter(new Parameter("location", "Location", Location.class));
    rd.addDataSetDefinition("R", Mapped.mapStraightThrough(txRetDataset.constructTxRetDataset()));

    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "TX_RET_21_Report.xls",
              "TX_RET 2.1 Report",
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
}
