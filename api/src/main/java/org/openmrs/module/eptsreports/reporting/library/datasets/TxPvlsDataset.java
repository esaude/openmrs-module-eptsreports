package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.ColumnParameters;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CompositionCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.HivIndicators;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	public DataSetDefinition constructTxPvlsDatset() {
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		dsd.setName("Tx_Pvls Dataset");
		dsd.addParameters(getParameters());
		// tie dimensions to this data definition
		dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
		dsd.addDimension("age", EptsReportUtils.map(age(), "effectiveDate=${endDate}"));
		dsd.addDimension("pb", EptsReportUtils.map(breastfeedingAndPregnant(), mappings));
		
		// build the column parameters here
		// start with pregnant and breastfeeding
		ColumnParameters pregnantAndBreastfeeding = new ColumnParameters("pb", "Pregnant and Breastfeeding", "gender=F|pb=pb");
		// put this into a list of columns and add more if needed
		List<ColumnParameters> pb = Arrays.asList(pregnantAndBreastfeeding);
		// column parameters for children under 1
		ColumnParameters under1M = new ColumnParameters("<1M", "<1-Male", "gender=M|age=<1");
		ColumnParameters under1F = new ColumnParameters("<1F", "<1-Female", "gender=F|age=<1");
		ColumnParameters under1T = new ColumnParameters("<1T", "<1-Total", "age=<1");
		// columns parameter for children 1- 9 years
		ColumnParameters oneTo9M = new ColumnParameters("1-9M", "1-9 Male", "gender=M|age=1-9");
		ColumnParameters oneTo9F = new ColumnParameters("1-9F", "1-9 Female", "gender=F|age=1-9");
		ColumnParameters oneTo9T = new ColumnParameters("1-9T", "1-9 Total", "age=1-9");
		// providing children columns into a list
		List<ColumnParameters> under1 = Arrays.asList(under1M, under1F, under1T);
		List<ColumnParameters> oneTo9 = Arrays.asList(oneTo9M, oneTo9F, oneTo9T);
		
		// constructing the first row of pregnant and breast feeding mothers
		EptsReportUtils.addRow(dsd, "pbn", "Pregnant and Breastfeeding numerator",
		    EptsReportUtils.map(hivIndicators.patientsHavingViralLoadWithin12Months(), mappings), pb, Arrays.asList("01"));
		EptsReportUtils.addRow(dsd, "pbd", "Pregnant and Breastfeeding denominator",
		    EptsReportUtils.map(hivIndicators.patientsWithSuppressedViralLoadWithin12Months(), mappings), pb,
		    Arrays.asList("01"));
		// constructing the row for children under 1 year
		EptsReportUtils.addRow(dsd, "u1n", "UNDER1-N",
		    EptsReportUtils.map(hivIndicators.patientsHavingViralLoadWithin12Months(), mappings), under1,
		    Arrays.asList("01", "02", "03"));
		EptsReportUtils.addRow(dsd, "u1d", "UNDER1-D",
		    EptsReportUtils.map(hivIndicators.patientsWithSuppressedViralLoadWithin12Months(), mappings), under1,
		    Arrays.asList("01", "02", "03"));
		// constructing the rows for children aged between 1 and 9 years
		EptsReportUtils.addRow(dsd, "oneTo9n", "1-9N",
		    EptsReportUtils.map(hivIndicators.patientsHavingViralLoadWithin12Months(), mappings), oneTo9,
		    Arrays.asList("01", "02", "03"));
		EptsReportUtils.addRow(dsd, "oneTo9d", "1-9D",
		    EptsReportUtils.map(hivIndicators.patientsWithSuppressedViralLoadWithin12Months(), mappings), oneTo9,
		    Arrays.asList("01", "02", "03"));
		
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
		dim.addParameter(new Parameter("location", "Location", Location.class));
		dim.addCohortDefinition("pb", EptsReportUtils.map(ccq.pregnantAndBreastFeedingWomen(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		
		return dim;
	}
}
