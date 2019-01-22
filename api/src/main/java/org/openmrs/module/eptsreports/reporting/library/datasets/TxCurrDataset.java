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

import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.HivIndicators;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxCurrDataset extends BaseDataSet {
	
	@Autowired
	private AgeCohortQueries ageCohortQueries;
	
	@Autowired
	private GenderCohortQueries genderCohortQueries;
	
	@Autowired
	private TxCurrCohortQueries txCurrCohortQueries;
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	@Autowired
	private HivIndicators hivIndicators;
	
	@Autowired
	private HivMetadata hivMetadata;
	
	public CohortIndicatorDataSetDefinition constructTxCurrDataset(boolean currentSpec) {
		
		CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
		dataSetDefinition.setName("TX_CURR Data Set");
		dataSetDefinition.addParameters(getParameters());
		
		CohortDefinition enrolledBeforeEndDate = genericCohortQueries.createInProgram("InARTProgram",
		    hivMetadata.getARTProgram());
		CohortDefinition patientWithSTARTDRUGSObs = txCurrCohortQueries.getPatientWithSTARTDRUGSObsBeforeOrOnEndDate();
		CohortDefinition patientWithHistoricalDrugStartDateObs = txCurrCohortQueries
		        .getPatientWithHistoricalDrugStartDateObsBeforeOrOnEndDate();
		CohortDefinition patientsWithDrugPickUpEncounters = txCurrCohortQueries
		        .getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate();
		SqlCohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate = txCurrCohortQueries
		        .getPatientsWhoLeftARTProgramBeforeOrOnEndDate();
		SqlCohortDefinition patientsThatMissedNexPickup = txCurrCohortQueries.getPatientsThatMissedNexPickup();
		SqlCohortDefinition patientsThatMissNextConsultation = txCurrCohortQueries.getPatientsThatMissNextConsultation();
		SqlCohortDefinition patientsReportedAsAbandonmentButStillInPeriod = txCurrCohortQueries
		        .getPatientsReportedAsAbandonmentButStillInPeriod();
		SqlCohortDefinition patientsWithNextPickupDate = txCurrCohortQueries.getPatientsWithNextPickupDate();
		SqlCohortDefinition patientsWithNextConsultationDate = txCurrCohortQueries.getPatientsWithNextConsultationDate();
		
		CohortDefinition males = genderCohortQueries.MaleCohort();
		CohortDefinition females = genderCohortQueries.FemaleCohort();
		
		ArrayList<CohortDefinition> agesRange = createAgeCohorts();
		
		final String columnNameTemplate = "C1%s%s";
		final String labelTemplate = "%s:TX_CURR: Currently on ART by age and sex: %s";
		
		// Males
		int i = 1;
		for (CohortDefinition ageCohort : agesRange) {
			String columnName = String.format(columnNameTemplate, "M", String.valueOf(i++));
			String label = String.format(labelTemplate, "Males", ageCohort.getName());
			CohortDefinition rangeMales = txCurrCohortQueries.getTxCurrCompositionCohort("patientEnrolledInARTStartedMales",
			    enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs,
			    patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsThatMissedNexPickup,
			    patientsThatMissNextConsultation, patientsReportedAsAbandonmentButStillInPeriod, ageCohort, males,
			    patientsWithNextPickupDate, patientsWithNextConsultationDate, currentSpec);
			CohortIndicator indicator = hivIndicators
			        .patientInYearRangeEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(rangeMales);
			dataSetDefinition.addColumn(
			    columnName,
			    label,
			    new Mapped<CohortIndicator>(indicator, ParameterizableUtil
			            .createParameterMappings("endDate=${endDate},location=${location}")), "");
		}
		
		// Females
		i = 1;
		for (CohortDefinition ageCohort : agesRange) {
			String columnName = String.format(columnNameTemplate, "F", String.valueOf(i++));
			String label = String.format(labelTemplate, "Females", ageCohort.getName());
			CohortDefinition rangeFemales = txCurrCohortQueries.getTxCurrCompositionCohort(
			    "patientEnrolledInARTStartedFemales", enrolledBeforeEndDate, patientWithSTARTDRUGSObs,
			    patientWithHistoricalDrugStartDateObs, patientsWithDrugPickUpEncounters,
			    patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsThatMissedNexPickup, patientsThatMissNextConsultation,
			    patientsReportedAsAbandonmentButStillInPeriod, ageCohort, females, patientsWithNextPickupDate,
			    patientsWithNextConsultationDate, currentSpec);
			CohortIndicator indicator = hivIndicators
			        .patientInYearRangeEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(rangeFemales);
			dataSetDefinition.addColumn(
			    columnName,
			    label,
			    new Mapped<CohortIndicator>(indicator, ParameterizableUtil
			            .createParameterMappings("endDate=${endDate},location=${location}")), "");
		}
		
		// Unknown males
		CohortDefinition unknownMales = txCurrCohortQueries.getTxCurrCompositionCohort("allPatientsCurrentlyInART",
		    enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs,
		    patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsThatMissedNexPickup,
		    patientsThatMissNextConsultation, patientsReportedAsAbandonmentButStillInPeriod,
		    genericCohortQueries.getUnknownAgeCohort(), males, patientsWithNextPickupDate, patientsWithNextConsultationDate,
		    currentSpec);
		CohortIndicator unknownMalesIndicator = hivIndicators
		        .patientEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(unknownMales);
		dataSetDefinition.addColumn("C1UNKM", "TX_CURR: Unknown Age", new Mapped<CohortIndicator>(unknownMalesIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")), "");
		
		// Unknown females
		CohortDefinition unknownFemales = txCurrCohortQueries.getTxCurrCompositionCohort("allPatientsCurrentlyInART",
		    enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs,
		    patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsThatMissedNexPickup,
		    patientsThatMissNextConsultation, patientsReportedAsAbandonmentButStillInPeriod,
		    genericCohortQueries.getUnknownAgeCohort(), females, patientsWithNextPickupDate,
		    patientsWithNextConsultationDate, currentSpec);
		CohortIndicator unknownFemalesIndicator = hivIndicators
		        .patientEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(unknownFemales);
		dataSetDefinition.addColumn("C1UNKF", "TX_CURR: Unknown Age", new Mapped<CohortIndicator>(unknownFemalesIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")), "");
		
		// Total
		CohortDefinition all = txCurrCohortQueries.getTxCurrCompositionCohort("allPatientsCurrentlyInART",
		    enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs,
		    patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsThatMissedNexPickup,
		    patientsThatMissNextConsultation, patientsReportedAsAbandonmentButStillInPeriod, null, null,
		    patientsWithNextPickupDate, patientsWithNextConsultationDate, currentSpec);
		CohortIndicator allIndicator = hivIndicators.patientEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(all);
		dataSetDefinition.addColumn("C1All", "TX_CURR: Currently on ART", new Mapped<CohortIndicator>(allIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")), "");
		
		return dataSetDefinition;
	}
	
	private ArrayList<CohortDefinition> createAgeCohorts() {
		ArrayList<CohortDefinition> agesRange = new ArrayList<CohortDefinition>();
		agesRange.add(ageCohortQueries.createBelowYAgeCohort("PatientBelow1Year", 1));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween1And9Years", 1, 4));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween1And9Years", 5, 9));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween10And14Years", 10, 14));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween15And19Years", 15, 19));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween20And24Years", 20, 24));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween25And29Years", 25, 29));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween30And34Years", 30, 34));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween35And39Years", 35, 39));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween40And49Years", 40, 44));
		agesRange.add(ageCohortQueries.createXtoYAgeCohort("PatientBetween40And49Years", 45, 49));
		agesRange.add(ageCohortQueries.createOverXAgeCohort("PatientBetween50YearsAndAbove", 50));
		return agesRange;
	}
}
