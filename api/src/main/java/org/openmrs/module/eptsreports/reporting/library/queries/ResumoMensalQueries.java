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
   * Get the header and footer information for the report
   *
   * @param location_attribute_id
   * @return String
   */
  public static String getDefaultSettings(int location_attribute_id) {

    return "SELECT la.value_reference AS code, l.name AS name, DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s') AS date_time, l.state_province AS province, l.county_district AS district FROM location l INNER JOIN location_attribute la ON l.location_id=la.location_id INNER JOIN location_attribute_type lat ON la.attribute_type_id=lat.location_attribute_type_id WHERE l.location_id=:location AND lat.location_attribute_type_id="
        + location_attribute_id;
  }

  /**
   * All patients with encounter type 53, and Pre-ART Start Date that is less than startDate
   *
   * @return String
   */
  public static String getAllPatientsWithPreArtStartDateLessThanReportingStartDate(
      int encounterType, int conceptId) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=%d AND e.location_id=:location AND e.encounter_datetime<:startDate AND o.concept_id=%d";
    return String.format(query, encounterType, conceptId);
  }
}
