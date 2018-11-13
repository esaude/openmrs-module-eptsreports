package org.openmrs.module.eptsreports.reporting.library.queries;

public class BreastfeedingQueries {
	
	public static String getPatientsWhoGaveBirthTwoYearsAgo() {
		return "select 	pg.patient_id" + " from 	patient p"
		        + " inner join patient_program pg on p.patient_id=pg.patient_id"
		        + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id"
		        + " where 	pg.voided=0 and ps.voided=0 and p.voided=0 and"
		        + " pg.program_id=8 and ps.state=27 and ps.end_date is null and"
		        + " ps.start_date between date_add(:startDate, interval -2 year) and date_add(:startDate, interval -1 day) and location_id=:location";
	}
	
	/**
	 * GRAVIDAS INSCRITAS NO SERVIÇO TARV
	 */
	public static String getPregnantWhileOnArt() {
		
		return "Select 	p.patient_id" + " from 	patient p" + " inner join encounter e on p.patient_id=e.patient_id"
		        + " inner join obs o on e.encounter_id=o.encounter_id"
		        + " where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1982 and value_coded=44 and"
		        + " e.encounter_type in (5,6) and e.encounter_datetime between :startDate and :endDate and e.location_id=:location"
		        + " union" + " Select 	p.patient_id"
		        + " from 	patient p inner join encounter e on p.patient_id=e.patient_id"
		        + " inner join obs o on e.encounter_id=o.encounter_id"
		        + " where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1279 and"
		        + " e.encounter_type in (5,6) and e.encounter_datetime between :startDate and :endDate and e.location_id=:location"
		        + " union" + " Select 	p.patient_id"
		        + " from 	patient p inner join encounter e on p.patient_id=e.patient_id"
		        + " inner join obs o on e.encounter_id=o.encounter_id"
		        + " where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1600 and"
		        + " e.encounter_type in (5,6) and e.encounter_datetime between :startDate and :endDate and e.location_id=:location"
		        + " union" + " select 	pp.patient_id" + " from 	patient_program pp"
		        + " where 	pp.program_id=8 and pp.voided=0 and"
		        + " pp.date_enrolled between :startDate and :endDate and pp.location_id=:location";
		
	}
	
}
