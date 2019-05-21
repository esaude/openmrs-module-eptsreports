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

public class BreastfeedingQueries {

  public static String getPatientsWhoGaveBirthWithinReportingPeriod(
      int etvProgram, int patientState) {
    return "SELECT 	pg.patient_id"
        + " FROM patient p"
        + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id"
        + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND"
        + " pg.program_id="
        + etvProgram
        + " AND ps.state="
        + patientState
        + " AND ps.start_date IS NOT NULL AND ps.end_date IS NULL AND"
        + " ps.start_date BETWEEN :startDate AND :endDate AND location_id=:location";
  }
}
