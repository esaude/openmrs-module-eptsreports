select patient_id 
from (
	select *
	from	(
	select cargaQuantitativa.patient_id, cargaQuantitativa.encounter_datetime, 1 ordem
	from (
		select cargaQuantitativa.patient_id, cargaQuantitativa.encounter_datetime
		from	(
				select p.patient_id, max(e.encounter_datetime)  encounter_datetime from patient p
					inner join encounter e on e.patient_id = p.patient_id
					inner join obs o on o.encounter_id = e.encounter_id
				where p.voided = 0  and e.voided = 0 and o.voided = 0 and e.encounter_type  = 13 and o.concept_id = 856 
					and e.location_id =:location and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate
			 		group by p.patient_id
			) cargaQuantitativa
			left join
			(	
				select p.patient_id, max(e.encounter_datetime) encounter_datetime from patient p
					inner join encounter e on e.patient_id = p.patient_id
					inner join obs o on o.encounter_id = e.encounter_id
				where p.voided = 0  and e.voided = 0 and o.voided = 0 and e.encounter_type  = 13 and o.concept_id = 1305 
					and e.location_id =:location and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate
			 		group by p.patient_id
			)	cargaQualitativa on cargaQuantitativa.patient_id = cargaQualitativa.patient_id 
		where cargaQualitativa.patient_id is null  or cargaQuantitativa.encounter_datetime >= cargaQualitativa.encounter_datetime
	) cargaQuantitativa
	inner join encounter e on e.patient_id = cargaQuantitativa.patient_id and e.encounter_datetime =cargaQuantitativa.encounter_datetime
	inner join obs o on o.encounter_id = e.encounter_id
	where e.voided = 0 and o.voided =0 and e.encounter_type = 13 and o.concept_id = 856 
		and e.location_id = :location	and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate and o.value_numeric >= 1000
	union
	select cargaQuantitativa.patient_id, cargaQuantitativa.encounter_datetime, 2 ordem
	from (
		select cargaQuantitativa.patient_id, cargaQuantitativa.encounter_datetime
		from	(
				select p.patient_id, max(e.encounter_datetime)  encounter_datetime from patient p
					inner join encounter e on e.patient_id = p.patient_id
					inner join obs o on o.encounter_id = e.encounter_id
				where p.voided = 0  and e.voided = 0 and o.voided = 0 and e.encounter_type  = 51 and o.concept_id = 856 
					and e.location_id =:location and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate
			 		group by p.patient_id
			) cargaQuantitativa
			left join
			(	
				select p.patient_id, max(e.encounter_datetime) encounter_datetime from patient p
					inner join encounter e on e.patient_id = p.patient_id
					inner join obs o on o.encounter_id = e.encounter_id
				where p.voided = 0  and e.voided = 0 and o.voided = 0 and e.encounter_type  = 51 and o.concept_id = 1305 
					and e.location_id =:location and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate
			 		group by p.patient_id
			)	cargaQualitativa on cargaQuantitativa.patient_id = cargaQualitativa.patient_id 
		where cargaQualitativa.patient_id is null  or cargaQuantitativa.encounter_datetime >= cargaQualitativa.encounter_datetime
	) cargaQuantitativa
	inner join encounter e on e.patient_id = cargaQuantitativa.patient_id and e.encounter_datetime =cargaQuantitativa.encounter_datetime
	inner join obs o on o.encounter_id = e.encounter_id
	where e.voided = 0 and o.voided =0 and e.encounter_type = 51 and o.concept_id = 856 
		and e.location_id = :location	and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate and o.value_numeric >= 1000
	union
	select cargaQuantitativa.patient_id, cargaQuantitativa.encounter_datetime, 3 ordem
	from (
		select cargaQuantitativa.patient_id, cargaQuantitativa.encounter_datetime
		from	(
				select p.patient_id, max(e.encounter_datetime)  encounter_datetime from patient p
					inner join encounter e on e.patient_id = p.patient_id
					inner join obs o on o.encounter_id = e.encounter_id
				where p.voided = 0  and e.voided = 0 and o.voided = 0 and e.encounter_type  = 6 and o.concept_id = 856 
					and e.location_id =:location and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate
			 		group by p.patient_id
			) cargaQuantitativa
			left join
			(	
				select p.patient_id, max(e.encounter_datetime) encounter_datetime from patient p
					inner join encounter e on e.patient_id = p.patient_id
					inner join obs o on o.encounter_id = e.encounter_id
				where p.voided = 0  and e.voided = 0 and o.voided = 0 and e.encounter_type  = 6 and o.concept_id = 1305 
					and e.location_id =:location and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate
			 		group by p.patient_id
			)	cargaQualitativa on cargaQuantitativa.patient_id = cargaQualitativa.patient_id 
		where cargaQualitativa.patient_id is null  or cargaQuantitativa.encounter_datetime >= cargaQualitativa.encounter_datetime
	) cargaQuantitativa
	inner join encounter e on e.patient_id = cargaQuantitativa.patient_id and e.encounter_datetime =cargaQuantitativa.encounter_datetime
	inner join obs o on o.encounter_id = e.encounter_id
	where e.voided = 0 and o.voided =0 and e.encounter_type = 6 and o.concept_id = 856 
		and e.location_id = :location	and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate and o.value_numeric >= 1000
	union
	select cargaQuantitativa.patient_id, cargaQuantitativa.encounter_datetime, 4 ordem
	from (
		select cargaQuantitativa.patient_id, cargaQuantitativa.encounter_datetime
		from	(
				select p.patient_id, max(e.encounter_datetime)  encounter_datetime from patient p
					inner join encounter e on e.patient_id = p.patient_id
					inner join obs o on o.encounter_id = e.encounter_id
				where p.voided = 0  and e.voided = 0 and o.voided = 0 and e.encounter_type  = 53 and o.concept_id = 856 
					and e.location_id =:location and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate
			 		group by p.patient_id
			) cargaQuantitativa
			left join
			(	
				select p.patient_id, max(e.encounter_datetime) encounter_datetime from patient p
					inner join encounter e on e.patient_id = p.patient_id
					inner join obs o on o.encounter_id = e.encounter_id
				where p.voided = 0  and e.voided = 0 and o.voided = 0 and e.encounter_type  = 53 and o.concept_id = 1305 
					and e.location_id =:location and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate
			 		group by p.patient_id
			)	cargaQualitativa on cargaQuantitativa.patient_id = cargaQualitativa.patient_id 
		where cargaQualitativa.patient_id is null  or cargaQuantitativa.encounter_datetime >= cargaQualitativa.encounter_datetime
	) cargaQuantitativa
	inner join encounter e on e.patient_id = cargaQuantitativa.patient_id and e.encounter_datetime =cargaQuantitativa.encounter_datetime
	inner join obs o on o.encounter_id = e.encounter_id
	where e.voided = 0 and o.voided =0 and e.encounter_type = 53 and o.concept_id = 856 
		and e.location_id = :location	and e.encounter_datetime between date_sub(:endDate, interval 12 month) and :endDate and o.value_numeric >= 1000
) result order by patient_id, ordem
 ) finalCV group by patient_id