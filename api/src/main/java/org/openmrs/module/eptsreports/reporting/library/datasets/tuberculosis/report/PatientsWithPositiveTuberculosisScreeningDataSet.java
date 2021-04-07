package org.openmrs.module.eptsreports.reporting.library.datasets.tuberculosis.report;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.tuberculosis.reports.PatientsWithPositiveTuberculosisScreeningQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class PatientsWithPositiveTuberculosisScreeningDataSet extends BaseDataSet {

  public DataSetDefinition patientsWithPositiveTuberculosisScreeningDataSet(
      List<Parameter> parameterList) {
    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("LISTA DE PACIENTES COM RASTREIO DE TB POSITIVO");
    dsd.addParameters(parameterList);
    dsd.setSqlQuery(
        PatientsWithPositiveTuberculosisScreeningQueries
            .findPatientsWithPositiveTuberculosisScreening());
    return dsd;
  }
}
