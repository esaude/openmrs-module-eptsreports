package org.openmrs.module.eptsreports.reporting.library.queries;

public class BreastfeedingQueries {
	
	public static String getPatientsWhoGaveBirthTwoYearsAgo(int etvProgram, int patientState) {
		return "select 	pg.patient_id" + " from patient p" + " inner join patient_program pg on p.patient_id=pg.patient_id"
		        + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id"
		        + " where pg.voided=0 and ps.voided=0 and p.voided=0 and" + " pg.program_id=" + etvProgram + " and ps.state="
		        + patientState + " and ps.end_date is null and"
		        + " ps.start_date between date_add(:startDate, interval -2 year) and date_add(:startDate, interval -1 day) and location_id=:location";
	}
	
}
