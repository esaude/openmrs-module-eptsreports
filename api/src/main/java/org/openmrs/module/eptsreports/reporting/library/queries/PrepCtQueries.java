package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

public class PrepCtQueries {

  /**
   * <b>Description:</b> Number of patients who initiated PREP by end of previous reporting period
   * date as follows: All clients who have “O utente esta iniciar pela 1a vez a PrEP Data” (Concept
   * id 165296 from encounter type 80) and value coded equal to “Start drugs” (concept id 1256) and
   * value_datetime < start date;
   *
   * <p>Number of patients who initiated PREP by end of previous reporting period date as follows:
   * or All clients with “Data de Inicio PrEP” (Concept id 165211 from encounter type 80) and value
   * datetime < start date;
   *
   * <p>Number of patients who initiated PREP by end of previous reporting period date as follows:
   * And had at least one follow-up visit registered in Ficha de Consulta de Seguimento PrEP
   * (encounter type 81, encounter datetime) during the reporting period
   *
   * @return
   */
  public static String getPatientsListInitiatedOnPREP(
      int initialStatusPrep,
      int prepInicialEncounterType,
      int startDrugsConcept,
      int prepStartDateConcept,
      int prepSeguimentoEncounterType) {
    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("165296", initialStatusPrep);
    valuesMap.put("80", prepInicialEncounterType);
    valuesMap.put("81", prepSeguimentoEncounterType);
    valuesMap.put("1256", startDrugsConcept);
    valuesMap.put("165211", prepStartDateConcept);
    String query =
        "SELECT patient_id FROM ( "
            + "    SELECT p.patient_id, min(tbl.earliest_date) AS  earliest_date_ever "
            + "FROM   patient p "
            + "           INNER JOIN encounter e "
            + "                      ON p.patient_id = e.patient_id "
            + "           INNER JOIN obs o "
            + "                      ON e.encounter_id = o.encounter_id "
            + "           INNER JOIN (SELECT p.patient_id, "
            + "                              o.obs_datetime AS earliest_date "
            + "                       FROM   patient p "
            + "                                  INNER JOIN encounter e "
            + "                                             ON p.patient_id = e.patient_id "
            + "                                  INNER JOIN obs o "
            + "                                             ON e.encounter_id = o.encounter_id "
            + "                       WHERE  p.voided = 0 "
            + "                         AND e.voided = 0 "
            + "                         AND o.voided = 0 "
            + "                         AND e.encounter_type = ${80} "
            + "                         AND o.concept_id = ${165296} "
            + "                         AND o.value_coded = ${1256}"
            + "                         AND e.location_id = :location "
            + "                         AND o.obs_datetime < :startDate "
            + "                       GROUP  BY p.patient_id "
            + "                       UNION "
            + "                       SELECT p.patient_id, "
            + "                              o.value_datetime AS earliest_date "
            + "                       FROM   patient p "
            + "                                  INNER JOIN encounter e "
            + "                                             ON p.patient_id = e.patient_id "
            + "                                  INNER JOIN obs o "
            + "                                             ON e.encounter_id = o.encounter_id "
            + "                       WHERE  p.voided = 0 "
            + "                         AND e.voided = 0 "
            + "                         AND o.voided = 0 "
            + "                         AND e.encounter_type = ${80} "
            + "                         AND o.concept_id = ${165211} "
            + "                         AND e.location_id = :location "
            + "                         AND o.value_datetime < :startDate "
            + "                       GROUP  BY p.patient_id ) tbl "
            + "                      ON tbl.patient_id = p.patient_id "
            + "WHERE  p.voided = 0 "
            + "  AND e.voided = 0 "
            + "  AND o.voided = 0 "
            + "  AND e.location_id = :location "
            + "GROUP  BY p.patient_id "
            + "    ) clients "
            + "WHERE "
            + "      clients.earliest_date_ever < :startDate "
            + " AND clients.patient_id IN ("
            + "                SELECT  p.patient_id "
            + "                               FROM patient p "
            + "                           INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + "                            INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "                           WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                           AND e.encounter_type=${81} AND e.encounter_datetime >= :startDate "
            + "                            AND e.encounter_datetime <= :endDate "
            + "                     AND e.location_id = :location GROUP  BY p.patient_id) ";
    StringSubstitutor sb = new StringSubstitutor(valuesMap);
    return sb.replace(query);
  }

