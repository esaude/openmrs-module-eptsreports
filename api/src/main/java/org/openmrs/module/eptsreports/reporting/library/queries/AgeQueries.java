package org.openmrs.module.eptsreports.reporting.library.queries;

public class AgeQueries {
	
	/**
	 * Get patients who are aged between age brackets
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static String getPatientsBetweenAgeBracketsInYears(int min, int max) {
		return "SELECT person_id FROM person pe INNER JOIN patient pa ON pe.person_id=pa.patient_id"
		        + " INNER JOIN encounter e ON pa.patient_id=e.patient_id" + " WHERE TIMESTAMPDIFF(year, pe.birthdate, :endDate)>="
		        + min + " AND TIMESTAMPDIFF(year, pe.birthdate, :endDate) <=" + max
		        + " AND pe.voided=0 AND pa.voided=0 AND e.location_id=:location AND e.voided=0" + " AND pe.birthdate is not null";
	}
	
	/**
	 * Find patients who are aged below
	 * 
	 * @param age
	 * @return
	 */
	public static String getPatientsWhoAreBelowXyears(int age) {
		return "SELECT person_id FROM person pe INNER JOIN patient pa ON pe.person_id=pa.patient_id"
		        + " INNER JOIN encounter e ON pa.patient_id=e.patient_id" + " WHERE TIMESTAMPDIFF(year, pe.birthdate, :endDate)<" + age
		        + " AND pe.voided=0 AND pa.voided=0 AND e.location_id=:location AND e.voided=0" + " AND pe.birthdate is not null";
	}
}
