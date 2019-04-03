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
}
