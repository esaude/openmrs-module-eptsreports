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
   * Fetch patients who are pregnant based on DSD criteria: check in last 9 months from endDate
   *
   * @param pregnantConcept
   * @param gestationConcept
   * @param weeksPregnantConcept
   * @param eddConcept
   * @param adultInitailEncounter
   * @param adultSegEncounter
   * @param etvProgram
   * @return
   */
  public static String getPregnantWhileOnArtDsd(
      int pregnantConcept,
      int gestationConcept,
      int weeksPregnantConcept,
      int eddConcept,
      int adultInitailEncounter,
      int adultSegEncounter,
      int etvProgram) {

    return "SELECT  p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + pregnantConcept
        + " AND value_coded="
        + gestationConcept
        + " AND e.encounter_type in ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") AND e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -9 MONTH) AND :endDate AND e.location_id=:location "
        + " union"
        + " SELECT p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + weeksPregnantConcept
        + " AND"
        + " e.encounter_type in ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") AND e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -9 MONTH) AND :endDate AND e.location_id=:location "
        + " UNION"
        + " SELECT p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + eddConcept
        + " AND"
        + " e.encounter_type in ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") AND e.encounter_datetime BETWEEN date_add(date_add(:endDate, interval -9 MONTH) AND :endDate AND e.location_id=:location "
        + " UNION"
        + " SELECT pp.patient_id FROM patient_program pp"
        + " INNER JOIN person pe ON pp.patient_id=pe.person_id"
        + " WHERE pp.program_id="
        + etvProgram
        + " AND pp.voided=0 AND pp.date_enrolled BETWEEN date_add(date_add(:endDate, interval -9 MONTH) AND :endDate AND pp.location_id=:location ";
  }
}
