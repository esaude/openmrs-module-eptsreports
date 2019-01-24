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

import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxCurrDataset extends BaseDataSet {
	
	@Autowired
	private TxCurrCohortQueries txCurrCohortQueries;
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	@Autowired
	private EptsGeneralIndicator eptsGeneralIndicator;
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private EptsCommonDimension eptsCommonDimension;
	
	public CohortIndicatorDataSetDefinition constructTxCurrDataset(boolean currentSpec) {
		
		CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
		dataSetDefinition.setName("TX_CURR Data Set");
		dataSetDefinition.addParameters(getParameters());
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		
		dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
		dataSetDefinition.addDimension("age",
		    EptsReportUtils.map(eptsCommonDimension.pvlsAges(), "endDate=${endDate},location=${location}"));
		
		CohortDefinition enrolledBeforeEndDate = genericCohortQueries.createInProgram("InARTProgram",
		    hivMetadata.getARTProgram());
		CohortDefinition patientWithSTARTDRUGSObs = txCurrCohortQueries.getPatientWithSTARTDRUGSObsBeforeOrOnEndDate();
		CohortDefinition patientWithHistoricalDrugStartDateObs = txCurrCohortQueries
		        .getPatientWithHistoricalDrugStartDateObsBeforeOrOnEndDate();
		CohortDefinition patientsWithDrugPickUpEncounters = txCurrCohortQueries
		        .getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate();
		CohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate = txCurrCohortQueries
		        .getPatientsWhoLeftARTProgramBeforeOrOnEndDate();
		CohortDefinition patientsThatMissedNexPickup = txCurrCohortQueries.getPatientsThatMissedNexPickup();
		CohortDefinition patientsReportedAsAbandonmentButStillInPeriod = txCurrCohortQueries
		        .getPatientsReportedAsAbandonmentButStillInPeriod();
		CohortDefinition patientsThatMissNextConsultation = txCurrCohortQueries.getPatientsThatMissNextConsultation();
		CohortDefinition patientsWithNextPickupDate = txCurrCohortQueries.getPatientsWithNextPickupDate();
		CohortDefinition patientsWithNextConsultationDate = txCurrCohortQueries.getPatientsWithNextConsultationDate();
		
		CohortDefinition txCurrCompositionCohort = txCurrCohortQueries.getTxCurrCompositionCohort("compositionCohort",
		    enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs,
		    patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsThatMissedNexPickup,
		    patientsThatMissNextConsultation, patientsReportedAsAbandonmentButStillInPeriod, null, null,
		    patientsWithNextPickupDate, patientsWithNextConsultationDate, currentSpec);
		CohortIndicator txCurrIndicator = eptsGeneralIndicator.getIndicator(
		    "patientInYearRangeEnrolledInHIVStartedARTIndicatorUnknownFemales",
		    EptsReportUtils.map(txCurrCompositionCohort, "onOrBefore=${endDate},location=${location}"));
		
		addRow(dataSetDefinition, "C1", "Children", EptsReportUtils.map(txCurrIndicator, mappings), getColumnsForChildren());
		addRow(dataSetDefinition, "C2", "Adults", EptsReportUtils.map(txCurrIndicator, mappings), getColumnsForAdults());
		dataSetDefinition
		        .addColumn("C1All", "TX_CURR: Currently on ART", EptsReportUtils.map(txCurrIndicator, mappings), "");
		
		return dataSetDefinition;
	}
	
	private List<ColumnParameters> getColumnsForChildren() {
		ColumnParameters under1M = new ColumnParameters("under1M", "under 1 year male", "gender=M|age=<1", "M1");
		ColumnParameters oneTo4M = new ColumnParameters("oneTo4M", "1 - 4 years male", "gender=M|age=1-4", "M2");
		ColumnParameters fiveTo9M = new ColumnParameters("fiveTo9M", "5 - 9 years male", "gender=M|age=5-9", "M3");
		ColumnParameters under1F = new ColumnParameters("under1F", "under 1 year female", "gender=F|age=<1", "F1");
		ColumnParameters oneTo4F = new ColumnParameters("oneTo4F", "1 - 4 years female", "gender=F|age=1-4", "F2");
		ColumnParameters fiveTo9F = new ColumnParameters("fiveTo9F", "5 - 9 years female", "gender=F|age=5-9", "F3");
		
		return Arrays.asList(under1M, oneTo4M, fiveTo9M, under1F, oneTo4F, fiveTo9F);
	}
	
	private List<ColumnParameters> getColumnsForAdults() {
		ColumnParameters unknownM = new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "UNKM");
		ColumnParameters tenTo14M = new ColumnParameters("tenTo14M", "10 - 14 male", "gender=M|age=10-14", "M4");
		ColumnParameters fifteenTo19M = new ColumnParameters("fifteenTo19M", "15 - 19 male", "gender=M|age=15-19", "M5");
		ColumnParameters twentyTo24M = new ColumnParameters("twentyTo24M", "20 - 24 male", "gender=M|age=20-24", "M6");
		ColumnParameters twenty5To29M = new ColumnParameters("twenty4To29M", "25 - 29 male", "gender=M|age=25-29", "M7");
		ColumnParameters thirtyTo34M = new ColumnParameters("thirtyTo34M", "30 - 34 male", "gender=M|age=30-34", "M8");
		ColumnParameters thirty5To39M = new ColumnParameters("thirty5To39M", "35 - 39 male", "gender=M|age=35-39", "M9");
		ColumnParameters foutyTo44M = new ColumnParameters("foutyTo44M", "40 - 44 male", "gender=M|age=40-44", "M10");
		ColumnParameters fouty5To49M = new ColumnParameters("fouty5To49M", "45 - 49 male", "gender=M|age=45-49", "M11");
		ColumnParameters above50M = new ColumnParameters("above50M", "50+ male", "gender=M|age=50+", "M12");
		
		ColumnParameters unknownF = new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "UNKF");
		ColumnParameters tenTo14F = new ColumnParameters("tenTo14F", "10 - 14 female", "gender=F|age=10-14", "F4");
		ColumnParameters fifteenTo19F = new ColumnParameters("fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "F5");
		ColumnParameters twentyTo24F = new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "F6");
		ColumnParameters twenty5To29F = new ColumnParameters("twenty4To29F", "25 - 29 female", "gender=F|age=25-29", "F7");
		ColumnParameters thirtyTo34F = new ColumnParameters("thirtyTo34F", "30 - 34 female", "gender=F|age=30-34", "F8");
		ColumnParameters thirty5To39F = new ColumnParameters("thirty5To39F", "35 - 39 female", "gender=F|age=35-39", "F9");
		ColumnParameters foutyTo44F = new ColumnParameters("foutyTo44F", "40 - 44 female", "gender=F|age=40-44", "F10");
		ColumnParameters fouty5To49F = new ColumnParameters("fouty5To49F", "45 - 49 female", "gender=F|age=45-49", "F11");
		ColumnParameters above50F = new ColumnParameters("above50F", "50+ female", "gender=F|age=50+", "F12");
		ColumnParameters unknown = new ColumnParameters("unknown", "Unknown age", "age=UK", "UNK");
		
		return Arrays.asList(unknownM, tenTo14M, fifteenTo19M, twentyTo24M, twenty5To29M, thirtyTo34M, thirty5To39M,
		    foutyTo44M, fouty5To49M, above50M, unknownF, tenTo14F, fifteenTo19F, twentyTo24F, twenty5To29F, thirtyTo34F,
		    thirty5To39F, foutyTo44F, fouty5To49F, above50F, unknown);
	}
}
