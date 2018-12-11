package org.openmrs.module.eptsreports.reporting.library.queries;

public class BaseQueries {
	
	public static String getBaseCohortQuery() {
		return "select p.patient_id" + " from patient p inner join encounter e on e.patient_id=p.patient_id"
		        + " where e.voided=0 and p.voided=0 and e.encounter_type in (5,7) and e.encounter_datetime<=:endDate and e.location_id = :location"
		        + "\n" + "union" + "\n" + "select pg.patient_id"
		        + " from patient p inner join patient_program pg on p.patient_id=pg.patient_id"
		        + " where pg.voided=0 and p.voided=0 and program_id=1 and date_enrolled<=:endDate and location_id=:location" + "\n"
		        + "union" + "\n" + "select pg.patient_id" + " from patient p"
		        + " inner join patient_program pg on p.patient_id=pg.patient_id"
		        + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id"
		        + " where pg.voided=0 and ps.voided=0 and p.voided=0 and"
		        + " pg.program_id=1 and ps.state=28 and ps.start_date=pg.date_enrolled and"
		        + " ps.start_date<=:endDate and location_id=:location" + "\n" + "union" + "\n" + "select pg.patient_id"
		        + " from patient p" + " inner join patient_program pg on p.patient_id=pg.patient_id"
		        + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id"
		        + " where pg.voided=0 and ps.voided=0 and p.voided=0 and" + " pg.program_id=2 and ps.state=29 and"
		        + " ps.start_date<=:endDate and location_id=:location";
	}
}
