package org.openmrs.module.eptsreports.reporting.library.queries.data.quality;

public class Ec16Queries {

  /**
   * Get the query for EC16 patient listing
   *
   * @param programId
   * @param arvPediatriaSeguimentoEncounterType
   * @param adultoSeguimentoEncounterType
   * @return
   */
  public static String getEc16CombinedQuery(
      int programId, int arvPediatriaSeguimentoEncounterType, int adultoSeguimentoEncounterType) {
    String query =
        "SELECT patient_id, NID, Name, birthdate, Estimated_dob, Sex, First_entry_date, Last_updated, MIN(encounter_date) AS encounter_date, encounter_date_created FROM("
            + " SELECT pa.patient_id, pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate, IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date, DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated, DATE_FORMAT(e.encounter_datetime, '%d-%m-%Y %H:%i:%s') AS encounter_date, DATE_FORMAT(e.date_created, '%d-%m-%Y %H:%i:%s') AS encounter_date_created FROM patient pa "
            + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
            + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
            + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id "
            + " INNER JOIN patient_program pg ON pa.patient_id=pg.patient_id "
            + " INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + "WHERE "
            + " pg.program_id="
            + programId
            + " AND e.voided=0 "
            + " AND e.encounter_type IN("
            + arvPediatriaSeguimentoEncounterType
            + ","
            + adultoSeguimentoEncounterType
            + ")"
            + " AND pe.birthdate IS NOT NULL"
            + " AND e.location_id IN(:location) AND pa.voided = 0 and e.voided=0 "
            + " AND pe.birthdate > e.encounter_datetime"
            + ")f_ec16 GROUP BY f_ec16.patient_id";
    return query;
  }
}
