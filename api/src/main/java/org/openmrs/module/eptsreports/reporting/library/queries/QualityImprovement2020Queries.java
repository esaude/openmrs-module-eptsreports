package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
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
            + "          GROUP  BY p.patient_id "
            + "          UNION "
            + "          SELECT 	p.patient_id, Min(pickupdate.value_datetime) AS data_inicio "
            + "          FROM   	patient p "
            + "              JOIN encounter e "
            + "                ON p.patient_id = e.patient_id "
            + "              JOIN obs pickup "
            + "                ON e.encounter_id = pickup.encounter_id "
            + "              JOIN obs pickupdate "
            + "                ON e.encounter_id = pickupdate.encounter_id "
            + "          WHERE  	p.voided = 0 "
            + "              AND pickup.voided = 0 "
            + "              AND pickup.concept_id = ${23865} "
            + "              AND pickup.value_coded = ${1065} "
            + "              AND pickupdate.voided = 0 "
            + "              AND pickupdate.concept_id = ${23866} "
            + "              AND pickupdate.value_datetime <= :endDate "
            + "              AND e.encounter_type = ${52} "
            + "              AND e.voided = 0 "
            + "              AND e.location_id = :location "
            + "          GROUP  BY p.patient_id) inicio "
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
        "SELECT second_visit.patient_id "
            + "FROM   (SELECT first_visit.patient_id, first_visit.first_visit_1, after_first_visit.first_visit_2  "
            + "        FROM   (SELECT inicio_real.patient_id, inicio_real.data_inicio, first_real.first_visit_1  "
            + "                FROM   (SELECT patient_id, data_inicio  "
            + "                        FROM   (SELECT patient_id, Min(data_inicio) data_inicio  "
            + "                                FROM   (SELECT p.patient_id, Min(value_datetime) data_inicio  "
            + "                                        FROM   patient p  "
            + "                                               INNER JOIN encounter e  "
            + "                                                       ON p.patient_id = e.patient_id  "
            + "                                               INNER JOIN obs o  "
            + "                                                       ON e.encounter_id = o.encounter_id  "
            + "                                        WHERE  p.voided = 0  "
            + "                                               AND e.voided = 0  "
            + "                                               AND o.voided = 0  "
            + "                                               AND e.encounter_type = ${53}  "
            + "                                               AND o.concept_id = ${1190}  "
            + "                                               AND o.value_datetime IS NOT NULL  "
            + "                                               AND o.value_datetime <= :endDate  "
            + "                                               AND e.location_id = :location  "
            + "                                        GROUP  BY p.patient_id  "
            + "                                        UNION  "
            + "                                        SELECT p.patient_id, Min(pickupdate.value_datetime) AS data_inicio  "
            + "                                        FROM   patient p  "
            + "                                               JOIN encounter e  "
            + "                                                 ON p.patient_id = e.patient_id  "
            + "                                               JOIN obs pickup  "
            + "                                                 ON e.encounter_id =  "
            + "                                                    pickup.encounter_id  "
            + "                                               JOIN obs pickupdate  "
            + "                                                 ON e.encounter_id =  "
            + "                                                    pickupdate.encounter_id  "
            + "                                        WHERE  p.voided = 0  "
            + "                                               AND pickup.voided = 0  "
            + "                                               AND pickup.concept_id = ${23865}  "
            + "                                               AND pickup.value_coded = ${1065}  "
            + "                                               AND pickupdate.voided = 0  "
            + "                                               AND pickupdate.concept_id = ${23866}  "
            + "                                               AND pickupdate.value_datetime <= :endDate  "
            + "                                               AND e.encounter_type = ${52}  "
            + "                                               AND e.voided = 0  "
            + "                                               AND e.location_id = :location  "
            + "                                        GROUP  BY p.patient_id) inicio  "
            + "                                GROUP  BY patient_id) inicio1  "
            + "                        WHERE  data_inicio BETWEEN :startDate AND :endDate  "
            + "                       )  "
            + "                       inicio_real  "
            + "                       INNER JOIN (SELECT e.patient_id,  "
            + "                                          e.encounter_datetime AS first_visit_1  "
            + "                                   FROM   patient p  "
            + "                                          INNER JOIN encounter e  "
            + "                                                  ON e.patient_id = p.patient_id  "
            + "                                   WHERE  e.voided = 0  "
            + "                                          AND e.location_id = :location  "
            + "                                          AND e.encounter_type = ${6}  "
            + "                                          AND e.encounter_datetime <= :endDate) AS first_real  "
            + "                               ON inicio_real.patient_id = first_real.patient_id  "
            + "                WHERE  first_real.first_visit_1 >= DATE_ADD(inicio_real.data_inicio, INTERVAL "
            + lowerBound
            + " DAY)  "
            + "                       AND first_real.first_visit_1 <= DATE_ADD(inicio_real.data_inicio, INTERVAL "
            + upperBound
            + " DAY)) AS first_visit  "
            + "                INNER JOIN (SELECT e.patient_id, e.encounter_datetime AS first_visit_2  "
            + "                           FROM   patient p  "
            + "                                  INNER JOIN encounter e  "
            + "                                          ON e.patient_id = p.patient_id  "
            + "                           WHERE  e.voided = 0  "
            + "                                  AND e.location_id = :location  "
            + "                                  AND e.encounter_type = ${6}  "
            + "                                  AND e.encounter_datetime <= :endDate) AS  "
            + "                       after_first_visit  "
            + "                       ON first_visit.patient_id = after_first_visit.patient_id  "
            + "        WHERE  after_first_visit.first_visit_2 >= DATE_ADD(first_visit.first_visit_1, INTERVAL "
            + lowerBound
            + " DAY)  "
            + "               AND after_first_visit.first_visit_2 <= DATE_ADD(first_visit.first_visit_1, INTERVAL "
            + upperBound
            + " DAY)) AS second_visit  "
            + "        INNER JOIN (SELECT e.patient_id, e.encounter_datetime AS first_visit_3  "
            + "                   FROM   patient p  "
            + "                          INNER JOIN encounter e  "
            + "                                  ON e.patient_id = p.patient_id  "
            + "                   WHERE  e.voided = 0  "
            + "                          AND e.location_id = :location  "
            + "                          AND e.encounter_type = ${6}  "
            + "                          AND e.encounter_datetime <= :endDate) AS after_second_visit  "
            + "        ON after_second_visit.patient_id = second_visit.patient_id  "
            + "WHERE  after_second_visit.first_visit_3 > DATE_ADD(second_visit.first_visit_2, INTERVAL "
            + lowerBound
            + " DAY)  "
            + "AND after_second_visit.first_visit_3 <= DATE_ADD(second_visit.first_visit_2, INTERVAL "
            + upperBound
            + " DAY);";
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
  public static SqlCohortDefinition getMQ15DenA(
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
            + "              AND e.location_id = :location "
            + "              AND e.encounter_type = ${6} "
            + "              AND ( o.concept_id = ${23724} OR o.concept_id = ${23730} ) "
            + "              AND o.value_coded = ${1256} "
            + "              AND e.encounter_datetime <= :endDate "
            + "       GROUP  BY p.patient_id "
            + "        UNION  "
            + "        SELECT p.patient_id, "
            + "               Max(e.encounter_datetime) encounter_datetime "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e "
            + "                       ON p.patient_id = e.patient_id "
            + "               INNER JOIN obs o "
            + "                       ON e.encounter_id = o.encounter_id "
            + "        WHERE  p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_type = ${6} "
            + "               AND o.concept_id = ${23739} "
            + "               AND o.value_coded = ${23720} "
            + "               AND e.encounter_datetime <= :endDate "
            + "        GROUP  BY p.patient_id) encounters "
            + "WHERE  encounters.encounter_datetime BETWEEN Date_sub(:endDate, INTERVAL 14 month) "
            + "       AND Date_sub(:endDate, INTERVAL 11 month) ";

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
            + "        AND e.voided = 0 "
            + "        AND e.location_id = :location "
            + "        AND e.encounter_type = ${6} "
            + "        AND "
            + middleQuery
            + "        AND o.value_coded = ${1256} "
            + "        AND e.encounter_datetime <= :endDate "
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
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("23720", quarterlyConcept);
    map.put("23739", typeOfDispensationConcept);

    String query =
        "SELECT a3.patient_id "
            + "FROM   (SELECT p.patient_id, "
            + "               Max(e.encounter_datetime) encounter_datetime "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e "
            + "                       ON p.patient_id = e.patient_id "
            + "               INNER JOIN obs o "
            + "                       ON e.encounter_id = o.encounter_id "
            + "        WHERE  p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_type = ${6} "
            + "               AND o.concept_id = ${23739} "
            + "               AND o.value_coded = ${23720} "
            + "               AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "        GROUP  BY p.patient_id) a3 ";

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
            + "                                          AND e.location_id = :location "
            + "                                          AND e.encounter_type = ${6} "
            + "                                          AND o.concept_id = ${23724} "
            + "                                          AND o.value_coded = ${1256} "
            + "                                          AND e.encounter_datetime <= :endDate "
            + "                                   GROUP  BY p.patient_id) started_gaac "
            + "                               ON p.patient_id = started_gaac.patient_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND o.concept_id = ${23724} "
            + "                       AND o.value_coded = ${1267} "
            + "                       AND e.encounter_datetime > started_gaac.encounter_datetime "
            + "                       AND e.encounter_datetime <= :endDate "
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
            + "                                          AND e.location_id = :location "
            + "                                          AND e.encounter_type = ${6} "
            + "                                          AND o.concept_id = ${23730} "
            + "                                          AND o.value_coded = ${1256} "
            + "                                          AND e.encounter_datetime <= :endDate "
            + "                                   GROUP  BY p.patient_id) started_dt "
            + "                               ON p.patient_id = started_dt.patient_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND o.concept_id = ${23730} "
            + "                       AND o.value_coded = ${1267} "
            + "                       AND e.encounter_datetime > started_dt.encounter_datetime "
            + "                       AND e.encounter_datetime <= :endDate "
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
            + "                                          AND e.location_id = :location "
            + "                                          AND e.encounter_type = ${6} "
            + "                                          AND o.concept_id = ${23739} "
            + "                                          AND o.value_coded = ${23720} "
            + "                                          AND e.encounter_datetime <= :endDate "
            + "                                   GROUP  BY p.patient_id) a3 "
            + "                       ON p.patient_id = a3.patient_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND o.concept_id = ${23739} "
            + "                       AND o.value_coded <> ${23720} "
            + "                       AND e.encounter_datetime <= :endDate "
            + "                GROUP  BY p.patient_id) not_dt) b1; ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }
}
