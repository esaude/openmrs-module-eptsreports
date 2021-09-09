package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsEligibleForVLCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TotalOfPatientsEligibleForVLDataSet extends BaseDataSet {

  private EptsGeneralIndicator eptsGeneralIndicator;
  private ListOfPatientsEligibleForVLCohortQueries listOfPatientsEligibleForVLCohortQueries;

  @Autowired
  public TotalOfPatientsEligibleForVLDataSet(
      EptsGeneralIndicator eptsGeneralIndicator,
      ListOfPatientsEligibleForVLCohortQueries listOfPatientsEligibleForVLCohortQueries) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.listOfPatientsEligibleForVLCohortQueries = listOfPatientsEligibleForVLCohortQueries;
  }

  public DataSetDefinition constructDataSet() {

    PatientDataSetDefinition pdd = new PatientDataSetDefinition();
    pdd.setName("TPEVL");

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("Total of patients eligible for VL report");
    dataSetDefinition.addParameters(getParameters());

    CohortIndicator Total =
        eptsGeneralIndicator.getIndicator(
            "total",
            EptsReportUtils.map(
                listOfPatientsEligibleForVLCohortQueries.getBaseCohort(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));

    dataSetDefinition.addColumn(
        "total",
        "Total de Pacientes ",
        EptsReportUtils.map(
            Total, "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    return dataSetDefinition;
  }
}
