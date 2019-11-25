package org.openmrs.module.eptsreports.reporting.library.queries;

public class HivQueries {

  /**
   * Patients on TB Treatment
   *
   * @param adultoSeguimentoEncounterTypeId
   * @param arvPediatriaSeguimentoEncounterTypeId
   * @param tbStartDateConceptId
   * @param tbEndDateConceptId
   * @param tbProgramId
   * @param patientStateId
   * @param activeTBConceptId
   * @param yesConceptId
   * @param tbTreatmentPlanConceptId
   * @param startDrugsConceptId
   * @param continueRegimenConceptId
   * @return
   */
  public static String getPatientsOnTbTreatmentQuery(
      int adultoSeguimentoEncounterTypeId,
      int arvPediatriaSeguimentoEncounterTypeId,
      int tbStartDateConceptId,
      int tbEndDateConceptId,
      int tbProgramId,
      int patientStateId,
      int activeTBConceptId,
      int yesConceptId,
      int tbTreatmentPlanConceptId,
      int startDrugsConceptId,
      int continueRegimenConceptId) {

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs o "
            + "         ON p.patient_id = o.person_id "
            + "WHERE  e.encounter_type IN ( %d, %d ) "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.concept_id = %d "
            + "       AND o.value_datetime BETWEEN :startDate AND :endDate "
            + "       AND e.location_id = :location "
            + "UNION "
            + "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs o "
            + "         ON p.patient_id = o.person_id "
            + "WHERE  e.encounter_type IN ( %d, %d ) "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.concept_id = %d "
            + "       AND o.value_datetime IS NULL "
            + "       AND e.location_id = :location "
            + "UNION "
            + "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs o "
            + "         ON p.patient_id = o.person_id "
            + "WHERE  e.encounter_type IN ( %d, %d ) "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.concept_id = %d "
            + "       AND o.value_datetime > :endDate "
            + "       AND e.location_id = :location "
            + "UNION "
            + "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs o "
            + "         ON p.patient_id = o.person_id "
            + "       JOIN patient_program pp "
            + "         ON p.patient_id = pp.patient_id "
            + "       JOIN patient_state ps "
            + "         ON pp.patient_program_id = ps.patient_program_id "
            + "WHERE  pp.program_id = %d "
            + "       AND ps.state = %d "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id = %d "
            + "       AND o.value_datetime BETWEEN :startDate AND :endDate "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "UNION "
            + "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs o "
            + "         ON p.patient_id = o.person_id "
            + "       JOIN patient_program pp "
            + "         ON p.patient_id = pp.patient_id "
            + "       JOIN patient_state ps "
            + "         ON pp.patient_program_id = ps.patient_program_id "
            + "WHERE  pp.program_id = %d "
            + "       AND ps.state = %d "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id = %d "
            + "       AND o.value_datetime IS NULL "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "UNION "
            + "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs o "
            + "         ON p.patient_id = o.person_id "
            + "       JOIN patient_program pp "
            + "         ON p.patient_id = pp.patient_id "
            + "       JOIN patient_state ps "
            + "         ON pp.patient_program_id = ps.patient_program_id "
            + "WHERE  pp.program_id = %d "
            + "       AND ps.state = %d "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id = %d "
            + "       AND o.value_datetime > :endDate "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "UNION "
            + "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs o "
            + "         ON p.patient_id = o.person_id "
            + "WHERE  e.encounter_type IN ( %d, %d ) "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.concept_id = %d "
            + "       AND o.value_coded = %d "
            + "       AND e.encounter_datetime BETWEEN :endDate AND Date_sub(:endDate, "
            + "                                                     INTERVAL 6 month) "
            + "       AND e.location_id = :location "
            + "UNION "
            + "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs o "
            + "         ON p.patient_id = o.person_id "
            + "WHERE  e.encounter_type IN ( %d, %d ) "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.concept_id = %d "
            + "       AND o.value_coded IN ( %d, %d ) "
            + "       AND o.value_datetime BETWEEN :startDate AND :endDate "
            + "       AND e.location_id = :location ";

    return String.format(
        query,
        adultoSeguimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        tbStartDateConceptId,
        adultoSeguimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        tbEndDateConceptId,
        adultoSeguimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        tbEndDateConceptId,
        tbProgramId,
        patientStateId,
        tbStartDateConceptId,
        tbProgramId,
        patientStateId,
        tbEndDateConceptId,
        tbProgramId,
        patientStateId,
        tbEndDateConceptId,
        adultoSeguimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        activeTBConceptId,
        yesConceptId,
        adultoSeguimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        tbTreatmentPlanConceptId,
        startDrugsConceptId,
        continueRegimenConceptId);
  }
}
