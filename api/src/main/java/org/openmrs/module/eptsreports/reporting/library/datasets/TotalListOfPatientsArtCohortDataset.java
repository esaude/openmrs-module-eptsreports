package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsArtCohortCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TotalListOfPatientsArtCohortDataset extends BaseDataSet {

  private EptsGeneralIndicator eptsGeneralIndicator;

  private ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries;

  @Autowired
  public TotalListOfPatientsArtCohortDataset(
      EptsGeneralIndicator eptsGeneralIndicator,
      ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.listOfPatientsArtCohortCohortQueries = listOfPatientsArtCohortCohortQueries;
  }

  String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

  public DataSetDefinition contructDataset() {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("ART Cohort Total");
    dataSetDefinition.addParameters(getParameters());

    CohortIndicator ARVTotal =
        eptsGeneralIndicator.getIndicator(
            "ARTTotal",
            EptsReportUtils.map(
                listOfPatientsArtCohortCohortQueries.getPatientsInitiatedART(), mappings));

    dataSetDefinition.addColumn(
        "ARTTotal",
        "TOTAL DE PACIENTES NA COORTE DE TARV",
        EptsReportUtils.map(ARVTotal, mappings),
        "");

    return dataSetDefinition;
  }
}
