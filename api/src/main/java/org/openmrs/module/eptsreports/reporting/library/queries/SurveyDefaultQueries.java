package org.openmrs.module.eptsreports.reporting.library.queries;

import org.openmrs.module.eptsreports.reporting.utils.TypePTV;

public class SurveyDefaultQueries {

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

  public static String findPatientswhoHaveScheduledAppointmentsDuringReportingPeriodNumeratorB() {
    String query =
        "select final.patient_id from ( "
            + "select NUN.patient_id, NUN.data_levantamento from ( "
            + "select defaulters.patient_id,defaulters.data_levantamento,defaulters.data_proximo_levantamento from ( "
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
            + ")defaulters "
            + "where defaulters.data_proximo_levantamento between :startDate  AND :endDate "
            + "group by defaulters.patient_id "
            + ")DEN "
            + "inner join ( "
            + "select fila.patient_id, fila.data_levantamento,obs_fila.value_datetime data_proximo_levantamento from ( "
            + "select p.patient_id,min(e.encounter_datetime) as data_levantamento from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "where encounter_type=18 and e.encounter_datetime <=:endDate and e.location_id=:location and e.voided=0 and p.voided=0 "
            + "group by p.patient_id "
            + ")fila   "
            + "inner join obs obs_fila on obs_fila.person_id=fila.patient_id   "
            + "where obs_fila.voided=0 and obs_fila.concept_id=5096 and fila.data_levantamento=obs_fila.obs_datetime "
            + "union "
            + "select p.patient_id,min(o.value_datetime) data_levantamento, date_add(max(o.value_datetime), INTERVAL 30 day) data_proximo_levantamento  from patient p "
            + "inner join encounter e on p.patient_id = e.patient_id "
            + "inner join obs o on o.encounter_id = e.encounter_id "
            + "where  e.voided = 0 and p.voided = 0 and o.value_datetime <=:endDate and o.voided = 0 and o.concept_id = 23866 and e.encounter_type=52 and e.location_id=:location "
            + "group by p.patient_id "
            + ")NUN on DEN.patient_id=NUN.patient_id "
            + "where NUN.data_levantamento between DATE_SUB(DEN.data_proximo_levantamento, INTERVAL 7 DAY) and DATE_ADD(:endDate, INTERVAL 7 DAY) and  "
            + "DATEDIFF(NUN.data_levantamento,DEN.data_proximo_levantamento) > 7 "
            + " )final ";
    return query;
  }

  public static String findPatientswhoHaveScheduledAppointmentsDuringReportingPeriodNumeratorC() {
    String query =
        "select p.patient_id from patient p  "
            + "left join ( "
            + "select patient_id from  ( "
            + "select fila.patient_id, max(fila.data_levantamento) data_levantamento from  (  "
            + "select p.patient_id,e.encounter_datetime as data_levantamento from patient p     "
            + "inner join encounter e on p.patient_id=e.patient_id     "
            + "where encounter_type=18  and e.location_id=:location and e.voided=0 and p.voided=0   "
            + "group by p.patient_id  "
            + ")fila     "
            + "union    "
            + "select p.patient_id,max(o.value_datetime) data_levantamento    from patient p    "
            + "inner join encounter e on p.patient_id = e.patient_id     "
            + "inner join obs o on o.encounter_id = e.encounter_id     "
            + "where  e.voided = 0 and p.voided = 0 and o.voided = 0    "
            + "and o.concept_id = 23866 and e.encounter_type=52 and e.location_id=:location   "
            + "group by p.patient_id   "
            + ")DEN where DEN.data_levantamento between :startDate and DATE_ADD(:endDate, INTERVAL 7 DAY) "
            + ") NUN on NUN.patient_id=p.patient_id "
            + "where NUN.patient_id is null ";
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
        "select finalPregLac.patient_id from ("
            + "select final.patient_id, final.concept_id, max(final.encounter_datetime), if(final.concept_id=1982,1,2) as preg_or_lac from ( "
            + "select p.patient_id, max(e.encounter_datetime) encounter_datetime,o.concept_id  from patient p  "
            + "inner join encounter e on p.patient_id = e.patient_id   "
            + "inner join obs o on o.encounter_id = e.encounter_id   "
            + "where  e.voided = 0 and p.voided = 0   "
            + "and e.encounter_datetime between DATE_SUB(:endDate, INTERVAL 9 MONTH) and :endDate  "
            + "and o.voided = 0  and o.concept_id = 1982 and o.value_coded = 1065 and e.encounter_type in(6,9)   "
            + "and e.location_id=:location "
            + "group by p.patient_id  "
            + "union "
            + "select p.patient_id, max(e.encounter_datetime) encounter_datetime, o.concept_id from patient p  "
            + "inner join encounter e on p.patient_id = e.patient_id   "
            + "inner join obs o on o.encounter_id = e.encounter_id   "
            + "where  e.voided = 0 and p.voided = 0   "
            + "and e.encounter_datetime between DATE_SUB(:endDate, INTERVAL 18 MONTH) and :endDate  "
            + "and o.voided = 0  and o.concept_id = 6332 and o.value_coded = 1065 and e.encounter_type in(6,9)   "
            + "and e.location_id=:location "
            + "group by p.patient_id  "
            + ")final "
            + "group by final.patient_id "
            + "order by final.patient_id,final.encounter_datetime desc,preg_or_lac "
            + ")finalPregLac ";

    switch (typePTV) {
      case PREGNANT:
        query = query + "where finalPregLac.concept_id in(1982)";
        break;

      case BREASTFEEDING:
        query = query + "where finalPregLac.concept_id in(6332)";
        break;
    }

    return query;
  }

