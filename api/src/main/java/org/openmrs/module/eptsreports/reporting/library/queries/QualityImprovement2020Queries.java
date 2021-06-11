package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class QualityImprovement2020Queries {

  /**
   * H - Filter all patients that returned for any clinical consultation (encounter type 6,
   * encounter_datetime) or ARV pickup (encounter type 52, concept ID 23866 value_datetime, Levantou
   * ARV (concept id 23865) = Sim (1065)) between 25 and 33 days after ART start date(Oldest date
   * From A).
   *
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getMQ12NumH(
      int lowerBound,
      int upperBound,
      int adultoSeguimentoEncounterType,
      int masterCardDrugPickupEncounterType,
      int masterCardEncounterType,
      int yesConcept,
      int historicalDrugStartDateConcept,
      int artPickupConcept,
      int artDatePickupMasterCard) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients that returned for another clinical consultation or ARV pickup between 25 and 33 days after ART start date(Oldest date From A)");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("52", masterCardDrugPickupEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("1065", yesConcept);
    map.put("1190", historicalDrugStartDateConcept);
    map.put("23865", artPickupConcept);
    map.put("23866", artDatePickupMasterCard);

    String query =
        "SELECT     inicio_real.patient_id "
            + "FROM       ("
            + "  SELECT patient_id, data_inicio "
            + "  FROM   ("
            + "  SELECT	patient_id, Min(data_inicio) data_inicio "
            + "      FROM	("
            + "          SELECT	p.patient_id, Min(value_datetime) data_inicio "
            + "                    FROM	patient p "
            + "              INNER JOIN encounter e "
            + "                  ON p.patient_id = e.patient_id "
            + "              INNER JOIN obs o "
            + "                  ON e.encounter_id = o.encounter_id "
            + "          WHERE 	p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND o.voided = 0 "
            + "              AND e.encounter_type = ${53} "
            + "              AND o.concept_id = ${1190} "
            + "              AND o.value_datetime IS NOT NULL "
            + "              AND o.value_datetime <= :endDate "
            + "              AND e.location_id = :location "
            + "          GROUP  BY p.patient_id ) inicio "
            + "      GROUP  BY patient_id) inicio1 "
            + "WHERE  data_inicio BETWEEN :startDate AND :endDate) inicio_real "
            + "INNER JOIN ("
            + "                      SELECT     e.patient_id, e.encounter_datetime AS first_visit"
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e "
            + "                      ON         e.patient_id = p.patient_id "
            + "                      WHERE      e.voided = 0 "
            + "                      AND        e.location_id = :location "
            + "                      AND        e.encounter_type = ${6}"
            + "                      AND        e.encounter_datetime <= :endDate"
            + "                      UNION "
            + "                      SELECT     e.patient_id, pickupdate.value_datetime AS first_visit"
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e "
            + "                      ON         e.patient_id = p.patient_id "
            + "                      INNER JOIN obs o "
            + "                      ON         o.encounter_id = e.encounter_id "
            + "                      INNER JOIN obs pickupdate "
            + "                      ON         e.encounter_id = pickupdate.encounter_id "
            + "                      WHERE      e.voided = 0 "
            + "                      AND        o.voided = 0 "
            + "                      AND        pickupdate.voided = 0 "
            + "                      AND        e.location_id = :location "
            + "                      AND        e.encounter_type = ${52} "
            + "                      AND        o.concept_id = ${23865} "
            + "                      AND        o.value_coded = ${1065} "
            + "                      AND        pickupdate.concept_id = ${23866} "
            + "                      AND        pickupdate.value_datetime IS NOT NULL "
            + "                      AND        pickupdate.value_datetime <= :endDate) AS first_real "
            + "ON         inicio_real.patient_id = first_real.patient_id "
            + "WHERE      first_real.first_visit >= date_add(inicio_real.data_inicio, INTERVAL "
            + lowerBound
            + " DAY) "
            + "AND        first_real.first_visit <= date_add(inicio_real.data_inicio, INTERVAL "
            + upperBound
            + " DAY) "
            + "GROUP BY   inicio_real.patient_id;";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>I - Filter all patients that returned for another clinical consultation (encounter type
   * 6)between 20 and 33 days after ART start date, another consultation between 20 and 33 days
   * after the first(previous) consultation, and another consultation between 20 and 33 days after
   * the second consultation(previous) as following:</b><br>
   *
   * <ul>
   *   <li>I1 - FIRST consultation (Encounter_datetime (from encounter type 6)) >= “ART Start Date”
   *       (oldest date from A)+20days and <= “ART Start Date” (oldest date from A)+33days (oldest
   *       date from A)+20days and <= “ART Start Date” (oldest date from A)+33days AND
   *   <li>I2 - At least one consultation (Encounter_datetime (from encounter type 6)) >= “First
   *       Consultation” (oldest date from I1)+20days and <=“First Consultation” (oldest date from
   *       I1)+33days AND
   *   <li>I3- At least one consultation (Encounter_datetime (from encounter type 6)) > “Second
   *       Consultation” (oldest date from I2)+20days and <= “Second Consultation” (oldest date from
   *       I2)+33days
   * </ul>
   *
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getMQ12NumI(
      int lowerBound,
      int upperBound,
      int adultoSeguimentoEncounterType,
      int masterCardDrugPickupEncounterType,
      int masterCardEncounterType,
      int yesConcept,
      int historicalDrugStartDateConcept,
      int artPickupConcept,
      int artDatePickupMasterCard) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients that returned for another clinical consultation or ARV pickup between 25 and 33 days after ART start date(Oldest date From A)");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("52", masterCardDrugPickupEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("1065", yesConcept);
    map.put("1190", historicalDrugStartDateConcept);
    map.put("23865", artPickupConcept);
    map.put("23866", artDatePickupMasterCard);
    map.put("lowerBound", lowerBound);
    map.put("upperBound", upperBound);

    String query =
        "  SELECT p.patient_id  "
            + "  FROM   patient p  "
            + "      INNER JOIN encounter e  "
            + "         ON e.patient_id = p.patient_id  "
            + "      INNER JOIN ( "
            + "            SELECT patient_id, ficha_resumo.data_inicio "
            + "            FROM ( "
            + "                   SELECT p.patient_id, Min(value_datetime) AS data_inicio  "
            + "                   FROM   patient p  "
            + "                       INNER JOIN encounter e  "
            + "                           ON p.patient_id = e.patient_id  "
            + "                       INNER JOIN obs o  "
            + "                           ON e.encounter_id = o.encounter_id  "
            + "                   WHERE  p.voided = 0  "
            + "                       AND e.voided = 0  "
            + "                       AND o.voided = 0  "
            + "                       AND e.encounter_type = ${53}  "
            + "                       AND o.concept_id = ${1190}  "
            + "                       AND o.value_datetime IS NOT NULL  "
            + "                       AND o.value_datetime <= :endDate  "
            + "                       AND e.location_id = :location  "
            + "                   GROUP  BY p.patient_id) AS ficha_resumo "
            + "              WHERE ficha_resumo.data_inicio BETWEEN :startDate AND :endDate "
            + "                 ) inicio    "
            + "           ON inicio.patient_id = p.patient_id                   "
            + "  WHERE p.voided = 0 "
            + "      AND e.voided = 0  "
            + "      AND e.location_id = :location  "
            + "      AND e.encounter_type = ${6}  "
            + "      AND e.encounter_datetime BETWEEN DATE_ADD(inicio.data_inicio, INTERVAL ${lowerBound} DAY) "
            + "                        AND DATE_ADD(inicio.data_inicio, INTERVAL ${upperBound} DAY) "
            + "  UNION "
            + "  SELECT p.patient_id "
            + "  FROM   patient p  "
            + "      INNER JOIN encounter e  "
            + "         ON e.patient_id = p.patient_id  "
            + "      INNER JOIN obs o1  "
            + "         ON o1.encounter_id = e.encounter_id  "
            + "      INNER JOIN obs o2  "
            + "         ON o2.encounter_id = e.encounter_id  "
            + "      INNER JOIN "
            + "                ( "
            + "                   SELECT p.patient_id, Min(value_datetime) AS data_inicio  "
            + "                   FROM   patient p  "
            + "                       INNER JOIN encounter e  "
            + "                           ON p.patient_id = e.patient_id  "
            + "                       INNER JOIN obs o  "
            + "                           ON e.encounter_id = o.encounter_id  "
            + "                   WHERE  p.voided = 0  "
            + "                       AND e.voided = 0  "
            + "                       AND o.voided = 0  "
            + "                       AND e.encounter_type = ${53}  "
            + "                       AND o.concept_id = ${1190}  "
            + "                       AND o.value_datetime IS NOT NULL  "
            + "                       AND o.value_datetime <= :endDate  "
            + "                       AND e.location_id = :location  "
            + "                   GROUP  BY p.patient_id "
            + "                 ) inicio    "
            + "           ON inicio.patient_id = p.patient_id                   "
            + "  WHERE p.voided = 0 "
            + "      AND e.voided = 0  "
            + "      AND o1.voided = 0  "
            + "      AND o2.voided = 0  "
            + "      AND e.location_id = :location  "
            + "      AND e.encounter_type = ${52}  "
            + "      AND o1.concept_id = ${23866} "
            + "      AND o1.value_datetime BETWEEN DATE_ADD(inicio.data_inicio, INTERVAL ${lowerBound} DAY) "
            + "                        AND DATE_ADD(inicio.data_inicio, INTERVAL ${upperBound} DAY) "
            + "      AND o2.concept_id = ${23865} "
            + "      AND o2.value_coded = ${1065} ";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ15DEN A </b> - Select all patients from Ficha Clinica (Encounter type 6) with at least one
   * of the following concepts between the period (startDateInclusion = endDateRevision - 14 months
   * and endDateInclusion = endDateRevision - 11 months)</b><br>
   *
   * <ul>
   *   <li>A1 - GAAC (GA) (Concept Id 23724) = “INICIAR” (value_coded = concept Id 1256) or
   *   <li>A2 - DISPENSA TRIMESTRAL (DT) (Concept Id 23730) = “INICIAR” (value_coded = concept Id
   *       1256)
   *   <li>A3 - Select all patients from Ficha Clinica (Encounter type 6) with THE LAST concept
   *       “TIPO DE DISPENSA” (Concept Id 23739) with value coded “DISPENSA TRIMESTRAL” (Concept Id
   *       23720) the period (startDateInclusion = endDateRevision - 14 months and endDateInclusion
   *       = endDateRevision - 11 months)
   * </ul>
   *
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getMQ15DenA1(
      int adultoSeguimentoEncounterType,
      int startDrugs,
      int quarterlyConcept,
      int gaac,
      int quarterlyDispensation,
      int typeOfDispensationConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients that returned for another clinical consultation or ARV pickup between 25 and 33 days after ART start date(Oldest date From A)");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("1256", startDrugs);
    map.put("23720", quarterlyConcept);
    map.put("23724", gaac);
    map.put("23730", quarterlyDispensation);
    map.put("23739", typeOfDispensationConcept);

    String query =
        "SELECT patient_id "
            + "FROM   ( "
            + "       SELECT p.patient_id, e.encounter_datetime "
            + "       FROM   patient p "
            + "              INNER JOIN encounter e "
            + "                      ON p.patient_id = e.patient_id "
            + "              INNER JOIN obs o "
            + "                      ON e.encounter_id = o.encounter_id "
            + "       WHERE  p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND o.voided = 0 "
            + "              AND e.location_id = :location "
            + "              AND e.encounter_type = ${6} "
            + "              AND ( o.concept_id = ${23724} OR o.concept_id = ${23730} ) "
            + "              AND o.value_coded = ${1256} "
            + "              AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + ") denA1";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ15DEN A1 </b> - GAAC (GA) (Concept Id 23724) = “INICIAR” (value_coded = concept Id 1256)
   * <br>
   * <b>MQ15DEN A2 </b> - DISPENSA TRIMESTRAL (DT) (Concept Id 23730) = “INICIAR” (value_coded =
   * concept Id 1256)<br>
   *
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getMQ15DenA1orA2(
      String flag,
      int adultoSeguimentoEncounterType,
      int startDrugs,
      int gaac,
      int quarterlyDispensation) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients who started GAAC)");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("1256", startDrugs);
    map.put("23724", gaac);
    map.put("23730", quarterlyDispensation);

    String middleQuery = "";

    if (flag.equals("A1")) middleQuery += "o.concept_id = ${23724}";
    if (flag.equals("A2")) middleQuery += "o.concept_id = ${23730}";

    String query =
        "SELECT p.patient_id "
            + " FROM   patient p "
            + "        INNER JOIN encounter e "
            + "                ON p.patient_id = e.patient_id "
            + "        INNER JOIN obs o "
            + "                ON e.encounter_id = o.encounter_id "
            + " WHERE  p.voided = 0 "
            + "        AND o.voided = 0 "
            + "        AND e.voided = 0 "
            + "        AND e.location_id = :location "
            + "        AND e.encounter_type = ${6} "
            + "        AND "
            + middleQuery
            + "        AND o.value_coded = ${1256} "
            + "        AND e.encounter_datetime BETWEEN Date_sub(:revisionEndDate, INTERVAL 14 month) "
            + "        AND Date_sub(:revisionEndDate, INTERVAL 11 month) "
            + " GROUP  BY p.patient_id; ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ15DEN A2 </b> - DISPENSA TRIMESTRAL (DT) (​ Concept Id 23730​ ) = “INICIAR” (​ value_coded
   * = concept Id 1256​ )<br>
   *
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getMQ15DenA3(
      int adultoSeguimentoEncounterType, int quarterlyConcept, int typeOfDispensationConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients who started GAAC)");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("23720", quarterlyConcept);
    map.put("23739", typeOfDispensationConcept);

    String query =
        "SELECT p.patient_id "
            + " FROM patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o "
            + " ON e.encounter_id = o.encounter_id "
            + " INNER JOIN "
            + "("
            + " SELECT p.patient_id,MAX(e.encounter_datetime) AS max_encounter_date "
            + " FROM patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o "
            + " ON o.encounter_id = e.encounter_id "
            + " WHERE "
            + " e.location_id = :location "
            + " AND e.encounter_type = ${6} "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + " AND o.concept_id =${23739} "
            + "GROUP BY p.patient_id "
            + " ) a1 "
            + " ON p.patient_id = a1.patient_id "
            + " wHERE "
            + " p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND a1.max_encounter_date = e.encounter_datetime"
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${6} "
            + " AND o.concept_id =${23739} "
            + " AND o.value_coded =${23720} "
            + " AND e.encounter_datetime "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ15DEN B1 </b> - Filter all patients From Ficha Clinica (​ encounter type 6​</b><br>
   *
   * <ul>
   *   <li>Gaac (GA) (​ Concept Id 23724​ ) = “FIM” (​ value_coded = concept Id 1267​ ) and
   *       encounter_datetime > encounter_datetime (from A1) and <= ​ endDateRevision or
   *   <li>DISPENSA TRIMESTRAL (DT) (​ Concept Id 23730​ ) = “FIM” (​ value_coded = concept Id 1267​
   *       ) and encounter_datetime > encounter_datetime (from A2) and <= ​ endDateRevision or
   *   <li>“TIPO DE DISPENSA” (​ Concept Id ​ 23739) with value_coded different than “DISPENSA
   *       TRIMESTRAL” (​ Concept Id ​ 23720) and encounter_datetime > encounter_datetime (from A3)
   *       and <= ​ endDateRevision
   * </ul>
   *
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getMQ15DenB1(
      int adultoSeguimentoEncounterType,
      int startDrugs,
      int completedConcept,
      int quarterlyConcept,
      int gaac,
      int quarterlyDispensation,
      int typeOfDispensationConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("M&Q Category 15 B1)");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("1256", startDrugs);
    map.put("1267", completedConcept);
    map.put("23720", quarterlyConcept);
    map.put("23724", gaac);
    map.put("23730", quarterlyDispensation);
    map.put("23739", typeOfDispensationConcept);

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT ended_gaac.patient_id "
            + "        FROM   (SELECT p.patient_id, e.encounter_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e "
            + "                               ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o "
            + "                               ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN (SELECT p.patient_id, e.encounter_datetime "
            + "                                   FROM   patient p "
            + "                                          INNER JOIN encounter e "
            + "                                                  ON p.patient_id = e.patient_id "
            + "                                          INNER JOIN obs o "
            + "                                                  ON e.encounter_id = o.encounter_id "
            + "                                   WHERE  p.voided = 0 "
            + "                                          AND e.voided = 0 "
            + "                                          AND o.voided = 0 "
            + "                                          AND e.location_id = :location "
            + "                                          AND e.encounter_type = ${6} "
            + "                                          AND o.concept_id = ${23724} "
            + "                                          AND o.value_coded = ${1256} "
            + "                                          AND e.encounter_datetime BETWEEN Date_sub(:revisionEndDate, INTERVAL 14 month) "
            + "                                          AND Date_sub(:revisionEndDate, INTERVAL 11 month) "
            + "                                   GROUP  BY p.patient_id) started_gaac "
            + "                               ON p.patient_id = started_gaac.patient_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND o.concept_id = ${23724} "
            + "                       AND o.value_coded = ${1267} "
            + "                       AND e.encounter_datetime > started_gaac.encounter_datetime "
            + "                       AND e.encounter_datetime <= :revisionEndDate "
            + "                GROUP  BY p.patient_id) ended_gaac "
            + "        UNION "
            + "        SELECT ended_dt.patient_id "
            + "        FROM   (SELECT p.patient_id, e.encounter_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e "
            + "                               ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o "
            + "                               ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN (SELECT p.patient_id, e.encounter_datetime "
            + "                                   FROM   patient p "
            + "                                          INNER JOIN encounter e "
            + "                                                  ON p.patient_id = e.patient_id "
            + "                                          INNER JOIN obs o "
            + "                                                  ON e.encounter_id = o.encounter_id "
            + "                                   WHERE  p.voided = 0 "
            + "                                          AND e.voided = 0 "
            + "                                          AND o.voided = 0 "
            + "                                          AND e.location_id = :location "
            + "                                          AND e.encounter_type = ${6} "
            + "                                          AND o.concept_id = ${23730} "
            + "                                          AND o.value_coded = ${1256} "
            + "                                          AND e.encounter_datetime BETWEEN Date_sub(:revisionEndDate, INTERVAL 14 month) "
            + "                                          AND Date_sub(:revisionEndDate, INTERVAL 11 month) "
            + "                                   GROUP  BY p.patient_id) started_dt "
            + "                               ON p.patient_id = started_dt.patient_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND o.concept_id = ${23730} "
            + "                       AND o.value_coded = ${1267} "
            + "                       AND e.encounter_datetime > started_dt.encounter_datetime "
            + "                       AND e.encounter_datetime <= :revisionEndDate "
            + "                GROUP  BY p.patient_id) ended_dt "
            + "        UNION "
            + "        SELECT not_dt.patient_id "
            + "        FROM   (SELECT p.patient_id, "
            + "                       e.encounter_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e "
            + "                               ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o "
            + "                               ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN (SELECT p.patient_id, Max(e.encounter_datetime) encounter_datetime "
            + "                                   FROM   patient p "
            + "                                          INNER JOIN encounter e "
            + "                                                  ON p.patient_id = e.patient_id "
            + "                                          INNER JOIN obs o "
            + "                                                  ON e.encounter_id = o.encounter_id "
            + "                                   WHERE  p.voided = 0 "
            + "                                          AND e.voided = 0 "
            + "                                          AND o.voided = 0 "
            + "                                          AND e.location_id = :location "
            + "                                          AND e.encounter_type = ${6} "
            + "                                          AND o.concept_id = ${23739} "
            + "                                          AND o.value_coded = ${23720} "
            + "                                          AND e.encounter_datetime BETWEEN Date_sub(:revisionEndDate, INTERVAL 14 month) "
            + "                                          AND Date_sub(:revisionEndDate, INTERVAL 11 month) "
            + "                                   GROUP  BY p.patient_id) a3 "
            + "                       ON p.patient_id = a3.patient_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND o.concept_id = ${23739} "
            + "                       AND o.value_coded <> ${23720} "
            + "                       AND e.encounter_datetime <= :revisionEndDate "
            + "                GROUP  BY p.patient_id) not_dt) b1 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ15NUM H </b></b><br>
   *
   * <ul>
   *   <li>H1 - Select all patients from Ficha Clinica (encounter type 6) with concept “PEDIDO DE
   *       INVESTIGACOES LABORATORIAIS” (Concept Id 23722) and value_coded “HIV CARGA VIRAL”
   *       (Concept id 856) and encounter_datetime (From H1) > encounter_datetime (From A, the most
   *       recent one) and during the revision period
   * </ul>
   *
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getMQ15NumH(
      Integer adultoSeguimentoEncounterType,
      Integer startDrugs,
      Integer quarterlyConcept,
      Integer gaac,
      Integer quarterlyDispensation,
      Integer typeOfDispensationConcept,
      Integer labReq,
      Integer viralLoad) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients with viral load request since last dispensation type change (Oldest date From A)");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("1256", startDrugs);
    map.put("23720", quarterlyConcept);
    map.put("23724", gaac);
    map.put("23730", quarterlyDispensation);
    map.put("23739", typeOfDispensationConcept);
    map.put("23722", labReq);
    map.put("856", viralLoad);

    String query =
        " SELECT patient_id FROM ( "
            + " SELECT pout.patient_id,eout.encounter_datetime FROM patient pout "
            + " INNER JOIN encounter eout ON pout.patient_id=eout.patient_id "
            + " INNER JOIN obs oout ON eout.encounter_id=oout.encounter_id "
            + " INNER JOIN ( "
            + " SELECT patient_id, MAX(encounter_datetime) AS encounter_datetime FROM ( "
            + " SELECT p.patient_id AS patient_id, e.encounter_datetime AS encounter_datetime "
            + " FROM   patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o "
            + " ON e.encounter_id = o.encounter_id "
            + " WHERE  p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${6} "
            + " AND o.concept_id IN(${23724}, ${23730}) "
            + " AND o.value_coded = ${1256} "
            + " AND e.encounter_datetime BETWEEN date_add(:revisionEndDate, INTERVAL -14 month) "
            + " AND date_add(:revisionEndDate, INTERVAL -11 month) "
            + " UNION "
            + " SELECT pa.patient_id AS patient_id,ee.encounter_datetime AS encounter_datetime FROM patient pa "
            + " INNER JOIN encounter ee "
            + " ON pa.patient_id=ee.patient_id "
            + " INNER JOIN obs ob "
            + " ON ee.encounter_id=ob.encounter_id "
            + " INNER JOIN( "
            + " SELECT p.patient_id, "
            + " MAX(e.encounter_datetime) AS encounter_datetime "
            + " FROM   patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o "
            + " ON e.encounter_id = o.encounter_id "
            + " WHERE  p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${6} "
            + " AND o.concept_id = ${23739} "
            + " AND e.encounter_datetime BETWEEN date_add(:revisionEndDate, INTERVAL -14 month) "
            + " AND date_add(:revisionEndDate, INTERVAL -11 month) group by p.patient_id) filt ON pa.patient_id=filt.patient_id "
            + " WHERE pa.voided = 0 "
            + " AND ee.voided = 0 "
            + " AND ob.voided = 0 "
            + " AND ob.concept_id = ${23739} "
            + " AND ob.value_coded = ${23720} "
            + " AND ee.encounter_datetime=filt.encounter_datetime) combined group by patient_id) fin "
            + " ON fin.patient_id=pout.patient_id "
            + " WHERE pout.voided= 0 "
            + " AND eout.voided = 0 "
            + " AND oout.voided = 0 "
            + " AND oout.concept_id = ${23722} "
            + " AND oout.value_coded = ${856} "
            + " AND eout.encounter_datetime <= :revisionEndDate "
            + " AND eout.encounter_datetime > fin.encounter_datetime) h1 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ15NUM H2 </b></b><br>
   *
   * <ul>
   *   <li>H2 - Select all patients with results in “Laboratorio” (encounter type 13) with concept
   *       “HIV CARGA VIRAL” (Concept Id 856) with value_numeric not null OR concept “Carga Viral
   *       Qualitative” (Concept id 1305) with value_coded not null, and encounter_datetime (From
   *       H2) > encounter_datetime (From H1, the most recent one) and during the revision period
   * </ul>
   *
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getMQ15NumH2(
      Integer adultoSeguimentoEncounterType,
      Integer startDrugs,
      Integer quarterlyConcept,
      Integer gaac,
      Integer quarterlyDispensation,
      Integer typeOfDispensationConcept,
      Integer labReq,
      Integer viralLoad,
      Integer viralLoadQualitative,
      Integer labEncounterType) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients with viral load result since last request after last dispensation type change");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("1256", startDrugs);
    map.put("23720", quarterlyConcept);
    map.put("23724", gaac);
    map.put("23730", quarterlyDispensation);
    map.put("23739", typeOfDispensationConcept);
    map.put("23722", labReq);
    map.put("856", viralLoad);
    map.put("1305", viralLoadQualitative);
    map.put("13", labEncounterType);

    String query =
        " SELECT vl.patient_id FROM ( "
            + " SELECT p.patient_id, e.encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE e.encounter_type=${13} "
            + " AND o.concept_id=${856} "
            + " AND p.voided = 0 "
            + " AND o.voided = 0 "
            + " AND e.voided = 0 "
            + " AND e.encounter_datetime <= :revisionEndDate "
            + " AND o.value_numeric IS NOT NULL "
            + " UNION "
            + " SELECT pp.patient_id, ee.encounter_datetime FROM patient pp "
            + " INNER JOIN encounter ee ON pp.patient_id=ee.patient_id "
            + " INNER JOIN obs ob ON ee.encounter_id=ob.encounter_id "
            + " WHERE ee.encounter_type=${13} "
            + " AND ob.concept_id=${1305} "
            + " AND pp.voided = 0 "
            + " AND ob.voided = 0 "
            + " AND ee.voided = 0 "
            + " AND ee.encounter_datetime <= :revisionEndDate "
            + " AND ob.value_coded IS NOT NULL ) vl "
            + " INNER JOIN ( "
            + " SELECT patient_id, MAX(encounter_datetime) AS encounter_datetime "
            + " FROM ("
            + " SELECT patient_id, encounter_datetime FROM ( "
            + " SELECT p.patient_id,e.encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " INNER JOIN ( "
            + " SELECT patient_id, MAX(encounter_datetime) AS encounter_datetime FROM ( "
            + " SELECT p.patient_id AS patient_id, e.encounter_datetime AS encounter_datetime "
            + " FROM   patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o "
            + " ON e.encounter_id = o.encounter_id "
            + " WHERE  p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${6} "
            + " AND o.concept_id IN(${23724}, ${23730}) "
            + " AND o.value_coded = ${1256} "
            + " AND e.encounter_datetime BETWEEN date_add(:revisionEndDate, INTERVAL -14 month) "
            + " AND date_add(:revisionEndDate, INTERVAL -11 month) "
            + " UNION "
            + " SELECT pa.patient_id AS patient_id,ee.encounter_datetime AS encounter_datetime FROM patient pa "
            + " INNER JOIN encounter ee "
            + " ON pa.patient_id=ee.patient_id "
            + " INNER JOIN obs ob "
            + " ON ee.encounter_id=ob.encounter_id "
            + " INNER JOIN( "
            + " SELECT p.patient_id, "
            + " MAX(e.encounter_datetime) AS encounter_datetime "
            + " FROM   patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o "
            + " ON e.encounter_id = o.encounter_id "
            + " WHERE  p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${6} "
            + " AND o.concept_id = ${23739} "
            + " AND e.encounter_datetime BETWEEN date_add(:revisionEndDate, INTERVAL -14 month) "
            + " AND date_add(:revisionEndDate, INTERVAL -11 month) group by p.patient_id) filt ON pa.patient_id=filt.patient_id "
            + " WHERE pa.voided = 0 "
            + " AND ee.voided = 0 "
            + " AND ob.voided = 0 "
            + " AND ob.concept_id = ${23739} "
            + " AND ob.value_coded = ${23720} "
            + " AND ee.encounter_datetime=filt.encounter_datetime) combined group by patient_id) fin "
            + " ON fin.patient_id=p.patient_id"
            + " WHERE p.voided= 0 "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND o.concept_id = ${23722} "
            + " AND o.value_coded = ${856} "
            + " AND e.encounter_datetime <= :revisionEndDate "
            + " AND e.encounter_datetime > fin.encounter_datetime) h1 ) h11  group by patient_id) h111 "
            + " ON vl.patient_id = h111.patient_id "
            + " WHERE vl.encounter_datetime <=:revisionEndDate AND vl.encounter_datetime > h111.encounter_datetime ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ15NUM I </b></b><br>
   *
   * <ul>
   *   <li>I - Select all patients with results in “Laboratorio” (encounter type 13) with concept
   *       “HIV CARGA VIRAL” (Concept Id 856) with value_numeric < 1000 OR concept “Carga Viral
   *       Qualitative” (Concept id 1305) with value_coded not null, and encounter_datetime(From I)
   *       > encounter_datetime(From H1, the most recent one) and during the revision period
   * </ul>
   *
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getMQ15NumI(
      Integer adultoSeguimentoEncounterType,
      Integer startDrugs,
      Integer quarterlyConcept,
      Integer gaac,
      Integer quarterlyDispensation,
      Integer typeOfDispensationConcept,
      Integer labReq,
      Integer viralLoad,
      Integer viralLoadQualitative,
      Integer labEncounterType) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients with viral load result since last request after last dispensation type change");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("1256", startDrugs);
    map.put("23720", quarterlyConcept);
    map.put("23724", gaac);
    map.put("23730", quarterlyDispensation);
    map.put("23739", typeOfDispensationConcept);
    map.put("23722", labReq);
    map.put("856", viralLoad);
    map.put("1305", viralLoadQualitative);
    map.put("13", labEncounterType);

    String query =
        " SELECT vl.patient_id FROM ( "
            + " SELECT p.patient_id, e.encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE e.encounter_type=${13} "
            + " AND o.concept_id=${856} "
            + " AND p.voided = 0 "
            + " AND o.voided = 0 "
            + " AND e.voided = 0 "
            + " AND e.encounter_datetime <= :revisionEndDate "
            + " AND o.value_numeric IS NOT NULL "
            + " AND o.value_numeric < 1000 "
            + " UNION "
            + " SELECT pp.patient_id, ee.encounter_datetime FROM patient pp "
            + " INNER JOIN encounter ee ON pp.patient_id=ee.patient_id "
            + " INNER JOIN obs ob ON ee.encounter_id=ob.encounter_id "
            + " WHERE ee.encounter_type=${13} "
            + " AND ob.concept_id=${1305} "
            + " AND pp.voided = 0 "
            + " AND ob.voided = 0 "
            + " AND ee.voided = 0 "
            + " AND ee.encounter_datetime <= :revisionEndDate "
            + " AND ob.value_coded IS NOT NULL ) vl "
            + " INNER JOIN ( "
            + " SELECT patient_id, MAX(encounter_datetime) AS encounter_datetime "
            + " FROM ("
            + " SELECT patient_id, encounter_datetime FROM ( "
            + " SELECT p.patient_id,e.encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " INNER JOIN ( "
            + " SELECT patient_id, MAX(encounter_datetime) AS encounter_datetime FROM ( "
            + " SELECT p.patient_id AS patient_id, e.encounter_datetime AS encounter_datetime "
            + " FROM   patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o "
            + " ON e.encounter_id = o.encounter_id "
            + " WHERE  p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${6} "
            + " AND o.concept_id IN(${23724}, ${23730}) "
            + " AND o.value_coded = ${1256} "
            + " AND e.encounter_datetime BETWEEN date_add(:revisionEndDate, INTERVAL -14 month) "
            + " AND date_add(:revisionEndDate, INTERVAL -11 month) "
            + " UNION "
            + " SELECT pa.patient_id AS patient_id,ee.encounter_datetime AS encounter_datetime FROM patient pa "
            + " INNER JOIN encounter ee "
            + " ON pa.patient_id=ee.patient_id "
            + " INNER JOIN obs ob "
            + " ON ee.encounter_id=ob.encounter_id "
            + " INNER JOIN( "
            + " SELECT p.patient_id, "
            + " MAX(e.encounter_datetime) AS encounter_datetime "
            + " FROM   patient p "
            + " INNER JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o "
            + " ON e.encounter_id = o.encounter_id "
            + " WHERE  p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${6} "
            + " AND o.concept_id = ${23739} "
            + " AND e.encounter_datetime BETWEEN date_add(:revisionEndDate, INTERVAL -14 month) "
            + " AND date_add(:revisionEndDate, INTERVAL -11 month) group by p.patient_id) filt "
            + " WHERE pa.voided = 0 "
            + " AND ee.voided = 0 "
            + " AND ob.voided = 0 "
            + " AND ob.concept_id = ${23739} "
            + " AND ob.value_coded = ${23720} "
            + " AND ee.encounter_datetime=filt.encounter_datetime) combined group by patient_id) fin "
            + " ON fin.patient_id=p.patient_id "
            + " WHERE p.voided= 0 "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND o.concept_id = ${23722} "
            + " AND o.value_coded = ${856} "
            + " AND e.encounter_datetime <= :revisionEndDate "
            + " AND e.encounter_datetime > fin.encounter_datetime) h1 ) h11 group by patient_id) h111 "
            + " ON vl.patient_id = h111.patient_id "
            + " WHERE vl.encounter_datetime <=:revisionEndDate AND vl.encounter_datetime > h111.encounter_datetime ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  public static CohortDefinition getTransferredInPatients(
      int masterCardEncounterType,
      int transferFromOtherFacilityConcept,
      int patientFoundYesConcept,
      int typeOfPatientTransferredFrom,
      int artStatus) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("transferred in patients");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", masterCardEncounterType);
    map.put("1369", transferFromOtherFacilityConcept);
    map.put("1065", patientFoundYesConcept);
    map.put("6300", typeOfPatientTransferredFrom);
    map.put("6276", artStatus);

    String query =
        "SELECT  p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON e.patient_id=p.patient_id "
            + "    INNER JOIN obs obs1 "
            + "        ON obs1.encounter_id=e.encounter_id "
            + "    INNER JOIN obs obs2 "
            + "        ON obs2.encounter_id=e.encounter_id "
            + "WHERE p.voided =0  "
            + "    AND e.voided = 0 "
            + "    AND obs1.voided =0 "
            + "    AND obs2.voided =0 "
            + "    AND e.encounter_type = ${53}  "
            + "    AND e.location_id = :location "
            + "    AND obs1.concept_id = ${1369} AND obs1.value_coded = ${1065} "
            + "    AND obs2.concept_id = ${6300} AND obs2.value_coded = ${6276} ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * All patients ll patients with a clinical consultation(encounter type 6) during the Revision
   * period with the following conditions:
   *
   * <p>- “TEM SINTOMAS DE TB” (concept_id 23758) value coded “SIM” or “NÃO”(concept_id IN [1065,
   * 1066]) and Encounter_datetime between:
   *
   * <p>- Encounter_datetime between startDateRevision and endDateRevision (should be the last
   * encounter during the revision period)
   */
  public static CohortDefinition getPatientsWithTBSymptoms(
      int adultoSeguimentoEncounterType, int tbSymptomsConcept, int yesConcept, int noConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("transferred in patients");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("23758", tbSymptomsConcept);
    map.put("1065", yesConcept);
    map.put("1066", noConcept);

    String query =
        " SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o"
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id,"
            + "                          Max(e.encounter_datetime) AS encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                   WHERE  e.encounter_type = ${6} "
            + "                          AND p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_datetime BETWEEN "
            + "                              :startDate AND :revisionEndDate "
            + "                   GROUP  BY p.patient_id) filtered "
            + "               ON p.patient_id = filtered.patient_id "
            + "WHERE  e.encounter_datetime = filtered.encounter_datetime "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id = ${23758} "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND o.value_coded IN ( ${1065}, ${1066}) "
            + "       AND e.encounter_type = ${6}  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <<<<<<< HEAD <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * B1- Select all patients who have the LAST “LINHA TERAPEUTICA” (Concept Id 21151) recorded in
   * Ficha Clinica (encounter type 6, encounter_datetime) with value coded “PRIMEIRA LINHA” (concept
   * id 21150) during the inclusion period (startDateInclusion and endDateInclusion)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public static CohortDefinition getMQ13DenB1(
      EncounterType treatmentEncounter,
      Concept treatmentConcept,
      List<Concept> treatmentValueCoded) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients on treatment for 6 months from last clinical visit");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    List<Integer> answerIds = new ArrayList<>();

    for (Concept concept : treatmentValueCoded) {
      answerIds.add(concept.getConceptId());
    }

    String query =
        " SELECT p.patient_id "
            + " FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " INNER JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) last_visit "
            + "               FROM patient p "
            + "               INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "               WHERE  p.voided = 0 AND e.voided = 0 "
            + "                   AND e.encounter_type = ${treatmentEncounter} "
            + "                   AND e.location_id = :location "
            + "                   AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate "
            + "               GROUP BY p.patient_id) last_linha_terapeutica ON last_linha_terapeutica.patient_id = e.patient_id "
            + "               WHERE p.voided = 0 AND e.voided = 0  AND o.voided = 0 "
            + "                   AND e.encounter_type = ${treatmentEncounter} "
            + "                   AND e.location_id = :location "
            + "                   AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate "
            + "                   AND e.encounter_datetime = last_linha_terapeutica.last_visit "
            + "                   AND o.concept_id = ${treatmentConcept} "
            + "                   AND o.value_coded = ${treatmentValueCoded}";

    Map<String, String> map = new HashMap<>();
    map.put("treatmentEncounter", String.valueOf(treatmentEncounter.getEncounterTypeId()));
    map.put("treatmentConcept", String.valueOf(treatmentConcept.getConceptId()));
    map.put("treatmentValueCoded", StringUtils.join(answerIds, ","));

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <<<<<<< HEAD <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * B4 - Select all patients from Ficha Clinica (encounter type 6) with “Carga Viral” (Concept id
   * 856) registered with numeric value > 1000 during the Inclusion period (startDateInclusion and
   * endDateInclusion) and filter all female patients registered with concept “GESTANTE”(Concept Id
   * 1982) with value coded ‘SIM’ (Concept Id 1065) on the same encounter. (Note: consider the
   * oldest encounter in case of more than one encounter “Carga Viral” with numeric value > 1000)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public static CohortDefinition getMQ11DenB4(
      int adultoSeguimentoEncounterType,
      int hivViralLoadConcept,
      int yesConcept,
      int pregnantConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Cat11 B4");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("856", hivViralLoadConcept);
    map.put("1065", yesConcept);
    map.put("1982", pregnantConcept);

    String query =
        " SELECT p.patient_id "
            + " FROM patient p "
            + " INNER JOIN ( "
            + "             SELECT p.patient_id, MIN(e.encounter_datetime) as first_carga_viral  "
            + "             FROM patient p  "
            + "             INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "             WHERE  p.voided = 0   "
            + "                    AND e.voided = 0   "
            + "                    AND o.voided = 0  "
            + "                    AND e.encounter_type = ${6}    "
            + "                    AND o.concept_id = ${856}   "
            + "                    AND o.value_numeric > 1000  "
            + "                    AND e.encounter_datetime BETWEEN :startDate AND :endDate   "
            + "                    AND e.location_id = :location   "
            + "             GROUP  BY p.patient_id    "
            + "           ) AS lab ON lab.patient_id = p.patient_id  "
            + " INNER JOIN (  "
            + "             SELECT p.patient_id, e.encounter_datetime AS gestante  "
            + "             FROM patient p   "
            + "             INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "             WHERE   p.voided = 0   "
            + "                     AND e.voided = 0   "
            + "                     AND o.voided = 0   "
            + "                     AND e.encounter_type = ${6}    "
            + "                     AND o.concept_id = ${1982}    "
            + "                     AND o.value_coded = ${1065}    "
            + "                     AND e.encounter_datetime BETWEEN :startDate AND :endDate   "
            + "                     AND e.location_id = :location    "
            + "           ) AS mulher ON mulher.patient_id = p.patient_id  "
            + " WHERE p.voided = 0  "
            + "       AND lab.first_carga_viral = mulher.gestante";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * B4 - Select all patients from Ficha Clinica (encounter type 6 or 53) with “Carga Viral”
   * (Concept id 856) registered with numeric value > 1000 during the Inclusion period
   * (startDateInclusion and endDateInclusion) and filter all female patients registered with
   * concept “GESTANTE”(Concept Id 1982) with value coded ‘SIM’ (Concept Id 1065) on the same
   * encounter. (Note: consider the oldest encounter in case of more than one encounter “Carga
   * Viral” with numeric value > 1000)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public static CohortDefinition getMQ13DenB4_P4(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int hivViralLoadConcept,
      int yesConcept,
      int pregnantConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Cat11 B4");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("856", hivViralLoadConcept);
    map.put("1065", yesConcept);
    map.put("1982", pregnantConcept);

    String query =
        "SELECT p.patient_id "
            + "             FROM patient p "
            + "             INNER JOIN ( "
            + "                         SELECT p.patient_id, MIN(e.encounter_datetime) as first_carga_viral  "
            + "                         FROM patient p  "
            + "                         INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "                         WHERE  p.voided = 0   "
            + "                                AND e.voided = 0   "
            + "                                AND o.voided = 0      "
            + "                                AND o.concept_id = ${856}   "
            + "                                AND o.value_numeric >= 1000  "
            + "                                AND (( e.encounter_type = ${6} AND e.encounter_datetime BETWEEN :startDate AND :endDate) "
            + "                                    OR (e.encounter_type = ${53} AND o.obs_datetime BETWEEN :startDate AND :endDate))   "
            + "                                AND e.location_id = :location   "
            + "                         GROUP  BY p.patient_id    "
            + "                       ) AS lab ON lab.patient_id = p.patient_id  "
            + "             INNER JOIN (  "
            + "                         SELECT p.patient_id, e.encounter_datetime AS gestante  "
            + "                         FROM patient p   "
            + "                         INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "                         WHERE   p.voided = 0   "
            + "                                 AND e.voided = 0   "
            + "                                 AND o.voided = 0       "
            + "                                 AND o.concept_id = ${1982}    "
            + "                                 AND o.value_coded = ${1065}    "
            + "                                 AND (( e.encounter_type = ${6} "
            + "                                 AND e.encounter_datetime BETWEEN :startDate AND :endDate) "
            + "                                    OR (e.encounter_type = ${53} AND o.obs_datetime BETWEEN :startDate AND :endDate))   "
            + "                                 AND e.location_id = :location    "
            + "                       ) AS mulher ON mulher.patient_id = p.patient_id  "
            + "             WHERE p.voided = 0  "
            + "                   AND lab.first_carga_viral = mulher.gestante";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }
  /**
   * Revised B13 for the MQ 15 indicators
   *
   * @return CohortDefinition
   */
  public static CohortDefinition getPatientsWithAtLeastAdrugPickup(
      int encpounterType52, int encounterType18, int dateOfArtPickup, int returnVisitDateForArv) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("B13 for MQ CAT 15");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("18", encounterType18);
    map.put("52", encpounterType52);
    map.put("23866", dateOfArtPickup);
    map.put("5096", returnVisitDateForArv);

    String query =
        " SELECT p.patient_id FROM patient p"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + " WHERE e.encounter_type = ${18} "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND p.voided = 0 "
            + " AND o.concept_id = ${5096} "
            + " AND e.encounter_datetime <=:endDate "
            + " AND o.value_datetime IS NOT NULL "
            + " UNION "
            + " SELECT p.patient_id FROM patient p"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + " WHERE e.encounter_type = ${52} "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND p.voided = 0 "
            + " AND o.concept_id = ${23866} "
            + " AND o.value_datetime IS NOT NULL "
            + " AND o.value_datetime <=:endDate ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <<<<<<< HEAD <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * B5 - Select all patients from Ficha Clinica (encounter type 6) with “Carga Viral” (Concept id
   * 856) registered with numeric value > 1000 during the Inclusion period (startDateInclusion and
   * endDateInclusion) and filter all female patients registered with concept “LACTANTE”(Concept Id
   * 6332) with value coded ‘SIM’ (Concept Id 1065) on the same encounter. (Note: consider the
   * oldest encounter in case of more than one encounter “Carga Viral” with numeric value > 1000)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public static CohortDefinition getMQ11DenB5(
      int adultoSeguimentoEncounterType,
      int hivViralLoadConcept,
      int yesConcept,
      int breastfeedingConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Cat11 B5");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("856", hivViralLoadConcept);
    map.put("1065", yesConcept);
    map.put("6332", breastfeedingConcept);

    String query =
        " SELECT p.patient_id "
            + " FROM patient p "
            + " INNER JOIN ( "
            + "             SELECT p.patient_id, MIN(e.encounter_datetime) as first_carga_viral  "
            + "             FROM patient p  "
            + "             INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "             WHERE  p.voided = 0   "
            + "                    AND e.voided = 0   "
            + "                    AND o.voided = 0  "
            + "                    AND e.encounter_type = ${6}    "
            + "                    AND o.concept_id = ${856}   "
            + "                    AND o.value_numeric > 1000  "
            + "                    AND e.encounter_datetime BETWEEN :startDate AND :endDate   "
            + "                    AND e.location_id = :location   "
            + "             GROUP  BY p.patient_id    "
            + "           ) AS lab ON lab.patient_id = p.patient_id  "
            + " INNER JOIN (  "
            + "             SELECT p.patient_id, e.encounter_datetime AS lactante  "
            + "             FROM patient p   "
            + "             INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "             INNER JOIN person per ON per.person_id= p.patient_id  "
            + "             WHERE   p.voided = 0   "
            + "                     AND e.voided = 0   "
            + "                     AND o.voided = 0   "
            + "                     AND e.encounter_type = ${6}    "
            + "                     AND o.concept_id = ${6332}     "
            + "                     AND o.value_coded = ${1065}    "
            + "                     AND e.encounter_datetime BETWEEN :startDate AND :endDate   "
            + "                     AND e.location_id = :location    "
            + "                     AND per.gender = 'F'      "
            + "           ) AS mulher ON mulher.patient_id = p.patient_id  "
            + " WHERE p.voided = 0  "
            + "       AND lab.first_carga_viral = mulher.lactante";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * B5 - Select all patients from Ficha Clinica (encounter type 6 or 53) with “Carga Viral”
   * (Concept id 856) registered with numeric value > 1000 during the Inclusion period
   * (startDateInclusion and endDateInclusion) and filter all female patients registered with
   * concept “LACTANTE”(Concept Id 6332) with value coded ‘SIM’ (Concept Id 1065) on the same
   * encounter. (Note: consider the oldest encounter in case of more than one encounter “Carga
   * Viral” with numeric value > 1000)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public static CohortDefinition getMQ13DenB5_P4(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int hivViralLoadConcept,
      int yesConcept,
      int breastfeedingConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Cat11 B5");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("856", hivViralLoadConcept);
    map.put("1065", yesConcept);
    map.put("6332", breastfeedingConcept);

    String query =
        " SELECT p.patient_id "
            + "             FROM patient p "
            + "             INNER JOIN ( "
            + "                         SELECT p.patient_id, MIN(e.encounter_datetime) as first_carga_viral  "
            + "                         FROM patient p  "
            + "                         INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "                         WHERE  p.voided = 0   "
            + "                                AND e.voided = 0   "
            + "                                AND o.voided = 0     "
            + "                                AND o.concept_id = ${856}   "
            + "                                AND o.value_numeric >= 1000  "
            + "                                AND (( e.encounter_type = ${6} AND e.encounter_datetime BETWEEN :startDate AND :endDate) "
            + "                                    OR (e.encounter_type = ${53} AND o.obs_datetime BETWEEN :startDate AND :endDate))   "
            + "                                AND e.location_id = :location   "
            + "                         GROUP  BY p.patient_id    "
            + "                       ) AS lab ON lab.patient_id = p.patient_id  "
            + "             INNER JOIN (  "
            + "                         SELECT p.patient_id, e.encounter_datetime AS lactante  "
            + "                         FROM patient p   "
            + "                         INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "                         INNER JOIN person per ON per.person_id= p.patient_id  "
            + "                         WHERE   p.voided = 0   "
            + "                                 AND e.voided = 0   "
            + "                                 AND o.voided = 0      "
            + "                                 AND o.concept_id = ${6332}     "
            + "                                 AND o.value_coded = ${1065}    "
            + "                                 AND (( e.encounter_type = ${6} AND e.encounter_datetime BETWEEN :startDate AND :endDate) "
            + "                                    OR (e.encounter_type = ${53} AND o.obs_datetime BETWEEN :startDate AND :endDate))    "
            + "                                 AND e.location_id = :location    "
            + "                                 AND per.gender = 'F'      "
            + "                       ) AS mulher ON mulher.patient_id = p.patient_id  "
            + "             WHERE p.voided = 0  "
            + "                   AND lab.first_carga_viral = mulher.lactante";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }
}
