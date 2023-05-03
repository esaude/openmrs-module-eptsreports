package org.openmrs.module.eptsreports.reporting.library.queries;

public class TPTEligiblePatientsQueries {

  /**
   * 1: Select Encounter Datetime From Ficha Clinica - Master Card (encounter type 6) with “Outras
   * prescricoes” (concept id 1719) with value coded equal to “DT-3HP” (concept id 165307) and
   * encounter datetime <= End date;
   *
   * @return {@link String}
   */
  public static String getMpart1() {

    return " SELECT p.patient_id, "
        + "               e.encounter_datetime AS encounter_datetime "
        + "        FROM   patient p "
        + "               inner join encounter e "
        + "                       ON e.patient_id = p.patient_id "
        + "               inner join obs o "
        + "                       ON o.encounter_id = e.encounter_id "
        + "        WHERE  p.voided = 0 "
        + "               AND e.voided = 0 "
        + "               AND o.voided = 0 "
        + "               AND e.location_id = :location "
        + "               AND e.encounter_type = ${6} "
        + "               AND o.concept_id = ${1719} "
        + "               AND o.value_coded = ${165307} "
        + "               AND e.encounter_datetime <= :endDate ";
  }

  /**
   * 2: Select Encounter Datetime from FILT (encounter type 60) with “Regime de TPT” (concept id
   * 23985) value coded “3HP” or ” 3HP+Piridoxina” (concept id in [23954, 23984]) and encounter
   * datetime <= End date
   *
   * @return {@link String}
   */
  public static String getMpart2() {

    return " SELECT p.patient_id, e.encounter_datetime AS encounter_datetime "
        + "                   FROM   patient p    "
        + "                          inner join encounter e   "
        + "                                  ON e.patient_id = p.patient_id   "
        + "                          inner join obs o "
        + "                                  ON o.encounter_id = e.encounter_id   "
        + "                   WHERE  p.voided = 0 "
        + "                          AND e.voided = 0 "
        + "                          AND o.voided = 0 "
        + "                          AND e.location_id = :location  "
        + "                          AND e.encounter_type = ${60}    "
        + "                          AND o.concept_id = ${23985} "
        + "                          AND o.value_coded IN ( ${23954}, ${23984} )    "
        + "                          AND e.encounter_datetime <= :endDate ";
  }

  /**
   * 3: Select obs datetime(obs datetime, concept id 165308) of Última profilaxia(concept id 23985)
   * value coded 3HP (concept id 23954) and Data Início (obs datetime, concept id 165308, value
   * 1256) registered by reporting end date on Ficha Resumo (Encounter type 53) ; or
   *
   * @return {@link String}
   */
  public static String getMpart3() {

    return " SELECT p.patient_id, o2.obs_datetime AS encounter_datetime "
        + "FROM   patient p "
        + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
        + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
        + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
        + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0"
        + "       AND e.location_id = :location "
        + "       AND e.encounter_type = ${53} "
        + "       AND (( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
        + "       AND ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} AND o2.obs_datetime <= :endDate )) ";
  }

  /**
   * 4: Select obs datetime of Profilaxia TPT (concept id 23985) value coded 3HP (concept id 23954)
   * and Estado da Profilaxia (concept id 165308) value coded Início (concept id 1256) registered by
   * reporting end date on Ficha Clinica (Encounter type 6) ; or
   *
   * @return String
   */
  public static String getMpart4() {

    return " SELECT p.patient_id, e.encounter_datetime AS encounter_datetime "
        + "FROM   patient p "
        + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
        + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
        + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
        + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
        + "       AND e.location_id = :location "
        + "       AND e.encounter_type = ${6}"
        + "       AND ((o.concept_id = ${23985} AND o.value_coded = ${23954}) "
        + "       AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
        + "       AND o2.obs_datetime <= :endDate)) ";
  }

