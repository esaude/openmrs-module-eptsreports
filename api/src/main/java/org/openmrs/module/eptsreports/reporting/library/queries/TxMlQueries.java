package org.openmrs.module.eptsreports.reporting.library.queries;

public class TxMlQueries {

  public static String getPatientsWhoMissedAppointment(
      int min,
      int max,
      int returnVisitDateForDrugsConcept,
      int returnVisitDate,
      int pharmacyEncounterType,
      int adultoSequimento,
      int arvPediatriaSeguimento) {
    String query =
        "SELECT patient_id FROM "
            + "(SELECT p.patient_id,MAX(o.value_datetime) return_date "
            + "FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id AND e.encounter_datetime <=:endDate AND e.location_id=:location "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id AND o.obs_datetime <=:endDate AND o.location_id=:location "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided=0 "
            + "AND e.encounter_type IN (%d, %d, %d) "
            + "AND o.concept_id in (%d, %d) "
            + "AND e.location_id =:location "
            + "GROUP BY p.patient_id "
            + ")lost_patients WHERE DATEDIFF(:endDate,lost_patients.return_date)>=%d AND DATEDIFF(:endDate,lost_patients.return_date)<=%d";
    return String.format(
        query,
        pharmacyEncounterType,
        adultoSequimento,
        arvPediatriaSeguimento,
        returnVisitDateForDrugsConcept,
        returnVisitDate,
        min,
        max);
  }

  public static String getNonConsentedPatients(
      int prevencaoPositivaInicial,
      int prevencaoPositivaSeguimento,
      int acceptContactConcept,
      int noConcept) {
    String query =
        "SELECT distinct(pp.patient_id) FROM patient pp "
            + "INNER JOIN encounter e ON e.patient_id=pp.patient_id "
            + "INNER JOIN obs o ON o.person_id = pp.patient_id "
            + "INNER JOIN person p ON o.person_id = p.person_id "
            + "WHERE pp.voided=0 AND e.voided=0 AND e.encounter_type IN(%d, %d) AND e.location_id=:location AND o.obs_datetime<=:endDate AND o.voided=0 AND o.concept_id=%d AND o.value_coded=%d AND o.location_id=:location "
            + "AND o.obs_id = (SELECT obs_id FROM obs WHERE concept_id = %d AND pp.patient_id = person_id GROUP BY obs_datetime DESC LIMIT 1)";

    return String.format(
        query,
        prevencaoPositivaInicial,
        prevencaoPositivaSeguimento,
        acceptContactConcept,
        noConcept,
        acceptContactConcept);
  }

  public static String getTransferredOutPatients(int program, int state) {
    String query =
        "SELECT pg.patient_id"
            + " FROM patient p"
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND"
            + " pg.program_id=%d"
            + " AND ps.state=%d"
            + " AND ps.start_date BETWEEN (:endDate - INTERVAL 183 DAY) AND  :endDate AND pg.location_id=:location AND ps.end_date is null";
    return String.format(query, program, state);
  }

  // All Patients marked as Dead in the patient home visit card
  public static String getPatientsMarkedDeadInHomeVisitCard(
      int homeVisitCardEncounterTypeId,
      int apoioReintegracaoParteAEncounterTypeId,
      int apoioReintegracaoParteBEncounterTypeId,
      int busca,
      int dead) {
    String query =
        " SELECT     pa.patient_id "
            + "FROM       patient pa "
            + "INNER JOIN encounter e "
            + "ON         pa.patient_id=e.patient_id "
            + "INNER JOIN obs o "
            + "ON         pa.patient_id=o.person_id "
            + "WHERE      e.encounter_type IN (%d) "
            + "AND        o.concept_id= %d "
            + "AND        o.value_coded = %d "
            + "AND        e.location_id=:location "
            + "AND        o.obs_datetime <=:endDate";

    return String.format(
        query,
        homeVisitCardEncounterTypeId,
        apoioReintegracaoParteAEncounterTypeId,
        apoioReintegracaoParteBEncounterTypeId,
        busca,
        dead);
  }

