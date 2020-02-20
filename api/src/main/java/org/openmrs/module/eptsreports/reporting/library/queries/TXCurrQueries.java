package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

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
      int buscaActivaEncounterType,
      int visitaApoioReintegracaoParteAEncounterType,
      int visitaApoioReintegracaoParteBEncounterType,
      int reasonPatientNotFound,
      int reasonPatientNotFoundByActivist2ndVisit,
      int reasonPatientNotFoundByActivist3rdVisit,
      int patientIsDead) {
    String query =
        "  SELECT  max_date.patient_id FROM  "
            + "    (SELECT  "
            + "      p.patient_id,  "
            + "      MAX(e.encounter_datetime) last   "
            + "    FROM patient p "
            + "      INNER  JOIN encounter e ON e.patient_id=p.patient_id "
            + "     WHERE  "
            + "      e.encounter_datetime <= :onOrBefore "
            + "      AND e.location_id = :location "
            + "      AND e.encounter_type  in( ${buscaActiva},${visitaApoioReintegracaoParteA},${visitaApoioReintegracaoParteB})  "
            + "      AND e.voided=0 "
            + "      AND p.voided = 0 "
            + "    GROUP BY  p.patient_id  ) max_date "
            + "    INNER  JOIN encounter ee "
            + "            ON ee.patient_id = max_date.patient_id "
            + "    INNER  JOIN obs o ON ee.encounter_id = o.encounter_id  "
            + "        WHERE  "
            + "        ( "
            + "            (o.concept_id = ${reasonPatientNotFound} AND o.value_coded = ${patientIsDead}) OR "
            + "            (o.concept_id = ${reasonPatientNotFoundByActivist2ndVisit} AND o.value_coded = ${patientIsDead}) OR "
            + "            (o.concept_id = ${reasonPatientNotFoundByActivist3rdVisit} AND o.value_coded = ${patientIsDead} ) "
            + "        )  "
            + "    AND o.voided=0 "
            + "    AND ee.voided = 0 "
            + "    GROUP BY  max_date.patient_id";

    Map<String, Integer> map = new HashMap<>();
    map.put("buscaActiva", buscaActivaEncounterType);
    map.put("visitaApoioReintegracaoParteA", visitaApoioReintegracaoParteAEncounterType);
    map.put("visitaApoioReintegracaoParteB", visitaApoioReintegracaoParteBEncounterType);
    map.put("reasonPatientNotFound", reasonPatientNotFound);
    map.put("reasonPatientNotFoundByActivist2ndVisit", reasonPatientNotFoundByActivist2ndVisit);
    map.put("reasonPatientNotFoundByActivist3rdVisit", reasonPatientNotFoundByActivist3rdVisit);
    map.put("patientIsDead", patientIsDead);

    StringSubstitutor sub = new StringSubstitutor(map);
    return sub.replace(query);
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
      int stateOfStayOfArtPatientConcept,
      int masterCardEncounterType,
      int stateOfStayPriorArtPatientConcept,
      int transferredOutConcept) {

    String query =
        " SELECT  p.patient_id "
            + " FROM patient p "
            + " 	INNER JOIN encounter e "
            + "			ON e.patient_id=p.patient_id "
            + " 	INNER JOIN obs o "
            + "			ON o.encounter_id=e.encounter_id "
            + " WHERE e.encounter_type = ${adultoSeguimento} "
            + "		AND p.voided=0  "
            + "		AND e.voided=0 "
            + "		AND o.voided=0 "
            + " 	AND o.concept_id = ${stateOfStayOfArtPatient} "
            + "		AND o.value_coded= ${transferredOut} "
            + " 	AND e.location_id = :location "
            + "		AND e.encounter_datetime <= :onOrBefore "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT  p.patient_id "
            + " FROM patient p  "
            + " 	INNER JOIN encounter e "
            + "			ON e.patient_id=p.patient_id "
            + " 	INNER JOIN obs o "
            + "			ON o.encounter_id=e.encounter_id "
            + " WHERE e.encounter_type = ${masterCard}"
            + "		AND p.voided=0 "
            + "		AND e.voided=0 "
            + "		AND o.voided=0 "
            + " 	AND o.concept_id = ${stateOfStayPriorArtPatient} "
            + "		AND o.value_coded= ${transferredOut}"
            + " 	AND e.location_id = :location "
            + "		AND o.value_datetime <= :onOrBefore "
            + " GROUP BY p.patient_id ";

    Map<String, Integer> map = new HashMap<>();
    map.put("adultoSeguimento", adultoSeguimentoEncounterType);
    map.put("stateOfStayOfArtPatient", stateOfStayOfArtPatientConcept);
    map.put("masterCard", masterCardEncounterType);
    map.put("stateOfStayPriorArtPatient", stateOfStayPriorArtPatientConcept);
    map.put("transferredOut", transferredOutConcept);

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query);
  }

  public static String getPatientSuspendedInFichaResumeAndClinicaOfMasterCardByReportEndDate(
      int adultoSeguimentoEncounterType,
      int stateOfStayOfArtPatientConcept,
      int masterCardEncounterType,
      int stateOfStayPriorArtPatientConcept,
      int suspendedTreatmentConcept) {

    String query =
        " SELECT  p.patient_id "
            + " FROM patient p "
            + " 	INNER JOIN encounter e "
            + "			ON e.patient_id=p.patient_id "
            + " 	INNER JOIN obs o "
            + "			ON o.encounter_id=e.encounter_id "
            + " WHERE e.encounter_type = ${adultoSeguimento} "
            + "		AND p.voided=0  "
            + "		AND e.voided=0 "
            + "		AND o.voided=0 "
            + " 	AND o.concept_id = ${stateOfStayOfArtPatient} "
            + "		AND o.value_coded= ${suspendedTreatment} "
            + " 	AND e.location_id = :location "
            + "		AND e.encounter_datetime <= :onOrBefore "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT  p.patient_id "
            + " FROM patient p  "
            + " 	INNER JOIN encounter e "
            + "			ON e.patient_id=p.patient_id "
            + " 	INNER JOIN obs o "
            + "			ON o.encounter_id=e.encounter_id "
            + " WHERE e.encounter_type = ${masterCard}"
            + "		AND p.voided=0 "
            + "		AND e.voided=0 "
            + "		AND o.voided=0 "
            + " 	AND o.concept_id = ${stateOfStayPriorArtPatient} "
            + "		AND o.value_coded= ${suspendedTreatment}"
            + " 	AND e.location_id = :location "
            + "		AND o.value_datetime <= :onOrBefore "
            + " GROUP BY p.patient_id ";

    Map<String, Integer> map = new HashMap<>();
    map.put("adultoSeguimento", adultoSeguimentoEncounterType);
    map.put("stateOfStayOfArtPatient", stateOfStayOfArtPatientConcept);
    map.put("masterCard", masterCardEncounterType);
    map.put("stateOfStayPriorArtPatient", stateOfStayPriorArtPatientConcept);
    map.put("suspendedTreatment", suspendedTreatmentConcept);

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query);
  }

  public static String getPatientHavingLastScheduledDrugPickupDate(
      int returnVisitDateForArvDrugConcept,
      int ARVPharmaciaEncounterType,
      int returnVisitDateConcept,
      int adultoSeguimentoEncounterType,
      int aRVPediatriaSeguimentoEncounterType,
      int artDatePickup,
      int msterCardDrugPickupEncounterType,
      int numDays) {
    String query =
        "SELECT final.patient_id  "
            + " from(  "
            + "     SELECT  "
            + "         most_recent.patient_id, Date_add(Max(most_recent.value_datetime), interval %d day) final_encounter_date  "
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
            + "                 AND obs.value_datetime <= :onOrBefore  "
            + "            GROUP  BY pa.patient_id  "
            + "        ) most_recent  "
            + "    GROUP BY most_recent.patient_id  "
            + "    HAVING final_encounter_date < :onOrBefore  "
            + " ) final  "
            + " GROUP BY final.patient_id;";

    return String.format(
        query,
        numDays,
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
        returnVisitDateConcept);
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
      int reasonPatientNotFound,
      int reasonPatientNotFoundByActivist2ndVisit,
      int reasonPatientNotFoundByActivist3rdVisit,
      int patientHasDiedConcept,
      int stateOfStayOfPreArtPatient,
      int transferredOutConcept,
      int suspendedTreatmentConcept,
      int artProgram,
      int defaultingMotiveConcept,
      int autoTransferConcept,
      int stateOfStayOfArtPatient){

    Map<String, Integer> map = new HashMap<>();
    map.put("adultoSeguimento", adultoSeguimento);
    map.put("aRVPediatriaSeguimento", aRVPediatriaSeguimento);
    map.put("aRVPharmacia", aRVPharmacia);
    map.put("masterCardDrugPickup", masterCardDrugPickup);
    map.put("artDatePickup", artDatePickup);
    map.put("masterCardEncounterType", masterCardEncounterType);
    map.put(
        "transferredOutToAnotherHealthFacilityWorkflowState",
        transferredOutToAnotherHealthFacilityWorkflowState);
    map.put("getSuspendedTreatmentWorkflowState", getSuspendedTreatmentWorkflowState);
    map.put("getArtDeadWorkflowState", getArtDeadWorkflowState);
    map.put("buscaActivaEncounterType", buscaActivaEncounterType);
    map.put("visitaApoioReintegracaoParteA", visitaApoioReintegracaoParteA);
    map.put("visitaApoioReintegracaoParteB", visitaApoioReintegracaoParteB);
    map.put("reasonPatientNotFound", reasonPatientNotFound);//2031
    map.put("reasonPatientNotFoundByActivist2ndVisit", reasonPatientNotFoundByActivist2ndVisit);
    map.put("reasonPatientNotFoundByActivist3rdVisit", reasonPatientNotFoundByActivist3rdVisit);
    map.put("stateOfStayOfPreArtPatient", stateOfStayOfPreArtPatient);
    map.put("patientHasDiedConcept", patientHasDiedConcept);
    map.put("transferredOutConcept", transferredOutConcept);
    map.put("suspendedTreatmentConcept", suspendedTreatmentConcept);
    map.put("artProgram", artProgram);//2
    map.put("defaultingMotiveConcept", defaultingMotiveConcept);
    map.put("autoTransferConcept", autoTransferConcept);
    map.put("stateOfStayOfArtPatient", stateOfStayOfArtPatient);

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
            + "    AND        pg.program_id= ${artProgram}"
            + "    AND        ps.state IN (${transferredOutToAnotherHealthFacilityWorkflowState},${getSuspendedTreatmentWorkflowState},${getArtDeadWorkflowState}) "
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
            + "  SELECT  max_date.patient_id, max_date.last FROM  "
            + "    (SELECT  "
            + "      p.patient_id,  "
            + "      MAX(e.encounter_datetime) last   "
            + "    FROM patient p "
            + "      INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + "     WHERE  "
            + "      e.encounter_datetime <= :onOrBefore "
            + "      AND e.location_id = :location "
            + "      AND e.encounter_type  in( ${buscaActivaEncounterType},${visitaApoioReintegracaoParteA},${visitaApoioReintegracaoParteB})  "
            + "      AND e.voided=0 "
            + "      AND p.voided = 0 "
            + "     GROUP BY p.patient_id  ) max_date "
            + "    INNER JOIN encounter ee "
            + "            ON ee.patient_id = max_date.patient_id "
            + "   INNER JOIN obs o ON ee.encounter_id = o.encounter_id  "
            + "        WHERE  "
            + "        ( "
            + "            (o.concept_id = ${reasonPatientNotFound} AND o.value_coded =${patientHasDiedConcept}) OR "
            + "            (o.concept_id = ${reasonPatientNotFoundByActivist2ndVisit} AND o.value_coded = ${patientHasDiedConcept}) OR "
            + "            (o.concept_id = ${reasonPatientNotFoundByActivist3rdVisit} AND o.value_coded = ${patientHasDiedConcept}) "
            + "        )  "
            + "    AND o.voided=0 "
            + "    AND ee.voided = 0"
            + "    GROUP BY  max_date.patient_id"
            + "   UNION  "
            + "   SELECT p.patient_id, "
            + "                max(e.encounter_datetime) common_date "
            + "   FROM patient p "
            + "   INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + "   INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "   WHERE e.encounter_type IN (${adultoSeguimento},${masterCardEncounterType}) "
            + "     AND p.voided=0 "
            + "     AND e.voided=0 "
            + "     AND o.voided=0 "
            + "     AND o.concept_id= ${stateOfStayOfPreArtPatient} "
            + "     AND o.value_coded=${patientHasDiedConcept}"
            + "     AND e.location_id =  :location "
            + "     AND e.encounter_datetime <=  :onOrBefore "
            + "   GROUP BY p.patient_id "
            + "   UNION  "
            + " SELECT  last10.patient_id, max(last10.encounterdatetime) FROM ( "
            + " SELECT  p.patient_id, e.encounter_datetime  AS encounterdatetime "
            + "            FROM patient p  "
            + "              INNER JOIN encounter e  "
            + "                          ON e.patient_id=p.patient_id  "
            + "              INNER JOIN obs o  "
            + "                          ON o.encounter_id=e.encounter_id  "
            + "            WHERE e.encounter_type = ${adultoSeguimento}  "
            + "                    AND p.voided=0   "
            + "                    AND e.voided=0  "
            + "                    AND o.voided=0  "
            + "              AND o.concept_id = ${stateOfStayOfArtPatient}  "
            + "                    AND o.value_coded= ${transferredOutConcept}  "
            + "              AND e.location_id = :location  "
            + "                    AND e.encounter_datetime <= :onOrBefore  "
            + "            GROUP BY p.patient_id  "
            + "            UNION  "
            + "            SELECT  p.patient_id, e.encounter_datetime  AS encounterdatetime "
            + "            FROM patient p    "
            + "              INNER JOIN encounter e  "
            + "                          ON e.patient_id=p.patient_id    "
            + "              INNER JOIN obs o  "
            + "                          ON o.encounter_id=e.encounter_id   "
            + "            WHERE e.encounter_type = ${masterCardEncounterType} "
            + "                    AND p.voided=0  "
            + "                    AND e.voided=0  "
            + "                    AND o.voided=0    "
            + "              AND o.concept_id = ${stateOfStayOfPreArtPatient}  "
            + "                    AND o.value_coded= ${transferredOutConcept}  "
            + "              AND e.location_id = :location  "
            + "                    AND o.value_datetime <= :onOrBefore    "
            + "            GROUP BY p.patient_id ) last10 "
            + " GROUP BY last10.patient_id  "
            + "   UNION  "
            + " SELECT  last11.patient_id, max(last11.encounterdatetime) FROM ( "
            + " SELECT  p.patient_id , e.encounter_datetime  AS encounterdatetime "
            + "                         FROM patient p  "
            + "                           INNER JOIN encounter e  "
            + "                                       ON e.patient_id=p.patient_id  "
            + "                           INNER JOIN obs o  "
            + "                                       ON o.encounter_id=e.encounter_id  "
            + "                         WHERE e.encounter_type = ${adultoSeguimento}  "
            + "                                 AND p.voided=0   "
            + "                                 AND e.voided=0  "
            + "                                 AND o.voided=0  "
            + "                           AND o.concept_id = ${stateOfStayOfArtPatient}  "
            + "                                 AND o.value_coded= ${suspendedTreatmentConcept}  "
            + "                           AND e.location_id = :location  "
            + "                                 AND e.encounter_datetime <= :onOrBefore  "
            + "                         GROUP BY p.patient_id  "
            + "                         UNION  "
            + "                         SELECT  p.patient_id , e.encounter_datetime  AS encounterdatetime "
            + "                         FROM patient p    "
            + "                           INNER JOIN encounter e  "
            + "                                       ON e.patient_id=p.patient_id    "
            + "                           INNER JOIN obs o  "
            + "                                       ON o.encounter_id=e.encounter_id   "
            + "                         WHERE e.encounter_type = ${masterCardEncounterType} "
            + "                                 AND p.voided=0  "
            + "                                 AND e.voided=0  "
            + "                                 AND o.voided=0    "
            + "                           AND o.concept_id = ${stateOfStayOfPreArtPatient}  "
            + "                                 AND o.value_coded= ${suspendedTreatmentConcept}  "
            + "                           AND e.location_id = :location  "
            + "                                 AND o.value_datetime <= :onOrBefore    "
            + "                         GROUP BY p.patient_id ) last11 "
            + " GROUP BY last11.patient_id   "
            + " UNION "
            + " SELECT p.patient_id ,last_home_visit.last    "
            + "	  	FROM patient p    "
            + " 	  	INNER JOIN     "
            + "	  		    (SELECT e.patient_id, max(encounter_datetime)  as last  "
            + "	  		     FROM encounter e     "
            + "	  		     WHERE  e.encounter_type = ${buscaActivaEncounterType}      "
            + "	  		        AND e.voided = 0      "
            + "	  		        AND e.encounter_datetime <= :onOrBefore     "
            + "	  		        AND e.location_id = :location     "
            + "	  		     GROUP BY e.patient_id) last_home_visit     "
            + "	  		          ON last_home_visit.patient_id=p.patient_id     "
            + "	  		 INNER JOIN encounter ee     "
            + "	  		         ON ee.patient_id=last_home_visit.patient_id     "
            + "							AND ee.encounter_datetime = last_home_visit.last "
            + "	  		 INNER JOIN obs o     "
            + "	  		         ON o.encounter_id = ee.encounter_id     "
            + " WHERE o.concept_id = ${defaultingMotiveConcept}     "
            + "	  	AND o.value_coded IN (${transferredOutConcept},${autoTransferConcept})    "
            + "	  	AND ee.encounter_type = ${buscaActivaEncounterType}      "
            + "	  	AND ee.encounter_datetime <= :onOrBefore   "
            + "	  	AND ee.location_id = :location       "
            + "	  	AND p.voided = 0     "
            + "	  	AND o.voided = 0    "
            + "	  	AND ee.voided = 0   "
            + "	GROUP BY p.patient_id "
            + ")most_recent "
            + "        INNER JOIN encounter e ON e.patient_id = most_recent.patient_id "
            + "        INNER JOIN obs obss ON obss.encounter_id=e.encounter_id "
            + "        WHERE e.voided=0 "
            + "            AND obss.voided=0 "
            + "            AND (e.encounter_type IN (${adultoSeguimento},${aRVPediatriaSeguimento},${aRVPharmacia}) AND  e.encounter_datetime >  most_recent.common_date  AND e.encounter_datetime <= :onOrBefore ) OR "
            + "            ( e.encounter_type = ${masterCardDrugPickup} "
            + "                AND obss.concept_id= ${artDatePickup} AND  obss.value_datetime > most_recent.common_date AND obss.value_datetime <= :onOrBefore )   "
            + "            and e.location_id =  :location "
            + "    GROUP BY most_recent.patient_id;";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query);
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
            + " AND o.concept_id=%d AND o.value_coded IN(%d, %d) AND e.location_id=:location AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore";
    return String.format(
        query, encounterType, lastOnsStopConcept, startDrugsConcept, continueRegimenConcept);
  }

  public static String getPatientsTransferedOutInLastHomeVisitCard(
      int buscaActivaEncounterType,
      int defaultingMotiveConcept,
      int transferredOutConcept,
      int autoTransferConcept) {

    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN  "
            + "        (SELECT e.patient_id, max(encounter_datetime)  AS maxdate"
            + "         FROM encounter e "
            + "         WHERE  e.encounter_type = ${buscaActivaEncounterType}  "
            + "            AND e.voided = 0  "
            + "            AND e.encounter_datetime <= :onOrBefore  "
            + "            AND e.location_id = :location "
            + "         GROUP BY e.patient_id) last_home_visit "
            + "            ON last_home_visit.patient_id=p.patient_id "
            + "    INNER JOIN encounter ee "
            + "        ON ee.patient_id=last_home_visit.patient_id "
            + "				AND ee.encounter_datetime = last_home_visit.maxdate "
            + "    INNER JOIN obs o "
            + "        ON o.encounter_id = ee.encounter_id "
            + "WHERE o.concept_id = ${defaultingMotiveConcept} "
            + "    AND o.value_coded IN (${transferredOutConcept},${autoTransferConcept}) "
            + "    AND ee.encounter_type = ${buscaActivaEncounterType}  "
            + "    AND ee.encounter_datetime <= :onOrBefore "
            + "    AND ee.location_id = :location   "
            + "    AND p.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND ee.voided = 0 "
            + "GROUP BY p.patient_id ";

    Map<String, Integer> map = new HashMap<>();
    map.put("buscaActivaEncounterType", buscaActivaEncounterType);
    map.put("defaultingMotiveConcept", defaultingMotiveConcept);
    map.put("transferredOutConcept", transferredOutConcept);
    map.put("autoTransferConcept", autoTransferConcept);

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query);
  }
}
