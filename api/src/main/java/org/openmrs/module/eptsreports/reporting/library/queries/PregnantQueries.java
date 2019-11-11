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

/** Re usable queries that can be used for finding patients who are pregnant */
public class PregnantQueries {

  /** GRAVIDAS INSCRITAS NO SERVIÇO TARV */
  public static String getPregnantWhileOnArt(
      int pregnantConcept,
      int gestationConcept,
      int weeksPregnantConcept,
      int eddConcept,
      int adultInitailEncounter,
      int adultSegEncounter,
      int fichaResumo,
      int lastMenstrualPeriod,
      int etvProgram) {

    return "Select     p.patient_id"
        + " from patient p"
        + " inner join person pe on p.patient_id=pe.person_id"
        + " inner join encounter e on p.patient_id=e.patient_id"
        + " inner join obs o on e.encounter_id=o.encounter_id"
        + " where p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
        + pregnantConcept
        + " and value_coded="
        + gestationConcept
        + " and e.encounter_type in ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
        + " union"
        + " Select p.patient_id"
        + " from patient p"
        + " inner join person pe on p.patient_id=pe.person_id"
        + " inner join encounter e on p.patient_id=e.patient_id"
        + " inner join obs o on e.encounter_id=o.encounter_id"
        + " where p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
        + weeksPregnantConcept
        + " and"
        + " e.encounter_type in ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
        + " union"
        + " Select p.patient_id"
        + " from patient p"
        + " inner join person pe on p.patient_id=pe.person_id"
        + " inner join encounter e on p.patient_id=e.patient_id"
        + " inner join obs o on e.encounter_id=o.encounter_id"
        + " where p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
        + eddConcept
        + " and"
        + " e.encounter_type in ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
        + " union"
        + " select pp.patient_id from patient_program pp"
        + " inner join person pe on pp.patient_id=pe.person_id"
        + " where pp.program_id="
        + etvProgram
        + " and pp.voided=0 and pp.date_enrolled between :startDate and :endDate and pp.location_id=:location "
            + "union "
            + " SELECT p.patient_id FROM patient p "
            + " inner join person pe on p.patient_id=pe.person_id "
            + " inner join encounter e on p.patient_id=e.patient_id "
            + " inner join obs o on e.encounter_id=o.encounter_id "
            + "  where p.voided=0 and e.voided=0 and o.voided=0 and concept_id= "
            + lastMenstrualPeriod + " AND e.encounter_type_id IN ( "
            + fichaResumo + ") "
            + " AND o.value_datetime BETWEEN :startDate AND :endDate ";
  }
}