  /**
   * <b>Description:</b> Number of patients who were transferred-in from another HF by end of the
   * previous reporting period: All clients who are enrolled in PrEP Program (PREP) (program id 25)
   * and have the first historical state as “Transferido de outra US” (patient state id= 76) in the
   * client chart by start of the reporting period;
   *
   * @return
   */
  public static String getPatientsTransferredInFromAnotherHFByEndOfPreviousReportingPeriod1(
      int prepProgramId, int transferidoDeOutraUsStateId) {
    Map<String, Integer> map = new HashMap<>();
    map.put("25", prepProgramId);
    map.put("76", transferidoDeOutraUsStateId);

    String query =
        " SELECT p.patient_id FROM patient p  "
            + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id  "
            + "INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id  "
            + "WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id=${25}  "
            + "AND ps.state=${76} AND ps.end_date IS NULL AND ps.start_date<= :startDate "
            + "AND pg.location_id= :location GROUP BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Number of patients who were transferred-in from another HF by end of the
   * previous reporting period: or All clients who have marked “Transferido de outra US”(concept id
   * 1594 value coded 1369) in the first Ficha de Consulta Inicial PrEP(encounter type 80,
   * Min(encounter datetime)) registered in the system by the start of the reporting period.
   *
   * @return
   */
  public static String getPatientsTransferredInFromAnotherHFByEndOfPreviousReportingPeriod2(
      int entryPointIntoCareConceptId,
      int transferFromAnotherFacilityConceptId,
      int prepIncialEncounterType) {
    Map<String, Integer> map = new HashMap<>();
    map.put("1594", entryPointIntoCareConceptId);
    map.put("1369", transferFromAnotherFacilityConceptId);
    map.put("80", prepIncialEncounterType);

    String query =
        "SELECT results.patient_id FROM ( "
            + " SELECT  p.patient_id, Min(e.encounter_datetime) "
            + " FROM patient p     "
            + " INNER JOIN encounter e ON e.patient_id=p.patient_id    "
            + " INNER JOIN obs o ON o.encounter_id=e.encounter_id    "
            + " WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + " AND o.concept_id = ${1594} AND o.value_coded = ${1369} "
            + " AND e.location_id = :location AND e.encounter_type=${80}  "
            + " AND e.encounter_datetime < :startDate   "
            + " GROUP  BY p.patient_id "
            + ")results";

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
            + "AND ps.state=${76} AND ps.end_date IS NULL AND ps.start_date>= :startDate AND ps.start_date<= :endDate "
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
            + "AND o.obs_datetime >= :startDate   "
            + "AND o.obs_datetime <= :endDate "
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
            + "AND o.obs_datetime >= :startDate   "
            + "AND o.obs_datetime <= :endDate "
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
   * <b>Description:</b> Negative Test Results: Clients with the field “Resultado do Teste” (concept
   * id 1040) with value “Negativo” (concept id 664) on Ficha de Consulta de Seguimento PrEP
   * (encounter type 81) registered during the reporting period;
   *
   * @return
   */
  public static String getNegativeTestResults(
      int hivRapidTest1QualitativeConceptId,
      int negativeConceptId,
      int prepIncialEncounterType,
      int prepSeguimentoEncounterType,
      int dateOfInitialHivTestConceptId) {
    Map<String, Integer> map = new HashMap<>();
    map.put("1040", hivRapidTest1QualitativeConceptId);
    map.put("664", negativeConceptId);
    map.put("80", prepIncialEncounterType);
    map.put("81", prepSeguimentoEncounterType);
    map.put("165194", dateOfInitialHivTestConceptId);

    String query =
        " SELECT patient_id  "
            + "FROM (  "
            + " SELECT negative_result.patient_id, MAX(result_date)  "
            + " FROM (  "
            + "   SELECT  p.patient_id, e.encounter_datetime AS result_date  "
            + "   FROM patient p  "
            + "   INNER JOIN encounter e ON e.patient_id=p.patient_id  "
            + "   INNER JOIN obs o ON o.encounter_id=e.encounter_id  "
            + "   WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + "   AND o.concept_id = ${1040} AND o.value_coded = ${664}  "
            + "   AND e.location_id = :location AND e.encounter_type= ${81}  "
            + "   AND e.encounter_datetime >= :startDate  "
            + "   AND e.encounter_datetime <= :endDate  "
            + "   GROUP  BY p.patient_id  "
            + "      "
            + "   UNION  "
            + "      "
            + "   SELECT  p.patient_id, o.value_datetime AS result_date  "
            + "   FROM patient p  "
            + "   INNER JOIN encounter e ON e.patient_id=p.patient_id  "
            + "   INNER JOIN obs o ON o.encounter_id=e.encounter_id  "
            + "   WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + "   AND o.concept_id = ${165194}  "
            + "   AND o.value_datetime  >= :startDate  "
            + "   AND o.value_datetime <= :endDate  "
            + "   AND e.location_id = :location AND e.encounter_type=${80}  "
            + "   AND e.encounter_datetime >= :startDate  "
            + "   AND e.encounter_datetime <= :endDate  "
            + "   GROUP  BY p.patient_id  "
            + "    ) negative_result  "
            + " GROUP BY negative_result.patient_id  "
            + ") negative";

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
   * (encounter type 81) registered during the reporting period; or Clients without the field
   * “Resultado do Teste” (concept id not in 1040, null) marked with any value on Ficha de Consulta
   * de Seguimento PrEP (encounter type 81) registered during the reporting period; AND Clients
   * without “Data do teste HIV com resultado Negativo no Inicio da PrEP” (concept id 165194, value
   * datetime null) filled on Ficha de Consulta Inicial PrEP (encounter type 80) registered during
   * the reporting period;
   *
   * @return
   */
  public static String getOtherTestResults(
      int hivRapidTest1QualitativeConceptId,
      int indeterminateConceptId,
      int prepSeguimentoEncounterType,
      int prepIncialEncounterType,
      int dateOfHivTestWithNegativeResultsPrepConceptId) {
    Map<String, Integer> map = new HashMap<>();
    map.put("1040", hivRapidTest1QualitativeConceptId);
    map.put("1138", indeterminateConceptId);
    map.put("81", prepSeguimentoEncounterType);
    map.put("80", prepIncialEncounterType);
    map.put("165293", dateOfHivTestWithNegativeResultsPrepConceptId);

    String query =
        "SELECT patient_id FROM "
            + "( "
            + "SELECT  p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "AND o.concept_id = ${1040} AND o.value_coded = ${1138} "
            + "AND e.location_id = :location AND e.encounter_type = ${81} "
            + "AND e.encounter_datetime >= :startDate "
            + "AND e.encounter_datetime <= :endDate "
            + "GROUP  BY p.patient_id "
            + " "
            + "UNION "
            + " "
            + "SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id  = p.patient_id "
            + "WHERE p.voided = 0 "
            + "AND e.voided = 0 "
            + "AND e.encounter_type = ${81} "
            + "AND e.encounter_id = :location "
            + "AND e.encounter_datetime >= :startDate "
            + "AND e.encounter_datetime <= :endDate "
            + "AND p.patient_id "
            + "NOT IN( "
            + " "
            + "SELECT  pa.patient_id "
            + "FROM patient pa "
            + "INNER JOIN encounter en ON en.patient_id=pa.patient_id "
            + "INNER JOIN obs ob ON ob.encounter_id=en.encounter_id "
            + "WHERE  pa.voided = 0 AND en.voided = 0 AND ob.voided = 0 "
            + "AND ob.concept_id = ${1040} "
            + "AND en.location_id = :location AND e.encounter_type= ${81} "
            + "AND en.encounter_datetime >= :startDate "
            + "AND en.encounter_datetime <= :endDate "
            + "GROUP  BY pa.patient_id "
            + " "
            + " "
            + ") "
            + "GROUP BY p.patient_id "
            + " "
            + "UNION "
            + " "
            + "SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "WHERE p.voided = 0 "
            + "AND e.voided = 0 "
            + "AND e.encounter_datetime >= :startDate "
            + "AND e.encounter_datetime <= :endDate "
            + "AND e.encounter_type = ${80} "
            + "AND e.location_id = :location "
            + "AND p.patient_id "
            + "NOT IN ( "
            + " SELECT  pa.patient_id "
            + "FROM patient pa "
            + "INNER JOIN encounter en ON en.patient_id=pa.patient_id "
            + "INNER JOIN obs ob ON ob.encounter_id=en.encounter_id "
            + "WHERE  pa.voided = 0 AND en.voided = 0 AND ob.voided = 0 "
            + "AND ob.concept_id = ${165293} "
            + "AND ob.value_datetime IS NOT NULL "
            + "AND en.location_id = :location AND en.encounter_type= ${80} "
            + "AND en.encounter_datetime >= :startDate "
            + "AND en.encounter_datetime <= :endDate "
            + "GROUP  BY pa.patient_id "
            + ") "
            + ") other_result "
            + "GROUP BY other_result.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> For clients with birth date information registered in the system the age of
   * the client will be calculated at the end date of the reporting period (reporting end date minus
   * birthdate / 365. Clients without birth date information should be considered in unknown age
   *
   * @return
   */
  public static String patientAgeBasedOnPrepEndDate(int minAge, int maxAge) {
    Map<String, Integer> map = new HashMap<>();
    map.put("minAge", minAge);
    map.put("maxAge", maxAge);

    String query =
        "SELECT patient_id FROM( "
            + "SELECT  p.patient_id , TIMESTAMPDIFF(YEAR, pn.birthdate, :endDate ) AS age "
            + "FROM patient p    "
            + "INNER JOIN person pn ON pn.person_id=p.patient_id     "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id     "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND pn.voided = 0   "
            + "AND e.location_id = :location  "
            + "GROUP  BY p.patient_id) age_data "
            + "WHERE age BETWEEN ${minAge} AND ${maxAge}";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Pregnant Clients with the field “Se mulher, indique o estado de
   * Gravidez/Lactação” (concept id = 165223) marked with value “Grávida” (concept id =1982) on the
   * most recent Ficha de Consulta de Seguimento PrEP(encounter type 81) during the reporting period
   * (encounter datetime between start date and end date); or Clients with the field “Mulher
   * Grávida” (concept id =1982) marked on section C “Estado da Mulher” with the value “Sim”(value
   * coded = 1065) on the most recent Ficha de Consulta Inicial PrEP (encounter type 80) during the
   * reporting period(encounter datetime between start date and end date)
   *
   * @return
   */
  public static String pregnantPatientsBasedOnPrep(
      int prepIncialEncounterType,
      int prepSeguimentoEncounterType,
      int currentStateOfTheWomanConceptId,
      int pregnantConceptId,
      int yesConceptId,
      int breastfeedingConceptId) {
    Map<String, Integer> map = new HashMap<>();
    map.put("80", prepIncialEncounterType);
    map.put("81", prepSeguimentoEncounterType);
    map.put("165223", currentStateOfTheWomanConceptId);
    map.put("1982", pregnantConceptId);
    map.put("1065", yesConceptId);
    map.put("6332", breastfeedingConceptId);

    String query =
        "SELECT pregnant.patient_id FROM ( "
            + "SELECT p.patient_id, MAX(e.encounter_datetime) pregnancy_date "
            + "FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "INNER JOIN obs o ON o.encounter_id  = e.encounter_id "
            + "WHERE p.voided  = 0 "
            + "AND e.voided = 0 "
            + "AND o.voided = 0 "
            + "AND e.location_id = :location "
            + "AND e.encounter_type IN (${81},${80}) "
            + "AND o.concept_id IN (${165223},${1982}) "
            + "AND o.value_coded IN (${1982},${1065}) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "GROUP BY p.patient_id "
            + ") pregnant "
            + " LEFT JOIN (SELECT  p.patient_id, MAX(e.encounter_datetime) breastfeed_date "
            + "   FROM patient p "
            + "   INNER JOIN encounter e ON e.patient_id  = p.patient_id "
            + "   INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "   WHERE p.voided = 0 "
            + "   AND e.voided = 0 "
            + "   AND o.voided = 0 "
            + "   AND e.location_id = :location "
            + "   AND e.encounter_type IN (${81},${80}) "
            + "   AND o.concept_id IN(${165223},${6332}) "
            + "   AND o.value_coded IN(${6332},${1065}) "
            + "   AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "   GROUP BY p.patient_id "
            + " "
            + ") AS breastfeeding "
            + " ON breastfeeding.patient_id = pregnant.patient_id "
            + " "
            + " WHERE pregnant.pregnancy_date >= breastfeeding.breastfeed_date OR breastfeeding.breastfeed_date IS NULL "
            + " GROUP BY pregnant.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Pregnant Clients with the field “Se mulher, indique o estado de
   * Gravidez/Lactação” (concept id = 165223) marked with value “Grávida” (concept id =1982) on the
   * most recent Ficha de Consulta de Seguimento PrEP(encounter type 81) during the reporting period
   * (encounter datetime between start date and end date); or Clients with the field “Mulher
   * Grávida” (concept id =1982) marked on section C “Estado da Mulher” with the value “Sim”(value
   * coded = 1065) on the most recent Ficha de Consulta Inicial PrEP (encounter type 80) during the
   * reporting period(encounter datetime between start date and end date)
   *
   * @return
   */
  public static String breastfeedingPatientsBasedOnPrep(
      int prepIncialEncounterType,
      int prepSeguimentoEncounterType,
      int currentStateOfTheWomanConceptId,
      int pregnantConceptId,
      int yesConceptId,
      int breastfeedingConceptId) {
    Map<String, Integer> map = new HashMap<>();
    map.put("80", prepIncialEncounterType);
    map.put("81", prepSeguimentoEncounterType);
    map.put("165223", currentStateOfTheWomanConceptId);
    map.put("1982", pregnantConceptId);
    map.put("1065", yesConceptId);
    map.put("6332", breastfeedingConceptId);

    String query =
        " SELECT breastfeeding.patient_id FROM ( "
            + "SELECT  p.patient_id, MAX(e.encounter_datetime) breastfeed_date "
            + "   FROM patient p "
            + "   INNER JOIN encounter e ON e.patient_id  = p.patient_id "
            + "   INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "   WHERE p.voided = 0 "
            + "   AND e.voided = 0 "
            + "   AND o.voided = 0 "
            + "   AND e.location_id = :location "
            + "   AND e.encounter_type IN (${81},${80}) "
            + "   AND o.concept_id IN(${165223},${6332}) "
            + "   AND o.value_coded IN(${6332},${1065}) "
            + "   AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "   GROUP BY p.patient_id "
            + ") breastfeeding "
            + "LEFT JOIN ( "
            + "SELECT p.patient_id, MAX(e.encounter_datetime) pregnancy_date "
            + "FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "INNER JOIN obs o ON o.encounter_id  = e.encounter_id "
            + "WHERE p.voided  = 0 "
            + "AND e.voided = 0 "
            + "AND o.voided = 0 "
            + "AND e.location_id = :location "
            + "AND e.encounter_type IN (${81},${80}) "
            + "AND o.concept_id IN (${165223},${1982}) "
            + "AND o.value_coded IN (${1982},${1065}) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "GROUP BY p.patient_id "
            + ") AS pregnant "
            + "ON breastfeeding.patient_id = pregnant.patient_id "
            + "WHERE breastfeeding.breastfeed_date > pregnant.pregnancy_date  OR pregnant.pregnancy_date IS NULL "
            + "GROUP BY breastfeeding.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }
}
