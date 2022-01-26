package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.stereotype.Component;

@Component
public class MISAUResumoMensalPrepDataset extends BaseDataSet {

  public DataSetDefinition constructResumoMensalPrepDataset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();

    dsd.setName("Resumo Mesal PrEP Dataset");
    dsd.addParameters(getParameters());
    return dsd;
  }
}
