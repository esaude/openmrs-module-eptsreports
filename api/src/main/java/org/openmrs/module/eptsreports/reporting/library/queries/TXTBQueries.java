package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringSubstitutor;

public class TXTBQueries {

  /**
   * Copied straight from INICIO DE TRATAMENTO ARV - NUM PERIODO: INCLUI TRANSFERIDOS DE COM DATA DE
   * INICIO CONHECIDA (SQL) SqlCohortDefinition#91787a86-0362-4820-a4ee-025d5501198b in backup
   *
   * @return sql
   */
  public static String arvTreatmentIncludesTransfersFromWithKnownStartData(
      Integer arvPlanConceptId,
      Integer startDrugsConceptId,
      Integer historicalDrugsStartDateConceptId,
      Integer artProgramId,
      Integer pharmacyEncounterTypeId,
      Integer artAdultFollowupEncounterTypeId,
      Integer artPedFollowupEncounterTypeId) {
    String encounterTypeIds =
        StringUtils.join(
            Arrays.asList(
                pharmacyEncounterTypeId,
                artAdultFollowupEncounterTypeId,
                artPedFollowupEncounterTypeId),
            ",");
    return String.format(
        "SELECT patient_id FROM (SELECT patient_id, Min(data_inicio) data_inicio "
            + "FROM (SELECT p.patient_id, Min(e.encounter_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "WHERE e.voided = 0 AND o.voided = 0 AND p.voided = 0 AND e.encounter_type IN ( %s ) "
            + "AND o.concept_id = %s AND o.value_coded = %s AND e.encounter_datetime <= :endDate "
            + "AND e.location_id = :location GROUP BY p.patient_id "
            + "UNION SELECT p.patient_id, Min(value_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type IN ( %s ) "
            + "AND o.concept_id = %s AND o.value_datetime IS NOT NULL AND o.value_datetime <= :endDate AND e.location_id = :location "
            + "GROUP BY p.patient_id UNION SELECT pg.patient_id, date_enrolled data_inicio FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id = pg.patient_id WHERE pg.voided = 0 AND p.voided = 0 "
            + "AND program_id = %s AND date_enrolled <= :endDate AND location_id = :location "
            + "UNION SELECT e.patient_id, Min(e.encounter_datetime) AS data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id WHERE p.voided = 0 AND e.encounter_type = %s "
            + "AND e.voided = 0 AND e.encounter_datetime <= :endDate AND e.location_id = :location GROUP BY p.patient_id) inicio_real "
            + "GROUP BY patient_id)inicio WHERE data_inicio BETWEEN :startDate AND :endDate ",
        encounterTypeIds,
        arvPlanConceptId,
        startDrugsConceptId,
        encounterTypeIds,
        historicalDrugsStartDateConceptId,
        artProgramId,
        pharmacyEncounterTypeId);
  }

  // exited by either transfer out, treatment suspension, treatment abandoned
  // or death of patient
  public static String patientsAtProgramStates(Integer artProgramId, List<Integer> stateIds) {
    return String.format(
        "SELECT pg.patient_id FROM patient p  "
            + "INNER JOIN patient_program pg  ON p.patient_id = pg.patient_id  "
            + "INNER JOIN patient_state ps  ON pg.patient_program_id = ps.patient_program_id "
            + "WHERE pg.voided = 0  AND ps.voided = 0  AND p.voided = 0  AND pg.program_id = %s "
            + "AND ps.state IN ( %s )  AND ps.end_date IS NULL  AND ps.start_date <= :endDate  "
            + "AND location_id = :location ",
        artProgramId, StringUtils.join(stateIds, ","));
  }

