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
        ""
            + "SELECT "
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
      int otherReasonWhyPatientMissedVisitConceptId) {

    String query =
        "SELECT e.patient_id "
            + "FROM encounter e "
            + "         JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) encounter_datetime "
            + "               FROM patient p "
            + "   JOIN encounter e ON p.patient_id = e.patient_id "
            + "               WHERE e.encounter_type=%d AND e.location_id = :location "
            + "                 AND e.encounter_datetime BETWEEN :startDate AND :endDate AND p.voided=0 "
            + "               GROUP BY p.patient_id) last "
            + "              ON e.patient_id = last.patient_id AND last.encounter_datetime = e.encounter_datetime "
            + "WHERE o.concept_id=%d AND o.value_coded IN (%d,%d,%d,%d,%d,%d,%d,%d,%d) AND e.location_id = :location AND e.voided=0 AND o.voided=0 ";

    return String.format(
        query,
        homeVisitCardEncounterTypeId,
        reasonPatientMissedVisitConceptId,
        patientForgotVisitDateConceptId,
        patientIsBedriddenAtHomeConceptId,
        distanceOrMoneyForTransportIsToMuchForPatientConceptId,
        patientIsDissatifiedWithDayHospitalServicesConceptId,
        fearOfTheProviderConceptId,
        absenceOfHealthProviderInHealthUnitConceptId,
        patientDoesNotLikeArvTreatmentSideEffectsConceptId,
        patientIsTreatingHivWithTraditionalMedicineConceptId,
        otherReasonWhyPatientMissedVisitConceptId);
  }
}
