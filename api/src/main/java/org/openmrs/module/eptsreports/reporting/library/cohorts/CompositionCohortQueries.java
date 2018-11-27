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

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.springframework.stereotype.Component;

/**
 * Defines all of the CompositionCohortDefinition instances we want to expose for EPTS
 */
@Component
public class CompositionCohortQueries {
	
	// Male and Female <1
	@DocumentedDefinition(value = "patientBelow1YearEnrolledInHIVStartedART")
	public CompositionCohortDefinition getPatientBelow1YearEnrolledInHIVStartedART(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition transferredFromOtherHealthFacility, CohortDefinition PatientBelow1Year) {
		CompositionCohortDefinition patientBelow1YearEnrolledInHIVStartedART = new CompositionCohortDefinition();
		patientBelow1YearEnrolledInHIVStartedART.setName("patientBelow1YearEnrolledInHIVStartedART");
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("location", "location", Location.class));
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("4", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("5", new Mapped<CohortDefinition>(PatientBelow1Year, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBelow1YearEnrolledInHIVStartedART.setCompositionString("((1 or 2 or 3) and (not 4)) and 5");
		
		return patientBelow1YearEnrolledInHIVStartedART;
	}
	
	// Male and Female between 1 and 9 years
	@DocumentedDefinition(value = "patientBetween1And9YearsEnrolledInHIVStartedART")
	public CompositionCohortDefinition getPatientBetween1And9YearsEnrolledInHIVStartedART(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition transferredFromOtherHealthFacility, CohortDefinition PatientBetween1And9Years) {
		
		CompositionCohortDefinition patientBetween1And9YearsEnrolledInHIVStartedART = new CompositionCohortDefinition();
		patientBetween1And9YearsEnrolledInHIVStartedART.setName("patientBetween1And9YearsEnrolledInHIVStartedART");
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("location", "location", Location.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("4", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("5", new Mapped<CohortDefinition>(PatientBetween1And9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.setCompositionString("((1 or 2 or 3) and (not 4)) and 5");
		return patientBetween1And9YearsEnrolledInHIVStartedART;
	}
	
	@DocumentedDefinition(value = "patientInYearRangeEnrolledInARTStarted")
	public CompositionCohortDefinition getPatientInYearRangeEnrolledInARTStarted(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition transferredFromOtherHealthFacility, CohortDefinition PatientBetween1And9Years, CohortDefinition ageCohort, CohortDefinition gender) {
		
		CompositionCohortDefinition patientInYearRangeEnrolledInARTStarted = new CompositionCohortDefinition();
		patientInYearRangeEnrolledInARTStarted.setName("patientInYearRangeEnrolledInHIVStarted");
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("location", "location", Location.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("4", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientInYearRangeEnrolledInARTStarted.getSearches().put("5", new Mapped<CohortDefinition>(ageCohort, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("6", new Mapped<CohortDefinition>(gender, null));
		patientInYearRangeEnrolledInARTStarted.setCompositionString("((1 or 2 or 3) and (not 4)) and 5 and 6");
		return patientInYearRangeEnrolledInARTStarted;
	}
	
	@DocumentedDefinition(value = "patientEnrolledInART")
	public CompositionCohortDefinition getPatientEnrolledInART(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition transferredFromOtherHealthFacility) {
		
		CompositionCohortDefinition patientEnrolledInART = new CompositionCohortDefinition();
		patientEnrolledInART.setName("patientEnrolledInART");
		patientEnrolledInART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientEnrolledInART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientEnrolledInART.addParameter(new Parameter("location", "location", Location.class));
		patientEnrolledInART.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientEnrolledInART.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientEnrolledInART.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientEnrolledInART.getSearches().put("4", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientEnrolledInART.setCompositionString("(1 or 2 or 3) and (not 4)");
		
		return patientEnrolledInART;
	}
	
	// Starting TX_CURR
	
	@DocumentedDefinition(value = "patientBellowOneYearCurrentlyInARTStarted")
	public CompositionCohortDefinition getPatientBellowOneYearCurrentlyInARTStarted(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, EncounterCohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate, CohortDefinition patientsWhoHaveNotReturned, CohortDefinition patientsWhoHaveNotCompleted60Days, CohortDefinition abandonedButHaveNotcompleted60Days, CohortDefinition ageCohort) {
		
		CompositionCohortDefinition patientBellowOneYearCurrentlyInARTStarted = new CompositionCohortDefinition();
		patientBellowOneYearCurrentlyInARTStarted.setName("patientBellowOneYearCurrentlyInARTStarted");
		patientBellowOneYearCurrentlyInARTStarted.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientBellowOneYearCurrentlyInARTStarted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBellowOneYearCurrentlyInARTStarted.addParameter(new Parameter("location", "location", Location.class));
		patientBellowOneYearCurrentlyInARTStarted.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("5", new Mapped<CohortDefinition>(patientsWhoLeftARTProgramBeforeOrOnEndDate, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("6", new Mapped<CohortDefinition>(patientsWhoHaveNotReturned, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("7", new Mapped<CohortDefinition>(patientsWhoHaveNotCompleted60Days, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("8", new Mapped<CohortDefinition>(abandonedButHaveNotcompleted60Days, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		// TODO -Check specifications document on how to use query 6,7 and 8
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("9", new Mapped<CohortDefinition>(ageCohort, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBellowOneYearCurrentlyInARTStarted.setCompositionString("((1 or 2 or 3 or 4) and (not 5) and not ( 6 and not (5 or 7 or 8))) and 9");
		return patientBellowOneYearCurrentlyInARTStarted;
	}
	
	@DocumentedDefinition(value = "patientBetween1And9YearsCurrently")
	public CompositionCohortDefinition getPatientBetween1And9YearsCurrently(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, EncounterCohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate, CohortDefinition patientsWhoHaveNotReturned, CohortDefinition patientsWhoHaveNotCompleted60Days, CohortDefinition abandonedButHaveNotcompleted60Days, CohortDefinition ageCohort) {
		
		CompositionCohortDefinition patientBetween1And9YearsCurrently = new CompositionCohortDefinition();
		patientBetween1And9YearsCurrently.setName("patientBetween1And9YearsCurrently");
		patientBetween1And9YearsCurrently.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientBetween1And9YearsCurrently.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBetween1And9YearsCurrently.addParameter(new Parameter("location", "location", Location.class));
		patientBetween1And9YearsCurrently.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBetween1And9YearsCurrently.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientBetween1And9YearsCurrently.getSearches().put("5", new Mapped<CohortDefinition>(patientsWhoLeftARTProgramBeforeOrOnEndDate, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("6", new Mapped<CohortDefinition>(patientsWhoHaveNotReturned, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("7", new Mapped<CohortDefinition>(patientsWhoHaveNotCompleted60Days, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("8", new Mapped<CohortDefinition>(abandonedButHaveNotcompleted60Days, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		// TODO -Check specifications document on how to use query 6,7 and 8
		patientBetween1And9YearsCurrently.getSearches().put("9", new Mapped<CohortDefinition>(ageCohort, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBetween1And9YearsCurrently.setCompositionString("((1 or 2 or 3 or 4) and (not 5) and not (6 and not (5 or 7 or 8))) and 9");
		return patientBetween1And9YearsCurrently;
	}
	
	@DocumentedDefinition(value = "patientInYearRangeEnrolledInARTStarted")
	public CompositionCohortDefinition getPatientInYearRangeCurrentlyInARTStarted(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, EncounterCohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate, CohortDefinition patientsWhoHaveNotReturned, CohortDefinition patientsWhoHaveNotCompleted60Days, CohortDefinition abandonedButHaveNotcompleted60Days, CohortDefinition PatientBetween1And9Years, CohortDefinition ageCohort, CohortDefinition gender) {
		
		CompositionCohortDefinition patientInYearRangeEnrolledInARTStarted = new CompositionCohortDefinition();
		patientInYearRangeEnrolledInARTStarted.setName("patientInYearRangeEnrolledInHIVStarted");
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("location", "location", Location.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("5", new Mapped<CohortDefinition>(patientsWhoLeftARTProgramBeforeOrOnEndDate, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("6", new Mapped<CohortDefinition>(patientsWhoHaveNotReturned, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("7", new Mapped<CohortDefinition>(patientsWhoHaveNotCompleted60Days, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("8", new Mapped<CohortDefinition>(abandonedButHaveNotcompleted60Days, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		// TODO -Check specifications document on how to use query 6,7 and 8
		patientInYearRangeEnrolledInARTStarted.getSearches().put("9", new Mapped<CohortDefinition>(ageCohort, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("10", new Mapped<CohortDefinition>(gender, null));
		patientInYearRangeEnrolledInARTStarted.setCompositionString("((1 or 2 or 3 or 4) and (not 5) and not (6 and not (5 or 7 or 8))) and 9 and 10");
		return patientInYearRangeEnrolledInARTStarted;
	}
	
	@DocumentedDefinition(value = "patientsCurrentlyInARTStarted")
	public CompositionCohortDefinition getAllPatientsCurrentlyInARTStarted(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, EncounterCohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate, CohortDefinition patientsWhoHaveNotReturned, CohortDefinition patientsWhoHaveNotCompleted60Days, CohortDefinition abandonedButHaveNotcompleted60Days) {
		
		CompositionCohortDefinition patientsCurrentlyInARTStarted = new CompositionCohortDefinition();
		patientsCurrentlyInARTStarted.setName("patientInYearRangeEnrolledInHIVStarted");
		patientsCurrentlyInARTStarted.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsCurrentlyInARTStarted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsCurrentlyInARTStarted.addParameter(new Parameter("location", "location", Location.class));
		patientsCurrentlyInARTStarted.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsCurrentlyInARTStarted.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientsCurrentlyInARTStarted.getSearches().put("5", new Mapped<CohortDefinition>(patientsWhoLeftARTProgramBeforeOrOnEndDate, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("6", new Mapped<CohortDefinition>(patientsWhoHaveNotReturned, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("7", new Mapped<CohortDefinition>(patientsWhoHaveNotCompleted60Days, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("8", new Mapped<CohortDefinition>(abandonedButHaveNotcompleted60Days, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		// TODO -Check specifications document on how to use query 6,7 and 8
		patientsCurrentlyInARTStarted.setCompositionString("(1 or 2 or 3 or 4) and (not 5) and not (6 and not (5 or 7 or 8))");
		return patientsCurrentlyInARTStarted;
	}
}
