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
        + ") AND e.encounter_datetime BETWEEN date_add(:endDate, interval -9 MONTH) AND :endDate AND e.location_id=:location "
        + " UNION "
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
        + ") AND e.encounter_datetime BETWEEN date_add(:endDate, interval -9 MONTH) AND :endDate AND e.location_id=:location "
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
        + ") AND e.encounter_datetime BETWEEN date_add(:endDate, interval -9 MONTH) AND :endDate AND e.location_id=:location "
        + " UNION"
        + " SELECT pp.patient_id FROM patient_program pp"
        + " INNER JOIN person pe ON pp.patient_id=pe.person_id"
        + " WHERE pp.program_id="
        + etvProgram
        + " AND pp.voided=0 AND pp.date_enrolled BETWEEN date_add(:endDate, interval -9 MONTH) AND :endDate AND pp.location_id=:location ";
  }

  /**
   * Get the raw sql query for breastfeeding patients with period as 18 months from the endDate
   *
   * @param deliveryDateConcept
   * @param arvInitiationConcept
   * @param lactationConcept
   * @param registeredBreastfeedingConcept
   * @param yesConcept
   * @param ptvProgram
   * @param gaveBirthState
   * @param adultInitialEncounter
   * @param adultSegEncounter
   * @return
   */
  public static String getBreastfeedingPatientsDsd(
      int deliveryDateConcept,
      int arvInitiationConcept,
      int lactationConcept,
      int registeredBreastfeedingConcept,
      int yesConcept,
      int ptvProgram,
      int gaveBirthState,
      int adultInitialEncounter,
      int adultSegEncounter) {

    return " SELECT p.patient_id"
        + " FROM patient p"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_datetime BETWEEN date_add(:endDate, interval -18 MONTH) AND :endDate AND concept_id="
        + deliveryDateConcept
        + " AND"
        + " e.encounter_type in ("
        + adultInitialEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + " UNION "
        + "SELECT  p.patient_id"
        + " FROM patient p"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_datetime BETWEEN date_add(:endDate, interval -18 MONTH) AND :endDate AND concept_id="
        + arvInitiationConcept
        + " AND value_coded="
        + lactationConcept
        + " AND e.encounter_type IN ("
        + adultInitialEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location)"
        + " UNION "
        + " SELECT p.patient_id"
        + " FROM patient p"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_datetime BETWEEN date_add(:endDate, interval -18 MONTH) AND :endDate AND concept_id="
        + registeredBreastfeedingConcept
        + " AND value_coded="
        + yesConcept
        + " AND"
        + " e.encounter_type IN ("
        + adultInitialEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + "UNION "
        + "SELECT 	pg.patient_id"
        + " FROM patient p"
        + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id"
        + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND ps.start_date BETWEEN date_add(:endDate, interval -18 MONTH) AND :endDate AND"
        + " pg.program_id="
        + ptvProgram
        + " AND ps.state="
        + gaveBirthState
        + " AND ps.end_date IS NULL AND"
        + " location_id IN(:location)";
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
        " SELECT encounters.patient_id"
            + " FROM ("
            + " SELECT p.patient_id, e.encounter_id, MAX(e.encounter_datetime) e_datetime"
            + " FROM patient p"
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 "
            + " AND e.encounter_type IN ("
            + adultSeguimentoEncounter
            + ","
            + pediatriaSeguimentoEncounter
            + ")"
            + " AND e.encounter_datetime <= :endDate"
            + " AND e.location_id=6"
            + " GROUP BY e.encounter_id"
            + " ) encounters"
            + " INNER JOIN obs o ON encounters.encounter_id=o.encounter_id"
            + " WHERE o.voided=0 "
            + " AND o.concept_id="
            + currentWHOHIVStageConcept
            + " AND o.value_coded IN ("
            + whoStage3
            + ","
            + whoStage4
            + ") GROUP BY encounters.patient_id ";

    return query;
  }
}