  /**
   * 5: Select encounter datetime from Ficha Clinica - Master Card (encounter type 6) with “Outras
   * prescricoes” (concept id 1719) with value coded equal to “DT-3HP” (concept id 165307) and
   * encounter <= end date and
   * <li>no other 3HP prescriptions [“Outras prescricoes” (concept id 1719) with value coded equal
   *     to “DT-3HP” (concept id 165307)] marked on Ficha-Clínica in the 4 months prior to the 3HP
   *     Start Date and
   * <li>no “Regime de TPT” (concept id 23985) with value coded “3HP” or ” 3HP+Piridoxina” (concept
   *     id in [23954, 23984]) marked on FILT (encounter type 60) in the 4 months prior to the 3HP
   *     Start Date;
   *
   * @return String
   */
  public static String getMpart5() {

    return " SELECT p.patient_id, pickup.first_pickup_date AS encounter_datetime "
        + "                 FROM  patient p "
        + "                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
        + "                  INNER JOIN obs o ON o.encounter_id = e.encounter_id "
        + "                  INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) as first_pickup_date "
        + "                      FROM    patient p "
        + "                      INNER JOIN encounter e ON e.patient_id = p.patient_id "
        + "                      INNER JOIN obs o ON o.encounter_id = e.encounter_id "
        + "                      WHERE   p.voided = 0 "
        + "                          AND e.voided = 0 "
        + "                          AND o.voided = 0 "
        + "                          AND e.location_id = :location "
        + "                          AND e.encounter_type = ${6} "
        + "                          AND o.concept_id = ${1719} "
        + "                          AND o.value_coded = ${165307} "
        + "                          AND e.encounter_datetime <= :endDate "
        + "                      GROUP BY p.patient_id) AS pickup "
        + "              ON pickup.patient_id = p.patient_id "
        + "                 WHERE p.patient_id NOT IN ( SELECT pp.patient_id "
        + "                      FROM patient pp "
        + "                            INNER JOIN encounter ee ON ee.patient_id = pp.patient_id "
        + "                            INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id "
        + "                      WHERE pp.voided = 0 "
        + "                            AND p.patient_id = pp.patient_id "
        + "                           AND ee.voided = 0 "
        + "                           AND oo.voided = 0 "
        + "                           AND ee.location_id = :location "
        + "                           AND ee.encounter_type = ${6} "
        + "                           AND oo.concept_id = ${1719} "
        + "                           AND oo.value_coded = ${165307} "
        + "                           AND ee.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 4 MONTH) "
        + "                           AND ee.encounter_datetime < pickup.first_pickup_date "
        + "                 UNION "
        + "                    SELECT pp.patient_id "
        + "                          FROM patient pp "
        + "                                INNER JOIN encounter ee ON ee.patient_id = pp.patient_id "
        + "                                INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id "
        + "                          WHERE pp.voided = 0 "
        + "                                AND p.patient_id = pp.patient_id "
        + "                               AND ee.voided = 0 "
        + "                               AND oo.voided = 0 "
        + "                               AND ee.location_id = :location "
        + "                               AND ee.encounter_type = ${60} "
        + "                               AND oo.concept_id = ${23985} "
        + "                               AND oo.value_coded IN (${23954},${23984}) "
        + "                               AND oo.obs_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 4 MONTH) "
        + "                               AND oo.obs_datetime < pickup.first_pickup_date )";
  }

  /**
   *
   * <li>
   *
   *     <p>6: Select all patients with Outras prescricoes(concept id 1719) value coded DT-3HP
   *     (concept id 165307) on Ficha clinica (encounter type 6) by report end date;
   *
   * @return {@link String}
   */
  public static String getMpart6() {

    return "SELECT p.patient_id, e.encounter_datetime AS encounter_datetime "
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
        + "       AND e.encounter_datetime <= :endDate ";
  }

  /**
   *
   * <li>7: Select encounter datetime as FILT 3HP start date from “Regime de TPT” (concept id 23985)
   *     with value coded “3HP” or ” 3HP+Piridoxina” (concept id in [23954, 23984]) and “Seguimento
   *     de tratamento TPT”(concept ID 23987) value coded “inicio” or “re-inicio”(concept ID in
   *     [1256, 1705]) marked on FILT (encounter type 60) and encounter datetime <= end date;
   *
   * @return {@link String}
   */
  public static String getMpart7() {

    return "SELECT p.patient_id, e.encounter_datetime AS encounter_datetime "
        + "FROM   patient p "
        + "       inner join encounter e ON p.patient_id = e.patient_id "
        + "       inner join obs o ON e.encounter_id = o.encounter_id "
        + "       inner join obs o2 ON e.encounter_id = o2.encounter_id "
        + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
        + "       AND e.location_id = :location "
        + "       AND e.encounter_type = ${60} "
        + "       AND ( ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) "
        + "       AND o.obs_datetime <= :endDate ) "
        + "       AND ( o2.concept_id=  ${23987} AND o2.value_coded IN ( ${1256} , ${1705} ) ) ) ";
  }

