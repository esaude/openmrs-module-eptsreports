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
        "SELECT p.patient_id " +
                "FROM   patient p " +
                "JOIN   encounter e " +
                "ON     p.patient_id = e.patient_id " +
                "JOIN   obs start " +
                "ON     e.encounter_id = start.encounter_id " +
                "WHERE  e.encounter_type IN (%d, " +
                "                            %d) " +
                "AND    start.concept_id = %d " +
                "AND    start.value_datetime IS NOT NULL " +
                "AND    start.value_datetime BETWEEN :startDate AND    :endDate " +
                "AND    e.location_id=:location " +
                "AND    p.patient_id NOT IN " +
                "       ( " +
                "              SELECT p1.patient_id " +
                "              FROM   patient p1 " +
                "              JOIN   encounter e1 " +
                "              ON     p1.patient_id = e1.patient_id " +
                "              JOIN   obs o " +
                "              ON     e1.encounter_id = o.encounter_id " +
                "              WHERE  e1.encounter_type IN (%d, " +
                "                                           %d) " +
                "              AND    o.concept_id = %d " +
                "              AND    ( " +
                "                            o.value_datetime IS NOT NULL " +
                "                     AND    o.value_datetime <= :endDate ) " +
                "              AND    o.value_datetime > start.value_datetime " +
                "              AND    e.location_id=:location ) " +
                "UNION " +
                "SELECT p.patient_id " +
                "FROM   patient p " +
                "JOIN   encounter e " +
                "ON     p.patient_id=e.patient_id " +
                "JOIN   patient_program pp " +
                "ON     p.patient_id=pp.patient_id " +
                "JOIN   patient_state ps " +
                "ON     pp.patient_program_id=ps.patient_program_id " +
                "WHERE  pp.program_id=%d " +
                "AND    ps.state=%d " +
                "AND    e.location_id=:location " +
                "AND    ps.start_date BETWEEN :startDate AND    :endDate " +
                "AND    ( " +
                "              ps.end_date IS NULL " +
                "       OR     ps.end_date > :endDate ) " +
                "AND    p.voided=0 " +
                "AND    e.voided=0 " +
                "UNION " +
                "SELECT e.patient_id " +
                "FROM   encounter e " +
                "JOIN   obs o " +
                "ON     e.encounter_id = o.encounter_id " +
                "JOIN " +
                "       ( " +
                "                SELECT   p.patient_id, " +
                "                         max(e.encounter_datetime) encounter_datetime " +
                "                FROM     patient p " +
                "                JOIN     encounter e " +
                "                ON       p.patient_id = e.patient_id " +
                "                JOIN     obs o " +
                "                ON       e.encounter_id = o.encounter_id " +
                "                WHERE    o.concept_id = %d " +
                "                AND      e.location_id = :location " +
                "                AND      e.encounter_type IN (%d, " +
                "                                              %d) " +
                "                AND      e.encounter_datetime BETWEEN date_sub(e.encounter_datetime, INTERVAL 6 month) AND      :endDate" +
                "                AND      p.voided=0 " +
                "                GROUP BY p.patient_id) last " +
                "ON     e.patient_id = last.patient_id " +
                "AND    e.encounter_datetime = last.encounter_datetime " +
                "WHERE  o.value_coded = %d " +
                "AND    e.voided=0 " +
                "AND    o.voided=0 " +
                "UNION " +
                "SELECT e.patient_id " +
                "FROM   encounter e " +
                "JOIN   obs o " +
                "ON     e.encounter_id = o.encounter_id " +
                "JOIN " +
                "       ( " +
                "                SELECT   p.patient_id, " +
                "                         max(o.value_datetime) value_datetime " +
                "                FROM     patient p " +
                "                JOIN     encounter e " +
                "                ON       p.patient_id = e.patient_id " +
                "                JOIN     obs o " +
                "                ON       e.encounter_id = o.encounter_id " +
                "                WHERE    o.concept_id = %d " +
                "                AND      e.location_id = :location " +
                "                AND      e.encounter_type IN (%d, " +
                "                                              %d) " +
                "                AND      o.value_datetime BETWEEN :startDate AND      :endDate " +
                "                AND      p.voided=0 " +
                "                GROUP BY p.patient_id) last " +
                "ON     e.patient_id = last.patient_id " +
                "AND    o.value_datetime = last.value_datetime " +
                "WHERE  o.value_coded IN(%d, " +
                "                        %d) " +
                "AND    e.voided=0 " +
                "AND    o.voided=0";

    return String.format(
        query,
        adultoSeguimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        tbStartDateConceptId,
        adultoSeguimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        tbEndDateConceptId,
        tbProgramId,
        patientStateId,
        activeTBConceptId,
        adultoSeguimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        yesConceptId,
        tbTreatmentPlanConceptId,
        adultoSeguimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        startDrugsConceptId,
        continueRegimenConceptId);
  }
}
