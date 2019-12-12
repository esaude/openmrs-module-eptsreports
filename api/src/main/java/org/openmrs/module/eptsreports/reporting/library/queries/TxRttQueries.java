/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

/** @author Stélio Moiane */
public interface TxRttQueries {

  class QUERY {

    public static final String findPatientsWithEncountersInASpecificPeriod =
        "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id WHERE p.voided = 0 AND e.voided = 0 AND e.encounter_type IN (6,9,18,52) "
            + "AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location GROUP BY p.patient_id";

    public static final String findFollowUpEncountersByPatient =
        "SELECT e.encounter_id, e.encounter_datetime, o.value_datetime FROM encounter e "
            + "LEFT JOIN obs o ON o.encounter_id = e.encounter_id AND o.voided = 0 AND o.concept_id = 1410 "
            + "WHERE e.voided = 0 AND e.encounter_type IN (6,9) "
            + "AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate "
            + "AND e.location_id = :location AND e.patient_id = %d ORDER BY e.encounter_datetime DESC";

    public static final String findArtPickUpEncountersByPatient =
        "SELECT * FROM ("
            + "SELECT e.encounter_id, e.encounter_datetime, o.value_datetime as next_pick_up FROM encounter e "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id WHERE e.voided = 0 AND o.voided = 0 AND e.encounter_type = 18 "
            + "AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate "
            + "AND e.location_id = :location AND o.concept_id = 5096 AND e.patient_id = %d UNION "
            + "SELECT e.encounter_id, o.value_datetime, (o.value_datetime + INTERVAL 30 DAY) as next_pick_up FROM encounter e "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id WHERE e.voided = 0 AND o.voided = 0 AND e.encounter_type = 52 "
            + "AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate "
            + "AND e.location_id = :location AND o.concept_id = 23866 AND e.patient_id = %d "
            + ") art_pick_up ORDER BY art_pick_up.encounter_datetime DESC";

    public static final String findLastFollowUpEncounterByPatientAndPeriod =
        "SELECT e.encounter_id, e.encounter_datetime, o.value_datetime FROM encounter e "
            + "LEFT JOIN obs o ON o.encounter_id = e.encounter_id AND o.voided = 0 AND o.concept_id = 1410 "
            + "WHERE e.voided = 0 AND e.encounter_type IN (6,9) "
            + "AND e.encounter_datetime < :startDate AND e.location_id = :location AND e.patient_id = %d "
            + "ORDER BY e.encounter_datetime DESC, o.value_datetime DESC LIMIT 1";

    public static final String findLastArtPickUpEncounterByPatientAndPeriod =
        "SELECT max_art_pick_up.encounter_id, max_art_pick_up.encounter_datetime, max_art_pick_up.next_pick_up FROM ("
            + "SELECT e.encounter_id as encounter_id, e.encounter_datetime as encounter_datetime, o.value_datetime as next_pick_up FROM encounter e "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id WHERE e.voided = 0 AND o.voided = 0 AND e.encounter_type = 18 "
            + "AND e.encounter_datetime < :startDate AND e.location_id = :location AND o.concept_id = 5096 AND e.patient_id = %d UNION "
            + "SELECT e.encounter_id as encounter_id, o.value_datetime as encounter_datetime, (o.value_datetime + INTERVAL 30 DAY) as next_pick_up FROM encounter e "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id WHERE e.voided = 0 AND o.voided = 0 AND e.encounter_type = 52 "
            + "AND o.value_datetime < :startDate AND e.location_id = :location AND o.concept_id = 23866 AND e.patient_id = %d ) max_art_pick_up "
            + "ORDER BY max_art_pick_up.encounter_datetime DESC, max_art_pick_up.next_pick_up DESC LIMIT 1";
  }
}
