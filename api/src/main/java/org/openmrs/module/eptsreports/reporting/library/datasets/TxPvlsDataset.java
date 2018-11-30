/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;

import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.BreastfeedingIndicators;
import org.openmrs.module.eptsreports.reporting.library.indicators.HivIndicators;
import org.openmrs.module.eptsreports.reporting.library.indicators.PregnantIndicators;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxPvlsDataset extends BaseDataSet {
	
	@Autowired
	private HivIndicators hivIndicators;
	
	@Autowired
	private EptsCommonDimension eptsCommonDimension;
	
	@Autowired
	private PregnantIndicators pregnantIndicators;
	
	@Autowired
	private BreastfeedingIndicators breastfeedingIndicators;
	
	public DataSetDefinition constructTxPvlsDatset() {
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		dsd.setName("Tx_Pvls Data Set");
		dsd.addParameters(getParameters());
		// tie dimensions to this data definition
		dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
		dsd.addDimension("age", EptsReportUtils.map(eptsCommonDimension.age(), "effectiveDate=${endDate}"));
		dsd.addDimension("rtn", EptsReportUtils.map(eptsCommonDimension.routineTargetNotDocumented(), mappings));
		
		dsd.addColumn("0N", "Total patients with suppressed Viral load - Numerator", EptsReportUtils.map(hivIndicators.patientsWithViralLoadSuppression(), mappings), "");
		
		dsd.addColumn("0D", "Total patients with Viral load - Denominator", EptsReportUtils.map(hivIndicators.patientsWithViralLoadBetweenDates(), mappings), "");
		
		// constructing the first row of pregnant and breast feeding mothers

		addRow(dsd,"1N", "Pregnant Women - Numerator", EptsReportUtils.map(pregnantIndicators.getPregnantWomenWithSuppressedViralLoadIn12Months(), mappings), rtndDisagregationPregnant());

		addRow(dsd, "1D", "Pregnant Women - Denominator", EptsReportUtils.map(pregnantIndicators.getPregnantWomenWithViralLoadIn12Months(), mappings), rtndDisagregationPregnant());
		
		// constructing the rows for breastfeeding women

		addRow(dsd, "2N", "Breastfeeding - Women Numerator", EptsReportUtils.map(breastfeedingIndicators.getBreastfeedingWomenWithSuppressedViralLoadIn12Months(), mappings), rtndDisagregationBreastfeeding());

		addRow(dsd,"2D", "Breastfeeding - Women Denominator", EptsReportUtils.map(breastfeedingIndicators.getBreastfeedingWomenWithViralLoadIn12Months(), mappings), rtndDisagregationBreastfeeding());
		
		// constructing the rows for children
		addRow(dsd, "3N", "Children Numerator", EptsReportUtils.map(hivIndicators.patientsWithViralLoadSuppression(), mappings), childrenDisagregation());
		
		addRow(dsd, "3D", "Children Denominator", EptsReportUtils.map(hivIndicators.patientsWithViralLoadBetweenDates(), mappings), childrenDisagregation());
		
		// Numerator
		addRow(dsd, "4N", "Adults with suppressed VL Numerator", EptsReportUtils.map(hivIndicators.patientsWithViralLoadSuppression(), mappings), adultsDisagregation());
		
		// Denominator
		addRow(dsd, "4D", "Adults with VL Denominator", EptsReportUtils.map(hivIndicators.patientsWithViralLoadBetweenDates(), mappings), adultsDisagregation());
		return dsd;
		
	}
	
	public List<Parameter> getParameters() {
		return Arrays.asList(ReportingConstants.START_DATE_PARAMETER, ReportingConstants.END_DATE_PARAMETER, ReportingConstants.LOCATION_PARAMETER);
	}
	
	// build dimensions specific for this data set
	/**
	 * List of {@link ColumnParameters} for disaggregation
	 * 
	 * @return List of {@link ColumnParameters}
	 */
	private List<ColumnParameters> adultsDisagregation() {
		// columns for patients aged 10 to 14 years, defined by gender
		ColumnParameters tenTo14M = new ColumnParameters("10-14M", "10-14 years males", "gender=M|age=10-14", "01");
		ColumnParameters tenTo14F = new ColumnParameters("10-14F", "10-14 years female", "gender=F|age=10-14", "02");
		ColumnParameters tenTo14T = new ColumnParameters("10-14 Totals", "10-14 years patients", "age=10-14", "03");
		// columns for patients aged 15 to 19 years, defined by gender
		ColumnParameters fftnTo19M = new ColumnParameters("15-19M", "15-19 years males", "gender=M|age=15-19", "04");
		ColumnParameters fftnTo19F = new ColumnParameters("15-19F", "15-19 years female", "gender=F|age=15-19", "05");
		ColumnParameters fftnTo19T = new ColumnParameters("15-19T", "15-19 years patients", "age=15-19", "06");
		// columns for patients aged 20 to 24 years, defined by gender
		ColumnParameters twtyTo24M = new ColumnParameters("20-24M", "20-24 years males", "gender=M|age=20-24", "07");
		ColumnParameters twtyTo24F = new ColumnParameters("20-24F", "20-24 years female", "gender=F|age=20-24", "08");
		ColumnParameters twtyTo24T = new ColumnParameters("20-24T", "20-24 years patients", "age=20-24", "09");
		// columns for patients aged 25 to 29 years, defined by gender
		ColumnParameters twty5To29M = new ColumnParameters("25-29M", "25-29 years males", "gender=M|age=25-29", "10");
		ColumnParameters twty5To29F = new ColumnParameters("25-29F", "25-29 years female", "gender=F|age=25-29", "11");
		ColumnParameters twty5To29T = new ColumnParameters("25-29T", "25-29 years patients", "age=25-29", "12");
		// columns for patients aged 30 to 34 years, defined by gender
		ColumnParameters thtyTo34M = new ColumnParameters("30-34M", "30-34 years males", "gender=M|age=30-34", "13");
		ColumnParameters thtyTo34F = new ColumnParameters("30-34F", "30-34 years female", "gender=F|age=30-34", "14");
		ColumnParameters thtyTo34T = new ColumnParameters("30-34T", "30-34 years patients", "age=30-34", "15");
		// columns for patients aged 35 to 39 years, defined by gender
		ColumnParameters thty5To39M = new ColumnParameters("35-39M", "35-39 years males", "gender=M|age=35-39", "16");
		ColumnParameters thty5To39F = new ColumnParameters("35-39F", "35-39 years female", "gender=F|age=35-39", "17");
		ColumnParameters thty5To39T = new ColumnParameters("35-39T", "35-39 years patients", "age=35-39", "18");
		// columns for patients aged 40 to 44 years, defined by age
		ColumnParameters ftyTo44M = new ColumnParameters("40-44M", "40-44 years males", "gender=M|age=40-44", "19");
		ColumnParameters ftyTo44F = new ColumnParameters("40-44F", "40-44 years female", "gender=F|age=40-44", "20");
		ColumnParameters ftyTo44T = new ColumnParameters("40-44T", "40-44 years patients", "age=40-44", "21");
		// columns for patients aged 45 to 49 years, defined by age
		ColumnParameters fty5To49M = new ColumnParameters("45-49M", "45-49 years males", "gender=M|age=45-49", "22");
		ColumnParameters fty5To49F = new ColumnParameters("45-49F", "45-49 years female", "gender=F|age=45-49", "23");
		ColumnParameters fty5To49T = new ColumnParameters("45-49T", "45-49 years patients", "age=45-49", "24");
		// coloumn parameters for patients for 50 and above years, defined by gender
		ColumnParameters fftyAndAboveM = new ColumnParameters(">=50M", ">=50 years males", "gender=M|age=>49", "25");
		ColumnParameters fftyAndAboveF = new ColumnParameters(">=50F", ">=50 years female", "gender=F|age=>49", "26");
		ColumnParameters fftyAndAboveT = new ColumnParameters(">=50T", ">=50 years patients", "age=>49", "27");
		
		return Arrays.asList(tenTo14M, tenTo14F, tenTo14T, fftnTo19M, fftnTo19F, fftnTo19T, twtyTo24M, twtyTo24F, twtyTo24T, twty5To29M, twty5To29F, twty5To29T, thtyTo34M, thtyTo34F, thtyTo34T, thty5To39M, thty5To39F, thty5To39T, ftyTo44M, ftyTo44F, ftyTo44T, fty5To49M, fty5To49F, fty5To49T, fftyAndAboveM, fftyAndAboveF, fftyAndAboveT);
		
	}
	
	private List<ColumnParameters> childrenDisagregation() {
		// columns parameter for children 0- 9 years
		ColumnParameters under1M = new ColumnParameters("<1M", "Under 1 year male", "gender=M|age=<1", "01");
		ColumnParameters under1F = new ColumnParameters("<1F", "Under 1 year female", "gender=F|age=<1", "02");
		ColumnParameters oneTo4M = new ColumnParameters("1-4M", "1-4 years male", "gender=M|age=1-4", "03");
		ColumnParameters oneTo4F = new ColumnParameters("1-4F", "1-4 years female", "gender=F|age=1-4", "04");
		ColumnParameters fiveTo9M = new ColumnParameters("5-9M", "5-9 years male", "gender=M|age=5-9", "05");
		ColumnParameters fiveTo9F = new ColumnParameters("5-9F", "5-9 years female", "gender=F|age=5-9", "06");
		return Arrays.asList(under1M, under1F, oneTo4M, oneTo4F, fiveTo9M, fiveTo9F);
	}

	private List<ColumnParameters> rtndDisagregationPregnant() {
		//columns for routine, target and Not documented for pregnant women
		ColumnParameters routineP = new ColumnParameters("routine-pregnant", "Routine Pregnant", "rtn=r", "01");
		ColumnParameters targetP = new ColumnParameters("target-pregnant", "Target Pregnant", "rtn=t", "02");
		ColumnParameters nDocumentedP = new ColumnParameters("not-documented-pregnant", "Not documented Pregnant", "rtn=n", "03");
		return Arrays.asList(routineP, targetP, nDocumentedP);
	}

	private List<ColumnParameters> rtndDisagregationBreastfeeding() {
		//columns for routine, target and Not documented for breastfeeding women
		ColumnParameters routineB = new ColumnParameters("routine-breastfeeding", "Routine Breastfeeding", "rtn=r", "01");
		ColumnParameters targetB = new ColumnParameters("target-breastfeeding", "Target Breastfeeding", "rtn=t", "02");
		ColumnParameters nDocumentedB = new ColumnParameters("not-documented-breastfeeding", "Not documented Breastfeeding", "rtn=n", "03");
		return Arrays.asList(routineB, targetB, nDocumentedB);
	}
}
