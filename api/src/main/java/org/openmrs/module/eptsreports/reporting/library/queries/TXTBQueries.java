package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class TXTBQueries {

  /**
   * Copied straight from INICIO DE TRATAMENTO ARV - NUM PERIODO: INCLUI TRANSFERIDOS DE COM DATA DE
   * INICIO CONHECIDA (SQL) SqlCohortDefinition#91787a86-0362-4820-a4ee-025d5501198b in backup
   *
   * @return sql
   */
  public static String arvTreatmentIncludesTransfersFromWithKnownStartData(
      Integer arvPlanConceptId,
      Integer startDrugsConceptId,
      Integer historicalDrugsStartDateConceptId,
      Integer artProgramId,
      Integer pharmacyEncounterTypeId,
      Integer artAdultFollowupEncounterTypeId,
      Integer artPedFollowupEncounterTypeId) {
    String encounterTypeIds =
        StringUtils.join(
            Arrays.asList(
                pharmacyEncounterTypeId,
                artAdultFollowupEncounterTypeId,
                artPedFollowupEncounterTypeId),
            ",");
    return String.format(
        "SELECT patient_id FROM (SELECT patient_id, MIN(data_inicio) data_inicio "
            + "FROM (SELECT p.patient_id, MIN(e.encounter_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "WHERE e.voided = 0 AND o.voided = 0 AND p.voided = 0 AND e.encounter_type IN ( %s ) "
            + "AND o.concept_id = %s AND o.value_coded = %s AND e.encounter_datetime <= :endDate "
            + "AND e.location_id = :location GROUP BY p.patient_id "
            + "UNION SELECT p.patient_id, MIN(value_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type IN ( %s ) "
            + "AND o.concept_id = %s AND o.value_datetime IS NOT NULL AND o.value_datetime <= :endDate AND e.location_id = :location "
            + "GROUP BY p.patient_id UNION SELECT pg.patient_id, date_enrolled data_inicio FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id = pg.patient_id WHERE pg.voided = 0 AND p.voided = 0 "
            + "AND program_id = %s AND date_enrolled IS NOT NULL  AND date_enrolled <= :endDate AND location_id = :location "
            + "UNION SELECT e.patient_id, Min(e.encounter_datetime) AS data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id WHERE p.voided = 0 AND e.encounter_type = %s "
            + "AND e.voided = 0 AND e.encounter_datetime <= :endDate AND e.location_id = :location GROUP BY p.patient_id) inicio_real "
            + "GROUP BY patient_id)inicio WHERE data_inicio BETWEEN :startDate AND :endDate ",
        encounterTypeIds,
        arvPlanConceptId,
        startDrugsConceptId,
        encounterTypeIds,
        historicalDrugsStartDateConceptId,
        artProgramId,
        pharmacyEncounterTypeId);
  }

  // exited by either transfer out, treatment suspension, treatment abandoned
  // or death of patient
  public static String patientsAtProgramStates(Integer artProgramId, List<Integer> stateIds) {
    return String.format(
        "SELECT pg.patient_id FROM patient p  "
            + "INNER JOIN patient_program pg  ON p.patient_id = pg.patient_id  "
            + "INNER JOIN patient_state ps  ON pg.patient_program_id = ps.patient_program_id "
            + "WHERE pg.voided = 0  AND ps.voided = 0  AND p.voided = 0  AND pg.program_id = %s "
            + "AND ps.state IN ( %s )  AND ps.end_date IS NULL  AND ps.start_date IS NOT NULL AND ps.start_date <= :endDate  "
            + "AND location_id = :location ",
        artProgramId, StringUtils.join(stateIds, ","));
  }

  /** ABANDONO NÃO NOTIFICADO - TARV SqlCohortDefinition#a1145104-132f-460b-b85e-ea265916625b */
  public static String abandonedWithNoNotification(AbandonedWithoutNotificationParams params) {
    String stateIds =
        StringUtils.join(
            Arrays.asList(
                params.transferOutStateId,
                params.treatmentSuspensionStateId,
                params.treatmentAbandonedStateId,
                params.deathStateId),
            ",");
    return String.format(
        "SELECT patient_id FROM (SELECT p.patient_id, MAX(encounter_datetime) encounter_datetime FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id WHERE p.voided = 0 AND e.voided = 0 "
            + "AND e.encounter_type = %s AND e.location_id = :location AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) max_frida "
            + "INNER JOIN obs o ON o.person_id = max_frida.patient_id WHERE max_frida.encounter_datetime = o.obs_datetime AND o.voided = 0 "
            + "AND o.concept_id = %s AND o.location_id = :location AND patient_id "
            + "NOT IN (SELECT pg.patient_id FROM patient p INNER JOIN patient_program pg ON p.patient_id = pg.patient_id "
            + "INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id WHERE pg.voided = 0 AND ps.voided = 0 "
            + "AND p.voided = 0 AND pg.program_id = %s AND ps.state IN ( %s ) AND ps.end_date IS NULL AND ps.start_date IS NOT NULL AND ps.start_date <= :endDate "
            + "AND location_id = :location) AND patient_id NOT IN(SELECT patient_id FROM "
            + "(SELECT p.patient_id, MAX(encounter_datetime) encounter_datetime FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id WHERE p.voided = 0 AND e.voided = 0 "
            + "AND e.encounter_type IN ( %s, %s ) AND e.location_id = :location AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) max_mov "
            + "INNER JOIN obs o ON o.person_id = max_mov.patient_id WHERE max_mov.encounter_datetime = o.obs_datetime AND o.voided = 0 "
            + "AND o.concept_id = %s AND o.location_id = :location AND o.value_datetime IS NOT NULL AND Datediff(:endDate, o.value_datetime) <= 60) AND patient_id "
            + "NOT IN(SELECT abandono.patient_id FROM (SELECT pg.patient_id FROM patient p INNER JOIN patient_program pg ON p.patient_id = pg.patient_id "
            + "INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id WHERE pg.voided = 0 AND ps.voided = 0 AND p.voided = 0 AND pg.program_id = %s "
            + "AND ps.state = %s AND ps.end_date IS NULL AND ps.start_date IS NOT NULL AND ps.start_date <= :endDate AND location_id = :location)abandono "
            + "INNER JOIN (SELECT max_frida.patient_id, max_frida.encounter_datetime, o.value_datetime FROM "
            + "(SELECT p.patient_id, Max(encounter_datetime) encounter_datetime FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id WHERE p.voided = 0 AND e.voided = 0 AND e.encounter_type = %s"
            + " AND e.location_id = :location "
            + "AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) max_frida INNER JOIN obs o ON o.person_id = max_frida.patient_id "
            + "WHERE max_frida.encounter_datetime = o.obs_datetime AND o.voided = 0 AND o.concept_id = %s AND o.location_id = :location) ultimo_fila "
            + "ON abandono.patient_id = ultimo_fila.patient_id WHERE Datediff(:endDate, ultimo_fila.value_datetime) < 60) AND o.value_datetime IS NOT NULL AND Datediff(:endDate, o.value_datetime) >= 60;",
        params.pharmacyEncounterTypeId,
        params.returnVisitDateForARVDrugConceptId,
        params.programId,
        stateIds,
        params.artAdultFollowupEncounterTypeId,
        params.artPedInicioEncounterTypeId,
        params.returnVisitDateConceptId,
        params.programId,
        params.treatmentAbandonedStateId,
        params.pharmacyEncounterTypeId,
        params.returnVisitDateForARVDrugConceptId);
  }

  public static String inTBProgramWithinReportingPeriodAtLocation(Integer tbProgramId) {
    return String.format(
        "SELECT pg.patient_id FROM patient p INNER JOIN "
            + "patient_program pg ON p.patient_id=pg.patient_id "
            + "WHERE pg.voided=0 AND p.voided=0 AND program_id=%s AND pg.date_completed IS NULL "
            + " AND pg.date_enrolled IS NOT NULL  AND pg.date_enrolled BETWEEN :startDate AND :endDate AND pg.location_id=:location",
        tbProgramId);
  }

  public static String dateObs(
      Integer questionId, List<Integer> encounterTypeIds, boolean startDate) {
    String sql =
        String.format(
            "SELECT person_id FROM obs "
                + "WHERE concept_id = %s AND encounter_id IN("
                + "SELECT DISTINCT encounter_id "
                + "FROM encounter "
                + "WHERE encounter_type IN(%s)) AND location_id = :location and ",
            questionId, StringUtils.join(encounterTypeIds, ","));
    if (startDate) {
      sql +=
          "value_datetime IS NOT NULL AND value_datetime >= :startDate and value_datetime <= :endDate and voided=0";
    } else {
      sql += "value_datetime IS NOT NULL AND value_datetime <= :endDate and voided=0";
    }
    return sql;
  }
  public static String dateObsWithinXMonthsBeforeStartDate(
      Integer questionId, List<Integer> encounterTypeIds, Integer xMonths) {
    return String.format(
        "SELECT person_id FROM obs "
            + "WHERE concept_id = %s AND encounter_id IN"
            + "( SELECT DISTINCT encounter_id FROM encounter "
            + "WHERE encounter_type IN(%s)) AND location_id = :location "
            + "AND value_datetime IS NOT NULL AND value_datetime >= DATE_SUB(:startDate, INTERVAL "
            + "%s MONTH) "
            + "AND value_datetime IS NOT NULL AND value_datetime < :startDate and voided=0",
        questionId, StringUtils.join(encounterTypeIds, ","), xMonths);
  }

  public static String encounterObs(Integer encounterTypeId) {
    return String.format(
        "SELECT DISTINCT patient_id FROM encounter WHERE encounter_type =%s AND location_id = :location AND encounter_datetime <= :endDate and voided=0;",
        encounterTypeId);
  }

  public static String patientWithFirstDrugPickupEncounterInReportingPeriod(
      Integer encounterTypeId) {
    return String.format(
        "SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "WHERE p.voided=0 AND e.encounter_type=%s AND e.voided=0 AND e.encounter_datetime>=:startDate AND e.encounter_datetime<=:endDate AND e.location_id=:location GROUP BY p.patient_id",
        encounterTypeId);
  }

  public static class AbandonedWithoutNotificationParams {
    protected Integer programId;
    protected Integer returnVisitDateConceptId;
    protected Integer returnVisitDateForARVDrugConceptId;
    protected Integer pharmacyEncounterTypeId;
    protected Integer artAdultFollowupEncounterTypeId;
    protected Integer artPedInicioEncounterTypeId;
    protected Integer transferOutStateId;
    protected Integer treatmentSuspensionStateId;
    protected Integer treatmentAbandonedStateId;
    protected Integer deathStateId;

    public AbandonedWithoutNotificationParams programId(Integer programId) {
      this.programId = programId;
      return this;
    }

    public AbandonedWithoutNotificationParams returnVisitDateConceptId(
        Integer returnVisitDateConceptId) {
      this.returnVisitDateConceptId = returnVisitDateConceptId;
      return this;
    }

    public AbandonedWithoutNotificationParams returnVisitDateForARVDrugConceptId(
        Integer returnVisitDateForARVDrugConceptId) {
      this.returnVisitDateForARVDrugConceptId = returnVisitDateForARVDrugConceptId;
      return this;
    }

    public AbandonedWithoutNotificationParams pharmacyEncounterTypeId(
        Integer pharmacyEncounterTypeId) {
      this.pharmacyEncounterTypeId = pharmacyEncounterTypeId;
      return this;
    }

    public AbandonedWithoutNotificationParams artAdultFollowupEncounterTypeId(
        Integer artAdultFollowupEncounterTypeId) {
      this.artAdultFollowupEncounterTypeId = artAdultFollowupEncounterTypeId;
      return this;
    }

    public AbandonedWithoutNotificationParams artPedInicioEncounterTypeId(
        Integer artPedInicioEncounterTypeId) {
      this.artPedInicioEncounterTypeId = artPedInicioEncounterTypeId;
      return this;
    }

    public AbandonedWithoutNotificationParams transferOutStateId(Integer transferOutStateId) {
      this.transferOutStateId = transferOutStateId;
      return this;
    }

    public AbandonedWithoutNotificationParams treatmentSuspensionStateId(
        Integer treatmentSuspensionStateId) {
      this.treatmentSuspensionStateId = treatmentSuspensionStateId;
      return this;
    }

    public AbandonedWithoutNotificationParams treatmentAbandonedStateId(
        Integer treatmentAbandonedStateId) {
      this.treatmentAbandonedStateId = treatmentAbandonedStateId;
      return this;
    }

    public AbandonedWithoutNotificationParams deathStateId(Integer deathStateId) {
      this.deathStateId = deathStateId;
      return this;
    }
  }
}
