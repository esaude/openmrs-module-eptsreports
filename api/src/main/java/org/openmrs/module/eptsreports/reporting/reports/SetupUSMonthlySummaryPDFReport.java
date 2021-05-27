package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.UsMonthlySummaryDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Deprecated
public class SetupUSMonthlySummaryPDFReport extends EptsDataExportManager {

  @Autowired private UsMonthlySummaryDataset usMonthlySummaryDataset;

  @Override
  public String getExcelDesignUuid() {
    return "714795a2-59f2-11e9-8647-d663bd873d93";
  }

  @Override
  public String getUuid() {
    return "71479854-59f2-11e9-8647-d663bd873d93";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "US Monthly Summary PDF";
  }

  @Override
  public String getDescription() {
    return "US Monthly Summary PDF Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(usMonthlySummaryDataset.getParameters());
    rd.addDataSetDefinition(
        "S", Mapped.mapStraightThrough(usMonthlySummaryDataset.constructUsMonthlySummaryDataset()));

    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "US_MONTHLY_SUMMARY_PDF.xls",
              "US MONTHLY SUMMARY PDF excell design",
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
