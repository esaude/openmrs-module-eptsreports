package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.data.converter.*;
import org.openmrs.module.eptsreports.reporting.data.definition.CalculationDataDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.DQACargaViralCohortQueries;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DQACargaViralDataset extends BaseDataSet {

  private HivMetadata hivMetadata;

  private DQACargaViralCohortQueries dQACargaViralCohortQueries;

  @Autowired
  public DQACargaViralDataset(
      HivMetadata hivMetadata, DQACargaViralCohortQueries dQACargaViralCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.dQACargaViralCohortQueries = dQACargaViralCohortQueries;
  }

  public DataSetDefinition constructDQACargaViralDataset() {
    PatientDataSetDefinition pdd = new PatientDataSetDefinition();

    pdd.addParameter(new Parameter("startDate", "startDate", Date.class));
    pdd.addParameter(new Parameter("endDate", "endDate", Date.class));
    pdd.addParameter(new Parameter("location", "Location", Location.class));
    pdd.setName("DQA Carga Viral");

    PatientIdentifierType identifierType =
        Context.getPatientService()
            .getPatientIdentifierTypeByUuid("e2b966d0-1d5f-11e0-b929-000c29ad1d07");

    pdd.addRowFilter(
        dQACargaViralCohortQueries.getBaseCohort(),
        "startDate=${startDate},endDate=${endDate},location=${location}");

    /** Patient counter - Sheet 1: Column A */
    pdd.addColumn("counter", new PersonIdDataDefinition(), "", new ObjectCounterConverter());

    /** 1 - NID - Sheet 1: Column B */
    pdd.addColumn("nid", getNID(identifierType.getPatientIdentifierTypeId()), "");

    /** 2 - Sexo - Sheet 1: Column C */
    pdd.addColumn("gender", new GenderDataDefinition(), "", new GenderConverter());

    /** 3 - Faixa Etária - Sheet 1: Column D */
    pdd.addColumn("age", new AgeDataDefinition(), "", new AgeToLetterConverter());

    /** 4 - Data Início TARV - Sheet 1: Column E */
    pdd.addColumn(
        "inicio_tarv",
        getArtStartDate(),
        "onOrBefore=${endDate},location=${location}",
        new CalculationResultConverter());

    /**
     * 5 - Data de Consulta onde Notificou o Resultado de CV dentro do Período de Revisão - Sheet 1:
     * Column F
     */
    pdd.addColumn(
        "data_consulta_resultado_cv",
        this.getDataNotificouCV(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        new ForwardSlashDateConverter());

    /** 6 - Resultado da Carga Viral - Sheet 1: Column G */
    pdd.addColumn(
        "resultado_carga_viral",
        this.getViralLoadResults(),
        "startDate=${startDate},endDate=${endDate},location=${location}");

    return pdd;
  }

  private DataDefinition getNID(int identifierType) {
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

  private DataDefinition getArtStartDate() {
    CalculationDataDefinition cd =
        new CalculationDataDefinition(
            "Art start date",
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
    return cd;
  }

  private DataDefinition getDataNotificouCV() {

    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();

    spdd.setName("Data de Consulta onde Notificou o Resultado de CV");
    spdd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    spdd.addParameter(new Parameter("endDate", "End Date", Date.class));
    spdd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

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

  private DataDefinition getViralLoadResults() {

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
        "SELECT patient_id, min(first_vl_result) as first_vl_result "
            + "            FROM( "
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
            + "                 AND e.encounter_datetime >= :startDate "
            + "                 AND e.encounter_datetime <= DATE_SUB(DATE_ADD(:startDate, INTERVAL 1 MONTH), INTERVAL 1 DAY) "
            + "               GROUP  BY p.patient_id "
            + "               UNION "
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
            + "            SELECT p.patient_id, Min(o.value_numeric) AS first_vl_result "
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
            + "               SELECT p.patient_id, cn.name AS first_vl_result "
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
            + "            SELECT p.patient_id, Min(o.value_numeric) AS first_vl_result "
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
            + "                   SELECT p.patient_id, cn.name AS first_vl_result "
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
            + "                 AND e.encounter_type = ${6} "
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

  private Integer DQACounter() {
    int counter = 0;

    return counter;
  }
}
