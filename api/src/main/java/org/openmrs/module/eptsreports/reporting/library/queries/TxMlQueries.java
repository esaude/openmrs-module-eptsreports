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
            + "              ON e.patient_id = last.patient_id AND last.encounter_datetime <= :endDate "
            + "WHERE o.value_coded IN (%d,%d) AND e.location_id = :location AND e.voided=0 AND o.voided=0 ";

    return String.format(
        query,
        reasonPatientMissedVisitConceptId,
        homeVisitCardEncounterTypeId,
        transferredOutToAnotherFacilityConceptId,
        autoTransferConceptId);
  }
}
