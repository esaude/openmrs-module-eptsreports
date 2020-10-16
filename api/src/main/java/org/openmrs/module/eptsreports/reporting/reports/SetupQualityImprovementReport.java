package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.QualityImprovementDataSet;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupQualityImprovementReport extends EptsDataExportManager {

  @Autowired protected GenericCohortQueries genericCohortQueries;

  @Autowired private QualityImprovementDataSet initQltyImpDataSet;

  @Override
  public String getUuid() {
    return "565258f6-0774-4429-826c-2ba3284ec8b2";
  }

  @Override
  public String getName() {
    return "QUALITY IMPROVEMENT REPORT 2019";
  }

  @Override
  public String getDescription() {
    return "Novo relatórios com Indicadores para estratégia de Melhoria de Qualidade";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "5bbbd64c-4d0e-4e8f-99d6-cd8b453f3726";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(getUuid());
    reportDefinition.setName(getName());
    reportDefinition.setDescription(getDescription());
    reportDefinition.setParameters(getParameters());
    reportDefinition.addDataSetDefinition(
        "ALL", Mapped.mapStraightThrough(initQltyImpDataSet.constructInitQltyImpDataSet()));

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
              "TEMPLATE_MELHORIA_QUALIDADE.xls",
              "QUALITY IMPROVEMENT REPORT",
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
        new Parameter("startDate", "Data Inicial Inclusão", Date.class),
        new Parameter("endDate", "Data Final Inclusão", Date.class),
        new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class),
        new Parameter("testStart", "Testar Iniciar", Boolean.class));
  }
}
