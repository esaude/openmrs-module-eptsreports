package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.eptsreports.reporting.library.queries.TxPvlsQueriesInterface.QUERY.WomanState;

public interface TxPvlsBySourceQueriesInterface {

  class QUERY {

    public enum SourceType {
      LAB_FSR,
      MASTERCARD;
    }

    public static final String
        findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
            SourceType sourceType) {

      String query =
          "select inicio_real.patient_id from (select patient_id,min(data_inicio) data_inicio from ("
              + "select p.patient_id,min(e.encounter_datetime) data_inicio from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id "
              + "where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and  e.encounter_datetime<=:endDate and e.location_id=:location group by p.patient_id "
              + "union "
              + "Select p.patient_id,min(value_datetime) data_inicio from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and o.concept_id=1190 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id "
              + "union "
              + "select pg.patient_id,min(date_enrolled) data_inicio from patient p "
              + "inner join patient_program pg on p.patient_id=pg.patient_id "
              + "where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location group by pg.patient_id "
              + "union "
              + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio  FROM patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location GROUP BY 	p.patient_id "
              + "union "
              + "Select p.patient_id,min(value_datetime) data_inicio from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  o.concept_id=23866 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id) inicio group by patient_id "
              + ") inicio_real "
              + "inner join ( "
              + "Select p.patient_id,max(o.obs_datetime) data_carga from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and "
              + "e.encounter_type in (%s) and  o.concept_id in (856,1305) and  o.obs_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location group by p.patient_id) carga_viral on inicio_real.patient_id=carga_viral.patient_id "
              + "where carga_viral.data_carga>=date_add(inicio_real.data_inicio, interval 90 DAY)    ";

      switch (sourceType) {
        case LAB_FSR:
          query = String.format(query, StringUtils.join(Arrays.asList(13, 51), ","));
          break;

        case MASTERCARD:
          query = String.format(query, StringUtils.join(Arrays.asList(6, 9, 53), ","));
          break;
      }
      return query;
    }

