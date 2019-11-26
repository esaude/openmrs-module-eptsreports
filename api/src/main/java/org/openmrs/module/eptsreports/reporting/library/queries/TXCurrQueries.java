package org.openmrs.module.eptsreports.reporting.library.queries;

public class TXCurrQueries {

  public static String getPatientWithSTARTDRUGSObsBeforeOrOnEndDate(
      int aRVPharmaciaEncounterType,
      int adultoSeguimentoEncounterType,
      int aRVPediatriaSeguimentoEncounterType,
      int aRVPlanConcept,
      int startDrugsConcept) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "WHERE e.voided=0 AND o.voided=0 AND p.voided=0 AND e.encounter_type in (%d, %d, %d) "
            + "AND o.concept_id=%d AND o.value_coded in (%d) "
            + "AND e.encounter_datetime <= :onOrBefore AND e.location_id=:location GROUP BY p.patient_id";

    return String.format(
        query,
        aRVPharmaciaEncounterType,
        adultoSeguimentoEncounterType,
        aRVPediatriaSeguimentoEncounterType,
        aRVPlanConcept,
        startDrugsConcept);
  }

  public static String getPatientEnrolledInArtProgramByEndReportingPeriod(
      int aRTProgram, int artTransferredFromOtherHealthFacilityWorkflowState) {

    String query =
        "select p.patient_id  "
            + "from patient p "
            + "inner join patient_program pg on pg.patient_id=p.patient_id "
            + "inner  join patient_state ps on ps.patient_program_id=pg.patient_program_id "
            + "where p.voided=0 and  pg.voided=0  and ps.voided=0 "
            + "and pg.program_id=%s and  ps.state=%s and pg.date_enrolled <= :onOrBefore  "
            + "and pg.location_id= :location group by p.patient_id ";

    return String.format(query, aRTProgram, artTransferredFromOtherHealthFacilityWorkflowState);
  }

  public static String getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate(
      int aRVPharmaciaEncounterType) {

    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "WHERE p.voided=0 AND e.encounter_type=%d "
            + "AND e.voided=0 AND e.encounter_datetime <= :onOrBefore AND e.location_id=:location GROUP BY p.patient_id";

    return String.format(query, aRVPharmaciaEncounterType);
  }

  public static String getPatientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate(
      int artProgram,
      int transferredOutToAnotherHealthFacilityWorkflowState,
      int suspendedTreatmentWorkflowState,
      int artDeadWorkflowState) {

    String query =
        " select p.patient_id from patient p "
            + " inner join patient_program pg on p.patient_id=pg.patient_id "
            + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + " where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%d "
            + " and ps.state in (%d,%d,%d) and ps.end_date is null and ps.start_date<=:onOrBefore "
            + "and pg.location_id=:location group by p.patient_id  ";

    return String.format(
        query,
        artProgram,
        transferredOutToAnotherHealthFacilityWorkflowState,
        suspendedTreatmentWorkflowState,
        artDeadWorkflowState);
  }

  public static String getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate() {

    String query =
        "select p.patient_id  "
            + " from patient p "
            + " inner join person prs on prs.person_id=p.patient_id "
            + " inner join  encounter e on  e.patient_id=p.patient_id "
            + " where prs.dead=1 and prs.death_date <= :onOrBefore and p.voided=0 and prs.voided=0 "
            + " and e.location_id = :location group by p.patient_id ";

    return query;
  }

  public static String getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate(
      String encounterTypes,
      int patientFoundConcept,
      int reasonPatientNotFound,
      int noConcept,
      int patientIsDead) {
    String query =
        "select  p.patient_id"
            + " from patient p "
            + " inner join encounter e on e.patient_id=p.patient_id "
            + " inner join obs o  on o.encounter_id=e.encounter_id "
            + " where  p.voided=0  and e.voided=0 and o.voided=0 "
            + " and e.encounter_type in (%s) and ((o.concept_id = %s and o.value_coded = %s ) or (o.concept_id = %s and o.value_coded = %s )) "
            + " and e.encounter_datetime = (select  max(encounter_datetime) from encounter "
            + " where  patient_id= p.patient_id and encounter_type = e.encounter_type) "
            + " and e.encounter_datetime <= :onOrBefore and  e.location_id = :location group by p.patient_id ";

    return String.format(
        query,
        encounterTypes,
        patientFoundConcept,
        reasonPatientNotFound,
        noConcept,
        patientIsDead);
  }

  public static String getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayPriorArtPatientConcept,
      int patientHasDiedConcept) {

    String query =
        "select  p.patient_id "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + "and o.concept_id=%s and   o.value_coded=%s "
            + " and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + "group by p.patient_id";

    return String.format(
        query,
        adultoSeguimentoEncounterType,
        masterCardEncounterType,
        stateOfStayPriorArtPatientConcept,
        patientHasDiedConcept);
  }

  public static String getTransferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayPriorArtPatientConcept,
      int transferredOutConcept) {

    String query =
        "select  p.patient_id "
            + " from patient p "
            + " inner join encounter e on e.patient_id=p.patient_id "
            + " inner join obs o on o.encounter_id=e.encounter_id "
            + " where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + " and o.concept_id=%s and   o.value_coded=%s "
            + " and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + " group by p.patient_id ";

    return String.format(
        query,
        adultoSeguimentoEncounterType,
        masterCardEncounterType,
        stateOfStayPriorArtPatientConcept,
        transferredOutConcept);
  }

  public static String getPatientSuspendedInFichaResumeAndClinicaOfMasterCardByReportEndDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayPriorArtPatientConcept,
      int suspendedTreatmentConcept) {

    String query =
        "select  p.patient_id "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + "and o.concept_id=%s and   o.value_coded=%s "
            + " and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + "group by p.patient_id ";

    return String.format(
        query,
        adultoSeguimentoEncounterType,
        masterCardEncounterType,
        stateOfStayPriorArtPatientConcept,
        suspendedTreatmentConcept);
  }

  public static String getPatientHavingLastScheduledDrugPickupDate(
      int returnVisitDateForArvDrugConcept,
      int ARVPharmaciaEncounterType,
      int returnVisitDateConcept,
      int adultoSeguimentoEncounterType,
      int aRVPediatriaSeguimentoEncounterType,
      int artDatePickup,
      int msterCardDrugPickupEncounterType) {
    String query =
        "SELECT final.patient_id "
            + " FROM("
            + " SELECT most_recent.patient_id, Date_add(MAX(most_recent.obs_datetime), interval 30 day) AS final_obs_date "
            + " FROM("
            + " SELECT p.patient_id, MAX(recent.obs_datetime) AS obs_datetime "
            + " FROM   patient p "
            + " INNER JOIN ("
            + " SELECT enc.patient_id, MAX(enc.encounter_datetime) AS encounter_datetime, obs.value_datetime AS obs_datetime "
            + " FROM patient pa "
            + " INNER JOIN encounter enc "
            + " ON enc.patient_id =pa.patient_id "
            + " INNER JOIN obs obs "
            + " ON obs.encounter_id = enc.encounter_id "
            + " WHERE pa.voided = 0 "
            + " AND enc.voided = 0 "
            + " AND obs.voided = 0 "
            + " AND obs.concept_id =%d "
            + " AND obs.value_datetime IS NOT NULL "
            + " AND enc.encounter_type = %d "
            + " AND enc.location_id =:location "
            + " AND enc.encounter_datetime < :onOrBefore "
            + " GROUP BY pa.patient_id) recent "
            + " ON p.patient_id = recent.patient_id "
            + " WHERE  p.voided = 0 "
            + " GROUP  BY recent.patient_id "
            + " UNION "
            + " SELECT p.patient_id, MAX(recent.obs_datetime)  AS obs_datetime "
            + " FROM   patient p "
            + " INNER JOIN ("
            + " SELECT enc.patient_id, MAX(enc.encounter_datetime) AS encounter_datetime, obs.value_datetime AS obs_datetime "
            + " FROM patient pa "
            + " INNER JOIN encounter enc "
            + " ON enc.patient_id = pa.patient_id "
            + " INNER JOIN obs obs "
            + " ON obs.encounter_id = enc.encounter_id "
            + " WHERE  pa.voided = 0 "
            + " AND enc.voided = 0 "
            + " AND obs.voided = 0 "
            + " AND obs.concept_id =%d"
            + " AND obs.value_datetime IS NOT NULL "
            + " AND enc.encounter_type IN ( %d, %d ) "
            + " AND enc.location_id =:location "
            + " AND enc.encounter_datetime < :onOrBefore "
            + " GROUP  BY pa.patient_id) recent "
            + " ON p.patient_id = recent.patient_id "
            + " WHERE p.voided = 0 "
            + " GROUP  BY recent.patient_id "
            + " UNION "
            + " SELECT p.patient_id, MAX(recent.obs_datetime) AS obs_datetime "
            + " FROM   patient p "
            + " INNER JOIN("
            + " SELECT enc.patient_id, MAX(enc.encounter_datetime) AS encounter_datetime, Date_add(obs.value_datetime, interval 30 day) AS  obs_datetime "
            + " FROM patient pa "
            + " INNER JOIN encounter enc "
            + " ON enc.patient_id = pa.patient_id "
            + " INNER JOIN obs obs "
            + " ON obs.encounter_id = enc.encounter_id "
            + " WHERE  pa.voided = 0 "
            + " AND enc.voided = 0 "
            + " AND obs.voided = 0 "
            + " AND obs.concept_id = %d"
            + " AND obs.value_datetime IS NOT NULL "
            + " AND enc.encounter_type = %d "
            + " AND enc.location_id = :location "
            + " AND enc.encounter_datetime < :onOrBefore"
            + " GROUP  BY pa.patient_id) recent "
            + " ON p.patient_id = recent.patient_id "
            + " WHERE p.voided = 0 "
            + " GROUP  BY recent.patient_id) most_recent "
            + " GROUP  BY most_recent.patient_id) final "
            + " WHERE final.final_obs_date < :onOrBefore";

    return String.format(
        query,
        returnVisitDateForArvDrugConcept,
        ARVPharmaciaEncounterType,
        returnVisitDateConcept,
        adultoSeguimentoEncounterType,
        aRVPediatriaSeguimentoEncounterType,
        artDatePickup,
        msterCardDrugPickupEncounterType);
  }

  public static String getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup(
      int adultoSeguimentoEncounterType,
      int ARVPediatriaSeguimentoEncounterType,
      int aRVPharmaciaEncounterType,
      int masterCardDrugPickupEncounterType,
      int adultoSeguimentoEncounterType1,
      int aRVPediatriaSeguimentoEncounterType,
      int aRVPharmaciaEncounterType1,
      int adultoSeguimentoEncounterType2,
      int aRVPediatriaSeguimentoEncounterType2,
      int aRVPharmaciaEncounterType2,
      int returnVisitDateConcept,
      int returnVisitDateForArvDrugConcept) {

    String query =
        "SELECT ps.patient_id  "
            + "FROM   (SELECT p.patient_id  "
            + "        FROM   patient p  "
            + "               INNER JOIN encounter e  "
            + "                       ON e.patient_id = p.patient_id  "
            + "        WHERE  p.voided = 0  "
            + "               AND e.voided = 0  "
            + "               AND p.patient_id NOT IN (SELECT patient_id  "
            + "                                        FROM   encounter  "
            + "                                        WHERE  encounter_type IN (  "
            + "                                               %s,%s, %s, %s )  "
            + "                                               AND location_id = :location)  "
            + "               AND e.location_id = :location  "
            + "        GROUP  BY p.patient_id  "
            + "        UNION  "
            + "        SELECT p.patient_id  "
            + "        FROM   patient p  "
            + "               INNER JOIN encounter e  "
            + "                       ON e.patient_id = p.patient_id  "
            + "               INNER JOIN obs o  "
            + "                       ON o.encounter_id = e.encounter_id  "
            + "        WHERE  p.voided = 0  "
            + "               AND e.voided = 0  "
            + "               AND o.voided = 0  "
            + "               AND e.encounter_datetime = (SELECT Max(encounter_datetime)  "
            + "                                           FROM   encounter  "
            + "                                           WHERE  patient_id = p.patient_id  "
            + "                                                  AND encounter_type  IN ( %s, %s, %s )  "
            + "                                          )  "
            + "               AND e.encounter_type IN ( %s, %s, %s )  "
            + "               AND e.encounter_id NOT IN (SELECT encounter_id  "
            + "                                          FROM   obs  "
            + "                                          WHERE  (concept_id = %s and (value_datetime is null or value_numeric is null)) "
            + "                                                  OR (concept_id = %s and (value_datetime is null or value_numeric is null)))  "
            + "               AND e.location_id = :location  "
            + "        GROUP  BY p.patient_id) ps  "
            + "GROUP  BY ps.patient_id;";

    return String.format(
        query,
        adultoSeguimentoEncounterType,
        ARVPediatriaSeguimentoEncounterType,
        aRVPharmaciaEncounterType,
        masterCardDrugPickupEncounterType,
        adultoSeguimentoEncounterType1,
        aRVPediatriaSeguimentoEncounterType,
        aRVPharmaciaEncounterType1,
        adultoSeguimentoEncounterType2,
        aRVPediatriaSeguimentoEncounterType2,
        aRVPharmaciaEncounterType2,
        returnVisitDateConcept,
        returnVisitDateForArvDrugConcept);
  }

  public static String getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultation(
      int adultoSeguimentoEncounterType,
      int aRVPediatriaSeguimentoEncounterType,
      int aRVPharmaciaEncounterType,
      int masterCardDrugPickupEncounterType,
      int artDatePickup) {
    String query =
        " select p.patient_id "
            + " from patient p "
            + " inner join encounter e on  e.patient_id = p.patient_id "
            + " where  p.voided=0  and e.voided=0 "
            + " and e.encounter_type in (%s,%s,%s)  "
            + " and e.encounter_datetime > (select  max(encounter_datetime) from encounter where  patient_id= p.patient_id "
            + " and encounter_type = e.encounter_type ) "
            + " and e.location_id= :location group by p.patient_id "
            + " union "
            + " select p.patient_id "
            + " from patient p "
            + " inner join encounter e on e.patient_id=p.patient_id "
            + " inner join obs obss on obss.encounter_id=e.encounter_id "
            + " where e.encounter_type = %s and p.voided=0  and e.voided=0 and obss.voided=0 "
            + " and obss.concept_id=%s  "
            + " and obss.value_datetime > (select  max(value_datetime) from obs where  encounter_id= e.encounter_id  "
            + " and concept_id = obss.concept_id) "
            + " and e.location_id= :location group by p.patient_id ";

    return String.format(
        query,
        adultoSeguimentoEncounterType,
        aRVPediatriaSeguimentoEncounterType,
        aRVPharmaciaEncounterType,
        masterCardDrugPickupEncounterType,
        artDatePickup);
  }

  public static String getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultationComposition(
      int adultoSeguimento,
      int aRVPediatriaSeguimento,
      int aRVPharmacia,
      int masterCardDrugPickup,
      int artDatePickup,
      int masterCardEncounterType,
      int transferredOutToAnotherHealthFacilityWorkflowState,
      int getSuspendedTreatmentWorkflowState,
      int getArtDeadWorkflowState,
      int buscaActivaEncounterType,
      int visitaApoioReintegracaoParteA,
      int visitaApoioReintegracaoParteB,
      int patientFoundConcept,
      int noConcept,
      int reasonPatientNotFound,
      int patientIsDead,
      int stateOfStayOfPreArtPatient,
      int patientHasDiedConcept,
      int transferredOutConcept,
      int suspendedTreatmentConcept,
      int artProgram) {
    String query =
        "select p.patient_id  "
            + "             from patient p  "
            + "             inner join encounter e on  e.patient_id = p.patient_id  "
            + "             inner join obs obss on obss.encounter_id=e.encounter_id  "
            + "             where  p.voided=0  and e.voided=0  and obss.voided=0  "
            + "             and e.encounter_type in ("
            + adultoSeguimento
            + ","
            + aRVPediatriaSeguimento
            + ","
            + aRVPharmacia
            + ")  or  ( e.encounter_type = "
            + masterCardDrugPickup
            + " and obss.concept_id="
            + artDatePickup
            + " ) "
            + "             and e.encounter_datetime > (select max(most_recent.common_date)  "
            + "from( "
            + " "
            + "select p.patient_id  , ps.end_date common_date from patient p  "
            + "             inner join patient_program pg on p.patient_id=pg.patient_id  "
            + "             inner join patient_state ps on pg.patient_program_id=ps.patient_program_id  "
            + "             where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id="
            + artProgram
            + " "
            + "            and ps.state in ("
            + transferredOutToAnotherHealthFacilityWorkflowState
            + ","
            + getSuspendedTreatmentWorkflowState
            + ","
            + getArtDeadWorkflowState
            + ") and ps.end_date is null and ps.start_date<= :onOrBefore "
            + "            and pg.location_id= :location group by p.patient_id  "
            + " "
            + "union "
            + "             select p.patient_id  , prs.death_date common_date "
            + "            from patient p  "
            + "            inner join person prs on prs.person_id=p.patient_id  "
            + "            inner join  encounter e on  e.patient_id=p.patient_id  "
            + "               where prs.dead=1 and prs.death_date <= :onOrBefore and p.voided=0 and prs.voided=0  "
            + "            and e.location_id = :location group by p.patient_id "
            + " "
            + "union "
            + " "
            + "select  p.patient_id , e.encounter_datetime common_date "
            + "            from patient p  "
            + "            inner join encounter e on e.patient_id=p.patient_id  "
            + "            inner join obs o  on o.encounter_id=e.encounter_id  "
            + "            where  p.voided=0  and e.voided=0 and o.voided=0  "
            + "            and e.encounter_type in ("
            + buscaActivaEncounterType
            + ","
            + visitaApoioReintegracaoParteA
            + ","
            + visitaApoioReintegracaoParteB
            + ") and ((o.concept_id = "
            + patientFoundConcept
            + " and o.value_coded = "
            + noConcept
            + " )  "
            + "                        or (o.concept_id = "
            + reasonPatientNotFound
            + " and o.value_coded ="
            + patientIsDead
            + " ))  "
            + "            and e.encounter_datetime = (select  max(encounter_datetime) from encounter  "
            + "            where  patient_id= p.patient_id and encounter_type = e.encounter_type)  "
            + "            and e.encounter_datetime <= :onOrBefore and  e.location_id = :location group by p.patient_id  "
            + " "
            + "union "
            + " "
            + "select  p.patient_id , e.encounter_datetime common_date  "
            + "             from patient p  "
            + "             inner join encounter e on e.patient_id=p.patient_id  "
            + "             inner join obs o on o.encounter_id=e.encounter_id  "
            + "             where e.encounter_type in ("
            + adultoSeguimento
            + ","
            + masterCardEncounterType
            + ") and p.voided=0  and e.voided=0 and o.voided=0  "
            + "             and o.concept_id="
            + stateOfStayOfPreArtPatient
            + " and   o.value_coded="
            + patientHasDiedConcept
            + " "
            + "              and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + "             group by p.patient_id "
            + "union "
            + "             select  p.patient_id , e.encounter_datetime common_date  "
            + "             from patient p  "
            + "             inner join encounter e on e.patient_id=p.patient_id  "
            + "             inner join obs o on o.encounter_id=e.encounter_id  "
            + "             where e.encounter_type in ("
            + adultoSeguimento
            + ","
            + masterCardEncounterType
            + ") and p.voided=0  and e.voided=0 and o.voided=0  "
            + "             and o.concept_id="
            + stateOfStayOfPreArtPatient
            + " and   o.value_coded="
            + transferredOutConcept
            + " "
            + "              and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + "             group by p.patient_id "
            + "union "
            + "             select  p.patient_id , e.encounter_datetime common_date  "
            + "             from patient p  "
            + "             inner join encounter e on e.patient_id=p.patient_id  "
            + "             inner join obs o on o.encounter_id=e.encounter_id  "
            + "             where e.encounter_type in ("
            + adultoSeguimento
            + ","
            + masterCardEncounterType
            + ") and p.voided=0  and e.voided=0 and o.voided=0  "
            + "             and o.concept_id="
            + stateOfStayOfPreArtPatient
            + " and   o.value_coded="
            + suspendedTreatmentConcept
            + " "
            + "              and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + "             group by p.patient_id) most_recent  where  most_recent.common_date is not null )  "
            + "             and e.location_id= :location group by p.patient_id ";

    return query;
  }
}
