package org.openmrs.module.eptsreports.reporting.library.queries;

public class ViralLoadQueries {
	
	/**
	 * Patients with viral load suppression within 12 months
	 * 
	 * @return String
	 */
	public static String getPatientsWithViralLoadSuppression(int labEncounter, int adultSegEncounter, int paedEncounter,
	        int vlConceptQuestion) {
		return "SELECT ultima_carga.patient_id FROM(SELECT p.patient_id,MAX(o.obs_datetime) data_carga"
		        + " FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id"
		        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
		        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN (" + labEncounter + ","
		        + adultSegEncounter + "," + paedEncounter + ") AND  o.concept_id=" + vlConceptQuestion
		        + " AND o.value_numeric IS NOT NULL AND"
		        + " e.encounter_datetime BETWEEN date_add(:endDate, interval -12 MONTH) and :endDate AND"
		        + " e.location_id=:location GROUP BY p.patient_id" + ") ultima_carga"
		        + " INNER JOIN obs ON obs.person_id=ultima_carga.patient_id AND obs.obs_datetime="
		        + "ultima_carga.data_carga  WHERE obs.voided=0 AND obs.concept_id=" + vlConceptQuestion
		        + " AND obs.location_id=:location AND" + " obs.value_numeric < 1000";
	}
	
	/**
	 * Patients having viral load within the 12 months period
	 * 
	 * @return String
	 */
	public static String getPatientsHavingViralLoadInLast12Months(int labEncounter, int adultSegEncounter, int paedEncounter,
	        int vlConceptQuestion) {
		return "SELECT p.patient_id FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN"
		        + " obs o ON e.encounter_id=o.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND"
		        + " e.encounter_type IN (" + labEncounter + "," + adultSegEncounter + "," + paedEncounter
		        + ") AND o.concept_id=" + vlConceptQuestion + " AND o.value_numeric IS NOT NULL AND"
		        + " e.encounter_datetime BETWEEN date_add(:endDate, interval -12 MONTH) AND :endDate AND"
		        + " e.location_id=:location";
	}
}
