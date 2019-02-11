/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;

import org.openmrs.module.eptsreports.reporting.library.cohorts.TxPvlsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
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
	private EptsCommonDimension eptsCommonDimension;
	
	@Autowired
	private EptsGeneralIndicator eptsGeneralIndicator;
	
	@Autowired
	private TxPvlsCohortQueries txPvls;
	
	public DataSetDefinition constructTxPvlsDatset() {
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		dsd.setName("Tx_Pvls Data Set");
		dsd.addParameters(getParameters());
		// tie dimensions to this data definition
		
		dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
		dsd.addDimension("query", EptsReportUtils.map(eptsCommonDimension.maternityDimension(), mappings));
		dsd.addDimension("age",
		    EptsReportUtils.map(eptsCommonDimension.pvlsAges(), "endDate=${endDate},location=${location}"));
		
		// Totals for both numerator and denominator
		
		dsd.addColumn(
		    "0N",
		    "Total patients with suppressed Viral load - Numerator",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("suppressed viral load",
		            EptsReportUtils.map(txPvls.getPatientsWithViralLoadSuppression(), mappings)), mappings), "");
		
		dsd.addColumn("P", "Pregnant", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("Pregnant",
		        EptsReportUtils.map(txPvls.getPatientsWhoArePregnant(), "endDate=${endDate},location=${location}")),
		    "endDate=${endDate},location=${location}"), "");
		
		dsd.addColumn("B", "Breastfeeding", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("Breastfeeding",
		        EptsReportUtils.map(txPvls.getPatientsWhoAreBreastfeeding(), "onDate=${endDate},location=${location}")),
		    "endDate=${endDate},location=${location}"), "");
		
		dsd.addColumn(
		    "PVLS",
		    "Pregnant and has VL suppression",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("Pregnant and VLS",
		            EptsReportUtils.map(txPvls.getPregnantWomenWithViralLoadSuppressionNumerator(), mappings)), mappings),
		    "");
		dsd.addColumn(
		    "PVLR",
		    "Pregnant and has VL results",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("Pregnant and VLR",
		            EptsReportUtils.map(txPvls.getPregnantWomenWithViralLoadResultsDenominator(), mappings)), mappings), "");
		dsd.addColumn(
		    "BVLS",
		    "Breastfeeding and has VL suppression",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("Breastfeeding and VLS",
		            EptsReportUtils.map(txPvls.getBreastfeedingWomenWhoHaveViralSuppression(), mappings)), mappings), "");
		dsd.addColumn(
		    "BVLR",
		    "Breastfeeding and has VL results",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("Breastfeeding and VLR",
		            EptsReportUtils.map(txPvls.getBreastfeedingWomenWhoHaveViralLoadResults(), mappings)), mappings), "");
		dsd.addColumn(
		    "0D",
		    "Total patients with Viral load - Denominator",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("patients with viral load",
		            EptsReportUtils.map(txPvls.getPatientsWithViralLoadResults(), mappings)), mappings), "");
		
		// add patients on routine for adults and children
		dsd.addColumn("B01", "Breast feeding and on routine Numerator", EptsReportUtils.map(eptsGeneralIndicator
		        .getIndicator("breastfeedingWomenWithViralLoadSuppression routine numerator",
		            EptsReportUtils.map(txPvls.getBreastFeedingWomenOnRoutineNumerator(), mappings)), mappings), "");
		dsd.addColumn("B02", "Breast feeding and NOT documented Numerator", EptsReportUtils.map(eptsGeneralIndicator
		        .getIndicator("breastfeedingWomenWithViralLoadSuppression not documented numerator",
		            EptsReportUtils.map(txPvls.getBreastFeedingWomenNotDocumentedNumerator(), mappings)), mappings), "");
		dsd.addColumn(
		    "B03",
		    "Pregnant and on routine Numerator",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("pregnantWomenWithViralLoadSuppression and on routine",
		            EptsReportUtils.map(txPvls.getPregnantAndOnRoutineNumerator(), mappings)), mappings), "");
		dsd.addColumn("B04", "Pregnant and NOT documented Numerator", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("pregnantWomenWithViralLoadSuppression and NOT documented",
		        EptsReportUtils.map(txPvls.getPregnantAndNotDocumentedNumerator(), mappings)), mappings), "");
		
		// add breastfeeding and pregnant Denominator
		dsd.addColumn("B05", "Breast feeding and on routine Denominator", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("breastfeedingWomenWithViralLoad-routine denominator",
		        EptsReportUtils.map(txPvls.getBreastfeedingWomenOnRoutineWithViralLoadResultsDenominator(), mappings)),
		    mappings), "");
		dsd.addColumn("B06", "Breast feeding and NOT documented Denominator", EptsReportUtils.map(eptsGeneralIndicator
		        .getIndicator("breastfeedingWomenWithViralLoad-not documented denominator", EptsReportUtils.map(
		            txPvls.getBreastfeedingWomenAndNotDocumentedWithViralLoadResultsDenominator(), mappings)), mappings), "");
		dsd.addColumn("B07", "Pregnant and on routine Denominator", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("pregnantWomenWithViralLoad and not documented denominator",
		        EptsReportUtils.map(txPvls.getPregnantWomenAndOnRoutineWithViralLoadResultsDenominator(), mappings)),
		    mappings), "");
		dsd.addColumn("B08", "Pregnant and NOT documented Denominator", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("pregnantWomenWithViralLoad results not documented denominator",
		        EptsReportUtils.map(txPvls.getPregnantWomenAndNotDocumentedWithViralLoadResultsDenominator(), mappings)),
		    mappings), "");
		
		// constructing the rows for children // /// Numerator routine
		addRow(dsd, "3NR", "Children numerator routine", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("viral load suppression on routine adults and children",
		        EptsReportUtils.map(txPvls.getPatientWithViralSuppressionAndOnRoutineAdultsAndChildren(), mappings)),
		    mappings), childrenColumns()); ////// Numerator NOT documented
		addRow(dsd, "3NND", "Children numerator NOT documented", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("viral load suppression on routine adults and children",
		        EptsReportUtils.map(txPvls.getPatientWithViralSuppressionAndNotDocumentedForAdultsAndChildren(), mappings)),
		    mappings), childrenColumns());
		
		// constructing the rows for adults ///// Numerator routine
		addRow(dsd, "4NR", "Adults numerator routine", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("viral load suppression on routine adults",
		        EptsReportUtils.map(txPvls.getPatientWithViralSuppressionAndOnRoutineAdultsAndChildren(), mappings)),
		    mappings), getColumnsForAdults()); //// Numerator NOT documented
		addRow(
		    dsd,
		    "4NND",
		    "Adults numerator NOT documented",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("viral load suppression on routine adults", EptsReportUtils.map(
		            txPvls.getPatientWithViralSuppressionAndNotDocumentedForAdultsAndChildren(), mappings)), mappings),
		    getColumnsForAdults());
		
		// denominators follow here for routine and NOT documented // ///
		// Denominator
		// routine for children
		addRow(dsd, "3DR", "Children denominator routine", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("viral load results on routine adults and children",
		        EptsReportUtils.map(txPvls.getPatientsWithViralLoadREsultsAndOnRoutineForChildrenAndAdults(), mappings)),
		    mappings), childrenColumns()); ///// Denominator NOT
		                                   ///// documented
		addRow(
		    dsd,
		    "3DND",
		    "Children denominator NOT documented",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("viral load suppression on routine adults", EptsReportUtils.map(
		            txPvls.getPatientsWithViralLoadREsultsAndNotDocumenetdForChildrenAndAdults(), mappings)), mappings),
		    childrenColumns()); ///// Denominator routine for adults
		addRow(dsd, "4DR", "Adults denominator routine", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("Adults denominator routine indicator",
		        EptsReportUtils.map(txPvls.getPatientsWithViralLoadREsultsAndOnRoutineForChildrenAndAdults(), mappings)),
		    mappings), getColumnsForAdults()); //// denominator NOT documented
		addRow(
		    dsd,
		    "4DND",
		    "Adults denominator NOT documented",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("Adults denominator NOT documented indicator", EptsReportUtils.map(
		            txPvls.getPatientsWithViralLoadREsultsAndNotDocumenetdForChildrenAndAdults(), mappings)), mappings),
		    getColumnsForAdults());
		
		return dsd;
		
	}
	
	public List<Parameter> getParameters() {
		return Arrays.asList(ReportingConstants.START_DATE_PARAMETER, ReportingConstants.END_DATE_PARAMETER,
		    ReportingConstants.LOCATION_PARAMETER);
	}
	
	private List<ColumnParameters> childrenColumns() {
		ColumnParameters under1M = new ColumnParameters("under1M", "under 1 year male", "gender=M|age=<1", "01");
		ColumnParameters oneTo4M = new ColumnParameters("oneTo4M", "1 - 4 years male", "gender=M|age=1-4", "02");
		ColumnParameters fiveTo9M = new ColumnParameters("fiveTo9M", "5 - 9 years male", "gender=M|age=5-9", "03");
		ColumnParameters under1F = new ColumnParameters("under1F", "under 1 year female", "gender=F|age=<1", "04");
		ColumnParameters oneTo4F = new ColumnParameters("oneTo4F", "1 - 4 years female", "gender=F|age=1-4", "05");
		ColumnParameters fiveTo9F = new ColumnParameters("fiveTo9F", "5 - 9 years female", "gender=F|age=5-9", "06");
		return Arrays.asList(under1M, oneTo4M, fiveTo9M, under1F, oneTo4F, fiveTo9F);
	}
	
	private List<ColumnParameters> getColumnsForAdults() {
		ColumnParameters unknownM = new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "01");
		ColumnParameters tenTo14M = new ColumnParameters("tenTo14M", "10 - 14 male", "gender=M|age=10-14", "02");
		ColumnParameters fifteenTo19M = new ColumnParameters("fifteenTo19M", "15 - 19 male", "gender=M|age=15-19", "03");
		ColumnParameters twentyTo24M = new ColumnParameters("twentyTo24M", "20 - 24 male", "gender=M|age=20-24", "04");
		ColumnParameters twenty5To29M = new ColumnParameters("twenty4To29M", "25 - 29 male", "gender=M|age=25-29", "05");
		ColumnParameters thirtyTo34M = new ColumnParameters("thirtyTo34M", "30 - 34 male", "gender=M|age=30-34", "06");
		ColumnParameters thirty5To39M = new ColumnParameters("thirty5To39M", "35 - 39 male", "gender=M|age=35-39", "07");
		ColumnParameters foutyTo44M = new ColumnParameters("foutyTo44M", "40 - 44 male", "gender=M|age=40-44", "08");
		ColumnParameters fouty5To49M = new ColumnParameters("fouty5To49M", "45 - 49 male", "gender=M|age=45-49", "09");
		ColumnParameters above50M = new ColumnParameters("above50M", "50+ male", "gender=M|age=50+", "10");
		
		ColumnParameters unknownF = new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "11");
		ColumnParameters tenTo14F = new ColumnParameters("tenTo14F", "10 - 14 female", "gender=F|age=10-14", "12");
		ColumnParameters fifteenTo19F = new ColumnParameters("fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "13");
		ColumnParameters twentyTo24F = new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "14");
		ColumnParameters twenty5To29F = new ColumnParameters("twenty4To29F", "25 - 29 female", "gender=F|age=25-29", "15");
		ColumnParameters thirtyTo34F = new ColumnParameters("thirtyTo34F", "30 - 34 female", "gender=F|age=30-34", "16");
		ColumnParameters thirty5To39F = new ColumnParameters("thirty5To39F", "35 - 39 female", "gender=F|age=35-39", "17");
		ColumnParameters foutyTo44F = new ColumnParameters("foutyTo44F", "40 - 44 female", "gender=F|age=40-44", "18");
		ColumnParameters fouty5To49F = new ColumnParameters("fouty5To49F", "45 - 49 female", "gender=F|age=45-49", "19");
		ColumnParameters above50F = new ColumnParameters("above50F", "50+ female", "gender=F|age=50+", "20");
		
		return Arrays.asList(unknownM, tenTo14M, fifteenTo19M, twentyTo24M, twenty5To29M, thirtyTo34M, thirty5To39M,
		    foutyTo44M, fouty5To49M, above50M, unknownF, tenTo14F, fifteenTo19F, twentyTo24F, twenty5To29F, thirtyTo34F,
		    thirty5To39F, foutyTo44F, fouty5To49F, above50F);
		
	}
}
