package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.data.definition.CalculationDataDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DQACargaViralCohortQueries {

  private HivMetadata hivMetadata;
  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Autowired
  public DQACargaViralCohortQueries(
      ResumoMensalCohortQueries resumoMensalCohortQueries, HivMetadata hivMetadata) {
    this.hivMetadata = hivMetadata;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
  }

  public CohortDefinition getBaseCohort() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String mapping1 = "startDate=${startDate},endDate=${startDate+1m-1d},location=${location}";
    String mapping2 = "startDate=${startDate+1m},endDate=${startDate+2m-1d},location=${location}";
    String mapping3 = "startDate=${startDate+2m},endDate=${endDate},location=${location}";

    CohortDefinition E2 =
        resumoMensalCohortQueries
            .getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults();

    compositionCohortDefinition.addSearch("L1", EptsReportUtils.map(E2, mapping1));
    compositionCohortDefinition.addSearch("L2", EptsReportUtils.map(E2, mapping2));
    compositionCohortDefinition.addSearch("L3", EptsReportUtils.map(E2, mapping3));

    compositionCohortDefinition.setCompositionString("L1 OR L2 OR L3");

    return compositionCohortDefinition;
  }

  public DataDefinition getNID(int identifierType) {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.setName("NID");

    String sql =
        " SELECT p.patient_id,pi.identifier  FROM patient p INNER JOIN patient_identifier pi ON p.patient_id=pi.patient_id "
            + " INNER JOIN patient_identifier_type pit ON pit.patient_identifier_type_id=pi.identifier_type "
            + " WHERE p.voided=0 AND pi.voided=0 AND pit.retired=0 AND pit.patient_identifier_type_id ="
            + identifierType;

    spdd.setQuery(sql);
    return spdd;
  }

  public DataDefinition getArtStartDate() {
    CalculationDataDefinition cd =
        new CalculationDataDefinition(
            "Art start date",
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
    return cd;
  }

  public DataDefinition getAge(String calculateAgeOn) {

    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();

    if (calculateAgeOn.equals("startDate")) {
      spdd.setName("Patient Age at reporting startDate");
      spdd.addParameter(new Parameter("startDate", "startDate", Date.class));
    } else if (calculateAgeOn.equals("endDate")) {
      spdd.setName("Patient Age at reporting endDate");
      spdd.addParameter(new Parameter("endDate", "endDate", Date.class));
    } else {
      spdd.setName("Patient Age at reporting evaluation Date");
      spdd.addParameter(new Parameter("evaluationDate", "evaluationDate", Date.class));
    }

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql = "";
    if (calculateAgeOn.equals("startDate")) {
      sql += " SELECT p.patient_id, FLOOR(DATEDIFF(:startDate,ps.birthdate)/365) AS age ";
    } else if (calculateAgeOn.equals("endDate")) {
      sql += " SELECT p.patient_id, FLOOR(DATEDIFF(:endDate,ps.birthdate)/365) AS age ";
    } else {
      sql += " SELECT p.patient_id, FLOOR(DATEDIFF(:evaluationDate,ps.birthdate)/365) AS age ";
    }
    sql +=
        " FROM patient p "
            + " INNER JOIN person ps ON p.patient_id = ps.person_id "
            + " WHERE p.voided=0"
            + " AND ps.voided=0";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  public DataDefinition getDataNotificouCV() {

    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();

    spdd.setName("Data de Consulta onde Notificou o Resultado de CV");
    spdd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    spdd.addParameter(new Parameter("endDate", "End Date", Date.class));
    spdd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        "SELECT patient_id, MIN(first_result_date) AS lab_result_date "
            + "            FROM( "
            + "               SELECT p.patient_id, Min(e.encounter_datetime) AS first_result_date "
            + "               FROM   patient p "
            + "                   INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "               WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND o.voided = 0 "
            + "                 AND e.location_id = :location "
            + "                 AND e.encounter_type = ${6} "
            + "                 AND o.concept_id = ${856} "
            + "                 AND o.value_numeric IS NOT NULL "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 1 MONTH), INTERVAL 1 DAY) "
            + "               GROUP  BY p.patient_id "
            + "               UNION "
            + "               SELECT p.patient_id, Min(e.encounter_datetime) AS first_result_date "
            + "               FROM patient p "
            + "                        INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                        INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                        INNER JOIN concept_name cn ON cn.concept_id = o.value_coded "
            + "                        INNER JOIN ( "
            + "                   SELECT p.patient_id, Min(e.encounter_datetime) AS first_encounter_date "
            + "                   FROM patient p "
            + "                            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                   WHERE p.voided = 0 "
            + "                     AND e.voided = 0 "
            + "                     AND o.voided = 0 "
            + "                     AND e.location_id = :location "
            + "                     AND e.encounter_type = ${6} "
            + "                     AND o.concept_id = ${1305} "
            + "                     AND o.value_coded IS NOT NULL "
            + "                     AND e.encounter_datetime >= :startDate "
            + "                     AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 1 MONTH), INTERVAL 1 DAY) "
            + "                   GROUP BY p.patient_id "
            + "               ) first_encounter ON first_encounter.patient_id = p.patient_id "
            + "               WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND o.voided = 0 "
            + "                 AND cn.voided = 0 "
            + "                 AND e.location_id = :location "
            + "                 AND e.encounter_type = ${6} "
            + "                 AND o.concept_id = ${1305} "
            + "                 AND o.value_coded IS NOT NULL "
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 1 MONTH), INTERVAL 1 DAY) "
            + "                 AND first_encounter.first_encounter_date = e.encounter_datetime "
            + "                 AND cn.locale = 'pt' "
            + "               GROUP BY p.patient_id "
            + "               UNION "
            + "            SELECT p.patient_id, Min(e.encounter_datetime) AS first_result_date "
            + "            FROM   patient p "
            + "                   INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "            WHERE  p.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND o.voided = 0 "
            + "                   AND e.location_id = :location "
            + "                   AND e.encounter_type = ${6} "
            + "                   AND o.concept_id = ${856} "
            + "                   AND o.value_numeric IS NOT NULL "
            + "                   AND e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 1 MONTH) "
            + "                   AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 2 MONTH), INTERVAL 1 DAY) "
            + "            GROUP  BY p.patient_id "
            + "            UNION "
            + "               SELECT p.patient_id, Min(e.encounter_datetime) AS first_result_date "
            + "               FROM patient p "
            + "                        INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                        INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                        INNER JOIN concept_name cn ON cn.concept_id = o.value_coded "
            + "                        INNER JOIN ( "
            + "                   SELECT p.patient_id, Min(e.encounter_datetime) AS first_encounter_date "
            + "                   FROM patient p "
            + "                            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                   WHERE p.voided = 0 "
            + "                     AND e.voided = 0 "
            + "                     AND o.voided = 0 "
            + "                     AND e.location_id = :location "
            + "                     AND e.encounter_type = ${6} "
            + "                     AND o.concept_id = ${1305} "
            + "                     AND o.value_coded IS NOT NULL "
            + "                     AND e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 1 MONTH) "
            + "                     AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 2 MONTH), INTERVAL 1 DAY) "
            + "                   GROUP BY p.patient_id "
            + "               ) first_encounter ON first_encounter.patient_id = p.patient_id "
            + "               WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND o.voided = 0 "
            + "                 AND cn.voided = 0 "
            + "                 AND e.location_id = :location "
            + "                 AND e.encounter_type = ${6} "
            + "                 AND o.concept_id = ${1305} "
            + "                 AND o.value_coded IS NOT NULL "
            + "                 AND e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 1 MONTH) "
            + "                 AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 2 MONTH), INTERVAL 1 DAY) "
            + "                 AND first_encounter.first_encounter_date = e.encounter_datetime "
            + "                 AND cn.locale = 'pt' "
            + "               GROUP BY p.patient_id "
            + "            UNION "
            + "            SELECT p.patient_id, Min(e.encounter_datetime) AS first_result_date "
            + "            FROM   patient p "
            + "                   INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "            WHERE  p.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND o.voided = 0 "
            + "                   AND e.location_id = :location "
            + "                   AND e.encounter_type = ${6} "
            + "                   AND o.concept_id = ${856} "
            + "                   AND o.value_numeric IS NOT NULL "
            + "                   AND e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 2 MONTH) "
            + "                   AND e.encounter_datetime <= :endDate "
            + "            GROUP  BY p.patient_id "
            + "            UNION "
            + "                   SELECT p.patient_id, Min(e.encounter_datetime) AS first_result_date "
            + "               FROM patient p "
            + "                   INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                   INNER JOIN concept_name cn ON cn.concept_id = o.value_coded "
            + "                   INNER JOIN ( "
            + "                   SELECT p.patient_id, Min(e.encounter_datetime) AS first_encounter_date "
            + "                   FROM patient p "
            + "                   INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                   WHERE p.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND o.voided = 0 "
            + "                   AND e.location_id = :location "
            + "                   AND e.encounter_type = ${6} "
            + "                   AND o.concept_id = ${1305} "
            + "                   AND o.value_coded IS NOT NULL "
            + "                   AND e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 2 MONTH) "
            + "                   AND e.encounter_datetime <= :endDate "
            + "                   GROUP BY p.patient_id "
            + "                   ) first_encounter ON first_encounter.patient_id = p.patient_id "
            + "               WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND o.voided = 0 "
            + "                 AND cn.voided = 0 "
            + "                 AND e.location_id = :location "
            + "                 AND e.encounter_type = 6 "
            + "                 AND o.concept_id = ${1305} "
            + "                 AND o.value_coded IS NOT NULL "
            + "                 AND e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 2 MONTH) "
            + "                 AND e.encounter_datetime <= :endDate "
            + "                 AND first_encounter.first_encounter_date = e.encounter_datetime "
            + "                 AND cn.locale = 'pt' "
            + "               GROUP BY p.patient_id "
            + "            ) vl GROUP BY vl.patient_id";

    StringSubstitutor substitutor = new StringSubstitutor(map);

    spdd.setQuery(substitutor.replace(query));

    return spdd;
  }

  public DataDefinition getViralLoadResults(boolean isVLQuantitative) {

    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();

    spdd.setName("Data de Consulta onde Notificou o Resultado de CV");
    spdd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    spdd.addParameter(new Parameter("endDate", "End Date", Date.class));
    spdd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        "SELECT patient_id, min(first_vl_result) as first_vl_result " + "            FROM( ";

    if (isVLQuantitative) {
      query +=
          ""
              + "               SELECT p.patient_id, Min(o.value_numeric) AS first_vl_result "
              + "               FROM   patient p "
              + "                   INNER JOIN encounter e ON p.patient_id = e.patient_id "
              + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
              + "               WHERE p.voided = 0 "
              + "                 AND e.voided = 0 "
              + "                 AND o.voided = 0 "
              + "                 AND e.location_id = :location "
              + "                 AND e.encounter_type = ${6} "
              + "                 AND o.concept_id = ${856} "
              + "                 AND o.value_numeric IS NOT NULL "
              + "                 AND ("
              + "                       (e.encounter_datetime >= :startDate "
              + "                         AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 1 MONTH), INTERVAL 1 DAY)) "
              + "                     OR "
              + "                       (e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 1 MONTH) "
              + "                         AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 2 MONTH), INTERVAL 1 DAY)) "
              + "                     OR "
              + "                       (e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 2 MONTH) "
              + "                         AND e.encounter_datetime <= :endDate) "
              + "                     )"
              + "               GROUP  BY p.patient_id ";
    } else {
      query +=
          ""
              + "               SELECT p.patient_id, cn.name AS first_vl_result"
              + "               FROM patient p "
              + "                        INNER JOIN encounter e ON p.patient_id = e.patient_id "
              + "                        INNER JOIN obs o ON e.encounter_id = o.encounter_id "
              + "                        INNER JOIN concept_name cn ON cn.concept_id = o.value_coded "
              + "                        INNER JOIN ( "
              + "                   SELECT p.patient_id, Min(e.encounter_datetime) AS first_encounter_date "
              + "                   FROM patient p "
              + "                            INNER JOIN encounter e ON p.patient_id = e.patient_id "
              + "                            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
              + "                   WHERE p.voided = 0 "
              + "                     AND e.voided = 0 "
              + "                     AND o.voided = 0 "
              + "                     AND e.location_id = :location "
              + "                     AND e.encounter_type = ${6} "
              + "                     AND o.concept_id = ${1305} "
              + "                     AND o.value_coded IS NOT NULL "
              + "                     AND ( "
              + "                           (e.encounter_datetime >= :startDate "
              + "                             AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 1 MONTH), INTERVAL 1 DAY)) "
              + "                         OR "
              + "                           (e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 1 MONTH) "
              + "                             AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 2 MONTH), INTERVAL 1 DAY)) "
              + "                         OR "
              + "                           (e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 2 MONTH) "
              + "                             AND e.encounter_datetime <= :endDate) "
              + "                         ) "
              + "                   GROUP BY p.patient_id "
              + "               ) first_encounter ON first_encounter.patient_id = p.patient_id "
              + "               WHERE p.voided = 0 "
              + "                 AND e.voided = 0 "
              + "                 AND o.voided = 0 "
              + "                 AND cn.voided = 0 "
              + "                 AND e.location_id = :location "
              + "                 AND e.encounter_type = ${6} "
              + "                 AND o.concept_id = ${1305} "
              + "                 AND o.value_coded IS NOT NULL "
              + "                 AND ( "
              + "                       (e.encounter_datetime >= :startDate "
              + "                         AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 1 MONTH), INTERVAL 1 DAY)) "
              + "                     OR "
              + "                       (e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 1 MONTH) "
              + "                         AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 2 MONTH), INTERVAL 1 DAY)) "
              + "                     OR "
              + "                       (e.encounter_datetime >= DATE_ADD(:startDate, INTERVAL 2 MONTH) "
              + "                         AND e.encounter_datetime <= :endDate) "
              + "                     ) "
              + "                 AND first_encounter.first_encounter_date = e.encounter_datetime "
              + "                 AND cn.locale = 'pt' "
              + "               GROUP BY p.patient_id ";
    }
    query += "            ) vl GROUP BY vl.patient_id";
    StringSubstitutor substitutor = new StringSubstitutor(map);

    spdd.setQuery(substitutor.replace(query));

    return spdd;
  }
}
