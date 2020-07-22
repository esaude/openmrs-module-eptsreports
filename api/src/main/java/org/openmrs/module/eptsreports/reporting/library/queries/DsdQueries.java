package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;

public class DsdQueries {

  public static String getPatientsEnrolledOnGAAC() {
    String query =
        "SELECT gm.member_id FROM gaac g "
            + "INNER JOIN gaac_member gm "
            + "ON g.gaac_id=gm.gaac_id "
            + "WHERE gm.start_date < :endDate "
            + "AND gm.voided = 0 "
            + "AND g.voided = 0 "
            + "AND ((leaving is null) OR (leaving = 0) OR (leaving = 1 AND gm.end_date > :endDate)) "
            + "AND location_id = :location";
    return query;
  }

  /*
   * Get Patients who participate in at least one of the following measured DSD model (AF, CA, PU, DC)
   *
   * @return String
   * */
  public static String getPatientsParticipatingInAfCaPuFrDcDsdModels() {

    String query =
        ""
            + "SELECT "
            + "	p.patient_id "
            + "FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON p.patient_id=o.person_id "
            + "WHERE e.encounter_id = %d "
            + "	AND o.concept_id IN (%d, %d, %d) "
            + "	AND o.value_coded IN (%d, %d) "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "	AND e.location_id = :location";

    return String.format(
        query,
        new HivMetadata().getAdultoSeguimentoEncounterType().getEncounterTypeId(),
        new HivMetadata().getFamilyApproach().getConceptId(), // fa
        new HivMetadata().getAccessionClubs().getConceptId(), // ca
        new HivMetadata().getCommunityDispensation().getConceptId(), // dc
        new HivMetadata().getStartDrugs().getConceptId(),
        new HivMetadata().getContinueRegimenConcept().getConceptId());
  }

  /**
   * Looks for patients who had last drug pickup within last 5 months from end date
   *
   * <p>returns @String
   */
  public static String patientsWithLastDrugPickupWithin5monthsFromEndDate() {
    String query =
        ""
            + "SELECT "
            + "	DISTINCT ee.patient_id "
            + "FROM encounter ee "
            + "INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + "WHERE "
            + "	ee.encounter_id = ee.encounter_id AND "
            + "	ee.encounter_type = %d "
            + "	AND oo.concept_id = %d  "
            + "	AND oo.value_datetime BETWEEN DATE_ADD(:endDate, INTERVAL -5 MONTH) AND :endDate "
            + "	AND oo.voided = 0 "
            + "	AND ee.voided = 0 "
            + "	AND ee.location_id = :location ";

    return String.format(
        query,
        new HivMetadata().getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
        new HivMetadata().getArtDatePickupMasterCard().getConceptId());
  }