  /** ABANDONO NÃO NOTIFICADO - TARV SqlCohortDefinition#a1145104-132f-460b-b85e-ea265916625b */
  public static String abandonedWithNoNotification(AbandonedWithoutNotificationParams params) {
    String stateIds =
        StringUtils.join(
            Arrays.asList(
                params.transferOutStateId,
                params.treatmentSuspensionStateId,
                params.treatmentAbandonedStateId,
                params.deathStateId),
            ",");
    return String.format(
        "SELECT patient_id FROM (SELECT p.patient_id, Max(encounter_datetime) encounter_datetime FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id WHERE p.voided = 0 AND e.voided = 0 "
            + "AND e.encounter_type = %s AND e.location_id = :location AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) max_frida "
            + "INNER JOIN obs o ON o.person_id = max_frida.patient_id WHERE max_frida.encounter_datetime = o.obs_datetime AND o.voided = 0 "
            + "AND o.concept_id = %s AND o.location_id = :location AND patient_id "
            + "NOT IN (SELECT pg.patient_id FROM patient p INNER JOIN patient_program pg ON p.patient_id = pg.patient_id "
            + "INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id WHERE pg.voided = 0 AND ps.voided = 0 "
            + "AND p.voided = 0 AND pg.program_id = %s AND ps.state IN ( %s ) AND ps.end_date IS NULL AND ps.start_date <= :endDate "
            + "AND location_id = :location) AND patient_id NOT IN(SELECT patient_id FROM "
            + "(SELECT p.patient_id, Max(encounter_datetime) encounter_datetime FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id WHERE p.voided = 0 AND e.voided = 0 "
            + "AND e.encounter_type IN ( %s, %s ) AND e.location_id = :location AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) max_mov "
            + "INNER JOIN obs o ON o.person_id = max_mov.patient_id WHERE max_mov.encounter_datetime = o.obs_datetime AND o.voided = 0 "
            + "AND o.concept_id = %s AND o.location_id = :location AND Datediff(:endDate, o.value_datetime) <= 60) AND patient_id "
            + "NOT IN(SELECT abandono.patient_id FROM (SELECT pg.patient_id FROM patient p INNER JOIN patient_program pg ON p.patient_id = pg.patient_id "
            + "INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id WHERE pg.voided = 0 AND ps.voided = 0 AND p.voided = 0 AND pg.program_id = %s "
            + "AND ps.state = %s AND ps.end_date IS NULL AND ps.start_date <= :endDate AND location_id = :location)abandono "
            + "INNER JOIN (SELECT max_frida.patient_id, max_frida.encounter_datetime, o.value_datetime FROM "
            + "(SELECT p.patient_id, Max(encounter_datetime) encounter_datetime FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id WHERE p.voided = 0 AND e.voided = 0 AND e.encounter_type = %s"
            + " AND e.location_id = :location "
            + "AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) max_frida INNER JOIN obs o ON o.person_id = max_frida.patient_id "
            + "WHERE max_frida.encounter_datetime = o.obs_datetime AND o.voided = 0 AND o.concept_id = %s AND o.location_id = :location) ultimo_fila "
            + "ON abandono.patient_id = ultimo_fila.patient_id WHERE Datediff(:endDate, ultimo_fila.value_datetime) < 60) AND Datediff(:endDate, o.value_datetime) >= 60;",
        params.pharmacyEncounterTypeId,
        params.returnVisitDateForARVDrugConceptId,
        params.programId,
        stateIds,
        params.artAdultFollowupEncounterTypeId,
        params.artPedInicioEncounterTypeId,
        params.returnVisitDateConceptId,
        params.programId,
        params.treatmentAbandonedStateId,
        params.pharmacyEncounterTypeId,
        params.returnVisitDateForARVDrugConceptId);
  }

  public static String inTBProgramWithinReportingPeriodAtLocation(Integer tbProgramId) {
    return String.format(
        "select pg.patient_id from patient p inner join "
            + "patient_program pg on p.patient_id=pg.patient_id "
            + "where pg.voided=0 and p.voided=0 and program_id=%s AND pg.date_completed is null "
            + "  and date_enrolled between :startDate and :endDate and location_id=:location",
        tbProgramId);
  }

