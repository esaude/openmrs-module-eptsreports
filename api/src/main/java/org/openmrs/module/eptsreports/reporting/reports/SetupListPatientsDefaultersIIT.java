package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListPatientsDefaultersIITDataSet;
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
public class SetupListPatientsDefaultersIIT extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private ListPatientsDefaultersIITDataSet listPatientsDefaultersIITDataSet;

  @Override
  public String getExcelDesignUuid() {
    return "1167dacd-0da1-4868-b9e2-98923923ba70";
  }

  @Override
  public String getUuid() {
    return "fe014f5c-b569-41a4-bd2c-7facd1d22b11";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES FALTOSOS OU ABANDONOS AO TARV";
  }

  @Override
  public String getDescription() {
    return "LISTA DE PACIENTES FALTOSOS OU ABANDONOS AO TARV";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(this.getParameters());

    rd.addDataSetDefinition(
        "DEFAULTERIITTOTAL",
        Mapped.mapStraightThrough(
            this.listPatientsDefaultersIITDataSet.getTotalDefaultersIITDataset(
                this.getParameters())));

    rd.addDataSetDefinition(
        "DEFAULTERIIT",
        Mapped.mapStraightThrough(
            listPatientsDefaultersIITDataSet.constructDataset(this.getParameters())));

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
              "List_Patients_Defaulters_IIT.xls",
              "LISTA DE PACIENTES FALTOSOS OU ABANDONOS AO TARV",
              getExcelDesignUuid(),
              null);

      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:8,dataset:DEFAULTERIIT");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }

  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.addAll(getCustomParameteres());
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }

  public static List<Parameter> getCustomParameteres() {
    return Arrays.asList(
        getMinimumDaysOfDelayConfigurableParameter(), getMaximumDaysOfDelayConfigurableParameter());
  }

  private static Parameter getMinimumDaysOfDelayConfigurableParameter() {
    final Parameter parameter = new Parameter();
    parameter.setName("minDelayDays");
    parameter.setLabel("Minimum Days of Delay");
    parameter.setType(Integer.class);
    parameter.setRequired(Boolean.TRUE);

    parameter.setDefaultValue(Integer.valueOf(5));
    return parameter;
  }

  private static Parameter getMaximumDaysOfDelayConfigurableParameter() {
    final Parameter parameter = new Parameter();
    parameter.setName("maxDelayDays");
    parameter.setLabel("Maximum Days of Delay");
    parameter.setType(Integer.class);
    parameter.setRequired(Boolean.TRUE);

    parameter.setDefaultValue(Integer.valueOf(59));
    return parameter;
  }
}
