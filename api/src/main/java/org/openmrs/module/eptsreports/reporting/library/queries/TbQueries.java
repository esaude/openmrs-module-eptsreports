/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

/** @author Stélio Moiane */
public interface TbQueries {

  class QUERY {

    public static final String findPatientsWhoAreInTbTreatment =
        "select inicio_tb.patient_id from "
            + "(select patient_id,max(data_inicio_tb) data_inicio_tb from ( "
            + "select p.patient_id,o.value_datetime data_inicio_tb from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (6,9) and e.voided=0 and o.voided=0 and p.voided=0 and o.concept_id=1113 and e.location_id=:location and  o.value_datetime<=:endDate "
            + "union "
            + "select patient_id,date_enrolled data_inicio_tb from patient_program "
            + "where program_id=5 and voided=0 and date_enrolled<=:endDate and location_id=:location) inicio1 group by patient_id) inicio_tb "
            + "left join ("
            + "select patient_id,max(data_fim_tb) data_fim_tb from ( "
            + "select p.patient_id,o.value_datetime data_fim_tb from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (6,9) and e.voided=0 and o.voided=0 and p.voided=0 and o.concept_id=6120 and e.location_id=:location and o.value_datetime<=:endDate "
            + "union "
            + "select patient_id,date_completed data_fim_tb from patient_program "
            + "where program_id=5 and voided=0 and location_id=:location and date_completed is not null and date_completed<=:endDate) fim1 group by patient_id) "
            + "fim on inicio_tb.patient_id=fim.patient_id  and data_fim_tb>data_inicio_tb "
            + "where data_fim_tb is null "
            + " union "
            + "select max_tb.patient_id from ( "
            + "select p.patient_id,max(o.obs_datetime) max_datatb from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (6,9) and e.voided=0 and o.voided=0 and p.voided=0 and o.concept_id=1268 and e.location_id=:location and o.obs_datetime<=:endDate group by p.patient_id) max_tb "
            + "inner join obs on obs.person_id=max_tb.patient_id and max_tb.max_datatb=obs.obs_datetime "
            + "where obs.concept_id=1268 and obs.value_coded in (1256,1257) and obs.location_id=:location "
            + "union "
            + "select p.patient_id from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type=6 and e.voided=0 and o.voided=0 and p.voided=0 and o.concept_id=23761 and o.value_coded=1065 and e.encounter_datetime between (:endDate - INTERVAL 6 MONTH + INTERVAL 1 DAY) and :endDate and e.location_id=:location group by p.patient_id";
  }
}
