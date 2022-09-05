package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListOfPatientsWhoPickedupArvDuringPeriodDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.SismaCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.TotalOfPatientsWhoPickedupArvDuringPeriodDataSet;
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
public class SetupListPatientsPickedUpARVDuringAPeriod extends EptsDataExportManager {

  @Autowired
  private TotalOfPatientsWhoPickedupArvDuringPeriodDataSet
      totalOfPatientsWhoPickedupArvDuringPeriodDataSet;

  @Autowired
  private ListOfPatientsWhoPickedupArvDuringPeriodDataSet
      listOfPatientsWhoPickedupArvDuringPeriodDataSet;

  @Override
  public String getExcelDesignUuid() {
    return "c0e09148-2ac0-11ed-a3f2-bb91dc3c379d";
  }

  @Override
  public String getUuid() {
    return "9891147a-2abf-11ed-a43c-7b350f7a62ce";
  }

  @Override
  public String getName() {
    return "List of Patients Who Picked Up ARVs During a Period";
  }

  @Override
  public String getDescription() {
    return "List of Patients Who Picked Up ARVs During a Period";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public ReportDefinition constructReportDefinition() {

    ReportDefinition rd = new ReportDefinition();

    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(getParameters());
    rd.addDataSetDefinition(
        "TOTAL",
        Mapped.mapStraightThrough(
            totalOfPatientsWhoPickedupArvDuringPeriodDataSet.constructDataSet()));
    try {
      rd.addDataSetDefinition(
          "PPADP",
          Mapped.mapStraightThrough(
              listOfPatientsWhoPickedupArvDuringPeriodDataSet.contructDataset()));
    } catch (EvaluationException e) {
      throw new RuntimeException(e);
    }
    rd.addDataSetDefinition("DT", Mapped.mapStraightThrough(new DatimCodeDatasetDefinition()));
    rd.addDataSetDefinition("SM", Mapped.mapStraightThrough((new SismaCodeDatasetDefinition())));

    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;

    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "Template_ListPatientsPickedUpARVDuringAPeriod_v1.0.xls",
              "List of Patients Who Picked Up ARVs During a Period Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:9,dataset:PPADP");
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
