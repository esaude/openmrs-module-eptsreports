package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CXCASCRNCohortQueries;

public class CXCASCRNQueries {

  public static String getAA1OrAA2Query(
      CXCASCRNCohortQueries.CXCASCRNResult cxcascrnResult,
      boolean isAA1,
      boolean max,
      int m28,
      int m6,
      int m53,
      int m2094,
      int m664,
      int m703,
      int m2093) {

    Map<String, Integer> map = new HashMap<>();
    map.put("28", m28);
    map.put("6", m6);
    map.put("53", m53);
    map.put("2094", m2094);
    map.put("664", m664);
    map.put("703", m703);
    map.put("2093", m2093);

    StringBuilder query = new StringBuilder();
    if (max && isAA1) {
      query.append(" SELECT p.patient_id, MAX(e.encounter_datetime) as x_datetime");
    }
    if (max && !isAA1) {
      query.append(" SELECT p.patient_id, MAX(o.value_datetime) as x_datetime");
    } else if (!max) {
      query.append(" SELECT p.patient_id ");
    }
    query.append(" FROM patient p ");
    query.append("    INNER JOIN encounter e ");
    query.append("        ON e.patient_id = p.patient_id ");
    query.append("    INNER JOIN obs o ");
    query.append("        ON o.encounter_id = e.encounter_id ");
    query.append(" WHERE p.voided = 0 ");
    query.append("    AND e.voided = 0 ");
    query.append("    AND o.voided = 0 ");
    if (isAA1) {
      query.append("    AND e.encounter_type IN (${6},${28}) ");
    } else {
      query.append("    AND e.encounter_type = ${53} ");
    }
    query.append("    AND o.concept_id = ${2094} ");
    if (cxcascrnResult == CXCASCRNCohortQueries.CXCASCRNResult.NEGATIVE) {
      query.append("    AND o.value_coded = ${664} ");
    }
    if (cxcascrnResult == CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE) {
      query.append("    AND o.value_coded = ${703} ");
    }
    if (cxcascrnResult == CXCASCRNCohortQueries.CXCASCRNResult.SUSPECTED) {
      query.append("    AND o.value_coded = ${2093}  ");
    }
    if (cxcascrnResult == CXCASCRNCohortQueries.CXCASCRNResult.ANY) {
      query.append("    AND o.value_coded IS NOT NULL ");
    }
    if (isAA1) {
      query.append("    AND e.encounter_datetime < :onOrAfter ");
    } else {
      query.append("    AND o.value_datetime < :onOrAfter ");
    }
    query.append("    AND e.location_id = :location ");
    if (max) {
      query.append(" GROUP BY p.patient_id ");
    }
    StringSubstitutor sb = new StringSubstitutor(map);

    return sb.replace(query);
  }
}
