package org.openmrs.module.eptsreports.reporting.reports;

import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.LocationDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.MISAUResumoMensalPrepDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.SismaCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class SetupMISAUResumoMensalPrepReport extends EptsDataExportManager {

  private GenericCohortQueries genericCohortQueries;

  private MISAUResumoMensalPrepDataset misauResumoMensalPrepDataset;

  @Autowired
  public SetupMISAUResumoMensalPrepReport(
      GenericCohortQueries genericCohortQueries,
      MISAUResumoMensalPrepDataset misauResumoMensalPrepDataset) {
    this.genericCohortQueries = genericCohortQueries;
    this.misauResumoMensalPrepDataset = misauResumoMensalPrepDataset;
  }

  @Override
  public String getExcelDesignUuid() {
    return "2ac4826a-805b-11ec-8529-af582b961358";
  }

  @Override
  public String getUuid() {
    return "3b192624-7d00-11ec-b2d5-5b4b27468a25";
  }

  @Override
  public String getName() {
    return "RESUMO MENSAL DE PrEP - MISAU";
  }

  @Override
  public String getDescription() {
    return "RESUMO MENSAL DE PrEP - MISAU";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(misauResumoMensalPrepDataset.getParameters());
    rd.addDataSetDefinition("DT", Mapped.mapStraightThrough(new DatimCodeDatasetDefinition()));
    rd.addDataSetDefinition("SM", Mapped.mapStraightThrough(new SismaCodeDatasetDefinition()));
    rd.addDataSetDefinition("LC", Mapped.mapStraightThrough(new LocationDataSetDefinition()));
    rd.addDataSetDefinition(
        "RMPREP",
        Mapped.mapStraightThrough(misauResumoMensalPrepDataset.constructResumoMensalPrepDataset()));

    // add a base cohort here to help in calculations running
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));

    return rd;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign rd = null;
    try {
      rd =
          createXlsReportDesign(
              reportDefinition,
              "Template_Resumo_Mensal_de_PrEP_V1.3.XLS",
              "RESUMO MENSAL DE PrEP - MISAU Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      rd.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(rd);
  }
}
