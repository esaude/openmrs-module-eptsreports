package org.openmrs.module.eptsreports.reporting.library.queries;

public interface ListOfChildrenARVFormulationsQueries {

  class QUERY {
    public static final String findChildrenARVFormulations =
        "select distinct coorte12meses_final.patient_id as patient_id,  "
            + "coorte12meses_final.data_inicio as data_inicio,  "
            + "concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,''),' ',ifnull(pn.family_name,'')) as NomeCompleto,  "
            + "pid.identifier as NID,  "
            + "p.gender as gender,  "
            + "(TIMESTAMPDIFF(year,birthdate,:endDate)) as idade_actual, "
            + "TB.criteria as criteria, "
            + "max_filaF.data_fila, "
            + "max_filaF.code, "
            + "max_filaF.D1, "
            + "max_filaF.D2, "
            + "max_filaF.D3, "
            + "max_filaF.D4, "
            + "max_filaF.proxima_levantamento, "
            + "max_consulta_final.data_consulta, "
            + "max_consulta_final.peso, "
            + "max_consulta_final.abordagem,"
            + "max_consulta_final.dispensa, "
            + "max_consulta_final.proxima_consulta, "
            + "max_consulta_final.codeReg from  ( "
            + "select inicio_fila_seg_prox.*,  "
            + "GREATEST(COALESCE(data_fila,data_seguimento,data_recepcao_levantou),COALESCE(data_seguimento,data_fila,data_recepcao_levantou),COALESCE(data_recepcao_levantou,data_seguimento,data_fila))  data_usar_c,  "
            + "GREATEST(COALESCE(data_proximo_lev,data_proximo_seguimento,data_recepcao_levantou30),COALESCE(data_proximo_seguimento,data_proximo_lev,data_recepcao_levantou30),COALESCE(data_recepcao_levantou30,data_proximo_seguimento,data_proximo_lev)) data_usar  "
            + "from  ( "
            + "select inicio_fila_seg.*,  "
            + "max(obs_fila.value_datetime) data_proximo_lev,  "
            + "max(obs_seguimento.value_datetime) data_proximo_seguimento,  "
            + "date_add(data_recepcao_levantou, interval 30 day) data_recepcao_levantou30  from  ( "
            + "select inicio.*, "
            + "saida.data_estado, "
            + "max_fila.data_fila,  "
            + "max_consulta.data_seguimento,  "
            + "max_recepcao.data_recepcao_levantou  from  ( "
            + "Select patient_id,min(data_inicio) data_inicio  from  ( "
            + "Select p.patient_id,min(e.encounter_datetime) data_inicio  from 	patient p   "
            + "inner join encounter e on p.patient_id=e.patient_id	  "
            + "inner join obs o on o.encounter_id=e.encounter_id  "
            + "where e.voided=0 and o.voided=0 and p.voided=0 and   "
            + "e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and   "
            + "e.encounter_datetime<=:endDate and e.location_id=:location  "
            + "group by p.patient_id  "
            + "union  "
            + "Select p.patient_id,min(value_datetime) data_inicio  from patient p  "
            + "inner join encounter e on p.patient_id=e.patient_id   "
            + "inner join obs o on e.encounter_id=o.encounter_id  "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and   "
            + "o.concept_id=1190 and o.value_datetime is not null and   "
            + "o.value_datetime<=:endDate and e.location_id=:location  "
            + "group by p.patient_id  "
            + "union  "
            + "select pg.patient_id,min(date_enrolled) data_inicio  from  patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id  "
            + "where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location  "
            + "group by pg.patient_id  "
            + "union  "
            + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio   FROM patient p  "
            + "inner join encounter e on p.patient_id=e.patient_id  "
            + "WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location  "
            + "GROUP BY p.patient_id  "
            + "union  "
            + "Select p.patient_id,min(value_datetime) data_inicio  from patient p  "
            + "inner join encounter e on p.patient_id=e.patient_id  "
            + "inner join obs o on e.encounter_id=o.encounter_id  "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and   "
            + "o.concept_id=23866 and o.value_datetime is not null and   "
            + "o.value_datetime<=:endDate and e.location_id=:location  "
            + "group by p.patient_id "
            + ") inicio_real  "
            + "group by patient_id  "
            + ")inicio "
            + "left join  ( "
            + "select patient_id,max(data_estado) data_estado  from   (  "
            + "select pg.patient_id,  "
            + "max(ps.start_date) data_estado  from patient p   "
            + "inner join patient_program pg on p.patient_id=pg.patient_id  "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id  "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and   "
            + "pg.program_id=2 and ps.state in (7,8,10) and ps.end_date is null and   "
            + "ps.start_date<=:endDate and location_id=:location  "
            + "group by pg.patient_id  "
            + "union  select p.patient_id,  "
            + "max(o.obs_datetime) data_estado  from patient p   "
            + "inner join encounter e on p.patient_id=e.patient_id  "
            + "inner join obs  o on e.encounter_id=o.encounter_id  "
            + "where 	e.voided=0 and o.voided=0 and p.voided=0 and   "
            + "e.encounter_type in (53,6) and o.concept_id in (6272,6273) and o.value_coded in (1706,1366,1709) and    "
            + "o.obs_datetime<=:endDate and e.location_id=:location  "
            + "group by p.patient_id  "
            + "union  "
            + "select person_id as patient_id,death_date as data_estado  from person   "
            + "where dead=1 and death_date is not null and death_date<=:endDate  "
            + "union  "
            + "select p.patient_id,  max(obsObito.obs_datetime) data_estado  from patient p   "
            + "inner join encounter e on p.patient_id=e.patient_id  "
            + "inner join obs obsObito on e.encounter_id=obsObito.encounter_id  "
            + "where e.voided=0 and p.voided=0 and obsObito.voided=0 and   "
            + "e.encounter_type in (21,36,37) and  e.encounter_datetime<=:endDate and  e.location_id=:location and   "
            + "obsObito.concept_id in (2031,23944,23945) and obsObito.value_coded=1366	  "
            + "group by p.patient_id  "
            + ") allSaida  "
            + "group by patient_id "
            + ") saida on inicio.patient_id=saida.patient_id  "
            + "left join  ( "
            + "Select p.patient_id,max(encounter_datetime) data_fila  from patient p   "
            + "inner join encounter e on e.patient_id=p.patient_id  "
            + "where p.voided=0 and e.voided=0 and e.encounter_type=18 and   "
            + "e.location_id=:location and e.encounter_datetime<=:endDate  "
            + "group by p.patient_id "
            + ") max_fila on inicio.patient_id=max_fila.patient_id	 "
            + "left join  ( "
            + "Select p.patient_id,max(encounter_datetime) data_seguimento  from patient p   "
            + "inner join encounter e on e.patient_id=p.patient_id  "
            + "where p.voided=0 and e.voided=0 and e.encounter_type in (6,9) and   "
            + "e.location_id=:location and e.encounter_datetime<=:endDate  "
            + "group by p.patient_id  "
            + ") max_consulta on inicio.patient_id=max_consulta.patient_id  "
            + "left join  (  "
            + "Select p.patient_id,max(value_datetime) data_recepcao_levantou  from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id  inner join obs o on e.encounter_id=o.encounter_id  "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and   "
            + "o.concept_id=23866 and o.value_datetime is not null and   "
            + "o.value_datetime<=:endDate and e.location_id=:location  "
            + "group by p.patient_id  "
            + ") max_recepcao on inicio.patient_id=max_recepcao.patient_id  "
            + "group by inicio.patient_id  "
            + ") inicio_fila_seg  "
            + "left join  "
            + "obs obs_fila on obs_fila.person_id=inicio_fila_seg.patient_id  "
            + "and obs_fila.voided=0  "
            + "and obs_fila.obs_datetime=inicio_fila_seg.data_fila  "
            + "and obs_fila.concept_id=5096  "
            + "and obs_fila.location_id=:location  "
            + "left join  "
            + "obs obs_seguimento on obs_seguimento.person_id=inicio_fila_seg.patient_id  "
            + "and obs_seguimento.voided=0  "
            + "and obs_seguimento.obs_datetime=inicio_fila_seg.data_seguimento  "
            + "and obs_seguimento.concept_id=1410  "
            + "and obs_seguimento.location_id=:location  "
            + "group by inicio_fila_seg.patient_id  "
            + ") inicio_fila_seg_prox  "
            + "group by patient_id  "
            + ") coorte12meses_final  "
            + "inner join person p on p.person_id=coorte12meses_final.patient_id		  "
            + "left join   ( "
            + "select pad1.*  from person_address pad1  "
            + "inner join   (  "
            + "select person_id,min(person_address_id) id   from person_address  "
            + "where voided=0  "
            + "group by person_id  "
            + ") pad2  "
            + "where pad1.person_id=pad2.person_id and pad1.person_address_id=pad2.id  "
            + ") pad3 on pad3.person_id=coorte12meses_final.patient_id "
            + "left join( "
            + "select pn1.*  from person_name pn1  "
            + "inner join (  "
            + "select person_id,min(person_name_id) id   from person_name  "
            + "where voided=0  "
            + "group by person_id  "
            + ") pn2  "
            + "where pn1.person_id=pn2.person_id and pn1.person_name_id=pn2.id  "
            + ") pn on pn.person_id=coorte12meses_final.patient_id "
            + "left join  ( "
            + "select pid1.*  from patient_identifier pid1  "
            + "inner join  (  "
            + "select patient_id,min(patient_identifier_id) id  from patient_identifier  "
            + "where voided=0  "
            + "group by patient_id  "
            + ") pid2 "
            + "where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id  "
            + ") pid on pid.patient_id=coorte12meses_final.patient_id  "
            + "left join  ( "
            + "select p.patient_id, obsTB.obs_datetime , 'S' AS criteria from patient p  "
            + "inner join encounter e on e.patient_id=p.patient_id  "
            + "inner join obs obsTB on obsTB.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and obsTB.obs_datetime between :endDate - INTERVAL 7 MONTH and  :endDate  and   "
            + "e.location_id=:location and e.encounter_type=6 and obsTB.concept_id=1268 and obsTB.value_coded in (1256) and obsTB.voided=0  "
            + "group by p.patient_id "
            + "union "
            + "select p.patient_id,obsTB.obs_datetime,'S' AS criteria from patient p  "
            + "inner join encounter e on e.patient_id=p.patient_id  "
            + "inner join obs obsTB on obsTB.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and obsTB.obs_datetime between :endDate - INTERVAL 7 MONTH and  :endDate  and   "
            + "e.location_id=:location and e.encounter_type=6 and obsTB.concept_id=5965 and obsTB.value_coded =1065 and obsTB.voided=0  "
            + "group by p.patient_id  "
            + "union "
            + "select p.patient_id,obsTB.obs_datetime,'S' AS criteria from patient p  "
            + "inner join encounter e on e.patient_id=p.patient_id  "
            + "inner join obs obsTB on obsTB.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and obsTB.obs_datetime between :endDate - INTERVAL 7 MONTH and  :endDate  and   "
            + "e.location_id=:location and e.encounter_type=53 and obsTB.concept_id=1406 and obsTB.value_coded =42 and obsTB.voided=0  "
            + "group by p.patient_id "
            + "union "
            + "select  pg.patient_id,min(date_enrolled) data_inicio, 'S' AS criteria from  patient p  "
            + "inner join patient_program pg on p.patient_id=pg.patient_id                         "
            + "where pg.voided=0 and p.voided=0 and program_id=5 and date_enrolled between :endDate - INTERVAL 7 MONTH and  :endDate  and location_id=:location "
            + "group by pg.patient_id "
            + "union "
            + "select p.patient_id,obsTB.obs_datetime, 'S' AS criteria from patient p  "
            + "inner join encounter e on e.patient_id=p.patient_id  "
            + "inner join obs obsTB on obsTB.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and obsTB.obs_datetime between :endDate - INTERVAL 7 MONTH and  :endDate  and   "
            + "e.location_id=:location and e.encounter_type=6 and obsTB.concept_id=23761 and obsTB.value_coded =1065 and obsTB.voided=0  "
            + "group by p.patient_id  "
            + ")TB on coorte12meses_final.patient_id=TB.patient_id "
            + "left join  "
            + "( "
            + "select distinct max_filaFinal.patient_id,max_filaFinal.data_fila, "
            + "case  o.value_coded "
            + "when 1703 then 'AZT+3TC+EFV' "
            + "when 6100 then 'AZT+3TC+LPV/r' "
            + "when 1651 then 'AZT+3TC+NVP' "
            + "when 6324 then 'TDF+3TC+EFV' "
            + "when 6104 then 'ABC+3TC+EFV' "
            + "when 23784 then 'TDF+3TC+DTG' "
            + "when 23786 then 'ABC+3TC+DTG' "
            + "when 6116 then 'AZT+3TC+ABC' "
            + "when 6106 then 'ABC+3TC+LPV/r' "
            + "when 6105 then 'ABC+3TC+NVP' "
            + "when 6108 then 'TDF+3TC+LPV/r' "
            + "when 23790 then 'TDF+3TC+LPV/r+RTV' "
            + "when 23791 then 'TDF+3TC+ATV/r' "
            + "when 23792 then 'ABC+3TC+ATV/r' "
            + "when 23793 then 'AZT+3TC+ATV/r' "
            + "when 23795 then 'ABC+3TC+ATV/r+RAL' "
            + "when 23796 then 'TDF+3TC+ATV/r+RAL' "
            + "when 23801 then 'AZT+3TC+RAL' "
            + "when 23802 then 'AZT+3TC+DRV/r' "
            + "when 23815 then 'AZT+3TC+DTG' "
            + "when 6329 then 'TDF+3TC+RAL+DRV/r' "
            + "when 23797 then 'ABC+3TC+DRV/r+RAL' "
            + "when 23798 then '3TC+RAL+DRV/r' "
            + "when 23803 then 'AZT+3TC+RAL+DRV/r' "
            + "when 6243 then 'TDF+3TC+NVP' "
            + "when 6103 then 'D4T+3TC+LPV/r' "
            + "when 792 then 'D4T+3TC+NVP' "
            + "when 1827 then 'D4T+3TC+EFV' "
            + "when 6102 then 'D4T+3TC+ABC' "
            + "when 1311 then 'ABC+3TC+LPV/r' "
            + "when 1312 then 'ABC+3TC+NVP' "
            + "when 1313 then 'ABC+3TC+EFV' "
            + "when 1314 then 'AZT+3TC+LPV/r' "
            + "when 1315 then 'TDF+3TC+EFV'	"
            + "when 6330 then 'AZT+3TC+RAL+DRV/r' "
            + "when 6325 then 'D4T+3TC+ABC+LPV/r' "
            + "when 6326 then 'AZT+3TC+ABC+LPV/r' "
            + "when 6327 then 'D4T+3TC+ABC+EFV' "
            + "when 6328 then 'AZT+3TC+ABC+EFV' "
            + "when 6109 then 'AZT+DDI+LPV/r' "
            + "when 21163 then 'AZT+3TC+LPV/r' "
            + "when 23799 then 'TDF+3TC+DTG ' "
            + "when 23800 then 'ABC+3TC+DTG '	"
            + "when 6110 then  'D4T20+3TC+NVP' "
            + "when 1702 then 'AZT+3TC+NFV' "
            + "when 817  then 'AZT+3TC+ABC' "
            + "when 6244 then 'AZT+3TC+RTV' "
            + "when 1700 then 'AZT+DDl+NFV' "
            + "when 633  then 'EFV' "
            + "when 625  then 'D4T' "
            + "when 631  then 'NVP' "
            + "when 628  then '3TC' "
            + "when 635  then 'NFV' "
            + "when 797  then 'AZT' "
            + "when 814  then 'ABC' "
            + "when 6107 then 'TDF+AZT+3TC+LPV/r' "
            + "when 6236 then 'D4T+DDI+RTV-IP' "
            + "when 1701 then 'ABC+DDI+NFV' "
            + "when 6114 then '3DFC' "
            + "when 6115 then '2DFC+EFV' "
            + "when 6233 then 'AZT+3TC+DDI+LPV' "
            + "when 6234 then 'ABC+TDF+LPV' "
            + "when 6242 then 'D4T+DDI+NVP' "
            + "when 6118 then 'DDI50+ABC+LPV' "
            + "else 'OUTRO' end as code, "
            + "drug.D4,drug.D3,drug.D2,drug.D1,obs_proximo.value_datetime proxima_levantamento from ( "
            + "Select p.patient_id,max(encounter_datetime) data_fila,e.encounter_id  from  patient p  "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_type=18 and   "
            + "e.location_id=:location and e.encounter_datetime<=:endDate  "
            + "group by p.patient_id  "
            + ") max_filaFinal  "
            + "inner join obs o on o.person_id=max_filaFinal.patient_id and o.concept_id=1088 and o.obs_datetime=max_filaFinal.data_fila and o.voided=0 "
            + "inner join obs obs_proximo on obs_proximo.person_id=max_filaFinal.patient_id and obs_proximo.concept_id=5096 and obs_proximo.obs_datetime=max_filaFinal.data_fila and obs_proximo.voided=0 "
            + "left join  (  "
            + "select  "
            + "@num_drugs := 1 + LENGTH(drugname) - LENGTH(REPLACE(drugname, ',', '')) AS num_drugs,  "
            + "SUBSTRING_INDEX(drugname, ',', 1) AS D1,  "
            + "IF(@num_drugs > 1, SUBSTRING_INDEX(SUBSTRING_INDEX(drugname, ',', 2), ',', -1), '') AS D2,  "
            + "IF(@num_drugs > 2, SUBSTRING_INDEX(SUBSTRING_INDEX(drugname, ',', 3), ',', -1), '') AS D3,  "
            + "IF(@num_drugs > 3, SUBSTRING_INDEX(SUBSTRING_INDEX(drugname, ',', 4), ',', -1), '') AS D4,  "
            + "drug.person_id, drug.encounter_datetime  from (  "
            + "select formulacao.person_id as person_id, e.encounter_datetime encounter_datetime, group_concat(drug1.name ORDER BY formulacao.value_drug  ASC) drugname from encounter e  "
            + "join obs grupo on grupo.encounter_id=e.encounter_id  "
            + "join obs formulacao on formulacao.encounter_id=e.encounter_id  "
            + "inner join drug drug1 on formulacao.value_drug=drug1.drug_id  "
            + "where formulacao.concept_id = 165256  "
            + "and  grupo.concept_id =165252  "
            + "and formulacao.obs_group_id = grupo.obs_id  "
            + "and formulacao.voided=0  "
            + "and grupo.voided=0  "
            + "and e.voided=0  "
            + "and e.encounter_datetime <=:endDate  "
            + "and e.location_id=:location  "
            + "group by formulacao.person_id, e.encounter_datetime  "
            + "order by grupo.obs_id  "
            + ") drug  "
            + ") drug on drug.person_id=max_filaFinal.patient_id and max_filaFinal.data_fila=drug.encounter_datetime "
            + ")max_filaF  on max_filaF.patient_id=coorte12meses_final.patient_id "
            + "left join ( "
            + "select max_consulta.patient_id, max_consulta.data_seguimento data_consulta, obsPeso.value_numeric peso, "
            + "case maxAbordagem.value_coded "
            + "when 1256 then 'INICIO' "
            + "when 1257 then 'CONTINUA' "
            + "when 1267 then 'FIM' "
            + "else null end as abordagem, "
            + "case obsDispensa.value_coded "
            + "when 23720 then 'SIM' "
            + "else 'NAO' end as dispensa, "
            + "obsProximaConsulta.value_datetime as proxima_consulta, "
            + "case  obsRegConsulta.value_coded     "
            + "when 1703 then 'AZT+3TC+EFV' "
            + "when 6100 then 'AZT+3TC+LPV/r' "
            + "when 1651 then 'AZT+3TC+NVP' "
            + "when 6324 then 'TDF+3TC+EFV' "
            + "when 6104 then 'ABC+3TC+EFV' "
            + "when 23784 then 'TDF+3TC+DTG' "
            + "when 23786 then 'ABC+3TC+DTG' "
            + "when 6116 then 'AZT+3TC+ABC' "
            + "when 6106 then 'ABC+3TC+LPV/r' "
            + "when 6105 then 'ABC+3TC+NVP' "
            + "when 6108 then 'TDF+3TC+LPV/r' "
            + "when 23790 then 'TDF+3TC+LPV/r+RTV' "
            + "when 23791 then 'TDF+3TC+ATV/r' "
            + "when 23792 then 'ABC+3TC+ATV/r' "
            + "when 23793 then 'AZT+3TC+ATV/r' "
            + "when 23795 then 'ABC+3TC+ATV/r+RAL' "
            + "when 23796 then 'TDF+3TC+ATV/r+RAL' "
            + "when 23801 then 'AZT+3TC+RAL' "
            + "when 23802 then 'AZT+3TC+DRV/r' "
            + "when 23815 then 'AZT+3TC+DTG' "
            + "when 6329 then 'TDF+3TC+RAL+DRV/r' "
            + "when 23797 then 'ABC+3TC+DRV/r+RAL' "
            + "when 23798 then '3TC+RAL+DRV/r' "
            + "when 23803 then 'AZT+3TC+RAL+DRV/r' "
            + "when 6243 then 'TDF+3TC+NVP' "
            + "when 6103 then 'D4T+3TC+LPV/r' "
            + "when 792 then 'D4T+3TC+NVP' "
            + "when 1827 then 'D4T+3TC+EFV' "
            + "when 6102 then 'D4T+3TC+ABC' "
            + "when 1311 then 'ABC+3TC+LPV/r' "
            + "when 1312 then 'ABC+3TC+NVP' "
            + "when 1313 then 'ABC+3TC+EFV' "
            + "when 1314 then 'AZT+3TC+LPV/r' "
            + "when 1315 then 'TDF+3TC+EFV'	"
            + "when 6330 then 'AZT+3TC+RAL+DRV/r' "
            + "when 6325 then 'D4T+3TC+ABC+LPV/r' "
            + "when 6326 then 'AZT+3TC+ABC+LPV/r' "
            + "when 6327 then 'D4T+3TC+ABC+EFV' "
            + "when 6328 then 'AZT+3TC+ABC+EFV' "
            + "when 6109 then 'AZT+DDI+LPV/r' "
            + "when 21163 then 'AZT+3TC+LPV/r' "
            + "when 23799 then 'TDF+3TC+DTG ' "
            + "when 23800 then 'ABC+3TC+DTG '	"
            + "when 6110 then 'D4T20+3TC+NVP' "
            + "when 1702 then 'AZT+3TC+NFV' "
            + "when 817  then 'AZT+3TC+ABC' "
            + "when 6244 then 'AZT+3TC+RTV' "
            + "when 1700 then 'AZT+DDl+NFV' "
            + "when 633  then 'EFV' "
            + "when 625  then 'D4T' "
            + "when 631  then 'NVP' "
            + "when 628  then '3TC' "
            + "when 635  then 'NFV' "
            + "when 797  then 'AZT' "
            + "when 814  then 'ABC' "
            + "when 6107 then 'TDF+AZT+3TC+LPV/r' "
            + "when 6236 then 'D4T+DDI+RTV-IP' "
            + "when 1701 then 'ABC+DDI+NFV' "
            + "when 6114 then '3DFC' "
            + "when 6115 then '2DFC+EFV' "
            + "when 6233 then 'AZT+3TC+DDI+LPV' "
            + "when 6234 then 'ABC+TDF+LPV' "
            + "when 6242 then 'D4T+DDI+NVP' "
            + "when 6118 then 'DDI50+ABC+LPV' "
            + "else 'OUTRO' end as codeReg  from  ("
            + "Select p.patient_id,max(encounter_datetime) data_seguimento  from  patient p   "
            + "inner join encounter e on e.patient_id=p.patient_id  "
            + "where  p.voided=0 and e.voided=0 and e.encounter_type in (6,9) and   "
            + "e.location_id=:location and e.encounter_datetime<=:endDate  "
            + "group by p.patient_id  "
            + ") max_consulta  "
            + "left join obs obsPeso on obsPeso.person_id=max_consulta.patient_id and obsPeso.concept_id=5089 and obsPeso.obs_datetime=max_consulta.data_seguimento and obsPeso.voided=0 "
            + "left join "
            + "(select p.patient_id, max(encAbordagem.encounter_datetime),obsAbordagem.value_coded from patient p "
            + "inner join encounter encAbordagem on encAbordagem.patient_id=p.patient_id "
            + "inner join obs obsAbordagem on obsAbordagem.encounter_id=encAbordagem.encounter_id "
            + "where encAbordagem.encounter_type=6 and encAbordagem.encounter_datetime <=:endDate"
            + "and obsAbordagem.concept_id=23725 "
            + "and obsAbordagem.obs_datetime=encAbordagem.encounter_datetime "
            + "and obsAbordagem.voided=0 "
            + "and encAbordagem.voided=0 "
            + "group by p.patient_id"
            + ") maxAbordagem on maxAbordagem.patient_id=max_consulta.patient_id "
            + "left join  obs obsDispensa  on obsDispensa.person_id=max_consulta.patient_id and obsDispensa.concept_id=23739 and  obsDispensa.obs_datetime=max_consulta.data_seguimento and obsDispensa.voided=0 "
            + "left join  obs obsProximaConsulta  on obsProximaConsulta.person_id=max_consulta.patient_id and obsProximaConsulta.concept_id=1410 and  obsProximaConsulta.obs_datetime=max_consulta.data_seguimento and obsProximaConsulta.voided=0 "
            + "left join  obs obsRegConsulta  on obsRegConsulta.person_id=max_consulta.patient_id and obsRegConsulta.concept_id=1087 and  obsRegConsulta.obs_datetime=max_consulta.data_seguimento and obsRegConsulta.voided=0"
            + ") max_consulta_final on  coorte12meses_final.patient_id=max_consulta_final.patient_id "
            + "where (data_estado is null or (data_estado is not null and  data_usar_c>data_estado)) and date_add(data_usar, interval 28 day) >=:endDate and (TIMESTAMPDIFF(year,birthdate,:endDate))<15 ";
  }
}
