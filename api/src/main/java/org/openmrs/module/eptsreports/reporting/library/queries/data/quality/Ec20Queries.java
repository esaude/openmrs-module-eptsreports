package org.openmrs.module.eptsreports.reporting.library.queries.data.quality;

public class Ec20Queries {

  /**
   * EC20 Patients that are not enrolled in TARV but has a consultation or drugs pick up recorded in
   * the system
   *
   * @param programId
   * @param arvPediatriaSeguimentoEncounterType
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @return
   */
  public static String getEc20CombinedQuery() {
    String query =
        ""
            + "SELECT  pe.person_id as patient_id,  "
            + "pid.identifier AS NID, "
            + "concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,''),' ',ifnull(pn.family_name,'')) AS Name, "
            + "DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, "
            + "IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, "
            + "pe.gender AS Sex, "
            + "DATE_FORMAT(pe.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, "
            + "DATE_FORMAT(pe.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, "
            + "MIN(DATE_FORMAT(fila.encounter_datetime, '%d-%m-%Y')) AS encounter_date_fila, "
            + "DATE_FORMAT(fila.date_created, '%d-%m-%Y %H:%i:%s') AS encounter_date_created_fila, "
            + "DATE_FORMAT(recepcao.encounter_datetime, '%d-%m-%Y') AS encounter_datetime, "
            + "DATE_FORMAT(recepcao.dataFichaRecepcao, '%d-%m-%Y %H:%i:%s') AS data_encounter_recepcao, "
            + "DATE_FORMAT(recepcao.date_created, '%d-%m-%Y %H:%i:%s') AS date_created, "
            + "l.name AS location_name FROM  person pe  "
            + "left join (select pid1.* from patient_identifier pid1 "
            + "inner join ( "
            + " select patient_id,min(patient_identifier_id) id from patient_identifier where voided=0 group by patient_id "
            + ") pid2 "
            + "where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id "
            + ") pid on pid.patient_id=pe.person_id "
            + "left join  (select pn1.* from person_name pn1 "
            + "inner join ( "
            + "select person_id,min(person_name_id) id from person_name where voided=0 "
            + "group by person_id "
            + ") pn2 "
            + "where pn1.person_id=pn2.person_id and pn1.person_name_id=pn2.id "
            + ") pn on pn.person_id=pe.person_id "
            + "left join ("
            + "Select p.patient_id, e.encounter_datetime, l.name  location_name, e.date_created from  patient p "
            + "inner join encounter e on p.patient_id = e.patient_id "
            + "inner join location l on l.location_id = e.location_id "
            + "where  p.voided = 0 and e.voided = 0 and e.encounter_type = 18 and e.location_id IN (:location) "
            + "AND e.encounter_datetime between :startDate and :endDate "
            + ") fila  on fila.patient_id = pe.person_id "
            + "left join ( "
            + "SELECT  p.patient_id, o.value_datetime encounter_datetime, e.encounter_datetime dataFichaRecepcao, l.name location_name, e.date_created date_created FROM  patient p "
            + "INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "inner join obs o on e.encounter_id = o.encounter_id "
            + "inner join location l on l.location_id = e.location_id "
            + "where  p.voided = 0 and e.voided = 0 and o.voided = 0 and e.encounter_type = 52 "
            + "AND o.concept_id = 23866 and o.value_datetime is not null and e.location_id IN (:location) "
            + "AND o.value_datetime between :startDate and :endDate "
            + ") recepcao  on recepcao.patient_id = pe.person_id "
            + "left join  patient_program pg ON pe.person_id = pg.patient_id and pg.program_id = 2 and pg.location_id IN (:location) "
            + "left join  patient_state ps ON pg.patient_program_id = ps.patient_program_id and ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + "left join location l on pid.location_id=l.location_id "
            + "where pe.voided = 0  "
            + "AND (fila.patient_id not in(select p.patient_id FROM patient p INNER JOIN patient_program pg on pg.patient_id=p.patient_id where pg.program_id=2))  "
            + "OR  (recepcao.patient_id not in(select p.patient_id FROM patient p INNER JOIN patient_program pg on pg.patient_id=p.patient_id where pg.program_id=2))  "
            + "GROUP BY pe.person_id ";

    return query;
  }

  public static String getEc20Total() {
    String query =
        "SELECT  pe.person_id as patient_id FROM  person pe  "
            + "left join (select pid1.* from patient_identifier pid1 "
            + "inner join ( "
            + " select patient_id,min(patient_identifier_id) id from patient_identifier where voided=0 group by patient_id "
            + ") pid2 "
            + "where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id "
            + ") pid on pid.patient_id=pe.person_id "
            + "left join  (select pn1.* from person_name pn1 "
            + "inner join ( "
            + "select person_id,min(person_name_id) id from person_name where voided=0 "
            + "group by person_id "
            + ") pn2 "
            + "where pn1.person_id=pn2.person_id and pn1.person_name_id=pn2.id "
            + ") pn on pn.person_id=pe.person_id "
            + "left join ("
            + "Select p.patient_id, e.encounter_datetime, l.name  location_name, e.date_created from  patient p "
            + "inner join encounter e on p.patient_id = e.patient_id "
            + "inner join location l on l.location_id = e.location_id "
            + "where  p.voided = 0 and e.voided = 0 and e.encounter_type = 18 and e.location_id IN (:location) "
            + "AND e.encounter_datetime between :startDate and :endDate "
            + ") fila  on fila.patient_id = pe.person_id "
            + "left join ( "
            + "SELECT  p.patient_id, o.value_datetime encounter_datetime, e.encounter_datetime dataFichaRecepcao, l.name location_name, e.date_created date_created FROM  patient p "
            + "INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "inner join obs o on e.encounter_id = o.encounter_id "
            + "inner join location l on l.location_id = e.location_id "
            + "where  p.voided = 0 and e.voided = 0 and o.voided = 0 and e.encounter_type = 52 "
            + "AND o.concept_id = 23866 and o.value_datetime is not null and e.location_id IN (:location) "
            + "AND o.value_datetime between :startDate and :endDate "
            + ") recepcao  on recepcao.patient_id = pe.person_id "
            + "left join  patient_program pg ON pe.person_id = pg.patient_id and pg.program_id = 2 and pg.location_id IN (:location) "
            + "left join  patient_state ps ON pg.patient_program_id = ps.patient_program_id and ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + "left join location l on pid.location_id=l.location_id "
            + "where pe.voided = 0  "
            + "AND (fila.patient_id not in(select p.patient_id FROM patient p INNER JOIN patient_program pg on pg.patient_id=p.patient_id where pg.program_id=2))  "
            + "OR  (recepcao.patient_id not in(select p.patient_id FROM patient p INNER JOIN patient_program pg on pg.patient_id=p.patient_id where pg.program_id=2))  "
            + "GROUP BY pe.person_id ";

    return query;
  }
}
