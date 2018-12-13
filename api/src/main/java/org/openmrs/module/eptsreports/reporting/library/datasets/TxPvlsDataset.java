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

import java.util.Arrays;
import java.util.List;

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
		dsd.addDimension("q", EptsReportUtils.map(eptsCommonDimension.maternityDimension(), mappings));
		
		dsd.addColumn("0N", "Total patients with suppressed Viral load - Numerator",
		    EptsReportUtils.map(hivIndicators.patientsWithViralLoadSuppression(), mappings), "");
		
		dsd.addColumn("0D", "Total patients with Viral load - Denominator",
		    EptsReportUtils.map(hivIndicators.patientsWithViralLoadBetweenDates(), mappings), "");
		
		// constructing the first row of pregnant and breast feeding mothers
		
		addRow(dsd, "1N", "Pregnant Women - Numerator",
		    EptsReportUtils.map(pregnantIndicators.getPregnantWomenWithSuppressedViralLoadIn12Months(), mappings), pregnant());
		
		addRow(dsd,"1D", "Pregnant Women - Denominator",
		    EptsReportUtils.map(pregnantIndicators.getPregnantWomenWithViralLoadIn12Months(), mappings), pregnant());
		
		// constructing the rows for breastfeeding women
		
		addRow(dsd, "2N", "Breastfeeding - Women Numerator",
		    EptsReportUtils.map(breastfeedingIndicators.getBreastfeedingWomenWithSuppressedViralLoadIn12Months(), mappings), breastfeeding());

		addRow(dsd,"2D", "Breastfeeding - Women Denominator",
		    EptsReportUtils.map(breastfeedingIndicators.getBreastfeedingWomenWithViralLoadIn12Months(), mappings), breastfeeding());
		
		// constructing the rows for children
		// Numerator
		dsd.addColumn("3N-01M", "Children Numerator (Under 1 year - male) ",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionAgeBelow(1), mappings), "gender=M");
		dsd.addColumn("3N-01F", "Children Numerator (Under 1 year - female) ",
				EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionAgeBelow(1), mappings), "gender=F");

		dsd.addColumn("3N-02M", "Children Numerator (1-4 years - male) ",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(1, 4), mappings), "gender=M");
		dsd.addColumn("3N-02F", "Children Numerator (1-4 years - female) ",
				EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(1, 4), mappings), "gender=F");

		dsd.addColumn("3N-03M", "Children Numerator (5 - 9 years - male) ",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(5, 9), mappings), "gender=M");
		dsd.addColumn("3N-03F", "Children Numerator (5 - 9 years - female) ",
				EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(5, 9), mappings), "gender=F");
		
		// denominator
		dsd.addColumn("3D-01", "Children Denominator (Under 1 year) ",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithAgeBelow(1), mappings), "");
		dsd.addColumn("3D-02", "Children Denominator (1-4 years) ",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(1, 4), mappings), "");
		dsd.addColumn("3D-03", "Children Denominator (5 - 9 years) ",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(5, 9), mappings), "");
		
		// constructing for adults
		// Numerator
		dsd.addColumn("4N-01", "Adults with suppressed VL Numerator (10-14 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(10, 14), mappings), "gender=M");
		dsd.addColumn("4N-02", "Adults with suppressed VL Numerator (10-14 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(10, 14), mappings), "gender=F");
		dsd.addColumn("4N-04", "Adults with suppressed VL Numerator (15-19 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(15, 19), mappings), "gender=M");
		dsd.addColumn("4N-05", "Adults with suppressed VL Numerator (15-19 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(15, 19), mappings), "gender=F");
		dsd.addColumn("4N-07", "Adults with suppressed VL Numerator (20-24 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(20, 24), mappings), "gender=M");
		dsd.addColumn("4N-08", "Adults with suppressed VL Numerator (20-24 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(20, 24), mappings), "gender=F");
		dsd.addColumn("4N-10", "Adults with suppressed VL Numerator (25-29 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(25, 29), mappings), "gender=M");
		dsd.addColumn("4N-11", "Adults with suppressed VL Numerator (25-29 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(25, 29), mappings), "gender=F");
		dsd.addColumn("4N-13", "Adults with suppressed VL Numerator (30-34 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(30, 34), mappings), "gender=M");
		dsd.addColumn("4N-14", "Adults with suppressed VL Numerator (30-34 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(30, 34), mappings), "gender=F");
		dsd.addColumn("4N-16", "Adults with suppressed VL Numerator (35-39 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(35, 39), mappings), "gender=M");
		dsd.addColumn("4N-17", "Adults with suppressed VL Numerator (35-39 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(35, 39), mappings), "gender=F");
		dsd.addColumn("4N-19", "Adults with suppressed VL Numerator (40-44 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(40, 44), mappings), "gender=M");
		dsd.addColumn("4N-20", "Adults with suppressed VL Numerator (40-44 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(40, 44), mappings), "gender=F");
		dsd.addColumn("4N-22", "Adults with suppressed VL Numerator (45-49 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(45, 49), mappings), "gender=M");
		dsd.addColumn("4N-23", "Adults with suppressed VL Numerator (45-49 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(45, 49), mappings), "gender=F");
		dsd.addColumn("4N-25", "Adults with suppressed VL Numerator (50+ years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(50, 200), mappings), "gender=M");
		dsd.addColumn("4N-26", "Adults with suppressed VL Numerator (50+ years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadSuppressionWithinAgeBracket(50, 200), mappings), "gender=F");
		
		// denominator
		
		dsd.addColumn("4D-01", "Adults with VL Denominator (10-14 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(10, 14), mappings), "gender=M");
		dsd.addColumn("4D-02", "Adults with VL Denominator (10-14 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(10, 14), mappings), "gender=F");
		dsd.addColumn("4D-04", "Adults with VL Denominator (15-19 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(15, 19), mappings), "gender=M");
		dsd.addColumn("4D-05", "Adults with VL Denominator (15-19 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(15, 19), mappings), "gender=F");
		dsd.addColumn("4D-07", "Adults with VL Denominator (20-24 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(20, 24), mappings), "gender=M");
		dsd.addColumn("4D-08", "Adults with VL Denominator (20-24 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(20, 24), mappings), "gender=F");
		dsd.addColumn("4D-10", "Adults with VL Denominator (25-29 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(25, 29), mappings), "gender=M");
		dsd.addColumn("4D-11", "Adults with VL Denominator (25-29 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(25, 29), mappings), "gender=F");
		dsd.addColumn("4D-13", "Adults with VL Denominator (30-34 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(30, 34), mappings), "gender=M");
		dsd.addColumn("4D-14", "Adults with VL Denominator (30-34 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(30, 34), mappings), "gender=F");
		dsd.addColumn("4D-16", "Adults with VL Denominator (35-39 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(35, 39), mappings), "gender=M");
		dsd.addColumn("4D-17", "Adults with VL Denominator (35-39 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(35, 39), mappings), "gender=F");
		dsd.addColumn("4D-19", "Adults with VL Denominator (40-44 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(40, 44), mappings), "gender=M");
		dsd.addColumn("4D-20", "Adults with VL Denominator (40-44 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(40, 44), mappings), "gender=F");
		dsd.addColumn("4D-22", "Adults with VL Denominator (45-49 years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(45, 49), mappings), "gender=M");
		dsd.addColumn("4D-23", "Adults with VL Denominator (45-49 years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(45, 49), mappings), "gender=F");
		dsd.addColumn("4D-25", "Adults with VL Denominator (50+ years males)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(50, 200), mappings), "gender=M");
		dsd.addColumn("4D-26", "Adults with VL Denominator (50+ years females)",
		    EptsReportUtils.map(hivIndicators.getPatientsWithViralLoadResultsWithinAgeBracket(50, 200), mappings), "gender=F");
		return dsd;
		
	}
	
	public List<Parameter> getParameters() {
		return Arrays.asList(ReportingConstants.START_DATE_PARAMETER, ReportingConstants.END_DATE_PARAMETER,
		    ReportingConstants.LOCATION_PARAMETER);
	}
	
	private List<ColumnParameters> breastfeeding() {
		// columns for routine and Not documented for pregnant women
		ColumnParameters routine = new ColumnParameters("routine-Breastfeeding", "Routine Breastfeeding", "rtn=r", "01");
		ColumnParameters notDocumented = new ColumnParameters("not-documented-breastfeeding", "Not documented Breastfeeding", "rtn=n", "02");
		return Arrays.asList(routine, notDocumented);
	}
	
	private List<ColumnParameters> pregnant() {
		// columns for routine and Not documented for breastfeeding women
		ColumnParameters routinePregnant = new ColumnParameters("routine-pregnant", "Routine Pregnant", "rtn=r|q=pregnant", "01");
		ColumnParameters nDocumentedPregnant = new ColumnParameters("not-documented-pregnant", "Not documented Pregnant", "rtn=n|q=pregnant",
		        "02");
		return Arrays.asList(routinePregnant, nDocumentedPregnant);
	}
}
