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
   * Check each encounter against the previous scheduled date of drug pickup AND consultations to be
   * greater than 28 days. Pick any of the encounters which occurred 28 dyas after the previously
   * scheduled consultation or drug pickup.
   *
   * @param adultoSegEncounter
   * @param pediatriaSegEncounter
   * @param pharmacyEncounter
   * @param masterCardEncounter
   * @param consultationConcept
   * @param nextDrugPickupConcept
   * @param dateOfArtPickupConcept
   * @return String
   */
  public static String getAllPatientsWhoMissedPreviousAppointmentBy28Days(
      int adultoSegEncounter,
      int pediatriaSegEncounter,
      int pharmacyEncounter,
      int masterCardEncounter,
      int consultationConcept,
      int nextDrugPickupConcept,
      int dateOfArtPickupConcept) {
    String query =
        "SELECT e.patient_id FROM "
            + " (SELECT"
            + "     e.encounter_id, "
            + "     p.patient_id, "
            + "     ("
            + "       SELECT oo.value_datetime "
            + "       FROM encounter ee "
            + "         INNER JOIN obs oo ON "
            + "           ee.encounter_id = oo.encounter_id "
            + "       WHERE "
            + "         ee.voided = 0 "
            + "         AND oo.voided = 0 "
            + "         AND ee.encounter_type IN (%d,%d) "
            + "         AND oo.concept_id = %d "
            + "         AND ee.patient_id = e.patient_id "
            + "         AND ee.encounter_datetime < e.encounter_datetime "
            + "         AND ee.location_id = :location "
            + "         AND ee.encounter_datetime <= :endDate "
            + "       ORDER BY ee.encounter_datetime DESC "
            + "       LIMIT 1) AS prev_consultation_scheduled_date, "
            + "     (SELECT "
            + "       CASE WHEN ee.encounter_type = %d THEN oo.value_datetime "
            + "       ELSE DATE_ADD(oo.value_datetime,"
            + "       INTERVAL 30 DAY) END AS next_schedule_date "
            + "     FROM encounter ee "
            + "       INNER JOIN obs oo ON "
            + "         ee.encounter_id = oo.encounter_id "
            + "     WHERE "
            + "       ee.voided = 0 "
            + "       AND oo.voided = 0 "
            + "       AND ( "
            + "         (ee.encounter_type = %d	AND oo.concept_id = %d) "
            + "         OR (ee.encounter_type = %d AND oo.concept_id = %d) "
            + "       )"
            + "       AND ee.patient_id = e.patient_id "
            + "       AND ee.encounter_datetime < e.encounter_datetime "
            + "       AND ee.location_id = :location "
            + "       AND ee.encounter_datetime <= :endDate "
            + "     ORDER BY "
            + "       ee.encounter_datetime DESC "
            + "     LIMIT 1 ) AS prev_drug_pickup_scheduled_date, "
            + "     e.encounter_datetime "
            + "   FROM patient p "
            + "     INNER JOIN encounter e ON "
            + "       e.patient_id = p.patient_id "
            + "   WHERE "
            + "     p.voided = 0 "
            + "     AND e.voided = 0 "
            + "     AND e.encounter_type IN (%d,%d,%d,%d) "
            + "     AND e.location_id = :location ) e "
            + " WHERE "
            + "   TIMESTAMPDIFF(DAY,e.prev_consultation_scheduled_date,e.encounter_datetime) > 28 AND "
            + "   TIMESTAMPDIFF(DAY,e.prev_drug_pickup_scheduled_date,e.encounter_datetime) > 28 AND "
            + "   e.encounter_datetime BETWEEN :startDate AND :endDate "
            + " GROUP BY patient_id";
    return String.format(
        query,
        adultoSegEncounter,
        pediatriaSegEncounter,
        consultationConcept,
        pharmacyEncounter,
        pharmacyEncounter,
        nextDrugPickupConcept,
        masterCardEncounter,
        dateOfArtPickupConcept,
        adultoSegEncounter,
        pediatriaSegEncounter,
        pharmacyEncounter,
        masterCardEncounter);
  }
}
