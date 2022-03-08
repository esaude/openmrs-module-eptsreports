package org.openmrs.module.eptsreports.reporting.library.queries;

import org.openmrs.module.eptsreports.reporting.utils.TypePTV;

public class FaltososAoLevantamentoARVQueries {

  public static String findPatientswhoHaveScheduledAppointmentsDuringReportingPeriod() {
    String query =
        "select finalDefaulters.patient_id from ( "
            + "select defaulters.patient_id,defaulters.data_levantamento as data_levantamento, max(defaulters.data_proximo_levantamento) data_proximo_levantamento from ( "
            + "select fila.patient_id, fila.data_levantamento,obs_fila.value_datetime data_proximo_levantamento from (   "
            + "select p.patient_id,max(e.encounter_datetime) as data_levantamento from patient p  "
            + "inner join encounter e on p.patient_id=e.patient_id  "
            + "where encounter_type=18 and e.encounter_datetime <:startDate and e.location_id=:location and e.voided=0 and p.voided=0  "
            + "group by p.patient_id  "
            + ")fila  "
            + "inner join obs obs_fila on obs_fila.person_id=fila.patient_id  "
            + "where obs_fila.voided=0 and obs_fila.concept_id=5096 and fila.data_levantamento=obs_fila.obs_datetime "
            + "union "
            + "select p.patient_id,max(o.value_datetime) data_levantamento, date_add(max(o.value_datetime), INTERVAL 30 day)  data_proximo_levantamento  from patient p "
            + "inner join encounter e on p.patient_id = e.patient_id  "
            + "inner join obs o on o.encounter_id = e.encounter_id  "
            + "inner join obs obsLevantou on obsLevantou.encounter_id=e.encounter_id "
            + "where  e.voided = 0 and p.voided = 0 and o.value_datetime <:startDate and o.voided = 0 "
            + "and obsLevantou.voided=0 and obsLevantou.concept_id=23865 and obsLevantou.value_coded = 1065 "
            + "and o.concept_id = 23866 and e.encounter_type=52 and e.location_id=:location  "
            + "group by p.patient_id "
            + ") defaulters "
            + "group by defaulters.patient_id "
            + ")finalDefaulters "
            + "where finalDefaulters.data_proximo_levantamento between :startDate  AND :endDate "
            + "group by finalDefaulters.patient_id ";

    return query;
  }