  /**
   *
   *
   * <blockquote>
   *
   * <p>Select all patients with “Regime de TPT” (concept id 23985) with value coded “3HP” or ”
   * 3HP+Piridoxina” (concept id in [23954, 23984]) and “Seguimento de tratamento TPT”(concept ID
   * 23987) value coded “continua” or “fim” or no value(concept ID in [1257, 1267, null]) marked on
   * the first FILT (encounter type 60) and encounter datetime between start date and end date and:
   *
   * </blockquote>
   *
   * <li>
   *
   *     <p>No other Regime de TPT (concept id 23985) value coded “3HP” or ” 3HP+Piridoxina”
   *     (concept id in [23954, 23984]) marked on FILT (encounter type 60) in the 4 months prior to
   *     the FILT 3HP start date. ; and
   * <li>
   *
   *     <p>No other 3HP start dates marked on Ficha clinica (encounter type 6, encounter datetime)
   *     with Profilaxia TPT (concept id 23985) value coded 3HP (concept id 23954) and Estado da
   *     Profilaxia (concept id 165308) value coded Início (concept id 1256) or Outras prescrições
   *     (concept id 1719) value coded DT-3HP (concept id 165307)in the 4 months prior to the FILT
   *     3HP start date. ; and
   * <li>
   *
   *     <p>No other 3HP start dates marked on Ficha Resumo (encounter type 53) with Última
   *     profilaxia(concept id 23985) value coded 3HP(concept id 23954) and Data Início (obs
   *     datetime, concept id 165308, value 1256) in the 4 months prior to the FILT 3HP start date.
   *     ;
   *
   * @return String
   */
  public static String getMpart8() {

    return "  SELECT p.patient_id, o2.obs_datetime AS encounter_datetime "
        + "                   FROM   patient p "
        + "                          INNER JOIN encounter e ON p.patient_id = e.patient_id "
        + "                          INNER JOIN obs o ON e.encounter_id = o.encounter_id "
        + "                          INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
        + "                   WHERE  p.voided = 0 "
        + "                          AND e.voided = 0 "
        + "                          AND o.voided = 0 "
        + "                          AND o2.voided = 0 "
        + "                          AND e.encounter_type = ${60} "
        + "                          AND e.location_id = :location "
        + "                          AND  ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
        + "                          AND ( o2.concept_id = ${23987}  AND ( o2.value_coded IN ( ${1257}, ${1267} OR o2.value_coded IS NULL ) AND o2.obs_datetime <   :endDate  )) "
        + "       AND NOT EXISTS (SELECT ee.encounter_id  "
        + "                       FROM  encounter ee "
        + "                              INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
        + "                       WHERE  p.patient_id = ee.patient_id "
        + "                              AND ee.voided = 0 "
        + "                              AND oo.voided = 0 "
        + "                              AND ee.location_id = :location "
        + "                              AND ee.encounter_type = ${60} "
        + "                              AND oo.concept_id = ${23985} "
        + "                              AND oo.value_coded IN ( ${23954}, ${23984} ) "
        + "                              AND ee.encounter_datetime >= Date_sub(o2.obs_datetime,INTERVAL 4 month) AND ee.encounter_datetime < o2.obs_datetime) "
        + "       AND NOT EXISTS (SELECT ee.encounter_id "
        + "                       FROM   encounter ee "
        + "                              INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
        + "                              INNER JOIN obs oo2 ON ee.encounter_id = oo2.encounter_id "
        + "                              INNER JOIN obs oo3 ON ee.encounter_id = oo3.encounter_id "
        + "                       WHERE  p.patient_id = ee.patient_id "
        + "                              AND ee.voided = 0 "
        + "                              AND oo.voided = 0 "
        + "                              AND oo2.voided = 0 "
        + "                              AND oo3.voided = 0 "
        + "                              AND ee.location_id = :location "
        + "                              AND ee.encounter_type = ${6} "
        + "                              AND( "
        + "                                (( oo.concept_id = ${23985} AND oo.value_coded = ${23954} ) "
        + "                                      AND ( oo2.concept_id = ${165308} AND oo2.value_coded = ${1256} "
        + "                                    AND oo2.obs_datetime >= Date_sub(o2.obs_datetime, INTERVAL 4 month) "
        + "                                    AND oo2.obs_datetime < o2.obs_datetime  ) ) "
        + "                               OR ( oo3.concept_id = ${1719}  AND oo3.value_coded = ${165307} ))) "
        + "       AND NOT EXISTS (SELECT ee.encounter_id "
        + "                       FROM   encounter ee "
        + "                              INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
        + "                              INNER JOIN obs oo2 ON ee.encounter_id = oo2.encounter_id "
        + "                       WHERE  ee.patient_id = p.patient_id "
        + "                              AND ee.voided = 0 "
        + "                              AND oo.voided = 0 "
        + "                              AND oo2.voided = 0 "
        + "                              AND ee.location_id = :location "
        + "                              AND ee.encounter_type = ${53} "
        + "                              AND ( oo.concept_id = ${23985} AND o.value_coded = ${23954} ) "
        + "                              AND ( oo2.concept_id = ${165308} AND oo2.value_coded = ${1256} "
        + "                                        AND oo2.obs_datetime >= Date_sub(o2.obs_datetime,  INTERVAL 4 month)"
        + "                                        AND oo2.obs_datetime < o2.obs_datetime "
        + "                                       )"
        + "                        ) ";
  }

