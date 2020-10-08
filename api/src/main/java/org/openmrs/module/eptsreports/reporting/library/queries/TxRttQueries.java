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

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

public class TxRttQueries {

  /**
   * <b>Technical Specs</b>
   *
   * <p>Check each encounter against the previous scheduled date of drug pickup AND consultations to
   * be greater than 28 days. Pick any of the encounters which occurred 28 days after the previously
   * scheduled consultation or drug pickup.
   *
   * @return {@link String}
   */
  public static String getAllPatientsWhoMissedPreviousAppointmentBy28Days(
      int adultoSegEncounter,
      int pediatriaSegEncounter,
      int pharmacyEncounter,
      int masterCardEncounter,
      int consultationConcept,
      int nextDrugPickupConcept,
      int dateOfArtPickupConceptMasterCard) {
    String query =
        "SELECT  "
            + "    patient_id "
            + " FROM "
            + "    (SELECT "
            + "        e.encounter_id,"
            + "            p.patient_id,"
            + "            (SELECT "
            + "                    MAX(oo.value_datetime)"
            + "                FROM"
            + "                    encounter ee"
            + "                JOIN obs oo ON ee.encounter_id = oo.encounter_id"
            + "                WHERE"
            + "                    ee.encounter_datetime = (SELECT "
            + "                            ee.encounter_datetime"
            + "                        FROM"
            + "                            encounter ee"
            + "                        WHERE"
            + "                            DATE(IF(e.encounter_type = ${masterCardEncounter}, o.value_datetime, e.encounter_datetime)) > DATE(ee.encounter_datetime)"
            + "                                AND ee.voided = 0"
            + "                                AND ee.encounter_type IN (${adultoSegEncounter} , ${pediatriaSegEncounter})"
            + "                                AND ee.patient_id = e.patient_id"
            + "                                AND ee.location_id = :location"
            + "                                AND ee.encounter_datetime <= :endDate"
            + "                        ORDER BY ee.encounter_datetime DESC"
            + "                        LIMIT 1)"
            + "                        AND ee.patient_id = e.patient_id"
            + "                        AND oo.concept_id = ${consultationConcept}"
            + "                        AND ee.encounter_type IN (${adultoSegEncounter} , ${pediatriaSegEncounter}) AND oo.voided=0) AS prev_consultation_scheduled_date,"
            + "            (SELECT "
            + "                    MAX(oo.value_datetime)"
            + "                FROM"
            + "                    encounter ee"
            + "                JOIN obs oo ON ee.encounter_id = oo.encounter_id"
            + "                WHERE"
            + "                    ee.encounter_datetime = (SELECT "
            + "                            ee.encounter_datetime"
            + "                        FROM"
            + "                            encounter ee"
            + "                        WHERE"
            + "                            DATE(IF(e.encounter_type = ${masterCardEncounter}, o.value_datetime, e.encounter_datetime)) > DATE(ee.encounter_datetime)"
            + "                                AND ee.voided = 0"
            + "                                AND ee.encounter_type = ${pharmacyEncounter}"
            + "                                AND ee.patient_id = e.patient_id"
            + "                                AND ee.location_id = :location"
            + "                                AND ee.encounter_datetime <= :endDate"
            + "                        ORDER BY ee.encounter_datetime DESC"
            + "                        LIMIT 1)"
            + "                        AND ee.patient_id = e.patient_id"
            + "                        AND oo.concept_id = ${nextDrugPickupConcept}"
            + "                        AND ee.encounter_type = ${pharmacyEncounter} AND oo.voided=0) AS prev_drug_pickup_scheduled_date_fila,"
            + "            (SELECT "
            + "                    DATE_ADD(oo.value_datetime, INTERVAL 30 DAY) AS value_datetime"
            + "                FROM"
            + "                    encounter ee"
            + "                INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id"
            + "                WHERE"
            + "                    DATE(IF(e.encounter_type = ${masterCardEncounter}, o.value_datetime, e.encounter_datetime)) > DATE(oo.value_datetime)"
            + "                        AND ee.voided = 0"
            + "                        AND oo.voided = 0"
            + "                        AND ee.encounter_type = ${masterCardEncounter}"
            + "                        AND oo.concept_id = ${dateOfArtPickupConceptMasterCard}"
            + "                        AND ee.patient_id = e.patient_id"
            + "                        AND ee.location_id = :location"
            + "                        AND oo.value_datetime <= :endDate"
            + "                ORDER BY oo.value_datetime DESC"
            + "                LIMIT 1) AS prev_drug_pickup_scheduled_date_master_card,"
            + "            e.encounter_datetime,"
            + "            e.encounter_type,"
            + "            o.value_datetime"
            + "    FROM"
            + "        patient p"
            + "    INNER JOIN encounter e ON e.patient_id = p.patient_id"
            + "    JOIN obs o ON o.encounter_id = e.encounter_id"
            + "    WHERE"
            + "        p.voided = 0 AND e.voided = 0 AND o.voided=0"
            + "            AND e.location_id = :location"
            + "            AND ((e.encounter_type IN (${adultoSegEncounter} , ${pediatriaSegEncounter}, ${pharmacyEncounter})"
            + "            AND e.encounter_datetime BETWEEN :startDate AND :endDate)"
            + "            OR (e.encounter_type = ${masterCardEncounter}"
            + "            AND o.value_datetime BETWEEN :startDate AND :endDate"
            + "            AND o.concept_id = ${dateOfArtPickupConceptMasterCard})) "
            + " GROUP BY e.encounter_id,p.patient_id) e "
            + "WHERE "
            + "    (e.encounter_type =  ${masterCardEncounter} "
            + "        AND e.value_datetime BETWEEN :startDate AND :endDate "
            + "        AND TIMESTAMPDIFF(DAY, "
            + "        e.prev_consultation_scheduled_date, "
            + "        e.value_datetime) > 28 "
            + "        AND (CASE "
            + "        WHEN "
            + "            prev_drug_pickup_scheduled_date_fila IS NOT NULL "
            + "                AND prev_drug_pickup_scheduled_date_master_card IS NULL "
            + "        THEN "
            + "            TIMESTAMPDIFF(DAY, "
            + "                e.prev_drug_pickup_scheduled_date_fila, "
            + "                e.value_datetime) "
            + "        WHEN "
            + "            prev_drug_pickup_scheduled_date_fila IS NULL "
            + "                AND prev_drug_pickup_scheduled_date_master_card IS NOT NULL "
            + "        THEN "
            + "            TIMESTAMPDIFF(DAY, "
            + "                e.prev_drug_pickup_scheduled_date_master_card, "
            + "                e.value_datetime) "
            + "    END > 28 "
            + "        OR CASE "
            + "        WHEN "
            + "            prev_drug_pickup_scheduled_date_fila IS NOT NULL "
            + "                AND prev_drug_pickup_scheduled_date_master_card IS NOT NULL "
            + "        THEN "
            + "            CASE "
            + "                WHEN "
            + "                    TIMESTAMPDIFF(DAY, "
            + "                        e.prev_drug_pickup_scheduled_date_fila, "
            + "                        e.value_datetime) > 28 "
            + "                        AND TIMESTAMPDIFF(DAY, "
            + "                        e.prev_drug_pickup_scheduled_date_master_card, "
            + "                        e.value_datetime) > 28 "
            + "                THEN "
            + "                    57 "
            + "                ELSE 45 "
            + "            END "
            + "    END > 56)) "
            + "        OR (e.encounter_type !=  ${masterCardEncounter} "
            + "        AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "        AND TIMESTAMPDIFF(DAY, "
            + "        e.prev_consultation_scheduled_date, "
            + "        e.encounter_datetime) > 28 "
            + "        AND ((CASE "
            + "        WHEN "
            + "            prev_drug_pickup_scheduled_date_fila IS NOT NULL "
            + "                AND prev_drug_pickup_scheduled_date_master_card IS NULL "
            + "        THEN "
            + "            TIMESTAMPDIFF(DAY, "
            + "                e.prev_drug_pickup_scheduled_date_fila, "
            + "                e.encounter_datetime) "
            + "        WHEN "
            + "            prev_drug_pickup_scheduled_date_fila IS NULL "
            + "                AND prev_drug_pickup_scheduled_date_master_card IS NOT NULL "
            + "        THEN "
            + "            TIMESTAMPDIFF(DAY, "
            + "                e.prev_drug_pickup_scheduled_date_master_card, "
            + "                e.encounter_datetime) "
            + "    END > 28 )"
            + "        OR ( CASE "
            + "        WHEN "
            + "            prev_drug_pickup_scheduled_date_fila IS NOT NULL "
            + "                AND prev_drug_pickup_scheduled_date_master_card IS NOT NULL "
            + "        THEN "
            + "            CASE "
            + "                WHEN "
            + "                    TIMESTAMPDIFF(DAY, "
            + "                        e.prev_drug_pickup_scheduled_date_fila, "
            + "                        e.encounter_datetime) > 28 "
            + "                        AND TIMESTAMPDIFF(DAY, "
            + "                        e.prev_drug_pickup_scheduled_date_master_card, "
            + "                        e.encounter_datetime) > 28 "
            + "                THEN "
            + "                    57 "
            + "                ELSE 45 "
            + "            END "
            + "    END > 56))) "
            + "GROUP BY patient_id";

    Map<String, Integer> map = new HashMap<>();
    map.put("adultoSegEncounter", adultoSegEncounter);
    map.put("pediatriaSegEncounter", pediatriaSegEncounter);
    map.put("pharmacyEncounter", pharmacyEncounter);
    map.put("masterCardEncounter", masterCardEncounter);
    map.put("consultationConcept", consultationConcept);
    map.put("nextDrugPickupConcept", nextDrugPickupConcept);
    map.put("dateOfArtPickupConceptMasterCard", dateOfArtPickupConceptMasterCard);

    StringSubstitutor sub = new StringSubstitutor(map);
    return sub.replace(query);
  }
}
