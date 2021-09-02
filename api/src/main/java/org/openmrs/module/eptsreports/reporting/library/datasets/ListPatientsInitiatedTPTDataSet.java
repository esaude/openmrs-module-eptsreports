package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.queries.ListOfPatientsWhoInitiatedTPTQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ListPatientsInitiatedTPTDataSet extends BaseDataSet {

  public DataSetDefinition eTptDataSetDefinition(List<Parameter> parameterList) {
    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("INICIOTPI");
    dsd.addParameters(parameterList);
    dsd.setSqlQuery(ListOfPatientsWhoInitiatedTPTQueries.QUERY.finPatientsWhoInitietedTPTList);
    return dsd;
  }
}
