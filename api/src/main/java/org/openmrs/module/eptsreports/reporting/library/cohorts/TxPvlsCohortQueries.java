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
	private GenericCohortQueries genericCohortQueries;
	
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
		    EptsReportUtils.map(genericCohortQueries.hasCodedObs(hivMetadata.getBreastfeeding(), hivMetadata.getYesConcept()),
		        "onOrAfter=${startDate},onOrBefore=${endDate}"));
		cd.setCompositionString("hasEncounter AND hasObs");
		
		return cd;
	}
	
	/**
	 *
	 */
	@DocumentedDefinition(value = "dil")
	public CohortDefinition dil() {
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
	 * Get deceased persons in the database
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getDeadPersons() {
		return genericCohortQueries.general("Dead persons", "SELECT person_id FROM person WHERE dead=1");
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
		cd.setQuery(PregnantQueries.getPregnantWhileOnArt(hivMetadata.getPregnantConcept().getConceptId(),
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
		cd.addSearch("hasObs",
		    EptsReportUtils.map(genericCohortQueries.hasCodedObs(hivMetadata.getCriteriaForArtStart(), hivMetadata.getBreastfeeding()),
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
		cd.setQuestion(hivMetadata.getDateOfDelivery());
		cd.setEncounterTypeList(
		    Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType(), hivMetadata.getARVAdultInitialEncounterType()));
		return cd;
	}
}
