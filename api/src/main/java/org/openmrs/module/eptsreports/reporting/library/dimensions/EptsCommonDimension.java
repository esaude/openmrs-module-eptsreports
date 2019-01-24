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
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxPvlsCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EptsCommonDimension {
	
	@Autowired
	private GenderCohortQueries genderCohortQueries;
	
	@Autowired
	private AgeCohortQueries ageCohortQueries;
	
	@Autowired
	private TxNewCohortQueries txNewCohortQueries;
	
	@Autowired
	private TxPvlsCohortQueries txPvlsCohortQueries;
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	/**
	 * Gender dimension
	 * 
	 * @return the {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
	 */
	public CohortDefinitionDimension gender() {
		CohortDefinitionDimension dim = new CohortDefinitionDimension();
		dim.setName("gender");
		dim.addCohortDefinition("M", EptsReportUtils.map(genderCohortQueries.MaleCohort(), ""));
		dim.addCohortDefinition("F", EptsReportUtils.map(genderCohortQueries.FemaleCohort(), ""));
		return dim;
	}
	
	/**
	 * Age range dimension 10-14, 15-19, 20-24, 25-29, 30-34, 35-39, 40-44, 45-49, >=50
	 * 
	 * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
	 */
	public CohortDefinitionDimension pvlsAges() {
		CohortDefinitionDimension dim = new CohortDefinitionDimension();
		dim.addParameter(new Parameter("endDate", "End Date", Date.class));
		dim.addParameter(new Parameter("location", "Location", Location.class));
		dim.setName("pvls ages");
		
		dim.addCohortDefinition("UK",
		    EptsReportUtils.map(ageCohortQueries.getPatientsWithUnknownAge(), "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("<1", EptsReportUtils.map(txPvlsCohortQueries.findPatientsagedBelowInYears(1),
		    "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("1-4", EptsReportUtils.map(txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(1, 4),
		    "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("5-9", EptsReportUtils.map(txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(5, 9),
		    "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("10-14", EptsReportUtils.map(
		    txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(10, 14), "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("15-19", EptsReportUtils.map(
		    txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(15, 19), "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("20-24", EptsReportUtils.map(
		    txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(20, 24), "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("25-29", EptsReportUtils.map(
		    txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(25, 29), "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("30-34", EptsReportUtils.map(
		    txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(30, 34), "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("35-39", EptsReportUtils.map(
		    txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(35, 39), "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("40-44", EptsReportUtils.map(
		    txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(40, 44), "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("45-49", EptsReportUtils.map(
		    txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(45, 49), "endDate=${endDate},location=${location}"));
		dim.addCohortDefinition("50+", EptsReportUtils.map(
		    txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(50, 200), "endDate=${endDate},location=${location}"));
		return dim;
	}
	
	/**
	 * Age range dimension 10-14, 15-19, 20-24, 25-29, 30-34, 35-39, 40-44, 45-49, >=50
	 * 
	 * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
	 */
	public CohortDefinitionDimension getAgeDimension() {
		CohortDefinitionDimension dim = new CohortDefinitionDimension();
		dim.addParameter(new Parameter("effectiveDate", "End Date", Date.class));
		dim.setName("age");
		dim.addCohortDefinition("<1",
		    EptsReportUtils.map(ageCohortQueries.createBelowYAgeCohort("", 1), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("1-4",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("1-4", 1, 4), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("5-9",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("5-9", 5, 9), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("10-14",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("10-14", 10, 14), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("15-19",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("15-19", 15, 19), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("20-24",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("20-24", 20, 24), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("25-29",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("25-29", 25, 29), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("30-34",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("30-34", 30, 34), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("35-39",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("35-39", 35, 39), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("40-44",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("40-44", 40, 44), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("45-49",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("45-49", 45, 49), "effectiveDate=${endDate}"));
		dim.addCohortDefinition(">49",
		    EptsReportUtils.map(ageCohortQueries.createOverXAgeCohort("", 50), "effectiveDate=${endDate}"));
		dim.addCohortDefinition("unknown", EptsReportUtils.map(genericCohortQueries.getUnknownAgeCohort(), ""));
		return dim;
	}
	
	/**
	 * @return CohortDefinitionDimension
	 */
	public CohortDefinitionDimension maternityDimension() {
		CohortDefinitionDimension dim = new CohortDefinitionDimension();
		dim.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dim.addParameter(new Parameter("endDate", "End Date", Date.class));
		dim.addParameter(new Parameter("location", "location", Location.class));
		dim.setName("Maternity Dimension");
		
		dim.addCohortDefinition("breastfeeding", EptsReportUtils.map(txNewCohortQueries.getTxNewBreastfeedingComposition(),
		    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
		dim.addCohortDefinition("pregnant", EptsReportUtils.map(txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		return dim;
	}
}
