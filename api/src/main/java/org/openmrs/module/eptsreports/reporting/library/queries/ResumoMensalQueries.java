/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.queries;

public class ResumoMensalQueries {

  /**
   * All patients with encounter type 53, and Pre-ART Start Date that is less than startDate
   *
   * @return String
   */
  public static String getAllPatientsWithPreArtStartDateLessThanReportingStartDate(
      int encouterTypeFichaResumo,
      int conceptDataInicioPreTart,
      int encounterTypeAdultoInicialA,
      int encounterTypePediatriaInicialA,
      int programServicoTarvCuidado) {
    String query =
        "SELECT InitArt.patient_id FROM (SELECT p.patient_id,MIN(o.value_datetime) AS initialDate FROM patient p  "
            + "INNER JOIN encounter e  ON e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "WHERE e.voided=0 AND o.voided=0 AND e.encounter_type=%d AND e.location_id=:location AND o.value_datetime IS NOT NULL AND o.concept_id=%d AND o.value_datetime<:startDate GROUP BY p.patient_id "
            + "UNION SELECT p.patient_id,min(e.encounter_datetime) AS initialDate FROM patient p  "
            + "INNER JOIN encounter e  ON e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "WHERE e.voided=0 AND o.voided=0 AND e.encounter_type IN(%d, %d) "
            + "AND e.location_id=:location  AND e.encounter_datetime<:startDate GROUP BY p.patient_id UNION "
            + "SELECT pg.patient_id, pg.date_enrolled  AS initialDate FROM patient p "
            + "INNER JOIN patient_program pg on pg.patient_id=p.patient_id "
            + "WHERE pg.program_id=%d AND pg.voided=0 AND pg.date_enrolled<:startDate GROUP BY patient_id) InitArt";

    return String.format(
        query,
        encouterTypeFichaResumo,
        conceptDataInicioPreTart,
        encounterTypeAdultoInicialA,
        encounterTypePediatriaInicialA,
        programServicoTarvCuidado);
  }

  /**
   * All patients with encounter type 53, and Pre-ART Start Date that falls between startDate and
   * enddate
   *
   * @return String
   */
  public static String getAllPatientsWithPreArtStartDateWithBoundaries(
      int encouterTypeFichaResumo,
      int conceptDataInicioPreTart,
      int encounterTypeAdultoInicialA,
      int encounterTypePediatriaInicialA,
      int programServicoTarvCuidado) {
    String query =
        "SELECT preTarvFinal.patient_id FROM ( "
            + "SELECT preTarv.patient_id, MIN(preTarv.initialDate) FROM ( "
            + "SELECT p.patient_id,min(o.value_datetime) AS initialDate FROM patient p  "
            + "INNER JOIN encounter e  ON e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id WHERE e.voided=0 AND o.voided=0 AND e.encounter_type=%s "
            + "AND e.location_id=:location  AND o.value_datetime IS NOT NULL AND o.concept_id=%s AND o.value_datetime<=:endDate GROUP BY p.patient_id "
            + "UNION SELECT p.patient_id,min(e.encounter_datetime) AS initialDate FROM patient p "
            + "INNER JOIN encounter e  ON e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id WHERE e.voided=0 AND o.voided=0 AND e.encounter_type IN(%s, %s) AND e.location_id=:location AND e.encounter_datetime<=:endDate GROUP BY p.patient_id "
            + "UNION SELECT pg.patient_id, MIN(pg.date_enrolled) AS initialDate FROM patient p "
            + "INNER JOIN patient_program pg on pg.patient_id=p.patient_id WHERE pg.program_id=%s AND pg.voided=0 AND pg.date_enrolled<=:endDate  GROUP BY patient_id ) preTarv "
            + "WHERE preTarv.initialDate BETWEEN :startDate AND :endDate GROUP BY preTarv.patient_id) "
            + "preTarvFinal GROUP BY preTarvFinal.patient_id ";

    return String.format(
        query,
        encouterTypeFichaResumo,
        conceptDataInicioPreTart,
        encounterTypeAdultoInicialA,
        encounterTypePediatriaInicialA,
        programServicoTarvCuidado);
  }

