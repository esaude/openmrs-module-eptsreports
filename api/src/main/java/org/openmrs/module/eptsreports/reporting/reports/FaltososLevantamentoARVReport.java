package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.FaltososLevantamentoARVDataSet;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FaltososLevantamentoARVReport extends EptsDataExportManager {
  protected GenericCohortQueries genericCohortQueries;
  private FaltososLevantamentoARVDataSet faltososLevantamentoARVDataSet;

  @Autowired
  public FaltososLevantamentoARVReport(
      GenericCohortQueries genericCohortQueries,
      FaltososLevantamentoARVDataSet faltososLevantamentoARVDataSet) {
    this.genericCohortQueries = genericCohortQueries;
    this.faltososLevantamentoARVDataSet = faltososLevantamentoARVDataSet;
  }

  @Override
  public String getUuid() {
    return "73ba713a-3bca-11ec-b0d7-b7913905b82c";
  }

  @Override
  public String getName() {
    return "RELATÓRIO DE FALTOSOS AO LEVANTAMENTO DE ARV - MISAU";
  }

  @Override
  public String getDescription() {
    return "Relatório de Faltosos ao Levantamento de ARV for the selected location and reporting period";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "67f5bf1c-3bca-11ec-9e72-cb0763b0876d";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(getUuid());
    reportDefinition.setName(getName());
    reportDefinition.setDescription(getDescription());
    reportDefinition.addParameters(getParameters());
    reportDefinition.addDataSetDefinition(
        "FALTOSOS", Mapped.mapStraightThrough(faltososLevantamentoARVDataSet.constructDataSet()));

    // add a base cohort here to help in calculations running
    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "Template_Faltosos_Levantamento_ARV.xls",
              "Relatório de Faltosos ao Levantamento de ARV",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return Arrays.asList(reportDesign);
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("endDate", "End date", Date.class),
        new Parameter("startDate", "Start date", Date.class),
        new Parameter("location", "Location", Location.class));
  }
}
