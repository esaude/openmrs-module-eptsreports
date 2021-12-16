select final.patient_id from (
select DEN.patient_id, DEN.data_proximo_levantamento from ( 
            select defaulters.patient_id,defaulters.data_levantamento,defaulters.data_proximo_levantamento as data_proximo_levantamento from ( 
            select defaulters.patient_id,defaulters.data_levantamento as data_levantamento, max(defaulters.data_proximo_levantamento) data_proximo_levantamento from ( 
            select fila.patient_id, fila.data_levantamento,obs_fila.value_datetime data_proximo_levantamento from (   
            select p.patient_id,max(e.encounter_datetime) as data_levantamento from patient p  
            inner join encounter e on p.patient_id=e.patient_id  
            where encounter_type=18 and e.encounter_datetime <:startDate and e.location_id=:location and e.voided=0 and p.voided=0  
            group by p.patient_id  
            )fila  
            inner join obs obs_fila on obs_fila.person_id=fila.patient_id  
            where obs_fila.voided=0 and obs_fila.concept_id=5096 and fila.data_levantamento=obs_fila.obs_datetime 
            union 
            select p.patient_id,max(o.value_datetime) data_levantamento, date_add(max(o.value_datetime), INTERVAL 30 day)  data_proximo_levantamento  from patient p 
            inner join encounter e on p.patient_id = e.patient_id  
            inner join obs o on o.encounter_id = e.encounter_id  
            inner join obs obsLevantou on obsLevantou.encounter_id=e.encounter_id 
            where  e.voided = 0 and p.voided = 0 and o.value_datetime <:startDate and o.voided = 0 
            and obsLevantou.voided=0 and obsLevantou.concept_id=23865 and obsLevantou.value_coded = 1065 
            and o.concept_id = 23866 and e.encounter_type=52 and e.location_id=:location  
            group by p.patient_id 
            ) defaulters 
            group by defaulters.patient_id 
            )defaulters 
            where defaulters.data_proximo_levantamento between :startDate  AND :endDate 
            group by defaulters.patient_id 
            )DEN 
            inner join (select l.patient_id,l.data_levantamento from ( 
            select p.patient_id,e.encounter_datetime as data_levantamento from patient p 
            inner join encounter e on p.patient_id=e.patient_id 
            where encounter_type=18  and e.location_id=:location and e.voided=0 and p.voided=0 and e.encounter_datetime<=DATE_ADD(:endDate, INTERVAL 7 DAY)  
            union 
            select p.patient_id,o.value_datetime data_levantamento  from patient p  
            inner join encounter e on p.patient_id = e.patient_id   
            inner join obs o on o.encounter_id = e.encounter_id   
            inner join obs obsLevantou on obsLevantou.encounter_id=e.encounter_id  
            where  e.voided = 0 and p.voided = 0 and o.value_datetime <:startDate and o.voided = 0  
            and obsLevantou.voided=0 and obsLevantou.concept_id=23865 and obsLevantou.value_coded = 1065  
            and o.concept_id = 23866 and e.encounter_type=52 and e.location_id=:location and o.value_datetime<= DATE_ADD(:endDate, INTERVAL 7 DAY)  
            ) l 
            )l on l.patient_id=DEN.patient_id 
            where l.data_levantamento between DATE_SUB(DEN.data_proximo_levantamento, INTERVAL 7 DAY) and DATE_ADD(:endDate, INTERVAL 7 DAY)  
            
            group by DEN.patient_id having DATEDIFF(min(l.data_levantamento),DEN.data_proximo_levantamento) > 7 
            order by DEN.patient_id
            
            )final