  public static String findPatientswhoHaveScheduledAppointmentsDuringReportingPeriodNumeratorC() {
    String query =
        "select DEN.patient_id from (  "
            + "select defaulters.patient_id,defaulters.data_levantamento,defaulters.data_proximo_levantamento from (  "
            + "select defaulters.patient_id,max(defaulters.data_levantamento) as data_levantamento, max(defaulters.data_proximo_levantamento) data_proximo_levantamento from (  "
            + "select fila.patient_id, fila.data_levantamento,obs_fila.value_datetime data_proximo_levantamento from (    "
            + "select p.patient_id,max(e.encounter_datetime) as data_levantamento from patient p   "
            + "inner join encounter e on p.patient_id=e.patient_id   "
            + "where encounter_type=18 and e.encounter_datetime <:startDate and e.location_id=:location and e.voided=0 and p.voided=0   "
            + "group by p.patient_id   "
            + ")fila   "
            + "inner join obs obs_fila on obs_fila.person_id=fila.patient_id   "
            + "where obs_fila.voided=0 and obs_fila.concept_id=5096 and fila.data_levantamento=obs_fila.obs_datetime  "
            + "union  "
            + "select p.patient_id,max(o.value_datetime) data_levantamento, date_add(max(o.value_datetime), INTERVAL 30 day)  data_proximo_levantamento  from patient p  "
            + "inner join encounter e on p.patient_id = e.patient_id   "
            + "inner join obs o on o.encounter_id = e.encounter_id   "
            + "inner join obs obsLevantou on obsLevantou.encounter_id=e.encounter_id  "
            + "where  e.voided = 0 and p.voided = 0 and o.value_datetime <:startDate and o.voided = 0  "
            + "and obsLevantou.voided=0 and obsLevantou.concept_id=23865 and obsLevantou.value_coded = 1065  "
            + "and o.concept_id = 23866 and e.encounter_type=52 and e.location_id=:location   "
            + "group by p.patient_id  "
            + ") defaulters  "
            + "group by defaulters.patient_id  "
            + ")defaulters  "
            + "where defaulters.data_proximo_levantamento between :startDate  AND :endDate  "
            + "group by defaulters.patient_id  "
            + ")DEN  "
            + "left join ( "
            + "select p.patient_id,e.encounter_datetime as data_levantamento from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "where encounter_type=18  and e.location_id=:location and e.voided=0 and p.voided=0 and e.encounter_datetime<=DATE_ADD(:endDate, INTERVAL 7 DAY)  "
            + " union "
            + "select p.patient_id,o.value_datetime data_levantamento  from patient p  "
            + "inner join encounter e on p.patient_id = e.patient_id   "
            + "inner join obs o on o.encounter_id = e.encounter_id   "
            + "inner join obs obsLevantou on obsLevantou.encounter_id=e.encounter_id  "
            + "where  e.voided = 0 and p.voided = 0 and o.voided = 0  "
            + "and obsLevantou.voided=0 and obsLevantou.concept_id=23865 and obsLevantou.value_coded = 1065  "
            + "and o.concept_id = 23866 and e.encounter_type=52 and e.location_id=:location and o.value_datetime<= DATE_ADD(:endDate, INTERVAL 7 DAY)  "
            + ") l on l.patient_id=DEN.patient_id and l.data_levantamento between DATE_ADD(DEN.data_levantamento, INTERVAL 1 DAY) and DATE_ADD(:endDate, INTERVAL 7 DAY)  "
            + "where l.patient_id is null ";
    return query;
  }

  public static String getPatientsTransferredFromAnotherHealthFacilityUntilEndDate() {
    String query =
        "select transferidopara.patient_id from ( "
            + "select patient_id,max(data_transferidopara) data_transferidopara from ( "
            + "select maxEstado.patient_id,maxEstado.data_transferidopara from ( "
            + "select pg.patient_id,max(ps.start_date) data_transferidopara "
            + "from patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and "
            + "pg.program_id=2 and ps.start_date<=:endDate and pg.location_id=:location group by p.patient_id "
            + ") maxEstado "
            + "inner join patient_program pg2 on pg2.patient_id=maxEstado.patient_id "
            + "inner join patient_state ps2 on pg2.patient_program_id=ps2.patient_program_id "
            + "where pg2.voided=0 and ps2.voided=0 and pg2.program_id=2 and "
            + "ps2.start_date=maxEstado.data_transferidopara and pg2.location_id=:location and ps2.state=7 and ps2.end_date is null "
            + "union "
            + "select p.patient_id,max(o.obs_datetime) data_transferidopara from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.voided=0 and p.voided=0 and o.obs_datetime<=:endDate and o.voided=0 and o.concept_id=6272 and o.value_coded=1706 and e.encounter_type=53 and  e.location_id=:location group by p.patient_id "
            + "union "
            + "select p.patient_id,max(e.encounter_datetime) data_transferidopara from  patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id where  e.voided=0 and p.voided=0 and e.encounter_datetime<=:endDate and "
            + "o.voided=0 and o.concept_id=6273 and o.value_coded=1706 and e.encounter_type=6 and  e.location_id=:location group by p.patient_id) transferido group by patient_id ) transferidopara "
            + "inner join( "
            + "select patient_id,max(encounter_datetime) encounter_datetime from( "
            + "select p.patient_id,max(e.encounter_datetime) encounter_datetime from  patient p "
            + "inner join encounter e on e.patient_id=p.patient_id where  p.voided=0 and e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location and e.encounter_type in (18,6,9) "
            + "group by p.patient_id "
            + "union "
            + "Select p.patient_id,max(value_datetime) encounter_datetime from  patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id) consultaLev group by patient_id) consultaOuARV on transferidopara.patient_id=consultaOuARV.patient_id "
            + "where consultaOuARV.encounter_datetime<=transferidopara.data_transferidopara and transferidopara.data_transferidopara <= :endDate ";
    return query;
  }

