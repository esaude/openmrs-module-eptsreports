/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.queries.data.quality;

public class Ec8Queries {

  /**
   * Get the query to be used to display the EC8 patient listing
   *
   * @return String
   */
  public static String getEc8CombinedQuery() {
    String query =
        "SELECT "
            + "pe.person_id As patient_id,  "
            + "pid.identifier AS NID,  "
            + "concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,''),' ',ifnull(pn.family_name,'')) AS Name,  "
            + "DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate,  "
            + "IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob,  "
            + "pe.gender AS Sex,  "
            + "DATE_FORMAT(pe.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date,  "
            + "DATE_FORMAT(pe.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated,  "
            + "DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y') AS date_enrolled,  "
            + "case  "
            + "when ps.state = 9 then 'DROPPED FROM TREATMENT'  "
            + "when ps.state = 6 then 'ACTIVE ON PROGRAM'  "
            + "when ps.state = 10 then 'PATIENT HAS DIED'  "
            + "when ps.state = 8 then 'SUSPENDED TREATMENT'  "
            + "when ps.state = 7 then 'TRANSFERED OUT TO ANOTHER FACILITY'  "
            + "when ps.state = 29 then 'TRANSFERRED FROM OTHER FACILTY'  "
            + "end AS state,  "
            + "DATE_FORMAT(ps.start_date, '%d-%m-%Y') AS state_date,  "
            + "MIN(DATE_FORMAT(requestLabDate.dataPedidoLab, '%d-%m-%Y')) AS dataPedidoLab,  "
            + "MIN(DATE_FORMAT(colheitaLabDate.dataColheitaLab, '%d-%m-%Y')) AS dataColheitaLab,  "
            + "DATE_FORMAT(requestLabDate.dataLabForm, '%d-%m-%Y %H:%i:%s') AS dataLabFormPedido,  "
            + "DATE_FORMAT(requestLabDate.dataLabCreation, '%d-%m-%Y %H:%i:%s') AS  dataLabCreation,  "
            + "MIN(DATE_FORMAT(colheitaFSRDate.dataLabFSR, '%d-%m-%Y')) AS dataColheitaFSR,  "
            + "DATE_FORMAT(colheitaFSRDate.dataFSRCreation, '%d-%m-%Y %H:%i:%s') AS dataFSRCreation,  "
            + "l.name location_name FROM person pe "
            + "left join ( "
            + "SELECT p.patient_id patient_id, ps.start_date trasfered_date FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id = pg.patient_id AND pg.date_completed IS NULL "
            + "INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id  "
            + "WHERE ps.state=7 AND pg.voided = 0 AND ps.voided = 0 AND pg.program_id = 2 AND (ps.start_date IS NOT NULL AND ps.end_date IS NULL and ps.voided =0)  "
            + "GROUP by patient_id "
            + ") trasferedOut  on pe.person_id=trasferedOut.patient_id "
            + "left join ("
            + "select pn1.* from person_name pn1 "
            + "inner join ( "
            + "select person_id,min(person_name_id) id from person_name "
            + "where voided=0 "
            + "group by person_id "
            + ") pn2 "
            + "where pn1.person_id=pn2.person_id and pn1.person_name_id=pn2.id "
            + ") pn on pn.person_id=pe.person_id "
            + "left join ( "
            + "select pid1.* from patient_identifier pid1 "
            + "inner join ( "
            + "select patient_id,min(patient_identifier_id) id from patient_identifier "
            + "where voided=0 "
            + "group by patient_id "
            + ") pid2 "
            + "where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id "
            + ") pid on pid.patient_id=pe.person_id "
            + "LEFT JOIN ( "
            + "SELECT p.patient_id as patient_id, o.value_datetime as dataPedidoLab,e.encounter_datetime dataLabForm, e.date_created dataLabCreation, l.name locationPedidoLab FROM patient p "
            + "INNER JOIN encounter e on e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "INNER JOIN location l on l.location_id=e.location_id "
            + "WHERE e.encounter_type=13 AND o.concept_id=6246  AND o.voided=0 AND e.voided=0 and "
            + "o.value_datetime between :startDate and :endDate and e.location_id =:location "
            + ")requestLabDate  on requestLabDate.patient_id=pe.person_id "
            + "LEFT JOIN ( "
            + "SELECT p.patient_id as patient_id, o.value_datetime as dataColheitaLab,e.encounter_datetime dataLabForm,  e.date_created dataLabCreation, l.name locationColheitaLab "
            + "FROM patient p "
            + "INNER JOIN encounter e on e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "INNER JOIN location l on l.location_id=e.location_id "
            + "WHERE e.encounter_type=13 AND o.concept_id=23821  AND o.voided=0 AND e.voided=0 and "
            + "o.value_datetime between :startDate and :endDate and e.location_id =:location "
            + ")colheitaLabDate on colheitaLabDate.patient_id=pe.person_id "
            + "LEFT JOIN ( "
            + "SELECT p.patient_id as patient_id, o.value_datetime as dataColheitaFSR,e.encounter_datetime dataLabFSR,e.date_created dataFSRCreation, l.name locationColheitaFSR FROM patient p "
            + "INNER JOIN encounter e on e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "INNER JOIN location l on l.location_id=e.location_id "
            + "WHERE e.encounter_type=51 AND o.concept_id=23821  AND o.voided=0 AND e.voided=0 and "
            + "o.value_datetime between :startDate and :endDate and e.location_id =:location "
            + ")colheitaFSRDate on colheitaFSRDate.patient_id=pe.person_id "
            + "left join  patient_program pg ON pe.person_id=pg.patient_id and pg.program_id=2 and pg.location_id IN(:location) "
            + "inner join  patient_state ps ON pg.patient_program_id=ps.patient_program_id and (ps.start_date IS NOT NULL AND ps.end_date IS NULL and ps.voided =0) "
            + "left join location l on l.location_id=pid.location_id "
            + "where pe.voided=0 and (trasferedOut.patient_id is not null) "
            + "and ((requestLabDate.dataPedidoLab>trasferedOut.trasfered_date) "
            + "OR "
            + "(colheitaLabDate.dataColheitaLab>trasferedOut.trasfered_date) "
            + "OR "
            + "(colheitaFSRDate.dataColheitaFSR>trasferedOut.trasfered_date)) GROUP BY pe.person_id ";

    return query;
  }

