/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

/** @author Stélio Moiane */
public interface TbQueries {

  class QUERY {

    public static final String findPatientsWhoAreInTbTreatment =
        "select tb.patient_id from ( select inicioTB.patient_id from ( "
            + "select p.patient_id,min(o.value_datetime) data_inicio_tb from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (6,9) and e.voided=0 and o.voided=0 and p.voided=0 and o.concept_id=1113 and e.location_id=:location "
            + "and  o.value_datetime  between (:endDate - INTERVAL 6 MONTH ) and :endDate "
            + "group by p.patient_id "
            + ") inicioTB "
            + "left join ( "
            + "select p.patient_id,max(o.value_datetime) data_fim_tb  from 	patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (6,9) and e.voided=0 and o.voided=0 and p.voided=0 and o.concept_id=6120 and e.location_id=:location "
            + "and o.value_datetime between (:endDate - INTERVAL 6 MONTH ) and :endDate "
            + "group by p.patient_id "
            + ") fim_tb "
            + "on inicioTB.patient_id=fim_tb.patient_id and inicioTB.data_inicio_tb<fim_tb.data_fim_tb "
            + "where fim_tb.patient_id is null "
            + "union "
            + "select p.patient_id from patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "where pg.program_id=5 and pg.voided=0 and p.voided=0 and pg.date_enrolled between (:endDate - INTERVAL 6 MONTH) and :endDate "
            + "and pg.location_id=:location and (pg.date_completed is null or (pg.date_completed is not null and pg.date_completed> :endDate)) "
            + "union "
            + "select max_tb.patient_id from ( "
            + "select p.patient_id,max(o.obs_datetime) max_datatb from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type in (6,9) and e.voided=0 and o.voided=0 and p.voided=0 and o.concept_id=1268 and e.location_id=:location "
            + "and o.obs_datetime between (:endDate - INTERVAL 6 MONTH) and :endDate "
            + "group by p.patient_id "
            + ") max_tb "
            + "inner join obs on obs.person_id=max_tb.patient_id and max_tb.max_datatb=obs.obs_datetime "
            + "where obs.concept_id=1268 and obs.value_coded in (1256,1257) and obs.voided=0 and obs.location_id=:location "
            + "union "
            + "select maxdiagnostico.patient_id from ( "
            + "select p.patient_id,max(e.encounter_datetime) max_datadiagnostico from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.encounter_type=6 and e.voided=0 and o.voided=0 and p.voided=0 and o.concept_id=23761 "
            + "and e.encounter_datetime between (:endDate - INTERVAL 6 MONTH ) and :endDate and e.location_id=:location "
            + "group by p.patient_id "
            + ") maxdiagnostico "
            + "inner join obs o on o.person_id=maxdiagnostico.patient_id "
            + "where o.voided=0 and o.concept_id=23761 and o.obs_datetime=maxdiagnostico.max_datadiagnostico and "
            + "o.value_coded=1065 and o.location_id=:location "
            + "union "
            + "select p.patient_id from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id where e.encounter_type=53 and e.voided=0 and o.voided=0 and p.voided=0 and o.concept_id=1113 "
            + "and o.value_datetime between (:endDate - INTERVAL 6 MONTH) and :endDate "
            + "and e.location_id=:location "
            + ") tb";
  }
}
