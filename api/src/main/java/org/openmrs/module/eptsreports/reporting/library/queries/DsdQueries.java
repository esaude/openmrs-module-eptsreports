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
   * Get Patients with Viral Load less than 1000 in the last 12 Months for DSD criteria 5B
   *
   * @param labEncounter
   * @param adultSeguimentoEncounter
   * @param pediatriaSeguimentoEncounter
   * @param vlConceptQuestion
   * @return
   */
  public static String patientsWithViralLoadLessThan1000(
      int labEncounter,
      int adultSeguimentoEncounter,
      int pediatriaSeguimentoEncounter,
      int vlConceptQuestion) {
    String query =
        "SELECT p.patient_id "
            + " FROM patient p"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN ("
            + labEncounter
            + ","
            + adultSeguimentoEncounter
            + ","
            + pediatriaSeguimentoEncounter
            + ")"
            + " AND  o.concept_id="
            + vlConceptQuestion
            + " AND o.value_numeric IS NOT NULL AND"
            + " e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + " AND e.location_id=:location "
            + " AND o.value_numeric < 1000"
            + " GROUP BY p.patient_id";

    return query;
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
   * N5: Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment)
   * who are in AF
   * @param adultSeguimentoEncounterTypeId
   * @param lastFamilyApproachConceptId
   * @param startDrugsConceptId
   * @param continueRegimenConceptId
   * @return
   */
  public static String getPatientsOnMasterCardAF(
          int adultSeguimentoEncounterTypeId,
          int lastFamilyApproachConceptId,
          int startDrugsConceptId,
          int continueRegimenConceptId
  ){
    String query = "SELECT p.patient_id FROM patient p " +
            "JOIN encounter e ON p.patient_id=e.patient_id " +
            "JOIN obs o ON p.patient_id=o.person_id " +
            "WHERE e.encounter_type=%d AND o.concept_id=%d AND o.value_coded IN (%d, %d) AND e.location_id=:location " +
            "AND e.encounter_datetime BETWEEN :startDate AND :endDate  AND e.voided=0 AND o.voided=0 AND p.voided=0 ";

    return String.format(query,
            adultSeguimentoEncounterTypeId,
            lastFamilyApproachConceptId,
            startDrugsConceptId,
            continueRegimenConceptId);
  }
}
