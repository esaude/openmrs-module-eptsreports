package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.*;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class TPTCompletionQueries {

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A -A1: Select all patients with Ficha Resumo (encounter type 53) with “Ultima profilaxia
   *       Isoniazida (Data Inicio)” (concept id 6128) value datetime not null and before end date
   *   <li>
   *
   * @return CohortDefinition
   */
  public static CohortDefinition getINHStartA1(
      int masterCardEncounterType, int dataInicioProfilaxiaIsoniazidaConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Ultima profilaxia Isoniazida (Data Inicio)");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", masterCardEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);

    String query =
        "  SELECT"
            + "  p.patient_id"
            + "  FROM"
            + "  patient p"
            + "     INNER JOIN"
            + "  encounter e ON p.patient_id = e.patient_id"
            + "     INNER JOIN"
            + " obs o ON e.encounter_id = o.encounter_id"
            + " WHERE"
            + " p.voided = 0 AND e.voided AND o.voided"
            + " and e.encounter_type = ${53}"
            + " and o.concept_id = ${6128}"
            + " and o.value_datetime IS NOT NULL"
            + " and o.value_datetime < :endDate"
            + " and e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A2: Select all patients with Ficha clinica (encounter type 6) with “Profilaxia INH”
   *       (concept id 6122) with value code “Inicio” (concept id 1256) and encounter datetime
   *       before end date
   *   <li>
   *
   * @return CohortDefinition
   */
  public static CohortDefinition getINHStartA2(
      int adultoSeguimentoEncounterType, int startDrugsConcept, int isoniazidUsageConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Profilaxia INH");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("6122", isoniazidUsageConcept);
    map.put("1256", startDrugsConcept);

    String query =
        " SELECT"
            + " p.patient_id"
            + " FROM"
            + " patient p"
            + " INNER JOIN"
            + " encounter e ON p.patient_id = e.patient_id"
            + " INNER JOIN"
            + " obs o ON e.encounter_id = o.encounter_id"
            + " WHERE"
            + " p.voided = 0 AND e.voided AND o.voided"
            + "    AND e.encounter_type = ${6}"
            + "    AND o.concept_id = ${6122}"
            + "    AND o.value_coded = ${1256}"
            + "    AND e.encounter_datetime <= :endDate"
            + "    AND e.location_id = :location";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A3: Select all patients with Ficha clinica (encounter type 6) with “Profilaxia com INH”
   *       (concept id 6128) value datetime before end date
   *   <li>
   *
   * @return CohortDefinition
   */
  public static CohortDefinition getINHStartA3(int encounterType, int profilaxiaIsoniazidaConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Ficha Clinica ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "After Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", encounterType);
    map.put("6128", profilaxiaIsoniazidaConcept);

    String query =
        "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE e.encounter_type = ${6} "
            + "AND o.concept_id = ${6128} "
            + "AND e.location_id = :location "
            + "AND o.value_datetime < :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A4: Select all patients with Ficha Seguimento PEdiatrico (encounter type 9) with
   *       “Profilaxia com INH” (concept id 6128) value datetime before end date
   *   <li>
   *
   * @return CohortDefinition
   */
  public static CohortDefinition getINHStartA4(
      int pediatriaSeguimentoEncounterType, int dataInicioProfilaxiaIsoniazidaConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Ficha Seguimento Pediatrico ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);

    String query =
        " SELECT "
            + "	p.patient_id "
            + " FROM "
            + "  	patient p "
            + "     	INNER JOIN "
            + " 	encounter e ON p.patient_id = e.patient_id "
            + "     	INNER JOIN "
            + " 	obs o ON e.encounter_id = o.encounter_id "
            + " WHERE "
            + " 	p.voided = 0 AND e.voided = 0 "
            + "     	AND o.voided = 0 "
            + "     	AND e.encounter_type = ${9} "
            + "     	AND o.concept_id = ${6128} "
            + " 	AND e.location_id = :location "
            + "     	AND o.value_datetime < :endDate ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A5: Select all patients with FILT (encounter type 60) with “Regime de TPT” (concept id
   *       23985) value coded ‘Isoniazid’ or ‘Isoniazid + piridoxina’ (concept id in [656, 23982])
   *       and encounter datetime before the reporting period
   *   <li>
   *
   * @return CohortDefinition
   */
  public static CohortDefinition getINHStartA5(
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Regime de TPT");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);

    String query =
        " SELECT"
            + " p.patient_id"
            + " FROM"
            + " patient p"
            + "    INNER JOIN"
            + "  encounter e ON p.patient_id = e.patient_id"
            + "    INNER JOIN"
            + " obs o ON e.encounter_id = o.encounter_id"
            + " WHERE"
            + " p.voided = 0 AND e.voided AND o.voided"
            + "     AND e.encounter_type = ${60}"
            + "     AND o.concept_id = ${23985}"
            + "    AND o.value_coded IN (${656} , ${23982})"
            + "     AND e.encounter_datetime < :endDate"
            + "        AND e.location_id = :location";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>C1: Select all patients with Ficha Clinica - Master Card (encounter type 6) with “Outras
   *       prescricoes” (concept id 1719) with value coded equal to “3HP” (concept id 23954) and
   *       encounter datetime before end date;
   *   <li>
   *
   * @return CohortDefinition
   */
  public static CohortDefinition get3HPStartC1(
      int encounterType, int treatmentPrescribedConcept, int threeHPConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Ficha Clinica Master Card ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "After Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", encounterType);
    map.put("1719", treatmentPrescribedConcept);
    map.put("23954", threeHPConcept);

    String query =
        "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE e.encounter_type = ${6} "
            + "AND o.concept_id = ${1719} AND o.value_coded = ${23954} "
            + "AND e.location_id = :location "
            + "AND e.encounter_datetime < :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>C2: Select all patients with FILT (encounter type 6) with “Regime de TPT” (concept id
   *       23985) value coded “3HP” or ” 3HP+Piridoxina” (concept id in [23954, 23984]) and
   *       encounter datetime before end date;
   *   <li>
   *
   * @return CohortDefinition
   */
  public static CohortDefinition get3HPStartC2(
      int adultoSeguimentoEncounterType,
      int regimeTPTConcept,
      int hPConcept,
      int hPPiridoxinaConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with FILT ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("23954", hPConcept);
    map.put("23984", hPPiridoxinaConcept);

    String query =
        " SELECT  "
            + "  p.patient_id "
            + " 	FROM "
            + "     	patient p "
            + " 	INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " 	INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " 	WHERE "
            + "    	e.encounter_type = ${6} AND p.voided = 0 "
            + "         	AND e.voided = 0 "
            + "         	AND o.voided = 0 "
            + "         	AND o.concept_id = ${23985} "
            + "         	AND o.value_coded in (${23954},${23984}) "
            + "         	AND e.location_id = :location "
            + "         	AND e.encounter_datetime <:endDate ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }
}
