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

import java.util.Arrays;
import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.BreastfeedingQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.PregnantQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines all of the TxNew Cohort Definition instances we want to expose for EPTS
 */
@Component
public class TxPvlsCohortQueries {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private CommonMetadata commonMetadata;
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	@Autowired
	private HivCohortQueries hivCohortQueries;
	
	/**
	 * Mothers who have registered observation as breastfeeding
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "registeredBreastFeeding")
	public CohortDefinition registeredBreastFeeding() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Registered Breastfeeding");
		cd.setDescription("Patient with breastfeeding obs collected from encounters");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("hasEncounter",
		    EptsReportUtils.map(genericCohortQueries.hasEncounter(hivMetadata.getAdultoSeguimentoEncounterType()),
		        "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
		cd.addSearch("hasObs",
		    EptsReportUtils.map(
		        genericCohortQueries.hasCodedObs(hivMetadata.getBreastfeeding(), Arrays.asList(commonMetadata.getYesConcept())),
		        "onOrAfter=${startDate},onOrBefore=${endDate}"));
		cd.setCompositionString("hasEncounter AND hasObs");
		
		return cd;
	}
	
	/**
	 * DATAPARTO OR INICIOLACTANTE OR LACTANTEPROGRAMA
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "dil")
	public CohortDefinition getWomenWithDeliveryDateOnArtAndStartedArtWhileBreastfeedingAndGaveBirthTwoYearsAgo() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setDescription("dil");// comprise of DATAPARTO OR INICIOLACTANTE OR LACTANTEPROGRAMA
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("DATAPARTO", EptsReportUtils.map(patientsWithDateOfBirthUpdatedOnARTService(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("INICIOLACTANTE",
		    EptsReportUtils.map(aRTStartForBeingBreastfeeding(), "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("LACTANTEPROGRAMA",
		    EptsReportUtils.map(patientsWhoGaveBirthTwoYearsAgo(), "startDate=${startDate},location=${location}"));
		
		cd.setCompositionString("DATAPARTO OR INICIOLACTANTE OR LACTANTEPROGRAMA");
		return cd;
		
	}

	/**
	 * Get pregnant women inscribed in ART service
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition pregnantsInscribedOnARTService() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("pregnantsInscribedOnARTService");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuery(PregnantQueries.getPregnantWhileOnArt(commonMetadata.getPregnantConcept().getConceptId(),
		    hivMetadata.getGestationConcept().getConceptId(), hivMetadata.getNumberOfWeeksPregnant().getConceptId(),
		    hivMetadata.getPregnancyDueDate().getConceptId(), hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
		    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(), hivMetadata.getPtvEtvProgram().getProgramId()));
		
		return cd;
	}
	
	/**
	 * Women who gave birth 2 years ago
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition patientsWhoGaveBirthTwoYearsAgo() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsWhoGaveBirthTwoYearsAgo");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuery(BreastfeedingQueries.getPatientsWhoGaveBirthTwoYearsAgo(hivMetadata.getPtvEtvProgram().getProgramId(), 27));
		
		return cd;
	}
	
	/**
	 * Women who are breastfeeding and have started ART
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "aRTStartForBeingBreastfeeding")
	public CohortDefinition aRTStartForBeingBreastfeeding() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		
		cd.setName("aRTStartForBeingBreastfeeding");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("hasEncounter",
		    EptsReportUtils.map(genericCohortQueries.hasEncounter(hivMetadata.getAdultoSeguimentoEncounterType()),
		        "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
		cd.addSearch("hasObs", EptsReportUtils.map(
		    genericCohortQueries.hasCodedObs(hivMetadata.getCriteriaForArtStart(), Arrays.asList(commonMetadata.getBreastfeeding())),
		    "onOrAfter=${startDate},onOrBefore=${endDate}"));
		cd.setCompositionString("hasObs AND hasEncounter");
		
		return cd;
	}
	
	/**
	 * Patients with the date of birth updated on ART service
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "patientsWithDateOfBirthUpdatedOnARTService")
	public CohortDefinition patientsWithDateOfBirthUpdatedOnARTService() {
		DateObsCohortDefinition cd = new DateObsCohortDefinition();
		cd.setName("patientsWithDateOfBirthUpdatedOnARTService");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuestion(commonMetadata.getPriorDeliveryDateConcept());
		cd.setEncounterTypeList(
		    Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType(), hivMetadata.getARVAdultInitialEncounterType()));
		return cd;
	}
	
	// Start TxPvls
	@DocumentedDefinition(value = "pregnantWomen")
	public CohortDefinition getPregnantWomen() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Facility", Location.class));
		
		int adultSegEnc = hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId();
		int adultInEnc = hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId();
		
		int ptvPro = hivMetadata.getPtvEtvProgram().getProgramId();
		
		int pregnant = commonMetadata.getPregnantConcept().getConceptId();
		int gestation = hivMetadata.getGestationConcept().getConceptId();
		int numOfWeeks = hivMetadata.getNumberOfWeeksPregnant().getConceptId();
		int dueDate = hivMetadata.getPregnancyDueDate().getConceptId();
		
		// set the mappings here
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		
		cd.addSearch("opt1",
		    EptsReportUtils.map(
		        genericCohortQueries.generalSql("opt1",
		            PregnantQueries.getPregnantOnInitialOrFollowUpConsulation(pregnant, gestation, adultInEnc, adultSegEnc)),
		        mappings));
		cd.addSearch("opt2", EptsReportUtils.map(genericCohortQueries.generalSql("opt2",
		    PregnantQueries.getWeeksPregnantOnInitialOrFollowUpConsultations(numOfWeeks, adultInEnc, adultSegEnc)), mappings));
		cd.addSearch("opt3",
		    EptsReportUtils.map(genericCohortQueries.generalSql("opt3", PregnantQueries.getEnrolledInPtvOrEtv(ptvPro)), mappings));
		cd.addSearch("opt4", EptsReportUtils.map(
		    genericCohortQueries.generalSql("opt4", PregnantQueries.getPregnancyDueDateRegistred(dueDate, adultInEnc, adultSegEnc)),
		    mappings));
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
		cd.addSearch("dil", EptsReportUtils.map(getWomenWithDeliveryDateOnArtAndStartedArtWhileBreastfeedingAndGaveBirthTwoYearsAgo(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("lactating",
		    EptsReportUtils.map(registeredBreastFeeding(), "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("preg",
		    EptsReportUtils.map(pregnantsInscribedOnARTService(), "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("(dil OR lactating) AND NOT preg");
		
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
		cd.addSearch("supp", EptsReportUtils.map(hivCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(), mappings));
		cd.addSearch("baseCohort", EptsReportUtils.map(genericCohortQueries.getBaseCohort(), mappings));
		cd.setCompositionString("supp AND baseCohort");
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
		cd.addSearch("results", EptsReportUtils.map(hivCohortQueries.getPatientsViralLoadWithin12Months(), mappings));
		cd.addSearch("baseCohort", EptsReportUtils.map(genericCohortQueries.getBaseCohort(), mappings));
		cd.setCompositionString("results AND baseCohort");
		return cd;
	}
}