  /**
   * Patients with Pulmonary TB Date in Patient Clinical Record of ART date TB
   *
   * @param encounterTypeId
   * @param pulmonaryTBConcept
   * @param yesConcept
   * @return
   */
  public static String pulmonaryTB(
      Integer encounterTypeId, Integer pulmonaryTBConcept, Integer yesConcept) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s AND o.concept_id = %s AND o.value_coded = %s AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, pulmonaryTBConcept, yesConcept);
  }

  /**
   * Patients marked as “Tratamento TB= Inicio (I) ” in Ficha Clinica Master Card
   *
   * @param encounterTypeId
   * @param tbTreatmentPlan
   * @param startDrugs
   * @return
   */
  public static String tbTreatmentStart(
      Integer encounterTypeId, Integer tbTreatmentPlan, Integer startDrugs) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s AND o.concept_id = %s  AND o.value_coded = %s AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, tbTreatmentPlan, startDrugs);
  }

  /**
   * TUBERCULOSIS SYMPTOMS
   *
   * @param encounterTypeId
   * @param tbSymptomsId
   * @param yesConcept
   * @param noConcept
   * @return
   */
  public static String tuberculosisSympots(
      Integer encounterTypeId, Integer tbSymptomsId, Integer yesConcept, Integer noConcept) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, tbSymptomsId, yesConcept, noConcept);
  }

  /**
   * ACTIVE TUBERCULOSIS
   *
   * @param encounterTypeId
   * @param activeTuberculosis
   * @param yesConcept
   * @return
   */
  public static String activeTuberculosis(
      Integer encounterTypeId, Integer activeTuberculosis, Integer yesConcept) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND o.concept_id = %s  AND o.value_coded = %s "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, activeTuberculosis, yesConcept);
  }

  /**
   * TB OBSERVATIONS
   *
   * @param encounterTypeId
   * @param tbObservation
   * @param fever
   * @param weight
   * @param nightweats
   * @param cough
   * @param asthenia
   * @param cohabitant
   * @param lymphadenopathy
   * @return
   */
  public static String tbObservation(
      Integer encounterTypeId,
      Integer tbObservation,
      Integer fever,
      Integer weight,
      Integer nightweats,
      Integer cough,
      Integer asthenia,
      Integer cohabitant,
      Integer lymphadenopathy) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s "
            + "OR o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId,
        tbObservation,
        fever,
        weight,
        nightweats,
        cough,
        asthenia,
        cohabitant,
        lymphadenopathy);
  }

  /**
   * APPLICATION FOR LABORATORY RESEARCH
   *
   * @param encounterTypeId
   * @param applicationForLaboratory
   * @param tbGenexpertTest
   * @param cultureTest
   * @param testTBLAM
   * @return
   */
  public static String applicationForLaboratoryResearch(
      Integer encounterTypeId,
      Integer applicationForLaboratory,
      Integer tbGenexpertTest,
      Integer cultureTest,
      Integer testTBLAM) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, applicationForLaboratory, tbGenexpertTest, cultureTest, testTBLAM);
  }

  /**
   * TB GENEXPERT TEST
   *
   * @param encounterTypeId
   * @param tbGenexpertTest
   * @param positive
   * @param negative
   * @return
   */
  public static String tbGenexpertTest(
      Integer encounterTypeId, Integer tbGenexpertTest, Integer positive, Integer negative) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, tbGenexpertTest, positive, negative);
  }

  /**
   * CULTURE TEST
   *
   * @param encounterTypeId
   * @param cultureTest
   * @param positive
   * @param negative
   * @return
   */
  public static String cultureTest(
      Integer encounterTypeId, Integer cultureTest, Integer positive, Integer negative) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, cultureTest, positive, negative);
  }

  /**
   * Test TB LAM
   *
   * @param encounterTypeId
   * @param testTBLAM
   * @param positive
   * @param negative
   * @return
   */
  public static String testTBLAM(
      Integer encounterTypeId, Integer testTBLAM, Integer positive, Integer negative) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, testTBLAM, positive, negative);
  }

  public static String resultForBasiloscopia(
      Integer encounterTypeId, Integer basiloscopia, Integer positive, Integer negative) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, basiloscopia, positive, negative);
  }

  public static String dateObs(
      Integer questionId, List<Integer> encounterTypeIds, boolean startDate) {
    String sql =
        String.format(
            "select person_id from obs "
                + "where concept_id = %s and encounter_id in("
                + "select distinct encounter_id "
                + "from encounter "
                + "where encounter_type in(%s)) and location_id = :location and ",
            questionId, StringUtils.join(encounterTypeIds, ","));
    if (startDate) {
      sql += "value_datetime >= :startDate and value_datetime <= :endDate and voided=0";
    } else {
      sql += "value_datetime <= :endDate and voided=0";
    }
    return sql;
  }

  public static String dateObsWithinXMonthsBeforeStartDate(
      Integer questionId, List<Integer> encounterTypeIds, Integer xMonths) {
    return String.format(
        "select person_id from obs "
            + "where concept_id = %s and encounter_id in"
            + "( select distinct encounter_id from encounter "
            + "where encounter_type in(%s)) and location_id = :location "
            + "and value_datetime >= DATE_SUB(:startDate, INTERVAL "
            + "%s MONTH) "
            + "and value_datetime < :startDate and voided=0",
        questionId, StringUtils.join(encounterTypeIds, ","), xMonths);
  }

  public static String encounterObs(Integer encounterTypeId) {
    return String.format(
        "select distinct patient_id from encounter where encounter_type =%s and location_id = :location and encounter_datetime <= :endDate and voided=0;",
        encounterTypeId);
  }

  public static String patientWithFirstDrugPickupEncounterInReportingPeriod(
      Integer encounterTypeId) {
    return String.format(
        "SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "WHERE p.voided=0 AND e.encounter_type=%s AND e.voided=0 AND e.encounter_datetime>=:startDate AND e.encounter_datetime<=:endDate AND e.location_id=:location GROUP BY p.patient_id",
        encounterTypeId);
  }

  /**
   * Get patients who sent specimen within date boundaries
   *
   * @return String
   */
  public static String getPatientsWhoHaveSentSpecimen(
      int laboratory,
      int applicationForLaboratoryResearch,
      int fichaClinica,
      int basiloscopiaExam,
      int genexpertTest,
      int tbLamTest,
      int cultureTest,
      int positive,
      int negative) {
    String query =
        "SELECT specimen.patient_id "
            + "FROM   ( "
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                         ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                         ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${laboratory} "
            + "                 AND exam.concept_id = ${basiloscopiaExam} "
            + "                 AND (exam.value_coded = ${negative} OR exam.value_coded = ${positive}) "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + "       UNION "
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                 ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                 ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${fichaClinica} "
            + "                 AND exam.concept_id = ${genexpertTest} "
            + "                 AND (exam.value_coded = ${negative} OR exam.value_coded = ${positive}) "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + "       UNION "
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                 ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                 ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${fichaClinica} "
            + "                 AND exam.concept_id = ${tbLamTest} "
            + "                 AND (exam.value_coded = ${negative} OR exam.value_coded = ${positive}) "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + "       UNION "
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                 ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                 ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${fichaClinica} "
            + "                 AND exam.concept_id = ${cultureTest} "
            + "                 AND (exam.value_coded = ${negative} OR exam.value_coded = ${positive}) "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + "       UNION "
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                 ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                 ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${fichaClinica} "
            + "                 AND exam.concept_id = ${applicationForLaboratoryResearch} "
            + "                 AND (exam.value_coded = ${genexpertTest} OR exam.value_coded = ${cultureTest} OR exam.value_coded = ${tbLamTest}) "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + ") specimen";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("laboratory", laboratory);
    valuesMap.put("applicationForLaboratoryResearch", applicationForLaboratoryResearch);
    valuesMap.put("fichaClinica", fichaClinica);
    valuesMap.put("basiloscopiaExam", basiloscopiaExam);
    valuesMap.put("genexpertTest", genexpertTest);
    valuesMap.put("tbLamTest", tbLamTest);
    valuesMap.put("cultureTest", cultureTest);
    valuesMap.put("positive", positive);
    valuesMap.put("negative", negative);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  /**
   * Get patients who have a GeneXpert Positivo or Negativo registered in the investigations - lab
   * results - ficha clinica - mastercard OR Get patients who have a GeneXpert request registered in
   * the investigations - lab results - ficha clinica - mastercard
   *
   * @return String
   */
  public static String getPatientsWhoHaveGeneXpert(
      int applicationForLaboratoryResearch,
      int fichaClinica,
      int genexpertTest,
      int positive,
      int negative) {
    String query =
        "SELECT genexpert.patient_id "
            + "FROM   ( "
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                 ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                 ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${fichaClinica} "
            + "                 AND exam.concept_id = ${genexpertTest} "
            + "                 AND (exam.value_coded = ${negative} OR exam.value_coded = ${positive}) "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + "       UNION "
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                 ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                 ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${fichaClinica} "
            + "                 AND exam.concept_id = ${applicationForLaboratoryResearch} "
            + "                 AND exam.value_coded = ${genexpertTest} "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + ") genexpert";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("applicationForLaboratoryResearch", applicationForLaboratoryResearch);
    valuesMap.put("fichaClinica", fichaClinica);
    valuesMap.put("genexpertTest", genexpertTest);
    valuesMap.put("positive", positive);
    valuesMap.put("negative", negative);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  /**
   * Smear Microscopy - Get patients who have a Basiloscopia Positivo or Negativo registered in the
   * laboratory form encounter type 13 Except patients identified in GeneXpert
   *
   * @return String
   */
  public static String getSmearMicroscopyOnly(
      int laboratory,
      int fichaClinica,
      int basiloscopiaExam,
      int genexpertTest,
      int positive,
      int negative) {
    String query =
        "SELECT smearmicroscopy.patient_id "
            + "FROM ( "
            + "    SELECT p.patient_id "
            + "    FROM patient p "
            + "        INNER JOIN encounter e "
            + "        ON p.patient_id = e.patient_id "
            + "        INNER JOIN obs exam "
            + "        ON e.encounter_id = exam.encounter_id "
            + "    WHERE p.voided = 0 "
            + "        AND e.voided = 0 "
            + "        AND exam.voided = 0 "
            + "        AND e.encounter_type = ${laboratory} "
            + "        AND exam.concept_id = ${basiloscopiaExam} "
            + "        AND (exam.value_coded = ${negative} OR exam.value_coded = ${positive}) "
            + "        AND e.encounter_datetime >= :startDate "
            + "        AND e.encounter_datetime <= :endDate "
            + "    GROUP BY p.patient_id "
            + ") smearmicroscopy "
            + "WHERE smearmicroscopy.patient_id NOT IN (SELECT p.patient_id "
            + "                                         FROM patient p "
            + "                                             INNER JOIN encounter e "
            + "                                             ON p.patient_id = e.patient_id "
            + "                                             INNER JOIN obs exam "
            + "                                             ON e.encounter_id = exam.encounter_id "
            + "                                         WHERE p.voided = 0 "
            + "                                             AND e.voided = 0 "
            + "                                             AND exam.voided = 0 "
            + "                                             AND e.encounter_type = ${fichaClinica} "
            + "                                             AND exam.concept_id = ${genexpertTest} "
            + "                                             AND (exam.value_coded = ${negative} OR exam.value_coded = ${positive}) "
            + "                                             AND e.encounter_datetime >= :startDate "
            + "                                             AND e.encounter_datetime <= :endDate "
            + "                                         GROUP BY p.patient_id) "
            + "GROUP BY smearmicroscopy.patient_id";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("laboratory", laboratory);
    valuesMap.put("fichaClinica", fichaClinica);
    valuesMap.put("basiloscopiaExam", basiloscopiaExam);
    valuesMap.put("genexpertTest", genexpertTest);
    valuesMap.put("positive", positive);
    valuesMap.put("negative", negative);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  /**
   * Get patients who have a Additional Test AND Not GeneXpert AND Not Smear Microscopy Only
   *
   * @return String
   */
  public static String getAdditionalTest(
      int laboratory,
      int applicationForLaboratoryResearch,
      int fichaClinica,
      int basiloscopiaExam,
      int genexpertTest,
      int tbLamTest,
      int cultureTest,
      int positive,
      int negative) {
    String query =
        "SELECT tests.patient_id "
            + "FROM   ( "
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                         ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                         ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${fichaClinica} "
            + "                 AND exam.concept_id = ${tbLamTest} "
            + "                 AND (exam.value_coded = ${negative} OR exam.value_coded = ${positive}) "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + "       UNION "
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                 ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                 ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${fichaClinica} "
            + "                 AND exam.concept_id = ${cultureTest} "
            + "                 AND (exam.value_coded = ${negative} OR exam.value_coded = ${positive}) "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + "       UNION "
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                 ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                 ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${fichaClinica} "
            + "                 AND exam.concept_id = ${applicationForLaboratoryResearch} "
            + "                 AND (exam.value_coded = ${cultureTest} OR exam.value_coded = ${tbLamTest}) "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + ") tests "
            + "WHERE tests.patient_id NOT IN (SELECT p.patient_id "
            + "                                   FROM patient p "
            + "                                       INNER JOIN encounter e "
            + "                                       ON p.patient_id = e.patient_id "
            + "                                       INNER JOIN obs exam "
            + "                                       ON e.encounter_id = exam.encounter_id "
            + "                                   WHERE p.voided = 0 "
            + "                                       AND e.voided = 0 "
            + "                                       AND exam.voided = 0 "
            + "                                       AND e.encounter_type = ${fichaClinica} "
            + "                                       AND exam.concept_id = ${genexpertTest} "
            + "                                       AND (exam.value_coded = ${negative} OR exam.value_coded = ${positive}) "
            + "                                       AND e.encounter_datetime >= :startDate "
            + "                                       AND e.encounter_datetime <= :endDate "
            + "                                   GROUP BY p.patient_id) "
            + "GROUP BY tests.patient_id";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("laboratory", laboratory);
    valuesMap.put("applicationForLaboratoryResearch", applicationForLaboratoryResearch);
    valuesMap.put("fichaClinica", fichaClinica);
    valuesMap.put("basiloscopiaExam", basiloscopiaExam);
    valuesMap.put("genexpertTest", genexpertTest);
    valuesMap.put("tbLamTest", tbLamTest);
    valuesMap.put("cultureTest", cultureTest);
    valuesMap.put("positive", positive);
    valuesMap.put("negative", negative);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  /**
   * Get patients who have positive results returned
   *
   * @return String
   */
  public static String getPositiveResultsReturned(
      int laboratory,
      int fichaClinica,
      int basiloscopiaExam,
      int genexpertTest,
      int tbLamTest,
      int cultureTest,
      int positive,
      int negative) {
    String query =
        "SELECT positiveResults.patient_id "
            + "FROM   ("
            + "        SELECT p.patient_id "
            + "               FROM patient p "
            + "                   INNER JOIN encounter e "
            + "                           ON p.patient_id = e.patient_id "
            + "                   INNER JOIN obs exam "
            + "                           ON e.encounter_id = exam.encounter_id "
            + "               WHERE p.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND exam.voided = 0 "
            + "                   AND e.encounter_type = ${fichaClinica} "
            + "                   AND exam.concept_id = ${genexpertTest} "
            + "                   AND exam.value_coded = ${positive} "
            + "                   AND e.encounter_datetime >= :startDate "
            + "                   AND e.encounter_datetime <= :endDate "
            + "               GROUP BY p.patient_id"
            + "       UNION"
            + "       SELECT p.patient_id"
            + "              FROM patient p"
            + "                  INNER JOIN encounter e"
            + "                          ON p.patient_id = e.patient_id"
            + "                  INNER JOIN obs exam"
            + "                          ON e.encounter_id = exam.encounter_id"
            + "              WHERE p.voided = 0"
            + "                  AND e.voided = 0"
            + "                  AND exam.voided = 0"
            + "                  AND e.encounter_type = ${laboratory} "
            + "                  AND exam.concept_id = ${basiloscopiaExam} "
            + "                  AND exam.value_coded = ${positive} "
            + "                  AND e.encounter_datetime >= :startDate "
            + "                  AND e.encounter_datetime <= :endDate "
            + "              GROUP BY p.patient_id"
            + "       UNION"
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                         ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                         ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${fichaClinica} "
            + "                 AND exam.concept_id = ${tbLamTest} "
            + "                 AND exam.value_coded = ${positive} "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + "       UNION "
            + "       SELECT p.patient_id "
            + "           FROM  patient p "
            + "                 INNER JOIN encounter e "
            + "                         ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs exam "
            + "                         ON e.encounter_id = exam.encounter_id "
            + "           WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND exam.voided = 0 "
            + "                 AND e.encounter_type = ${fichaClinica} "
            + "                 AND exam.concept_id = ${cultureTest} "
            + "                 AND exam.value_coded = ${positive} "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "           GROUP BY p.patient_id "
            + ") positiveResults "
            + "GROUP BY positiveResults.patient_id";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("laboratory", laboratory);
    valuesMap.put("fichaClinica", fichaClinica);
    valuesMap.put("basiloscopiaExam", basiloscopiaExam);
    valuesMap.put("genexpertTest", genexpertTest);
    valuesMap.put("tbLamTest", tbLamTest);
    valuesMap.put("cultureTest", cultureTest);
    valuesMap.put("positive", positive);
    valuesMap.put("negative", negative);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  public static class AbandonedWithoutNotificationParams {
    protected Integer programId;
    protected Integer returnVisitDateConceptId;
    protected Integer returnVisitDateForARVDrugConceptId;
    protected Integer pharmacyEncounterTypeId;
    protected Integer artAdultFollowupEncounterTypeId;
    protected Integer artPedInicioEncounterTypeId;
    protected Integer transferOutStateId;
    protected Integer treatmentSuspensionStateId;
    protected Integer treatmentAbandonedStateId;
    protected Integer deathStateId;

    public AbandonedWithoutNotificationParams programId(Integer programId) {
      this.programId = programId;
      return this;
    }

    public AbandonedWithoutNotificationParams returnVisitDateConceptId(
        Integer returnVisitDateConceptId) {
      this.returnVisitDateConceptId = returnVisitDateConceptId;
      return this;
    }

    public AbandonedWithoutNotificationParams returnVisitDateForARVDrugConceptId(
        Integer returnVisitDateForARVDrugConceptId) {
      this.returnVisitDateForARVDrugConceptId = returnVisitDateForARVDrugConceptId;
      return this;
    }

    public AbandonedWithoutNotificationParams pharmacyEncounterTypeId(
        Integer pharmacyEncounterTypeId) {
      this.pharmacyEncounterTypeId = pharmacyEncounterTypeId;
      return this;
    }

    public AbandonedWithoutNotificationParams artAdultFollowupEncounterTypeId(
        Integer artAdultFollowupEncounterTypeId) {
      this.artAdultFollowupEncounterTypeId = artAdultFollowupEncounterTypeId;
      return this;
    }

    public AbandonedWithoutNotificationParams artPedInicioEncounterTypeId(
        Integer artPedInicioEncounterTypeId) {
      this.artPedInicioEncounterTypeId = artPedInicioEncounterTypeId;
      return this;
    }

    public AbandonedWithoutNotificationParams transferOutStateId(Integer transferOutStateId) {
      this.transferOutStateId = transferOutStateId;
      return this;
    }

    public AbandonedWithoutNotificationParams treatmentSuspensionStateId(
        Integer treatmentSuspensionStateId) {
      this.treatmentSuspensionStateId = treatmentSuspensionStateId;
      return this;
    }

    public AbandonedWithoutNotificationParams treatmentAbandonedStateId(
        Integer treatmentAbandonedStateId) {
      this.treatmentAbandonedStateId = treatmentAbandonedStateId;
      return this;
    }

    public AbandonedWithoutNotificationParams deathStateId(Integer deathStateId) {
      this.deathStateId = deathStateId;
      return this;
    }
  }
}
