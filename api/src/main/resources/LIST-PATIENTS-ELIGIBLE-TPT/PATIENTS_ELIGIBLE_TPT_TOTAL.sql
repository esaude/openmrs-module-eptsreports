select 	coorte12meses_final.patient_id patient_id 
            from 
            (select   	inicio_fila_seg_prox.*, 
            			GREATEST(COALESCE(data_fila,data_seguimento,data_recepcao_levantou),COALESCE(data_seguimento,data_fila,data_recepcao_levantou),COALESCE(data_recepcao_levantou,data_seguimento,data_fila))  data_usar_c, 
            GREATEST(COALESCE(data_proximo_lev,data_proximo_seguimento,data_recepcao_levantou30),COALESCE(data_proximo_seguimento,data_proximo_lev,data_recepcao_levantou30),COALESCE(data_recepcao_levantou30,data_proximo_seguimento,data_proximo_lev)) data_usar 
            from 
            (select 	inicio_fila_seg.*, 
            		max(obs_fila.value_datetime) data_proximo_lev, 
            		max(obs_seguimento.value_datetime) data_proximo_seguimento, 
            		date_add(data_recepcao_levantou, interval 30 day) data_recepcao_levantou30 
            from 
            (select inicio.*,		 
            		saida.data_estado,		 
            		max_fila.data_fila, 
            		max_consulta.data_seguimento, 
            		max_recepcao.data_recepcao_levantou 
            from 
            (	Select patient_id,min(data_inicio) data_inicio 
            		from 
            			(	 
            				Select 	p.patient_id,min(e.encounter_datetime) data_inicio 
            				from 	patient p  
            						inner join encounter e on p.patient_id=e.patient_id	 
            						inner join obs o on o.encounter_id=e.encounter_id 
            				where 	e.voided=0 and o.voided=0 and p.voided=0 and  
            						e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and  
            						e.encounter_datetime<=:endDate and e.location_id=:location 
            				group by p.patient_id 
            				union 
            				Select 	p.patient_id,min(value_datetime) data_inicio 
            				from 	patient p 
            						inner join encounter e on p.patient_id=e.patient_id 
            						inner join obs o on e.encounter_id=o.encounter_id 
            				where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and  
            						o.concept_id=1190 and o.value_datetime is not null and  
            						o.value_datetime<=:endDate and e.location_id=:location 
            				group by p.patient_id 
            				union 
            				select 	pg.patient_id,min(date_enrolled) data_inicio 
            				from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id 
            				where 	pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location 
            				group by pg.patient_id 
            				union 
            				  SELECT 	e.patient_id, MIN(e.encounter_datetime) AS data_inicio  
            				  FROM 		patient p 
            							inner join encounter e on p.patient_id=e.patient_id 
            				  WHERE		p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location 
            				  GROUP BY 	p.patient_id 
            				union 
            				Select 	p.patient_id,min(value_datetime) data_inicio 
            				from 	patient p 
            						inner join encounter e on p.patient_id=e.patient_id 
            						inner join obs o on e.encounter_id=o.encounter_id 
            				where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  
            						o.concept_id=23866 and o.value_datetime is not null and  
            						o.value_datetime<=:endDate and e.location_id=:location 
            				group by p.patient_id	 
            			) inicio_real 
            		group by patient_id 
            )inicio 
            left join 
            (select patient_id,max(data_estado) data_estado 
            	from  
            		( 
            			select 	pg.patient_id, 
            					max(ps.start_date) data_estado 
            			from 	patient p  
            					inner join patient_program pg on p.patient_id=pg.patient_id 
            					inner join patient_state ps on pg.patient_program_id=ps.patient_program_id 
            			where 	pg.voided=0 and ps.voided=0 and p.voided=0 and  
            					pg.program_id=2 and ps.state in (7,8,10) and ps.end_date is null and  
            					ps.start_date<=:endDate and location_id=:location 
            			group by pg.patient_id 
            			union 
            			select 	p.patient_id, 
            					max(o.obs_datetime) data_estado 
            			from 	patient p  
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs  o on e.encounter_id=o.encounter_id 
            			where 	e.voided=0 and o.voided=0 and p.voided=0 and  
            					e.encounter_type in (53,6) and o.concept_id in (6272,6273) and o.value_coded in (1706,1366,1709) and   
            					o.obs_datetime<=:endDate and e.location_id=:location 
            			group by p.patient_id 
            			union 
            			select person_id as patient_id,death_date as data_estado 
            			from person  
            			where dead=1 and death_date is not null and death_date<=:endDate 
            			union 
            			select 	p.patient_id, 
            					max(obsObito.obs_datetime) data_estado 
            			from 	patient p  
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs obsObito on e.encounter_id=obsObito.encounter_id 
            			where 	e.voided=0 and p.voided=0 and obsObito.voided=0 and  
            					e.encounter_type in (21,36,37) and  e.encounter_datetime<=:endDate and  e.location_id=:location and  
            					obsObito.concept_id in (2031,23944,23945) and obsObito.value_coded=1366	 
            			group by p.patient_id 
            		) allSaida 
            	group by patient_id			 
            ) saida on inicio.patient_id=saida.patient_id 
            left join 
            (	Select 	p.patient_id,max(encounter_datetime) data_fila 
            	from 	patient p  
            			inner join encounter e on e.patient_id=p.patient_id 
            	where 	p.voided=0 and e.voided=0 and e.encounter_type=18 and  
            			e.location_id=:location and e.encounter_datetime<=:endDate 
            	group by p.patient_id 
            ) max_fila on inicio.patient_id=max_fila.patient_id	 
            left join 
            (	Select 	p.patient_id,max(encounter_datetime) data_seguimento 
            	from 	patient p  
            			inner join encounter e on e.patient_id=p.patient_id 
            	where 	p.voided=0 and e.voided=0 and e.encounter_type in (6,9) and  
            			e.location_id=:location and e.encounter_datetime<=:endDate 
            	group by p.patient_id 
            ) max_consulta on inicio.patient_id=max_consulta.patient_id 
            left join 
            ( 
            	Select 	p.patient_id,max(value_datetime) data_recepcao_levantou 
            	from 	patient p 
            			inner join encounter e on p.patient_id=e.patient_id 
            			inner join obs o on e.encounter_id=o.encounter_id 
            	where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  
            			o.concept_id=23866 and o.value_datetime is not null and  
            			o.value_datetime<=:endDate and e.location_id=:location 
            	group by p.patient_id 
            ) max_recepcao on inicio.patient_id=max_recepcao.patient_id 
            group by inicio.patient_id 
            ) inicio_fila_seg 
            left join 
            	obs obs_fila on obs_fila.person_id=inicio_fila_seg.patient_id 
            	and obs_fila.voided=0 
            	and obs_fila.obs_datetime=inicio_fila_seg.data_fila 
            	and obs_fila.concept_id=5096 
            	and obs_fila.location_id=:location 
            left join 
            	obs obs_seguimento on obs_seguimento.person_id=inicio_fila_seg.patient_id 
            	and obs_seguimento.voided=0 
            	and obs_seguimento.obs_datetime=inicio_fila_seg.data_seguimento 
            	and obs_seguimento.concept_id=1410 
            	and obs_seguimento.location_id=:location 
            group by inicio_fila_seg.patient_id 
            ) inicio_fila_seg_prox 
            group by patient_id 
            ) coorte12meses_final 
            inner join person p on p.person_id=coorte12meses_final.patient_id		 
            left join  
            (	select pad1.* 
            	from person_address pad1 
            	inner join  
            	( 
            		select person_id,min(person_address_id) id  
            		from person_address 
            		where voided=0 
            		group by person_id 
            	) pad2 
            	where pad1.person_id=pad2.person_id and pad1.person_address_id=pad2.id 
            ) pad3 on pad3.person_id=coorte12meses_final.patient_id				 
            left join 			 
            (	select pn1.* 
            	from person_name pn1 
            	inner join  
            	( 
            		select person_id,min(person_name_id) id  
            		from person_name 
            		where voided=0 
            		group by person_id 
            	) pn2 
            	where pn1.person_id=pn2.person_id and pn1.person_name_id=pn2.id 
            ) pn on pn.person_id=coorte12meses_final.patient_id			 
            left join 
            (   select pid1.* 
            	from patient_identifier pid1 
            	inner join 
            	( 
            		select patient_id,min(patient_identifier_id) id 
            		from patient_identifier 
            		where voided=0 
            		group by patient_id 
            	) pid2 
            	where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id 
            ) pid on pid.patient_id=coorte12meses_final.patient_id 
            left join  
            (		select 	p.patient_id,min(o.value_datetime) data_inicio_tpi 
            		from	patient p 
            				inner join encounter e on p.patient_id=e.patient_id 
            				inner join obs o on o.encounter_id=e.encounter_id 
            		where 	e.voided=0 and p.voided=0 and o.value_datetime between (:endDate - INTERVAL 210 DAY) and :endDate and 
            				o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location 
            		group by p.patient_id 
            		union 
            		select 	p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
            		from	patient p 
            				inner join encounter e on p.patient_id=e.patient_id 
            				inner join obs o on o.encounter_id=e.encounter_id 
            		where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 210 DAY) and :endDate and 
            				o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location 
            		group by p.patient_id 
            		union
            		select p.patient_id,min(e.encounter_datetime) data_inicio_tpi from patient p														     
					inner join encounter e on p.patient_id=e.patient_id																				             
					inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
				where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 210 DAY) and :endDate	             
					and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	                 
					and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 
					group by p.patient_id 
            		union 
            		select inicio.patient_id,inicio.data_inicio_tpi 
            		from 
            		(	select 	p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 210 DAY) and :endDate  
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
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 210 DAY) and :endDate  
           		 			and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location 
            					and seguimentoTPT.obs_id is null
            			group by p.patient_id 
            		)inicio 
            		left join  
            		(select 	p.patient_id,e.encounter_datetime data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 15 MONTH) and :endDate and 
            					o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location 
            			union
            			select p.patient_id, o.value_datetime data_inicio_tpi 
	            		from	patient p 
	            				inner join encounter e on p.patient_id=e.patient_id 
	            				inner join obs o on o.encounter_id=e.encounter_id 
	            		where 	e.voided=0 and p.voided=0 and o.value_datetime between (:endDate - INTERVAL 15 MONTH) and :endDate and 
	            				o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location 
	            	)
            		inicioAnterior on inicioAnterior.patient_id=inicio.patient_id and  
            							inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 210 day) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
            		where inicioAnterior.patient_id is null 
            		union 
            		select inicio.patient_id,inicio.data_inicio_tpi 
            		from  
            		( select 	p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 120 DAY) and :endDate and 
            					o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location 
            			group by p.patient_id 
            		) inicio  
            		left join 		 
            		(	select 	p.patient_id,e.encounter_datetime data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 9 MONTH) and :endDate and 
            					o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location 
            		     union
            		     select 	p.patient_id, e.encounter_datetime data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 9 MONTH) and :endDate and 
            					o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 
            		) inicioAnterior on inicio.patient_id=inicioAnterior.patient_id and  
            							inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 120 day) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
            		where inicioAnterior.patient_id is null 
            		union
            		select p.patient_id,min(e.encounter_datetime) data_inicio_tpi from patient p														     
					inner join encounter e on p.patient_id=e.patient_id																				             
					inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
				where e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 120 DAY) and :endDate	             
					and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	                 
					and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 
					group by p.patient_id  
            		union
            		select inicio.patient_id,inicio.data_inicio_tpi 
            		from 
            		(select 	p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id		
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 120 DAY) and :endDate 
            					and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257,1267) 	
            					and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 
            			group by p.patient_id 
					union
					select 	p.patient_id,min(e.encounter_datetime) data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            					left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
								and seguimentoTPT.concept_id =23987  																						
								and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
								and seguimentoTPT.voided =0)	
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 120 DAY) and :endDate 
            					and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 
            					and seguimentoTPT.obs_id is null
            				group by p.patient_id 
            		) inicio  
            		left join  
            		( select 	p.patient_id,e.encounter_datetime data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 9 MONTH) and :endDate and 
            					o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location
            		     union
            		     select 	p.patient_id, e.encounter_datetime data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 9 MONTH) and :endDate and 
            					o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location 
            		) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id and  
            							inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 120 day) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
            		where inicioAnterior.patient_id is null 
            ) TPT_ELIG_FR4 on TPT_ELIG_FR4.patient_id=coorte12meses_final.patient_id 
            left join  
            (select inicio_inh.patient_id  
            	from 					
            	( 	select 	p.patient_id, o.value_datetime data_inicio_inh 						 
            		from	patient p 					   	 
            				inner join encounter e on p.patient_id=e.patient_id 				 
            				inner join obs o on o.encounter_id=e.encounter_id 					 
            		where  p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128 					 
            				and o.value_datetime <=:endDate and e.location_id=:location 		 
            		union 					 
            		select 	p.patient_id, e.encounter_datetime data_inicio_inh 					 
            		from 	patient p 		 
            				inner join encounter e on p.patient_id=e.patient_id 				 
            				inner join obs o on o.encounter_id=e.encounter_id 					 
            		where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  and o.concept_id=6122 and o.value_coded=1256   
            				and e.encounter_datetime <=:endDate and e.location_id=:location 
            		union
            		select p.patient_id, e.encounter_datetime data_inicio_inh from patient p														     
					inner join encounter e on p.patient_id=e.patient_id																				             
					inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
				where e.voided=0 and p.voided=0 and e.encounter_datetime <=:endDate	             
					and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location	                 
					and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705) 
            		union  
            		select inicio.patient_id,inicio.data_inicio_tpi 
            		from 
            		(	select 	p.patient_id, e.encounter_datetime data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime <=:endDate
           		 			and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257)	
            					and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location 
            			union
					select p.patient_id, e.encounter_datetime data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            					left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
								and seguimentoTPT.concept_id =23987  																						
								and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
								and seguimentoTPT.voided =0)	
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime between (:endDate - INTERVAL 210 DAY) and :endDate  
           		 			and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location 
            					and seguimentoTPT.obs_id is null
            		)inicio 
            		left join  
            		(select 	p.patient_id,e.encounter_datetime data_inicio_tpi 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime <=:endDate  
            					and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location 
            			union
            			select p.patient_id, o.value_datetime data_inicio_tpi 
	            		from	patient p 
	            				inner join encounter e on p.patient_id=e.patient_id 
	            				inner join obs o on o.encounter_id=e.encounter_id 
	            		where 	e.voided=0 and p.voided=0 and o.value_datetime <=:endDate  
	            				and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location 
	            	)
            		inicioAnterior on inicioAnterior.patient_id=inicio.patient_id and  
            							inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) 
            		where inicioAnterior.patient_id is null 
            	) inicio_inh 				 
            	inner join 					 
            	(	select patient_id, data_final_inh 				
            		from( 					 
            			select 	p.patient_id, o.value_datetime data_final_inh 					 
            			from 	patient p 		 
            					inner join encounter e on p.patient_id=e.patient_id 				 
            					inner join obs o on o.encounter_id=e.encounter_id 					 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6129 						 
            					and o.value_datetime <= :endDate and e.location_id=:location 		 
            			union 
            			select 	p.patient_id, e.encounter_datetime data_final_inh 				 
            			from 	patient p 		 
            					inner join encounter e on p.patient_id=e.patient_id 			 
            					inner join obs o on o.encounter_id=e.encounter_id 				 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=6122 and o.value_coded=1267 		 
            					and e.encounter_datetime<=:endDate and e.location_id=:location  
            		) endINH 				 
            	) termino_inh on inicio_inh.patient_id=termino_inh.patient_id 					 
            	where termino_inh.data_final_inh between  inicio_inh.data_inicio_inh + interval 173 day and inicio_inh.data_inicio_inh + interval 365 day 
              union  
            	select inicio_inh.patient_id  
            	from 					
            	(	select 	p.patient_id, o.value_datetime data_inicio_inh 						 
            		from	patient p 					   	 
            				inner join encounter e on p.patient_id=e.patient_id 				 
            				inner join obs o on o.encounter_id=e.encounter_id 					 
            		where  p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128 					 
            				and o.value_datetime <=:endDate and e.location_id=:location 		 
            		union 					 
            		select 	p.patient_id, e.encounter_datetime data_inicio_inh 					 
            		from 	patient p 		 
            				inner join encounter e on p.patient_id=e.patient_id 				 
            				inner join obs o on o.encounter_id=e.encounter_id 					 
            		where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  and o.concept_id=6122 and o.value_coded=1256   
            				and e.encounter_datetime <=:endDate and e.location_id=:location 
            	) inicio_inh 				 
            	inner join encounter e on e.patient_id=inicio_inh.patient_id 					 
            	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 				 			 
            	where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded in (1257,1065,1256)  
            		and e.encounter_datetime between (inicio_inh.data_inicio_inh + INTERVAL 1 day) and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH) 				 
            		and e.location_id=:location 				 
            	group by inicio_inh.patient_id,inicio_inh.data_inicio_inh  
            	having count(*)>=5  
            	union  
            	select inicio_inh.patient_id  
            	from 					
            	(	select 	p.patient_id, o.value_datetime data_inicio_inh 						 
            		from	patient p 					   	 
            				inner join encounter e on p.patient_id=e.patient_id 				 
            				inner join obs o on o.encounter_id=e.encounter_id 					 
            		where  p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128 					 
            				and o.value_datetime <=:endDate and e.location_id=:location 		 
            		union  
            		select 	p.patient_id, e.encounter_datetime data_inicio_inh 					 
            		from 	patient p 		 
            				inner join encounter e on p.patient_id=e.patient_id 				 
            				inner join obs o on o.encounter_id=e.encounter_id 					 
            		where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  and o.concept_id=6122 and o.value_coded=1256   
            				and e.encounter_datetime <=:endDate and e.location_id=:location			 
            	) inicio_inh 				 
            	inner join encounter e on e.patient_id=inicio_inh.patient_id 					 
            	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 				 
            	inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id and obsDTINH.voided=0 					
            	where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded in (1257,1065,1256)  
            		and obsDTINH.concept_id=1719 and obsDTINH.value_coded=23955 				 
            		and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 5 MONTH) and e.location_id=:location  
            	group by inicio_inh.patient_id,inicio_inh.data_inicio_inh  
            	having count(*)>=2 	 
            	union  
            	select inicio_inh.patient_id  
            	from 					
            	(	select inicio_inh.patient_id  
            		from 				
            		(						 
            			select 	p.patient_id, o.value_datetime data_inicio_inh 						 
            			from	patient p 					   	 
            					inner join encounter e on p.patient_id=e.patient_id 				 
            					inner join obs o on o.encounter_id=e.encounter_id 					 
            			where  p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128 					 
            					and o.value_datetime <=:endDate and e.location_id=:location 		 
            			union 					 
            			select 	p.patient_id, e.encounter_datetime data_inicio_inh 					 
            			from 	patient p 		 
            					inner join encounter e on p.patient_id=e.patient_id 				 
            					inner join obs o on o.encounter_id=e.encounter_id 					 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  and o.concept_id=6122 and o.value_coded=1256   
            					and e.encounter_datetime <=:endDate and e.location_id=:location	 
            		) inicio_inh 			 
            		inner join encounter e on e.patient_id=inicio_inh.patient_id 				 
            		inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 			 
            		where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded in (1257,1065,1256)  
            			and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH)  			 
            			and e.location_id=:location 			 
            		group by inicio_inh.patient_id,inicio_inh.data_inicio_inh  
            		having count(*)>=3 						 
            	) inicio_inh 				 
            	inner join 					 
            	(	select inicio_inh.patient_id  
            		from 				
            		( 	select 	p.patient_id, o.value_datetime data_inicio_inh 						 
            			from	patient p 					   	 
            					inner join encounter e on p.patient_id=e.patient_id 				 
            					inner join obs o on o.encounter_id=e.encounter_id 					 
            			where  p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128 					 
            					and o.value_datetime <=:endDate and e.location_id=:location 		 
            			union 					 
            			select 	p.patient_id, e.encounter_datetime data_inicio_inh 					 
            			from 	patient p 		 
            					inner join encounter e on p.patient_id=e.patient_id 				 
            					inner join obs o on o.encounter_id=e.encounter_id 					 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  and o.concept_id=6122 and o.value_coded=1256   
            					and e.encounter_datetime <=:endDate and e.location_id=:location	 
            		) inicio_inh 			 
            		inner join encounter e on e.patient_id=inicio_inh.patient_id 				 
            		inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 			 
            		inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id and obsDTINH.voided=0 				
            		where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded in (1257,1065,1256)  
            			and obsDTINH.concept_id=1719 and obsDTINH.value_coded=23955 			 
            			and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH) and e.location_id=:location  
            		group by inicio_inh.patient_id,inicio_inh.data_inicio_inh  
            		having count(*)>=1 						 
            	) inicio_inh_dt on inicio_inh_dt.patient_id = inicio_inh.patient_id 
            	union  
            	select inicio_inh.patient_id  
            	from 					
            	(	select 	p.patient_id, e.encounter_datetime data_inicio_inh  
            		from 	patient p	 
            				inner join encounter e on p.patient_id=e.patient_id	
            				inner join obs o on o.encounter_id=e.encounter_id	
            		where 	e.voided=0 and p.voided=0 and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60  
            				and e.encounter_datetime <=:endDate and e.location_id=:location 			 
            	) inicio_inh 				 
            	inner join encounter e on e.patient_id=inicio_inh.patient_id 					 
            	inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id 				 
            	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 				 
            	where e.voided=0 and obsDTINH.voided=0 and obsLevTPI.voided=0 and e.encounter_type=60 				
            		and obsDTINH.concept_id=23986 and obsDTINH.value_coded=1098   
            		and obsLevTPI.concept_id=23985 and obsLevTPI.value_coded in (656,23982)  
            		and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH) and e.location_id=:location  
            	group by inicio_inh.patient_id,inicio_inh.data_inicio_inh  
            	having count(*)>=6  
            	union 						 
            	select inicio_inh.patient_id  
            	from 					
            	( 	select 	p.patient_id, e.encounter_datetime data_inicio_inh  
            		from 	patient p	 
            				inner join encounter e on p.patient_id=e.patient_id	
            				inner join obs o on o.encounter_id=e.encounter_id	
            		where 	e.voided=0 and p.voided=0 and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60  
            				and e.encounter_datetime <=:endDate and e.location_id=:location    	 
            	) inicio_inh 				 
            	inner join encounter e on e.patient_id=inicio_inh.patient_id 					 
            	inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id 				 
            	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 				 
            	where e.voided=0 and obsDTINH.voided=0 and obsLevTPI.voided=0 and e.encounter_type=60 				
            		and obsDTINH.concept_id=23986 and obsDTINH.value_coded=23720   
            		and obsLevTPI.concept_id=23985 and obsLevTPI.value_coded in (656,23982)  
            		and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 5 MONTH) and e.location_id=:location  
            	group by inicio_inh.patient_id,inicio_inh.data_inicio_inh  
            	having count(*)>=2 
            	union 
            	select inicio_inh.patient_id  
            	from 					
            	( 	select inicio_inh.patient_id from 				
            		(	select 	p.patient_id, e.encounter_datetime data_inicio_inh  
            			from 	patient p	 
            					inner join encounter e on p.patient_id=e.patient_id	
            					inner join obs o on o.encounter_id=e.encounter_id	
            			where 	e.voided=0 and p.voided=0 and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60  
            					and e.encounter_datetime <=:endDate and e.location_id=:location   					
            		) inicio_inh 			 
            		inner join encounter e on e.patient_id=inicio_inh.patient_id 				 
            		inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id 			 
            		inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 			 
            		where e.voided=0 and obsDTINH.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (60) 			
            			and obsDTINH.concept_id=23986 and obsDTINH.value_coded=1098  and obsLevTPI.concept_id=23985 and obsLevTPI.value_coded in (656,23982)  
            			and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH) and e.location_id=:location  
            		group by inicio_inh.patient_id,inicio_inh.data_inicio_inh  
            		having count(*)>=3						 
            	) inicio_inh 				 
            	inner join 					 
            	( 	select inicio_inh.patient_id  
            		from 				
            		(	select 	p.patient_id, e.encounter_datetime data_inicio_inh  
            			from 	patient p	 
            					inner join encounter e on p.patient_id=e.patient_id	
            					inner join obs o on o.encounter_id=e.encounter_id	
            			where 	e.voided=0 and p.voided=0 and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60  
            		) inicio_inh 			 
            		inner join encounter e on e.patient_id=inicio_inh.patient_id				 
            		inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id 			 
            		inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 			 
            		where e.voided=0 and obsDTINH.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (60) 			
            			and obsDTINH.concept_id=23986 and obsDTINH.value_coded=23720  and obsLevTPI.concept_id=23985 and obsLevTPI.value_coded in (656,23982)  
            			and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH) and e.location_id=:location  
            		group by inicio_inh.patient_id,inicio_inh.data_inicio_inh  
            		having count(*)>=1   						 
            	) inicio_inh_dt on inicio_inh_dt.patient_id = inicio_inh.patient_id 
            )TPT_ELIG_FR8 on TPT_ELIG_FR8.patient_id=coorte12meses_final.patient_id 
            left join  
            (	select inicio_3HP.patient_id  
            	from (                                                                                                                                                     
            			select inicio.patient_id,  inicio.data_inicio_3HP
            			from
            				(	select p.patient_id, e.encounter_datetime data_inicio_3HP                                                                                         
			            		from patient p                                                                                                                             
			            			inner join encounter e on p.patient_id=e.patient_id                                                                                         
			            			inner join obs o on o.encounter_id=e.encounter_id                                                                                         
			            		where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  
			            				and o.concept_id=1719 and o.value_coded=23954                      
			            				and e.encounter_datetime <=:endDate and e.location_id=:location         
            				) inicio
            				left join
            				(	select p.patient_id, e.encounter_datetime data_inicio_3HP                                                                                         
			            		from patient p                                                                                                                             
			            			inner join encounter e on p.patient_id=e.patient_id                                                                                         
			            			inner join obs o on o.encounter_id=e.encounter_id                                                                                         
			            		where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  
			            				and o.concept_id=1719 and o.value_coded=23954                      
			            				and e.encounter_datetime <=:endDate and e.location_id=:location       
            				) inicioAnterior on inicioAnterior.patient_id  = inicio.patient_id 
            					and inicioAnterior.data_inicio_3HP between (inicio.data_inicio_3HP - INTERVAL 4 MONTH) and (inicio.data_inicio_3HP - INTERVAL 1 day) 
            				where inicioAnterior.patient_id is null                                                     
            	) inicio_3HP                                                                                                                                         
            	inner join encounter e on e.patient_id=inicio_3HP.patient_id                                                                                         
            	inner join obs o on o.encounter_id=e.encounter_id                                                                                                     
            	where e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and o.concept_id=1719 and o.value_coded=23954                                           
            			and e.encounter_datetime between inicio_3HP.data_inicio_3HP and (inicio_3HP.data_inicio_3HP + INTERVAL 4 month) and e.location_id=:location      
            	group by inicio_3HP.patient_id,inicio_3HP.data_inicio_3HP  
            	having count(*)>=3  
              union  
            	select inicio_3HP.patient_id  
            	from(                                                                                                                                                     
            		select inicio.patient_id,inicio.data_inicio_3HP 
            		from 
            		(	select 	p.patient_id, e.encounter_datetime data_inicio_3HP 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime <=:endDate
           		 			and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257,1267)	
            					and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 
            			union
					select p.patient_id, e.encounter_datetime data_inicio_3HP 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            					left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
								and seguimentoTPT.concept_id =23987  																						
								and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
								and seguimentoTPT.voided =0)	
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime <= :endDate 
           		 			and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 
            					and seguimentoTPT.obs_id is null
            		)inicio 
            		left join  
            		(select 	p.patient_id,e.encounter_datetime data_inicio_3HP 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime <=:endDate  
            					and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 
            			union
            			select p.patient_id, e.encounter_datetime data_inicio_3HP                                                                                         
	            		from patient p                                                                                                                             
	            			inner join encounter e on p.patient_id=e.patient_id                                                                                         
	            			inner join obs o on o.encounter_id=e.encounter_id                                                                                         
	            		where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  
	            				and o.concept_id=1719 and o.value_coded=23954                      
	            				and e.encounter_datetime <=:endDate and e.location_id=:location         
	            	)
            		inicioAnterior on inicioAnterior.patient_id=inicio.patient_id and  
            							inicioAnterior.data_inicio_3HP between (inicio.data_inicio_3HP - INTERVAL 4 MONTH) and (inicio.data_inicio_3HP - INTERVAL 1 day) 
            		where inicioAnterior.patient_id is null 
            		union
            		select p.patient_id, e.encounter_datetime data_inicio_3HP from patient p														     
					inner join encounter e on p.patient_id=e.patient_id																				             
					inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
				where e.voided=0 and p.voided=0 and e.encounter_datetime <=:endDate	             
					and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	                 
					and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)                                                                          
            	) inicio_3HP                                                                                                                                         
            	inner join encounter e on e.patient_id= inicio_3HP.patient_id                                                                                         
            	inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id                                                                                             
            	inner join obs obsTipo on obsTipo.encounter_id=e.encounter_id                                                                                         
            	where 	e.voided=0 and obs3hp.voided=0 and obsTipo.voided=0                                                                                              
            			and e.encounter_type=60 and obs3hp.concept_id=23985  
            			and obs3hp.value_coded in (23954,23984) and obsTipo.concept_id=23986 and obsTipo.value_coded=23720     
            			and e.encounter_datetime between inicio_3HP.data_inicio_3HP and (inicio_3HP.data_inicio_3HP + INTERVAL 4 month) and e.location_id=:location      
            	group by inicio_3HP.patient_id,inicio_3HP.data_inicio_3HP  
            	having count(*)>=1  
            	union   
            	select inicio_3HP.patient_id  
            	from                                                                                                                     
            	( 	select inicio.patient_id,inicio.data_inicio_3HP 
            		from 
            		(	select 	p.patient_id, e.encounter_datetime data_inicio_3HP 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime <=:endDate
           		 			and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257,1267)	
            					and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 
            			union
					select p.patient_id, e.encounter_datetime data_inicio_3HP 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            					left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
								and seguimentoTPT.concept_id =23987  																						
								and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
								and seguimentoTPT.voided =0)	
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime <= :endDate 
           		 			and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 
            					and seguimentoTPT.obs_id is null
            		)inicio 
            		left join  
            		(select 	p.patient_id,e.encounter_datetime data_inicio_3HP 
            			from	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on o.encounter_id=e.encounter_id 
            			where 	e.voided=0 and p.voided=0 and e.encounter_datetime <=:endDate  
            					and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location 
            			union
            			select p.patient_id, e.encounter_datetime data_inicio_3HP                                                                                         
	            		from patient p                                                                                                                             
	            			inner join encounter e on p.patient_id=e.patient_id                                                                                         
	            			inner join obs o on o.encounter_id=e.encounter_id                                                                                         
	            		where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  
	            				and o.concept_id=1719 and o.value_coded=23954                      
	            				and e.encounter_datetime <=:endDate and e.location_id=:location         
	            	)
            		inicioAnterior on inicioAnterior.patient_id=inicio.patient_id and  
            							inicioAnterior.data_inicio_3HP between (inicio.data_inicio_3HP - INTERVAL 4 MONTH) and (inicio.data_inicio_3HP - INTERVAL 1 day) 
            		where inicioAnterior.patient_id is null 
            		union
            		select p.patient_id, e.encounter_datetime data_inicio_3HP from patient p														     
					inner join encounter e on p.patient_id=e.patient_id																				             
					inner join obs o on o.encounter_id=e.encounter_id		                                                                                     
					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id																	 
				where e.voided=0 and p.voided=0 and e.encounter_datetime <=:endDate	             
					and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	                 
					and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)                                                                         
            	) inicio_3HP                                                                                                                                         
            	inner join encounter e on e.patient_id= inicio_3HP.patient_id                                                                                         
            	inner join obs obs3hp on obs3hp.encounter_id=e.encounter_id                                                                                             
            	inner join obs obsTipo on obsTipo.encounter_id=e.encounter_id                                                                                         
            	where 	e.voided=0 and obs3hp.voided=0 and obsTipo.voided=0                                                                                              
            			and e.encounter_type=60 and obs3hp.concept_id=23985 and obs3hp.value_coded in (23954,23984)  
            			and obsTipo.concept_id=23986 and obsTipo.value_coded=1098     
            			and e.encounter_datetime between inicio_3HP.data_inicio_3HP and (inicio_3HP.data_inicio_3HP + INTERVAL 4 month)  
            			and e.location_id=:location     
            	group by inicio_3HP.patient_id,inicio_3HP.data_inicio_3HP  
            	having count(*)>=3 
            )TPT_ELIG_FR9 on TPT_ELIG_FR9.patient_id=coorte12meses_final.patient_id 
            left join  
            (	select 	p.patient_id 
            	from 	patient p 
            			inner join encounter e on p.patient_id=e.patient_id 
            			inner join obs o on o.encounter_id=e.encounter_id 
            	where 	e.encounter_type in (6,9) and e.voided=0 and o.voided=0 and p.voided=0  
            			and o.concept_id=1268 and o.value_coded=1256 and e.location_id=:location  
            			and o.obs_datetime between (:endDate - INTERVAL 210 DAY) and :endDate 
            	union 
            	select 	p.patient_id 
            	from 	patient p 
            			inner join encounter e on p.patient_id=e.patient_id 
            			inner join obs o on o.encounter_id=e.encounter_id 
            	where 	e.encounter_type in (6,9) and e.voided=0 and o.voided=0 and p.voided=0 and 
            			o.concept_id=1113 and e.location_id=:location and  
            			o.value_datetime between (:endDate - INTERVAL 210 DAY) and :endDate	 
            	union 
            	select 	patient_id 
            	from 	patient_program 
            	where	program_id=5 and voided=0 and date_enrolled between (:endDate - INTERVAL 210 DAY) and :endDate and 
            			location_id=:location 
            	union 
                SELECT 	p.patient_id  
            	FROM 	patient p 
            			INNER JOIN encounter e ON e.patient_id = p.patient_id 
            			INNER JOIN obs o ON o.encounter_id = e.encounter_id 
                WHERE 	p.voided = 0 AND e.voided = 0 AND o.voided = 0 
                        AND e.encounter_type=53 
                        AND o.concept_id = 42 
                        AND o.value_coded=1065 
                        AND e.location_id=:location AND o.obs_datetime between (:endDate - INTERVAL 210 DAY) and :endDate 
            	union 
            	select 	p.patient_id 
            	from 	patient p 
            			inner join encounter e on p.patient_id=e.patient_id 
            			inner join obs o on o.encounter_id=e.encounter_id 
            	where 	e.encounter_type=6 and e.voided=0 and o.voided=0 and p.voided=0 and 
            			o.concept_id=23761 and o.value_coded=1065 and  
            			e.encounter_datetime between (:endDate - INTERVAL 210 DAY) and :endDate and  
            			e.location_id=:location 
            )TPT_ELIG_FR10 on TPT_ELIG_FR10.patient_id=coorte12meses_final.patient_id 
            left join  
            (    SELECT p.patient_id  
            	FROM 	patient p 
            			INNER JOIN encounter e ON e.patient_id = p.patient_id 
            			INNER JOIN obs o ON o.encounter_id = e.encounter_id 
                WHERE 	p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (6,9) 
                        AND o.concept_id in (6257,23758) AND o.value_coded=1065 
                        AND e.location_id=:location AND e.encounter_datetime between (:endDate - INTERVAL 14 DAY) and :endDate 
               UNION 
                SELECT 	p.patient_id  
            	FROM 	patient p 
            			INNER JOIN encounter e ON e.patient_id = p.patient_id 
            			INNER JOIN obs o ON o.encounter_id = e.encounter_id 
                WHERE 	p.voided = 0 AND e.voided = 0 AND o.voided = 0 
                        AND e.encounter_type=6 
                        AND o.concept_id = 1766 
                        AND o.value_coded in (1763,1764,1762,1760,23760,1765,161) 
                        AND e.location_id=:location AND e.encounter_datetime between (:endDate - INTERVAL 14 DAY) and :endDate  
            	UNION 
                SELECT 	p.patient_id  
            	FROM 	patient p 
            			INNER JOIN encounter e ON e.patient_id = p.patient_id 
            			INNER JOIN obs o ON o.encounter_id = e.encounter_id 
                WHERE 	p.voided = 0 AND e.voided = 0 AND o.voided = 0 
                        AND e.encounter_type=6 
                        AND o.concept_id = 23722 
                        AND o.value_coded in (23723,23774,23951,307,12) 
                        AND e.location_id=:location AND e.encounter_datetime between (:endDate - INTERVAL 14 DAY) and :endDate   
            	UNION	 
                SELECT 	p.patient_id  
            	FROM 	patient p 
            			INNER JOIN encounter e ON e.patient_id = p.patient_id 
            			INNER JOIN obs o ON o.encounter_id = e.encounter_id 
                WHERE 	p.voided = 0 AND e.voided = 0 AND o.voided = 0 
                        AND e.encounter_type=6 
                        AND o.concept_id in (23723,23774,23951,307,12) 
                        AND o.value_coded in (703,664,23956,664,1138) 
                        AND e.location_id=:location AND e.encounter_datetime between (:endDate - INTERVAL 14 DAY) and :endDate  
               UNION 
            	 SELECT 	p.patient_id  
            	FROM 	patient p 
            			INNER JOIN encounter e ON e.patient_id = p.patient_id 
            			INNER JOIN obs o ON o.encounter_id = e.encounter_id 
                WHERE 	p.voided = 0 AND e.voided = 0 AND o.voided = 0 
                        AND e.encounter_type=13 
                        AND o.concept_id in (23723,165189,165191,165192,23774,23951,307,165185) 
                        AND e.location_id=:location AND e.encounter_datetime between (:endDate - INTERVAL 14 DAY) and :endDate  
               UNION 
            	 SELECT p.patient_id  
            	FROM	 patient p 
            			INNER JOIN encounter e ON e.patient_id = p.patient_id 
            			INNER JOIN obs o ON o.encounter_id = e.encounter_id 
                WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 
                        AND e.encounter_type IN (6,9) 
                            AND o.concept_id = 6277 
                            AND o.value_coded = 703 
                            AND e.location_id=:location AND e.encounter_datetime between (:endDate - INTERVAL 14 DAY) and :endDate  
                 UNION	 
            	select	p.patient_id					 
            	from 	patient p 
            			inner join encounter e on p.patient_id=e.patient_id 
            			inner join obs oRastreio on e.encounter_id=oRastreio.encounter_id and oRastreio.concept_id=6257 and oRastreio.voided=0 and oRastreio.value_coded in (1065,1066) 
            			inner join obs oInvestigacao on e.encounter_id=oInvestigacao.encounter_id and oInvestigacao.concept_id=6277 and oInvestigacao.voided=0 and oInvestigacao.value_coded=664 
            	where 	e.encounter_type in (6,9) and e.voided=0 and e.location_id=:location and e.encounter_datetime between (:endDate - INTERVAL 14 DAY) and :endDate 			 
            	group by p.patient_id 
            ) TPT_ELIG_FR12 on TPT_ELIG_FR12.patient_id=coorte12meses_final.patient_id 
            left join  
            (select patient_id,decisao 
            	from  
            	(	select 	inicio_real.patient_id, 
            				gravida_real.data_gravida, 
            				lactante_real.data_parto, 
            				if(max(gravida_real.data_gravida) is null and max(lactante_real.data_parto) is null,null, 
            					if(max(gravida_real.data_gravida) is null,'LACTANTE', 
            						if(max(lactante_real.data_parto) is null,'GRÁVIDA', 
            							if(max(lactante_real.data_parto)>max(gravida_real.data_gravida),'LACTANTE','GRÁVIDA')))) decisao 
            		from 
            		(	select 	p.patient_id  
            			from 	patient p inner join encounter e on e.patient_id=p.patient_id  
            			where 	e.voided=0 and p.voided=0 and e.encounter_type in (5,7) and e.encounter_datetime<=:endDate and e.location_id=:location 
            			union 
            			select 	pg.patient_id 
            			from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id 
            			where 	pg.voided=0 and p.voided=0 and program_id in (1,2) and date_enrolled<=:endDate and location_id=:location 
            			union 
            			Select 	p.patient_id 
            			from 	patient p 
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on e.encounter_id=o.encounter_id 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=53 and  
            					o.concept_id=23891 and o.value_datetime is not null and  
            					o.value_datetime<=:endDate and e.location_id=:location 
            		)inicio_real 
            		left join  
            		(	Select 	p.patient_id,e.encounter_datetime data_gravida 
            			from 	patient p  
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on e.encounter_id=o.encounter_id 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1982 and value_coded=1065 and  
            					e.encounter_type in (5,6) and e.encounter_datetime  between (curdate() - INTERVAL 9 MONTH) and curdate() and e.location_id=:location 
            			union 
            			Select 	p.patient_id,e.encounter_datetime data_gravida 
            			from 	patient p inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on e.encounter_id=o.encounter_id 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1279 and  
            					e.encounter_type in (5,6) and e.encounter_datetime between (curdate() - INTERVAL 9 MONTH) and curdate() and e.location_id=:location 
            			union 
            			Select 	p.patient_id,e.encounter_datetime data_gravida 
            			from 	patient p inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on e.encounter_id=o.encounter_id 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=1600 and  
            					e.encounter_type in (5,6) and e.encounter_datetime between (curdate() - INTERVAL 9 MONTH) and curdate() and e.location_id=:location	 
            			union 
            			Select 	p.patient_id,e.encounter_datetime data_gravida 
            			from 	patient p  
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on e.encounter_id=o.encounter_id 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6334 and value_coded=6331 and  
            					e.encounter_type in (5,6) and e.encounter_datetime between (curdate() - INTERVAL 9 MONTH) and curdate() and e.location_id=:location		 
            			union 
            			select 	pp.patient_id,pp.date_enrolled data_gravida 
            			from 	patient_program pp  
            			where 	pp.program_id=8 and pp.voided=0 and  
            					pp.date_enrolled between (curdate() - INTERVAL 9 MONTH) and curdate() and pp.location_id=:location 
            			union 
            			Select 	p.patient_id,obsART.value_datetime data_gravida 
            			from 	patient p  
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on e.encounter_id=o.encounter_id 
            					inner join obs obsART on e.encounter_id=obsART.encounter_id 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=1982 and o.value_coded=1065 and  
            					e.encounter_type=53 and obsART.value_datetime between (curdate() - INTERVAL 9 MONTH) and curdate() and e.location_id=:location and  
            					obsART.concept_id=1190 and obsART.voided=0 
            			union  
            			Select 	p.patient_id,o.value_datetime data_gravida 
            			from 	patient p inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on e.encounter_id=o.encounter_id 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=1465 and  
            					e.encounter_type=6 and o.value_datetime between (curdate() - INTERVAL 9 MONTH) and curdate() and e.location_id=:location 
            			) gravida_real on gravida_real.patient_id=inicio_real.patient_id   
            		left join  
            		(Select 	p.patient_id,o.value_datetime data_parto 
            			from 	patient p inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on e.encounter_id=o.encounter_id 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=5599 and  
            					e.encounter_type in (5,6) and o.value_datetime between (curdate() - INTERVAL 18 MONTH) and curdate() and e.location_id=:location	 
            			union	 
            			Select 	p.patient_id, e.encounter_datetime data_parto 
            			from 	patient p  
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on e.encounter_id=o.encounter_id 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6332 and value_coded=1065 and  
            					e.encounter_type=6 and e.encounter_datetime between (curdate() - INTERVAL 18 MONTH) and curdate() and e.location_id=:location 
            			union 
            			Select 	p.patient_id, obsART.value_datetime data_parto 
            			from 	patient p  
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on e.encounter_id=o.encounter_id 
            					inner join obs obsART on e.encounter_id=obsART.encounter_id 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=6332 and o.value_coded=1065 and  
            					e.encounter_type=53 and e.location_id=:location and  
            					obsART.value_datetime between (curdate() - INTERVAL 18 MONTH) and curdate() and  
            					obsART.concept_id=1190 and obsART.voided=0 
            			union  
            			Select 	p.patient_id, e.encounter_datetime data_parto 
            			from 	patient p  
            					inner join encounter e on p.patient_id=e.patient_id 
            					inner join obs o on e.encounter_id=o.encounter_id 
            			where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=6334 and value_coded=6332 and  
            					e.encounter_type in (5,6) and e.encounter_datetime between (curdate() - INTERVAL 18 MONTH) and curdate() and e.location_id=:location 
            			union		 
            			select 	pg.patient_id,ps.start_date data_parto 
            			from 	patient p  
            					inner join patient_program pg on p.patient_id=pg.patient_id 
            					inner join patient_state ps on pg.patient_program_id=ps.patient_program_id 
            			where 	pg.voided=0 and ps.voided=0 and p.voided=0 and  
            					pg.program_id=8 and ps.state=27 and ps.end_date is null and  
            					ps.start_date between (curdate() - INTERVAL 18 MONTH) and curdate() and location_id=:location 
            		) lactante_real on lactante_real.patient_id=inicio_real.patient_id 
            		where 	lactante_real.data_parto is not null or gravida_real.data_gravida is not null 
            		group by inicio_real.patient_id 
            	) gravidaLactante		 
            	inner join person pe on pe.person_id=gravidaLactante.patient_id		 
            	where pe.voided=0 and pe.gender='F' 
            ) TPT_ELIG_FR16 on TPT_ELIG_FR16.patient_id=coorte12meses_final.patient_id 
            left join  
            (select max_consulta.patient_id,max_consulta.data_seguimento,obs_seguimento.value_datetime 
            	from  
            	(	Select 	p.patient_id,max(encounter_datetime) data_seguimento 
            		from 	patient p  
            				inner join encounter e on e.patient_id=p.patient_id 
            		where 	p.voided=0 and e.voided=0 and e.encounter_type in (6,9) and  
            				e.location_id=:location and e.encounter_datetime<=curdate() 
            		group by p.patient_id 
            	) max_consulta  
            	left join 
            		obs obs_seguimento on obs_seguimento.person_id=max_consulta.patient_id 
            		and obs_seguimento.voided=0 
            		and obs_seguimento.obs_datetime=max_consulta.data_seguimento 
            		and obs_seguimento.concept_id=1410 
            		and obs_seguimento.location_id=:location 
            ) TPT_ELIG_FR19 on TPT_ELIG_FR19.patient_id=coorte12meses_final.patient_id 
            where (data_estado is null or (data_estado is not null and  data_usar_c>data_estado)) and date_add(data_usar, interval 28 day) >=:endDate  
            		and  TPT_ELIG_FR4.patient_id is null  
            		and  TPT_ELIG_FR8.patient_id is null  
            		and  TPT_ELIG_FR9.patient_id is null  
            		and  TPT_ELIG_FR10.patient_id is null  
            		and  TPT_ELIG_FR12.patient_id is null group by patient_id  