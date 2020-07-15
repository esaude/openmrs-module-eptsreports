/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

/** @author Stélio Moiane */
public interface KeyPopulationQueries {

  class QUERY {

    public static final String findKeyPopulationPatients =
        "SELECT patient_id FROM ( "
            + "SELECT e.patient_id FROM encounter e "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "WHERE e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (6,35) "
            + "AND o.concept_id = 23703  AND e.encounter_datetime <= :endDate "
            + "AND e.location_id = :location GROUP BY e.patient_id "
            + "UNION "
            + "SELECT pa.person_id as patient_id FROM person_attribute pa "
            + "WHERE pa.person_attribute_type_id=%d AND pa.voided=0 "
            + "AND DATE(pa.date_created) <= :endDate)key_population";

    public static final String findFilledKeyPopulationByPatient =
        "SELECT * FROM ("
            + "SELECT MAX(e.encounter_datetime) encounter_datetime, o.value_coded as value, e.encounter_type FROM encounter e "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id AND o.voided = 0 AND o.concept_id = 23703 "
            + "WHERE e.voided = 0 AND e.encounter_type IN (6,35) AND e.encounter_datetime <= :endDate "
            + "AND e.location_id = :location AND e.patient_id = %d GROUP BY e.encounter_type "
            + "UNION "
            + "SELECT MAX(DATE(pa.date_created)) encounter_datetime, pa.value, pa.person_attribute_type_id encounter_type FROM person_attribute pa "
            + "WHERE pa.person_attribute_type_id = %d AND pa.voided=0 AND DATE(pa.date_created) <= :endDate AND pa.person_id = %d"
            + ")key_population WHERE key_population.encounter_datetime IS NOT NULL ORDER BY key_population.encounter_datetime DESC";
  }
}
