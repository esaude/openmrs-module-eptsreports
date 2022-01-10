package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

public class PrepCtQueries {

  /**
   * <b>Description:</b> Number of patients who initiated PREP by end of previous reporting period
   * date as follows: All clients who have “O utente esta iniciar pela 1a vez a PrEP Data” (Concept
   * id 165296 from encounter type 80) and value coded equal to “Start drugs” (concept id 1256) and
   * value_datetime < start date; or All clients with “Data de Inicio PrEP” (Concept id 165211 from
   * encounter type 80) and value datetime < start date; And had at least one follow-up visit
   * registered in Ficha de Consulta de Seguimento PrEP (encounter type 81, encounter datetime)
   * during the reporting period
   *
   * @return
   */
  public static String getPatientsListInitiatedOnPREP(
      int prepIncialEncounterType,
      int prepSeguimentoEncounterType,
      int initialStatusOfPrEPUserConceptId,
      int startDrugsConceptId,
      int prepStartDateConceptId) {
    Map<String, Integer> map = new HashMap<>();
    map.put("80", prepIncialEncounterType);
    map.put("81", prepSeguimentoEncounterType);
    map.put("165296", initialStatusOfPrEPUserConceptId);
    map.put("1256", startDrugsConceptId);
    map.put("165211", prepStartDateConceptId);

    String query =
        "SELECT  p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "AND e.encounter_type=${80} AND "
            + "( "
            + " (o.concept_id=${165296} AND o.value_coded=${1256}) "
            + " OR "
            + " (o.concept_id=${165211}) "
            + ") "
            + " AND o.value_datetime < :startDate "
            + " AND e.location_id = :location "
            + " AND p.patient_id IN ( "
            + " SELECT  p.patient_id "
            + " FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + " INNER JOIN obs o ON o.encounter_id=e.encounter_id  "
            + " WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + " AND e.encounter_type=${81} AND e.encounter_datetime >= '2021-03-21' "
            + " AND e.encounter_datetime <= :endDate "
            + " AND e.location_id = :location "
            + " GROUP  BY p.patient_id "
            + ") "
            + "GROUP BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Number of patients who were transferred-in from another HF by end of the
   * previous reporting period: All clients who are enrolled in PrEP Program (PREP) (program id 25)
   * and have the first historical state as “Transferido de outra US” (patient state id= 76) in the
   * client chart by start of the reporting period; or All clients who have marked “Transferido de
   * outra US”(concept id 1594 value coded 1369) in the first Ficha de Consulta Inicial
   * PrEP(encounter type 80, Min(encounter datetime)) registered in the system by the start of the
   * reporting period.
   *
   * @return
   */
  public static String getPatientsTransferredInFromAnotherHFByEndOfReportingPeriod(
      int entryPointIntoCareConceptId,
      int transferFromAnotherFacilityConceptId,
      int prepIncialEncounterType,
      int prepProgramId,
      int transferidoDeOutraUsStateId) {
    Map<String, Integer> map = new HashMap<>();
    map.put("1594", entryPointIntoCareConceptId);
    map.put("1369", transferFromAnotherFacilityConceptId);
    map.put("80", prepIncialEncounterType);
    map.put("25", prepProgramId);
    map.put("76", transferidoDeOutraUsStateId);

    String query =
        "SELECT results.patient_id FROM ( "
            + " SELECT  p.patient_id, Min(e.encounter_datetime) "
            + " FROM patient p     "
            + " INNER JOIN encounter e ON e.patient_id=p.patient_id    "
            + " INNER JOIN obs o ON o.encounter_id=e.encounter_id    "
            + " WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + " AND o.concept_id = ${1594} AND o.value_coded = ${1369} "
            + " AND e.location_id = :location AND e.encounter_type=${80}  "
            + " AND e.encounter_datetime >= :startDate   "
            + " AND e.encounter_datetime <= :endDate "
            + " GROUP  BY p.patient_id "
            + ")results "
            + "UNION "
            + "SELECT p.patient_id FROM patient p  "
            + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id  "
            + "INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id  "
            + "WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id=${25}  "
            + "AND ps.patient_state_id=${76} AND ps.end_date IS NULL AND ps.start_date<= :endDate "
            + "AND pg.location_id= :location GROUP BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Number of patients who Transferred in during the reporting period: All
   * clients who are enrolled in PrEP Program (PREP)(program id 25) and have the first historical
   * state as “Transferido de outra US” (patient state id= 76) in the client chart during the
   * reporting period; or All clients who have marked “Transferido de outra US”(concept id 1594
   * value coded 1369) in the first Ficha de Consulta Inicial PrEP(encounter type 80, Min(encounter
   * datetime)) registered in the system during the reporting period.
   *
   * @return
   */
  public static String getPatientsTransferredInFromAnotherHFDuringReportingPeriod(
      int entryPointIntoCareConceptId,
      int transferFromAnotherFacilityConceptId,
      int prepIncialEncounterType,
      int prepProgramId,
      int transferidoDeOutraUsStateId) {
    Map<String, Integer> map = new HashMap<>();
    map.put("1594", entryPointIntoCareConceptId);
    map.put("1369", transferFromAnotherFacilityConceptId);
    map.put("80", prepIncialEncounterType);
    map.put("25", prepProgramId);
    map.put("76", transferidoDeOutraUsStateId);

    String query =
        "SELECT results.patient_id FROM ( "
            + " SELECT  p.patient_id, Min(e.encounter_datetime) "
            + " FROM patient p     "
            + " INNER JOIN encounter e ON e.patient_id=p.patient_id    "
            + " INNER JOIN obs o ON o.encounter_id=e.encounter_id    "
            + " WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + " AND o.concept_id = ${1594} AND o.value_coded = ${1369} "
            + " AND e.location_id = :location AND e.encounter_type=${80}  "
            + " AND e.encounter_datetime >= :startDate   "
            + " AND e.encounter_datetime <= :endDate "
            + " GROUP  BY p.patient_id "
            + ")results "
            + "UNION "
            + "SELECT p.patient_id FROM patient p  "
            + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id  "
            + "INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id  "
            + "WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id=${25}  "
            + "AND ps.patient_state_id=${76} AND ps.end_date IS NULL AND ps.start_date<= :endDate "
            + "AND pg.location_id= :location GROUP BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Re-initiated PrEP during the reporting period All clients who have “O
   * utente está a Retomar PrEP” (Concept id 165296 from encounter type 80) value coded “RESTART”
   * (concept id 1705) and value datetime between start date and end date;
   *
   * @return
   */
  public static String getPatientsReInitiatedToPrEP(
      int initialStatusOfThePrEPUserConceptId, int restartConceptId, int prepIncialEncounterType) {
    Map<String, Integer> map = new HashMap<>();
    map.put("165296", initialStatusOfThePrEPUserConceptId);
    map.put("1705", restartConceptId);
    map.put("80", prepIncialEncounterType);

    String query =
        " SELECT  p.patient_id "
            + "FROM patient p     "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id    "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id    "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + "AND o.concept_id = ${165296} AND o.value_coded = ${1705} "
            + "AND e.location_id = :location AND e.encounter_type=${80}  "
            + "AND e.encounter_datetime >= :startDate   "
            + "AND e.encounter_datetime <= :endDate "
            + "GROUP  BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Continued PrEP during the reporting period All clients who have “O utente
   * está a Continuar PrEP” (Concept id 165296 from encounter type 80) value coded “CONTINUE REGIMEN
   * ” (concept id 1257) and value datetime between start date and end date;
   *
   * @return
   */
  public static String getPatientsOnContinuedPrEP(
      int initialStatusOfThePrEPUserConceptId,
      int continueRegimenConceptId,
      int prepIncialEncounterType) {
    Map<String, Integer> map = new HashMap<>();
    map.put("165296", initialStatusOfThePrEPUserConceptId);
    map.put("1257", continueRegimenConceptId);
    map.put("80", prepIncialEncounterType);

    String query =
        " SELECT  p.patient_id "
            + "FROM patient p     "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id    "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id    "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + "AND o.concept_id = ${165296} AND o.value_coded = ${1257} "
            + "AND e.location_id = :location AND e.encounter_type=${80}  "
            + "AND e.encounter_datetime >= :startDate   "
            + "AND e.encounter_datetime <= :endDate "
            + "GROUP  BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Positive Test Results: Clients with the field “Resultado do Teste”(concept
   * id 1040) with value “Positivo” (concept id 703) on Ficha de Consulta de Seguimento
   * PrEP(encounter type 81) registered during the reporting period;
   *
   * @return
   */
  public static String getPositiveTestResults(
      int hivRapidTest1QualitativeConceptId,
      int positiveConceptId,
      int prepSeguimentoEncounterType) {
    Map<String, Integer> map = new HashMap<>();
    map.put("1040", hivRapidTest1QualitativeConceptId);
    map.put("703", positiveConceptId);
    map.put("81", prepSeguimentoEncounterType);

    String query =
        " SELECT  p.patient_id "
            + "FROM patient p     "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id    "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id    "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + "AND o.concept_id = ${1040} AND o.value_coded = ${703} "
            + "AND e.location_id = :location AND e.encounter_type=${81}  "
            + "AND e.encounter_datetime >= :startDate   "
            + "AND e.encounter_datetime <= :endDate "
            + "GROUP  BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Negative Test Results Part 1: Clients with the field “Resultado do Teste”
   * (concept id 1040) with value “Negativo” (concept id 664) on Ficha de Consulta de Seguimento
   * PrEP (encounter type 81) registered during the reporting period;
   *
   * @return
   */
  public static String getNegativeTestResultsPart1(
      int hivRapidTest1QualitativeConceptId,
      int negativeConceptId,
      int prepSeguimentoEncounterType) {
    Map<String, Integer> map = new HashMap<>();
    map.put("1040", hivRapidTest1QualitativeConceptId);
    map.put("664", negativeConceptId);
    map.put("81", prepSeguimentoEncounterType);

    String query =
        " SELECT  p.patient_id "
            + "FROM patient p     "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id    "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id    "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + "AND o.concept_id = ${1040} AND o.value_coded = ${664} "
            + "AND e.location_id = :location AND e.encounter_type= ${81}  "
            + "AND e.encounter_datetime >= :startDate   "
            + "AND e.encounter_datetime <= :endDate "
            + "GROUP  BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Negative Test Results Part 2: Clients with the field “Data do Teste de HIV
   * com resultado negativo no Inicio da PrEP” (concept id 165194, value datetime) marked with date
   * that falls during the reporting period on Ficha de Consulta Inicial PrEP (encounter type 80)
   *
   * @return
   */
  public static String getNegativeTestResultsPart2(
      int dateOfInitialHivTestConceptId, int prepIncialEncounterType) {
    Map<String, Integer> map = new HashMap<>();
    map.put("165194", dateOfInitialHivTestConceptId);
    map.put("80", prepIncialEncounterType);

    String query =
        " SELECT  p.patient_id  "
            + "FROM patient p      "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id     "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id     "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + "AND o.concept_id = ${165194}   "
            + "AND o.value_datetime  >= :startDate    "
            + "AND o.value_datetime  >= :endDate  "
            + "AND e.location_id = :location AND e.encounter_type=${80}  "
            + "AND e.encounter_datetime >= :startDate   "
            + "AND e.encounter_datetime <= :endDate  "
            + "GROUP  BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Other Test Results: Clients with the field “Resultado do Teste” (concept id
   * 1040) with value “Indeterminado” (concept id 1138) on Ficha de Consulta de Seguimento PrEP
   * (encounter type 81) registered during the reporting period;
   *
   * @return
   */
  public static String getOtherTestResultsPart1(
      int hivRapidTest1QualitativeConceptId,
      int indeterminateConceptId,
      int prepSeguimentoEncounterType) {
    Map<String, Integer> map = new HashMap<>();
    map.put("1040", hivRapidTest1QualitativeConceptId);
    map.put("1138", indeterminateConceptId);
    map.put("81", prepSeguimentoEncounterType);

    String query =
        " SELECT  p.patient_id "
            + "FROM patient p     "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id    "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id    "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + "AND o.concept_id = ${1040} AND o.value_coded = ${1138} "
            + "AND e.location_id = :location AND e.encounter_type=${81}  "
            + "AND e.encounter_datetime >= :startDate   "
            + "AND e.encounter_datetime <= :endDate "
            + "GROUP  BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Other Test Results: Clients without the field “Resultado do Teste” (concept
   * id not in 1040, null) marked with any value on Ficha de Consulta de Seguimento PrEP (encounter
   * type 81) registered during the reporting period;
   *
   * @return
   */
  public static String getOtherTestResultsPart2(
      int hivRapidTest1QualitativeConceptId, int prepSeguimentoEncounterType) {
    Map<String, Integer> map = new HashMap<>();
    map.put("1040", hivRapidTest1QualitativeConceptId);
    map.put("81", prepSeguimentoEncounterType);

    String query =
        " SELECT  p.patient_id "
            + "FROM patient p     "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id    "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id    "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + "AND o.concept_id <> ${1040} "
            + "AND e.location_id = :location AND e.encounter_type=${81}  "
            + "AND e.encounter_datetime >= :startDate   "
            + "AND e.encounter_datetime <= :endDate "
            + "GROUP  BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Other Test Results: Clients without “Data do teste HIV com resultado
   * Negativo no Inicio da PrEP” (concept id 165194, value datetime null) filled on Ficha de
   * Consulta Inicial PrEP (encounter type 80) registered during the reporting period;
   *
   * @return
   */
  public static String getOtherTestResultsPart3(
      int dateOfInitialHivTestConceptId, int prepIncialEncounterType) {
    Map<String, Integer> map = new HashMap<>();
    map.put("165194", dateOfInitialHivTestConceptId);
    map.put("80", prepIncialEncounterType);

    String query =
        " SELECT  p.patient_id  "
            + "FROM patient p      "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id     "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id     "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + "AND o.concept_id = ${165194}   "
            + "AND o.value_datetime  IS NULL    "
            + "AND e.location_id = :location AND e.encounter_type=${80}  "
            + "AND e.encounter_datetime >= :startDate   "
            + "AND e.encounter_datetime <= :endDate  "
            + "GROUP  BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }
}