  /**
   * <b>K. Patients IPT Start Dates as:</b>
   *
   * <p>Y1: Select “Ultima Profilaxia TPT (concept id 23985) with value INH (concept id 656) and
   * (Data Inicio) ESTADO_DA_PROFILAXIA (concept_id=165308) with Value_coded = Iniciar (id= 1256)
   * from Ficha Resumo (encounter type 53) and INH Start Date <= endDate (INH Start Date =
   * ObsDateTime for concept id = 165308).
   * <li>Note: if there is more than one Ficha Resumo then information from all Ficha Resumo should
   *     be included.
   *
   * @return {@link String}
   */
  public static String getY1Query() {
    return " SELECT ee.patient_id, oo2.obs_datetime start_date "
        + "FROM   encounter ee "
        + "       JOIN obs oo "
        + "         ON oo.encounter_id = ee.encounter_id "
        + "       JOIN obs oo2 "
        + "         ON oo2.encounter_id = ee.encounter_id "
        + "WHERE  ee.encounter_type = ${53} "
        + "       AND (( oo.concept_id = ${23985} "
        + "           AND oo.value_coded = ${656} ) "
        + "       AND ( oo2.concept_id = ${165308} "
        + "           AND oo2.value_coded = ${1256} "
        + "           AND oo2.obs_datetime <= :endDate )) "
        + "       AND oo.voided = 0 "
        + "       AND oo2.voided = 0 "
        + "       AND ee.voided = 0 "
        + "       AND ee.location_id = :location ";
  }

  /**
   * @see #getY1Query()
   * @return {@link String}
   */
  public static String getY1QueryWithPatientIdForB5() {
    return "SELECT ee.patient_id, o3.obs_datetime AS start_date "
        + "FROM   encounter ee "
        + "       JOIN obs o2 "
        + "         ON o2.encounter_id = ee.encounter_id "
        + "       JOIN obs o3 "
        + "         ON o3.encounter_id = ee.encounter_id "
        + "WHERE  ee.encounter_type = ${53} "
        + "       AND o2.voided = 0 "
        + "       AND o3.voided = 0 "
        + "       AND ee.voided = 0 "
        + "       AND ee.location_id = :location "
        + "       AND ((o2.concept_id = ${23985} "
        + "        AND o2.value_coded = ${656}) "
        + "       AND (o3.concept_id = ${165308} "
        + "        AND o3.value_coded = ${1256} "
        + "        AND o3.obs_datetime <= :endDate)) ";
  }

  /**
   * <b>K. Patients IPT Start Dates as:</b>
   *
   * <p>Y2: Select Encounter Datetime from Ficha clinica (encounter type 6) with Profilaxia TPT
   * (concept id 23985) value coded INH (concept id 656) and Estado da Profilaxia (concept id
   * 165308) value coded Início (concept id 1256) and encounter datetime <= end date. or
   *
   * @return {@link String}
   */
  public static String getY2Query() {
    return "SELECT ee.patient_id, o3.obs_datetime start_date "
        + "FROM encounter ee "
        + "    JOIN obs o2 ON o2.encounter_id = ee.encounter_id "
        + "    JOIN obs o3 ON o3.encounter_id = ee.encounter_id "
        + "WHERE "
        + "        ee.encounter_type = ${6} "
        + "  AND o2.voided = 0 "
        + "  AND o3.voided = 0 "
        + "  AND ee.voided = 0 "
        + "  AND ee.location_id = :location "
        + "  AND (( o2.concept_id = ${23985} "
        + "    AND o2.value_coded = ${656} ) "
        + "  AND ( o3.concept_id = ${165308} "
        + "    AND o3.value_coded = ${1256} "
        + "    AND o3.obs_datetime <= :endDate )) ";
  }

