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
package org.openmrs.module.eptsreports.metadata;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants;
import org.springframework.stereotype.Component;

@Component("commonMetadata")
public class CommonMetadata extends Metadata {
	
	// CONCEPTS
	private String START_DRUGS = "e1d9ef28-1d5f-11e0-b929-000c29ad1d07";
	
	private String HISTORICAL_DRUG_START_DATE = "e1d8f690-1d5f-11e0-b929-000c29ad1d07";
	
	private String YES = "e1d81b62-1d5f-11e0-b929-000c29ad1d07";
	
	private String PREGNANT = "e1e056a6-1d5f-11e0-b929-000c29ad1d07";
	
	private String GESTATION = "e1cdd58a-1d5f-11e0-b929-000c29ad1d07";
	
	private String NUMBER_OF_WEEKS_PREGNANT = "e1da0788-1d5f-11e0-b929-000c29ad1d07";
	
	private String PREGNANCY_DUE_DATE = "e1dca8ee-1d5f-11e0-b929-000c29ad1d07";
	
	private String BREASTFEEDING = "bc4fe755-fc8f-49b8-9956-baf2477e8313";
	
	private String RETURN_VISIT_DATE = "e1dae630-1d5f-11e0-b929-000c29ad1d07";
	
	public Concept getYesConcept() {
		String uuid = Context.getAdministrationService().getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_YES_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	public Concept getstartDrugsConcept() {
		String uuid = Context.getAdministrationService().getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_START_DRUGS_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	public Concept gethistoricalDrugStartDateConcept() {
		String uuid = Context.getAdministrationService().getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_HISTORICAL_START_DATE_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	public Concept getPregnantConcept() {
		return getConcept(PREGNANT);
	}
	
	public Concept getGestationConcept() {
		return getConcept(GESTATION);
	}
	
	public Concept getNumberOfWeeksPregnant() {
		return getConcept(NUMBER_OF_WEEKS_PREGNANT);
	}
	
	public Concept getPregnancyDueDate() {
		return getConcept(PREGNANCY_DUE_DATE);
	}
	
	public Concept getBreastfeeding() {
		return getConcept(BREASTFEEDING);
	}
	
	public Concept getReturnVisitDateConcept() {
		return getConcept(RETURN_VISIT_DATE);
	}
	
}
