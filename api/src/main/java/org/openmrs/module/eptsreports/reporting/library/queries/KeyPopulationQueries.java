/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

/** @author Stélio Moiane */
public interface KeyPopulationQueries {

  class QUERY {

    public static final String findPatientsWhoAreHomosexual =
        "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 "
            + "AND concept_id=23703 AND value_coded=1377 AND e.encounter_type=6 "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location";

    public static final String findPatientsWhoUseDrugs =
        "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=23703 AND value_coded=20454 "
            + "AND e.encounter_type=6 AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location";

    public static final String findPatientsWhoAreInPrison =
        "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=23703 AND value_coded=20426 "
            + "AND e.encounter_type=6 AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location";

    public static final String findPatientsWhoAreSexWorker =
        "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=23703 AND value_coded=1901 "
            + "AND e.encounter_type=6 AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location";
  }
}
