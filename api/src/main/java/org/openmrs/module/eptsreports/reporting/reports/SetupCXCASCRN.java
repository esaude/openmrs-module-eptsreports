package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.CXCASCRNDataSet;
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
@Deprecated
public class SetupCXCASCRN extends EptsDataExportManager {

  @Autowired private CXCASCRNDataSet cXCASCRNDataSet;
  @Autowired private GenericCohortQueries genericCohortQueries;

  @Override
  public String getExcelDesignUuid() {
    return "61fea06a-472b-11e9-8b42-876961a47296";
  }

  @Override
  public String getUuid() {
    return "6febad76-472b-11e9-a41e-db8c77c78800";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "CXCASCRN";
  }

  @Override
  public String getDescription() {
    return "CXCASCRN";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(cXCASCRNDataSet.getParameters());
    rd.addDataSetDefinition("CX", Mapped.mapStraightThrough(cXCASCRNDataSet.constructTMqDatset()));

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
              reportDefinition, "CXCASCRN.xls", "CXCASCRN ", getExcelDesignUuid(), null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
