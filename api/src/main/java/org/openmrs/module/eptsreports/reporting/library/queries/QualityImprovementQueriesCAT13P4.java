package org.openmrs.module.eptsreports.reporting.library.queries;

public interface QualityImprovementQueriesCAT13P4 {

  class QUERY {
    public static final String findPatientsWhoHaveLastTerapeutcLineByQueryB1 =
        " Select patient_id from (select ultimaLinha.patient_id, ultimaLinha.maxDataLinha "
            + " from "
            + " ( "
            + " select p.patient_id, max(o.obs_datetime) maxDataLinha "
            + " from patient p "
            + " join encounter e on p.patient_id = e.patient_id "
            + " join obs o on o.encounter_id = e.encounter_id "
            + " where e.encounter_type = 6 and e.voided = 0 and o.voided = 0 and p.voided = 0 "
            + " and o.concept_id = 21151 and e.location_id = :location "
            + " and o.obs_datetime between :startInclusionDate AND :endInclusionDate "
            + " group by p.patient_id "
            + " ) ultimaLinha "
            + " join obs on obs.person_id = ultimaLinha.patient_id and ultimaLinha.maxDataLinha = obs.obs_datetime "
            + " INNER JOIN person pe ON ultimaLinha.patient_id = pe.person_id "
            + " where obs.concept_id = 21151 and obs.value_coded = 21150 and obs.voided = 0 and obs.location_id = :location "
            + " AND (TIMESTAMPDIFF(year, birthdate, :endInclusionDate)) >= 15 AND birthdate IS NOT NULL AND pe.voided = 0) query ";

    public static final String findPatientsWhohaveCVMoreThan1000CopiesByQueryB2 =
        " Select patient_id from (select e.patient_id, min(e.encounter_datetime) AS encounter_datetime "
            + " from "
            + " encounter e "
            + " inner join obs o ON o.encounter_id = e.encounter_id "
            + " INNER JOIN person pe ON e.patient_id = pe.person_id "
            + " WHERE e.voided = 0 AND e.encounter_type = 6 AND o.voided = 0 AND o.concept_id = 856 "
            + " and o.value_numeric > 1000 and e.location_id = :location "
            + " AND e.encounter_datetime BETWEEN :startInclusionDate AND :endInclusionDate "
            + " AND (TIMESTAMPDIFF(year, birthdate, :endInclusionDate)) >= 15 AND birthdate IS NOT NULL AND pe.voided = 0 "
            + " group by e.patient_id) query ";

    public static final String findPatientsWhoHave3APSSPPConsultationInSameDayOfCVByQueryG =
        " select primeira_consulta.patient_id "
            + " FROM "
            + " (select e.patient_id, min(e.encounter_datetime) AS encounter_datetime "
            + " from "
            + " encounter e "
            + " inner join obs o ON o.encounter_id = e.encounter_id "
            + " WHERE e.voided = 0 AND e.encounter_type = 6 AND o.voided = 0 AND o.concept_id = 856 "
            + " and o.value_numeric > 1000 and e.location_id = :location "
            + " AND e.encounter_datetime BETWEEN :startInclusionDate AND :endInclusionDate "
            + " group by e.patient_id) B2 "
            + " inner join encounter primeira_consulta ON primeira_consulta.patient_id = B2.patient_id "
            + " AND primeira_consulta.encounter_datetime = B2.encounter_datetime AND primeira_consulta.encounter_type = 35 AND primeira_consulta.location_id = :location "
            + " AND primeira_consulta.voided = 0 "
            + " inner join encounter segunda_consulta on primeira_consulta.patient_id = segunda_consulta.patient_id and segunda_consulta.voided = 0 and segunda_consulta.encounter_type = 35 and "
            + " segunda_consulta.location_id = :location and (TIMESTAMPDIFF(DAY, primeira_consulta.encounter_datetime, segunda_consulta.encounter_datetime)) between 20 and 33 "
            + " inner join encounter terceira_consulta on primeira_consulta.patient_id = terceira_consulta.patient_id and terceira_consulta.voided = 0 and terceira_consulta.encounter_type = 35 and "
            + " terceira_consulta.location_id = :location and (TIMESTAMPDIFF(DAY, segunda_consulta.encounter_datetime, terceira_consulta.encounter_datetime)) between 20 and 33 ";

