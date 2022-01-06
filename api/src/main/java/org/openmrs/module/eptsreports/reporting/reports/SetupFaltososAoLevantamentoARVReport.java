package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.FaltososAoLevantamentoARVDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupFaltososAoLevantamentoARVReport extends EptsDataExportManager {

  @Autowired private FaltososAoLevantamentoARVDataSet faltososAoLevantamentoARVDataSet;
  @Autowired private GenericCohortQueries genericCohortQueries;
  @Autowired private DatinCodeDataSet datimCodeDataSet;

  @Override
  public String getExcelDesignUuid() {
    return "d4caee44-0a99-11ec-a947-0f3e772973da";
  }

  @Override
  public String getUuid() {
    return "cac66234-0a99-11ec-94d2-f35432fbd63a";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "Relatório de Faltosos ao Levantamento de ARV - MISAU";
  }

  @Override
  public String getDescription() {
    return "Relatório de Faltosos ao Levantamento de ARV - MISAU";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(this.getDataParameters());
    rd.addDataSetDefinition(
        "FL", Mapped.mapStraightThrough(faltososAoLevantamentoARVDataSet.constructDatset()));

    rd.addDataSetDefinition(
        "D",
        Mapped.mapStraightThrough(
            this.datimCodeDataSet.constructDataset(this.getDataParameters())));

    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));
    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "Faltosos_ao_levantamento_arv.xls",
              "Relatorio de Faltosos ao Levantamento de ARV - MISAU",
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

  private List<Parameter> getDataParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }
}
