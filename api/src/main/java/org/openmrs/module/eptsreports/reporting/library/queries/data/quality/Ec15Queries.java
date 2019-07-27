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

public class Ec15Queries {

  /**
   * Get the query for EC15 patient listing
   *
   * @return String
   */
  public static String getEc15CombinedQuery(int identifierType, int programId, int encounterType) {
    String query =
        "SELECT patient_id, NID, Name, birthdate, Estimated_dob, Sex, First_entry_date, Last_updated, MIN(encounter_date) AS encounter_date, encounter_date_created FROM("
            + " SELECT pa.patient_id, pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(e.encounter_datetime, '%d-%m-%Y %H:%i:%s') AS encounter_date, DATE_FORMAT(e.date_created, '%d-%m-%Y %H:%i:%s') AS encounter_date_created FROM patient pa "
            + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
            + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + "WHERE "
            + " pg.program_id="
            + programId
            + " AND e.voided=0 "
            + " AND pi.identifier_type="
            + identifierType
            + " AND e.encounter_type="
            + encounterType
            + " AND pe.birthdate IS NOT NULL"
            + " AND e.location_id IN(:location) AND pa.voided = 0 and e.voided=0 "
            + " AND pe.birthdate > e.encounter_datetime"
            + ")f_ec15 GROUP BY f_ec15.patient_id";
    return query;
  }
}
