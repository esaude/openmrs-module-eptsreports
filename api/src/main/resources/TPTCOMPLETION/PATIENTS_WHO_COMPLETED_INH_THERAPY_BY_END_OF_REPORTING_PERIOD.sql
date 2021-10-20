select distinct inicio_inh.patient_id  																											
from 																																			
	(	select p.patient_id, o.value_datetime data_inicio_inh 																				 		
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
		where e.voided=0 and p.voided=0 and e.encounter_datetime <= :endDate and e.location_id=:location    										
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
				(	select p.patient_id, e.encounter_datetime data_inicio_inh  																		
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
					    left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
							and seguimentoTPT.concept_id =23987  																						
							and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
							and seguimentoTPT.voided =0)			
					where e.voided=0 and p.voided=0 and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60  
					 	and e.encounter_datetime < :endDate and e.location_id=:location and seguimentoTPT.obs_id is null	 						
				)  																																	
			inicio 																																	
			left join  																																
				(	select p.patient_id, o.value_datetime data_inicio_inh 																			
				     from patient p 																												
				     	inner join encounter e on p.patient_id=e.patient_id 																		
				        inner join obs o on o.encounter_id=e.encounter_id 																		    
				  	where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128  						
				     	and o.value_datetime < :endDate and e.location_id=:location 		 														
			 	)  																																	
			inicio_sem_inh on inicio.patient_id = inicio_sem_inh.patient_id 																		
			where inicio_sem_inh.patient_id is null 																								
       	) 															      								 										
	)
inicio_inh  																																	
	inner join        																																
		(	select patient_id, data_final_inh         																								
			from 																																	
				(	select p.patient_id, o.value_datetime data_final_inh    																		
				    	from patient p    																											
				   		inner join encounter e on p.patient_id=e.patient_id              															
				    	inner join obs o on o.encounter_id=e.encounter_id       															    	
				     where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6129          				
				    		and o.value_datetime <= :endDate and e.location_id=:location     														
				     union             																												
				     select p.patient_id, e.encounter_datetime data_final_inh               														
				     from patient p    																												
				     	inner join encounter e on p.patient_id=e.patient_id        																	
				        inner join obs o on o.encounter_id=e.encounter_id                															
				      where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=6 and o.concept_id=6122 and o.value_coded=1267            
				      	and e.encounter_datetime<= :endDate and e.location_id= :location    														
				  )  																																
			endINH                																													
		)  																																			
	termino_inh  																																	
		on inicio_inh.patient_id=termino_inh.patient_id      																						
	where termino_inh.data_final_inh between  inicio_inh.data_inicio_inh + interval 173 day and inicio_inh.data_inicio_inh + interval 365 day  
union	

select inicio_inh.patient_id 
from(
select distinct inicio_inh.patient_id ,data_inicio_inh																											
from 																																			
	(	select p.patient_id, o.value_datetime data_inicio_inh 																				 		
          from patient p 																													 	
          	inner join encounter e on p.patient_id=e.patient_id 																		 		
             	inner join obs o on o.encounter_id=e.encounter_id 																			 	
        	where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128  						 		
             	and o.value_datetime < :endDate and e.location_id=:location 																 	
          union	 																																
          select p.patient_id, e.encounter_datetime data_inicio_inh 																				
          from patient p 																												 		
          	inner join encounter e on p.patient_id=e.patient_id 																		 		
             	inner join obs o on o.encounter_id=e.encounter_id 																			 	
       	where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  and o.concept_id=6122  									
       		and o.value_coded=1256 and e.encounter_datetime < :endDate and e.location_id=:location 												
    )
inicio_inh 
	inner join encounter e on e.patient_id=inicio_inh.patient_id 
	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 
where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded in (1257,1065,1256) 
	and e.encounter_datetime between (inicio_inh.data_inicio_inh + INTERVAL 1 DAY)  and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH) 
	and e.location_id=:location 
	group by inicio_inh.patient_id,e.encounter_datetime having count(*)>=5 
) inicio_inh
inner join encounter e on e.patient_id=inicio_inh.patient_id 
	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 
	where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded =1267 
		and e.encounter_datetime between inicio_inh.data_inicio_inh + interval 173 day and inicio_inh.data_inicio_inh + interval 365 day  
		and e.location_id=:location 
		group by inicio_inh.patient_id,inicio_inh.data_inicio_inh having count(*)>=1 
