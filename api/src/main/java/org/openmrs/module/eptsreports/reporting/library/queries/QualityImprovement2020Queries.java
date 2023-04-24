package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CommonCohortQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class QualityImprovement2020Queries {

  /**
   * H - Filter all patients that returned for any clinical consultation (encounter type 6,
   * encounter_datetime) or ARV pickup (encounter type 52, concept ID 23866 value_datetime, Levantou
   * ARV (concept id 23865) = Sim (1065)) between 25 and 33 days after ART start date(Oldest date
   * From A).
   *
   * @param lowerBound The Lower Bound in days
   * @param upperBound The Upper Bound in days
   * @param adultoSeguimentoEncounterType The Clinical Consultation Encounter Type 6
   * @param masterCardDrugPickupEncounterType The masterCard Drug Pickup Encounter Type 52
   * @param masterCardEncounterType The masterCard Encounter Type 53
   * @param yesConcept The answer yes concept Id 1065
   * @param historicalDrugStartDateConcept historical Drug Start Date Concept Id 1190
   * @param artPickupConcept The ART Pickup Concept Id 23865
   * @param artDatePickupMasterCard The ART Date Pickup MasterCard Concept Id 23866
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
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

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
            + "                      SELECT     p.patient_id, e.encounter_datetime AS first_visit"
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e "
            + "                      ON         e.patient_id = p.patient_id "
            + "                      WHERE      e.voided = 0 "
            + "                      AND      p.voided = 0 "
            + "                      AND        e.location_id = :location "
            + "                      AND        e.encounter_type = ${6}"
            + "                      AND        e.encounter_datetime <= :endDate"
            + "                      UNION "
            + "                      SELECT     p.patient_id, pickupdate.value_datetime AS first_visit"
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e "
            + "                      ON         e.patient_id = p.patient_id "
            + "                      INNER JOIN obs o "
            + "                      ON         o.encounter_id = e.encounter_id "
            + "                      INNER JOIN obs pickupdate "
            + "                      ON         e.encounter_id = pickupdate.encounter_id "
            + "                      WHERE      e.voided = 0 "
            + "                      AND        o.voided = 0 "
            + "                      AND        p.voided = 0 "
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
   * @param lowerBound The Lower Bound in days
   * @param upperBound The Upper Bound in days
   * @param adultoSeguimentoEncounterType The Clinical Consultation Encounter Type 6
   * @param masterCardDrugPickupEncounterType The masterCard Drug Pickup Encounter Type 52
   * @param masterCardEncounterType The masterCard Encounter Type 53
   * @param yesConcept The answer yes concept Id 1065
   * @param historicalDrugStartDateConcept historical Drug Start Date Concept Id 1190
   * @param artPickupConcept The ART Pickup Concept Id 23865
   * @param artDatePickupMasterCard The ART Date Pickup MasterCard Concept Id 23866
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
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

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
   * <b>MQ15DEN RF5 </b> - O sistema irá identificar utentes adultos inscritos no MDS
   * (GAAC/DT/APE/DD/DS/FR) há 24 meses selecionando:</b><br>
   *
   * <ul>
   *   <li>os utentes com registo de um MDC (MDC 1 ou MDC 2 ou MDC 3 ou MDC 4 ou MDC 5) como “GA” e
   *       o respectivo “Estado” = “Início” numa consulta clínica (“Ficha Clínica”) decorrida há 24
   *       meses (“Data Consulta Clínica” >= “Data Fim Revisão” – 26 meses+1dia e “Data Consulta
   *       Clínica” <= “Data Fim Revisão” – 24 meses) ou<br>
   *   <li>os utentes com registo de um MDC (MDC 1 ou MDC 2 ou MDC 3 ou MDC 4 ou MDC 5) como “DT” e
   *       o respectivo “Estado” = “Início” numa consulta clínica (“Ficha Clínica”) decorrida há 24
   *       meses (“Data Consulta Clínica” >= “Data Fim Revisão” – 26 meses+1dia e “Data Consulta
   *       Clínica” <= “Data Fim Revisão” – 24 meses) ou<br>
   *   <li>os utentes com registo de um MDC (MDC 1 ou MDC 2 ou MDC 3 ou MDC 4 ou MDC 5) como “APE” e
   *       o respectivo “Estado” = “Início” numa consulta clínica (“Ficha Clínica”) decorrida há 24
   *       meses (“Data Consulta Clínica” >= “Data Fim Revisão” – 26 meses+1dia e “Data Consulta
   *       Clínica” <= “Data Fim Revisão” – 24 meses) ou<br>
   *   <li>os utentes com registo de um MDC (MDC 1 ou MDC 2 ou MDC 3 ou MDC 4 ou MDC 5) como “DD” e
   *       o respectivo “Estado” = “Início” numa consulta clínica (“Ficha Clínica”) decorrida há 24
   *       meses (“Data Consulta Clínica” >= “Data Fim Revisão” – 26 meses+1dia e “Data Consulta
   *       Clínica” <= “Data Fim Revisão” – 24 meses) ou<br>
   *   <li>os utentes com registo de um MDC (MDC 1 ou MDC 2 ou MDC 3 ou MDC 4 ou MDC 5) como “DS” e
   *       o respectivo “Estado” = “Início” numa consulta clínica (“Ficha Clínica”) decorrida há 24
   *       meses (“Data Consulta Clínica” >= “Data Fim Revisão” – 26 meses+1dia e “Data Consulta
   *       Clínica” <= “Data Fim Revisão” – 24 meses) ou<br>
   *   <li>os utentes com registo de um MDC (MDC 1 ou MDC 2 ou MDC 3 ou MDC 4 ou MDC 5) como “FR” e
   *       o respectivo “Estado” = “Início” numa consulta clínica (“Ficha Clínica”) decorrida há 24
   *       meses (“Data Consulta Clínica” >= “Data Fim Revisão” – 26 meses+1dia e “Data Consulta
   *       Clínica” <= “Data Fim Revisão” – 24 meses) ou<br>
   * </ul>
   *
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getPatientsWithFollowingMdcDispensationsWithStates(
      List<Integer> dispensationTypes, List<Integer> states) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Utentes Inscritos no MDS com determinados estados");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("dispensationTypes", getMetadataFrom(dispensationTypes));
    map.put("states", getMetadataFrom(states));

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "           INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "           INNER JOIN obs otype ON otype.encounter_id = e.encounter_id "
            + "           INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${6} "
            + "  AND e.location_id = :location "
            + "  AND otype.concept_id = ${165174} "
            + "  AND otype.value_coded IN (${dispensationTypes}) "
            + "  AND ostate.concept_id = ${165322} "
            + "  AND ostate.value_coded IN (${states}) "
            + "  AND e.encounter_datetime >= :startDate "
            + "  AND e.encounter_datetime <= :endDate "
            + "  AND otype.obs_group_id = ostate.obs_group_id "
            + "  AND e.voided = 0 "
            + "  AND p.voided = 0 "
            + "  AND otype.voided = 0 "
            + "  AND ostate.voided = 0 "
            + "GROUP  BY p.patient_id";

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
   * @param flag flag
   * @param adultoSeguimentoEncounterType The Clinical Consultation Encounter Type 6
   * @param startDrugs The start Drugs concept Id 1256
   * @param gaac GAAC (GA) Concept Id 23724 (DT) Concept Id 23730
   * @param quarterlyDispensation The quarterly Dispensation Concept Id 23730
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
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

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
            + "        AND e.encounter_datetime BETWEEN :startDate "
            + "        AND :endDate"
            + " GROUP  BY p.patient_id; ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Os utentespacientes com registo de “Tipo de Dispensa” = “DT” na última consulta (“Ficha
   * Clínica”) decorrida há 12 24 meses (última “Data Consulta Clínica” >= “Data Fim Revisão” – 2614
   * meses+1dia e “Data Consulta Clínica” <= “Data Fim Revisão” – 2411 meses) ou
   *
   * <p>Os utentes com registo de “Tipo de Dispensa” = “DS” na última consulta (“Ficha Clínica”)
   * decorrida há 24 meses (última “Data Consulta Clínica” >= “Data Fim Revisão” – 26 meses+1dia e
   * “Data Consulta Clínica” <= “Data Fim Revisão” – 24 meses)
   *
   * @param adultoSeguimentoEncounterType The Clinical Consultation Encounter Type 6
   * @param quarterlyConcept The Quarterly Dispensation Concept Id 23720
   * @param typeOfDispensationConcept The Type Of Dispensation Concept Id 23739
   * @param semiannualDispensation The Type Of Dispensation Concept Id 23888
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getMQ15DenA3(
      int adultoSeguimentoEncounterType,
      int quarterlyConcept,
      int typeOfDispensationConcept,
      int semiannualDispensation) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients who started GAAC)");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("23720", quarterlyConcept);
    map.put("23739", typeOfDispensationConcept);
    map.put("23888", semiannualDispensation);

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
            + " GROUP BY p.patient_id "
            + " ) a1 "
            + " ON p.patient_id = a1.patient_id "
            + " WHERE "
            + " p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND a1.max_encounter_date = e.encounter_datetime"
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${6} "
            + " AND o.concept_id =${23739} "
            + " AND o.value_coded IN (${23720}, ${23888}) "
            + " AND e.encounter_datetime "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }
  /**
   * os utentes com registo de último levantamento na farmácia (FILA) há 24 meses (última “Data
   * Levantamento”>= “Data Fim Revisão” – 26 meses+1dia e <= “Data Fim Revisão” – 24 meses) com
   * próximo levantamento agendado para 83 a 97 dias ( “Data Próximo Levantamento” menos “Data
   * Levantamento”>= 83 dias e <= 97 dias)
   *
   * <p>os utentes com registo de último levantamento na farmácia (FILA) há 24 meses (última “Data
   * Levantamento”>= “Data Fim Revisão” – 26 meses+1dia e <= “Data Fim Revisão” – 24 meses) com
   * próximo levantamento agendado para 173 a 187 dias ( “Data Próximo Levantamento” menos “Data
   * Levantamento”>= 173 dias e <= 187 dias).
   *
   * @param lowerBounded
   * @param upperBounded
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getPatientsWithPickupOnFilaBetween(
      int lowerBounded, int upperBounded) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients who have pickup registered on FILA)");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    map.put("lower", lowerBounded);
    map.put("upper", upperBounded);

    String query =
        "SELECT     p.patient_id "
            + "FROM       patient p "
            + "INNER JOIN encounter e ON  e.patient_id = p.patient_id "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "INNER JOIN (          SELECT     p.patient_id, Max(e.encounter_datetime) consultation_date "
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e  ON  e.patient_id = p.patient_id "
            + "                      WHERE      e.encounter_type = ${18} "
            + "                      AND        e.location_id = :location "
            + "                      AND        e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                      AND        e.voided = 0 "
            + "                      AND        p.voided = 0 "
            + "                      GROUP BY   p.patient_id ) recent_clinical ON recent_clinical.patient_id = p.patient_id "
            + "WHERE      e.encounter_datetime = recent_clinical.consultation_date "
            + "AND        e.encounter_type = ${18} "
            + "AND        e.location_id = :location "
            + "AND        o.concept_id = ${5096} "
            + "AND        DATEDIFF(o.value_datetime, e.encounter_datetime) >= ${lower} "
            + " AND        DATEDIFF(o.value_datetime, e.encounter_datetime) <= ${upper} "
            + " AND        p.voided = 0 "
            + " AND        e.voided = 0 "
            + " AND        o.voided = 0 "
            + " GROUP BY   p.patient_id";

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
   * @param adultoSeguimentoEncounterType The Clinical Consultation Encounter Type 6
   * @param startDrugs The start Drugs concept Id 1256
   * @param gaac GAAC (GA) Concept Id 23724 (DT) Concept Id 23730
   * @param quarterlyDispensation The quarterly Dispensation Concept Id 23730
   * @param quarterlyConcept The Quarterly Dispensation Concept Id 23720
   * @param typeOfDispensationConcept The Type Of Dispensation Concept Id 23739
   * @param labReq ”PEDIDO DE INVESTIGACOES LABORATORIAIS” Concept Id 23722
   * @param viralLoad The viral Load Concept Id 856
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
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

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
   * @param adultoSeguimentoEncounterType The Clinical Consultation Encounter Type 6
   * @param startDrugs The start Drugs concept Id 1256
   * @param gaac GAAC (GA) Concept Id 23724 (DT) Concept Id 23730
   * @param quarterlyDispensation The quarterly Dispensation Concept Id 23730
   * @param quarterlyConcept The Quarterly Dispensation Concept Id 23720
   * @param typeOfDispensationConcept The Type Of Dispensation Concept Id 23739
   * @param labReq ”PEDIDO DE INVESTIGACOES LABORATORIAIS” Concept Id 23722
   * @param viralLoad The viral Load Concept Id 856yConcept
   * @param viralLoadQualitative The viral Load Qualitative Concept Id 1305
   * @param labEncounterType The lab Encounter Type 13
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
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

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
   * @param adultoSeguimentoEncounterType The Clinical Consultation Encounter Type 6
   * @param startDrugs The start Drugs concept Id 1256
   * @param gaac GAAC (GA) Concept Id 23724 (DT) Concept Id 23730
   * @param quarterlyDispensation The quarterly Dispensation Concept Id 23730
   * @param quarterlyConcept The Quarterly Dispensation Concept Id 23720
   * @param typeOfDispensationConcept The Type Of Dispensation Concept Id 23739
   * @param labReq ”PEDIDO DE INVESTIGACOES LABORATORIAIS” Concept Id 23722
   * @param viralLoad The viral Load Concept Id 856yConcept
   * @param viralLoadQualitative The viral Load Qualitative Concept Id 1305
   * @param labEncounterType The lab Encounter Type 13
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
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

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

  /**
   * <b>Description:</b> MOH Transferred In Query
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * All patients registered in Ficha Resumo (Encounter Type Id= 53) and marked as Transferred-in
   * (“Transfer from other facility” concept Id 1369 = “Yes” concept id 1065) in TARV (“Type of
   * Patient Transferred from” concept id 6300 = “ART” concept id 6276)
   *
   * <blockquote>
   *
   * @param masterCardEncounterType The Encounter Type Id 53
   * @param transferFromOtherFacilityConcept The Transfer from other facility Concept Id 1369
   * @param patientFoundYesConcept The answer Yes Concept Id 1065
   * @param typeOfPatientTransferredFrom The Type of Patient Transferred from Concept Id 6300
   * @param artStatus ART concept Id 6276
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients transfer from other facility
   */
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
   *
   * @param adultoSeguimentoEncounterType clinical consultation encounterType = 6
   * @param tbSymptomsConcept TB Symptoms concept_id = 23758
   * @param yesConcept answer yes concept_id = 1065
   * @param noConcept answer no concept_id = 1066
   * @return CohortDefinition
   */
  public static CohortDefinition getPatientsWithTBSymptoms(
      int adultoSeguimentoEncounterType, int tbSymptomsConcept, int yesConcept, int noConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("transferred in patients");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
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
   * B4 - Select all patients from Ficha Clinica (encounter type 6 or 53) with “Carga Viral”
   * (Concept id 856, encounter_datetime for Ficha Clinica and obs_datetime for Ficha Resumo)
   * registered with numeric value >= 1000 during the Inclusion period (startDateInclusion and
   * endDateInclusion) and filter all female patients registered with concept “GESTANTE”(Concept Id
   * 1982) with value coded ‘SIM’ (Concept Id 1065) on the same encounter. (Note: consider the
   * oldest encounter in case of more than one encounter “Carga Viral” with numeric value > 1000)
   *
   * </blockquote>
   *
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param hivViralLoadConcept The HIV ViralLoad Concept Id 856
   * @param yesConcept The answer yes Concept Id 1065
   * @param pregnantConcept The Pregnant Concept Id 1982
   * @param vlQuantity Quantity of viral load to evaluate
   * @return {@link CohortDefinition}
   */
  public static CohortDefinition getMQ13DenB4_P4(
      int adultoSeguimentoEncounterType,
      int hivViralLoadConcept,
      int yesConcept,
      int pregnantConcept,
      int vlQuantity) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Cat11 B4");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
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
            + "                                AND o.value_numeric >= "
            + vlQuantity
            + "                                AND ( e.encounter_type = ${6} AND e.encounter_datetime BETWEEN :startDate AND :endDate) "
            + "                                AND e.location_id = :location   "
            + "                         GROUP  BY p.patient_id    "
            + "                       ) AS lab ON lab.patient_id = p.patient_id  "
            + "             INNER JOIN (  "
            + "                         SELECT p.patient_id, e.encounter_datetime AS gestante  "
            + "                         FROM patient p   "
            + "                         INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "                         INNER JOIN person pe ON p.patient_id = pe.person_id "
            + "                         WHERE   p.voided = 0   "
            + "                                 AND e.voided = 0   "
            + "                                 AND o.voided = 0       "
            + "                                 AND o.concept_id = ${1982}    "
            + "                                 AND o.value_coded = ${1065}    "
            + "                                 AND pe.gender = 'F' "
            + "                                 AND ( e.encounter_type = ${6} "
            + "                                 AND e.encounter_datetime BETWEEN :startDate AND :endDate) "
            + "                                 AND e.location_id = :location    "
            + "                       ) AS mulher ON mulher.patient_id = p.patient_id  "
            + "             WHERE p.voided = 0  "
            + "                   AND lab.first_carga_viral = mulher.gestante";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  public static CohortDefinition getMQ13DenB4_P4(
      int adultoSeguimentoEncounterType,
      int hivViralLoadConcept,
      int yesConcept,
      int pregnantConcept) {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition indicator =
        getMQ13DenB4_P4(
            adultoSeguimentoEncounterType, hivViralLoadConcept, yesConcept, pregnantConcept, 1000);

    compositionCohortDefinition.addSearch("indicator", Mapped.mapStraightThrough(indicator));

    compositionCohortDefinition.setCompositionString("indicator");

    return compositionCohortDefinition;
  }

  /**
   * Revised B13 for the MQ 15 indicators
   *
   * @param encpounterType52
   * @param encounterType18
   * @param dateOfArtPickup
   * @param returnVisitDateForArv
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
   * B5 - Select all patients from Ficha Clinica (encounter type 6 or 53) with “Carga Viral”
   * (Concept id 856, encounter_datetime for Ficha Clinica and obs_datetime for Ficha Resumo)
   * registered with numeric value > 1000 during the Inclusion period (startDateInclusion and
   * endDateInclusion) and filter all female patients registered with concept “LACTANTE”(Concept Id
   * 6332) with value coded ‘SIM’ (Concept Id 1065) on the same encounter. (Note: consider the
   * oldest encounter in case of more than one encounter “Carga Viral” with numeric value > 1000)
   *
   * </blockquote>
   *
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param hivViralLoadConcept The HIV ViralLoad Concept Id 856
   * @param yesConcept The answer yes Concept Id 1065
   * @param breastfeedingConcept The breastfeeding Concept Id 6332
   * @param vlQuantity Quantity of viral load to evaluate
   * @return CohortDefinition
   */
  public static CohortDefinition getMQ13DenB5_P4(
      int adultoSeguimentoEncounterType,
      int hivViralLoadConcept,
      int yesConcept,
      int breastfeedingConcept,
      int vlQuantity) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Cat11 B5");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
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
            + "                                AND o.value_numeric >=  "
            + vlQuantity
            + "                                AND ( e.encounter_type = ${6} AND e.encounter_datetime BETWEEN :startDate AND :endDate) "
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
            + "                                 AND ( e.encounter_type = ${6} AND e.encounter_datetime BETWEEN :startDate AND :endDate) "
            + "                                 AND e.location_id = :location    "
            + "                                 AND per.gender = 'F'      "
            + "                       ) AS mulher ON mulher.patient_id = p.patient_id  "
            + "             WHERE p.voided = 0  "
            + "                   AND lab.first_carga_viral = mulher.lactante";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  public static CohortDefinition getMQ13DenB5_P4(
      int adultoSeguimentoEncounterType,
      int hivViralLoadConcept,
      int yesConcept,
      int breastfeedingConcept) {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition indicator =
        getMQ13DenB5_P4(
            adultoSeguimentoEncounterType,
            hivViralLoadConcept,
            yesConcept,
            breastfeedingConcept,
            1000);

    compositionCohortDefinition.addSearch("indicator", Mapped.mapStraightThrough(indicator));

    compositionCohortDefinition.setCompositionString("indicator");

    return compositionCohortDefinition;
  }

  /**
   * <b> O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período de
   * revisão seguinte forma: </b>
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com registo de “Mudança de Estado de Permanência” = “Abandono” na Ficha
   * Clínica nos 6 meses anteriores a data a última consulta. (“Data Consulta Abandono” >= “Data
   * Última Consulta” menos 6 meses e <= “Data última Consulta”).
   *
   * <blockquote>
   *
   * <p>Nota MISAU: Data da última consulta recuar 6 meses [data última consulta menos (-) 6 meses]
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com registo de “Mudança de Estado de Permanência” = “Abandono” na Ficha
   * Resumo durante o período (“Data de Mudança de Estado Permanência Abandono” (“Data Consulta
   * Abandono” >= “Data Última Consulta” menos 6 meses e <= “Data última Consulta”)
   *
   * <p>Nota: “Data Última Consulta” é a data da última consulta clínica ocorrida durante o período
   * de revisão.
   *
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param masterCardEncounterType The Ficha Resumo Encounter Type 53
   * @param stateOfStayOfArtPatient The State of Stay in ART Concept 6273
   * @param abandonedConcept The Abandoned Concept 1707
   * @param stateOfStayOfPreArtPatient The State of Stay in Pre Art Concept 6272
   * @return {@link String}
   */
  public static String getMQ13AbandonedTarvOnArtStartDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayOfArtPatient,
      int abandonedConcept,
      int stateOfStayOfPreArtPatient) {

    CommonQueries commonQueries = new CommonQueries(new CommonMetadata(), new HivMetadata());
    String artStart = commonQueries.getARTStartDate(true);

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("6273", stateOfStayOfArtPatient);
    map.put("1707", abandonedConcept);
    map.put("6272", stateOfStayOfPreArtPatient);

    String query =
        " SELECT abandoned.patient_id from ( "
            + "                                     SELECT p.patient_id, max(e.encounter_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + artStart
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + "                                     WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${6} "
            + "                                       AND o.concept_id = ${6273} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= end_period.first_pickup "
            + "                                       AND e.encounter_datetime >= DATE_SUB(end_period.first_pickup, INTERVAL 6 MONTH) "
            + "                                       AND e.encounter_datetime <= end_period.first_pickup "
            + "                                     GROUP BY p.patient_id "
            + "UNION "
            + "     SELECT p.patient_id, max(o.obs_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + artStart
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${53} "
            + "                                       AND o.concept_id = ${6272} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND o.obs_datetime >= end_period.first_pickup "
            + "                                       AND o.obs_datetime >= DATE_SUB(end_period.first_pickup, INTERVAL 6 MONTH) "
            + "                                       AND o.obs_datetime <= end_period.first_pickup "
            + "                                     GROUP BY p.patient_id "
            + "                                 ) abandoned GROUP BY abandoned.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b> O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período da
   * seguinte forma: </b>
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Clínica durante o período (“Data Consulta”>=”Data Início Período” e “Data
   * Consulta”<=”Data Fim Período”
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Resumo durante o período (“Data de Mudança de Estado Permanência”>=”Data Início
   * Período” e “Data Consulta”<=”Data Fim Período”
   *
   * <p>Nota: O período é definido conforme o requisito onde os utentes abandonos em TARV no fim do
   * período serão excluídos:
   * <li>5. para exclusão nas mulheres grávidas que iniciaram TARV a “Data Início Período” será
   *     igual a “Data Início TARV” e “Data Fim do Período” será igual a “Data Início TARV”+3meses.
   *
   *     <p>Patient ART Start Date is the oldest date from the set of criterias defined in the
   *     common query: 1/1 Patients who initiated ART and ART Start Date as earliest from the
   *     following criterias is by End of the period (reporting endDate)
   *
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param masterCardEncounterType The Ficha Resumo Encounter Type 53
   * @param stateOfStayOfArtPatient The State of Stay in ART Concept 6273
   * @param abandonedConcept The Abandoned Concept 1707
   * @param stateOfStayOfPreArtPatient The State of Stay in Pre Art Concept 6272
   * @return {@link String}
   */
  public static String getMQ13AbandonedTarvOnArtStartDateForPregnants(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayOfArtPatient,
      int abandonedConcept,
      int stateOfStayOfPreArtPatient) {

    CommonQueries commonQueries = new CommonQueries(new CommonMetadata(), new HivMetadata());
    String artStart = commonQueries.getARTStartDate(true);

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("6273", stateOfStayOfArtPatient);
    map.put("1707", abandonedConcept);
    map.put("6272", stateOfStayOfPreArtPatient);

    String query =
        " SELECT abandoned.patient_id from ( "
            + "                                     SELECT p.patient_id, max(e.encounter_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + artStart
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + "                                     WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${6} "
            + "                                       AND o.concept_id = ${6273} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= end_period.first_pickup "
            + "                                       AND e.encounter_datetime <= DATE_ADD(end_period.first_pickup, INTERVAL 3 MONTH) "
            + "                                     GROUP BY p.patient_id "
            + "UNION "
            + "     SELECT p.patient_id, max(o.obs_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + artStart
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${53} "
            + "                                       AND o.concept_id = ${6272} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND o.obs_datetime >= end_period.first_pickup "
            + "                                       AND o.obs_datetime <= DATE_ADD(end_period.first_pickup, INTERVAL 3 MONTH)"
            + "                                     GROUP BY p.patient_id "
            + "                                 ) abandoned GROUP BY abandoned.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b> O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período da
   * seguinte forma: </b>
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Clínica durante o período (“Data Consulta”>=”Data Início Período” e “Data
   * Consulta”<=”Data Fim Período”
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Resumo durante o período (“Data de Mudança de Estado Permanência”>=”Data Início
   * Período” e “Data Consulta”<=”Data Fim Período”
   *
   * <p>Nota: O período é definido conforme o requisito onde os utentes abandonos em TARV no fim do
   * período serão excluídos:
   * <li>2.para exclusão nos utentes que reiniciaram TARV, a “Data Início Período” será igual a
   *     “Data Consulta Reinício TARV” e “Data Fim do Período” será igual a “Data Consulta Reínicio
   *     TARV”+6meses.
   *
   *     <p>Select all patients who restarted ARV for at least 6 months following: all patients who
   *     have “Mudança de Estado de Permanência TARV”=”Reinício” na Ficha Clínica durante o período
   *     de inclusão (“Data Consulta Reinício TARV” >= “Data Início Inclusão” e <= “Data Fim
   *     Inclusão”), where “Data Última Consulta” durante o período de revisão, menos (-) “Data
   *     Consulta Reinício TARV” maior ou igual (>=) a 6 meses
   *
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param masterCardEncounterType The Ficha Resumo Encounter Type 53
   * @param stateOfStayOfArtPatient The State of Stay in ART Concept 6273
   * @param abandonedConcept The Abandoned Concept 1707
   * @param stateOfStayOfPreArtPatient The State of Stay in Pre Art Concept 6272
   * @param pediatriaSeguimentoEncounterType The Pediatria Seguimento Encounter Type 9
   * @return {@link String}
   */
  public static String getMQ13AbandonedTarvOnArtRestartDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayOfArtPatient,
      int abandonedConcept,
      int stateOfStayOfPreArtPatient,
      int pediatriaSeguimentoEncounterType,
      int restartConcept) {

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("6273", stateOfStayOfArtPatient);
    map.put("1707", abandonedConcept);
    map.put("6272", stateOfStayOfPreArtPatient);
    map.put("1705", restartConcept);

    String query =
        " SELECT abandoned.patient_id from ( "
            + "                                     SELECT p.patient_id, max(e.encounter_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + getRestartedArtQuery()
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + "                                     WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${6} "
            + "                                       AND o.concept_id = ${6273} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= end_period.last_encounter "
            + "                                       AND e.encounter_datetime <= DATE_ADD(end_period.last_encounter, INTERVAL 6 MONTH) "
            + "                                     GROUP BY p.patient_id "
            + "UNION "
            + "     SELECT p.patient_id, max(o.obs_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + getRestartedArtQuery()
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${53} "
            + "                                       AND o.concept_id = ${6272} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND o.obs_datetime >= end_period.last_encounter "
            + "                                       AND o.obs_datetime <= DATE_ADD(end_period.last_encounter, INTERVAL 6 MONTH)"
            + "                                     GROUP BY p.patient_id "
            + "                                 ) abandoned GROUP BY abandoned.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b> O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período da
   * seguinte forma: </b>
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Clínica durante o período (“Data Consulta”>=”Data Início Período” e “Data
   * Consulta”<=”Data Fim Período”
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Resumo durante o período (“Data de Mudança de Estado Permanência”>=”Data Início
   * Período” e “Data Consulta”<=”Data Fim Período”
   *
   * <p>Nota: O período é definido conforme o requisito onde os utentes abandonos em TARV no fim do
   * período serão excluídos:
   * <li>3. para exclusão nos utentes que iniciaram novo regime de 1ª Linha, a “Data Início Período”
   *     será igual a “Data última Alternativa 1ª Linha” e a “Data Fim do Período” será “Data última
   *     Alternativa 1ª Linha” + 6meses.
   *
   *     <p>B1= (BI1 and not B1E) ‘: MUDANCA DE REGIME
   * <li>BI1 - Select all patients who have the most recent “ALTERNATIVA A LINHA - 1a LINHA”
   *     (Concept Id 21190, obs_datetime) recorded in Ficha Resumo (encounter type 53) with any
   *     value coded (not null) during the inclusion period (startDateInclusion and
   *     endDateInclusion) AND
   * <li>B1E - Exclude all patients from Ficha Clinica (encounter type 6, encounter_datetime) who
   *     have “LINHA TERAPEUTICA”(Concept id 21151) with value coded DIFFERENT THAN “PRIMEIRA
   *     LINHA”(Concept id 21150) and encounter_datetime > the most recent “ALTERNATIVA A LINHA - 1a
   *     LINHA” (from B1) and <= endDateInclusion
   *
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param masterCardEncounterType The Ficha Resumo Encounter Type 53
   * @param stateOfStayOfArtPatient The State of Stay in ART Concept 6273
   * @param abandonedConcept The Abandoned Concept 1707
   * @param stateOfStayOfPreArtPatient The State of Stay in Pre Art Concept 6272
   * @param therapeuticLineConcept The Therapeutic Line Concept 21151
   * @param firstLineConcept The First Line Concept 21150
   * @param arvStartDateConcept The Art Start Date Concept 1190
   * @param justificativeToChangeArvTreatment The Justificative To Change Arv Treatment Concept 1792
   * @param regimenAlternativeToFirstLineConcept he Regimen Alternative To First Line Concept 21190
   * @param pregnantConcept The Pregnant Concept 1982
   * @return {@link String}
   */
  public static String getMQ13AbandonedTarvOnFirstLineDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayOfArtPatient,
      int abandonedConcept,
      int stateOfStayOfPreArtPatient,
      int therapeuticLineConcept,
      int firstLineConcept,
      int arvStartDateConcept,
      int justificativeToChangeArvTreatment,
      int regimenAlternativeToFirstLineConcept,
      int pregnantConcept) {

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("6273", stateOfStayOfArtPatient);
    map.put("1707", abandonedConcept);
    map.put("6272", stateOfStayOfPreArtPatient);
    map.put("21151", therapeuticLineConcept);
    map.put("21150", firstLineConcept);
    map.put("1190", arvStartDateConcept);
    map.put("1792", justificativeToChangeArvTreatment);
    map.put("21190", regimenAlternativeToFirstLineConcept);
    map.put("1982", pregnantConcept);

    String query =
        " SELECT abandoned.patient_id from ( "
            + "                                     SELECT p.patient_id, max(e.encounter_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + getRegimenLineQuery(Linha.FIRST)
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + "                                     WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${6} "
            + "                                       AND o.concept_id = ${6273} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= end_period.last_encounter "
            + "                                       AND e.encounter_datetime <= DATE_ADD(end_period.last_encounter, INTERVAL 6 MONTH) "
            + "                                     GROUP BY p.patient_id "
            + "UNION "
            + "     SELECT p.patient_id, max(o.obs_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + getRegimenLineQuery(Linha.FIRST)
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${53} "
            + "                                       AND o.concept_id = ${6272} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND o.obs_datetime >= end_period.last_encounter "
            + "                                       AND o.obs_datetime <= DATE_ADD(end_period.last_encounter, INTERVAL 6 MONTH)"
            + "                                     GROUP BY p.patient_id "
            + "                                 ) abandoned GROUP BY abandoned.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b> O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período da
   * seguinte forma: </b>
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Clínica durante o período (“Data Consulta”>=”Data Início Período” e “Data
   * Consulta”<=”Data Fim Período”
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Resumo durante o período (“Data de Mudança de Estado Permanência”>=”Data Início
   * Período” e “Data Consulta”<=”Data Fim Período”
   *
   * <p>Nota: O período é definido conforme o requisito onde os utentes abandonos em TARV no fim do
   * período serão excluídos:
   * <li>6. para exclusão nos utentes que estão na 1ª linha de TARV, a “Data Início Período” será
   *     igual a “Data 1a Linha” – 6 meses e “Data Fim do Período” será igual a “Data 1a Linha”.
   *
   *     <p>B1= (BI1 and not B1E) ‘: MUDANCA DE REGIME
   * <li>BI1 - Select all patients who have the most recent “ALTERNATIVA A LINHA - 1a LINHA”
   *     (Concept Id 21190, obs_datetime) recorded in Ficha Resumo (encounter type 53) with any
   *     value coded (not null) during the inclusion period (startDateInclusion and
   *     endDateInclusion) AND
   * <li>B1E - Exclude all patients from Ficha Clinica (encounter type 6, encounter_datetime) who
   *     have “LINHA TERAPEUTICA”(Concept id 21151) with value coded DIFFERENT THAN “PRIMEIRA
   *     LINHA”(Concept id 21150) and encounter_datetime > the most recent “ALTERNATIVA A LINHA - 1a
   *     LINHA” (from B1) and <= endDateInclusion
   *
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param masterCardEncounterType The Ficha Resumo Encounter Type 53
   * @param stateOfStayOfArtPatient The State of Stay in ART Concept 6273
   * @param abandonedConcept The Abandoned Concept 1707
   * @param stateOfStayOfPreArtPatient The State of Stay in Pre Art Concept 6272
   * @param therapeuticLineConcept The Therapeutic Line Concept 21151
   * @param firstLineConcept The First Line Concept 21150
   * @param arvStartDateConcept The Art Start Date Concept 1190
   * @param justificativeToChangeArvTreatment The Justificative To Change Arv Treatment Concept 1792
   * @param regimenAlternativeToFirstLineConcept he Regimen Alternative To First Line Concept 21190
   * @param pregnantConcept The Pregnant Concept 1982
   * @return {@link String}
   */
  public static String getMQ13AbandonedTarvInTheLastSixMonthsFromFirstLineDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayOfArtPatient,
      int abandonedConcept,
      int stateOfStayOfPreArtPatient,
      int therapeuticLineConcept,
      int firstLineConcept,
      int arvStartDateConcept,
      int justificativeToChangeArvTreatment,
      int regimenAlternativeToFirstLineConcept,
      int pregnantConcept) {

    CommonCohortQueries commonCohortQueries =
        new CommonCohortQueries(new HivMetadata(), new TbMetadata());
    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("6273", stateOfStayOfArtPatient);
    map.put("1707", abandonedConcept);
    map.put("6272", stateOfStayOfPreArtPatient);
    map.put("21151", therapeuticLineConcept);
    map.put("21150", firstLineConcept);
    map.put("1190", arvStartDateConcept);
    map.put("1792", justificativeToChangeArvTreatment);
    map.put("21190", regimenAlternativeToFirstLineConcept);
    map.put("1982", pregnantConcept);

    String query =
        " SELECT abandoned.patient_id from ( "
            + "                                     SELECT p.patient_id, max(e.encounter_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + commonCohortQueries.getFirstLineTherapyQuery()
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + "                                     WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${6} "
            + "                                       AND o.concept_id = ${6273} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= DATE_SUB(end_period.last_encounter, INTERVAL 6 MONTH)  "
            + "                                       AND e.encounter_datetime <= end_period.last_encounter "
            + "                                     GROUP BY p.patient_id "
            + "UNION "
            + "     SELECT p.patient_id, max(o.obs_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + commonCohortQueries.getFirstLineTherapyQuery()
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${53} "
            + "                                       AND o.concept_id = ${6272} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND o.obs_datetime >= DATE_SUB(end_period.last_encounter, INTERVAL 6 MONTH)  "
            + "                                       AND o.obs_datetime <= end_period.last_encounter "
            + "                                     GROUP BY p.patient_id "
            + "                                 ) abandoned GROUP BY abandoned.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b> O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período da
   * seguinte forma: </b>
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Clínica durante o período (“Data Consulta”>=”Data Início Período” e “Data
   * Consulta”<=”Data Fim Período”
   *
   * <blockquote>
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Resumo durante o período (“Data de Mudança de Estado Permanência”>=”Data Início
   * Período” e “Data Consulta”<=”Data Fim Período”
   *
   * <p>Nota: O período é definido conforme o requisito onde os utentes abandonos em TARV no fim do
   * período serão excluídos:
   * <li>4. para exclusão nos utentes que iniciaram 2ª linha de TARV, a “Data Início Período” será
   *     igual a “Data 2ª Linha” a “Data Fim do Período” será “Data 2ª Linha”+ 6 meses.
   *
   *     <p>Select all patients who have the REGIME ARV SEGUNDA LINHA (Concept Id 21187, value coded
   *     different NULL) recorded in Ficha Resumo (encounter type 53) and obs_datetime >=
   *     inclusionStartDate and <= inclusionEndDate AND at least for 6 months ( “Last Clinical
   *     Consultation” (last encounter_datetime from B1) minus obs_datetime(from B2) >= 6 months)
   *
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param masterCardEncounterType The Ficha Resumo Encounter Type 53
   * @param stateOfStayOfArtPatient The State of Stay in ART Concept 6273
   * @param abandonedConcept The Abandoned Concept 1707
   * @param stateOfStayOfPreArtPatient The State of Stay in Pre Art Concept 6272
   * @param regArvSecondLineConcept The ARV REGIMEN 2ND LINE 21187
   * @return {@link String}
   */
  public static String getMQ13AbandonedTarvOnSecondLineDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayOfArtPatient,
      int abandonedConcept,
      int stateOfStayOfPreArtPatient,
      int regArvSecondLineConcept) {

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("6273", stateOfStayOfArtPatient);
    map.put("1707", abandonedConcept);
    map.put("6272", stateOfStayOfPreArtPatient);
    map.put("21187", regArvSecondLineConcept);

    String query =
        " SELECT abandoned.patient_id from ( "
            + "                                     SELECT p.patient_id, max(e.encounter_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + getRegimenLineQuery(Linha.SECOND)
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + "                                     WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${6} "
            + "                                       AND o.concept_id = ${6273} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= end_period.last_encounter "
            + "                                       AND e.encounter_datetime <= DATE_ADD(end_period.last_encounter, INTERVAL 6 MONTH) "
            + "                                     GROUP BY p.patient_id "
            + "UNION "
            + "     SELECT p.patient_id, max(o.obs_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + getRegimenLineQuery(Linha.SECOND)
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${53} "
            + "                                       AND o.concept_id = ${6272} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND o.obs_datetime >= end_period.last_encounter "
            + "                                       AND o.obs_datetime <= DATE_ADD(end_period.last_encounter, INTERVAL 6 MONTH)"
            + "                                     GROUP BY p.patient_id "
            + "                                 ) abandoned GROUP BY abandoned.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  enum Linha {
    FIRST,
    SECOND
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <ul>
   *   <p>B1= (BI1 and not B1E) ‘: MUDANCA DE REGIME
   *   <li>BI1 - Select all patients who have the most recent “ALTERNATIVA A LINHA - 1a LINHA”
   *       (Concept Id 21190, obs_datetime) recorded in Ficha Resumo (encounter type 53) with any
   *       value coded (not null) during the inclusion period (startDateInclusion and
   *       endDateInclusion) AND
   *   <li>B1E - Exclude all patients from Ficha Clinica (encounter type 6, encounter_datetime) who
   *       have “LINHA TERAPEUTICA”(Concept id 21151) with value coded DIFFERENT THAN “PRIMEIRA
   *       LINHA”(Concept id 21150) and encounter_datetime > the most recent “ALTERNATIVA A LINHA -
   *       1a LINHA” (from B1) and <= endDateInclusion
   * </ul>
   *
   * <blockquote>
   *
   * <li>Select all patients who have the REGIME ARV SEGUNDA LINHA (Concept Id 21187, value coded
   *     different NULL) recorded in Ficha Resumo (encounter type 53) and obs_datetime >=
   *     inclusionStartDate and <= inclusionEndDate AND at least for 6 months ( “Last Clinical
   *     Consultation” (last encounter_datetime from B1) minus obs_datetime(from B2) >= 6 months)
   */
  public static String getRegimenLineQuery(Linha line) {

    if (line.equals(Linha.FIRST)) {
      return " SELECT patient_id, regime_date AS last_encounter "
          + "FROM   (SELECT p.patient_id, "
          + "               Max(o.obs_datetime) AS regime_date "
          + "        FROM   patient p "
          + "               JOIN encounter e "
          + "                 ON e.patient_id = p.patient_id "
          + "               JOIN obs o "
          + "                 ON o.encounter_id = e.encounter_id "
          + "               JOIN obs o2 "
          + "                 ON o2.encounter_id = e.encounter_id "
          + "        WHERE  e.encounter_type = ${53} "
          + "               AND o.concept_id = ${21190} "
          + "               AND o.value_coded IS NOT NULL "
          + "               AND e.location_id = :location "
          + "               AND ( "
          + "                      (o2.concept_id = ${1792} AND o2.value_coded <> ${1982})"
          + "                       OR "
          + "                      (o2.concept_id = ${1792} AND o2.value_coded IS NULL) "
          + "                       OR "
          + "                      ( "
          + "                       NOT EXISTS ( "
          + "                               SELECT * FROM obs oo "
          + "                               WHERE oo.voided = 0 "
          + "                               AND oo.encounter_id = e.encounter_id "
          + "                               AND oo.concept_id = ${1792} "
          + "                           ) "
          + "                     ) "
          + "                    ) "
          + "               AND e.voided = 0 "
          + "               AND p.voided = 0 "
          + "               AND o.voided = 0 "
          + "               AND o2.voided = 0"
          + "               AND o.obs_datetime BETWEEN :startDate AND :endDate "
          + "        GROUP  BY p.patient_id) bI1 "
          + "WHERE  bI1.patient_id NOT IN (SELECT p.patient_id "
          + "                              FROM   patient p "
          + "                                     JOIN encounter e "
          + "                                       ON e.patient_id = p.patient_id "
          + "                                     JOIN obs o "
          + "                                       ON o.encounter_id = e.encounter_id "
          + "                              WHERE  e.encounter_type = ${6} "
          + "                                     AND o.concept_id = ${21151} AND o.value_coded <> ${21150} "
          + "                                     AND e.location_id = :location "
          + "                                     AND e.voided = 0 "
          + "                                     AND p.voided = 0 "
          + "                                     AND e.encounter_datetime > bI1.regime_date "
          + "                                     AND e.encounter_datetime <= :revisionEndDate)";
    } else {
      return " SELECT     p.patient_id , o.obs_datetime  AS last_encounter "
          + "                                   FROM       patient p "
          + "                                   INNER JOIN encounter e "
          + "                                   ON         e.patient_id = p.patient_id "
          + "                                   INNER JOIN obs o "
          + "                                   ON         o.encounter_id = e.encounter_id "
          + "                                   INNER JOIN "
          + "                                              ( "
          + "                                                         SELECT     p.patient_id, "
          + "                                                                    Max(e.encounter_datetime) last_visit "
          + "                                                         FROM       patient p "
          + "                                                         INNER JOIN encounter e "
          + "                                                         ON         e.patient_id = p.patient_id "
          + "                                                         WHERE      p.voided = 0 "
          + "                                                         AND        e.voided = 0 "
          + "                                                         AND        e.encounter_type = ${6} "
          + "                                                         AND        e.location_id = :location "
          + "                                                         AND        e.encounter_datetime BETWEEN :startDate AND :revisionEndDate "
          + "                                                         GROUP BY   p.patient_id) AS last_clinical "
          + "                                   ON         last_clinical.patient_id = p.patient_id "
          + "                                   WHERE      e.voided = 0 "
          + "                                   AND        p.voided = 0 "
          + "                                   AND        o.voided = 0 "
          + "                                   AND        e.encounter_type = ${53} "
          + "                                   AND        e.location_id = :location "
          + "                                   AND        o.concept_id = ${21187} "
          + "                                   AND        o.value_coded IS NOT NULL "
          + "                                   AND        o.obs_datetime >= :startDate "
          + "                                   AND        o.obs_datetime <= :revisionEndDate "
          + "                                   AND        timestampdiff(month, o.obs_datetime, last_clinical.last_visit) >= 6 ";
    }
  }

  /**
   * <b>RF14</b>: Select all patients who restarted ART for at least 6 months following: all
   * patients who have “Mudança de Estado de Permanência TARV”=”Reinício” na Ficha Clínica durante o
   * período de inclusão (“Data Consulta Reinício TARV” >= “Data Início Inclusão” e <= “Data Fim
   * Inclusão”), where “Data Última Consulta” durante o período de revisão, menos (-) “Data Consulta
   * Reinício TARV” maior ou igual (>=) a 6 meses
   *
   * @return {@link String}
   */
  public static String getRestartedArtQuery() {
    return " SELECT patient_id, "
        + "       the_time AS last_encounter "
        + "FROM   (SELECT p.patient_id, "
        + "               e.encounter_datetime AS the_time, "
        + "               last_consultation.last_consultation_date "
        + "        FROM   patient p "
        + "                   INNER JOIN encounter e "
        + "                              ON p.patient_id = e.patient_id "
        + "                   INNER JOIN obs o "
        + "                              ON e.encounter_id = o.encounter_id "
        + "                   INNER JOIN (SELECT p.patient_id, "
        + "                                      Max(e.encounter_datetime) AS "
        + "                                          last_consultation_date "
        + "                               FROM   patient p "
        + "                                          INNER JOIN encounter e "
        + "                                                     ON e.patient_id = p.patient_id "
        + "                                          INNER JOIN obs o "
        + "                                                     ON o.encounter_id = e.encounter_id "
        + "                               WHERE  e.encounter_type IN( ${6}, ${9} ) "
        + "                                 AND e.encounter_datetime <= :endDate "
        + "                                 AND e.location_id = :location "
        + "                                 AND e.voided = 0 "
        + "                                 AND p.voided = 0 "
        + "                                 AND o.voided = 0 "
        + "                               GROUP  BY p.patient_id) last_consultation "
        + "                              ON last_consultation.patient_id = p.patient_id "
        + "        WHERE  p.voided = 0 "
        + "          AND e.voided = 0 "
        + "          AND o.voided = 0 "
        + "          AND e.encounter_type = ${6} "
        + "          AND o.concept_id = ${6273} "
        + "          AND o.value_coded = ${1705} "
        + "          AND e.encounter_datetime >= :startDate "
        + "          AND e.encounter_datetime <= :endDate "
        + "          AND e.location_id = :location "
        + "        GROUP  BY p.patient_id) restarted "
        + "WHERE  Timestampdiff(month, restarted.last_consultation_date, "
        + "                     restarted.the_time) >= 6";
  }

  /**
   * Todos os utentes com registo de um resultado de Carga Viral >= 1000 cps/ml na Ficha Clínica
   * durante o período compreendido entre “Data Fim Revisão” - 26 meses + 1 dia e “Data Fim Revisão”
   * - On RF17.
   */
  public static CohortDefinition getPatientsWithVlGreaterThen1000() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patient with VL >= 1000");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    HivMetadata hivMetadata = new HivMetadata();
    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        "   SELECT p.patient_id "
            + "  FROM patient p  "
            + "  INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "  INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "  WHERE  p.voided = 0   "
            + "         AND e.voided = 0   "
            + "         AND o.voided = 0     "
            + "         AND o.concept_id = ${856}   "
            + "         AND o.value_numeric >= 1000  "
            + "         AND e.encounter_type = ${6} "
            + "         AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "         AND e.location_id = :location   "
            + "         GROUP  BY p.patient_id    ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  private static String getMetadataFrom(List<Integer> dispensationTypes) {
    if (dispensationTypes == null || dispensationTypes.isEmpty()) {
      throw new RuntimeException("The list of encounters or concepts might not be empty ");
    }
    return StringUtils.join(dispensationTypes, ",");
  }

  /**
   * <<<<<<< HEAD os utentes com registo de último levantamento na farmácia (FILA) há 24 meses
   * (última “Data Levantamento”>= “Data Fim Revisão” – 26 meses+1dia e <= “Data Fim Revisão” – 24
   * meses) com próximo levantamento agendado para 83 a 97 dias ( “Data Próximo Levantamento” menos
   * “Data Levantamento”>= 83 dias e <= 97 dias)
   *
   * <p>os utentes com registo de último levantamento na farmácia (FILA) há 24 meses (última “Data
   * Levantamento”>= “Data Fim Revisão” – 26 meses+1dia e <= “Data Fim Revisão” – 24 meses) com
   * próximo levantamento agendado para 173 a 187 dias ( “Data Próximo Levantamento” menos “Data
   * Levantamento”>= 173 dias e <= 187 dias).
   *
   * @param lowerBounded
   * @param upperBounded
   * @return SqlCohortDefinition
   */
  public static SqlCohortDefinition getPatientsWithPickupOnFilaBasedOnLastVl12Months(
      int lowerBounded, int upperBounded) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients who have pickup registered on FILA based on last VL 12 months period)");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    map.put("lower", lowerBounded);
    map.put("upper", upperBounded);
    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        " SELECT     p.patient_id "
            + " FROM       patient p "
            + " INNER JOIN encounter e ON         e.patient_id = p.patient_id "
            + " INNER JOIN obs o ON         o.encounter_id = e.encounter_id "
            + " INNER JOIN  ( SELECT p.patient_id, MAX(e.encounter_datetime) consultation_date "
            + "               FROM       patient p "
            + "               INNER JOIN encounter e ON  e.patient_id = p.patient_id "
            + "               INNER JOIN obs os ON e.encounter_id = os.encounter_id "
            + "               INNER JOIN (SELECT   patient_id, DATE( MAX(encounter_date) ) AS vl_max_date "
            + "                           FROM (SELECT  p.patient_id, DATE(e.encounter_datetime) AS encounter_date "
            + "                                 FROM       patient p "
            + "                                 INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "                                 INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "                                 WHERE      p.voided=0 "
            + "                                 AND        e.voided=0 "
            + "                                 AND        o.voided=0 "
            + "                                 AND        e.encounter_type IN (${13},${6},${9}, ${51}) "
            + "                                 AND        (( o.concept_id=${856} AND o.value_numeric IS NOT NULL) OR ( o.concept_id=${1305} AND o.value_coded IS NOT NULL)) "
            + "                                 AND        DATE(e.encounter_datetime) BETWEEN :startDate AND :endDate "
            + "                                 AND        e.location_id=:location "
            + "                             UNION "
            + "                             SELECT     p.patient_id, DATE(o.obs_datetime) AS encounter_date "
            + "                             FROM       patient p "
            + "                             INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "                             INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "                             WHERE      p.voided=0 "
            + "                             AND        e.voided=0 "
            + "                             AND        o.voided=0 "
            + "                             AND        e.encounter_type IN (${53}) "
            + "                             AND        o.concept_id=${856} "
            + "                             AND        o.value_numeric IS NOT NULL "
            + "                             AND        DATE(o.obs_datetime) BETWEEN :startDate AND :endDate "
            + "                             AND        e.location_id=:location ) max_vl_date "
            + "                             GROUP BY patient_id )vl "
            + "             ON  vl.patient_id=p.patient_id "
            + "             WHERE e.encounter_type = ${18} "
            + "             AND os.concept_id = ${5096} "
            + "             AND e.location_id = :location "
            + "             AND e.voided = 0 "
            + "             AND p.voided = 0 "
            + "             AND os.voided = 0 "
            + "             AND e.encounter_datetime < vl.vl_max_date "
            + "             GROUP BY   p.patient_id ) recent_clinical "
            + " ON  recent_clinical.patient_id = p.patient_id "
            + " WHERE e.encounter_type = ${18} "
            + " AND        o.concept_id = ${5096} "
            + " AND        DATEDIFF(o.value_datetime, e.encounter_datetime) >= ${lower} "
            + " AND        DATEDIFF(o.value_datetime, e.encounter_datetime) <= ${upper} "
            + " AND        e.encounter_datetime = recent_clinical.consultation_date "
            + " AND        e.location_id = :location "
            + " AND        p.voided = 0 "
            + " AND        e.voided = 0 "
            + " AND        o.voided = 0 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }
  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * B2 NEW - B2New - Select all patients with “LINHA TERAPEUTICA” (Concept Id 21151) equal to
   * “PRIMEIRA LINHA” (concept id 21150) recorded in the Last Clinical Consultation (encounter type
   * 6, encounter_datetime) occurred during the period (encounter_datetime >= startDateInclusion and
   * <= endDateRevision) and Last Clinical Consultation Date (encounter_datetime) minus “Patient ART
   * Start Date” (Concept Id 1190, value_datetime) recorded in Ficha Resumo (encounter type 53,
   * encounter_datetime) >= 6 months)
   *
   * <blockquote>
   */
  public static String getFirstLineTherapyQuery() {

    return " SELECT "
        + "     pa.patient_id, first_line.encounter_datetime AS last_encounter "
        + " FROM "
        + "     patient pa "
        + "          JOIN "
        + "     encounter enc ON enc.patient_id = pa.patient_id "
        + "          JOIN "
        + "     obs ob ON ob.encounter_id = enc.encounter_id "
        + "          JOIN "
        + "      (SELECT "
        + "          p.patient_id, filtered.encounter_datetime "
        + "      FROM "
        + "          patient p "
        + "      JOIN encounter e ON e.patient_id = p.patient_id "
        + "      JOIN obs o ON o.encounter_id = e.encounter_id "
        + "      INNER JOIN (SELECT "
        + "          p.patient_id, "
        + "              MAX(e.encounter_datetime) AS encounter_datetime "
        + "      FROM "
        + "          patient p "
        + "     INNER JOIN encounter e ON e.patient_id = p.patient_id "
        + "      WHERE "
        + "          e.encounter_type = ${6} AND p.voided = 0 "
        + "              AND e.voided = 0 "
        + "             AND e.location_id = :location "
        + "              AND e.encounter_datetime BETWEEN :startDate AND :revisionEndDate "
        + "      GROUP BY p.patient_id) filtered ON filtered.patient_id = p.patient_id "
        + "      WHERE "
        + "          e.encounter_datetime = filtered.encounter_datetime "
        + "              AND e.location_id = :location "
        + "              AND o.concept_id = ${21151} "
        + "              AND e.voided = 0 "
        + "              AND o.voided = 0 "
        + "              AND o.value_coded = ${21150} "
        + "              AND e.encounter_type = ${6}) first_line ON first_line.patient_id = pa.patient_id "
        + "          INNER JOIN "
        + "      (SELECT "
        + "          p.patient_id, MAX(e.encounter_datetime) last_visit "
        + "      FROM "
        + "          patient p "
        + "      INNER JOIN encounter e ON e.patient_id = p.patient_id "
        + "      WHERE "
        + "          p.voided = 0 AND e.voided = 0 "
        + "              AND e.encounter_type = ${6} "
        + "              AND e.location_id = :location "
        + "              AND e.encounter_datetime BETWEEN :startDate AND :revisionEndDate "
        + "      GROUP BY p.patient_id) AS last_clinical ON last_clinical.patient_id = pa.patient_id "
        + "          INNER JOIN "
        + "      (SELECT "
        + "          p.patient_id, o.value_datetime AS arv_date "
        + "      FROM "
        + "          patient p "
        + "      INNER JOIN encounter e ON e.patient_id = p.patient_id "
        + "      INNER JOIN obs o ON o.encounter_id = e.encounter_id "
        + "      WHERE "
        + "          o.concept_id = ${1190} "
        + "              AND e.encounter_type = ${53} "
        + "              AND e.location_id = :location "
        + "              AND p.voided = 0 "
        + "              AND e.voided = 0 "
        + "              AND o.voided = 0) arv_start_date ON arv_start_date.patient_id = pa.patient_id "
        + "          AND DATE(arv_start_date.arv_date) <= DATE_SUB(last_clinical.last_visit, INTERVAL 6 MONTH) "
        + " GROUP BY pa.patient_id ";
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <ul>
   *   <li>incluindo todos os utentes do sexo feminino que tiveram a primeira consulta clínica com
   *       registo de grávida durante o período de inclusão (“Data 1ª Consulta Grávida” >= “Data
   *       Início Inclusão” e <= “Data Fim Inclusão”) e três meses após o início TARV (“Data 1ª
   *       Consulta Grávida”> “Data do Início TARV” + 3 meses).
   *   <li>filtrando os utentes que têm o registo de “Pedido de Investigações Laboratoriais” igual a
   *       “Carga Viral” na primeira consulta clínica com registo de grávida durante o período de
   *       inclusão (“Data 1ª Consulta Grávida”).
   *       <p>Nota 1: “Data 1ª Consulta Grávida” deve ser a primeira consulta de sempre com registo
   *       de grávida e essa consulta deve ter ocorrido no período de inclusão. Nota 2: “Data do
   *       Início TARV” é a data definida no RF5.
   * </ul>
   *
   * <blockquote>
   */
  public static String getPregnancyDuringPeriod() {
    return "       SELECT patient_id, first_gestante "
        + " FROM ("
        + "         SELECT p.patient_id, MIN(e.encounter_datetime) AS first_gestante "
        + "         FROM  patient p "
        + "               INNER JOIN person per on p.patient_id=per.person_id "
        + "               INNER JOIN encounter e ON e.patient_id = p.patient_id "
        + "               INNER JOIN obs o ON e.encounter_id = o.encounter_id "
        + "               INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
        + "         WHERE p.voided = 0 "
        + "           AND per.voided=0 AND per.gender = 'F' "
        + "           AND e.voided = 0 AND o.voided  = 0 "
        + "           AND o2.voided  = 0 "
        + "           AND e.encounter_type = ${6} "
        + "           AND o.concept_id = ${1982} "
        + "           AND o.value_coded = ${1065} "
        + "           AND o2.concept_id = ${23722} "
        + "           AND o2.value_coded = ${856} "
        + "           AND e.location_id = :location "
        + "         GROUP BY p.patient_id) gest  "
        + " WHERE gest.first_gestante >= :startDate "
        + "   AND gest.first_gestante <= :endDate "
        + "   AND gest.first_gestante > DATE_ADD((SELECT MIN(o.value_datetime) as art_date "
        + "                                       FROM encounter e "
        + "                                           INNER JOIN obs o ON e.encounter_id = o.encounter_id "
        + "                                       WHERE gest.patient_id = e.patient_id "
        + "                                         AND e.voided = 0 AND o.voided = 0 "
        + "                                         AND e.encounter_type = ${53} AND o.concept_id = ${1190} "
        + "                                         AND o.value_datetime IS NOT NULL AND o.value_datetime <= :endDate AND e.location_id = :location "
        + "                                       LIMIT 1), interval 3 MONTH) ";
  }

  public static SqlCohortDefinition getDisclosureOfHIVDiagnosisToChildrenAdolescents() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "utentes com registo de revelação total do diagnóstico no primeiro ano de TARV");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();

    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put(
        "6340",
        hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept().getConceptId());
    map.put("6337", hivMetadata.getRevealdConcept().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT     art.patient_id "
            + "FROM       (        SELECT     p.patient_id, Min(o.value_datetime) art_date "
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                      WHERE      p.voided = 0 "
            + "                      AND        e.voided = 0 "
            + "                      AND        o.voided = 0 "
            + "                      AND        e.encounter_type = ${53} "
            + "                      AND        o.concept_id = ${1190} "
            + "                      AND        o.value_datetime IS NOT NULL "
            + "                      AND        o.value_datetime <= :endDate "
            + "                      AND        e.location_id = :location "
            + "                      GROUP BY   p.patient_id ) art "
            + "INNER JOIN (          SELECT     p.patient_id, Min(e.encounter_datetime) diagnostic_date "
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e  ON e.patient_id = p.patient_id "
            + "                      INNER JOIN obs o  ON o.encounter_id = e.encounter_id "
            + "                      WHERE      p.voided = 0 "
            + "                      AND        e.voided = 0 "
            + "                      AND        o.voided = 0 "
            + "                      AND        o.concept_id = ${6340} "
            + "                      AND        o.value_coded = ${6337} "
            + "                      AND        e.encounter_type = ${35} "
            + "                      AND        e.location_id = :location "
            + "                      AND        e.encounter_datetime <= :revisionEndDate "
            + "                      GROUP BY   p.patient_id ) revelacao ON revelacao.patient_id = art.patient_id "
            + "WHERE      art.art_date BETWEEN :startDate AND :endDate "
            + "AND        revelacao.diagnostic_date BETWEEN art.art_date AND date_add(art.art_date, INTERVAL 12 month) "
            + "GROUP BY   art.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }
}
