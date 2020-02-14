package org.openmrs.module.eptsreports.reporting.library.queries;

public class TxMlQueries {

  public static String getPatientsWhoMissedAppointment(
      int returnVisitDateForDrugsConcept,
      int returnVisitDate,
      int pharmacyEncounterType,
      int adultoSequimento,
      int arvPediatriaSeguimento,
      int masterCardDrugEncounterType,
      int artPickupDateConcept) {
    String query =
        "SELECT "
            + "	patient_id "
            + "FROM "
            + "("
            + "	SELECT "
            + "		pp.patient_id, MAX(pp.return_date) AS return_date"
            + "	FROM"
            + "	("
            + "		SELECT "
            + "			p.patient_id, "
            + "			o.value_datetime AS return_date,"
            + "			e.encounter_id"
            + "		FROM patient p "
            + "		INNER JOIN encounter e ON e.patient_id = p.patient_id"
            + "		INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "		WHERE "
            + "			p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "			AND e.encounter_type IN (%d, %d) "
            + "			AND o.concept_id = %d "
            + "			AND e.encounter_datetime <=:endDate AND e.location_id=:location "
            + "		UNION"
            + "		SELECT "
            + "			p.patient_id, "
            + "			o.value_datetime AS return_date,"
            + "			e.encounter_id"
            + "		FROM patient p "
            + "		INNER JOIN encounter e ON e.patient_id = p.patient_id"
            + "		INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "		WHERE "
            + "			p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "			AND e.encounter_type IN (%d) "
            + "			AND o.concept_id = %d "
            + "			AND e.encounter_datetime <=:endDate AND e.location_id=:location "
            + "		UNION"
            + "		SELECT"
            + "			p.patient_id, "
            + "			DATE_ADD(o.value_datetime, INTERVAL 30 DAY) AS return_date,"
            + "			e.encounter_id			"
            + "		FROM patient p"
            + "		INNER JOIN encounter e ON e.patient_id = p.patient_id"
            + "		INNER JOIN obs o ON o.encounter_id = e.encounter_id"
            + "		WHERE"
            + "			p.voided = 0 AND e.voided = 0 AND o.voided = 0"
            + "			AND e.encounter_type = %d "
            + "			AND o.concept_id = %d"
            + "			AND e.encounter_datetime <=:endDate AND e.location_id=:location "
            + "	) pp"
            + "	INNER JOIN ("
            + "		SELECT p.patient_id, "
            + "			("
            + "				SELECT "
            + "					e.encounter_id "
            + "				FROM encounter e"
            + "				INNER JOIN obs o ON e.encounter_id = o.encounter_id"
            + "				WHERE "
            + "					e.patient_id = p.patient_id"
            + "					AND e.voided = 0"
            + "					AND o.voided = 0"
            + "					AND e.encounter_datetime <= :endDate"
            + "					AND e.encounter_type IN (%d, %d)"
            + "				ORDER BY e.encounter_datetime DESC"
            + "				LIMIT 1"
            + "			) last_cl_encounter,"
            + "			("
            + "				SELECT "
            + "					e.encounter_id "
            + "				FROM encounter e"
            + "				INNER JOIN obs o ON e.encounter_id = o.encounter_id"
            + "				WHERE "
            + "					e.patient_id = p.patient_id"
            + "					AND e.voided = 0"
            + "					AND o.voided = 0"
            + "					AND e.encounter_datetime <= :endDate"
            + "					AND e.encounter_type = %d"
            + "				ORDER BY e.encounter_datetime DESC"
            + "				LIMIT 1"
            + "			) AS latest_pharm_encounter_id,"
            + "			("
            + "					SELECT "
            + "						e.encounter_id "
            + "					FROM encounter e"
            + "					INNER JOIN obs o ON e.encounter_id = o.encounter_id"
            + "					WHERE "
            + "						e.patient_id = p.patient_id"
            + "						AND e.voided = 0"
            + "						AND o.voided = 0"
            + "						AND e.encounter_datetime <= :endDate"
            + "						AND e.encounter_type = %d"
            + "					ORDER BY e.encounter_datetime DESC, o.value_datetime DESC"
            + "					LIMIT 1"
            + "			) AS latest_dp_encounter_id				"
            + "		FROM patient p"
            + "	) last_encounters ON last_encounters.patient_id = pp.patient_id"
            + "	WHERE pp.encounter_id IN (last_cl_encounter, latest_pharm_encounter_id, latest_dp_encounter_id)"
            + "	GROUP BY pp.patient_id"
            + ")all_patients "
            + " WHERE "
            + "	DATE_ADD(return_date, INTERVAL 28 DAY)  > :startDate "
            + "	AND DATE_ADD(return_date, INTERVAL 28 DAY) < :endDate";

    return String.format(
        query,
        adultoSequimento,
        arvPediatriaSeguimento,
        returnVisitDate,
        pharmacyEncounterType,
        returnVisitDateForDrugsConcept,
        masterCardDrugEncounterType,
        artPickupDateConcept,
        adultoSequimento,
        arvPediatriaSeguimento,
        pharmacyEncounterType,
        masterCardDrugEncounterType);
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
            + "WHERE      e.encounter_type IN (%d, %d, %d) "
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

  public static String getPatientsWithMissedVisitOnMasterCard(
      int homeVisitCardEncounterTypeId,
      int reasonPatientMissedVisitConceptId,
      int transferredOutToAnotherFacilityConceptId,
      int autoTransferConceptId) {
    String query =
        "SELECT e.patient_id "
            + "FROM encounter e "
            + "         JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) encounter_datetime "
            + "               FROM patient p "
            + "                        JOIN encounter e ON p.patient_id = e.patient_id "
            + "                        JOIN obs o ON e.encounter_id = o.encounter_id "
            + "               WHERE o.concept_id = %d "
            + "                 AND e.location_id = :location "
            + "                 AND e.encounter_type= %d "
            + "                 AND e.encounter_datetime BETWEEN :startDate AND :endDate AND p.voided=0 "
            + "               GROUP BY p.patient_id) last "
            + "              ON e.patient_id = last.patient_id AND last.encounter_datetime = e.encounter_datetime "
            + "WHERE o.value_coded IN (%d,%d) AND e.location_id = :location AND e.voided=0 AND o.voided=0 ";

    return String.format(
        query,
        reasonPatientMissedVisitConceptId,
        homeVisitCardEncounterTypeId,
        transferredOutToAnotherFacilityConceptId,
        autoTransferConceptId);
  }

  public static String getRefusedOrStoppedTreatment(
      int homeVisitCardEncounterTypeId,
      int reasonPatientMissedVisitConceptId,
      int patientForgotVisitDateConceptId,
      int patientIsBedriddenAtHomeConceptId,
      int distanceOrMoneyForTransportIsToMuchForPatientConceptId,
      int patientIsDissatifiedWithDayHospitalServicesConceptId,
      int fearOfTheProviderConceptId,
      int absenceOfHealthProviderInHealthUnitConceptId,
      int patientDoesNotLikeArvTreatmentSideEffectsConceptId,
      int patientIsTreatingHivWithTraditionalMedicineConceptId,
      int otherReasonWhyPatientMissedVisitConceptId,
      int pharmacyEncounterTypeId,
      int returnVisitDateForDrugsConcept,
      int adultoSequimentoEncounterTypeId,
      int arvPediatriaSeguimentoEncounterTypeId,
      int returnVisitDateConcept,
      int masterCardDrugPickupEncounterTypeId,
      int artDatePickupConceptId) {

    String query =
        "SELECT e.patient_id "
            + "FROM encounter e "
            + "         JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         JOIN (SELECT patient_id, DATE_ADD(MAX(return_date), INTERVAL 28 DAY) AS return_date "
            + "               FROM (SELECT p.patient_id, "
            + "                            (SELECT o.value_datetime AS return_date "
            + "                             FROM encounter e "
            + "                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                             WHERE p.patient_id = e.patient_id "
            + "                               AND e.voided = 0 "
            + "                               AND o.voided = 0 "
            + "                               AND e.encounter_type = %d "
            + "                               AND e.location_id = :location "
            + "                               AND o.concept_id = %d "
            + "                               AND e.encounter_datetime <= :endDate "
            + "                             ORDER BY e.encounter_datetime DESC "
            + "                             LIMIT 1) AS return_date "
            + "                     FROM patient p "
            + "                     UNION "
            + "                     SELECT p.patient_id, "
            + "                            (SELECT o.value_datetime AS return_date "
            + "                             FROM encounter e "
            + "                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                             WHERE p.patient_id = e.patient_id "
            + "                               AND e.voided = 0 "
            + "                               AND o.voided = 0 "
            + "                               AND e.encounter_type IN (%d, %d) "
            + "                               AND e.location_id = :location "
            + "                               AND o.concept_id = %d "
            + "                               AND e.encounter_datetime <= :endDate "
            + "                             ORDER BY e.encounter_datetime DESC "
            + "                             LIMIT 1) AS return_date "
            + "                     FROM patient p "
            + "                     UNION "
            + "                     SELECT p.patient_id, "
            + "                            (SELECT DATE_ADD(o.value_datetime, INTERVAL 30 DAY) AS return_date "
            + "                             FROM encounter e "
            + "                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                             WHERE p.patient_id = e.patient_id "
            + "                               AND e.voided = 0 "
            + "                               AND o.voided = 0 "
            + "                               AND e.encounter_type = %d "
            + "                               AND e.location_id = :location "
            + "                               AND o.concept_id = %d "
            + "                               AND e.encounter_datetime <= :endDate "
            + "                             ORDER BY e.encounter_datetime DESC "
            + "                             LIMIT 1) AS return_date "
            + "                     FROM patient p) e "
            + "               GROUP BY e.patient_id) lp ON e.patient_id = lp.patient_id "
            + "WHERE o.concept_id = %d AND o.value_coded IN (%d,%d,%d,%d,%d,%d,%d,%d,%d) "
            + "AND e.encounter_type = %d "
            + "AND e.encounter_datetime BETWEEN lp.return_date and :endDate "
            + "AND e.location_id = :location "
            + "AND e.voided=0 "
            + "AND o.voided=0 ";

    return String.format(
        query,
        pharmacyEncounterTypeId,
        returnVisitDateForDrugsConcept,
        adultoSequimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        returnVisitDateConcept,
        masterCardDrugPickupEncounterTypeId,
        artDatePickupConceptId,
        reasonPatientMissedVisitConceptId,
        patientForgotVisitDateConceptId,
        patientIsBedriddenAtHomeConceptId,
        distanceOrMoneyForTransportIsToMuchForPatientConceptId,
        patientIsDissatifiedWithDayHospitalServicesConceptId,
        fearOfTheProviderConceptId,
        absenceOfHealthProviderInHealthUnitConceptId,
        patientDoesNotLikeArvTreatmentSideEffectsConceptId,
        patientIsTreatingHivWithTraditionalMedicineConceptId,
        otherReasonWhyPatientMissedVisitConceptId,
        homeVisitCardEncounterTypeId);
  }

  /*
   Untraced Patients Criteria 2
   Patients without Patient Visit Card of type busca and with a set of observations
  */
  public static String getPatientsWithVisitCardAndWithObs(
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
      int cardDeliveryDate,
      int masterCardDrugPickupEncounterTypeId,
      int artDatePickupConceptId) {
    String query =
        "SELECT pa.patient_id "
            + "FROM   patient pa "
            + "INNER JOIN ("
            + "SELECT patient_id, MAX(return_date) AS return_date "
            + "FROM (SELECT p.patient_id, "
            + "             (SELECT o.value_datetime AS return_date "
            + "              FROM encounter e "
            + "                       INNER JOIN obs o "
            + "                                  ON e.encounter_id = o.encounter_id "
            + "              WHERE p.patient_id = e.patient_id "
            + "                AND e.voided = 0 "
            + "                AND o.voided = 0 "
            + "                AND e.encounter_type = %d "
            + "                AND e.location_id = :location "
            + "                AND o.concept_id = %d "
            + "                AND e.encounter_datetime <= :endDate "
            + "              ORDER BY e.encounter_datetime DESC "
            + "              LIMIT 1) AS return_date "
            + "      FROM patient p "
            + "      UNION "
            + "      SELECT p.patient_id, "
            + "             (SELECT o.value_datetime AS return_date "
            + "              FROM encounter e "
            + "                       INNER JOIN obs o "
            + "                                  ON e.encounter_id = o.encounter_id "
            + "              WHERE p.patient_id = e.patient_id "
            + "                AND e.voided = 0 "
            + "                AND o.voided = 0 "
            + "                AND e.encounter_type IN (%d, %d) "
            + "                AND e.location_id = :location "
            + "                AND o.concept_id = %d "
            + "                AND e.encounter_datetime <= :endDate "
            + "              ORDER BY e.encounter_datetime DESC "
            + "              LIMIT 1) AS return_date "
            + "      FROM patient p "
            + "      UNION "
            + "      SELECT p.patient_id, "
            + "             (SELECT DATE_ADD(o.value_datetime, INTERVAL 30 DAY) AS return_date "
            + "              FROM encounter e "
            + "                       INNER JOIN obs o "
            + "                                  ON e.encounter_id = o.encounter_id "
            + "              WHERE p.patient_id = e.patient_id "
            + "                AND e.voided = 0 "
            + "                AND o.voided = 0 "
            + "                AND e.encounter_type = %d "
            + "                AND e.location_id = :location "
            + "                AND o.concept_id = %d "
            + "                AND e.encounter_datetime <= :endDate "
            + "              ORDER BY e.encounter_datetime DESC "
            + "              LIMIT 1) AS return_date "
            + "      FROM patient p) e "
            + "GROUP BY e.patient_id) lp ON pa.patient_id=lp.patient_id "
            + "INNER JOIN encounter e ON "
            + "  pa.patient_id=e.patient_id AND "
            + "  e.encounter_datetime >= lp.return_date AND "
            + "  e.encounter_datetime <= :endDate AND "
            + "  e.encounter_type IN (%d, %d, %d) AND "
            + "  e.location_id=:location "
            + "INNER JOIN obs visitType ON "
            + "  pa.patient_id=visitType.person_id AND "
            + "  visitType.encounter_id = e.encounter_id AND "
            + "  visitType.concept_id = %d AND "
            + "  visitType.value_coded = %d AND "
            + "  visitType.obs_datetime <= :endDate "
            + "INNER JOIN obs o ON "
            + "  pa.patient_id=o.person_id AND "
            + "  o.encounter_id = e.encounter_id AND "
            + "  o.concept_id IN (%d, %d, %d, %d, %d, %d, %d, %d, %d, %d) AND "
            + "  o.obs_datetime <= :endDate "
            + "GROUP BY pa.patient_id ";

    return String.format(
        query,
        pharmacyEncounterTypeId,
        returnVisitDateForDrugsConcept,
        adultoSequimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        returnVisitDateConcept,
        masterCardDrugPickupEncounterTypeId,
        artDatePickupConceptId,
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
  public static String
      getPatientsWithoutVisitCardRegisteredBtwnLastAppointmentOrDrugPickupAndEnddate(
          int pharmacyEncounterTypeId,
          int adultoSequimentoEncounterTypeId,
          int arvPediatriaSeguimentoEncounterTypeId,
          int returnVisitDateForDrugsConcept,
          int returnVisitDateConcept,
          int homeVisitCardEncounterTypeId,
          int apoioReintegracaoParteAEncounterTypeId,
          int apoioReintegracaoParteBEncounterTypeId,
          int masterCardDrugPickupEncounterTypeId,
          int artDatePickupConceptId) {

    String query =
        " SELECT pa.patient_id FROM patient pa "
            + " WHERE pa.patient_id NOT IN ("
            + "  SELECT pa.patient_id "
            + "  FROM patient pa"
            + "  INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + "  INNER JOIN obs o ON pa.patient_id=o.person_id "
            + "  INNER JOIN ("
            + "                SELECT patient_id, MAX(return_date) AS return_date "
            + "                FROM (SELECT p.patient_id, "
            + "                             (SELECT o.value_datetime AS return_date "
            + "                              FROM encounter e "
            + "                                       INNER JOIN obs o "
            + "                                                  ON e.encounter_id = o.encounter_id "
            + "                              WHERE p.patient_id = e.patient_id "
            + "                                AND e.voided = 0 "
            + "                                AND o.voided = 0 "
            + "                                AND e.encounter_type = %d "
            + "                                AND e.location_id = :location "
            + "                                AND o.concept_id = %d "
            + "                                AND e.encounter_datetime <= :endDate "
            + "                              ORDER BY e.encounter_datetime DESC "
            + "                              LIMIT 1) AS return_date "
            + "                      FROM patient p "
            + "                      UNION "
            + "                      SELECT p.patient_id, "
            + "                             (SELECT o.value_datetime AS return_date "
            + "                              FROM encounter e "
            + "                                       INNER JOIN obs o "
            + "                                                  ON e.encounter_id = o.encounter_id "
            + "                              WHERE p.patient_id = e.patient_id "
            + "                                AND e.voided = 0 "
            + "                                AND o.voided = 0 "
            + "                                AND e.encounter_type IN (%d, %d) "
            + "                                AND e.location_id = :location "
            + "                                AND o.concept_id = %d "
            + "                                AND e.encounter_datetime <= :endDate "
            + "                              ORDER BY e.encounter_datetime DESC "
            + "                              LIMIT 1) AS return_date "
            + "                      FROM patient p "
            + "                      UNION "
            + "                      SELECT p.patient_id, "
            + "                             (SELECT DATE_ADD(o.value_datetime, INTERVAL 30 DAY) AS return_date "
            + "                              FROM encounter e "
            + "                                       INNER JOIN obs o "
            + "                                                  ON e.encounter_id = o.encounter_id "
            + "                              WHERE p.patient_id = e.patient_id "
            + "                                AND e.voided = 0 "
            + "                                AND o.voided = 0 "
            + "                                AND e.encounter_type = %d "
            + "                                AND e.location_id = :location "
            + "                                AND o.concept_id = %d "
            + "                                AND e.encounter_datetime <= :endDate "
            + "                              ORDER BY e.encounter_datetime DESC "
            + "                              LIMIT 1) AS return_date "
            + "                      FROM patient p) e "
            + "                GROUP BY e.patient_id)lp ON pa.patient_id=lp.patient_id "
            + "  WHERE e.encounter_datetime >= lp.return_date AND e.encounter_datetime<=:endDate"
            + "  AND e.encounter_type IN (%d, %d, %d) "
            + "  AND e.location_id=:location  "
            + "  GROUP BY pa.patient_id"
            + ") "
            + " GROUP BY pa.patient_id";

    return String.format(
        query,
        pharmacyEncounterTypeId,
        returnVisitDateForDrugsConcept,
        adultoSequimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        returnVisitDateConcept,
        masterCardDrugPickupEncounterTypeId,
        artDatePickupConceptId,
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
      int masterCardDrugPickupEncounterTypeId,
      int homeVisitCardEncounterTypeId,
      int apoioReintegracaoParteAEncounterTypeId,
      int apoioReintegracaoParteBEncounterTypeId,
      int typeOfVisitConcept,
      int buscaConcept,
      int patientFoundConcept,
      int patientFoundAnswerConcept,
      int artDatePickupConceptId) {
    String query =
        "SELECT DISTINCT pa.patient_id FROM patient pa "
            + "    INNER JOIN ( "
            + "    SELECT patient_id, MAX(return_date) AS return_date "
            + "    FROM (SELECT p.patient_id, "
            + "                 (SELECT o.value_datetime AS return_date "
            + "                  FROM encounter e "
            + "                           INNER JOIN obs o "
            + "                                      ON e.encounter_id = o.encounter_id "
            + "                  WHERE p.patient_id = e.patient_id "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.encounter_type = %d "
            + "                    AND e.location_id = :location "
            + "                    AND o.concept_id = %d "
            + "                    AND e.encounter_datetime <= :endDate"
            + "                  ORDER BY e.encounter_datetime DESC "
            + "                  LIMIT 1) AS return_date "
            + "          FROM patient p "
            + "          UNION "
            + "          SELECT p.patient_id, "
            + "                 (SELECT o.value_datetime AS return_date "
            + "                  FROM encounter e "
            + "                           INNER JOIN obs o "
            + "                                      ON e.encounter_id = o.encounter_id "
            + "                  WHERE p.patient_id = e.patient_id "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.encounter_type IN (%d, %d) "
            + "                    AND e.location_id = :location "
            + "                    AND o.concept_id = 1410 "
            + "                    AND e.encounter_datetime <= :endDate"
            + "                  ORDER BY e.encounter_datetime DESC "
            + "                  LIMIT 1) AS return_date "
            + "          FROM patient p "
            + "          UNION "
            + "          SELECT p.patient_id, "
            + "                 (SELECT DATE_ADD(o.value_datetime, INTERVAL 30 DAY) AS return_date "
            + "                  FROM encounter e "
            + "                           INNER JOIN obs o "
            + "                                      ON e.encounter_id = o.encounter_id "
            + "                  WHERE p.patient_id = e.patient_id "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.encounter_type = %d "
            + "                    AND e.location_id = :location "
            + "                    AND o.concept_id = %d "
            + "                    AND e.encounter_datetime <= :endDate"
            + "                  ORDER BY e.encounter_datetime DESC "
            + "                  LIMIT 1) AS return_date "
            + "          FROM patient p) e "
            + "    GROUP BY e.patient_id)lp ON pa.patient_id=lp.patient_id "
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
        returnVisitDateForDrugsConcept,
        adultoSequimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        returnVisitDateConcept,
        masterCardDrugPickupEncounterTypeId,
        artDatePickupConceptId,
        homeVisitCardEncounterTypeId,
        apoioReintegracaoParteAEncounterTypeId,
        apoioReintegracaoParteBEncounterTypeId,
        typeOfVisitConcept,
        buscaConcept,
        patientFoundConcept,
        patientFoundAnswerConcept);
  }
}
