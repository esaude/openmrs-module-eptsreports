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

public class Ec2Queries {
  /** Get the combined query for EC2 patient listing */
  public static String getEc2CombinedQuery(
      int deliveryDateConcept,
      int arvInitiationConcept,
      int lactationConcept,
      int yesConcept,
      int ptvProgram,
      int gaveBirthState,
      int adultInitialEncounter,
      int adultSegEncounter) {
    return "SELECT patient_id, NID, Name, birthdate, estimated_dob, Sex, first_entry_date, last_updated, criteria, encounter_date, date_enrolled, state, breastfeeding.location_name FROM("
        + " SELECT brest_final.patient_id, pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS estimated_dob, pe.gender AS Sex, DATE_FORMAT(pe.date_created, '%d-%m-%Y %H:%i:%s') AS first_entry_date, DATE_FORMAT(pe.date_changed, '%d-%m-%Y %H:%i:%s') AS last_updated ,criteria, DATE_FORMAT(encounter_date, '%d-%m-%Y') AS encounter_date, brest_final.location_name AS location_name FROM( "
        + "SELECT brest.patient_id AS patient_id, brest.criteria AS criteria, MAX(brest.encounter_date) AS encounter_date, brest.location_name AS location_name FROM ("
        + "SELECT p.patient_id, 'BC1' AS criteria, e.encounter_datetime AS encounter_date, l.name AS location_name "
        + " FROM patient p "
        + " INNER JOIN person pe ON p.patient_id=pe.person_id "
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + " INNER JOIN location l ON e.location_id=l.location_id "
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + deliveryDateConcept
        + " AND e.encounter_type in ("
        + adultInitialEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + " AND pe.gender ='M'"
        + " UNION "
        + " SELECT  p.patient_id, 'BC2' AS criteria, e.encounter_datetime AS encounter_date, l.name AS location_name "
        + " FROM patient p "
        + " INNER JOIN person pe ON p.patient_id=pe.person_id "
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + " INNER JOIN location l ON e.location_id=l.location_id "
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + arvInitiationConcept
        + " AND value_coded="
        + lactationConcept
        + " AND e.encounter_type IN ("
        + adultInitialEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + " AND pe.gender ='M'"
        + " UNION "
        + " SELECT p.patient_id, 'BC3' AS criteria, e.encounter_datetime AS encounter_date, l.name AS location_name "
        + " FROM patient p "
        + " INNER JOIN person pe ON p.patient_id=pe.person_id "
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + " INNER JOIN location l ON e.location_id=l.location_id "
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + lactationConcept
        + " AND value_coded="
        + yesConcept
        + " AND "
        + "e.encounter_type IN ("
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + " AND pe.gender ='M'"
        + " UNION "
        + " SELECT pg.patient_id, 'BC4' AS criteria, pg.date_enrolled AS encounter_date, l.name AS location_name "
        + " FROM patient p "
        + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
        + " INNER JOIN person pe ON pg.patient_id=pe.person_id "
        + " INNER JOIN location l ON pg.location_id=l.location_id "
        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
        + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND "
        + "pg.program_id="
        + ptvProgram
        + " AND ps.state="
        + gaveBirthState
        + " AND ps.end_date IS NULL AND "
        + " pg.location_id IN(:location) "
        + " AND pe.gender ='M') brest GROUP BY brest.patient_id"
        + ") brest_final "
        + " INNER JOIN patient_identifier pi ON brest_final.patient_id=pi.patient_id "
        + " INNER JOIN person_name pn ON brest_final.patient_id=pn.person_id "
        + " INNER JOIN person pe ON brest_final.patient_id=pe.person_id "
        + ") breastfeeding "
        + " LEFT JOIN "
        + " (SELECT pa.patient_id AS patientId, DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y') AS date_enrolled,"
        + " case when ps.state = 25 then 'PREGNANT' when ps.state = 26 then 'PREGNANCY TERMINATION'"
        + " when ps.state = 27 then 'DELIVERY' end AS state FROM patient pa "
        + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
        + " WHERE "
        + " pg.program_id="
        + ptvProgram
        + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL GROUP BY pa.patient_id "
        + ") prog ON breastfeeding.patient_id=prog.patientId ";
  }
}