  /*
   Untraced Patients Criteria 2
   Patients without Patient Visit Card without a set of observations
  */
  public static String getPatientsWithVisitCardAndWithoutObs(
      int pharmacyEncounterTypeId,
      int adultoSequimentoEncounterTypeId,
      int arvPediatriaSeguimentoEncounterTypeId,
      int returnVisitDateForDrugsConcept,
      int returnVisitDateConcept,
      int homeVisitCardEncounterTypeId,
      int apoioReintegracaoParteAEncounterTypeId,
      int apoioReintegracaoParteBEncounterTypeId,
      int typeOfVisitConcept,
      int buscaConcept,
      int secondAttemptConcept,
      int thirdAttemptConcept,
      int patientFoundConcept,
      int defaultingMotiveConcept,
      int reportVisitConcept1,
      int reportVisitConcept2,
      int patientFoundForwardedConcept,
      int reasonForNotFindingConcept,
      int whoGaveInformationConcept,
      int cardDeliveryDate) {
    String query =
        "SELECT pa.patient_id "
            + "FROM   patient pa "
            + " INNER JOIN ("
            + "      SELECT pa.patient_id,e.encounter_id FROM patient pa "
            + "       INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + "       INNER JOIN obs o ON pa.patient_id=o.person_id "
            + "       INNER JOIN ("
            + "          SELECT p.patient_id,MAX(e.encounter_datetime) return_date FROM patient p "
            + "             INNER JOIN encounter e ON e.patient_id = p.patient_id AND e.encounter_datetime <=:endDate AND e.location_id=:location "
            + "             INNER JOIN obs o ON o.encounter_id = e.encounter_id  AND o.location_id=:location  "
            + "          WHERE p.voided = 0 AND e.voided = 0 AND o.voided=0 "
            + "            AND e.encounter_type IN (%d,%d,%d) "
            + "            AND o.concept_id IN (%d, %d) "
            + "            AND e.location_id = :location "
            + "          GROUP BY p.patient_id)lp ON pa.patient_id=lp.patient_id "
            + "      WHERE e.encounter_datetime >= lp.return_date AND e.encounter_datetime<=:endDate "
            + "         AND e.encounter_type IN (%d, %d, %d) "
            + "         AND e.location_id=:location  "
            + "      GROUP BY pa.patient_id) visitCard on visitCard.patient_id = pa.patient_id "
            + "    LEFT JOIN obs visitType ON "
            + "        visitType.encounter_id = visitCard.encounter_id AND "
            + "        visitType.concept_id = %d AND "
            + "        visitType.value_coded = %d AND "
            + "        visitType.obs_datetime <= :endDate "
            + "    LEFT JOIN obs o ON "
            + "        o.encounter_id = visitCard.encounter_id AND "
            + "        o.concept_id IN (%d, %d, %d, %d, %d, %d, %d, %d, %d, %d) AND "
            + "        o.obs_datetime <= :endDate "
            + "   WHERE o.obs_id IS NULL OR visitType.obs_id IS NULL "
            + "GROUP  BY pa.patient_id ";

    return String.format(
        query,
        pharmacyEncounterTypeId,
        adultoSequimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        returnVisitDateConcept,
        returnVisitDateForDrugsConcept,
        homeVisitCardEncounterTypeId,
        apoioReintegracaoParteAEncounterTypeId,
        apoioReintegracaoParteBEncounterTypeId,
        typeOfVisitConcept,
        buscaConcept,
        secondAttemptConcept,
        thirdAttemptConcept,
        patientFoundConcept,
        defaultingMotiveConcept,
        reportVisitConcept1,
        reportVisitConcept2,
        patientFoundForwardedConcept,
        reasonForNotFindingConcept,
        whoGaveInformationConcept,
        cardDeliveryDate);
  }
  /*
       All Patients without “Patient Visit Card” (Encounter type 21 or 36 or 37) registered between
       ◦ the last scheduled appointment or drugs pick up (the most recent one) by reporting end date and
       ◦ the reporting end date
  */
  public static String getPatientsWithVisitCardRegisteredBtwnLastAppointmentOrDrugPickupAndEnddate(
      int pharmacyEncounterTypeId,
      int adultoSequimentoEncounterTypeId,
      int arvPediatriaSeguimentoEncounterTypeId,
      int returnVisitDateForDrugsConcept,
      int returnVisitDateConcept,
      int homeVisitCardEncounterTypeId,
      int apoioReintegracaoParteAEncounterTypeId,
      int apoioReintegracaoParteBEncounterTypeId) {

    String query =
        " SELECT pa.patient_id FROM patient pa "
            + "  INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + "  INNER JOIN obs o ON pa.patient_id=o.person_id "
            + "  INNER JOIN ("
            + "    SELECT p.patient_id,MAX(e.encounter_datetime) return_date FROM patient p "
            + "      INNER JOIN encounter e ON e.patient_id = p.patient_id AND e.encounter_datetime <=:endDate AND e.location_id=:location "
            + "      INNER JOIN obs o ON o.encounter_id = e.encounter_id  AND o.location_id=:location  "
            + "    WHERE p.voided = 0 AND e.voided = 0 AND o.voided=0 "
            + "      AND e.encounter_type IN (%d,%d,%d) "
            + "      AND o.concept_id IN (%d, %d) "
            + "      AND e.location_id =:location "
            + "    GROUP BY p.patient_id)lp ON pa.patient_id=lp.patient_id "
            + "  WHERE e.encounter_datetime >= lp.return_date AND e.encounter_datetime<=:endDate"
            + "  AND e.encounter_type IN (%d, %d, %d) "
            + "  AND e.location_id=:location  "
            + "  GROUP BY pa.patient_id";

    return String.format(
        query,
        pharmacyEncounterTypeId,
        adultoSequimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        returnVisitDateConcept,
        returnVisitDateForDrugsConcept,
        homeVisitCardEncounterTypeId,
        apoioReintegracaoParteAEncounterTypeId,
        apoioReintegracaoParteBEncounterTypeId);
  }

