package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.PepfarEarlyRetentionCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
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

	@Autowired
	private EptsCommonDimension eptsCommonDimension;

    public DataSetDefinition constructPepfarEarlyRetentionDatset() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
        dsd.setName("Pepfar early retention Data Set");
        dsd.addParameters(getParameters());

        //apply disagregations here
		dsd.addDimension("age",
				EptsReportUtils.map(eptsCommonDimension.pvlsAges(), "endDate=${endDate},location=${location}"));
		//start forming the columns
		dsd.addColumn("T1", "Early retention", EptsReportUtils.map(eptsGeneralIndicator.getIndicator("Early retention", EptsReportUtils.map(pepfarEarlyRetentionCohortQueries.getPatientsRetainedOnArtFor3MonthsFromArtInitiation(), mappings)), mappings), "");
        return dsd;
    }
}
