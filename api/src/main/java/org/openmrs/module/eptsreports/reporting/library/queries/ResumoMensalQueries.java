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
      int encounterType, int conceptId) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=%d AND e.location_id=:location AND o.value_datetime IS NOT NULL AND o.concept_id=%d AND o.value_datetime <:startDate";
    return String.format(query, encounterType, conceptId);
  }

  /**
   * All patients with encounter type 53, and Pre-ART Start Date that falls between startDate and
   * enddate
   *
   * @return String
   */
  public static String getAllPatientsWithPreArtStartDateWithBoundaries(
      int encounterType, int conceptId) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=%d AND e.location_id=:location AND o.value_datetime IS NOT NULL  AND o.value_datetime BETWEEN :startDate AND :endDate AND o.concept_id=%d";
    return String.format(query, encounterType, conceptId);
  }

  /**
   * Number of patients transferred-in from another HFs during the current month
   *
   * @return String
   */
  public static String getPatientsTransferredFromAnotherHealthFacilityDuringTheCurrentMonth(
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
            + "       AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "       AND transf.voided = 0 "
            + "       AND transf.concept_id = %d "
            + "       AND transf.value_coded = %d "
            + "       AND transf.obs_datetime BETWEEN :startDate AND :endDate "
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

  public static String getPatientsForF2ForExclusionFromMainQuery(int encounterType, int conceptId) {
    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + " JOIN encounter e "
            + " ON p.patient_id = e.patient_id "
            + " JOIN obs o "
            + " ON o.encounter_id = e.encounter_id "
            + " WHERE  p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND e.encounter_type = %d "
            + " AND e.location_id = :location "
            + " AND e.encounter_datetime =:endDate "
            + " AND o.voided = 0 "
            + " AND o.concept_id = %d ";
    return String.format(query, encounterType, conceptId);
  }
}
