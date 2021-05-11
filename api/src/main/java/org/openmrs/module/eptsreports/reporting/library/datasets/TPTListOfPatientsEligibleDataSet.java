package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.List;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class TPTListOfPatientsEligibleDataSet extends BaseDataSet {

  public DataSetDefinition constructDataset(List<Parameter> parameterList) {

    SqlDataSetDefinition sdd = new SqlDataSetDefinition();

    sdd.setName("TPT");

    sdd.addParameters(parameterList);

    sdd.setSqlQuery("select * from patient");

    return sdd;
  }
}