  /**
   * @see #getY2Query()
   * @return {@link String}
   */
  public static String getY2QueryWithPatientIdForB5() {
    return "SELECT "
        + "    ee.patient_id, o3.obs_datetime AS start_date "
        + "FROM "
        + "    encounter ee "
        + "        JOIN "
        + "    obs o2 ON o2.encounter_id = ee.encounter_id "
        + "    JOIN obs o3 ON o3.encounter_id = ee.encounter_id "
        + "WHERE "
        + "        ee.encounter_type = ${6} "
        + "  AND o2.voided = 0 "
        + "  AND o3.voided = 0 "
        + "  AND ee.voided = 0 "
        + "  AND ee.location_id = :location "
        + "  AND (( o2.concept_id = ${23985} "
        + "    AND o2.value_coded = ${656} ) "
        + "  AND ( o3.concept_id = ${165308} "
        + "    AND o3.value_coded = ${1256} "
        + "    AND o3.obs_datetime <= :endDate )) ";
  }

  /**
   * <b>K. Patients IPT Start Dates as:</b>
   *
   * <p>Y3: Select obs datetime from “Profilaxia TPT (concept id 23985) with the value “Isoniazida
   * (INH) (concept id 656) Data Início ESTADO_DA_PROFILAXIA (concept_id=165308) with Value_coded =
   * Iniciar (id= 1256) marked in Ficha de Seguimento (Adulto e Pediatria) (encounter type 6,9) by
   * reporting end date.
   *
   * @return {@link String}
   */
  public static String getY3Query() {
    return "SELECT ee.patient_id, oo2.obs_datetime start_date "
        + "FROM   encounter ee "
        + "           JOIN obs oo ON oo.encounter_id = ee.encounter_id "
        + "           JOIN obs oo2 ON oo2.encounter_id = ee.encounter_id "
        + "WHERE  ee.encounter_type = ${9} "
        + "    AND (( oo.concept_id = ${23985} "
        + "         AND oo.value_coded = ${656} ) "
        + "    AND ( oo2.concept_id = ${165308} "
        + "         AND oo2.value_coded = ${1256} "
        + "         AND oo2.obs_datetime <= :endDate )) "
        + "    AND oo.voided = 0 "
        + "    AND ee.voided = 0 "
        + "    AND ee.location_id = :location ";
  }

  /**
   * @see #getY3Query()
   * @return {@link String}
   */
  public static String getY3QueryWithPatientIdForB5() {
    return "SELECT ee.patient_id, oo2.obs_datetime AS start_date "
        + "FROM   encounter ee "
        + "           JOIN obs oo "
        + "                ON oo.encounter_id = ee.encounter_id "
        + "           JOIN obs oo2 "
        + "                ON oo2.encounter_id = ee.encounter_id "
        + "WHERE  ee.encounter_type = ${9} "
        + "    AND oo.voided = 0 "
        + "    AND oo2.voided = 0 "
        + "    AND ee.voided = 0 "
        + "    AND ee.location_id = :location "
        + "    AND ((oo.concept_id = ${23985} "
        + "    AND oo.value_coded = ${656}) "
        + "    AND (oo2.concept_id = ${165308} "
        + "    AND oo2.value_coded = ${1256} "
        + "    AND oo2.obs_datetime <= :endDate)) ";
  }

  /**
   * <b>K. Patients IPT Start Dates as:</b>
   *
   * <p>Y4: Select Encounter Datetime from FILT (encounter type 60) with “Regime de TPT” (concept id
   * 23985) value coded ‘Isoniazid’ or ‘Isoniazid + piridoxina’ (concept id in [656, 23982]) and
   * encounter datetime <= end date.
   *
   * @return {@link String}
   */
  public static String getY4Query() {
    return "SELECT ee.patient_id, ee.encounter_datetime start_date "
        + "FROM   encounter ee "
        + "       INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
        + "WHERE  ee.voided = 0 "
        + "       AND oo.voided = 0 "
        + "       AND ee.encounter_type = ${60} "
        + "       AND oo.concept_id = ${23985} "
        + "       AND oo.value_coded IN ( ${656}, ${23982} ) "
        + "       AND ee.location_id = :location "
        + "       AND ee.encounter_datetime <= :endDate ";
  }

  /**
   * @see #getY4Query()
   * @return {@link String}
   */
  public static String getY4QueryWithPatientIdForB5() {
    return "SELECT ee.patient_id, ee.encounter_datetime AS start_date "
        + "FROM   encounter ee "
        + "       INNER JOIN obs oo "
        + "               ON ee.encounter_id = oo.encounter_id "
        + "WHERE  ee.voided = 0 "
        + "       AND oo.voided = 0 "
        + "       AND ee.encounter_type = ${60} "
        + "       AND oo.concept_id = ${23985} "
        + "       AND oo.value_coded IN ( ${656}, ${23982} ) "
        + "       AND ee.location_id = :location "
        + "       AND ee.encounter_datetime <= :endDate ";
  }

