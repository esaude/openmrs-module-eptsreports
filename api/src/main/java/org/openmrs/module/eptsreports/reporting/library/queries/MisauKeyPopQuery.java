package org.openmrs.module.eptsreports.reporting.library.queries;

public class MisauKeyPopQuery {

  public static final String findPatientsWhoAreKeyPop(final KeyPopType keyPopType) {

    String query =
        "select patient_id  																																			"
            + "from (  																																					"
            + "	select * from	( 																																		"
            + "		select * from	(  																																	"
            + "  			select maxkp.patient_id, o.value_coded,o.obs_datetime,1 ordemSource,if(o.value_coded=20454,2,4) ordemKp from	( 							"
            + "  				select p.patient_id,max(e.encounter_datetime) maxkpdate  																				"
            + "  				from patient p   																														"
            + "  					inner join encounter e on p.patient_id=e.patient_id  																				"
            + "					inner join obs o on e.encounter_id=o.encounter_id 																						"
            + "				where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and  e.encounter_type=6 and e.encounter_datetime<=:endDate and  		"
            + "				e.location_id= :location  																													"
            + "					group by p.patient_id  																													"
            + "				)  																																			"
            + "			maxkp  																																			"
            + "				inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime  											"
            + "			  	inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime  														"
            + "			where o.concept_id=23703 and o.voided=0 and e.encounter_type=6 and e.voided=0 and e.location_id= :location and o.value_coded in (20454,20426)  	"
            + "			union  																																			"
            + "			select maxkp.patient_id, o.value_coded,o.obs_datetime,1 ordemSource,3 ordemKp from (  															"
            + "				select p.patient_id,max(e.encounter_datetime) maxkpdate from patient p  																	"
            + "			  		inner join encounter e on p.patient_id=e.patient_id  																					"
            + "			  		inner join obs o on e.encounter_id=o.encounter_id 																						"
            + "			  	where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and  e.encounter_type=6 and e.encounter_datetime<=:endDate and  		"
            + "                      e.location_id= :location  																											"
            + "  					group by p.patient_id  																												"
            + "  				)  																																		"
            + "  			maxkp  																																		"
            + "				inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime  											"
            + "			  	inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime  														"
            + "			  	inner join person pe on pe.person_id=maxkp.patient_id  																						"
            + "			where o.concept_id=23703 and o.voided=0 and e.encounter_type=6 and e.voided=0 and e.location_id= :location and pe.voided=0  					"
            + "				and ((pe.gender='F' and o.value_coded=1901) or  (pe.gender='M' and o.value_coded=1377))  													"
            + "  			union 																			 															"
            + "  			select maxkp.patient_id, o.value_coded,o.obs_datetime,1 ordemSource,5 ordemKp from (  														"
            + "  				select p.patient_id,max(e.encounter_datetime) maxkpdate from patient p  																"
            + "  					inner join encounter e on p.patient_id=e.patient_id 			 																	"
            + " 	 				inner join obs o on e.encounter_id=o.encounter_id 																					"
            + "  				where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and  e.encounter_type=6 and e.encounter_datetime<=:endDate and  	"
            + "    					e.location_id= :location  																											"
            + "  					group by p.patient_id  																												"
            + "  				)  																																		"
            + "  			maxkp  																																		"
            + "				inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime  											"
            + "			  	inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime  														"
            + "			  	inner join person pe on pe.person_id=maxkp.patient_id  																						"
            + "			where o.concept_id=23703 and o.voided=0 and e.encounter_type=6 and e.voided=0 and e.location_id= :location and pe.voided=0 and o.value_coded=5622 "
            + "			union 																			 																"
            + "  			select maxkp.patient_id, o.value_coded,o.obs_datetime,2 ordemSource,5 ordemKp from (  														"
            + "  				select p.patient_id,max(e.encounter_datetime) maxkpdate from patient p  																"
            + "  					inner join encounter e on p.patient_id=e.patient_id 			 																	"
            + " 	 				inner join obs o on e.encounter_id=o.encounter_id 																					"
            + " 				where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and  e.encounter_type=35 and e.encounter_datetime<=:endDate and  	"
            + " 					e.location_id= :location  																											"
            + "  					group by p.patient_id  																												"
            + "  				)  																																		"
            + "  			maxkp  																																		"
            + "				inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime  											"
            + "			  	inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime  														"
            + "			  	inner join person pe on pe.person_id=maxkp.patient_id  																						"
            + "			where o.concept_id=23703 and o.voided=0 and e.encounter_type=35 and e.voided=0 and e.location_id= :location and pe.voided=0 and o.value_coded=23885 "
            + "  			union 										 																								"
            + "  			select maxkp.patient_id, o.value_coded,o.obs_datetime,2 ordemSource,if(o.value_coded=20454,2,4) ordemKp from  (  							"
            + "  				select p.patient_id,max(e.encounter_datetime) maxkpdate from patient p   																"
            + "  					inner join encounter e on p.patient_id=e.patient_id  																				"
            + "  					inner join obs o on e.encounter_id=o.encounter_id 																					"
            + "  				where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and  e.encounter_type in (46,35) and e.encounter_datetime<=:endDate "
            + "							and e.location_id= :location  																									"
            + "  					group by p.patient_id  																												"
            + "  				)  																																		"
            + "  			maxkp  																																		"
            + "  				inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime  										"
            + "  				inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime  													"
            + "  			where o.concept_id=23703 and o.voided=0 and e.encounter_type in (46,35) and e.voided=0 and e.location_id= :location 						"
            + " 					and o.value_coded in (20454,20426)  																								"
            + "  			union  																																		"
            + "  			select maxkp.patient_id, o.value_coded,o.obs_datetime,2 ordemSource,3 ordemKp from (  														"
            + "  				select p.patient_id,max(e.encounter_datetime) maxkpdate from patient p   																"
            + "  					inner join encounter e on p.patient_id=e.patient_id  																				"
            + "  					inner join obs o on e.encounter_id=o.encounter_id 																					"
            + "  				where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and  e.encounter_type in (46,35) 									"
            + "						and e.encounter_datetime<=:endDate and e.location_id= :location  																	"
            + "  					group by p.patient_id  																												"
            + "  				) 																																		"
            + "  			maxkp  																																		"
            + "				inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime  											"
            + "  				inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime  													"
            + " 	 			inner join person pe on pe.person_id=maxkp.patient_id  																					"
            + "  			where o.concept_id=23703 and o.voided=0 and e.encounter_type in (46,35) and e.voided=0 and e.location_id= :location and pe.voided=0  		"
            + "  				and ((pe.gender='F' and o.value_coded=1901) or  (pe.gender='M' and o.value_coded=1377))  												"
            + "  			union   																																	"
            + "  			Select pa.person_id,  																														"
            + "  				case upper(pa.value)  																													"
            + "  					when 'HSM' then 1377  																												"
            + "  					when 'HSH' then 1377  																												"
            + "  					when 'MSM' then 1377  																												"
            + "  					when 'MTS' then 1901  																												"
            + "  					when 'CSW' then 1901  																												"
            + "  					when 'TS' then 1901  																												"
            + "  					when 'PRISONER' then 20426  																										"
            + "  					when 'REC' then 20426  																												"
            + "  					when 'RC' then 20426  																												"
            + " 	 				when 'PID' then 20454 																												"
            + "   				else null end as estado,  																												"
            + "  				date(pa.date_created),  																												"
            + "  				3 as ordemSource,  																														"
            + "  				5 as ordemKp from person_attribute pa  																									"
            + "  				inner join person_attribute_type pat on pa.person_attribute_type_id=pat.person_attribute_type_id  										"
            + "  			where pat.uuid='c89c90eb-5b03-4899-ab9f-06fecd123511' and pa.value is not null and pa.value<>''  											"
            + "					and pa.voided=0 and date(pa.date_created)<=:endDate  																					"
            + "  		)  																																				"
            + "  	allkpsource  																																		"
            + "  		order by patient_id, obs_datetime desc, ordemSource,ordemKp  																					"
            + "  	)  																																					"
            + " allkpsorcetakefirst group by patient_id 																												"
            + " ) finalkptable 																																			";

    switch (keyPopType) {
      case HOMOSEXUAL:
        query = query + "where value_coded=1377 ";
        break;

      case PRISIONER:
        query = query + "where value_coded=20426 ";
        break;

      case SEXWORKER:
        query = query + "where value_coded=1901 ";
        break;

      case DRUGUSER:
        query = query + "where value_coded=20454 ";
        break;
    }

    return query;
  }
}