union
select inicio_inh.patient_id 
from(
	select distinct inicio_inh.patient_id ,data_inicio_inh																											
from 																																			
	(	select p.patient_id, o.value_datetime data_inicio_inh 																				 		
          from patient p 																													 	
          	inner join encounter e on p.patient_id=e.patient_id 																		 		
             	inner join obs o on o.encounter_id=e.encounter_id 																			 	
        	where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128  						 		
             	and o.value_datetime < :endDate and e.location_id=:location 																 	
          union	 																																
          select p.patient_id, e.encounter_datetime data_inicio_inh 																				
          from patient p 																												 		
          	inner join encounter e on p.patient_id=e.patient_id 																		 		
             	inner join obs o on o.encounter_id=e.encounter_id 																			 	
       	where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  and o.concept_id=6122  									
       		and o.value_coded=1256 and e.encounter_datetime < :endDate and e.location_id=:location 																							      								 										
	)
inicio_inh 
		inner join encounter e on e.patient_id=inicio_inh.patient_id       
		inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id  
		inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id and obsDTINH.voided=0              
	where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded in (1257,1065,1256)   
		and obsDTINH.concept_id=1719 and obsDTINH.value_coded=23955  
		and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 5 MONTH) and e.location_id=:location   
	group by inicio_inh.patient_id, e.encounter_datetime having count(*)>=2 
	) inicio_inh
inner join encounter e on e.patient_id=inicio_inh.patient_id 
	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 
	where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded =1267 
		and e.encounter_datetime between inicio_inh.data_inicio_inh + interval 173 day and inicio_inh.data_inicio_inh + interval 365 day  
		and e.location_id=:location 
		group by inicio_inh.patient_id,inicio_inh.data_inicio_inh having count(*)>=1 
union
select inicio_inh.patient_id
from
	(
	select termino_inh.patient_id,data_inicio_inh
	from
		(	select d1.patient_id,data_inicio_inh 
			from	(	select distinct inicio_inh.patient_id ,data_inicio_inh
					from
						(	select p.patient_id, o.value_datetime data_inicio_inh 																				
							from patient p 																													
								inner join encounter e on p.patient_id=e.patient_id 																		
			   					inner join obs o on o.encounter_id=e.encounter_id 																			
							where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128  						
			   					and o.value_datetime < :endDate and e.location_id=:location 
			   			union	 																																
				          select p.patient_id, e.encounter_datetime data_inicio_inh 																				
				          from patient p 																												 		
				          	inner join encounter e on p.patient_id=e.patient_id 																		 		
				             	inner join obs o on o.encounter_id=e.encounter_id 																			 	
				       	where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9)  and o.concept_id=6122  									
				       		and o.value_coded=1256 and e.encounter_datetime < :endDate and e.location_id=:location 	
			   			) 
					inicio_inh
						inner join encounter e on e.patient_id=inicio_inh.patient_id                 
						inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id                      
					where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded in (1257,1065,1256)
						and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH) and e.location_id=:location          
						group by inicio_inh.patient_id,inicio_inh.data_inicio_inh having count(*)>=3    
				) 
			d1
			left join
			(	select distinct inicio_inh.patient_id 
				from
					(	select p.patient_id, o.value_datetime data_inicio_inh 																				
						from patient p 																													
							inner join encounter e on p.patient_id=e.patient_id 																		
		   					inner join obs o on o.encounter_id=e.encounter_id 																			
						where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128  						
		   					and o.value_datetime < :endDate and e.location_id=:location 
		   			) 
				inicio_inh
					inner join encounter e on e.patient_id=inicio_inh.patient_id                 
					inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id            
					inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id    
				where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded in (1257,1065,1256)    
					and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH)   
					and obsDTINH.voided=0  and obsDTINH.concept_id=1719 and obsDTINH.value_coded=23955 and e.location_id=:location          
					group by inicio_inh.patient_id,inicio_inh.data_inicio_inh having count(*)>=1   
			) d2 
					on d1.patient_id = d2.patient_id   
				where d2.patient_id is null 
		) 
	termino_inh 
	inner join 
	(	select distinct inicio_inh.patient_id 
		from
			(	select p.patient_id, o.value_datetime data_inicio_inh 																				
				from patient p 																													
					inner join encounter e on p.patient_id=e.patient_id 																		
						inner join obs o on o.encounter_id=e.encounter_id 																			
				where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128  						
						and o.value_datetime < :endDate and e.location_id=:location 
			) 
		inicio_inh
			inner join encounter e on e.patient_id=inicio_inh.patient_id                 
	       	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id            
	        	inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id and obsDTINH.voided=0         
		where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded in (1257,1065,1256)    
			and obsDTINH.concept_id=1719 and obsDTINH.value_coded=23955            
	         	and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH) and e.location_id=:location  
			group by inicio_inh.patient_id,inicio_inh.data_inicio_inh having count(*)>=1  
	) inh_at_least_1DT  on inh_at_least_1DT.patient_id = termino_inh.patient_id
) 
inicio_inh
inner join encounter e on e.patient_id=inicio_inh.patient_id 
	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 
	where e.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (6,9) and obsLevTPI.concept_id=6122 and obsLevTPI.value_coded =1267 
		and e.encounter_datetime between inicio_inh.data_inicio_inh + interval 173 day and inicio_inh.data_inicio_inh + interval 365 day  
		and e.location_id=:location 
		group by inicio_inh.patient_id,inicio_inh.data_inicio_inh having count(*)>=1 