  /**
   * <b>K. Patients IPT Start Dates as:</b>
   *
   * <p>Y5: Select obs datetime for “Seguimento de tratamento TPT”(concept ID 23987) with “Regime de
   * TPT” (concept id 23985) value coded ‘Isoniazid’ or ‘Isoniazid + piridoxina’ (concept id in
   * [656, 23982]) and “Seguimento de tratamento TPT”(concept ID 23987) value coded “inicio” or
   * “re-inicio”(concept ID in [1256, 1705]) marked on FILT (encounter type 60) and obs datetime by
   * reporting end date.
   *
   * @return {@link String}
   */
  public static String getY5Query() {
    return "SELECT   ee.patient_id,  o2.obs_datetime start_date "
        + "FROM       encounter ee "
        + "INNER JOIN obs oo "
        + "ON         ee.encounter_id = oo.encounter_id "
        + "INNER JOIN obs o2 "
        + "ON         ee.encounter_id = o2.encounter_id "
        + "WHERE      ee.voided = 0 "
        + "AND        oo.voided = 0 "
        + "AND        o2.voided = 0 "
        + "AND        ee.encounter_type = ${60} "
        + "AND        ee.location_id = :location "
        + "AND        ( ( oo.concept_id = ${23985} "
        + "AND            oo.value_coded IN ( ${656}, ${23982} ) ) "
        + "AND          ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1256}, ${1705} ) "
        + "AND            o2.obs_datetime <= :endDate ) )";
  }

  /**
   * @see #getY5Query()
   * @return {@link String}
   */
  public static String getY5QueryWithPatientIdForB5() {
    return "SELECT ee.patient_id, o2.obs_datetime AS start_date "
        + "FROM       encounter ee "
        + "INNER JOIN obs oo "
        + "ON         ee.encounter_id = oo.encounter_id "
        + "INNER JOIN obs o2 "
        + "ON         ee.encounter_id = o2.encounter_id "
        + "WHERE      ee.voided = 0 "
        + "AND        oo.voided = 0 "
        + "AND        o2.voided = 0 "
        + "AND        ee.encounter_type = ${60} "
        + "AND        oo.concept_id = ${23985} "
        + "AND        oo.value_coded IN ( ${656}, "
        + "                              ${23982} ) "
        + "AND        o2.concept_id = ${23987} "
        + "AND        o2.value_coded IN ( ${1256}, "
        + "                              ${1705} ) "
        + "AND        ee.location_id = :location "
        + "AND        o2.obs_datetime <= :endDate";
  }

