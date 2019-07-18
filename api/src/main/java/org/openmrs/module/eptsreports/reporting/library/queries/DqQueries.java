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
   * @return String
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
            + " AND pg.location_id IN(:location) AND ps.end_date IS NULL GROUP BY pg.patient_id) states INNER JOIN "
            + "(SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime) AS encounter_date FROM "
            + "patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided = 0 and e.voided=0 "
            + "AND e.encounter_type IN (%s) AND e.location_id IN(:location) GROUP BY p.patient_id"
            + ") encounter ON states.patient_id=encounter.patient_id) WHERE encounter.encounter_date > states.start_date";
    return String.format(query, programId, stateId, str2);
  }

  /**
   * Get patients whose year of birth is before 1920
   *
   * @return String
   */
  public static String getPatientsWhoseYearOfBirthIsBeforeYear(int year) {
    ;
    return "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL AND YEAR(pe.birthdate) <"
        + year;
  }

  /**
   * Get patients whose date of birth, estimated date of birth or age is negative
   *
   * @return String
   */
  public static String getPatientsWithNegativeBirthDates() {
    return "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NULL"
        + " UNION "
        + "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL AND pe.birthdate > pe.date_created";
  }

  /**
   * The patients birth, estimated date of birth or age indicates they are > 100 years of age
   *
   * @return String
   */
  public static String getPatientsWithMoreThanXyears(int years) {
    return "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL AND TIMESTAMPDIFF(YEAR, pe.birthdate, :endDate) >"
        + years;
  }

  /**
   * The patient’s date of birth is after any drug pick up date
   *
   * @return String
   */
  public static String getPatientsWhoseBirthdateIsAfterDrugPickup(List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String str2 = str1.replaceAll("]", "");
    String query =
        "SELECT birth_date.patient_id FROM "
            + "((SELECT pa.patient_id, pe.birthdate AS birthdate FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL) birth_date "
            + "INNER JOIN "
            + "(SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime) AS encounter_date FROM "
            + "patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided = 0 and e.voided=0 "
            + "AND e.encounter_type IN (%s) AND e.location_id IN(:location) GROUP BY p.patient_id "
            + ") encounter ON birth_date.patient_id=encounter.patient_id) WHERE birth_date.birthdate > encounter.encounter_date";
    return String.format(query, str2);
  }

  /**
   * Get patients who are marked as deceased and have a consultation after deceased date
   *
   * @return String
   */
  public static String getPatientsMarkedAsDeceasedAndHaveAnEncounter(List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String encounters = str1.replaceAll("]", "");
    String query =
        "SELECT demo.patient_id FROM "
            + "((SELECT p.patient_id AS patient_id, pe.death_date AS death_date "
            + "FROM patient p "
            + "INNER JOIN person pe ON p.patient_id=pe.person_id "
            + "WHERE pe.death_date IS NOT NULL GROUP BY p.patient_id) demo INNER JOIN "
            + "(SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime) AS encounter_date FROM "
            + "patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided = 0 and e.voided=0 "
            + "AND e.encounter_type IN (%s) AND e.location_id IN(:location) GROUP BY p.patient_id"
            + ") encounter ON demo.patient_id=encounter.patient_id) WHERE encounter.encounter_date >= demo.death_date";
    return String.format(query, encounters);
  }

  /**
   * Get the date when the patient was created in the system
   *
   * @return String
   */
  public static String getPatientDateCreated() {
    return "SELECT patient_id, DATE_FORMAT(date_created, '%d-%m-%Y') FROM patient";
  }

  /**
   * Get the date when the patient was changed
   *
   * @return String
   */
  public static String getPatientDateChanged() {
    return "SELECT patient_id, DATE_FORMAT(date_changed, '%d-%m-%Y') FROM patient";
  }

  /**
   * Get the date when the patient died
   *
   * @return String
   */
  public static String getPatientDeathDate() {
    return "SELECT pa.patient_id, DATE_FORMAT(pe.death_date, '%d-%m-%Y') FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id";
  }

  /**
   * Get patient date of birth
   *
   * @return String
   */
  public static String getPatientBirthDate() {
    return "SELECT pa.patient_id, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id";
  }

  /**
   * Get patient gender
   *
   * @return String
   */
  public static String getPatientGender() {
    return "SELECT pa.patient_id, pe.gender FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id";
  }

  /**
   * Get whether the patient date of birth is exact or estimated
   *
   * @return String
   */
  public static String getExactOrEstimatedDateOfBirth() {
    return "SELECT pa.patient_id, pe.birthdate_estimated FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id";
  }

  /**
   * Get the query to be used to display the EC11 patient listing
   *
   * @return String
   */
  public static String getEc11CombinedQuery(
      int programId, int stateId, int labencounterType, int adultFollowUp, int childFollowUp) {
    String query =
        "SELECT DISTINCT(pa.patient_id), pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y %H:%i:%s') AS date_enrolled, ps.state AS state, DATE_FORMAT(ps.start_date, '%d-%m-%Y %H:%i:%s') AS state_date FROM patient pa "
            + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
            + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " WHERE "
            + " pg.program_id="
            + programId
            + " AND e.encounter_datetime > ps.start_date"
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND pa.patient_id IN("
            + " SELECT states.patient_id FROM "
            + " ((SELECT pg.patient_id AS patient_id, ps.start_date AS start_date "
            + " FROM patient p "
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id="
            + programId
            + " AND ps.state="
            + stateId
            + " AND pg.location_id IN(:location) AND ps.end_date is null GROUP BY pg.patient_id) states INNER JOIN "
            + " (SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime) AS encounter_date FROM "
            + " patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided = 0 and e.voided=0 "
            + " AND e.encounter_type IN("
            + labencounterType
            + ","
            + adultFollowUp
            + ","
            + childFollowUp
            + ")"
            + " AND e.location_id IN(:location) GROUP BY p.patient_id "
            + ") encounter ON states.patient_id=encounter.patient_id) WHERE encounter.encounter_date > states.start_date "
            + ")";
    return query;
  }

  /**
   * Get the query for EC15 patient listing
   *
   * @return String
   */
  public static String getEc15CombinedQuery(int programId, int encounterType) {
    String query =
        "SELECT DISTINCT(pa.patient_id), pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(e.encounter_datetime, '%d-%m-%Y %H:%i:%s') AS encounter_date FROM patient pa "
            + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
            + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + "WHERE "
            + " pg.program_id="
            + programId
            + " AND e.encounter_type="
            + encounterType
            + " AND pa.patient_id IN("
            + "SELECT birth_date.patient_id FROM "
            + "((SELECT pa.patient_id, pe.birthdate AS birthdate FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL) birth_date "
            + "INNER JOIN "
            + "(SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime) AS encounter_date FROM "
            + "patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided = 0 and e.voided=0 "
            + "AND e.encounter_type="
            + encounterType
            + " AND e.location_id IN(:location) GROUP BY p.patient_id "
            + ") encounter ON birth_date.patient_id=encounter.patient_id) WHERE birth_date.birthdate > encounter.encounter_date)";
    return query;
  }

  /**
   * Get the query for EC14 patient listing
   *
   * @return String
   */
  public static String getEc14CombinedQuery(int programId, int years) {
    String query =
        "SELECT DISTINCT(pa.patient_id), pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y %H:%i:%s') AS date_enrolled, ps.state AS state FROM patient pa "
            + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
            + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE "
            + " pg.program_id="
            + programId
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND pa.patient_id IN("
            + " SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " WHERE pe.birthdate IS NOT NULL AND TIMESTAMPDIFF(YEAR, pe.birthdate, :endDate) >"
            + years
            + ")";
    return query;
  }

  /**
   * Get the query for EC13 patient listing
   *
   * @return String
   */
  public static String getEc13CombinedQuery(int programId) {
    String query =
        "SELECT DISTINCT(pa.patient_id), pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y %H:%i:%s') AS date_enrolled, ps.state AS state FROM patient pa "
            + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
            + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE "
            + " pg.program_id="
            + programId
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND pa.patient_id IN("
            + " SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " WHERE pe.birthdate IS NULL "
            + " UNION "
            + " SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " WHERE pe.birthdate IS NOT NULL AND pe.birthdate > pe.date_created)";
    return query;
  }

  /**
   * Get the query for EC12 patient listing
   *
   * @return String
   */
  public static String getEc12CombinedQuery(int programId, int year) {
    String query =
        "SELECT DISTINCT(pa.patient_id), pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y %H:%i:%s') AS date_enrolled, ps.state AS state FROM patient pa "
            + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
            + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE "
            + " pg.program_id="
            + programId
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND pa.patient_id IN("
            + " SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " WHERE pe.birthdate IS NOT NULL AND YEAR(pe.birthdate) < "
            + year
            + ")";
    return query;
  }

  /**
   * Get the query to be used to display the EC10 patient listing
   *
   * @return String
   */
  public static String getEc10CombinedQuery(
      int programId, int stateId, int adultFollowUp, int childFollowUp) {
    String query =
        "SELECT DISTINCT(pa.patient_id), pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y %H:%i:%s') AS date_enrolled, ps.state AS state, DATE_FORMAT(ps.start_date, '%d-%m-%Y %H:%i:%s') AS state_date,DATE_FORMAT(e.encounter_datetime, '%d-%m-%Y %H:%i:%s') AS encounter_date, DATE_FORMAT(e.date_created, '%d-%m-%Y %H:%i:%s') AS encounter_date_created FROM patient pa "
            + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
            + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " WHERE "
            + " pg.program_id="
            + programId
            + " AND e.encounter_datetime > ps.start_date"
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND pa.patient_id IN("
            + " SELECT states.patient_id FROM "
            + " ((SELECT pg.patient_id AS patient_id, ps.start_date AS start_date "
            + " FROM patient p "
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id="
            + programId
            + " AND ps.state="
            + stateId
            + " AND pg.location_id IN(:location) AND ps.end_date is null GROUP BY pg.patient_id) states INNER JOIN "
            + " (SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime) AS encounter_date FROM "
            + " patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided = 0 and e.voided=0 "
            + " AND e.encounter_type IN("
            + adultFollowUp
            + ","
            + childFollowUp
            + ")"
            + " AND e.location_id IN(:location) GROUP BY p.patient_id "
            + ") encounter ON states.patient_id=encounter.patient_id) WHERE encounter.encounter_date > states.start_date "
            + ")";
    return query;
  }

  /**
   * Get the combine query for EC9 patient listing report
   *
   * @return String
   */
  public static String getEc9CombinedQuery(int programId, int stateId, int encounterType) {
    String query =
        "SELECT DISTINCT(pa.patient_id), pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y %H:%i:%s') AS date_enrolled, ps.state AS state, DATE_FORMAT(ps.start_date, '%d-%m-%Y %H:%i:%s') AS state_date, DATE_FORMAT(e.encounter_datetime, '%d-%m-%Y %H:%i:%s') AS encounter_date, DATE_FORMAT(e.date_created, '%d-%m-%Y %H:%i:%s') AS encounter_date_created FROM patient pa "
            + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
            + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " WHERE "
            + " pg.program_id="
            + programId
            + " AND e.encounter_datetime > ps.start_date"
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND pa.patient_id IN(SELECT states.patient_id FROM "
            + " ((SELECT pg.patient_id AS patient_id, ps.start_date AS start_date "
            + " FROM patient p "
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id="
            + programId
            + " AND ps.state="
            + stateId
            + " AND pg.location_id IN(:location) AND ps.end_date IS NULL GROUP BY pg.patient_id) states INNER JOIN "
            + " (SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime) AS encounter_date FROM "
            + " patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided = 0 and e.voided=0 "
            + " AND e.encounter_type="
            + encounterType
            + " AND e.location_id IN(:location) GROUP BY p.patient_id "
            + ") encounter ON states.patient_id=encounter.patient_id) WHERE encounter.encounter_date > states.start_date)";
    return query;
  }
}
