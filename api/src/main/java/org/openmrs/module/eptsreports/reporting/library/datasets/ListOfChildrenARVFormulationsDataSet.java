package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.queries.ListOfChildrenARVFormulationsQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ListOfChildrenARVFormulationsDataSet extends BaseDataSet {

  public DataSetDefinition costructDataSet(List<Parameter> parameterList) {
    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("LISTA DE CRIANÇAS COM FORMULAÇÕES DE ARV");
    dsd.addParameters(parameterList);
    dsd.setSqlQuery(ListOfChildrenARVFormulationsQueries.QUERY.findChildrenARVFormulations);
    return dsd;
  }
}
