package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.IntensiveMonitoringDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.QualityImprovement2020DataSet;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupIntensiveMonitoringReport extends EptsDataExportManager {

  private GenericCohortQueries genericCohortQueries;

  private QualityImprovement2020DataSet initQltyImpDataSet;

  private IntensiveMonitoringDataSet intensiveMonitoringDataSet;

  @Autowired
  public SetupIntensiveMonitoringReport(
      GenericCohortQueries genericCohortQueries,
      QualityImprovement2020DataSet initQltyImpDataSet,
      IntensiveMonitoringDataSet intensiveMonitoringDataSet) {
    this.genericCohortQueries = genericCohortQueries;
    this.initQltyImpDataSet = initQltyImpDataSet;
    this.intensiveMonitoringDataSet = intensiveMonitoringDataSet;
  }

  @Override
  public String getUuid() {
    return "e248b75a-9b85-11eb-a09b-338d8a9a6376";
  }

  @Override
  public String getExcelDesignUuid() {
    return "91b97104-9c29-11eb-b128-2306b37b08da";
  }

  @Override
  public String getName() {
    return "Monitoria Intensiva  - 2021";
  }

  @Override
  public String getDescription() {
    return "Relatórios com os Indicadores Monitoria de Intensiva";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(getUuid());
    reportDefinition.setName(getName());
    reportDefinition.setDescription(getDescription());
    reportDefinition.setParameters(getParameters());
    reportDefinition.addDataSetDefinition(
        "IM",
        Mapped.mapStraightThrough(
            intensiveMonitoringDataSet.constructIntensiveMonitoringDataSet()));
    // add a base cohort here to help in calculations running
    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(),
            "endDate=${revisionEndDate},location=${location}"));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "Monitoria_Intensiva_HIV_v1.xls",
              "Template Ficha Relatório Monitoria Intensiva HIV",
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
        new Parameter("revisionEndDate", "Data Final Revisão", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class));
  }
}
