package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.TXTBMontlyCascadeReportDataSet;
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
public class SetupTxTBMontlyCascadeReport extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TXTBMontlyCascadeReportDataSet txtbMontlyCascadeReportDataSet;

  @Autowired private DatinCodeDataSet datimCodeDataSet;

  @Override
  public String getExcelDesignUuid() {
    return "b42abe33-1d23-42ed-bd19-a44b31aa33dc";
  }

  @Override
  public String getUuid() {
    return "31bd56b3-e65c-44f9-88b8-4f1449c71511";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "TX TB Montly Cascade Report";
  }

  @Override
  public String getDescription() {
    return "TX TB Montly Cascade Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(getUuid());
    reportDefinition.setName(getName());
    reportDefinition.setDescription(getDescription());
    reportDefinition.setParameters(this.getParameters());
    reportDefinition.addDataSetDefinition(
        "TBM", Mapped.mapStraightThrough(txtbMontlyCascadeReportDataSet.constructDatset()));

    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));

    reportDefinition.addDataSetDefinition(
        "D",
        Mapped.mapStraightThrough(this.datimCodeDataSet.constructDataset(this.getParameters())));
    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "TX_TB_Montly_Cascade_Report.xls",
              "TX TB Montly Cascade Report",
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

  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }
}
