package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.stereotype.Component;

@Component
public class SummaryDQRForDuplicateFichaResumoDataSet extends BaseDataSet {

  public DataSetDefinition constructDataSet() {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("DQR for Duplicate Ficha Resumo DataSet");
    dataSetDefinition.addParameters(getParameters());

    return dataSetDefinition;
  }
}
