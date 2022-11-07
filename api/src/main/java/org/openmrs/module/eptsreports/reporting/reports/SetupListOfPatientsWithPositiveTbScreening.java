package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListOfPatientsWithPositiveTbScreeningDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.SismaCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.TotalOfPatientsWithPositiveTbScreeningDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupListOfPatientsWithPositiveTbScreening extends EptsDataExportManager {

  @Autowired
  private TotalOfPatientsWithPositiveTbScreeningDataset
      totalOfPatientsWithPositiveTbScreeningDataset;

  @Autowired
  private ListOfPatientsWithPositiveTbScreeningDataset listOfPatientsWithPositiveTbScreeningDataset;

  @Override
  public String getUuid() {
    return "cf2a15bc-329f-11ed-bdd8-27b90b1144d8";
  }

  @Override
  public String getName() {
    return "TB6: List of Patients Currently on ART with Positive TB Screening";
  }

  @Override
  public String getDescription() {
    return "TB6: Lista de Pacientes Activos em TARV que tiveram Rastreio Positivo de TB";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "da036f6a-329f-11ed-8bcf-0b52284b58d5";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getParameters());
    rd.addDataSetDefinition("DT", Mapped.mapStraightThrough(new DatimCodeDatasetDefinition()));
    rd.addDataSetDefinition("SM", Mapped.mapStraightThrough(new SismaCodeDatasetDefinition()));
    rd.addDataSetDefinition(
        "TOTAL",
        Mapped.mapStraightThrough(totalOfPatientsWithPositiveTbScreeningDataset.contructDataset()));
    try {
      rd.addDataSetDefinition(
          "TBPS",
          Mapped.mapStraightThrough(
              listOfPatientsWithPositiveTbScreeningDataset.contructDataset()));
    } catch (EvaluationException e) {
      e.printStackTrace();
    }

    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "Template_List_Patients_With_Positive_TB_Screening_v1.0.xls",
              "List of Patients with Positive TB Screening Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:9,dataset:TBPS");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }

  @Override
  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }
}
