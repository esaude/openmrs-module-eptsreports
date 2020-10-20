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

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

public class BaseQueries {

  // State ids are left as hard coded for now because all reference same
  // concept
  // they map to concept_id=1369 - TRANSFER FROM OTHER FACILITY
  /**
   * Get the most basic base cohort query
   *
   * @param arvAdultInitialEncounterTypeId Id of Encounter ID 5
   * @param arvPediatriaInitialEncounterTypeId Id of Encounter ID 7
   * @param masterCardResumoMensalEncounterTypeId Id of Encounter ID 53
   * @param hivCareProgramId Id of Program 1
   * @param artProgramId Id of program 2
   * @return base cohort
   */
  public static String getBaseCohortQuery(
      int arvAdultInitialEncounterTypeId,
      int arvPediatriaInitialEncounterTypeId,
      int masterCardResumoMensalEncounterTypeId,
      int hivCareProgramId,
      int artProgramId,
      int masterCardFileOpeningConceptId) {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("arvAdultInitialEncounterTypeId", arvAdultInitialEncounterTypeId);
    valuesMap.put("arvPediatriaInitialEncounterTypeId", arvPediatriaInitialEncounterTypeId);
    valuesMap.put("masterCardResumoMensalEncounterTypeId", masterCardResumoMensalEncounterTypeId);
    valuesMap.put("hivCareProgramId", hivCareProgramId);
    valuesMap.put("artProgramId", artProgramId);
    valuesMap.put("masterCardFileOpeningConceptId", masterCardFileOpeningConceptId);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON e.patient_id=p.patient_id "
            + "WHERE e.voided=0 AND p.voided=0 AND "
            + "e.encounter_type IN (${arvAdultInitialEncounterTypeId},${arvPediatriaInitialEncounterTypeId}) "
            + "AND e.encounter_datetime <= :endDate "
            + "AND e.location_id = :location "
            + "UNION  "
            + "SELECT p.patient_id FROM patient p "
            + "JOIN encounter e ON e.patient_id=p.patient_id "
            + "JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE e.voided=0 AND p.voided=0 AND o.voided=0 AND "
            + "e.encounter_type = ${masterCardResumoMensalEncounterTypeId} "
            + "AND o.concept_id =${masterCardFileOpeningConceptId} "
            + "AND o.value_datetime <= :endDate "
            + "AND e.location_id = :location "
            + "UNION "
            + "SELECT pg.patient_id FROM patient p JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "WHERE pg.voided=0 AND p.voided=0 AND program_id IN (${hivCareProgramId},${artProgramId}) AND pg.date_enrolled <= :endDate AND location_id= :location";

    return sub.replace(query);
  }

  /**
   * Find patients who qualifies to be in a set of facilities and state work flows
   *
   * @return String
   */
  public static String getBaseQueryForDataQuality(int programId) {
    return "SELECT p.patient_id from patient p JOIN patient_program pg ON p.patient_id=pg.patient_id JOIN patient_state ps on pg.patient_program_id=ps.patient_program_id "
        + "WHERE pg.program_id="
        + programId
        + " AND pg.voided=0 AND ps.voided=0 AND p.voided=0  AND ps.state IN(:state) AND pg.location_id IN(:location) AND p.date_created BETWEEN :startDate AND :endDate";
  }

  /**
   * Find patients who qualifies to be in the EC20 workflow.
   *
   * @return String
   */
  public static String getBaseQueryForEc20DataQuality(
      int adultoSeguimentoEncounterTypeId,
      int arvPediatriaInitialEncounterTypeId,
      int arvPharmaciaEncounterTypeId) {
    return "SELECT p.patient_id from patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided=0  AND e.location_id IN(:location) AND p.date_created BETWEEN :startDate AND :endDate AND e.encounter_type IN("
        + adultoSeguimentoEncounterTypeId
        + ","
        + arvPediatriaInitialEncounterTypeId
        + ","
        + arvPharmaciaEncounterTypeId
        + " )";
  }
}
