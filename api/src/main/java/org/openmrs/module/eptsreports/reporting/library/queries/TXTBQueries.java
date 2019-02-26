package org.openmrs.module.eptsreports.reporting.library.queries;

public class TXTBQueries {

  /**
   * Copied straight from INICIO DE TRATAMENTO ARV - NUM PERIODO: INCLUI TRANSFERIDOS DE COM DATA DE
   * INICIO CONHECIDA (SQL) SqlCohortDefinition#91787a86-0362-4820-a4ee-025d5501198b in backup
   *
   * @return sql
   */
  public static String arvTreatmentIncludesTransfersFromWithKnownStartData(
      Integer ARVPlanConceptId,
      Integer startDrugsConceptId,
      Integer historicalDrugsStartDateConceptId,
      Integer ARTProgramId,
      Integer pharmacyEncounterTypeId,
      Integer artAdultFollowupEncounterTypeId,
      Integer artPedFollowupEncounterTypeId) {
    return "SELECT patient_id FROM (SELECT patient_id, Min(data_inicio) data_inicio "
        + "FROM (SELECT p.patient_id, Min(e.encounter_datetime) data_inicio FROM patient p "
        + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
        + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
        + "WHERE e.voided = 0 AND o.voided = 0 AND p.voided = 0 AND e.encounter_type IN ( "
        + pharmacyEncounterTypeId
        + ", "
        + artAdultFollowupEncounterTypeId
        + ", "
        + artPedFollowupEncounterTypeId
        + " ) "
        + "AND o.concept_id = "
        + ARVPlanConceptId
        + " AND o.value_coded = "
        + startDrugsConceptId
        + " AND e.encounter_datetime <= :endDate "
        + "AND e.location_id = :location GROUP BY p.patient_id "
        + "UNION SELECT p.patient_id, Min(value_datetime) data_inicio FROM patient p "
        + "INNER JOIN encounter e ON p.patient_id = e.patient_id INNER JOIN obs o ON e.encounter_id = o.encounter_id "
        + "WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type IN ( "
        + pharmacyEncounterTypeId
        + ", "
        + artAdultFollowupEncounterTypeId
        + ", "
        + artPedFollowupEncounterTypeId
        + " ) "
        + "AND o.concept_id = "
        + historicalDrugsStartDateConceptId
        + " AND o.value_datetime IS NOT NULL AND o.value_datetime <= :endDate AND e.location_id = :location "
        + "GROUP BY p.patient_id UNION SELECT pg.patient_id, date_enrolled data_inicio FROM patient p "
        + "INNER JOIN patient_program pg ON p.patient_i d = pg.patient_id WHERE pg.voided = 0 AND p.voided = 0 "
        + "AND program_id = "
        + ARTProgramId
        + " AND date_enrolled <= :endDate AND location_id = :location "
        + "UNION SELECT e.patient_id, Min(e.encounter_datetime) AS data_inicio FROM patient p "
        + "INNER JOIN encounter e ON p.patient_id = e.patient_id WHERE p.voided = 0 AND e.encounter_type = "
        + pharmacyEncounterTypeId
        + " "
        + "AND e.voided = 0 AND e.encounter_datetime <= :endDate AND e.location_id = :location GROUP BY p.patient_id) inicio_real "
        + "GROUP BY patient_id)inicio WHERE data_inicio BETWEEN :startDate AND :endDate ";
  }
}