  /**
   * Number of patients transferred-in from another HFs during the current month
   *
   * @return String
   */
  public static String getPatientsTransferredFromAnotherHealthFacilityDuringTheCurrentMonth(
      int preTarvProgram,
      int trasferedState,
      int encouterTypeFichaResumo,
      int typeOfPatientTransferedFromConcept,
      int typeOfPatientTransferedFromAnswer,
      int transferFromOtherFacility,
      int transferFromOtherFacilityAnswer) {

    String query =
        "SELECT trasferedPatients.patient_id FROM ("
            + "SELECT pg.patient_id, MIN(ps.start_date) FROM  patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "WHERE pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%d and ps.state=%d and ps.start_date=pg.date_enrolled and ps.start_date<:startDate  and location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id,o.obs_datetime AS initialDate  FROM patient p  "
            + "INNER JOIN encounter e  ON e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "INNER JOIN obs obsPretarv on e.encounter_id=obsPretarv.encounter_id "
            + "WHERE e.voided=0 AND o.voided=0 AND e.encounter_type=%d and obsPretarv.voided=0 and obsPretarv.concept_id=%d AND obsPretarv.value_coded=%d "
            + "AND e.location_id=:location  AND o.concept_id=%d AND o.value_coded=%d AND o.obs_datetime<:startDate GROUP BY p.patient_id)trasferedPatients GROUP BY trasferedPatients.patient_id ";

    return String.format(
        query,
        preTarvProgram,
        trasferedState,
        encouterTypeFichaResumo,
        typeOfPatientTransferedFromConcept,
        typeOfPatientTransferedFromAnswer,
        transferFromOtherFacility,
        transferFromOtherFacilityAnswer);
  }

  public static String
      getPatientsTransferredFromAnotherHealthFacilityDuringTheCurrentStartDateEndDate(
          int preTarvProgram,
          int trasferedState,
          int encouterTypeFichaResumo,
          int typeOfPatientTransferedFromConcept,
          int typeOfPatientTransferedFromAnswer,
          int transferFromOtherFacility,
          int transferFromOtherFacilityAnswer) {

    String query =
        "SELECT trasferedPatients.patient_id FROM ( "
            + "SELECT pg.patient_id, MIN(ps.start_date) FROM  patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "WHERE pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%d and ps.state=%d and ps.start_date=pg.date_enrolled and ps.start_date BETWEEN :startDate AND :endDate and location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id,MIN(o.obs_datetime) AS initialDate  FROM patient p  "
            + "INNER JOIN encounter e  ON e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "INNER JOIN obs obsPretarv on e.encounter_id=obsPretarv.encounter_id "
            + "WHERE e.voided=0 AND o.voided=0 AND e.encounter_type=%d and obsPretarv.voided=0 and obsPretarv.concept_id=%d AND obsPretarv.value_coded=%d "
            + "AND e.location_id=:location AND o.concept_id=%d AND o.value_coded=%d AND o.obs_datetime BETWEEN :startDate AND :endDate GROUP BY p.patient_id )trasferedPatients GROUP BY trasferedPatients.patient_id ";

    return String.format(
        query,
        preTarvProgram,
        trasferedState,
        encouterTypeFichaResumo,
        typeOfPatientTransferedFromConcept,
        typeOfPatientTransferedFromAnswer,
        transferFromOtherFacility,
        transferFromOtherFacilityAnswer);
  }

  public static String getPatientsTransferredFromAnotherHealthFacilityEndDate(
      int preTarvProgram,
      int trasferedState,
      int encouterTypeFichaResumo,
      int typeOfPatientTransferedFromConcept,
      int typeOfPatientTransferedFromAnswer,
      int transferFromOtherFacility,
      int transferFromOtherFacilityAnswer) {

    String query =
        "SELECT trasferedPatients.patient_id FROM ("
            + "SELECT pg.patient_id, MIN(ps.start_date) FROM  patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "WHERE pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%d and ps.state=%d and ps.start_date=pg.date_enrolled and ps.start_date<:endDate  and location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id,o.obs_datetime AS initialDate FROM patient p "
            + "INNER JOIN encounter e  ON e.patient_id=p.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "INNER JOIN obs obsPretarv on e.encounter_id=obsPretarv.encounter_id "
            + "WHERE e.voided=0 AND o.voided=0 AND e.encounter_type=%d and obsPretarv.voided=0 and obsPretarv.concept_id=%d AND obsPretarv.value_coded=%d "
            + "AND e.location_id=:location  AND o.concept_id=%d AND o.value_coded=%d AND o.obs_datetime<:startDate GROUP BY p.patient_id)trasferedPatients GROUP BY trasferedPatients.patient_id ";

    return String.format(
        query,
        preTarvProgram,
        trasferedState,
        encouterTypeFichaResumo,
        typeOfPatientTransferedFromConcept,
        typeOfPatientTransferedFromAnswer,
        transferFromOtherFacility,
        transferFromOtherFacilityAnswer);
  }

