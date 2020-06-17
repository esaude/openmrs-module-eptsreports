package org.openmrs.module.eptsreports.reporting.library.queries;

public interface Eri2MonthsQueriesInterface {
  class QUERY {
    @SuppressWarnings("incomplete-switch")
    public static final String
        findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickup33DaysForASpecificPatientType(
            final Eri4mType eri4mType) {

      String query =
          "SELECT patient_id FROM ( SELECT inicio_real.patient_id,inicio_real.data_inicio,"
              + "IF(MIN(lev_seg.encounter_datetime) < MAX(lev_seg.encounter_datetime),MAX(lev_seg.encounter_datetime),null) last_encounter, "
              + "IF(saida.data_estado is not null and max(lev_seg.encounter_datetime)>saida.data_estado,null,saida.state_id) final_state, "
              + "IF(saida.data_estado is not null and max(lev_seg.encounter_datetime)>saida.data_estado,null,saida.data_estado) final_statedate FROM ( "
              + "SELECT patient_id,data_inicio FROM ( "
              + "Select patient_id,min(data_inicio) data_inicio FROM ( "
              + "SELECT p.patient_id,min(e.encounter_datetime) data_inicio FROM patient p "
              + "INNER JOIN encounter e on p.patient_id=e.patient_id	 "
              + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
              + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 AND e.encounter_datetime<=:endDate and e.location_id=:location "
              + "GROUP BY p.patient_id "
              + "UNION "
              + "SELECT p.patient_id,min(value_datetime) data_inicio FROM patient p "
              + "INNER JOIN encounter e on p.patient_id=e.patient_id "
              + "INNER JOIN obs o on e.encounter_id=o.encounter_id "
              + "WHERE p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) AND o.concept_id=1190 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location "
              + "group by p.patient_id "
              + "UNION "
              + "SELECT pg.patient_id,min(date_enrolled) data_inicio FROM patient p "
              + "INNER JOIN patient_program pg on p.patient_id=pg.patient_id "
              + "WHERE pg.voided=0 and p.voided=0 and program_id=2 AND date_enrolled<=:endDate and location_id=:location "
              + "GROUP BY pg.patient_id "
              + "UNION "
              + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio FROM patient p "
              + "INNER JOIN encounter e on p.patient_id=e.patient_id "
              + "WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location "
              + "GROUP BY p.patient_id "
              + "UNION "
              + "SELECT p.patient_id,min(value_datetime) data_inicio "
              + "FROM patient p "
              + "INNER JOIN encounter e on p.patient_id=e.patient_id "
              + "INNER JOIN obs o on e.encounter_id=o.encounter_id "
              + "WHERE p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location "
              + "group by p.patient_id) inicio "
              + "group by patient_id "
              + ")inicio1 "
              + "WHERE data_inicio between (:endDate - INTERVAL 2 MONTH + INTERVAL 1 DAY) AND (:endDate - INTERVAL  1 MONTH) "
              + ") inicio_real "
              + "LEFT JOIN ( "
              + "select p.patient_id, encounter_datetime "
              + "FROM patient p "
              + "INNER JOIN encounter e on p.patient_id=e.patient_id "
              + "where p.voided=0 and e.voided=0 and  e.encounter_type in (6,9,18) AND e.encounter_datetime between (:endDate - INTERVAL 2 MONTH + INTERVAL 1 DAY) and :endDate and e.location_id=:location "
              + "UNION "
              + "SELECT p.patient_id,value_datetime as encounter_datetime FROM patient p "
              + "INNER JOIN encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 AND o.concept_id=23866 and o.value_datetime is not null and o.value_datetime between  (:endDate - INTERVAL 2 MONTH + INTERVAL 1 DAY) and :endDate and e.location_id=:location "
              + ") lev_seg "
              + "ON lev_seg.patient_id = inicio_real.patient_id and lev_seg.encounter_datetime BETWEEN inicio_real.data_inicio AND (inicio_real.data_inicio + INTERVAL 33 DAY) "
              + "LEFT JOIN ( "
              + "SELECT patient_id,max(data_estado) data_estado,state_id FROM ( "
              + "select pg.patient_id, max(ps.start_date) data_estado, ps.state as state_id from patient p "
              + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
              + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
              + "WHERE pg.voided=0 and ps.voided=0 and p.voided=0 AND pg.program_id=2 AND ps.state in (7,8,10) AND ps.end_date is null AND ps.start_date between  (:endDate - INTERVAL 2 MONTH  + INTERVAL 1 DAY) and :endDate and location_id=:location "
              + "group by pg.patient_id "
              + "UNION "
              + "select p.patient_id, max(o.obs_datetime) data_estado, IF(o.value_coded=1706,7,if(o.value_coded=1366,10,8)) as state_id FROM 	patient p "
              + "INNER JOIN encounter e on p.patient_id=e.patient_id "
              + "INNER JOIN obs  o on e.encounter_id=o.encounter_id "
              + "WHERE e.voided=0 and o.voided=0 AND p.voided=0 AND e.encounter_type in (53,6) and "
              + "o.concept_id in (6272,6273) and o.value_coded in (1706,1366,1709) AND o.obs_datetime between  (:endDate - INTERVAL 2 MONTH  + INTERVAL 1 DAY) and :endDate and e.location_id=:location "
              + "GROUP BY p.patient_id "
              + "UNION "
              + "select person_id as patient_id,death_date as data_estado,10 as state_id from person "
              + "where dead=1 and death_date is not null and death_date between (:endDate - INTERVAL 2 MONTH  + INTERVAL 1 DAY) and :endDate "
              + "UNION "
              + "SELECT p.patient_id, max(obsObito.obs_datetime) data_estado,10 as state_id from 	patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs obsObito on e.encounter_id=obsObito.encounter_id "
              + "WHERE e.voided=0 and p.voided=0 and obsObito.voided=0 AND e.encounter_type in (21,36,37) AND e.encounter_datetime between (:endDate - INTERVAL 5 MONTH  + INTERVAL 1 DAY) and :endDate and e.location_id=:location AND obsObito.concept_id in (2031,23944,23945) and obsObito.value_coded=1366 "
              + "GROUP BY p.patient_id "
              + ") allSaida "
              + "group by patient_id "
              + ") saida ON inicio_real.patient_id=saida.patient_id and saida.data_estado BETWEEN inicio_real.data_inicio and :endDate "
              + "LEFT JOIN ( "
              + "select pg.patient_id FROM patient p "
              + "INNER JOIN patient_program pg on p.patient_id=pg.patient_id "
              + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
              + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=2 and ps.state=29 and "
              + "ps.start_date <=:endDate and pg.location_id=:location "
              + "UNION "
              + "SELECT p.patient_id from patient p "
              + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
              + "INNER JOIN obs obsTrans ON e.encounter_id=obsTrans.encounter_id AND obsTrans.voided=0 AND obsTrans.concept_id=1369 AND obsTrans.value_coded=1065 "
              + "INNER JOIN obs obsTarv ON e.encounter_id=obsTarv.encounter_id AND obsTarv.voided=0 AND obsTarv.concept_id=6300 AND obsTarv.value_coded=6276 "
              + "INNER JOIN obs obsData ON e.encounter_id=obsData.encounter_id AND obsData.voided=0 AND obsData.concept_id=23891 "
              + "WHERE p.voided=0 AND e.voided=0 AND e.encounter_type=53 AND "
              + "obsData.value_datetime <=:endDate AND e.location_id=:location "
              + ") transferedIn on inicio_real.patient_id=transferedIn.patient_id "
              + "WHERE transferedIn.patient_id is null "
              + "group by inicio_real.patient_id "
              + ") allPatient2Month ";

      switch (eri4mType) {
        case DEFAULTER:
          query = query + " WHERE last_encounter IS NULL AND final_state IS NULL";
          break;

        case IN_TREATMENT:
          query = query + " WHERE last_encounter IS NOT NULL AND final_state IS NULL";
          break;

        case DEAD:
          query = query + " WHERE final_state = 10";
          break;

        case TRANFERED_OUT:
          query = query + " WHERE final_state = 7";
          break;

        case SPTOPPED_TREATMENT:
          query = query + " WHERE final_state = 8";
          break;
      }

      return query;
    }
  }
}