  public static String getPatientsWhoDied() {

    String query =
        "select obito.patient_id from ( "
            + "select patient_id,max(data_obito) data_obito from ( "
            + "select maxEstado.patient_id,maxEstado.data_obito from ( "
            + "select pg.patient_id,max(ps.start_date) data_obito from patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and "
            + "pg.program_id=2 and ps.start_date<=:endDate and pg.location_id=:location "
            + "group by p.patient_id ) maxEstado "
            + "inner join patient_program pg2 on pg2.patient_id=maxEstado.patient_id "
            + "inner join patient_state ps2 on pg2.patient_program_id=ps2.patient_program_id "
            + "where pg2.voided=0 and ps2.voided=0 and pg2.program_id=2 and "
            + "ps2.start_date=maxEstado.data_obito and pg2.location_id=:location and ps2.state=10 and ps2.end_date is null "
            + "union "
            + "select p.patient_id,max(o.obs_datetime) data_obito from	patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.voided=0 and p.voided=0 and o.obs_datetime<=:endDate and "
            + "o.voided=0 and o.concept_id=6272 and o.value_coded=1366 and e.encounter_type=53 and  e.location_id=:location group by p.patient_id "
            + "union  "
            + "select p.patient_id,max(e.encounter_datetime) data_obito from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and p.voided=0 and e.encounter_datetime<=:endDate "
            + "and o.voided=0 and o.concept_id=6273 and o.value_coded=1366 and e.encounter_type=6 and  e.location_id=:location "
            + "group by p.patient_id "
            + "union  "
            + "Select person_id,death_date from person p where p.dead=1 and p.death_date<=:endDate )transferido "
            + "group by patient_id) obito "
            + "inner join ( "
            + "select patient_id,max(encounter_datetime) encounter_datetime from ( "
            + "select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location and e.encounter_type in (18,6,9) "
            + "group by p.patient_id "
            + "union "
            + "select p.patient_id,max(value_datetime) encounter_datetime from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and "
            + "o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location "
            + "group by p.patient_id ) consultaLev "
            + "group by patient_id ) "
            + "consultaOuARV on obito.patient_id=consultaOuARV.patient_id "
            + "where consultaOuARV.encounter_datetime<=obito.data_obito and obito.data_obito <= :endDate ";

    return query;
  }

  public static String getPatientsWhoSuspendTratment() {

    String query =
        "select suspenso1.patient_id from ( "
            + "select patient_id,max(data_suspencao) data_suspencao from ( "
            + "select maxEstado.patient_id,maxEstado.data_suspencao from( "
            + "select pg.patient_id,max(ps.start_date) data_suspencao from patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and "
            + "pg.program_id=2 and ps.start_date<=:endDate and pg.location_id=:location "
            + "group by p.patient_id )maxEstado "
            + "inner join patient_program pg2 on pg2.patient_id=maxEstado.patient_id "
            + "inner join patient_state ps2 on pg2.patient_program_id=ps2.patient_program_id where pg2.voided=0 and ps2.voided=0 and pg2.program_id=2 and "
            + "ps2.start_date=maxEstado.data_suspencao and pg2.location_id=:location and ps2.state=8 and ps2.end_date is null "
            + "union "
            + " select p.patient_id,max(o.obs_datetime) data_suspencao from  patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where e.voided=0 and p.voided=0 and o.obs_datetime<=:endDate and o.voided=0 and o.concept_id=6272 "
            + "and o.value_coded=1709 and e.encounter_type=53 and  e.location_id=:location group by p.patient_id "
            + "union "
            + "select  p.patient_id,max(e.encounter_datetime) data_suspencao from  patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where  e.voided=0 and p.voided=0 and e.encounter_datetime<=:endDate and o.voided=0 and o.concept_id=6273 "
            + "and o.value_coded=1709 and e.encounter_type=6 and  e.location_id=:location group by p.patient_id ) suspenso group by patient_id) suspenso1 "
            + "inner join ( "
            + "select patient_id,max(encounter_datetime) encounter_datetime from ( "
            + "select p.patient_id,max(e.encounter_datetime) encounter_datetime from  patient p "
            + "inner join encounter e on e.patient_id=p.patient_id where p.voided=0 and e.voided=0 and e.encounter_datetime<=:endDate and  "
            + "e.location_id=:location and e.encounter_type=18 group by p.patient_id "
            + "union "
            + "Select  p.patient_id,max(value_datetime) encounter_datetime from  patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where  p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 "
            + "and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id"
            + ") consultaLev group by patient_id) consultaOuARV on suspenso1.patient_id=consultaOuARV.patient_id "
            + "where consultaOuARV.encounter_datetime<=suspenso1.data_suspencao and suspenso1.data_suspencao <= :endDate ";

    return query;
  }

