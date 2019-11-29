package org.openmrs.module.eptsreports.reporting.library.queries;

public class DsdQueries {

  public static String getPatientsEnrolledOnGAAC() {
    String query =
        "SELECT gm.member_id FROM gaac g INNER JOIN gaac_member gm ON g.gaac_id=gm.gaac_id "
            + "WHERE gm.start_date<:endDate AND gm.voided=0 AND g.voided=0 AND ((leaving is null) "
            + "OR (leaving=0) OR (leaving=1 AND gm.end_date>:endDate)) AND location_id=:location";
    return query;
  }

  public static String getPatientsParticipatingInDsdModel(
      int prevencaoPositivaInicialEncounterType,
      int prevencaoPositivaSeguimentoEncounterType,
      Integer gaac,
      Integer af,
      Integer ca,
      Integer pu,
      Integer fr,
      Integer dt,
      Integer dc,
      Integer otherModel,
      Integer valueCoded1,
      Integer valueCoded2) {

    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id "
            + "JOIN obs o ON p.patient_id=o.person_id WHERE e.encounter_id IN (%d,%d) AND o.concept_id "
            + "IN (%d, %d, %d, %d, %d, %d, %d, %d) "
            + "AND o.value_coded IN (%d, %d) AND e.encounter_datetime=:endDate";

    return String.format(
        query,
        prevencaoPositivaInicialEncounterType,
        prevencaoPositivaSeguimentoEncounterType,
        gaac,
        af,
        ca,
        pu,
        fr,
        dt,
        dc,
        otherModel,
        valueCoded1,
        valueCoded2);
  }

