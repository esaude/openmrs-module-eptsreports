package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.PepfarEarlyRetentionCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PepfarEarlyRetentionDataset extends BaseDataSet{
	@Autowired
	private PepfarEarlyRetentionCohortQueries pepfarEarlyRetentionCohortQueries;

	@Autowired
	private EptsGeneralIndicator eptsGeneralIndicator;

    public DataSetDefinition constructPepfarEarlyRetentionDatset() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
        dsd.setName("Pepfar early retention Data Set");
        dsd.addParameters(getParameters());
        return dsd;
    }
}