  public static String getPatientsTransferredFromAnotherHealthFacilityB5() {
    String query =
        "select transferidopara.patient_id from ( "
            + "select patient_id,max(data_transferidopara) data_transferidopara from ( "
            + "select pg.patient_id,max(ps.start_date) data_transferidopara from  patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id where pg.voided=0 and ps.voided=0 and p.voided=0 and "
            + "pg.program_id=2 and ps.state=7 and ps.end_date is null and ps.start_date<=:endDate and location_id=:location group by p.patient_id "
            + "union "
            + "select p.patient_id,max(o.obs_datetime) data_transferidopara from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.voided=0 and p.voided=0 and o.obs_datetime<=:endDate and o.voided=0 and o.concept_id=6272 and o.value_coded=1706 and e.encounter_type=53 and  e.location_id=:location group by p.patient_id "
            + "union "
            + "select p.patient_id,max(e.encounter_datetime) data_transferidopara from  patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id where  e.voided=0 and p.voided=0 and e.encounter_datetime<=:endDate and "
            + "o.voided=0 and o.concept_id=6273 and o.value_coded=1706 and e.encounter_type=6 and  e.location_id=:location group by p.patient_id) transferido group by patient_id ) transferidopara "
            + "inner join( "
            + "select patient_id,max(encounter_datetime) encounter_datetime from( "
            + "select p.patient_id,max(e.encounter_datetime) encounter_datetime from  patient p "
            + "inner join encounter e on e.patient_id=p.patient_id where  p.voided=0 and e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location and e.encounter_type in (18,6,9) "
            + "group by p.patient_id "
            + "union "
            + "Select p.patient_id,max(value_datetime) encounter_datetime from  patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id) consultaLev group by patient_id) consultaOuARV on transferidopara.patient_id=consultaOuARV.patient_id "
            + "where consultaOuARV.encounter_datetime<transferidopara.data_transferidopara and transferidopara.data_transferidopara between :startDate AND :endDate ";
    return query;
  }

  public static String getPatientsWhoSuspendTratmentB6() {

    String query =
        "select suspenso1.patient_id from ( "
            + "select patient_id,max(data_suspencao) data_suspencao from ( "
            + "select pg.patient_id,max(ps.start_date) data_suspencao from  patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=2 and ps.state=8 and ps.end_date is null and ps.start_date<=:endDate and location_id=:location "
            + "group by p.patient_id  "
            + "union "
            + " select p.patient_id,max(o.obs_datetime) data_suspencao from  patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.voided=0 and p.voided=0 and o.obs_datetime<=:endDate and o.voided=0 and o.concept_id=6272 and o.value_coded=1709 and e.encounter_type=53 and  e.location_id=:location group by p.patient_id "
            + "union "
            + "select  p.patient_id,max(e.encounter_datetime) data_suspencao from  patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where  e.voided=0 and p.voided=0 and e.encounter_datetime<=:endDate and o.voided=0 and o.concept_id=6273 and o.value_coded=1709 and e.encounter_type=6 and  e.location_id=:location group by p.patient_id ) suspenso group by patient_id) suspenso1 "
            + "inner join ( "
            + "select patient_id,max(encounter_datetime) encounter_datetime from ( "
            + "select p.patient_id,max(e.encounter_datetime) encounter_datetime from  patient p "
            + "inner join encounter e on e.patient_id=p.patient_id where p.voided=0 and e.voided=0 and e.encounter_datetime<=:endDate and  e.location_id=:location and e.encounter_type in (18,6,9) group by p.patient_id "
            + "union "
            + "Select  p.patient_id,max(value_datetime) encounter_datetime from  patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where  p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id) consultaLev group by patient_id) consultaOuARV on suspenso1.patient_id=consultaOuARV.patient_id "
            + "where consultaOuARV.encounter_datetime<suspenso1.data_suspencao and suspenso1.data_suspencao between :startDate  AND :endDate ";

    return query;
  }

