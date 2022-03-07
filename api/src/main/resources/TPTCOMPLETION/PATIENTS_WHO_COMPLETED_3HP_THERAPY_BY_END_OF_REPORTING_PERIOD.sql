            select inicio_3HP.patient_id  																														
            from																												 									
            	(	select inicio.patient_id, inicio.data_inicio_3HP	 																							
            		from 																																			
            			(	
                    select p.patient_id, e.encounter_datetime data_inicio_3HP																			 	
            				from	patient p																												 		
            					inner join encounter e on p.patient_id=e.patient_id																			 		
            					inner join obs o on o.encounter_id=e.encounter_id																			 		
            				where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and o.concept_id=1719 and o.value_coded=23954 		 		
            					and e.encounter_datetime < :endDate and e.location_id= :location	
                      
                      union

                       -- Acreicentando novas fontes para o inicio 3HP na ficha resumo e ficha clinica

                        select p.patient_id, obsInicio3hp.value_datetime data_inicio_3HP                                                                           
                            from    patient p                                                                                                                   
                                inner join encounter e on p.patient_id=e.patient_id                                                                             
                                inner join obs o on o.encounter_id=e.encounter_id    
                                inner join obs obsInicio3hp on  obsInicio3hp.encounter_id=e.encounter_id                                                                          
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type =53 and o.concept_id=23985 and o.value_coded=23954          
                                and e.encounter_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=6128

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

                    -- Acreicentando novas fontes para o inicio 3HP na ficha resumo e ficha clinica

                        select p.patient_id, obsInicio3hp.value_datetime data_inicio_3HP                                                                           
                            from    patient p                                                                                                                   
                                inner join encounter e on p.patient_id=e.patient_id                                                                             
                                inner join obs o on o.encounter_id=e.encounter_id    
                                inner join obs obsInicio3hp on  obsInicio3hp.encounter_id=e.encounter_id                                                                          
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type =53 and o.concept_id=23985 and o.value_coded=23954          
                                and e.encounter_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=6128

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
            	) inicio_3HP 																																			
            	inner join encounter e on e.patient_id=inicio_3HP.patient_id                                                                                        
            	inner join obs o on o.encounter_id=e.encounter_id                                                                                                   
            where e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and o.concept_id=1719 and o.value_coded=23954                                          	
            		and e.encounter_datetime between inicio_3HP.data_inicio_3HP and (inicio_3HP.data_inicio_3HP + INTERVAL 4 month) and e.location_id= :location    
            		group by inicio_3HP.patient_id, e.encounter_datetime having count(*)>=3   

            union      																
                select inicio.patient_id, inicio.data_inicio_3HP                                                                                                
                    from                                                                                                                                            
                        (   
                    select p.patient_id, e.encounter_datetime data_inicio_3HP                                                                               
                            from    patient p                                                                                                                       
                                inner join encounter e on p.patient_id=e.patient_id                                                                                 
                                inner join obs o on o.encounter_id=e.encounter_id                                                                                   
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and o.concept_id=1719 and o.value_coded=23954              
                                and e.encounter_datetime < :endDate and e.location_id= :location    
                      
                      union

                       -- Acreicentando novas fontes para o inicio 3HP na ficha resumo e ficha clinica

                        select p.patient_id, obsInicio3hp.value_datetime data_inicio_3HP                                                                           
                            from    patient p                                                                                                                   
                                inner join encounter e on p.patient_id=e.patient_id                                                                             
                                inner join obs o on o.encounter_id=e.encounter_id    
                                inner join obs obsInicio3hp on  obsInicio3hp.encounter_id=e.encounter_id                                                                          
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type =53 and o.concept_id=23985 and o.value_coded=23954          
                                and e.encounter_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=6128

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
                            from    patient p                                                                                                                       
                                inner join encounter e on p.patient_id=e.patient_id                                                                                 
                                inner join obs o on o.encounter_id=e.encounter_id                                                                                   
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and o.concept_id=1719 and o.value_coded=23954              
                                and e.encounter_datetime < :endDate and e.location_id= :location                                                                    
                            
                    union                                                                                                                                   
                            
                    select p.patient_id, e.encounter_datetime data_inicio_3HP                                                                               
                            from    patient p                                                                                                                       
                                inner join encounter e on p.patient_id=e.patient_id                                                                                 
                                inner join obs o on o.encounter_id=e.encounter_id                                                                                   
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=60  and o.concept_id=23985 and o.value_coded in (23954,23984)       
                                and e.encounter_datetime < :endDate and e.location_id= :location    
                      
                      union

                    -- Acreicentando novas fontes para o inicio 3HP na ficha resumo e ficha clinica

                        select p.patient_id, obsInicio3hp.value_datetime data_inicio_3HP                                                                           
                            from    patient p                                                                                                                   
                                inner join encounter e on p.patient_id=e.patient_id                                                                             
                                inner join obs o on o.encounter_id=e.encounter_id    
                                inner join obs obsInicio3hp on  obsInicio3hp.encounter_id=e.encounter_id                                                                          
                            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type =53 and o.concept_id=23985 and o.value_coded=23954          
                                and e.encounter_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=6128

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
                ) inicio_3HP   

                inner join 

                (   select patient_id, max(data_final_3hp) data_final_3hp   
                    from 
                        (   
                            -- Determinando o FIM 3HP Na Ficha Resumo e Ficha Clinica
                        
                     select p.patient_id,max(obsfim3HP.value_datetime) data_final_3hp 
                      from 
                        patient p 
                        inner join encounter e on p.patient_id = e.patient_id 
                        inner join obs o on o.encounter_id = e.encounter_id 
                        inner join obs obsfim3HP on obsfim3HP.encounter_id = e.encounter_id 
                      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=23954
                            and obsfim3HP.concept_id=6129 and  obsfim3HP.voided=0
                            and obsfim3HP.value_datetime between (:startDate - interval 6 month) and :endDate
                      group by p.patient_id     

                      union

                     select p.patient_id,max(e.encounter_datetime) data_final_3hp 
                      from 
                        patient p 
                        inner join encounter e on p.patient_id = e.patient_id 
                        inner join obs o on o.encounter_id = e.encounter_id 
                        inner join obs obsfim3HP on obsfim3HP.encounter_id = e.encounter_id 
                      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=23954
                            and obsfim3HP.concept_id=165308 and obsfim3HP.value_coded=1267 and  obsfim3HP.voided=0
                            and e.encounter_datetime between (:startDate - interval 6 month) and :endDate
                      group by p.patient_id         
         
                    ) endTPI group by patient_id 
                
                ) termino_3hp on inicio3hp.patient_id=termino_3hp.patient_id 
            where termino_3hp.data_final_3hp between inicio3hp.data_inicio_tpi + interval 86 day  and  inicio3hp.data_inicio_tpi + interval 365 day


            union 																																				
            
            select inicio_3HP.patient_id  																														
            from																												 									
            	(	 																																				
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
                                and e.encounter_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=6128

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
            	) inicio_3HP 																																			
            	
              inner join encounter e on e.patient_id= inicio_3HP.patient_id                                                                                       
            	inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id                                                                                         
            	inner join obs obsTipo on obsTipo.encounter_id=e.encounter_id                                                                                       
            where e.voided=0 and obs3hp.voided=0 and obsTipo.voided=0                                                                                            	
            	and e.encounter_type=60 and obs3hp.concept_id=23985 and obs3hp.value_coded in (23954,23984) and obsTipo.concept_id=23986 and obsTipo.value_coded=23720   
            	and e.encounter_datetime between inicio_3HP.data_inicio_3HP and (inicio_3HP.data_inicio_3HP + INTERVAL 4 month) and e.location_id= :location     	
            	group by inicio_3HP.patient_id, e.encounter_datetime having count(*)>=1    																			
            
            union 																																				
            
            select inicio_3HP.patient_id  																														
            from																												 									
            	(	 																																				
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
                                and e.encounter_datetime < :endDate and e.location_id= :location
                                and obsInicio3hp.voided=0 and obsInicio3hp.concept_id=6128

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
            	) inicio_3HP 																																			
            	inner join encounter e on e.patient_id= inicio_3HP.patient_id                                                                                       
            	inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id                                                                                         
            	inner join obs obsTipo on obsTipo.encounter_id=e.encounter_id                                                                                       
            where e.voided=0 and obs3hp.voided=0 and obsTipo.voided=0                                                                                             
            	and e.encounter_type=60 and obs3hp.concept_id=23985 and obs3hp.value_coded in (23954,23984) and obsTipo.concept_id=23986 and obsTipo.value_coded=1098  
            	and e.encounter_datetime between inicio_3HP.data_inicio_3HP and (inicio_3HP.data_inicio_3HP + INTERVAL 4 month) and e.location_id= :location    	
            	group by inicio_3HP.patient_id, e.encounter_datetime having count(*)>=3             																;
