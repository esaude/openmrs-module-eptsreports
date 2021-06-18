package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListPatientsEligibleTPTCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.library.queries.ListOfPatientsEligileToTPTQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsEligileToTPTDataSet extends BaseDataSet {

  @Autowired private ListPatientsEligibleTPTCohortQueries listPatientsEligibleTPTCohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public DataSetDefinition constructDataset(List<Parameter> list) {

    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("Find list of patients who are eligible to TPT");
    dsd.addParameters(list);
    dsd.setSqlQuery(ListOfPatientsEligileToTPTQueries.QUERY.findPatientsEligibleToTPTList);
    return dsd;
  }

  public DataSetDefinition getTotalEligibleTPTDataset() {
    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("Patients Eligible TPT Total");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "endDate=${endDate},location=${location}";
    final CohortDefinition tptEligibleTotal =
        this.listPatientsEligibleTPTCohortQueries.findPatientsEligibleTPT();
    dataSetDefinition.addColumn(
        "TOTALELIGIBLETPT",
        "Total de Pacientes Elegiveis a TPT",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "tptEligibleTotal", EptsReportUtils.map(tptEligibleTotal, mappings)),
            mappings),
        "");

    return dataSetDefinition;
  }
}
