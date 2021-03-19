package org.openmrs.module.eptsreports.reporting.reports.tuberculosis.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.datasets.tuberculosis.report.PatientsWithPositiveTuberculosisWhoStartdTPIDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.tuberculosis.report.SummaryTuberculosisReportDataset;
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
public class SetupPatientsWithTuberculosisWhoStartedTPIReport extends EptsDataExportManager {

  private PatientsWithPositiveTuberculosisWhoStartdTPIDataSet
      patientsWithPositiveTuberculosisWhoStartdTPIDataSet;
  private SummaryTuberculosisReportDataset summaryTuberculosisReportDataset;

  @Autowired
  public SetupPatientsWithTuberculosisWhoStartedTPIReport(
      PatientsWithPositiveTuberculosisWhoStartdTPIDataSet
          patientsWithPositiveTuberculosisWhoStartdTPIDataSet,
      SummaryTuberculosisReportDataset summaryTuberculosisReportDataset) {
    this.patientsWithPositiveTuberculosisWhoStartdTPIDataSet =
        patientsWithPositiveTuberculosisWhoStartdTPIDataSet;
    this.summaryTuberculosisReportDataset = summaryTuberculosisReportDataset;
  }

  @Override
  public String getExcelDesignUuid() {
    return "f374bbf6-6227-11eb-ae93-0242ac130002";
  }

  @Override
  public String getUuid() {
    return "fed281d6-6227-11eb-ae93-0242ac130002";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES QUE INICIARAM PROFILAXIA COM ISONIAZIDA (TPI) - 2021";
  }

  @Override
  public String getDescription() {
    return "O RELATÓRIO DEVOLVE A LISTA DE PACIENTES QUE INICIARAM PROFILAXIA COM ISONIAZIDA";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getDataParameters());

    rd.addDataSetDefinition(
        "S",
        Mapped.mapStraightThrough(
            summaryTuberculosisReportDataset.constructSummaryDataQualityDatset(
                getDataParameters())));

    rd.addDataSetDefinition(
        "TB2",
        Mapped.mapStraightThrough(
            patientsWithPositiveTuberculosisWhoStartdTPIDataSet
                .patientsWithPositiveTuberculosisWhoStartdTPIDataSet(getDataParameters())));

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
              "ListaPacientesTBQueIniciaramTPI.xls",
              "LISTA DE PACIENTES QUE INICIARAM PROFILAXIA COM ISONIAZIDA (TPI)",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:3,dataset:TB2");
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