  /**
   * Looks for patients who had next drug pickup scheduled for 3 months later who had an encounter
   * within the last 5 months from end date
   *
   * <p>returns @String
   */
  public static String patientsWithNextDrugPickupScheduled3MonthsLater() {
    String query =
        ""
            + "SELECT "
            + "	DISTINCT ee.patient_id "
            + "FROM encounter ee "
            + "INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + "WHERE "
            + "	ee.encounter_type = %d "
            + "	AND oo.concept_id = %d "
            + "	AND TIMESTAMPDIFF(MONTH, ee.encounter_datetime, oo.value_datetime) >= 3 "
            + "	AND oo.voided = 0 "
            + "	AND ee.voided = 0 "
            + "	AND ee.encounter_datetime BETWEEN DATE_ADD(:endDate, INTERVAL -5 MONTH) AND :endDate "
            + "	AND ee.location_id = :location";

    return String.format(
        query,
        new HivMetadata().getARVPharmaciaEncounterType().getEncounterTypeId(),
        new HivMetadata().getReturnVisitDateForArvDrugConcept().getConceptId());
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

    Map<String, Integer> map = new HashMap<>();
    map.put("hivViralLoadConceptId", hivViralLoadConceptId);
    map.put("hivViralLoadQualitativeConceptId", hivViralLoadQualitativeConceptId);
    map.put("beyondDetectableLimitConceptId", beyondDetectableLimitConceptId);
    map.put("undetectableViralLoadConceptId", undetectableViralLoadConceptId);
    map.put("lessThan10CopiesConceptId", lessThan10CopiesConceptId);
    map.put("lessThan20CopiesConceptId", lessThan20CopiesConceptId);
    map.put("lessThan40CopiesConceptId", lessThan40CopiesConceptId);
    map.put("lessThan400CopiesConceptId", lessThan400CopiesConceptId);

    String query =
        "SELECT p.patient_id FROM patient p "
            + "		INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "		INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE o.concept_id=${hivViralLoadConceptId} "
            + "		AND o.value_numeric < 1000 AND e.location_id=:location "
            + "		AND e.encounter_datetime "
            + "				BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + "		AND p.voided=0 AND e.voided=0 AND o.voided=0 "
            + "UNION "
            + "SELECT p.patient_id FROM patient p "
            + "		INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "		INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE o.concept_id=${hivViralLoadQualitativeConceptId} "
            + " 	AND o.value_coded IN (${beyondDetectableLimitConceptId},"
            + "${undetectableViralLoadConceptId},"
            + "${lessThan10CopiesConceptId},"
            + "${lessThan20CopiesConceptId},"
            + "${lessThan40CopiesConceptId},"
            + "${lessThan400CopiesConceptId}"
            + ") 	AND e.location_id=:location "
            + "		AND e.encounter_datetime "
            + "				BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + " 	AND p.voided=0 AND e.voided=0 AND o.voided=0";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replaced = sb.replace(query);

    return replaced;
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
   *
   * @param encounterTypeId
   * @param lastCommunityConceptId
   * @param startDrugsConceptId
   * @param continueRegimenConceptId
   * @return query
   */
  public static String getPatientsWithDispense(
      int encounterTypeId,
      int lastCommunityConceptId,
      int startDrugsConceptId,
      int continueRegimenConceptId) {
    String query =
        "select "
            + " p.patient_id FROM patient p "
            + " JOIN  "
            + " encounter e ON "
            + "    p.patient_id = e.patient_id "
            + " JOIN "
            + " obs o  ON "
            + "    p.patient_id = o.person_id "
            + " WHERE "
            + " e.encounter_type=%d "
            + "    AND o.concept_id=%d "
            + "    AND o.value_coded in (%d,%d) AND e.location_id= :location  "
            + "    AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "    AND e.voided=0 AND o.voided=0 AND p.voided=0";

    return String.format(
        query,
        encounterTypeId,
        lastCommunityConceptId,
        startDrugsConceptId,
        continueRegimenConceptId);
  }
  /**
   * N5: Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment)
   * who are in AF
   *
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
      int continueRegimenConceptId) {
    Map<String, Integer> map = new HashMap<>();
    map.put("adultSeguimentoEncounterTypeId", adultSeguimentoEncounterTypeId);
    map.put("lastFamilyApproachConceptId", lastFamilyApproachConceptId);
    map.put("startDrugsConceptId", startDrugsConceptId);
    map.put("continueRegimenConceptId", continueRegimenConceptId);
    String query =
        "SELECT last_abordagem_familiar.patient_id "
            + "FROM ( "
            + "      SELECT p.patient_id, max(e.encounter_datetime)  "
            + "      FROM patient p  "
            + "        JOIN encounter e ON p.patient_id=e.patient_id  "
            + "        JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "      WHERE e.encounter_type= ${adultSeguimentoEncounterTypeId}  "
            + "        AND o.concept_id= ${lastFamilyApproachConceptId}  "
            + "        AND o.value_coded IN (${startDrugsConceptId},${continueRegimenConceptId})  "
            + "        AND e.location_id= :location  "
            + "        AND e.encounter_datetime <= :endDate   "
            + "        AND e.voided=0  "
            + "        AND o.voided=0  "
            + "        AND p.voided=0  "
            + "      GROUP BY p.patient_id "
            + "      ) last_abordagem_familiar";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);

    return replacedQuery;
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
        ""
            + "SELECT "
            + " p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE "
            + "e.encounter_type IN (%d,%d) AND o.concept_id=%d AND o.value_coded=%d AND e.location_id= :location AND e.encounter_datetime<= :endDate AND p.voided=0 AND e.voided=0 AND o.voided=0";

    return String.format(
        query,
        adultSeguimentoEncounter,
        pediatriaSeguimentoEncounter,
        otherDiagnosisConceptId,
        sarcomakarposiConceptId);
  }

  public static String patientsWithLastFollowUpConsultationWithinLast7MonthsFromEndDate() {
    String query =
        ""
            + "SELECT "
            + "	DISTINCT ee.patient_id "
            + "FROM encounter ee "
            + "INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + "WHERE "
            + "	ee.encounter_type IN (%d, %d) "
            + "	AND oo.voided = 0 "
            + "	AND ee.voided = 0 "
            + "	AND ee.encounter_datetime BETWEEN DATE_ADD(:endDate, INTERVAL -7 MONTH) AND :endDate "
            + "	AND ee.location_id = :location";

    return String.format(
        query,
        new HivMetadata().getAdultoSeguimentoEncounterType().getEncounterTypeId(),
        new HivMetadata().getPediatriaSeguimentoEncounterType().getEncounterTypeId());
  }

  public static String patientsWithNextApptmt6MonthsAfterConsultationDate() {
    String query =
        ""
            + "SELECT "
            + "	DISTINCT ee.patient_id "
            + "FROM encounter ee "
            + "INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + "WHERE "
            + "	ee.encounter_type IN (%d, %d) "
            + "	AND oo.concept_id = %d    "
            + "	AND TIMESTAMPDIFF(MONTH, ee.encounter_datetime, oo.value_datetime) >= 6 "
            + "	AND oo.voided = 0 "
            + "	AND ee.voided = 0 "
            + "	AND ee.encounter_datetime BETWEEN DATE_ADD(:endDate, INTERVAL -6 MONTH) AND :endDate "
            + "	AND ee.location_id = :location";

    return String.format(
        query,
        new HivMetadata().getAdultoSeguimentoEncounterType().getEncounterTypeId(),
        new HivMetadata().getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
        new CommonMetadata().getReturnVisitDateConcept().getConceptId());
  }
}
