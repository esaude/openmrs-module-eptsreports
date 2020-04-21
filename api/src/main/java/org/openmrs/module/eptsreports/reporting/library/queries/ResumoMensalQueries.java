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

public class ResumoMensalQueries {

  /**
   * All patients with encounter type 53, and Pre-ART Start Date that is less than startDate
   *
   * @return String
   */
  public static String getAllPatientsWithPreArtStartDateLessThanReportingStartDate(
      int encounterType, int conceptId) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=%d AND e.location_id=:location AND o.value_datetime IS NOT NULL AND o.concept_id=%d AND o.value_datetime <=:startDate";
    return String.format(query, encounterType, conceptId);
  }

  public static String getPatientsWhoInitiatedPreArtDuringCurrentMonthWithConditions(
      int masterCardEncounterType,
      int preArtStartDateConceptId,
      int HIVCareProgramId,
      int ARVAdultInitialEncounterType,
      int ARVPediatriaInitialEncounterType) {
    Map<String, Integer> map = new HashMap<>();
    map.put("masterCardEncounterType", masterCardEncounterType);
    map.put("preArtStartDateConceptId", preArtStartDateConceptId);
    map.put("HIVCareProgramId", HIVCareProgramId);
    map.put("ARVAdultInitialEncounterType", ARVAdultInitialEncounterType);
    map.put("ARVPediatriaInitialEncounterType", ARVPediatriaInitialEncounterType);

    String query =
        "SELECT res.patient_id FROM "
            + "(SELECT results.patient_id, "
            + "       Min(results.enrollment_date) enrollment_date "
            + "FROM   (SELECT p.patient_id, "
            + "               o.value_datetime AS enrollment_date "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e "
            + "                       ON p.patient_id = e.patient_id "
            + "               INNER JOIN obs o "
            + "                       ON o.encounter_id = e.encounter_id "
            + "        WHERE  p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND e.encounter_type = ${masterCardEncounterType} "
            + "               AND e.location_id =:location "
            + "               AND o.value_datetime IS NOT NULL "
            + "               AND o.concept_id =${preArtStartDateConceptId} "
            + "        UNION ALL "
            + "        SELECT p.patient_id, "
            + "               date_enrolled AS enrollment_date "
            + "        FROM   patient_program pp "
            + "               JOIN patient p "
            + "                 ON pp.patient_id = p.patient_id "
            + "        WHERE  p.voided = 0 "
            + "               AND pp.voided = 0 "
            + "               AND pp.program_id =${HIVCareProgramId} "
            + "               AND pp.location_id =:location "
            + "        UNION ALL "
            + "        SELECT p.patient_id, "
            + "               enc.encounter_datetime AS enrollment_date "
            + "        FROM   encounter enc "
            + "               JOIN patient p "
            + "                 ON p.patient_id = enc.patient_id "
            + "        WHERE  p.voided = 0 "
            + "               AND enc.encounter_type IN (${ARVAdultInitialEncounterType},${ARVPediatriaInitialEncounterType}) "
            + "               AND enc.location_id =:location "
            + " 			  AND enc.voided = 0 "
            + "        ORDER  BY enrollment_date ASC) results "
            + "     GROUP  BY results.patient_id) res "
            + "     WHERE res.enrollment_date BETWEEN :startDate AND :endDate ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query);
  }

  public static String getPatientsTransferredFromAnotherHealthFacilityByEndOfPreviousMonth(
      int masterCardEncounter,
      int transferFromConcept,
      int yesConcept,
      int typeOfPantientConcept,
      int tarvConcept,
      int artProgram,
      int transferInState) {

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e ON p.patient_id = e.patient_id "
            + "       JOIN obs transf ON transf.encounter_id = e.encounter_id "
            + "       JOIN obs type ON type.encounter_id = e.encounter_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.encounter_type = ${masterCardEncounter} "
            + "       AND e.location_id = :location "
            + "       AND transf.voided = 0 "
            + "       AND transf.concept_id = ${transferFromConcept} "
            + "       AND transf.value_coded = ${yesConcept} "
            + "       AND type.voided = 0 "
            + "       AND type.concept_id = ${typeOfPantientConcept} "
            + "       AND type.value_coded = ${tarvConcept}"
            + "       AND transf.obs_datetime < :onOrBefore"
            + "UNION"
            + "       SELECT pg.patient_id"
            + "       FROM patient p"
            + "       INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
            + "       INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id"
            + "       WHERE pg.voided=0"
            + "       AND ps.voided=0"
            + "       AND p.voided=0"
            + "       AND pg.program_id=${artProgram}"
            + "       AND ps.state=${transferInState}"
            + "       AND ps.end_date is null"
            + "       and ps.start_date < :onOrBefore";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("masterCardEncounter", masterCardEncounter);
    valuesMap.put("transferFromConcept", transferFromConcept);
    valuesMap.put("yesConcept", yesConcept);
    valuesMap.put("typeOfPantientConcept", typeOfPantientConcept);
    valuesMap.put("tarvConcept", tarvConcept);
    valuesMap.put("artProgram", artProgram);
    valuesMap.put("transferInState", transferInState);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  /** Get transferred-in patients as specified in Resumo Mensal */
  public static String getTransferredIn(
      int mastercard,
      int transferFromOther,
      int yes,
      int dateOfMasterCardFileOpening,
      int typeOfPatient,
      int tarv,
      int programEnrolled,
      int transferredInState,
      boolean isExclusion) {
    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs transf "
            + "         ON transf.encounter_id = e.encounter_id "
            + "       JOIN obs type "
            + "         ON type.encounter_id = e.encounter_id "
            + "       JOIN obs opening "
            + "         ON opening.encounter_id = e.encounter_id "
            + "WHERE  p.voided = 0 "
            + "        AND e.voided = 0 "
            + "        AND e.encounter_type = ${mastercard} "
            + "        AND e.location_id = :location "
            + "        AND transf.voided = 0 "
            + "        AND transf.concept_id = ${transferFromOther} "
            + "        AND transf.value_coded = ${yes} "
            + "        AND opening.voided = 0 "
            + "        AND opening.concept_id = ${dateOfMasterCardFileOpening} ";
    if (isExclusion == true) {
      query = query + "AND opening.value_datetime < :onOrAfter ";
    } else {
      query = query + " AND opening.value_datetime BETWEEN :onOrAfter AND :onOrBefore  ";
    }
    query =
        query
            + "        AND type.voided = 0 "
            + "        AND type.concept_id = ${typeOfPatient} "
            + "        AND type.value_coded in (${tarv}) "
            + "UNION "
            + "SELECT p.patient_id "
            + "FROM patient p   "
            + "    JOIN patient_program pp  "
            + "        ON p.patient_id=pp.patient_id "
            + "    JOIN patient_state ps  "
            + "        ON ps.patient_program_id=pp.patient_program_id "
            + "WHERE  pp.program_id = ${programEnrolled} "
            + "       AND ps.state = ${transferredInState} "
            + "       AND pp.location_id = :location ";
    if (isExclusion == true) {
      query = query + "    AND ps.start_date < :onOrAfter ";
    } else {
      query = query + "    AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore ";
    }
    query = query + "    AND p.voided = 0 " + "    AND pp.voided = 0 " + "    AND ps.voided = 0";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("mastercard", mastercard);
    valuesMap.put("transferFromOther", transferFromOther);
    valuesMap.put("yes", yes);
    valuesMap.put("dateOfMasterCardFileOpening", dateOfMasterCardFileOpening);
    valuesMap.put("typeOfPatient", typeOfPatient);
    valuesMap.put("tarv", tarv);
    valuesMap.put("programEnrolled", programEnrolled);
    valuesMap.put("transferredInState", transferredInState);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  /**
   * Patient screened for TB
   *
   * @param encounterType
   * @param tbScreening
   * @param yesConcept
   * @param noConcept
   * @param tbTreatment
   * @return
   */
  public static String getPatientsWithTBScreening(
      int adultoSeguimentoEncounterType,
      int tbScreening,
      int yesConcept,
      int noConcept,
      int tbTreatment) {
    String query =
        "SELECT screening.patient_id FROM (SELECT p.patient_id,e.encounter_datetime "
            + " FROM patient p "
            + "   INNER  JOIN encounter e "
            + "     ON p.patient_id=e.patient_id "
            + "   INNER  JOIN obs o "
            + "     ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided = 0 "
            + "   AND e.voided = 0 "
            + "   AND o.voided = 0 "
            + "   AND e.location_id = :location "
            + "   AND e.encounter_datetime "
            + " BETWEEN :startDate AND :endDate "
            + "   AND e.encounter_type=${adultoSeguimentoEncounterType} "
            + "   AND o.concept_id=${tbScreening} "
            + "   AND o.value_coded in (${yesConcept},${noConcept})) as screening"
            + "   LEFT JOIN "
            + "(SELECT p.patient_id,e.encounter_datetime "
            + " FROM patient p "
            + "   INNER  JOIN encounter e "
            + "     ON p.patient_id=e.patient_id "
            + "   INNER  JOIN obs o "
            + "     ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided = 0 "
            + "   AND e.voided = 0 "
            + "   AND o.voided = 0 "
            + "   AND e.location_id = :location "
            + "   AND e.encounter_datetime "
            + " BETWEEN :startDate AND :endDate "
            + "   AND e.encounter_type=${adultoSeguimentoEncounterType} "
            + "   AND o.concept_id=${tbTreatment}"
            + "   AND o.value_coded is not null) as treatment"
            + "   ON screening.patient_id = treatment.patient_id"
            + "   AND screening.encounter_datetime = treatment.encounter_datetime"
            + " WHERE treatment.patient_id is null"
            + " GROUP BY screening.patient_id";

    Map<String, Integer> map = new HashMap<>();
    map.put("adultoSeguimentoEncounterType", adultoSeguimentoEncounterType);
    map.put("tbScreening", tbScreening);
    map.put("yesConcept", yesConcept);
    map.put("noConcept", noConcept);
    map.put("tbTreatment", tbTreatment);

    StringSubstitutor sub = new StringSubstitutor(map);
    return sub.replace(query);
  }

  /**
   * Get patients with encounters within start and end date F1: Number of patients who had clinical
   * appointment during the reporting month
   *
   * @return String
   */
  public static String getPatientsWithGivenEncounterType(int encounterType) {
    String query =
        " SELECT p.patient_id "
            + " FROM patient p "
            + "	INNER JOIN encounter e "
            + "		ON p.patient_id=e.patient_id "
            + " WHERE e.encounter_type= %d "
            + "		AND e.location_id=:location "
            + " 	AND e.encounter_datetime "
            + "			BETWEEN :startDate AND :endDate "
            + "		AND p.voided=0 "
            + "		AND e.voided=0 ";
    return String.format(query, encounterType);
  }

  /**
   * Get patients with viral load suppression
   *
   * @return String
   */
  public static String getPatientsHavingViralLoadSuppression(
      int viralLoadConcept, int encounterType) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + " AND o.value_numeric IS NOT NULL "
            + " AND o.concept_id=%d "
            + " AND e.encounter_type=%d "
            + " AND o.value_numeric < 1000";
    return String.format(query, viralLoadConcept, encounterType);
  }

  /**
   * getPatientsWithCodedObsAndAnswers
   *
   * @return String
   */
  public static String getPatientsWithCodedObsAndAnswers(
      int encounterType, int questionConceptId, int answerConceptId) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + " AND e.location_id = :location AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.encounter_type=%d "
            + " AND o.concept_id=%d AND o.value_coded=%d";
    return String.format(query, encounterType, questionConceptId, answerConceptId);
  }

  /**
   * Get patients with viral load suppression
   *
   * @return String
   */
  public static String getPatientsHavingViralLoadResults(int viralLoadConcept, int encounterType) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + " AND o.value_numeric IS NOT NULL "
            + " AND o.concept_id=%d "
            + " AND e.encounter_type=%d ";
    return String.format(query, viralLoadConcept, encounterType);
  }

  /**
   * Get patients with any coded obs value
   *
   * @return String
   */
  public static String gePatientsWithCodedObs(int encounterType, int conceptId) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + " AND e.location_id = :location AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.encounter_type=%d "
            + " AND o.concept_id=%d ";
    return String.format(query, encounterType, conceptId);
  }

  /**
   * E1 exclusions
   *
   * @return String
   */
  public static String getE1ExclusionCriteria(
      int encounterType, int questionConceptId, int answerConceptId) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " JOIN (SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM patient pat "
            + " JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob ON enc.encounter_id=ob.encounter_id "
            + " WHERE pat.voided = 0 AND enc.voided = 0 AND ob.voided = 0 AND enc.location_id = :location "
            + " AND enc.encounter_datetime BETWEEN :startDate AND :endDate AND enc.encounter_type=%d AND "
            + " ob.concept_id=%d AND ob.value_coded=%d) ed "
            + " ON p.patient_id=ed.patient_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + " AND e.location_id = :location AND e.encounter_datetime BETWEEN "
            + " IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) "
            + " AND DATE_ADD(:startDate, INTERVAL -1 DAY) AND e.encounter_type=%d "
            + " AND o.concept_id=%d AND o.value_coded=%d";
    return String.format(
        query,
        encounterType,
        questionConceptId,
        answerConceptId,
        encounterType,
        questionConceptId,
        answerConceptId);
  }

  /**
   * E2 exclusions
   *
   * @param viralLoadConcept
   * @param encounterType
   * @param qualitativeConcept
   * @return String
   */
  public static String getE2ExclusionCriteria(
      int viralLoadConcept, int encounterType, int qualitativeConcept) {

    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + "JOIN (SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM patient pat JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob "
            + " ON enc.encounter_id=ob.encounter_id "
            + " WHERE pat.voided=0 AND enc.voided=0 AND ob.voided=0 AND enc.location_id=:location AND enc.encounter_datetime "
            + " BETWEEN :startDate AND :endDate AND ob.concept_id IN(%d, %d) AND enc.encounter_type=%d) ed "
            + " ON p.patient_id=ed.patient_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN "
            + " IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) "
            + " AND DATE_ADD(:startDate, INTERVAL -1 DAY) "
            + " AND o.concept_id IN (%d, %d)"
            + " AND e.encounter_type=%d ";
    return String.format(
        query,
        viralLoadConcept,
        qualitativeConcept,
        encounterType,
        viralLoadConcept,
        qualitativeConcept,
        encounterType);
  }

  /**
   * E3 exclusion
   *
   * @param viralLoadConcept
   * @param encounterType
   * @param qualitativeConcept
   * @return
   */
  public static String getE3ExclusionCriteria(
      int viralLoadConcept, int adultoSeguimentoEncounterType, int qualitativeConcept) {
    String query =
        " SELECT patient_id FROM ( "
            + " SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM patient pat "
            + " JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob ON enc.encounter_id=ob.encounter_id "
            + " WHERE pat.voided=0 AND enc.voided=0 AND ob.voided=0 AND enc.location_id=:location "
            + " AND enc.encounter_datetime BETWEEN "
            + " IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) "
            + " AND DATE_ADD(:startDate, INTERVAL -1 DAY) "
            + " AND ob.value_numeric IS NOT NULL "
            + " AND ob.concept_id=${viralLoadConcept} AND enc.encounter_type=${adultoSeguimentoEncounterType} AND ob.value_numeric < 1000"
            + " UNION "
            + " SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM patient pat "
            + " JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob ON enc.encounter_id=ob.encounter_id "
            + " WHERE pat.voided = 0 AND enc.voided = 0 AND ob.voided = 0 AND enc.location_id = :location AND "
            + " enc.encounter_datetime BETWEEN "
            + " IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) "
            + " AND DATE_ADD(:startDate, INTERVAL -1 DAY) "
            + " AND enc.encounter_type=${adultoSeguimentoEncounterType} AND ob.concept_id=${qualitativeConcept} ) E3exclusion";

    Map<String, Integer> map = new HashMap<>();
    map.put("viralLoadConcept", viralLoadConcept);
    map.put("adultoSeguimentoEncounterType", adultoSeguimentoEncounterType);
    map.put("qualitativeConcept", qualitativeConcept);

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query.toString());
  }

  /**
   * F3 exclusions
   *
   * @param encounterType
   * @return
   */
  public static String getF3Exclusion(int encounterType) {
    String query =
        " SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id "
            + " WHERE e.encounter_type=%d AND e.location_id=:location "
            + " AND e.encounter_datetime >= "
            + " IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) "
            + " AND e.encounter_datetime < :startDate "
            + " AND p.voided=0 AND e.voided=0 ";
    return String.format(query, encounterType);
  }

  /**
   * Patients with first clinical consultation 6 on the same Pre-ART Start date() Concept ID 23808
   *
   * @return String
   */
  public static String getPatientsWithFirstClinicalConsultationOnTheSameDateAsPreArtStartDate(
      int mastercardEncounterType, int consultationEncounterType, int preArtStarConceptId) {
    String query =
        "SELECT l.patient_id FROM"
            + " (SELECT p.patient_id AS patient_id, MIN(e.encounter_datetime) AS encounter_datetime FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN(SELECT p.patient_id AS patient_id,o.value_datetime AS art_start_date FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON o.encounter_id=e.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=%d AND e.location_id=:location "
            + " AND o.value_datetime IS NOT NULL AND o.concept_id=%d GROUP BY p.patient_id) pre_art ON p.patient_id=pre_art.patient_id WHERE p.voided=0 "
            + " AND e.voided=0 AND e.encounter_type=%d AND e.location_id=:location AND encounter_datetime <= :endDate  AND encounter_datetime=pre_art.art_start_date GROUP BY patient_id) l";
    return String.format(
        query, mastercardEncounterType, preArtStarConceptId, consultationEncounterType);
  }

  /**
   * B7
   *
   * @param returnVisitDateForArvDrugConcept
   * @param arvPharmaciaEncounterType
   * @param artDatePickup
   * @param masterCardDrugPickupEncounterType
   * @return String
   */
  public static String getNumberOfPatientsWhoAbandonedArtBySpecifiedDateB7(
      int returnVisitDateForArvDrugConcept,
      int arvPharmaciaEncounterType,
      int artDatePickup,
      int masterCardDrugPickupEncounterType) {

    Map<String, Integer> map = new HashMap<>();
    map.put("returnVisitDateForArvDrugConcept", returnVisitDateForArvDrugConcept);
    map.put("arvPharmaciaEncounterType", arvPharmaciaEncounterType);
    map.put("artDatePickup", artDatePickup);
    map.put("masterCardDrugPickupEncounterType", masterCardDrugPickupEncounterType);

    StringBuilder query = new StringBuilder();
    query.append("SELECT final.patient_id   ");
    query.append("             from(   ");
    query.append("                 SELECT   ");
    query.append(
        "                     most_recent.patient_id, Date_add(Max(most_recent.value_datetime), interval 60 day) final_encounter_date   ");
    query.append("                 FROM   (SELECT fila.patient_id, o.value_datetime from (  ");
    query.append("                             SELECT enc.patient_id,   ");
    query.append(
        "                                 Max(enc.encounter_datetime)  encounter_datetime  ");
    query.append("                             FROM   patient pa   ");
    query.append("                                 inner join encounter enc   ");
    query.append("                                     ON enc.patient_id =  pa.patient_id   ");
    query.append("                                 inner join obs obs   ");
    query.append("                                     ON obs.encounter_id = enc.encounter_id   ");
    query.append("                             WHERE  pa.voided = 0   ");
    query.append("                                 AND enc.voided = 0   ");
    query.append("                                 AND obs.voided = 0   ");
    query.append(
        "                                 AND obs.concept_id =  ${returnVisitDateForArvDrugConcept}   ");
    query.append("                                 AND obs.value_datetime IS NOT NULL   ");
    query.append(
        "                                 AND enc.encounter_type = ${arvPharmaciaEncounterType}   ");
    query.append("                                 AND enc.location_id = :location   ");

    query.append("                                 AND datediff(enc.encounter_datetime,:date) <=0");

    query.append("                             GROUP  BY pa.patient_id) fila   ");
    query.append("                         INNER JOIN encounter e on  ");
    query.append("                             e.patient_id = fila.patient_id and  ");
    query.append(
        "                             e.encounter_datetime = fila.encounter_datetime AND  ");
    query.append(
        "                             e.encounter_type = ${arvPharmaciaEncounterType} AND  ");
    query.append("                             e.location_id = :location AND  ");
    query.append("                             e.voided = 0  ");
    query.append("                         INNER JOIN obs o on  ");
    query.append("                             o.encounter_id = e.encounter_id AND  ");
    query.append(
        "                             o.concept_id = ${returnVisitDateForArvDrugConcept} AND  ");
    query.append("                             o.voided = 0  ");
    query.append("                         UNION   ");
    query.append("                         SELECT enc.patient_id,   ");
    query.append(
        "                             Date_add(Max(obs.value_datetime), interval 30 day) value_datetime  ");
    query.append("                         FROM   patient pa   ");
    query.append("                             inner join encounter enc   ");
    query.append("                                 ON enc.patient_id = pa.patient_id   ");
    query.append("                             inner join obs obs   ");
    query.append("                                 ON obs.encounter_id = enc.encounter_id   ");
    query.append("                         WHERE  pa.voided = 0   ");
    query.append("                             AND enc.voided = 0   ");
    query.append("                             AND obs.voided = 0   ");
    query.append("                             AND obs.concept_id = ${artDatePickup}   ");
    query.append("                             AND obs.value_datetime IS NOT NULL   ");
    query.append(
        "                             AND enc.encounter_type = ${masterCardDrugPickupEncounterType}   ");
    query.append("                             AND enc.location_id = :location   ");

    query.append("                             AND datediff(obs.value_datetime,:date) <=0");
    query.append("                        GROUP  BY pa.patient_id   ");
    query.append("                    ) most_recent   ");
    query.append("                GROUP BY most_recent.patient_id   ");
    query.append("                HAVING datediff(final_encounter_date,:date) < 0  ");

    query.append("             ) final   ");
    query.append("             GROUP BY final.patient_id ");

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query.toString());
  }

  /**
   * Get all patients enrolled in PRE-ART program id 1, with date enrolled less than startDate
   *
   * @return String
   */
  public static String getAllPatientsEnrolledInPreArtProgramWithDateEnrolledLessThanStartDate(
      int programId) {
    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN patient_program pp "
            + "               ON p.patient_id = pp.patient_id "
            + "WHERE  p.voided = 0 "
            + "       AND pp.voided = 0 "
            + "       AND pp.location_id = :location "
            + "       AND pp.program_id = ${programId} "
            + "       AND pp.date_enrolled <= :startDate";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("programId", programId);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  /**
   * Get all patients registered in encounterType 5 or 7, with date enrolled less than startDate
   *
   * @return String
   */
  public static String
      getAllPatientsRegisteredInEncounterType5or7WithEncounterDatetimeLessThanStartDate(
          int encounterType5, int encounterType7) {
    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "WHERE p.voided = 0 "
            + "AND e.voided = 0 "
            + "AND e.location_id = :location "
            + "AND e.encounter_type IN (${encounterType5}, ${encounterType7}) "
            + "AND e.encounter_datetime < :startDate";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("encounterType5", encounterType5);
    valuesMap.put("encounterType7", encounterType7);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  public static String getPatientsRegisteredAsTransferredInDuringTheStatisticalYear(
      int mastercard,
      int transferFromOther,
      int yes,
      int typeOfPatient,
      int answer,
      int masteCardFileOpenDate,
      int programEnrolled,
      int transferredInState) {

    Map<String, Integer> map = new HashMap<>();
    map.put("mastercard", mastercard);
    map.put("transferFromOther", transferFromOther);
    map.put("yes", yes);
    map.put("typeOfPatient", typeOfPatient);
    map.put("answer", answer);
    map.put("masteCardFileOpenDate", masteCardFileOpenDate);
    map.put("programEnrolled", programEnrolled);
    map.put("transferredInState", transferredInState);

    String query =
        " SELECT p.patient_id  "
            + "        FROM   patient p  "
            + "           INNER JOIN encounter e  "
            + "             ON p.patient_id = e.patient_id  "
            + "           INNER JOIN obs transf  "
            + "             ON transf.encounter_id = e.encounter_id  "
            + "           INNER JOIN obs type  "
            + "             ON type.encounter_id = e.encounter_id  "
            + "           INNER JOIN obs mastercardFile  "
            + "             ON mastercardFile.encounter_id = e.encounter_id  "
            + "        WHERE  p.voided = 0  "
            + "           AND e.voided = 0  "
            + "           AND e.encounter_type = ${mastercard}  "
            + "           AND e.location_id = :location  "
            + "  "
            + " "
            + "           AND transf.voided = 0  "
            + "           AND transf.concept_id = ${transferFromOther}  "
            + "           AND transf.value_coded = ${yes}  "
            + "     "
            + "           AND type.voided = 0  "
            + "           AND type.concept_id = ${typeOfPatient}  "
            + "           AND type.value_coded = ${answer}  "
            + "          AND mastercardFile.voided = 0 "
            + "           AND mastercardFile.concept_id = ${masteCardFileOpenDate}"
            + "	         AND mastercardFile.value_datetime BETWEEN  "
            + "	  		        IF(MONTH(:onOrAfter) = 12  && DAY(:onOrAfter) = 21, :onOrAfter, CONCAT(YEAR(:onOrAfter)-1, '-12','-21'))  "
            + "	  		          AND  "
            + "	  		         :onOrBefore   "
            + "    	UNION  "
            + " "
            + "   	SELECT p.patient_id  "
            + "    	FROM patient p    "
            + "        INNER JOIN patient_program pp   "
            + "            ON p.patient_id=pp.patient_id  "
            + "        INNER JOIN patient_state ps   "
            + "            ON ps.patient_program_id=pp.patient_program_id  "
            + "    	WHERE  pp.program_id = ${programEnrolled}  "
            + "        AND ps.state = ${transferredInState}  "
            + "    	  AND pp.location_id = :location"
            + "        AND ps.start_date BETWEEN "
            + "             IF(MONTH(:onOrAfter) = 12  && DAY(:onOrAfter) = 21, :onOrAfter, CONCAT(YEAR(:onOrAfter)-1, '-12','-21'))  "
            + "        AND  "
            + "            :onOrBefore  "
            + "        AND p.voided = 0  "
            + "        AND pp.voided = 0  "
            + "        AND ps.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    String mappedQuery = sb.replace(query);
    return mappedQuery;
  }
}
