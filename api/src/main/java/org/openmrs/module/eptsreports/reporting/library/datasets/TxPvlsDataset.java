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
		
		addRow(dsd, "1N", "Pregnant Women - Numerator", EptsReportUtils.map(pregnantIndicators.getPregnantWomenWithSuppressedViralLoadIn12Months(), mappings), rtndDisagregationPregnant());
		
		addRow(dsd, "1D", "Pregnant Women - Denominator", EptsReportUtils.map(pregnantIndicators.getPregnantWomenWithViralLoadIn12Months(), mappings), rtndDisagregationPregnant());
		
		// constructing the rows for breastfeeding women
		
		addRow(dsd, "2N", "Breastfeeding - Women Numerator", EptsReportUtils.map(breastfeedingIndicators.getBreastfeedingWomenWithSuppressedViralLoadIn12Months(), mappings), rtndDisagregationBreastfeeding());
		
		addRow(dsd, "2D", "Breastfeeding - Women Denominator", EptsReportUtils.map(breastfeedingIndicators.getBreastfeedingWomenWithViralLoadIn12Months(), mappings), rtndDisagregationBreastfeeding());
		
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
		////Routine
		//////Male
		ColumnParameters tenTo14RM = new ColumnParameters("10-14RM", "Routine 10-14 years males", "rtn=r|gender=M|age=10-14", "01");
		ColumnParameters fftnTo19RM = new ColumnParameters("15-19RM", "Routine 15-19 years males", "rtn=r|gender=M|age=15-19", "02");
		ColumnParameters twtyTo24RM = new ColumnParameters("20-24RM", "Routine 20-24 years males", "rtn=r|gender=M|age=20-24", "03");
		ColumnParameters twty5To29RM = new ColumnParameters("25-29RM", "Routine 25-29 years males", "rtn=r|gender=M|age=25-29", "04");
		ColumnParameters thtyTo34RM = new ColumnParameters("30-34RM", "Routine 30-34 years males", "rtn=r|gender=M|age=30-34", "05");
		ColumnParameters thty5To39RM = new ColumnParameters("35-39RM", "Routine 35-39 years males", "rtn=r|gender=M|age=35-39", "06");
		ColumnParameters ftyTo44RM = new ColumnParameters("40-44RM", "Routine 40-44 years males", "rtn=r|gender=M|age=40-44", "07");
		ColumnParameters fty5To49RM = new ColumnParameters("45-49RM", "Routine 45-49 years males", "rtn=r|gender=M|age=45-49", "08");
		ColumnParameters fftyAndAboveRM = new ColumnParameters(">=50RM", "Routine 50+ years males", "rtn=r|gender=M|age=>49", "09");
		ColumnParameters unknownAgeRM = new ColumnParameters("unknownAgeRM", "Routine unknown age males", "rtn=r|gender=M|age=UK", "10");
		//////Female
		ColumnParameters tenTo14RF = new ColumnParameters("10-14RF", "Routine 10-14 years females", "rtn=r|gender=F|age=10-14", "11");
		ColumnParameters fftnTo19RF = new ColumnParameters("15-19RF", "Routine 15-19 years females", "rtn=r|gender=F|age=15-19", "12");
		ColumnParameters twtyTo24RF = new ColumnParameters("20-24RF", "Routine 20-24 years females", "rtn=r|gender=F|age=20-24", "13");
		ColumnParameters twty5To29RF = new ColumnParameters("25-29RF", "Routine 25-29 years females", "rtn=r|gender=F|age=25-29", "14");
		ColumnParameters thtyTo34RF = new ColumnParameters("30-34RF", "Routine 30-34 years females", "rtn=r|gender=F|age=30-34", "15");
		ColumnParameters thty5To39RF = new ColumnParameters("35-39RF", "Routine 35-39 years females", "rtn=r|gender=F|age=35-39", "16");
		ColumnParameters ftyTo44RF = new ColumnParameters("40-44RF", "Routine 40-44 years females", "rtn=r|gender=F|age=40-44", "17");
		ColumnParameters fty5To49RF = new ColumnParameters("45-49RF", "Routine 45-49 years females", "rtn=r|gender=F|age=45-49", "18");
		ColumnParameters fftyAndAboveRF = new ColumnParameters(">=50RF", "Routine 50+ years females", "rtn=r|gender=F|age=>49", "19");
		ColumnParameters unknownAgeRF = new ColumnParameters("unknownAgeRF", "Routine unknown age females", "rtn=r|gender=F|age=UK", "20");

		////Not documented
		//////Male
		ColumnParameters tenTo14NM = new ColumnParameters("10-14NM", "Not documented 10-14 years males", "rtn=n|gender=M|age=10-14", "21");
		ColumnParameters fftnTo19NM = new ColumnParameters("15-19NM", "Not documented 15-19 years males", "rtn=n|gender=M|age=15-19", "22");
		ColumnParameters twtyTo24NM = new ColumnParameters("20-24NM", "Not documented 20-24 years males", "rtn=n|gender=M|age=20-24", "23");
		ColumnParameters twty5To29NM = new ColumnParameters("25-29NM", "Not documented 25-29 years males", "rtn=n|gender=M|age=25-29", "24");
		ColumnParameters thtyTo34NM = new ColumnParameters("30-34NM", "Not documented 30-34 years males", "rtn=n|gender=M|age=30-34", "25");
		ColumnParameters thty5To39NM = new ColumnParameters("35-39NM", "Not documented 35-39 years males", "rtn=n|gender=M|age=35-39", "26");
		ColumnParameters ftyTo44NM = new ColumnParameters("40-44NM", "Not documented 40-44 years males", "rtn=n|gender=M|age=40-44", "27");
		ColumnParameters fty5To49NM = new ColumnParameters("45-49NM", "Not documented 45-49 years males", "rtn=n|gender=M|age=45-49", "28");
		ColumnParameters fftyAndAboveNM = new ColumnParameters(">=50NM", "Not documented 50+ years males", "rtn=n|gender=M|age=>49", "29");
		ColumnParameters unknownAgeNM = new ColumnParameters("unknownAgeNM", "Not documented unknown age males", "rtn=n|gender=M|age=UK", "30");
		//////Female
		ColumnParameters tenTo14NF = new ColumnParameters("10-14NF", "Not documented 10-14 years females", "rtn=n|gender=F|age=10-14", "31");
		ColumnParameters fftnTo19NF = new ColumnParameters("15-19NF", "Not documented 15-19 years females", "rtn=n|gender=F|age=15-19", "32");
		ColumnParameters twtyTo24NF = new ColumnParameters("20-24NF", "Not documented 20-24 years females", "rtn=n|gender=F|age=20-24", "33");
		ColumnParameters twty5To29NF = new ColumnParameters("25-29NF", "Not documented 25-29 years females", "rtn=n|gender=F|age=25-29", "34");
		ColumnParameters thtyTo34NF = new ColumnParameters("30-34NF", "Not documented 30-34 years females", "rtn=n|gender=F|age=30-34", "35");
		ColumnParameters thty5To39NF = new ColumnParameters("35-39NF", "Not documented 35-39 years females", "rtn=n|gender=F|age=35-39", "36");
		ColumnParameters ftyTo44NF = new ColumnParameters("40-44NF", "Not documented 40-44 years females", "rtn=n|gender=F|age=40-44", "37");
		ColumnParameters fty5To49NF = new ColumnParameters("45-49NF", "Not documented 45-49 years females", "rtn=n|gender=F|age=45-49", "38");
		ColumnParameters fftyAndAboveNF = new ColumnParameters(">=50NF", "Not documented 50+ years females", "rtn=n|gender=F|age=>49", "39");
		ColumnParameters unknownAgeNF = new ColumnParameters("unknownAgeNF", "Not documented unknown age females", "rtn=n|gender=F|age=UK", "40");
		
		return Arrays.asList(tenTo14RM, fftnTo19RM, twtyTo24RM, twty5To29RM, thtyTo34RM, thty5To39RM, ftyTo44RM, fty5To49RM, fftyAndAboveRM, unknownAgeRM, tenTo14RF,
				fftnTo19RF, twtyTo24RF, twty5To29RF, thtyTo34RF, thty5To39RF, ftyTo44RF, fty5To49RF, fftyAndAboveRF, unknownAgeRF,  tenTo14NM, fftnTo19NM, twtyTo24NM,
				twty5To29NM, thtyTo34NM, thty5To39NM, ftyTo44NM,fty5To49NM,fftyAndAboveNM,unknownAgeNM,tenTo14NF, fftnTo19NF, twtyTo24NF,twty5To29NF,thtyTo34NF,
				thty5To39NF,ftyTo44NF,fty5To49NF,fftyAndAboveNF,unknownAgeNF);
		
	}
	
	private List<ColumnParameters> childrenDisagregation() {
		// columns parameter for children 0- 9 years
		// Routine
		//////// male
		ColumnParameters under1MR = new ColumnParameters("<1MR", "Routine Under 1 year male", "rtn=r|gender=M|age=<1", "01");
		ColumnParameters oneTo4MR = new ColumnParameters("1-4MR", "Routine 1-4 years male", "rtn=r|gender=M|age=1-4", "02");
		ColumnParameters fiveTo9MR = new ColumnParameters("5-9MR", "Routine 5-9 years male", "rtn=r|gender=M|age=5-9", "03");
		
		///// female
		ColumnParameters under1FR = new ColumnParameters("<1FR", "Routine Under 1 year female", "rtn=r|gender=F|age=<1", "04");
		ColumnParameters oneTo4FR = new ColumnParameters("1-4FR", "Routine 1-4 years female", "rtn=r|gender=F|age=1-4", "05");
		ColumnParameters fiveTo9FR = new ColumnParameters("5-9FR", "Routine 5-9 years female", "rtn=r|gender=F|age=5-9", "06");
		
		// Not documented
		///// male
		ColumnParameters under1MN = new ColumnParameters("<1MTN", "Not documented Under 1 year male", "rtn=n|gender=M|age=<1", "07");
		ColumnParameters oneTo4MN = new ColumnParameters("1-4MTN", "Not documented 1-4 years male", "rtn=n|gender=M|age=1-4", "08");
		ColumnParameters fiveTo9MN = new ColumnParameters("5-9MTN", "Not documented 5-9 years male", "rtn=n|gender=M|age=5-9", "09");
		
		////// female
		ColumnParameters under1FN = new ColumnParameters("<1FTN", "Not documented Under 1 year female", "rtn=n|gender=F|age=<1", "10");
		ColumnParameters oneTo4FN = new ColumnParameters("1-4FTN", "Not documented 1-4 years female", "rtn=n|gender=F|age=1-4", "11");
		ColumnParameters fiveTo9FN = new ColumnParameters("5-9FTN", "Not documented 5-9 years female", "rtn=n|gender=F|age=5-9", "12");
		
		return Arrays.asList(under1MR, oneTo4MR, fiveTo9MR, under1FR, oneTo4FR, fiveTo9FR, under1MN, oneTo4MN, fiveTo9MN, under1FN, oneTo4FN, fiveTo9FN);
	}
	
	private List<ColumnParameters> rtndDisagregationPregnant() {
		// columns for routine and Not documented for pregnant women
		ColumnParameters routineP = new ColumnParameters("routine-pregnant", "Routine Pregnant", "rtn=r", "01");
		ColumnParameters nDocumentedP = new ColumnParameters("not-documented-pregnant", "Not documented Pregnant", "rtn=n", "02");
		return Arrays.asList(routineP, nDocumentedP);
	}
	
	private List<ColumnParameters> rtndDisagregationBreastfeeding() {
		// columns for routine and Not documented for breastfeeding women
		ColumnParameters routineB = new ColumnParameters("routine-breastfeeding", "Routine Breastfeeding", "rtn=r", "01");
		ColumnParameters nDocumentedB = new ColumnParameters("not-documented-breastfeeding", "Not documented Breastfeeding", "rtn=n", "02");
		return Arrays.asList(routineB, nDocumentedB);
	}
}
