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

public class ViralLoadQueries {

  /**
   * Patients with viral load suppression within 12 months
   *
   * @return String
   */
  public static String getPatientsWithViralLoadSuppression(
      int labEncounter,
      int adultSeguimentoEncounter,
      int pediatriaSeguimentoEncounter,
      int mastercardEncounter,
      int fsrEncounter,
      int vlConceptQuestion,
      int vlQualitativeConceptQuestion) {
    String query =
            "SELECT patient_id FROM( "
            +" SELECT comb.patient_id,comb.value_numeric,comb.value_coded, MAX(comb.data_carga) FROM( "
            +" SELECT ultima_carga.patient_id,ultima_carga.value_numeric,ultima_carga.value_coded,ultima_carga.data_carga FROM "
              + " (SELECT p.patient_id,o.value_numeric AS value_numeric,o.value_coded AS value_coded,MAX(o.obs_datetime) data_carga"
              + " FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id"
              + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
              + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN (%d, %d, %d, %d)"
              + " AND  o.concept_id IN(%d, %d) "
              + " AND (o.value_numeric IS NOT NULL OR o.value_coded IS NOT NULL) AND"
              + " e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND"
              + " e.location_id=:location GROUP BY p.patient_id"
              + ") ultima_carga"
                + " INNER JOIN obs ON obs.person_id=ultima_carga.patient_id AND obs.obs_datetime="
                + "ultima_carga.data_carga  WHERE obs.voided=0 AND obs.concept_id IN (%d, %d) "
                + " AND obs.location_id=:location AND"
                + " (obs.value_numeric IS NOT NULL OR obs.value_coded IS NOT NULL) "
              + " UNION "
              + " SELECT p.patient_id, o.value_numeric AS value_numeric,o.value_coded AS value_coded, MAX(o.obs_datetime) AS data_carga FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN "
              + " obs o ON e.encounter_id=o.encounter_id "
              + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND "
              + " e.encounter_type IN (%d) AND o.concept_id=%d AND o.value_numeric IS NOT NULL AND "
              + " o.obs_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND e.location_id=:location GROUP BY p.patient_id) comb WHERE "
            +"(value_numeric < 1000 OR value_coded IS NOT NULL) group by patient_id) fn ";

    return String.format(
        query,
        labEncounter,
        adultSeguimentoEncounter,
        pediatriaSeguimentoEncounter,
        fsrEncounter,
        vlConceptQuestion,
        vlQualitativeConceptQuestion,
        vlConceptQuestion,
        vlQualitativeConceptQuestion,
        mastercardEncounter,
        vlConceptQuestion);
  }

  /**
   * Patients having viral load within the 12 months period
   *
   * @return String
   */
  public static String getPatientsHavingViralLoadInLast12Months(
      int labEncounter,
      int adultSeguimentoEncounter,
      int pediatriaSeguimentoEncounter,
      int mastercardEncounter,
      int fsrEncounter,
      int vlConceptQuestion,
      int vlQualitativeConceptQuestion) {

    String query =
        "SELECT p.patient_id FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN"
            + " obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND "
            + " e.encounter_type IN (%d,%d,%d,%d) AND "
            + " ((o.concept_id=%d AND o.value_numeric IS NOT NULL) OR (o.concept_id=%d AND o.value_coded IS NOT NULL)) AND "
            + " e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND "
            + " e.location_id=:location "
            + " UNION "
            + " SELECT p.patient_id FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN "
            + " obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND "
            + " e.encounter_type IN (%d) AND o.concept_id=%d AND o.value_numeric IS NOT NULL AND "
            + " o.obs_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND "
            + " e.location_id=:location ";
    return String.format(
        query,
        labEncounter,
        adultSeguimentoEncounter,
        pediatriaSeguimentoEncounter,
        fsrEncounter,
        vlConceptQuestion,
        vlQualitativeConceptQuestion,
        mastercardEncounter,
        vlConceptQuestion);
  }

  public static String getPatientsHavingRoutineViralLoadTestsUsingFsr(
      int labEncounter,
      int viralLoadRequestReasonConceptId,
      int routineViralLoadConceptId,
      int unknownConceptId) {
    String query =
        " SELECT final.patient_id FROM( "
            + " SELECT p.patient_id, MAX(ee.encounter_datetime) AS viral_load_date "
            + " FROM patient p "
            + " INNER JOIN encounter ee ON p.patient_id=ee.patient_id "
            + " INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + " WHERE "
            + " ee.voided = 0 AND "
            + " ee.encounter_type = %d AND "
            + " oo.voided = 0 AND "
            + " oo.concept_id = %d AND oo.value_coded IN(%d, %d) AND "
            + " ee.location_id = :location "
            + " AND ee.encounter_datetime <= :endDate "
            + " GROUP BY p.patient_id ) final";

    return String.format(
        query,
        labEncounter,
        viralLoadRequestReasonConceptId,
        routineViralLoadConceptId,
        unknownConceptId);
  }
}
