package org.openmrs.module.eptsreports.reporting.library.queries;

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
        + "INNER JOIN patient_program pg ON p.patient_id = pg.patient_id WHERE pg.voided = 0 AND p.voided = 0 "
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

  // exited by either transfer out, treatment suspension, treatment abandoned
  // or death of patient
  public static String patientsWhoCameOutOfARVTreatmentProgram(
      Integer ARTProgramId,
      Integer transferOutStateId,
      Integer treatmentSuspensionStateId,
      Integer treatmentAbandonedStateId,
      Integer deathStateId) {
    return "SELECT pg.patient_id FROM patient p  "
        + "INNER JOIN patient_program pg  ON p.patient_id = pg.patient_id  "
        + "INNER JOIN patient_state ps  ON pg.patient_program_id = ps.patient_program_id "
        + "WHERE pg.voided = 0  AND ps.voided = 0  AND p.voided = 0  AND pg.program_id = "
        + ARTProgramId
        + " AND ps.state IN ( "
        + transferOutStateId
        + ", "
        + treatmentSuspensionStateId
        + ", "
        + treatmentAbandonedStateId
        + ", "
        + deathStateId
        + " )  AND ps.end_date IS NULL  AND ps.start_date <= :endDate  "
        + "AND location_id = :location ";
  }

  /** ABANDONO NÃO NOTIFICADO - TARV SqlCohortDefinition#a1145104-132f-460b-b85e-ea265916625b */
  public static String abandonedWithNoNotification(AbandonedWithoutNotificationParams params) {
    return "SELECT patient_id FROM (SELECT p.patient_id, Max(encounter_datetime) encounter_datetime FROM patient p "
        + "INNER JOIN encounter e ON e.patient_id = p.patient_id WHERE p.voided = 0 AND e.voided = 0 "
        + "AND e.encounter_type = "
        + params.pharmacyEncounterTypeId
        + " AND e.location_id = :location AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) max_frida "
        + "INNER JOIN obs o ON o.person_id = max_frida.patient_id WHERE max_frida.encounter_datetime = o.obs_datetime AND o.voided = 0 "
        + "AND o.concept_id = "
        + params.returnVisitDateForARVDrugConceptId
        + " AND o.location_id = :location AND patient_id "
        + "NOT IN (SELECT pg.patient_id FROM patient p INNER JOIN patient_program pg ON p.patient_id = pg.patient_id "
        + "INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id WHERE pg.voided = 0 AND ps.voided = 0 "
        + "AND p.voided = 0 AND pg.program_id = "
        + params.programId
        + " AND ps.state IN ( "
        + params.transferOutStateId
        + ", "
        + params.treatmentSuspensionStateId
        + ", "
        + params.treatmentAbandonedStateId
        + ", "
        + params.deathStateId
        + ") AND ps.end_date IS NULL AND ps.start_date <= :endDate "
        + "AND location_id = :location) AND patient_id NOT IN(SELECT patient_id FROM "
        + "(SELECT p.patient_id, Max(encounter_datetime) encounter_datetime FROM patient p "
        + "INNER JOIN encounter e ON e.patient_id = p.patient_id WHERE p.voided = 0 AND e.voided = 0 "
        + "AND e.encounter_type IN ( "
        + params.artAdultFollowupEncounterTypeId
        + ", "
        + params.artPedInicioEncounterTypeId
        + " ) AND e.location_id = :location AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) max_mov "
        + "INNER JOIN obs o ON o.person_id = max_mov.patient_id WHERE max_mov.encounter_datetime = o.obs_datetime AND o.voided = 0 "
        + "AND o.concept_id = "
        + params.returnVisitDateConceptId
        + " AND o.location_id = :location AND Datediff(:endDate, o.value_datetime) <= 60) AND patient_id "
        + "NOT IN(SELECT abandono.patient_id FROM (SELECT pg.patient_id FROM patient p INNER JOIN patient_program pg ON p.patient_id = pg.patient_id "
        + "INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id WHERE pg.voided = 0 AND ps.voided = 0 AND p.voided = 0 AND pg.program_id = 2 "
        + "AND ps.state = "
        + params.treatmentAbandonedStateId
        + " AND ps.end_date IS NULL AND ps.start_date <= :endDate AND location_id = :location)abandono "
        + "INNER JOIN (SELECT max_frida.patient_id, max_frida.encounter_datetime, o.value_datetime FROM "
        + "(SELECT p.patient_id, Max(encounter_datetime) encounter_datetime FROM patient p "
        + "INNER JOIN encounter e ON e.patient_id = p.patient_id WHERE p.voided = 0 AND e.voided = 0 AND e.encounter_type = "
        + params.pharmacyEncounterTypeId
        + " AND e.location_id = :location "
        + "AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) max_frida INNER JOIN obs o ON o.person_id = max_frida.patient_id "
        + "WHERE max_frida.encounter_datetime = o.obs_datetime AND o.voided = 0 AND o.concept_id = "
        + params.returnVisitDateForARVDrugConceptId
        + " AND o.location_id = :location) ultimo_fila "
        + "ON abandono.patient_id = ultimo_fila.patient_id WHERE Datediff(:endDate, ultimo_fila.value_datetime) < 60) AND Datediff(:endDate, o.value_datetime) >= 60; ";
  }

  public static String patientsEnrolledInARTCareAndOnTreatment(
      Integer artCareProgramId,
      Integer artTreatmentProgramId,
      Integer artAdultInitialEncounterTypeId,
      Integer artPedsInitialEncounterTypeId,
      Integer screeningState,
      Integer evaluationAndPrepState) {
    return "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON e.patient_id = p.patient_id "
        + "WHERE e.voided = 0 AND p.voided = 0 AND e.encounter_type IN ( "
        + artAdultInitialEncounterTypeId
        + ", "
        + artPedsInitialEncounterTypeId
        + " ) AND e.encounter_datetime <= :endDate AND e.location_id = :location "
        + "UNION SELECT pg.patient_id FROM patient p INNER JOIN patient_program pg ON p.patient_id = pg.patient_id WHERE pg.voided = 0 AND p.voided = 0 "
        + "AND program_id = "
        + artCareProgramId
        + " AND date_enrolled <= :endDate AND location_id = :location UNION SELECT pg.patient_id FROM patient p "
        + "INNER JOIN patient_program pg ON p.patient_id = pg.patient_id INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id "
        + "WHERE pg.voided = 0 AND ps.voided = 0 AND p.voided = 0 AND pg.program_id = "
        + artCareProgramId
        + " AND ps.state = "
        + screeningState
        + " AND ps.start_date = pg.date_enrolled AND ps.start_date <= :endDate "
        + "AND location_id = :location UNION SELECT pg.patient_id FROM patient p INNER JOIN patient_program pg ON p.patient_id = pg.patient_id "
        + "INNER JOIN patient_state ps ON pg.patient_program_id = ps.patient_program_id WHERE pg.voided = 0 AND ps.voided = 0 AND p.voided = 0 "
        + "AND pg.program_id = "
        + artTreatmentProgramId
        + " AND ps.state = "
        + evaluationAndPrepState
        + " AND ps.start_date <= :endDate AND location_id = :location ";
  }

  public static String inARTProgramToEndDateAtLocation(Integer aRTProgramId) {
    return "select pg.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id where pg.voided=0 and p.voided=0 and program_id="
        + aRTProgramId
        + " and date_enrolled<=:endDate and location_id=:location";
  }

  public static String inTBProgramToEndDateAtLocation(Integer tbProgramId) {
    return "select pg.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id where pg.voided=0 and p.voided=0 and program_id="
        + tbProgramId
        + " and date_enrolled between :startDate and :endDate and location_id=:location";
  }

  public static String dateObs(
      Integer questionId, List<Integer> encounterTypeIds, boolean startDate) {
    String sql =
        "select person_id from obs where concept_id = "
            + questionId
            + " and encounter_id in(select distinct encounter_id from encounter where encounter_type in("
            + StringUtils.join(encounterTypeIds, ",")
            + ")) and location_id = :location and ";
    if (startDate) {
      sql += "value_datetime >= :startDate and value_datetime <= :endDate and voided=0";
    } else {
      sql += "value_datetime <= :endDate and voided=0";
    }
    return sql;
  }

  public static String encounterObs(Integer encounterTypeId) {
    return "select distinct patient_id from encounter where encounter_type ="
        + encounterTypeId
        + " and location_id = :location and encounter_datetime <= :endDate and voided=0;";
  }

  public static String patientsTransferredFromARTTreatment(
      Integer artProgramId, Integer transferOutStateId) {
    return "select pg.patient_id from patient p  inner join patient_program pg on p.patient_id=pg.patient_id inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
        + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id="
        + artProgramId
        + " and ps.state="
        + transferOutStateId
        + " and ps.start_date=pg.date_enrolled and ps.start_date between :startDate and :endDate and location_id=:location";
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
