package org.openmrs.module.eptsreports.reporting.library.queries;

public interface CXCASCRNQueries {

  class QUERY {
    public static final String findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod =
        "SELECT finalCCU.patient_id FROM  ( "
            + "SELECT p.patient_id,min(e.encounter_datetime) dataccu FROM patient p  "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id      "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id  "
            + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type=28 "
            + "and o.concept_id=2094 and o.value_coded in(2093,664,703) AND e.encounter_datetime>=:startDate and e.encounter_datetime<=:endDate and e.location_id=:location "
            + "GROUP BY p.patient_id  "
            + ")finalCCU "
            + "GROUP by finalCCU.patient_id  order by finalCCU.patient_id asc";

    public static final String
        findPatientsWithScreeningTestForCervicalCancerPreviousReportingPeriod =
            "SELECT finalCCU.patient_id FROM  ( "
                + "SELECT p.patient_id,e.encounter_datetime dataccu FROM patient p  "
                + "INNER JOIN encounter e on p.patient_id=e.patient_id      "
                + "INNER JOIN obs o on o.encounter_id=e.encounter_id  "
                + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type=28 "
                + "and o.concept_id=2094 and o.value_coded in(2093,664,703) AND e.encounter_datetime<:startDate and e.location_id=:location "
                + "GROUP BY p.patient_id  "
                + ")finalCCU "
                + "GROUP by finalCCU.patient_id order by finalCCU.patient_id asc";

    public static final String
        findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodByReusult(
            int concept, int answer) {
      String sql =
          "SELECT finalCCU.patient_id FROM  ( "
              + "SELECT p.patient_id,min(o.obs_datetime) dataccu FROM patient p  "
              + "INNER JOIN encounter e on p.patient_id=e.patient_id      "
              + "INNER JOIN obs o on o.encounter_id=e.encounter_id  "
              + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type=28 "
              + "and o.concept_id=%s and o.value_coded=%s AND e.encounter_datetime>=:startDate and e.encounter_datetime<=:endDate and e.location_id=:location "
              + "GROUP BY p.patient_id  "
              + ")finalCCU "
              + "GROUP by finalCCU.patient_id ";

      return String.format(sql, concept, answer);
    }

    public static final String findPatientWithScreeningTypeVisitAsRescreenedAfterPreviousNegative =
        "SELECT finalCCU.patient_id FROM ( "
            + "SELECT p.patient_id,max(o.obs_datetime) dataccu FROM patient p  "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id      "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id  "
            + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type=28 "
            + "and o.concept_id=2094 and o.value_coded=664 AND e.encounter_datetime<:startDate and e.location_id=:location "
            + "GROUP BY p.patient_id "
            + ")finalCCU "
            + "GROUP by finalCCU.patient_id ";

    public static final String findpatientwithScreeningTypeVisitAsPostTreatmentFollowUp =
        "SELECT firstCCU.patient_id FROM ( "
            + "SELECT p.patient_id,min(e.encounter_datetime) dataccu FROM patient p  "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id  "
            + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type=28 "
            + "and o.concept_id=2094 and o.value_coded in(2093,664,703) AND e.encounter_datetime>=:startDate and e.encounter_datetime<=:endDate and e.location_id=:location "
            + "GROUP BY p.patient_id  "
            + ")firstCCU "
            + "INNER JOIN ( "
            + "SELECT p.patient_id,max(e.encounter_datetime) dataccu FROM patient p  "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id  "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id  "
            + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type=28 and e.encounter_datetime<:startDate and o.concept_id=2094 and o.value_coded=703  and e.location_id=:location "
            + "GROUP BY p.patient_id  "
            + ") positiveCCU on positiveCCU.patient_id=firstCCU.patient_id "
            + "INNER JOIN ( "
            + "SELECT p.patient_id,max(e.encounter_datetime) dataccu FROM patient p  "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id  "
            + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type=28 "
            + "and o.concept_id=2117 and o.value_coded=1065 and e.location_id=:location "
            + "GROUP BY p.patient_id  "
            + "UNION "
            + "SELECT p.patient_id,max(o.value_datetime) dataccu FROM patient p  "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id  "
            + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type=28 "
            + "and o.concept_id=23967 and e.location_id=:location "
            + "GROUP BY p.patient_id  "
            + "UNION "
            + "SELECT p.patient_id,max(e.encounter_datetime) dataccu FROM patient p  "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id  "
            + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type=28 "
            + "and o.concept_id=2149 AND o.value_coded in(23974,23972,23970,23973)  and e.location_id=:location "
            + "GROUP BY p.patient_id "
            + ")ccu "
            + "where firstCCU.dataccu>=positiveCCU.dataccu and ccu.dataccu<firstCCU.dataccu "
            + "GROUP BY firstCCU.patient_id  ";
  }
}
