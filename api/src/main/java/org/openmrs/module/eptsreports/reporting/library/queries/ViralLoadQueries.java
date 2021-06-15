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

public class ViralLoadQueries {

  /**
   * <b>Description</b>Patients with viral load suppression within 12 months
   *
   * @param labEncounter
   * @param adultSeguimentoEncounter
   * @param pediatriaSeguimentoEncounter
   * @param mastercardEncounter
   * @param fsrEncounter
   * @param vlConceptQuestion
   * @param vlQualitativeConceptQuestion
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
        "SELECT fn1.patient_id FROM( "
            + " SELECT  patient_id, MAX(data_carga) AS data_carga FROM( "
            + " SELECT patient_id,data_carga FROM "
            + " (SELECT p.patient_id,MAX(o.obs_datetime) data_carga "
            + " FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN (%d, %d, %d, %d) "
            + " AND  o.concept_id IN(%d, %d) "
            + " AND (o.value_numeric IS NOT NULL OR o.value_coded IS NOT NULL) AND "
            + " e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND "
            + " e.location_id=:location GROUP BY p.patient_id "
            + ") ultima_carga "
            + " INNER JOIN obs ON obs.person_id=ultima_carga.patient_id AND obs.obs_datetime= "
            + " ultima_carga.data_carga  WHERE obs.voided=0 AND obs.concept_id IN (%d, %d) "
            + " AND obs.location_id=:location AND (obs.value_numeric IS NOT NULL OR obs.value_coded IS NOT NULL) "
            + " UNION "
            + " SELECT patient_id,data_carga FROM "
            + " (SELECT p.patient_id, MAX(o.obs_datetime) AS data_carga FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN "
            + " obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND "
            + " e.encounter_type IN (%d) AND o.concept_id=%d AND o.value_numeric IS NOT NULL AND "
            + " o.obs_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + " AND e.location_id=:location GROUP BY p.patient_id) comb INNER JOIN obs ON obs.person_id=comb.patient_id AND obs.obs_datetime= "
            + " comb.data_carga  WHERE obs.voided=0 AND obs.concept_id IN (%d) "
            + " AND obs.location_id=:location AND "
            + " (obs.value_numeric IS NOT NULL OR obs.value_coded IS NOT NULL) GROUP BY patient_id)fn GROUP BY patient_id)fn1 "
            + " INNER JOIN obs os ON os.person_id=fn1.patient_id WHERE fn1.data_carga=os.obs_datetime AND os.concept_id IN(%d, %d) "
            + " AND (os.value_numeric < 1000 OR os.value_coded IS NOT NULL) AND os.location_id=:location AND voided=0 ";

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
        vlConceptQuestion,
        vlConceptQuestion,
        vlConceptQuestion,
        vlQualitativeConceptQuestion);
  }

  /**
   * <b>Description:</b> Patients having viral load within the 12 months period
   *
   * @param labEncounter
   * @param adultSeguimentoEncounter
   * @param pediatriaSeguimentoEncounter
   * @param mastercardEncounter
   * @param fsrEncounter
   * @param vlConceptQuestion
   * @param vlQualitativeConceptQuestion
   * @return {@link String}
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

  /**
   * <b>Description</b> Patients or routine using FSR with VL results
   *
   * @param labEncounter
   * @param viralLoadRequestReasonConceptId
   * @param routineViralLoadConceptId
   * @param unknownConceptId
   * @return String
   */
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
  /**
   * <b>Descritpion</b>
   *
   * <p>Patients whose age is depended on the Viral load - VL date and DOB difference will result
   * into age required
   */
  public static String getPatientAgeBasedOnFirstViralLoadDate(
      int conceptId, int encounterType, int masterCardEncounterType, int minAge, int maxAge) {
    Map<String, Integer> map = new HashMap<>();
    map.put("viralLoadConcept", conceptId);
    map.put("encounterType", encounterType);
    map.put("masterCardEncounterType", masterCardEncounterType);
    map.put("minAge", minAge);
    map.put("maxAge", maxAge);
    String query =
        "SELECT patient_id "
            + " FROM   ( "
            + " SELECT     pat.patient_id, "
            + " Timestampdiff(year, pn.birthdate, encounter_datetime) AS age "
            + " FROM       patient pat "
            + " INNER JOIN person pn "
            + " ON         pat.patient_id=pn.person_id "
            + " INNER JOIN "
            + " ( "
            + " SELECT   patient_id, "
            + " Min(encounter_datetime) AS encounter_datetime "
            + " FROM     ( "
            + " SELECT     p.patient_id, "
            + " e.encounter_datetime "
            + " FROM       patient p "
            + " INNER JOIN encounter e "
            + " ON         p.patient_id=e.patient_id "
            + " INNER JOIN obs o "
            + " ON         e.encounter_id=o.encounter_id "
            + " WHERE      o.value_numeric IS NOT NULL "
            + " AND        p.voided=0 "
            + " AND        e.voided=0 "
            + " AND        o.voided=0 "
            + " AND        e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + " AND e.encounter_type in (${encounterType}, ${masterCardEncounterType}) "
            + " AND o.concept_id=${viralLoadConcept} "
            + " AND o.value_numeric > 1000 ) al "
            + " GROUP BY patient_id) ex "
            + " ON pat.patient_id=ex.patient_id ) fin "
            + " WHERE  age BETWEEN ${minAge} AND ${maxAge} ";
    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }
}
