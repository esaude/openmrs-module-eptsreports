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

    return "SELECT patient_id FROM (SELECT p.patient_id,MAX(encounter_datetime) encounter_datetime FROM patient p "
        + "INNER JOIN encounter e ON e.patient_id=p.patient_id WHERE p.voided=0 AND e.voided=0 "
        + "AND e.encounter_type IN("
        + pharmacyEncounterType
        + ") AND e.location_id=:location AND e.encounter_datetime<=:endDate "
        + "GROUP BY p.patient_id ) max_frida INNER JOIN obs o ON o.person_id=max_frida.patient_id "
        + "WHERE max_frida.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id="
        + returnVisitDateForDrugsConcept
        + " AND o.location_id=:location AND DATEDIFF(:endDate,o.value_datetime)>="
        + min
        + " AND DATEDIFF(:endDate,o.value_datetime)<="
        + max
        + " UNION "
        + "SELECT patient_id FROM "
        + "(SELECT p.patient_id,MAX(encounter_datetime) encounter_datetime "
        + "FROM patient p INNER JOIN encounter e ON e.patient_id=p.patient_id "
        + "WHERE p.voided=0 AND e.voided=0 AND e.encounter_type IN ("
        + adultoSequimento
        + ","
        + arvPediatriaSeguimento
        + ") "
        + "AND e.location_id=:location AND e.encounter_datetime<=:endDate GROUP BY p.patient_id ) max_mov "
        + "INNER JOIN obs o ON o.person_id=max_mov.patient_id "
        + "WHERE max_mov.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id="
        + returnVisitDate
        + " AND o.location_id=:location AND DATEDIFF(:endDate,o.value_datetime)>="
        + min
        + " AND DATEDIFF(:endDate,o.value_datetime)<="
        + max;
  }

  public static String getNonConsistentPatients(
      int prevencaoPositivaInicial,
      int prevencaoPositivaSeguimento,
      int acceptContactConcept,
      int noConcept) {
    return "SELECT patient_id FROM (SELECT p.patient_id,MAX(encounter_datetime) encounter_datetime FROM patient p "
        + "INNER JOIN encounter e ON e.patient_id=p.patient_id WHERE p.voided=0 AND e.voided=0 "
        + "AND e.encounter_type IN("
        + prevencaoPositivaInicial
        + ","
        + prevencaoPositivaSeguimento
        + ") AND e.location_id=:location AND e.encounter_datetime<=:endDate "
        + "GROUP BY p.patient_id ) max_encounter INNER JOIN obs o ON o.person_id=max_encounter.patient_id "
        + "WHERE max_encounter.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id="
        + acceptContactConcept
        + " AND o.value_coded="
        + noConcept
        + " AND o.location_id=:location";
  }
}