  public static String getPatientsWhoArePregnantOrBreastfeeding(TypePTV typePTV) {

    String query =
        "select f.patient_id from ( "
            + "select "
            + "p.person_id as  patient_id,	 "
            + "lactante.data_lactante, "
            + "gravida.data_gravida, "
            + "if(max(lactante.data_lactante) is null,1, "
            + "if(max(gravida.data_gravida) is null,2, "
            + "if(max(gravida.data_gravida)>=max(lactante.data_lactante),1,2))) decisao from person p "
            + "left join ( "
            + "select p.patient_id, e.encounter_datetime data_lactante   from 	patient p "
            + "inner join encounter e on p.patient_id = e.patient_id "
            + "inner join obs o on o.encounter_id = e.encounter_id "
            + "where	e.voided = 0 and p.voided = 0 "
            + "and e.encounter_datetime between DATE_SUB(:endDate  , INTERVAL 18 MONTH) and :endDate "
            + "and o.voided = 0  and o.concept_id = 6332 and o.value_coded = 1065 and e.encounter_type in(6,9) and e.location_id=:location "
            + ") lactante on p.person_id=lactante.patient_id "
            + "left join "
            + "( "
            + "select p.patient_id, e.encounter_datetime data_gravida   from 	patient p "
            + "inner join encounter e on p.patient_id = e.patient_id "
            + "inner join obs o on o.encounter_id = e.encounter_id "
            + "where 	e.voided = 0 and p.voided = 0 and  "
            + "e.encounter_datetime between DATE_SUB(:endDate, INTERVAL 9 MONTH) and :endDateand  "
            + "o.voided = 0  and o.concept_id = 1982 and o.value_coded = 1065 and e.encounter_type in(6,9) and e.location_id=:location "
            + ")gravida on gravida.patient_id=p.person_id "
            + "where (lactante.data_lactante is not null or gravida.data_gravida is not null) and p.voided=0 and p.gender='F' "
            + "group by p.person_id "
            + ")f ";
    switch (typePTV) {
      case PREGNANT:
        query = query + "where f.decisao = 1 ";
        break;

      case BREASTFEEDING:
        query = query + "where f.decisao = 2 ";
        break;
    }

    return query;
  }

  public static String getPatientsWhoHaveAPSSConsultation() {

    String query =
        "select p.patient_id from patient p "
            + "inner join encounter e on p.patient_id = e.patient_id  "
            + "inner join obs o on o.encounter_id = e.encounter_id  "
            + "where  "
            + "e.voided = 0 "
            + "and p.voided = 0  "
            + "and e.encounter_datetime between DATE_SUB(:endDate, INTERVAL 3 MONTH) and :endDate "
            + "and o.voided = 0  "
            + "and e.encounter_type in(35)  "
            + "and e.location_id=:location ";

    return query;
  }
}
