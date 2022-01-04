package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

public class TxPvlsBySourceQueries {

  /**
   * <b>Description:</b> Patients having viral load within the 12 months period
   *
   * @param labEncounter
   * @param fsrEncounter
   * @param vlConceptQuestion
   * @param vlQualitativeConceptQuestion
   * @return {@link String}
   */
  public static String getPatientsHavingViralLoadInLast12MonthsForLabAndFsrDenominator(
      int labEncounter, int fsrEncounter, int vlConceptQuestion, int vlQualitativeConceptQuestion) {

    Map<String, String> map = new HashMap<>();
    map.put("13", String.valueOf(labEncounter));
    map.put("51", String.valueOf(fsrEncounter));
    map.put("856", String.valueOf(vlConceptQuestion));
    map.put("1305", String.valueOf(vlQualitativeConceptQuestion));

    String query =
        "SELECT p.patient_id FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN"
            + " obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND "
            + " e.encounter_type IN ( ${13}, ${51}) AND "
            + " ((o.concept_id= ${856} AND o.value_numeric IS NOT NULL) OR (o.concept_id=${1305} AND o.value_coded IS NOT NULL)) AND "
            + " e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND "
            + " e.location_id=:location ";
    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * <b>Description</b>Patients with viral load suppression within 12 months
   *
   * @param labEncounter
   * @param fsrEncounter
   * @param vlConceptQuestion
   * @param vlQualitativeConceptQuestion
   * @return String
   */
  public static String getPatientsWithViralLoadSuppressionForLabAndFsrNumerator(
      int labEncounter, int fsrEncounter, int vlConceptQuestion, int vlQualitativeConceptQuestion) {
    Map<String, String> map = new HashMap<>();
    map.put("13", String.valueOf(labEncounter));
    map.put("51", String.valueOf(fsrEncounter));
    map.put("856", String.valueOf(vlConceptQuestion));
    map.put("1305", String.valueOf(vlQualitativeConceptQuestion));
    String query =
        " SELECT patient_id FROM "
            + " (SELECT p.patient_id,MAX(o.obs_datetime) data_carga "
            + " FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN (${13}, ${51}) "
            + " AND  o.concept_id IN(${856}, ${1305}) "
            + " AND (o.value_numeric IS NOT NULL OR o.value_coded IS NOT NULL) AND "
            + " e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND "
            + " e.location_id=:location GROUP BY p.patient_id "
            + ") ultima_carga "
            + " INNER JOIN obs ON obs.person_id=ultima_carga.patient_id AND obs.obs_datetime=ultima_carga.data_carga "
            + " WHERE obs.voided=0 AND obs.concept_id IN (${856}, ${1305}) "
            + " AND obs.location_id=:location AND ((obs.value_numeric IS NOT NULL AND obs.value_numeric < 1000) OR obs.value_coded IS NOT NULL) ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * <b>Description:</b> Patients having viral load within the 12 months period
   *
   * @param adultSeguimentoEncounter
   * @param pediatriaSeguimentoEncounter
   * @param mastercardEncounter
   * @param vlConceptQuestion
   * @param vlQualitativeConceptQuestion
   * @return {@link String}
   */
  public static String getPatientsHavingViralLoadInLast12MonthsPvlsFichaMastreDenominator(
      int adultSeguimentoEncounter,
      int pediatriaSeguimentoEncounter,
      int mastercardEncounter,
      int vlConceptQuestion,
      int vlQualitativeConceptQuestion) {
    Map<String, String> map = new HashMap<>();
    map.put("6", String.valueOf(adultSeguimentoEncounter));
    map.put("9", String.valueOf(pediatriaSeguimentoEncounter));
    map.put("53", String.valueOf(mastercardEncounter));
    map.put("856", String.valueOf(vlConceptQuestion));
    map.put("1305", String.valueOf(vlQualitativeConceptQuestion));

    String query =
        "SELECT p.patient_id FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN"
            + " obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND "
            + " e.encounter_type IN (${6},${9}) AND "
            + " ((o.concept_id=${856} AND o.value_numeric IS NOT NULL) OR (o.concept_id=${1305} AND o.value_coded IS NOT NULL)) AND "
            + " e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND "
            + " e.location_id=:location "
            + " UNION "
            + " SELECT p.patient_id FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN "
            + " obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND "
            + " e.encounter_type IN (${53}) AND o.concept_id=${856} AND o.value_numeric IS NOT NULL AND "
            + " o.obs_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND "
            + " e.location_id=:location ";
    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * <b>Description</b>Patients with viral load suppression within 12 months
   *
   * @param adultSeguimentoEncounter
   * @param pediatriaSeguimentoEncounter
   * @param mastercardEncounter
   * @param vlConceptQuestion
   * @param vlQualitativeConceptQuestion
   * @return String
   */
  public static String getPatientsWithViralLoadSuppressionPvlsFichaMastreNumerator(
      int adultSeguimentoEncounter,
      int pediatriaSeguimentoEncounter,
      int mastercardEncounter,
      int vlConceptQuestion,
      int vlQualitativeConceptQuestion) {

    Map<String, String> map = new HashMap<>();
    map.put("6", String.valueOf(adultSeguimentoEncounter));
    map.put("9", String.valueOf(pediatriaSeguimentoEncounter));
    map.put("53", String.valueOf(mastercardEncounter));
    map.put("856", String.valueOf(vlConceptQuestion));
    map.put("1305", String.valueOf(vlQualitativeConceptQuestion));
    String query =
        "SELECT fn1.patient_id FROM( "
            + " SELECT  patient_id, MAX(data_carga) AS data_carga FROM( "
            + " SELECT patient_id,data_carga FROM "
            + " (SELECT p.patient_id,MAX(o.obs_datetime) data_carga "
            + " FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN (${6},${9}) "
            + " AND  o.concept_id IN(${856}, ${1305}) "
            + " AND (o.value_numeric IS NOT NULL OR o.value_coded IS NOT NULL) AND "
            + " e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate AND "
            + " e.location_id=:location GROUP BY p.patient_id "
            + ") ultima_carga "
            + " INNER JOIN obs ON obs.person_id=ultima_carga.patient_id AND obs.obs_datetime= "
            + " ultima_carga.data_carga  WHERE obs.voided=0 AND obs.concept_id IN (${856}, ${1305}) "
            + " AND obs.location_id=:location AND (obs.value_numeric IS NOT NULL OR obs.value_coded IS NOT NULL) "
            + " UNION "
            + " SELECT patient_id,data_carga FROM "
            + " (SELECT p.patient_id, MAX(o.obs_datetime) AS data_carga FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN "
            + " obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND "
            + " e.encounter_type IN (${53}) AND o.concept_id=${856} AND o.value_numeric IS NOT NULL AND "
            + " o.obs_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + " AND e.location_id=:location GROUP BY p.patient_id) comb INNER JOIN obs ON obs.person_id=comb.patient_id AND obs.obs_datetime= "
            + " comb.data_carga  WHERE obs.voided=0 AND obs.concept_id IN (${856}) "
            + " AND obs.location_id=:location AND "
            + " (obs.value_numeric IS NOT NULL OR obs.value_coded IS NOT NULL) GROUP BY patient_id)fn GROUP BY patient_id)fn1 "
            + " INNER JOIN obs os ON os.person_id=fn1.patient_id WHERE fn1.data_carga=os.obs_datetime AND os.concept_id IN(${856}, ${1305}) "
            + " AND (os.value_numeric < 1000 OR os.value_coded IS NOT NULL) AND os.location_id=:location AND voided=0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * <b>Description</b> Patients or routine using FSR with VL results
   *
   * @param fsrEncounter
   * @param viralLoadRequestReasonConceptId
   * @param routineViralLoadConceptId
   * @param unknownConceptId
   * @return String
   */
  public static String getPatientsHavingRoutineViralLoadTestsUsingFsr(
      int fsrEncounter,
      int viralLoadRequestReasonConceptId,
      int routineViralLoadConceptId,
      int unknownConceptId) {
    Map<String, String> map = new HashMap<>();
    map.put("51", String.valueOf(fsrEncounter));
    map.put("23818", String.valueOf(viralLoadRequestReasonConceptId));
    map.put("23817", String.valueOf(routineViralLoadConceptId));
    map.put("1067", String.valueOf(unknownConceptId));
    String query =
        " SELECT final.patient_id FROM( "
            + " SELECT p.patient_id, MAX(ee.encounter_datetime) AS viral_load_date "
            + " FROM patient p "
            + " INNER JOIN encounter ee ON p.patient_id=ee.patient_id "
            + " INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + " WHERE "
            + " ee.voided = 0 AND "
            + " ee.encounter_type = ${51} AND "
            + " oo.voided = 0 AND "
            + " oo.concept_id = ${23818} AND oo.value_coded IN(${23817}, ${1067}) AND "
            + " ee.location_id = :location "
            + " AND ee.encounter_datetime <= :endDate "
            + " GROUP BY p.patient_id ) final";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * <b>Description</b> Patients on target using FSR with VL results
   *
   * @param fsrEncounter
   * @param viralLoadRequestReasonConceptId
   * @param routineViralLoadConceptId
   * @param unknownConceptId
   * @return String
   */
  public static String getPatientsOnTargetWithViralLoadTestsUsingFsr(
      int fsrEncounter,
      int viralLoadRequestReasonConceptId,
      int routineViralLoadConceptId,
      int unknownConceptId) {
    Map<String, String> map = new HashMap<>();
    map.put("51", String.valueOf(fsrEncounter));
    map.put("23818", String.valueOf(viralLoadRequestReasonConceptId));
    map.put("23817", String.valueOf(routineViralLoadConceptId));
    map.put("1067", String.valueOf(unknownConceptId));
    String query =
        " SELECT final.patient_id FROM( "
            + " SELECT p.patient_id, MAX(ee.encounter_datetime) AS viral_load_date "
            + " FROM patient p "
            + " INNER JOIN encounter ee ON p.patient_id=ee.patient_id "
            + " INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + " WHERE "
            + " ee.voided = 0 AND "
            + " ee.encounter_type = ${51} AND "
            + " oo.voided = 0 AND "
            + " oo.concept_id = ${23818} AND oo.value_coded NOT IN(${23817}, ${1067}) AND "
            + " ee.location_id = :location "
            + " AND ee.encounter_datetime <= :endDate "
            + " GROUP BY p.patient_id ) final";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * <b>Description</b> Patients or routine using FSR with VL results
   *
   * @param adultoEncounter
   * @param paedEncounter
   * @param resumoEncounter
   * @param vlConceptQuestion
   * @param vlQualitativeConceptQuestion
   * @return String
   */
  public static String getPatientsHavingRoutineViralLoadTestsUsingClinicalForms(
      int adultoEncounter,
      int paedEncounter,
      int resumoEncounter,
      int vlConceptQuestion,
      int vlQualitativeConceptQuestion) {
    Map<String, String> map = new HashMap<>();
    map.put("6", String.valueOf(adultoEncounter));
    map.put("9", String.valueOf(paedEncounter));
    map.put("53", String.valueOf(resumoEncounter));
    map.put("856", String.valueOf(vlConceptQuestion));
    map.put("1305", String.valueOf(vlQualitativeConceptQuestion));
    String query =
        "SELECT p.patient_id FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN"
            + " obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND "
            + " e.encounter_type IN (${6},${9}) AND "
            + " ((o.concept_id=${856} AND o.value_numeric IS NOT NULL) OR (o.concept_id=${1305} AND o.value_coded IS NOT NULL)) AND "
            + " e.encounter_datetime <=:endDate AND "
            + " e.location_id=:location "
            + " UNION "
            + " SELECT p.patient_id FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN "
            + " obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND "
            + " e.encounter_type IN (${53}) AND o.concept_id=${856} AND o.value_numeric IS NOT NULL AND "
            + " o.obs_datetime <=:endDate AND "
            + " e.location_id=:location ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }
}
