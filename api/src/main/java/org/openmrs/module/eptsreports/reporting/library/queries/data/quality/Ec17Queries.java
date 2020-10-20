package org.openmrs.module.eptsreports.reporting.library.queries.data.quality;

public class Ec17Queries {

  /**
   * EC17 The date of drug pick up is before 1985
   *
   * @param programId - program ID
   * @param encounterType - encounterType
   * @param year - year
   * @return String
   */
  public static String getEc17CombinedQuery(int programId, int encounterType, int year) {
    return "SELECT patient_id, NID, Name, birthdate, Estimated_dob, Sex, First_entry_date, Last_updated, date_enrolled,  MIN(encounter_date) AS encounter_date, encounter_date_created, state, location_name FROM("
        + " SELECT pa.patient_id, pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(pg.date_enrolled, '%d-%m-%Y %H:%i:%s') AS date_enrolled, DATE_FORMAT(e.encounter_datetime, '%d-%m-%Y %H:%i:%s') AS encounter_date, DATE_FORMAT(e.date_created, '%d-%m-%Y %H:%i:%s') AS encounter_date_created,case when ps.state = 9 then 'DROPPED FROM TREATMENT' when ps.state = 6 then 'ACTIVE ON PROGRAM' when ps.state = 10 then 'PATIENT HAS DIED' when ps.state = 8 then 'SUSPENDED TREATMENT' when ps.state = 7 then 'TRANSFERED OUT TO ANOTHER FACILITY' when ps.state = 29 then 'TRANSFERRED FROM OTHER FACILTY' end AS state, l.name AS location_name FROM patient pa "
        + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
        + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
        + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id "
        + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
        + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
        + " INNER JOIN location l ON e.location_id=l.location_id"
        + " WHERE "
        + " pg.program_id="
        + programId
        + " AND e.voided=0 "
        + " AND e.encounter_type="
        + encounterType
        + " AND pe.birthdate IS NOT NULL"
        + " AND e.location_id IN(:location) AND pa.voided = 0 and e.voided=0 "
        + " AND YEAR(e.encounter_datetime) < "
        + year
        + ")f_ec16 GROUP BY f_ec16.patient_id";
  }
}
