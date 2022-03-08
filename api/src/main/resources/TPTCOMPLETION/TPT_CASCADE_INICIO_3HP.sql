            select patient_id  																																
            from																												 								
            	(	select inicio.patient_id, inicio.data_inicio_3HP	 																						
            		from 																																		
            			(	select p.patient_id, e.encounter_datetime data_inicio_3HP																			
            				from	patient p																													
            					inner join encounter e on p.patient_id=e.patient_id																				
            					inner join obs o on o.encounter_id=e.encounter_id																				
            				where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and o.concept_id=1719 and o.value_coded=23954 			
            					and e.encounter_datetime < :endDate and e.location_id= :location

                            union
                        

                        -- Acreicentando novas fontes para o inicio 3HP na ficha resumo e ficha clinica

                        select p.patient_id,obsInicio3hp.value_datetime data_inicio_3HP                                                                           
                            from    patient p                                                                                                                   
                                inner join encounter e on p.patient_id=e.patient_id                                                                             
                                inner join obs o on o.encounter_id=e.encounter_id    
                                inner join obs obsInicio3hp on  obsInicio3hp.encounter_id=e.encounter_id                                                                          
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type =53 and o.concept_id=23985 and o.value_coded=23954          
                                and obsInicio3hp.value_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=165326

                                union

                        select p.patient_id, e.encounter_datetime data_inicio_3HP                                                                           
                            from    patient p                                                                                                                   
                                inner join encounter e on p.patient_id=e.patient_id                                                                             
                                inner join obs o on o.encounter_id=e.encounter_id    
                                inner join obs obsInicio3hp on  obsInicio3hp.encounter_id=e.encounter_id                                                                          
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type =6 and o.concept_id=23985 and o.value_coded=23954          
                                and e.encounter_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=165308 and obsInicio3hp.value_coded=1256

            			) inicio 																																		
            			
                        left join 																																
            			
                        ( 																																		
            				select p.patient_id, e.encounter_datetime data_inicio_3HP																			
            				from	patient p																													
            					inner join encounter e on p.patient_id=e.patient_id																				
            					inner join obs o on o.encounter_id=e.encounter_id																				
            				where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and o.concept_id=1719 and o.value_coded=23954 			
            					and e.encounter_datetime < :endDate and e.location_id= :location	 															
            				union 																																
            				select p.patient_id, e.encounter_datetime data_inicio_3HP																			
            				from	patient p																												 	
            					inner join encounter e on p.patient_id=e.patient_id																			 	
            					inner join obs o on o.encounter_id=e.encounter_id 																				
            				where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=60  and o.concept_id=23985 and o.value_coded in (23954,23984)  	
            					and	e.encounter_datetime < :endDate and e.location_id= :location

                            union

                        -- Acreicentando novas fontes para o inicio 3HP na ficha resumo e ficha clinica para determinar o inicio anterior

                        select p.patient_id, obsInicio3hp.value_datetime data_inicio_3HP                                                                           
                            from    patient p                                                                                                                   
                                inner join encounter e on p.patient_id=e.patient_id                                                                             
                                inner join obs o on o.encounter_id=e.encounter_id    
                                inner join obs obsInicio3hp on  obsInicio3hp.encounter_id=e.encounter_id                                                                          
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type =53 and o.concept_id=23985 and o.value_coded=23954          
                                and obsInicio3hp.value_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=165326

                                union

                        select p.patient_id, e.encounter_datetime data_inicio_3HP                                                                           
                            from    patient p                                                                                                                   
                                inner join encounter e on p.patient_id=e.patient_id                                                                             
                                inner join obs o on o.encounter_id=e.encounter_id    
                                inner join obs obsInicio3hp on  obsInicio3hp.encounter_id=e.encounter_id                                                                          
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type =6 and o.concept_id=23985 and o.value_coded=23954          
                                and e.encounter_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=165308 and obsInicio3hp.value_coded=1256
		 														
            			)  inicio_anterior 

            			on inicio_anterior.patient_id = inicio.patient_id   																					
            			and inicio_anterior.data_inicio_3HP between (inicio.data_inicio_3HP - INTERVAL 4 MONTH) and (inicio.data_inicio_3HP - INTERVAL 1 day) 	
            		where inicio_anterior.patient_id is null 																									
            		
                    union

            		select p.patient_id,e.encounter_datetime data_inicio_3HP  																					
            		from patient p														 			  															
            			inner join encounter e on p.patient_id=e.patient_id																				 		
            			inner join obs o on o.encounter_id=e.encounter_id		 																				
            			inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																
            		where e.voided=0 and p.voided=0 and e.encounter_datetime < :endDate	 			  															
            			and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	  		
            			and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 							
            		group by p.patient_id	 																													
            		
                    union																																 		
            		
                    select inicio.patient_id, inicio.data_inicio_3HP	 																						
            		from 																																		
            			(	select p.patient_id, e.encounter_datetime data_inicio_3HP																			
            				from	patient p																												 	
            					inner join encounter e on p.patient_id=e.patient_id																			 	
            					inner join obs o on o.encounter_id=e.encounter_id 																				
            					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														
            				where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=60  and o.concept_id=23985 and o.value_coded in (23954,23984)  	
            					and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257,1267)  					
            					and	e.encounter_datetime < :endDate and e.location_id= :location 																
            				union 																																
            				select p.patient_id, e.encounter_datetime data_inicio_3HP																			
            				from	patient p																												 	
            					inner join encounter e on p.patient_id=e.patient_id																			 	
            					inner join obs o on o.encounter_id=e.encounter_id 																				
            					left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
            						and seguimentoTPT.concept_id =23987  																						
            						and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
            						and seguimentoTPT.voided =0)																			 					
            				where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=60  and o.concept_id=23985 and o.value_coded in (23954,23984)  	
            					and	e.encounter_datetime < :endDate and e.location_id =:location 																
            					and seguimentoTPT.concept_id is null 																							
            			) inicio 																																		
            		
                    left join

            		( 																																			
            			select p.patient_id, e.encounter_datetime data_inicio_3HP																			 	
            			from	patient p																												 		
            				inner join encounter e on p.patient_id=e.patient_id																			 		
            				inner join obs o on o.encounter_id=e.encounter_id																			 		
            			where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and o.concept_id=1719 and o.value_coded=23954 		 		
            				and e.encounter_datetime < :endDate and e.location_id= :location 																	
            			union 																																	
            			select p.patient_id, e.encounter_datetime data_inicio_3HP																			 	
            			from	patient p																												 		
            				inner join encounter e on p.patient_id=e.patient_id																			 		
            				inner join obs o on o.encounter_id=e.encounter_id 																					
            			where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=60  and o.concept_id=23985 and o.value_coded in (23954,23984)  		
            				and	e.encounter_datetime < :endDate and e.location_id= :location

                            union

                        -- Acreicentando novas fontes para o inicio 3HP na ficha resumo e ficha clinica

                        select p.patient_id, obsInicio3hp.value_datetime data_inicio_3HP                                                                           
                            from    patient p                                                                                                                   
                                inner join encounter e on p.patient_id=e.patient_id                                                                             
                                inner join obs o on o.encounter_id=e.encounter_id    
                                inner join obs obsInicio3hp on  obsInicio3hp.encounter_id=e.encounter_id                                                                          
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type =53 and o.concept_id=23985 and o.value_coded=23954          
                                and obsInicio3hp.value_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=165326

                                union

                        select p.patient_id, e.encounter_datetime data_inicio_3HP                                                                           
                            from    patient p                                                                                                                   
                                inner join encounter e on p.patient_id=e.patient_id                                                                             
                                inner join obs o on o.encounter_id=e.encounter_id    
                                inner join obs obsInicio3hp on  obsInicio3hp.encounter_id=e.encounter_id                                                                          
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type =6 and o.concept_id=23985 and o.value_coded=23954          
                                and e.encounter_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=165308 and obsInicio3hp.value_coded=1256
		 															
            		) inicio_anterior 																															
            			on inicio_anterior.patient_id = inicio.patient_id   																					
            			and inicio_anterior.data_inicio_3HP between (inicio.data_inicio_3HP - INTERVAL 4 MONTH) and (inicio.data_inicio_3HP - INTERVAL 1 day) 	
            		where inicio_anterior.patient_id is null		 																							
            	
                ) inicio_3HP 																																		;
