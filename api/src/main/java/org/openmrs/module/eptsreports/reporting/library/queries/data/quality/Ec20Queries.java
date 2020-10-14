package org.openmrs.module.eptsreports.reporting.library.queries.data.quality;

import org.openmrs.module.eptsreports.metadata.HivMetadata;

public class Ec20Queries {

  /**
   * EC20 Patients that are not enrolled in TARV but has a consultation or drugs pick up recorded in
   * the system
   *
   * @param programId - program ID
   * @param arvPediatriaSeguimentoEncounterType - {@link
   *     HivMetadata#getPediatriaSeguimentoEncounterType()}
   * @param adultoSeguimentoEncounterType - {@link HivMetadata#getAdultoSeguimentoEncounterType()}
   *     ()}
   * @param arvPharmaciaEncounterType - - {@link HivMetadata#getARVPharmaciaEncounterType()} ()}
   * @return String
   */
  public static String getEc20CombinedQuery(
      int programId,
      int arvPediatriaSeguimentoEncounterType,
      int adultoSeguimentoEncounterType,
      int arvPharmaciaEncounterType) {

    return "SELECT patient_id, NID, Name, birthdate, Estimated_dob, Sex, First_entry_date, Last_updated,"
        + " CASE WHEN encounter_type=6 THEN encounter_date WHEN encounter_type=9 THEN encounter_date END AS first_consultation_date,"
        + " CASE WHEN encounter_type=18 THEN encounter_date END AS first_drug_pickup_date, location_name FROM"
        + " (SELECT pa.patient_id, pi.identifier AS NID, CONCAT(pn.given_name, ' ', pn.family_name ) AS Name, DATE_FORMAT(pe.birthdate, '%d-%m-%Y') AS birthdate,"
        + " IF(pe.birthdate_estimated = 1, 'Yes','No') AS Estimated_dob, pe.gender AS Sex, DATE_FORMAT(pa.date_created, '%d-%m-%Y %H:%i:%s') AS First_entry_date,"
        + " DATE_FORMAT(pa.date_changed, '%d-%m-%Y %H:%i:%s') AS Last_updated,"
        + " DATE_FORMAT(e.encounter_datetime, '%d-%m-%Y %H:%i:%s') AS encounter_date, DATE_FORMAT(e.date_created, '%d-%m-%Y %H:%i:%s') AS encounter_date_created,"
        + " e.encounter_type, l.name AS location_name FROM patient pa"
        + " INNER JOIN patient_identifier pi ON pa.patient_id=pi.patient_id"
        + " INNER JOIN person pe ON pa.patient_id=pe.person_id"
        + " INNER JOIN person_name pn ON pa.patient_id=pn.person_id"
        + " INNER JOIN encounter e ON pa.patient_id=e.patient_id"
        + " INNER JOIN location l ON e.location_id=l.location_id"
        + " WHERE pa.patient_id NOT IN(SELECT patient_id FROM patient_program WHERE program_id="
        + programId
        + " )"
        + " AND e.voided=0  AND e.encounter_type IN ("
        + arvPediatriaSeguimentoEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPharmaciaEncounterType
        + " ) AND pe.birthdate IS NOT NULL AND e.location_id IN(:location)"
        + " AND pa.voided = 0 and e.voided=0)f_ec20 GROUP BY f_ec20.patient_id;";
  }
}
