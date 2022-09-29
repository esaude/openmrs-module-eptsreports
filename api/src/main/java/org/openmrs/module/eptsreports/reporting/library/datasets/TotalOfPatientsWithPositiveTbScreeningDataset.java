package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsWithPositiveTbScreeningCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TotalOfPatientsWithPositiveTbScreeningDataset extends BaseDataSet {

  private final EptsGeneralIndicator eptsGeneralIndicator;

  private final ListOfPatientsWithPositiveTbScreeningCohortQueries
      listOfPatientsWithPositiveTbScreeningCohortQueries;

  @Autowired
  public TotalOfPatientsWithPositiveTbScreeningDataset(
      EptsGeneralIndicator eptsGeneralIndicator,
      ListOfPatientsWithPositiveTbScreeningCohortQueries
          listOfPatientsWithPositiveTbScreeningCohortQueries) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.listOfPatientsWithPositiveTbScreeningCohortQueries =
        listOfPatientsWithPositiveTbScreeningCohortQueries;
  }

  public DataSetDefinition contructDataset() {

    CohortIndicatorDataSetDefinition cd = new CohortIndicatorDataSetDefinition();
    cd.setName("Total of Patients With Positive Tb Screening");
    cd.setParameters(getParameters());
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    // Totals
    cd.addColumn(
        "TOTAL",
        "Total de pacientes que levantaram ARV dentro do periodo",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total de pacientes que levantaram ARV dentro do periodo",
                EptsReportUtils.map(
                    listOfPatientsWithPositiveTbScreeningCohortQueries.getBaseCohort(), mappings)),
            mappings),
        "");

    return cd;
  }
}
