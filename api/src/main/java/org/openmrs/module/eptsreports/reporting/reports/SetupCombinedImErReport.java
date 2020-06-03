package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.*;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupCombinedImErReport extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private Eri2MonthsDataset eri2MonthsDataset;

  @Autowired private Eri4MonthsDataset eri4MonthsDataset;

  @Autowired private TxNewDataset txNewDataset;

  @Autowired private TxCurrDataset txCurrDataset;

  @Override
  public String getExcelDesignUuid() {
    return "f6a597ba-5fa2-47d4-ab45-da128cabe7ac";
  }

  @Override
  public String getUuid() {
    return "c6743d00-7107-4760-b19e-a9c1b4432ac0";
  }

  @Override
  public String getName() {
    return "IM-ER-Report";
  }

  @Override
  public String getDescription() {
    return "PEPFAR Early Retention Indicators";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();

    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(txNewDataset.getParameters());
    rd.addDataSetDefinition("N", Mapped.mapStraightThrough(txNewDataset.constructTxNewDataset()));

    rd.addDataSetDefinition(
        "C", Mapped.mapStraightThrough(txCurrDataset.constructTxCurrDataset(true)));

    rd.addDataSetDefinition(
        "ERI2", Mapped.mapStraightThrough(eri2MonthsDataset.constructEri2MonthsDatset()));
    rd.addDataSetDefinition(
        "ERI4", Mapped.mapStraightThrough(eri4MonthsDataset.constructEri4MonthsDataset()));
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
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition, "IM_ER_Report.xls", "IM_ER-Report", getExcelDesignUuid(), null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