union
select distinct inicio_inh.patient_id 
from
	(    select p.patient_id, e.encounter_datetime data_inicio_inh 
		from	patient p 											 
			inner join encounter e on p.patient_id=e.patient_id																				 
			inner join obs o on o.encounter_id=e.encounter_id	 																			 
			inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
		where e.voided=0 and p.voided=0 and e.encounter_datetime <= :endDate and e.location_id=:location   
			and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)	 						 
			and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60	
		union
       	(
			select inicio.patient_id, inicio.data_inicio_inh 
			from
				(	select p.patient_id, e.encounter_datetime data_inicio_inh 
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
					     left join obs seguimentoTPT on (seguimentoTPT.encounter_id=e.encounter_id and seguimentoTPT.concept_id =	23987)																				
					where e.voided=0 and p.voided=0 and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 
					 	and e.encounter_datetime < :endDate and e.location_id=:location and seguimentoTPT.obs_id is null	
				) 
			inicio
			left join 
				(	select p.patient_id, o.value_datetime data_inicio_inh 																				
				     from patient p 																													
				     	inner join encounter e on p.patient_id=e.patient_id 																		
				          inner join obs o on o.encounter_id=e.encounter_id 																			
				  	where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128  						
				     	and o.value_datetime < :endDate and e.location_id=:location 		
			 	) 
			inicio_sem_inh on inicio.patient_id = inicio_sem_inh.patient_id
			where inicio_sem_inh.patient_id is null
       	) 															      								
	) 
inicio_inh 
	inner join encounter e on e.patient_id=inicio_inh.patient_id      
	inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id            
	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 
where e.voided=0 and obsDTINH.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (60)        
      and obsDTINH.concept_id=23986 and obsDTINH.value_coded=1098  and obsLevTPI.concept_id=23985 and obsLevTPI.value_coded in (656,23982)  
      and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH) and e.location_id=:location  
      group by inicio_inh.patient_id,inicio_inh.data_inicio_inh having count(*)>=6  
union   
select distinct inicio_inh.patient_id 
from
	(    select p.patient_id, e.encounter_datetime data_inicio_inh 
		from	patient p 											 
			inner join encounter e on p.patient_id=e.patient_id																				 
			inner join obs o on o.encounter_id=e.encounter_id	 																			 
			inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
		where e.voided=0 and p.voided=0 and e.encounter_datetime <= :endDate and e.location_id=:location   
			and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)	 						 
			and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60	
		union
       	(
			select inicio.patient_id, inicio.data_inicio_inh 
			from
				(	select p.patient_id, e.encounter_datetime data_inicio_inh 
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
					     left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
							and seguimentoTPT.concept_id =23987  																						
							and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
							and seguimentoTPT.voided =0)																					
					where e.voided=0 and p.voided=0 and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 
					 	and e.encounter_datetime < :endDate and e.location_id=:location and seguimentoTPT.obs_id is null	
				) 
			inicio
			left join 
				(	select p.patient_id, o.value_datetime data_inicio_inh 																				
				     from patient p 																													
				     	inner join encounter e on p.patient_id=e.patient_id 																		
				          inner join obs o on o.encounter_id=e.encounter_id 																			
				  	where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128  						
				     	and o.value_datetime < :endDate and e.location_id=:location 		
			 	) 
			inicio_sem_inh on inicio.patient_id = inicio_sem_inh.patient_id
			where inicio_sem_inh.patient_id is null
       	) 															      								
	) 
inicio_inh
	inner join encounter e on e.patient_id=inicio_inh.patient_id      
	inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id            
	inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id 
where e.voided=0 and obsDTINH.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (60)        
      and obsDTINH.concept_id=23986 and obsDTINH.value_coded=23720  and obsLevTPI.concept_id=23985 and obsLevTPI.value_coded in (656,23982)  
      and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 5 MONTH) and e.location_id=:location  
      group by inicio_inh.patient_id,inicio_inh.data_inicio_inh having count(*)>=2 
