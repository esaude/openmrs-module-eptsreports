package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.module.eptsreports.ColumnParameters;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CompositionCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.SqlCohortQueries;
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
	
	@Autowired
	private SqlCohortQueries sqlCohortQueries;
	
	public DataSetDefinition constructTxPvlsDatset() {
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		dsd.setName("Tx_Pvls Data Set");
		dsd.addParameters(getParameters());
		// tie dimensions to this data definition
		dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
		dsd.addDimension("age", EptsReportUtils.map(age(), "effectiveDate=${endDate}"));
		
		dsd.addColumn("0N", "Total patients with suppressed Viral load - Numerator",
		    EptsReportUtils.map(hivIndicators.cohortIndicator("suppressed",
		        sqlCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(), mappings), mappings),
		    "");
		
		dsd.addColumn("0D", "Total patients with Viral load - Denominator",
		    EptsReportUtils.map(
		        hivIndicators.cohortIndicator("suppressed", sqlCohortQueries.getPatientsViralLoadWithin12Months(), mappings),
		        mappings),
		    "");
		
		// constructing the first row of pregnant and breast feeding mothers
		dsd.addColumn("1N", "Pregnant Women - Numerator", EptsReportUtils.map(hivIndicators.cohortIndicator("pregnant",
		    ccq.pregnantWomenAndHasSuppressedViralLoadInTheLast12MonthsNumerator(), mappings), mappings), "");
		
		dsd.addColumn("1D", "Pregnant Women - Denominator", EptsReportUtils.map(hivIndicators.cohortIndicator("pregnant",
		    ccq.pregnantWomenAndHasViralLoadInTheLast12MonthsDenominator(), mappings), mappings), "");
		
		dsd.addColumn("2N", "Breastfeeding - Women Numerator",
		    EptsReportUtils.map(hivIndicators.cohortIndicator("breastfeeding",
		        ccq.breastfeedingWomenAndHasViralLoadSuppressionInTheLast12MonthsNumerator(), mappings), mappings),
		    "");
		
		dsd.addColumn("2D", "Breastfeeding - Women Denominator",
		    EptsReportUtils.map(hivIndicators.cohortIndicator("breastfeeding",
		        ccq.breastfeedingWomenAndHasViralLoadInTheLast12MonthsDenominator(), mappings), mappings),
		    "");
		
		// constructing the rows for children
		EptsReportUtils.addRow(dsd, "3N", "Children Numerator",
		    EptsReportUtils.map(hivIndicators.cohortIndicator("children",
		        sqlCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(), mappings), mappings),
		    children(), Arrays.asList("01", "02", "03"));
		
		EptsReportUtils.addRow(dsd, "3D", "Children Denominator",
		    EptsReportUtils.map(
		        hivIndicators.cohortIndicator("children", sqlCohortQueries.getPatientsViralLoadWithin12Months(), mappings),
		        mappings),
		    children(), Arrays.asList("01", "02", "03"));
		
		// Numerator
		EptsReportUtils.addRow(dsd, "4N", "Adults with suppressed VL Numerator",
		    EptsReportUtils.map(hivIndicators.cohortIndicator("adults",
		        sqlCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(), mappings), mappings),
		    adultsDisagregation(), adultColumns());
		
		// Denominator
		EptsReportUtils.addRow(dsd, "4D", "Adults with VL Denominator",
		    EptsReportUtils.map(
		        hivIndicators.cohortIndicator("adults", sqlCohortQueries.getPatientsViralLoadWithin12Months(), mappings),
		        mappings),
		    adultsDisagregation(), adultColumns());
		return dsd;
		
	}
	
	public List<Parameter> getParameters() {
		return Arrays.asList(ReportingConstants.START_DATE_PARAMETER, ReportingConstants.END_DATE_PARAMETER,
		    ReportingConstants.LOCATION_PARAMETER);
	}
	
	// build dimensions specific for this data set
	/**
	 * Age range dimension 10-14, 15-19, 20-24, 25-29, 30-34, 35-39, 40-44, 45-49, >=50
	 * 
	 * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
	 */
	private CohortDefinitionDimension age() {
		CohortDefinitionDimension dim = new CohortDefinitionDimension();
		dim.addParameter(new Parameter("effectiveDate", "End Date", Date.class));
		dim.setName("age");
		dim.addCohortDefinition("<1",
		    EptsReportUtils.map(ageCohortQueries.patientWithAgeBelow(1), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("1-4",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("1-4", 1, 4), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("5-9",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("5-9", 5, 9), "effectiveDate=${endDate}"));
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
		dim.addCohortDefinition("40-44",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("40-44", 40, 44), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("45-49",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("40-44", 40, 44), "effectiveDate=${endDate}"));
		dim.addCohortDefinition(">49",
		    EptsReportUtils.map(ageCohortQueries.patientWithAgeAbove(50), "effectiveDate=${endDate}"));
		return dim;
	}
	
	/**
	 * List of {@link ColumnParameters} for disaggregation
	 * 
	 * @return List of {@link ColumnParameters}
	 */
	private List<ColumnParameters> adultsDisagregation() {
		// columns for patients aged 10 to 14 years, defined by gender
		ColumnParameters tenTo14M = new ColumnParameters("10-14M", "10-14 years males", "gender=M|age=10-14");// 01
		ColumnParameters tenTo14F = new ColumnParameters("10-14F", "10-14 years female", "gender=F|age=10-14");// 02
		ColumnParameters tenTo14T = new ColumnParameters("10-14T", "10-14 years patients", "age=10-14");// 03
		// columns for patients aged 15 to 19 years, defined by gender
		ColumnParameters fftnTo19M = new ColumnParameters("15-19M", "15-19 years males", "gender=M|age=15-19");// 04
		ColumnParameters fftnTo19F = new ColumnParameters("15-19F", "15-19 years female", "gender=F|age=15-19");// 05
		ColumnParameters fftnTo19T = new ColumnParameters("15-19T", "15-19 years patients", "age=15-19");// 06
		// columns for patients aged 20 to 24 years, defined by gender
		ColumnParameters twtyTo24M = new ColumnParameters("20-24M", "20-24 years males", "gender=M|age=20-24");// 07
		ColumnParameters twtyTo24F = new ColumnParameters("20-24F", "20-24 years female", "gender=F|age=20-24");// 08
		ColumnParameters twtyTo24T = new ColumnParameters("20-24T", "20-24 years patients", "age=20-24");// 10
		// columns for patients aged 25 to 29 years, defined by gender
		ColumnParameters twty5To29M = new ColumnParameters("25-29M", "25-29 years males", "gender=M|age=25-29");// 11
		ColumnParameters twty5To29F = new ColumnParameters("25-29F", "25-29 years female", "gender=F|age=25-29");// 12
		ColumnParameters twty5To29T = new ColumnParameters("25-29T", "25-29 years patients", "age=25-29");// 13
		// columns for patients aged 30 to 34 years, defined by gender
		ColumnParameters thtyTo34M = new ColumnParameters("30-34M", "30-34 years males", "gender=M|age=30-34");// 14
		ColumnParameters thtyTo34F = new ColumnParameters("30-34F", "30-34 years female", "gender=F|age=30-44");// 15
		ColumnParameters thtyTo34T = new ColumnParameters("30-34T", "30-34 years patients", "age=30-34");// 16
		// columns for patients aged 35 to 39 years, defined by gender
		ColumnParameters thty5To39M = new ColumnParameters("35-39M", "35-39 years males", "gender=M|age=35-39");// 17
		ColumnParameters thty5To39F = new ColumnParameters("35-39F", "35-39 years female", "gender=F|age=35-39");// 18
		ColumnParameters thty5To39T = new ColumnParameters("35-39T", "35-39 years patients", "age=35-39");// 19
		// columns for patients aged 40 to 44 years, defined by age
		ColumnParameters ftyTo44M = new ColumnParameters("40-44M", "40-44 years males", "gender=M|age=40-44");// 19
		ColumnParameters ftyTo44F = new ColumnParameters("40-44F", "40-44 years female", "gender=F|age=40-44");// 20
		ColumnParameters ftyTo44T = new ColumnParameters("40-44T", "40-44 years patients", "age=40-44");// 21
		// columns for patients aged 45 to 49 years, defined by age
		ColumnParameters fty5To49M = new ColumnParameters("45-49M", "45-49 years males", "gender=M|age=45-49");// 22
		ColumnParameters fty5To49F = new ColumnParameters("45-49F", "45-49 years female", "gender=F|age=45-49");// 23
		ColumnParameters fty5To49T = new ColumnParameters("45-49T", "45-49 years patients", "age=45-49");// 24
		// coloumn parameters for patients for 50 and above years, defined by gender
		ColumnParameters fftyAndAboveM = new ColumnParameters(">=50M", ">=50 years males", "gender=M|age=>49");// 25
		ColumnParameters fftyAndAboveF = new ColumnParameters(">=50F", ">=50 years female", "gender=F|age=>49");// 26
		ColumnParameters fftyAndAboveT = new ColumnParameters(">=50T", ">=50 years patients", "age=>49");// 27
		
		return Arrays.asList(tenTo14M, tenTo14F, tenTo14T, fftnTo19M, fftnTo19F, fftnTo19T, twtyTo24M, twtyTo24F, twtyTo24T,
		    twty5To29M, twty5To29F, twty5To29T, thtyTo34M, thtyTo34F, thtyTo34T, thty5To39M, thty5To39F, thty5To39T,
		    ftyTo44M, ftyTo44F, ftyTo44T, fty5To49M, fty5To49F, fty5To49T, fftyAndAboveM, fftyAndAboveF, fftyAndAboveT);
		
	}
	
	private List<String> adultColumns() {
		return Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
		    "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27");
	}
	
	private List<ColumnParameters> children() {
		// columns parameter for children 0- 9 years
		ColumnParameters under1 = new ColumnParameters("<1", "Under 1 year", "age=<1");
		ColumnParameters oneTo4 = new ColumnParameters("1-4", "1-4 years", "age=1-4");
		ColumnParameters fiveTo9 = new ColumnParameters("5-9", "5-9 years", "age=5-9");
		
		return Arrays.asList(under1, oneTo4, fiveTo9);
	}
}
