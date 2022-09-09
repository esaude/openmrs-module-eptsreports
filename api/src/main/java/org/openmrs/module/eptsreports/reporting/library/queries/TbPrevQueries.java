package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsQueriesUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TbPrevQueries {

  @Autowired private CommonQueries commonQueries;

  @Autowired private TbMetadata tbMetadata;
  @Autowired private HivMetadata hivMetadata;

  public static String getRegimeTPTOrOutrasPrescricoes(
      EncounterType encounterType, Concept question, List<Concept> answers, Integer boundary) {
    List<Integer> answerIds = new ArrayList<>();
    for (Concept concept : answers) {
      answerIds.add(concept.getConceptId());
    }
    String query =
        " SELECT distinct p.patient_id "
            + " FROM  patient p  "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + " INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + " INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date "
            + "             FROM    patient p  "
            + "             INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "             INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "             WHERE   p.voided = 0  "
            + "                 AND e.voided = 0  "
            + "                 AND o.voided = 0  "
            + "                 AND e.location_id = :location "
            + "                 AND e.encounter_type = ${encounterType} "
            + "                 AND o.concept_id = ${question} "
            + "                 AND o.value_coded IN (${answers}) "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= :endDate "
            + "             GROUP BY p.patient_id) AS inh  on inh.patient_id = p.patient_id "
            + " WHERE p.voided = 0 "
            + "    and e.voided = 0 "
            + "    and o.voided = 0 "
            + "    and p.patient_id NOT IN ( SELECT patient_id  "
            + "                             FROM patient p "
            + "                             WHERE   p.voided = 0  "
            + "                                  AND e.voided = 0  "
            + "                                  AND o.voided = 0  "
            + "                                  AND e.location_id = :location "
            + "                                  AND e.encounter_type = ${encounterType} "
            + "                                  AND o.concept_id = ${question} "
            + "                                  AND o.value_coded IN (${answers}) "
            + "                                  AND e.encounter_datetime >= DATE_SUB(inh.first_pickup_date, INTERVAL "
            + boundary
            + " MONTH)  "
            + "                                  AND e.encounter_datetime < inh.first_pickup_date) ";
    Map<String, String> map = new HashMap<>();
    map.put("encounterType", String.valueOf(encounterType.getEncounterTypeId()));
    map.put("question", String.valueOf(question.getConceptId()));
    map.put("answers", StringUtils.join(answerIds, ","));
    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  private Map<String, Integer> getReportMetadata() {

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23987", tbMetadata.getTreatmentFollowUpTPTConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    map.put("23986", tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId());
    map.put("1098", hivMetadata.getMonthlyConcept().getConceptId());
    map.put("23720", hivMetadata.getQuarterlyConcept().getConceptId());
    map.put("23955", tbMetadata.getDtINHConcept().getConceptId());

    return map;
  }

  /**
   * Patients who have Última Profilaxia TPT with value “3HP” and Data Início selected in Ficha
   * Resumo - Mastercard during the previous reporting period (3HP Start Date)
   *
   * @return String
   */
  public String get3HPStartOnFichaResumo() {
    String query =
        "SELECT p.patient_id, o2.obs_datetime AS start_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0"
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${53} "
            + "       AND ( (o.concept_id = ${23985} AND o.value_coded = ${23954}) "
            + "        AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "        AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND DATE_SUB(:endDate, INTERVAL 6 MONTH) ) )";
    return new StringSubstitutor(getReportMetadata()).replace(query);
  }

  /**
   * Patients who have Profilaxia TPT with the value “3HP” and Estado da Profilaxia with the value
   * “Início (I)” marked in Ficha Clínica – Mastercard during the previous reporting period (3HP
   * Start Date)
   *
   * @return String
   */
  public String getStartDateOf3HPOnFichaClinica() {
    String query =
        "SELECT p.patient_id, o2.obs_datetime AS start_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6}"
            + "       AND (o.concept_id = ${23985} AND o.value_coded = ${23954})  "
            + "       AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "       AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND DATE_SUB(:endDate, INTERVAL 6 MONTH) ) ";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    return sb.replace(query);
  }

  /**
   * Patients who have Outras Prescrições with the value “DT-3HP” marked in Ficha Clínica -
   * Mastercard during the previous reporting period (3HP Start Date)
   *
   * @return String
   */
  public String getStartDateOfDT3HPOnFichaClinica() {
    String query =
        "SELECT p.patient_id, e.encounter_datetime AS start_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND o.concept_id = ${1719} "
            + "       AND o.value_coded = ${165307} "
            + "       AND e.encounter_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND DATE_SUB(:endDate, INTERVAL 6 MONTH) ";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    return sb.replace(query);
  }

  /**
   * Patients who have Regime de TPT with the values “3HP or 3HP + Piridoxina” and “Seguimento de
   * Tratamento TPT” with values “Continua” or “Fim” or no value marked on the first pick-up date in
   * Ficha de Levantamento de TPT (FILT) during the previous reporting period (FILT 3HP Start Date)
   * and
   *
   * <ul>
   *   <li>No other Regime de TPT with the values “3HP or 3HP + Piridoxina” marked in FILT in the 4
   *       months prior to this FILT 3HP Start Date and
   *   <li>No other 3HP Start Dates marked in Ficha Clinica ((Profilaxia TPT with the value “3HP”
   *       and Estado da Profilaxia with the value “Início (I)”) or (Outras Prescrições with the
   *       value “DT-3HP”)) in the 4 months prior to this FILT 3HP Start Date and
   *   <li>No other 3HP Start Dates marked in Ficha Resumo (Última Profilaxia TPT with value “3HP”
   *       and Data Início da Profilaxia TPT) in the 4 months prior to this FILT 3HP Start Date
   * </ul>
   *
   * The system will consider the earliest date from the various sources as well as within the same
   * sources as the 3HP Start Date
   *
   * @return String
   */
  public String getStartDateOf3hpPiridoxinaOnFilt() {

    String query =
        "SELECT filt.patient_id, filt_3hp_start_date AS start_date "
            + "FROM  (SELECT p.patient_id, "
            + "              Min(o2.obs_datetime) filt_3hp_start_date "
            + "       FROM   patient p "
            + "              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "              INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "              INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "       WHERE  p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND o.voided = 0 "
            + "              AND o2.voided = 0 "
            + "              AND e.location_id = :location "
            + "              AND e.encounter_type = ${60} "
            + "              AND ( ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "              AND ( o2.concept_id = ${23987} AND ( o2.value_coded IN ( ${1257}, ${1267} ) OR o2.value_coded IS NULL ) "
            + "              AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND DATE_SUB(:endDate, INTERVAL 6 MONTH) ) ) "
            + "       GROUP  BY p.patient_id) filt "
            + "WHERE  NOT EXISTS (SELECT ee.encounter_id "
            + "                   FROM   encounter ee "
            + "                          INNER JOIN obs o ON ee.encounter_id = o.encounter_id "
            + "                          INNER JOIN obs o2 ON ee.encounter_id = o2.encounter_id "
            + "                   WHERE  ee.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND ee.patient_id = filt.patient_id "
            + "                          AND ee.encounter_type = ${60} "
            + "                          AND ee.location_id = :location "
            + "                          AND ( ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                          AND ( o2.concept_id = ${23987} AND ( o2.value_coded IN ( ${1256}, ${1705} ) OR o2.value_coded IS NULL ) "
            + "                          AND o2.obs_datetime >= Date_sub(filt.filt_3hp_start_date, INTERVAL 4 month ) AND o2.obs_datetime <= filt.filt_3hp_start_date ) "
            + "                              )) "
            + "       AND NOT EXISTS (SELECT ee.encounter_id "
            + "                       FROM   encounter ee "
            + "                              INNER JOIN obs o ON ee.encounter_id = o.encounter_id "
            + "                              INNER JOIN obs o2 ON ee.encounter_id = o2.encounter_id "
            + "                       WHERE  ee.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND ee.encounter_type = ${6} "
            + "                              AND ee.location_id = :location "
            + "                              AND ee.patient_id = filt.patient_id "
            + "                              AND o.concept_id = ${1719} "
            + "                              AND o.value_coded = ${165307} "
            + "                              AND ee.encounter_datetime >= Date_sub(filt.filt_3hp_start_date, INTERVAL 4 month ) "
            + "                              AND ee.encounter_datetime <= filt.filt_3hp_start_date) "
            + "       AND NOT EXISTS (SELECT ee.encounter_id "
            + "                       FROM   encounter ee "
            + "                              INNER JOIN obs o ON ee.encounter_id = o.encounter_id "
            + "                              INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + "                       WHERE  ee.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND ee.encounter_type = ${53} "
            + "                              AND ee.location_id = :location "
            + "                              AND ee.patient_id = filt.patient_id "
            + "                              AND o.concept_id = ${23985} "
            + "                              AND o.value_coded = ${23954} "
            + "                              AND oo.concept_id = ${165308} "
            + "                              AND oo.value_coded = ${1256} "
            + "                              AND oo.obs_datetime >= Date_sub(filt.filt_3hp_start_date, INTERVAL 4 month) "
            + "                              AND oo.obs_datetime <= filt.filt_3hp_start_date) ";

    return new StringSubstitutor(getReportMetadata()).replace(query);
  }

  /**
   * Patients who have Regime de TPT with the values “3HP or 3HP + Piridoxina” and “Seguimento de
   * tratamento TPT” = (‘Início’ or ‘Re-Início’) marked in Ficha de Levantamento de TPT (FILT)
   * during the previous reporting period (3HP Start Date)
   *
   * @return String
   */
  public String get3HPStartSeguimentoTptOnFilt() {

    String query =
        "SELECT p.patient_id, o2.obs_datetime AS start_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = 60 "
            + "       AND o.concept_id = ${23985} AND o.value_coded IN (${23954}, ${23984}) "
            + "       AND o2.concept_id = 23987 AND o2.value_coded IN ( ${1256}, ${1705}) "
            + "       AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND DATE_SUB(:endDate, INTERVAL 6 MONTH) ";
    return new StringSubstitutor(getReportMetadata()).replace(query);
  }

  /**
   * Patients who have (Última Pprofilaxia TPT with value “Isoniazida (INH)” and Data Início da
   * Profilaxia TPT) registered selected in Ficha Resumo - Mastercard during the previous reporting
   * period (INH Start Date) or
   */
  public String getINHStartDateOnFichaResumo() {
    String query =
        "SELECT p.patient_id, o2.obs_datetime AS start_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0"
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${53} "
            + "       AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "        AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "        AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND DATE_SUB(:endDate, INTERVAL 6 MONTH) ) )";
    return new StringSubstitutor(getReportMetadata()).replace(query);
  }

  /**
   * Patients who have Profilaxia (INH) with the value “I” (Início) or (Profilaxia TPT with the
   * value “Isoniazida (INH)” and Estado da Profilaxia with the value “Início (I)”) marked in Ficha
   * Clínica – Mastercard during the previous reporting period (INH Start Date) or
   *
   * @return String
   */
  public String getStartDateINHOnFichaClinica() {
    String query =
        "SELECT p.patient_id, o2.obs_datetime AS start_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type IN (${6},${9}) "
            + "       AND (o.concept_id = ${23985} AND o.value_coded = ${656})  "
            + "       AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "       AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND DATE_SUB(:endDate, INTERVAL 6 MONTH) ) ";
    return new StringSubstitutor(getReportMetadata()).replace(query);
  }

  /**
   * Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”) and
   * “Seguimento de Tratamento TPT” with values “Continua” or no value marked on the first pick-up
   * date in Ficha de Levantamento de TPT (FILT) during the previous reporting period (FILT INH
   * Start Date) and:
   *
   * <ul>
   *   <li>No other Regime de TPT with the values “Isoniazida” or “Isoniazida + Piridoxina” marked
   *       in FILT in the 7 months prior to this FILT INH Start Date and
   *   <li>No other INH Start Dates marked in Ficha Clinica (Profilaxia TPT with the value
   *       “Isoniazida (INH)” and Estado da Profilaxia with the value “Início (I)”) in the 7 months
   *       prior to this FILT INH Start Date and
   *   <li>No other INH Start Dates marked on Ficha de Seguimento (Profilaxia TPT with the value
   *       “Isoniazida (INH)” with Data Início) marked in the 7 months prior to this FILT INH Start
   *       Date and
   *   <li>No other INH Start Dates marked in Ficha Resumo (Última pProfilaxia TPT with value
   *       “Isoniazida (INH)” and Data Início) selected in the 7 months prior to this FILT INH Start
   *       Date *
   * </ul>
   *
   * The system will consider the earliest date from the various sources as well as within the same
   * sources as the INH Start Date
   *
   * @return
   */
  public String getStartDateOfINHOnFilt() {

    String query =
        "SELECT filt.patient_id, filt_3hp_start_date AS start_date "
            + "FROM  (SELECT p.patient_id, "
            + "              Min(o2.obs_datetime) filt_3hp_start_date "
            + "       FROM   patient p "
            + "              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "              INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "              INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "       WHERE  p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND o.voided = 0 "
            + "              AND o2.voided = 0 "
            + "              AND e.location_id = :location "
            + "              AND e.encounter_type = ${60} "
            + "              AND ( ( o.concept_id = ${23985} AND o.value_coded IN ( ${656}, ${23982} ) ) "
            + "              AND ( o2.concept_id = ${23987} AND ( o2.value_coded IN ( ${1257} ) OR o2.value_coded IS NULL ) "
            + "              AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND DATE_SUB(:endDate, INTERVAL 6 MONTH) ) ) "
            + "       GROUP  BY p.patient_id) filt "
            + "WHERE NOT EXISTS (SELECT ee.encounter_id "
            + "                       FROM   encounter ee "
            + "                              INNER JOIN obs o ON ee.encounter_id = o.encounter_id "
            + "                              INNER JOIN obs o2 ON ee.encounter_id = o2.encounter_id "
            + "                       WHERE  ee.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND ee.encounter_type IN (${6},${9}) "
            + "                              AND ee.location_id = :location "
            + "                              AND ee.patient_id = filt.patient_id "
            + "                              AND o.concept_id = ${23985} "
            + "                              AND o.value_coded = ${656} "
            + "                              AND o2.concept_id = ${165308} "
            + "                              AND o2.value_coded = ${1256} "
            + "                              AND ee.encounter_datetime >= Date_sub(filt.filt_3hp_start_date, INTERVAL 7 month ) "
            + "                              AND ee.encounter_datetime <= filt.filt_3hp_start_date) "
            + "       AND NOT EXISTS (SELECT ee.encounter_id "
            + "                       FROM   encounter ee "
            + "                              INNER JOIN obs o ON ee.encounter_id = o.encounter_id "
            + "                              INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + "                       WHERE  ee.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND ee.encounter_type = ${53} "
            + "                              AND ee.location_id = :location "
            + "                              AND ee.patient_id = filt.patient_id "
            + "                              AND o.concept_id = ${23985} "
            + "                              AND o.value_coded = ${656} "
            + "                              AND oo.concept_id = ${165308} "
            + "                              AND oo.value_coded = ${1256} "
            + "                              AND oo.obs_datetime >= Date_sub(filt.filt_3hp_start_date, INTERVAL 7 month) "
            + "                              AND oo.obs_datetime <= filt.filt_3hp_start_date) ";

    return new StringSubstitutor(getReportMetadata()).replace(query);
  }

  /**
   * Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”) and
   * “Seguimento de tratamento TPT” = (‘Inicío’ or ‘Re-Início’) marked in Ficha de Levantamento de
   * TPT (FILT) during the previous reporting period (INH Start Date) or
   *
   * @return String
   */
  public String getINHStartDate4InhAndSeguimentoOnFilt() {
    String query =
        "SELECT p.patient_id, o2.obs_datetime AS start_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${60} "
            + "       AND o.concept_id = ${23985} AND o.value_coded IN (${656}, ${23982}) "
            + "       AND o2.concept_id = ${23987} AND o2.value_coded IN ( ${1256}, ${1705}) "
            + "       AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND DATE_SUB(:endDate, INTERVAL 6 MONTH) ";
    return new StringSubstitutor(getReportMetadata()).replace(query);
  }

  /**
   * Patients who have Última Profilaxia TPT with value “3HP” and Data de Fim da selected in Ficha
   * Resumo - Mastercard and at least 86 days apart from the 3HP Start Date or
   *
   * @return String
   */
  public String getCompleted3HPOnFichaResumo() {
    String query =
        "SELECT p.patient_id, o2.obs_datetime AS complete_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0"
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${53} "
            + "       AND ( (o.concept_id = ${23985} AND o.value_coded = ${23954}) "
            + "        AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "        AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) )";
    return new StringSubstitutor(getReportMetadata()).replace(query);
  }

  /**
   * Patients who have Profilaxia TPT with the value “3HP” and Estado da Profilaxia with the value
   * “Fim (F)” marked in Ficha Clínica – Mastercard and at least 86 days apart from the 3HP Start
   * Date or
   *
   * @return String
   */
  public String getCompletedDateOf3HPOnFichaClinica() {
    String query =
        "SELECT p.patient_id, o2.obs_datetime AS complete_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6}"
            + "       AND (o.concept_id = ${23985} AND o.value_coded = ${23954})  "
            + "       AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) ";

    return new StringSubstitutor(getReportMetadata()).replace(query);
  }

  /**
   * Patients who have Última Profilaxia TPT with value “3HP” and Data de Fim selected in Ficha
   * Resumo - Mastercard and at least 86 days apart from the 3HP Start Date or Patients who have
   * Profilaxia TPT with the value “3HP” and Estado da Profilaxia with the value “Fim (F)” marked in
   * Ficha Clínica – Mastercard and at least 86 days apart from the 3HP Start Date or
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoCompleted3HPAtLeast86Days() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients who completed 3HP - At Least 86 Days ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String completed3hpAllSources =
        new EptsQueriesUtil()
            .unionBuilder(getCompletedDateOf3HPOnFichaClinica())
            .union(getCompleted3HPOnFichaResumo())
            .buildQuery();

    String query =
        " SELECT tpt_completed.patient_id "
            + " FROM (        "
            + "              SELECT patient_id,  MAX(complete_date) complete_date       "
            + "              FROM (     "
            + "                   "
            + completed3hpAllSources
            + "                ) recent_3hp "
            + "                 GROUP BY recent_3hp.patient_id  "
            + "                ) tpt_completed "
            + " INNER JOIN ( "
            + "                SELECT patient_id,  MIN(start_date) start_date             "
            + "                FROM (             "
            + "              "
            + get3HPStartDateQuery()
            + "               ) start "
            + "               GROUP BY start.patient_id"
            + "            ) start on start.patient_id = tpt_completed.patient_id "
            + "WHERE TIMESTAMPDIFF(DAY,start.start_date, tpt_completed.complete_date) >= 86 "
            + "GROUP BY tpt_completed.patient_id ";

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * At least 3 consultations registered on Ficha Clínica – Mastercard with Profilaxia 3HP
   * (Profilaxia TPT=”3HP” and Estado da Profilaxia=”Início(I)/Continua(C)”) until a 4-month period
   * from the 3HP Start Date (including the 3HP Start Date)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAtLeast3ConsultationOnFichaClinica() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("At least 1 consultation registered on Ficha Clínica ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT profilaxy.patient_id,COUNT(obs_datetime) encounters "
            + "        FROM   (SELECT p.patient_id, o2.obs_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND ( o.concept_id = ${23985}  AND o.value_coded = ${23954} ) "
            + "                       AND ( o2.concept_id = ${165308} AND o2.value_coded IN ( ${1256}, ${1257} ) )"
            + "                       AND ( o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) "
            + "                       GROUP BY e.encounter_id "
            + " ) profilaxy "
            + "               INNER JOIN (SELECT patient_id,MIN(start_date) start_date "
            + "                           FROM   ("
            + get3HPStartDateQuery()
            + "                                  )tpt "
            + "                           GROUP  BY tpt.patient_id) tpt_start ON tpt_start.patient_id = profilaxy.patient_id "
            + "        WHERE  profilaxy.obs_datetime BETWEEN tpt_start.start_date AND DATE_ADD(tpt_start.start_date, INTERVAL 4 MONTH) "
            + "        GROUP  BY profilaxy.patient_id) three_encounters "
            + "WHERE  three_encounters.encounters >= 3";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * At least 1 FILT with 3HP Trimestral (Regime de TPT= 3HP/”3HP + Piridoxina” and Tipo de Dispensa
   * = Trimestral) or
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAtLeastOne3HPWithDTOnFilt() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("At least 1 FILT with 3HP Trimestral ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${60}"
            + "       AND (o.concept_id = ${23985} AND o.value_coded IN (${23954}, ${23984}))  "
            + "       AND (o2.concept_id = ${23986} AND o2.value_coded = ${23720} "
            + "       AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) "
            + "GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * At least 3 FILTs with 3HP Mensal (Regime de TPT= Isoniazida/”Isoniazida + Piridoxina” and Tipo
   * de Dispensa = Mensal) until a 4-month period from the 3HP Start Date (including the 3HP Start
   * Date) or
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAtLeast3ConsultarionWithDispensaMensalOnFilt() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("At least 3 FILTs with 3HP Mensal ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT profilaxy.patient_id,COUNT(obs_datetime) encounters "
            + "        FROM   (SELECT p.patient_id, o2.obs_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${60} "
            + "                       AND ( o.concept_id = ${23985} AND o.value_coded IN (${23954}, ${23984}) ) "
            + "                       AND ( o2.concept_id = ${23986} AND o2.value_coded = ${1098} )"
            + "                       AND ( o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) "
            + "               GROUP BY e.encounter_id "
            + " ) profilaxy "
            + "               INNER JOIN (SELECT patient_id,MIN(start_date) start_date "
            + "                           FROM   ("
            + get3HPStartDateQuery()
            + "                                  )tpt "
            + "                           GROUP  BY tpt.patient_id) tpt_start ON tpt_start.patient_id = profilaxy.patient_id "
            + "        WHERE  profilaxy.obs_datetime BETWEEN tpt_start.start_date AND DATE_ADD(tpt_start.start_date, INTERVAL 4 MONTH) "
            + "        GROUP  BY profilaxy.patient_id) three_encounters "
            + "WHERE  three_encounters.encounters >= 3";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * At least 1 consultation registered on Ficha Clínica – Mastercard with DT-3HP (Outras
   * Prescrições=“DT-3HP”) until a 4-month period from the 3HP Start Date (including the 3HP Start
   * Date) or
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAtLeast1ConsultarionWithDT3HPOnFichaClinica() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("At least 1 consultation registered on Ficha Clínica ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        " SELECT profilaxy.patient_id "
            + "        FROM   (SELECT p.patient_id, e.encounter_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND o.concept_id = ${1719} AND o.value_coded = ${165307}  "
            + "               GROUP BY e.encounter_id "
            + "                       ) profilaxy "
            + "               INNER JOIN (SELECT patient_id,MIN(start_date) start_date "
            + "                           FROM   ("
            + get3HPStartDateQuery()
            + "                                  )tpt "
            + "                           GROUP  BY tpt.patient_id) tpt_start ON tpt_start.patient_id = profilaxy.patient_id "
            + "        WHERE  profilaxy.encounter_datetime BETWEEN tpt_start.start_date AND DATE_ADD(tpt_start.start_date, INTERVAL 4 MONTH) "
            + "        GROUP  BY profilaxy.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Patients who have (Última profilaxia TPT with value “Isoniazida (INH)” and Data de selected)
   * until reporting end date registered in Ficha Resumo - Mastercard and at least 173 days apart
   * from the INH start date or
   *
   * @return String
   */
  public String getCompletedIPTOnFichaResumo() {
    String query =
        "SELECT p.patient_id, o2.obs_datetime AS complete_date  "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0"
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${53} "
            + "       AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "        AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "        AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) )";
    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    return sb.replace(query);
  }

  /**
   * Patients who have (Profilaxia TPT with the value “Isoniazida (INH)” and Estado da Profilaxia
   * with the value “Fim (F)” marked in Ficha Clínica -– Mastercard and at least 173 days apart from
   * the INH start date or
   *
   * @return String
   */
  public String getCompletedDateOfIPTOnFichaClinica() {
    String query =
        "SELECT p.patient_id, o2.obs_datetime AS complete_date  "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND (o.concept_id = ${23985} AND o.value_coded = ${656})  "
            + "       AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) ";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    return sb.replace(query);
  }

  /**
   * Patients who have (Última profilaxia TPT with value “Isoniazida (INH)” and Data de selected)
   * until reporting end date registered in Ficha Resumo - Mastercard and at least 173 days apart
   * from the INH start date or Patients who have (Profilaxia TPT with the value “Isoniazida (INH)”
   * and Estado da Profilaxia with the value “Fim (F)” marked in Ficha Clínica -– Mastercard and at
   * least 173 days apart from the INH start date or
   *
   * @return String
   */
  public CohortDefinition getPatientsWhoCompletedINHAtLeast173Days() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients who completed IPT - At Least 173 Days ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String completedInhAllSources =
        new EptsQueriesUtil()
            .unionBuilder(getCompletedIPTOnFichaResumo())
            .union(getCompletedDateOfIPTOnFichaClinica())
            .buildQuery();

    String query =
        " SELECT tpt_completed.patient_id "
            + " FROM (        "
            + "              SELECT patient_id,  MAX(complete_date) complete_date       "
            + "              FROM (     "
            + "                   "
            + completedInhAllSources
            + "                ) recent_3hp "
            + "                 GROUP BY recent_3hp.patient_id  "
            + "                ) tpt_completed "
            + " INNER JOIN ( "
            + "                SELECT patient_id,  MIN(start_date) start_date             "
            + "                FROM (             "
            + "              "
            + getIPTStartDateQuery()
            + "               ) start "
            + "               GROUP BY start.patient_id"
            + "            ) start on start.patient_id = tpt_completed.patient_id "
            + "WHERE TIMESTAMPDIFF(DAY,start.start_date, tpt_completed.complete_date) >= 173 "
            + "GROUP BY tpt_completed.patient_id ";

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * At least 5 consultations registered on Ficha Clínica or Ficha de Seguimento (Adulto or
   * Pediatria) with INH (Profilaxia TPT=” Isoniazida (INH)” and Estado da
   * Profilaxia=”Início(I)/Continua( C)”) until a 7-month period after the INH Start Date (not
   * including the INH Start Date) or
   *
   * @return
   */
  public CohortDefinition getAtLeast5ConsultarionINHOnFichaClinica() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("At least 1 consultation registered on Ficha Clínica ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT profilaxy.patient_id,COUNT(obs_datetime) encounters "
            + "        FROM   (SELECT p.patient_id, o2.obs_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type IN (${6},${9}) "
            + "                       AND ( o.concept_id = ${23985}  AND o.value_coded = ${656} ) "
            + "                       AND ( o2.concept_id = ${165308} AND o2.value_coded IN ( ${1256}, ${1257} ) ) "
            + "                       AND ( o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) "
            + "              GROUP BY e.encounter_id "
            + " ) profilaxy "
            + "               INNER JOIN (SELECT patient_id,MIN(start_date) start_date "
            + "                           FROM   ("
            + getIPTStartDateQuery()
            + "                                  )tpt "
            + "                           GROUP  BY tpt.patient_id) tpt_start ON tpt_start.patient_id = profilaxy.patient_id "
            + "        WHERE profilaxy.obs_datetime > tpt_start.start_date AND  profilaxy.obs_datetime <= DATE_ADD(tpt_start.start_date, INTERVAL 7 MONTH) "
            + "        GROUP  BY profilaxy.patient_id) three_encounters "
            + "WHERE  three_encounters.encounters >= 5";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * At least 6 FILT with INH Mensal (Regime de TPT= Isoniazida/’Isoniazida + Piridoxina’ and Tipo
   * de Dispensa = Mensal) until a 7-month period from the INH Start Date (including the INH Start
   * Date) or
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAtLeast6ConsultarionWithINHDispensaMensalOnFilt() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("At least 6 FILT with INH Mensal ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT profilaxy.patient_id,COUNT(obs_datetime) encounters "
            + "        FROM   (SELECT p.patient_id, o2.obs_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${60} "
            + "                       AND ( o.concept_id = ${23985} AND o.value_coded IN (${656}, ${23982}) ) "
            + "                       AND ( o2.concept_id = ${23986} AND o2.value_coded = ${1098} )"
            + "                       AND ( o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) "
            + "               GROUP BY e.encounter_id "
            + " ) profilaxy "
            + "               INNER JOIN (SELECT patient_id,MIN(start_date) start_date "
            + "                           FROM   ("
            + getIPTStartDateQuery()
            + "                                  )tpt "
            + "                           GROUP  BY tpt.patient_id) tpt_start ON tpt_start.patient_id = profilaxy.patient_id "
            + "        WHERE  profilaxy.obs_datetime BETWEEN tpt_start.start_date AND DATE_ADD(tpt_start.start_date, INTERVAL 7 MONTH) "
            + "        GROUP  BY profilaxy.patient_id) six_encounters "
            + "WHERE  six_encounters.encounters >= 6";
    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * At least 2 consultations registered on Ficha Clínica with DT-INH or (Profilaxia TPT=”Isoniazida
   * (INH)” and Estado da Profilaxia=“Início(I)/Continua( C) ” and Outras Prescrições = DT-INH )
   * until a 5-month period from the INH Start Date (including the INH Start Date) or
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAtLeast2ConsultarionOfDTINHOnFichaClinica() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "At least 2 consultations registered on Ficha Clínica with DT-INH ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT profilaxy.patient_id,COUNT(obs_datetime) encounters "
            + "        FROM   (SELECT p.patient_id, o2.obs_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                       INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND ( o.concept_id = ${23985}  AND o.value_coded = ${656} ) "
            + "                       AND ( o2.concept_id = ${165308} AND o2.value_coded IN ( ${1256}, ${1257} ) )"
            + "                       AND ( o3.concept_id = ${1719} AND o3.value_coded = ${23955} )"
            + "                       AND (e.encounter_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) and :endDate ) "
            + "                       AND (o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) and :endDate ) "
            + "              GROUP BY e.encounter_id "
            + "                    ) profilaxy "
            + "               INNER JOIN (SELECT patient_id,MIN(start_date) start_date "
            + "                           FROM   ("
            + getIPTStartDateQuery()
            + "                                  )tpt "
            + "                           GROUP  BY tpt.patient_id) tpt_start ON tpt_start.patient_id = profilaxy.patient_id "
            + "        WHERE  profilaxy.obs_datetime BETWEEN tpt_start.start_date AND DATE_ADD(tpt_start.start_date, INTERVAL 5 MONTH) "
            + "        GROUP  BY profilaxy.patient_id) three_encounters "
            + "WHERE  three_encounters.encounters >= 2";
    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * At least 2 FILT with DT-INH (Regime de TPT= Isoniazida/’Isoniazida + Piridoxina’ and Tipo de
   * Dispensa = Trimestral) until a 5-month period from the Start Date (including the INH Start
   * Date): or
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAtLeast2ConsultarionWithINHDispensaTrimestralOnFilt() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("At least 2 FILT with DT-INH  ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT profilaxy.patient_id,COUNT(obs_datetime) encounters "
            + "        FROM   (SELECT p.patient_id, o2.obs_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${60} "
            + "                       AND ( o.concept_id = ${23985} AND o.value_coded IN (${656}, ${23982}) ) "
            + "                       AND ( o2.concept_id = ${23986} AND o2.value_coded = ${23720} )"
            + "                       AND ( o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) "
            + "               GROUP BY e.encounter_id "
            + "            ) profilaxy "
            + "               INNER JOIN (SELECT patient_id,MIN(start_date) start_date "
            + "                           FROM   ("
            + getIPTStartDateQuery()
            + "                                  )tpt "
            + "                           GROUP  BY tpt.patient_id) tpt_start ON tpt_start.patient_id = profilaxy.patient_id "
            + "        WHERE  profilaxy.obs_datetime BETWEEN tpt_start.start_date AND DATE_ADD(tpt_start.start_date, INTERVAL 5 MONTH) "
            + "        GROUP  BY profilaxy.patient_id) three_encounters "
            + "WHERE  three_encounters.encounters >= 2";
    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * At least 3 consultations registered on Ficha Clínica with INH or (Profilaxia TPT=” Isoniazida
   * (INH)” and Estado da Profilaxia=”Início(I)/Continua( C)”)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAtLeast3ConsultarionOfINHOnFichaClinica() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("At least 3 consultations registered on Ficha Clínica with INH  ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT profilaxy.patient_id,COUNT(obs_datetime) encounters "
            + "        FROM   (SELECT p.patient_id, o2.obs_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o  ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN obs o2  ON e.encounter_id = o2.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND ( o.concept_id = ${23985}  AND o.value_coded = ${656} ) "
            + "                       AND ( o2.concept_id = ${165308} AND o2.value_coded IN ( ${1256}, ${1257} ) )"
            + "                   AND ( o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) "
            + "              GROUP BY e.encounter_id "
            + " ) profilaxy "
            + "               INNER JOIN (SELECT patient_id,MIN(start_date) start_date "
            + "                           FROM   ("
            + getIPTStartDateQuery()
            + "                                  )tpt "
            + "                           GROUP  BY tpt.patient_id) tpt_start ON tpt_start.patient_id = profilaxy.patient_id "
            + "        WHERE  profilaxy.obs_datetime BETWEEN tpt_start.start_date AND DATE_ADD(tpt_start.start_date, INTERVAL 7 MONTH) "
            + "        GROUP  BY profilaxy.patient_id) three_encounters "
            + "WHERE  three_encounters.encounters >= 3";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * + 1 Ficha Clínica com DT-INH (Profilaxia TPT=”Isoniazida (INH)” and Estado da
   * Profilaxia=“Início(I)/Continua( C)” and Outras Prescrições = DT-INH ) until a 7-month period
   * from the INH Start Date (including INH Start Date) or
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAtLeast1ConsultarionWithDTINHOnFichaClinica() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("1 Ficha Clínica com DT-INH ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        " SELECT profilaxy.patient_id "
            + "        FROM   (SELECT p.patient_id, e.encounter_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                       INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND (o.concept_id = ${1719} AND o.value_coded = ${23955} )"
            + "                       AND (o2.concept_id = ${23985} AND o.value_coded = ${656})  "
            + "                       AND (o3.concept_id = ${165308} AND o.value_coded IN (${1256}, ${1257}))  "
            + "                       AND (e.encounter_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate )  "
            + "                       AND (o3.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate )  "
            + "                GROUP BY e.encounter_id "
            + "                       ) profilaxy "
            + "               INNER JOIN (SELECT patient_id,MIN(start_date) start_date "
            + "                           FROM   ("
            + getIPTStartDateQuery()
            + "                                  )tpt "
            + "                           GROUP  BY tpt.patient_id) tpt_start ON tpt_start.patient_id = profilaxy.patient_id "
            + "        WHERE  profilaxy.encounter_datetime BETWEEN tpt_start.start_date AND DATE_ADD(tpt_start.start_date, INTERVAL 7 MONTH) "
            + "        GROUP  BY profilaxy.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * At least 3 FILT with INH Mensal (Regime de TPT= Isoniazida/’Isoniazida + Piridoxina’ and Tipo
   * de Dispensa = Mensal)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAtLeast3ConsultarionWithINHDispensaMensalOnFilt() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("At least 3 FILT with INH Mensal  ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT profilaxy.patient_id,COUNT(obs_datetime) encounters "
            + "        FROM   (SELECT p.patient_id, o2.obs_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o  ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN obs o2  ON e.encounter_id = o2.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${60} "
            + "                       AND ( o.concept_id = ${23985} AND o.value_coded IN (${656}, ${23982}) ) "
            + "                       AND ( o2.concept_id = ${23986} AND o2.value_coded = ${1098} )"
            + "                   AND ( o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) "
            + "              GROUP BY e.encounter_id "
            + "                ) profilaxy "
            + "               INNER JOIN (SELECT patient_id,MIN(start_date) start_date "
            + "                           FROM   ("
            + getIPTStartDateQuery()
            + "                                  )tpt "
            + "                           GROUP  BY tpt.patient_id) tpt_start ON tpt_start.patient_id = profilaxy.patient_id "
            + "        WHERE  profilaxy.obs_datetime BETWEEN tpt_start.start_date AND DATE_ADD(tpt_start.start_date, INTERVAL 7 MONTH) "
            + "        GROUP  BY profilaxy.patient_id) three_encounters "
            + "WHERE  three_encounters.encounters >= 3";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * + 1 FILT with DT-INH (Regime de TPT= Isoniazida/’Isoniazida + Piridoxina’ and Tipo de Dispensa
   * = Trimestral) until a 7-month period from the INH Start Date (including INH Start Date)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAtLeast1ConsultarionWithDTINHDispensaTrimestralOnFilt() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" 1 FILT with DT-INH  ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT profilaxy.patient_id,COUNT(obs_datetime) encounters "
            + "        FROM   (SELECT p.patient_id, o2.obs_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o  ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN obs o2  ON e.encounter_id = o2.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${60} "
            + "                       AND ( o.concept_id = ${23985} AND o.value_coded IN (${656}, ${23982}) ) "
            + "                       AND ( o2.concept_id = ${23986} AND o2.value_coded = ${23720} )"
            + "                   AND ( o2.obs_datetime BETWEEN DATE_SUB(:startDate, INTERVAL 6 MONTH) AND :endDate ) "
            + "                GROUP BY e.encounter_id "
            + "              ) profilaxy "
            + "               INNER JOIN (SELECT patient_id,MIN(start_date) start_date "
            + "                           FROM   ("
            + getIPTStartDateQuery()
            + "                                  )tpt "
            + "                           GROUP  BY tpt.patient_id) tpt_start ON tpt_start.patient_id = profilaxy.patient_id "
            + "        WHERE  profilaxy.obs_datetime BETWEEN tpt_start.start_date AND DATE_ADD(tpt_start.start_date, INTERVAL 7 MONTH) "
            + "        GROUP  BY profilaxy.patient_id) three_encounters "
            + "WHERE  three_encounters.encounters >= 3";

    StringSubstitutor sb = new StringSubstitutor(getReportMetadata());
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  public String getTPTStartDateQuery() {
    EptsQueriesUtil eptsQueriesUtil = new EptsQueriesUtil();
    String tptQuery =
        eptsQueriesUtil
            .unionBuilder(getStartDateOf3hpPiridoxinaOnFilt())
            .union(getStartDateOf3HPOnFichaClinica())
            .union(getStartDateOfDT3HPOnFichaClinica())
            .union(get3HPStartOnFichaResumo())
            .union(get3HPStartSeguimentoTptOnFilt())
            .union(getStartDateOfINHOnFilt())
            .union(getStartDateINHOnFichaClinica())
            .union(getINHStartDateOnFichaResumo())
            .union(getINHStartDate4InhAndSeguimentoOnFilt())
            .buildQuery();

    return tptQuery;
  }

  public String get3HPStartDateQuery() {
    EptsQueriesUtil eptsQueriesUtil = new EptsQueriesUtil();
    String tptQuery =
        eptsQueriesUtil
            .unionBuilder(getStartDateOf3hpPiridoxinaOnFilt())
            .union(get3HPStartSeguimentoTptOnFilt())
            .union(getStartDateOf3HPOnFichaClinica())
            .union(getStartDateOfDT3HPOnFichaClinica())
            .union(get3HPStartOnFichaResumo())
            .buildQuery();

    return tptQuery;
  }

  public String getIPTStartDateQuery() {
    EptsQueriesUtil eptsQueriesUtil = new EptsQueriesUtil();
    String tptQuery =
        eptsQueriesUtil
            .unionBuilder(getStartDateOfINHOnFilt())
            .union(getStartDateINHOnFichaClinica())
            .union(getINHStartDateOnFichaResumo())
            .union(getINHStartDate4InhAndSeguimentoOnFilt())
            .buildQuery();

    return tptQuery;
  }

  /**
   * And the system will select patients as New on ART (denominator and numerator) as those who have
   * the TPT start date within 6 months of initiating treatment on ART. (earliest date among the
   * above source). The system will include all patients with ART start date greater than TPT start
   * date
   *
   * @return
   */
  public CohortDefinition getPatientsWhoStartedTptAndNewOnArt() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Get Patients Who Started TPT and New on ART ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT tpt.patient_id FROM ("
            + " SELECT tpt_start.patient_id, MIN(tpt_start.start_date) AS start_date "
            + " FROM (        "
            + getTPTStartDateQuery()
            + "                ) tpt_start GROUP BY tpt_start.patient_id"
            + "    ) tpt "
            + " INNER JOIN ( "
            + commonQueries.getARTStartDate(true)
            + " ) art on art.patient_id = tpt.patient_id "
            + " WHERE tpt.start_date <= DATE_ADD(art.first_pickup, INTERVAL 6 MONTH) "
            + "GROUP BY tpt.patient_id ";

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * And the system will select patients as Previously on ART (denominator and numerator) as those
   * who have the TPI start date greater than 6 months of initiating treatment on ART (earliest date
   * among the above source).
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoStartedTptPreviouslyOnArt() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Get Patients Who Started TPT and Previously on ART ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));

    String query =
        "SELECT tpt.patient_id FROM ("
            + " SELECT tpt_start.patient_id, MIN(tpt_start.start_date) AS start_date "
            + " FROM (        "
            + getTPTStartDateQuery()
            + "                ) tpt_start GROUP BY tpt_start.patient_id"
            + "    ) tpt "
            + " INNER JOIN ( "
            + commonQueries.getARTStartDate(true)
            + " ) art on art.patient_id = tpt.patient_id "
            + " WHERE tpt.start_date > DATE_ADD(art.first_pickup, INTERVAL 6 MONTH) "
            + "GROUP BY tpt.patient_id ";

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }
}
