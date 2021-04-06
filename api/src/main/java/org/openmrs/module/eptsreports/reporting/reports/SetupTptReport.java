package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.TptListDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.TptTotalDataseet;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxRttDataset;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupTptReport extends EptsDataExportManager {

  @Autowired private TptListDataSet tptListDataSet;
  @Autowired private TptTotalDataseet tptTotalDataseet;
  @Autowired protected GenericCohortQueries genericCohortQueries;
  @Autowired private TxRttDataset txRttDataset;

  @Override
  public String getExcelDesignUuid() {
    return "65e364d6-9be0-11e9-a7a2-cb23750cc2d0";
  }

  @Override
  public String getUuid() {
    return "82ea2326-9be0-11e9-9cee-5384d0247691";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES QUE INICIARAM TRATAMENTO PREVENTIVO DE TUBERCULOSE (TPT)";
  }

  @Override
  public String getDescription() {
    return "LISTA DE PACIENTES QUE INICIARAM TRATAMENTO PREVENTIVO DE TUBERCULOSE (TPT)";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(txRttDataset.getParameters());

    rd.addDataSetDefinition(
        "INICIOTPI",
        Mapped.mapStraightThrough(
            tptListDataSet.eTptDataSetDefinition(txRttDataset.getParameters())));

    rd.addDataSetDefinition(
        "TPI", Mapped.mapStraightThrough(this.tptTotalDataseet.constructDataset()));

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
              reportDefinition, "TPT.xls", "INICIO TPI", getExcelDesignUuid(), null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:4,dataset:INICIOTPI");

      props.put("sortWeight", "5000");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
