package org.openmrs.module.eptsreports.reporting.reports;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxPvlsBySourceDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupPvlsBySourceReport extends EptsDataExportManager {

  private TxPvlsBySourceDataset txPvlsBySourceDataset;

  private GenericCohortQueries genericCohortQueries;

  @Autowired
  public SetupPvlsBySourceReport(
      TxPvlsBySourceDataset txPvlsBySourceDataset, GenericCohortQueries genericCohortQueries) {
    this.txPvlsBySourceDataset = txPvlsBySourceDataset;
    this.genericCohortQueries = genericCohortQueries;
  }

  @Override
  public String getExcelDesignUuid() {
    return "b4c94868-48a9-11ec-b7f3-77069269667a";
  }

  @Override
  public String getUuid() {
    return "be6adcf6-48a9-11ec-848e-7738679e3c37";
  }

  @Override
  public String getName() {
    return "Tx PVLS By Source";
  }

  @Override
  public String getDescription() {
    return "Tx PVLS By Source Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.addParameters(txPvlsBySourceDataset.getParameters());
    rd.addDataSetDefinition("PLF", mapStraightThrough(txPvlsBySourceDataset.getPvlsLabFsr()));
    rd.addDataSetDefinition("PFM", mapStraightThrough(txPvlsBySourceDataset.getPvlFichaMestre()));
    rd.addDataSetDefinition("DATIM", Mapped.mapStraightThrough(new DatimCodeDatasetDefinition()));
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
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "TX_PVLS_BY_SOURCE_Template.xls",
              "TX PVLS BY SOURCE",
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
}
