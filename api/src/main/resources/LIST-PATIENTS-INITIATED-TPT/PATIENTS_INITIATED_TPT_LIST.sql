select tpt.*  
from 
(  
	Select   
				inicio_tpi.patient_id,  
				inicio_tpi.data_inicio_tpi data_inicio_tpt,  
				pe.gender,  
				if(pe.birthdate is not null,timestampdiff(year,pe.birthdate,:endDate),'N/A') idade_actual,  
				concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,''),' ',ifnull(pn.family_name,'')) nome,  
				pid.identifier as nid,  
				seguimento.ultimo_seguimento,  
				if(receivedTPT.encounter_id is null,'Não','Sim') recebeu_profilaxia,  
				inicioInh_TPT_INI_FR5.data_completa_6meses, 
				timestampdiff(day,inicioInh_TPT_INI_FR5.data_completa_6meses,finalinh.data_final_inh) diferencaFinalEsperada,
				inicio_tarv.data_inicio data_inicio_tarv,  
				ini3hpclinica.data_inicio_3hpclinica, 
				ini3hpResumo.data_inicio_3hpResumo, 
				ini3hpfilt.data_inicio_3hpfilt,  
				final3hpfilt.data_final_3hpfilt,  
				final3hpfilt.tipoDispensa3hp,  
				iniinhfilt.data_inicio_inhfilt,  
				iniinhresumo.data_inicio_inhresumo,  
				iniinhseguimento.data_inicio_inhSeguimento,  
				finalinhfilt.data_final_inhfilt,  
				finalinhfilt.tipoDispensainh,  
				finalinhresumo.data_final_inhresumo,  
				finalinhseguimento.data_final_inhSeguimento,  
				inicioInh_TPT_INI_FR5.data_inicio_tpi data_inicio_inh,  
				finalinh.data_final_inh,  
				gravidaLactante.decisao as estadoMulher,
				TPT_INI_FR16_CO.data_final_3HP dataFinal3hpResumo,
				TPT_INI_FR16.data_final_3HP dataFinal3hpClinica,
				inicio3hp_TPT_INI_FR4.data_inicio_3HP,
				inicio3hp_TPT_INI_FR4.expected3hpCompletion,
				final3hp.data_final_3HP,
				timestampdiff(day,inicio3hp_TPT_INI_FR4.expected3hpCompletion, final3hp.data_final_3HP) expected3hpDiference
	from  	(  
			select inicio_tpi.patient_id, min(data_inicio_3HP) data_inicio_tpi  
			from  
				( 	
					/*
					Patients who have Última profilaxia TPT with value “3HP” and Data Inicio da Profilaxia TPT registered in 
					Ficha Resumo - Mastercard (3HP Start Date) during the reporting period
					*/
					select 	p.patient_id,min(obs3hpStart.value_datetime) data_inicio_3HP  																		
					from 	patient p  																													
							inner join encounter e on p.patient_id=e.patient_id  																			
							inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id 
							inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id 
					where 	e.voided=0 and p.voided=0 and obs3hpStart.value_datetime between :startDate and :endDate and  										
							obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=53 and  
							e.location_id=:location and obs3hpStart.concept_id=165326 and obs3hpStart.voided=0			
					group by p.patient_id 

					union 
				
					/*
						Patients who have Profilaxia TPT with the value “3HP” and Estado da Profilaxia with the value “Inicio (I)” marked on 
						Ficha Clínica – Mastercard (3HP Start Date) during the reporting period 
					*/
					select 	p.patient_id, min(e.encounter_datetime) data_inicio_3HP  																		
					from 	patient p														 			  															
							inner join encounter e on p.patient_id=e.patient_id																				 		
							inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id		 																				
							inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id																
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
							and obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=6 and  e.location_id=:location	  		
							and obs3hpStart.voided =0 and obs3hpStart.concept_id =165308 and obs3hpStart.value_coded=1256 							
					group by p.patient_id
					
					union 
					
					/*
						Patients who have Outras Prescrições with the value “3HP” marked on Ficha Clínica - Mastercard during 
						the reporting period (3HP Start Date) and no other 3HP prescriptions (Outras Prescrições = “3HP”) marked 
						on Ficha Clínica in the 4 months prior to this consultation date and no other pick-ups with Regime de 
						TPT = 3HP or 3HP + Piridoxina marked on FILT in the 4 months prior to the 3HP Start Date 
					*/
				
					select inicio.patient_id,inicio.data_inicio_3HP  																							
					from   																																		
						(	select 	p.patient_id,min(e.encounter_datetime) data_inicio_3HP  																		
							from 	patient p  																													
									inner join encounter e on p.patient_id=e.patient_id  																			
									inner join obs o on o.encounter_id=e.encounter_id  																				
							where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate and  										
									o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  
									e.location_id=:location 			
							group by p.patient_id  																										
						)  																																	
						inicio   																																
						left join  																															
						(  																																	
							select 	p.patient_id,e.encounter_datetime data_inicio_3HP  																		
							from 	patient p  																												
									inner join encounter e on p.patient_id=e.patient_id  																		
									inner join obs o on o.encounter_id=e.encounter_id  																				
							where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 8 MONTH) and :endDate  						
									and o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) 
									and  e.location_id=:location
									
						   union 
						   
							select 	p.patient_id, e.encounter_datetime data_inicio_3HP																		
							from	patient p																												 	
									inner join encounter e on p.patient_id=e.patient_id																			 	
									inner join obs o on o.encounter_id=e.encounter_id 																				
							where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=60  and o.concept_id=23985 and o.value_coded in (23954,23984)  	
									and	e.encounter_datetime between (:startDate - INTERVAL 8 MONTH) and :endDate  and e.location_id= :location		 	 			
						)  																																		
					   inicioAnterior on inicio.patient_id=inicioAnterior.patient_id  																			
						and inicioAnterior.data_inicio_3HP between (inicio.data_inicio_3HP - INTERVAL 4 MONTH) and (inicio.data_inicio_3HP - INTERVAL 1 day) 
					where inicioAnterior.patient_id is null 
					
					/*
						Patients who have Outras Prescrições with the value “DT-3HP” marked on 
						Ficha Clínica - Mastercard during the reporting period (3HP Start Date)
					*/	
					
					union
					
					select 	p.patient_id,min(e.encounter_datetime) data_inicio_3HP  																		
					from 	patient p  																												
							inner join encounter e on p.patient_id=e.patient_id  																		
							inner join obs o on o.encounter_id=e.encounter_id  																				
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  						
							and o.voided=0 and o.concept_id=1719 and o.value_coded=165307 and e.encounter_type in (6,9) 
							and  e.location_id=:location 
					group by p.patient_id
					
					/*
						Patients who have Regime de TPT with the values “3HP or 3HP + Piridoxina” and 
						“Seguimento de tratamento TPT” = (‘Inicio’ or ‘Re-Inicio’) marked on Ficha de Levantamento de TPT (FILT) 
						during the reporting period
					*/
					union 
						
					select p.patient_id, min(e.encounter_datetime) data_inicio_3HP  																		
					from 	patient p														 			  															
							inner join encounter e on p.patient_id=e.patient_id																				 		
							inner join obs o on o.encounter_id=e.encounter_id		 																				
							inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
							and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	  		
							and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 							
					group by p.patient_id
					
					union 

					/*
						Patients who have Regime de TPT with the values “3HP or 3HP + Piridoxina” and “Seguimento de Tratamento TPT” 
						with values “Continua” or “Fim” or no value marked on the first pick-up date on Ficha de Levantamento de TPT 
						(FILT) during the reporting period (FILT 3HP Start Date) and:
							•	No other Regime de TPT with the values “3HP or 3HP + Piridoxina” marked on FILT in the 
								4 months prior to this FILT 3HP Start Date and
							•	No other 3HP Start Dates marked on Ficha Clinica ((Profilaxia TPT with the value “3HP” and 
								Estado da Profilaxia with the value “Inicio (I)”) or (Outras Prescrições with the value “3HP”/“DT-3HP”)) 
								in the 4 months prior to this FILT 3HP Start Date and 
							•	No other 3HP Start Dates marked on Ficha Resumo (Última profilaxia TPT with value “3HP” and 
								Data Inicio da Profilaxia TPT) in the 4 months prior to this FILT 3HP Start Date:

					
					*/		
						
					 select inicio.patient_id,inicio.data_inicio_3HP  																							
					 from 																																		
						(	
						
							Select firstFilt.patient_id,firstFilt.dataFirstFilt data_inicio_3HP
							from 
							(	select 	p.patient_id,min(e.encounter_datetime) dataFirstFilt  																	
								from 	patient p  																												
										inner join encounter e on p.patient_id=e.patient_id	 												
								where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  										
										and e.encounter_type=60 and  e.location_id=:location   					
								group by p.patient_id
							) firstFilt
							inner join encounter e on e.patient_id=firstFilt.patient_id
							inner join obs obsTPT on obsTPT.encounter_id=e.encounter_id
							left join obs seguimentoTPT on (seguimentoTPT.encounter_id=e.encounter_id and seguimentoTPT.voided=0 and 
																seguimentoTPT.concept_id=23987)
							where 	firstFilt.dataFirstFilt=e.encounter_datetime and 
									e.encounter_type=60 and obsTPT.voided=0 and obsTPT.concept_id=23985 and obsTPT.value_coded in (23954,23984) and 
									e.location_id=:location and (seguimentoTPT.value_coded in (1257,1267) or seguimentoTPT.value_coded is null)																													
						) inicio   																																	
					left join   																															
					(  																																		
						select p.patient_id,e.encounter_datetime data_inicio_3HP 
						from 	patient p  															
								inner join encounter e on p.patient_id=e.patient_id  																				
								inner join obs o on o.encounter_id=e.encounter_id  																				
						where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 4 MONTH) and :endDate  	
								and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 	
						
						union
						
						select 	p.patient_id, e.encounter_datetime data_inicio_3HP																			 	
						from	patient p																												 		
								inner join encounter e on p.patient_id=e.patient_id																			 		
								inner join obs o on o.encounter_id=e.encounter_id																			 		
						where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and o.concept_id=1719 and o.value_coded in (23954,165307) 		 		
								and e.encounter_datetime  between (:startDate - INTERVAL 4 MONTH) and :endDate  and e.location_id=:location 

						union 
						
						select 	p.patient_id, e.encounter_datetime data_inicio_3HP  																		
						from 	patient p														 			  															
								inner join encounter e on p.patient_id=e.patient_id																				 		
								inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id		 																				
								inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id																
						where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 4 MONTH) and :endDate	 			  									
								and obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=6 and  e.location_id=:location	  		
								and obs3hpStart.voided =0 and obs3hpStart.concept_id =165308 and obs3hpStart.value_coded=1256
						union			
						
						-- Change concept 
						select 	p.patient_id,obs3hpStart.value_datetime data_inicio_3HP  																		
						from 	patient p  																													
								inner join encounter e on p.patient_id=e.patient_id  																			
								inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id 
								inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id 
						where 	e.voided=0 and p.voided=0 and obs3hpStart.value_datetime between (:startDate - INTERVAL 4 MONTH) and :endDate and  										
								obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=53 and  
								e.location_id=:location and obs3hpStart.concept_id=165326 and obs3hpStart.voided=0			 
					)  																																			
					inicioAnterior on inicioAnterior.patient_id=inicio.patient_id 																				
						and	inicioAnterior.data_inicio_3HP between (inicio.data_inicio_3HP - INTERVAL 4 MONTH) and (inicio.data_inicio_3HP - INTERVAL 1 day)  	
					where inicioAnterior.patient_id is null		
					
					-- =================INH=========================
					union  
					
					/*
						Patients who have Última profilaxia Isoniazida (Data Início) or (Última profilaxia TPT with value 
						“Profilaxia com Isoniazida (INH)” and Data Inicio da Profilaxia TPT) registered in Ficha Resumo - 
						Mastercard during the reporting period 
						
						Patients who have Profilaxia com INH – TPI (Data Início) marked in Ficha de Seguimento during the reporting period
						
					*/
					   
					select 	p.patient_id,min(o.value_datetime) data_inicio_tpi  																			
					from 	patient p  																														
							inner join encounter e on p.patient_id=e.patient_id  																				
							inner join obs o on o.encounter_id=e.encounter_id  																					
					where 	e.voided=0 and p.voided=0 and o.value_datetime between :startDate and :endDate  													
							and o.voided=0 and o.concept_id=6128 and e.encounter_type in (53,6,9) and e.location_id=:location 									
					group by p.patient_id
					
					union 
					
					-- Change concept
					select 	p.patient_id,min(obsInhStart.value_datetime) data_inicio_tpi  																		
					from 	patient p  																													
							inner join encounter e on p.patient_id=e.patient_id  																			
							inner join obs obsInh on obsInh.encounter_id=e.encounter_id 
							inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id 
					where 	e.voided=0 and p.voided=0 and obsInhStart.value_datetime between :startDate and :endDate and  										
							obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=53 and  
							e.location_id=:location and obsInhStart.concept_id=165326 and obsInhStart.voided=0			
					group by p.patient_id 
					
					union
					
					/*
						Patients who have Profilaxia (INH) with the value “I” (Início) or (Profilaxia TPT with the value “Isoniazida” 
						and Estado da Profilaxia with the value “Inicio (I)”) marked on Ficha Clínica - Mastercard during the reporting period 
					*/
					
									
					select 	p.patient_id,min(e.encounter_datetime) data_inicio_tpi  																		
					from 	patient p  																														
							inner join encounter e on p.patient_id=e.patient_id  																				
							inner join obs o on o.encounter_id=e.encounter_id  																					
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  												
							and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location 				
					group by p.patient_id
					
					union 
					
					select 	p.patient_id, min(e.encounter_datetime) data_inicio_tpi  																		
					from 	patient p														 			  															
							inner join encounter e on p.patient_id=e.patient_id																				 		
							inner join obs obsInh on obsInh.encounter_id=e.encounter_id		 																				
							inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id																
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
							and obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=6 and  e.location_id=:location	  		
							and obsInhStart.voided =0 and obsInhStart.concept_id =165308 and obsInhStart.value_coded=1256 							
					group by p.patient_id
					
					
					union 
					
					/*
						Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”) and 
						“Seguimento de tratamento TPT”= (‘Inicio’ or ‘Re-Inicio’) marked on the first pick-up date on 
						Ficha de Levantamento de TPT (FILT) during the reporting period (INH Start Date)
					*/
					
					 select p.patient_id, min(e.encounter_datetime) data_inicio_3HP  																		
					 from 	patient p														 			  														
							inner join encounter e on p.patient_id=e.patient_id																				 		
							inner join obs o on o.encounter_id=e.encounter_id		 																				
							inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																
					 where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	  		 	
							and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 							
					 group by p.patient_id 
					 
					 
					union 
					/*
						Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”) and 
						“Seguimento de Tratamento TPT” with values “Continua” or no value marked on the first pick-up date on 
						Ficha de Levantamento de TPT (FILT) during the reporting period (FILT INH Start Date) and:
							•	No other INH Start Dates marked on Ficha Clinica (Profilaxia (INH) with the value “I” (Início) or 
								(Profilaxia TPT with the value “Isoniazida (INH)” and Estado da Profilaxia with the value “Inicio (I)”) 
								in the 7 months prior to this FILT INH Start Date and
							•	No other INH Start Dates marked on  Ficha de Seguimento (Profilaxia com INH – TPI (Data Início)) 
								in the 7 months prior to this FILT INH Start Date and
							•	No other INH Start Dates marked on Ficha resumo (“Última profilaxia Isoniazida (Data Início)” or 
								(Última profilaxia TPT with value “Isoniazida (INH)” and Data Inicio da Profilaxia TPT)) 
								in the 7 months prior to this FILT INH Start Date
					*/
					
					  select inicio.patient_id,inicio.data_inicio_tpi  																						
					  from  																																	
						( 	
							
							Select firstFilt.patient_id,firstFilt.dataFirstFilt data_inicio_tpi
							from 
							(	select 	p.patient_id,min(e.encounter_datetime) dataFirstFilt  																	
								from 	patient p  																												
										inner join encounter e on p.patient_id=e.patient_id	 												
								where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  										
										and e.encounter_type=60 and  e.location_id=:location   					
								group by p.patient_id
							) firstFilt
							inner join encounter e on e.patient_id=firstFilt.patient_id
							inner join obs obsTPT on obsTPT.encounter_id=e.encounter_id
							left join obs seguimentoTPT on (seguimentoTPT.encounter_id=e.encounter_id and seguimentoTPT.voided=0 and 
																seguimentoTPT.concept_id=23987)
							where 	firstFilt.dataFirstFilt=e.encounter_datetime and 
									e.encounter_type=60 and obsTPT.voided=0 and obsTPT.concept_id=23985 and obsTPT.value_coded in (656,23982) and 
									e.location_id=:location and (seguimentoTPT.value_coded=1257 or seguimentoTPT.value_coded is null) 																											
						) inicio  																																
						left join   																															
						( 	
							select 	p.patient_id,e.encounter_datetime data_inicio_tpi  																		
							from 	patient p  																														
									inner join encounter e on p.patient_id=e.patient_id  																				
									inner join obs o on o.encounter_id=e.encounter_id  																					
							where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 14 MONTH) and :endDate  												
									and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location 
							
							union 
							
							select 	p.patient_id, e.encounter_datetime data_inicio_tpi  																		
							from 	patient p														 			  															
									inner join encounter e on p.patient_id=e.patient_id																				 		
									inner join obs obsInh on obsInh.encounter_id=e.encounter_id		 																				
									inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id																
							where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 14 MONTH) and :endDate	 			  									
									and obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=6 and  e.location_id=:location	  		
									and obsInhStart.voided =0 and obsInhStart.concept_id =165308 and obsInhStart.value_coded=1256
							union 
							
							select 	p.patient_id,o.value_datetime data_inicio_tpi  																			
							from 	patient p  																														
									inner join encounter e on p.patient_id=e.patient_id  																				
									inner join obs o on o.encounter_id=e.encounter_id  																					
							where 	e.voided=0 and p.voided=0 and o.value_datetime between (:startDate - INTERVAL 14 MONTH) and :endDate  													
									and o.voided=0 and o.concept_id=6128 and e.encounter_type in (53,6,9) and e.location_id=:location 
							
							union 
							
							-- Change concept
							select 	p.patient_id,obsInhStart.value_datetime data_inicio_tpi  																		
							from 	patient p  																													
									inner join encounter e on p.patient_id=e.patient_id  																			
									inner join obs obsInh on obsInh.encounter_id=e.encounter_id 
									inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id 
							where 	e.voided=0 and p.voided=0 and obsInhStart.value_datetime between (:startDate - INTERVAL 14 MONTH) and :endDate and  										
									obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=53 and  
									e.location_id=:location and obsInhStart.concept_id=165326 and obsInhStart.voided=0
							
							union 
							
							-- This is not specified
							select p.patient_id, e.encounter_datetime data_inicio_tpi  																		
							from 	patient p														 			  														
									inner join encounter e on p.patient_id=e.patient_id																				 		
									inner join obs o on o.encounter_id=e.encounter_id		 																				
									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																
							 where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 14 MONTH) and :endDate	 			  									
									and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	  		 	
									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 							

						) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id 																		
							and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
					   where inicioAnterior.patient_id is null 	
					) inicio_tpi  
            group by inicio_tpi.patient_id  
        ) inicio_tpi
		inner join person pe on pe.person_id=inicio_tpi.patient_id 
		left join 
		-- TPT_INI_FR4
		(
			select patient_id,min(data_inicio_3HP) data_inicio_3HP,
					--	Expected 3HP Completion Date – Sheet 1: Column P
					DATE_ADD(min(data_inicio_3HP), INTERVAL 86 DAY) expected3hpCompletion
			from 
			(
				/*
					Patients who have Última profilaxia TPT with value “3HP” and Data Inicio da Profilaxia TPT registered in 
					Ficha Resumo - Mastercard (3HP Start Date) during the reporting period
					*/
					select 	p.patient_id,min(obs3hpStart.value_datetime) data_inicio_3HP  																		
					from 	patient p  																													
							inner join encounter e on p.patient_id=e.patient_id  																			
							inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id 
							inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id 
					where 	e.voided=0 and p.voided=0 and obs3hpStart.value_datetime between :startDate and :endDate and  										
							obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=53 and  
							e.location_id=:location and obs3hpStart.concept_id=165326 and obs3hpStart.voided=0			
					group by p.patient_id 

					union 
				
					/*
						Patients who have Profilaxia TPT with the value “3HP” and Estado da Profilaxia with the value “Inicio (I)” marked on 
						Ficha Clínica – Mastercard (3HP Start Date) during the reporting period 
					*/
					select 	p.patient_id, min(e.encounter_datetime) data_inicio_3HP  																		
					from 	patient p														 			  															
							inner join encounter e on p.patient_id=e.patient_id																				 		
							inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id		 																				
							inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id																
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
							and obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=6 and  e.location_id=:location	  		
							and obs3hpStart.voided =0 and obs3hpStart.concept_id =165308 and obs3hpStart.value_coded=1256 							
					group by p.patient_id
					
					union 
					
					/*
						Patients who have Outras Prescrições with the value “3HP” marked on Ficha Clínica - Mastercard during 
						the reporting period (3HP Start Date) and no other 3HP prescriptions (Outras Prescrições = “3HP”) marked 
						on Ficha Clínica in the 4 months prior to this consultation date and no other pick-ups with Regime de 
						TPT = 3HP or 3HP + Piridoxina marked on FILT in the 4 months prior to the 3HP Start Date 
					*/
				
					select inicio.patient_id,inicio.data_inicio_3HP  																							
					from   																																		
						(	select 	p.patient_id,min(e.encounter_datetime) data_inicio_3HP  																		
							from 	patient p  																													
									inner join encounter e on p.patient_id=e.patient_id  																			
									inner join obs o on o.encounter_id=e.encounter_id  																				
							where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate and  										
									o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  
									e.location_id=:location 			
							group by p.patient_id  																										
						)  																																	
						inicio   																																
						left join  																															
						(  																																	
							select 	p.patient_id,e.encounter_datetime data_inicio_3HP  																		
							from 	patient p  																												
									inner join encounter e on p.patient_id=e.patient_id  																		
									inner join obs o on o.encounter_id=e.encounter_id  																				
							where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 8 MONTH) and :endDate  						
									and o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) 
									and  e.location_id=:location
									
						   union 
						   
							select 	p.patient_id, e.encounter_datetime data_inicio_3HP																		
							from	patient p																												 	
									inner join encounter e on p.patient_id=e.patient_id																			 	
									inner join obs o on o.encounter_id=e.encounter_id 																				
							where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=60  and o.concept_id=23985 and o.value_coded in (23954,23984)  	
									and	e.encounter_datetime between (:startDate - INTERVAL 8 MONTH) and :endDate  and e.location_id= :location		 	 			
						)  																																		
					   inicioAnterior on inicio.patient_id=inicioAnterior.patient_id  																			
						and inicioAnterior.data_inicio_3HP between (inicio.data_inicio_3HP - INTERVAL 4 MONTH) and (inicio.data_inicio_3HP - INTERVAL 1 day) 
					where inicioAnterior.patient_id is null 
					
					/*
						Patients who have Outras Prescrições with the value “DT-3HP” marked on 
						Ficha Clínica - Mastercard during the reporting period (3HP Start Date)
					*/	
					
					union
					
					select 	p.patient_id,min(e.encounter_datetime) data_inicio_3HP  																		
					from 	patient p  																												
							inner join encounter e on p.patient_id=e.patient_id  																		
							inner join obs o on o.encounter_id=e.encounter_id  																				
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  						
							and o.voided=0 and o.concept_id=1719 and o.value_coded=165307 and e.encounter_type in (6,9) 
							and  e.location_id=:location 
					group by p.patient_id
					
					/*
						Patients who have Regime de TPT with the values “3HP or 3HP + Piridoxina” and 
						“Seguimento de tratamento TPT” = (‘Inicio’ or ‘Re-Inicio’) marked on Ficha de Levantamento de TPT (FILT) 
						during the reporting period
					*/
					union 
						
					select p.patient_id, min(e.encounter_datetime) data_inicio_3HP  																		
					from 	patient p														 			  															
							inner join encounter e on p.patient_id=e.patient_id																				 		
							inner join obs o on o.encounter_id=e.encounter_id		 																				
							inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
							and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	  		
							and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 							
					group by p.patient_id
					
					union 

					/*
						Patients who have Regime de TPT with the values “3HP or 3HP + Piridoxina” and “Seguimento de Tratamento TPT” 
						with values “Continua” or “Fim” or no value marked on the first pick-up date on Ficha de Levantamento de TPT 
						(FILT) during the reporting period (FILT 3HP Start Date) and:
							•	No other Regime de TPT with the values “3HP or 3HP + Piridoxina” marked on FILT in the 
								4 months prior to this FILT 3HP Start Date and
							•	No other 3HP Start Dates marked on Ficha Clinica ((Profilaxia TPT with the value “3HP” and 
								Estado da Profilaxia with the value “Inicio (I)”) or (Outras Prescrições with the value “3HP”/“DT-3HP”)) 
								in the 4 months prior to this FILT 3HP Start Date and 
							•	No other 3HP Start Dates marked on Ficha Resumo (Última profilaxia TPT with value “3HP” and 
								Data Inicio da Profilaxia TPT) in the 4 months prior to this FILT 3HP Start Date:

					
					*/		
						
					 select inicio.patient_id,inicio.data_inicio_3HP  																							
					 from 																																		
						(	
						
							Select firstFilt.patient_id,firstFilt.dataFirstFilt data_inicio_3HP
							from 
							(	select 	p.patient_id,min(e.encounter_datetime) dataFirstFilt  																	
								from 	patient p  																												
										inner join encounter e on p.patient_id=e.patient_id	 												
								where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  										
										and e.encounter_type=60 and  e.location_id=:location   					
								group by p.patient_id
							) firstFilt
							inner join encounter e on e.patient_id=firstFilt.patient_id
							inner join obs obsTPT on obsTPT.encounter_id=e.encounter_id
							left join obs seguimentoTPT on (seguimentoTPT.encounter_id=e.encounter_id and seguimentoTPT.voided=0 and 
																seguimentoTPT.concept_id=23987)
							where 	firstFilt.dataFirstFilt=e.encounter_datetime and 
									e.encounter_type=60 and obsTPT.voided=0 and obsTPT.concept_id=23985 and obsTPT.value_coded in (23954,23984) and 
									e.location_id=:location and (seguimentoTPT.value_coded in (1257,1267) or seguimentoTPT.value_coded is null)																													
						) inicio   																																	
					left join   																															
					(  																																		
						select p.patient_id,e.encounter_datetime data_inicio_3HP 
						from 	patient p  															
								inner join encounter e on p.patient_id=e.patient_id  																				
								inner join obs o on o.encounter_id=e.encounter_id  																				
						where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 4 MONTH) and :endDate  	
								and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 	
						
						union
						
						select 	p.patient_id, e.encounter_datetime data_inicio_3HP																			 	
						from	patient p																												 		
								inner join encounter e on p.patient_id=e.patient_id																			 		
								inner join obs o on o.encounter_id=e.encounter_id																			 		
						where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and o.concept_id=1719 and o.value_coded in (23954,165307) 		 		
								and e.encounter_datetime  between (:startDate - INTERVAL 4 MONTH) and :endDate  and e.location_id=:location 

						union 
						
						select 	p.patient_id, e.encounter_datetime data_inicio_3HP  																		
						from 	patient p														 			  															
								inner join encounter e on p.patient_id=e.patient_id																				 		
								inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id		 																				
								inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id																
						where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 4 MONTH) and :endDate	 			  									
								and obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=6 and  e.location_id=:location	  		
								and obs3hpStart.voided =0 and obs3hpStart.concept_id =165308 and obs3hpStart.value_coded=1256
						union			
						
						-- Change concept 
						select 	p.patient_id,obs3hpStart.value_datetime data_inicio_3HP  																		
						from 	patient p  																													
								inner join encounter e on p.patient_id=e.patient_id  																			
								inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id 
								inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id 
						where 	e.voided=0 and p.voided=0 and obs3hpStart.value_datetime between (:startDate - INTERVAL 4 MONTH) and :endDate and  										
								obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=53 and  
								e.location_id=:location and obs3hpStart.concept_id=165326 and obs3hpStart.voided=0			 
					)  																																			
					inicioAnterior on inicioAnterior.patient_id=inicio.patient_id 																				
						and	inicioAnterior.data_inicio_3HP between (inicio.data_inicio_3HP - INTERVAL 4 MONTH) and (inicio.data_inicio_3HP - INTERVAL 1 day)  	
					where inicioAnterior.patient_id is null	
			) start3hp
			group by start3hp.patient_id 
		) inicio3hp_TPT_INI_FR4 on inicio_tpi.patient_id=inicio3hp_TPT_INI_FR4.patient_id
		left join 
		-- TPT_INI_FR5
		(
			select patient_id,min(data_inicio_tpi) data_inicio_tpi,
					-- TPT_INI_FR22: Expected Completion Date = IPT Start Date (defined in TPT_INI_FR19) + 173 days
					DATE_ADD(min(data_inicio_tpi), INTERVAL 173 DAY) data_completa_6meses
			from 
			(
			
				/*
						Patients who have Última profilaxia Isoniazida (Data Início) or (Última profilaxia TPT with value 
						“Profilaxia com Isoniazida (INH)” and Data Inicio da Profilaxia TPT) registered in Ficha Resumo - 
						Mastercard during the reporting period 
						
						Patients who have Profilaxia com INH – TPI (Data Início) marked in Ficha de Seguimento during the reporting period
						
					*/
					   
					select 	p.patient_id,min(o.value_datetime) data_inicio_tpi  																			
					from 	patient p  																														
							inner join encounter e on p.patient_id=e.patient_id  																				
							inner join obs o on o.encounter_id=e.encounter_id  																					
					where 	e.voided=0 and p.voided=0 and o.value_datetime between :startDate and :endDate  													
							and o.voided=0 and o.concept_id=6128 and e.encounter_type in (53,6,9) and e.location_id=:location 									
					group by p.patient_id
					
					union 
					
					-- Change concept
					select 	p.patient_id,min(obsInhStart.value_datetime) data_inicio_tpi  																		
					from 	patient p  																													
							inner join encounter e on p.patient_id=e.patient_id  																			
							inner join obs obsInh on obsInh.encounter_id=e.encounter_id 
							inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id 
					where 	e.voided=0 and p.voided=0 and obsInhStart.value_datetime between :startDate and :endDate and  										
							obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=53 and  
							e.location_id=:location and obsInhStart.concept_id=165326 and obsInhStart.voided=0			
					group by p.patient_id 
					
					union
					
					/*
						Patients who have Profilaxia (INH) with the value “I” (Início) or (Profilaxia TPT with the value “Isoniazida” 
						and Estado da Profilaxia with the value “Inicio (I)”) marked on Ficha Clínica - Mastercard during the reporting period 
					*/
					
									
					select 	p.patient_id,min(e.encounter_datetime) data_inicio_tpi  																		
					from 	patient p  																														
							inner join encounter e on p.patient_id=e.patient_id  																				
							inner join obs o on o.encounter_id=e.encounter_id  																					
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  												
							and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location 				
					group by p.patient_id
					
					union 
					
					select 	p.patient_id, min(e.encounter_datetime) data_inicio_tpi  																		
					from 	patient p														 			  															
							inner join encounter e on p.patient_id=e.patient_id																				 		
							inner join obs obsInh on obsInh.encounter_id=e.encounter_id		 																				
							inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id																
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
							and obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=6 and  e.location_id=:location	  		
							and obsInhStart.voided =0 and obsInhStart.concept_id =165308 and obsInhStart.value_coded=1256 							
					group by p.patient_id
					
					
					union 
					
					/*
						Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”) and 
						“Seguimento de tratamento TPT”= (‘Inicio’ or ‘Re-Inicio’) marked on the first pick-up date on 
						Ficha de Levantamento de TPT (FILT) during the reporting period (INH Start Date)
					*/
					
					 select p.patient_id, min(e.encounter_datetime) data_inicio_3HP  																		
					 from 	patient p														 			  														
							inner join encounter e on p.patient_id=e.patient_id																				 		
							inner join obs o on o.encounter_id=e.encounter_id		 																				
							inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																
					 where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
							and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	  		 	
							and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 							
					 group by p.patient_id 
					 
					 
					union 
					/*
						Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”) and 
						“Seguimento de Tratamento TPT” with values “Continua” or no value marked on the first pick-up date on 
						Ficha de Levantamento de TPT (FILT) during the reporting period (FILT INH Start Date) and:
							•	No other INH Start Dates marked on Ficha Clinica (Profilaxia (INH) with the value “I” (Início) or 
								(Profilaxia TPT with the value “Isoniazida (INH)” and Estado da Profilaxia with the value “Inicio (I)”) 
								in the 7 months prior to this FILT INH Start Date and
							•	No other INH Start Dates marked on  Ficha de Seguimento (Profilaxia com INH – TPI (Data Início)) 
								in the 7 months prior to this FILT INH Start Date and
							•	No other INH Start Dates marked on Ficha resumo (“Última profilaxia Isoniazida (Data Início)” or 
								(Última profilaxia TPT with value “Isoniazida (INH)” and Data Inicio da Profilaxia TPT)) 
								in the 7 months prior to this FILT INH Start Date
					*/
					
					  select inicio.patient_id,inicio.data_inicio_tpi  																						
					  from  																																	
						( 	
							
							Select firstFilt.patient_id,firstFilt.dataFirstFilt data_inicio_tpi
							from 
							(	select 	p.patient_id,min(e.encounter_datetime) dataFirstFilt  																	
								from 	patient p  																												
										inner join encounter e on p.patient_id=e.patient_id	 												
								where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  										
										and e.encounter_type=60 and  e.location_id=:location   					
								group by p.patient_id
							) firstFilt
							inner join encounter e on e.patient_id=firstFilt.patient_id
							inner join obs obsTPT on obsTPT.encounter_id=e.encounter_id
							left join obs seguimentoTPT on (seguimentoTPT.encounter_id=e.encounter_id and seguimentoTPT.voided=0 and 
																seguimentoTPT.concept_id=23987)
							where 	firstFilt.dataFirstFilt=e.encounter_datetime and 
									e.encounter_type=60 and obsTPT.voided=0 and obsTPT.concept_id=23985 and obsTPT.value_coded in (656,23982) and 
									e.location_id=:location and (seguimentoTPT.value_coded=1257 or seguimentoTPT.value_coded is null) 																											
						) inicio  																																
						left join   																															
						( 	
							select 	p.patient_id,e.encounter_datetime data_inicio_tpi  																		
							from 	patient p  																														
									inner join encounter e on p.patient_id=e.patient_id  																				
									inner join obs o on o.encounter_id=e.encounter_id  																					
							where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 14 MONTH) and :endDate  												
									and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location 
							
							union 
							
							select 	p.patient_id, e.encounter_datetime data_inicio_tpi  																		
							from 	patient p														 			  															
									inner join encounter e on p.patient_id=e.patient_id																				 		
									inner join obs obsInh on obsInh.encounter_id=e.encounter_id		 																				
									inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id																
							where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 14 MONTH) and :endDate	 			  									
									and obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=6 and  e.location_id=:location	  		
									and obsInhStart.voided =0 and obsInhStart.concept_id =165308 and obsInhStart.value_coded=1256
							union 
							
							select 	p.patient_id,o.value_datetime data_inicio_tpi  																			
							from 	patient p  																														
									inner join encounter e on p.patient_id=e.patient_id  																				
									inner join obs o on o.encounter_id=e.encounter_id  																					
							where 	e.voided=0 and p.voided=0 and o.value_datetime between (:startDate - INTERVAL 14 MONTH) and :endDate  													
									and o.voided=0 and o.concept_id=6128 and e.encounter_type in (53,6,9) and e.location_id=:location 
							
							union 
							
							-- Change concept
							select 	p.patient_id,obsInhStart.value_datetime data_inicio_tpi  																		
							from 	patient p  																													
									inner join encounter e on p.patient_id=e.patient_id  																			
									inner join obs obsInh on obsInh.encounter_id=e.encounter_id 
									inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id 
							where 	e.voided=0 and p.voided=0 and obsInhStart.value_datetime between (:startDate - INTERVAL 14 MONTH) and :endDate and  										
									obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=53 and  
									e.location_id=:location and obsInhStart.concept_id=165326 and obsInhStart.voided=0
							
							union 
							
							-- This is not specified
							select p.patient_id, e.encounter_datetime data_inicio_tpi  																		
							from 	patient p														 			  														
									inner join encounter e on p.patient_id=e.patient_id																				 		
									inner join obs o on o.encounter_id=e.encounter_id		 																				
									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																
							 where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 14 MONTH) and :endDate	 			  									
									and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	  		 	
									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 							

						) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id 																		
							and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
					   where inicioAnterior.patient_id is null 
			) startINH
			group by patient_id			
		
		) inicioInh_TPT_INI_FR5 on inicio_tpi.patient_id=inicioInh_TPT_INI_FR5.patient_id         
		left join 
		-- TPT_INI_FR7
		(  
            				Select patient_id,min(data_inicio) data_inicio from (	 
            				Select p.patient_id,min(e.encounter_datetime) data_inicio from patient p   
            				inner join encounter e on p.patient_id=e.patient_id	  
            				inner join obs o on o.encounter_id=e.encounter_id  
            				where e.voided=0 and o.voided=0 and p.voided=0 and   
            				e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and   
            				e.encounter_datetime<=:endDate and e.location_id=:location  group by p.patient_id  union  
            				Select p.patient_id,min(value_datetime) data_inicio from patient p  
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and   
            				o.concept_id=1190 and o.value_datetime is not null and   
            				o.value_datetime<=:endDate and e.location_id=:location  group by p.patient_id  union  
            				select pg.patient_id,min(date_enrolled) data_inicio from patient p  
            				inner join patient_program pg on p.patient_id=pg.patient_id  
            				where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location  
            				group by pg.patient_id  union  
            				SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio  FROM 	patient p  
            				inner join encounter e on p.patient_id=e.patient_id  
            				WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location  
            				GROUP BY p.patient_id  union  
            				Select p.patient_id,min(value_datetime) data_inicio from patient p  
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and   
            				o.concept_id=23866 and o.value_datetime is not null and   
            				o.value_datetime<=:endDate and e.location_id=:location  group by p.patient_id  
            				) inicio_real  group by patient_id  
        )inicio_tarv on inicio_tpi.patient_id=inicio_tarv.patient_id  
		left join  
		-- TPT_INI_FR12
		(  
           select seguimento.patient_id,seguimento.ultimo_seguimento,max(e2.encounter_id) encounter_id
			from 
			(
				select  p.patient_id,max(encounter_datetime) ultimo_seguimento 
				from 	patient p  
						inner join encounter e on p.patient_id=e.patient_id  
				where 	e.voided=0 and p.voided=0 and e.encounter_datetime<=curdate() and  
						e.encounter_type in (6,9) and e.location_id=:location  
				group by p.patient_id
			) seguimento inner join encounter e2 on  e2.patient_id=seguimento.patient_id
			where 	e2.voided=0 and e2.encounter_type in (6,9) and e2.location_id=:location and 
					e2.encounter_datetime=seguimento.ultimo_seguimento
			group by seguimento.patient_id
        ) seguimento on inicio_tpi.patient_id=seguimento.patient_id 
		left join 
		-- TPT_INI_FR13
		(
			select 	e.encounter_id  																		
			from 	patient p  																												
					inner join encounter e on p.patient_id=e.patient_id  																		
					inner join obs o on o.encounter_id=e.encounter_id  																				
			where 	e.voided=0 and p.voided=0 and e.encounter_datetime<=curdate()  						
					and o.voided=0 and o.concept_id=1719 and o.value_coded in (165307,23955,23954) and e.encounter_type in (6,9) 
					and  e.location_id=:location
			union 

			select 	e.encounter_id  																		
			from 	patient p														 			  															
					inner join encounter e on p.patient_id=e.patient_id																				 		
					inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id		 																				
					inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id																
			where 	e.voided=0 and p.voided=0 and e.encounter_datetime<=curdate()	 			  									
					and obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded in (23954,656) and e.encounter_type=6 and  e.location_id=:location	  		
					and obs3hpStart.voided =0 and obs3hpStart.concept_id =165308 and obs3hpStart.value_coded in (1256,1257)
					
			union

			select 	e.encounter_id  																		
			from 	patient p  																														
					inner join encounter e on p.patient_id=e.patient_id  																				
					inner join obs o on o.encounter_id=e.encounter_id  																					
			where 	e.voided=0 and p.voided=0 and e.encounter_datetime<=curdate()  												
					and o.voided=0 and o.concept_id=6122 and o.value_coded in (1256,1257,1065) and e.encounter_type in (6,9) and  e.location_id=:location 
		) receivedTPT  on receivedTPT.encounter_id=seguimento.encounter_id		
        left join 
		-- TPT_INI_FR14: Ficha Clinica
		(  
			
			select ini3hp.patient_id,min(ini3hp.data_inicio_3HP) data_inicio_3hpclinica
			from 
			(
				select inicio.patient_id,inicio.data_inicio_3HP  																							
				from   																																		
				(	select 	p.patient_id,min(e.encounter_datetime) data_inicio_3HP  																		
					from 	patient p  																													
							inner join encounter e on p.patient_id=e.patient_id  																			
							inner join obs o on o.encounter_id=e.encounter_id  																				
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate and  										
							o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  
							e.location_id=:location 			
					group by p.patient_id  																										
				)  																																	
				inicio   																																
				left join  																															
				(  																																	
					select 	p.patient_id,e.encounter_datetime data_inicio_3HP  																		
					from 	patient p  																												
							inner join encounter e on p.patient_id=e.patient_id  																		
							inner join obs o on o.encounter_id=e.encounter_id  																				
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 8 MONTH) and :endDate  						
							and o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) 
							and  e.location_id=:location
							
				   union 
				   
					select 	p.patient_id, e.encounter_datetime data_inicio_3HP																		
					from	patient p																												 	
							inner join encounter e on p.patient_id=e.patient_id																			 	
							inner join obs o on o.encounter_id=e.encounter_id 																				
					where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=60  and o.concept_id=23985 and o.value_coded in (23954,23984)  	
							and	e.encounter_datetime between (:startDate - INTERVAL 8 MONTH) and :endDate  and e.location_id= :location		 	 			
				)  																																		
			   inicioAnterior on inicio.patient_id=inicioAnterior.patient_id  																			
				and inicioAnterior.data_inicio_3HP between (inicio.data_inicio_3HP - INTERVAL 4 MONTH) and (inicio.data_inicio_3HP - INTERVAL 1 day) 
				where inicioAnterior.patient_id is null 
				
				union
				
				select 	p.patient_id,min(e.encounter_datetime) data_inicio_3HP  																		
				from 	patient p  																												
						inner join encounter e on p.patient_id=e.patient_id  																		
						inner join obs o on o.encounter_id=e.encounter_id  																				
				where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  						
						and o.voided=0 and o.concept_id=1719 and o.value_coded=165307 and e.encounter_type in (6,9) 
						and  e.location_id=:location 
				group by p.patient_id
				
				UNION
				
				select 	p.patient_id, min(e.encounter_datetime) data_inicio_3HP  																		
				from 	patient p														 			  															
						inner join encounter e on p.patient_id=e.patient_id																				 		
						inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id		 																				
						inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id																
				where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
						and obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=6 and  e.location_id=:location	  		
						and obs3hpStart.voided =0 and obs3hpStart.concept_id =165308 and obs3hpStart.value_coded=1256 							
				group by p.patient_id
			)ini3hp
			group by ini3hp.patient_id			
		) ini3hpclinica on ini3hpclinica.patient_id=inicio_tpi.patient_id  
		left join 
		-- TPT_INI_FR14: FILT
		(  	
			select ini3hp.patient_id,min(ini3hp.data_inicio_3HP) data_inicio_3hpfilt
			from 
			(
				select p.patient_id, min(e.encounter_datetime) data_inicio_3HP  																		
				from 	patient p														 			  															
						inner join encounter e on p.patient_id=e.patient_id																				 		
						inner join obs o on o.encounter_id=e.encounter_id		 																				
						inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																
				where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
						and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	  		
						and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 							
				group by p.patient_id
				
				union 
				
				select inicio.patient_id,inicio.data_inicio_3HP  																							
				 from 																																		
					(	
					
						Select firstFilt.patient_id,firstFilt.dataFirstFilt data_inicio_3HP
						from 
						(	select 	p.patient_id,min(e.encounter_datetime) dataFirstFilt  																	
							from 	patient p  																												
									inner join encounter e on p.patient_id=e.patient_id	 												
							where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  										
									and e.encounter_type=60 and  e.location_id=:location   					
							group by p.patient_id
						) firstFilt
						inner join encounter e on e.patient_id=firstFilt.patient_id
						inner join obs obsTPT on obsTPT.encounter_id=e.encounter_id
						left join obs seguimentoTPT on (seguimentoTPT.encounter_id=e.encounter_id and seguimentoTPT.voided=0 and 
															seguimentoTPT.concept_id=23987)
						where 	firstFilt.dataFirstFilt=e.encounter_datetime and 
								e.encounter_type=60 and obsTPT.voided=0 and obsTPT.concept_id=23985 and obsTPT.value_coded in (23954,23984) and 
								e.location_id=:location and (seguimentoTPT.value_coded in (1257,1267) or seguimentoTPT.value_coded is null)																													
					) inicio   																																	
				left join   																															
				(  																																		
					select p.patient_id,e.encounter_datetime data_inicio_3HP 
					from 	patient p  															
							inner join encounter e on p.patient_id=e.patient_id  																				
							inner join obs o on o.encounter_id=e.encounter_id  																				
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 4 MONTH) and :endDate  	
							and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 	
					
					union
					
					select 	p.patient_id, e.encounter_datetime data_inicio_3HP																			 	
					from	patient p																												 		
							inner join encounter e on p.patient_id=e.patient_id																			 		
							inner join obs o on o.encounter_id=e.encounter_id																			 		
					where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and o.concept_id=1719 and o.value_coded in (23954,165307) 		 		
							and e.encounter_datetime  between (:startDate - INTERVAL 4 MONTH) and :endDate  and e.location_id=:location 

					union 
					
					select 	p.patient_id, e.encounter_datetime data_inicio_3HP  																		
					from 	patient p														 			  															
							inner join encounter e on p.patient_id=e.patient_id																				 		
							inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id		 																				
							inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id																
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 4 MONTH) and :endDate	 			  									
							and obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=6 and  e.location_id=:location	  		
							and obs3hpStart.voided =0 and obs3hpStart.concept_id =165308 and obs3hpStart.value_coded=1256
					union			
					
					-- Change concept 
					select 	p.patient_id,obs3hpStart.value_datetime data_inicio_3HP  																		
					from 	patient p  																													
							inner join encounter e on p.patient_id=e.patient_id  																			
							inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id 
							inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id 
					where 	e.voided=0 and p.voided=0 and obs3hpStart.value_datetime between (:startDate - INTERVAL 4 MONTH) and :endDate and  										
							obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=53 and  
							e.location_id=:location and obs3hpStart.concept_id=165326 and obs3hpStart.voided=0			 
				)  																																			
				inicioAnterior on inicioAnterior.patient_id=inicio.patient_id 																				
					and	inicioAnterior.data_inicio_3HP between (inicio.data_inicio_3HP - INTERVAL 4 MONTH) and (inicio.data_inicio_3HP - INTERVAL 1 day)  	
				where inicioAnterior.patient_id is null	
			) ini3hp
			group by ini3hp.patient_id
		) ini3hpfilt on ini3hpfilt.patient_id=inicio_tpi.patient_id
		left join 
		-- TPT_INI_FR14: Ficha Resumo
		(  
			-- Concept Change
			select 	p.patient_id,min(obs3hpStart.value_datetime) data_inicio_3hpResumo  																		
			from 	patient p  																													
					inner join encounter e on p.patient_id=e.patient_id  																			
					inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id 
					inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id 
			where 	e.voided=0 and p.voided=0 and obs3hpStart.value_datetime between :startDate and :endDate and  										
					obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=53 and  
					e.location_id=:location and obs3hpStart.concept_id=165326 and obs3hpStart.voided=0			
			group by p.patient_id		
		) ini3hpResumo on ini3hpResumo.patient_id=inicio_tpi.patient_id 
        left join 
		-- TPT_INI_FR15: Last FILT Dispensation with 3HP
		(  
			select 	filt.patient_id,filt.dataMaxFilt data_final_3hpfilt,
					if(obsDispensa.concept_id is null,'',if(obsDispensa.value_coded=1098,'Mensal','Trimestral')) tipoDispensa3hp
			from 
			(  
				select maxFilt.patient_id,maxFilt.dataMaxFilt,max(e2.encounter_id) encounter_id
				from 
				(
					select  p.patient_id,max(encounter_datetime) dataMaxFilt 
					from 	patient p  
							inner join encounter e on p.patient_id=e.patient_id 
							inner join obs o on o.encounter_id=e.encounter_id 
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime<=curdate() and  
							e.encounter_type=60 and e.location_id=:location and o.voided=0 and 
							o.concept_id=23985 and o.value_coded in (23954,23984)
					group by p.patient_id
					
				) maxFilt 
				inner join encounter e2 on  e2.patient_id=maxFilt.patient_id
				where 	e2.voided=0 and e2.encounter_type=60 and e2.location_id=:location and 
						e2.encounter_datetime=maxFilt.dataMaxFilt
				group by maxFilt.patient_id
			) filt 
			left join obs obsDispensa on obsDispensa.encounter_id=filt.encounter_id and obsDispensa.voided=0 and obsDispensa.concept_id=23986						
        ) final3hpfilt on final3hpfilt.patient_id=inicio_tpi.patient_id  
		left join 
		(
			select patient_id,max(data_final_3HP) data_final_3HP
			from 
			(
				select 	p.patient_id, max(e.encounter_datetime) data_final_3HP  																		
				from 	patient p														 			  															
						inner join encounter e on p.patient_id=e.patient_id																				 		
						inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id		 																				
						inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id																
				where 	e.voided=0 and p.voided=0 and e.encounter_datetime BETWEEN :startDate and curdate()	 			  									
						and obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=6 and  e.location_id=:location	  		
						and obs3hpStart.voided =0 and obs3hpStart.concept_id =165308 and obs3hpStart.value_coded=1267 							
				group by p.patient_id
				
				union 
				
				-- obs change
				select 	p.patient_id,max(obs3hpStart.value_datetime) data_final_3HP  																		
				from 	patient p  																													
						inner join encounter e on p.patient_id=e.patient_id  																			
						inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id 
						inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id 
				where 	e.voided=0 and p.voided=0 and obs3hpStart.value_datetime BETWEEN :startDate and curdate() and  										
						obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=53 and  
						e.location_id=:location and obs3hpStart.concept_id=165327 and obs3hpStart.voided=0			
				group by p.patient_id
			) hp3final
			group by patient_id			
		) final3hp on final3hp.patient_id=inicio3hp_TPT_INI_FR4.patient_id and final3hp.data_final_3HP>=inicio3hp_TPT_INI_FR4.data_inicio_3HP		
		left join 
		
		/*
			TPT_INI_FR16:
			3HP Completion Date on Ficha Clínica – Sheet 1: Column N
			Profilaxia TPT=”3HP” and Estado da Profilaxia marked with the value Fim(F) on Ficha Clínica between the 3HP Start Date 
			(obtained in TPT_INI_FR14) and until the report generation date 
			(Note: if more than one Ficha Clínica exists the system should consider the most recent date amongst the sources)

		*/
		(
			select 	p.patient_id, max(e.encounter_datetime) data_final_3HP  																		
			from 	patient p														 			  															
					inner join encounter e on p.patient_id=e.patient_id																				 		
					inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id		 																				
					inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id																
			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and curdate()	 			  									
					and obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=6 and  e.location_id=:location	  		
					and obs3hpStart.voided =0 and obs3hpStart.concept_id =165308 and obs3hpStart.value_coded=1267 							
			group by p.patient_id
		) TPT_INI_FR16 on inicio3hp_TPT_INI_FR4.patient_id=TPT_INI_FR16.patient_id and TPT_INI_FR16.data_final_3HP>=inicio3hp_TPT_INI_FR4.data_inicio_3HP
		left join 
		/*
			3HP Completion Date on Ficha Resumo – Sheet 1: Column O
			The most recent Profilaxia TPT = “3HP” and Data da Última Profilaxia TPT registered in Ficha Resumo – Mastercard 
			between the 3HP Start Date (obtained in TPT_INI_FR14) and until the report generation date

		*/
		(
			-- obs change
			select 	p.patient_id,max(obs3hpStart.value_datetime) data_final_3HP  																		
			from 	patient p  																													
					inner join encounter e on p.patient_id=e.patient_id  																			
					inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id 
					inner join obs obs3hpStart on obs3hpStart.encounter_id=e.encounter_id 
			where 	e.voided=0 and p.voided=0 and obs3hpStart.value_datetime BETWEEN :startDate and curdate() and  										
					obs3hp.voided=0 and obs3hp.concept_id=23985 and obs3hp.value_coded=23954 and e.encounter_type=53 and  
					e.location_id=:location and obs3hpStart.concept_id=165327 and obs3hpStart.voided=0			
			group by p.patient_id
		) TPT_INI_FR16_CO on inicio3hp_TPT_INI_FR4.patient_id=TPT_INI_FR16_CO.patient_id and TPT_INI_FR16_CO.data_final_3HP>=inicio3hp_TPT_INI_FR4.data_inicio_3HP
		/*Not specified 3HP Data Final on FILT */ 		
		
		left join 
		(  	/*
				TPT_INI_FR19:
				IPT Initiation Date on FILT – Sheet 1: Column RM
				The earliest IPT drug pick-up date registered on FILT (Regime TPT = “Isoniazida” or “Isoniazida + Piridoxina”) 
				that falls during the reporting period				
			*/
			select p.patient_id, min(e.encounter_datetime) data_inicio_inhfilt  																		
			from 	patient p														 			  														
					inner join encounter e on p.patient_id=e.patient_id																				 		
					inner join obs o on o.encounter_id=e.encounter_id		 																				
					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																
			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
					and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	  		 	
					and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 							
			group by p.patient_id 
		) iniinhfilt on iniinhfilt.patient_id=inicio_tpi.patient_id  
        left join  
		(  					
			/*
				TPT_INI_FR19:
				IPT Initiation Date on Ficha Resumo – Sheet 1: Column TO - 
				The earliest Última profilaxia Isoniazida (Data Início)IPT initiation date registered in Ficha Resumo 
				- Mastercard (Última profilaxia Isoniazida (Data Início) or (Última profilaxia TPT with value “Isoniazida (INH)” and 
				Data Inicio da Profilaxia TPT)) that falls during the reporting period 
			*/
			select 	p.patient_id,min(o.value_datetime) data_inicio_inhresumo  																			
			from 	patient p  																														
					inner join encounter e on p.patient_id=e.patient_id  																				
					inner join obs o on o.encounter_id=e.encounter_id  																					
			where 	e.voided=0 and p.voided=0 and o.value_datetime between :startDate and :endDate  													
					and o.voided=0 and o.concept_id=6128 and e.encounter_type=53 and e.location_id=:location 									
			group by p.patient_id
			
			union 
			
			-- Change concept
			select 	p.patient_id,min(obsInhStart.value_datetime) data_inicio_inhresumo  																		
			from 	patient p  																													
					inner join encounter e on p.patient_id=e.patient_id  																			
					inner join obs obsInh on obsInh.encounter_id=e.encounter_id 
					inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id 
			where 	e.voided=0 and p.voided=0 and obsInhStart.value_datetime between :startDate and :endDate and  										
					obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=53 and  
					e.location_id=:location and obsInhStart.concept_id=165326 and obsInhStart.voided=0			
			group by p.patient_id 		
		
		) iniinhresumo on iniinhresumo.patient_id=inicio_tpi.patient_id  
        left join  
		(  	
			/*
				TPT_INI_FR19:
				IPT Initiation Date on Ficha Resumo – Sheet 1: Column TO
				The earliest Última profilaxia Isoniazida (Data Início)IPT initiation date registered in Ficha Resumo - 
				Mastercard (Última profilaxia Isoniazida (Data Início) or (Última profilaxia TPT with value “Isoniazida (INH)” and 
				Data Inicio da Profilaxia TPT)) that falls during the reporting period 
			*/
			select patient_id,min(data_inicio_inhSeguimento) data_inicio_inhSeguimento
			from 
			(
				select 	p.patient_id,min(o.value_datetime) data_inicio_inhSeguimento  																			
				from 	patient p  																														
						inner join encounter e on p.patient_id=e.patient_id  																				
						inner join obs o on o.encounter_id=e.encounter_id  																					
				where 	e.voided=0 and p.voided=0 and o.value_datetime between :startDate and :endDate  													
						and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9) and e.location_id=:location 									
				group by p.patient_id
				
				union 
				
				select 	p.patient_id,min(e.encounter_datetime) data_inicio_inhSeguimento  																		
				from 	patient p  																														
						inner join encounter e on p.patient_id=e.patient_id  																				
						inner join obs o on o.encounter_id=e.encounter_id  																					
				where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  												
						and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location 				
				group by p.patient_id
				
				union 
				
				select 	p.patient_id, min(e.encounter_datetime) data_inicio_inhSeguimento  																		
				from 	patient p														 			  															
						inner join encounter e on p.patient_id=e.patient_id																				 		
						inner join obs obsInh on obsInh.encounter_id=e.encounter_id		 																				
						inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id																
				where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
						and obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=6 and  e.location_id=:location	  		
						and obsInhStart.voided =0 and obsInhStart.concept_id =165308 and obsInhStart.value_coded=1256 							
				group by p.patient_id
			) iniSeg
			group by patient_id
			
        ) iniinhseguimento on iniinhseguimento.patient_id=inicio_tpi.patient_id  
		left join 
		(  
            /*
				TPT_INI_FR20:
				Last INH FILT: Date – Sheet 1: Column U
				The Most recent FILT that has Regime de TPT with the values (Isoniazida or Isoniazida + Piridoxina) 
				marked until the report generation date

			*/
			select 	filt.patient_id,filt.dataMaxFilt data_final_inhfilt,
					if(obsDispensa.concept_id is null,'',if(obsDispensa.value_coded=1098,'Mensal','Trimestral')) tipoDispensainh
			from 
			(  
				select maxFilt.patient_id,maxFilt.dataMaxFilt,max(e2.encounter_id) encounter_id
				from 
				(
					select  p.patient_id,max(encounter_datetime) dataMaxFilt 
					from 	patient p  
							inner join encounter e on p.patient_id=e.patient_id 
							inner join obs o on o.encounter_id=e.encounter_id 
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime<=curdate() and  
							e.encounter_type=60 and e.location_id=:location and o.voided=0 and 
							o.concept_id=23985 and o.value_coded in (656,23982)
					group by p.patient_id
					
				) maxFilt 
				inner join encounter e2 on  e2.patient_id=maxFilt.patient_id
				where 	e2.voided=0 and e2.encounter_type=60 and e2.location_id=:location and 
						e2.encounter_datetime=maxFilt.dataMaxFilt
				group by maxFilt.patient_id
			) filt 
			left join obs obsDispensa on obsDispensa.encounter_id=filt.encounter_id and obsDispensa.voided=0 and obsDispensa.concept_id=23986							 
        ) finalinhfilt on finalinhfilt.patient_id=inicio_tpi.patient_id  
		left join  
		(  
			/*TPT_INI_FR21:
				IPT Completion Date on Ficha Resumo – Sheet 1: Column X
				The most recent Última Profilaxia Isoniazida (Data Fim) or (Última profilaxia TPT with value “Isoniazida (INH)” 
				and Data de Fim da Profilaxia TPT) registered in Ficha Resumo – Mastercard between the IPT Start Date 
				(obtained in TPT_INI_FR19) and until the report generation date			
			*/
			select patient_id,max(data_final_inhresumo) data_final_inhresumo
			from 
			(
				select p.patient_id,max(o.value_datetime) data_final_inhresumo 
				from 	patient p  
						inner join encounter e on p.patient_id=e.patient_id  
						inner join obs o on o.encounter_id=e.encounter_id  
				where 	e.voided=0 and p.voided=0 and o.value_datetime between :startDate and curdate() and  
						o.voided=0 and o.concept_id=6129 and e.encounter_type=53 and e.location_id=:location  
				group by p.patient_id 

				union 
				
				-- Concept Change
				select 	p.patient_id,max(obsInhStart.value_datetime) data_final_inhresumo  																		
				from 	patient p  																													
						inner join encounter e on p.patient_id=e.patient_id  																			
						inner join obs obsInh on obsInh.encounter_id=e.encounter_id 
						inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id 
				where 	e.voided=0 and p.voided=0 and obsInhStart.value_datetime between :startDate and curdate() and  										
						obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=53 and  
						e.location_id=:location and obsInhStart.concept_id=165327 and obsInhStart.voided=0			
				group by p.patient_id
			) finalResumo
			group by patient_id
		) finalinhresumo on finalinhresumo.patient_id=inicioInh_TPT_INI_FR5.patient_id and inicioInh_TPT_INI_FR5.data_inicio_tpi>=finalinhresumo.data_final_inhresumo
        left join  
		(  				
			/* TPT_INI_FR21:
				IPT Completion Date on Ficha Clínica or Ficha de Seguimento– Sheet 1: Column W
				Profilaxia (INH) marked with the value Fim(F) or (Profilaxia TPT with the value “Isoniazida (INH)” and 
				Estado da Profilaxia with the value “Fim (F)”) marked on Ficha Clínica – 
				Mastercard or Profilaxia com INH – TPI (Data Fim) marked in Ficha de Seguimento 
				between the IPT Start Date (obtained in TPT_INI_FR19) and until the report generation date 		
			*/
			select patient_id, max(data_final_inhSeguimento) data_final_inhSeguimento
			from 
			(	select 	p.patient_id,max(o.value_datetime) data_final_inhSeguimento  																			
				from 	patient p  																														
						inner join encounter e on p.patient_id=e.patient_id  																				
						inner join obs o on o.encounter_id=e.encounter_id  																					
				where 	e.voided=0 and p.voided=0 and o.value_datetime between :startDate and curdate()  													
						and o.voided=0 and o.concept_id=6129 and e.encounter_type in (6,9) and e.location_id=:location 									
				group by p.patient_id
				
				union 
				
				select 	p.patient_id,max(e.encounter_datetime) data_final_inhSeguimento  																		
				from 	patient p  																														
						inner join encounter e on p.patient_id=e.patient_id  																				
						inner join obs o on o.encounter_id=e.encounter_id  																					
				where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate  												
						and o.voided=0 and o.concept_id=6122 and o.value_coded=1267 and e.encounter_type in (6,9) and  e.location_id=:location 				
				group by p.patient_id
				
				union 
				
				select 	p.patient_id, max(e.encounter_datetime) data_final_inhSeguimento  																		
				from 	patient p														 			  															
						inner join encounter e on p.patient_id=e.patient_id																				 		
						inner join obs obsInh on obsInh.encounter_id=e.encounter_id		 																				
						inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id																
				where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and :endDate	 			  									
						and obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=6 and  e.location_id=:location	  		
						and obsInhStart.voided =0 and obsInhStart.concept_id =165308 and obsInhStart.value_coded=1267 							
				group by p.patient_id	
			) finalClinica
			group by patient_id				
         )finalinhseguimento on finalinhseguimento.patient_id=inicioInh_TPT_INI_FR5.patient_id and inicioInh_TPT_INI_FR5.data_inicio_tpi>=finalinhseguimento.data_final_inhSeguimento  
		 left join  
		 ( 
			select patient_id,max(data_fim_tpi) data_final_inh 
			from  
				(  
						
					select p.patient_id,max(o.value_datetime) data_fim_tpi 
					from 	patient p  
							inner join encounter e on p.patient_id=e.patient_id  
							inner join obs o on o.encounter_id=e.encounter_id  
					where 	e.voided=0 and p.voided=0 and o.value_datetime between :startDate and curdate() and  
							o.voided=0 and o.concept_id=6129 and e.encounter_type=53 and e.location_id=:location  
					group by p.patient_id 

					union 
					
					-- Concept Change
					select 	p.patient_id,max(obsInhStart.value_datetime) data_fim_tpi  																		
					from 	patient p  																													
							inner join encounter e on p.patient_id=e.patient_id  																			
							inner join obs obsInh on obsInh.encounter_id=e.encounter_id 
							inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id 
					where 	e.voided=0 and p.voided=0 and obsInhStart.value_datetime between :startDate and curdate() and  										
							obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=53 and  
							e.location_id=:location and obsInhStart.concept_id=165327 and obsInhStart.voided=0			
					group by p.patient_id

					union
					
					
					
					select 	p.patient_id,max(o.value_datetime) data_fim_tpi  																			
					from 	patient p  																														
							inner join encounter e on p.patient_id=e.patient_id  																				
							inner join obs o on o.encounter_id=e.encounter_id  																					
					where 	e.voided=0 and p.voided=0 and o.value_datetime between :startDate and curdate()  													
							and o.voided=0 and o.concept_id=6129 and e.encounter_type in (6,9) and e.location_id=:location 									
					group by p.patient_id
					
					union 
					
					select 	p.patient_id,max(e.encounter_datetime) data_fim_tpi  																		
					from 	patient p  																														
							inner join encounter e on p.patient_id=e.patient_id  																				
							inner join obs o on o.encounter_id=e.encounter_id  																					
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and curdate()  												
							and o.voided=0 and o.concept_id=6122 and o.value_coded=1267 and e.encounter_type in (6,9) and  e.location_id=:location 				
					group by p.patient_id
					
					union 
					
					select 	p.patient_id, max(e.encounter_datetime) data_fim_tpi  																		
					from 	patient p														 			  															
							inner join encounter e on p.patient_id=e.patient_id																				 		
							inner join obs obsInh on obsInh.encounter_id=e.encounter_id		 																				
							inner join obs obsInhStart on obsInhStart.encounter_id=e.encounter_id																
					where 	e.voided=0 and p.voided=0 and e.encounter_datetime between :startDate and curdate()	 			  									
							and obsInh.voided=0 and obsInh.concept_id=23985 and obsInh.value_coded=656 and e.encounter_type=6 and  e.location_id=:location	  		
							and obsInhStart.voided =0 and obsInhStart.concept_id =165308 and obsInhStart.value_coded=1267 							
					group by p.patient_id	
            				 
				) finalSeguimento  
				group by patient_id  
        )finalinh on finalinh.patient_id=inicioInh_TPT_INI_FR5.patient_id		 
		left join 
		(  
            				select patient_id,decisao from  (  select inicio_real.patient_id,  
            				gravida_real.data_gravida,  lactante_real.data_parto,  
            				if(max(gravida_real.data_gravida) is null and max(lactante_real.data_parto) is null,null,  
            				if(max(gravida_real.data_gravida) is null,'Lactante',  
            				if(max(lactante_real.data_parto) is null,'Gravida',  
            				if(max(lactante_real.data_parto)>max(gravida_real.data_gravida),'Lactante','Gravida')))) decisao from (	  
            				select p.patient_id  from patient p  inner join encounter e on e.patient_id=p.patient_id   
            				where e.voided=0 and p.voided=0 and e.encounter_type in (5,7) and e.encounter_datetime<=curdate() and e.location_id = :location  
            				union  select pg.patient_id from patient p  
            				inner join patient_program pg on p.patient_id=pg.patient_id  
            				where pg.voided=0 and p.voided=0 and program_id in (1,2) and date_enrolled<=curdate() and location_id=:location  
            				union  Select p.patient_id from patient p  
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=53 and   
            				o.concept_id=23891 and o.value_datetime is not null and   
            				o.value_datetime<=curdate() and e.location_id=:location  )inicio_real  left join  (  
            				Select p.patient_id,e.encounter_datetime data_gravida from patient p   
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1982 and value_coded=1065 and   
            				e.encounter_type in (5,6) and e.encounter_datetime  between :startDate and curdate() and e.location_id=:location  
            				union  Select p.patient_id,e.encounter_datetime data_gravida from patient p  
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1279 and   
            				e.encounter_type in (5,6) and e.encounter_datetime between :startDate and curdate() and e.location_id=:location  
            				union  Select p.patient_id,e.encounter_datetime data_gravida from patient p  
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1600 and   
            				e.encounter_type in (5,6) and e.encounter_datetime between :startDate and curdate() and e.location_id=:location	  
            				union  Select p.patient_id,e.encounter_datetime data_gravida from patient p   
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6334 and value_coded=6331 and   
            				e.encounter_type in (5,6) and e.encounter_datetime between :startDate and curdate() and e.location_id=:location		  
            				union  select pp.patient_id,pp.date_enrolled data_gravida from patient_program pp   
            				where pp.program_id=8 and pp.voided=0 and   
            				pp.date_enrolled between :startDate and curdate() and pp.location_id=:location  union  
            				Select p.patient_id,obsART.value_datetime data_gravida from patient p   
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				inner join obs obsART on e.encounter_id=obsART.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=1982 and o.value_coded=1065 and   
            				e.encounter_type=53 and obsART.value_datetime between :startDate and curdate() and e.location_id=:location and   
            				obsART.concept_id=1190 and obsART.voided=0  union  
            				Select p.patient_id,o.value_datetime data_gravida from patient p  
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=1465 and   
            				e.encounter_type=6 and o.value_datetime between :startDate and curdate() and e.location_id=:location  
            				) gravida_real on gravida_real.patient_id=inicio_real.patient_id    left join   (  
            				Select p.patient_id,o.value_datetime data_parto from patient p  
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				where  p.voided=0 and e.voided=0 and o.voided=0 and concept_id=5599 and   
            				e.encounter_type in (5,6) and o.value_datetime between :startDate and curdate() and e.location_id=:location	  
            				union  Select p.patient_id, e.encounter_datetime data_parto from patient p   
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6332 and value_coded=1065 and  
            				e.encounter_type=6 and e.encounter_datetime between :startDate and curdate() and e.location_id=:location  
            				union  Select p.patient_id, obsART.value_datetime data_parto from patient p   
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				inner join obs obsART on e.encounter_id=obsART.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=6332 and o.value_coded=1065 and   
            				e.encounter_type=53 and e.location_id=:location and   
            				obsART.value_datetime between :startDate and curdate() and   
            				obsART.concept_id=1190 and obsART.voided=0  union  
            				Select p.patient_id, e.encounter_datetime data_parto from patient p   
            				inner join encounter e on p.patient_id=e.patient_id  
            				inner join obs o on e.encounter_id=o.encounter_id  
            				where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6334 and value_coded=6332 and   
            				e.encounter_type in (5,6) and e.encounter_datetime between :startDate and curdate() and e.location_id=:location  
            				union  select pg.patient_id,ps.start_date data_parto from patient p   
            				inner join patient_program pg on p.patient_id=pg.patient_id  
            				inner join patient_state ps on pg.patient_program_id=ps.patient_program_id  
            				where pg.voided=0 and ps.voided=0 and p.voided=0 and   
            				pg.program_id=8 and ps.state=27 and ps.end_date is null and   
            				ps.start_date between :startDate and curdate() and location_id=:location  
            				) lactante_real on lactante_real.patient_id=inicio_real.patient_id  
            				where lactante_real.data_parto is not null or gravida_real.data_gravida is not null  
            				group by inicio_real.patient_id  ) gravidaLactante		  
            				inner join person pe on pe.person_id=gravidaLactante.patient_id		  
            				where pe.voided=0 and pe.gender='F'  
        )gravidaLactante on gravidaLactante.patient_id=inicio_tpi.patient_id  
		left join 
		(  
            				select pid1.* from patient_identifier pid1  inner join  (  
            				select patient_id,max(patient_identifier_id) id  from patient_identifier  where voided=0  
            				group by patient_id  ) pid2  
            				where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id  
         ) pid on pid.patient_id=inicio_tpi.patient_id  left join  (  
            				select pn1.* from person_name pn1  inner join  (  
            				select person_id,max(person_name_id) id  from person_name  where voided=0  
            				group by person_id  ) pn2  where pn1.person_id=pn2.person_id and pn1.person_name_id=pn2.id 
            				) pn on pn.person_id=inicio_tpi.patient_id  
) tpt  
group by patient_id  					