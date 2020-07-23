/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

/** @author Stélio Moiane */
public interface TxRttQueries {

  class QUERY {

    public static final String findPatientsWithEncountersInASpecificPeriod =
        "select visitPeriod.patient_id from ( "
            + "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "WHERE p.voided = 0 AND e.voided = 0 AND e.encounter_type IN (6,9,18) AND "
            + "e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o.concept_id = 23866 AND "
            + "e.encounter_type = 52 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location "
            + "GROUP BY p.patient_id "
            + ") visitPeriod "
            + "inner join ( "
            + "Select patient_id,min(data_inicio) data_inicio from ( "
            + "Select p.patient_id,min(e.encounter_datetime) data_inicio from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.voided=0 and o.voided=0 and p.voided=0 and "
            + "e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and "
            + "e.encounter_datetime<=:endDate and e.location_id=:location "
            + "group by p.patient_id "
            + "union "
            + "Select p.patient_id,min(value_datetime) data_inicio from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and o.concept_id=1190 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location "
            + "group by p.patient_id "
            + "union "
            + "select pg.patient_id,min(date_enrolled) data_inicio "
            + "from patient p inner join patient_program pg on p.patient_id=pg.patient_id "
            + "where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location "
            + "group by pg.patient_id "
            + "union "
            + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio FROM patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location "
            + "GROUP BY p.patient_id "
            + "union "
            + "Select p.patient_id,min(value_datetime) data_inicio from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location "
            + "group by p.patient_id "
            + ") inicio "
            + "group by patient_id "
            + ") inicio_real on visitPeriod.patient_id=inicio_real.patient_id ";

    public static final String findEncountersByPatient =
        "SELECT encounters.patient_id, encounters.encounter_datetime FROM ( "
            + "SELECT p.patient_id, e.encounter_datetime FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "WHERE e.voided = 0 AND e.encounter_type IN (6,9) AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location GROUP BY p.patient_id, e.encounter_datetime "
            + "UNION "
            + "SELECT p.patient_id, e.encounter_datetime FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "WHERE e.voided = 0 AND e.encounter_type = 18 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location GROUP BY p.patient_id, e.encounter_datetime "
            + "UNION "
            + "SELECT p.patient_id, o.value_datetime encounter_datetime FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "WHERE e.voided = 0 AND o.voided = 0 AND e.encounter_type = 52 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location AND o.concept_id = 23866 "
            + "GROUP BY p.patient_id, o.value_datetime ) encounters "
            + "WHERE encounters.patient_id IN (:patients) ORDER BY encounters.encounter_datetime DESC ";

    public static final String findLastNextPickupDateOnThePreviousPickupEncounterByPatient =
        "SELECT MAX(pickupFinal.value_datetime) FROM ( "
            + "SELECT pickup.patient_id,o.value_datetime  value_datetime FROM  ( "
            + "select maxpkp.patient_id,e.encounter_datetime,e.encounter_id from (  "
            + "SELECT p.patient_id, MAX(e.encounter_datetime) encounter_datetime FROM patient p  "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "WHERE p.voided = 0 AND e.voided = 0 AND e.encounter_type = 18 AND e.encounter_datetime < date(:encounterDate) AND e.location_id = :location AND p.patient_id = :patientId  "
            + "GROUP BY p.patient_id  "
            + ") maxpkp  "
            + "inner join encounter e on e.patient_id=maxpkp.patient_id  "
            + "where e.encounter_datetime=maxpkp.encounter_datetime and  e.encounter_type=18 and e.location_id=:location and e.patient_id=:patientId and e.voided=0  "
            + ") pickup  "
            + "INNER JOIN obs o ON pickup.patient_id = o.person_id AND o.concept_id = 5096 AND o.voided = 0 AND pickup.encounter_id = o.encounter_id  "
            + "UNION  "
            + "SELECT p.patient_id,(MAX(o.value_datetime)  + INTERVAL 30 DAY) value_datetime FROM patient p  "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o.concept_id = 23866 AND  "
            + "e.encounter_type = 52 AND o.value_datetime < date(:encounterDate) AND e.location_id = :location AND p.patient_id = :patientId "
            + "GROUP BY p.patient_id "
            + ")pickupFinal ";

    public static final String
        findLastNextEncounterScheduledDateOnThePreviousFolloupEncounterByPatient =
            "SELECT max(o.value_datetime) value_datetime FROM ( "
                + "SELECT max_encounter.patient_id,e.encounter_datetime,e.encounter_id FROM ( "
                + "SELECT p.patient_id, MAX(e.encounter_datetime) encounter_datetime FROM patient p "
                + "INNER JOIN encounter e ON e.patient_id = p.patient_id "
                + "WHERE 	p.voided = 0  AND e.voided = 0 AND e.encounter_type IN (6,9) AND e.encounter_datetime < date(:encounterDate)  AND e.location_id = :location AND p.patient_id = :patientId "
                + "GROUP BY p.patient_id "
                + ") max_encounter "
                + "INNER JOIN encounter e ON e.patient_id=max_encounter.patient_id "
                + "WHERE e.encounter_datetime=max_encounter.encounter_datetime  AND e.encounter_type IN(6,9)  AND e.location_id=:location AND e.voided=0 "
                + ") enc "
                + "INNER JOIN obs o ON enc.patient_id = o.person_id AND o.concept_id = 1410 AND enc.encounter_id=o.encounter_id AND o.voided=0 "
                + "group by enc.patient_id ";
  }
}
