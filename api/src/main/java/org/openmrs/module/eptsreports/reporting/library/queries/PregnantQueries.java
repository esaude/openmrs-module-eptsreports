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

/**
 * Re usable queries that can be used for finding patients who are pregnant
 */
public class PregnantQueries {

    /**
     * Looks for patients indicated PREGNANT in the initial or follow-up
     * consultation between start date and end date
     */
    public static String getPregnantOnInitialOrFollowUpConsulation(int pregnant, int gestation, int adultInEnc, int adultSegEnc) {

        return "SELECT p.patient_id" + " FROM patient p" + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
                        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
                        + " WHERE p.voided=0 and e.voided=0 and o.voided=0 and concept_id=" + pregnant + " AND value_coded=" + gestation
                        + " AND e.encounter_type IN (" + adultInEnc + "," + adultSegEnc + ")"
                        + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location";

    }

    /**
     * Looks for patients with Number of Weeks Pregnant registered in the
     * initial or follow-up consultation
     */
    public static String getWeeksPregnantOnInitialOrFollowUpConsultations(int numOfWeeks, int adultInEnc, int adultSegEnc) {
        return "SELECT p.patient_id" + " FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id"
                        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
                        + " WHERE p.voided=0 and e.voided=0 and o.voided=0 and concept_id=" + numOfWeeks + " AND e.encounter_type IN ("
                        + adultInEnc + "," + adultSegEnc + ")" + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND"
                        + " e.location_id=:location";
    }

