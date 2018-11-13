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
		String mappings = "endDate=${endDate},location=${location}";
		dsd.setName("Tx_Pvls Dataset");
		dsd.addParameters(getParameters());
		// tie dimensions to this data definition
		dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
		dsd.addDimension("age", EptsReportUtils.map(age(), "effectiveDate=${endDate}"));
		dsd.addDimension("pb", EptsReportUtils.map(breastfeedingAndPregnant(), mappings));
		
		// build the column parameters here
		// start with pregnant and breastfeeding
		ColumnParameters pregnantAndBreastfeeding = new ColumnParameters("pb", "Pregnant and Breastfeeding",
		        "gender=F|pb=pb");

		// providing all columns into lists to be used
		List<ColumnParameters> pb = Arrays.asList(pregnantAndBreastfeeding);
		
		// constructing the first row of pregnant and breast feeding mothers
		EptsReportUtils.addRow(dsd, "1N", "Numerator",
		    EptsReportUtils.map(hivIndicators.patientsHavingViralLoadWithin12Months(), mappings), pb, Arrays.asList("01"));
		EptsReportUtils.addRow(dsd, "1D", "Denominator",
		    EptsReportUtils.map(hivIndicators.patientsWithSuppressedViralLoadWithin12Months(), mappings), pb,
		    Arrays.asList("01"));
		// constructing the row for children under 1 year
		EptsReportUtils.addRow(dsd, "2N", "Numerator",
		    EptsReportUtils.map(hivIndicators.patientsHavingViralLoadWithin12Months(), mappings), infants(),
		    Arrays.asList("01", "02", "03"));
		EptsReportUtils.addRow(dsd, "2D", "Denominator",
		    EptsReportUtils.map(hivIndicators.patientsWithSuppressedViralLoadWithin12Months(), mappings), infants(),
		    Arrays.asList("01", "02", "03"));
		// constructing the rows for children aged between 1 and 9 years
		EptsReportUtils.addRow(dsd, "3N", "Numerator",
		    EptsReportUtils.map(hivIndicators.patientsHavingViralLoadWithin12Months(), mappings), children(),
		    Arrays.asList("01", "02", "03"));
		EptsReportUtils.addRow(dsd, "3D", "Denominator",
		    EptsReportUtils.map(hivIndicators.patientsWithSuppressedViralLoadWithin12Months(), mappings), children(),
		    Arrays.asList("01", "02", "03"));
		// Constructing the rows for adults patients disaggregated by age and gender
		////// Numerator
		EptsReportUtils.addRow(dsd, "4N", "Numerator",
		    EptsReportUtils.map(hivIndicators.patientsHavingViralLoadWithin12Months(), mappings), adultsDisagregation(),
		    adultColumns());
		///// Denominator
		EptsReportUtils.addRow(dsd, "4D", "Denominator",
		    EptsReportUtils.map(hivIndicators.patientsWithSuppressedViralLoadWithin12Months(), mappings),
		    adultsDisagregation(), adultColumns());
		return dsd;
		
	}
	
	public List<Parameter> getParameters() {
		return Arrays.asList(ReportingConstants.END_DATE_PARAMETER, ReportingConstants.LOCATION_PARAMETER);
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
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("1-9", 1, 9), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("10-14",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("10-14", 10, 14), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("15-19",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("15-19", 15, 19), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("20-24",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("20-24", 20, 24), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("25-29",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("25-29", 25, 29), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("30-34",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("30-34", 30, 34), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("35-39",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("35-39", 35, 39), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("40-49",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("40-49", 40, 49), "effectiveDate=${endDate}"));
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
		dim.addParameter(new Parameter("endDate", "End Date", Date.class));
		dim.addParameter(new Parameter("location", "Location", Location.class));
		dim.addCohortDefinition("pb",
		    EptsReportUtils.map(ccq.pregnantAndBreastFeedingWomen(), "endDate=${endDate},location=${location}"));
		
		return dim;
	}
	
	/**
	 * List of {@link ColumnParameters} for disaggregation
	 * 
	 * @return List of {@link ColumnParameters}
	 */
	private List<ColumnParameters> adultsDisagregation() {
		// columns for patients aged 10 to 14 years, defined by gender
		ColumnParameters tenTo14M = new ColumnParameters("10-14M", "10-14 years males", "gender=M|age=10-14");
		ColumnParameters tenTo14F = new ColumnParameters("10-14F", "10-14 years female", "gender=F|age=10-14");
		ColumnParameters tenTo14T = new ColumnParameters("10-14T", "10-14 years patients", "age=10-14");
		// columns for patients aged 15 to 19 years, defined by gender
		ColumnParameters fftnTo19M = new ColumnParameters("15-19M", "15-19 years males", "gender=M|age=15-19");
		ColumnParameters fftnTo19F = new ColumnParameters("15-19F", "15-19 years female", "gender=F|age=15-19");
		ColumnParameters fftnTo19T = new ColumnParameters("15-19T", "15-19 years patients", "age=15-19");
		// columns for patients aged 20 to 24 years, defined by gender
		ColumnParameters twtyTo24M = new ColumnParameters("20-24M", "20-24 years males", "gender=M|age=20-24");
		ColumnParameters twtyTo24F = new ColumnParameters("20-24F", "20-24 years female", "gender=F|age=20-24");
		ColumnParameters twtyTo24T = new ColumnParameters("20-24T", "20-24 years patients", "age=20-24");
		// columns for patients aged 25 to 29 years, defined by gender
		ColumnParameters twty5To29M = new ColumnParameters("25-29M", "25-29 years males", "gender=M|age=25-29");
		ColumnParameters twty5To29F = new ColumnParameters("25-29F", "25-29 years female", "gender=F|age=25-29");
		ColumnParameters twty5To29T = new ColumnParameters("25-29T", "25-29 years patients", "age=25-29");
		// columns for patients aged 30 to 34 years, defined by gender
		ColumnParameters thtyTo34M = new ColumnParameters("30-34M", "30-34 years males", "gender=M|age=30-34");
		ColumnParameters thtyTo34F = new ColumnParameters("30-34F", "30-34 years female", "gender=F|age=30-44");
		ColumnParameters thtyTo34T = new ColumnParameters("30-34T", "30-34 years patients", "age=30-34");
		// columns for patients aged 35 to 39 years, defined by gender
		ColumnParameters thty5To39M = new ColumnParameters("35-39M", "35-39 years males", "gender=M|age=35-39");
		ColumnParameters thty5To39F = new ColumnParameters("35-39F", "35-39 years female", "gender=F|age=35-39");
		ColumnParameters thty5To39T = new ColumnParameters("35-39T", "35-39 years patients", "age=35-39");
		// columns for patients aged 40 to 49 years, defined by age
		ColumnParameters ftyTo49M = new ColumnParameters("40-49M", "40-49 years males", "gender=M|age=40-49");
		ColumnParameters ftyTo49F = new ColumnParameters("40-49F", "40-49 years female", "gender=F|age=40-49");
		ColumnParameters ftyTo49T = new ColumnParameters("40-49T", "40-49 years patients", "age=40-49");
		// coloumn parameters for patients for 50 and above years, defined by gender
		ColumnParameters fftyAndAboveM = new ColumnParameters(">=50M", ">=50 years males", "gender=M|age=>49");
		ColumnParameters fftyAndAboveF = new ColumnParameters(">=50F", ">=50 years female", "gender=F|age=>49");
		ColumnParameters fftyAndAboveT = new ColumnParameters(">=50T", ">=50 years patients", "age=>49");
		
		return Arrays.asList(tenTo14M, tenTo14F, tenTo14T, fftnTo19M, fftnTo19F, fftnTo19T, twtyTo24M, twtyTo24F, twtyTo24T,
		    twty5To29M, twty5To29F, twty5To29T, thtyTo34M, thtyTo34F, thtyTo34T, thty5To39M, thty5To39F, thty5To39T,
		    ftyTo49M, ftyTo49F, ftyTo49T, fftyAndAboveM, fftyAndAboveF, fftyAndAboveT);
		
	}
	
	private List<String> adultColumns() {
		return Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
		    "17", "18", "19", "20", "21", "22", "23", "24");
	}

	private List<ColumnParameters> infants() {
		// column parameters for children under 1
		ColumnParameters under1M = new ColumnParameters("<1M", "<1-Male", "gender=M|age=<1");
		ColumnParameters under1F = new ColumnParameters("<1F", "<1-Female", "gender=F|age=<1");
		ColumnParameters under1T = new ColumnParameters("<1T", "<1-Total", "age=<1");

		return Arrays.asList(under1M, under1F, under1T);
	}

	private List<ColumnParameters> children() {
		// columns parameter for children 1- 9 years
		ColumnParameters oneTo9M = new ColumnParameters("1-9M", "1-9 Male", "gender=M|age=1-9");
		ColumnParameters oneTo9F = new ColumnParameters("1-9F", "1-9 Female", "gender=F|age=1-9");
		ColumnParameters oneTo9T = new ColumnParameters("1-9T", "1-9 Total", "age=1-9");

		return Arrays.asList(oneTo9M, oneTo9F, oneTo9T);
	}
}