    public static final String findPatientsWhoHaveRequestedCV120DaysAfterCVResultByQueryH =
        " Select B2.patient_id"
            + " from "
            + " (select e.patient_id, min(e.encounter_datetime) AS encounter_datetime "
            + " from "
            + " encounter e "
            + " inner join obs o ON o.encounter_id = e.encounter_id "
            + " WHERE e.voided = 0 AND e.encounter_type = 6 AND o.voided = 0 AND o.concept_id = 856 "
            + " and o.value_numeric > 1000 and e.location_id = :location "
            + " AND e.encounter_datetime BETWEEN :startInclusionDate AND :endInclusionDate "
            + " group by e.patient_id) B2 "
            + " inner join encounter e ON e.patient_id = B2.patient_id and e.voided = 0 and e.location_id = :location and e.encounter_type = 6 "
            + " inner join obs o ON o.encounter_id = e.encounter_id and o.voided = 0 and o.concept_id = 23722 and o.value_coded = 856 "
            + " and e.encounter_datetime BETWEEN date_add(B2.encounter_datetime, INTERVAL 66 DAY) AND date_add(B2.encounter_datetime, INTERVAL 120 DAY) ";

    public static final String findCHILDRENBETWEEN2AND15WhoHaveLastTerapeutcLineByQueryB1 =
        " Select patient_id from (select ultimaLinha.patient_id, ultimaLinha.maxDataLinha "
            + " from "
            + " ( "
            + " select p.patient_id, max(o.obs_datetime) maxDataLinha "
            + " from patient p "
            + " join encounter e on p.patient_id = e.patient_id "
            + " join obs o on o.encounter_id = e.encounter_id "
            + " where e.encounter_type = 6 and e.voided = 0 and o.voided = 0 and p.voided = 0 "
            + " and o.concept_id = 21151 and e.location_id = :location "
            + " and o.obs_datetime between :startInclusionDate AND :endInclusionDate "
            + " group by p.patient_id "
            + " ) ultimaLinha "
            + " join obs on obs.person_id = ultimaLinha.patient_id and ultimaLinha.maxDataLinha = obs.obs_datetime "
            + " INNER JOIN person pe ON ultimaLinha.patient_id = pe.person_id "
            + " where obs.concept_id = 21151 and obs.value_coded = 21150 and obs.voided = 0 and obs.location_id = :location "
            + " AND (TIMESTAMPDIFF(year, birthdate, :endInclusionDate)) > 2 AND (TIMESTAMPDIFF(year, birthdate, :endInclusionDate)) < 15 "
            + " AND birthdate IS NOT NULL AND pe.voided = 0) query ";

    public static final String findCHILDRENBETWEEN2AND15WhohaveCVMoreThan1000CopiesByQueryB2 =
        " Select patient_id from (select e.patient_id, min(e.encounter_datetime) AS encounter_datetime "
            + " from "
            + " encounter e "
            + " inner join obs o ON o.encounter_id = e.encounter_id "
            + " INNER JOIN person pe ON e.patient_id = pe.person_id "
            + " WHERE e.voided = 0 AND e.encounter_type = 6 AND o.voided = 0 AND o.concept_id = 856 "
            + " and o.value_numeric > 1000 and e.location_id = :location "
            + " AND e.encounter_datetime BETWEEN :startInclusionDate AND :endInclusionDate "
            + " AND (TIMESTAMPDIFF(year, birthdate, :endInclusionDate)) > 2 AND (TIMESTAMPDIFF(year, birthdate, :endInclusionDate)) < 15 "
            + " AND birthdate IS NOT NULL AND pe.voided = 0 "
            + " group by e.patient_id) query ";
  }
}
