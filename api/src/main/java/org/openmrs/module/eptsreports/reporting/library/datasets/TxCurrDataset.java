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
	
	public CohortIndicatorDataSetDefinition constructTxNewDatset() {
		
		CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
		dataSetDefinition.setName("TX_CURR Data Set");
		dataSetDefinition.addParameters(getParameters());
		
		/*
		 * Looks for patients enrolled in ART program (program 2=SERVICO TARV -
		 * TRATAMENTO) before or on end date
		 */
		CohortDefinition enrolledBeforeEndDate = genericCohortQueries.createInProgram("InARTProgram", hivMetadata.getARTProgram());
		
		/*
		 * Looks for patients registered as START DRUGS (answer to question 1255 = ARV
		 * PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type
		 * 18=S.TARV: FARMACIA) or follow up consultation for adults and children
		 * (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA
		 * SEGUIMENTO) before or on end date
		 */
		CohortDefinition patientWithSTARTDRUGSObs = txCurrCohortQueries.getPatientWithSTARTDRUGSObsBeforeOrOnEndDate();
		
		/*
		 * Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in
		 * drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation
		 * for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
		 * 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date
		 */
		CohortDefinition patientWithHistoricalDrugStartDateObs = txCurrCohortQueries
		        .getPatientWithHistoricalDrugStartDateObsBeforeOrOnEndDate();
		
		// Looks for patients who had at least one drug pick up (encounter type
		// 18=S.TARV: FARMACIA) before end date
		CohortDefinition patientsWithDrugPickUpEncounters = txCurrCohortQueries
		        .getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate();
		
		// Looks for patients enrolled on art program (program 2 - SERVICO TARV -
		// TRATAMENTO) who left ART program
		SqlCohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate = txCurrCohortQueries
		        .getPatientsWhoLeftARTProgramBeforeOrOnEndDate();
		
		// Looks for patients that from the date scheduled for next drug pickup (concept
		// 5096=RETURN VISIT DATE FOR ARV DRUG) until end date have completed 60 days
		// and have not returned
		SqlCohortDefinition patientsWhoHaveNotReturned = txCurrCohortQueries.getPatientsWhoHaveNotReturned();
		
		// Looks for patients that from the date scheduled for next follow up
		// consultation (concept 1410=RETURN VISIT DATE) until the end date have not
		// completed 60 days
		SqlCohortDefinition patientsWhoHaveNotCompleted60Days = txCurrCohortQueries.patientsWhoHaveNotCompletedFollowup();
		
		// Looks for patients that were registered as abandonment (program workflow
		// state is 9=ABANDONED) but from the date scheduled for next drug pick up
		// (concept 5096=RETURN VISIT DATE FOR ARV DRUG) until the end date have not
		// completed 60 days
		SqlCohortDefinition abandonedButHaveNotcompleted60Days = txCurrCohortQueries.getAbandonedButStilInGracePeriod();
		
		SqlCohortDefinition patientsWithoutNextPickupDate = txCurrCohortQueries.getPatientsWithoutNextPickupDate();
		SqlCohortDefinition patientsWithoutNextConsultationDate = txCurrCohortQueries.getPatientsWithoutNextConsultationDate();
		
		CohortDefinition males = genderCohortQueries.MaleCohort();
		
		CohortDefinition females = genderCohortQueries.FemaleCohort();
		
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
		
		final String columnNameTemplate = "C1%s%s";
		final String labelTemplate = "%s:TX_CURR: Currently on ART by age and sex: %s";
		
		// Males
		int i = 1;
		for (CohortDefinition ageCohort : agesRange) {
			String columnName = String.format(columnNameTemplate, "M", String.valueOf(i++));
			String label = String.format(labelTemplate, "Males", ageCohort.getName());
			CohortDefinition rangeMales = txCurrCohortQueries.getTxCurrCompositionCohort("patientEnrolledInARTStartedMales",
			    enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs,
			    patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsWhoHaveNotReturned,
			    patientsWhoHaveNotCompleted60Days, abandonedButHaveNotcompleted60Days, ageCohort, males, patientsWithoutNextPickupDate,
			    patientsWithoutNextConsultationDate);
			CohortIndicator indicator = hivIndicators.patientInYearRangeEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(rangeMales);
			dataSetDefinition.addColumn(columnName, label, new Mapped<CohortIndicator>(indicator,
			        ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")),
			    "");
		}
		
		// Females
		i = 1;
		for (CohortDefinition ageCohort : agesRange) {
			String columnName = String.format(columnNameTemplate, "F", String.valueOf(i++));
			String label = String.format(labelTemplate, "Females", ageCohort.getName());
			CohortDefinition rangeFemales = txCurrCohortQueries.getTxCurrCompositionCohort("patientEnrolledInARTStartedFemales",
			    enrolledBeforeEndDate, patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs,
			    patientsWithDrugPickUpEncounters, patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsWhoHaveNotReturned,
			    patientsWhoHaveNotCompleted60Days, abandonedButHaveNotcompleted60Days, ageCohort, females,
			    patientsWithoutNextPickupDate, patientsWithoutNextConsultationDate);
			CohortIndicator indicator = hivIndicators
			        .patientInYearRangeEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(rangeFemales);
			dataSetDefinition.addColumn(columnName, label, new Mapped<CohortIndicator>(indicator,
			        ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")),
			    "");
		}
		
		// Unknown
		CohortDefinition unknown = txCurrCohortQueries.getTxCurrCompositionCohort("allPatientsCurrentlyInART", enrolledBeforeEndDate,
		    patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs, patientsWithDrugPickUpEncounters,
		    patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsWhoHaveNotReturned, patientsWhoHaveNotCompleted60Days,
		    abandonedButHaveNotcompleted60Days, genericCohortQueries.getUnknownAgeCohort(), null, patientsWithoutNextPickupDate,
		    patientsWithoutNextConsultationDate);
		CohortIndicator unknownIndicator = hivIndicators.patientEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(unknown);
		dataSetDefinition.addColumn("C1UNK", "TX_CURR: Unknown Age", new Mapped<CohortIndicator>(unknownIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")),
		    "");
		
		// Total
		CohortDefinition all = txCurrCohortQueries.getTxCurrCompositionCohort("allPatientsCurrentlyInART", enrolledBeforeEndDate,
		    patientWithSTARTDRUGSObs, patientWithHistoricalDrugStartDateObs, patientsWithDrugPickUpEncounters,
		    patientsWhoLeftARTProgramBeforeOrOnEndDate, patientsWhoHaveNotReturned, patientsWhoHaveNotCompleted60Days,
		    abandonedButHaveNotcompleted60Days, null, null, patientsWithoutNextPickupDate, patientsWithoutNextConsultationDate);
		CohortIndicator allIndicator = hivIndicators.patientEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(all);
		dataSetDefinition.addColumn("C1All", "TX_CURR: Currently on ART", new Mapped<CohortIndicator>(allIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}")),
		    "");
		
		return dataSetDefinition;
	}
}
