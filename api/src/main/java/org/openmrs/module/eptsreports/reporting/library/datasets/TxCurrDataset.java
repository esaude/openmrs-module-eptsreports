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
import java.util.Arrays;
import java.util.List;

import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CompositionCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.EncounterCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.InprogramCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.SqlCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.HivIndicators;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxCurrDataset {
	
	@Autowired
	private AgeCohortQueries ageCohortQueries;
	
	@Autowired
	private GenderCohortQueries genderCohortQueries;
	
	@Autowired
	private SqlCohortQueries sqlCohortQueries;
	
	@Autowired
	private EncounterCohortQueries encounterCohortQueries;
	
	@Autowired
	private CompositionCohortQueries compositionCohortQueries;
	
	@Autowired
	private InprogramCohortQueries inprogramCohortQueries;
	
	@Autowired
	private HivIndicators hivIndicators;
	
	@Autowired
	private HivMetadata hivMetadata;
	
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(ReportingConstants.START_DATE_PARAMETER);
		parameters.add(ReportingConstants.END_DATE_PARAMETER);
		parameters.add(ReportingConstants.LOCATION_PARAMETER);
		return parameters;
	}
	
	public CohortIndicatorDataSetDefinition constructTxNewDatset(List<Parameter> parameters) {
		
		CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
		dataSetDefinition.setName("TX_CURR Data Set");
		dataSetDefinition.addParameters(parameters);
		
		/*
		 * Looks for patients enrolled in ART program (program 2=SERVICO TARV -
		 * TRATAMENTO) before or on end date
		 */
		CohortDefinition enrolledBeforeEndDate = sqlCohortQueries.getEnrolledInARTBeforeEndDate();
		
		/*
		 * Looks for patients registered as START DRUGS (answer to question 1255 = ARV
		 * PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type
		 * 18=S.TARV: FARMACIA) or follow up consultation for adults and children
		 * (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA
		 * SEGUIMENTO) before or on end date
		 */
		CohortDefinition patientWithSTARTDRUGSObs = sqlCohortQueries.getPatientWithSTARTDRUGSObsBeforeOrOnEndDate();
		
		/*
		 * Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in
		 * drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation
		 * for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
		 * 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date
		 */
		CohortDefinition patientWithHistoricalDrugStartDateObs = sqlCohortQueries.getPatientWithHistoricalDrugStartDateObsBeforeOrOnEndDate();
		
		// Looks for patients who had at least one drug pick up (encounter type
		// 18=S.TARV: FARMACIA) before end date
		CohortDefinition patientsWithDrugPickUpEncounters = sqlCohortQueries.getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate();
		
		// Looks for patients enrolled on art program (program 2 - SERVICO TARV -
		// TRATAMENTO) who left ART program
		SqlCohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate = sqlCohortQueries.getPatientsWhoLeftARTProgramBeforeOrOnEndDate();
		
		// Looks for patients that from the date scheduled for next drug pickup (concept
		// 5096=RETURN VISIT DATE FOR ARV DRUG) until end date have completed 60 days
		// and have not returned
		SqlCohortDefinition patientsWhoHaveNotReturned = sqlCohortQueries.getPatientsWhoHaveNotReturned();
		
		// Looks for patients that from the date scheduled for next follow up
		// consultation (concept 1410=RETURN VISIT DATE) until the end date have not
		// completed 60 days
		SqlCohortDefinition patientsWhoHaveNotCompleted60Days = sqlCohortQueries.getPatientsWhoHaveNotCompleted60Days();
		
		// Looks for patients that were registered as abandonment (program workflow
		// state is 9=ABANDONED) but from the date scheduled for next drug pick up
		// (concept 5096=RETURN VISIT DATE FOR ARV DRUG) until the end date have not
		// completed 60 days
		SqlCohortDefinition abandonedButHaveNotcompleted60Days = sqlCohortQueries.getAbandonedButHaveNotcompleted60Days();
		
		CohortDefinition males = genderCohortQueries.MaleCohort();
		
		CohortDefinition females = genderCohortQueries.FemaleCohort();
		
		CohortDefinition PatientBelow1Year = ageCohortQueries.patientWithAgeBelow(1);
		CohortDefinition PatientBetween1And9Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween1And9Years", 1, 9);
		CohortDefinition PatientBetween10And14Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween10And14Years", 10, 14);
		CohortDefinition PatientBetween15And19Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween15And19Years", 15, 19);
		CohortDefinition PatientBetween20And24Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween20And24Years", 20, 24);
		CohortDefinition PatientBetween25And29Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween25And29Years", 25, 29);
		CohortDefinition PatientBetween30And34Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween30And34Years", 30, 34);
		CohortDefinition PatientBetween35And39Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween35And39Years", 35, 39);
		CohortDefinition PatientBetween40And49Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween40And49Years", 40, 49);
		CohortDefinition PatientBetween50YearsAndAbove = ageCohortQueries.patientWithAgeAbove(50);
		PatientBetween50YearsAndAbove.setName("PatientBetween50YearsAndAbove");
		
		ArrayList<CohortDefinition> agesRange = new ArrayList<CohortDefinition>();
		agesRange.add(PatientBetween10And14Years);
		agesRange.add(PatientBetween15And19Years);
		agesRange.add(PatientBetween20And24Years);
		agesRange.add(PatientBetween25And29Years);
		agesRange.add(PatientBetween30And34Years);
		agesRange.add(PatientBetween35And39Years);
		agesRange.add(PatientBetween40And49Years);
		agesRange.add(PatientBetween50YearsAndAbove);
		
		// Male and Female <1
		CompositionCohortDefinition patientBellowOneYearCurrentlyInART = compositionCohortQueries.getPatientBellowOneYearCurrentlyInARTStarted(enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs, patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsWhoHaveNotReturned, patientsWhoHaveNotCompleted60Days, abandonedButHaveNotcompleted60Days, PatientBelow1Year);
		CohortIndicator patientBelow1YearCurrentlyInARTIndicator = hivIndicators.patientBelow1YearEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(patientBellowOneYearCurrentlyInART);
		dataSetDefinition.addColumn("C1<1", "TX_CURR: Currently on ART: Patients below 1 year", new Mapped<CohortIndicator>(patientBelow1YearCurrentlyInARTIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")), "");
		
		// Male and Female between 1 and 9 years
		CompositionCohortDefinition patientBetween1And9YearsCurrentlyInART = compositionCohortQueries.getPatientBetween1And9YearsCurrently(enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs, patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsWhoHaveNotReturned, patientsWhoHaveNotCompleted60Days, abandonedButHaveNotcompleted60Days, PatientBetween1And9Years);
		CohortIndicator patientBetween1And9YearsCurrentlyInARTIndicator = hivIndicators.patientBetween1And9YearsEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(patientBetween1And9YearsCurrentlyInART);
		dataSetDefinition.addColumn("C119", "TX_CURR: Currently on ART: Patients between 1 and 9 years", new Mapped<CohortIndicator>(patientBetween1And9YearsCurrentlyInARTIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")), "");
		
		// Male
		
		int i = 2;
		for (CohortDefinition ageCohort : agesRange) {
			CompositionCohortDefinition patientInYearRange = compositionCohortQueries.getPatientInYearRangeCurrentlyInARTStarted(enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs, patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsWhoHaveNotReturned, patientsWhoHaveNotCompleted60Days, abandonedButHaveNotcompleted60Days, PatientBetween1And9Years, ageCohort, males);
			CohortIndicator patientInYearRangeCurrenltyInHIVStartedARTIndicator = hivIndicators.patientInYearRangeEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(patientInYearRange);
			dataSetDefinition.addColumn("C1M" + i, "Males:TX_CURR: Currently on ART by age and sex: " + ageCohort.getName(), new Mapped<CohortIndicator>(patientInYearRangeCurrenltyInHIVStartedARTIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")), "");
			
			i++;
		}
		
		// Females
		int j = 2;
		for (CohortDefinition ageCohort : agesRange) {
			CompositionCohortDefinition patientInYearRange = compositionCohortQueries.getPatientInYearRangeCurrentlyInARTStarted(enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs, patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsWhoHaveNotReturned, patientsWhoHaveNotCompleted60Days, abandonedButHaveNotcompleted60Days, PatientBetween1And9Years, ageCohort, females);
			CohortIndicator patientInYearRangeCurrenltyInHIVStartedARTIndicator = hivIndicators.patientInYearRangeEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(patientInYearRange);
			
			dataSetDefinition.addColumn("C1F" + j, "Females:TX_CURR: Currently on ART by age and sex: " + ageCohort.getName(), new Mapped<CohortIndicator>(patientInYearRangeCurrenltyInHIVStartedARTIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")), "");
			j++;
		}
		
		CompositionCohortDefinition allPatientsCurrentlyInART = compositionCohortQueries.getAllPatientsCurrentlyInARTStarted(enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs, patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsWhoHaveNotReturned, patientsWhoHaveNotCompleted60Days, abandonedButHaveNotcompleted60Days);
		CohortIndicator allPatientsCurrentlyInARTARTIndicator = hivIndicators.patientEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(allPatientsCurrentlyInART);
		dataSetDefinition.addColumn("C1All", "TX_CURR: Currently on ART", new Mapped<CohortIndicator>(allPatientsCurrentlyInARTARTIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")), "");
		
		return dataSetDefinition;
	}
}
