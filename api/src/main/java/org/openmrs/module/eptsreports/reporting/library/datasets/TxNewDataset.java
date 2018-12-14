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

import java.util.ArrayList;

import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.HivCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.HivIndicators;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxNewDataset extends BaseDataSet {
	
	@Autowired
	private AgeCohortQueries ageCohortQueries;
	
	@Autowired
	private GenderCohortQueries genderCohortQueries;
	
	@Autowired
	private TxNewCohortQueries txNewCohortQueries;
	
	@Autowired
	private HivIndicators hivIndicators;
	
	@Autowired
	private EptsCommonDimension eptsCommonDimension;
	
	@Autowired
	private HivCohortQueries hivCohortQueries;

	public DataSetDefinition constructTxNewDataset() {
		
		CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
		dataSetDefinition.setName("TX_NEW Data Set");
		dataSetDefinition.addParameters(getParameters());
		
		// Looks for patients enrolled in ART program (program 2=SERVICO TARV -
		// TRATAMENTO) before or on end date
		CohortDefinition inARTProgramDuringTimePeriod = txNewCohortQueries.getPatientsinARTProgramDuringTimePeriod();
		
		// Looks for patients registered as START DRUGS (answer to question 1255 = ARV
		// PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type
		// 18=S.TARV: FARMACIA) or follow up consultation for adults and children
		// (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA
		// SEGUIMENTO) before or on end date
		CohortDefinition patientWithSTARTDRUGSObs = txNewCohortQueries.getPatientWithSTARTDRUGSObs();
		
		// Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in
		// drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation
		// for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
		// 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date
		CohortDefinition patientWithHistoricalDrugStartDateObs = txNewCohortQueries.getPatientWithHistoricalDrugStartDateObs();
		
		// Looks for patients who had at least one drug pick up (encounter type
		// 18=S.TARV: FARMACIA) before end date
		CohortDefinition patientsWithDrugPickUpEncounters = txNewCohortQueries.getPatientWithFirstDrugPickupEncounter();
		
		// Looks for patients enrolled on ART program (program 2=SERVICO TARV -
		// TRATAMENTO), transferred from other health facility (program workflow state
		// is 29=TRANSFER FROM OTHER FACILITY) between start date and end date
		CohortDefinition transferredFromOtherHealthFacility = txNewCohortQueries.getPatientsTransferredFromOtherHealthFacility();
		
		CohortDefinition patientsWhoRestartedTreatment = hivCohortQueries.getPatientsWhoRestartedTreatment();

		CohortDefinition males = genderCohortQueries.MaleCohort();
		
		CohortDefinition females = genderCohortQueries.FemaleCohort();
		
		// The maxAge value in these age cohorts are one year above the actual year to
		// fit the "<" operator used in the UnionQueries
		AgeCohortDefinition patientBelow1Year = new AgeCohortDefinition();
		patientBelow1Year.setName("patientBelow1Year");
		patientBelow1Year.setMaxAge(1);

		ArrayList<CohortDefinition> agesRange = new ArrayList<CohortDefinition>();
		agesRange.add(patientBelow1Year);
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween1And9Years", 1, 5));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween1And9Years", 5, 10));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween10And14Years", 10, 15));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween15And19Years", 15, 20));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween20And24Years", 20, 25));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween25And29Years", 25, 30));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween30And34Years", 30, 35));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween35And39Years", 35, 40));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween40And49Years", 40, 45));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween40And49Years", 45, 50));
		agesRange.add(ageCohortQueries.createOverXAgeCohort("PatientBetween50YearsAndAbove", 50));
		
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		
		// Male
		int i = 0;
		for (CohortDefinition ageCohort : agesRange) {
			CohortDefinition patientInYearRangeEnrolledInARTStarted = txNewCohortQueries.getTxNewCompositionCohort(
			    "patientEnrolledInARTStartedMales", inARTProgramDuringTimePeriod, patientWithSTARTDRUGSObs,
			    patientWithHistoricalDrugStartDateObs, patientsWithDrugPickUpEncounters, transferredFromOtherHealthFacility,
			    patientsWhoRestartedTreatment, ageCohort, males);
			CohortIndicator patientInYearRangeEnrolledInHIVStartedARTIndicator = hivIndicators
			        .patientInYearRangeEnrolledInHIVStartedARTIndicator(patientInYearRangeEnrolledInARTStarted);
			dataSetDefinition.addColumn("1M" + i, "Males:TX_NEW: New on ART by age and sex: " + ageCohort.getName(),
			    new Mapped<CohortIndicator>(patientInYearRangeEnrolledInHIVStartedARTIndicator,
			            ParameterizableUtil.createParameterMappings(mappings)),
			    "");
			
			i++;
		}
		
		// Female
		int j = 0;
		for (CohortDefinition ageCohort : agesRange) {
			CohortDefinition patientInYearRangeEnrolledInARTStarted = txNewCohortQueries.getTxNewCompositionCohort(
			    "patientEnrolledInARTStartedMales", inARTProgramDuringTimePeriod, patientWithSTARTDRUGSObs,
			    patientWithHistoricalDrugStartDateObs, patientsWithDrugPickUpEncounters, transferredFromOtherHealthFacility,
			    patientsWhoRestartedTreatment, ageCohort, females);
			CohortIndicator patientInYearRangeEnrolledInHIVStartedARTIndicator = hivIndicators
			        .patientInYearRangeEnrolledInHIVStartedARTIndicator(patientInYearRangeEnrolledInARTStarted);
			dataSetDefinition.addColumn("1F" + j, "Females:TX_NEW: New on ART by age and sex: " + ageCohort.getName(),
			    new Mapped<CohortIndicator>(patientInYearRangeEnrolledInHIVStartedARTIndicator,
			            ParameterizableUtil.createParameterMappings(mappings)),
			    "");
			j++;
		}
		
		CohortDefinition patientEnrolledInART = txNewCohortQueries.getTxNewCompositionCohort("patientEnrolledInART",
		    inARTProgramDuringTimePeriod, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs,
		    patientsWithDrugPickUpEncounters, transferredFromOtherHealthFacility, patientsWhoRestartedTreatment, null, null);
		CohortIndicator patientEnrolledInHIVStartedARTIndicator = hivIndicators
		        .patientEnrolledInHIVStartedARTIndicator(patientEnrolledInART);
		dataSetDefinition.addColumn("1All", "TX_NEW: New on ART", new Mapped<CohortIndicator>(patientEnrolledInHIVStartedARTIndicator,
		        ParameterizableUtil.createParameterMappings(mappings)),
		    "");
		
		// Obtain patients breastfeeding newly enrolled on ART
		dataSetDefinition.addDimension("maternity", EptsReportUtils.map(eptsCommonDimension.maternityDimension(), mappings));
		dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
		dataSetDefinition.addDimension("age", EptsReportUtils.map(eptsCommonDimension.age(), "effectiveDate=${endDate}"));
		
		dataSetDefinition.addColumn("ANC", "TX_NEW: Pregnant Started ART",
		    EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings), "maternity=breastfeeding");
		dataSetDefinition.addColumn("UM", "Males:TX_NEW: New on ART by age and sex: Unknown Age",
		    EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings), "gender=M|age=unknown");
		dataSetDefinition.addColumn("UF", "Females:TX_NEW: New on ART by age and sex: Unknown Age",
		    EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings), "gender=F|age=unknown");

		return dataSetDefinition;
	}
}
