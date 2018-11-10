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

import javax.persistence.Column;
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
		// column parameters for children under 1
		ColumnParameters under1M = new ColumnParameters("<1M", "<1-Male", "gender=M|age=<1");
		ColumnParameters under1F = new ColumnParameters("<1F", "<1-Female", "gender=F|age=<1");
		ColumnParameters under1T = new ColumnParameters("<1T", "<1-Total", "age=<1");
		// columns parameter for children 1- 9 years
		ColumnParameters oneTo9M = new ColumnParameters("1-9M", "1-9 Male", "gender=M|age=1-9");
		ColumnParameters oneTo9F = new ColumnParameters("1-9F", "1-9 Female", "gender=F|age=1-9");
		ColumnParameters oneTo9T = new ColumnParameters("1-9T", "1-9 Total", "age=1-9");
		//columns for patients aged 10 to 14 years, defined by gender
		ColumnParameters tenTo14M = new ColumnParameters("10-14M", "10 to 14 years males", "gender=M|age=10-14");
		ColumnParameters tenTo14F = new ColumnParameters("10-14F", "10 to 14 years female", "gender=F|age=10-14");
		ColumnParameters tenTo14T = new ColumnParameters("10-14T", "10 to 14 years patients", "age=10-14");
		//columns for patients aged 15 to 19 years, defined by gender
		ColumnParameters fftnTo19M= new ColumnParameters("15-19M", "15 to 19 years males", "gender=M|age=15-19");
		ColumnParameters fftnTo19F = new ColumnParameters("15-19F", "15 to 19 years female", "gender=F|age=15-19");
		ColumnParameters fftnTo19T = new ColumnParameters("15-19T", "15 to 19 years patients", "age=15-19");
		//columns for patients aged 20 to 24 years, defined by gender
		ColumnParameters twtyTo24M= new ColumnParameters("20-24M", "20 to 24 years males", "gender=M|age=20-24");
		ColumnParameters twtyTo24F = new ColumnParameters("20-24F", "20 to 24 years female", "gender=F|age=20-24");
		ColumnParameters twtyTo24T = new ColumnParameters("20-24T", "20 to 24 years patients", "age=20-24");
		//columns for patients aged 25 to 29 years, defined by gender
		ColumnParameters twty5To29M= new ColumnParameters("25-29M", "25 to 29 years males", "gender=M|age=25-29");
		ColumnParameters twty5To29F = new ColumnParameters("25-29F", "25 to 29 years female", "gender=F|age=25-29");
		ColumnParameters twty5To29T = new ColumnParameters("25-29T", "25 to 29 years patients", "age=25-29");
		//columns for patients aged 30 to 34 years, defined by gender
		ColumnParameters thtyTo34M= new ColumnParameters("30-34M", "30 to 34 years males", "gender=M|age=30-34");
		ColumnParameters thtyTo34F = new ColumnParameters("30-34F", "30 to 34 years female", "gender=F|age=30-44");
		ColumnParameters thtyTo34T = new ColumnParameters("30-34T", "30 to 34 years patients", "age=30-34");
		//columns for patients aged 35 to 39 years, defined by gender
		ColumnParameters thty5To39M	= new ColumnParameters("35-39M", "35 to 39 years males", "gender=M|age=35-39");
		ColumnParameters thty5To39F	= new ColumnParameters("35-39F", "35 to 39 years female", "gender=F|age=35-39");
		ColumnParameters thty5To39T	= new ColumnParameters("35-39T", "35 to 39 years patients", "age=35-39");
		//columns for patients aged 40 to 49 years, defined by age
		ColumnParameters ftyTo49M	= new ColumnParameters("40-49M", "40 to 49 years males", "gender=M|age=40-49");
		ColumnParameters ftyTo49F	= new ColumnParameters("40-49F", "40 to 49 years female", "gender=F|age=40-49");
		ColumnParameters ftyTo49T	= new ColumnParameters("40-49T", "40 to 49 years patients", "age=40-49");
		//coloumn parameters for patients for 50 and above years, defined by gender
		ColumnParameters fftyAndAboveM	= new ColumnParameters(">=50M", "50 years and above  males", "gender=M|age=>49");
		ColumnParameters fftyAndAboveF	= new ColumnParameters(">=50F", "50 years and above female", "gender=F|age=>49");
		ColumnParameters fftyAndAboveT	= new ColumnParameters(">=50T", "50 years and above patients", "age=>49");


		// providing all columns into lists to be used
		List<ColumnParameters> pb = Arrays.asList(pregnantAndBreastfeeding);
		List<ColumnParameters> under1 = Arrays.asList(under1M, under1F, under1T);
		List<ColumnParameters> oneTo9 = Arrays.asList(oneTo9M, oneTo9F, oneTo9T);
		List<ColumnParameters> tenTo14 = Arrays.asList(tenTo14M, tenTo14F, tenTo14T);
		List<ColumnParameters> fftnTo19 = Arrays.asList(fftnTo19M, fftnTo19F, fftnTo19T);
		List<ColumnParameters> twtyTo24 = Arrays.asList(twtyTo24M, twtyTo24F, twtyTo24T);
		List<ColumnParameters> twty5To29 = Arrays.asList(twty5To29M, twty5To29F, twty5To29T);
		List<ColumnParameters> thtyTo34 = Arrays.asList(thtyTo34M, thtyTo34F, thtyTo34T);
		List<ColumnParameters> thty5To39 = Arrays.asList(thty5To39M, thty5To39F, thty5To39T);
		List<ColumnParameters> ftyTo49 = Arrays.asList(ftyTo49M, ftyTo49F, ftyTo49T);
		List<ColumnParameters> fftyAndAbove = Arrays.asList(fftyAndAboveM, fftyAndAboveF, fftyAndAboveT);

		// constructing the first row of pregnant and breast feeding mothers
		EptsReportUtils.addRow(dsd, "pbn", "Pregnant and Breastfeeding numerator",
		    EptsReportUtils.map(hivIndicators.patientsHavingViralLoadWithin12Months(), mappings), pb, Arrays.asList("01"));
		EptsReportUtils.addRow(dsd, "pbd", "Pregnant and Breastfeeding denominator",
		    EptsReportUtils.map(hivIndicators.patientsWithSuppressedViralLoadWithin12Months(), mappings), pb,
		    Arrays.asList("01"));
		// constructing the row for children under 1 year
		EptsReportUtils.addRow(dsd, "u1n", "Under 1 year numerator",
		    EptsReportUtils.map(hivIndicators.patientsHavingViralLoadWithin12Months(), mappings), under1,
		    Arrays.asList("01", "02", "03"));
		EptsReportUtils.addRow(dsd, "u1d", "Under 1 year denominator",
		    EptsReportUtils.map(hivIndicators.patientsWithSuppressedViralLoadWithin12Months(), mappings), under1,
		    Arrays.asList("01", "02", "03"));
		// constructing the rows for children aged between 1 and 9 years
		EptsReportUtils.addRow(dsd, "oneTo9n", "Aged 1-9 numerator",
		    EptsReportUtils.map(hivIndicators.patientsHavingViralLoadWithin12Months(), mappings), oneTo9,
		    Arrays.asList("01", "02", "03"));
		EptsReportUtils.addRow(dsd, "oneTo9d", "Aged 1-9 denominator",
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
		dim.addCohortDefinition(">49",
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
