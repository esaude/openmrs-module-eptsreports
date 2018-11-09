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
import org.openmrs.module.eptsreports.reporting.library.queries.PregnantQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines all of the CompositionCohortDefinition instances we want to expose for EPTS
 */
@Component
public class CompositionCohortQueries {
	
	@Autowired
	private CommonCohortQueries commonCohortQueries;
	
	@Autowired
	private PregnantQueries pregnantQueries;
	
	// Male and Female <1
	@DocumentedDefinition(value = "patientBelow1YearEnrolledInHIVStartedART")
	public CompositionCohortDefinition getPatientBelow1YearEnrolledInHIVStartedART(
	        CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs,
	        CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition transferredFromOtherHealthFacility,
	        CohortDefinition PatientBelow1Year) {
		CompositionCohortDefinition patientBelow1YearEnrolledInHIVStartedART = new CompositionCohortDefinition();
		patientBelow1YearEnrolledInHIVStartedART.setName("patientBelow1YearEnrolledInHIVStartedART");
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("1",
		    new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("2",
		    new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("3",
		    new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("4",
		    new Mapped<CohortDefinition>(transferredFromOtherHealthFacility,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("5", new Mapped<CohortDefinition>(PatientBelow1Year,
		        ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBelow1YearEnrolledInHIVStartedART.setCompositionString("((1 or 2 or 3) and (not 4)) and 5");
		
		return patientBelow1YearEnrolledInHIVStartedART;
	}
	
	// Male and Female between 1 and 9 years
	@DocumentedDefinition(value = "patientBetween1And9YearsEnrolledInHIVStartedART")
	public CompositionCohortDefinition getPatientBetween1And9YearsEnrolledInHIVStartedART(
	        CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs,
	        CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition transferredFromOtherHealthFacility,
	        CohortDefinition PatientBetween1And9Years) {
		
		CompositionCohortDefinition patientBetween1And9YearsEnrolledInHIVStartedART = new CompositionCohortDefinition();
		patientBetween1And9YearsEnrolledInHIVStartedART.setName("patientBetween1And9YearsEnrolledInHIVStartedART");
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART
		        .addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("1",
		    new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("2",
		    new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("3",
		    new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("4",
		    new Mapped<CohortDefinition>(transferredFromOtherHealthFacility,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("5", new Mapped<CohortDefinition>(
		        PatientBetween1And9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.setCompositionString("((1 or 2 or 3) and (not 4)) and 5");
		return patientBetween1And9YearsEnrolledInHIVStartedART;
	}
	
	@DocumentedDefinition(value = "patientInYearRangeEnrolledInARTStarted")
	public CompositionCohortDefinition getPatientInYearRangeEnrolledInARTStarted(
	        CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs,
	        CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition transferredFromOtherHealthFacility,
	        CohortDefinition PatientBetween1And9Years, CohortDefinition ageCohort, CohortDefinition gender) {
		
		CompositionCohortDefinition patientInYearRangeEnrolledInARTStarted = new CompositionCohortDefinition();
		patientInYearRangeEnrolledInARTStarted.setName("patientInYearRangeEnrolledInHIVStarted");
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("1",
		    new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs,
		        ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("3",
		    new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("4",
		    new Mapped<CohortDefinition>(transferredFromOtherHealthFacility,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		
		patientInYearRangeEnrolledInARTStarted.getSearches().put("5", new Mapped<CohortDefinition>(ageCohort,
		        ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("6", new Mapped<CohortDefinition>(gender, null));
		patientInYearRangeEnrolledInARTStarted.setCompositionString("((1 or 2 or 3) and (not 4)) and 5 and 6");
		return patientInYearRangeEnrolledInARTStarted;
	}
	
	@DocumentedDefinition(value = "patientEnrolledInART")
	public CompositionCohortDefinition getPatientEnrolledInART(CohortDefinition inARTProgramDuringTimePeriod,
	        CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs,
	        CohortDefinition transferredFromOtherHealthFacility) {
		
		CompositionCohortDefinition patientEnrolledInART = new CompositionCohortDefinition();
		patientEnrolledInART.setName("patientEnrolledInART");
		patientEnrolledInART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientEnrolledInART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientEnrolledInART.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod,
		        ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientEnrolledInART.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs,
		        ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientEnrolledInART.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs,
		        ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientEnrolledInART.getSearches().put("4", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility,
		        ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientEnrolledInART.setCompositionString("(1 or 2 or 3) and (not 4)");
		
		return patientEnrolledInART;
	}
	
	@DocumentedDefinition(value = "pregnantWomen")
	public CohortDefinition getPregnantWomen() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Facility", Location.class));
		
		// set the mappings here
		String mappings = "startDate=${startDate},endDate=${endDate},location={location}";
		
		cd.addSearch("opt1", EptsReportUtils.map(
		    commonCohortQueries.general("opt1", pregnantQueries.getPregnantOnInitialOrFollowUpConsultation()), mappings));
		cd.addSearch("opt2",
		    EptsReportUtils.map(
		        commonCohortQueries.general("opt2", pregnantQueries.getWeeksPregnantOnInitialOrFollowUpConsultation()),
		        mappings));
		cd.addSearch("opt3",
		    EptsReportUtils.map(commonCohortQueries.general("opt3", pregnantQueries.getEnrolledInPTVorETV()), mappings));
		cd.addSearch("opt4", EptsReportUtils
		        .map(commonCohortQueries.general("opt4", pregnantQueries.getPregnacyDueDateRegistered()), mappings));
		cd.setCompositionString("opt1 OR opt2 OR opt3 OR opt4");
		return cd;
	}
}
