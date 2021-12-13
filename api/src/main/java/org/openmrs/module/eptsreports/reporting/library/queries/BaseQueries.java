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
  public static String getBaseCohortQuery(final Map<String, String> parameters) {
    final String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON e.patient_id=p.patient_id "
            + "WHERE e.voided=0 AND p.voided=0 AND e.encounter_type IN (%s) AND e.encounter_datetime<=:endDate AND e.location_id = :location "
            + "UNION "
            + "SELECT pg.patient_id FROM patient p JOIN patient_program pg ON p.patient_id=pg.patient_id WHERE pg.voided=0 AND p.voided=0 AND program_id IN (%s) AND date_enrolled<=:endDate AND location_id=:location ";
    final String encounterTypes =
        StringUtils.join(
            Arrays.asList(
                parameters.get("arvAdultInitialEncounterTypeId"),
                parameters.get("arvPediatriaInitialEncounterTypeId")),
            ',');
    final String programs =
        StringUtils.join(
            Arrays.asList(parameters.get("hivCareProgramId"), parameters.get("artProgramId")), ',');
    return String.format(query, encounterTypes, programs);
  }

  public static String getBaseCohortQuery() {

    final String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + "WHERE e.voided=0 AND p.voided=0 AND e.encounter_type IN (5,7) "
            + "AND e.encounter_datetime<=:endDate AND e.location_id = :location "
            + "UNION SELECT pg.patient_id FROM 	patient p INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "WHERE pg.voided=0 AND p.voided=0 AND program_id IN (1,2) AND date_enrolled<=:endDate AND location_id=:location "
            + "UNION SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=53 AND o.concept_id=23891 "
            + "AND o.value_datetime IS NOT NULL AND o.value_datetime<=:endDate AND e.location_id=:location  "
            + " union																				"
            + " select client.patient_id from patient client											"
            + " 	inner join encounter e on e.patient_id = client.patient_id							"
            + " 	inner join obs o on o.encounter_id = e.encounter_id 								"
            + " where client.voided = 0 and e.voided = 0 and o.voided = 0							"
            + " and e.encounter_type = 80 and o.concept_id =165296 and o.value_coded = 1256			"
            + "  and e.location_id = :location and o.obs_datetime <= :endDate						"
            + " union 																				"
            + " select client.patient_id from patient client 										"
            + " 	inner join encounter e on e.patient_id = client.patient_id 							"
            + " 	inner join obs o on o.encounter_id = e.encounter_id 								"
            + " where client.voided = 0 and e.voided = 0 and o.voided = 0 							"
            + " 	and e.encounter_type = 80   and o.concept_id =165211 and e.location_id = :location  "
            + "	and o.value_datetime <= :endDate 													";

    return query;
  }

  public static String getAdultBaseCohortQuery() {
    final String query =
        "select inscrito.patient_id                                                                                       "
            + "from                                                                                                                        "
            + "(                                                                                                                           "
            + "	select p.patient_id                                                                                                        "
            + "	from patient p inner join encounter e on e.patient_id=p.patient_id                                                         "
            + "	where e.voided=0 and p.voided=0 and e.encounter_type in (5,7) and e.encounter_datetime<=:endDate and                       "
            + " e.location_id = :location                                                                                                  "
            + "                                                                                                                            "
            + "	union                                                                                                                      "
            + "                                                                                                                            "
            + "	select 	pg.patient_id                                                                                                      "
            + "	from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id                                              "
            + "	where 	pg.voided=0 and p.voided=0 and program_id in (1,2) and date_enrolled<=:endDate and location_id=:location           "
            + "                                                                                                                            "
            + "	union                                                                                                                      "
            + "                                                                                                                            "
            + "	Select 	p.patient_id                                                                                                       "
            + "	from 	patient p                                                                                                          "
            + "			inner join encounter e on p.patient_id=e.patient_id                                                                "
            + "			inner join obs o on e.encounter_id=o.encounter_id                                                                  "
            + "	where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=53 and                                               "
            + "			o.concept_id=23891 and o.value_datetime is not null and                                                            "
            + "			o.value_datetime<=:endDate and e.location_id=:location                                                             "
            + ") inscrito                                                                                                                  "
            + "inner join person pe on pe.person_id=inscrito.patient_id                                                                    "
            + "where timestampdiff(YEAR,pe.birthdate,:endDate)>=15                                                                         ";
    return query;
  }

  /**
   * Find patients who qualifies to be in a set of facilities and state work flows
   *
   * @retrun String
   */
  public static String getBaseQueryForDataQuality(final int programId) {
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
  public static String getBaseQueryForEc20DataQuality(
      final int adultoSeguimentoEncounterTypeId,
      final int arvPediatriaInitialEncounterTypeId,
      final int arvPharmaciaEncounterTypeId) {
    return "SELECT p.patient_id from patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided=0  AND e.location_id IN(:location) AND p.date_created BETWEEN :startDate AND :endDate AND e.encounter_type IN("
        + adultoSeguimentoEncounterTypeId
        + ","
        + arvPediatriaInitialEncounterTypeId
        + ","
        + arvPharmaciaEncounterTypeId
        + " )";
  }
}
