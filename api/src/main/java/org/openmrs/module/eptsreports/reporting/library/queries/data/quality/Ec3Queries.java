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

public class Ec3Queries {

  /**
   * Get the combine query for EC3 patient listing report
   *
   * @return String
   */
  public static String getEc3CombinedQuery(
      int programId,
      int stateId,
      int encounterType,
      int stateOfStayPriorArtPatient,
      int stateOfStayArtPatient,
      int patientHasDiedConceptUuid,
      int masterCardEncounterType,
      int sTarvAdultoSeguimentoEncounterTypeUuid,
      int masterCardDrugPickupEncounterTypeUuid,
      int preArtPickupDate) {
    String query =
        "SELECT pe.person_id As patient_id, "
            + " pid.identifier AS NID, "
            + " concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,''),' ',ifnull(pn.family_name,'')) AS Name, "
            + " DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, "
            + " IF(pe.birthdate_estimated = 1, 'Yes','No') AS estimated_dob, "
            + " pe.gender AS Sex, DATE_FORMAT(pe.date_created, '%d-%m-%Y %H:%i:%s') AS first_entry_date, "
            + " DATE_FORMAT(pe.date_changed, '%d-%m-%Y %H:%i:%s') AS last_updated, "
            + " DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y') AS date_enrolled, "
            + " case "
            + " when ps.state = 9 then 'DROPPED FROM TREATMENT' "
            + " when ps.state = 6 then 'ACTIVE ON PROGRAM' "
            + " when ps.state = 10 then 'PATIENT HAS DIED' "
            + " when ps.state = 8 then 'SUSPENDED TREATMENT' "
            + " when ps.state = 7 then 'TRANSFERED OUT TO ANOTHER FACILITY' "
            + " when ps.state = 29 then 'TRANSFERRED FROM OTHER FACILTY' "
            + " end AS state, "
            + " DATE_FORMAT(ps.start_date, '%d-%m-%Y') AS state_date, "
            + " DATE_FORMAT(pe.death_date,'%d-%m-%Y') As death_date_demographic, "
            + " DATE_FORMAT(deadPrograma.death_date,'%d-%m-%Y') As death_date_program, "
            + " DATE_FORMAT(deadFichaResumo.death_date,'%d-%m-%Y') As death_date_resumo, "
            + " DATE_FORMAT(deadFichaClinica.death_date,'%d-%m-%Y') As death_date_clinica, "
            + " IF(pe.death_date<fila.encounter_datetime OR deadPrograma.death_date<fila.encounter_datetime OR deadFichaResumo.death_date<fila.encounter_datetime OR deadFichaClinica.death_date<fila.encounter_datetime,DATE_FORMAT(fila.encounter_datetime, '%d-%m-%Y'),'') AS encounter_date_fila, "
            + " IF(pe.death_date<fila.date_created OR deadPrograma.death_date<fila.date_created OR deadFichaResumo.death_date<fila.date_created OR deadFichaClinica.death_date<fila.date_created,DATE_FORMAT(fila.date_created, '%d-%m-%Y %H:%i:%s'),'') AS encounter_date_created_fila, "
            + " IF(pe.death_date<recepcao.encounter_datetime OR deadPrograma.death_date<recepcao.encounter_datetime OR deadFichaResumo.death_date<recepcao.encounter_datetime OR deadFichaClinica.death_date<recepcao.encounter_datetime,DATE_FORMAT(recepcao.encounter_datetime, '%d-%m-%Y'),'') AS encounter_date_recepcao, "
            + " IF(pe.death_date<recepcao.date_created OR deadPrograma.death_date<recepcao.date_created OR deadFichaResumo.death_date<recepcao.date_created OR deadFichaClinica.death_date<recepcao.date_created,DATE_FORMAT(recepcao.date_created, '%d-%m-%Y %H:%i:%s'),'') AS encounter_date_created_recepcao, "
            + " l.name AS location_name_fila, "
            + " recepcao.location_name AS location_name_recepcao "
            + " FROM "
            + " person pe "
            + " left join person peObito on pe.person_id = peObito.person_id and peObito.voided = 0 and peObito.death_date IS NOT NULL "
            + " left join "
            + " ( "
            + " SELECT pg.patient_id AS patient_id, ps.start_date As death_date "
            + " FROM patient p "
            + " INNER JOIN patient_program pg ON p.patient_id = pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id "
            + " WHERE p.voided = 0 AND pg.program_id = "
            + programId // 2
            + " AND pg.voided = 0 "
            + " AND ps.voided = 0 "
            + " AND ps.state = 10 "
            + " AND pg.location_id IN (:location) "
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND ps.start_date <= :endDate "
            + " ) deadPrograma on pe.person_id = deadPrograma.patient_id "
            + " left join "
            + " ( "
            + " SELECT p.patient_id AS patient_id, o.obs_datetime AS death_date "
            + " FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o.concept_id = "
            + stateOfStayPriorArtPatient // 6272
            + " AND o.value_coded = "
            + patientHasDiedConceptUuid // 1366
            + " AND e.encounter_type = "
            + masterCardEncounterType // 53
            + " AND e.location_id IN (:location) "
            + " AND o.obs_datetime <= :endDate"
            + " ) deadFichaResumo on pe.person_id = deadFichaResumo.patient_id "
            + " left join "
            + " ( "
            + " SELECT p.patient_id AS patient_id, o.obs_datetime AS death_date "
            + " FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o.concept_id = "
            + stateOfStayArtPatient // 6273
            + " AND value_coded = "
            + patientHasDiedConceptUuid // 1366
            + " AND e.encounter_type = "
            + sTarvAdultoSeguimentoEncounterTypeUuid // 6
            + " AND e.location_id IN (:location) "
            + " AND o.obs_datetime <= :endDate"
            + " ) deadFichaClinica on pe.person_id = deadFichaClinica.patient_id "
            + " left join "
            + " ( select pn1.* "
            + " from person_name pn1 "
            + " inner join "
            + " ( "
            + " select person_id, min(person_name_id) id "
            + " from person_name "
            + " where voided = 0 "
            + " group by person_id "
            + " ) pn2 "
            + " where pn1.person_id = pn2.person_id and pn1.person_name_id = pn2.id "
            + " ) pn on pn.person_id = pe.person_id "
            + " left join "
            + " ( select pid1.* "
            + " from patient_identifier pid1 "
            + " inner join "
            + " ( "
            + " select patient_id, min(patient_identifier_id) id "
            + " from patient_identifier "
            + " where voided = 0 "
            + " group by patient_id "
            + " ) pid2 "
            + " where pid1.patient_id = pid2.patient_id and pid1.patient_identifier_id = pid2.id "
            + " ) pid on pid.patient_id = pe.person_id "
            + " left join location l on l.location_id = pid.location_id "
            + " left join "
            + " ( "
            + " Select p.patient_id, e.encounter_datetime, l.name  location_name, e.date_created "
            + " from patient p "
            + " inner join encounter e on p.patient_id = e.patient_id "
            + " inner join location l on l.location_id = e.location_id "
            + " where 	p.voided = 0 and e.voided = 0 and e.encounter_type = "
            + encounterType // 18
            + " and e.location_id IN (:location) "
            + " AND e.encounter_datetime between :startDate AND :endDate"
            + " ) fila 	on fila.patient_id = pe.person_id "
            + " left join "
            + " ( "
            + " SELECT 	p.patient_id, o.value_datetime encounter_datetime, l.name location_name, e.date_created "
            + " FROM 	patient p "
            + " INNER JOIN encounter e on p.patient_id = e.patient_id "
            + " inner join obs o on e.encounter_id = o.encounter_id "
            + " inner join location l on l.location_id = e.location_id "
            + " where 	p.voided = 0 and e.voided = 0 and o.voided = 0 and e.encounter_type = "
            + masterCardDrugPickupEncounterTypeUuid // 52
            + " AND o.concept_id = "
            + preArtPickupDate // 23866
            + " and o.value_datetime is not null and e.location_id IN (:location) "
            + " AND o.value_datetime between :startDate AND :endDate"
            + " ) recepcao	on recepcao.patient_id = pe.person_id "
            + " left join  patient_program pg ON pe.person_id = pg.patient_id and pg.program_id = "
            + programId // 2
            + " and pg.location_id IN (:location) "
            + " left join  patient_state ps ON pg.patient_program_id = ps.patient_program_id and ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " where pe.voided = 0 and "
            + " ( "
            + " peObito.person_id is not null or "
            + " deadPrograma.patient_id is not null or "
            + " deadFichaResumo.patient_id is not null or "
            + " deadFichaClinica.patient_id is not null "
            + " ) and "
            + " ( "
            + " ( "
            + " fila.encounter_datetime>peObito.death_date or "
            + " fila.encounter_datetime>deadPrograma.death_date or "
            + " fila.encounter_datetime>deadFichaResumo.death_date or "
            + " fila.encounter_datetime>deadFichaClinica.death_date "
            + " ) "
            + " OR "
            + " ( "
            + " recepcao.encounter_datetime>peObito.death_date or "
            + " recepcao.encounter_datetime>deadPrograma.death_date or "
            + " recepcao.encounter_datetime>deadFichaResumo.death_date or "
            + " recepcao.encounter_datetime>deadFichaClinica.death_date "
            + " ) "
            + " ) "
            + " GROUP BY pe.person_id;";
    return query;
  }
}