  public static String getPatientsWhoHaveViralLoadNotSupresed() {

    String query =
        "SELECT allCV.patient_id, allCV.encounter_datetime,allCV.concept_numeric,allCV.concept_coded,if(allCV.value_numeric>=1000,allCV.value_numeric,null) value_numeric,allCV.value_coded FROM ( "
            + "SELECT maxLastCV.patient_id, maxLastCV.encounter_datetime encounter_datetime, maxLastCV.concept_numeric,maxLastCV.concept_coded,maxLastCV.value_numeric,maxLastCV.value_coded, maxLastCV.ordemSource FROM ( "
            + "SELECT cvLab.patient_id,cvLab.encounter_datetime,cvLab.concept_numeric,cvLab.concept_coded,cvLab.value_numeric,cvLab.value_coded, 1 ordemSource FROM ( "
            + "SELECT cv1.patient_id,cv1.encounter_datetime,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM ( "
            + "SELECT pat.patient_id,MAX(enc.encounter_datetime) encounter_datetime, enc.encounter_id FROM patient pat "
            + "INNER JOIN encounter enc ON pat.patient_id = enc.patient_id "
            + "INNER JOIN obs o on enc.encounter_id=o.encounter_id "
            + "WHERE pat.voided = 0 AND enc.voided = 0 "
            + "AND enc.location_id =:location "
            + "AND enc.encounter_type IN (13) "
            + "AND o.concept_id in(856,1305) "
            + "AND enc.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate "
            + "group by pat.patient_id "
            + ") cv1 "
            + "LEFT JOIN obs obsNumeric ON cv1.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856  "
            + "LEFT JOIN obs obsQualitative ON cv1.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305 "
            + ")cvLab "
            + "UNION "
            + "SELECT cvFSR.patient_id,cvFSR.encounter_datetime,cvFSR.concept_numeric,cvFSR.concept_coded,cvFSR.value_numeric,cvFSR.value_coded, 2 ordemSource FROM ( "
            + "SELECT cv1.patient_id,cv1.encounter_datetime,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM ( "
            + "SELECT  pat.patient_id,MAX(enc.encounter_datetime) encounter_datetime,enc.encounter_id FROM patient pat "
            + "INNER JOIN encounter enc ON pat.patient_id = enc.patient_id "
            + "INNER JOIN obs o on enc.encounter_id=o.encounter_id "
            + "WHERE pat.voided = 0 AND enc.voided = 0 "
            + "AND enc.location_id=:location "
            + "AND enc.encounter_type IN (51) "
            + "AND o.concept_id in(856,1305) "
            + "AND enc.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate "
            + "group by pat.patient_id "
            + ") cv1 "
            + "LEFT JOIN obs obsNumeric ON cv1.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856  "
            + "LEFT JOIN obs obsQualitative ON cv1.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305 "
            + ")cvFSR "
            + "UNION "
            + "SELECT cvFC.patient_id,cvFC.encounter_datetime,cvFC.concept_numeric,cvFC.concept_coded,cvFC.value_numeric,cvFC.value_coded, 3 ordemSource FROM ( "
            + "SELECT cv1.patient_id,cv1.encounter_datetime,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM ( "
            + "SELECT pat.patient_id,MAX(enc.encounter_datetime) encounter_datetime,enc.encounter_id FROM patient pat "
            + "INNER JOIN encounter enc ON pat.patient_id = enc.patient_id "
            + "INNER JOIN obs o on enc.encounter_id=o.encounter_id "
            + "WHERE pat.voided = 0 AND enc.voided = 0 "
            + "AND enc.location_id=:location "
            + "AND enc.encounter_type IN (6) "
            + "AND o.concept_id in(856,1305) "
            + "AND enc.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate "
            + "group by pat.patient_id "
            + ") cv1 LEFT JOIN obs obsNumeric ON cv1.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856  "
            + "LEFT JOIN obs obsQualitative ON cv1.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305 "
            + ")cvFC "
            + "UNION "
            + "SELECT cvRES.patient_id,cvRES.encounter_datetime,cvRES.concept_numeric,cvRES.concept_coded,cvRES.value_numeric,cvRES.value_coded, 4 ordemSource FROM ( "
            + "SELECT  cv1.patient_id,cv1.encounter_datetime,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM ( "
            + "SELECT  pat.patient_id, MAX(enc.encounter_datetime) encounter_datetime,enc.encounter_id FROM patient pat "
            + "INNER JOIN encounter enc ON pat.patient_id = enc.patient_id "
            + "INNER JOIN obs o on enc.encounter_id=o.encounter_id "
            + "WHERE pat.voided = 0 AND enc.voided = 0 "
            + "AND enc.location_id=:location "
            + "AND enc.encounter_type IN (53) "
            + "AND o.concept_id in(856,1305) "
            + "AND enc.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate "
            + "group by pat.patient_id "
            + ") cv1 "
            + "LEFT JOIN obs obsNumeric ON cv1.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856  "
            + "LEFT JOIN obs obsQualitative ON cv1.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305 "
            + ")cvRES "
            + ")maxLastCV "
            + "group by maxLastCV.patient_id "
            + "order by maxLastCV.patient_id,maxLastCV.encounter_datetime desc,maxLastCV.ordemSource "
            + ") allCV";
    return query;
  }

  public static String getPatientsWhoHaveAPSSConsultation() {

    String query =
        "select p.patient_id from patient p "
            + "inner join encounter e on p.patient_id = e.patient_id  "
            + "inner join obs o on o.encounter_id = e.encounter_id  "
            + "where  e.voided = 0 and p.voided = 0  "
            + "and e.encounter_datetime between DATE_SUB(:endDate, INTERVAL 3 MONTH) and :endDate "
            + "and o.voided = 0  and e.encounter_type in(34)  and e.location_id=:location "
            + "group by p.patient_id,e.encounter_datetime ";

    return query;
  }
}
