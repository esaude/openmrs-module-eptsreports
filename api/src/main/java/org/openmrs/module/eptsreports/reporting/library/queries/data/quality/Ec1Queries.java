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

public class Ec1Queries {

  /** Get the combined query for EC1 patient listing */
  public static String getEc1CombinedQuery(
      int pregnantConcept,
      int yesConcept,
      int weeksPregnantConcept,
      int eddConcept,
      int adultInitailEncounter,
      int adultSegEncounter,
      int etvProgram) {
    return "SELECT preg_final.patient_id, preg_final.NID, preg_final.Name, preg_final.birthdate, preg_final.estimated_dob, preg_final.sex, preg_final.first_entry_date, preg_final.last_updated, preg_final.criteria, preg_final.encounter_date, prog.date_enrolled, prog.state, preg_final.location_name FROM ("
        + " SELECT preg.patient_id AS patient_id, preg.NID AS NID, preg.Name As Name, preg.birthdate AS birthdate, preg.estimated_dob AS estimated_dob, preg.Sex AS sex, preg.first_entry_date AS first_entry_date, preg.last_updated AS last_updated, preg.criteria AS criteria, DATE_FORMAT(MAX(preg.encounter_date),'%d-%m-%Y') AS encounter_date, preg.location_name AS location_name  FROM ("
        + " SELECT  p.patient_id AS patient_id, pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS estimated_dob, pe.gender AS Sex, DATE_FORMAT(p.date_created, '%d-%m-%Y %H:%i:%s') AS first_entry_date, DATE_FORMAT(p.date_changed, '%d-%m-%Y %H:%i:%s') AS last_updated ,e.encounter_datetime AS encounter_date, 'PC1' AS criteria, l.name AS location_name "
        + " FROM patient p "
        + " INNER JOIN person pe ON p.patient_id=pe.person_id "
        + " INNER JOIN patient_identifier pi ON p.patient_id=pi.patient_id"
        + " INNER JOIN person_name pn ON p.patient_id=pn.person_id "
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + " INNER JOIN location l ON e.location_id=l.location_id "
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= "
        + pregnantConcept
        + " AND value_coded="
        + yesConcept
        + " AND e.encounter_type IN ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") "
        + " AND e.location_id IN(:location)"
        + " AND pe.gender ='M'"
        + " UNION "
        + " SELECT p.patient_id AS patient_id, pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS estimated_dob, pe.gender AS Sex, DATE_FORMAT(p.date_created, '%d-%m-%Y %H:%i:%s') AS first_entry_date, DATE_FORMAT(p.date_changed, '%d-%m-%Y %H:%i:%s') AS last_updated, e.encounter_datetime AS encounter_date, 'PC2' AS criteria, l.name AS location_name "
        + " FROM patient p "
        + " INNER JOIN person pe ON p.patient_id=pe.person_id "
        + " INNER JOIN patient_identifier pi ON p.patient_id=pi.patient_id"
        + " INNER JOIN person_name pn ON p.patient_id=pn.person_id "
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
        + " INNER JOIN location l ON e.location_id=l.location_id "
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= "
        + +weeksPregnantConcept
        + " AND "
        + " e.encounter_type IN ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ")"
        + " AND e.location_id IN(:location)"
        + " AND pe.gender ='M'"
        + " UNION "
        + " SELECT p.patient_id AS patient_id, pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS estimated_dob, pe.gender AS Sex, DATE_FORMAT(p.date_created, '%d-%m-%Y %H:%i:%s') AS first_entry_date, DATE_FORMAT(p.date_changed, '%d-%m-%Y %H:%i:%s') AS last_updated  ,e.encounter_datetime AS encounter_date, 'PC3' AS criteria, l.name AS location_name "
        + " FROM patient p "
        + " INNER JOIN person pe ON p.patient_id=pe.person_id "
        + " INNER JOIN patient_identifier pi ON p.patient_id=pi.patient_id "
        + " INNER JOIN person_name pn ON p.patient_id=pn.person_id "
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + " INNER JOIN location l ON e.location_id=l.location_id "
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + eddConcept
        + " AND "
        + " e.encounter_type in ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") "
        + " AND e.location_id IN(:location) "
        + " AND pe.gender ='M'"
        + " UNION "
        + " SELECT pp.patient_id AS patient_id, pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS estimated_dob, pe.gender AS Sex, DATE_FORMAT(pe.date_created, '%d-%m-%Y %H:%i:%s') AS first_entry_date, DATE_FORMAT(pe.date_changed, '%d-%m-%Y %H:%i:%s') AS last_updated ,pp.date_enrolled AS encounter_date, 'PC4' AS criteria, l.name AS location_name FROM patient_program pp "
        + " INNER JOIN person pe ON pp.patient_id=pe.person_id "
        + " INNER JOIN patient_identifier pi ON pp.patient_id=pi.patient_id "
        + " INNER JOIN person_name pn ON pp.patient_id=pn.person_id "
        + " INNER JOIN location l ON pp.location_id=l.location_id "
        + " WHERE pp.program_id= "
        + etvProgram
        + " AND pp.voided=0 AND pp.location_id IN(:location) "
        + " AND pe.gender ='M') preg GROUP BY preg.patient_id) preg_final "
        + " LEFT JOIN ("
        + " SELECT pa.patient_id AS patientId, DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y') AS date_enrolled, "
        + " case when ps.state = 25 then 'PREGNANT' when ps.state = 26 then 'PREGNANCY TERMINATION'"
        + " when ps.state = 27 then 'DELIVERY' end AS state FROM patient pa"
        + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
        + " WHERE "
        + " pg.program_id="
        + etvProgram
        + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL GROUP BY pa.patient_id"
        + ") prog ON preg_final.patient_id=prog.patientId";
  }
}
