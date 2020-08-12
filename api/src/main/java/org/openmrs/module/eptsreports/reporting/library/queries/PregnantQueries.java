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
      final int pregnantConcept,
      final int gestationConcept,
      final int weeksPregnantConcept,
      final int eddConcept,
      final int adultInitailEncounter,
      final int adultSegEncounter,
      final int etvProgram) {

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
        + " and pp.voided=0 and pp.date_enrolled between :startDate and :endDate and pp.location_id=:location ";
  }

  public static final String findPatientsWhoArePregnantInAPeriod() {

    final String query =
        "select patient_id from ( select inicio_real.patient_id,gravida_real.data_gravida, lactante_real.data_parto,"
            + "if(max(gravida_real.data_gravida) is null and max(lactante_real.data_parto) is null,null, "
            + "if(max(gravida_real.data_gravida) is null,1, "
            + "if(max(lactante_real.data_parto) is null,2, "
            + "if(max(lactante_real.data_parto)>max(gravida_real.data_gravida),1,2)))) decisao from ("
            + "select p.patient_id "
            + "from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "where e.voided=0 and p.voided=0 and e.encounter_type in (5,7) and e.encounter_datetime<=:endDate and e.location_id = :location "
            + "union "
            + "select pg.patient_id from patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "where pg.voided=0 and p.voided=0 and program_id in (1,2) and date_enrolled<=:endDate and location_id=:location "
            + "union "
            + "Select p.patient_id from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23891 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location )inicio_real "
            + "left join ( "
            + "Select p.patient_id,e.encounter_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1982 and value_coded=1065 and e.encounter_type in (5,6) and e.encounter_datetime  between :startDate and :endDate and e.location_id=:location "
            + "union Select p.patient_id,e.encounter_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1279 and e.encounter_type in (5,6) and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
            + "union "
            + "Select 	p.patient_id,e.encounter_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1600 and e.encounter_type in (5,6) and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
            + "union "
            + "Select p.patient_id,e.encounter_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6334 and value_coded=6331 and e.encounter_type in (5,6) and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
            + "union "
            + "select pp.patient_id,pp.date_enrolled data_gravida from patient_program pp "
            + "where pp.program_id=8 and pp.voided=0 and pp.date_enrolled between :startDate and :endDate and pp.location_id=:location "
            + "union "
            + "Select p.patient_id,obsART.value_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "inner join obs obsART on e.encounter_id=obsART.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=1982 and o.value_coded=1065 and "
            + "e.encounter_type=53 and obsART.value_datetime between :startDate and :endDate and e.location_id=:location and obsART.concept_id=1190 and obsART.voided=0 "
            + "union "
            + "Select p.patient_id,o.value_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=1465 and e.encounter_type=6 and o.value_datetime between :startDate and :endDate and e.location_id=:location "
            + ") gravida_real on gravida_real.patient_id=inicio_real.patient_id "
            + "left join ( "
            + "Select p.patient_id,o.value_datetime data_parto from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=5599 and "
            + "e.encounter_type in (5,6) and o.value_datetime between :startDate and :endDate and e.location_id=:location "
            + "union "
            + "Select p.patient_id, e.encounter_datetime data_parto from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6332 and value_coded=1065 and e.encounter_type=6 and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
            + "union "
            + "Select p.patient_id, obsART.value_datetime data_parto from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "inner join obs obsART on e.encounter_id=obsART.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=6332 and o.value_coded=1065 and e.encounter_type=53 and e.location_id=:location and "
            + "obsART.value_datetime between :startDate and :endDate and obsART.concept_id=1190 and obsART.voided=0 "
            + "union "
            + "Select p.patient_id, e.encounter_datetime data_parto from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6334 and value_coded=6332 and "
            + "e.encounter_type in (5,6) and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
            + "union "
            + "select pg.patient_id,ps.start_date data_parto from patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=8 and ps.state=27 and ps.end_date is null and ps.start_date between :startDate and :endDate and location_id=:location "
            + ") lactante_real on lactante_real.patient_id=inicio_real.patient_id "
            + "where lactante_real.data_parto is not null or gravida_real.data_gravida is not null "
            + "group by inicio_real.patient_id ) gravidaLactante "
            + "inner join person pe on pe.person_id=gravidaLactante.patient_id "
            + "where decisao=2 and pe.voided=0 and pe.gender='F' ";

    return query;
  }

  public static final String findPatientsWhoArePregnantInAPeriodNotCheckingBreastfeeding() {

    final String query =
        "Select gravida_real.patient_id from ( "
            + "select p.patient_id,e.encounter_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1982 and value_coded=1065 "
            + "and e.encounter_type in (5,6) and e.encounter_datetime  between :startDate and :endDate and e.location_id=:location "
            + "union "
            + "select p.patient_id,e.encounter_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1279 and e.encounter_type in (5,6) "
            + "and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
            + "union "
            + "select p.patient_id,e.encounter_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1600 and e.encounter_type in (5,6) and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
            + "union "
            + "select p.patient_id,e.encounter_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6334 and value_coded=6331 "
            + "and e.encounter_type in (5,6) and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
            + "union "
            + "select pp.patient_id,pp.date_enrolled data_gravida from patient_program pp "
            + "where pp.program_id=8 and pp.voided=0 and pp.date_enrolled between :startDate and :endDate and pp.location_id=:location "
            + "union "
            + "select p.patient_id,obsART.value_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "inner join obs obsART on e.encounter_id=obsART.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=1982 and o.value_coded=1065 and "
            + "e.encounter_type=53 and obsART.value_datetime between :startDate and :endDate and e.location_id=:location and obsART.concept_id=1190 and obsART.voided=0 "
            + "union "
            + "Select p.patient_id,o.value_datetime data_gravida from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=1465 and e.encounter_type=6 and o.value_datetime between :startDate and :endDate and e.location_id=:location "
            + ") gravida_real "
            + "inner join person pe on pe.person_id=gravida_real.patient_id "
            + "where pe.voided=0 and pe.gender='F' ";
    return query;
  }
}