  /**
   * <b>K. Patients IPT Start Dates as:</b>
   *
   * <p>Y6: Select obs datetime from FILT (encounter type 60) with “Regime de TPT” (concept id
   * 23985) value coded ‘Isoniazid’ or ‘Isoniazid + piridoxina’ (concept id in [656, 23982]) and
   * “Seguimento de Tratamento TPT” (concept ID 23987) with values “Continua” (concept ID 1257) or
   * no value(null) and encounter datetime (as INH Start Date) between start date and end date and:
   * <li>No other INH values [Regime de TPT” (concept id 23985) value coded ‘Isoniazid’ or
   *     ‘Isoniazid + piridoxina’ (concept id in [656, 23982])] marked on FILT in the 7 months prior
   *     to this FILT Start Date and
   * <li>No Profilaxia TPT with the value “Isoniazida (INH)” (Concept ID 23985, value_coded 656) and
   *     Estado da Profilaxia (concept id 165308) value coded Início (concept id 1256) registered in
   *     Ficha Clinica (Encounter Type ID 6) in the 7 months prior to this FILT Start Date and
   * <li>No Profilaxia TPT with the value “Isoniazida (INH)” (Concept ID 23985, value_coded 656) and
   *     Data Inicio (ESTADO_DA_PROFILAXIA (concept_id=165308) with Value_coded = Iniciar (id= 1256)
   *     ) marked on Ficha de Seguimento (Encounter Type ID IN 6, 9) in the 7 months prior to ‘INH
   *     Start Date’; and
   * <li>No Última Profilaxia TPT with value “Isoniazida (INH)” (Concept ID 23985, value_coded 656)
   *     and Data Início (ESTADO_DA_PROFILAXIA (concept_id=165308) with Value_coded = Iniciar (id=
   *     1256) ) marked in Ficha Resumo (Encounter Type ID 53) selected in the 7 months prior to
   *     this FILT Start Date
   *
   * @return {@link String}
   */
  public static String getY6Query() {
    return "SELECT p.patient_id, o2.obs_datetime AS start_date "
        + "                   FROM   patient p "
        + "                          INNER JOIN encounter e ON p.patient_id = e.patient_id "
        + "                          INNER JOIN obs o ON e.encounter_id = o.encounter_id "
        + "                          INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
        + "                   WHERE  p.voided = 0 "
        + "                          AND e.voided = 0 "
        + "                          AND o.voided = 0 "
        + "                          AND o2.voided = 0 "
        + "                          AND e.encounter_type = ${60} "
        + "                          AND e.location_id = :location "
        + "                      AND (o.concept_id = ${23985} AND o.value_coded IN (${656}, ${23982})) "
        + "                      AND (o2.concept_id = ${23987} AND ( o2.value_coded = ${1257} OR o2.value_coded IS NULL )  "
        + "                        AND o2.obs_datetime <= :endDate )"
        + "       AND NOT EXISTS ( SELECT ee.encounter_id  "
        + "                 FROM encounter ee "
        + "                 INNER JOIN obs oo ON oo.encounter_id =  ee.encounter_id "
        + "                       WHERE  ee.patient_id = p.patient_id "
        + "                              AND ee.voided = 0 "
        + "                              AND oo.voided = 0 "
        + "                              AND ee.location_id = :location "
        + "                              AND ee.encounter_type = ${60} "
        + "                              AND oo.concept_id = ${23985} "
        + "                              AND oo.value_coded IN ( ${656}, ${23982} ) "
        + "                              AND ee.encounter_datetime >= DATE_SUB(o2.obs_datetime, INTERVAL 7 month) "
        + "                              AND ee.encounter_datetime < o2.obs_datetime) "
        + "       AND NOT EXISTS (SELECT ee.encounter_id "
        + "                       FROM   encounter ee "
        + "                              INNER JOIN obs oo  ON ee.encounter_id = oo.encounter_id "
        + "                              INNER JOIN obs oo2 ON ee.encounter_id = oo2.encounter_id "
        + "                       WHERE   ee.voided = 0 "
        + "                              AND oo.voided = 0 "
        + "                              AND ee.patient_id = p.patient_id "
        + "                              AND oo2.voided = 0 "
        + "                              AND ee.location_id = :location "
        + "                              AND ee.encounter_type IN ( ${6}, ${9} ) "
        + "                              AND ( ( ( oo.concept_id = ${23985} AND oo.value_coded = ${656} ) "
        + "                                      AND ( oo2.concept_id = ${165308} AND oo2.value_coded = ${1256} "
        + "                                            AND oo2.obs_datetime >= Date_sub(o2.obs_datetime,INTERVAL 7 month) AND oo2.obs_datetime < o2.obs_datetime ) ) "
        + "                                      )) "
        + "       AND NOT EXISTS (SELECT ee.encounter_id "
        + "                       FROM   encounter ee "
        + "                              INNER JOIN obs oo "
        + "                                      ON ee.encounter_id = oo.encounter_id "
        + "                              INNER JOIN obs oo2 "
        + "                                      ON ee.encounter_id = oo2.encounter_id "
        + "                       WHERE  ee.voided = 0 "
        + "                              AND ee.patient_id  = p.patient_id "
        + "                              AND oo.voided = 0 "
        + "                              AND oo2.voided = 0 "
        + "                              AND ee.location_id = :location "
        + "                              AND ee.encounter_type = ${53} "
        + "                              AND ( ( oo.concept_id = ${23985} "
        + "                                      AND oo.value_coded = ${656} ) "
        + "                                    AND ( oo2.concept_id = ${165308} "
        + "                                          AND oo2.value_coded = ${1256} "
        + "                                          AND oo2.obs_datetime >= Date_sub(o2.obs_datetime, INTERVAL 7 month) AND oo2.obs_datetime < o2.obs_datetime ) "
        + "                                  ))";
  }

