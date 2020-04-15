package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralQueries {

  /**
   * Number of patients with ART suspension during the current month with LAST patient state
   *
   * @return String
   */
  public static String getPatientsWhoSuspendedTreatment(
      int art,
      int suspendedState,
      int adultSeg,
      int masterCard,
      int artStateOfStay,
      int preArtStateOfStay,
      int suspendedConcept,
      int fila,
      int mcDrugPickup,
      int drugPickup) {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("art", art);
    valuesMap.put("suspendedState", suspendedState);
    valuesMap.put("adultSeg", adultSeg);
    valuesMap.put("masterCard", masterCard);
    valuesMap.put("artStateOfStay", artStateOfStay);
    valuesMap.put("preArtStateOfStay", preArtStateOfStay);
    valuesMap.put("suspendedConcept", suspendedConcept);
    valuesMap.put("fila", fila);
    valuesMap.put("mcDrugPickup", mcDrugPickup);
    valuesMap.put("drugPickup", drugPickup);

    StringBuilder sql = new StringBuilder();
    sql.append("SELECT patient_id ");
    sql.append("FROM (SELECT patient_id, ");
    sql.append("             suspended_date ");
    sql.append("      FROM (SELECT p.patient_id, ");
    sql.append("                   Max(ps.start_date) suspended_date ");
    sql.append("            FROM patient p ");
    sql.append("                     JOIN patient_program pp ");
    sql.append("                          ON p.patient_id = pp.patient_id ");
    sql.append("                     JOIN patient_state ps ");
    sql.append("                          ON pp.patient_program_id = ps.patient_program_id ");
    sql.append("            WHERE p.voided = 0 ");
    sql.append("              AND pp.voided = 0 ");
    sql.append("              AND pp.program_id = ${art} ");
    sql.append("              AND pp.location_id = :location ");
    sql.append("              AND ps.voided = 0 ");
    sql.append("              AND ps.state = ${suspendedState} ");
    sql.append("            AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore ");
    sql.append("              AND ps.end_date = NULL ");
    sql.append("            UNION ");
    sql.append("            SELECT p.patient_id, ");
    sql.append("                   Max(e.encounter_datetime) suspended_date ");
    sql.append("            FROM patient p ");
    sql.append("                     JOIN encounter e ");
    sql.append("                          ON p.patient_id = e.patient_id ");
    sql.append("                     JOIN obs o ");
    sql.append("                          ON e.encounter_id = o.encounter_id ");
    sql.append("            WHERE p.voided = 0 ");
    sql.append("              AND e.voided = 0 ");
    sql.append("              AND e.location_id = :location ");
    sql.append("              AND e.encounter_type = ${adultSeg} ");
    sql.append("            AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore ");
    sql.append("              AND o.voided = 0 ");
    sql.append("              AND o.concept_id = ${artStateOfStay} ");
    sql.append("              AND o.value_coded = ${suspendedConcept} ");
    sql.append("            UNION ");
    sql.append("            SELECT p.patient_id, ");
    sql.append("                   Max(o.obs_datetime) suspended_date ");
    sql.append("            FROM patient p ");
    sql.append("                     JOIN encounter e ");
    sql.append("                          ON p.patient_id = e.patient_id ");
    sql.append("                     JOIN obs o ");
    sql.append("                          ON e.encounter_id = o.encounter_id ");
    sql.append("            WHERE p.voided = 0 ");
    sql.append("              AND e.voided = 0 ");
    sql.append("              AND e.location_id = :location ");
    sql.append("              AND e.encounter_type = ${masterCard} ");
    sql.append("              AND o.voided = 0 ");
    sql.append("              AND o.concept_id = ${preArtStateOfStay} ");
    sql.append("            AND o.obs_datetime BETWEEN :onOrAfter AND :onOrBefore ");
    sql.append("              AND o.value_coded = ${suspendedConcept}) transferout ");
    sql.append("      GROUP BY patient_id) max_transferout ");
    sql.append("WHERE patient_id NOT IN (SELECT p.patient_id ");
    sql.append("                         FROM patient p ");
    sql.append("                                  JOIN encounter e ");
    sql.append("                                       ON p.patient_id = e.patient_id ");
    sql.append("                         WHERE p.voided = 0 ");
    sql.append("                           AND e.voided = 0 ");
    sql.append("                           AND e.encounter_type IN (${fila}) ");
    sql.append("                           AND e.location_id = :location ");
    sql.append("                           AND e.encounter_datetime > suspended_date ");
    sql.append("                           AND e.encounter_datetime <= :onOrBefore ");
    sql.append("                         UNION ");
    sql.append("                         SELECT p.patient_id ");
    sql.append("                         FROM patient p ");
    sql.append("                                  JOIN encounter e ");
    sql.append("                                       ON p.patient_id = e.patient_id ");
    sql.append("                                  JOIN obs o ");
    sql.append("                                       ON e.encounter_id = o.encounter_id ");
    sql.append("                         WHERE p.voided = 0 ");
    sql.append("                           AND e.voided = 0 ");
    sql.append("                           AND e.encounter_type = ${mcDrugPickup} ");
    sql.append("                           AND e.location_id = :location ");
    sql.append("                           AND o.concept_id = ${drugPickup} ");
    sql.append("                           AND o.value_datetime ");
    sql.append("                             > suspended_date");
    sql.append("                           AND o.value_datetime");
    sql.append("                             <= :onOrBefore);");

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(sql);
  }
}
