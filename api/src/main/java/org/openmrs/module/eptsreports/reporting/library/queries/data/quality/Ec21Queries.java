package org.openmrs.module.eptsreports.reporting.library.queries.data.quality;

public class Ec21Queries {
  /**
   * EC21 The patient’s sex is not defined
   *
   * @return
   */
  public static String getEc21CombinedQuery() {
    String query =
        " SELECT "
            + " pe.person_id As patient_id, "
            + " pid.identifier AS NID, "
            + " concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,''),' ',ifnull(pn.family_name,'')) AS Name, "
            + " DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, "
            + " IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, "
            + " pe.gender AS Sex, DATE_FORMAT(pe.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, "
            + " DATE_FORMAT(pe.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, "
            + " DATE_FORMAT(programState.date_enrolled, '%d-%m-%Y') AS date_enrolled, "
            + " case "
            + " when programState.state = 9 then 'DROPPED FROM TREATMENT' "
            + " when programState.state = 6 then 'ACTIVE ON PROGRAM' "
            + " when programState.state = 10 then 'PATIENT HAS DIED' "
            + " when programState.state = 8 then 'SUSPENDED TREATMENT' "
            + " when programState.state = 7 then 'TRANSFERED OUT TO ANOTHER FACILITY' "
            + " when programState.state = 29 then 'TRANSFERRED FROM OTHER FACILTY' "
            + " end AS state, "
            + " DATE_FORMAT(programState.start_date, '%d-%m-%Y') AS state_date, "
            + " l.name AS location_name"
            + " FROM "
            + " person pe "
            + " INNER JOIN "
            + " ( select pn1.* "
            + " from person_name pn1 "
            + " inner join "
            + " ( "
            + " select person_id, min(person_name_id) id "
            + " from person_name "
            + " where voided = 0 "
            + " group by person_id "
            + " ) pn2 "
            + " where pn1.person_id = pn2.person_id and pn1.person_name_id = pn2.id "
            + " ) pn on pn.person_id = pe.person_id "
            + " INNER JOIN "
            + " ( select pid1.* "
            + " from patient_identifier pid1 "
            + " inner join "
            + " ( "
            + " select patient_id, min(patient_identifier_id) id "
            + " from patient_identifier "
            + " where voided = 0 "
            + " group by patient_id "
            + " ) pid2 "
            + " where pid1.patient_id = pid2.patient_id and pid1.patient_identifier_id = pid2.id "
            + " ) pid on pid.patient_id = pe.person_id "
            + " INNER JOIN location l ON l.location_id = pid.location_id "
            + " LEFT JOIN (SELECT pg.patient_id, pg.date_enrolled, ps.state, max(ps.start_date) AS start_date "
            + " FROM patient_program pg "
            + " INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id "
            + " AND ps.start_date IS NOT NULL "
            + " AND ps.end_date IS NULL "
            + " AND pg.program_id = 2 "
            + " AND pg.location_id IN (:location) "
            + " GROUP BY pg.patient_id "
            + " ) AS programState ON pe.person_id = programState.patient_id "
            + " where pe.voided = 0 and pe.gender is null ";
    return query;
  }
}
