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
        " SELECT named.patient_id FROM ( "
            + " SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime)   "
            + " FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + " WHERE p.voided=0 AND e.voided=0 AND e.encounter_type IN(%s) AND e.encounter_datetime is not null AND e.encounter_datetime <= :onOrBefore AND "
            + " e.encounter_id IN ( "
            + "      select ee.encounter_id from "
            + "      encounter ee INNER JOIN obs o ON o.encounter_id=ee.encounter_id AND o.voided=0 AND "
            + "      ee.encounter_type IN(%s) AND "
            + "      o.concept_id=%s AND o.value_coded=%s) AND "
            + " e.encounter_id IN ( "
            + "      select eee.encounter_id from "
            + "      encounter eee INNER JOIN obs oo ON oo.encounter_id=eee.encounter_id AND oo.voided=0 AND "
            + "      eee.encounter_type IN(%s) AND "
            + "oo.concept_id=%s AND oo.value_coded=%s) and e.location_id = :location  group by p.patient_id) named ";

    return String.format(
        query,
        encounterTypes,
        encounterTypes,
        patientFoundConcept,
        reasonPatientNotFound,
        encounterTypes,
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
        " SELECT final.patient_id  "
            + "FROM   (SELECT most_recent.patient_id,  "
            + "               Date_add(Max(most_recent.obs_datetime), interval 30 day)  "
            + "               final_obs_date  "
            + "        FROM   (SELECT p.patient_id,  "
            + "                         Max(recent.obs_datetime)  as obs_datetime "
            + "                FROM   patient p  "
            + "                       inner join (SELECT enc.patient_id,  "
            + "                                           Max(enc.encounter_datetime)  "
            + "                                          encounter_datetime,  "
            + "                                          obs.value_datetime  "
            + "                                          obs_datetime  "
            + "                                   FROM   patient pa  "
            + "                                          inner join encounter enc  "
            + "                                                  ON enc.patient_id =  "
            + "                                                     pa.patient_id  "
            + "                                          inner join obs obs  "
            + "                                                  ON obs.encounter_id =  "
            + "                                                     enc.encounter_id  "
            + "                                   WHERE  pa.voided = 0  "
            + "                                          AND enc.voided = 0  "
            + "                                          AND obs.voided = 0  "
            + "                                          AND obs.concept_id = %s  "
            + "                                          AND obs.value_datetime IS NOT NULL  "
            + "                                          AND enc.encounter_type = %s  "
            + "                                          AND enc.location_id =  :location  "
            + "                                          AND enc.encounter_datetime <  "
            + "                                               :onOrBefore  "
            + "                                   GROUP  BY pa.patient_id) recent  "
            + "                               ON p.patient_id = recent.patient_id  "
            + "                                  AND p.voided = 0  "
            + "                GROUP  BY recent.patient_id  "
            + "                UNION  "
            + "                SELECT p.patient_id,  "
            + "                        Max(recent.obs_datetime)  as obs_datetime "
            + "                FROM   patient p  "
            + "                       inner join (SELECT enc.patient_id,  "
            + "                                          Max(enc.encounter_datetime)  "
            + "                                          encounter_datetime,  "
            + "                                          obs.value_datetime  "
            + "                                          obs_datetime  "
            + "                                   FROM   patient pa  "
            + "                                          inner join encounter enc  "
            + "                                                  ON enc.patient_id =  "
            + "                                                     pa.patient_id  "
            + "                                          inner join obs obs  "
            + "                                                  ON obs.encounter_id =  "
            + "                                                     enc.encounter_id  "
            + "                                   WHERE  pa.voided = 0  "
            + "                                          AND enc.voided = 0  "
            + "                                          AND obs.voided = 0  "
            + "                                          AND obs.concept_id = %s  "
            + "                                          AND obs.value_datetime IS NOT NULL  "
            + "                                          AND enc.encounter_type IN ( %s, %s )  "
            + "                                          AND enc.location_id =  :location  "
            + "                                          AND enc.encounter_datetime <  "
            + "                                               :onOrBefore  "
            + "                                   GROUP  BY pa.patient_id) recent  "
            + "                               ON p.patient_id = recent.patient_id  "
            + "                                  AND p.voided = 0  "
            + "                GROUP  BY recent.patient_id  "
            + "                UNION  "
            + "                SELECT p.patient_id,  "
            + "                         Max(recent.obs_datetime)  as obs_datetime  "
            + "                FROM   patient p  "
            + "                       inner join (SELECT enc.patient_id,  "
            + "                                          Max(enc.encounter_datetime)  "
            + "                                                           encounter_datetime,  "
            + "                                          Date_add(obs.value_datetime, interval  "
            + "                                          30 day)  "
            + "                                          obs_datetime  "
            + "                                   FROM   patient pa  "
            + "                                          inner join encounter enc  "
            + "                                                  ON enc.patient_id =  "
            + "                                                     pa.patient_id  "
            + "                                          inner join obs obs  "
            + "                                                  ON obs.encounter_id =  "
            + "                                                     enc.encounter_id  "
            + "                                   WHERE  pa.voided = 0  "
            + "                                          AND enc.voided = 0  "
            + "                                          AND obs.voided = 0  "
            + "                                          AND obs.concept_id = %s  "
            + "                                          AND obs.value_datetime IS NOT NULL  "
            + "                                          AND enc.encounter_type = %s  "
            + "                                          AND enc.location_id =  :location  "
            + "                                          AND enc.encounter_datetime <  "
            + "                                               :onOrBefore  "
            + "                                   GROUP  BY pa.patient_id) recent  "
            + "                               ON p.patient_id = recent.patient_id  "
            + "                                  AND p.voided = 0  "
            + "                GROUP  BY recent.patient_id) most_recent   "
            + "        GROUP  BY most_recent.patient_id) final  "
            + "WHERE     final.final_obs_date <  :onOrBefore;  "
            + "";

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
      int returnVisitDateConcept,
      int returnVisitDateForArvDrugConcept) {

    String query =
        " SELECT ps.patient_id  " + 
        " FROM   (SELECT p.patient_id  " + 
        "        FROM   patient p  " + 
        "        WHERE  p.voided = 0  " + 
        "                AND p.patient_id NOT IN (SELECT patient_id  " + 
        "                                        FROM   encounter  " + 
        "                                        WHERE  encounter_type IN  " + 
        "                                               ( %s, %s, %s, %s )  " + 
        "                                               AND location_id = :location  " + 
        "                                               AND voided = 0)  " + 
        "        UNION  " + 
        "        SELECT recent.patient_id  " + 
        "        FROM   ( " + 
        "                select patient_id from patient " + 
        "where patient_id NOT in ( " + 
        "                Select  q1.patient_id from " + 
        "                (SELECT p.patient_id,  " + 
        "                       Max(e.encounter_datetime) " + 
        "                FROM   patient p  " + 
        "                       INNER JOIN encounter e  " + 
        "                               ON e.patient_id = p.patient_id  " + 
        "                       INNER JOIN obs o  " + 
        "                               ON o.encounter_id = e.encounter_id  " + 
        "                WHERE  p.voided = 0  " + 
        "                       AND e.voided = 0 " + 
        "                       AND o.voided = 0  " + 
        "                       AND (e.encounter_type IN (%s,%s) " + 
        "                       AND o.concept_id = %s ) " + 
        "                        AND e.location_id = :location " + 
        "                GROUP  BY p.patient_id " + 
        "                ) q1 " + 
        ") " + 
        "AND patient_id NOT in " + 
        "( " + 
        "                Select  q2.patient_id from " + 
        "                ( " + 
        "                SELECT p.patient_id as patient_id,  " + 
        "                       Max(e.encounter_datetime) " + 
        "                FROM   patient p  " + 
        "                       INNER JOIN encounter e  " + 
        "                               ON e.patient_id = p.patient_id  " + 
        "                       INNER JOIN obs o  " + 
        "                               ON o.encounter_id = e.encounter_id  " + 
        "                WHERE  p.voided = 0  " + 
        "                       AND e.voided = 0 " + 
        "                       AND o.voided = 0  " + 
        "                       AND (e.encounter_type IN (%s) " + 
        "                       AND o.concept_id =%s ) " + 
        "                       AND e.location_id = :location  " + 
        "                GROUP  BY p.patient_id) q2 " + 
        ") " + 
        " " + 
        ")recent )ps " + 
        "                        " + 
        "GROUP  BY ps.patient_id; ";

    return String.format(
        query,
        adultoSeguimentoEncounterType,
        ARVPediatriaSeguimentoEncounterType,
        aRVPharmaciaEncounterType,
        masterCardDrugPickupEncounterType,
        adultoSeguimentoEncounterType,
        ARVPediatriaSeguimentoEncounterType,
        returnVisitDateConcept,
        aRVPharmaciaEncounterType,
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
            + " SELECT named.patient_id, named.common_date FROM ( "
            + " SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime)  as  common_date "
            + " FROM patient p"
            + " INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + " WHERE p.voided=0 AND e.voided=0 AND e.encounter_type IN("
            + buscaActivaEncounterType
            + ","
            + visitaApoioReintegracaoParteA
            + ","
            + visitaApoioReintegracaoParteB
            + ") AND e.encounter_datetime is not null AND e.encounter_datetime <= :onOrBefore AND "
            + " e.encounter_id IN ( "
            + "      select ee.encounter_id from "
            + "      encounter ee INNER JOIN obs o ON o.encounter_id=ee.encounter_id AND o.voided=0 AND "
            + "      ee.encounter_type IN("
            + buscaActivaEncounterType
            + ","
            + visitaApoioReintegracaoParteA
            + ","
            + visitaApoioReintegracaoParteB
            + ") AND "
            + "      o.concept_id="
            + patientFoundConcept
            + " AND o.value_coded="
            + noConcept
            + ") AND "
            + " e.encounter_id IN ( "
            + "      select eee.encounter_id from "
            + "      encounter eee INNER JOIN obs oo ON oo.encounter_id=eee.encounter_id AND oo.voided=0 AND "
            + "      eee.encounter_type IN("
            + buscaActivaEncounterType
            + ","
            + visitaApoioReintegracaoParteA
            + ","
            + visitaApoioReintegracaoParteB
            + ") AND "
            + "oo.concept_id="
            + reasonPatientNotFound
            + " AND oo.value_coded="
            + patientIsDead
            + ") and e.location_id = :location  group by p.patient_id) named "
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
