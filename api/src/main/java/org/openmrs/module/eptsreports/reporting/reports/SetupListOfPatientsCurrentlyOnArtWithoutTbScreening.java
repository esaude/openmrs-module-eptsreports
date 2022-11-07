package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListOfPatientsCurrentlyOnArtWithoutTbScreeningDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.SismaCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.TotalOfPatientsCurrentlyOnArtWithoutTbScreeningDataSet;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Deprecated
public class SetupListOfPatientsCurrentlyOnArtWithoutTbScreening extends EptsDataExportManager {

  private ListOfPatientsCurrentlyOnArtWithoutTbScreeningDataset
      listOfPatientsCurrentlyOnArtWithoutTbScreeningDataset;

  private TotalOfPatientsCurrentlyOnArtWithoutTbScreeningDataSet
      totalOfPatientsCurrentlyOnArtWithoutTbScreeningDataSet;

  @Autowired
  public SetupListOfPatientsCurrentlyOnArtWithoutTbScreening(
      ListOfPatientsCurrentlyOnArtWithoutTbScreeningDataset
          listOfPatientsCurrentlyOnArtWithoutTbScreeningDataset,
      TotalOfPatientsCurrentlyOnArtWithoutTbScreeningDataSet
          totalOfPatientsCurrentlyOnArtWithoutTbScreeningDataSet) {
    this.listOfPatientsCurrentlyOnArtWithoutTbScreeningDataset =
        listOfPatientsCurrentlyOnArtWithoutTbScreeningDataset;

    this.totalOfPatientsCurrentlyOnArtWithoutTbScreeningDataSet =
        totalOfPatientsCurrentlyOnArtWithoutTbScreeningDataSet;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "9da72542-d5c6-11ec-838d-b7aa1d50c553";
  }

  @Override
  public String getExcelDesignUuid() {
    return "aab01db6-d5c6-11ec-9416-9b57335e52ae";
  }

  @Override
  public String getName() {
    return "TB5: List of Patients Currently on ART without TB Screening";
  }

  @Override
  public String getDescription() {
    return "List of patients currently on ART who do not have a documented TB screening in the last 6 months";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(getParameters());
    rd.addDataSetDefinition("DT", Mapped.mapStraightThrough(new DatimCodeDatasetDefinition()));
    rd.addDataSetDefinition("SC", Mapped.mapStraightThrough(new SismaCodeDatasetDefinition()));
    rd.addDataSetDefinition(
        "LP",
        Mapped.mapStraightThrough(
            listOfPatientsCurrentlyOnArtWithoutTbScreeningDataset
                .constructListOfPatientsDataset()));

    rd.addDataSetDefinition(
        "TP",
        Mapped.mapStraightThrough(
            totalOfPatientsCurrentlyOnArtWithoutTbScreeningDataSet.constructDataset()));
    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign rd = null;
    try {
      rd =
          createXlsReportDesign(
              reportDefinition,
              "Template_List_Patients_Currently_ART_No_TB_Screening.xls",
              "Patients Currently on ART without TB Screening",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:8,dataset:LP");
      props.put("sortWeight", "5000");
      rd.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(rd);
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("endDate", "Report End Date", Date.class),
        new Parameter("location", "Health Facility", Location.class));
  }
}
