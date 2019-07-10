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
package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.List;

public class DqQueries {

  /** GRAVIDAS INSCRITAS NO SERVIÇO TARV */
  public static String getPregnantPatients(
      int pregnantConcept,
      int gestationConcept,
      int weeksPregnantConcept,
      int eddConcept,
      int adultInitailEncounter,
      int adultSegEncounter,
      int etvProgram) {

    return "SELECT  p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + pregnantConcept
        + " AND value_coded="
        + gestationConcept
        + " AND e.encounter_type IN ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + " UNION"
        + " SELECT p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + weeksPregnantConcept
        + " AND"
        + " e.encounter_type IN ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + " UNION"
        + " SELECT p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + eddConcept
        + " AND"
        + " e.encounter_type in ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + " UNION"
        + " SELECT pp.patient_id FROM patient_program pp"
        + " INNER JOIN person pe ON pp.patient_id=pe.person_id"
        + " WHERE pp.program_id="
        + etvProgram
        + " AND pp.voided=0 AND pp.location_id IN(:location) ";
  }

  public static String getPatientsWhoGaveBirth(int etvProgram, int patientState) {
    return "SELECT 	pg.patient_id"
        + " FROM patient p"
        + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id"
        + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND"
        + " pg.program_id="
        + etvProgram
        + " AND ps.state="
        + patientState
        + " AND ps.end_date is null AND"
        + " location_id IN(:location)";
  }

  /**
   * Get patients who have a given state before an encounter
   *
   * @return CohortDefinition
   */
  public static String getPatientsWithStateThatIsBeforeAnEncounter(
      int programId, int stateId, List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String str2 = str1.replaceAll("]", "");
    String query =
        "SELECT states.patient_id FROM "
            + "((SELECT pg.patient_id AS patient_id, ps.start_date AS start_date "
            + "FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id IN (%s) "
            + " AND ps.state IN (%s) "
            + " AND pg.location_id=:location AND ps.end_date is null GROUP BY pg.patient_id) states INNER JOIN "
            + "(SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime) AS encounter_date FROM "
            + "patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided = 0 and e.voided=0 "
            + "AND e.encounter_type IN (%s) AND e.location_id=:location GROUP BY p.patient_id"
            + ") encounter ON states.patient_id=encounter.patient_id) WHERE encounter.encounter_date > states.start_date";
    return String.format(query, programId, stateId, str2);
  }
}
