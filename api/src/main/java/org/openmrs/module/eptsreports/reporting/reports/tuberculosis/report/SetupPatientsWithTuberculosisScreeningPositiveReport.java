package org.openmrs.module.eptsreports.reporting.reports.tuberculosis.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.datasets.tuberculosis.report.PatientsWithPositiveTuberculosisScreeningDataSet;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupPatientsWithTuberculosisScreeningPositiveReport extends EptsDataExportManager {

  private PatientsWithPositiveTuberculosisScreeningDataSet
      patientsWithPositiveTuberculosisScreeningDataSet;

  @Autowired
  public SetupPatientsWithTuberculosisScreeningPositiveReport(
      PatientsWithPositiveTuberculosisScreeningDataSet
          patientsWithPositiveTuberculosisScreeningDataSet) {
    this.patientsWithPositiveTuberculosisScreeningDataSet =
        patientsWithPositiveTuberculosisScreeningDataSet;
  }

  @Override
  public String getExcelDesignUuid() {
    return "eb11ef50-6214-11eb-ae93-0242ac130002";
  }

  @Override
  public String getUuid() {
    return "644ef020-6215-11eb-ae93-0242ac130002";
  }

  @Override
  public String getName() {
    return "Lista de Rastreio TB Positivo - 2021";
  }

  @Override
  public String getDescription() {
    return "O Relatório devolve a Lista de Pacientes com Rastreio TB Positivo";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getDataParameters());

    rd.addDataSetDefinition(
        "TB1",
        Mapped.mapStraightThrough(
            patientsWithPositiveTuberculosisScreeningDataSet
                .patientsWithPositiveTuberculosisScreeningDataSet(getDataParameters())));

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
              "ListaRastreioTBPositivo.xls",
              "Lista de Rastreio TB Positivo",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:TB1");
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
    parameters.add(new Parameter("location", "Facilities", Location.class, List.class, null));
    return parameters;
  }
}
