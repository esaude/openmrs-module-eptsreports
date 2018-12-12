package org.openmrs.module.eptsreports.reporting.library.queries;

public class BaseQueries {
	
	// State ids are left as hard coded for now because all reference same concept
	// they map to concept_id=1369 - TRANSFER FROM OTHER FACILITY
	// TODO: Query needs to be refactored
	public static String getBaseCohortQuery(int... parameters) {
		return "select p.patient_id" + " from patient p inner join encounter e on e.patient_id=p.patient_id"
		        + " where e.voided=0 and p.voided=0 and e.encounter_type in (" + parameters[0] + "," + parameters[1]
		        + ") and e.encounter_datetime<=:endDate and e.location_id = :location" + "\n" + "union" + "\n" + "select pg.patient_id"
		        + " from patient p inner join patient_program pg on p.patient_id=pg.patient_id"
		        + " where pg.voided=0 and p.voided=0 and program_id=" + parameters[2]
		        + " and date_enrolled<=:endDate and location_id=:location" + "\n" + "union" + "\n" + "select pg.patient_id"
		        + " from patient p" + " inner join patient_program pg on p.patient_id=pg.patient_id"
		        + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id"
		        + " where pg.voided=0 and ps.voided=0 and p.voided=0 and" + " pg.program_id=" + parameters[2]
		        + " and ps.state=28 and ps.start_date=pg.date_enrolled and" + " ps.start_date<=:endDate and location_id=:location"
		        + "\n" + "union" + "\n" + "select pg.patient_id" + " from patient p"
		        + " inner join patient_program pg on p.patient_id=pg.patient_id"
		        + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id"
		        + " where pg.voided=0 and ps.voided=0 and p.voided=0 and" + " pg.program_id=" + parameters[3] + " and ps.state=29 and"
		        + " ps.start_date<=:endDate and location_id=:location";
	}
	
	public static String getPatientsBetweenAgeBracketsInYears(int min, int max) {
		return "SELECT person_id FROM person pe INNER JOIN patient pa ON pe.person_id=pa.patient_id"
		        + " INNER JOIN encounter e ON pa.patient_id=e.patient_id" + " WHERE TIMESTAMPDIFF(year, pe.birthdate, :endDate)>="
		        + min + " AND TIMESTAMPDIFF(year, pe.birthdate, :endDate) <=" + max
		        + " AND pe.voided=0 AND pa.voided=0 AND e.location_id=:location AND e.voided=0" + " AND pe.birthdate is not null";
	}
}
