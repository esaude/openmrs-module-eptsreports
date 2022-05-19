package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralQueries {

  /**
   * <b>Description:</b> Number of patients who received Viral Load Result
   *
   * @return {@link String}
   */
  public static String getPatientsWhoReceivedOneViralLoadResult(
      int adultoSeguimentoEncounterType, int viralLoadConcept, int viralLoadQualitativeConcept) {
    String query =
        "SELECT p.patient_id FROM "
            + "patient p JOIN encounter e "
            + "ON p.patient_id = e.patient_id JOIN obs o "
            + "ON o.encounter_id = e.encounter_id "
            + "WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND "
            + "e.encounter_type = ${adultoSeguimentoEncounterType} AND ((o.concept_id = ${viralLoadConcept} AND o.value_numeric IS NOT NULL) "
            + "OR (o.concept_id = ${viralLoadQualitativeConcept} AND o.value_coded IS NOT NULL)) "
            + "AND e.encounter_datetime <= :onOrBefore ";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("adultoSeguimentoEncounterType", adultoSeguimentoEncounterType);
    valuesMap.put("viralLoadConcept", viralLoadConcept);
    valuesMap.put("viralLoadQualitativeConcept", viralLoadQualitativeConcept);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  /**
   * <b>Description:</b> Number of patients transferred-Out (-) other HF's on ART during the month
   *
   * @return {@link String}
   */
  public static String getTransferedOutPatients(
      int artProgramConcept,
      int transferredOutToAnotherHealthFacilityWorkflowStateConcept,
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayOfPreArtPatient,
      int stateOfStayOfArtPatient,
      int transferredOutConcept,
      int pediatriaSeguimentoEncounterType,
      int ARVPharmaciaEncounterType,
      int masterCardDrugPickupEncounterType,
      int ArtDatePickupMasterCard) {
    String query =
        "SELECT patient_id "
            + "FROM (SELECT patient_id, "
            + "             transferout_date "
            + "      FROM (SELECT p.patient_id, "
            + "                   Max(ps.start_date) transferout_date "
            + "            FROM patient p "
            + "                     JOIN patient_program pp "
            + "                          ON p.patient_id = pp.patient_id "
            + "                     JOIN patient_state ps "
            + "                          ON pp.patient_program_id = ps.patient_program_id "
            + "            WHERE p.voided = 0 "
            + "              AND pp.voided = 0 "
            + "              AND pp.program_id = ${artProgramConcept} "
            + "              AND pp.location_id = :location "
            + "              AND ps.voided = 0 "
            + "              AND ps.state = ${transferredOutToAnotherHealthFacilityWorkflowStateConcept} "
            + "              AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore "
            + "            GROUP BY patient_id"
            + "            UNION "
            + "            SELECT p.patient_id, "
            + "                   e.encounter_datetime transferout_date "
            + "            FROM patient p "
            + "                     JOIN encounter e "
            + "                          ON p.patient_id = e.patient_id "
            + "                     JOIN obs o "
            + "                          ON e.encounter_id = o.encounter_id "
            + "            WHERE p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND e.location_id = :location "
            + "              AND e.encounter_type = ${adultoSeguimentoEncounterType} "
            + "              AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore "
            + "              AND o.voided = 0 "
            + "              AND o.concept_id = ${stateOfStayOfArtPatient} "
            + "              AND o.value_coded = ${transferredOutConcept}"
            + "            UNION "
            + "            SELECT p.patient_id, "
            + "                   o.obs_datetime transferout_date "
            + "            FROM patient p "
            + "                     JOIN encounter e "
            + "                          ON p.patient_id = e.patient_id "
            + "                     JOIN obs o "
            + "                          ON e.encounter_id = o.encounter_id "
            + "            WHERE p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND e.location_id = :location "
            + "              AND e.encounter_type = ${masterCardEncounterType} "
            + "              AND o.obs_datetime BETWEEN :onOrAfter AND :onOrBefore "
            + "              AND o.voided = 0 "
            + "              AND o.concept_id = ${stateOfStayOfPreArtPatient} "
            + "              AND o.value_coded = ${transferredOutConcept}) transferout "
            + "WHERE patient_id NOT IN (SELECT p.patient_id "
            + "                         FROM patient p "
            + "                                  JOIN encounter e "
            + "                                       ON p.patient_id = e.patient_id "
            + "                         WHERE p.voided = 0 "
            + "                           AND e.voided = 0 "
            + "                           AND e.encounter_type IN (${adultoSeguimentoEncounterType}, ${pediatriaSeguimentoEncounterType}, ${ARVPharmaciaEncounterType}) "
            + "                           AND e.location_id = :location "
            + "                           AND e.encounter_datetime > transferout_date "
            + "                           AND e.encounter_datetime <= :onOrBefore "
            + "                         UNION "
            + "                         SELECT p.patient_id "
            + "                         FROM patient p "
            + "                                  JOIN encounter e "
            + "                                       ON p.patient_id = e.patient_id "
            + "                                  JOIN obs o "
            + "                                       ON e.encounter_id = o.encounter_id "
            + "                         WHERE p.voided = 0 "
            + "                           AND e.voided = 0 "
            + "                           AND o.voided = 0 "
            + "                           AND e.encounter_type = ${masterCardDrugPickupEncounterType} "
            + "                           AND e.location_id = :location "
            + "                           AND o.concept_id = ${ArtDatePickupMasterCard} "
            + "                           AND o.value_datetime "
            + "                             > transferout_date "
            + "                           AND o.value_datetime "
            + "                             <= :onOrBefore)) as list;";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("artProgramConcept", artProgramConcept);
    valuesMap.put(
        "transferredOutToAnotherHealthFacilityWorkflowStateConcept",
        transferredOutToAnotherHealthFacilityWorkflowStateConcept);
    valuesMap.put("adultoSeguimentoEncounterType", adultoSeguimentoEncounterType);
    valuesMap.put("masterCardEncounterType", masterCardEncounterType);
    valuesMap.put("stateOfStayOfPreArtPatient", stateOfStayOfPreArtPatient);
    valuesMap.put("stateOfStayOfArtPatient", stateOfStayOfArtPatient);
    valuesMap.put("transferredOutConcept", transferredOutConcept);
    valuesMap.put("pediatriaSeguimentoEncounterType", pediatriaSeguimentoEncounterType);
    valuesMap.put("ARVPharmaciaEncounterType", ARVPharmaciaEncounterType);
    valuesMap.put("masterCardDrugPickupEncounterType", masterCardDrugPickupEncounterType);
    valuesMap.put("ArtDatePickupMasterCard", ArtDatePickupMasterCard);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  /**
   * <b>Description:</b> Number of patients with ART suspension during the current month with LAST
   * patient state
   *
   * @return {@link String}
   */
  public static String getPatientsWhoSuspendedTreatment(
      int art,
      int suspendedState,
      int adultSeg,
      int masterCard,
      int artStateOfStay,
      int preArtStateOfStay,
      int suspendedConcept,
      int fila,
      int mcDrugPickup,
      int drugPickup) {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("art", art);
    valuesMap.put("suspendedState", suspendedState);
    valuesMap.put("adultSeg", adultSeg);
    valuesMap.put("masterCard", masterCard);
    valuesMap.put("artStateOfStay", artStateOfStay);
    valuesMap.put("preArtStateOfStay", preArtStateOfStay);
    valuesMap.put("suspendedConcept", suspendedConcept);
    valuesMap.put("fila", fila);
    valuesMap.put("mcDrugPickup", mcDrugPickup);
    valuesMap.put("drugPickup", drugPickup);

    String sql =
        "SELECT patient_id FROM ("
            + "  SELECT patient_id, MAX(suspended_date) suspended_date FROM ("
            + " SELECT p.patient_id, Max(ps.start_date) suspended_date "
            + "  FROM patient p "
            + " INNER JOIN patient_program pp "
            + " ON p.patient_id = pp.patient_id "
            + " INNER JOIN patient_state ps "
            + " ON pp.patient_program_id = ps.patient_program_id "
            + " WHERE p.voided = 0 "
            + " AND pp.voided = 0 "
            + " AND pp.program_id = ${art} "
            + " AND pp.location_id = :location "
            + " AND ps.voided = 0 "
            + " AND ps.state = ${suspendedState} "
            + " AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore "
            + " AND ps.end_date IS NULL "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT p.patient_id, Max(e.encounter_datetime) suspended_date "
            + " FROM patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o "
            + " ON e.encounter_id = o.encounter_id "
            + " WHERE p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${adultSeg} "
            + " AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore "
            + " AND o.voided = 0 "
            + " AND o.concept_id = ${artStateOfStay} "
            + " AND o.value_coded = ${suspendedConcept} "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT p.patient_id, Max(o.obs_datetime) suspended_date "
            + " FROM patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " JOIN obs o "
            + " ON e.encounter_id = o.encounter_id "
            + " WHERE p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${masterCard} "
            + " AND o.voided = 0 "
            + " AND o.concept_id = ${preArtStateOfStay} "
            + " AND o.obs_datetime BETWEEN :onOrAfter AND :onOrBefore "
            + " AND o.value_coded = ${suspendedConcept} "
            + " GROUP BY patient_id"
            + ") max_susp "
            + "WHERE max_susp.patient_id NOT IN ("
            + " SELECT p.patient_id "
            + " FROM patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " WHERE p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND e.encounter_type IN (${fila}) "
            + " AND e.location_id = :location "
            + " AND e.encounter_datetime > max_susp.suspended_date "
            + " AND e.encounter_datetime <= :onOrBefore "
            + " UNION "
            + " SELECT p.patient_id "
            + " FROM patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o "
            + " ON e.encounter_id = o.encounter_id "
            + " WHERE p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND e.encounter_type = ${mcDrugPickup} "
            + " AND e.location_id = :location "
            + " AND o.concept_id = ${drugPickup} "
            + " AND o.value_datetime > max_susp.suspended_date "
            + " AND o.value_datetime <= :onOrBefore "
            + ")"
            + " GROUP BY max_susp.patient_id "
            + ") max_final_suspended_patients";
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(sql);
  }

  /**
   * <b>Description:</b> Fetches Patients with Last registered Line Treatment equals to (1st Line)
   * of without information regarding the therapeutic line
   *
   * @return {@link String}
   */
  public static String getPatientsWithLastTherapeuticLineEqualsToFirstLineOrWithoutInformation(
      int adultoSeguimentoEncounterType, int therapeuticLineConcept, int firstLineConcept) {

    String query =
        " SELECT patient_id "
            + "FROM   (SELECT base_tbl.patient_id "
            + "        FROM   (SELECT p.patient_id, "
            + "                       (SELECT e.encounter_id "
            + "                        FROM   encounter e "
            + "                               JOIN obs o "
            + "                                 ON o.encounter_id = e.encounter_id "
            + "                                    AND e.encounter_type = ${adultoSeguimentoEncounterType} "
            + "                                    AND o.concept_id = ${therapeuticLineConcept} "
            + "                                    AND o.location_id = :location "
            + "                                    AND o.voided = 0 "
            + "                                    AND e.encounter_datetime <= :onOrBefore "
            + "                                    AND e.voided = 0 "
            + "                        WHERE  e.patient_id = p.patient_id "
            + "                        ORDER  BY o.obs_id DESC "
            + "                        LIMIT  1) last_therapeutic_line_encounter "
            + "                FROM   patient p "
            + "                WHERE  p.voided = 0) base_tbl "
            + "               JOIN (SELECT obs.encounter_id "
            + "                     FROM   obs "
            + "                     WHERE  obs.value_coded = ${firstLineConcept} "
            + "                            AND obs.concept_id = ${therapeuticLineConcept} "
            + "                            AND obs.location_id = :location "
            + "                            AND obs.voided = 0) inner_tbl "
            + "                 ON inner_tbl.encounter_id = "
            + "                    base_tbl.last_therapeutic_line_encounter) "
            + "       last_1st_line_tbl "
            + " UNION "
            + "(SELECT enc.patient_id "
            + " FROM   encounter enc "
            + " JOIN patient p "
            + " ON p.patient_id = enc.patient_id "
            + " WHERE  enc.patient_id NOT IN (SELECT enc.patient_id "
            + "         FROM   encounter enc "
            + "         JOIN obs o "
            + "         ON o.encounter_id = enc.encounter_id "
            + "         WHERE o.concept_id = ${therapeuticLineConcept} "
            + "         AND enc.location_id = :location "
            + "         AND enc.voided = 0 "
            + "         AND o.location_id = :location "
            + "         AND o.voided = 0"
            + "         AND enc.encounter_datetime <= :onOrBefore) "
            + " AND enc.encounter_type = ${adultoSeguimentoEncounterType} "
            + " AND enc.encounter_datetime <= :onOrBefore "
            + " AND enc.voided = 0 "
            + " AND enc.location_id = :location "
            + " AND p.voided = 0 GROUP  BY patient_id  )";

    Map<String, Integer> map = new HashMap<>();
    map.put("adultoSeguimentoEncounterType", adultoSeguimentoEncounterType);
    map.put("therapeuticLineConcept", therapeuticLineConcept);
    map.put("firstLineConcept", firstLineConcept);

    StringSubstitutor sub = new StringSubstitutor(map);
    return sub.replace(query);
  }

  /**
   * <b>Description:</b> Number of Deceased patients in the actual cohort
   *
   * @return {@link String}
   */
  public static String getDeceasedPatients(
      Integer artProgram,
      Integer patientHasDiedWorkflowState,
      Integer buscaActivaEncounterType,
      Integer visitaApoioReintegracaoParteAEncounterType,
      Integer visitApoioReintegracaoBEncounterType,
      Integer patientFoundConcept,
      Integer noConcept,
      Integer reasonPatientNotFoundConcept,
      Integer patientIsDeadConcept,
      Integer adultoSeguimentoEncounterType,
      Integer masterCardDrugPickupEncounterType,
      Integer stateOfStayOfPreArtPatientConcept,
      Integer stateOfStayOfArtPatientConcept,
      Integer patientHasDiedConcept,
      Integer pediatriaSeguimentoEncounterType,
      Integer arvFarmaciaEncounterType,
      Integer artDatePickupMasterCardConcept) {

    String sql =
        "SELECT patient_id "
            + "FROM   (SELECT patient_id, "
            + "               Max(death_date) death_date "
            + "        FROM   (SELECT p.patient_id, "
            + "                       ps.start_date death_date "
            + "                FROM   patient p "
            + "                       JOIN patient_program pp "
            + "                         ON p.patient_id = pp.patient_id "
            + "                       JOIN patient_state ps "
            + "                         ON pp.patient_program_id = ps.patient_program_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND pp.voided = 0 "
            + "                       AND pp.program_id = ${artProgram} "
            + "                       AND pp.location_id = :location "
            + "                       AND ps.voided = 0 "
            + "                       AND ps.state = ${patientHasDiedWorkflowState} "
            + "                       AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore "
            + "                       AND ps.end_date IS NULL "
            + "                UNION "
            + "                SELECT p.person_id, "
            + "                       p.death_date "
            + "                FROM   person p "
            + "                WHERE  p.voided = 0"
            + "                   AND p.dead = 1 "
            + "                   AND p.death_date BETWEEN :onOrAfter AND :onOrBefore "
            + "                UNION "
            + "                SELECT e.patient_id, "
            + "                       visit_card.death_date "
            + "                FROM   (SELECT p.patient_id, "
            + "                               Max(e.encounter_datetime) death_date "
            + "                        FROM   patient p "
            + "                               JOIN encounter e "
            + "                                 ON p.patient_id = e.patient_id "
            + "                               JOIN obs notfound "
            + "                                 ON e.encounter_id = notfound.encounter_id "
            + "                               JOIN obs reason "
            + "                                 ON e.encounter_id = reason.encounter_id "
            + "                        WHERE  p.voided = 0 "
            + "                               AND e.voided = 0 "
            + "                               AND e.encounter_type IN ( ${buscaActivaEncounterType}, ${visitaApoioReintegracaoParteAEncounterType}, ${visitApoioReintegracaoBEncounterType} ) "
            + "                               AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore "
            + "                               AND notfound.voided = 0 "
            + "                               AND notfound.concept_id = ${patientFoundConcept} "
            + "                               AND notfound.value_coded = ${noConcept} "
            + "                               AND reason.voided = 0 "
            + "                               AND reason.concept_id = ${reasonPatientNotFoundConcept} "
            + "                               AND reason.value_coded = ${patientIsDeadConcept} "
            + "                        GROUP  BY p.patient_id) visit_card "
            + "                       JOIN encounter e "
            + "                         ON visit_card.patient_id = e.patient_id "
            + "                            AND visit_card.death_date = e.encounter_datetime "
            + "                UNION "
            + "                SELECT p.patient_id, "
            + "                       e.encounter_datetime death_date "
            + "                FROM   patient p "
            + "                       JOIN encounter e "
            + "                         ON p.patient_id = e.patient_id "
            + "                       JOIN obs o "
            + "                         ON e.encounter_id = o.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type IN ( ${adultoSeguimentoEncounterType}, ${masterCardDrugPickupEncounterType} ) "
            + "                       AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore "
            + "                       AND o.voided = 0 "
            + "                       AND o.concept_id IN ( ${stateOfStayOfPreArtPatientConcept}, ${stateOfStayOfArtPatientConcept} ) "
            + "                       AND o.value_coded = ${patientHasDiedConcept}) dead "
            + "        GROUP  BY patient_id) max_dead "
            + "WHERE  patient_id NOT IN (SELECT p.patient_id "
            + "                          FROM   patient p "
            + "                                 JOIN encounter e "
            + "                                   ON p.patient_id = e.patient_id "
            + "                          WHERE  p.voided = 0 "
            + "                                 AND e.voided = 0 "
            + "                                 AND e.encounter_type IN ( ${adultoSeguimentoEncounterType}, ${pediatriaSeguimentoEncounterType}, ${arvFarmaciaEncounterType} ) "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_datetime > death_date "
            + "                          UNION "
            + "                          SELECT p.patient_id "
            + "                          FROM   patient p "
            + "                                 JOIN encounter e "
            + "                                   ON p.patient_id = e.patient_id "
            + "                                 JOIN obs o "
            + "                                   ON e.encounter_id = o.encounter_id "
            + "                          WHERE  p.voided = 0 "
            + "                                 AND e.voided = 0 "
            + "                                 AND e.encounter_type = ${masterCardDrugPickupEncounterType} "
            + "                                 AND e.location_id = :location "
            + "                                 AND o.concept_id = ${artDatePickupMasterCardConcept} "
            + "                                 AND o.value_datetime > death_date); ";

    Map<String, Integer> map = new HashMap<>();

    StringSubstitutor sub = new StringSubstitutor(map);
    map.put("artProgram", artProgram);
    map.put("patientHasDiedWorkflowState", patientHasDiedWorkflowState);
    map.put("buscaActivaEncounterType", buscaActivaEncounterType);
    map.put(
        "visitaApoioReintegracaoParteAEncounterType", visitaApoioReintegracaoParteAEncounterType);
    map.put("visitApoioReintegracaoBEncounterType", visitApoioReintegracaoBEncounterType);
    map.put("patientFoundConcept", patientFoundConcept);
    map.put("noConcept", noConcept);
    map.put("reasonPatientNotFoundConcept", reasonPatientNotFoundConcept);
    map.put("patientIsDeadConcept", patientIsDeadConcept);
    map.put("adultoSeguimentoEncounterType", adultoSeguimentoEncounterType);
    map.put("masterCardDrugPickupEncounterType", masterCardDrugPickupEncounterType);
    map.put("stateOfStayOfPreArtPatientConcept", stateOfStayOfPreArtPatientConcept);
    map.put("stateOfStayOfArtPatientConcept", stateOfStayOfArtPatientConcept);
    map.put("patientHasDiedConcept", patientHasDiedConcept);
    map.put("pediatriaSeguimentoEncounterType", pediatriaSeguimentoEncounterType);
    map.put("arvFarmaciaEncounterType", arvFarmaciaEncounterType);
    map.put("artDatePickupMasterCardConcept", artDatePickupMasterCardConcept);
    return sub.replace(sql);
  }

  /**
   * <b>Description:</b> All patients with last registered Line Treatment equal to “Second Line” and
   * encounter date < = MonthEndDate in “Master Card – Ficha Clinica”
   *
   * @return {@link String}
   */
  public static String
      getPatientsWithLastObsInSecondTherapeuticLineInMasterCardFichaClinicaBeforeMonthEndDate(
          int adultoSeguimentoEncounterType, int therapeuticLineConcept, int secondLineConcept) {
    String sql =
        "SELECT patient_id FROM( "
            + " SELECT p.patient_id,MAX(o.obs_datetime) last_obs"
            + " FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type = ${adultoSeguimentoEncounterType} "
            + " AND  o.concept_id = ${therapeuticLineConcept} "
            + " AND o.value_coded = ${secondLineConcept} "
            + " AND e.encounter_datetime <= :endDate"
            + " AND e.location_id= :location GROUP BY p.patient_id) tt ";

    Map<String, Integer> map = new HashMap<>();

    StringSubstitutor sub = new StringSubstitutor(map);
    map.put("adultoSeguimentoEncounterType", adultoSeguimentoEncounterType);
    map.put("therapeuticLineConcept", therapeuticLineConcept);
    map.put("secondLineConcept", secondLineConcept);
    return sub.replace(sql);
  }
}
