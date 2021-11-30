package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.listarvsdatasets.ListOfPatientsInARTDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.listarvsdatasets.ListOfPatientsInARTTotalDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupListOfPatientsInART extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;
  @Autowired private ListOfPatientsInARTDataSet listOfPatientsInARTDataSet;
  @Autowired private ListOfPatientsInARTTotalDataSet listOfPatientsInARTTotalDataSet;

  @Override
  public String getExcelDesignUuid() {
    return "48dcb15c-515b-11ec-826c-ff14bb3686bd";
  }

  @Override
  public String getUuid() {
    return "567fc3d0-515b-11ec-91df-4fef42a0849f";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES QUE INICIARAM TARV";
  }

  @Override
  public String getDescription() {
    return "LISTA DE PACIENTES QUE INICIARAM TARV";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(this.getParameters());

    rd.addDataSetDefinition(
        "NR",
        Mapped.mapStraightThrough(listOfPatientsInARTDataSet.constructDataset(getParameters())));

    rd.addDataSetDefinition(
        "RT", Mapped.mapStraightThrough(listOfPatientsInARTTotalDataSet.constructTDatset()));

    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));

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
              "LISTA_DE_PACIENTES_QUE_INICIARAM_TARV.xls",
              "LISTA_DE_PACIENTES_QUE_INICIARAM_TARV",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:NR");

      props.put("sortWeight", "5000");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("cohortStartDate", "Cohort Start Date", Date.class),
        new Parameter("cohorEndDate", "Cohort End Date", Date.class),
        new Parameter("evaluationDate", "Evaluation Date", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class));
  }
}
