/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

/** @author Stélio Moiane */
public interface TxRttQueries {

  class QUERY {

    public static final String findMinEncounterDateByPatientInReportingPeriod =
        "select patient_id, min(min_date) from ( "
            + "SELECT e.patient_id, MIN(e.encounter_datetime)  min_date "
            + "FROM patient p inner join encounter e on p.patient_id=e.patient_id "
            + "WHERE p.voided=0 and e.encounter_type in (6,9,18) AND e.voided=0 and e.encounter_datetime>=:startDate and e.encounter_datetime<=:endDate and e.patient_id in(:patientIds) and e.location_id=:location GROUP BY p.patient_id "
            + "union Select p.patient_id,min(value_datetime) min_date from patient p "
            + " inner join encounter e on p.patient_id=e.patient_id "
            + " inner join obs o on e.encounter_id=o.encounter_id "
            + " where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 and o.value_datetime is not null "
            + " and o.value_datetime>=:startDate and o.value_datetime<=:endDate and e.patient_id in(:patientIds) and e.location_id=:location group by p.patient_id) min_data_consulta_levantamento group by patient_id ";

    public static final String findMaxEncounterDateByPatientInReportingPeriod =
        "select patient_id, max(last_date) from ( "
            + "SELECT e.patient_id, max(e.encounter_datetime)  last_date "
            + "FROM patient p inner join encounter e on p.patient_id=e.patient_id "
            + "WHERE p.voided=0 and e.encounter_type in (6,9,18) AND e.voided=0 and e.encounter_datetime<:startDate and e.location_id=:location and e.patient_id in(:patientIds) GROUP BY p.patient_id "
            + "union Select p.patient_id,max(value_datetime) last_date from patient p "
            + " inner join encounter e on p.patient_id=e.patient_id "
            + " inner join obs o on e.encounter_id=o.encounter_id "
            + " where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 and o.value_datetime is not null "
            + " and o.value_datetime<:startDate and e.location_id=:location and e.patient_id in(:patientIds) group by p.patient_id ) last_consulta_levantamento group by patient_id ";
  }
}
