package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.TPTInitiationCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPTInitiationTotalNewDataSet extends BaseDataSet {

  private EptsGeneralIndicator eptsGeneralIndicator;
  private TPTInitiationCohortQueries tptInitiationCohortQueries;

  @Autowired
  public TPTInitiationTotalNewDataSet(
      EptsGeneralIndicator eptsGeneralIndicator,
      TPTInitiationCohortQueries tptInitiationCohortQueries) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.tptInitiationCohortQueries = tptInitiationCohortQueries;
  }

  public DataSetDefinition constructDataSet() {

    PatientDataSetDefinition pdd = new PatientDataSetDefinition();
    pdd.setName("TOTAL");

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("Total de Pacientes que Iniciaram TPT");
    dataSetDefinition.addParameters(getParameters());

    CohortIndicator Total =
        eptsGeneralIndicator.getIndicator(
            "total",
            EptsReportUtils.map(
                tptInitiationCohortQueries.getBaseCohort(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));

    dataSetDefinition.addColumn(
        "total",
        "Total de Pacientes que iniciaram TPT ",
        EptsReportUtils.map(
            Total, "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    return dataSetDefinition;
  }
}
