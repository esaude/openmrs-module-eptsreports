package org.openmrs.module.eptsreports.reporting.library.queries;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class IntensiveMonitoringQueries {

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
   * <li>registados no formulário “Ficha de Resumo” com a “Data do Início TARV” decorrida no mês de
   *     avaliação (>= “Data Início Avaliação” e <= “Data Fim Avaliação”).
   *
   *     <p>Nota: “Data do Início TARV” é a data início TARV registada na “Ficha Resumo”. Caso
   *     exista o registo de mais que uma “Ficha Resumo” deve-se considerar a data de início TARV
   *     mais antiga.
   *
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param masterCardEncounterType The Ficha Resumo Encounter Type 53
   * @param stateOfStayOfArtPatient The State of Stay in ART Concept 6273
   * @param abandonedConcept The Abandoned Concept 1707
   * @param stateOfStayOfPreArtPatient The State of Stay in Pre Art Concept 6272
   * @param arvStartDateConcept The ARV Start Date Concept
   * @return {@link String}
   */
  public static String getMI13AbandonedTarvOnArtStartDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayOfArtPatient,
      int abandonedConcept,
      int stateOfStayOfPreArtPatient,
      int arvStartDateConcept) {

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("6273", stateOfStayOfArtPatient);
    map.put("1707", abandonedConcept);
    map.put("6272", stateOfStayOfPreArtPatient);
    map.put("1190", arvStartDateConcept);

    String query =
        " SELECT abandoned.patient_id from ( "
            + "                                     SELECT p.patient_id, max(e.encounter_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + getArtStartDateOnFichaResumoQuery()
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + "                                     WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${6} "
            + "                                       AND o.concept_id = ${6273} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= end_period.first_pickup "
            + "                                       AND e.encounter_datetime <= DATE_ADD(end_period.first_pickup, INTERVAL 6 MONTH) "
            + "                                     GROUP BY p.patient_id "
            + "UNION "
            + "     SELECT p.patient_id, max(o.obs_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + getArtStartDateOnFichaResumoQuery()
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${53} "
            + "                                       AND o.concept_id = ${6272} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND o.obs_datetime >= end_period.first_pickup "
            + "                                       AND o.obs_datetime <= DATE_ADD(end_period.first_pickup, INTERVAL 6 MONTH)"
            + "                                     GROUP BY p.patient_id "
            + "                                 ) abandoned GROUP BY abandoned.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * *
   *
   * <p>A2-All patients who have the first historical start drugs date (earliest concept ID 1190) *
   * set in FICHA RESUMO (Encounter Type 53) earliest “historical start date” Encounter Type Ids = *
   * 53 The earliest “Historical Start Date” (Concept Id 1190)And historical start *
   * date(Value_datetime) <=EndDate And the earliest date from A1 and A2 (identified as Patient ART
   * * Start Date) is >= startDateRevision and <=endDateInclusion
   *
   * @return {@link String}
   */
  public static String getArtStartDateOnFichaResumoQuery() {
    return " SELECT patient_id, art_date AS first_pickup "
        + "        FROM   (SELECT p.patient_id, Min(value_datetime) art_date "
        + "                    FROM patient p "
        + "              INNER JOIN encounter e "
        + "                  ON p.patient_id = e.patient_id "
        + "              INNER JOIN obs o "
        + "                  ON e.encounter_id = o.encounter_id "
        + "          WHERE  p.voided = 0 "
        + "              AND e.voided = 0 "
        + "              AND o.voided = 0 "
        + "              AND e.encounter_type = ${53} "
        + "              AND o.concept_id = ${1190} "
        + "              AND o.value_datetime IS NOT NULL "
        + "              AND o.value_datetime <= :endDate "
        + "              AND e.location_id = :location "
        + "          GROUP  BY p.patient_id  )  "
        + "               union_tbl  "
        + "        WHERE  union_tbl.art_date BETWEEN :startDate AND :endDate";
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
   * <li>1. para exclusão nas mulheres grávidas que iniciaram TARV a “Data Início Período” será
   *     igual a “Data 1ª Consulta Grávida” – 3 meses” e “Data Fim do Período” será igual a e “Data
   *     1ª Consulta Grávida”).
   *
   *     <p>Select all female patients with first clinical consultation (encounter type 6) that have
   *     the concept “GESTANTE” (Concept Id 1982) and value coded “SIM” (Concept Id 1065) registered
   *     <=endDate)
   *
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param masterCardEncounterType The Ficha Resumo Encounter Type 53
   * @param stateOfStayOfArtPatient The State of Stay in ART Concept 6273
   * @param abandonedConcept The Abandoned Concept 1707
   * @param stateOfStayOfPreArtPatient The State of Stay in Pre Art Concept 6272
   * @param patientFoundYesConcept The Yes Concept 1065
   * @param pregnantConcept The Pregnant Concept 1982
   * @return {@link String}
   */
  public static String getMI13AbandonedTarvOnFirstPregnancyStateDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayOfArtPatient,
      int abandonedConcept,
      int stateOfStayOfPreArtPatient,
      int patientFoundYesConcept,
      int pregnantConcept) {

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("53", masterCardEncounterType);
    map.put("6273", stateOfStayOfArtPatient);
    map.put("1707", abandonedConcept);
    map.put("6272", stateOfStayOfPreArtPatient);
    map.put("1065", patientFoundYesConcept);
    map.put("1982", pregnantConcept);

    String query =
        " SELECT abandoned.patient_id from ( "
            + "                                     SELECT p.patient_id, max(e.encounter_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + getFemaleFirstPregnancyStateQuery()
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + "                                     WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${6} "
            + "                                       AND o.concept_id = ${6273} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= DATE_SUB(end_period.first_gestante, INTERVAL 3 MONTH) "
            + "                                       AND e.encounter_datetime <= end_period.first_gestante "
            + "                                     GROUP BY p.patient_id "
            + "UNION "
            + "     SELECT p.patient_id, max(o.obs_datetime) as last_encounter FROM patient p "
            + "                                                                                                               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                                               INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                                                                                                               INNER JOIN ( "
            + getFemaleFirstPregnancyStateQuery()
            + " ) end_period ON end_period.patient_id = p.patient_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.encounter_type = ${53} "
            + "                                       AND o.concept_id = ${6272} "
            + "                                       AND o.value_coded = ${1707} "
            + "                                       AND e.location_id = :location "
            + "       AND o.obs_datetime >= DATE_SUB(end_period.first_gestante, INTERVAL 3 MONTH) "
            + "                                       AND o.obs_datetime <= end_period.first_gestante "
            + "                                     GROUP BY p.patient_id "
            + "                                 ) abandoned GROUP BY abandoned.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * *
   *
   * <p>B2 - Select all female patients with first clinical consultation (encounter type 6) that
   * have the concept “GESTANTE” (Concept Id 1982) and value coded “SIM” (Concept Id 1065)
   * registered (1)during the inclusion period (first occurrence, encounter_datetime >= startDate
   * and <=endDate)
   *
   * @return {@link String}
   */
  public static String getFemaleFirstPregnancyStateQuery() {
    return " SELECT p.patient_id, MIN(e.encounter_datetime) AS first_gestante "
        + "         FROM  patient p "
        + "               INNER JOIN person per on p.patient_id=per.person_id "
        + "               INNER JOIN encounter e ON e.patient_id = p.patient_id "
        + "               INNER JOIN obs o ON e.encounter_id = o.encounter_id "
        + "         WHERE p.voided = 0 "
        + "           AND per.voided=0 AND per.gender = 'F' "
        + "           AND e.voided = 0 AND o.voided  = 0 "
        + "           AND e.encounter_type = ${6} "
        + "           AND o.concept_id = ${1982} "
        + "           AND o.value_coded = ${1065} "
        + "           AND e.location_id = :location "
        + "           AND e.encounter_datetime >= :startDate "
        + "           AND e.encounter_datetime <= :endDate"
        + "         GROUP BY p.patient_id ";
  }

  /**
   * <p>Select all patients with S.TARV: ADULTO SEGUIMENTO (ID=6) that have Resultado Carga Viral
   * Quantitativo (Concept ID = 856) or Resultado Carga Viral Qualitativo (Concept ID = 1305) and
   * Value_numeric = any not empty for concept Id 856 (Resultado Carga Viral Quantitativo) or
   * Value_coded = any not empty for concept Id 1305 (Resultado Carga Viral Qualitativo)
   *
   * <li>Nota: Se existir o registo de mais do que uma consulta clínica com registo de resultado de
   *     CV durante o mês de avaliação deve ser considerada a primeira consulta clínica com o
   *     registo de resultado de CV durante o mês de avaliação.
   *
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param hivViralLoadConcept The Hiv Viral Load Concept 856
   * @param hivViralLoadQualitative The Hiv Viral Load Qualitative Concept 1305
   * @return {@link String}
   */
  public static String getViralLoadResultQuery(
      int adultoSeguimentoEncounterType, int hivViralLoadConcept, int hivViralLoadQualitative) {

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("856", hivViralLoadConcept);
    map.put("1305", hivViralLoadQualitative);

    String query =
        " SELECT patient_id FROM ( "
            + "                           SELECT vl.patient_id, MIN(vl.first_occurrence) as first_vl_result "
            + "                           FROM  ( "
            + "                                     SELECT p.patient_id, MIN(e.encounter_datetime) AS first_occurrence "
            + "                                     FROM   patient p "
            + "                                                INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                                INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                     WHERE p.voided = 0 "
            + "                                       AND e.voided = 0 "
            + "                                       AND o.voided = 0 "
            + "                                       AND e.location_id = :location "
            + "                                       AND e.encounter_type = ${6} "
            + "                                       AND o.concept_id = ${856} "
            + "                                       AND o.value_numeric IS NOT NULL "
            + "                                       AND e.encounter_datetime >= :startDate "
            + "                                       AND e.encounter_datetime <= :endDate "
            + "                                     GROUP  BY p.patient_id "
            + "                                     UNION "
            + "                                     SELECT p.patient_id, MIN(e.encounter_datetime) AS first_occurrence "
            + "                                     FROM   patient p "
            + "                                                INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                                INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                     WHERE p.voided = 0 "
            + "                                       AND e.voided = 0 "
            + "                                       AND o.voided = 0 "
            + "                                       AND e.location_id = :location "
            + "                                       AND e.encounter_type = ${6} "
            + "                                       AND o.concept_id = ${1305} "
            + "                                       AND o.value_coded IS NOT NULL "
            + "                                       AND e.encounter_datetime >= :startDate "
            + "                                       AND e.encounter_datetime <= :endDate "
            + "                                     GROUP  BY p.patient_id "
            + "                                 ) vl "
            + "                           GROUP BY vl.patient_id) vl_result ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * Filtrando os utentes com o registo de pedido de CV (“Pedido de Investigações Laboratoriais”) na
   * Ficha Clínica imediatamente anterior ao registo do resultado de CV durante o período de
   * avaliação (< “Data Resultado CV”) e sendo este pedido efectuado em 33 dias, ou seja, “Data
   * Resultado CV” menos “Pedido CV Anterior” <= 33 dias)
   *
   * <p>select all patients with S.TARV: ADULTO SEGUIMENTO (ID=6) that have Pedido de Investigações
   * Laboratoriais (Concept ID = 23722) Data de Consulta (encounter.encounter_datetime) and
   * Value_coded = “Carga Viral” (concept id 856) for concept Id 23722 (Pedido de Investigações
   * Laboratoriais) and Max (Encounter_datetime ) as ”Pedido CV Anterior” < “Data Resultado CV” And
   * “Data Resultado CV” menos “Pedido CV Anterior” <= 33 dias
   * <li>Nota: “Data Resultado CV” encontra-se definido no Denominador (RF32- Categoria 13 MG
   *     Indicador 13.17 – Denominador Resultado CV)
   *
   * @see #getViralLoadResultQuery(int, int, int)
   * @param adultoSeguimentoEncounterType The Adulto Seguimento Encounter Type 6
   * @param hivViralLoadConcept The Hiv Viral Load Concept 856
   * @param hivViralLoadQualitative The Hiv Viral Load Qualitative Concept 1305
   * @param applicationForLaboratoryResearch The Application for Laboratory Research 23722
   * @return {@link String}
   */
  public static String getPreviousViralLoadQuery(
      int adultoSeguimentoEncounterType,
      int hivViralLoadConcept,
      int hivViralLoadQualitative,
      int applicationForLaboratoryResearch) {

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("856", hivViralLoadConcept);
    map.put("1305", hivViralLoadQualitative);
    map.put("23722", applicationForLaboratoryResearch);

    String query =
        "SELECT patient_id FROM ( SELECT p.patient_id, MAX(e.encounter_datetime) as previous_vl "
            + "FROM       patient p "
            + "               INNER JOIN encounter e "
            + "                          ON         p.patient_id = e.patient_id "
            + "               INNER JOIN obs o "
            + "                          ON         o.encounter_id=e.encounter_id "
            + "               INNER JOIN "
            + "           ( "
            + "               SELECT vl.patient_id, MIN(vl.first_occurrence) as first_vl_result "
            + "               FROM  ( "
            + "                         SELECT p.patient_id, MIN(e.encounter_datetime) AS first_occurrence "
            + "                         FROM   patient p "
            + "                                    INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                    INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                         WHERE p.voided = 0 "
            + "                           AND e.voided = 0 "
            + "                           AND o.voided = 0 "
            + "                           AND e.location_id = :location "
            + "                           AND e.encounter_type = ${6} "
            + "                           AND o.concept_id = ${856} "
            + "                           AND o.value_numeric IS NOT NULL "
            + "                           AND e.encounter_datetime >= :startDate "
            + "                           AND e.encounter_datetime <= :endDate "
            + "                         GROUP  BY p.patient_id "
            + "                         UNION "
            + "                         SELECT p.patient_id, MIN(e.encounter_datetime) AS first_occurrence "
            + "                         FROM   patient p "
            + "                                    INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                    INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                         WHERE p.voided = 0 "
            + "                           AND e.voided = 0 "
            + "                           AND o.voided = 0 "
            + "                           AND e.location_id = :location "
            + "                           AND e.encounter_type = ${6} "
            + "                           AND o.concept_id = ${1305} "
            + "                           AND o.value_coded IS NOT NULL "
            + "                           AND e.encounter_datetime >= :startDate "
            + "                           AND e.encounter_datetime <= :endDate "
            + "                         GROUP  BY p.patient_id "
            + "                     ) vl "
            + "               GROUP BY vl.patient_id "
            + "               ) vl_result "
            + "           ON         p.patient_id = vl_result.patient_id "
            + "WHERE      p.voided = 0 "
            + "  AND        e.voided = 0 "
            + "  AND        o.voided = 0 "
            + "  AND        e.location_id = :location "
            + "  AND        e.encounter_type = ${6} "
            + "  AND        o.concept_id = ${23722} "
            + "  AND        o.value_coded = ${856} "
            + "  AND        e.encounter_datetime < vl_result.first_vl_result "
            + "  AND        TIMESTAMPDIFF(DAY, e.encounter_datetime, vl_result.first_vl_result) >= 33 "
            + "GROUP BY   p.patient_id ) previous_consultation ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }
}