union               
select inicio_inh.patient_id 
from            
	(    
		select distinct inicio_inh.patient_id 
		from
			(    select p.patient_id, e.encounter_datetime data_inicio_inh 
				from	patient p 											 
					inner join encounter e on p.patient_id=e.patient_id																				 
					inner join obs o on o.encounter_id=e.encounter_id	 																			 
					inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
				where e.voided=0 and p.voided=0 and e.encounter_datetime <= :endDate and e.location_id=:location   
					and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)	 						 
					and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60	
				union
		       	(
					select inicio.patient_id, inicio.data_inicio_inh 
					from
						(	select p.patient_id, e.encounter_datetime data_inicio_inh 
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
							    left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
									and seguimentoTPT.concept_id =23987  																						
									and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
									and seguimentoTPT.voided =0)																				
							where e.voided=0 and p.voided=0 and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 
							 	and e.encounter_datetime < :endDate and e.location_id=:location and seguimentoTPT.obs_id is null	
						) 
					inicio
					left join 
						(	select p.patient_id, o.value_datetime data_inicio_inh 																				
						     from patient p 																													
						     	inner join encounter e on p.patient_id=e.patient_id 																		
						          inner join obs o on o.encounter_id=e.encounter_id 																			
						  	where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128  						
						     	and o.value_datetime < :endDate and e.location_id=:location 		
					 	) 
					inicio_sem_inh on inicio.patient_id = inicio_sem_inh.patient_id
					where inicio_sem_inh.patient_id is null
		     	) 															      								
			) 
		inicio_inh 
			inner join encounter e on e.patient_id=inicio_inh.patient_id               
			inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id      
			inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id          
		where e.voided=0 and obsDTINH.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (60)  
			and obsDTINH.concept_id=23986 and obsDTINH.value_coded=1098  and obsLevTPI.concept_id=23985 and obsLevTPI.value_coded in (656,23982)  
			and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH) and e.location_id=:location  
			group by inicio_inh.patient_id,inicio_inh.data_inicio_inh having count(*)>=3          
	) 
inicio_inh   
inner join     
(
	select distinct inicio_inh.patient_id 
	from
		(    select p.patient_id, e.encounter_datetime data_inicio_inh 
			from	patient p 											 
				inner join encounter e on p.patient_id=e.patient_id																				 
				inner join obs o on o.encounter_id=e.encounter_id	 																			 
				inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id														 
			where e.voided=0 and p.voided=0 and e.encounter_datetime <= :endDate and e.location_id=:location   
				and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1256,1705)	 						 
				and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60	
			union
	       	(
				select inicio.patient_id, inicio.data_inicio_inh 
				from
					(	select p.patient_id, e.encounter_datetime data_inicio_inh 
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
						     left join obs seguimentoTPT on (e.encounter_id =seguimentoTPT.encounter_id	 													
								and seguimentoTPT.concept_id =23987  																						
								and seguimentoTPT.value_coded in(1256,1257,1705,1267)  																		
								and seguimentoTPT.voided =0)																					
						where e.voided=0 and p.voided=0 and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 
						 	and e.encounter_datetime < :endDate and e.location_id=:location and seguimentoTPT.obs_id is null	
					) 
				inicio
				left join 
					(	select p.patient_id, o.value_datetime data_inicio_inh 																				
					     from patient p 																													
					     	inner join encounter e on p.patient_id=e.patient_id 																		
					          inner join obs o on o.encounter_id=e.encounter_id 																			
					  	where p.voided=0 and  e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53) and o.concept_id=6128  						
					     	and o.value_datetime < :endDate and e.location_id=:location 		
				 	) 
				inicio_sem_inh on inicio.patient_id = inicio_sem_inh.patient_id
				where inicio_sem_inh.patient_id is null
	       	) 															      								
		) 
	inicio_inh 
		inner join encounter e on e.patient_id=inicio_inh.patient_id               
		inner join obs obsDTINH on e.encounter_id=obsDTINH.encounter_id      
		inner join obs obsLevTPI on e.encounter_id=obsLevTPI.encounter_id          
	where e.voided=0 and obsDTINH.voided=0 and obsLevTPI.voided=0 and e.encounter_type in (60)  
	  and obsDTINH.concept_id=23986 and obsDTINH.value_coded=23720  and obsLevTPI.concept_id=23985 and obsLevTPI.value_coded in (656,23982)  
	  and e.encounter_datetime between inicio_inh.data_inicio_inh and (inicio_inh.data_inicio_inh + INTERVAL 7 MONTH) and e.location_id=:location  
	  group by inicio_inh.patient_id,inicio_inh.data_inicio_inh having count(*)>=1 
) inicio_inh_dt on inicio_inh_dt.patient_id = inicio_inh.patient_id