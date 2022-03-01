package org.openmrs.module.eptsreports.reporting.reports;

import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.*;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class SetupMERSemiAnnualReport extends EptsDataExportManager {

  @Autowired private TxMlDataset txMlDataset;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TxTBDataset txTBDataset;

  @Autowired private TbPrevDataset tbPrevDataset;

  @Autowired private CXCASCRNDataset cxcascrnDataset;

  @Autowired private CXCASCRNPositiveDataset cxcascrnPositiveDataset;

  @Autowired private TXCXCADataset txcxcaDataset;

  @Override
  public String getExcelDesignUuid() {
    return "61fea06a-472b-11e9-8b42-876961a472ef";
  }

  @Override
  public String getUuid() {
    return "6febad76-472b-11e9-a41e-db8c77c788cd";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "PEPFAR MER 2.6 SEMI-ANNUAL";
  }

  @Override
  public String getDescription() {
    return "PEPFAR MER 2.6 Semi-Annual Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(txMlDataset.getParameters());
    rd.addDataSetDefinition("T", Mapped.mapStraightThrough(txTBDataset.constructTxTBDataset()));
    rd.addDataSetDefinition("TBPREV", Mapped.mapStraightThrough(tbPrevDataset.constructDatset()));
    rd.addDataSetDefinition("DATIM", Mapped.mapStraightThrough(new DatimCodeDatasetDefinition()));
    rd.addDataSetDefinition(
        "CXCA", Mapped.mapStraightThrough(cxcascrnDataset.constructCXCASCRNDataset()));
    rd.addDataSetDefinition(
        "CXCAP",
        Mapped.mapStraightThrough(cxcascrnPositiveDataset.constructCXCASCRNPositiveDataset()));
    rd.addDataSetDefinition(
        "TXCXCA", Mapped.mapStraightThrough(txcxcaDataset.constructTXCXCASCRNDataset()));
    // add a base cohort to the report
    rd.setBaseCohortDefinition(
        genericCohortQueries.getBaseCohort(),
        ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}"));

    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "PEPFAR_MER_2.6_SEMIANNUAL.xls",
              "PEPFAR MER 2.6 Semi-Annual Report",
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