  public static String getEc8Total() {
    String query =
        "SELECT pe.person_id As patient_id FROM person pe "
            + " left join ( "
            + " SELECT p.patient_id patient_id, ps.start_date trasfered_date FROM patient p "
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id AND pg.date_completed IS NULL "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id  "
            + " WHERE  ps.state=7  AND pg.voided=0  AND ps.voided=0  AND pg.program_id=2 AND ps.start_date IS NOT NULL AND ps.end_date IS NULL  "
            + " GROUP by patient_id "
            + " ) trasferedOut  on pe.person_id=trasferedOut.patient_id "
            + " left join ("
            + " select pn1.* from person_name pn1 "
            + "inner join ( "
            + "select person_id,min(person_name_id) id from person_name "
            + "where voided=0 "
            + "group by person_id "
            + ") pn2 "
            + "where pn1.person_id=pn2.person_id and pn1.person_name_id=pn2.id "
            + ") pn on pn.person_id=pe.person_id "
            + "left join ( "
            + "select pid1.* from patient_identifier pid1 "
            + "inner join ( "
            + "select patient_id,min(patient_identifier_id) id from patient_identifier "
            + "where voided=0 "
            + "group by patient_id "
            + ") pid2 "
            + "where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id "
            + ") pid on pid.patient_id=pe.person_id "
            + "LEFT JOIN ( "
            + "SELECT p.patient_id as patient_id, o.value_datetime as dataPedidoLab,e.encounter_datetime dataLabForm, e.date_created dataLabCreation, l.name locationPedidoLab FROM patient p "
            + "INNER JOIN encounter e on e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "INNER JOIN location l on l.location_id=e.location_id "
            + "WHERE e.encounter_type=13 AND o.concept_id=6246  AND o.voided=0 AND e.voided=0 and "
            + "o.value_datetime between :startDate and :endDate and e.location_id =:location "
            + ")requestLabDate  on requestLabDate.patient_id=pe.person_id "
            + "LEFT JOIN ( "
            + "SELECT p.patient_id as patient_id, o.value_datetime as dataColheitaLab,e.encounter_datetime dataLabForm,  e.date_created dataLabCreation, l.name locationColheitaLab "
            + "FROM patient p "
            + "INNER JOIN encounter e on e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "INNER JOIN location l on l.location_id=e.location_id "
            + "WHERE e.encounter_type=13 AND o.concept_id=23821  AND o.voided=0 AND e.voided=0 and "
            + "o.value_datetime between :startDate and :endDate and e.location_id =:location "
            + ")colheitaLabDate on colheitaLabDate.patient_id=pe.person_id "
            + "LEFT JOIN ( "
            + "SELECT p.patient_id as patient_id, o.value_datetime as dataColheitaFSR,e.encounter_datetime dataLabFSR,e.date_created dataFSRCreation, l.name locationColheitaFSR FROM patient p "
            + "INNER JOIN encounter e on e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "INNER JOIN location l on l.location_id=e.location_id "
            + "WHERE e.encounter_type=51 AND o.concept_id=23821  AND o.voided=0 AND e.voided=0 and "
            + "o.value_datetime between :startDate and :endDate and e.location_id =:location "
            + ")colheitaFSRDate on colheitaFSRDate.patient_id=pe.person_id "
            + "left join  patient_program pg ON pe.person_id=pg.patient_id and pg.program_id=2 and pg.location_id IN(:location) "
            + "inner join  patient_state ps ON pg.patient_program_id=ps.patient_program_id and (ps.start_date IS NOT NULL AND ps.end_date IS NULL and ps.voided =0) "
            + "left join location l on l.location_id=pid.location_id "
            + "where pe.voided=0 and (trasferedOut.patient_id is not null) "
            + "and ((requestLabDate.dataPedidoLab>trasferedOut.trasfered_date) "
            + "OR "
            + "(colheitaLabDate.dataColheitaLab>trasferedOut.trasfered_date) "
            + "OR "
            + "(colheitaFSRDate.dataColheitaFSR>trasferedOut.trasfered_date)) GROUP BY pe.person_id ";

    return query;
  }
}
