package org.openmrs.module.eptsreports.reporting.reports;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.LocationDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.UsMonthlySummaryHivDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Deprecated
public class SetupUSMonthlySummaryHivReport extends EptsDataExportManager {

  @Autowired private UsMonthlySummaryHivDataset usMonthlySummaryHivDataset;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Override
  public String getExcelDesignUuid() {
    return "fc2d90a8-5d07-4184-a45b-2d35f98b179b";
  }

  @Override
  public String getUuid() {
    return "c233fc7b-0d36-4a62-8666-bf444ff51b74";
  }

  @Override
  public String getName() {
    return "RESUMO MENSAL US - HIV/SIDA - Ficha de Seguimento";
  }

  @Override
  public String getDescription() {
    return "RESUMO MENSAL US - HIV/SIDA - Ficha de Seguimento";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(usMonthlySummaryHivDataset.getParameters());
    rd.setBaseCohortDefinition(mapStraightThrough(genericCohortQueries.getBaseCohort()));
    rd.addDataSetDefinition(
        "S", mapStraightThrough(usMonthlySummaryHivDataset.constructUsMonthlySummaryHivDataset()));
    rd.addDataSetDefinition("location", mapStraightThrough(new LocationDataSetDefinition()));
    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "US_MONTHLY_SUMMARY_HIV.xls",
              "US MONTHLY SUMMARY HIV excel",
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

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }
}
