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
      int artDeadWorkflowState,
      boolean maxDate) {

    String dateCluase =
        " and ps.state in (%d,%d,%d) and ps.end_date is null and ps.start_date<=:onOrBefore ";
    ;

    if (maxDate) {
      dateCluase =
          " and ps.state in (%d,%d,%d) and ps.end_date is null and ps.start_date ="
              + "(select max(start_date)  from patient_state where patient_state_id = ps.patient_state_id ) ";
    }

    String query =
        " select p.patient_id from patient p "
            + " inner join patient_program pg on p.patient_id=pg.patient_id "
            + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + " where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%d "
            + dateCluase
            + "and pg.location_id=:location group by p.patient_id  ";

    return String.format(
        query,
        artProgram,
        transferredOutToAnotherHealthFacilityWorkflowState,
        suspendedTreatmentWorkflowState,
        artDeadWorkflowState);
  }

  public static String getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(
      boolean maxDate) {

    String dateCluase =
        " where prs.dead=1 and prs.death_date <= :onOrBefore and p.voided=0 and prs.voided=0 ";

    if (maxDate) {
      dateCluase =
          " where prs.dead=1 and prs.death_date = (select max(death_date) from person where  person_id = prs.person_id )"
              + " and p.voided=0 and prs.voided=0  ";
    }
    String query =
        "select p.patient_id  "
            + " from patient p "
            + " inner join person prs on prs.person_id=p.patient_id "
            + " inner join  encounter e on  e.patient_id=p.patient_id "
            + dateCluase
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
      int patientHasDiedConcept,
      boolean maxDate) {

    String dateClause = " and e.location_id = :location and e.encounter_datetime <= :onOrBefore ";

    if (maxDate) {
      dateClause =
          " and e.location_id = :location and e.encounter_datetime "
              + " =  (select  max(encounter_datetime) from encounter where  patient_id= p.patient_id "
              + " and encounter_type = e.encounter_type) ";
    }
    String query =
        "select  p.patient_id "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + "and o.concept_id=%s and   o.value_coded=%s "
            + dateClause
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
      int transferredOutConcept,
      boolean maxDate) {
    String dateClause = " and e.location_id = :location and e.encounter_datetime <= :onOrBefore ";

    if (maxDate) {
      dateClause =
          " and e.location_id = :location and e.encounter_datetime "
              + " =  (select  max(encounter_datetime) from encounter where  patient_id= p.patient_id "
              + " and encounter_type = e.encounter_type ) ";
    }

    String query =
        "select  p.patient_id "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + "and o.concept_id=%s and   o.value_coded=%s "
            + dateClause
            + "group by p.patient_id ";

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
      int suspendedTreatmentConcept,
      boolean maxDate) {
    String dateClause = " and e.location_id = :location and e.encounter_datetime <= :onOrBefore ";

    if (maxDate) {
      dateClause =
          " and e.location_id = :location and e.encounter_datetime "
              + " =  (select  max(encounter_datetime) from encounter where  patient_id= p.patient_id "
              + " and encounter_type = e.encounter_type )";
    }
    String query =
        "select  p.patient_id "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (%s,%s) and p.voided=0  and e.voided=0 and o.voided=0 "
            + "and o.concept_id=%s and   o.value_coded=%s "
            + dateClause
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
        "select  final.patient_id "
            + " from ( "
            + " select most_recent.patient_id , max(date_add(most_recent.obs_datetime, interval 30 day)) final_obs_date "
            + "        from (SELECT  p.patient_id, "
            + "		       recent.location_id, "
            + "		       recent.obs_datetime "
            + "		FROM   patient p  "
            + "	       INNER join  (SELECT enc.patient_id, enc.location_id, max(obs.value_datetime) obs_datetime "
            + "            	FROM   patient pa 	 "
            + "					INNER JOIN encounter enc  "
            + "						ON enc.patient_id = pa.patient_id  "
            + "				    INNER JOIN obs obs "
            + "						ON obs.encounter_id = enc.encounter_id "
            + "        		WHERE  pa.voided = 0  "
            + "			       AND enc.voided = 0  "
            + "			       AND obs.voided = 0  "
            + "			       and obs.concept_id = %s                                  "
            + "                AND obs.value_datetime IS NOT NULL "
            + "               and enc.encounter_type=%s  and  enc.location_id= :location "
            + "               GROUP by pa.patient_id, enc.location_id) recent on p.patient_id=recent.patient_id and "
            + "                p.voided=0			        "
            + "		GROUP  BY p.patient_id  "
            + "               "
            + "            union    "
            + "             "
            + "            SELECT p.patient_id, "
            + "			       recent.location_id, "
            + "			       recent.obs_datetime "
            + " "
            + "	FROM   patient p  "
            + "	       INNER join  (SELECT enc.patient_id, enc.location_id, max(obs.value_datetime) obs_datetime "
            + "	        	FROM   patient pa  "
            + "				INNER JOIN encounter enc  "
            + "					ON enc.patient_id = pa.patient_id  "
            + "			    INNER JOIN obs obs "
            + "					ON obs.encounter_id = enc.encounter_id "
            + "    		WHERE  pa.voided = 0  "
            + "		       AND enc.voided = 0  "
            + "		       AND obs.voided = 0  "
            + "		       and obs.concept_id = %s                                  "
            + "            AND obs.value_datetime IS NOT NULL "
            + "           and enc.encounter_type in (%s,%s) and  enc.location_id= :location "
            + "           GROUP by pa.patient_id, enc.location_id) recent on p.patient_id=recent.patient_id and "
            + "            p.voided=0		        "
            + "	GROUP  BY p.patient_id "
            + " "
            + "	union "
            + " "
            + "	            SELECT p.patient_id, "
            + "			       recent.location_id, "
            + "			       recent.obs_datetime "
            + " "
            + "	FROM   patient p  "
            + "	       INNER join  (SELECT enc.patient_id, enc.location_id, max(date_add(obs.value_datetime, interval 30 day)) obs_datetime "
            + "	        	FROM   patient pa  "
            + "				INNER JOIN encounter enc  "
            + "					ON enc.patient_id = pa.patient_id  "
            + "			    INNER JOIN obs obs "
            + "					ON obs.encounter_id = enc.encounter_id "
            + "    		WHERE  pa.voided = 0  "
            + "		       AND enc.voided = 0  "
            + "		       AND obs.voided = 0  "
            + "		       and obs.concept_id = %s                                  "
            + "            AND obs.value_datetime IS NOT NULL "
            + "           and enc.encounter_type = %s and  enc.location_id= :location "
            + "           GROUP by pa.patient_id, enc.location_id) recent on p.patient_id=recent.patient_id and "
            + "            p.voided=0		        "
            + "	GROUP  BY p.patient_id "
            + " "
            + " "
            + "	) most_recent    GROUP by   most_recent.patient_id) final  "
            + "where final.final_obs_date < :onOrBefore  ;  ";

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
      int returnVisitDateConcept,
      int returnVisitDateForArvDrugConcept) {

    String query =
        "SELECT ps.patient_id  "
            + "FROM   (SELECT p.patient_id  "
            + "        FROM   patient p  "
            + "               INNER JOIN encounter e  "
            + "                       ON e.patient_id = p.patient_id  "
            + "               INNER JOIN obs o  "
            + "                       ON o.encounter_id = e.encounter_id  "
            + "        WHERE  p.voided = 0  "
            + "               AND e.voided = 0  "
            + "               AND o.voided = 0  "
            + "               AND p.patient_id NOT IN (SELECT patient_id  "
            + "                                        FROM   encounter  "
            + "                                        WHERE  encounter_type NOT IN (  "
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
            + "                                                  AND encounter_type =  "
            + "                                                      e.encounter_type  "
            + "                                          )  "
            + "               AND e.encounter_type IN ( %s, %s, %s )  "
            + "               AND e.encounter_id NOT IN (SELECT encounter_id  "
            + "                                          FROM   obs  "
            + "                                          WHERE  concept_id = %s  "
            + "                                                  OR concept_id = %s)  "
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
}
