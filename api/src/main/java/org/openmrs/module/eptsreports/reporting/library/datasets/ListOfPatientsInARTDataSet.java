package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.queries.ListOfPatientsInARTQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsInARTDataSet extends BaseDataSet {

  public DataSetDefinition costructDataSet(List<Parameter> parameterList) {
    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("LISTA DE PACIENTES QUE INICIARAM TARV");
    dsd.addParameters(parameterList);
    dsd.setSqlQuery(ListOfPatientsInARTQueries.QUERY.findPatientsInART);
    return dsd;
  }
}