  public static String getPatientsTransferredFromAnotherHealthFacilityByEndOfPreviousMonth(
      int masterCardEncounter,
      int transferFromConcept,
      int yesConcept,
      int typeOfPantientConcept,
      int tarvConcept) {

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs transf "
            + "         ON transf.encounter_id = e.encounter_id "
            + "       JOIN obs type "
            + "         ON type.encounter_id = e.encounter_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.encounter_type = %d "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime < :onOrBefore "
            + "       AND transf.voided = 0 "
            + "       AND transf.concept_id = %d "
            + "       AND transf.value_coded = %d "
            + "       AND transf.obs_datetime < :onOrBefore "
            + "       AND type.voided = 0 "
            + "       AND type.concept_id = %d "
            + "       AND type.value_coded = %d";

    return String.format(
        query,
        masterCardEncounter,
        transferFromConcept,
        yesConcept,
        typeOfPantientConcept,
        tarvConcept);
  }

  public static String getPatientsForF2ForExclusionFromMainQuery(
      int encounterType, int tbSymptomsConcept, int yesConcept, int tbTreatmentPlanConcept) {
    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + " JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " JOIN obs o "
            + " ON o.encounter_id = e.encounter_id "
            + " JOIN (SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM encounter enc JOIN patient pat ON pat.patient_id=enc.patient_id "
            + " JOIN obs ob ON enc.encounter_id=ob.encounter_id WHERE pat.voided = 0 AND enc.voided = 0 AND ob.voided = 0 "
            + " AND enc.location_id = :location AND enc.encounter_datetime BETWEEN :startDate AND :endDate "
            + " AND enc.encounter_type= %d AND ob.concept_id=%d AND ob.value_coded=%d) ed "
            + " ON p.patient_id=ed.patient_id"
            + " WHERE  p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND e.encounter_type = %d "
            + " AND e.location_id = :location "
            + " AND e.encounter_datetime = ed.endDate "
            + " AND o.voided = 0 "
            + " AND o.concept_id = %d ";
    return String.format(
        query, encounterType, tbSymptomsConcept, yesConcept, encounterType, tbTreatmentPlanConcept);
  }

