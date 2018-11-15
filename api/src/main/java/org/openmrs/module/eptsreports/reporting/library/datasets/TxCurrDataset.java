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
import org.openmrs.module.eptsreports.reporting.library.cohorts.EncounterCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.SqlCohortQueries;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxCurrDataset {
	
	@Autowired
	private SqlCohortQueries sqlCohortQueries;
	
	@Autowired
	private EncounterCohortQueries encountertQueries;
	
	@Autowired
	private HivMetadata hivMetadata;
	
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(ReportingConstants.START_DATE_PARAMETER);
		parameters.add(ReportingConstants.END_DATE_PARAMETER);
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
		SqlCohortDefinition inARTProgramDuringTimePeriod = sqlCohortQueries.getPatientsinARTProgramDuringTimePeriod();
		
		/*
		 * Looks for patients registered as START DRUGS (answer to question 1255 = ARV
		 * PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type
		 * 18=S.TARV: FARMACIA) or follow up consultation for adults and children
		 * (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA
		 * SEGUIMENTO) before or on end date
		 */
		SqlCohortDefinition patientWithSTARTDRUGSObs = sqlCohortQueries.getPatientWithSTARTDRUGSObs();
		
		/*
		 * Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in
		 * drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation
		 * for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
		 * 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date
		 */
		SqlCohortDefinition patientWithHistoricalDrugStartDateObs = sqlCohortQueries
		        .getPatientWithHistoricalDrugStartDateObs();
		
		// Looks for patients who had at least one drug pick up (encounter type
		// 18=S.TARV: FARMACIA) before end date
		EncounterCohortDefinition patientsWithDrugPickUpEncounters = encountertQueries.createEncounterParameterizedByDate(
		    "patientsWithDrugPickUpEncounters", Arrays.asList("onOrBefore"), hivMetadata.getARVPharmaciaEncounterType());
		
		// Looks for patients enrolled on art program (program 2 - SERVICO TARV -
		// TRATAMENTO) who left ART program
		SqlCohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate = sqlCohortQueries
		        .getPatientsWhoLeftARTProgramBeforeOrOnEndDate();
		
		// Looks for patients that from the date scheduled for next drug pickup (concept
		// 5096=RETURN VISIT DATE FOR ARV DRUG) until end date have completed 60 days
		// and have not returned
		SqlCohortDefinition patientsWhoHaveNotReturned = sqlCohortQueries.getPatientsWhoHaveNotReturned();
		return dataSetDefinition;
	}
}
