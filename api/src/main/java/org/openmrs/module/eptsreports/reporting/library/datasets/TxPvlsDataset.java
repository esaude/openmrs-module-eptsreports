package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CompositionCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.HivIndicators;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TxPvlsDataset {
	
	@Autowired
	private HivIndicators hivIndicators;
	
	@Autowired
	private EptsCommonDimension eptsCommonDimension;
	
	@Autowired
	private AgeCohortQueries ageCohortQueries;
	
	@Autowired
	private CompositionCohortQueries ccq;
	
	public CohortIndicatorDataSetDefinition constructTxPvlsDatset() {
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location={location}";
		dsd.setName("Tx_Pvls Dataset");
		dsd.addParameters(getParameters());
		// tie dimensions to this data definition
		dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
		dsd.addDimension("age", EptsReportUtils.map(age(), "effectiveDate=${endDate}"));
		dsd.addDimension("pb", EptsReportUtils.map(breastfeedingAndPregnant(), mappings));
		
		return dsd;
		
	}
	
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(ReportingConstants.START_DATE_PARAMETER);
		parameters.add(ReportingConstants.END_DATE_PARAMETER);
		parameters.add(ReportingConstants.LOCATION_PARAMETER);
		return parameters;
	}
	
	// build dimensions specific for this data set
	/**
	 * Age range dimension 10-14, 15-19, 20-24, 25-29, 30-34, 35-39, 40-49, >=50
	 * 
	 * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
	 */
	private CohortDefinitionDimension age() {
		CohortDefinitionDimension dim = new CohortDefinitionDimension();
		dim.addParameter(new Parameter("effectiveDate", "End Date", Date.class));
		dim.setName("age");
		dim.addCohortDefinition("<1", EptsReportUtils.map(ageCohortQueries.patientWithAgeBelow(1), ""));
		dim.addCohortDefinition("1-9",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("oneTo9", 1, 9), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("10-14",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("tenTo14", 10, 14), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("15-19",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("fifteenTo19", 15, 19), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("20-24",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("twentyTo24", 20, 24), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("25-29",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("twenty5To29", 25, 29), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("30-34",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("thirtyTo34", 30, 34), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("35-39",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("thirty5To39", 35, 39), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("40-49",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("fortyTo49", 40, 49), "effectiveDate=${endDate}"));
		dim.addCohortDefinition(">=50",
		    EptsReportUtils.map(ageCohortQueries.patientWithAgeAbove(50), "effectiveDate=${endDate}"));
		return dim;
	}
	
	/**
	 * Pregnant and breast feeding
	 * 
	 * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
	 */
	private CohortDefinitionDimension breastfeedingAndPregnant() {
		CohortDefinitionDimension dim = new CohortDefinitionDimension();
		dim.setName("breastfeedingAndPregnant");
		dim.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dim.addParameter(new Parameter("endDate", "End Date", Date.class));
		dim.addCohortDefinition("pb", EptsReportUtils.map(ccq.pregnantAndBreastFeedingWomen(),
		    "startDate=${startDate},endDate=${endDate},location={location}"));
		
		return dim;
	}
}
