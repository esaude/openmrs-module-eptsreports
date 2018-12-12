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
package org.openmrs.module.eptsreports.reporting.library.indicators;

import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxPvlsCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HivIndicators extends BaseIndicators {
	
	@Autowired
	private TxPvlsCohortQueries pvls;
	
	@Autowired
	private TxNewCohortQueries txNew;
	
	public CohortIndicator patientBelow1YearEnrolledInHIVStartedARTIndicator(CohortDefinition cohortDefinition) {
		return newCohortIndicator("patientBelow1YearEnrolledInHIVStartedARTIndicator", EptsReportUtils.map(cohortDefinition,
		    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location},effectiveDate=${endDate}"));
	}
	
	public CohortIndicator patientBetween1And9YearsEnrolledInHIVStartedARTIndicator(CohortDefinition cohortDefinition) {
		return newCohortIndicator("patientBelow1YearEnrolledInHIVStartedARTIndicator", EptsReportUtils.map(cohortDefinition,
		    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location},effectiveDate=${endDate}"));
	}
	
	public CohortIndicator patientInYearRangeEnrolledInHIVStartedARTIndicator(CohortDefinition cohortDefinition) {
		return newCohortIndicator("patientInYearRangeEnrolledInHIVStartedARTIndicator", EptsReportUtils.map(cohortDefinition,
		    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location},effectiveDate=${endDate}"));
	}
	
	public CohortIndicator patientEnrolledInHIVStartedARTIndicator(CohortDefinition cohortDefinition) {
		return newCohortIndicator("patientNewlyEnrolledInHIVIndicator",
		    EptsReportUtils.map(cohortDefinition, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
	}
	
	public CohortIndicator patientBelow1YearEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(CohortDefinition cohortDefinition) {
		return newCohortIndicator("patientBelow1YearEnrolledInHIVStartedARTIndicator",
		    EptsReportUtils.map(cohortDefinition, "onOrBefore=${endDate},location=${location},effectiveDate=${endDate}"));
	}
	
	public CohortIndicator patientBetween1And9YearsEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(
	        CohortDefinition cohortDefinition) {
		return newCohortIndicator("patientBelow1YearEnrolledInHIVStartedARTIndicator",
		    EptsReportUtils.map(cohortDefinition, "onOrBefore=${endDate},location=${location},effectiveDate=${endDate}"));
	}
	
	public CohortIndicator patientInYearRangeEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(CohortDefinition cohortDefinition) {
		return newCohortIndicator("patientInYearRangeEnrolledInHIVStartedARTIndicator",
		    EptsReportUtils.map(cohortDefinition, "onOrBefore=${endDate},location=${location},effectiveDate=${endDate}"));
	}
	
	public CohortIndicator patientEnrolledInHIVStartedARTIndicatorBeforeOrOnEndDate(CohortDefinition cohortDefinition) {
		return newCohortIndicator("patientNewlyEnrolledInHIVIndicator",
		    EptsReportUtils.map(cohortDefinition, "onOrBefore=${endDate},location=${location}"));
	}
	
	// add viral load indicators
	/**
	 * Find patients with VL <1000 - viral suppression
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator patientsWithViralLoadSuppression() {
		return newCohortIndicator("suppressed viral load", EptsReportUtils.map(pvls.getPatientsWithViralLoadSuppression(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
	
	/**
	 * Find patients with viral load between dates
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator patientsWithViralLoadBetweenDates() {
		return newCohortIndicator("patients with viral load", EptsReportUtils.map(pvls.getPatientsWithViralLoadResults(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
	
	/**
	 * Find patients with Viral load results disagregated by age
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator getPatientsWithViralLoadResultsWithinAgeBracket(int min, int max) {
		return newCohortIndicator("patients with viral load results disagreagated by age",
		    EptsReportUtils.map(pvls.getPatientsWithViralLoadResultsWithinAgeBracket(min, max),
		        "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
	
	/**
	 * Find patients with Viral load results disagregated by age
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator getPatientsWithViralLoadSuppressionWithinAgeBracket(int min, int max) {
		return newCohortIndicator("patients with viral load results disagreagated by age",
		    EptsReportUtils.map(pvls.getPatientsWithViralLoadSuppressionWithinAgeBracket(min, max),
		        "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
}
