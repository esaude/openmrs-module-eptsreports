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
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Collections;
import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.ViralLoadQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines all of the HivCohortDefinition instances we want to expose for EPTS
 */
@Component
public class HivCohortQueries {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private GenericCohortQueries genericCohortQueires;
	
	/**
	 * Adult and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml)
	 * documented in the medical records and /or supporting laboratory results within the past 12
	 * months
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "suppressedViralLoadWithin12Months")
	public CohortDefinition getPatientsWithSuppressedViralLoadWithin12Months() {
		SqlCohortDefinition sql = new SqlCohortDefinition();
		sql.setName("suppressedViralLoadWithin12Months");
		sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
		sql.addParameter(new Parameter("endDate", "End Date", Date.class));
		sql.addParameter(new Parameter("location", "Location", Location.class));
		sql.setQuery(ViralLoadQueries.getPatientsWithViralLoadSuppression(hivMetadata.getMisauLaboratorioEncounterType()
		        .getEncounterTypeId(), hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
				hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(), hivMetadata.getHivViralLoadConcept().getConceptId()));
		return sql;
	}
	
	/**
	 * Number of adult and pediatric ART patients with a viral load result documented in the patient
	 * medical record and/ or laboratory records in the past 12 months.
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "viralLoadWithin12Months")
	public CohortDefinition getPatientsViralLoadWithin12Months() {
		SqlCohortDefinition sql = new SqlCohortDefinition();
		sql.setName("viralLoadWithin12Months");
		sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
		sql.addParameter(new Parameter("endDate", "End Date", Date.class));
		sql.addParameter(new Parameter("location", "Location", Location.class));
		sql.setQuery(ViralLoadQueries.getPatientsHavingViralLoadInLast12Months(hivMetadata
		        .getMisauLaboratorioEncounterType().getEncounterTypeId(), hivMetadata.getAdultoSeguimentoEncounterType()
				.getEncounterTypeId(), hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(), hivMetadata.getHivViralLoadConcept()
		        .getConceptId()));
		return sql;
	}
	
	/**
	 * Adult and pediatric patients on ART who have re-initiated the treatment.
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "restartedTreatment")
	public CohortDefinition getPatientsWhoRestartedTreatment() {
		return genericCohortQueires.hasCodedObs(hivMetadata.getARVPlanConcept(),
		    Collections.singletonList(hivMetadata.getRestartConcept()));
	}
}
