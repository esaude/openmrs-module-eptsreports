package org.openmrs.module.eptsreports.reporting.library.datasets.tuberculosis.report;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.tuberculosis.reports.PatientsWithPositiveTuberculosisWhoStartdTPIDataSetQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class PatientsWithPositiveTuberculosisWhoStartdTPIDataSet extends BaseDataSet {

  public DataSetDefinition patientsWithPositiveTuberculosisWhoStartdTPIDataSet(
      List<Parameter> parameterList) {
    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("LISTA DE PACIENTES QUE INICIARAM PROFILAXIA COM ISONIAZIDA (TPI)");
    dsd.addParameters(parameterList);
    dsd.setSqlQuery(
        PatientsWithPositiveTuberculosisWhoStartdTPIDataSetQueries
            .findPatientsWithPositiveTuberculosisWhoStartedTPI());
    return dsd;
  }
}
