package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.stereotype.Component;

@Component
public class TotalListOfPatientsDefaultersOrIITTemplateDataSet extends BaseDataSet {

  public DataSetDefinition constructDataSet() {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("List of Patients Defaulters or IIT TEMPLATE DataSet");
    dataSetDefinition.addParameters(getParameters());

    return dataSetDefinition;
  }
}
