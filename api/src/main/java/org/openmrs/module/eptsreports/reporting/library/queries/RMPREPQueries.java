package org.openmrs.module.eptsreports.reporting.library.queries;

import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RMPREPQueries {

  /**
   * @param prepInicialEncounterType
   * @param acceptStartMedicationConcept
   * @param yesConcept
   * @return
   */
  public static String getA1(
      int prepInicialEncounterType, int acceptStartMedicationConcept, int yesConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("Clients Eligible for PrEP");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    valuesMap.put("80", prepInicialEncounterType);
    valuesMap.put("165289", acceptStartMedicationConcept);
    valuesMap.put("1065", yesConcept);

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "           INNER JOIN encounter e "
            + "                      ON p.patient_id = e.patient_id "
            + "           INNER JOIN obs o "
            + "                      ON e.encounter_id = o.encounter_id "
            + "           INNER JOIN (SELECT p.patient_id, "
            + "                              e.encounter_datetime AS eligible_date "
            + "                       FROM   patient p "
            + "                                  INNER JOIN encounter e "
            + "                                             ON p.patient_id = e.patient_id "
            + "                                  INNER JOIN obs o "
            + "                                             ON e.encounter_id = o.encounter_id "
            + "                       WHERE  p.voided = 0 "
            + "                         AND e.voided = 0 "
            + "                         AND o.voided = 0 "
            + "                         AND e.encounter_type = ${80} "
            + "                         AND e.location_id = :location "
            + "                         AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                       GROUP  BY p.patient_id "
            + "                     ) inicial_consultation "
            + "                      ON inicial_consultation.patient_id = p.patient_id "
            + "WHERE  p.voided = 0 "
            + "  AND e.voided = 0 "
            + "  AND o.voided = 0 "
            + "  AND e.encounter_type = ${80} "
            + "  AND o.concept_id = ${165289} "
            + "  AND o.value_coded = ${1065} "
            + "  AND e.location_id = :location "
            + "  AND e.encounter_datetime = inicial_consultation.eligible_date "
            + "GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);
    return sb.replace(query);
  }

  public static String getB2(
      int prepInicialEncounterType, int initialStatusPrep, int restartConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("Clients Eligible for PrEP");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    valuesMap.put("80", prepInicialEncounterType);
    valuesMap.put("165296", initialStatusPrep);
    valuesMap.put("1705", restartConcept);

    String query =
        " SELECT p.patient_id "
            + "FROM   patient p "
            + "           INNER JOIN encounter e "
            + "                      ON p.patient_id = e.patient_id "
            + "           INNER JOIN obs o "
            + "                      ON e.encounter_id = o.encounter_id "
            + "WHERE p.voided = 0 "
            + "  AND e.voided = 0 "
            + "  AND o.voided = 0 "
            + "  AND e.encounter_type = ${80} "
            + "  AND o.concept_id = ${165296} "
            + "  AND o.value_coded = ${1705} "
            + "  AND e.location_id = :location "
            + "  AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);
    return sb.replace(query);
  }

  /**
   * @param prepInicialEncounterType
   * @param prepSeguimentoEncounterType
   * @param prepRegimeConcept
   * @param tdfAnd3tcConcept
   * @param tdfAndFtcConcept
   * @param otherDrugForPrepConcept
   * @return
   */
  public static String getC1(
      int prepInicialEncounterType,
      int prepSeguimentoEncounterType,
      int prepRegimeConcept,
      int tdfAnd3tcConcept,
      int tdfAndFtcConcept,
      int otherDrugForPrepConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("All Clients Who Received PrEP");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    valuesMap.put("80", prepInicialEncounterType);
    valuesMap.put("81", prepSeguimentoEncounterType);
    valuesMap.put("165213", prepRegimeConcept);
    valuesMap.put("165214", tdfAnd3tcConcept);
    valuesMap.put("165215", tdfAndFtcConcept);
    valuesMap.put("165216", otherDrugForPrepConcept);

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT p.patient_id "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e "
            + "                       ON p.patient_id = e.patient_id "
            + "               INNER JOIN obs o "
            + "                       ON e.encounter_id = o.encounter_id "
            + "        WHERE  p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND e.encounter_type = ${80} "
            + "               AND o.concept_id = ${165213} "
            + "               AND o.value_coded IN ( ${165214}, ${165215}, ${165216} ) "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "        GROUP  BY p.patient_id "
            + "        UNION "
            + "        SELECT p.patient_id "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e "
            + "                       ON p.patient_id = e.patient_id "
            + "               INNER JOIN obs o "
            + "                       ON e.encounter_id = o.encounter_id "
            + "        WHERE  p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND e.encounter_type = ${81} "
            + "               AND o.concept_id = ${165213} "
            + "               AND o.value_coded IN ( ${165214}, ${165215} ) "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "        GROUP  BY p.patient_id) received";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);
    return sb.replace(query);
  }

  /**
   * @param prepInicialEncounterType
   * @param prepSeguimentoEncounterType
   * @param numberOfBottles
   * @param initialStatusPrep
   * @param startDrugsConcept
   * @param prepStartDateConcept
   * @param prepProgram
   * @param stateOfStayOnPrepProgram
   * @param referalType
   * @param transferredFromOtherFacility
   * @return
   */
  public static String getD1(
      int prepInicialEncounterType,
      int prepSeguimentoEncounterType,
      int numberOfBottles,
      int initialStatusPrep,
      int startDrugsConcept,
      int prepStartDateConcept,
      int prepProgram,
      int stateOfStayOnPrepProgram,
      int referalType,
      int transferredFromOtherFacility) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("All Clients with at least 2 pickups");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    valuesMap.put("80", prepInicialEncounterType);
    valuesMap.put("81", prepSeguimentoEncounterType);
    valuesMap.put("165217", numberOfBottles);
    valuesMap.put("165296", initialStatusPrep);
    valuesMap.put("1256", startDrugsConcept);
    valuesMap.put("165211", prepStartDateConcept);
    valuesMap.put("25", prepProgram);
    valuesMap.put("76", stateOfStayOnPrepProgram);
    valuesMap.put("1594", referalType);
    valuesMap.put("1369", transferredFromOtherFacility);

    String query =
        "SELECT patient_id from ( "
            + "  SELECT p.patient_id, SUM(o.value_numeric) as pickups FROM patient p "
            + "                               INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                               INNER JOIN ( "
            + "      SELECT clients.patient_id, clients.earliest_date_ever as previous_date FROM ( "
            + "                                                                                      SELECT p.patient_id, min(tbl.earliest_date) AS  earliest_date_ever "
            + "                                                                                      FROM   patient p "
            + "                                                                                                 INNER JOIN encounter e "
            + "                                                                                                            ON p.patient_id = e.patient_id "
            + "                                                                                                 INNER JOIN obs o "
            + "                                                                                                            ON e.encounter_id = o.encounter_id "
            + "                                                                                                 INNER JOIN (SELECT p.patient_id, "
            + "                                                                                                                    o.obs_datetime AS earliest_date "
            + "                                                                                                             FROM   patient p "
            + "                                                                                                                        INNER JOIN encounter e "
            + "                                                                                                                                   ON p.patient_id = e.patient_id "
            + "                                                                                                                        INNER JOIN obs o "
            + "                                                                                                                                   ON e.encounter_id = o.encounter_id "
            + "                                                                                                             WHERE  p.voided = 0 "
            + "                                                                                                               AND e.voided = 0 "
            + "                                                                                                               AND o.voided = 0 "
            + "                                                                                                               AND e.encounter_type = ${80} "
            + "                                                                                                               AND o.concept_id = ${165296} "
            + "                                                                                                               AND o.value_coded = ${1256} "
            + "                                                                                                               AND e.location_id = :location "
            + "                                                                                                               AND o.obs_datetime <= :endDate "
            + "                                                                                                             GROUP  BY p.patient_id "
            + "                                                                                                             UNION "
            + "                                                                                                             SELECT p.patient_id, "
            + "                                                                                                                    o.value_datetime AS earliest_date "
            + "                                                                                                             FROM   patient p "
            + "                                                                                                                        INNER JOIN encounter e "
            + "                                                                                                                                   ON p.patient_id = e.patient_id "
            + "                                                                                                                        INNER JOIN obs o "
            + "                                                                                                                                   ON e.encounter_id = o.encounter_id "
            + "                                                                                                             WHERE  p.voided = 0 "
            + "                                                                                                               AND e.voided = 0 "
            + "                                                                                                               AND o.voided = 0 "
            + "                                                                                                               AND e.encounter_type = ${80} "
            + "                                                                                                               AND o.concept_id = ${165211} "
            + "                                                                                                               AND e.location_id = :location "
            + "                                                                                                               AND o.value_datetime <= :endDate "
            + "                                                                                                             GROUP  BY p.patient_id) tbl "
            + "                                                                                                            ON tbl.patient_id = p.patient_id "
            + "                                                                                      WHERE  p.voided = 0 "
            + "                                                                                        AND e.voided = 0 "
            + "                                                                                        AND o.voided = 0 "
            + "                                                                                        AND e.location_id = :location "
            + "                                                                                      GROUP  BY p.patient_id "
            + "                                                                                  ) clients "
            + "      WHERE "
            + "          clients.earliest_date_ever BETWEEN "
            + "              DATE_SUB(:startDate, INTERVAL 1 MONTH) "
            + "              AND DATE_SUB(:endDate, INTERVAL 1 MONTH) "
            + " AND clients.patient_id NOT IN ( SELECT patient_id FROM ( "
            + "                           SELECT p.patient_id, ps.start_date AS start_date "
            + "                           FROM   patient p "
            + "                                      INNER JOIN patient_program pg "
            + "                                                 ON p.patient_id = pg.patient_id "
            + "                                      INNER JOIN patient_state ps "
            + "                                                 ON pg.patient_program_id = "
            + "                                                    ps.patient_program_id "
            + "                           WHERE  pg.program_id = ${25} AND ps.voided = 0 "
            + "                             AND pg.voided = 0 "
            + "                             AND ps.state = ${76} "
            + "                             AND ps.start_date < :endDate "
            + "                             AND pg.location_id = :location "
            + "                           GROUP  BY p.patient_id "
            + "                           UNION "
            + "                           SELECT p.patient_id, "
            + "                                  Min(e.encounter_datetime) AS start_date "
            + "                           FROM   patient p "
            + "                                      INNER JOIN encounter e "
            + "                                                 ON p.patient_id = e.patient_id "
            + "                                      INNER JOIN obs o "
            + "                                                 ON e.encounter_id = o.encounter_id "
            + "                           WHERE  p.voided = 0 "
            + "                             AND e.voided = 0 "
            + "                             AND o.voided = 0 "
            + "                             AND e.encounter_type = ${80} "
            + "                             AND o.concept_id = ${1594} "
            + "                             AND o.value_coded = ${1369} "
            + "                             AND e.location_id = :location "
            + "                             AND e.encounter_datetime < :endDate "
            + "                           GROUP  BY p.patient_id "
            + "                       ) transferred ) "
            + "  )b ON b.patient_id = p.patient_id "
            + "  WHERE p.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.encounter_type = ${81} "
            + "    AND e.location_id = :location "
            + "     AND o.concept_id = ${165217} "
            + "     AND o.value_numeric >= 1 "
            + "     AND e.encounter_datetime BETWEEN b.previous_date AND DATE_ADD(b.previous_date, INTERVAL 33 DAY) "
            + "    GROUP BY p.patient_id "
            + "  ) last WHERE last.pickups =2";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);
    return sb.replace(query);
  }
}
