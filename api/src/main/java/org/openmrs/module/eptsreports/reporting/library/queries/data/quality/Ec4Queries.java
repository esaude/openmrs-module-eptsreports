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

public class Ec4Queries {

  /**
   * Get the query to be used to display the EC4 patient listing
   *
   * @return String
   */
  public static String getEc4CombinedQuery(
      int programId,
      int stateId,
      int adultFollowUp,
      int childFollowUp,
      int fichaResumo,
      int stateOfStayPriorArtPatient,
      int stateOfStayOfArtPatient,
      int patientHasDiedConcept) {
    String query =
        "SELECT 	pe.person_id As patient_id, "
            + "		pid.identifier AS NID, "
            + "		concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,''),' ',ifnull(pn.family_name,'')) AS Name, "
            + "		DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, "
            + "		IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, "
            + "		pe.gender AS Sex, DATE_FORMAT(pe.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, "
            + "		DATE_FORMAT(pe.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, "
            + "		DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y') AS date_enrolled, "
            + "		case "
            + "			when ps.state = 9 then 'DROPPED FROM TREATMENT' "
            + "			when ps.state = 6 then 'ACTIVE ON PROGRAM' "
            + "			when ps.state = 10 then 'PATIENT HAS DIED' "
            + "			when ps.state = 8 then 'SUSPENDED TREATMENT' "
            + "			when ps.state = 7 then 'TRANSFERED OUT TO ANOTHER FACILITY' "
            + "			when ps.state = 29 then 'TRANSFERRED FROM OTHER FACILTY' "
            + "		end AS state, "
            + "		DATE_FORMAT(ps.start_date, '%d-%m-%Y') AS state_date, "
            + "		DATE_FORMAT(pe.death_date,'%d-%m-%Y') As death_date_demographic, "
            + "		DATE_FORMAT(deadFichaResumo.death_date,'%d-%m-%Y') As death_date_resumo, "
            + "		DATE_FORMAT(deadFichaClinica.death_date,'%d-%m-%Y') As death_date_clinica, "
            + "		MIN(DATE_FORMAT(seguimento.encounter_datetime, '%d-%m-%Y')) AS encounter_date, "
            + "		DATE_FORMAT(seguimento.date_created, '%d-%m-%Y %H:%i:%s') AS encounter_date_created, "
            + "	     seguimento.location_name "
            + "FROM "
            + "person pe "
            + "left join person peObito on pe.person_id = peObito.person_id and peObito.voided = 0 and peObito.death_date IS NOT NULL "
            + "left join "
            + "( "
            + "	 SELECT pg.patient_id AS patient_id, ps.start_date As death_date "
            + "	 FROM patient p "
            + "	 INNER JOIN patient_program pg ON p.patient_id = pg.patient_id "
            + "	 INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id "
            + "	 WHERE p.voided = 0 "
            + "	 AND pg.program_id = "
            + programId
            + "	 AND pg.voided = 0 "
            + "	 AND ps.voided = 0 "
            + "	 AND ps.state = "
            + stateId
            + "	 AND pg.location_id IN (:location) "
            + "	 AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + ") deadPrograma on pe.person_id = deadPrograma.patient_id "
            + "left join "
            + "( "
            + "	SELECT p.patient_id AS patient_id, o.obs_datetime AS death_date, e.encounter_datetime "
            + "	FROM patient p "
            + "	INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "	INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "	WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o.concept_id = "
            + stateOfStayPriorArtPatient
            + "	AND o.value_coded = "
            + patientHasDiedConcept
            + "	AND e.encounter_type = "
            + fichaResumo
            + "	AND e.location_id IN (:location) "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate"
            + ") deadFichaResumo on pe.person_id = deadFichaResumo.patient_id "
            + "left join "
            + "( "
            + "	SELECT p.patient_id AS patient_id, o.obs_datetime AS death_date, e.date_created, e.encounter_datetime "
            + "	FROM patient p "
            + "	INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "	INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "	WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o.concept_id = "
            + stateOfStayOfArtPatient
            + "	AND value_coded = "
            + patientHasDiedConcept
            + "	AND e.encounter_type = "
            + adultFollowUp
            + "	AND e.location_id IN (:location) "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + ") deadFichaClinica on pe.person_id = deadFichaClinica.patient_id "
            + "left join "
            + "(	select pn1.* "
            + "	from person_name pn1 "
            + "	inner join "
            + "	( "
            + "		select person_id, min(person_name_id) id "
            + "		from person_name "
            + "		where voided = 0 "
            + "		group by person_id "
            + "	) pn2 "
            + "	where pn1.person_id = pn2.person_id and pn1.person_name_id = pn2.id "
            + ") pn on pn.person_id = pe.person_id "
            + "left join "
            + "(   select pid1.* "
            + "	from patient_identifier pid1 "
            + "	inner join "
            + "	( "
            + "		select patient_id, min(patient_identifier_id) id "
            + "		from patient_identifier "
            + "		where voided = 0 "
            + "		group by patient_id "
            + "	) pid2 "
            + "	where pid1.patient_id = pid2.patient_id and pid1.patient_identifier_id = pid2.id "
            + ") pid on pid.patient_id = pe.person_id "
            + "left join "
            + "( "
            + "	Select p.patient_id, e.encounter_datetime, l.name  location_name, e.date_created "
            + "	from patient p "
            + "			inner join encounter e on p.patient_id = e.patient_id "
            + "			inner join location l on l.location_id = e.location_id "
            + "	where 	p.voided = 0 and e.voided = 0 and e.encounter_type in ("
            + childFollowUp
            + ","
            + adultFollowUp
            + ")and e.location_id IN (:location) "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + ") seguimento on seguimento.patient_id = pe.person_id "
            + "left join  patient_program pg ON pe.person_id = pg.patient_id and pg.program_id = "
            + programId
            + " and pg.location_id IN (:location) "
            + "left join  patient_state ps ON pg.patient_program_id = ps.patient_program_id and ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + "where 	pe.voided = 0 and "
            + "		( "
            + "			peObito.person_id is not null or "
            + "			deadPrograma.patient_id is not null or "
            + "			deadFichaResumo.patient_id is not null or "
            + "			deadFichaClinica.patient_id is not null "
            + "		) and "
            + " "
            + "			( "
            + "				seguimento.encounter_datetime>peObito.death_date or "
            + "				seguimento.encounter_datetime>deadPrograma.death_date or "
            + "				seguimento.encounter_datetime>deadFichaResumo.death_date or "
            + "				seguimento.encounter_datetime>deadFichaClinica.death_date "
            + "			) "
            + "GROUP BY pe.person_id; ";
    return query;
  }
}
