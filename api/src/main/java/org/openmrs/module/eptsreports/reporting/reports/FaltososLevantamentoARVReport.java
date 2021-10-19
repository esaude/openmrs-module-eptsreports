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
    return "6b3762b8-2cf0-11ec-8e14-97de0861d5ef";
  }

  @Override
  public String getName() {
    return "Faltosos ao Levantamento de ARV - MISAU";
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
    return "284c9ad8-81b0-11eb-b8e0-0242ac120002";
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
              "Faltosos_Levantamento_ARV_Template.xls",
              "Faltos ao Levantameto de ARV - MISAU",
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
