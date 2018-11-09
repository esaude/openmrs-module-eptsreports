package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.HivIndicators;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TxPvlsDataset {

	@Autowired
	private HivIndicators hivIndicators;

	@Autowired
	private EptsCommonDimension eptsCommonDimension;
	
	public CohortIndicatorDataSetDefinition constructTxPvlsDatset() {
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Tx_Pvls Dataset");
		dsd.addParameters(getParameters());
		//tie dimensions to this data definition
		dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
		dsd.addDimension("age", EptsReportUtils.map(eptsCommonDimension.age(), "effectiveDate=${endDate}"));
		dsd.addDimension("pb", EptsReportUtils.map(eptsCommonDimension.breastfeedingAndPregnant(), "startDate=${startDate},endDate=${endDate},location={location}"));
		
		return dsd;
		
	}
	
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(ReportingConstants.START_DATE_PARAMETER);
		parameters.add(ReportingConstants.END_DATE_PARAMETER);
		return parameters;
	}
}