  // Traced Patients (Unable to locate)
  public static String getPatientsTracedWithVisitCard(
      int pharmacyEncounterTypeId,
      int adultoSequimentoEncounterTypeId,
      int arvPediatriaSeguimentoEncounterTypeId,
      int returnVisitDateForDrugsConcept,
      int returnVisitDateConcept,
      int homeVisitCardEncounterTypeId,
      int apoioReintegracaoParteAEncounterTypeId,
      int apoioReintegracaoParteBEncounterTypeId,
      int typeOfVisitConcept,
      int buscaConcept,
      int patientFoundConcept,
      int patientFoundAnswerConcept) {
    String query =
        "SELECT DISTINCT pa.patient_id FROM patient pa "
            + "    INNER JOIN ("
            + "        SELECT p.patient_id,MAX(e.encounter_datetime) return_date FROM patient p "
            + "            INNER JOIN encounter e ON e.patient_id = p.patient_id AND e.encounter_datetime <= :endDate AND e.location_id=:location "
            + "            INNER JOIN obs o ON o.encounter_id = e.encounter_id AND o.location_id= :location "
            + "        WHERE p.voided = 0 AND e.voided = 0 AND o.voided=0 "
            + "            AND e.encounter_type IN (%d,%d,%d) "
            + "            AND o.concept_id IN (%d, %d) "
            + "            AND e.location_id = :location "
            + "        GROUP BY p.patient_id)lp ON pa.patient_id=lp.patient_id "
            + "    INNER JOIN encounter e ON "
            + "        pa.patient_id=e.patient_id AND "
            + "        e.encounter_type IN (%d,%d,%d) AND "
            + "        e.location_id = :location AND "
            + "        e.encounter_datetime >= lp.return_date AND "
            + "        e.encounter_datetime <= :endDate "
            + "    INNER JOIN obs visitType ON "
            + "        pa.patient_id=visitType.person_id AND "
            + "        visitType.encounter_id=e.encounter_id AND "
            + "        (visitType.concept_id=%d AND visitType.value_coded=%d) "
            + "    LEFT JOIN obs patientNotFound ON "
            + "        pa.patient_id=patientNotFound.person_id AND "
            + "        patientNotFound.encounter_id=e.encounter_id AND "
            + "        (patientNotFound.concept_id=%d AND patientNotFound.value_coded=%d) "
            + " WHERE patientNotFound.obs_id IS NOT NULL "
            + " ORDER BY pa.patient_id ";

    return String.format(
        query,
        pharmacyEncounterTypeId,
        adultoSequimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        returnVisitDateConcept,
        returnVisitDateForDrugsConcept,
        homeVisitCardEncounterTypeId,
        apoioReintegracaoParteAEncounterTypeId,
        apoioReintegracaoParteBEncounterTypeId,
        typeOfVisitConcept,
        buscaConcept,
        patientFoundConcept,
        patientFoundAnswerConcept);
  }
}
