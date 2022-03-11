package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.stereotype.Component;

@Component
public class TXTBMonthlyDataset extends BaseDataSet {

  public DataSetDefinition constructTXTBMonthlyDataset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();

    dsd.setName("TX_TB Monthly Cascade Dataset");

    return dsd;
  }
}
