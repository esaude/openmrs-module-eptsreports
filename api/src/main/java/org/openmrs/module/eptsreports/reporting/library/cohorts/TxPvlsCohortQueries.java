/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.BreastfeedingAndPregnantCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.OnArtForMoreThanXmonthsCalcultion;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.RoutineCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.PregnantQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxPvlsQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.BreastfeedingAndPregnant;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.PatientsOnRoutineEnum;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
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
	private GenericCohortQueries genericCohortQueries;
	
	@Autowired
	private HivCohortQueries hivCohortQueries;
	
	@Autowired
	private HivMetadata hivMetadata;
	
	/**
	 * Patients who have NOT been on ART for 3 months based on the ART initiation date and date of
	 * last viral load registered
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWhoAreMoreThan3MonthsOnArt() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition("On ART for at least 3 months", Context
		        .getRegisteredComponents(OnArtForMoreThanXmonthsCalcultion.class).get(0));
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		return cd;
	}
	
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
		cd.addSearch("breastfeeding",
		    EptsReportUtils.map(getBreastFeedingSQLQuery(), "endDate=${endDate},location=${location}"));
		
		// cd.addSearch("breastfeeding",
		// EptsReportUtils.map(getPatientsWhoArePregnantOrBreastfeeding(BreastfeedingAndPregnant.BREASTFEEDING),
		// "onDate=${endDate},location=${location}"));
		
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
		cd.addSearch("breastfeeding",
		    EptsReportUtils.map(getBreastFeedingSQLQuery(), "endDate=${endDate},location=${location}"));
		
		//
		// cd.addSearch("breastfeeding",
		// EptsReportUtils.map(getPatientsWhoArePregnantOrBreastfeeding(BreastfeedingAndPregnant.BREASTFEEDING),
		// "onDate=${endDate},location=${location}"));
		
		cd.addSearch("results", EptsReportUtils.map(getPatientsWithViralLoadResults(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("breastfeeding AND results");
		
		return cd;
	}
	
	/**
	 * Patients with viral suppression of <1000 in the last 12 months excluding dead, LTFU,
	 * transferred out, stopped ART
	 */
	public CohortDefinition getPatientsWithViralLoadSuppression() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("supp",
		    EptsReportUtils.map(hivCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(), mappings));
		cd.addSearch("baseCohort", EptsReportUtils.map(genericCohortQueries.getBaseCohort(), mappings));
		cd.addSearch("onArtLongEnough",
		    EptsReportUtils.map(getPatientsWhoAreMoreThan3MonthsOnArt(), "onDate=${endDate},location=${location}"));
		cd.setCompositionString("supp AND baseCohort AND onArtLongEnough");
		return cd;
	}
	
	/**
	 * Patients with viral results recorded in the last 12 months excluding dead, LTFU, transferred
	 * out, stopped ARTtxNewCohortQueries
	 */
	public CohortDefinition getPatientsWithViralLoadResults() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("results", EptsReportUtils.map(hivCohortQueries.getPatientsViralLoadWithin12Months(), mappings));
		cd.addSearch("baseCohort", EptsReportUtils.map(genericCohortQueries.getBaseCohort(), mappings));
		cd.addSearch("onArtLongEnough",
		    EptsReportUtils.map(getPatientsWhoAreMoreThan3MonthsOnArt(), "onDate=${endDate},location=${location}"));
		cd.setCompositionString("results AND baseCohort AND onArtLongEnough");
		return cd;
	}
	
	/**
	 * Get patients who are aged between age bracket
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public CohortDefinition findPatientsBetweenAgeBracketsInYears(int min, int max) {
		return genericCohortQueries.generalSql("aged between age brackets",
		    TxPvlsQueries.getPatientsBetweenAgeBracketsInYears(min, max));
	}
	
	/**
	 * Find patients who are aged below
	 * 
	 * @param age
	 * @return
	 */
	public CohortDefinition findPatientsagedBelowInYears(int age) {
		return genericCohortQueries.generalSql("aged between age brackets", TxPvlsQueries.getPatientsWhoAreBelowXyears(age));
	}
	
	/**
	 * Get adults and children patients who are on routine
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum criteria) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition("criteria", Context.getRegisteredComponents(
		    RoutineCalculation.class).get(0));
		cd.setName("Routine for all patients controlled by parameter");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addCalculationParameter("criteria", criteria);
		return cd;
		
	}
	
	/**
	 * Get patients having viral load suppression and routine for adults and children - Numerator
	 * 
	 * @retrun CohortDefinition
	 */
	public CohortDefinition getPatientWithViralSuppressionAndOnRoutineAdultsAndChildren() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Suppression and on routine adult and children");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("supp", EptsReportUtils.map(getPatientsWithViralLoadSuppression(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.ADULTCHILDREN),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("supp AND routine");
		return cd;
	}
	
	/**
	 * Get patients having viral load suppression and not documented for adults and children -
	 * Numerator
	 * 
	 * @retrun CohortDefinition
	 */
	public CohortDefinition getPatientWithViralSuppressionAndNotDocumentedForAdultsAndChildren() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("suppression and not documented adults and children");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("supp", EptsReportUtils.map(getPatientsWithViralLoadSuppression(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.ADULTCHILDREN),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("supp AND NOT routine");
		return cd;
	}
	
	/**
	 * Get patients with viral load results and on routine - Denominator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWithViralLoadREsultsAndOnRoutineForChildrenAndAdults() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Viral load results with routine for children and adults denominator");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("results", EptsReportUtils.map(getPatientsWithViralLoadResults(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.ADULTCHILDREN),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("results AND routine");
		return cd;
	}
	
	/**
	 * Get patients with viral load results and NOT documented
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWithViralLoadREsultsAndNotDocumenetdForChildrenAndAdults() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Viral load results with not documentation for children and adults denominator");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("results", EptsReportUtils.map(getPatientsWithViralLoadResults(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.ADULTCHILDREN),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("results AND NOT routine");
		return cd;
	}
	
	// breastfeeding and pregnant women
	// breast feeding Numerator.
	/**
	 * Get breastfeding women on routine Numerator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getBreastFeedingWomenOnRoutineNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding with viral results and on routine");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("breastfeedingVl", EptsReportUtils.map(getBreastfeedingWomenWhoHaveViralSuppression(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("breastfeedingVl AND routine");
		
		return cd;
	}
	
	/**
	 * Get breastfeeding women NOT documented Numerator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getBreastFeedingWomenNotDocumentedNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding with viral results and NOT documented");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("breastfeedingVl", EptsReportUtils.map(getBreastfeedingWomenWhoHaveViralSuppression(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("breastfeedingVl AND NOT routine");
		
		return cd;
	}
	
	/**
	 * Get pregnant women Numerator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantWomenWithViralLoadSuppressionNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Get pregnant women with viral load suppression");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("suppression", EptsReportUtils.map(getPatientsWithViralLoadSuppression(), mappings));
		
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantSQLQuery(), "endDate=${endDate},location=${location}"));
		
		// cd.addSearch("pregnant",
		// EptsReportUtils.map(getPatientsWhoArePregnantOrBreastfeeding(BreastfeedingAndPregnant.PREGNANT),
		// "onDate=${endDate},location=${location}"));
		cd.setCompositionString("suppression AND pregnant");
		return cd;
	}
	
	/**
	 * Get pregnant women Denominator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantWomenWithViralLoadResultsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Get pregnant women with viral load results denominator");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("results", EptsReportUtils.map(getPatientsWithViralLoadResults(), mappings));
		
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantSQLQuery(), "endDate=${endDate},location=${location}"));
		
		// cd.addSearch("pregnant",
		// EptsReportUtils.map(getPatientsWhoArePregnantOrBreastfeeding(BreastfeedingAndPregnant.PREGNANT),
		// "onDate=${endDate},location=${location}"));
		cd.setCompositionString("results AND pregnant");
		return cd;
	}
	
	/**
	 * Get pregnant, has viral load suppression, and on routine Numerator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantAndOnRoutineNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Pregnant and on Routine Numerator");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantWomenWithViralLoadSuppressionNumerator(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("pregnant AND routine");
		return cd;
	}
	
	/**
	 * Get pregnant, has viral load, and NOT documented Numerator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantAndNotDocumentedNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Pregnant and NOT documented Numerator");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantWomenWithViralLoadSuppressionNumerator(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("pregnant AND NOT routine");
		return cd;
	}
	
	// Pregnant and breastfeeding denominator
	// Breastfeeding
	/**
	 * Patients who have viral load results, breastfeeding and on routine denominator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getBreastfeedingWomenOnRoutineWithViralLoadResultsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding women on routine and have Viral load results");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("vlandBreastfeeding", EptsReportUtils.map(getBreastfeedingWomenWhoHaveViralLoadResults(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("vlandBreastfeeding AND routine");
		return cd;
	}
	
	/**
	 * Patients who have virial load results and NOT documented and are breastfeeding denominator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getBreastfeedingWomenAndNotDocumentedWithViralLoadResultsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding women NOT documented and have Viral load results");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("vlandBreastfeeding", EptsReportUtils.map(getBreastfeedingWomenWhoHaveViralLoadResults(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("vlandBreastfeeding AND NOT routine");
		return cd;
	}
	
	// Pregnant
	/**
	 * Patients who are pregnant, have viral load results and on routine denominator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantWomenAndOnRoutineWithViralLoadResultsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Get pregnant women with viral load results Not documented");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantWomenWithViralLoadResultsDenominator(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("pregnant AND routine");
		return cd;
	}
	
	/**
	 * Patients who are pregnant, have viral load and NOT Documented
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantWomenAndNotDocumentedWithViralLoadResultsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Get pregnant women with viral load results Not documented");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantWomenWithViralLoadResultsDenominator(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
		    "onDate=${endDate},location=${location}"));
		cd.setCompositionString("pregnant AND NOT routine");
		return cd;
	}
	
	/**
	 * Patients doing ART for at least 3 months compared to the VL date
	 * 
	 * @return
	 */
	public CohortDefinition getPatientsOnArtLongEnough() {
		SqlCohortDefinition sql = new SqlCohortDefinition();
		sql.setName("patientsOnArtLongEnough");
		sql.addParameter(new Parameter("onDate", "onDate", Date.class));
		sql.addParameter(new Parameter("location", "Location", Location.class));
		String query = "select patient_start_date.patient_id from ( "
		        + "select all_start_dates.patient_id, min(all_start_dates.start_date) start_date from ( "
		        + "select person_id patient_id, min(date(obs.obs_datetime)) start_date from obs where obs.location_id = :location and obs.concept_id = %s and obs.value_coded = %s and obs.voided = false group by patient_id "
		        + "union "
		        + "select person_id patient_id, min(date(obs.value_datetime)) start_date from obs where obs.location_id = :location and obs.concept_id = %s and obs.voided = false group by patient_id "
		        + "union "
		        + "select patient_id, date(min(patient_program.date_enrolled)) start_date from patient_program where patient_program.location_id = :location and patient_program.program_id = %s and patient_program.voided = false group by patient_id "
		        + "union "
		        + "select patient_id, min(date(encounter.encounter_datetime)) start_date from encounter where encounter.encounter_type = %s and encounter.voided = false and encounter.location_id = :location group by patient_id "
		        + "union "
		        + "select person_id patient_id, date(min(obs.obs_datetime)) start_date from obs where obs.location_id = :location and obs.concept_id = %s and obs.value_coded = %s and obs.voided = false group by patient_id "
		        + ") all_start_dates group by all_start_dates.patient_id) patient_start_date join (select encounter.patient_id, max(date(encounter.encounter_datetime)) result_date from encounter join obs on obs.encounter_id = encounter.encounter_id "
		        + "where encounter.encounter_type = %s and encounter.voided = false and encounter.location_id = :location and encounter.encounter_datetime between date_add(:onDate, interval -1 year) and :onDate and obs.value_numeric is not null and obs.concept_id = %s and obs.voided = false "
		        + "group by patient_id) patient_vl on patient_vl.patient_id = patient_start_date.patient_id where date_add(patient_start_date.start_date, interval 3 month) <= patient_vl.result_date ";
		sql.setQuery(String.format(query, hivMetadata.getARVPlanConcept().getConceptId(), hivMetadata.getstartDrugsConcept()
		        .getConceptId(), hivMetadata.gethistoricalDrugStartDateConcept().getConceptId(), hivMetadata.getARTProgram()
		        .getProgramId(), hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(), hivMetadata
		        .getARVPlanConcept().getConceptId(), hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
		    hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(), hivMetadata.getHivViralLoadConcept()
		            .getConceptId()));
		return sql;
	}
	
	/**
	 * Get patients who are pregnant or breastfeeding
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWhoArePregnantOrBreastfeeding(BreastfeedingAndPregnant criteria) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition("breastfeeding or pregnant", Context
		        .getRegisteredComponents(BreastfeedingAndPregnantCalculation.class).get(0));
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addCalculationParameter("criteria", criteria);
		return cd;
	}
	
	public CohortDefinition getPregnantSQLQuery() {
		SqlCohortDefinition sql = new SqlCohortDefinition();
		sql.setName("pregnant");
		sql.addParameter(new Parameter("endDate", "endDate", Date.class));
		sql.addParameter(new Parameter("location", "Location", Location.class));
		sql.setQuery(PregnantQueries.PREGNANT_SQL_QUERY);
		return sql;
	}
	
	public CohortDefinition getBreastFeedingSQLQuery() {
		
		SqlCohortDefinition sql = new SqlCohortDefinition();
		sql.setName("Breastfeeding");
		sql.addParameter(new Parameter("endDate", "endDate", Date.class));
		sql.addParameter(new Parameter("location", "Location", Location.class));
		sql.setQuery(PregnantQueries.BREASTFEEDING_SQL);
		
		return sql;
	}
	
	// String query = "select carga_viral.patient_id from " + "( Select
	// patient_id,min(data_inicio) data_inicio from "
	// + " ( Select p.patient_id,min(e.encounter_datetime) data_inicio "
	// + " from patient p "
	// + " inner join encounter e on p.patient_id=e.patient_id "
	// + " inner join obs o on o.encounter_id=e.encounter_id "
	// + " where e.voided=0 and o.voided=0 and p.voided=0 and "
	// + " e.encounter_type in (18,6,9) and o.concept_id=1255 and
	// o.value_coded=1256 and "
	// + " e.encounter_datetime<=:endDate and e.location_id=:location "
	// + " group by p.patient_id union "
	// + " Select p.patient_id,min(value_datetime) data_inicio from patient
	// p "
	// + " inner join encounter e on p.patient_id=e.patient_id "
	// + " inner join obs o on e.encounter_id=o.encounter_id "
	// + " where p.voided=0 and e.voided=0 and o.voided=0 and
	// e.encounter_type in (18,6,9) and "
	// + " o.concept_id=1190 and o.value_datetime is not null and "
	// + " o.value_datetime<=:endDate and e.location_id=:location "
	// + " group by p.patient_id union "
	// + " select pg.patient_id,date_enrolled data_inicio "
	// + " from patient p inner join patient_program pg on
	// p.patient_id=pg.patient_id "
	// + " where pg.voided=0 and p.voided=0 and program_id=2 and
	// date_enrolled<=:endDate and location_id=:location "
	// + " union SELECT e.patient_id, MIN(e.encounter_datetime) AS
	// data_inicio "
	// + " FROM patient p "
	// + " inner join encounter e on p.patient_id=e.patient_id "
	// + " WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and
	// e.encounter_datetime<=:endDate and e.location_id=:location "
	// + " GROUP BY p.patient_id) inicio " + " group by
	// patient_id)inicio_real inner join ( "
	// + " Select
	// ultima_carga.patient_id,ultima_carga.data_carga,obs.value_numeric
	// valor_carga " + " from "
	// + " ( Select p.patient_id,max(o.obs_datetime) data_carga "
	// + " from patient p inner join encounter e on
	// p.patient_id=e.patient_id "
	// + " inner join obs o on e.encounter_id=o.encounter_id "
	// + " where p.voided=0 and e.voided=0 and o.voided=0 and
	// e.encounter_type in (13,6,9) and "
	// + " o.concept_id=856 and o.value_numeric is not null and "
	// + " e.encounter_datetime between date_add(date_add(:endDate, interval
	// -12 MONTH), interval 1 day) and :endDate and e.location_id=:location
	// "
	// + " group by p.patient_id) ultima_carga "
	// + " inner join obs on obs.person_id=ultima_carga.patient_id and
	// obs.obs_datetime=ultima_carga.data_carga "
	// + " where obs.voided=0 and obs.concept_id=856 and
	// obs.location_id=:location "
	// + ") carga_viral on inicio_real.patient_id=carga_viral.patient_id "
	// + " inner join ( Select p.patient_id, e.encounter_datetime data_parto
	// "
	// + " from patient p inner join encounter e on
	// p.patient_id=e.patient_id "
	// + " inner join obs o on e.encounter_id=o.encounter_id "
	// + " where p.voided=0 and e.voided=0 and o.voided=0 and
	// concept_id=6332 and value_coded=1065 and "
	// + " e.encounter_type in (5,6,9) and e.encounter_datetime <=:endDate
	// and e.location_id=:location "
	// + " union Select p.patient_id, e.encounter_datetime data_parto from
	// patient p "
	// + " inner join encounter e on p.patient_id=e.patient_id "
	// + " inner join obs o on e.encounter_id=o.encounter_id "
	// + " where p.voided=0 and e.voided=0 and o.voided=0 and
	// concept_id=6334 and value_coded=6332 and "
	// + " e.encounter_type in (5,6,9) and e.encounter_datetime<=:endDate
	// and e.location_id=:location "
	// + " union " + " Select p.patient_id,o.value_datetime data_parto "
	// + " from patient p inner join encounter e on
	// p.patient_id=e.patient_id "
	// + " inner join obs o on e.encounter_id=o.encounter_id "
	// + " where p.voided=0 and e.voided=0 and o.voided=0 and
	// concept_id=5599 and "
	// + " e.encounter_type in (5,6) and o.value_datetime<=:endDate and
	// e.location_id=:location "
	// + " union select pg.patient_id,ps.start_date data_parto "
	// + " from patient p inner join patient_program pg on
	// p.patient_id=pg.patient_id "
	// + " inner join patient_state ps on
	// pg.patient_program_id=ps.patient_program_id "
	// + " where pg.voided=0 and ps.voided=0 and p.voided=0 and "
	// + " pg.program_id=8 and ps.state=27 and ps.end_date is null and "
	// + " ps.start_date<=:endDate and location_id=:location "
	// + " ) lactante_real on
	// lactante_real.patient_id=carga_viral.patient_id "
	// + " where carga_viral.data_carga>=date_add(inicio_real.data_inicio,
	// interval 3 MONTH) and "
	// + " carga_viral.data_carga>lactante_real.data_parto and "
	// + " lactante_real.data_parto between date_add(carga_viral.data_carga,
	// interval -18 MONTH) and carga_viral.data_carga "; // and
	// carga_viral.valor_carga<1000
	
}
