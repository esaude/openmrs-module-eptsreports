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

public class TxRttQueries {

  /**
   * For each occurred consultation (Ficha de Seguimento Adulto, Pediatria or Ficha Clinica) of
   * Encounter Type Ids 6 or 9 during the reporting period, the occurred encounter date minus the
   * previous scheduled consultation date (Concept ID 1410 value_datetime) from the previous
   * consultation of Encounter Type Ids 6 or 9, is greater than 28 days
   */
  public static String getpatientsHavingConsultation(int adultoSeg, int padiatSeg, int conceptid) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON o.encounter_id=e.encounter_id WHERE p.voide=0 AND e.voide=0 AND o.voided=0 AND e.encounter_type IN(%d, %d)"
            + " AND o.concept_id=%d";

    return String.format(query, adultoSeg, padiatSeg, conceptid);
  }
}
