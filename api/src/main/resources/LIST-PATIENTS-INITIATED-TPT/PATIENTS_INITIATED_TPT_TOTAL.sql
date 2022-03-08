select inicio_tpi.patient_id  																													
from  																																			
	( 	/*
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