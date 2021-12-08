package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonQueries {

  private CommonMetadata commonMetadata;
  private HivMetadata hivMetadata;

  @Autowired
  public CommonQueries(CommonMetadata commonMetadata, HivMetadata hivMetadata) {
    this.commonMetadata = commonMetadata;
    this.hivMetadata = hivMetadata;
  }

  /**
   * Patients on TB Treatment
   *
   * @param adultoSeguimentoEncounterTypeId {@link HivMetadata#getAdultoSeguimentoEncounterType()}
   * @param arvPediatriaSeguimentoEncounterTypeId {@link
   *     HivMetadata#getPediatriaSeguimentoEncounterType()} ()}
   * @param tbStartDateConceptId - TB start date Concept ID
   * @param tbEndDateConceptId - TB end date Concept ID
   * @param tbProgramId - TB program ID
   * @param patientStateId - patient state
   * @param activeTBConceptId -
   * @param yesConceptId - ues concept
   * @param tbTreatmentPlanConceptId - TB Treatment Plan Concept
   * @param startDrugsConceptId - start Drugs Concept
   * @param continueRegimenConceptId - Continue Regime Concept
   * @return {@link String}
   */
  public static String getPatientsOnTbTreatmentQuery(
      int adultoSeguimentoEncounterTypeId,
      int arvPediatriaSeguimentoEncounterTypeId,
      int tbStartDateConceptId,
      int tbEndDateConceptId,
      int tbProgramId,
      int patientStateId,
      int activeTBConceptId,
      int yesConceptId,
      int tbTreatmentPlanConceptId,
      int startDrugsConceptId,
      int continueRegimenConceptId,
      int masterCardEncounterType,
      int pulmonaryTBConceptId) {
    Map<String, Integer> map = new HashMap<>();
    map.put("adultoSeguimentoEncounterTypeId", adultoSeguimentoEncounterTypeId);
    map.put("arvPediatriaSeguimentoEncounterTypeId", arvPediatriaSeguimentoEncounterTypeId);
    map.put("tbStartDateConceptId", tbStartDateConceptId);
    map.put("tbEndDateConceptId", tbEndDateConceptId);
    map.put("tbProgramId", tbProgramId);
    map.put("patientStateId", patientStateId);
    map.put("activeTBConceptId", activeTBConceptId);
    map.put("yesConceptId", yesConceptId);
    map.put("tbTreatmentPlanConceptId", tbTreatmentPlanConceptId);
    map.put("startDrugsConceptId", startDrugsConceptId);
    map.put("continueRegimenConceptId", continueRegimenConceptId);
    map.put("masterCardEncounterType", masterCardEncounterType);
    map.put("pulmonaryTBConceptId", pulmonaryTBConceptId);

    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "JOIN encounter e ON p.patient_id = e.patient_id "
            + "JOIN obs "
            + "start ON e.encounter_id = start.encounter_id "
            + "WHERE e.encounter_type IN (${adultoSeguimentoEncounterTypeId}, "
            + "                           ${arvPediatriaSeguimentoEncounterTypeId}) "
            + "  AND start.concept_id = ${tbStartDateConceptId} "
            + "  AND start.voided = 0"
            + "  AND e.voided = 0"
            + "  AND start.value_datetime IS NOT NULL "
            + "  AND start.value_datetime BETWEEN date_sub(:endDate, INTERVAL 6 MONTH) AND :endDate "
            + "  AND e.location_id=  :location  "
            + "  AND p.patient_id NOT IN "
            + "    (SELECT p1.patient_id "
            + "     FROM patient p1 "
            + "     JOIN encounter e1 ON p1.patient_id = e1.patient_id "
            + "     JOIN obs o ON e1.encounter_id = o.encounter_id "
            + "     WHERE e1.encounter_type IN (${adultoSeguimentoEncounterTypeId}, "
            + "                                 ${arvPediatriaSeguimentoEncounterTypeId}) "
            + "       AND o.concept_id = ${tbEndDateConceptId} "
            + "       AND (o.value_datetime IS NOT NULL "
            + "            OR o.value_datetime >   :endDate) "
            + "       AND e.location_id=  :location  ) "
            + "UNION   "
            + "SELECT p.patient_id "
            + "FROM patient p "
            + "JOIN patient_program pp ON p.patient_id=pp.patient_id "
            + "JOIN patient_state ps ON pp.patient_program_id=ps.patient_program_id "
            + "WHERE pp.program_id=${tbProgramId} "
            + "  AND ps.state=${patientStateId} "
            + "  AND pp.location_id=  :location  "
            + "  AND pp.date_enrolled BETWEEN   date_sub(:endDate, INTERVAL 6 MONTH) AND   :endDate "
            + "  AND (pp.date_completed IS NULL "
            + "       OR pp.date_completed >   :endDate) "
            + "  AND p.voided=0 "
            + "  AND ps.voided=0 "
            + "UNION  "
            + "SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e  "
            + "        ON p.patient_id = e.patient_id "
            + "    INNER JOIN obs o  "
            + "        ON e.encounter_id = o.encounter_id "
            + "    INNER JOIN (SELECT p.patient_id, "
            + "            max(e.encounter_datetime) encounter_datetime "
            + "        FROM patient p "
            + "        INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "        INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "        WHERE o.concept_id = ${activeTBConceptId} "
            + "            AND e.location_id =   :location  "
            + "            AND e.encounter_type IN (${adultoSeguimentoEncounterTypeId},${arvPediatriaSeguimentoEncounterTypeId}) "
            + "            AND e.encounter_datetime BETWEEN date_sub(:endDate, INTERVAL 6 MONTH) AND   :endDate "
            + "            AND p.voided = 0 "
            + "            and e.voided = 0 "
            + "            and o.voided = 0 "
            + "        GROUP BY p.patient_id) last  "
            + "                ON p.patient_id = last.patient_id "
            + "    AND e.encounter_datetime = last.encounter_datetime "
            + "WHERE o.value_coded = ${yesConceptId} "
            + "    AND o.concept_id = ${activeTBConceptId} "
            + "    AND e.encounter_type IN (${adultoSeguimentoEncounterTypeId},${arvPediatriaSeguimentoEncounterTypeId}) "
            + "    AND e.voided=0 "
            + "    AND o.voided=0 "
            + "    AND p.voided=0 "
            + "UNION "
            + "SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e  "
            + "        ON p.patient_id = e.patient_id "
            + "    INNER JOIN obs o  "
            + "        ON e.encounter_id = o.encounter_id "
            + "JOIN "
            + "  (SELECT p.patient_id, "
            + "          max(o.obs_datetime) obs_datetime "
            + "   FROM patient p "
            + "   JOIN encounter e ON p.patient_id = e.patient_id "
            + "   JOIN obs o ON e.encounter_id = o.encounter_id "
            + "   WHERE o.concept_id = ${tbTreatmentPlanConceptId} "
            + "     AND e.location_id =   :location  "
            + "     AND e.encounter_type IN (${adultoSeguimentoEncounterTypeId}, "
            + "                              ${arvPediatriaSeguimentoEncounterTypeId}) "
            + "     AND o.obs_datetime BETWEEN   date_sub(:endDate, INTERVAL 6 MONTH) AND   :endDate"
            + "     AND p.voided=0 "
            + "     AND e.voided=0 "
            + "     AND o.voided=0 "
            + "   GROUP BY p.patient_id) last ON p.patient_id = last.patient_id "
            + "AND o.obs_datetime = last.obs_datetime "
            + "WHERE o.value_coded IN(${startDrugsConceptId},${continueRegimenConceptId}) "
            + "  AND o.concept_id = ${tbTreatmentPlanConceptId} "
            + "  AND e.encounter_type IN (${adultoSeguimentoEncounterTypeId},${arvPediatriaSeguimentoEncounterTypeId}) "
            + "  AND e.voided=0 "
            + "  AND o.voided=0 "
            + "  AND p.voided=0 "
            + "  AND e.location_id =   :location  "
            + " UNION "
            + "  SELECT p.patient_id "
            + "  FROM patient p "
            + "       INNER JOIN encounter e "
            + "             ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o "
            + "             ON e.encounter_id = o.encounter_id "
            + " WHERE o.concept_id = ${pulmonaryTBConceptId} "
            + "       AND e.location_id =   :location  "
            + "       AND e.encounter_type = ${masterCardEncounterType} "
            + "       AND o.obs_datetime BETWEEN date_sub(:endDate, INTERVAL 6 MONTH) AND :endDate "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND o.value_coded = ${yesConceptId}";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replaced = sb.replace(query);

    return replaced;
  }

  /**
   * Pregnant Women - {@link <a
   * href="https://docs.google.com/document/d/1EtpeIn-6seD5skZJteCdANhxkKXQye9RckGV2eoYj6c/edit">Common
   * Queries 8</a>} passes parameters to the method {@link
   * #getPregnantWomenAndMostEarliestPregnancyDateQuery(int, int, int, int, int, int, int, int, int,
   * int, int, int, int, int, int)} , facilitating its use in situations where the parameters are
   * the same as defined in the specification - most cases
   */
  public String getPregnantWomenAndMostEarliestPregnancyDateQuery() {

    return getPregnantWomenAndMostEarliestPregnancyDateQuery(
        hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
        commonMetadata.getPregnantConcept().getConceptId(),
        hivMetadata.getPatientFoundYesConcept().getConceptId(),
        hivMetadata.getARVStartDateConcept().getConceptId(),
        hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
        hivMetadata.getCriteriaForArtStart().getConceptId(),
        hivMetadata.getBpostiveConcept().getConceptId(),
        commonMetadata.getNumberOfWeeksPregnant().getConceptId(),
        hivMetadata.getPtvEtvProgram().getProgramId(),
        hivMetadata.getDateOfLastMenstruationConcept().getConceptId(),
        commonMetadata.getPriorDeliveryDateConcept().getConceptId(),
        commonMetadata.getBreastfeeding().getConceptId(),
        hivMetadata.getPatientGaveBirthWorkflowState().getProgramWorkflowStateId(),
        commonMetadata.getPregnancyDueDate().getConceptId());
  }

  /**
   * Breastfeeding Women - {@link <a
   * href="https://docs.google.com/document/d/1EtpeIn-6seD5skZJteCdANhxkKXQye9RckGV2eoYj6c/edit#">Common
   * Queries 7</a>} passes parameters to the method {@link
   * #getBreastFeedingWomenAndMostRecentBreastfeedingDateQuery(int, int, int, int, int, int, int,
   * int, int, int, int, int, int, int, int)} , facilitating its use in situations where the
   * parameters are the same as defined in the specification - most cases
   */
  public String getBreastFeedingWomenAndMostRecentBreastfeedingDateQuery() {

    return getBreastFeedingWomenAndMostRecentBreastfeedingDateQuery(
        hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
        commonMetadata.getPregnantConcept().getConceptId(),
        hivMetadata.getPatientFoundYesConcept().getConceptId(),
        hivMetadata.getARVStartDateConcept().getConceptId(),
        hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
        hivMetadata.getCriteriaForArtStart().getConceptId(),
        hivMetadata.getBpostiveConcept().getConceptId(),
        commonMetadata.getNumberOfWeeksPregnant().getConceptId(),
        hivMetadata.getPtvEtvProgram().getProgramId(),
        hivMetadata.getDateOfLastMenstruationConcept().getConceptId(),
        commonMetadata.getPriorDeliveryDateConcept().getConceptId(),
        commonMetadata.getBreastfeeding().getConceptId(),
        hivMetadata.getPatientGaveBirthWorkflowState().getProgramWorkflowStateId(),
        commonMetadata.getPregnancyDueDate().getConceptId());
  }

  /**
   * Pregnant Women - {@link <a
   * href="https://docs.google.com/document/d/1EtpeIn-6seD5skZJteCdANhxkKXQye9RckGV2eoYj6c/edit#">Common
   * Queries 8</a>}
   *
   * <p>Select the most earliest pregnancy entry (most earliest date) and verify if it falls within
   * the reporting period (startDate and endDate)
   *
   * <ul>
   *   <li>Patients that are female and were marked as “PREGNANT” in the initial consultation * or
   *       follow-up consultation.
   *   <li>Patients that are female and were marked as “PREGNANT” in the Ficha Resumo.
   *   <li>Patients that are female and have “Number of weeks Pregnant” registered in the initial or
   *       follow-up consultation between start and end date
   *   <li>Patients that are female and have “Pregnancy Due Date” registered in the initial or
   *       follow-up consultation between start and end date
   *   <li>Patients that started ART for being B+ as specified in “CRITÉRIO PARA INÍCIO DE TARV” in
   *       the follow-up consultations (Ficha de Seguimento) between start and end date
   *   <li>Patients that are female and enrolled on PTV/ETC program (patient program id =8) between
   *       start date and end date (patient program date_enrolled)
   *   <li>Patients with date of last menstrual period between start and end date
   * </ul>
   *
   * <p><b>a) If the patient has both states (pregnant and breastfeeding) the most recent one should
   * be considered. For patients who have both state (pregnant and breastfeeding) marked on the same
   * day, the system will consider the patient as pregnant</b>
   *
   * <p>
   *
   * <p><b>b)b) For patients that are marked pregnant or breastfeeding on Ficha Resumo but no
   * historical ART start date was registered in the same ficha resumo, this ficha resumo will not
   * be considered for evaluation of pregnancy and breastfeeding status</b>
   *
   * @param aRVAdultInitialEncounterType
   * @param adultoSeguimentoEncounterTypeId
   * @param pregnantConcept
   * @return {@link String} @Param patientFoundYesConcept @Param aRVStartDateConcept @Param
   *     masterCardEncounterType @Param criteriaForArtStartConcept @Param bPostiveConcept @Param
   *     numberOfWeeksPregnantConcept @Param ptvEtvProgramId @Param
   *     dateOfLastMenstruationConcept @Param priorDeliveryDateConcept @Param
   *     breastfeedingConcept @Param patientGaveBirthWorkflowStateId @Param pregnancyDueDateConcept
   */
  public String getPregnantWomenAndMostEarliestPregnancyDateQuery(
      int aRVAdultInitialEncounterType,
      int adultoSeguimentoEncounterTypeId,
      int pregnantConcept,
      int patientFoundYesConcept,
      int aRVStartDateConcept,
      int masterCardEncounterType,
      int criteriaForArtStartConcept,
      int bPostiveConcept,
      int numberOfWeeksPregnantConcept,
      int ptvEtvProgramId,
      int dateOfLastMenstruationConcept,
      int priorDeliveryDateConcept,
      int breastfeedingConcept,
      int patientGaveBirthWorkflowStateId,
      int pregnancyDueDateConcept) {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("5", aRVAdultInitialEncounterType);
    valuesMap.put("6", adultoSeguimentoEncounterTypeId);
    valuesMap.put("1982", pregnantConcept);
    valuesMap.put("1065", patientFoundYesConcept);
    valuesMap.put("1190", aRVStartDateConcept);
    valuesMap.put("53", masterCardEncounterType);
    valuesMap.put("6334", criteriaForArtStartConcept);
    valuesMap.put("6331", bPostiveConcept);
    valuesMap.put("1279", numberOfWeeksPregnantConcept);
    valuesMap.put("8", ptvEtvProgramId);
    valuesMap.put("1465", dateOfLastMenstruationConcept);
    valuesMap.put("5599", priorDeliveryDateConcept);
    valuesMap.put("6332", breastfeedingConcept);
    valuesMap.put("27", patientGaveBirthWorkflowStateId);
    valuesMap.put("1600", pregnancyDueDateConcept);

    String sql =
        "    Select max_pregnant.patient_id, pregnancy_date FROM  "
            + "                (SELECT pregnant.patient_id, MAX(pregnant.pregnancy_date) AS pregnancy_date FROM  "
            + "                 ( SELECT p.patient_id , MAX(e.encounter_datetime) AS pregnancy_date  "
            + "                     FROM patient p "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id  "
            + "                     INNER JOIN obs o ON e.encounter_id=o.encounter_id  "
            + "                     WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=  ${1982} "
            + "                     AND value_coded=  ${1065}    "
            + "                     AND e.encounter_type in (${5},${6}) AND e.encounter_datetime between :startDate AND curdate() AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id     "
            + "                     UNION  "
            + "                     SELECT p.patient_id, MAX(historical_date.value_datetime) as pregnancy_date FROM patient p "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "                     INNER JOIN obs pregnancy ON e.encounter_id=pregnancy.encounter_id "
            + "                     INNER JOIN obs historical_date ON e.encounter_id = historical_date.encounter_id     "
            + "                     WHERE p.voided=0 AND e.voided=0 AND pregnancy.voided=0 AND pregnancy.concept_id=  ${1982}    "
            + "                     AND pregnancy.value_coded= ${1065} "
            + "                     AND historical_date.voided=0 AND historical_date.concept_id=  ${1190} "
            + "                     AND historical_date.value_datetime IS NOT NULL "
            + "                     AND e.encounter_type = ${53} "
            + "                     AND historical_date.value_datetime between :startDate AND curdate() AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id     "
            + "                     UNION "
            + "                     SELECT p.patient_id,  MAX(e.encounter_datetime) as pregnancy_date "
            + "                     FROM patient p "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id  "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id  "
            + "                     INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "                     WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${1279}    "
            + "                     and  e.encounter_type in (${5},${6}) AND e.encounter_datetime between :startDate AND curdate() AND e.location_id=:location  AND pe.gender='F' GROUP BY p.patient_id     "
            + "                     UNION "
            + "                     SELECT p.patient_id,  MAX(e.encounter_datetime) as pregnancy_date "
            + "                     FROM patient p "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id  "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id  "
            + "                     INNER JOIN obs o ON e.encounter_id=o.encounter_id  "
            + "                     WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=  ${1600}    "
            + "                     and  e.encounter_type in (${5},${6}) AND e.encounter_datetime between :startDate AND curdate() AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id     "
            + "                     UNION  "
            + "                     SELECT p.patient_id, MAX(e.encounter_datetime) as pregnancy_date     "
            + "                     FROM patient p  "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id     "
            + "                     INNER JOIN obs o ON e.encounter_id=o.encounter_id     "
            + "                     WHERE p.voided=0 AND pe.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${6334} "
            + "                     AND value_coded=  ${6331}    "
            + "                     AND e.encounter_type in (${5},${6}) AND e.encounter_datetime between :startDate AND curdate() AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id     "
            + "                     UNION    "
            + "                     select pp.patient_id,  MAX(pp.date_enrolled) as pregnancy_date "
            + "                     FROM patient_program pp  "
            + "                     INNER JOIN person pe ON pp.patient_id=pe.person_id  "
            + "                     WHERE pp.program_id=  ${8} "
            + "                     AND pp.voided=0 AND pp.date_enrolled between  :startDate AND curdate() AND pp.location_id=:location AND pe.gender='F' GROUP BY pp.patient_id     "
            + "                     UNION "
            + "                     SELECT p.patient_id,  MAX(o.value_datetime) as pregnancy_date  FROM patient p     "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id     "
            + "                     INNER JOIN obs o ON e.encounter_id=o.encounter_id     "
            + "       WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${1465}    "
            + "                     AND e.encounter_type = ${6} "
            + "                     AND pe.gender='F' AND o.value_datetime BETWEEN :startDate AND curdate() GROUP BY p.patient_id) as pregnant "
            + "                     GROUP BY patient_id) max_pregnant "
            + "                    LEFT JOIN  (SELECT breastfeeding.patient_id, max(breastfeeding.last_date) as breastfeeding_date FROM ( "
            + "       SELECT p.patient_id, MAX(o.value_datetime) AS last_date     "
            + "       FROM patient p "
            + "       INNER JOIN person pe ON p.patient_id=pe.person_id  "
            + "       INNER JOIN encounter e ON p.patient_id=e.patient_id  "
            + "       INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "       WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${5599}    "
            + "       AND  e.encounter_type in (${5},${6})  AND o.value_datetime BETWEEN :startDate AND curdate()      "
            + "       AND e.location_id=:location AND pe.gender='F'     "
            + "       GROUP BY p.patient_id     "
            + "       UNION "
            + "       SELECT p.patient_id, MAX(e.encounter_datetime) AS last_date     "
            + "       FROM patient p     "
            + "       INNER JOIN person pe ON p.patient_id=pe.person_id "
            + "       INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "       WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id= ${6332}    "
            + "       AND o.value_coded= ${1065}    "
            + "       AND e.encounter_type in (${5},${6}) "
            + "        AND e.encounter_datetime BETWEEN :startDate AND curdate() "
            + "           AND e.location_id=:location AND pe.gender='F'     "
            + "       GROUP BY p.patient_id    "
            + "       UNION "
            + "       SELECT p.patient_id, MAX(e.encounter_datetime) AS last_date     "
            + "       FROM patient p     "
            + "       INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "       INNER JOIN encounter e ON p.patient_id=e.patient_id     "
            + "       INNER JOIN obs o ON e.encounter_id=o.encounter_id     "
            + "       WHERE p.voided=0 AND pe.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id= ${6334}    "
            + "       AND o.value_coded=  ${6332}    "
            + "       AND e.encounter_type in (${5},${6})  AND e.encounter_datetime BETWEEN :startDate AND curdate()      "
            + "       AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id     "
            + "       UNION "
            + "       SELECT pp.patient_id, MAX(ps.start_date) AS last_date     "
            + "       FROM patient_program pp     "
            + "       INNER JOIN person pe ON pp.patient_id=pe.person_id     "
            + "       INNER JOIN patient_state ps ON pp.patient_program_id=ps.patient_program_id     "
            + "       WHERE pp.program_id= ${8}    "
            + "       AND ps.state=  ${27}    "
            + "       AND pp.voided=0 AND  ps.start_date BETWEEN :startDate AND curdate()      "
            + "       AND pp.location_id=:location AND pe.gender='F'     "
            + "       GROUP BY pp.patient_id     "
            + "       UNION "
            + "       SELECT p.patient_id, MAX(hist.value_datetime) AS last_date     "
            + "       FROM patient p     "
            + "       INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "       INNER JOIN encounter e ON p.patient_id=e.patient_id     "
            + "       INNER JOIN obs o ON e.encounter_id=o.encounter_id     "
            + "       INNER JOIN obs hist ON e.encounter_id=hist.encounter_id     "
            + "        WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id=  ${6332}    "
            + "       AND o.value_coded=  ${1065}    "
            + "       AND e.encounter_type = ${53}    "
            + "       AND hist.concept_id=   ${1190}    "
            + "       AND pe.gender='F' AND hist.value_datetime IS NOT NULL      "
            + "        AND hist.value_datetime BETWEEN :startDate AND curdate()  GROUP BY p.patient_id     "
            + "       ) AS breastfeeding     "
            + "       GROUP BY patient_id) max_breastfeeding     "
            + "                     ON max_pregnant.patient_id = max_breastfeeding.patient_id     "
            + "                     WHERE (max_pregnant.pregnancy_date Is NOT NULL AND max_pregnant.pregnancy_date >= max_breastfeeding.breastfeeding_date)     "
            + "                     OR (max_breastfeeding.breastfeeding_date Is NULL) ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(sql);
  }

  /**
   * Breastfeeding Women - {@link <a
   * href="https://docs.google.com/document/d/1EtpeIn-6seD5skZJteCdANhxkKXQye9RckGV2eoYj6c/edit#">Common
   * Queries 7</a>}
   *
   * <p>Select the most recent breastfeeding entry (most recent date) and verify if it falls within
   * the reporting period (startDate and endDate)
   *
   * <ul>
   *   <li>Patients that are female and have the “Delivery date” (obs concept id 5599) registered in
   *       the initial or follow-up consultation
   *   <li>Patients that are female and have registered as breastfeeding in follow up consultation
   *       (encounter datetime)
   *   <li>Patients that are female and have started ART for being breastfeeding as specified in
   *       “CRITÉRIO PARA INICIO DE TRATAMENTO ARV” with response equal to “LACTACAO” in the initial
   *       or follow-up consultation
   *   <li>Patients enrolled in PTV(ETV) Program
   *   <li>Patients that are female and have registered as breastfeeding in Ficha Resumo
   * </ul>
   *
   * <p><b>If the patient has both states (pregnant and breastfeeding) the most recent one should be
   * considered. For patients who have both state (pregnant and breastfeeding) marked on the same
   * day, the system will consider the patient as pregnant</b>
   *
   * <p>*
   *
   * <p><b>b) For patients that are marked pregnant or breastfeeding on Ficha Resumo but no
   * historical ART start date was registered in the same Ficha Resumo, this ficha resumo will not
   * be considered for evaluation of pregnancy and breastfeeding status.</b>
   *
   * @param aRVAdultInitialEncounterType
   * @param adultoSeguimentoEncounterTypeId
   * @param pregnantConcept
   * @return {@link String} @Param patientFoundYesConcept @Param aRVStartDateConcept @Param
   *     masterCardEncounterType @Param criteriaForArtStartConcept @Param bPostiveConcept @Param
   *     numberOfWeeksPregnantConcept @Param ptvEtvProgramId @Param
   *     dateOfLastMenstruationConcept @Param priorDeliveryDateConcept @Param
   *     breastfeedingConcept @Param patientGaveBirthWorkflowStateId @Param pregnancyDueDateConcept
   */
  public String getBreastFeedingWomenAndMostRecentBreastfeedingDateQuery(
      int aRVAdultInitialEncounterType,
      int adultoSeguimentoEncounterTypeId,
      int pregnantConcept,
      int patientFoundYesConcept,
      int aRVStartDateConcept,
      int masterCardEncounterType,
      int criteriaForArtStartConcept,
      int bPostiveConcept,
      int numberOfWeeksPregnantConcept,
      int ptvEtvProgramId,
      int dateOfLastMenstruationConcept,
      int priorDeliveryDateConcept,
      int breastfeedingConcept,
      int patientGaveBirthWorkflowStateId,
      int pregnancyDueDateConcept) {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("5", aRVAdultInitialEncounterType);
    valuesMap.put("6", adultoSeguimentoEncounterTypeId);
    valuesMap.put("1982", pregnantConcept);
    valuesMap.put("1065", patientFoundYesConcept);
    valuesMap.put("1190", aRVStartDateConcept);
    valuesMap.put("53", masterCardEncounterType);
    valuesMap.put("6334", criteriaForArtStartConcept);
    valuesMap.put("6331", bPostiveConcept);
    valuesMap.put("1279", numberOfWeeksPregnantConcept);
    valuesMap.put("8", ptvEtvProgramId);
    valuesMap.put("1465", dateOfLastMenstruationConcept);
    valuesMap.put("5599", priorDeliveryDateConcept);
    valuesMap.put("6332", breastfeedingConcept);
    valuesMap.put("27", patientGaveBirthWorkflowStateId);
    valuesMap.put("1600", pregnancyDueDateConcept);

    String sql =
        "        Select max_breastfeeding.patient_id, breastfeeding_date FROM     "
            + "                  (SELECT breastfeeding.patient_id, max(breastfeeding.last_date) as breastfeeding_date FROM (     "
            + "       SELECT p.patient_id, MAX(o.value_datetime) AS last_date     "
            + "       FROM patient p     "
            + "       INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "       INNER JOIN encounter e ON p.patient_id=e.patient_id     "
            + "       INNER JOIN obs o ON e.encounter_id=o.encounter_id     "
            + "       WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=  ${5599}    "
            + "       AND  e.encounter_type in (${5},${6})  AND o.value_datetime BETWEEN :startDate AND curdate()      "
            + "       AND e.location_id=:location AND pe.gender='F'     "
            + "       GROUP BY p.patient_id "
            + "       UNION  SELECT p.patient_id, MAX(e.encounter_datetime) AS last_date     "
            + "       FROM patient p "
            + "       INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "       INNER JOIN encounter e ON p.patient_id=e.patient_id     "
            + "       INNER JOIN obs o ON e.encounter_id=o.encounter_id     "
            + "       WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id= ${6332}  "
            + "       AND o.value_coded= ${1065} "
            + "       AND e.encounter_type in (${5},${6}) "
            + "        AND e.encounter_datetime BETWEEN :startDate AND curdate() "
            + "           AND e.location_id=:location AND pe.gender='F' "
            + "       GROUP BY p.patient_id "
            + "       UNION "
            + "       SELECT p.patient_id, MAX(e.encounter_datetime) AS last_date     "
            + "       FROM patient p "
            + "       INNER JOIN person pe ON p.patient_id=pe.person_id "
            + "       INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "       WHERE p.voided=0 AND pe.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id= ${6334} "
            + "       AND o.value_coded=  ${6332} "
            + "       AND e.encounter_type in (${5},${6}) "
            + "               AND e.encounter_datetime BETWEEN :startDate AND curdate() "
            + "            AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id "
            + "       UNION "
            + "       SELECT pp.patient_id, MAX(ps.start_date) AS last_date "
            + "       FROM patient_program pp "
            + "       INNER JOIN person pe ON pp.patient_id=pe.person_id "
            + "       INNER JOIN patient_state ps ON pp.patient_program_id=ps.patient_program_id     "
            + "       WHERE pp.program_id=  ${8} "
            + "       AND ps.state=  ${27} "
            + "       AND pp.voided=0 AND "
            + "         ps.start_date BETWEEN :startDate AND curdate() "
            + "           AND pp.location_id=:location AND pe.gender='F' "
            + "       GROUP BY pp.patient_id"
            + "                     UNION "
            + "       SELECT p.patient_id, MAX(hist.value_datetime) AS last_date "
            + "       FROM patient p "
            + "       INNER JOIN person pe ON p.patient_id=pe.person_id "
            + "       INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "       INNER JOIN obs hist ON e.encounter_id=hist.encounter_id "
            + "        WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id=  ${6332} "
            + "       AND o.value_coded=  ${1065} "
            + "       AND e.encounter_type = ${53} "
            + "       AND hist.concept_id=   ${1190} "
            + "       AND pe.gender='F' AND hist.value_datetime IS NOT NULL "
            + "        AND hist.value_datetime BETWEEN :startDate AND curdate() GROUP BY p.patient_id "
            + "       ) AS breastfeeding "
            + "       GROUP BY patient_id) max_breastfeeding "
            + "                    LEFT JOIN "
            + "                     (SELECT pregnant.patient_id, MAX(pregnant.pregnancy_date) AS pregnancy_date FROM "
            + "                 (SELECT p.patient_id , MAX(e.encounter_datetime) AS pregnancy_date "
            + "                     FROM patient p "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id  "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "                     INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "                     WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=  ${1982}    "
            + "                     AND value_coded=  ${1065}    "
            + "                     AND e.encounter_type in (${5},${6}) AND e.encounter_datetime between :startDate AND curdate() AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id     "
            + "                     UNION     "
            + "                     SELECT p.patient_id, MAX(historical_date.value_datetime) as pregnancy_date FROM patient p     "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id     "
            + "                     INNER JOIN obs pregnancy ON e.encounter_id=pregnancy.encounter_id     "
            + "                     INNER JOIN obs historical_date ON e.encounter_id = historical_date.encounter_id     "
            + "                     WHERE p.voided=0 AND e.voided=0 AND pregnancy.voided=0 AND pregnancy.concept_id=  ${1982}    "
            + "                     AND pregnancy.value_coded=   ${1065}    "
            + "                     AND historical_date.voided=0 AND historical_date.concept_id=  ${1190}    "
            + "                     AND historical_date.value_datetime IS NOT NULL     "
            + "                     AND e.encounter_type = ${53}    "
            + "                 AND historical_date.value_datetime between :startDate AND curdate() AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id     "
            + "                     UNION     "
            + "                     SELECT p.patient_id,  MAX(e.encounter_datetime) as pregnancy_date     "
            + "                     FROM patient p     "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id     "
            + "                     INNER JOIN obs o ON e.encounter_id=o.encounter_id     "
            + "                     WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${1279}    "
            + "                     and     "
            + "                     e.encounter_type in (${5},${6}) AND e.encounter_datetime between :startDate AND curdate() AND e.location_id=:location  AND pe.gender='F' GROUP BY p.patient_id     "
            + "                     UNION "
            + "                     SELECT p.patient_id,  MAX(e.encounter_datetime) as pregnancy_date     "
            + "                     FROM patient p     "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id     "
            + "                     INNER JOIN obs o ON e.encounter_id=o.encounter_id     "
            + "                     WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=  ${1600}    "
            + "                     and  e.encounter_type in (${5},${6}) AND e.encounter_datetime between :startDate AND curdate() AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id    "
            + "                     UNION "
            + "                     SELECT p.patient_id, MAX(e.encounter_datetime) as pregnancy_date     "
            + "                     FROM patient p     "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id     "
            + "                     INNER JOIN obs o ON e.encounter_id=o.encounter_id     "
            + "                     WHERE p.voided=0 AND pe.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${6334}    "
            + "                     AND value_coded=  ${6331}    "
            + "                     AND e.encounter_type in (${5},${6}) AND e.encounter_datetime between :startDate AND curdate() AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id    "
            + " UNION "
            + "                     select pp.patient_id,  MAX(pp.date_enrolled) as pregnancy_date     "
            + "                     FROM patient_program pp     "
            + "                     INNER JOIN person pe ON pp.patient_id=pe.person_id     "
            + "                     WHERE pp.program_id=  ${8}    "
            + "                     AND pp.voided=0 AND pp.date_enrolled between  :startDate AND curdate() AND pp.location_id=:location AND pe.gender='F' GROUP BY pp.patient_id     "
            + "                     UNION   "
            + "                     SELECT p.patient_id,  MAX(o.value_datetime) as pregnancy_date  FROM patient p     "
            + "                     INNER JOIN person pe ON p.patient_id=pe.person_id     "
            + "                     INNER JOIN encounter e ON p.patient_id=e.patient_id     "
            + "                     INNER JOIN obs o ON e.encounter_id=o.encounter_id     "
            + "       WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=  ${1465}    "
            + "                     AND e.encounter_type = ${6}    "
            + "                     AND pe.gender='F' AND o.value_datetime BETWEEN :startDate AND curdate() GROUP BY p.patient_id) as pregnant     "
            + "                     GROUP BY patient_id) max_pregnant   "
            + "                     ON max_pregnant.patient_id = max_breastfeeding.patient_id     "
            + "                     WHERE (max_breastfeeding.breastfeeding_date Is NOT NULL AND max_breastfeeding.breastfeeding_date > max_pregnant.pregnancy_date)     "
            + "                     OR (max_pregnant.pregnancy_date Is NULL) ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(sql);
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Patient ART Start Date is the oldest date from the set of criterias defined in the common
   * query: 1/1 Patients who initiated ART and ART Start Date as earliest from the following
   * criterias is by End of the period (reporting endDate) To be used in situations where the {@link
   * #getARTStartDate(int, int, int, int, int, int, int, int, int, int)} * parameters are the same
   * as defined in the specification - most cases
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getARTStartDate(boolean startDate) {
    return getARTStartDate(
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
        hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
        hivMetadata.getStartDrugs().getConceptId(),
        hivMetadata.getARVPlanConcept().getConceptId(),
        hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
        hivMetadata.getARVStartDateConcept().getConceptId(),
        hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
        hivMetadata.getArtDatePickupMasterCard().getConceptId(),
        hivMetadata.getARTProgram().getProgramId(),
        startDate);
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Patient ART Start Date is the oldest date from the set of criterias defined in the common
   * query: 1/1 Patients who initiated ART and ART Start Date as earliest from the following
   * criterias is by End of the period (reporting endDate)
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getARTStartDate(
      int adultoSeguimentoEncounterType,
      int pediatriaSeguimentoEncounterType,
      int aRVPharmaciaEncounterType,
      int startDrugs,
      int aRVPlanConcept,
      int masterCardEncounterType,
      int aRVStartDateConcept,
      int masterCardDrugPickupEncounterType,
      int artDatePickupMasterCard,
      int aRTProgram,
      boolean startDate) {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", adultoSeguimentoEncounterType);
    valuesMap.put("9", pediatriaSeguimentoEncounterType);
    valuesMap.put("18", aRVPharmaciaEncounterType);
    valuesMap.put("1256", startDrugs);
    valuesMap.put("1255", aRVPlanConcept);
    valuesMap.put("53", masterCardEncounterType);
    valuesMap.put("1190", aRVStartDateConcept);
    valuesMap.put("52", masterCardDrugPickupEncounterType);
    valuesMap.put("23866", artDatePickupMasterCard);
    valuesMap.put("2", aRTProgram);

    String fromSQL =
        "  FROM   "
            + "       (SELECT p.patient_id, first_pickup FROM patient p   "
            + "       JOIN   "
            + "       patient_program pp ON pp.patient_id = p.patient_id   "
            + "       JOIN   "
            + "       (SELECT p.patient_id, MIN(e.encounter_datetime) first_pickup FROM patient  p   "
            + "       JOIN encounter e ON e.patient_id = p.patient_id   "
            + "       JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "       WHERE e.encounter_type =  ${18}    "
            + "       AND e.voided = 0 AND p.voided = 0   "
            + "       AND o.voided = 0 AND e.location_id = :location   "
            + "       GROUP BY p.patient_id) first_pickup   "
            + "       ON first_pickup.patient_id = p.patient_id   "
            + "       JOIN   "
            + "       (SELECT p.patient_id FROM patient p   "
            + "       JOIN encounter e ON e.patient_id = p.patient_id   "
            + "       JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "       WHERE e.encounter_type IN ( ${6} , ${9} , ${18} )   "
            + "       AND o.concept_id =  ${1255}  AND o.value_coded = ${1256}    "
            + "       AND e.voided = 0 AND p.voided = 0   "
            + "       AND o.voided = 0 AND e.location_id = :location    "
            + "       AND e.encounter_datetime <= :startDate) arv_plan    "
            + "       ON arv_plan.patient_id = p.patient_id   "
            + "       GROUP BY p.patient_id   "
            + "       UNION   "
            + "       SELECT p.patient_id, MIN(o.obs_datetime) AS first_pickup FROM patient p   "
            + "       JOIN encounter e ON e.patient_id = p.patient_id   "
            + "       JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "       WHERE e.encounter_type IN ( ${6} , ${9} , ${18} , ${53} )   "
            + "       AND o.concept_id =  ${1190}     "
            + "       AND e.voided = 0 AND p.voided = 0   "
            + "       AND o.voided = 0 AND e.location_id = :location    "
            + "       AND o.obs_datetime <= :startDate   "
            + "       GROUP BY p.patient_id   "
            + "       UNION   "
            + "       SELECT p.patient_id, MIN(o.value_datetime) first_pickup FROM patient p    "
            + "           JOIN encounter e ON e.patient_id = p.patient_id   "
            + "           JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "           JOIN obs oo ON oo.encounter_id = e.encounter_id   "
            + "           WHERE e.encounter_type =  ${52}  AND e.location_id = :location   "
            + "           AND e.voided = 0 AND p.voided = 0   "
            + "           AND o.concept_id =   ${23866}  AND o.voided =0   "
            + "           AND o.value_datetime <= :endDate   "
            + "           GROUP BY p.patient_id   "
            + "       UNION   "
            + "       SELECT p.patient_id, pp.date_enrolled AS first_pickup FROM patient p   "
            + "       JOIN   "
            + "       patient_program pp ON pp.patient_id = p.patient_id   "
            + "       WHERE pp.program_id =  ${2}    "
            + "       AND pp.date_enrolled <= :endDate   "
            + "       AND p.voided = 0   "
            + "       GROUP BY p.patient_id) union_tbl "
            + "       GROUP BY union_tbl.patient_id ";

    String sql =
        startDate
            ? "SELECT union_tbl.patient_id , MIN(union_tbl.first_pickup) first_pickup"
                .concat(fromSQL)
            : "SELECT union_tbl.patient_id".concat(fromSQL);

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(sql);
  }
}
