package org.openmrs.module.eptsreports.reporting.library.queries;

public class Eri4MonthsQueries {

  /**
   * Select all patients (adults and children) who ever initiated the treatment by end of reporting
   * period which include All patients who have initiated the drugs (ARV PLAN = START DRUGS) at the
   * pharmacy or clinical visits (adults and children) by end of reporting period. All patients who
   * have historical start drugs date set in pharmacy (FILA) or in clinical forms (Ficha de
   * Seguimento Adulto end Ficha de seguimento Crianca ) by end of reporting period. All patients
   * enrolled in ART Program by end of reporting period and All patients who have picked up drugs
   * (at least one pharmacy visit) by end of reporting period
   *
   * @return a union of cohort
   */
  public static String
      allPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate(
          int arvPharmaciaEncounter,
          int arvAdultoSeguimentoEncounter,
          int arvPediatriaSeguimentoEncounter,
          int arvPlanConcept,
          int startDrugsConcept,
          int historicalDrugsConcept,
          int artProgram,
          int artPickupConcept,
          int yesConcept,
          int artPickupDateConcept,
          int mastercardDrugPickupEncounterType) {

    String sql =
        "SELECT inicio_real.patient_id "
            + "FROM   (SELECT patient_id, "
            + "               data_inicio "
            + "        FROM   (SELECT patient_id, "
            + "                       Min(data_inicio) data_inicio "
            + "                FROM   (SELECT p.patient_id, "
            + "                               Min(e.encounter_datetime) data_inicio "
            + "                        FROM   patient p "
            + "                               INNER JOIN encounter e "
            + "                                       ON p.patient_id = e.patient_id "
            + "                               INNER JOIN obs o "
            + "                                       ON o.encounter_id = e.encounter_id "
            + "                        WHERE  e.voided = 0 "
            + "                               AND o.voided = 0 "
            + "                               AND p.voided = 0 "
            + "                               AND e.encounter_type IN( %d, %d, %d ) "
            + "                               AND o.concept_id = %d "
            + "                               AND o.value_coded = %d "
            + "                               AND e.encounter_datetime <= :endDate "
            + "                               AND e.location_id = :location "
            + "                        GROUP  BY p.patient_id "
            + "                        UNION "
            + "                        SELECT p.patient_id, "
            + "                               Min(value_datetime) data_inicio "
            + "                        FROM   patient p "
            + "                               INNER JOIN encounter e "
            + "                                       ON p.patient_id = e.patient_id "
            + "                               INNER JOIN obs o "
            + "                                       ON e.encounter_id = o.encounter_id "
            + "                        WHERE  p.voided = 0 "
            + "                               AND e.voided = 0 "
            + "                               AND o.voided = 0 "
            + "                               AND e.encounter_type IN ( %d, %d, %d ) "
            + "                               AND o.concept_id = %d "
            + "                               AND o.value_datetime IS NOT NULL "
            + "                               AND o.value_datetime <= :endDate "
            + "                               AND e.location_id = :location "
            + "                        GROUP  BY p.patient_id "
            + "                        UNION "
            + "                        SELECT pg.patient_id, "
            + "                               date_enrolled AS data_inicio "
            + "                        FROM   patient p "
            + "                               INNER JOIN patient_program pg "
            + "                                       ON p.patient_id = pg.patient_id "
            + "                        WHERE  pg.voided = 0 "
            + "                               AND p.voided = 0 "
            + "                               AND program_id = %d "
            + "                               AND date_enrolled <= :endDate "
            + "                               AND location_id = :location "
            + "                        UNION "
            + "                        SELECT e.patient_id, "
            + "                               Min(e.encounter_datetime) AS data_inicio "
            + "                        FROM   patient p "
            + "                               INNER JOIN encounter e "
            + "                                       ON p.patient_id = e.patient_id "
            + "                        WHERE  p.voided = 0 "
            + "                               AND e.encounter_type = %d "
            + "                               AND e.voided = 0 "
            + "                               AND e.encounter_datetime <= :endDate "
            + "                               AND e.location_id = :location "
            + "                        GROUP  BY p.patient_id "
            + "                        UNION "
            + "                        SELECT e.patient_id, "
            + "                               Min(pickupdate.value_datetime) AS data_inicio"
            + "                        FROM   patient p "
            + "                               JOIN encounter e "
            + "                                 ON p.patient_id = e.patient_id "
            + "                               JOIN obs pickup "
            + "                                 ON e.encounter_id = pickup.encounter_id "
            + "                               JOIN obs pickupdate "
            + "                                 ON e.encounter_id = pickupdate.encounter_id "
            + "                        WHERE  p.voided = 0 "
            + "                               AND pickup.voided = 0 "
            + "                               AND pickup.concept_id = %d "
            + "                               AND pickup.value_coded = %d "
            + "                               AND pickupdate.voided = 0 "
            + "                               AND pickupdate.concept_id = %d "
            + "                               AND pickupdate.value_datetime <= :endDate "
            + "                               AND e.encounter_type = %d "
            + "                               AND e.voided = 0 "
            + "                               AND e.location_id = :location"
            + "                          GROUP  BY e.patient_id ) inicio "
            + "                GROUP  BY patient_id) inicio1 "
            + "        WHERE  data_inicio BETWEEN :startDate AND :endDate) inicio_real "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = inicio_real.patient_id "
            + "       INNER JOIN obs o "
            + "               ON e.encounter_id = o.encounter_id "
            + "WHERE  e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND ((e.encounter_type IN( %d, %d, %d ) "
            + "       AND e.encounter_datetime BETWEEN Date_add(inicio_real.data_inicio, "
            + "                                        INTERVAL 61 day) "
            + "                                        AND "
            + "                                            Date_add( "
            + "                                            inicio_real.data_inicio, INTERVAL "
            + "                                            120 day))"
            + "       OR (e.encounter_type = %d "
            + "       AND o.value_datetime BETWEEN Date_add(inicio_real.data_inicio, "
            + "                                        INTERVAL 61 day) "
            + "                                        AND "
            + "                                            Date_add( "
            + "                                            inicio_real.data_inicio, INTERVAL "
            + "                                            120 day)))"
            + "GROUP  BY inicio_real.patient_id ";

    return String.format(
        sql,
        arvPharmaciaEncounter,
        arvAdultoSeguimentoEncounter,
        arvPediatriaSeguimentoEncounter,
        arvPlanConcept,
        startDrugsConcept,
        arvPharmaciaEncounter,
        arvAdultoSeguimentoEncounter,
        arvPediatriaSeguimentoEncounter,
        historicalDrugsConcept,
        artProgram,
        arvPharmaciaEncounter,
        artPickupConcept,
        yesConcept,
        artPickupDateConcept,
        mastercardDrugPickupEncounterType,
        arvPharmaciaEncounter,
        arvAdultoSeguimentoEncounter,
        arvPediatriaSeguimentoEncounter,
        mastercardDrugPickupEncounterType);
  }

