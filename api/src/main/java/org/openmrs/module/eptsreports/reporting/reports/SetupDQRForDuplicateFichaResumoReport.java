package org.openmrs.module.eptsreports.reporting.reports;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.DQRForDuplicateFichaResumoDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.LocationDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.SummaryDQRForDuplicateFichaResumoDataSet;
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
public class SetupDQRForDuplicateFichaResumoReport extends EptsDataExportManager {

  private GenericCohortQueries genericCohortQueries;
  private DQRForDuplicateFichaResumoDataSet dqrForDuplicateFichaResumoDataSet;
  private SummaryDQRForDuplicateFichaResumoDataSet summaryDQRForDuplicateFichaResumoDataSet;

  @Autowired
  public SetupDQRForDuplicateFichaResumoReport(
      GenericCohortQueries genericCohortQueries,
      DQRForDuplicateFichaResumoDataSet dqrForDuplicateFichaResumoDataSet,
      SummaryDQRForDuplicateFichaResumoDataSet summaryDQRForDuplicateFichaResumoDataSet) {
    this.genericCohortQueries = genericCohortQueries;
    this.dqrForDuplicateFichaResumoDataSet = dqrForDuplicateFichaResumoDataSet;
    this.summaryDQRForDuplicateFichaResumoDataSet = summaryDQRForDuplicateFichaResumoDataSet;
  }

  @Override
  public String getExcelDesignUuid() {
    return "0a22031c-f5bd-11eb-a56b-17be2817584c";
  }

  @Override
  public String getUuid() {
    return "fd077694-f5bc-11eb-ba44-3f9a8a6341a5";
  }

  @Override
  public String getName() {
    return "Data Quality Report to Identify Duplicate Ficha Resumo";
  }

  @Override
  public String getDescription() {
    return "This a Data Quality Report to Identify Duplicate for Ficha Resumo";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getParameters());
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));
    rd.addDataSetDefinition(
        "EC1T",
        Mapped.mapStraightThrough(
            summaryDQRForDuplicateFichaResumoDataSet.constructIndicatorDataset()));
    rd.addDataSetDefinition(
        "EC1",
        Mapped.mapStraightThrough(dqrForDuplicateFichaResumoDataSet.constructPatientDataSet()));
    rd.addDataSetDefinition("DT", mapStraightThrough(new LocationDataSetDefinition()));
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
              "Template_Data_Quality_Report_Duplicate_Ficha_Resumo_R.xls",
              "DQR for Duplicate Ficha Resumo Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:2,row:7,dataset:EC1");
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
        new Parameter("endDate", "Report Generation Date", Date.class),
        new Parameter("location", "Location", Location.class));
  }
}
