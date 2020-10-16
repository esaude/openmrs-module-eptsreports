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

import java.util.List;

public class Ec3Queries {

  /**
   * Get the combine query for EC3 patient listing report
   *
   * @return String
   */
  public static String getEc3CombinedQuery(
      int programId, int stateId, int encounterType, List<Integer> stateIds) {
    String states = (String.valueOf(stateIds).replaceAll("\\[", "")).replaceAll("]", "");
    return "SELECT dd.patient_id As patient_id, pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pe.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pe.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y') AS date_enrolled, case when ps.state = 9 then 'DROPPED FROM TREATMENT' when ps.state = 6 then 'ACTIVE ON PROGRAM' when ps.state = 10 then 'PATIENT HAS DIED' when ps.state = 8 then 'SUSPENDED TREATMENT' when ps.state = 7 then 'TRANSFERED OUT TO ANOTHER FACILITY' when ps.state = 29 then 'TRANSFERRED FROM OTHER FACILTY' end AS state, DATE_FORMAT(ps.start_date, '%d-%m-%Y') AS state_date, DATE_FORMAT(pe.death_date,'%d-%m-%Y') As death_date, MIN(DATE_FORMAT(e.encounter_datetime, '%d-%m-%Y')) AS encounter_date, DATE_FORMAT(e.date_created, '%d-%m-%Y %H:%i:%s') AS encounter_date_created, dd.location_name AS location_name FROM ("
        + " SELECT pg.patient_id AS patient_id, ps.start_date As death_date, l.name AS location_name "
        + " FROM patient p "
        + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + " INNER JOIN location l ON e.location_id=l.location_id "
        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
        + " WHERE p.voided=0 AND pg.program_id= "
        + programId
        + " AND pg.voided=0 "
        + " AND ps.voided=0 "
        + " AND ps.state="
        + stateId
        + " AND e.encounter_type="
        + encounterType
        + " AND pg.location_id IN(:location) "
        + " AND e.location_id IN(:location) AND e.voided=0 "
        + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
        + " AND e.encounter_datetime >= ps.start_date "
        + " UNION "
        + " SELECT p.patient_id AS patient_id, pe.death_date AS death_date, l.name AS location_name "
        + " FROM patient p "
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id AND e.location_id IN(:location) AND e.voided=0 "
        + " INNER JOIN location l ON e.location_id=l.location_id "
        + " INNER JOIN person pe ON p.patient_id=pe.person_id AND pe.voided=0 "
        + " WHERE p.voided = 0 "
        + " AND e.encounter_type= "
        + encounterType
        + " AND pe.death_date IS NOT NULL "
        + " AND e.encounter_datetime >= pe.death_date "
        + ") dd "
        + " INNER JOIN patient_identifier pi ON dd.patient_id=pi.patient_id "
        + " INNER JOIN person pe ON dd.patient_id=pe.person_id "
        + " INNER JOIN person_name pn ON dd.patient_id=pn.person_id "
        + " INNER JOIN patient_program pg ON dd.patient_id=pg.patient_id "
        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
        + " INNER JOIN encounter e ON dd.patient_id=e.patient_id "
        + " WHERE "
        + " pg.program_id="
        + programId
        + " AND e.voided=0 "
        + " AND e.encounter_type ="
        + encounterType
        + " AND pg.location_id IN(:location)"
        + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
        + " AND ps.state IN("
        + states
        + ") "
        + " AND e.encounter_datetime >= dd.death_date "
        + " GROUP BY dd.patient_id";
  }
}
