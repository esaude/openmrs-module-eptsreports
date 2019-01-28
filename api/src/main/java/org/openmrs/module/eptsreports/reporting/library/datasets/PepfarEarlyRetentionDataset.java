package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.stereotype.Component;

@Component
public class PepfarEarlyRetentionDataset extends BaseDataSet{

    public DataSetDefinition constructPepfarEarlyRetentionDatset() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
        dsd.setName("Tx_Pvls Data Set");
        dsd.addParameters(getParameters());
        return dsd;
    }
}
