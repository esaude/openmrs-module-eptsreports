package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TotalOfPatientsCurrentlyOnArtWithoutTbScreeningDataSet extends BaseDataSet {

  private EptsGeneralIndicator eptsGeneralIndicator;
  private ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
      listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries;

  @Autowired
  public TotalOfPatientsCurrentlyOnArtWithoutTbScreeningDataSet(
      EptsGeneralIndicator eptsGeneralIndicator,
      ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
          listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries =
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries;
  }

  public DataSetDefinition constructDataset() {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("Patients Currently on ART Without TB Screening");
    dataSetDefinition.addParameters(getParameters());

    CohortIndicator withoutScreening =
        eptsGeneralIndicator.getIndicator(
            "withoutScreening",
            EptsReportUtils.map(
                listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
                    .getPatientsCurrentlyOnArtWithoutTbScreening(),
                "endDate=${endDate},location=${location}"));

    dataSetDefinition.addColumn(
        "withoutScreening",
        "TOTAL Without Screening",
        EptsReportUtils.map(withoutScreening, "endDate=${endDate},location=${location}"),
        "");

    CohortIndicator withConsultation =
        eptsGeneralIndicator.getIndicator(
            "withConsultation",
            EptsReportUtils.map(
                listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
                    .getPatientsCurrentlyOnArtWithoutTbScreeningAndWithClinicalConsultationInLast6Months(),
                "endDate=${endDate},location=${location}"));

    dataSetDefinition.addColumn(
        "withConsultation",
        "TOTAL Without Screening and at least on clinical consultation",
        EptsReportUtils.map(withConsultation, "endDate=${endDate},location=${location}"),
        "");

    CohortIndicator withoutConsultation =
        eptsGeneralIndicator.getIndicator(
            "withoutConsultation",
            EptsReportUtils.map(
                listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
                    .getPatientsCurrentlyOnArtWithoutTbScreeningAndWithoutClinicalConsultationInLast6Months(),
                "endDate=${endDate},location=${location}"));

    dataSetDefinition.addColumn(
        "withoutConsultation",
        "TOTAL Without Screening and at least on clinical consultation",
        EptsReportUtils.map(withoutConsultation, "endDate=${endDate},location=${location}"),
        "");

    return dataSetDefinition;
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("endDate", "End date", Date.class),
        new Parameter("location", "Location", Location.class));
  }
}