  /**
   * @see #getY6Query()
   * @return {@link String}
   */
  public static String getY6QueryWithPatientIdForB5() {
    return "SELECT filt.patient_id, filt.start_date AS start_date "
        + "FROM   patient pp "
        + "       INNER JOIN encounter e "
        + "               ON pp.patient_id = e.patient_id "
        + "       INNER JOIN obs o "
        + "               ON e.encounter_id = o.encounter_id "
        + "       INNER JOIN (SELECT p.patient_id, "
        + "                          Min(o2.obs_datetime) AS start_date "
        + "                   FROM   patient p "
        + "                          INNER JOIN encounter e "
        + "                                  ON p.patient_id = e.patient_id "
        + "                          INNER JOIN obs o "
        + "                                  ON e.encounter_id = o.encounter_id "
        + "                          INNER JOIN obs o2 "
        + "                                  ON e.encounter_id = o2.encounter_id "
        + "                   WHERE  p.voided = 0 "
        + "                          AND e.voided = 0 "
        + "                          AND o.voided = 0 "
        + "                          AND o2.voided = 0 "
        + "                          AND e.encounter_type = ${60} "
        + "                          AND e.location_id = :location "
        + "                      AND ( "
        + "                                    (o.concept_id = ${23985} AND o.value_coded IN (${656}, ${23982})) "
        + "                                    AND ( "
        + "                                            (o2.concept_id = ${23987} AND o2.value_coded = ${1257}) "
        + "                                            OR NOT (o2.concept_id = ${23987} AND o2.value_coded IS NOT NULL) "
        + "                                        ) "
        + "                                    AND o2.obs_datetime <= :endDate "
        + "                                )"
        + "                   GROUP  BY p.patient_id) AS filt "
        + "               ON pp.patient_id = filt.patient_id "
        + "WHERE pp.patient_id = filt.patient_id "
        + "      AND pp.voided = 0 "
        + "      AND e.voided = 0 "
        + "      AND o.voided = 0 "
        + "      AND NOT EXISTS (SELECT p.patient_id "
        + "                       FROM   patient p "
        + "                              INNER JOIN encounter e "
        + "                                      ON p.patient_id = e.patient_id "
        + "                              INNER JOIN obs o "
        + "                                      ON e.encounter_id = o.encounter_id "
        + "                       WHERE  p.voided = 0 "
        + "                              AND e.voided = 0 "
        + "                              AND o.voided = 0 "
        + "                              AND e.location_id = :location "
        + "                              AND e.encounter_type = ${60} "
        + "                              AND o.concept_id = ${23985} "
        + "                              AND o.value_coded IN ( ${656}, ${23982} ) "
        + "                              AND e.encounter_datetime <= "
        + "                                  Date_sub(filt.start_date, "
        + "                                  INTERVAL 7 month)) "
        + "       AND NOT EXISTS (SELECT p.patient_id "
        + "                       FROM   patient p "
        + "                              INNER JOIN encounter e "
        + "                                      ON e.patient_id = p.patient_id "
        + "                              INNER JOIN obs o "
        + "                                      ON e.encounter_id = o.encounter_id "
        + "                              INNER JOIN obs o2 "
        + "                                      ON e.encounter_id = o2.encounter_id "
        + "                              INNER JOIN obs o3 "
        + "                                      ON e.encounter_id = o3.encounter_id "
        + "                       WHERE  p.voided = 0 "
        + "                              AND e.voided = 0 "
        + "                              AND o.voided = 0 "
        + "                              AND o2.voided = 0 "
        + "                              AND o3.voided = 0 "
        + "                              AND e.location_id = :location "
        + "                              AND e.encounter_type IN ( ${6}, ${9} ) "
        + "                              AND ( ( ( o.concept_id = ${23985} "
        + "                                        AND o.value_coded = ${656} ) "
        + "                                      AND ( o2.concept_id = ${165308} "
        + "                                            AND o2.value_coded = ${1256} "
        + "                                            AND o2.obs_datetime <= "
        + "                                                Date_sub(filt.start_date, "
        + "                                                INTERVAL "
        + "                                                7 month) "
        + "                                          ) ) "
        + "                                     OR ( o3.concept_id = ${1719} "
        + "                                          AND o3.value_coded = ${165307} ) )) "
        + "       AND NOT EXISTS (SELECT p.patient_id "
        + "                       FROM   patient p "
        + "                              INNER JOIN encounter e "
        + "                                      ON e.patient_id = p.patient_id "
        + "                              INNER JOIN obs o "
        + "                                      ON e.encounter_id = o.encounter_id "
        + "                              INNER JOIN obs o2 "
        + "                                      ON e.encounter_id = o2.encounter_id "
        + "                       WHERE  p.voided = 0 "
        + "                              AND e.voided = 0 "
        + "                              AND o.voided = 0 "
        + "                              AND o2.voided = 0 "
        + "                              AND e.location_id = :location "
        + "                              AND e.encounter_type = ${53} "
        + "                              AND ( ( o.concept_id = ${23985} "
        + "                                      AND o.value_coded = ${656} ) "
        + "                                    AND ( o2.concept_id = ${165308} "
        + "                                          AND o2.value_coded = ${1256} "
        + "                                          AND o2.obs_datetime <= "
        + "                                              Date_sub(filt.start_date, "
        + "                                              INTERVAL 7 month) ) "
        + "                                  ))";
  }
}