  /**
   * Get patients with encounters within start and end date F1: Number of patients who had clinical
   * appointment during the reporting month
   *
   * @return String
   */
  public static String getPatientsWithGivenEncounterType(int encounterType) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id "
            + " WHERE e.encounter_type=%d AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND p.voided=0 AND e.voided=0 ";
    return String.format(query, encounterType);
  }

  /**
   * Get patients with viral load suppression
   *
   * @return String
   */
  public static String getPatientsHavingViralLoadSuppression(
      int viralLoadConcept, int encounterType) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + " AND o.value_numeric IS NOT NULL "
            + " AND o.concept_id=%d "
            + " AND e.encounter_type=%d "
            + " AND o.value_numeric < 1000";
    return String.format(query, viralLoadConcept, encounterType);
  }

  /**
   * getPatientsWithCodedObsAndAnswers
   *
   * @return String
   */
  public static String getPatientsWithCodedObsAndAnswers(
      int encounterType, int questionConceptId, int answerConceptId) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + " AND e.location_id = :location AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.encounter_type=%d "
            + " AND o.concept_id=%d AND o.value_coded=%d";
    return String.format(query, encounterType, questionConceptId, answerConceptId);
  }

  /**
   * Get patients with viral load suppression
   *
   * @return String
   */
  public static String getPatientsHavingViralLoadResults(int viralLoadConcept, int encounterType) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + " AND o.value_numeric IS NOT NULL "
            + " AND o.concept_id=%d "
            + " AND e.encounter_type=%d ";
    return String.format(query, viralLoadConcept, encounterType);
  }

  /**
   * Get patients with any coded obs value
   *
   * @return String
   */
  public static String gePatientsWithCodedObs(int encounterType, int conceptId) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + " AND e.location_id = :location AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.encounter_type=%d "
            + " AND o.concept_id=%d ";
    return String.format(query, encounterType, conceptId);
  }

  /**
   * E1 exclusions
   *
   * @return String
   */
  public static String getE1ExclusionCriteria(
      int encounterType, int questionConceptId, int answerConceptId) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " JOIN (SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM patient pat "
            + " JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob ON enc.encounter_id=ob.encounter_id "
            + " WHERE pat.voided = 0 AND enc.voided = 0 AND ob.voided = 0 AND enc.location_id = :location "
            + " AND enc.encounter_datetime BETWEEN :startDate AND :endDate AND enc.encounter_type=%d AND "
            + " ob.concept_id=%d AND ob.value_coded=%d) ed "
            + " ON p.patient_id=ed.patient_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + " AND e.location_id = :location AND e.encounter_datetime BETWEEN "
            + " IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) "
            + " AND ed.endDate AND e.encounter_type=%d "
            + " AND o.concept_id=%d AND o.value_coded=%d";
    return String.format(
        query,
        encounterType,
        questionConceptId,
        answerConceptId,
        encounterType,
        questionConceptId,
        answerConceptId);
  }

  /**
   * E2 exclusions
   *
   * @param viralLoadConcept
   * @param encounterType
   * @param qualitativeConcept
   * @return String
   */
  public static String getE2ExclusionCriteria(
      int viralLoadConcept, int encounterType, int qualitativeConcept) {

    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + "JOIN (SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM patient pat JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob "
            + " ON enc.encounter_id=ob.encounter_id "
            + " WHERE pat.voided=0 AND enc.voided=0 AND ob.voided=0 AND enc.location_id=:location AND enc.encounter_datetime "
            + " BETWEEN :startDate AND :endDate AND ob.concept_id IN(%d, %d) AND enc.encounter_type=%d) ed "
            + " ON p.patient_id=ed.patient_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN "
            + " IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) "
            + " AND ed.endDate "
            + " AND o.concept_id IN (%d, %d)"
            + " AND e.encounter_type=%d ";
    return String.format(
        query,
        viralLoadConcept,
        qualitativeConcept,
        encounterType,
        viralLoadConcept,
        qualitativeConcept,
        encounterType);
  }

  /**
   * E3 exclusion
   *
   * @param viralLoadConcept
   * @param encounterType
   * @param qualitativeConcept
   * @return
   */
  public static String getE3ExclusionCriteria(
      int viralLoadConcept, int encounterType, int qualitativeConcept) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " JOIN (SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM patient pat "
            + " JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob ON enc.encounter_id=ob.encounter_id "
            + " WHERE pat.voided=0 AND enc.voided=0 AND ob.voided=0 AND enc.location_id=:location "
            + " AND enc.encounter_datetime BETWEEN :startDate AND :endDate AND ob.value_numeric IS NOT NULL "
            + " AND ob.concept_id=%d AND enc.encounter_type=%d AND ob.value_numeric < 1000) ed "
            + " ON p.patient_id=ed.patient_id"
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN "
            + " IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) "
            + " AND ed.endDate "
            + " AND o.value_numeric IS NOT NULL "
            + " AND o.concept_id=%d "
            + " AND e.encounter_type=%d "
            + " AND o.value_numeric < 1000"
            + " UNION "
            + " SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id "
            + " JOIN (SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM patient pat "
            + " JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob ON enc.encounter_id=ob.encounter_id "
            + " WHERE pat.voided = 0 AND enc.voided = 0 AND ob.voided = 0 AND enc.location_id = :location AND "
            + " enc.encounter_datetime BETWEEN :startDate AND :endDate AND enc.encounter_type=%d AND ob.concept_id=%d) ed "
            + " ON p.patient_id=ed.patient_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + " AND e.location_id = :location AND e.encounter_datetime BETWEEN "
            + " IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) "
            + " AND ed.endDate "
            + " AND e.encounter_type=%d "
            + " AND o.concept_id=%d ";

    return String.format(
        query,
        viralLoadConcept,
        encounterType,
        viralLoadConcept,
        encounterType,
        encounterType,
        qualitativeConcept,
        encounterType,
        qualitativeConcept);
  }

  /**
   * F3 exclusions
   *
   * @param encounterType
   * @return
   */
  public static String getF3Exclusion(int encounterType) {
    String query =
        " SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN ( "
            + " SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM encounter enc JOIN patient pat "
            + " ON enc.patient_id=pat.patient_id WHERE enc.encounter_type=%d AND enc.location_id=:location "
            + " AND enc.encounter_datetime BETWEEN :startDate AND :endDate AND pat.voided=0 AND enc.voided=0) ed "
            + " ON p.patient_id=ed.patient_id"
            + " WHERE e.encounter_type=%d AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN "
            + " IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) "
            + " AND ed.endDate "
            + "AND p.voided=0 AND e.voided=0 ";
    return String.format(query, encounterType, encounterType);
  }
}