    public static final String
        findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
            SourceType sourceType) {

      String query =
          "select inicio_real.patient_id from( "
              + "Select patient_id,min(data_inicio) data_inicio from ( "
              + "Select p.patient_id,min(e.encounter_datetime) data_inicio from patient p  "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on o.encounter_id=e.encounter_id "
              + "where e.voided=0 and o.voided=0 and p.voided=0 and  e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and  e.encounter_datetime<=:endDate and e.location_id=:location group by p.patient_id "
              + "union "
              + "Select p.patient_id,min(value_datetime) data_inicio from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and  o.concept_id=1190 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id "
              + "union "
              + "select pg.patient_id,min(date_enrolled) data_inicio from patient p inner join patient_program pg on p.patient_id=pg.patient_id "
              + "where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location group by pg.patient_id "
              + "union  "
              + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio  FROM 	patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location GROUP BY 	p.patient_id "
              + "union "
              + "Select p.patient_id,min(value_datetime) data_inicio from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  o.concept_id=23866 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id) inicio group by patient_id)inicio_real "
              + "inner join ( "
              + "Select p.patient_id,max(o.obs_datetime) data_carga from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (%s) and  o.concept_id in (856,1305) and  o.obs_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location group by p.patient_id) carga_viral on inicio_real.patient_id=carga_viral.patient_id "
              + "inner join obs obsRazao on obsRazao.person_id=carga_viral.patient_id "
              + "where carga_viral.data_carga>=date_add(inicio_real.data_inicio, interval 90 DAY) and  obsRazao.obs_datetime=carga_viral.data_carga and  obsRazao.concept_id=23818 and obsRazao.value_coded in (843,23864,23881,23882) and obsRazao.voided=0 ";

      switch (sourceType) {
        case LAB_FSR:
          query = String.format(query, StringUtils.join(Arrays.asList(13, 51), ","));
          break;

        case MASTERCARD:
          query = String.format(query, StringUtils.join(Arrays.asList(6, 9, 53), ","));
          break;
      }
      return query;
    }

    public static final String
        findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(
            SourceType sourceType) {

      String query =
          "select inicio_real.patient_id  from ( "
              + "select patient_id,min(data_inicio) data_inicio from ( "
              + "select p.patient_id,min(e.encounter_datetime) data_inicio from patient p  "
              + "inner join encounter e on p.patient_id=e.patient_id	"
              + "inner join obs o on o.encounter_id=e.encounter_id "
              + "where e.voided=0 and o.voided=0 and p.voided=0 and  e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and  e.encounter_datetime<=:endDate and e.location_id=:location group by p.patient_id "
              + "union "
              + "Select p.patient_id,min(value_datetime) data_inicio from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and o.concept_id=1190 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id "
              + "union "
              + "select pg.patient_id,min(date_enrolled) data_inicio from patient p "
              + "inner join patient_program pg on p.patient_id=pg.patient_id "
              + "where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location group by pg.patient_id "
              + "union "
              + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio  FROM patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location GROUP BY p.patient_id "
              + "union "
              + "Select p.patient_id,min(value_datetime) data_inicio from 	patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  o.concept_id=23866 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id) inicio group by patient_id) inicio_real "
              + "inner join ( "
              + "select ultima_carga.patient_id,ultima_carga.data_carga,obs.value_numeric valor_carga,obs.concept_id,obs.value_coded from ( "
              + "Select p.patient_id,max(o.obs_datetime) data_carga from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (%s) and  o.concept_id in (856,1305) and  o.obs_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location group by p.patient_id) ultima_carga "
              + "inner join obs on obs.person_id=ultima_carga.patient_id and obs.obs_datetime=ultima_carga.data_carga "
              + "where obs.voided=0 and ((obs.concept_id=856 and obs.value_numeric<1000) or (obs.concept_id=1305 and obs.value_coded in (1306,23814,23905,23906,23907,23908,23904)))  and obs.location_id=:location) carga_viral on inicio_real.patient_id=carga_viral.patient_id "
              + "where carga_viral.data_carga>=date_add(inicio_real.data_inicio, interval 90 DAY) ";
      switch (sourceType) {
        case LAB_FSR:
          query = String.format(query, StringUtils.join(Arrays.asList(13, 51), ","));
          break;

        case MASTERCARD:
          query = String.format(query, StringUtils.join(Arrays.asList(6, 9, 53), ","));
          break;
      }
      return query;
    }

    public static final String
        findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(
            SourceType sourceType) {

      String query =
          "select  inicio_real.patient_id from ( "
              + "Select patient_id,min(data_inicio) data_inicio from ( "
              + "Select p.patient_id,min(e.encounter_datetime) data_inicio from patient p  "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on o.encounter_id=e.encounter_id "
              + "where e.voided=0 and o.voided=0 and p.voided=0 and  e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and  e.encounter_datetime<=:endDate and e.location_id=:location group by p.patient_id "
              + "union "
              + "Select p.patient_id,min(value_datetime) data_inicio from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and  o.concept_id=1190 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id "
              + "union "
              + "select pg.patient_id,min(date_enrolled) data_inicio from patient p inner join patient_program pg on p.patient_id=pg.patient_id where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location group by pg.patient_id "
              + "union "
              + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio  FROM patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "WHERE	p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location GROUP BY p.patient_id "
              + "union "
              + "Select p.patient_id,min(value_datetime) data_inicio from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  o.concept_id=23866 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id) inicio group by patient_id)inicio_real "
              + "inner join( "
              + "Select p.patient_id,max(o.obs_datetime) data_carga from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (%s) and  o.concept_id in (856,1305) and  o.obs_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location group by p.patient_id) carga_viral on inicio_real.patient_id=carga_viral.patient_id "
              + "inner join obs obsRazao on obsRazao.person_id=carga_viral.patient_id and obsRazao.obs_datetime=carga_viral.data_carga "
              + "inner join obs obscv on obscv.person_id=carga_viral.patient_id and obscv.obs_datetime=carga_viral.data_carga "
              + "where carga_viral.data_carga>=date_add(inicio_real.data_inicio, interval 90 DAY) and  obsRazao.concept_id=23818 and obsRazao.value_coded in (843,23864,23881,23882) and obsRazao.voided=0 and ((obscv.concept_id=856 and obscv.value_numeric<1000) or (obscv.concept_id=1305 and obscv.value_coded in (1306,23814,23905,23906,23907,23908,23904))) and obscv.voided=0";

      switch (sourceType) {
        case LAB_FSR:
          query = String.format(query, StringUtils.join(Arrays.asList(13, 51), ","));
          break;

        case MASTERCARD:
          query = String.format(query, StringUtils.join(Arrays.asList(6, 9, 53), ","));
          break;
      }
      return query;
    }

    public static final String
        findWomanStateWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
            SourceType sourceType, WomanState womanState) {

      String query =
          "																																																			"
              + " select patient_id                                                                                                                                                                                                                                                          	"
              + " from (                                                                                                                                                                                                                                                                     	"
              + " 		select inicio_real.patient_id, inicio_real.data_inicio,carga_viral.data_carga, gravida_real.data_gravida, lactante_real.data_parto,                                                                                                                                	"
              + " 			if(gravida_real.data_gravida is null and lactante_real.data_parto is null, null, if(gravida_real.data_gravida is null, 1, if(lactante_real.data_parto is null, 2, if(max(lactante_real.data_parto)>max(gravida_real.data_gravida),1,2)))) decisao              	"
              + "         	from (                                                                                                                                                                                                                                                         	"
              + "         			select patient_id,min(data_inicio) data_inicio                                                                                                                                                                                                         	"
              + "         			from (                                                                                                                                                                                                                                                  "
              + "                			select p.patient_id,min(e.encounter_datetime) data_inicio from patient p                                                                                                                                                                            "
              + "		                		inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "		                		inner join obs o on o.encounter_id=e.encounter_id                                                                                                                                                                                               "
              + "		                	where e.voided=0 and o.voided=0 and p.voided=0 and  e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and  e.encounter_datetime<=:endDate and e.location_id=:location group by p.patient_id                                 "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select p.patient_id,min(value_datetime) data_inicio from patient p                                                                                                                                                                                  "
              + "			               	inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                                 "
              + "			                	inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "                			where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and  o.concept_id=1190 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id                        "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select pg.patient_id,min(date_enrolled) data_inicio from patient p                                                                                                                                                                                  "
              + "                				inner join patient_program pg on p.patient_id=pg.patient_id                                                                                                                                                                                     "
              + "                			where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location group by pg.patient_id                                                                                                                      "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select e.patient_id, min(e.encounter_datetime) AS data_inicio from patient p                                                                                                                                                                        "
              + "                				inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "                			where p.voided=0 and e.encounter_type=18 and e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location group by p.patient_id                                                                                                        "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select p.patient_id,min(value_datetime) data_inicio from patient p                                                                                                                                                                                  "
              + "			               	inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                                 "
              + "			                	inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "			               where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  o.concept_id=23866 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id) inicio group by patient_id        "
              + "			   		)	                                                                                                                                                                                                                                                        "
              + "			inicio_real                                                                                                                                                                                                                                                         "
              + "               	inner join                                                                                                                                                                                                                                                  "
              + "               		(	select p.patient_id,max(o.obs_datetime) data_carga from patient p                                                                                                                                                                                   "
              + "                				inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "                				inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "                			where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (%s) and  o.concept_id in (856,1305)                                                                                                                               "
              + "                				and o.obs_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location group by p.patient_id                                                                                      "
              + "                		)                                                                                                                                                                                                                                                       "
              + "               carga_viral on inicio_real.patient_id=carga_viral.patient_id                                                                                                                                                                                                  "
              + "                	left join                                                                                                                                                                                                                                                   "
              + "                		( 	select p.patient_id,e.encounter_datetime data_gravida from patient p                                                                                                                                                                                "
              + "                				inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "                				inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "                			where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1982 and value_coded=1065 and e.encounter_type in (5,6)                                                                                                                               "
              + "                				and e.encounter_datetime  between date_add(:endDate, interval -24 MONTH) and :endDate and e.location_id=:location                                                                                                                               "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select p.patient_id,e.encounter_datetime data_gravida from patient p                                                                                                                                                                                "
              + "                				inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "                				inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "                			where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1279 and  e.encounter_type in (5,6)                                                                                                                                                   "
              + "                				and e.encounter_datetime between date_add(:endDate, interval -24 MONTH) and :endDate and e.location_id=:location                                                                                                                                "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select p.patient_id,e.encounter_datetime data_gravida from patient p                                                                                                                                                                                "
              + "                				inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "                				inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "                			where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1600 and e.encounter_type in (5,6)                                                                                                                                                    "
              + "                				and e.encounter_datetime between date_add(:endDate, interval -24 MONTH) and :endDate and e.location_id=:location                                                                                                                                "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select p.patient_id,e.encounter_datetime data_gravida from patient p                                                                                                                                                                                "
              + "                				inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "                				inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "                			where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6334 and value_coded=6331                                                                                                                                                             "
              + "                				and  e.encounter_type in (5,6) and e.encounter_datetime between date_add(:endDate, interval -24 MONTH) and :endDate and e.location_id=:location                                                                                                 "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select pp.patient_id,pp.date_enrolled data_gravida from patient_program pp                                                                                                                                                                          "
              + "                			where pp.program_id=8 and pp.voided=0 and  pp.date_enrolled between date_add(:endDate, interval -24 MONTH) and :endDate and pp.location_id=:location                                                                                                "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select p.patient_id,obsART.value_datetime data_gravida from patient p                                                                                                                                                                               "
              + "                				inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "                				inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "                				inner join obs obsART on e.encounter_id=obsART.encounter_id                                                                                                                                                                                     "
              + "                			where p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=1982 and o.value_coded=1065 and  e.encounter_type=53                                                                                                                                "
              + "                				and obsART.value_datetime between date_add(:endDate, interval -24 MONTH) and :endDate and e.location_id=:location and  obsART.concept_id=1190 and obsART.voided=0                                                                               "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select p.patient_id,data_colheita.value_datetime data_gravida from patient p                                                                                                                                                                        "
              + "							inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                                 "
              + "							inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                                   "
              + "							inner join obs data_colheita on data_colheita.encounter_id = e.encounter_id                                                                                                                                                                         "
              + "						where p.voided=0 and e.voided=0 and o.voided=0 and data_colheita.voided = 0 and o.concept_id=1982 and o.value_coded = 1065 and  e.encounter_type=51                                                                                                     "
              + "						  and data_colheita.concept_id =23821 and data_colheita.value_datetime between date_add(:endDate, interval -24 MONTH) and :endDate and e.location_id=:location                                                                                          "
              + "                		)                                                                                                                                                                                                                                                       "
              + "                gravida_real on gravida_real.patient_id=carga_viral.patient_id                                                                                                                                                                                               "
              + "                	and carga_viral.data_carga >= gravida_real.data_gravida and gravida_real.data_gravida between date_add(carga_viral.data_carga, interval -9 MONTH) and carga_viral.data_carga                                                                                "
              + "                	left join                                                                                                                                                                                                                                                   "
              + "                		( 	select p.patient_id,o.value_datetime data_parto from patient p                                                                                                                                                                                      "
              + "                				inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "                				inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "                			where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=5599 and  e.encounter_type in (5,6) and o.value_datetime between date_add(:endDate, interval -36 MONTH) and :endDate and e.location_id=:location                                      "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select p.patient_id, e.encounter_datetime data_parto from patient p                                                                                                                                                                                 "
              + "                				inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "                				inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "               	 		where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6332 and value_coded=1065 and  e.encounter_type=6 and e.encounter_datetime between date_add(:endDate, interval -36 MONTH) and :endDate and e.location_id=:location                    "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select p.patient_id, obsART.value_datetime data_parto from patient p                                                                                                                                                                                "
              + "                				inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "                				inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "                				inner join obs obsART on e.encounter_id=obsART.encounter_id                                                                                                                                                                                     "
              + "                			where p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=6332 and o.value_coded=1065 and  e.encounter_type=53 and e.location_id=:location and obsART.value_datetime between date_add(:endDate, interval -36 MONTH) and :endDate and  obsART.concept_id=1190 and obsART.voided=0      "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select p.patient_id, e.encounter_datetime data_parto from patient p                                                                                                                                                                                 "
              + "                				inner join encounter e on p.patient_id=e.patient_id                                                                                                                                                                                             "
              + "                				inner join obs o on e.encounter_id=o.encounter_id                                                                                                                                                                                               "
              + "                			where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6334 and value_coded=6332 and  e.encounter_type in (5,6) and e.encounter_datetime between date_add(:endDate, interval -36 MONTH) and :endDate and e.location_id=:location             "
              + "                			union                                                                                                                                                                                                                                               "
              + "                			select pg.patient_id,ps.start_date data_parto from patient p                                                                                                                                                                                        "
              + "                				inner join patient_program pg on p.patient_id=pg.patient_id                                                                                                                                                                                     "
              + "                				inner join patient_state ps on pg.patient_program_id=ps.patient_program_id                                                                                                                                                                      "
              + "                			where pg.voided=0 and ps.voided=0 and p.voided=0 and  pg.program_id=8 and ps.state=27 and ps.end_date is null and  ps.start_date between date_add(:endDate, interval -36 MONTH) and :endDate and location_id=:location                              "
              + "                		   union																																																												"
              + "                        select p.patient_id,data_colheita.value_datetime data_parto from patient p    																																										"
              + "                				inner join encounter e on p.patient_id=e.patient_id    																																															"
              + "                				inner join obs o on e.encounter_id=o.encounter_id   																																															"
              + "                				inner join obs data_colheita on data_colheita.encounter_id = e.encounter_id    																																									"
              + "                			where p.voided=0 and e.voided=0 and o.voided=0 and data_colheita.voided = 0 and o.concept_id=6332 and o.value_coded = 1065 and  e.encounter_type=51    																								"
              + ")                                                                                                                                                                                                                                                       "
              + "                lactante_real on lactante_real.patient_id = carga_viral.patient_id and carga_viral.data_carga>=lactante_real.data_parto                                                                                                                                      "
              + "                	and lactante_real.data_parto between date_add(carga_viral.data_carga, interval -18 MONTH) and carga_viral.data_carga                                                                                                                                        "
              + "                where carga_viral.data_carga >= date_add(inicio_real.data_inicio, interval 90 DAY)  and (lactante_real.data_parto is not null or gravida_real.data_gravida is not null) group by carga_viral.patient_id                                                     "
              + "		)                                                                                                                                                                                                                                                                       "
              + "	cargaGravidaLactante                                                                                                                                                                                                                                                        "
              + "		inner join person pe on pe.person_id=cargaGravidaLactante.patient_id                                                                                                                                                                                                    "
              + "    where decisao= # and pe.voided=0 and pe.gender='F'                                                                                                                                                                                                                        ";

      switch (sourceType) {
        case LAB_FSR:
          query = String.format(query, StringUtils.join(Arrays.asList(13, 51), ","));
          break;

        case MASTERCARD:
          query = String.format(query, StringUtils.join(Arrays.asList(6, 9, 53), ","));
          break;
      }

      switch (womanState) {
        case PREGNANT:
          query = StringUtils.replaceChars(query, "#", "2");
          break;

        case BREASTFEEDING:
          query = StringUtils.replaceChars(query, "#", "1");
          break;
      }

      return query;
    }
  }
}