  // TODO: harmonise with LTFU queries from TxCurr
  public static String getPatientsLostToFollowUpOnDrugPickup(
      int arvFarmacyEncounterType, int drugPickupReturnVisitDateConcept, int daysThreshold) {
    String query =
        "SELECT patient_id FROM "
            + "(SELECT patient_id,value_datetime FROM "
            + "( SELECT p.patient_id,MAX(encounter_datetime) AS encounter_datetime FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id WHERE p.voided=0 AND e.voided=0 "
            + "AND e.encounter_type in(%d) AND e.location_id=:location AND e.encounter_datetime<=:endDate GROUP BY p.patient_id ) max_frida "
            + "INNER JOIN obs o ON o.person_id=max_frida.patient_id WHERE max_frida.encounter_datetime=o.obs_datetime AND "
            + "o.voided=0 AND o.concept_id=%d AND o.location_id=:location AND encounter_datetime BETWEEN "
            + ":startDate and :endDate "
            + ") final WHERE datediff(:endDate,final.value_datetime)>=%d";
    return String.format(
        query, arvFarmacyEncounterType, drugPickupReturnVisitDateConcept, daysThreshold);
  }

  public static String getPatientsLostToFollowUpOnConsultation(
      int adultEncounteyType, int paedEncounterType, int returnVisitConcept, int daysThreshold) {
    String query =
        "SELECT patient_id FROM "
            + "(SELECT patient_id, value_datetime FROM( SELECT p.patient_id,MAX(encounter_datetime)AS encounter_datetime "
            + "FROM patient p INNER JOIN encounter e ON e.patient_id=p.patient_id WHERE p.voided=0 AND e.voided=0 AND "
            + "e.encounter_type in (%d, %d) AND e.location_id=:location AND e.encounter_datetime<=:endDate "
            + "GROUP BY p.patient_id ) max_mov INNER JOIN obs o ON o.person_id=max_mov.patient_id "
            + "WHERE max_mov.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id=%d"
            + " AND o.location_id=:location "
            + "AND encounter_datetime BETWEEN :startDate and :endDate "
            + ") final WHERE datediff(:endDate,final.value_datetime)>=%d";
    return String.format(
        query, adultEncounteyType, paedEncounterType, returnVisitConcept, daysThreshold);
  }
}
