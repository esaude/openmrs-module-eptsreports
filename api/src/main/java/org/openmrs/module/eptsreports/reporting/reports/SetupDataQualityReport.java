package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.DataQualityOverallDataset;
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

  private DataQualityOverallDataset dataQualityOveralDataset;

  @Autowired
  public SetupDataQualityReport(
      GenericCohortQueries genericCohortQueries,
      DataQualityOverallDataset dataQualityOveralDataset) {
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
    return "Data Quality Report";
  }

  @Override
  public String getDescription() {
    return "Data Quality Report - PEPFAR MER Quarterly Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(dataQualityOveralDataset.getParameters());

    // add a base cohort here to help in calculations running
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));
    // adding respective data sets
    rd.addDataSetDefinition(
        "A",
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
