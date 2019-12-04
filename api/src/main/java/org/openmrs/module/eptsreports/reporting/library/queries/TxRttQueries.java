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

public class TxRttQueries {

  /**
   * For each occurred consultation (Ficha de Seguimento Adulto, Pediatria or Ficha Clinica) of
   * Encounter Type Ids 6 or 9 during the reporting period, the occurred encounter date minus the
   * previous scheduled consultation date (Concept ID 1410 value_datetime) from the previous
   * consultation of Encounter Type Ids 6 or 9, is greater than 28 days
   *
   * @return String
   */
  public static String getPatientsHavingConsultationAfter28DaysPriorToPreviousConsultation(
      int adultoSeg, int padiatSeg, int conceptid) {
    String query =
        "SELECT e.patient_id "
            + " FROM ("
            + " SELECT e.encounter_id,  p.patient_id, "
            + " ("
            + " SELECT oo.value_datetime "
            + " FROM encounter ee "
            + " INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + " WHERE "
            + " ee.voided = 0 AND "
            + " oo.voided = 0 AND "
            + " ee.encounter_type IN (%d, %d) AND "
            + " oo.concept_id = %d AND "
            + " ee.patient_id = e.patient_id AND "
            + " ee.encounter_datetime < e.encounter_datetime AND "
            + " ee.location_id = :location AND "
            + " ee.encounter_datetime <= :endDate "
            + " ORDER BY ee.encounter_datetime DESC LIMIT 1) AS prev_scheduled_date, e.encounter_datetime "
            + " FROM	patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " WHERE "
            + " p.voided = 0 AND "
            + " e.voided = 0 AND "
            + " e.encounter_type IN (%d, %d) AND "
            + " e.location_id = :location "
            + " ) e "
            + " WHERE "
            + " TIMESTAMPDIFF(DAY, e.prev_scheduled_date, e.encounter_datetime) > 28 "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate ";

    return String.format(query, adultoSeg, padiatSeg, conceptid, adultoSeg, padiatSeg);
  }

  /**
   * For each occurred drug pick ups of Encounter Types Ids 18 or 52 during the reporting period,
   * the occurred encounter date minus the previous scheduled drug pick up date (as the most recent
   * between previous encounter type 18 scheduled date (Concept ID 1410 value_datetime) and the
   * previous encounter type 52 pick up date (Concept ID 23866 value_datetime +30 days)), is greater
   * than 28 days
   *
   * @return String
   */
  public static String
      getAllPatientsWhoMissedDrugPickupHavingPreviousMasterCardAppointment30DaysWhichIs28DaysLaterThanEncounterDate(
          int pharmacyEncounter,
          int masterCardEncounter,
          int nextAppointmentConcept,
          int dateOfArtPickupConcept,
          int adultoSeg,
          int paedSeg) {
    String query =
        " SELECT e.patient_id "
            + " FROM ("
            + " SELECT e.encounter_id, p.patient_id,("
            + " SELECT "
            + " CASE WHEN ee.encounter_type = %d THEN "
            + " oo.value_datetime "
            + " ELSE "
            + " DATE_ADD(oo.value_datetime, INTERVAL 30 DAY) "
            + " END AS next_schedule_date "
            + " FROM encounter ee "
            + " INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + " WHERE "
            + " ee.voided = 0 AND "
            + " oo.voided = 0 AND "
            + "("
            + " (ee.encounter_type = %d AND oo.concept_id = %d) OR "
            + " (ee.encounter_type = %d AND oo.concept_id = %d) "
            + ") AND "
            + " ee.patient_id = e.patient_id AND "
            + " ee.encounter_datetime < e.encounter_datetime AND "
            + " ee.location_id = :location AND "
            + " ee.encounter_datetime <= :endDate "
            + " ORDER BY ee.encounter_datetime DESC LIMIT 1 "
            + ") AS prev_scheduled_date, "
            + " e.encounter_datetime "
            + " FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " WHERE "
            + " p.voided = 0 AND "
            + " e.voided = 0 AND "
            + " e.encounter_type IN (%d, %d) AND "
            + " e.location_id = :location "
            + ") e "
            + " WHERE "
            + " TIMESTAMPDIFF(DAY, e.prev_scheduled_date, e.encounter_datetime) > 28 "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate";
    return String.format(
        query,
        pharmacyEncounter,
        pharmacyEncounter,
        nextAppointmentConcept,
        masterCardEncounter,
        dateOfArtPickupConcept,
        adultoSeg,
        paedSeg);
  }
}
