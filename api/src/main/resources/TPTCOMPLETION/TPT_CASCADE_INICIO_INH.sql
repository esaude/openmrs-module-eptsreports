                                                                                   
             select distinct inicio_inh.patient_id                                                                                                               
             from                                                                                                                                                
                 (  select p.patient_id, o.value_datetime data_inicio_inh                                                                                       
                     from patient p                                                                                                                              
                         inner join encounter e on p.patient_id=e.patient_id                                                                                     
                         inner join obs o on o.encounter_id=e.encounter_id                                                                                       
                     where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128                                      
                         and o.value_datetime < :endDate and e.location_id=:location                                                                             
                     
                     union                                                                                                                                       
                     
                     select p.patient_id, e.encounter_datetime data_inicio_tpi                                                                                   
                     from patient p                                                                                                                              
                         inner join encounter e on p.patient_id=e.patient_id                                                                                     
                         inner join obs o on o.encounter_id=e.encounter_id                                                                                       
                         inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id                                                               
                     where e.voided=0 and p.voided=0 and e.encounter_datetime < :endDate and e.location_id=:location                                             
                         and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)                            
                         and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60                                          
                     
                     union                                                                                                                                       
                     
                     select p.patient_id, e.encounter_datetime data_inicio_inh                                                                                   
                     from patient p                                                                                                                              
                         inner join encounter e on p.patient_id=e.patient_id                                                                                     
                          inner join obs o on o.encounter_id=e.encounter_id                                                                                      
                     where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  and o.concept_id=6122                                         
                         and o.value_coded=1256 and e.encounter_datetime < :endDate and e.location_id=:location                                                  
                     
                     union                                                                                                                                       
                     
                     (                                                                                                                                           
                         select inicio.patient_id, inicio.data_inicio_inh                                                                                        
                         from                                                                                                                                    
                             (  select p.patient_id, e.encounter_datetime data_inicio_inh                                                                       
                                 from patient p                                                                                                                  
                                     inner join encounter e on p.patient_id=e.patient_id                                                                         
                                     inner join obs o on o.encounter_id=e.encounter_id                                                                           
                                     inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id                                                   
                                 where e.voided=0 and p.voided=0 and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60  
                                     and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257)                     
                                     and e.encounter_datetime < :endDate and e.location_id=:location                                                             
                                 
                                 union                                                                                                                           
                                 
                                 select p.patient_id, e.encounter_datetime data_inicio_inh                                                                       
                                 from patient p                                                                                                                  
                                     inner join encounter e on p.patient_id=e.patient_id                                                                         
                                     inner join obs o on o.encounter_id=e.encounter_id                                                                           
                                      left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in(1256,1257,1705,1267) and seguimentoTPT.voided =0) 
                                 where e.voided=0 and p.voided=0 and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60  
                                     and e.encounter_datetime < :endDate and e.location_id=:location and seguimentoTPT.obs_id is null                            
                             ) inicio                                                                                                                                  
                         left join                                                                                                                               
                             ( select p.patient_id, o.value_datetime data_inicio_inh                                                                           
                                 from patient p                                                                                                                  
                                     inner join encounter e on p.patient_id=e.patient_id                                                                         
                                     inner join obs o on o.encounter_id=e.encounter_id                                                                           
                                 where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128                          
                                     and o.value_datetime < :endDate and e.location_id=:location                                                                 
                             ) inicio_sem_inh                                                                                                                          
                             on inicio.patient_id = inicio_sem_inh.patient_id                                                                                    
                         where inicio_sem_inh.patient_id is null                                                                                                 
                          ) 

                         union

                    -- Acreicentando as novas fontes da ficha clinica e ficha resumo para determinar o inicio INH

                        select p.patient_id,min(obsInicioINH.value_datetime) data_inicio_inh 
                          from 
                            patient p 
                            inner join encounter e on p.patient_id = e.patient_id 
                            inner join obs o on o.encounter_id = e.encounter_id 
                            inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
                          where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
                                and obsInicioINH.concept_id=165328 and obsInicioINH.voided=0
                                and obsInicioINH.value_datetime < :endDate
                         group by p.patient_id

                         union

                        select p.patient_id,min(e.encounter_datetime) data_inicio_inh 
                          from 
                            patient p 
                            inner join encounter e on p.patient_id = e.patient_id 
                            inner join obs o on o.encounter_id = e.encounter_id 
                            inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
                          where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
                                and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
                                and e.encounter_datetime between < :endDate
                          group by p.patient_id         
                         
                      ) inicio_inh                                                                                                                                         ;
