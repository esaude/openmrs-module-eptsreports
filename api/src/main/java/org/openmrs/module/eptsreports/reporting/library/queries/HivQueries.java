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
<<<<<<< HEAD
   * @param patientStateId
=======
   * @param activeOnProgramConceptId
>>>>>>> 12d5a87afab5c4a26765f1c2d83d2d8bff3ab1ce
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
      int activeOnProgramConceptId,
      int activeTBConceptId,
      int yesConceptId,
      int tbTreatmentPlanConceptId,
      int startDrugsConceptId,
      int continueRegimenConceptId) {
    String query =
        " SELECT p.patient_id FROM patient p "
            + "JOIN encounter e ON p.patient_id=e.patient_id "
            + "JOIN obs o ON p.patient_id=o.person_id "
            + "WHERE e.encounter_type IN ( "
            + adultoSeguimentoEncounterTypeId
            + ","
            + arvPediatriaSeguimentoEncounterTypeId
            + " ) AND p.voided=0 AND e.voided=0 AND o.concept_id= "
            + tbStartDateConceptId
            + " AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location "
            + "UNION "
            + "SELECT p.patient_id FROM patient p "
            + "JOIN encounter e ON p.patient_id=e.patient_id "
            + "JOIN obs o ON p.patient_id=o.person_id "
            + "WHERE e.encounter_type IN ( "
            + adultoSeguimentoEncounterTypeId
            + ","
            + arvPediatriaSeguimentoEncounterTypeId
            + " ) AND p.voided=0 AND e.voided=0 AND o.concept_id= "
            + tbEndDateConceptId
            + " AND o.value_datetime IS NULL AND e.location_id=:location "
            + "UNION "
            + "SELECT p.patient_id FROM patient p "
            + "JOIN encounter e ON p.patient_id=e.patient_id "
            + "JOIN obs o ON p.patient_id=o.person_id "
            + "WHERE e.encounter_type IN ( "
            + adultoSeguimentoEncounterTypeId
            + ","
            + arvPediatriaSeguimentoEncounterTypeId
            + " ) AND p.voided=0 AND e.voided=0 AND o.concept_id= "
            + tbEndDateConceptId
            + " AND o.value_datetime > :endDate AND e.location_id=:location "
            + "UNION "
            + " SELECT p.patient_id FROM patient p "
            + " JOIN patient_program pp ON p.patient_id=pp.patient_id"
            + " JOIN encounter e ON p.patient_id=e.patient_id"
            + "  JOIN obs o ON p.patient_id=o.person_id"
            + " JOIN program_workflow pw ON pp.program_id=pw.program_id"
            + " JOIN program_workflow_state pws ON pw.program_workflow_id=pws.program_workflow_id"
            + "  WHERE pp.program_id= "
            + tbProgramId
            + " AND pws.concept_id= "
            + activeOnProgramConceptId
            + " AND e.location_id=:location AND o.concept_id= "
            + tbStartDateConceptId
            + " AND o.value_datetime BETWEEN :startDate AND :endDate "
            + " UNION"
            + " SELECT p.patient_id FROM patient p"
            + " JOIN patient_program pp ON p.patient_id=pp.patient_id"
            + "  JOIN encounter e ON p.patient_id=e.patient_id"
            + " JOIN obs o ON p.patient_id=o.person_id"
            + "  JOIN program_workflow pw ON pp.program_id=pw.program_id"
            + " JOIN program_workflow_state pws ON pw.program_workflow_id=pws.program_workflow_id"
            + "  WHERE pp.program_id= "
            + tbProgramId
            + " AND pws.concept_id= "
            + activeOnProgramConceptId
            + " AND e.location_id=:location AND o.concept_id= "
            + tbEndDateConceptId
            + " AND o.value_datetime IS NULL"
            + " UNION"
            + " SELECT p.patient_id FROM patient p"
            + " JOIN patient_program pp ON p.patient_id=pp.patient_id"
            + " JOIN encounter e ON p.patient_id=e.patient_id"
            + " JOIN obs o ON p.patient_id=o.person_id"
            + " JOIN program_workflow pw ON pp.program_id=pw.program_id"
            + "  JOIN program_workflow_state pws ON pw.program_workflow_id=pws.program_workflow_id"
            + " WHERE pp.program_id= "
            + tbProgramId
            + " AND pws.concept_id= "
            + activeOnProgramConceptId
            + " AND e.location_id= :location AND o.concept_id= "
            + tbEndDateConceptId
            + " AND o.value_datetime > :endDate "
            + " UNION "
            + "SELECT p.patient_id FROM patient p "
            + "JOIN encounter e ON p.patient_id=e.patient_id "
            + "JOIN obs o ON p.patient_id=o.person_id "
            + "WHERE e.encounter_type IN ( "
            + adultoSeguimentoEncounterTypeId
            + ","
            + arvPediatriaSeguimentoEncounterTypeId
            + " ) AND p.voided=0 AND e.voided=0 "
            + "AND o.concept_id= "
            + activeTBConceptId
            + " AND o.value_coded= "
            + yesConceptId
            + "AND e.encounter_datetime BETWEEN :endDate AND DATE_SUB(:endDate, INTERVAL 6 MONTH) AND e.location_id=:location "
            + "UNION "
            + "SELECT p.patient_id FROM patient p "
            + "JOIN encounter e ON p.patient_id=e.patient_id "
            + "JOIN obs o ON p.patient_id=o.person_id "
            + "WHERE e.encounter_type IN ( "
            + adultoSeguimentoEncounterTypeId
            + ","
            + arvPediatriaSeguimentoEncounterTypeId
            + " ) AND p.voided=0 AND e.voided=0 "
            + "AND o.concept_id= "
            + tbTreatmentPlanConceptId
            + " AND o.value_coded IN ( "
            + startDrugsConceptId
            + ","
            + continueRegimenConceptId
            + " ) "
            + "AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location ";

    return query;
  }
}
