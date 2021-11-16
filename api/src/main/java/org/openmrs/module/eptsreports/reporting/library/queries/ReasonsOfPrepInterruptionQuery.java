package org.openmrs.module.eptsreports.reporting.library.queries;

public class ReasonsOfPrepInterruptionQuery {

  public static final String findPatientsWhoInterruptPrep(
      PrepReasonInterruptionType prepReasonInterruptionType) {

    String query =
        "select patient_id from ( "
            + "select * from ( "
            + "select maxint.patient_id, data_consulta, o.value_coded from "
            + "(Select 	p.patient_id,max(o.obs_datetime) data_int, o.value_coded, e.encounter_datetime data_consulta from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=165292 and  e.encounter_type=80 and o.value_coded = 1260 and o.obs_datetime>=:startDate and o.obs_datetime<=:endDate and e.location_id=:location "
            + "group by p.patient_id "
            + ") maxint "
            + "inner join encounter e on e.patient_id=maxint.patient_id and maxint.data_consulta=e.encounter_datetime "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where o.concept_id=165225 and o.voided=0 and e.encounter_type=80 and e.voided=0 and o.value_coded = 1169 and e.location_id=:location "
            + "union "
            + " Select p.patient_id,max(e.encounter_datetime) data_consulta, o.value_coded from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=165225 and  e.encounter_type=81 and e.encounter_datetime>=:startDate and e.encounter_datetime<=:endDate and o.value_coded = 1169 and  e.location_id=:location "
            + "group by p.patient_id "
            + ") interrupcao group by patient_id "
            + "union "
            + " select * from ( "
            + "select maxint.patient_id, data_consulta, o.value_coded from "
            + "(Select 	p.patient_id,max(o.obs_datetime) data_int, o.value_coded, e.encounter_datetime data_consulta from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=165292 and  e.encounter_type=80 and o.value_coded = 1260 and o.obs_datetime>=:startDate and o.obs_datetime<=:endDate and e.location_id=:location "
            + "group by p.patient_id "
            + ") maxint "
            + "inner join encounter e on e.patient_id=maxint.patient_id and maxint.data_consulta=e.encounter_datetime "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where o.concept_id=165225 and o.voided=0 and e.encounter_type=80 and e.voided=0 and o.value_coded = 2015 and e.location_id=:location "
            + "union "
            + " Select p.patient_id,max(e.encounter_datetime) data_consulta, o.value_coded from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=165225 and  e.encounter_type=81 and e.encounter_datetime>=:startDate and e.encounter_datetime<=:endDate and o.value_coded = 2015 and  e.location_id=:location "
            + "group by p.patient_id "
            + ") interrupcao group by patient_id "
            + "union "
            + " select * from ( "
            + "select maxint.patient_id, data_consulta, o.value_coded from "
            + "(Select 	p.patient_id,max(o.obs_datetime) data_int, o.value_coded, e.encounter_datetime data_consulta from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=165292 and  e.encounter_type=80 and o.value_coded = 1260 and o.obs_datetime>=:startDate and o.obs_datetime<=:endDate and e.location_id=:location "
            + "group by p.patient_id "
            + ") maxint "
            + "inner join encounter e on e.patient_id=maxint.patient_id and maxint.data_consulta=e.encounter_datetime "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where o.concept_id=165225 and o.voided=0 and e.encounter_type=80 and e.voided=0 and o.value_coded = 165226 and e.location_id=:location "
            + "union "
            + " Select p.patient_id,max(e.encounter_datetime) data_consulta, o.value_coded from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=165225 and  e.encounter_type=81 and e.encounter_datetime>=:startDate and e.encounter_datetime<=:endDate and o.value_coded = 165226 and  e.location_id=:location "
            + "group by p.patient_id "
            + ") interrupcao group by patient_id "
            + "union "
            + " select * from ( "
            + "select maxint.patient_id, data_consulta, o.value_coded from "
            + "(Select 	p.patient_id,max(o.obs_datetime) data_int, o.value_coded, e.encounter_datetime data_consulta from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=165292 and  e.encounter_type=80 and o.value_coded = 1260 and o.obs_datetime>=:startDate and o.obs_datetime<=:endDate and e.location_id=:location "
            + "group by p.patient_id "
            + ") maxint "
            + "inner join encounter e on e.patient_id=maxint.patient_id and maxint.data_consulta=e.encounter_datetime "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where o.concept_id=165225 and o.voided=0 and e.encounter_type=80 and e.voided=0 and o.value_coded = 165227 and e.location_id=:location "
            + "union "
            + " Select p.patient_id,max(e.encounter_datetime) data_consulta, o.value_coded from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=165225 and  e.encounter_type=81 and e.encounter_datetime>=:startDate and e.encounter_datetime<=:endDate and o.value_coded = 165227 and  e.location_id=:location "
            + "group by p.patient_id "
            + ") interrupcao group by patient_id "
            + "union "
            + " select * from ( "
            + "select maxint.patient_id, data_consulta, o.value_coded from "
            + "(Select 	p.patient_id,max(o.obs_datetime) data_int, o.value_coded, e.encounter_datetime data_consulta from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=165292 and  e.encounter_type=80 and o.value_coded = 1260 and o.obs_datetime>=:startDate and o.obs_datetime<=:endDate and e.location_id=:location "
            + "group by p.patient_id "
            + ") maxint "
            + "inner join encounter e on e.patient_id=maxint.patient_id and maxint.data_consulta=e.encounter_datetime "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where o.concept_id=165225 and o.voided=0 and e.encounter_type=80 and e.voided=0 and o.value_coded = 5622 and e.location_id=:location "
            + "union "
            + " Select p.patient_id,max(e.encounter_datetime) data_consulta, o.value_coded from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=165225 and  e.encounter_type=81 and e.encounter_datetime>=:startDate and e.encounter_datetime<=:endDate and o.value_coded = 5622 and  e.location_id=:location "
            + "group by p.patient_id "
            + ") interrupcao group by patient_id "
            + ") interrupcao_real ";

    switch (prepReasonInterruptionType) {
      case INFECTED:
        query = query + "where value_coded=1169 ";
        break;

      case COLATERAL_DAMAGES:
        query = query + "where value_coded=2015 ";
        break;

      case NO_RISKS:
        query = query + "where value_coded=165226 ";
        break;

      case USER_PREFERENCE:
        query = query + "where value_coded=165227 ";
        break;

      case OTHER:
        query = query + "where value_coded=5622 ";
        break;
    }

    return query;
  }
}
