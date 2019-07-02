package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.SummaryDataQualityDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupDataQualityReport extends EptsDataExportManager {

  private GenericCohortQueries genericCohortQueries;

  private SummaryDataQualityDataset dataQualityOveralDataset;

  @Autowired
  public SetupDataQualityReport(
      GenericCohortQueries genericCohortQueries,
      SummaryDataQualityDataset dataQualityOveralDataset) {
    this.genericCohortQueries = genericCohortQueries;
    this.dataQualityOveralDataset = dataQualityOveralDataset;
  }

  @Override
  public String getExcelDesignUuid() {
    return "65e364d6-9be0-11e9-a7a2-cb23750cc2df";
  }

  @Override
  public String getUuid() {
    return "82ea2326-9be0-11e9-9cee-5384d0247641";
  }

  @Override
  public String getName() {
    return "Data Quality Report v1";
  }

  @Override
  public String getDescription() {
    return "This report provides a line listing of patient records failing to meet certain edit checks and allows the user to review the information so the patient’s information can be corrected";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(dataQualityOveralDataset.getDataQualityParameters());

    // add a base cohort here to help in calculations running
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${locationList}"));
    // adding respective data sets
    rd.addDataSetDefinition(
        "S",
        Mapped.mapStraightThrough(dataQualityOveralDataset.constructOveralDataQualityDatset()));

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
              "Data_Quality_Report.xls",
              "Data Quality Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportDesignConstructionException(e);
    }

    return Arrays.asList(reportDesign);
  }
}
