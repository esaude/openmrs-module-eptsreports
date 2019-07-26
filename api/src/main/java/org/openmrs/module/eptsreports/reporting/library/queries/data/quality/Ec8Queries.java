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
  public static String getEc8CombinedQuery(
      int identifierType,
      int programId,
      int stateId,
      int labEncounterType,
      int adultFollowUp,
      int childFollowUp) {
    String query =
        "SELECT patient_id, NID,Name,birthdate,Estimated_dob,Sex,First_entry_date,Last_updated,date_enrolled,state,state_date,lab_form_date,lab_form_date_created,MIN(clinical_form_date) AS clinical_form_date,clinical_form_date_created FROM("
            + " SELECT pa.patient_id, pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y') AS date_enrolled, case when ps.state = 9 then 'DROPPED FROM TREATMENT' when ps.state = 6 then 'ACTIVE ON PROGRAM' when ps.state = 10 then 'PATIENT HAS DIED' when ps.state = 8 then 'SUSPENDED TREATMENT' when ps.state = 7 then 'TRANSFERED OUT TO ANOTHER FACILITY' when ps.state = 29 then 'TRANSFERRED FROM OTHER FACILTY' end AS state, DATE_FORMAT(ps.start_date, '%d-%m-%Y') AS state_date, IF(e.encounter_type = '13', DATE_FORMAT(e.encounter_datetime, '%d-%m-%Y'), ' ') AS lab_form_date, IF(e.encounter_type = '13', DATE_FORMAT(e.date_created, '%d-%m-%Y %H:%i:%s'), ' ') AS lab_form_date_created, IF(e.encounter_type != '13', DATE_FORMAT(e.encounter_datetime, '%d-%m-%Y'), ' ') AS clinical_form_date, IF(e.encounter_type != '13', DATE_FORMAT(e.date_created, '%d-%m-%Y %H:%i:%s'), ' ') AS clinical_form_date_created FROM patient pa "
            + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
            + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " WHERE "
            + " pg.program_id="
            + programId
            + " AND e.voided=0 "
            + " AND pi.identifier_type="
            + identifierType
            + " AND e.encounter_type IN("
            + labEncounterType
            + ","
            + adultFollowUp
            + ","
            + childFollowUp
            + ")"
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND pa.patient_id IN("
            + " SELECT pg.patient_id AS patient_id "
            + " FROM patient p "
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id AND pg.voided=0 "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id AND e.location_id IN(:location) AND e.voided=0 "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id AND ps.voided=0 "
            + " WHERE p.voided=0 AND pg.program_id="
            + programId
            + " AND ps.state="
            + stateId
            + " AND e.encounter_type IN("
            + labEncounterType
            + ","
            + adultFollowUp
            + ","
            + childFollowUp
            + ") "
            + " AND pg.location_id IN(:location) "
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND e.encounter_datetime >= ps.start_date "
            + " GROUP BY pg.patient_id )) ec8 GROUP BY ec8.patient_id";
    return query;
  }
}
