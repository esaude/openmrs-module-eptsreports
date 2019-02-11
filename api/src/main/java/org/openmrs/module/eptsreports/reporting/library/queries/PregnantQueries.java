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
package org.openmrs.module.eptsreports.reporting.library.queries;

/**
 * Re usable queries that can be used for finding patients who are pregnant
 */
public class PregnantQueries {
	
	/**
	 * Looks for patients indicated PREGNANT in the initial or follow-up consultation between start
	 * date and end date
	 */
	public static String getPregnantOnInitialOrFollowUpConsulation(int pregnant, int gestation, int adultInEnc,
	        int adultSegEnc) {
		
		return "SELECT p.patient_id" + " FROM patient p" + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
		        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
		        + " WHERE p.voided=0 and e.voided=0 and o.voided=0 and concept_id=" + pregnant + " AND value_coded="
		        + gestation + " AND e.encounter_type IN (" + adultInEnc + "," + adultSegEnc + ")"
		        + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location";
		
	}
	
	/**
	 * Looks for patients with Number of Weeks Pregnant registered in the initial or follow-up
	 * consultation
	 */
	public static String getWeeksPregnantOnInitialOrFollowUpConsultations(int numOfWeeks, int adultInEnc, int adultSegEnc) {
		return "SELECT p.patient_id" + " FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id"
		        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
		        + " WHERE p.voided=0 and e.voided=0 and o.voided=0 and concept_id=" + numOfWeeks
		        + " AND e.encounter_type IN (" + adultInEnc + "," + adultSegEnc + ")"
		        + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND" + " e.location_id=:location";
	}
	
	/**
	 * Looks for patients with PREGNANCY DUE DATE registered in the initial or follow-up
	 * consultation between start date and end date
	 */
	public static String getPregnancyDueDateRegistred(int dueDate, int adultInEnc, int adultSegEnc) {
		return "SELECT p.patient_id FROM patient p INNER JOIN encounter"
		        + " e ON p.patient_id=e.patient_id INNER JOIN obs o ON e.encounter_id=o.encounter_id"
		        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=" + dueDate + " AND e.encounter_type IN ("
		        + adultInEnc + "," + adultSegEnc + ")" + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND"
		        + " e.location_id=:location";
	}
	
	/**
	 * Looks for patients enrolled on PTV/ETV program between start date and end date
	 */
	public static String getEnrolledInPtvOrEtv(int ptvProgram) {
		return "SELECT pp.patient_id FROM patient_program pp WHERE pp.program_id=" + ptvProgram
		        + " AND pp.voided=0 AND pp.date_enrolled BETWEEN " + ":startDate AND :endDate AND pp.location_id=:location";
	}
	
	/**
	 * GRAVIDAS INSCRITAS NO SERVIÇO TARV
	 */
	public static String getPregnantWhileOnArt(int pregnantConcept, int gestationConcept, int weeksPregnantConcept,
	        int eddConcept, int adultInitailEncounter, int adultSegEncounter, int etvProgram) {
		
		return "Select 	p.patient_id" + " from 	patient p" + " inner join encounter e on p.patient_id=e.patient_id"
		        + " inner join obs o on e.encounter_id=o.encounter_id"
		        + " where p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
		        + pregnantConcept
		        + " and value_coded="
		        + gestationConcept
		        + " and e.encounter_type in ("
		        + adultInitailEncounter
		        + ","
		        + adultSegEncounter
		        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location"
		        + " union"
		        + " Select 	p.patient_id"
		        + " from 	patient p inner join encounter e on p.patient_id=e.patient_id"
		        + " inner join obs o on e.encounter_id=o.encounter_id"
		        + " where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
		        + weeksPregnantConcept
		        + " and"
		        + " e.encounter_type in ("
		        + adultInitailEncounter
		        + ","
		        + adultSegEncounter
		        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location"
		        + " union"
		        + " Select p.patient_id"
		        + " from patient p inner join encounter e on p.patient_id=e.patient_id"
		        + " inner join obs o on e.encounter_id=o.encounter_id"
		        + " where p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
		        + eddConcept
		        + " and"
		        + " e.encounter_type in ("
		        + adultInitailEncounter
		        + ","
		        + adultSegEncounter
		        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location"
		        + " union"
		        + " select pp.patient_id from patient_program pp"
		        + " where pp.program_id="
		        + etvProgram
		        + " and pp.voided=0 and pp.date_enrolled between :startDate and :endDate and pp.location_id=:location";
		
	}
}