  /**
   * Get Patients with Recent Viral Load Encounter in the last 12 Months for DSD 1 criteria
   *
   * @param adultSeguimentoEncounterTypeId
   * @param pediatriaSeguimentoEncounterTypeId
   * @param labEncounterTypeId
   * @param masterCardEncounterTypeId
   * @param hivViralLoadConceptId
   * @param hivViralLoadQualitativeConceptId
   * @return
   */
  public static String patientsWithTheRecentViralLoadEncounter(
      int adultSeguimentoEncounterTypeId,
      int pediatriaSeguimentoEncounterTypeId,
      int labEncounterTypeId,
      int masterCardEncounterTypeId,
      int hivViralLoadConceptId,
      int hivViralLoadQualitativeConceptId) {
    String query =
        "SELECT vl_final.patient_id FROM ( "
            + "SELECT vl.patient_id, MAX(vl.latest_date) date FROM ( "
            + "SELECT p.patient_id, MAX(e.encounter_datetime) latest_date FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON p.patient_id=o.person_id "
            + "WHERE e.encounter_type IN ( "
            + adultSeguimentoEncounterTypeId
            + ","
            + pediatriaSeguimentoEncounterTypeId
            + ","
            + labEncounterTypeId
            + " ) AND  o.concept_id IN ( "
            + hivViralLoadConceptId
            + ","
            + hivViralLoadQualitativeConceptId
            + " ) AND "
            + "e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + "AND e.location_id=:location AND p.voided=0 AND e.voided=0 AND o.voided=0 GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id, MAX(o.obs_datetime) latest_date  FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON p.patient_id=o.person_id "
            + "WHERE e.encounter_type= "
            + masterCardEncounterTypeId
            + "AND  o.concept_id= "
            + hivViralLoadConceptId
            + " AND "
            + "o.obs_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + "AND e.location_id=:location AND p.voided=0 AND e.voided=0 AND o.voided=0 GROUP BY p.patient_id)vl GROUP BY vl.patient_id) vl_final ";

    return query;
  }

  /**
   * Get Patients with Viral Load less than 1000 in the last 12 Months for DSD 1 criteria
   *
   * @param hivViralLoadConceptId
   * @param hivViralLoadQualitativeConceptId
   * @param beyondDetectableLimitConceptId
   * @param undetectableViralLoadConceptId
   * @param lessThan10CopiesConceptId
   * @param lessThan20CopiesConceptId
   * @param lessThan40CopiesConceptId
   * @param lessThan400CopiesConceptId
   * @return
   */
  public static String patientsWithViralLoadLessThan1000(
      int hivViralLoadConceptId,
      int hivViralLoadQualitativeConceptId,
      int beyondDetectableLimitConceptId,
      int undetectableViralLoadConceptId,
      int lessThan10CopiesConceptId,
      int lessThan20CopiesConceptId,
      int lessThan40CopiesConceptId,
      int lessThan400CopiesConceptId) {
    String query =
        "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON p.patient_id=o.person_id "
            + "WHERE o.concept_id=%d "
            + " AND o.value_numeric < 1000 AND e.location_id=:location AND "
            + "e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + "AND p.voided=0 AND e.voided=0 AND o.voided=0 "
            + "UNION "
            + "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON p.patient_id=o.person_id "
            + "WHERE o.concept_id=%d "
            + " AND o.value_coded IN (%d,%d,%d,%d,%d,%d  ) AND e.location_id=:location "
            + "AND e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + "AND p.voided=0 AND e.voided=0 AND o.voided=0";

    return String.format(query,
            hivViralLoadConceptId,
            hivViralLoadQualitativeConceptId,
            beyondDetectableLimitConceptId,
            undetectableViralLoadConceptId,
            lessThan10CopiesConceptId,
            lessThan20CopiesConceptId,
            lessThan40CopiesConceptId,
            lessThan400CopiesConceptId);
  }

  /**
   * @param adultSeguimentoEncounter
   * @param pediatriaSeguimentoEncounter
   * @param whoStage3
   * @param whoStage4
   * @return
   */
  public static String getPatientsWithWHOStage3Or4(
      int currentWHOHIVStageConcept,
      int adultSeguimentoEncounter,
      int pediatriaSeguimentoEncounter,
      int whoStage3,
      int whoStage4) {

    String query =
        "SELECT encounters.patient_id "
            + "FROM ("
            + "SELECT ordered.patient_id, ordered.encounter_id, MAX(ordered.encounter_datetime) AS encounter_datetime "
            + "FROM ("
            + "SELECT e.patient_id, e.encounter_id, e.encounter_datetime "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "WHERE p.voided=0 AND e.voided=0 "
            + "AND e.encounter_type IN ("
            + adultSeguimentoEncounter
            + ","
            + pediatriaSeguimentoEncounter
            + ")AND e.encounter_datetime <= :endDate "
            + "AND e.location_id= :location "
            + "GROUP BY e.patient_id, e.encounter_id "
            + "ORDER BY e.encounter_datetime DESC "
            + ") ordered "
            + "GROUP BY ordered.patient_id "
            + ") encounters "
            + "INNER JOIN obs o ON encounters.encounter_id=o.encounter_id "
            + "WHERE o.voided=0 "
            + "AND o.concept_id="
            + currentWHOHIVStageConcept
            + " AND o.value_coded IN ("
            + whoStage3
            + ","
            + whoStage4
            + ") GROUP BY patient_id";

    return query;
  }

  /**
   * Get All Patients On Sarcoma Karposi
   *
   * @param adultSeguimentoEncounter
   * @param pediatriaSeguimentoEncounter
   * @param otherDiagnosisConceptId
   * @param sarcomakarposiConceptId
   * @return
   */
  public static String getPatientsOnSarcomaKarposi(
      int adultSeguimentoEncounter,
      int pediatriaSeguimentoEncounter,
      int otherDiagnosisConceptId,
      int sarcomakarposiConceptId) {
    String query =
        "SELECT p.patient_id FROM patient p "
            + "JOIN encounter e ON p.patient_id=e.patient_id "
            + "JOIN obs o ON p.patient_id=o.person_id "
            + "WHERE e.encounter_type IN (%d,%d) AND o.concept_id=%d AND o.value_coded=%d AND e.location_id= :location AND e.encounter_datetime<= :endDate AND p.voided=0 AND e.voided=0 ";

    return String.format(
        query,
        adultSeguimentoEncounter,
        pediatriaSeguimentoEncounter,
        otherDiagnosisConceptId,
        sarcomakarposiConceptId);
  }
}
