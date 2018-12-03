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
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
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
	private TxPvlsCohortQueries txPvlsCohortQueries;
	
	@Autowired
	private HivCohortQueries hivCohortQueries;
	
	@Autowired
	private GenderCohortQueries genderCohortQueries;
	
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
		
		int pregnant = hivMetadata.getPregnantConcept().getConceptId();
		int gestation = hivMetadata.getGestationConcept().getConceptId();
		int numOfWeeks = hivMetadata.getNumberOfWeeksPregnant().getConceptId();
		int dueDate = hivMetadata.getPregnancyDueDate().getConceptId();
		
		// set the mappings here
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		
		cd.addSearch("opt1",
		    EptsReportUtils.map(
		        commonCohortQueries.general("opt1",
		            PregnantQueries.getPregnantOnInitialOrFollowUpConsulation(pregnant, gestation, adultInEnc, adultSegEnc)),
		        mappings));
		cd.addSearch("opt2", EptsReportUtils.map(commonCohortQueries.general("opt2",
		    PregnantQueries.getWeeksPregnantOnInitialOrFollowUpConsultations(numOfWeeks, adultInEnc, adultSegEnc)), mappings));
		cd.addSearch("opt3",
		    EptsReportUtils.map(commonCohortQueries.general("opt3", PregnantQueries.getEnrolledInPtvOrEtv(ptvPro)), mappings));
		cd.addSearch("opt4",
		    EptsReportUtils.map(
		        commonCohortQueries.general("opt4", PregnantQueries.getPregnancyDueDateRegistred(dueDate, adultInEnc, adultSegEnc)),
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
		cd.addSearch("dil",
		    EptsReportUtils.map(txPvlsCohortQueries.dil(), "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("lactating", EptsReportUtils.map(txPvlsCohortQueries.registeredBreastFeeding(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("preg", EptsReportUtils.map(txPvlsCohortQueries.pregnantsInscribedOnARTService(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
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
		cd.addSearch("supp",
		    EptsReportUtils.map(getPatientsWithViralLoadSuppressionExcludingDeadLtfuTransferredoutStoppedArt(), mappings));
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
		cd.addSearch("suppression",
		    EptsReportUtils.map(getPatientsWithViralLoadSuppressionExcludingDeadLtfuTransferredoutStoppedArt(), mappings));
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
		cd.addSearch("supp", EptsReportUtils.map(hivCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(), mappings));
		cd.addSearch("dead", EptsReportUtils.map(txPvlsCohortQueries.getDeadPersons(), mappings));
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
		cd.addSearch("results", EptsReportUtils.map(hivCohortQueries.getPatientsViralLoadWithin12Months(), mappings));
		cd.addSearch("dead", EptsReportUtils.map(txPvlsCohortQueries.getDeadPersons(), mappings));
		cd.setCompositionString("results AND NOT dead");
		return cd;
	}
}
