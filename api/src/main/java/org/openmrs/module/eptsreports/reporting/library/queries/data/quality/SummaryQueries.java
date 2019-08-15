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

/** This class will contain the queries for the summary indicator page That aggregate them all */
public class SummaryQueries {

  /** GRAVIDAS INSCRITAS NO SERVIÇO TARV */
  public static String getPregnantPatients(
      int pregnantConcept,
      int gestationConcept,
      int weeksPregnantConcept,
      int eddConcept,
      int adultInitailEncounter,
      int adultSegEncounter,
      int etvProgram) {

    String query =
        "SELECT  p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND value_coded=%d"
            + " AND e.encounter_type IN (%d, %d)"
            + " AND e.location_id IN(:location)"
            + " AND pe.gender ='M'"
            + " UNION"
            + " SELECT p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND"
            + " e.encounter_type IN (%d, %d)"
            + " AND e.location_id IN(:location) "
            + " AND pe.gender ='M'"
            + " UNION"
            + " SELECT p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND"
            + " e.encounter_type in (%d, %d) "
            + " AND e.location_id IN(:location) "
            + " AND pe.gender ='M'"
            + " UNION"
            + " SELECT pp.patient_id FROM patient_program pp"
            + " INNER JOIN person pe ON pp.patient_id=pe.person_id"
            + " WHERE pp.program_id=%d"
            + " AND pp.voided=0 AND pp.location_id IN(:location) "
            + " AND pe.gender ='M'";
    return String.format(
        query,
        pregnantConcept,
        gestationConcept,
        adultInitailEncounter,
        adultSegEncounter,
        weeksPregnantConcept,
        adultInitailEncounter,
        adultSegEncounter,
        eddConcept,
        adultInitailEncounter,
        adultSegEncounter,
        etvProgram);
  }

  /**
   * Get the raw sql query for breastfeeding male patients
   *
   * @return String
   */
  public static String getBreastfeedingMalePatients(
      int deliveryDateConcept,
      int arvInitiationConcept,
      int lactationConcept,
      int registeredBreastfeedingConcept,
      int yesConcept,
      int ptvProgram,
      int gaveBirthState,
      int adultInitialEncounter,
      int adultSegEncounter) {

    String query =
        " SELECT p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND"
            + " e.encounter_type in (%d, %d)"
            + " AND e.location_id IN(:location) "
            + " AND pe.gender ='M'"
            + " UNION "
            + "SELECT  p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND value_coded=%d"
            + " AND e.encounter_type IN (%d, %d)"
            + " AND e.location_id IN(:location)"
            + " AND pe.gender ='M'"
            + " UNION "
            + " SELECT p.patient_id"
            + " FROM patient p"
            + " INNER JOIN person pe ON p.patient_id=pe.person_id"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=%d"
            + " AND value_coded=%d"
            + " AND"
            + " e.encounter_type IN (%d) "
            + " AND e.location_id IN(:location) "
            + " AND pe.gender ='M'"
            + "UNION "
            + "SELECT 	pg.patient_id"
            + " FROM patient p"
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
            + " INNER JOIN person pe ON pg.patient_id=pe.person_id"
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id"
            + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND"
            + " pg.program_id=%d"
            + " AND ps.state=%d"
            + " AND ps.end_date IS NULL AND"
            + " location_id IN(:location)"
            + " AND pe.gender ='M'";
    return String.format(
        query,
        deliveryDateConcept,
        adultInitialEncounter,
        adultSegEncounter,
        arvInitiationConcept,
        lactationConcept,
        adultInitialEncounter,
        adultSegEncounter,
        registeredBreastfeedingConcept,
        yesConcept,
        adultSegEncounter,
        ptvProgram,
        gaveBirthState);
  }

  /**
   * Get patients whose year of birth is before 1920
   *
   * @return String
   */
  public static String getPatientsWhoseYearOfBirthIsBeforeYear(int year) {
    String query =
        "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL AND YEAR(pe.birthdate) < %d ";
    return String.format(query, year);
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
    String query =
        "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL AND TIMESTAMPDIFF(YEAR, pe.birthdate, :endDate) > %d";
    return String.format(query, years);
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
        " SELECT pa.patient_id FROM patient pa "
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " WHERE pe.birthdate IS NOT NULL AND e.encounter_type IN(%s) "
            + " AND e.location_id IN(:location) AND pa.voided = 0 and e.voided=0 "
            + " AND pe.birthdate > e.encounter_datetime ";
    return String.format(query, str2);
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
        " SELECT pg.patient_id AS patient_id "
            + " FROM patient p "
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id  "
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE p.voided=0 AND pg.program_id=%d"
            + " AND ps.state=%d "
            + " AND pg.voided=0 "
            + " AND ps.voided=0 "
            + " AND e.encounter_type IN(%s) "
            + " AND pg.location_id IN(:location) "
            + " AND e.location_id IN(:location) AND e.voided=0 "
            + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL "
            + " AND e.encounter_datetime >= ps.start_date "
            + " GROUP BY pg.patient_id ";
    return String.format(query, programId, stateId, str2);
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
        " SELECT p.patient_id AS patientId "
            + " FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN person pe ON p.patient_id=pe.person_id "
            + " WHERE p.voided = 0 "
            + " AND e.encounter_type IN(%s) "
            + " AND e.location_id IN(:location) AND e.voided=0 "
            + " AND pe.voided=0 "
            + " AND pe.death_date IS NOT NULL "
            + " AND e.encounter_datetime >= pe.death_date "
            + " GROUP BY p.patient_id ";
    return String.format(query, encounters);
  }

  /**
   * The patients whose date of Encounter is before 1985
   *
   * @return String
   */
  public static String getPatientsWhoseEncounterIsBefore1985(List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String str2 = str1.replaceAll("]", "");
    String query =
        " SELECT pa.patient_id FROM patient pa "
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " WHERE pe.birthdate IS NOT NULL AND e.encounter_type IN(%s) "
            + " AND e.location_id IN(:location) AND pa.voided = 0 and e.voided=0 "
            + " AND e.encounter_datetime < '1985-01-01' ";
    return String.format(query, str2);
  }

  public static String getPatientsWithGivenEncounterList(List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String str2 = str1.replaceAll("]", "");
    String query =
        " SELECT pa.patient_id FROM patient pa "
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " WHERE pe.birthdate IS NOT NULL AND e.encounter_type IN(%s) "
            + " AND e.location_id IN(:location) AND pa.voided = 0 and e.voided=0 ";
    return String.format(query, str2);
  }

  public static String getPatientsEnrolledOnTARV(int programId) {
    String query =
        " SELECT pa.patient_id FROM patient pa "
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id"
            + " WHERE pe.birthdate IS NOT NULL  AND pg.program_id=%d"
            + " AND e.location_id IN(:location) AND pa.voided = 0 and e.voided=0 ";
    return String.format(query, programId);
  }
}
