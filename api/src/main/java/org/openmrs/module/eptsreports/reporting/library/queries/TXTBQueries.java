package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.EncounterType;

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
        "SELECT patient_id FROM (SELECT patient_id, Min(data_inicio) data_inicio "
            + "FROM (SELECT p.patient_id, Min(e.encounter_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "WHERE e.voided = 0 AND o.voided = 0 AND p.voided = 0 AND e.encounter_type IN ( %s ) "
            + "AND o.concept_id = %s AND o.value_coded = %s AND e.encounter_datetime <= :endDate "
            + "AND e.location_id = :location GROUP BY p.patient_id "
            + "UNION SELECT p.patient_id, Min(value_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type IN ( %s ) "
            + "AND o.concept_id = %s AND o.value_datetime IS NOT NULL AND o.value_datetime <= :endDate AND e.location_id = :location "
            + "GROUP BY p.patient_id UNION SELECT pg.patient_id, date_enrolled data_inicio FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id = pg.patient_id WHERE pg.voided = 0 AND p.voided = 0 "
            + "AND program_id = %s AND date_enrolled <= :endDate AND location_id = :location "
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

  public static String inTBProgramWithinReportingPeriodAtLocation(Integer tbProgramId) {
    return String.format(
        "select pg.patient_id from patient p inner join patient_program pg on "
            + " p.patient_id=pg.patient_id where pg.voided=0 and "
            + " p.voided=0 and program_id=%s "
            + "and date_enrolled between :startDate and :endDate and location_id=:location",
        tbProgramId);
  }

  public static String inTBProgramWithinPreviousReportingPeriodAtLocation(Integer tbProgramId) {
    return String.format(
        "select pg.patient_id from patient p inner join patient_program pg on "
            + " p.patient_id=pg.patient_id where pg.voided=0 and "
            + " p.voided=0 and program_id=%s "
            + "and date_enrolled between date_add(:startDate, interval -6 MONTH) and date_add(:startDate, interval -1 DAY) and location_id=:location",
        tbProgramId);
  }

  public static String dateObs(
      Integer questionId, List<Integer> encounterTypeIds, boolean startDate) {
    String sql =
        String.format(
            "select obs.person_id from obs inner join encounter on encounter.encounter_id = obs.encounter_id "
                + " where obs.concept_id = %s and encounter.encounter_type in(%s) and obs.location_id = :location and obs.voided=0 and ",
            questionId, StringUtils.join(encounterTypeIds, ","));
    if (startDate) {
      sql += "obs.value_datetime >= :startDate and obs.value_datetime <= :endDate";
    } else {
      sql += "obs.value_datetime <= :endDate";
    }
    return sql;
  }

  public static String dateObsForPreviousReportingPeriod(
      Integer questionId, List<Integer> encounterTypeIds, boolean startDate) {
    String sql =
        String.format(
            "select obs.person_id from obs inner join encounter on encounter.encounter_id = obs.encounter_id "
                + " where obs.concept_id = %s and encounter.encounter_type in(%s) and obs.location_id = :location and obs.voided=0 and ",
            questionId, StringUtils.join(encounterTypeIds, ","));
    if (startDate) {
      sql +=
          "obs.value_datetime >= date_add(:startDate, interval -6 MONTH) and obs.value_datetime <= date_add(:startDate, interval -1 DAY)";
    } else {
      sql += "obs.value_datetime <= date_add(:startDate, interval -1 DAY)";
    }
    return sql;
  }

  public static String dateObsByObsDateTimeClausule(
      Integer conceptQuestionId, Integer conceptAnswerId, Integer encounterId) {
    String sql =
        String.format(
            "select distinct obs.person_id from obs "
                + "    inner join encounter on encounter.encounter_id = obs.encounter_id "
                + "  where obs.concept_id =%s and obs.value_coded =%s and encounter.encounter_type =%s "
                + "  and obs.location_id =:location and obs.obs_datetime >=:startDate and obs.obs_datetime <=:endDate and obs.voided=0",
            conceptQuestionId, conceptAnswerId, encounterId);

    return sql;
  }

  public static String dateObsByObsDateTimeClausuleInPreviousReportingPeriod(
      Integer conceptQuestionId, Integer conceptAnswerId, Integer encounterId) {
    String sql =
        String.format(
            "select distinct obs.person_id from obs "
                + "    inner join encounter on encounter.encounter_id = obs.encounter_id "
                + "  where obs.concept_id =%s and obs.value_coded =%s and encounter.encounter_type =%s "
                + "  and obs.location_id =:location and obs.obs_datetime >=date_add(:startDate, interval -6 MONTH) and obs.obs_datetime <=date_add(:startDate, interval -1 DAY) and obs.voided=0",
            conceptQuestionId, conceptAnswerId, encounterId);

    return sql;
  }

  public static String dateObsByObsValueDateTimeClausule(
      Integer conceptQuestionId, Integer conceptAnswerId, Integer encounterId) {
    String sql =
        String.format(
            "select distinct obs.person_id from obs "
                + "   inner join encounter on encounter.encounter_id = obs.encounter_id "
                + "where obs.concept_id =%s and obs.value_coded =%s and encounter.encounter_type =%s "
                + "  and obs.location_id =:location and obs.value_datetime >=:startDate and obs.value_datetime <=:endDate and obs.voided=0",
            conceptQuestionId, conceptAnswerId, encounterId);

    return sql;
  }

  public static String dateObsForEncounterAndQuestionAndAnswers(
      Integer encounterId, List<Integer> questionConceptIds, List<Integer> answerConceptIds) {
    String sql =
        String.format(
            "select distinct obs.person_id from obs "
                + "	 inner join encounter on encounter.encounter_id = obs.encounter_id where encounter.encounter_type = %s "
                + "  and obs.concept_id in (%s) and obs.value_coded in (%s) and encounter.voided = 0 and obs.voided =0 and obs.location_id =:location "
                + "  and encounter.encounter_datetime >=:startDate and encounter.encounter_datetime <=:endDate",
            encounterId,
            StringUtils.join(questionConceptIds, ","),
            StringUtils.join(answerConceptIds, ","));

    return sql;
  }

  public static String dateObsWithinXMonthsBeforeStartDate(
      Integer questionId, List<Integer> encounterTypeIds, Integer xMonths) {
    return String.format(
        "select obs.person_id from obs inner join encounter on encounter.encounter_id = obs.encounter_id "
            + " where obs.concept_id = %s and encounter.encounter_type in(%s) and obs.location_id = :location "
            + " and obs.value_datetime >= DATE_SUB(:startDate, INTERVAL "
            + "%s MONTH) "
            + " and obs.value_datetime < :startDate and obs.voided=0",
        questionId, StringUtils.join(encounterTypeIds, ","), xMonths);
  }

  public static String encounterObs(Integer encounterTypeId) {
    return String.format(
        "select distinct patient_id from encounter where encounter_type =%s and location_id = :location and encounter_datetime <= :endDate and voided=0;",
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

  public static String findNegativeInvestigationResultAndAnyResultForTBScreening(
      EncounterType artAdultFollowupEncounterType,
      EncounterType artPedInicioEncounterType,
      Concept screeningForTBConcept,
      Concept yesConcept,
      Concept noConcept,
      Concept resultForResearchInvestigationConcept,
      Concept negativeResultConcept) {

    return String.format(
        "select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id"
            + "	  inner join obs oRastreio on e.encounter_id=oRastreio.encounter_id and oRastreio.concept_id=6257 and oRastreio.voided=0 and oRastreio.value_coded in (1065,1066)"
            + "	  inner join obs oInvestigacao on e.encounter_id=oInvestigacao.encounter_id and oInvestigacao.concept_id=6277 and oInvestigacao.voided=0 and oInvestigacao.value_coded=664"
            + "	 where e.encounter_type in (6,9) and e.voided=0 and e.location_id=:location and e.encounter_datetime between :startDate and :endDate group by p.patient_id",
        screeningForTBConcept.getId(),
        Arrays.asList(yesConcept.getId(), noConcept.getId()),
        resultForResearchInvestigationConcept.getId(),
        negativeResultConcept.getId(),
        Arrays.asList(artAdultFollowupEncounterType.getId(), artPedInicioEncounterType.getId()));
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
