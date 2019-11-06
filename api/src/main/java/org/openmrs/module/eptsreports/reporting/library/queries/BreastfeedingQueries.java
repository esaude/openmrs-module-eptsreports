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

public class BreastfeedingQueries {

  public static String getPatientsWhoGaveBirthWithinReportingPeriod(
      final int etvProgram, final int patientState) {
    return "select 	pg.patient_id"
        + " from patient p"
        + " inner join patient_program pg on p.patient_id=pg.patient_id"
        + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id"
        + " where pg.voided=0 and ps.voided=0 and p.voided=0 and"
        + " pg.program_id="
        + etvProgram
        + " and ps.state="
        + patientState
        + " and ps.end_date is null and"
        + " ps.start_date between :startDate and :endDate and location_id=:location";
  }

  public static String getLactatingPatients(
      final int breastFeedingConcept,
      final int yesValueConcept,
      final int adultFollowupEncounterType) {
    final String qry =
        "select max_visit.person_id "
            + "from ("
            + "select person_id, MAX(encounter_datetime) as edt "
            + "from obs "
            + "inner join encounter on obs.encounter_id = encounter.encounter_id "
            + "where obs.voided = false and obs.concept_id = %s  and obs.value_coded = %s "
            + "and encounter.encounter_type = %s "
            + "and obs.location_id in (:location) "
            + "and encounter.encounter_datetime >= :startDate "
            + "and encounter.encounter_datetime <= :endDate "
            + "group by person_id "
            + ") max_visit";

    return String.format(qry, breastFeedingConcept, yesValueConcept, adultFollowupEncounterType);
  }

  public static String getLactatingPatientsStartingART(
      final int artStartConcept,
      final int breastFeedingConcept,
      final int adultFollowupEncounterType) {
    final String qry =
        "select max_visit.person_id "
            + "from ("
            + "select person_id, MIN(encounter_datetime) as edt "
            + "from obs "
            + "inner join encounter on obs.encounter_id = encounter.encounter_id "
            + "where obs.voided = false and obs.concept_id = %s  and obs.value_coded = %s "
            + "and encounter.encounter_type = %s "
            + "and obs.location_id in (:location) "
            + "and encounter.encounter_datetime >= :startDate "
            + "and encounter.encounter_datetime <= :endDate "
            + "group by person_id "
            + ") max_visit";

    return String.format(qry, artStartConcept, breastFeedingConcept, adultFollowupEncounterType);
  }

  public static String findPatientsWhoAreBreastfeeding() {

    final String query =
        "SELECT lactante_real.patient_id FROM ( "
            + "SELECT p.patient_id, o.value_datetime data_parto FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=5599 "
            + "AND e.encounter_type IN (5,6) AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location "
            + "UNION "
            + "SELECT p.patient_id, e.encounter_datetime data_parto FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=6332 AND value_coded=1065 "
            + "AND e.encounter_type in (6,53) and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
            + "UNION "
            + "SELECT p.patient_id, e.encounter_datetime data_parto FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=6334 "
            + "AND value_coded=6332 AND e.encounter_type IN (5,6) AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location "
            + "UNION "
            + "SELECT pg.patient_id, ps.start_date data_parto FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id=8 "
            + "AND ps.state=27 AND ps.end_date IS NULL AND ps.start_date BETWEEN :startDate AND :endDate AND location_id=:location ) lactante_real "
            + "INNER JOIN person ON lactante_real.patient_id=person.person_id WHERE person.gender='F'";

    return query;
  }
}
