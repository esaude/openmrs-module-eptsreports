package org.openmrs.module.eptsreports.reporting.library.queries;

/**
 * Re usable queries that can be used for finding patients who are pregnant
 */
public class PregnantQueries {
	
	/**
	 * Looks for patients indicated PREGNANT in the initial or follow-up consultation between start date
	 * and end date
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
	 * Looks for patients with PREGNANCY DUE DATE registered in the initial or follow-up consultation
	 * between start date and end date
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
	
}
