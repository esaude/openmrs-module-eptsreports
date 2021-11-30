         select inicio.patient_id as patient_id
                       from 
                       ( 
                          Select 
                          inicio_real.patient_id,min(data_inicio) data_inicio
                          from 
                          ( 
                          Select p.patient_id,min(e.encounter_datetime) data_inicio from patient p  
                          inner join encounter e on p.patient_id=e.patient_id  
                          inner join obs o on o.encounter_id=e.encounter_id 
                          where e.voided=0 and o.voided=0 and p.voided=0 and  
                          e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and  
                          e.encounter_datetime<=:cohorEndDate and e.location_id=:location 
                          group by p.patient_id 
                          union 
                          Select p.patient_id,min(value_datetime) data_inicio from patient p 
                          inner join encounter e on p.patient_id=e.patient_id 
                          inner join obs o on e.encounter_id=o.encounter_id 
                          where  p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and  
                          o.concept_id=1190 and o.value_datetime is not null and  
                          o.value_datetime<=:cohorEndDate and e.location_id=:location 
                          group by p.patient_id 
                          union 
                          select pg.patient_id,min(date_enrolled) data_inicio  from patient p 
                          inner join patient_program pg on p.patient_id=pg.patient_id 
                          where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:cohorEndDate and location_id=:location 
                          group by pg.patient_id 
                          union 
                          SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio  FROM   patient p 
                          inner join encounter e on p.patient_id=e.patient_id 
                          WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:cohorEndDate and e.location_id=:location 
                          GROUP BY p.patient_id 
                          union 
                          Select p.patient_id,min(value_datetime) data_inicio from patient p 
                          inner join encounter e on p.patient_id=e.patient_id 
                          inner join obs o on e.encounter_id=o.encounter_id 
                          where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  
                          o.concept_id=23866 and o.value_datetime is not null and  
                          o.value_datetime<=:cohorEndDate and e.location_id=:location 
                          group by p.patient_id 
                          ) inicio_real 
                           

                         group by inicio_real.patient_id
                       
                       )inicio 

                      where inicio.data_inicio between :cohortStartDate and :cohorEndDate

                      group by inicio.patient_id
