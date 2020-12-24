package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.PatientEnrolledInArtProgramDataset;
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
public class SetupPatientEnrolledInArtProgramReport extends EptsDataExportManager {
  @Autowired protected GenericCohortQueries genericCohortQueries;
  @Autowired private PatientEnrolledInArtProgramDataset patientEnrolledInArtProgramDataset;

  @Override
  public String getUuid() {
    return "0c1db5b7-2774-497b-915b-fc88a154a6ff";
  }

  @Override
  public String getName() {
    return "Pacientes Inscritos no Programa ARV";
  }

  @Override
  public String getDescription() {
    return "Pacientes Inscritos no Programa ARV";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.patientEnrolledInArtProgramDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "P",
        Mapped.mapStraightThrough(
            this.patientEnrolledInArtProgramDataset.constructPatientEnrolledOnAetProgramDataset()));
    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));
    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "f55eda70-db91-4f8b-94d3-eba0c9a5829d";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "Relatorio_de_Populacao_Chave_MISAU.xls",
              "Relatorio de Populacao Chave - MISAU",
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
