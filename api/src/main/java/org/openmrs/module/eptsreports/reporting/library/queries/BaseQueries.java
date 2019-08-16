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

import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class BaseQueries {

  // State ids are left as hard coded for now because all reference same
  // concept
  // they map to concept_id=1369 - TRANSFER FROM OTHER FACILITY
  // TODO: Query needs to be refactored
  public static String getBaseCohortQuery(Map<String, String> parameters) {
    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON e.patient_id=p.patient_id "
            + "WHERE e.voided=0 AND p.voided=0 AND e.encounter_type IN (%s) AND e.encounter_datetime<=:endDate AND e.location_id = :location "
            + "UNION "
            + "SELECT pg.patient_id FROM patient p JOIN patient_program pg ON p.patient_id=pg.patient_id WHERE pg.voided=0 AND p.voided=0 AND program_id IN (%s) AND date_enrolled<=:endDate AND location_id=:location ";
    String encounterTypes =
        StringUtils.join(
            Arrays.asList(
                parameters.get("arvAdultInitialEncounterTypeId"),
                parameters.get("arvPediatriaInitialEncounterTypeId")),
            ',');
    String programs =
        StringUtils.join(
            Arrays.asList(parameters.get("hivCareProgramId"), parameters.get("artProgramId")), ',');
    return String.format(query, encounterTypes, programs);
  }

  /**
   * Find patients who qualifies to be in a set of facilities and state work flows
   *
   * @retrun String
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
   * @return
   */
  public static String getBaseQueryForEc20DataQuality() {
    return "SELECT p.patient_id from patient p JOIN patient_program pg ON p.patient_id=pg.patient_id JOIN patient_state ps on pg.patient_program_id=ps.patient_program_id "
        + "WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0  AND pg.location_id IN(:location) AND p.date_created BETWEEN :startDate AND :endDate";
  }
}
