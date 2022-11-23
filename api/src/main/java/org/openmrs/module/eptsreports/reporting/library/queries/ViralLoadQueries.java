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
import org.openmrs.module.eptsreports.metadata.HivMetadata;

public class ViralLoadQueries {

  /**
   * <b>Description</b>Patients with viral load suppression within 12 months
   *
   * @param labEncounter_13
   * @param adultSeguimentoEncounter_6
   * @param pediatriaSeguimentoEncounter_9
   * @param mastercardEncounter_53
   * @param fsrEncounter_51
   * @param vlConceptQuestion_856
   * @param vlQualitativeConceptQuestion_1305
   * @return String
   */
  public static String getPatientsWithViralLoadSuppression() {

    HivMetadata hivMetadata = new HivMetadata();
    Map<String, Integer> map = new HashMap<>();

    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        "SELECT fn1.patient_id FROM( "
            + " SELECT  patient_id, MAX(data_carga) AS data_carga FROM( "
            + " SELECT patient_id,data_carga FROM "
            + " (SELECT p.patient_id, MAX(o.obs_datetime) data_carga "
            + " FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 "
            + " AND e.voided=0 "
            + " AND o.voided=0 "
            + " AND e.encounter_type IN (${6}, ${9}, ${13}, ${51}, ${53}) "
            + " AND  o.concept_id IN(${856}, ${1305}) "
            + " AND (o.value_numeric IS NOT NULL OR o.value_coded IS NOT NULL) "
            + " AND DATE(e.encounter_datetime) BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND "
            + " e.location_id=:location GROUP BY p.patient_id "
            + ") ultima_carga "
            + " INNER JOIN obs ON obs.person_id=ultima_carga.patient_id AND obs.obs_datetime= "
            + " ultima_carga.data_carga  "
            + " WHERE obs.voided=0 AND obs.concept_id IN (${856}, ${1305}) "
            + " AND obs.location_id=:location AND (obs.value_numeric IS NOT NULL OR obs.value_coded IS NOT NULL) "
            + " UNION "
            + " SELECT patient_id,data_carga FROM "
            + " (SELECT p.patient_id, MAX(o.obs_datetime) AS data_carga FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN "
            + " obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND "
            + " e.encounter_type IN (${53}) AND o.concept_id=${856} AND o.value_numeric IS NOT NULL AND "
            + " o.obs_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + " AND e.location_id=:location GROUP BY p.patient_id) comb INNER JOIN obs ON obs.person_id=comb.patient_id AND obs.obs_datetime= "
            + " comb.data_carga  WHERE obs.voided=0 AND obs.concept_id IN (${856}) "
            + " AND obs.location_id=:location AND "
            + " (obs.value_numeric IS NOT NULL OR obs.value_coded IS NOT NULL) GROUP BY patient_id)fn GROUP BY patient_id)fn1 "
            + " INNER JOIN obs os ON os.person_id=fn1.patient_id WHERE fn1.data_carga=os.obs_datetime AND os.concept_id IN(${856}, ${1305}) "
            + " AND (os.value_numeric < 1000 OR os.value_coded IS NOT NULL) AND os.location_id=:location AND voided=0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
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
  public static String getPatientsHavingViralLoadInLast12Months() {

    HivMetadata hivMetadata = new HivMetadata();
    Map<String, Integer> map = new HashMap<>();

    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        "SELECT p.patient_id FROM  patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 "
            + " AND e.voided=0 "
            + " AND o.voided=0 "
            + " AND e.encounter_type IN (${6}, ${9}, ${13}, ${51}, ${53}) "
            + " AND ((o.concept_id=${856} AND o.value_numeric IS NOT NULL) OR (o.concept_id=${1305} AND o.value_coded IS NOT NULL)) "
            + " AND DATE(e.encounter_datetime) BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND "
            + " e.location_id=:location "
            + " UNION "
            + " SELECT p.patient_id FROM  patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 "
            + " AND e.voided=0 "
            + " AND o.voided=0 "
            + " AND e.encounter_type IN (${53}) "
            + " AND o.concept_id=${856} AND o.value_numeric IS NOT NULL AND "
            + " DATE(o.obs_datetime) BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + " AND e.location_id=:location ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
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
    map.put("856", conceptId);
    map.put("6", encounterType);
    map.put("53", masterCardEncounterType);
    map.put("minAge", minAge);
    map.put("maxAge", maxAge);
    String query =
        "SELECT patient_id "
            + "FROM   ( "
            + "         SELECT     pat.patient_id, Timestampdiff(year, pn.birthdate, encounter_datetime) AS age "
            + "         FROM       patient pat "
            + "            INNER JOIN person pn ON pat.patient_id=pn.person_id "
            + "            INNER JOIN "
            + "            ( "
            + "                SELECT   p.patient_id, Min(e.encounter_datetime) AS encounter_datetime "
            + "                FROM  patient p "
            + "                    INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "                    INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "                WHERE   o.value_numeric IS NOT NULL "
            + "                    AND p.voided = 0 "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.location_id = :location "
            + "                    AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                    AND e.encounter_type = ${6} "
            + "                    AND o.concept_id = ${856} "
            + "                    AND o.value_numeric >= 1000 "
            + "                GROUP BY patient_id "
            + "                UNION "
            + "                SELECT   p.patient_id, Min(o.obs_datetime) AS encounter_datetime "
            + "                FROM  patient p "
            + "                    INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "                    INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "                WHERE   o.value_numeric IS NOT NULL "
            + "                    AND   p.voided = 0 "
            + "                    AND   e.voided = 0 "
            + "                    AND   o.voided = 0 "
            + "                    AND   e.location_id = :location "
            + "                    AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "                    AND e.encounter_type = ${53} "
            + "                    AND o.concept_id = ${856} "
            + "                    AND o.value_numeric >= 1000 "
            + "             GROUP BY patient_id "
            + "         ) ex ON pat.patient_id=ex.patient_id ) fin "
            + "WHERE  age BETWEEN ${minAge} AND ${maxAge} ";
    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * <b>Description:</b> Patients having viral load within the 12 months period
   *
   * @return {@link String}
   */
  public static String getPatientsHavingTypeOfDispensationBasedOnTheirLastVlResults() {

    HivMetadata hivMetadata = new HivMetadata();
    Map<String, Integer> map = new HashMap<>();

    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());
    map.put("23739", hivMetadata.getTypeOfDispensationConcept().getConceptId());
    map.put("23720", hivMetadata.getQuarterlyConcept().getConceptId());
    map.put("23888", hivMetadata.getSemiannualDispensation().getConceptId());

    String query =
        "SELECT out_p.patient_id "
            + "FROM   patient pp "
            + "       INNER JOIN encounter ep ON pp.patient_id = ep.patient_id "
            + "       INNER JOIN obs op ON ep.encounter_id = op.encounter_id "
            + "       INNER JOIN (SELECT patient_id, MAX(encounter_datetime) AS max_vl_date_and_max_ficha "
            + "                   FROM   (SELECT pp.patient_id, ee.encounter_datetime "
            + "                           FROM   patient pp "
            + "                                  INNER JOIN encounter ee ON pp.patient_id = ee.patient_id "
            + "                                  INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + "                                  INNER JOIN (SELECT patient_id, DATE( Max(encounter_date)) AS vl_max_date "
            + "                                              FROM   (SELECT p.patient_id, DATE(e.encounter_datetime) AS encounter_date "
            + "                                                      FROM   patient p "
            + "                                                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                                      WHERE  p.voided = 0 "
            + "                                                       AND e.voided = 0 "
            + "                                                       AND o.voided = 0 "
            + "                                                       AND e.encounter_type IN ( ${13}, ${6}, ${9}, ${51} ) "
            + "                                                       AND ( ( o.concept_id = ${856} AND o.value_numeric IS NOT  NULL ) "
            + "                                                             OR ( o.concept_id = ${1305}  AND o.value_coded IS NOT NULL ) ) "
            + "                                                       AND DATE(e.encounter_datetime) BETWEEN :startDate AND :endDate "
            + "                                                       AND e.location_id = :location "
            + "                                               UNION "
            + "                                               SELECT p.patient_id, DATE(o.obs_datetime) AS encounter_date "
            + "                                               FROM   patient p "
            + "                                               INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                               INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                               WHERE  p.voided = 0 "
            + "                                                 AND e.voided = 0 "
            + "                                                 AND o.voided = 0 "
            + "                                                 AND e.encounter_type IN ( ${53} ) "
            + "                                                 AND o.concept_id = ${856} "
            + "                                                 AND o.value_numeric IS NOT NULL "
            + "                                                 AND DATE(o.obs_datetime) BETWEEN :startDate AND :endDate "
            + "                                                 AND e.location_id = :location) max_vl_date "
            + "                                                 GROUP  BY patient_id"
            + "                   ) vl_date_tbl ON pp.patient_id = vl_date_tbl.patient_id "
            + "                 WHERE  ee.encounter_datetime BETWEEN Date_add( vl_date_tbl.vl_max_date, INTERVAL - 12 MONTH) AND  DATE_ADD( vl_date_tbl.vl_max_date,INTERVAL - 1 DAY) "
            + "                 AND oo.concept_id = ${23739} "
            + "                 AND oo.voided = 0 "
            + "                 AND ee.voided = 0   "
            + "                 AND ee.encounter_type = ${6}) fin_tbl "
            + "                 GROUP  BY patient_id) out_p ON pp.patient_id = out_p.patient_id "
            + "WHERE  ep.encounter_type = ${6} "
            + "       AND op.voided = 0 "
            + "       AND ep.voided = 0 "
            + "       AND pp.voided = 0 "
            + "       AND ep.encounter_datetime = max_vl_date_and_max_ficha "
            + "       AND op.concept_id = ${23739} "
            + "       AND op.value_coded IN( ${23720}, ${23888} )";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query);
  }
}
