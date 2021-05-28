package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.TPTEligiblePatientListCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TPTTotalListOfPatientsEligibleDataSet extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TPTEligiblePatientListCohortQueries tPTEligiblePatientListCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructDataset() {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TPT Eligible DataSet 2020");
    dataSetDefinition.addParameters(getParameters());

    String mappings = "endDate=${endDate},location=${location}";

    CohortIndicator TPTTotal =
        eptsGeneralIndicator.getIndicator(
            "TPTTotal",
            EptsReportUtils.map(
                tPTEligiblePatientListCohortQueries.getTxCurrWithoutTPT(), mappings));

    dataSetDefinition.addColumn(
        "TPTTotal",
        "TOTAL DE PACIENTES ELEGIVEIS A TPT",
        EptsReportUtils.map(TPTTotal, mappings),
        "");

    return dataSetDefinition;
  }
}
