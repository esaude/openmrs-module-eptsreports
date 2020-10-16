package org.openmrs.module.eptsreports.reporting.library.queries;

public class Eri4MonthsQueries {

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
