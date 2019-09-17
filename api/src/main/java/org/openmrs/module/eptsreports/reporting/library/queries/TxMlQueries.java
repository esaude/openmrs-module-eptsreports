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

  public static String getNonConsistentPatients(
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
      int registrationEncounterTypeId,
      int admissionEncounterTypeId,
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
        registrationEncounterTypeId,
        admissionEncounterTypeId,
        busca,
        dead);
  }

  /*
   Untraced Patients Criteria 2
   Patients without Patient Visit Card with a set of observations
  */
  public static String getPatientsWithoutVisitCardAndWithObs(
      int homeVisitCardEncounterTypeId,
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
            + "       inner join encounter e "
            + "               ON e.patient_id = pa.patient_id "
            + "       inner join obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type NOT IN ( %d ) "
            + "       AND o.concept_id = %d "
            + "       AND o.value_coded = %d "
            + "       AND o.concept_id IN ( %d, %d, %d, %d, "
            + "                             %d, %d, %d, %d, "
            + "                             %d, %d ) "
            + "       AND e.location_id = :location "
            + "       AND o.obs_datetime <= :endDate "
            + "GROUP  BY pa.patient_id; ";

    return String.format(
        query,
        homeVisitCardEncounterTypeId,
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
   Untraced Patients Criteria 1 And Untraced Patients Criteria 2
   All Patients without “Patient Visit Card” (Encounter type 21 or 36 or 37)
  */
  public static String getAllPatientsWithoutVisitCard(int homeVisitCardEncounterTypeId) {
    String query =
        " SELECT pa.patient_id FROM patient pa "
            + " INNER JOIN encounter e ON e.patient_id = pa.patient_id "
            + " INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " WHERE e.encounter_type NOT IN ( "
            + homeVisitCardEncounterTypeId
            + " ) "
            + " AND e.location_id=:location "
            + " AND o.obs_datetime <=:endDate  "
            + " GROUP BY pa.patient_id ";
    return query;
  }

  /*
     Patients with last scheduled appointment or drugs pick up (the most recent one) by reporting end date and <= the reporting end date
  */
  public static String getPatientsLastScheduledAppointmentOrDrugPickup(
      int pharmacyEncounterTypeId,
      int adultoSequimentoEncounterTypeId,
      int arvPediatriaSeguimentoEncounterTypeId,
      int returnVisitDateForDrugsConcept,
      int returnVisitDateConcept) {
    String query =
        " SELECT patient_id FROM ( "
            + "  SELECT p.patient_id,max(e.encounter_datetime) encounter_datetime FROM patient p "
            + " INNER JOIN encounter e on e.patient_id=p.patient_id "
            + " WHERE p.voided=0 AND e.voided=0 AND e.encounter_type IN ( "
            + pharmacyEncounterTypeId
            + " , "
            + adultoSequimentoEncounterTypeId
            + " , "
            + arvPediatriaSeguimentoEncounterTypeId
            + " ) "
            + " AND e.location_id=:location AND e.encounter_datetime<=:endDate group by p.patient_id "
            + " ) max_frida "
            + " INNER JOIN obs o on o.person_id=max_frida.patient_id "
            + " WHERE max_frida.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id IN ( "
            + returnVisitDateForDrugsConcept
            + " , "
            + returnVisitDateConcept
            + " ) "
            + " AND o.location_id=:location AND datediff(:endDate,o.value_datetime)<=:endDate";

    return query;
  }

  /** Patient Found and Not Found: Traced Patient (Unable to locate) */
  public static String getPatientsFoundOrNotFoundHomeVisitCard(
      int homeVisitCardEncounterTypeId,
      int typeOfVisitConcept,
      int patientFoundConcept,
      int buscaConcept,
      int patientFoundAnswerConcept) {
    String query =
        " SELECT pa.patient_id FROM patient pa "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + " INNER JOIN obs o ON pa.patient_id=o.person_id "
            + " WHERE e.encounter_type IN ( "
            + homeVisitCardEncounterTypeId
            + " ) "
            + " AND o.concept_id = "
            + typeOfVisitConcept
            + " AND o.value_coded= "
            + buscaConcept
            + " AND o.concept_id= "
            + patientFoundConcept
            + " AND o.value_coded = "
            + patientFoundAnswerConcept
            + " AND e.location_id=:location "
            + " AND o.obs_datetime <=:endDate "
            + " GROUP BY pa.patient_id";

    return query;
  }
}
