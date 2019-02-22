/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.queries;

public class Eri2MonthsQueries {

  /**
   * C TODO: merge with allPatientsWhoInitiatedTreatmentDuringReportingPeriod and harmonizez with
   * txNew union query
   *
   * @param arvPharmaciaEncounter
   * @param arvAdultoSeguimentoEncounter
   * @param arvPediatriaSeguimentoEncounter
   * @param arvPlanConcept
   * @param startDrugsConcept
   * @param historicalDrugsConcept
   * @param artProgram
   * @return
   */
  public static String getAllPatientsWhoReturnedFor2ndConsultationOR2ndDrugsPickUpWithin33Days(
      int arvPharmaciaEncounter,
      int arvAdultoSeguimentoEncounter,
      int arvPediatriaSeguimentoEncounter,
      int arvPlanConcept,
      int startDrugsConcept,
      int historicalDrugsConcept,
      int artProgram) {
    return "SELECT inicio_real.patient_id "
        + "FROM "
        + "(SELECT patient_id,data_inicio "
        + "FROM "
        + "(SELECT patient_id,MIN(data_inicio) data_inicio "
        + "FROM "
        + "(SELECT p.patient_id,MIN(e.encounter_datetime) data_inicio "
        + "FROM patient p "
        + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + "INNER JOIN obs o ON o.encounter_id=e.encounter_id "
        + "WHERE e.voided=0 AND o.voided=0 AND p.voided=0 AND "
        + "e.encounter_type IN ("
        + arvPharmaciaEncounter
        + ","
        + arvAdultoSeguimentoEncounter
        + ","
        + arvPediatriaSeguimentoEncounter
        + ") AND o.concept_id="
        + arvPlanConcept
        + " AND o.value_coded="
        + startDrugsConcept
        + " AND "
        + "e.encounter_datetime<=:endDate AND e.location_id=:location "
        + "GROUP BY p.patient_id "
        + "UNION "
        + "SELECT p.patient_id,MIN(value_datetime) data_inicio "
        + "FROM patient p "
        + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
        + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN ("
        + arvPharmaciaEncounter
        + ","
        + arvAdultoSeguimentoEncounter
        + ","
        + arvPediatriaSeguimentoEncounter
        + ") AND o.concept_id="
        + arvPlanConcept
        + " AND o.value_coded="
        + startDrugsConcept
        + " AND "
        + "o.concept_id="
        + historicalDrugsConcept
        + " AND o.value_datetime IS NOT NULL AND "
        + "o.value_datetime<=:endDate AND e.location_id=:location "
        + "GROUP BY p.patient_id "
        + "UNION "
        + "SELECT pg.patient_id,date_enrolled data_inicio "
        + "FROM patient p INNER JOIN patient_program pg on p.patient_id=pg.patient_id "
        + "WHERE pg.voided=0 AND p.voided=0 AND program_id="
        + artProgram
        + " AND date_enrolled<=:endDate AND location_id=:location "
        + "UNION "
        + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio "
        + "FROM patient p "
        + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + "WHERE p.voided=0 AND e.encounter_type="
        + arvPharmaciaEncounter
        + " AND e.voided=0 and e.encounter_datetime<=:endDate AND e.location_id=:location "
        + "GROUP BY p.patient_id "
        + ") inicio "
        + "GROUP BY patient_id "
        + ")inicio1 "
        + "WHERE data_inicio BETWEEN :startDate AND :endDate "
        + ") inicio_real "
        + "INNER JOIN encounter e ON e.patient_id=inicio_real.patient_id "
        + "WHERE e.voided=0 AND e.encounter_type IN ("
        + arvPharmaciaEncounter
        + ","
        + arvAdultoSeguimentoEncounter
        + ","
        + arvPediatriaSeguimentoEncounter
        + ") AND e.location_id=:location AND "
        + "e.encounter_datetime BETWEEN inicio_real.data_inicio AND date_add(inicio_real.data_inicio, interval 33 day) "
        + "GROUP BY inicio_real.patient_id "
        + "HAVING MIN(e.encounter_datetime) < MAX(e.encounter_datetime)";
  }
}
