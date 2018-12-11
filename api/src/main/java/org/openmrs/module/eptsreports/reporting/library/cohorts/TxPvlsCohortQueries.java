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

import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Defines all of the TxNew Cohort Definition instances we want to expose for EPTS
 */
@Component
public class TxPvlsCohortQueries {
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	@Autowired
	private HivCohortQueries hivCohortQueries;
	
	@Autowired
	private TxNewCohortQueries txNewCohortQueries;
	
	/**
	 * Breast feeding women with viral load suppression
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "breastfeedingWomenWithViralSuppression")
	public CohortDefinition getBreastfeedingWomenWhoHaveViralSuppression() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding with viral suppression");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("breastfeeding", EptsReportUtils.map(txNewCohortQueries.getTxNewBreastfeedingComposition(),
		    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
		cd.addSearch("suppression", EptsReportUtils.map(getPatientsWithViralLoadSuppression(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("breastfeeding AND suppression");
		
		return cd;
	}
	
	/**
	 * Breast feeding women with viral load suppression
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "breastfeedingWomenWithViralLoadResults")
	public CohortDefinition getBreastfeedingWomenWhoHaveViralLoadResults() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding with viral results");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("breastfeeding", EptsReportUtils.map(txNewCohortQueries.getTxNewBreastfeedingComposition(),
		    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
		cd.addSearch("results",
		    EptsReportUtils.map(getPatientsWithViralLoadResults(), "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("breastfeeding AND results");
		
		return cd;
	}
	
	/**
	 * Patients with viral suppression of <1000 in the last 12 months excluding dead, LTFU, transferred
	 * out, stopped ART
	 */
	public CohortDefinition getPatientsWithViralLoadSuppression() {
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
	public CohortDefinition getPatientsWithViralLoadResults() {
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
