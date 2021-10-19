package org.openmrs.module.eptsreports.reporting.reports;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.LocationDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.midatasets.MIDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.viralloadmidatasets.VLMIDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
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

  @Autowired protected GenericCohortQueries genericCohortQueries;
  @Autowired MIDataSet miDataSet;
  @Autowired VLMIDataSet vlMIDataSet;

  @Override
  public String getUuid() {
    return "845b7c3c-b7e5-11eb-8c08-6f823c2e1af5";
  }

  @Override
  public String getName() {
    return "Monitoria Intensiva - 2021";
  }

  @Override
  public String getDescription() {
    return "Novo relatórios com Indicadores para estratégia de Monitoria Intesiva";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "8e912dd2-b7e5-11eb-a937-a7c714e47784";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(getUuid());
    reportDefinition.setName(getName());
    reportDefinition.setDescription(getDescription());
    reportDefinition.setParameters(getParameters());

    reportDefinition.addDataSetDefinition(
        "HF", mapStraightThrough(new LocationDataSetDefinition()));

    reportDefinition.addDataSetDefinition(
        "MI", Mapped.mapStraightThrough(miDataSet.constructTMiDatset()));

    reportDefinition.addDataSetDefinition(
        "VLMI", Mapped.mapStraightThrough(vlMIDataSet.constructVLMiDatset()));

    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endRevisionDate},location=${location}"));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "MI.xls",
              "Relatorio de Monitoria Intensiva",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:8,dataset:MI");
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
        new Parameter("endRevisionDate", "Data da Recolha de Dados", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class));
  }
}
