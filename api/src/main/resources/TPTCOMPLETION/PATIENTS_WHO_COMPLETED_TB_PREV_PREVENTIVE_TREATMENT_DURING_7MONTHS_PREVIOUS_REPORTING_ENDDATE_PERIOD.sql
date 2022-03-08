select inicio_tpt.*                 
from 
	(	select inicio3hp.patient_id                 
		from
			(	select inicio.patient_id,inicio.data_inicio_tpi 
				from
					(	select  p.patient_id,min(e.encounter_datetime) data_inicio_tpi  
						from patient p                     
							inner join encounter e on p.patient_id=e.patient_id  
							inner join obs o on o.encounter_id=e.encounter_id    
						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - interval 7 month) and :endDate 
							and  o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location    
							group by p.patient_id 

					  union

             			-- Neste bloco vamos buscar o inicio 3hp olhando novas fontes

	                 select p.patient_id,min(obsInicio3HP.value_datetime) data_inicio_tpi from  patient p 
				        inner join encounter e on p.patient_id = e.patient_id 
				        inner join obs o on o.encounter_id = e.encounter_id 
				        inner join obs obsInicio3HP on obsInicio3HP.encounter_id = e.encounter_id 
				      where e.voided=0  and p.voided=0  and obsInicio3HP.value_datetime between (:endDate - interval 7 month) and :endDate and 
				            o.voided=0  and o.concept_id = 23985  and o.value_coded = 23954 
				            and obsInicio3HP.concept_id=165326 and obsInicio3HP.voided=0 and 
				            e.encounter_type in (53) and e.location_id=:location 
				        group by p.patient_id 
				      
				      union 
				      
				      select p.patient_id,min(e.encounter_datetime) data_inicio_tpi from patient p 
				        inner join encounter e on p.patient_id = e.patient_id 
				        inner join obs o on o.encounter_id = e.encounter_id 
				        inner join obs obsInicio3HP on obsInicio3HP.encounter_id = e.encounter_id 
				      where e.voided=0  and p.voided=0  and e.encounter_datetime between (:endDate - interval 7 month) and :endDate and o.voided=0 and 
				            o.concept_id=23985 and o.value_coded=23954 
				            and obsInicio3HP.concept_id=165308 and obsInicio3HP.value_coded=1256 and obsInicio3HP.voided=0 
				            and e.encounter_type in (6) and e.location_id=:location 
				      group by p.patient_id 

				      union

				      select p.patient_id,min(e.encounter_datetime) data_inicio_tpi  from patient p 
				        inner join encounter e on p.patient_id = e.patient_id 
				        inner join obs o on o.encounter_id = e.encounter_id 
				      where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate and 
				      		o.voided=0 and o.concept_id=1719 and o.value_coded=165307  
				      		and e.encounter_type in (6)  and e.location_id=:location 
				      group by p.patient_id  
                 
					) inicio          
					
					left join         
					(       
						
					   select  p.patient_id,e.encounter_datetime data_inicio_tpi   
						from patient p                     
							inner join encounter e on p.patient_id=e.patient_id  
							inner join obs o on o.encounter_id=e.encounter_id    
						where   e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 17 MONTH) and :endDate  
							and o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location   
		 				
		 				union 																																 
	             		
	             		select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p														         
							inner join encounter e on p.patient_id=e.patient_id																				 		 
							inner join obs o on o.encounter_id=e.encounter_id																				         
						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - interval 17 month) and :endDate	         
							and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location

						union

						-- Neste bloco vamos buscar o inicio 3hp olhando novas fontes para exclusao

	                 select p.patient_id,min(obsInicio3HP.value_datetime) data_inicio_tpi from  patient p 
				        inner join encounter e on p.patient_id = e.patient_id 
				        inner join obs o on o.encounter_id = e.encounter_id 
				        inner join obs obsInicio3HP on obsInicio3HP.encounter_id = e.encounter_id 
				      where e.voided=0  and p.voided=0  and obsInicio3HP.value_datetime between (:startDate - interval 17 month) and :endDate and 
				            o.voided=0  and o.concept_id = 23985  and o.value_coded = 23954 
				            and obsInicio3HP.concept_id=165326 and obsInicio3HP.voided=0 and 
				            e.encounter_type in (53) and e.location_id=:location 
				        group by p.patient_id 
				      
				      union 
				      
				      select p.patient_id,min(e.encounter_datetime) data_inicio_tpi from patient p 
				        inner join encounter e on p.patient_id = e.patient_id 
				        inner join obs o on o.encounter_id = e.encounter_id 
				        inner join obs obsInicio3HP on obsInicio3HP.encounter_id = e.encounter_id 
				      where e.voided=0  and p.voided=0  and e.encounter_datetime between (:startDate - interval 17 month) and :endDate and o.voided=0 and 
				            o.concept_id=23985 and o.value_coded=23954 
				            and obsInicio3HP.concept_id=165308 and obsInicio3HP.value_coded=1256 and obsInicio3HP.voided=0 
				            and e.encounter_type in (6) and e.location_id=:location 
				      group by p.patient_id 

				      union

				      select p.patient_id,min(e.encounter_datetime) data_inicio_tpi  from patient p 
				        inner join encounter e on p.patient_id = e.patient_id 
				        inner join obs o on o.encounter_id = e.encounter_id 
				      where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - interval 17 month) and :endDate and 
				      		o.voided=0 and o.concept_id=1719 and o.value_coded=165307  
				      		and e.encounter_type in (6)  and e.location_id=:location 
				      group by p.patient_id  


		 			) inicioAnterior on inicio.patient_id=inicioAnterior.patient_id and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 4 MONTH)  
						and (inicio.data_inicio_tpi - INTERVAL 1 day)   
					where inicioAnterior.patient_id is null      
		     
		     ) inicio3hp     
			inner join encounter e on e.patient_id=inicio3hp.patient_id   
		    	inner join obs o on o.encounter_id=e.encounter_id    
		where e.voided=0 and o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and e.location_id=:location 
		 	and e.encounter_datetime between inicio3hp.data_inicio_tpi and (inicio3hp.data_inicio_tpi + INTERVAL 4 month)    
		     group by e.patient_id having count(*)>=3  

		union

		select inicio3hp.patient_id                    
		from
			(	select inicio_3HP.patient_id, inicio_3HP.data_inicio_tpi  
				from 														                                        						         
					(	select inicio_3HP.patient_id,	min(inicio_3HP.data_inicio_tpi) data_inicio_tpi 
						from 
							(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
								from patient p														 			 
									inner join encounter e on p.patient_id=e.patient_id																				 			 
									inner join obs o on o.encounter_id=e.encounter_id		 																					 
									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
								where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	 			 
									and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	  			 
									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 								 
									group by p.patient_id	                            																							 
								union																																	 
								select inicio.patient_id,inicio.data_inicio_tpi 
								from																					 
									(
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
										from patient p														 
											inner join encounter e on p.patient_id=e.patient_id																				 
											inner join obs o on o.encounter_id=e.encounter_id	 																			 
											inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
										where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	 
											and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	 
											and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257,1267) 					 
											group by p.patient_id	 																											 
										
										union 																																		 
										
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
										from patient p														 		 
											inner join encounter e on p.patient_id=e.patient_id																				 		 
											inner join obs o on o.encounter_id=e.encounter_id	 																					 
											left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
												and seguimentoTPT.concept_id =23987  																						
												and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
												and seguimentoTPT.voided =0)																									 
										where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	         
											and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	 	     
							  				and seguimentoTPT.obs_id is null  						 
											group by p.patient_id	 																													 
									) inicio 																																 		 
								
								left join 

								( select p.patient_id,e.encounter_datetime data_inicio_tpi 
									from patient p																 
										inner join encounter e on p.patient_id=e.patient_id																				 
										inner join obs o on o.encounter_id=e.encounter_id																				 
									where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 17 MONTH) and :endDate 
										and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location   
					          		
					          		union 																															 
					          		
					          		select p.patient_id,e.encounter_datetime data_inicio_tpi 
					          		from patient p														     
										inner join encounter e on p.patient_id=e.patient_id																				 
										inner join obs o on o.encounter_id=e.encounter_id																				 
									where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 17 MONTH) and :endDate  
										and o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location			 
									
									union

									-- Neste bloco vamos buscar o inicio 3hp olhando novas fontes para exclusao

				                 select p.patient_id,min(obsInicio3HP.value_datetime) data_inicio_tpi from  patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicio3HP on obsInicio3HP.encounter_id = e.encounter_id 
							      where e.voided=0  and p.voided=0  and obsInicio3HP.value_datetime between (:startDate - interval 17 month) and :endDate and 
							            o.voided=0  and o.concept_id = 23985  and o.value_coded = 23954 
							            and obsInicio3HP.concept_id=165326 and obsInicio3HP.voided=0 and 
							            e.encounter_type in (53) and e.location_id=:location 
							        group by p.patient_id 
							      
							      union 
							      
							      select p.patient_id,min(e.encounter_datetime) data_inicio_tpi from patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicio3HP on obsInicio3HP.encounter_id = e.encounter_id 
							      where e.voided=0  and p.voided=0  and e.encounter_datetime between (:startDate - interval 17 month) and :endDate and o.voided=0 and 
							            o.concept_id=23985 and o.value_coded=23954 
							            and obsInicio3HP.concept_id=165308 and obsInicio3HP.value_coded=1256 and obsInicio3HP.voided=0 
							            and e.encounter_type in (6) and e.location_id=:location 
							      group by p.patient_id 

							      union

							      select p.patient_id,min(e.encounter_datetime) data_inicio_tpi  from patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - interval 17 month) and :endDate and 
							      		o.voided=0 and o.concept_id=1719 and o.value_coded=165307  
							      		and e.encounter_type in (6)  and e.location_id=:location 
							      group by p.patient_id 

								) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id  																		 
									and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 4 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
								where inicioAnterior.patient_id is null																									 
							) inicio_3HP group by inicio_3HP.patient_id																									 																			 
					) inicio_3HP            
	 		) inicio3hp     
	     	inner join encounter e on e.patient_id=inicio3hp.patient_id   
	     	inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id   
	        	inner join obs obsTipo on obsTipo.encounter_id=e.encounter_id 
	    	where e.voided=0 and obs3hp.voided=0 and obsTipo.voided=0 and e.encounter_type=60 and e.location_id=:location  
			and e.encounter_datetime between inicio3hp.data_inicio_tpi and (inicio3hp.data_inicio_tpi + INTERVAL 4 month)  
			and obs3hp.concept_id=23985 and obs3hp.value_coded in (23954,23984) and obsTipo.concept_id=23986 and obsTipo.value_coded=1098       
	    		group by e.patient_id having count(*)>=3      
     	

     	union                
          
          select inicio3hp.patient_id                    
          from   
          	(	select inicio_3HP.patient_id, inicio_3HP.data_inicio_tpi  
				from 														                                        						         
					(	select inicio_3HP.patient_id,min(inicio_3HP.data_inicio_tpi) data_inicio_tpi 
						from 
							(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
								from patient p														 			 
									inner join encounter e on p.patient_id=e.patient_id																				 			 
									inner join obs o on o.encounter_id=e.encounter_id		 																					 
									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
								where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	 			 
									and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	  			 
									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 								 
									group by p.patient_id	                            																							 
								union																																	 
								select inicio.patient_id,inicio.data_inicio_tpi 
								from																					 
									(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
										from patient p														 
											inner join encounter e on p.patient_id=e.patient_id																				 
											inner join obs o on o.encounter_id=e.encounter_id	 																			 
											inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
										where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	 
											and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	 
											and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257,1267) 					 
											group by p.patient_id	 																											 
										union 																																		 
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
										from patient p														 		 
											inner join encounter e on p.patient_id=e.patient_id																				 		 
											inner join obs o on o.encounter_id=e.encounter_id	 																					 
											left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
												and seguimentoTPT.concept_id =23987  																						
												and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
												and seguimentoTPT.voided =0)							 
										where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	         
											and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	 	     
							  				and seguimentoTPT.obs_id is null 						 
											group by p.patient_id	 																													 
									) 
								inicio 																																 		 
								left join 																																 
								(	
									select p.patient_id,e.encounter_datetime data_inicio_tpi 
									from patient p																 
										inner join encounter e on p.patient_id=e.patient_id																				 
										inner join obs o on o.encounter_id=e.encounter_id																				 
									where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 17 MONTH) and :endDate 
										and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location   
					          		
					          		union 																															 
					          		

					          		select p.patient_id,e.encounter_datetime data_inicio_tpi 
					          		from patient p														     
										inner join encounter e on p.patient_id=e.patient_id																				 
										inner join obs o on o.encounter_id=e.encounter_id																				 
									where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 17 MONTH) and :endDate  
										and o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location			 
								
									union

								-- Neste bloco vamos buscar o inicio 3hp olhando novas fontes para exclusao

				                  select p.patient_id,min(obsInicio3HP.value_datetime) data_inicio_tpi from  patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicio3HP on obsInicio3HP.encounter_id = e.encounter_id 
							      where e.voided=0  and p.voided=0  and obsInicio3HP.value_datetime between (:startDate - interval 17 month) and :endDate and 
							            o.voided=0  and o.concept_id = 23985  and o.value_coded = 23954 
							            and obsInicio3HP.concept_id=165326 and obsInicio3HP.voided=0 and 
							            e.encounter_type in (53) and e.location_id=:location 
							        group by p.patient_id 
							      
							      union 
							      
							      select p.patient_id,min(e.encounter_datetime) data_inicio_tpi from patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicio3HP on obsInicio3HP.encounter_id = e.encounter_id 
							      where e.voided=0  and p.voided=0  and e.encounter_datetime between (:startDate - interval 17 month) and :endDate and o.voided=0 and 
							            o.concept_id=23985 and o.value_coded=23954 
							            and obsInicio3HP.concept_id=165308 and obsInicio3HP.value_coded=1256 and obsInicio3HP.voided=0 
							            and e.encounter_type in (6) and e.location_id=:location 
							      group by p.patient_id 

							      union

							      select p.patient_id,min(e.encounter_datetime) data_inicio_tpi  from patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - interval 17 month) and :endDate and 
							      		o.voided=0 and o.concept_id=1719 and o.value_coded=165307  
							      		and e.encounter_type in (6)  and e.location_id=:location 
							      group by p.patient_id 

								) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id  																		 
									and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 4 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
								where inicioAnterior.patient_id is null																									 
							) inicio_3HP group by inicio_3HP.patient_id																									 
																										 
					) inicio_3HP                  
 			) inicio3hp     
      		inner join encounter e on e.patient_id=inicio3hp.patient_id   
        		inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id   
        		inner join obs obsTipo on obsTipo.encounter_id=e.encounter_id 
        	where e.voided=0 and obs3hp.voided=0 and obsTipo.voided=0 and e.encounter_type=60 and e.location_id=:location 
        		and e.encounter_datetime between inicio3hp.data_inicio_tpi and :endDate  
			and obs3hp.concept_id=23985 and obs3hp.value_coded in (23954,23984) and obsTipo.concept_id=23986 
			and obsTipo.value_coded=23720      
        	 	 group by e.patient_id having count(*)>=1                
    		
    	union  
	     

	     -- Neste bloco buscamos o inicio INH

	     select inicio_tpi.patient_id                   
	     from
	     	(	select inicio_inh.* 
				from 														                                        						         
					(	select inicio_INH.patient_id,	min(inicio_INH.data_inicio_tpi) data_inicio_tpi 
						from 
							(	select inicio.patient_id,inicio.data_inicio_tpi 
								from
									(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													 
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id	 																			 
				   							inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257)	 						 
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location		 
				   							group by p.patient_id	 																											 
										union  																																		 
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													         
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id																				 
				   							left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
												and seguimentoTPT.concept_id =23987  																						
												and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
												and seguimentoTPT.voided =0)						 	
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   				 			and seguimentoTPT.obs_id is null 		         
				   							group by p.patient_id			                                                                                                     
				   					)inicio

				   				left join 																																 
				   				   (	
				   					select p.patient_id,e.encounter_datetime data_inicio_tpi 
				   					from patient p																 
				   						inner join encounter e on p.patient_id=e.patient_id																				 
				   						inner join obs o on o.encounter_id=e.encounter_id																				 
				   					where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 20 MONTH) and :endDate  
				   						and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   					
				   					union                                                                                                                                
				   					
				   					select  p.patient_id, o.value_datetime data_inicio_tpi 
				   					from patient p                                                                
										inner join encounter e on p.patient_id=e.patient_id                                                                                      
										inner join obs o on o.encounter_id=e.encounter_id                                                                                        
									where e.voided=0 and p.voided=0 and o.value_datetime between (:startDate - interval 20 month) and :endDate            
										and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location 

					       -- Estamos adicionando novas fontes da ficha resumo e ficha clinica tambem para exclusao caso o paciente tenha uma consulta INH no periodo anterior ao periodo em analise
				
									union 

									select p.patient_id,obsInicioINH.value_datetime data_inicio_tpi 
								      from 
								        patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
								      	    and obsInicioINH.value_datetime between (:startDate - interval 20 month) and :endDate 

								     union

								    select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
								      	    and e.encounter_datetime between (:startDate - interval 20 month) and :endDate 
                                 
				   				
				   				) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id  																		 
				   					and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
				   				where inicioAnterior.patient_id is null																									 
					   			
					   			union                                                                                                                                    
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p														     
									inner join encounter e on p.patient_id=e.patient_id																				             
									inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
								where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	             
									and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	                 
									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)                                 
								group by p.patient_id                                                                                                                            
					   			
					   			union																																	 
					   			
					   			select p.patient_id,min(o.value_datetime) data_inicio_tpi 
					   			from patient p																 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and o.value_datetime between (:endDate - interval 7 month) and :endDate 	     
					   				and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location								 
					   				group by p.patient_id																												 
					   			
					   			union 																																	 
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p															 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
					   				and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location			 
					   				group by p.patient_id

					   		    union

					   		   -- Acreicentando as novas fontes da ficha clinica e ficha resumo para determinar o inicio INH

				         	    select p.patient_id,min(obsInicioINH.value_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
							      	    and obsInicioINH.value_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							     group by p.patient_id

							     union

							    select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
							      	    and e.encounter_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							      group by p.patient_id 	    

					   		)inicio_INH group by inicio_INH.patient_id																									 
					) inicio_inh                         
			) inicio_tpi  

        		inner join 

        		(	select patient_id, max(data_final_tpi) data_final_tpi   
        			from 
        				(	

        				select  p.patient_id,max(o.value_datetime) data_final_tpi     
						from patient p            
				     		inner join encounter e on p.patient_id=e.patient_id  
				     		inner join obs o on o.encounter_id=e.encounter_id    
						where e.voided=0 and p.voided=0 and o.value_datetime between :startDate - interval 6 month and :endDate 
							and o.voided=0 and o.concept_id=6129 and e.encounter_type in (6,9,53) and e.location_id=:location   
							group by p.patient_id         
						
						union    
						
						select p.patient_id,max(e.encounter_datetime) data_final_tpi  
						from patient p            
  							inner join encounter e on p.patient_id=e.patient_id  
  							inner join obs o on o.encounter_id=e.encounter_id    
						where e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate - interval 6 month and :endDate 
							and o.voided=0 and o.concept_id=6122 and o.value_coded=1267 and e.encounter_type=6 and e.location_id=:location              
							group by p.patient_id  

				    -- Acreicentendo novas fontes para determinar o FIM de INH

					 union

				     select p.patient_id,max(obsfimINH.value_datetime) data_final_tpi 
				      from 
				        patient p 
				        inner join encounter e on p.patient_id = e.patient_id 
				        inner join obs o on o.encounter_id = e.encounter_id 
				        inner join obs obsfimINH on obsfimINH.encounter_id = e.encounter_id 
				      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
				      	    and obsfimINH.concept_id=165329 and  obsfimINH.voided=0
				      	    and obsfimINH.value_datetime between (:startDate - interval 6 month) and :endDate and e.location_id=:location
				      group by p.patient_id 	

				      union

				     select p.patient_id,max(e.encounter_datetime) data_final_tpi 
				      from 
				        patient p 
				        inner join encounter e on p.patient_id = e.patient_id 
				        inner join obs o on o.encounter_id = e.encounter_id 
				        inner join obs obsfimINH on obsfimINH.encounter_id = e.encounter_id 
				      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
				      	    and obsfimINH.concept_id=165308 and obsfimINH.value_coded=1267 and  obsfimINH.voided=0
				      	    and e.encounter_datetime between (:startDate - interval 6 month) and :endDate and e.location_id=:location
				      group by p.patient_id 	    
						       
					)endTPI group by patient_id 
        		) termino_tpi on inicio_tpi.patient_id=termino_tpi.patient_id 
        	where termino_tpi.data_final_tpi>=inicio_tpi.data_inicio_tpi + interval 173 day      
		

	   union

       select inicio_tpi.patient_id        
		from                 
			(	select inicio_inh.* 
				from 														                                        						         
					(	select inicio_INH.patient_id,	min(inicio_INH.data_inicio_tpi) data_inicio_tpi 
						from 
							(	select inicio.patient_id,inicio.data_inicio_tpi 
								from
									(	
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													 
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id	 																			 
				   							inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257)	 						 
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location		 
				   							group by p.patient_id	 																											 
										
										union  																																		 
										
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													         
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id																				 
				   							left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
												and seguimentoTPT.concept_id =23987  																						
												and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
												and seguimentoTPT.voided =0)						 	
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   				 			and seguimentoTPT.obs_id is null 		         
				   							group by p.patient_id			                                                                                                     
				   					)
				   				inicio																																	 
				   				left join 																																 
				   				(	
				   					select p.patient_id,e.encounter_datetime data_inicio_tpi 
				   					from patient p																 
				   						inner join encounter e on p.patient_id=e.patient_id																				 
				   						inner join obs o on o.encounter_id=e.encounter_id																				 
				   					where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 20 MONTH) and :endDate  
				   						and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   					
				   					union                                                                                                                                
				   					
				   					select  p.patient_id, o.value_datetime data_inicio_tpi 
				   					from patient p                                                                
										inner join encounter e on p.patient_id=e.patient_id                                                                                      
										inner join obs o on o.encounter_id=e.encounter_id                                                                                        
									where e.voided=0 and p.voided=0 and o.value_datetime between (:startDate - interval 20 month) and :endDate            
										and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location 

									-- Estamos adicionando novas fontes da ficha resumo e ficha clinica tambem para exclusao caso o paciente tenha uma consulta INH no periodo anterior ao periodo em analise
				
									union 

									select p.patient_id,obsInicioINH.value_datetime data_inicio_tpi 
								      from 
								        patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
								      	    and obsInicioINH.value_datetime between (:startDate - interval 20 month) and :endDate and e.location_id=:location

								     union

								    select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
								      	    and e.encounter_datetime between (:startDate - interval 20 month) and :endDate  and e.location_id=:location
                             
				   				) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id  																		 
				   					and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
				   				where inicioAnterior.patient_id is null																									 
					   		
					   			union                                                                                                                                    
					   		
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p														     
									inner join encounter e on p.patient_id=e.patient_id																				             
									inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
								where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	             
									and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	                 
									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)                                 
								group by p.patient_id                                                                                                                            
					   			
					   			union																																	 
					   			
					   			select p.patient_id,min(o.value_datetime) data_inicio_tpi 
					   			from patient p																 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and o.value_datetime between (:endDate - interval 7 month) and :endDate 	     
					   				and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location								 
					   				group by p.patient_id																												 
					   			
					   			union 																																	 
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p															 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
					   				and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location			 
					   				group by p.patient_id

					   		    union

					   		   -- Acreicentando as novas fontes da ficha clinica e ficha resumo para determinar o inicio INH

				         	    select p.patient_id,min(obsInicioINH.value_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
							      	    and obsInicioINH.value_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							     group by p.patient_id

							     union

							    select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
							      	    and e.encounter_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							      group by p.patient_id 	    																					 
					   		
					   		)inicio_INH group by inicio_INH.patient_id																									 
					) inicio_inh                    
			) inicio_tpi    
	     	inner join encounter e on e.patient_id=inicio_tpi.patient_id  
			inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id           
		where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) 
			and e.encounter_datetime between inicio_tpi.data_inicio_tpi and (inicio_tpi.data_inicio_tpi + INTERVAL 7 MONTH)  
			and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded in (1257,1065,1256) and e.location_id=:location  
	        	group by inicio_tpi.patient_id having count(*)>=5    
		
		union

       	select inicio_tpi.patient_id                   
       	from                 
       		(	select inicio_inh.* 
				from 														                                        						         
					(	select inicio_INH.patient_id,	min(inicio_INH.data_inicio_tpi) data_inicio_tpi 
						from 
							(	select inicio.patient_id,inicio.data_inicio_tpi 
								from
									(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													 
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id	 																			 
				   							inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257)	 						 
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location		 
				   							group by p.patient_id	 																											 
										union  																																		 
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													         
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id																				 
				   							left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
												and seguimentoTPT.concept_id =23987  																						
												and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
												and seguimentoTPT.voided =0)					 	
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   				 			and seguimentoTPT.obs_id is null 		         
				   							group by p.patient_id			                                                                                                     
				   					)
				   				inicio																																	 
				   				left join 																																 
				   				(	
				   					select p.patient_id,e.encounter_datetime data_inicio_tpi 
				   					from patient p																 
				   						inner join encounter e on p.patient_id=e.patient_id																				 
				   						inner join obs o on o.encounter_id=e.encounter_id																				 
				   					where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 20 MONTH) and :endDate  
				   						and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   					
				   					union                                                                                                                                
				   					
				   					select  p.patient_id, o.value_datetime data_inicio_tpi 
				   					from patient p                                                                
										inner join encounter e on p.patient_id=e.patient_id                                                                                      
										inner join obs o on o.encounter_id=e.encounter_id                                                                                        
									where e.voided=0 and p.voided=0 and o.value_datetime between (:startDate - interval 20 month) and :endDate            
										and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location 


								-- Estamos adicionando novas fontes da ficha resumo e ficha clinica tambem para exclusao caso o paciente tenha uma consulta INH no periodo anterior ao periodo em analise
				
									union 

									select p.patient_id,obsInicioINH.value_datetime data_inicio_tpi 
								      from 
								        patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
								      	    and obsInicioINH.value_datetime between (:startDate - interval 20 month) and :endDate and e.location_id=:location

								     union

								    select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
								      	    and e.encounter_datetime between (:startDate - interval 20 month) and :endDate  and e.location_id=:location
                                 				   				
				   				) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id  																		 
				   					and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
				   				where inicioAnterior.patient_id is null																									 
					   			
					   			union                                                                                                                                    
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p														     
									inner join encounter e on p.patient_id=e.patient_id																				             
									inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
								where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	             
									and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	                 
									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)                                 
								group by p.patient_id                                                                                                                            
					   			
					   			union																																	 
					   			
					   			select p.patient_id,min(o.value_datetime) data_inicio_tpi 
					   			from patient p																 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and o.value_datetime between (:endDate - interval 7 month) and :endDate 	     
					   				and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location								 
					   				group by p.patient_id																												 
					   			
					   			union 																																	 
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p															 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
					   				and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location			 
					   				group by p.patient_id

					   	        union

					   		   -- Acreicentando as novas fontes da ficha clinica e ficha resumo para determinar o inicio INH

				         	    select p.patient_id,min(obsInicioINH.value_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
							      	    and obsInicioINH.value_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							     group by p.patient_id

							     union

							    select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
							      	    and e.encounter_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							      group by p.patient_id 	    
																												 
					   		) inicio_INH group by inicio_INH.patient_id																									 
					) inicio_inh                    
			) inicio_tpi    
			inner join encounter e on e.patient_id=inicio_tpi.patient_id  
			inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id           
		where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type=60 
			and e.encounter_datetime between inicio_tpi.data_inicio_tpi and (inicio_tpi.data_inicio_tpi + INTERVAL 7 MONTH) 
			and obsLevTPI.concept_id=23985 and obsLevTPI.value_coded in (656,23982) and e.location_id=:location       
			group by inicio_tpi.patient_id having count(*)>=6      
     	
     	union

		select inicio_tpi.patient_id                   
		from	
			(	select inicio_inh.* 
				from 														                                        						         
					(	select inicio_INH.patient_id,	min(inicio_INH.data_inicio_tpi) data_inicio_tpi 
						from 
							(	select inicio.patient_id,inicio.data_inicio_tpi 
								from
									(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													 
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id	 																			 
				   							inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257)	 						 
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location		 
				   							group by p.patient_id	 																											 
										
										union  																																		 
										
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													         
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id																				 
				   							left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
												and seguimentoTPT.concept_id =23987  																						
												and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
												and seguimentoTPT.voided =0)					 	
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   				 			and seguimentoTPT.obs_id is null	         
				   							group by p.patient_id			                                                                                                     
				   					)inicio																																	 
				   				left join 																																 
				   				(	
				   					select p.patient_id,e.encounter_datetime data_inicio_tpi 
				   					from patient p																 
				   						inner join encounter e on p.patient_id=e.patient_id																				 
				   						inner join obs o on o.encounter_id=e.encounter_id																				 
				   					where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 20 MONTH) and :endDate  
				   						and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   					
				   					union                                                                                                                                
				   					
				   					select  p.patient_id, o.value_datetime data_inicio_tpi 
				   					from patient p                                                                
										inner join encounter e on p.patient_id=e.patient_id                                                                                      
										inner join obs o on o.encounter_id=e.encounter_id                                                                                        
									where e.voided=0 and p.voided=0 and o.value_datetime between (:startDate - interval 20 month) and :endDate            
										and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location

						        -- Estamos adicionando novas fontes da ficha resumo e ficha clinica tambem para exclusao caso o paciente tenha uma consulta INH no periodo anterior ao periodo em analise
				
									union 

									select p.patient_id,obsInicioINH.value_datetime data_inicio_tpi 
								      from 
								        patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
								      	    and obsInicioINH.value_datetime between (:startDate - interval 20 month) and :endDate and e.location_id=:location

								     union

								    select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
								      	    and e.encounter_datetime between (:startDate - interval 20 month) and :endDate and e.location_id=:location
                                 				   				
				   				) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id  																		 
				   					and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
				   				where inicioAnterior.patient_id is null																									 
					   			
					   			union                                                                                                                                    
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p														     
									inner join encounter e on p.patient_id=e.patient_id																				             
									inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
								where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	             
									and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	                 
									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)                                 
								group by p.patient_id                                                                                                                            
					   			
					   			union																																	 
					   			
					   			select p.patient_id,min(o.value_datetime) data_inicio_tpi 
					   			from patient p																 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and o.value_datetime between (:endDate - interval 7 month) and :endDate 	     
					   				and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location								 
					   				group by p.patient_id																												 
					   			
					   			union 																																	 
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p															 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
					   				and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location			 
					   				group by p.patient_id																												 
					   		
					   		   					   		    union

					   		   -- Acreicentando as novas fontes da ficha clinica e ficha resumo para determinar o inicio INH

				         	    select p.patient_id,min(obsInicioINH.value_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
							      	    and obsInicioINH.value_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							     group by p.patient_id

							     union

							    select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
							      	    and e.encounter_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							      group by p.patient_id 	    
		
					   		)inicio_INH group by inicio_INH.patient_id																									 
					) inicio_inh                  
			) inicio_tpi    
			inner join encounter e on e.patient_id=inicio_tpi.patient_id  
	     	inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id             
	       	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id           
	    where e.voided=0 and obsDTINH.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) 
	    		and e.encounter_datetime between inicio_tpi.data_inicio_tpi and (inicio_tpi.data_inicio_tpi + INTERVAL 5 MONTH) 
	    		and obsDTINH.concept_id=1719 and obsDTINH.value_coded=23955 and e.location_id=:location 
	    		and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded in (1257,1065,1256)      
	        group by inicio_tpi.patient_id                 
	        having count(*)>=2      
	   
	   union

        select inicio_tpi.patient_id                   
        from 
        		(select inicio_inh.* 
				from 														                                        						         
					(	select inicio_INH.patient_id,min(inicio_INH.data_inicio_tpi) data_inicio_tpi 
						from 
							(	select inicio.patient_id,inicio.data_inicio_tpi 
								from
									(	
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													 
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id	 																			 
				   							inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257)	 						 
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location		 
				   							group by p.patient_id	 																											 
										
										union  																																		 
										
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													         
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id																				 
				   							left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
												and seguimentoTPT.concept_id =23987  																						
												and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
												and seguimentoTPT.voided =0)						 	
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   				 			and seguimentoTPT.obs_id is null		         
				   							group by p.patient_id			                                                                                                     
				   					)inicio																																	 
				   				left join 																																 
				   				(	
				   				  select p.patient_id,e.encounter_datetime data_inicio_tpi 
				   					from patient p																 
				   						inner join encounter e on p.patient_id=e.patient_id																				 
				   						inner join obs o on o.encounter_id=e.encounter_id																				 
				   					where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 20 MONTH) and :endDate  
				   						and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   					
				   					union                                                                                                                                
				   					
				   					select  p.patient_id, o.value_datetime data_inicio_tpi 
				   					from patient p                                                                
										inner join encounter e on p.patient_id=e.patient_id                                                                                      
										inner join obs o on o.encounter_id=e.encounter_id                                                                                        
									where e.voided=0 and p.voided=0 and o.value_datetime between (:startDate - interval 20 month) and :endDate            
										and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location  

							-- Estamos adicionando novas fontes da ficha resumo e ficha clinica tambem para exclusao caso o paciente tenha uma consulta INH no periodo anterior ao periodo em analise
				
									union 

									select p.patient_id,obsInicioINH.value_datetime data_inicio_tpi 
								      from 
								        patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
								      	    and obsInicioINH.value_datetime between (:startDate - interval 20 month) and :endDate and e.location_id=:location

								     union

								    select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
								      	    and e.encounter_datetime between (:startDate - interval 20 month) and :endDate and e.location_id=:location

				   				) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id  																		 
				   					and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
				   				where inicioAnterior.patient_id is null																									 
					   			
					   			union

					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p														     
									inner join encounter e on p.patient_id=e.patient_id																				             
									inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
								where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	             
									and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	                 
									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)                                 
								group by p.patient_id                                                                                                                            
					   			
					   			union																																	 
					   			
					   			select p.patient_id,min(o.value_datetime) data_inicio_tpi 
					   			from patient p																 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and o.value_datetime between (:endDate - interval 7 month) and :endDate 	     
					   				and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location								 
					   				group by p.patient_id																												 
					   			
					   			union 																																	 
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p															 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
					   				and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location			 
					   				group by p.patient_id

					   		    union

					   		   -- Acreicentando as novas fontes da ficha clinica e ficha resumo para determinar o inicio INH

				         	    select p.patient_id,min(obsInicioINH.value_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
							      	    and obsInicioINH.value_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							     group by p.patient_id

							     union

							    select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
							      	    and e.encounter_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							      group by p.patient_id 																												 
					   		)inicio_INH group by inicio_INH.patient_id																									 
					) inicio_inh                              
			) inicio_tpi       
			inner join encounter e on e.patient_id=inicio_tpi.patient_id                   
	       	inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id                
	        	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id              
		where e.voided=0 and obsDTINH.voided=0 and obsLevTPI.voided=0 and e.encounter_type=60 
	          and e.encounter_datetime between inicio_tpi.data_inicio_tpi and (inicio_tpi.data_inicio_tpi + INTERVAL 5 MONTH) 
	          and obsDTINH.concept_id=23985 and obsDTINH.value_coded in (656,23982) and e.location_id=:location 
	          and obsLevTPI.concept_id=23986 and obsLevTPI.value_coded=23720     
	          group by inicio_tpi.patient_id having count(*)>=2 
		
		union                   
		
		select patient_id                
		from
			(	select inicio_tpi.patient_id,    
					sum(if(obsLevTPI.concept_id=6122,1,0)) mensal,         
					sum(if(obsLevTPI.concept_id=1719,1,0)) trimestral        
				from         
					(	select inicio_inh.* 
				from 														                                        						         
					(	select inicio_INH.patient_id,	min(inicio_INH.data_inicio_tpi) data_inicio_tpi 
						from 
							(	select inicio.patient_id,inicio.data_inicio_tpi 
								from
									(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													 
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id	 																			 
				   							inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257)	 						 
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location		 
				   							group by p.patient_id	 																											 
										union  																																		 
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													         
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id																				 
				   							left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
												and seguimentoTPT.concept_id =23987  																						
												and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
												and seguimentoTPT.voided =0)					 	
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   				 			and seguimentoTPT.obs_id is null		         
				   							group by p.patient_id			                                                                                                     
				   					)
				   				inicio																																	 
				   				left join 																																 
				   				(	
				   				   select p.patient_id,e.encounter_datetime data_inicio_tpi 
				   					from patient p																 
				   						inner join encounter e on p.patient_id=e.patient_id																				 
				   						inner join obs o on o.encounter_id=e.encounter_id																				 
				   					where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 20 MONTH) and :endDate  
				   						and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   					
				   					union                                                                                                                                
				   					
				   					select  p.patient_id, o.value_datetime data_inicio_tpi 
				   					from patient p                                                                
										inner join encounter e on p.patient_id=e.patient_id                                                                                      
										inner join obs o on o.encounter_id=e.encounter_id                                                                                        
									where e.voided=0 and p.voided=0 and o.value_datetime between (:startDate - interval 20 month) and :endDate            
										and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location  		                                 
				   				
								 -- Estamos adicionando novas fontes da ficha resumo e ficha clinica tambem para exclusao caso o paciente tenha uma consulta INH no periodo anterior ao periodo em analise
				
									union 

									select p.patient_id,obsInicioINH.value_datetime data_inicio_tpi 
								      from 
								        patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
								      	    and obsInicioINH.value_datetime between (:startDate - interval 20 month) and :endDate and e.location_id=:location

								     union

								    select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
								      	    and e.encounter_datetime between (:startDate - interval 20 month) and :endDate  and e.location_id=:location
                                 

				   				) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id  																		 
				   					and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
				   				where inicioAnterior.patient_id is null																									 
					   			
					   			union                                                                                                                                    
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p														     
									inner join encounter e on p.patient_id=e.patient_id																				             
									inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
								where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	             
									and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	                 
									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)                                 
								group by p.patient_id                                                                                                                            
					   			
					   			union																																	 
					   			
					   			select p.patient_id,min(o.value_datetime) data_inicio_tpi 
					   			from patient p																 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and o.value_datetime between (:endDate - interval 7 month) and :endDate 	     
					   				and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location								 
					   				group by p.patient_id																												 
					   			
					   			union 																																	 
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p															 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
					   				and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location			 
					   				group by p.patient_id																												 
					   		  
					   		    union

					   		   -- Acreicentando as novas fontes da ficha clinica e ficha resumo para determinar o inicio INH

				         	    select p.patient_id,min(obsInicioINH.value_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
							      	    and obsInicioINH.value_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							     group by p.patient_id

							     union

							    select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
							      	    and e.encounter_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							      group by p.patient_id 	    

					   		)inicio_INH group by inicio_INH.patient_id																									 
					) inicio_inh                       
					) inicio_tpi             
					inner join encounter e on e.patient_id=inicio_tpi.patient_id           
					inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 
					left join obs obsOutrasPresc on (obsOutrasPresc.encounter_id  = e.encounter_id and obsOutrasPresc.concept_id = 1719  and obsOutrasPresc.value_coded in(23954,23955))      
					where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) 
						and e.encounter_datetime between inicio_tpi.data_inicio_tpi and (inicio_tpi.data_inicio_tpi + INTERVAL 7 MONTH) 
						and ((obsLevTPI.concept_id = 6122 and  obsOutrasPresc.encounter_id is null) or (obsLevTPI.concept_id =1719 ))  
						and obsLevTPI.value_coded in (1257,1065,1256,23955) and e.location_id=:location  
						group by inicio_tpi.patient_id                  
			) inh where mensal>=3 and trimestral>=1                 
		
		union

		select patient_id                                 
		from      
			(	select  inicio_tpi.patient_id,    
					sum(if(obsLevTPI.value_coded=1098,1,0)) mensal,        
					sum(if(obsLevTPI.value_coded=23720,1,0)) trimestral      
				from      
					(	select inicio_inh.* 
				from 														                                        						         
					(	select inicio_INH.patient_id,min(inicio_INH.data_inicio_tpi) data_inicio_tpi 
						from 
							(	select inicio.patient_id,inicio.data_inicio_tpi 
								from
									(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													 
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id	 																			 
				   							inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257)	 						 
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location		 
				   							group by p.patient_id	 																											 
										union  																																		 
										select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	
										from	patient p													         
				   							inner join encounter e on p.patient_id=e.patient_id																				 
				   							inner join obs o on o.encounter_id=e.encounter_id																				 
				   							left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
												and seguimentoTPT.concept_id =23987  																						
												and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
												and seguimentoTPT.voided =0)						 	
				   						where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
				   							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   				 			and seguimentoTPT.obs_id is null 	         
				   							group by p.patient_id			                                                                                                     
				   					)inicio																																	 
				   				left join 																																 
				   				(	
				   				   select p.patient_id,e.encounter_datetime data_inicio_tpi 
				   					from patient p																 
				   						inner join encounter e on p.patient_id=e.patient_id																				 
				   						inner join obs o on o.encounter_id=e.encounter_id																				 
				   					where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 20 MONTH) and :endDate  
				   						and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      
				   					
				   					union                                                                                                                                
				   					
				   					select  p.patient_id, o.value_datetime data_inicio_tpi 
				   					from patient p                                                                
								 		inner join encounter e on p.patient_id=e.patient_id                                                                                      
										inner join obs o on o.encounter_id=e.encounter_id                                                                                        
									where e.voided=0 and p.voided=0 and o.value_datetime between (:startDate - interval 20 month) and :endDate            
										and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location  		                                 
				   				
					       -- Estamos adicionando novas fontes da ficha resumo e ficha clinica tambem para exclusao caso o paciente tenha uma consulta INH no periodo anterior ao periodo em analise
				
									union 

									select p.patient_id,obsInicioINH.value_datetime data_inicio_tpi 
								      from 
								        patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
								      	    and obsInicioINH.value_datetime between (:startDate - interval 20 month) and :endDate and e.location_id=:location

								     union

								    select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p 
								        inner join encounter e on p.patient_id = e.patient_id 
								        inner join obs o on o.encounter_id = e.encounter_id 
								        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
								      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
								      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
								      	    and e.encounter_datetime between (:startDate - interval 20 month) and :endDate  and e.location_id=:location
                                 
				   				) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id  																		 
				   					and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
				   				where inicioAnterior.patient_id is null																									 
					   			
					   			union                                                                                                                                    
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p														     
									inner join encounter e on p.patient_id=e.patient_id																				             
									inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
								where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate	             
									and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	                 
									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)                                 
								group by p.patient_id                                                                                                                            
					   			
					   			union																																	 
					   			
					   			select p.patient_id,min(o.value_datetime) data_inicio_tpi 
					   			from patient p																 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and o.value_datetime between (:endDate - interval 7 month) and :endDate 	     
					   				and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location								 
					   				group by p.patient_id																												 
					   			
					   			union 																																	 
					   			
					   			select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
					   			from patient p															 
					   				inner join encounter e on p.patient_id=e.patient_id																				 
					   				inner join obs o on o.encounter_id=e.encounter_id																				 
					   			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - interval 7 month) and :endDate   
					   				and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location			 
					   				group by p.patient_id																												 
					   				
					   		    union

					   		   -- Acreicentando as novas fontes da ficha clinica e ficha resumo para determinar o inicio INH

				         	    select p.patient_id,min(obsInicioINH.value_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165326 and obsInicioINH.voided=0
							      	    and obsInicioINH.value_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							     group by p.patient_id

							     union

							    select p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
							      from 
							        patient p 
							        inner join encounter e on p.patient_id = e.patient_id 
							        inner join obs o on o.encounter_id = e.encounter_id 
							        inner join obs obsInicioINH on obsInicioINH.encounter_id = e.encounter_id 
							      where e.voided=0 and p.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=23985 and o.value_coded=656
							      	    and obsInicioINH.concept_id=165308 and obsInicioINH.value_coded=1256 and obsInicioINH.voided=0
							      	    and e.encounter_datetime between (:endDate - interval 7 month) and :endDate and e.location_id=:location
							      group by p.patient_id 	    

					   		)inicio_INH group by inicio_INH.patient_id																									 
					) inicio_inh                      
					) inicio_tpi 
					inner join encounter e on e.patient_id=inicio_tpi.patient_id           
					inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id        
					inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id      
				where e.voided=0 and obsDTINH.voided=0 and obsLevTPI.voided=0 and e.encounter_type=60 
					and e.encounter_datetime between inicio_tpi.data_inicio_tpi and (inicio_tpi.data_inicio_tpi + INTERVAL 7 MONTH) 
					and obsDTINH.concept_id=23985 and obsDTINH.value_coded in (656,23982) and e.location_id=:location 
					and obsLevTPI.concept_id=23986 and obsLevTPI.value_coded in (23720,1098) 
					group by inicio_tpi.patient_id                 
			) 
		inh  where mensal>=3 and trimestral>=1              
	) inicio_tpt group by inicio_tpt.patient_id
