        select
        (@cnt := @cnt + 1) as ID,
        coorte12meses_final.patient_id,
        coorte12meses_final.data_usar,
        pid.identifier as NID,
        pe.gender SEXO,
        if(round(datediff(:startDate,pe.birthdate)/365) >= 15,'A','C') as AGE ,
        DATE_FORMAT(coorte12meses_final.data_inicio, '%d-%m-%Y') DATA_INICIO_TARV,
        L.encounter_datetime DATA_CV,
        L.cv CV

        from (

        	select  inicio_fila_seg_prox.*,GREATEST(COALESCE(data_fila,data_seguimento,data_recepcao_levantou),COALESCE(data_seguimento,data_fila,data_recepcao_levantou),COALESCE(data_recepcao_levantou,data_seguimento,data_fila))  data_usar_c, 
            GREATEST(COALESCE(data_proximo_lev,data_recepcao_levantou30),COALESCE(data_recepcao_levantou30,data_proximo_lev)) data_usar from (select inicio_fila_seg.*, 
            max(obs_fila.value_datetime) data_proximo_lev, 
            max(obs_seguimento.value_datetime) data_proximo_seguimento, 
            date_add(data_recepcao_levantou, interval 30 day) data_recepcao_levantou30 from (select inicio.*,saida.data_estado,max_fila.data_fila,max_consulta.data_seguimento, max_recepcao.data_recepcao_levantou from ( 
            Select patient_id,min(data_inicio) data_inicio from ( 
            Select p.patient_id,min(e.encounter_datetime) data_inicio from patient p  
            inner join encounter e on p.patient_id=e.patient_id 
            inner join obs o on o.encounter_id=e.encounter_id 
            where e.voided=0 and o.voided=0 and p.voided=0 and  e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and  e.encounter_datetime<=:endDate and e.location_id=:location 
            group by p.patient_id 
            union 
            Select p.patient_id,min(value_datetime) data_inicio from patient p 
            inner join encounter e on p.patient_id=e.patient_id 
            inner join obs o on e.encounter_id=o.encounter_id 
            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and  o.concept_id=1190 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id 
            union 
            select pg.patient_id,min(date_enrolled) data_inicio from patient p inner join patient_program pg on p.patient_id=pg.patient_id 
            where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location 
            group by pg.patient_id 
            union 
            SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio  FROM 	patient p 
            inner join encounter e on p.patient_id=e.patient_id 
            WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location 
            GROUP BY p.patient_id 
            union 
            Select p.patient_id,min(value_datetime) data_inicio from patient p 
            inner join encounter e on p.patient_id=e.patient_id 
            inner join obs o on e.encounter_id=o.encounter_id 
            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  o.concept_id=23866 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location 
            group by p.patient_id
            ) inicio_real group by patient_id)inicio 
            left join ( 
            select patient_id,max(data_estado) data_estado from ( 
            select maxEstado.patient_id,maxEstado.data_transferidopara data_estado from( 
            select pg.patient_id,max(ps.start_date) data_transferidopara from patient p 
            inner join patient_program pg on p.patient_id=pg.patient_id 
            inner join patient_state ps on pg.patient_program_id=ps.patient_program_id 
            where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=2 and ps.start_date<=:endDate and pg.location_id=:location 
            group by p.patient_id )maxEstado 
            inner join patient_program pg2 on pg2.patient_id=maxEstado.patient_id 
            inner join patient_state ps2 on pg2.patient_program_id=ps2.patient_program_id 
            where pg2.voided=0 and ps2.voided=0 and pg2.program_id=2 and 
            ps2.start_date=maxEstado.data_transferidopara and pg2.location_id=:location and ps2.state in (7,8,10) 
            union 
            select p.patient_id, max(o.obs_datetime) data_estado from patient p  
            inner join encounter e on p.patient_id=e.patient_id 
            inner join obs  o on e.encounter_id=o.encounter_id 
            where e.voided=0 and o.voided=0 and p.voided=0 and  e.encounter_type in (53,6) and o.concept_id in (6272,6273) and o.value_coded in (1706,1366,1709) and o.obs_datetime<=:endDate and e.location_id=:location 
            group by p.patient_id 
            union 
            select person_id as patient_id,death_date as data_estado from person  
            where dead=1 and death_date is not null and death_date<=:endDate) allSaida 
            group by patient_id) saida on inicio.patient_id=saida.patient_id 
            left join ( 
            Select p.patient_id,max(encounter_datetime) data_fila from patient p  
            inner join encounter e on e.patient_id=p.patient_id 
            where p.voided=0 and e.voided=0 and e.encounter_type=18 and e.location_id=:location and date(e.encounter_datetime)<=:endDate 
            group by p.patient_id) max_fila on inicio.patient_id=max_fila.patient_id	
            left join (Select p.patient_id,max(encounter_datetime) data_seguimento from patient p 
            inner join encounter e on e.patient_id=p.patient_id 
            where p.voided=0 and e.voided=0 and e.encounter_type in (6,9) and  e.location_id=:location and e.encounter_datetime<=:endDate group by p.patient_id) max_consulta on inicio.patient_id=max_consulta.patient_id 
            left join ( 
            Select p.patient_id,max(value_datetime) data_recepcao_levantou from patient p 
            inner join encounter e on p.patient_id=e.patient_id 
            inner join obs o on e.encounter_id=o.encounter_id 
            where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  o.concept_id=23866 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location 
            group by p.patient_id) max_recepcao on inicio.patient_id=max_recepcao.patient_id 
            group by inicio.patient_id) inicio_fila_seg 
            left join obs obs_fila on obs_fila.person_id=inicio_fila_seg.patient_id and obs_fila.voided=0 and obs_fila.obs_datetime=inicio_fila_seg.data_fila and obs_fila.concept_id=5096 and obs_fila.location_id=:location 
            left join obs obs_seguimento on obs_seguimento.person_id=inicio_fila_seg.patient_id and obs_seguimento.voided=0 and obs_seguimento.obs_datetime=inicio_fila_seg.data_seguimento and obs_seguimento.concept_id=1410 and obs_seguimento.location_id=:location 
            group by inicio_fila_seg.patient_id 
            ) inicio_fila_seg_prox 
            group by patient_id 

            ) coorte12meses_final 
			
			inner join person pe on pe.person_id=coorte12meses_final.patient_id
            
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

        	inner join 
        	( select 
        		L.patient_id,
        		L.encounter_datetime,
        		if(L.concept_id=1305,
					case L.value_coded 
						when 1306  then 'ALEM DO LIMITE DETECTAVEL' 
						when 1304  then 'MA QUALIDADE DA AMOSTRA' 
						when 23814 then 'INDETECTAVEL'
						when 23905 then 'MENOR QUE 10 COPIAS/ML'
						when 23906 then 'MENOR QUE 20 COPIAS/ML'
						when 23907 then 'MENOR QUE 40 COPIAS/ML'
						when 23908 then 'MENOR QUE 400 COPIAS/ML'
						when 23904 then 'MENOR QUE 839 COPIAS/ML'
						else null end,if(L.value_numeric<0,'INDETECTAVEL',L.value_numeric)) cv

        		from (
	        	
	        	 SELECT pat.patient_id,enc.encounter_datetime encounter_datetime,ob.concept_id,ob.value_coded,ob.value_numeric 
				 FROM 	patient pat JOIN encounter enc ON pat.patient_id=enc.patient_id 
	        	 JOIN 	obs ob ON enc.encounter_id=ob.encounter_id 
	             WHERE 	pat.voided=0 AND enc.voided=0 AND ob.voided=0 AND enc.location_id=:location 
						AND enc.encounter_datetime BETWEEN :startDate AND :startDate + interval 1 month - interval 1 day 
						AND ob.concept_id IN(856,1305) AND enc.encounter_type=6
	             AND pat.patient_id not in 
	             (
					 SELECT p.patient_id 
					 FROM 	patient p 
							JOIN encounter e ON p.patient_id=e.patient_id 
							JOIN obs o ON e.encounter_id=o.encounter_id 
							JOIN 
							(	SELECT 	pat.patient_id AS patient_id, 
										enc.encounter_datetime AS endDate 
								FROM 	patient pat 
								JOIN 	encounter enc ON pat.patient_id=enc.patient_id 
								JOIN 	obs ob ON enc.encounter_id=ob.encounter_id 
								WHERE 	pat.voided=0 AND enc.voided=0 AND ob.voided=0 AND enc.location_id=:location 
										AND enc.encounter_datetime BETWEEN :startDate AND :startDate + interval 1 month - interval 1 day AND ob.concept_id IN(856, 1305) AND enc.encounter_type=6 
							) ed ON p.patient_id=ed.patient_id 
							WHERE 	p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location 
									AND e.encounter_datetime BETWEEN IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) AND (:startDate -interval 1 day) 
					 AND o.concept_id IN (856,1305)
					 AND e.encounter_type=6
	             
	             )

	             union

	             SELECT pat.patient_id,enc.encounter_datetime encounter_datetime,ob.concept_id,ob.value_coded,ob.value_numeric FROM patient pat JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob ON enc.encounter_id=ob.encounter_id 
	             WHERE 
	             pat.voided=0 
	             AND enc.voided=0 
	             AND ob.voided=0 
	             AND enc.location_id=:location 
	             AND enc.encounter_datetime BETWEEN :startDate + interval 1 month AND :startDate + interval 2 month - interval 1 day AND ob.concept_id IN(856,1305) 
	             AND enc.encounter_type=6
	             AND pat.patient_id not in 
	             (
	             SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id 
	             JOIN (SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM patient pat JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob 
	             ON enc.encounter_id=ob.encounter_id 
	             WHERE pat.voided=0 AND enc.voided=0 AND ob.voided=0 AND enc.location_id=:location AND enc.encounter_datetime 
	             BETWEEN :startDate + interval 1 month AND :startDate + interval 2 month - interval 1 day AND ob.concept_id IN(856, 1305) AND enc.encounter_type=6 
	             ) ed 
	             ON p.patient_id=ed.patient_id 
	             WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location 
	             AND e.encounter_datetime BETWEEN IF(MONTH(:startDate + interval 1 month) = 12  && DAY(:startDate + interval 1 month) = 21, :startDate + interval 1 month, CONCAT(YEAR(:startDate + interval 1 month)-1, '-12','-21')) AND ((:startDate + interval 1 month) -interval 1 day) 
	             AND o.concept_id IN (856,1305)
	             AND e.encounter_type=6
	             )

	             union

	             SELECT pat.patient_id,enc.encounter_datetime encounter_datetime,ob.concept_id,ob.value_coded,ob.value_numeric FROM patient pat JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob ON enc.encounter_id=ob.encounter_id 
	             WHERE 
	             pat.voided=0 
	             AND enc.voided=0 
	             AND ob.voided=0 
	             AND enc.location_id=:location 
	             AND enc.encounter_datetime BETWEEN :startDate + interval 2 month AND :endDate AND ob.concept_id IN(856,1305) 
	             AND enc.encounter_type=6
	             AND pat.patient_id not in 
	             (
	             SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id JOIN obs o ON e.encounter_id=o.encounter_id 
	             JOIN (SELECT pat.patient_id AS patient_id, enc.encounter_datetime AS endDate FROM patient pat JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob 
	             ON enc.encounter_id=ob.encounter_id 
	             WHERE pat.voided=0 AND enc.voided=0 AND ob.voided=0 AND enc.location_id=:location AND enc.encounter_datetime 
	             BETWEEN :startDate + interval 2 month AND :endDate AND ob.concept_id IN(856, 1305) AND enc.encounter_type=6 
	             ) ed 
	             ON p.patient_id=ed.patient_id 
	             WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location 
	             AND e.encounter_datetime BETWEEN IF(MONTH(:startDate + interval 2 month) = 12  && DAY(:startDate + interval 2 month) = 21, :startDate + interval 2 month, CONCAT(YEAR(:startDate + interval 2 month)-1, '-12','-21')) AND ((:startDate + interval 2 month) -interval 1 day) 
	             AND o.concept_id IN (856,1305)
	             AND e.encounter_type=6
	             )
        	)L
        )L on L.patient_id=coorte12meses_final.patient_id

        CROSS JOIN (SELECT @cnt := 0) as DEMMY
            

            where (coorte12meses_final.data_estado is null or (coorte12meses_final.data_estado is not null and  coorte12meses_final.data_usar_c>coorte12meses_final.data_estado)) and date_add(coorte12meses_final.data_usar, interval 60 day) >=:endDate