    /**
     * Looks for patients with PREGNANCY DUE DATE registered in the initial or
     * follow-up consultation between start date and end date
     */
    public static String getPregnancyDueDateRegistred(int dueDate, int adultInEnc, int adultSegEnc) {
        return "SELECT p.patient_id FROM patient p INNER JOIN encounter"
                        + " e ON p.patient_id=e.patient_id INNER JOIN obs o ON e.encounter_id=o.encounter_id"
                        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=" + dueDate + " AND e.encounter_type IN ("
                        + adultInEnc + "," + adultSegEnc + ")" + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND"
                        + " e.location_id=:location";
    }

    /**
     * Looks for patients enrolled on PTV/ETV program between start date and end
     * date
     */
    public static String getEnrolledInPtvOrEtv(int ptvProgram) {
        return "SELECT pp.patient_id FROM patient_program pp WHERE pp.program_id=" + ptvProgram
                        + " AND pp.voided=0 AND pp.date_enrolled BETWEEN " + ":startDate AND :endDate AND pp.location_id=:location";
    }

    /**
     * GRAVIDAS INSCRITAS NO SERVIÇO TARV
     */
    public static String getPregnantWhileOnArt(int pregnantConcept, int gestationConcept, int weeksPregnantConcept, int eddConcept,
                    int adultInitailEncounter, int adultSegEncounter, int etvProgram) {

        return "Select 	p.patient_id" + " from 	patient p" + " inner join encounter e on p.patient_id=e.patient_id"
                        + " inner join obs o on e.encounter_id=o.encounter_id"
                        + " where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=" + pregnantConcept + " and value_coded="
                        + gestationConcept + " and e.encounter_type in (" + adultInitailEncounter + "," + adultSegEncounter
                        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location" + " union"
                        + " Select 	p.patient_id" + " from 	patient p inner join encounter e on p.patient_id=e.patient_id"
                        + " inner join obs o on e.encounter_id=o.encounter_id"
                        + " where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=" + weeksPregnantConcept + " and"
                        + " e.encounter_type in (" + adultInitailEncounter + "," + adultSegEncounter
                        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location" + " union"
                        + " Select p.patient_id" + " from patient p inner join encounter e on p.patient_id=e.patient_id"
                        + " inner join obs o on e.encounter_id=o.encounter_id"
                        + " where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=" + eddConcept + " and" + " e.encounter_type in ("
                        + adultInitailEncounter + "," + adultSegEncounter
                        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location" + " union"
                        + " select pp.patient_id from patient_program pp" + " where pp.program_id=" + etvProgram
                        + " and pp.voided=0 and pp.date_enrolled between :startDate and :endDate and pp.location_id=:location";

    }

    public static final String PREGNANT_SQL_QUERY = " ( "
                    + "    Select ultima_carga.patient_id,ultima_carga.data_carga,obs.value_numeric valor_carga from "
                    + "        (   Select  p.patient_id,max(o.obs_datetime) data_carga from patient p "
                    + "                    inner join encounter e on p.patient_id=e.patient_id "
                    + "                    inner join obs o on e.encounter_id=o.encounter_id "
                    + "            where   p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (13,6,9) and "
                    + "                    o.concept_id=856 and o.value_numeric is not null and "
                    + "                    e.encounter_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location "
                    + "            group by p.patient_id ) ultima_carga "
                    + "        inner join obs on obs.person_id=ultima_carga.patient_id and obs.obs_datetime=ultima_carga.data_carga "
                    + "    where   obs.voided=0 and obs.concept_id=856 and obs.location_id=:location "
                    + ") carga_viral on inicio_real.patient_id=carga_viral.patient_id inner join ( "
                    + "        Select  p.patient_id, e.encounter_datetime data_gravida from patient p "
                    + "                inner join encounter e on p.patient_id=e.patient_id "
                    + "               inner join obs o on e.encounter_id=o.encounter_id "
                    + "        where   p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1982 and value_coded=44 and "
                    + "               e.encounter_type in (5,6) and e.encounter_datetime<=:endDate and e.location_id=:location "
                    + "        union Select  p.patient_id, e.encounter_datetime data_gravida "
                    + "        from    patient p inner join encounter e on p.patient_id=e.patient_id "
                    + "                inner join obs o on e.encounter_id=o.encounter_id "
                    + "        where   p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1279 and "
                    + "                e.encounter_type in (5,6) and e.encounter_datetime<=:endDate and e.location_id=:location "
                    + "        union   Select  p.patient_id, e.encounter_datetime data_gravida "
                    + "        from    patient p inner join encounter e on p.patient_id=e.patient_id "
                    + "                inner join obs o on e.encounter_id=o.encounter_id "
                    + "        where   p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1600 and "
                    + "                e.encounter_type in (5,6) and e.encounter_datetime<=:endDate and e.location_id=:location "
                    + "            union select  pp.patient_id, pp.date_enrolled data_gravida from patient_program pp "
                    + "        where   pp.program_id=8 and pp.voided=0 and "
                    + "                pp.date_enrolled<=:endDate and pp.location_id=:location "
                    + " ) gravida_real on gravida_real.patient_id=carga_viral.patient_id "
                    + " where carga_viral.data_carga>=date_add(inicio_real.data_inicio, interval 3 MONTH) and "
                    + "        carga_viral.data_carga>gravida_real.data_gravida and "
                    + "        gravida_real.data_gravida between date_add(carga_viral.data_carga, interval -9 MONTH) and carga_viral.data_carga and "
                    + "        carga_viral.patient_id not in ( select carga_viral.patient_id from "
                    + "            (Select patient_id,min(data_inicio) data_inicio from "
                    + "                            (Select p.patient_id,min(e.encounter_datetime) data_inicio "
                    + "                                from    patient p "
                    + "                                       inner join encounter e on p.patient_id=e.patient_id "
                    + "                                        inner join obs o on o.encounter_id=e.encounter_id "
                    + "                                where   e.voided=0 and o.voided=0 and p.voided=0 and "
                    + "                                        e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and "
                    + "                                        e.encounter_datetime<=:endDate and e.location_id=:location "
                    + "                                group by p.patient_id " + "                                union "
                    + "                                Select  p.patient_id,min(value_datetime) data_inicio "
                    + "                                from    patient p "
                    + "                                        inner join encounter e on p.patient_id=e.patient_id "
                    + "                                        inner join obs o on e.encounter_id=o.encounter_id "
                    + "                                where   p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9) and "
                    + "                                        o.concept_id=1190 and o.value_datetime is not null and "
                    + "                                        o.value_datetime<=:endDate and e.location_id=:location "
                    + "                                group by p.patient_id union "
                    + "                                select  pg.patient_id,date_enrolled data_inicio "
                    + "                                from    patient p inner join patient_program pg on p.patient_id=pg.patient_id "
                    + "                                where   pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location "
                    + "                                union "
                    + "                              SELECT    e.patient_id, MIN(e.encounter_datetime) AS data_inicio "
                    + "                              FROM      patient p "
                    + "                                        inner join encounter e on p.patient_id=e.patient_id "
                    + "                              WHERE     p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location "
                    + "                              GROUP BY  p.patient_id ) inicio "
                    + "                        group by patient_id )inicio_real inner join "
                    + "                ( Select ultima_carga.patient_id,ultima_carga.data_carga,obs.value_numeric valor_carga "
                    + "                from ( Select  p.patient_id,max(o.obs_datetime) data_carga "
                    + "                        from    patient p "
                    + "                                inner join encounter e on p.patient_id=e.patient_id "
                    + "                                inner join obs o on e.encounter_id=o.encounter_id "
                    + "                        where   p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (13,6,9) and "
                    + "                                o.concept_id=856 and o.value_numeric is not null and "
                    + "                                e.encounter_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location "
                    + "                        group by p.patient_id ) ultima_carga "
                    + "                    inner join obs on obs.person_id=ultima_carga.patient_id and obs.obs_datetime=ultima_carga.data_carga "
                    + "                where   obs.voided=0 and obs.concept_id=856 and obs.location_id=:location "
                    + "            ) carga_viral on inicio_real.patient_id=carga_viral.patient_id inner join "
                    + "            ( Select  p.patient_id, e.encounter_datetime data_parto " + "                 from patient p "
                    + "                            inner join encounter e on p.patient_id=e.patient_id "
                    + "                            inner join obs o on e.encounter_id=o.encounter_id "
                    + "                    where   p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6332 and value_coded=1065 and "
                    + "                            e.encounter_type in (5,6,9) and e.encounter_datetime <=:endDate and e.location_id=:location "
                    + "                    union " + "                    Select  p.patient_id, e.encounter_datetime data_parto "
                    + "                    from    patient p "
                    + "                            inner join encounter e on p.patient_id=e.patient_id "
                    + "                            inner join obs o on e.encounter_id=o.encounter_id "
                    + "                    where   p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6334 and value_coded=6332 and "
                    + "                            e.encounter_type in (5,6,9) and e.encounter_datetime<=:endDate and e.location_id=:location "
                    + "                    union " + "                    Select  p.patient_id,o.value_datetime data_parto "
                    + "                    from    patient p inner join encounter e on p.patient_id=e.patient_id "
                    + "                            inner join obs o on e.encounter_id=o.encounter_id "
                    + "                    where   p.voided=0 and e.voided=0 and o.voided=0 and concept_id=5599 and "
                    + "                            e.encounter_type in (5,6) and o.value_datetime<=:endDate and e.location_id=:location "
                    + "                    union " + "                    select  pg.patient_id,ps.start_date data_parto from    patient p "
                    + "                            inner join patient_program pg on p.patient_id=pg.patient_id "
                    + "                            inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
                    + "                    where   pg.voided=0 and ps.voided=0 and p.voided=0 and "
                    + "                            pg.program_id=8 and ps.state=27 and ps.end_date is null and "
                    + "                            ps.start_date<=:endDate and location_id=:location "
                    + "                    ) lactante_real on lactante_real.patient_id=carga_viral.patient_id "
                    + "            where   carga_viral.data_carga>=date_add(inicio_real.data_inicio, interval 3 MONTH) and "
                    + "                    carga_viral.data_carga>lactante_real.data_parto and "
                    + "                    lactante_real.data_parto between date_add(carga_viral.data_carga, interval -18 MONTH) and carga_viral.data_carga "
                    + "         ) "; // and carga_viral.valor_carga<1000

    public static final String BREASTFEEDING_SQL = "select carga_viral.patient_id from "
                    + "(    Select patient_id,min(data_inicio) data_inicio " + "      from "
                    + "              (   Select  p.patient_id,min(e.encounter_datetime) data_inicio "
                    + "                  from    patient p  "
                    + "                          inner join encounter e on p.patient_id=e.patient_id  "
                    + "                          inner join obs o on o.encounter_id=e.encounter_id "
                    + "                  where   e.voided=0 and o.voided=0 and p.voided=0 and  "
                    + "                          e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and  "
                    + "                          e.encounter_datetime<=:endDate and e.location_id=:location "
                    + "                  group by p.patient_id union "
                    + "                  Select  p.patient_id,min(value_datetime) data_inicio " + "                  from    patient p "
                    + "                          inner join encounter e on p.patient_id=e.patient_id "
                    + "                          inner join obs o on e.encounter_id=o.encounter_id "
                    + "                  where   p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9) and  "
                    + "                          o.concept_id=1190 and o.value_datetime is not null and  "
                    + "                          o.value_datetime<=:endDate and e.location_id=:location "
                    + "                  group by p.patient_id " + "                  union "
                    + "                  select  pg.patient_id,date_enrolled data_inicio "
                    + "                  from    patient p inner join patient_program pg on p.patient_id=pg.patient_id "
                    + "                  where   pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location "
                    + "                  union " + "                SELECT    e.patient_id, MIN(e.encounter_datetime) AS data_inicio  "
                    + "                FROM      patient p "
                    + "                          inner join encounter e on p.patient_id=e.patient_id "
                    + "                WHERE     p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location "
                    + "                GROUP BY  p.patient_id                 " + "              ) inicio "
                    + "          group by patient_id  " + ")inicio_real  " + "inner join  " + "( "
                    + "  Select ultima_carga.patient_id,ultima_carga.data_carga,obs.value_numeric valor_carga " + "  from "
                    + "      (   Select  p.patient_id,max(o.obs_datetime) data_carga " + "          from    patient p "
                    + "                  inner join encounter e on p.patient_id=e.patient_id "
                    + "                  inner join obs o on e.encounter_id=o.encounter_id "
                    + "          where   p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (13,6,9) and  "
                    + "                  o.concept_id=856 and o.value_numeric is not null and  "
                    + "                  e.encounter_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location "
                    + "          group by p.patient_id " + "      ) ultima_carga "
                    + "      inner join obs on obs.person_id=ultima_carga.patient_id and obs.obs_datetime=ultima_carga.data_carga "
                    + "  where   obs.voided=0 and obs.concept_id=856 and obs.location_id=:location  "
                    + ") carga_viral on inicio_real.patient_id=carga_viral.patient_id " + " inner join  " + "( "
                    + "      Select  p.patient_id, e.encounter_datetime data_parto " + "      from patient p  "
                    + "              inner join encounter e on p.patient_id=e.patient_id "
                    + "              inner join obs o on e.encounter_id=o.encounter_id "
                    + "      where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6332 and value_coded=1065 and  "
                    + "              e.encounter_type in (5,6,9) and e.encounter_datetime <=:endDate and e.location_id=:location "
                    + "      union " + "      Select  p.patient_id, e.encounter_datetime data_parto " + "      from    patient p  "
                    + "              inner join encounter e on p.patient_id=e.patient_id "
                    + "              inner join obs o on e.encounter_id=o.encounter_id "
                    + "      where   p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6334 and value_coded=6332 and  "
                    + "              e.encounter_type in (5,6,9) and e.encounter_datetime<=:endDate and e.location_id=:location "
                    + "      union " + "      Select  p.patient_id,o.value_datetime data_parto "
                    + "      from    patient p inner join encounter e on p.patient_id=e.patient_id "
                    + "              inner join obs o on e.encounter_id=o.encounter_id "
                    + "      where   p.voided=0 and e.voided=0 and o.voided=0 and concept_id=5599 and  "
                    + "              e.encounter_type in (5,6) and o.value_datetime<=:endDate and e.location_id=:location " + "      union "
                    + "      select  pg.patient_id,ps.start_date data_parto " + "      from    patient p  "
                    + "              inner join patient_program pg on p.patient_id=pg.patient_id "
                    + "              inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
                    + "      where   pg.voided=0 and ps.voided=0 and p.voided=0 and  "
                    + "              pg.program_id=8 and ps.state=27 and ps.end_date is null and  "
                    + "              ps.start_date<=:endDate and location_id=:location "
                    + " ) lactante_real on lactante_real.patient_id=carga_viral.patient_id "
                    + " where carga_viral.data_carga>=date_add(inicio_real.data_inicio, interval 3 MONTH) and "
                    + "      carga_viral.data_carga>lactante_real.data_parto and  "
                    + "      lactante_real.data_parto between date_add(carga_viral.data_carga, interval -18 MONTH) and carga_viral.data_carga "; // and
                                                                                                                                                 // carga_viral.valor_carga<1000
                                                                                                                                                 //

}
