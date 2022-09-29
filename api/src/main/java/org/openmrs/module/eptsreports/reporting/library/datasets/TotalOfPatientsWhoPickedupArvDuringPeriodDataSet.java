package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsWhoPickedupArvDuringPeriodCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TotalOfPatientsWhoPickedupArvDuringPeriodDataSet extends BaseDataSet {

  private final ListOfPatientsWhoPickedupArvDuringPeriodCohortQueries
      listOfPatientsWhoPickedupArvDuringPeriodCohortQueries;

  private final EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  public TotalOfPatientsWhoPickedupArvDuringPeriodDataSet(
      ListOfPatientsWhoPickedupArvDuringPeriodCohortQueries
          listOfPatientsWhoPickedupArvDuringPeriodCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator) {
    this.listOfPatientsWhoPickedupArvDuringPeriodCohortQueries =
        listOfPatientsWhoPickedupArvDuringPeriodCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
  }

  public DataSetDefinition constructDataSet() {

    CohortIndicatorDataSetDefinition pdd = new CohortIndicatorDataSetDefinition();
    pdd.setName("Total of patients who picked up ARV during the period");
    pdd.setParameters(getParameters());
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    // Totals
    pdd.addColumn(
        "TOTAL",
        "Total de pacientes que levantaram ARV dentro do periodo",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total de pacientes que levantaram ARV dentro do periodo",
                EptsReportUtils.map(
                    listOfPatientsWhoPickedupArvDuringPeriodCohortQueries.getBaseCohort(),
                    mappings)),
            mappings),
        "");

    return pdd;
  }
}
