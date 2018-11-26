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
import java.util.List;

import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CompositionCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.SqlCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.HivIndicators;
import org.openmrs.module.eptsreports.reporting.library.indicators.TbIndicators;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxNewDataset {
	
	@Autowired
	private AgeCohortQueries ageCohortQueries;
	
	@Autowired
	private GenderCohortQueries genderCohortQueries;
	
	@Autowired
	private SqlCohortQueries sqlCohortQueries;
	
	@Autowired
	private CompositionCohortQueries compositionCohortQueries;
	
	@Autowired
	private HivIndicators hivIndicators;
	
	@Autowired
	private TbIndicators tbIndicators;
	
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(ReportingConstants.START_DATE_PARAMETER);
		parameters.add(ReportingConstants.END_DATE_PARAMETER);
		parameters.add(ReportingConstants.LOCATION_PARAMETER);
		return parameters;
	}
	
	public DataSetDefinition constructTxNewDatset(List<Parameter> parameters) {
		
		CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
		dataSetDefinition.setName("TX_NEW Data Set");
		dataSetDefinition.addParameters(parameters);
		
		// Looks for patients enrolled in ART program (program 2=SERVICO TARV -
		// TRATAMENTO) before or on end date
		CohortDefinition inARTProgramDuringTimePeriod = sqlCohortQueries.getPatientsinARTProgramDuringTimePeriod();
		
		// Looks for patients registered as START DRUGS (answer to question 1255 = ARV
		// PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type
		// 18=S.TARV: FARMACIA) or follow up consultation for adults and children
		// (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA
		// SEGUIMENTO) before or on end date
		CohortDefinition patientWithSTARTDRUGSObs = sqlCohortQueries.getPatientWithSTARTDRUGSObs();
		
		// Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in
		// drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation
		// for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
		// 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date
		CohortDefinition patientWithHistoricalDrugStartDateObs = sqlCohortQueries.getPatientWithHistoricalDrugStartDateObs();
		
		// Looks for patients enrolled on ART program (program 2=SERVICO TARV -
		// TRATAMENTO), transferred from other health facility (program workflow state
		// is 29=TRANSFER FROM OTHER FACILITY) between start date and end date
		CohortDefinition transferredFromOtherHealthFacility = sqlCohortQueries.getPatientsTransferredFromOtherHealthFacility();
		
		CohortDefinition males = genderCohortQueries.MaleCohort();
		
		CohortDefinition females = genderCohortQueries.FemaleCohort();
		
		CohortDefinition patientBelow1Year = ageCohortQueries.patientWithAgeBelow(1);
		CohortDefinition patientBetween1And4Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween1And4Years", 1, 4);
		CohortDefinition patientBetween5And9Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween5And9Years", 5, 9);
		CohortDefinition patientBetween10And14Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween10And14Years", 10, 14);
		CohortDefinition patientBetween15And19Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween15And19Years", 15, 19);
		CohortDefinition patientBetween20And24Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween20And24Years", 20, 24);
		CohortDefinition patientBetween25And29Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween25And29Years", 25, 29);
		CohortDefinition patientBetween30And34Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween30And34Years", 30, 34);
		CohortDefinition patientBetween35And39Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween35And39Years", 35, 39);
		CohortDefinition patientBetween40And49Years = ageCohortQueries.createXtoYAgeCohort("PatientBetween40And49Years", 40, 49);
		CohortDefinition patientBetween50YearsAndAbove = ageCohortQueries.patientWithAgeAbove(50);
		patientBetween50YearsAndAbove.setName("PatientBetween50YearsAndAbove");
		
		ArrayList<CohortDefinition> agesRange = new ArrayList<CohortDefinition>();
		agesRange.add(patientBelow1Year);
		agesRange.add(patientBetween1And4Years);
		agesRange.add(patientBetween5And9Years);
		agesRange.add(patientBetween10And14Years);
		agesRange.add(patientBetween15And19Years);
		agesRange.add(patientBetween20And24Years);
		agesRange.add(patientBetween25And29Years);
		agesRange.add(patientBetween30And34Years);
		agesRange.add(patientBetween35And39Years);
		agesRange.add(patientBetween40And49Years);
		agesRange.add(patientBetween50YearsAndAbove);
		
		// Male
		int i = 0;
		for (CohortDefinition ageCohort : agesRange) {
			CohortDefinition patientInYearRangeEnrolledInARTStarted = compositionCohortQueries.getPatientInYearRangeEnrolledInARTStarted(inARTProgramDuringTimePeriod, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs, transferredFromOtherHealthFacility, patientBetween1And4Years, ageCohort, males);
			CohortIndicator patientInYearRangeEnrolledInHIVStartedARTIndicator = hivIndicators.patientInYearRangeEnrolledInHIVStartedARTIndicator(patientInYearRangeEnrolledInARTStarted);
			dataSetDefinition.addColumn("1M" + i, "Males:TX_NEW: New on ART by age and sex: " + ageCohort.getName(), new Mapped<CohortIndicator>(patientInYearRangeEnrolledInHIVStartedARTIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")), "");
			i++;
		}
		
		// Female
		int j = 0;
		for (CohortDefinition ageCohort : agesRange) {
			CohortDefinition patientInYearRangeEnrolledInARTStarted = compositionCohortQueries.getPatientInYearRangeEnrolledInARTStarted(inARTProgramDuringTimePeriod, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs, transferredFromOtherHealthFacility, patientBetween1And4Years, ageCohort, females);
			CohortIndicator patientInYearRangeEnrolledInHIVStartedARTIndicator = hivIndicators.patientInYearRangeEnrolledInHIVStartedARTIndicator(patientInYearRangeEnrolledInARTStarted);
			dataSetDefinition.addColumn("1F" + j, "Females:TX_NEW: New on ART by age and sex: " + ageCohort.getName(), new Mapped<CohortIndicator>(patientInYearRangeEnrolledInHIVStartedARTIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")), "");
			j++;
		}
		
		CohortDefinition patientEnrolledInART = compositionCohortQueries.getPatientEnrolledInART(inARTProgramDuringTimePeriod, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs, transferredFromOtherHealthFacility);
		CohortIndicator patientEnrolledInHIVStartedARTIndicator = hivIndicators.patientEnrolledInHIVStartedARTIndicator(patientEnrolledInART);
		dataSetDefinition.addColumn("1All", "TX_NEW: New on ART", new Mapped<CohortIndicator>(patientEnrolledInHIVStartedARTIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")), "");
		
		// Obtain patients notified to be on TB treatment
		CohortDefinition notifiedToBeOnTbTreatment = sqlCohortQueries.getPatientsNotifiedToBeOnTbTreatment();
		CohortIndicator tuberculosePatientNewlyInitiatingARTIndicator = tbIndicators.tuberculosePatientNewlyInitiatingARTIndicator(notifiedToBeOnTbTreatment);
		dataSetDefinition.addColumn("TB", "TX_NEW: TB Started ART", new Mapped<CohortIndicator>(tuberculosePatientNewlyInitiatingARTIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")), "");
		
		return dataSetDefinition;
	}
}
