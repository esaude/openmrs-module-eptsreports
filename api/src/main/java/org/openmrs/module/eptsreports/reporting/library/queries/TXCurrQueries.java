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
        "SELECT p.person_id patient_id "
            + "   FROM person p "
            + "    WHERE p.dead=1 "
            + "     AND p.death_date <= :onOrBefore "
            + "     AND p.voided=0";

    return query;
  }

  public static String getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate(
      String encounterTypes,
      int patientFoundConcept,
      int reasonPatientNotFound,
      int noConcept,
      int patientIsDead) {
    String query =
        " SELECT named.patient_id FROM ( SELECT     p.patient_id, "
            + "            max(obsobito.obs_datetime) common_date "
            + "    FROM       patient p "
            + "    INNER JOIN encounter e "
            + "    ON         p.patient_id=e.patient_id  "
            + "    INNER JOIN obs obsencontrado "
            + "    ON         e.encounter_id=obsencontrado.encounter_id "
            + "    INNER JOIN obs obsobito "
            + "    ON         e.encounter_id=obsobito.encounter_id "
            + "    WHERE      e.voided=0 "
            + "    AND        obsencontrado.voided=0 "
            + "    AND        p.voided=0 "
            + "    AND        obsobito.voided=0 "
            + "    AND        e.encounter_type IN (%s)  "
            + "    AND        e.encounter_datetime<= :onOrBefore "
            + "    AND        e.location_id= :location "
            + "    AND        obsencontrado.concept_id = %s "
            + "    AND        obsencontrado.value_coded=%s "
            + "    AND        obsobito.concept_id=%s "
            + "    AND        obsobito.value_coded=%s "
            + "    GROUP BY   p.patient_id) named ";

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
      int stateOfStayOfArtPatient,
      int patientHasDiedConcept) {

    String query =
        "select  p.patient_id "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + "and o.concept_id in (%s,%s) and   o.value_coded=%s "
            + "and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + "group by p.patient_id";

    return String.format(
        query,
        adultoSeguimentoEncounterType,
        masterCardEncounterType,
        stateOfStayPriorArtPatientConcept,
        stateOfStayOfArtPatient,
        patientHasDiedConcept);
  }

  public static String getTransferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayPriorArtPatientConcept,
      int stateOfStayOfArtPatient,
      int transferredOutConcept) {

    String query =
        "select  p.patient_id "
            + " from patient p "
            + " inner join encounter e on e.patient_id=p.patient_id "
            + " inner join obs o on o.encounter_id=e.encounter_id "
            + " where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + " and o.concept_id in (%s,%s) and   o.value_coded=%s "
            + " and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + " group by p.patient_id ";

    return String.format(
        query,
        adultoSeguimentoEncounterType,
        masterCardEncounterType,
        stateOfStayPriorArtPatientConcept,
        stateOfStayOfArtPatient,
        transferredOutConcept);
  }

  public static String getPatientSuspendedInFichaResumeAndClinicaOfMasterCardByReportEndDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayPriorArtPatientConcept,
      int stateOfStayOfArtPatient,
      int suspendedTreatmentConcept) {

    String query =
        "select  p.patient_id "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + "and o.concept_id in (%s,%s) and o.value_coded=%s "
            + " and e.location_id = :location and e.encounter_datetime <= :onOrBefore "
            + "group by p.patient_id ";

    return String.format(
        query,
        adultoSeguimentoEncounterType,
        masterCardEncounterType,
        stateOfStayPriorArtPatientConcept,
        stateOfStayOfArtPatient,
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
        "SELECT final.patient_id  "
            + " from(  "
            + "     SELECT  "
            + "         most_recent.patient_id, Date_add(Max(most_recent.value_datetime), interval 28 day) final_encounter_date  "
            + "     FROM   (SELECT fila.patient_id, o.value_datetime from ( "
            + "                 SELECT enc.patient_id,  "
            + "                     Max(enc.encounter_datetime)  encounter_datetime "
            + "                 FROM   patient pa  "
            + "                     inner join encounter enc  "
            + "                         ON enc.patient_id =  pa.patient_id  "
            + "                     inner join obs obs  "
            + "                         ON obs.encounter_id = enc.encounter_id  "
            + "                 WHERE  pa.voided = 0  "
            + "                     AND enc.voided = 0  "
            + "                     AND obs.voided = 0  "
            + "                     AND obs.concept_id =  %s  "
            + "                     AND obs.value_datetime IS NOT NULL  "
            + "                     AND enc.encounter_type = %s  "
            + "                     AND enc.location_id = :location  "
            + "                     AND enc.encounter_datetime <= :onOrBefore  "
            + "                 GROUP  BY pa.patient_id) fila  "
            + "             INNER JOIN encounter e on "
            + "                 e.patient_id = fila.patient_id and "
            + "                 e.encounter_datetime = fila.encounter_datetime and "
            + "                 e.encounter_type = %s and "
            + "                 e.location_id = :location and "
            + "                 e.voided = 0 "
            + "             INNER JOIN obs o on "
            + "                 o.encounter_id = e.encounter_id and "
            + "                 o.concept_id = %s and "
            + "                 o.voided = 0 "
            + "             UNION  "
            + "             SELECT ficha.patient_id, o.value_datetime FROM ("
            + "                 SELECT enc.patient_id,  "
            + "                     Max(enc.encounter_datetime) encounter_datetime "
            + "                 FROM   patient pa  "
            + "                     inner join encounter enc  "
            + "                         ON enc.patient_id = pa.patient_id  "
            + "                     inner join obs obs  "
            + "                         ON obs.encounter_id = enc.encounter_id  "
            + "                 WHERE  pa.voided = 0  "
            + "                     AND enc.voided = 0  "
            + "                     AND obs.voided = 0  "
            + "                     AND obs.concept_id = %s "
            + "                     AND obs.value_datetime IS NOT NULL  "
            + "                     AND enc.encounter_type IN ( %s,%s )  "
            + "                     AND enc.location_id = :location  "
            + "                     AND enc.encounter_datetime <= :onOrBefore  "
            + "                 GROUP  BY pa.patient_id) ficha "
            + "             INNER JOIN encounter e on "
            + "                 e.patient_id = ficha.patient_id and "
            + "                 e.encounter_datetime = ficha.encounter_datetime and "
            + "                 e.encounter_type IN (%s,%s) and "
            + "                 e.location_id = :location and "
            + "                 e.voided = 0 "
            + "             INNER JOIN obs o on "
            + "                 o.encounter_id = e.encounter_id and "
            + "                 o.concept_id = %s and "
            + "                 o.voided = 0 "
            + "             UNION  "
            + "             SELECT enc.patient_id,  "
            + "                 Date_add(Max(obs.value_datetime), interval 30 day) value_datetime "
            + "             FROM   patient pa  "
            + "                 inner join encounter enc  "
            + "                     ON enc.patient_id = pa.patient_id  "
            + "                 inner join obs obs  "
            + "                     ON obs.encounter_id = enc.encounter_id  "
            + "             WHERE  pa.voided = 0  "
            + "                 AND enc.voided = 0  "
            + "                 AND obs.voided = 0  "
            + "                 AND obs.concept_id = %s  "
            + "                 AND obs.value_datetime IS NOT NULL  "
            + "                 AND enc.encounter_type = %s  "
            + "                 AND enc.location_id = :location  "
            + "                 AND enc.encounter_datetime <= :onOrBefore  "
            + "            GROUP  BY pa.patient_id  "
            + "        ) most_recent  "
            + "    GROUP BY most_recent.patient_id  "
            + "    HAVING final_encounter_date < :onOrBefore  "
            + " ) final  "
            + " GROUP BY final.patient_id;";

    return String.format(
        query,
        returnVisitDateForArvDrugConcept,
        ARVPharmaciaEncounterType,
        ARVPharmaciaEncounterType,
        returnVisitDateForArvDrugConcept,
        returnVisitDateConcept,
        adultoSeguimentoEncounterType,
        aRVPediatriaSeguimentoEncounterType,
        adultoSeguimentoEncounterType,
        aRVPediatriaSeguimentoEncounterType,
        returnVisitDateConcept,
        artDatePickup,
        msterCardDrugPickupEncounterType,
        returnVisitDateConcept,
        artDatePickup,
        returnVisitDateForArvDrugConcept);
  }

  public static String getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup(
      int adultoSeguimentoEncounterType,
      int ARVPediatriaSeguimentoEncounterType,
      int aRVPharmaciaEncounterType,
      int masterCardDrugPickupEncounterType,
      int returnVisitDateConcept,
      int returnVisitDateForArvDrugConcept) {
    String query =
        " SELECT ps.patient_id "
            + "   FROM (        "
            + "       SELECT p.patient_id "
            + "       FROM patient p "
            + "       WHERE  p.voided = 0 "
            + "           AND p.patient_id NOT IN "
            + "               (SELECT patient_id "
            + "                   FROM encounter "
            + "                   WHERE  encounter_type IN (%s,%s,%s,%s ) "
            + "						  AND encounter_datetime <= :onOrBefore"
            + "                       AND location_id = :location "
            + "                       AND voided = 0) "
            + "       UNION "
            + "       Select ficha.patient_id "
            + "       from ( "
            + "           SELECT q1.patient_id "
            + "           from "
            + "               ( "
            + "               SELECT p.patient_id, "
            + "                   Max(e.encounter_datetime) as max_enc_datetime, Max(e.encounter_id) AS encounter_id "
            + "               FROM patient p "
            + "                   INNER JOIN encounter e "
            + "                   ON e.patient_id = p.patient_id "
            + "                   INNER JOIN obs o "
            + "                   ON o.encounter_id = e.encounter_id "
            + "               WHERE  p.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND o.voided = 0 "
            + "                   AND e.encounter_type IN (%s,%s) "
            + "                   and e.encounter_datetime <= :onOrBefore "
            + "                   AND e.location_id = :location "
            + "               GROUP  BY p.patient_id ) q1 "
            + "               left join obs o2 on o2.encounter_id=q1.encounter_id and "
            + "                   o2.concept_id = %s and o2.voided=0 "
            + "               where  o2.obs_id  is null) ficha "
            + "           INNER JOIN ( "
            + "               SELECT q2.patient_id "
            + "               from ( "
            + "                   SELECT p.patient_id, "
            + "                       Max(e.encounter_datetime) as max_enc_datetime, Max(e.encounter_id) AS encounter_id "
            + "                   FROM patient p "
            + "                       INNER JOIN encounter e "
            + "                       ON e.patient_id = p.patient_id "
            + "                       INNER JOIN obs o "
            + "                       ON o.encounter_id = e.encounter_id "
            + "                   WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.encounter_type IN (%s) "
            + "                       and e.encounter_datetime <= :onOrBefore "
            + "                       AND e.location_id = :location "
            + "                   GROUP  BY p.patient_id  "
            + "               )q2 "
            + "               left join obs o1 on o1.encounter_id=q2.encounter_id and "
            + "                       o1.concept_id = %s and o1.voided=0 "
            + "               where  o1.obs_id is null "
            + "           ) fila ON ficha.patient_id=fila.patient_id "
            + "       )ps "
            + "       GROUP  BY ps.patient_id";

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
        "select most_recent.patient_id from  "
            + "   (SELECT     pg.patient_id, "
            + "            max(ps.start_date) common_date "
            + "    FROM       patient p "
            + "    INNER JOIN patient_program pg "
            + "    ON         p.patient_id=pg.patient_id "
            + "    INNER JOIN patient_state ps "
            + "    ON         pg.patient_program_id=ps.patient_program_id "
            + "    WHERE      pg.voided=0 "
            + "    AND        ps.voided=0 "
            + "    AND        p.voided=0 "
            + "    AND        pg.program_id= %s"
            + "    AND        ps.state IN (%s,%s,%s) "
            + "    AND        ps.end_date IS NULL "
            + "    AND        ps.start_date<= :onOrBefore "
            + "    AND        pg.location_id= :location "
            + "    GROUP BY   pg.patient_id "
            + "   UNION  "
            + "   SELECT p.person_id patient_id, p.death_date common_date "
            + "   FROM person p "
            + "    WHERE p.dead=1 "
            + "     AND p.death_date <=  :onOrBefore "
            + "     AND p.voided=0 "
            + "    UNION  "
            + "    "
            + "   SELECT     p.patient_id, "
            + "            max(obsobito.obs_datetime) common_date "
            + "    FROM       patient p "
            + "    INNER JOIN encounter e "
            + "    ON         p.patient_id=e.patient_id  "
            + "    INNER JOIN obs obsencontrado "
            + "    ON         e.encounter_id=obsencontrado.encounter_id "
            + "    INNER JOIN obs obsobito "
            + "    ON         e.encounter_id=obsobito.encounter_id "
            + "    WHERE      e.voided=0 "
            + "    AND        obsencontrado.voided=0 "
            + "    AND        p.voided=0 "
            + "    AND        obsobito.voided=0 "
            + "    AND        e.encounter_type IN (%s,%s,%s)  "
            + "    AND        e.encounter_datetime<= :onOrBefore "
            + "    AND        e.location_id= :location "
            + "    AND        obsencontrado.concept_id = %s "
            + "    AND        obsencontrado.value_coded=%s "
            + "    AND        obsobito.concept_id=%s "
            + "    AND        obsobito.value_coded=%s "
            + "    GROUP BY   p.patient_id "
            + "   UNION  "
            + "   SELECT p.patient_id, "
            + "                max(e.encounter_datetime) common_date "
            + "   FROM patient p "
            + "   INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + "   INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "   WHERE e.encounter_type IN (%s,%s) "
            + "     AND p.voided=0 "
            + "     AND e.voided=0 "
            + "     AND o.voided=0 "
            + "     AND o.concept_id= %s "
            + "     AND o.value_coded=%s"
            + "     AND e.location_id =  :location "
            + "     AND e.encounter_datetime <=  :onOrBefore "
            + "   GROUP BY p.patient_id "
            + "   UNION  "
            + "   SELECT p.patient_id, "
            + "                max(e.encounter_datetime) common_date "
            + "   FROM patient p "
            + "   INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + "   INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "   WHERE e.encounter_type IN (%s, %s) "
            + "     AND p.voided=0 "
            + "     AND e.voided=0 "
            + "     AND o.voided=0 "
            + "     AND o.concept_id=%s "
            + "     AND o.value_coded=%s "
            + "     AND e.location_id =  :location "
            + "     AND e.encounter_datetime <=  :onOrBefore "
            + "   GROUP BY p.patient_id "
            + "   UNION  "
            + "   SELECT p.patient_id, "
            + "                max(e.encounter_datetime) common_date "
            + "   FROM patient p "
            + "   INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + "   INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "   WHERE e.encounter_type IN (%s,%s) "
            + "     AND p.voided=0 "
            + "     AND e.voided=0 "
            + "     AND o.voided=0 "
            + "     AND o.concept_id= %s "
            + "     AND o.value_coded=%s "
            + "     AND e.location_id =  :location "
            + "     AND e.encounter_datetime <=  :onOrBefore "
            + "   GROUP BY p.patient_id)most_recent "
            + "        INNER JOIN encounter e ON e.patient_id = most_recent.patient_id "
            + "        INNER JOIN obs obss ON obss.encounter_id=e.encounter_id "
            + "        WHERE e.voided=0 "
            + "            AND obss.voided=0 "
            + "            AND (e.encounter_type IN (%s,%s,%s) and  e.encounter_datetime >  most_recent.common_date ) or "
            + "            ( e.encounter_type = %s "
            + "                AND obss.concept_id= %s and  obss.value_datetime > most_recent.common_date)   "
            + "            and e.location_id =  :location "
            + "    GROUP  by most_recent.patient_id;";

    return String.format(
        query,
        artProgram,
        transferredOutToAnotherHealthFacilityWorkflowState,
        getSuspendedTreatmentWorkflowState,
        getArtDeadWorkflowState,
        buscaActivaEncounterType,
        visitaApoioReintegracaoParteA,
        visitaApoioReintegracaoParteB,
        patientFoundConcept,
        noConcept,
        reasonPatientNotFound,
        patientIsDead,
        adultoSeguimento,
        masterCardEncounterType,
        stateOfStayOfPreArtPatient,
        patientHasDiedConcept,
        adultoSeguimento,
        masterCardEncounterType,
        stateOfStayOfPreArtPatient,
        transferredOutConcept,
        adultoSeguimento,
        masterCardEncounterType,
        stateOfStayOfPreArtPatient,
        suspendedTreatmentConcept,
        adultoSeguimento,
        aRVPediatriaSeguimento,
        aRVPharmacia,
        masterCardDrugPickup,
        artDatePickup);
  }

  /**
   * All patients marked in last “Paragen Unica (PU)” as Iniciar (I) or Continua (C) on Ficha
   * Clinica – Master Card
   *
   * @return @String
   */
  public static String getAllPatientsMarkedInLastPuAsIOrConFichaClinicaMasterCard(
      int encounterType,
      int lastOnsStopConcept,
      int startDrugsConcept,
      int continueRegimenConcept) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o "
            + " ON e.encounter_id=o.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=%d "
            + " AND o.concept_id=%d AND o.value_coded IN(%d, %d) AND e.location_id=:location AND e.encounter_datetime BETWEEN :startDate AND :endDate";
    return String.format(
        query, encounterType, lastOnsStopConcept, startDrugsConcept, continueRegimenConcept);
  }
}
