                      select 
                      inicio.patient_id as patient_id,
                      inicio.identifier as NID,
                      inicio.NomeCompleto as NAME,
                      inicio.gender AS GENDER,
                      inicio.idade as AGE,
                      inicio.data_inicio as ARV_INITIATION,
                      inicio.PREG_LAC as PREG_LAC,
                      inicio.TB as TB,
                      inicio.data_levantamento as DATA_LEVANTAMENTO_FILA,
                      inicio.data_proximo_levantamento as DATA_PROXIMO_LEVANTAMENTO_FILA,
                      inicio.data_levantamento_recepcao as DATA_LEVANTAMENTO_RECEPCAO,
                      inicio.data_proximo_levantamento_recepcao as DATA_PROXIMO_LEVANTAMENTO_RECEPCAO,
                      inicio.data_seguimento as DATA_SEGUIMENTO,
                      inicio.data_proximo_seguimento as DATA_PROXIMO_SEGUIMENTO,
                      inicio.state as STATE,
                      inicio.start_date DATA_ESTADO,
                      inicio.state_fc as STATE_FC,
                      inicio.SATE_FC_DATE as SATE_FC_DATE,
                      inicio.state_fr as STATE_FR,
                      inicio.STATE_FR_DATE as STATE_FR_DATE,
                      inicio.state_home_card as STATE_HOME_CARD,
                      inicio.home_card_date as HOME_CARD_DATE


                       from 
                       ( 
                          Select 
                          inicio_real.patient_id,
                          pid.identifier,
                          concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,''),' ',ifnull(pn.family_name,'')) as NomeCompleto,
                          p.gender as gender,
                          if(p.birthdate is not null, floor(datediff(:evaluationDate,p.birthdate)/365),'N/A') idade,    
                          min(data_inicio) data_inicio,
                          preg_or_lac.PREG_LAC,
                          if(tbFinal.patient_id is not null, 'SIM', '') as TB,
                          fila.data_levantamento,
                          fila.data_proximo_levantamento,
                          recepcao.data_levantamento_recepcao,
                          recepcao.data_proximo_levantamento_recepcao,
                          seguimento.data_seguimento,
                          seguimento.data_proximo_seguimento,
                          case 
                          when ps.state = 9 then 'LARGOU TRATAMENTO' 
                          when ps.state = 6 then 'ACTIVO NO PROGRAMA' 
                          when ps.state = 10 then 'OBITO' 
                          when ps.state = 8 then 'SUSPENSO' 
                          when ps.state = 7 then 'TRANSFERIDO PARA' 
                          when ps.state = 29 then 'TRANSFERIDO DE' 
                          end AS state,
                          ps.start_date,
                          homeCardVisit.state_home_card,
                          homeCardVisit.encounter_datetime as home_card_date,
                          FC.state_fc,
                          FC.encounter_datetime as SATE_FC_DATE,
                          FR.state_fr,
                          FR.encounter_datetime as STATE_FR_DATE

                          from 
                          ( 
                          Select p.patient_id,min(e.encounter_datetime) data_inicio from patient p  
                          inner join encounter e on p.patient_id=e.patient_id  
                          inner join obs o on o.encounter_id=e.encounter_id 
                          where e.voided=0 and o.voided=0 and p.voided=0 and  
                          e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and  
                          e.encounter_datetime<=:endDate and e.location_id=:location 
                          group by p.patient_id 
                          union 
                          Select p.patient_id,min(value_datetime) data_inicio from patient p 
                          inner join encounter e on p.patient_id=e.patient_id 
                          inner join obs o on e.encounter_id=o.encounter_id 
                          where  p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and  
                          o.concept_id=1190 and o.value_datetime is not null and  
                          o.value_datetime<=:endDate and e.location_id=:location 
                          group by p.patient_id 
                          union 
                          select pg.patient_id,min(date_enrolled) data_inicio  from patient p 
                          inner join patient_program pg on p.patient_id=pg.patient_id 
                          where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location 
                          group by pg.patient_id 
                          union 
                          SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio  FROM   patient p 
                          inner join encounter e on p.patient_id=e.patient_id 
                          WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location 
                          GROUP BY p.patient_id 
                          union 
                          Select p.patient_id,min(value_datetime) data_inicio from patient p 
                          inner join encounter e on p.patient_id=e.patient_id 
                          inner join obs o on e.encounter_id=o.encounter_id 
                          where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  
                          o.concept_id=23866 and o.value_datetime is not null and  
                          o.value_datetime<=:endDate and e.location_id=:location 
                          group by p.patient_id 
                          ) inicio_real 
                           
                          inner join person p on p.person_id=inicio_real.patient_id         
                          left join   ( 
                          select pad1.*  from person_address pad1  
                          inner join   (  
                          select person_id,min(person_address_id) id   from person_address  
                          where voided=0  
                          group by person_id  
                          ) pad2  
                          where pad1.person_id=pad2.person_id and pad1.person_address_id=pad2.id  
                          ) pad3 on pad3.person_id=inicio_real.patient_id 
                          left join( 
                          select pn1.*  from person_name pn1  
                          inner join (  
                          select person_id,min(person_name_id) id   from person_name  
                          where voided=0  
                          group by person_id  
                          ) pn2  
                          where pn1.person_id=pn2.person_id and pn1.person_name_id=pn2.id  
                          ) pn on pn.person_id=inicio_real.patient_id 
                          left join  ( 
                          select pid1.*  from patient_identifier pid1  
                          inner join  (  
                          select patient_id,min(patient_identifier_id) id  from patient_identifier  
                          where voided=0  
                          group by patient_id  
                          ) pid2 
                          where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id  
                          ) pid on pid.patient_id=inicio_real.patient_id  

                          left join
                          (
                           select preg_or_lac.patient_id, preg_or_lac.data_consulta,if(preg_or_lac.orderF=1,'Grávida','Lactante') as PREG_LAC from 
                           (
                          select final.patient_id,final.data_consulta,final.orderF 

                          from 
                          (
                           select p.patient_id,max(e.encounter_datetime) data_consulta, 1 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           where e.encounter_type in(5,6) and o.concept_id=1982 and o.value_coded=1065 and p.voided=0 and e.voided=0 and o.voided=0 
                           and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 9 MONTH) and CURDATE()
                           group by p.patient_id

                           union

                           select p.patient_id,max(e.encounter_datetime) data_consulta,1 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           where e.encounter_type in(5,6) and o.concept_id=1279 and p.voided=0 and e.voided=0 and o.voided=0 
                           and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 9 MONTH) and CURDATE()
                           group by p.patient_id

                           union

                           select p.patient_id,max(o.value_datetime) data_consulta,1 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           where e.encounter_type in(5,6) and o.concept_id=1600 and p.voided=0 and e.voided=0 and o.voided=0 
                           and o.value_datetime between date_sub(CURDATE(), INTERVAL 9 MONTH) and CURDATE()
                           group by p.patient_id

                           union

                           select p.patient_id,max(e.encounter_datetime) data_consulta,1 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           where e.encounter_type in(6) and o.concept_id=6334 and o.value_coded=6331 and p.voided=0 and e.voided=0 and o.voided=0 
                           and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 9 MONTH) and CURDATE()
                           group by p.patient_id

                           union

                           SELECT p.patient_id,pg.date_enrolled data_consulta,1 orderF FROM patient p
                           INNER JOIN patient_program pg ON p.patient_id=pg.patient_id 
                           INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id  
                           WHERE pg.program_id=8 AND (ps.start_date IS NOT NULL AND ps.end_date IS NULL and ps.voided = 0) 
                           and pg.date_enrolled between date_sub(CURDATE(), INTERVAL 9 MONTH) and CURDATE() 
                           GROUP BY p.patient_id

                           union

                           select p.patient_id,max(e.encounter_datetime) data_consulta,1 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           inner join obs obsInicio on obsInicio.encounter_id=e.encounter_id
                           where e.encounter_type in(53) and o.concept_id=1982 and o.value_coded=1065 and p.voided=0 and e.voided=0 and o.voided=0 and obsInicio.voided=0
                           and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 9 MONTH) and CURDATE()
                           and obsInicio.value_datetime between date_sub(CURDATE(), INTERVAL 9 MONTH) and CURDATE()
                           group by p.patient_id

                           union


                           select p.patient_id,max(e.encounter_datetime) data_consulta,1 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           where e.encounter_type in(6) and o.concept_id=1982 and o.value_coded=1065 and p.voided=0 and e.voided=0 and o.voided=0 
                           and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 9 MONTH) and CURDATE()
                           group by p.patient_id

                           union

                           select p.patient_id,max(e.encounter_datetime) data_consulta,1 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           inner join obs obsCv on obsCv.encounter_id=e.encounter_id
                           where e.encounter_type in(51) and o.concept_id=1982 and o.value_coded=1065 and p.voided=0 and e.voided=0 and o.voided=0 and obsCv.voided=0
                           and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 9 MONTH) and CURDATE()
                           and obsCv.value_datetime between date_sub(CURDATE(), INTERVAL 9 MONTH) and CURDATE()
                           group by p.patient_id
                      
                           union

                           select p.patient_id,max(o.value_datetime) data_consulta,2 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           where e.encounter_type in(5,6) and o.concept_id=5599 and p.voided=0 and e.voided=0 and o.voided=0 
                           and o.value_datetime between date_sub(CURDATE(), INTERVAL 18 MONTH) and CURDATE()
                           group by p.patient_id

                           union

                           select p.patient_id,max(e.encounter_datetime) data_consulta,2 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           where e.encounter_type in(6) and o.concept_id=6332 and o.value_coded=1065 and p.voided=0 and e.voided=0 and o.voided=0 
                           and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 18 MONTH) and CURDATE()
                           group by p.patient_id

                           union

                           select p.patient_id,max(e.encounter_datetime) data_consulta,2 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           where e.encounter_type in(5,6) and o.concept_id=6334 and o.value_coded=6332 and p.voided=0 and e.voided=0 and o.voided=0 
                           and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 18 MONTH) and CURDATE()
                           group by p.patient_id

                           union

                           SELECT p.patient_id,pg.date_enrolled data_consulta,2 orderF FROM patient p
                           INNER JOIN patient_program pg ON p.patient_id=pg.patient_id 
                           INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id  
                           WHERE  pg.program_id=8  and ps.voided = 0 AND ps.state=27
                           and ps.start_date between date_sub(CURDATE(), INTERVAL 18 MONTH) and CURDATE()
                           GROUP BY p.patient_id

                           union

                           select p.patient_id,max(e.encounter_datetime) data_consulta,2 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           inner join obs obsInicio on obsInicio.encounter_id=e.encounter_id
                           where e.encounter_type in(53) and o.concept_id=6332 and o.value_coded=1065 and p.voided=0 and e.voided=0 and o.voided=0 
                           and obsInicio.voided=0 and obsInicio.concept_id=1190
                           and obsInicio.value_datetime between date_sub(CURDATE(), INTERVAL 18 MONTH) and CURDATE() 
                           group by p.patient_id

                           union

                           select p.patient_id,max(e.encounter_datetime) data_consulta,2 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           inner join obs obsCv on obsCv.encounter_id=e.encounter_id
                           where e.encounter_type in(51) and o.concept_id=6332 and o.value_coded=1065 and p.voided=0 and e.voided=0 and o.voided=0 
                           and obsCv.voided=0 and obsCv.concept_id=23821
                           and obsCv.value_datetime between date_sub(CURDATE(), INTERVAL 18 MONTH) and CURDATE()
                           group by p.patient_id
                              )final
                             order by patient_id,data_consulta desc,orderF
                         )preg_or_lac
                          group by preg_or_lac.patient_id
                        ) preg_or_lac on preg_or_lac.patient_id=inicio_real.patient_id

                        left join

                        ( 

                             SELECT tb.patient_id from

                             (

                               select p.patient_id,max(o.value_datetime) data_consulta  from patient p 
                               inner join encounter e on e.patient_id=p.patient_id
                               inner join obs o on o.encounter_id=e.encounter_id
                               where e.encounter_type in(6,9) and o.concept_id=1113 and p.voided=0 and e.voided=0 and o.voided=0 
                               and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 7 MONTH) and CURDATE()
                               group by p.patient_id

                               union  


                               SELECT p.patient_id,pg.date_enrolled data_consulta FROM patient p
                               INNER JOIN patient_program pg ON p.patient_id=pg.patient_id 
                               INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id  
                               WHERE pg.program_id=5 AND (ps.start_date IS NOT NULL AND ps.end_date IS NULL and ps.voided = 0) 
                               and pg.date_enrolled between date_sub(CURDATE(), INTERVAL 7 MONTH) and CURDATE() 
                               GROUP BY p.patient_id

                               union

                               select p.patient_id,max(e.encounter_datetime) data_consulta from patient p 
                               inner join encounter e on e.patient_id=p.patient_id
                               inner join obs o on o.encounter_id=e.encounter_id
                               where e.encounter_type in(53) and o.concept_id=42 and o.value_coded=1065 and p.voided=0 and e.voided=0 and o.voided=0 
                               and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 7 MONTH) and CURDATE()
                               group by p.patient_id

                               union

                               select p.patient_id,max(e.encounter_datetime) data_consulta  from patient p 
                               inner join encounter e on e.patient_id=p.patient_id
                               inner join obs o on o.encounter_id=e.encounter_id
                               where e.encounter_type in(6) and o.concept_id=1268 and o.value_coded=1256 and p.voided=0 and e.voided=0 and o.voided=0 
                               and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 7 MONTH) and CURDATE()
                               group by p.patient_id

                               union

                               select p.patient_id,max(e.encounter_datetime) data_consulta  from patient p 
                               inner join encounter e on e.patient_id=p.patient_id
                               inner join obs o on o.encounter_id=e.encounter_id
                               where e.encounter_type in(6) and o.concept_id in (23761) and o.value_coded in(1065) and p.voided=0 and e.voided=0 and o.voided=0 
                               and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 7 MONTH) and CURDATE()
                               group by p.patient_id

                               union

                               select p.patient_id,max(e.encounter_datetime) data_consulta  from patient p 
                               inner join encounter e on e.patient_id=p.patient_id
                               inner join obs o on o.encounter_id=e.encounter_id
                               where e.encounter_type in(6) and o.concept_id in (23722) and o.value_coded in(23723,23774,23951,307,12) and p.voided=0 and e.voided=0 and o.voided=0 
                               and e.encounter_datetime between date_sub(CURDATE(), INTERVAL 7 MONTH) and CURDATE()
                               group by p.patient_id

                               )tb
                        ) tbFinal on tbFinal.patient_id=inicio_real.patient_id


                        left join
                        (
                           select fila.patient_id, fila.data_levantamento,obs_fila.value_datetime data_proximo_levantamento from (  
                           select p.patient_id,max(e.encounter_datetime) as data_levantamento from patient p 
                           inner join encounter e on p.patient_id=e.patient_id 
                           where encounter_type=18 and e.encounter_datetime <=:evaluationDate and e.location_id=:location and e.voided=0 and p.voided=0 
                           group by p.patient_id 
                           )fila 
                           inner join obs obs_fila on obs_fila.person_id=fila.patient_id 
                           where obs_fila.voided=0 and obs_fila.concept_id=5096 and fila.data_levantamento=obs_fila.obs_datetime

                        )fila on fila.patient_id=inicio_real.patient_id


                        left join

                        (
                        select p.patient_id,max(o.value_datetime) data_levantamento_recepcao, date_add(max(o.value_datetime), INTERVAL 30 day) data_proximo_levantamento_recepcao 
                        from patient p inner join encounter e on p.patient_id = e.patient_id 
                        inner join obs o on o.encounter_id = e.encounter_id 
                        where  e.voided = 0 and p.voided = 0 and o.value_datetime <= :evaluationDate and o.voided = 0 and o.concept_id = 23866 and e.encounter_type=52 and e.location_id=:location 
                        group by p.patient_id

                        )recepcao on recepcao.patient_id=inicio_real.patient_id

                        left join
                        (
                           select fila.patient_id, fila.data_seguimento,obs_seguimento.value_datetime data_proximo_seguimento from (  
                           select p.patient_id,max(e.encounter_datetime) as data_seguimento from patient p 
                           inner join encounter e on p.patient_id=e.patient_id 
                           where encounter_type in(6,9) and e.encounter_datetime <=:evaluationDate and e.location_id=:location and e.voided=0 and p.voided=0 
                           group by p.patient_id 
                           )fila 
                           inner join obs obs_seguimento on obs_seguimento.person_id=fila.patient_id 
                           where obs_seguimento.voided=0 and obs_seguimento.concept_id=1410 and fila.data_seguimento=obs_seguimento.obs_datetime

                        ) seguimento on seguimento.patient_id=inicio_real.patient_id

                        left join  patient_program pg ON p.person_id = pg.patient_id and pg.program_id = 2 and pg.location_id=:location
                        left join  patient_state ps ON pg.patient_program_id = ps.patient_program_id and ps.start_date IS NOT NULL AND ps.end_date IS NULL and ps.start_date<=:evaluationDate

                        left join

                        (
                           select 
                             p.patient_id,max(e.encounter_datetime) as encounter_datetime,   
                             case o.value_coded
                             when 1366  then 'OBITO '
                             when 1706  then 'TRANSFERIDO PARA'
                             when 23863 then 'AUTO TRASFERENCIA'
                             else null end as state_home_card
                         from  patient p 
                             inner join encounter e on e.patient_id=p.patient_id
                             inner join obs o on o.encounter_id=e.encounter_id
                         where  o.voided=0 and o.concept_id in(2031,23944) and e.encounter_type in (21) and e.voided=0 and e.location_id=:location and e.encounter_datetime<=:evaluationDate
                         GROUP BY p.patient_id 
                        ) homeCardVisit on homeCardVisit.patient_id=inicio_real.patient_id

                        left join

                        (
                           select 
                             p.patient_id,max(encounter_datetime) as encounter_datetime,   
                             case o.value_coded
                             when 1366  then  'OBITO '
                             when 1706  then  'TRANSFERIDO PARA'
                             when 1707  then  'ABANDONO'
                             when 1709  then  'SUSPENSO'
                             else null end as state_fr
                         from  patient p 
                             inner join encounter e on e.patient_id=p.patient_id
                             inner join obs o on o.encounter_id=e.encounter_id
                         where   o.voided=0 and o.concept_id in(6272) and e.encounter_type in (53) and e.voided=0 and e.location_id=:location and e.encounter_datetime<=:evaluationDate
                         GROUP BY p.patient_id 

                        )FR on FR.patient_id=inicio_real.patient_id

                        left join
                        (
                           select 
                             p.patient_id,max(encounter_datetime) as encounter_datetime,   
                             case o.value_coded
                             when 1366  then  'OBITO '
                             when 1706  then  'TRANSFERIDO PARA'
                             when 1707  then  'ABANDONO'
                             when 1709  then  'SUSPENSO'
                             else null end as state_fc
                         from  patient p 
                             inner join encounter e on e.patient_id=p.patient_id
                             inner join obs o on o.encounter_id=e.encounter_id
                         where   o.voided=0 and o.concept_id in(6273) and e.encounter_type in (6) and e.voided=0 and e.location_id=:location and e.encounter_datetime<=:evaluationDate
                         GROUP BY p.patient_id 
 
                        )FC on FC.patient_id=inicio_real.patient_id


                         group by inicio_real.patient_id
                       
                       )inicio 

                      where inicio.data_inicio between :startDate and :endDate

                      group by inicio.patient_id
