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
import org.openmrs.module.eptsreports.metadata.HivMetadata;
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
	private HivMetadata hivMetadata;
	
	@Autowired
	private SqlCohortQueries sqlCohortQueries;
	
	@Autowired
	private GenderCohortQueries genderCohortQueries;
	
	// Male and Female <1
	@DocumentedDefinition(value = "patientBelow1YearEnrolledInHIVStartedART")
	public CohortDefinition getPatientBelow1YearEnrolledInHIVStartedART(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition transferredFromOtherHealthFacility, CohortDefinition PatientBelow1Year) {
		CompositionCohortDefinition patientBelow1YearEnrolledInHIVStartedART = new CompositionCohortDefinition();
		patientBelow1YearEnrolledInHIVStartedART.setName("patientBelow1YearEnrolledInHIVStartedART");
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("location", "location", Location.class));
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("5", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("6", new Mapped<CohortDefinition>(PatientBelow1Year, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBelow1YearEnrolledInHIVStartedART.setCompositionString("((1 AND 2 AND 3 AND 4) AND (NOT 5)) AND 6");
		
		return patientBelow1YearEnrolledInHIVStartedART;
	}
	
	// Male and Female between 1 and 9 years
	@DocumentedDefinition(value = "patientBetween1And9YearsEnrolledInHIVStartedART")
	public CohortDefinition getPatientBetween1And9YearsEnrolledInHIVStartedART(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition transferredFromOtherHealthFacility, CohortDefinition PatientBetween1And9Years) {
		CompositionCohortDefinition patientBetween1And9YearsEnrolledInHIVStartedART = new CompositionCohortDefinition();
		patientBetween1And9YearsEnrolledInHIVStartedART.setName("patientBetween1And9YearsEnrolledInHIVStartedART");
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("location", "location", Location.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("5", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("6", new Mapped<CohortDefinition>(PatientBetween1And9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.setCompositionString("((1 AND 2 AND 3 AND 4) AND (NOT 5)) AND 6");
		return patientBetween1And9YearsEnrolledInHIVStartedART;
	}
	
	@DocumentedDefinition(value = "patientInYearRangeEnrolledInARTStarted")
	public CohortDefinition getPatientInYearRangeEnrolledInARTStarted(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition transferredFromOtherHealthFacility, CohortDefinition PatientBetween1And9Years, CohortDefinition ageCohort, CohortDefinition gender) {
		CompositionCohortDefinition patientInYearRangeEnrolledInARTStarted = new CompositionCohortDefinition();
		patientInYearRangeEnrolledInARTStarted.setName("patientInYearRangeEnrolledInHIVStarted");
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("location", "location", Location.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientInYearRangeEnrolledInARTStarted.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientInYearRangeEnrolledInARTStarted.getSearches().put("5", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientInYearRangeEnrolledInARTStarted.getSearches().put("6", new Mapped<CohortDefinition>(ageCohort, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("7", new Mapped<CohortDefinition>(gender, null));
		patientInYearRangeEnrolledInARTStarted.setCompositionString("((1 AND 2 AND 3 AND 4) AND (NOT 5)) AND 6 AND 7");
		return patientInYearRangeEnrolledInARTStarted;
	}
	
	@DocumentedDefinition(value = "patientEnrolledInART")
	public CohortDefinition getPatientEnrolledInART(CohortDefinition inARTProgramDuringTimePeriod, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition transferredFromOtherHealthFacility) {
		CompositionCohortDefinition patientEnrolledInART = new CompositionCohortDefinition();
		patientEnrolledInART.setName("patientEnrolledInART");
		patientEnrolledInART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientEnrolledInART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientEnrolledInART.addParameter(new Parameter("location", "location", Location.class));
		patientEnrolledInART.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientEnrolledInART.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientEnrolledInART.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientEnrolledInART.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		patientEnrolledInART.getSearches().put("5", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		patientEnrolledInART.setCompositionString("(1 AND 2 AND 3 AND 4) AND (NOT 5)");
		
		return patientEnrolledInART;
	}
	
	@DocumentedDefinition(value = "pregnantWomen")
	public CohortDefinition getPregnantWomen() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Facility", Location.class));
		
		int adultSegEnc = hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId();
		int adultInEnc = hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId();
		
		int ptvPro = hivMetadata.getPtvEtvProgram().getProgramId();
		
		int pregnant = hivMetadata.getPregnantConcept().getConceptId();
		int gestation = hivMetadata.getGestationConcept().getConceptId();
		int numOfWeeks = hivMetadata.getNumberOfWeeksPregnant().getConceptId();
		int dueDate = hivMetadata.getPregnancyDueDate().getConceptId();
		
		// set the mappings here
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		
		cd.addSearch("opt1", EptsReportUtils.map(commonCohortQueries.general("opt1", PregnantQueries.getPregnantOnInitialOrFollowUpConsulation(pregnant, gestation, adultInEnc, adultSegEnc)), mappings));
		cd.addSearch("opt2", EptsReportUtils.map(commonCohortQueries.general("opt2", PregnantQueries.getWeeksPregnantOnInitialOrFollowUpConsultations(numOfWeeks, adultInEnc, adultSegEnc)), mappings));
		cd.addSearch("opt3", EptsReportUtils.map(commonCohortQueries.general("opt3", PregnantQueries.getEnrolledInPtvOrEtv(ptvPro)), mappings));
		cd.addSearch("opt4", EptsReportUtils.map(commonCohortQueries.general("opt4", PregnantQueries.getPregnancyDueDateRegistred(dueDate, adultInEnc, adultSegEnc)), mappings));
		cd.setCompositionString("opt1 OR opt2 OR opt3 OR opt4");
		return cd;
	}
	
	/**
	 * Breast feeding women
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "breastfeedingWomen")
	public CohortDefinition getBreastfeedingWomen() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding or Paurpueras");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("dil", EptsReportUtils.map(sqlCohortQueries.dil(), "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("lactating", EptsReportUtils.map(sqlCohortQueries.registeredBreastFeeding(), "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("preg", EptsReportUtils.map(sqlCohortQueries.pregnantsInscribedOnARTService(), "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("(dil OR lactating) AND NOT preg");
		
		return cd;
	}
	
	/**
	 * Pregnant women with viral load in the last 12 months Denominator Denominator Denominator
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "pregnantWomenAndHasViralLoadInTheLast12MonthsDenominator")
	public CohortDefinition pregnantWomenAndHasViralLoadInTheLast12MonthsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantWomen(), mappings));
		cd.addSearch("vl", EptsReportUtils.map(getPatientsWithViralLoadResultsExcludingDeadLtfuTransferredoutStoppedArt(), mappings));
		cd.addSearch("female", EptsReportUtils.map(genderCohortQueries.FemaleCohort(), ""));
		cd.setCompositionString("(pregnant AND vl) AND female");
		return cd;
	}
	
	/**
	 * Pregnant women with viral load in the last 12 months Numerator with viral load of <1000 Numerator
	 * Numerator
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "pregnantWomenAndHasSuppressedViralLoadInTheLast12MonthsNumerator")
	public CohortDefinition pregnantWomenAndHasSuppressedViralLoadInTheLast12MonthsNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("preg", EptsReportUtils.map(getPregnantWomen(), mappings));
		cd.addSearch("supp", EptsReportUtils.map(getPatientsWithViralLoadSuppressionExcludingDeadLtfuTransferredoutStoppedArt(), mappings));
		cd.addSearch("female", EptsReportUtils.map(genderCohortQueries.FemaleCohort(), ""));
		cd.setCompositionString("(preg AND supp) AND female");
		return cd;
	}
	
	/**
	 * Breastfeeding women with viral load in the last 12 months Denominator Denominator
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "breastfeedingWomenAndHasViralLoadInTheLast12MonthsDenominator")
	public CohortDefinition breastfeedingWomenAndHasViralLoadInTheLast12MonthsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		
		// set the mappings here
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("breastfeeding", EptsReportUtils.map(getBreastfeedingWomen(), mappings));
		cd.addSearch("vl", EptsReportUtils.map(getPatientsWithViralLoadResultsExcludingDeadLtfuTransferredoutStoppedArt(), mappings));
		cd.addSearch("female", EptsReportUtils.map(genderCohortQueries.FemaleCohort(), ""));
		cd.setCompositionString("(breastfeeding AND vl) AND female");
		return cd;
	}
	
	/**
	 * Pregnant women with viral load in the last 12 months Numerator with viral load of <1000 Numerator
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "breastfeedingWomenAndHasViralLoadSuppressionInTheLast12MonthsNumerator")
	public CohortDefinition breastfeedingWomenAndHasViralLoadSuppressionInTheLast12MonthsNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("breastfeeding", EptsReportUtils.map(getBreastfeedingWomen(), mappings));
		cd.addSearch("suppression", EptsReportUtils.map(getPatientsWithViralLoadSuppressionExcludingDeadLtfuTransferredoutStoppedArt(), mappings));
		cd.addSearch("female", EptsReportUtils.map(genderCohortQueries.FemaleCohort(), ""));
		cd.setCompositionString("(breastfeeding AND suppression) AND female");
		return cd;
	}
	
	/**
	 * Patients with viral suppression of <1000 in the last 12 months excluding dead, LTFU, transferred
	 * out, stopped ART
	 */
	public CohortDefinition getPatientsWithViralLoadSuppressionExcludingDeadLtfuTransferredoutStoppedArt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("supp", EptsReportUtils.map(sqlCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(), mappings));
		cd.addSearch("dead", EptsReportUtils.map(sqlCohortQueries.getDeadPersons(), mappings));
		cd.setCompositionString("supp AND NOT dead");
		return cd;
	}
	
	/**
	 * Patients with viral results recorded in the last 12 months excluding dead, LTFU, transferred out,
	 * stopped ART
	 */
	public CohortDefinition getPatientsWithViralLoadResultsExcludingDeadLtfuTransferredoutStoppedArt() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("results", EptsReportUtils.map(sqlCohortQueries.getPatientsViralLoadWithin12Months(), mappings));
		cd.addSearch("dead", EptsReportUtils.map(sqlCohortQueries.getDeadPersons(), mappings));
		cd.setCompositionString("results AND NOT dead");
		return cd;
	}
	
	// Starting TX_CURR
	
	@DocumentedDefinition(value = "patientBellowOneYearCurrentlyInARTStarted")
	public CompositionCohortDefinition getPatientBellowOneYearCurrentlyInARTStarted(CohortDefinition inARTProgramAtEndDate, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate, CohortDefinition patientsWhoHaveNotReturned, CohortDefinition patientsWhoHaveNotCompleted60Days, CohortDefinition abandonedButHaveNotcompleted60Days, CohortDefinition ageCohort) {
		
		CompositionCohortDefinition patientBellowOneYearCurrentlyInARTStarted = new CompositionCohortDefinition();
		patientBellowOneYearCurrentlyInARTStarted.setName("patientBellowOneYearCurrentlyInARTStarted");
		patientBellowOneYearCurrentlyInARTStarted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBellowOneYearCurrentlyInARTStarted.addParameter(new Parameter("location", "location", Location.class));
		patientBellowOneYearCurrentlyInARTStarted.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramAtEndDate, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("5", new Mapped<CohortDefinition>(patientsWhoLeftARTProgramBeforeOrOnEndDate, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("6", new Mapped<CohortDefinition>(patientsWhoHaveNotReturned, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("7", new Mapped<CohortDefinition>(patientsWhoHaveNotCompleted60Days, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("8", new Mapped<CohortDefinition>(abandonedButHaveNotcompleted60Days, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		
		// TODO -Check specifications document on how to use query 6,7 and 8
		patientBellowOneYearCurrentlyInARTStarted.getSearches().put("9", new Mapped<CohortDefinition>(ageCohort, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBellowOneYearCurrentlyInARTStarted.setCompositionString("((1 AND 2 AND 3 AND 4) AND (NOT 5) AND NOT ( 6 AND NOT (5 OR 7 OR 8))) AND 9");
		return patientBellowOneYearCurrentlyInARTStarted;
	}
	
	@DocumentedDefinition(value = "patientBetween1And9YearsCurrently")
	public CompositionCohortDefinition getPatientBetween1And9YearsCurrently(CohortDefinition inARTProgramAtendDate, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate, CohortDefinition patientsWhoHaveNotReturned, CohortDefinition patientsWhoHaveNotCompleted60Days, CohortDefinition abandonedButHaveNotcompleted60Days, CohortDefinition ageCohort) {
		
		CompositionCohortDefinition patientBetween1And9YearsCurrently = new CompositionCohortDefinition();
		patientBetween1And9YearsCurrently.setName("patientBetween1And9YearsCurrently");
		patientBetween1And9YearsCurrently.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBetween1And9YearsCurrently.addParameter(new Parameter("location", "location", Location.class));
		patientBetween1And9YearsCurrently.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBetween1And9YearsCurrently.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramAtendDate, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("5", new Mapped<CohortDefinition>(patientsWhoLeftARTProgramBeforeOrOnEndDate, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("6", new Mapped<CohortDefinition>(patientsWhoHaveNotReturned, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("7", new Mapped<CohortDefinition>(patientsWhoHaveNotCompleted60Days, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientBetween1And9YearsCurrently.getSearches().put("8", new Mapped<CohortDefinition>(abandonedButHaveNotcompleted60Days, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		
		// TODO -Check specifications document on how to use query 6,7 and 8
		patientBetween1And9YearsCurrently.getSearches().put("9", new Mapped<CohortDefinition>(ageCohort, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBetween1And9YearsCurrently.setCompositionString("((1 AND 2 AND 3 AND 4) AND (NOT 5) AND NOT (6 AND NOT (5 OR 7 OR 8))) AND 9");
		return patientBetween1And9YearsCurrently;
	}
	
	@DocumentedDefinition(value = "patientInYearRangeEnrolledInARTStarted")
	public CompositionCohortDefinition getPatientInYearRangeCurrentlyInARTStarted(CohortDefinition inARTProgramAtEndDate, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate, CohortDefinition patientsWhoHaveNotReturned, CohortDefinition patientsWhoHaveNotCompleted60Days, CohortDefinition abandonedButHaveNotcompleted60Days, CohortDefinition PatientBetween1And9Years, CohortDefinition ageCohort, CohortDefinition gender) {
		
		CompositionCohortDefinition patientInYearRangeEnrolledInARTStarted = new CompositionCohortDefinition();
		patientInYearRangeEnrolledInARTStarted.setName("patientInYearRangeEnrolledInHIVStarted");
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("location", "location", Location.class));
		patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramAtEndDate, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("5", new Mapped<CohortDefinition>(patientsWhoLeftARTProgramBeforeOrOnEndDate, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("6", new Mapped<CohortDefinition>(patientsWhoHaveNotReturned, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("7", new Mapped<CohortDefinition>(patientsWhoHaveNotCompleted60Days, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("8", new Mapped<CohortDefinition>(abandonedButHaveNotcompleted60Days, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		
		// TODO -Check specifications document on how to use query 6,7 and 8
		patientInYearRangeEnrolledInARTStarted.getSearches().put("9", new Mapped<CohortDefinition>(ageCohort, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientInYearRangeEnrolledInARTStarted.getSearches().put("10", new Mapped<CohortDefinition>(gender, null));
		patientInYearRangeEnrolledInARTStarted.setCompositionString("((1 AND 2 AND 3 AND 4) AND (NOT 5) AND NOT (6 AND NOT (5 OR 7 OR 8))) AND 9 AND 10");
		return patientInYearRangeEnrolledInARTStarted;
	}
	
	@DocumentedDefinition(value = "patientsCurrentlyInARTStarted")
	public CompositionCohortDefinition getAllPatientsCurrentlyInARTStarted(CohortDefinition inARTProgramAtEndDate, CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs, CohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate, CohortDefinition patientsWhoHaveNotReturned, CohortDefinition patientsWhoHaveNotCompleted60Days, CohortDefinition abandonedButHaveNotcompleted60Days) {
		
		CompositionCohortDefinition patientsCurrentlyInARTStarted = new CompositionCohortDefinition();
		patientsCurrentlyInARTStarted.setName("patientInYearRangeEnrolledInHIVStarted");
		patientsCurrentlyInARTStarted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsCurrentlyInARTStarted.addParameter(new Parameter("location", "location", Location.class));
		patientsCurrentlyInARTStarted.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsCurrentlyInARTStarted.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramAtEndDate, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("5", new Mapped<CohortDefinition>(patientsWhoLeftARTProgramBeforeOrOnEndDate, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("6", new Mapped<CohortDefinition>(patientsWhoHaveNotReturned, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("7", new Mapped<CohortDefinition>(patientsWhoHaveNotCompleted60Days, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		patientsCurrentlyInARTStarted.getSearches().put("8", new Mapped<CohortDefinition>(abandonedButHaveNotcompleted60Days, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		
		// TODO -Check specifications document on how to use query 6,7 and 8
		patientsCurrentlyInARTStarted.setCompositionString("(1 AND 2 AND 3 AND 4) AND (NOT 5) AND NOT (6 AND NOT (5 OR 7 OR 8))");
		return patientsCurrentlyInARTStarted;
	}
}
