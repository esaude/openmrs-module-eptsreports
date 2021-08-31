package org.openmrs.module.eptsreports.reporting.library.queries;

public interface TxTbPrevQueriesInterface {

  class QUERY {

    public enum DisaggregationTypes {
      NEWLY_ENROLLED,
      PREVIOUSLY_ENROLLED;
    }

    //		public static final String
    // findPatientsWhoStartedTbPrevPreventiveTreatmentDuringPreviousReportingPeriod = "
    //			"
    //				+ "                                 select patient_id from
    //                       						         "
    //				+ "                                 (																					        												 "
    //				+ "                                 	select inicio_3HP.patient_id,
    //	min(inicio_3HP.data_inicio_tpi) data_inicio_tpi from (										        		 "
    //				+ "                                 		select inicio.patient_id,inicio.data_inicio_tpi from
    // 																					 "
    //				+ "                                 		(	select p.patient_id,min(e.encounter_datetime)
    // data_inicio_tpi from patient p														 "
    //				+ "                                 				inner join encounter e on
    // p.patient_id=e.patient_id																				 "
    //				+ "                                 				inner join obs o on o.encounter_id=e.encounter_id
    //																			 "
    //				+ "                                 			where e.voided=0 and p.voided=0 and
    // e.encounter_datetime between (:startDate - interval 6 month) and (:endDate - interval 6
    // month) 	 "
    //				+ "                                 				and o.voided=0 and o.concept_id=1719 and
    // o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location			 "
    //				+ "                                 			group by p.patient_id																												 "
    //				+ "                                 		) inicio 																																 "
    //				+ "                                 		left join 																																 "
    //				+ "                                 		(	select p.patient_id,e.encounter_datetime
    // data_inicio_tpi from patient p																 "
    //				+ "                                 				inner join encounter e on
    // p.patient_id=e.patient_id																				 "
    //				+ "                                 				inner join obs o on o.encounter_id=e.encounter_id
    //																			 "
    //				+ "                                 			where e.voided=0 and p.voided=0 and
    // e.encounter_datetime between (:startDate - INTERVAL 10 MONTH) and (:endDate - interval 6
    // month)  "
    //				+ "                                 				and o.voided=0 and o.concept_id=1719 and
    // o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location			 "
    //				+ "                                 		    union 																																 "
    //				+ "                                 		    select p.patient_id,e.encounter_datetime
    // data_inicio_tpi from patient p														         "
    //				+ "										inner join encounter e on p.patient_id=e.patient_id																				 		 "
    //				+ "										inner join obs o on o.encounter_id=e.encounter_id
    // "
    //				+ "									where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate -
    // interval 10 month) and (:endDate - interval 6 month)	         "
    //				+ "										and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and
    // e.encounter_type=60 and  e.location_id=:location	         "
    //				+ "                                 		            																															 "
    //				+ "                                 		) inicioAnterior on
    // inicio.patient_id=inicioAnterior.patient_id  																		 "
    //				+ "                                 			and inicioAnterior.data_inicio_tpi between
    // (inicio.data_inicio_tpi - INTERVAL 4 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) "
    //				+ "                                 		where inicioAnterior.patient_id is null
    //													 "
    //				+ "								union 																																			 "
    //				+ "								 select p.patient_id,min(e.encounter_datetime) data_inicio_tpi from patient p
    //												 			 "
    //				+ "									inner join encounter e on p.patient_id=e.patient_id																				 			 "
    //				+ "									inner join obs o on o.encounter_id=e.encounter_id		 																					 "
    //				+ "									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id
    //												 "
    //				+ "								where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate -
    // interval 6 month) and (:endDate - interval 6 month)	 			 "
    //				+ "									and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and
    // e.encounter_type=60 and  e.location_id=:location	  			 "
    //				+ "									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and
    // seguimentoTPT.value_coded in (1256,1705) 								 "
    //				+ "								group by p.patient_id	                            																							 "
    //				+ "                                 		union																																	 "
    //				+ "                                 		select inicio.patient_id,inicio.data_inicio_tpi from
    //																					 "
    //				+ "                                 		(	select p.patient_id,min(e.encounter_datetime)
    // data_inicio_tpi from patient p														 "
    //				+ "                                 				inner join encounter e on
    // p.patient_id=e.patient_id																				 "
    //				+ "                                 				inner join obs o on o.encounter_id=e.encounter_id
    // 																			 "
    //				+ "                                 				inner join obs seguimentoTPT on
    // seguimentoTPT.encounter_id=e.encounter_id														 "
    //				+ "                                 			where e.voided=0 and p.voided=0 and
    // e.encounter_datetime between (:startDate - interval 6 month) and (:endDate - interval 6
    // month)	 "
    //				+ "                                 				and o.voided=0 and o.concept_id=23985 and
    // o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location	 "
    //				+ "                                 				and seguimentoTPT.voided =0 and
    // seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257,126) 					 "
    //				+ "                                 			group by p.patient_id
    // "
    //				+ "									union 																																		 "
    //				+ "									select p.patient_id,min(e.encounter_datetime) data_inicio_tpi from patient p
    //												 		 "
    //				+ "										inner join encounter e on p.patient_id=e.patient_id																				 		 "
    //				+ "										inner join obs o on o.encounter_id=e.encounter_id	 																					 "
    //				+ "										left join obs seguimentoTPT on (e.encounter_id = seguimentoTPT.encounter_id
    // and seguimentoTPT.concept_id =23987)						 "
    //				+ "									where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate -
    // interval 6 month) and (:endDate - interval 6 month)	         "
    //				+ "										and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and
    // e.encounter_type=60 and  e.location_id=:location	 	     "
    //				+ "										  and  (seguimentoTPT.obs_id is null or (seguimentoTPT.obs_id is not null and
    // seguimentoTPT.value_coded is null)) 						 "
    //				+ "									group by p.patient_id	 																													 "
    //				+ "						   		) inicio 																																 		 "
    //				+ "                                 		left join 																																 "
    //				+ "                                 		(	select p.patient_id,e.encounter_datetime
    // data_inicio_tpi from patient p																 "
    //				+ "                                 				inner join encounter e on
    // p.patient_id=e.patient_id																				 "
    //				+ "                                 				inner join obs o on o.encounter_id=e.encounter_id
    //																			 "
    //				+ "                                 			where  e.voided=0 and p.voided=0 and
    // e.encounter_datetime between (:startDate - INTERVAL 10 MONTH) and (:endDate - interval 6
    // month) "
    //				+ "                                 				 and o.voided=0 and o.concept_id=23985 and
    // o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location   "
    //				+ "                                 	          union 																															 "
    //				+ "                                 	          select p.patient_id,e.encounter_datetime
    // data_inicio_tpi from patient p														     "
    //				+ "                                 				inner join encounter e on
    // p.patient_id=e.patient_id																				 "
    //				+ "                                 				inner join obs o on o.encounter_id=e.encounter_id
    //																			 "
    //				+ "                                 			where e.voided=0 and p.voided=0 and
    // e.encounter_datetime between (:startDate - INTERVAL 10 MONTH) and (:endDate - interval 6
    // month)  "
    //				+ "                                 				and o.voided=0 and o.concept_id=1719 and
    // o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location			 "
    //				+ "                                 		) inicioAnterior on
    // inicioAnterior.patient_id=inicio.patient_id  																		 "
    //				+ "                                 			and inicioAnterior.data_inicio_tpi between
    // (inicio.data_inicio_tpi - INTERVAL 4 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) "
    //				+ "                                 		where inicioAnterior.patient_id is null
    //												 "
    //				+ "                                 	) inicio_3HP group by inicio_3HP.patient_id
    //															 "
    //				+ "                                 	union																																		 "
    //				+ "                                 	select inicio_INH.patient_id,
    //	min(inicio_INH.data_inicio_tpi) data_inicio_tpi from (														 "
    //				+ "                                 		select inicio.patient_id,inicio.data_inicio_tpi from
    //																					 "
    //				+ "                                 		(	select p.patient_id,min(e.encounter_datetime)
    // data_inicio_tpi	from	patient p													 "
    //				+ "                                 				inner join encounter e on
    // p.patient_id=e.patient_id																				 "
    //				+ "                                 				inner join obs o on o.encounter_id=e.encounter_id
    // 																			 "
    //				+ "                                 				inner join obs seguimentoTPT on
    // seguimentoTPT.encounter_id=e.encounter_id														 "
    //				+ "                                 			where e.voided=0 and p.voided=0 and
    // e.encounter_datetime between (:startDate - interval 6 month) and (:endDate - interval 6
    // month)   "
    //				+ "                                 				and seguimentoTPT.voided =0 and
    // seguimentoTPT.concept_id =23987 and seguimentoTPT.value_coded in (1257)	 						 "
    //				+ "                                 				and o.voided=0 and o.concept_id=23985 and
    // o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location		 "
    //				+ "                                 			group by p.patient_id
    // "
    //				+ "									union  																																		 "
    //				+ "									select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	from	patient p
    //											         "
    //				+ "                                 				inner join encounter e on
    // p.patient_id=e.patient_id																				 "
    //				+ "                                 				inner join obs o on o.encounter_id=e.encounter_id
    //																			 "
    //				+ "                                 				left join obs seguimentoTPT on
    // (seguimentoTPT.encounter_id=e.encounter_id and seguimentoTPT.concept_id =23987)					 "
    //				+ "                                 			where e.voided=0 and p.voided=0 and
    // e.encounter_datetime between (:startDate - interval 6 month) and (:endDate - interval 6
    // month)   "
    //				+ "                                 				and o.voided=0 and o.concept_id=23985 and
    // o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      "
    //				+ "                                 				 and  (seguimentoTPT.obs_id is null or
    // (seguimentoTPT.obs_id is not null and seguimentoTPT.value_coded is null))		         "
    //				+ "                                 			group by p.patient_id
    //                                                                          "
    //				+ "                                 		)inicio																																	 "
    //				+ "                                 		left join 																																 "
    //				+ "                                 		(	select p.patient_id,e.encounter_datetime
    // data_inicio_tpi from patient p																 "
    //				+ "                                 				inner join encounter e on
    // p.patient_id=e.patient_id																				 "
    //				+ "                                 				inner join obs o on o.encounter_id=e.encounter_id
    //																			 "
    //				+ "                                 			where e.voided=0 and p.voided=0 and
    // e.encounter_datetime between (:startDate - INTERVAL 13 MONTH) and (:endDate - interval 6
    // month)  "
    //				+ "                                 				and o.voided=0 and o.concept_id=23985 and
    // o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location      "
    //				+ "                                 			union
    //                                                                                  "
    //				+ "                                 			select  p.patient_id, o.value_datetime
    // data_inicio_tpi from patient p
    // "
    //				+ "										inner join encounter e on p.patient_id=e.patient_id
    //                                                            "
    //				+ "										inner join obs o on o.encounter_id=e.encounter_id
    //                                                            "
    //				+ "									where   e.voided=0 and p.voided=0 and o.value_datetime between (:startDate -
    // interval 13 month) and (:endDate - interval 6 month)            "
    //				+ "										and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and
    // e.location_id=:location  		                                 "
    //				+ "                                 		) inicioAnterior on
    // inicioAnterior.patient_id=inicio.patient_id  																		 "
    //				+ "                                 			and inicioAnterior.data_inicio_tpi between
    // (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day) "
    //				+ "                                 		where inicioAnterior.patient_id is null
    //												 "
    //				+ "                                 		union
    //                                                                                     "
    //				+ "                                 		select p.patient_id,min(e.encounter_datetime)
    // data_inicio_tpi from patient p														     "
    //				+ "									inner join encounter e on p.patient_id=e.patient_id
    //      "
    //				+ "									inner join obs o on o.encounter_id=e.encounter_id
    //                                                          "
    //				+ "									inner join obs seguimentoTPT on seguimentoTPT.encounter_id=e.encounter_id
    //												 "
    //				+ "								where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate -
    // interval 6 month) and (:endDate - interval 6 month)	             "
    //				+ "									and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and
    // e.encounter_type=60 and  e.location_id=:location	                 "
    //				+ "									and seguimentoTPT.voided =0 and seguimentoTPT.concept_id =23987 and
    // seguimentoTPT.value_coded in (1256,1705)                                 "
    //				+ "								group by p.patient_id
    //                                                                  "
    //				+ "                                 		union																																	 "
    //				+ "                                 		select p.patient_id,min(o.value_datetime)
    // data_inicio_tpi from patient p																 "
    //				+ "                                 				inner join encounter e on
    // p.patient_id=e.patient_id																				 "
    //				+ "                                 				inner join obs o on o.encounter_id=e.encounter_id
    //																			 "
    //				+ "                                 			where e.voided=0 and p.voided=0 and
    // o.value_datetime between (:startDate - interval 6 month) and (:endDate - interval 6 month)
    //    "
    //				+ "                                 				and o.voided=0 and o.concept_id=6128 and
    // e.encounter_type in (6,9,53) and e.location_id=:location								 "
    //				+ "                                 			group by p.patient_id																												 "
    //				+ "                                 		union 																																	 "
    //				+ "                                 		select p.patient_id,min(e.encounter_datetime)
    // data_inicio_tpi from patient p															 "
    //				+ "                                 				inner join encounter e on
    // p.patient_id=e.patient_id																				 "
    //				+ "                                 				inner join obs o on o.encounter_id=e.encounter_id
    //																			 "
    //				+ "                                 			where e.voided=0 and p.voided=0 and
    // e.encounter_datetime between (:startDate - interval 6 month) and (:endDate - interval 6
    // month)   "
    //				+ "                                 				and o.voided=0 and o.concept_id=6122 and
    // o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location			 "
    //				+ "                                 			group by p.patient_id																												 "
    //				+ "                                 	) inicio_INH group by inicio_INH.patient_id
    //															 "
    //				+ "                                ) inicio_TPT
    //                                                                                       ";

    public static final String
        findPatientsWhoStartedArtAndTbPrevPreventiveTreatmentInDisaggregation(
            DisaggregationTypes disaggregationType) {

      String query =
          "                                                                                          																"
              + "select inicio_TPT.patient_id from 																													"
              + "(																																					"
              + "	select inicio_3HP.patient_id,	min(inicio_3HP.data_inicio_tpi) data_inicio_tpi from (															"
              + "		select inicio.patient_id,inicio.data_inicio_tpi from 																						"
              + "		(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi from patient p															"
              + "				inner join encounter e on p.patient_id=e.patient_id																					"
              + "				inner join obs o on o.encounter_id=e.encounter_id																					"
              + "			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - interval 6 month) and (:endDate - interval 6 month) 		"
              + "				and o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location				"
              + "			group by p.patient_id																													"
              + "		) inicio 																																	"
              + "		left join 																																	"
              + "		(	select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p																	"
              + "				inner join encounter e on p.patient_id=e.patient_id																					"
              + "				inner join obs o on o.encounter_id=e.encounter_id																					"
              + "			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 10 MONTH) and (:endDate - interval 6 month) 	"
              + "				and o.voided=0 and o.concept_id=1719 and o.value_coded=23954 and e.encounter_type in (6,9) and  e.location_id=:location				"
              + "		) inicioAnterior on inicio.patient_id=inicioAnterior.patient_id  																			"
              + "			and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 4 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day)	"
              + "		where inicioAnterior.patient_id is null																										"
              + "		union																																		"
              + "		select inicio.patient_id,inicio.data_inicio_tpi from																						"
              + "		(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi from patient p															"
              + "				inner join encounter e on p.patient_id=e.patient_id																					"
              + "				inner join obs o on o.encounter_id=e.encounter_id																					"
              + "			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - interval 6 month) and (:endDate - interval 6 month)		"
              + "				and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location		"
              + "			group by p.patient_id																													"
              + "		) inicio 																																	"
              + "		left join 																																	"
              + "		(	select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p																	"
              + "				inner join encounter e on p.patient_id=e.patient_id																					"
              + "				inner join obs o on o.encounter_id=e.encounter_id																					"
              + "			where  e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 10 MONTH) and (:endDate - interval 6 month)	"
              + "				 and o.voided=0 and o.concept_id=23985 and o.value_coded in (23954,23984) and e.encounter_type=60 and  e.location_id=:location		"
              + "		) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id  																			"
              + "			and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 4 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day)	"
              + "		where inicioAnterior.patient_id is null																										"
              + "	) inicio_3HP group by inicio_3HP.patient_id																										"
              + "	union																																			"
              + "	select inicio_INH.patient_id,	min(inicio_INH.data_inicio_tpi) data_inicio_tpi from (															"
              + "		select inicio.patient_id,inicio.data_inicio_tpi from																						"
              + "		(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi	from	patient p														"
              + "				inner join encounter e on p.patient_id=e.patient_id																					"
              + "				inner join obs o on o.encounter_id=e.encounter_id																					"
              + "			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - interval 6 month) and (:endDate - interval 6 month)		"
              + "				and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location			"
              + "			group by p.patient_id																													"
              + "		)inicio																																		"
              + "		left join 																																	"
              + "		(	select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p																	"
              + "				inner join encounter e on p.patient_id=e.patient_id																					"
              + "				inner join obs o on o.encounter_id=e.encounter_id																					"
              + "			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - INTERVAL 13 MONTH) and (:endDate - interval 6 month) 	"
              + "				and o.voided=0 and o.concept_id=23985 and o.value_coded in (656,23982) and e.encounter_type=60 and  e.location_id=:location			"
              + "		) inicioAnterior on inicioAnterior.patient_id=inicio.patient_id  																			"
              + "			and inicioAnterior.data_inicio_tpi between (inicio.data_inicio_tpi - INTERVAL 7 MONTH) and (inicio.data_inicio_tpi - INTERVAL 1 day)	"
              + "		where inicioAnterior.patient_id is null																										"
              + "		union																																		"
              + "		select inicio.patient_id,inicio.data_inicio_tpi from																						"
              + "		(	select p.patient_id,min(o.value_datetime) data_inicio_tpi from patient p																"
              + "				inner join encounter e on p.patient_id=e.patient_id																					"
              + "				inner join obs o on o.encounter_id=e.encounter_id																					"
              + "			where e.voided=0 and p.voided=0 and o.value_datetime between (:startDate - interval 6 month) and (:endDate - interval 6 month) 			"
              + "				and o.voided=0 and o.concept_id=6128 and e.encounter_type in (6,9,53) and e.location_id=:location									"
              + "			group by p.patient_id																													"
              + "		) inicio																																	"
              + "		union 																																		"
              + "		select inicio.patient_id,inicio.data_inicio_tpi from																						"
              + "		(	select p.patient_id,min(e.encounter_datetime) data_inicio_tpi from patient p															"
              + "				inner join encounter e on p.patient_id=e.patient_id																					"
              + "				inner join obs o on o.encounter_id=e.encounter_id																					"
              + "			where e.voided=0 and p.voided=0 and e.encounter_datetime between (:startDate - interval 6 month) and (:endDate - interval 6 month) 		"
              + "				and o.voided=0 and o.concept_id=6122 and o.value_coded=1256 and e.encounter_type in (6,9) and  e.location_id=:location 				"
              + "			group by p.patient_id																													"
              + "		) inicio 																																	"
              + "	) inicio_INH group by inicio_INH.patient_id																										"
              + ") inicio_TPT												 																						"
              + "inner join ( 																																	"
              + "	Select inicio_real.patient_id,min(data_inicio) data_inicio from( 																				"
              + "		select p.patient_id,min(e.encounter_datetime) data_inicio from  patient p 																	"
              + "			inner join encounter e on p.patient_id=e.patient_id 																					"
              + "			inner join obs o on o.encounter_id=e.encounter_id 																						"
              + "		where e.voided=0 and o.voided=0 and p.voided=0 and  e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 				"
              + "			and e.encounter_datetime<=:endDate and e.location_id=:location group by p.patient_id 													"
              + "		union 																																		"
              + "		select p.patient_id,min(value_datetime) data_inicio from  patient p 																		"
              + "			inner join encounter e on p.patient_id=e.patient_id 																					"
              + "			inner join obs o on e.encounter_id=o.encounter_id 																						"
              + "		where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and  o.concept_id=1190 									"
              + "			and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id 						"
              + "		union 																																		"
              + "		select pg.patient_id,min(date_enrolled) data_inicio from patient p 																			"
              + "			inner join patient_program pg on p.patient_id=pg.patient_id 																			"
              + "		where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location group by pg.patient_id 				"
              + "		union 																																		"
              + "		SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio FROM patient p 																"
              + "			inner join encounter e on p.patient_id=e.patient_id WHERE 																				"
              + "				p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location GROUP BY  p.patient_id "
              + "		union 																																		"
              + "		select p.patient_id,min(value_datetime) data_inicio from patient p 																			"
              + "			inner join encounter e on p.patient_id=e.patient_id 																					"
              + "			inner join obs o on e.encounter_id=o.encounter_id 																						"
              + "		where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 																		"
              + "			and o.concept_id=23866 and o.value_datetime is not null and  o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id "
              + "	) inicio_real group by patient_id 																												"
              + " ) inicioTarv on inicioTarv.patient_id = inicio_TPT.patient_id 																					";

      switch (disaggregationType) {
        case NEWLY_ENROLLED:
          query =
              query
                  + "and inicio_TPT.data_inicio_tpi <= DATE_ADD(inicioTarv.data_inicio, INTERVAL 6 MONTH)";
          break;

        case PREVIOUSLY_ENROLLED:
          query =
              query
                  + " and inicio_TPT.data_inicio_tpi > DATE_ADD(inicioTarv.data_inicio, INTERVAL 6 MONTH)";
          break;
      }
      return query;
    }

    public static final String findPatientsTransferredOut =
        "																														"
            + "select transferidopara.patient_id 																																	"
            + "from ( 																																								"
            + "		select patient_id,max(data_transferidopara) data_transferidopara 																								"
            + "		from ( 																																							"
            + "				select pg.patient_id,max(ps.start_date) data_transferidopara from patient p 																			"
            + "	          		inner join patient_program pg on p.patient_id=pg.patient_id 																						"
            + "	            		inner join patient_state ps on pg.patient_program_id=ps.patient_program_id 																		"
            + "	        		where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=2 and ps.state=7 and ps.end_date is null 										"
            + "	        			and ps.start_date>=:startDate - interval 6 month and  ps.start_date<=:endDate and location_id=:location group by p.patient_id 					"
            + "	         		union 																																				"
            + "	          	select p.patient_id,max(e.encounter_datetime) data_transferidopara from patient p 																		"
            + "	           		inner join encounter e on p.patient_id=e.patient_id 																								"
            + "	            		inner join obs o on o.encounter_id=e.encounter_id 																								"
            + "	            	where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=21 and o.concept_id=2016 and o.value_coded in (1706,23863) 						"
            + "	            		and e.encounter_datetime >=:startDate - interval 6 month and e.encounter_datetime<=:endDate and e.location_id=:location group by p.patient_id 	"
            + "	          	union 																																					"
            + "	          	select p.patient_id,max(o.obs_datetime) data_transferidopara from patient p 																			"
            + "	          		inner join encounter e on p.patient_id=e.patient_id 																								"
            + "	            		inner join obs o on o.encounter_id=e.encounter_id 																								"
            + "	          	where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=6272 and o.value_coded=1706 									"
            + "	          		and o.obs_datetime>=:startDate - interval 6 month  and o.obs_datetime<=:endDate and  e.location_id=:location group by p.patient_id 					"
            + "	          	union 																																					"
            + "	          	select p.patient_id,max(e.encounter_datetime) data_transferidopara from  patient p 																		"
            + "	          		inner join encounter e on p.patient_id=e.patient_id 																								"
            + "	            		inner join obs o on o.encounter_id=e.encounter_id 																								"
            + "	          	where p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=6273 and o.value_coded=1706 and e.encounter_type=6 										"
            + "	          		and e.encounter_datetime>=:startDate - interval 6 month and e.encounter_datetime<=:endDate and  e.location_id=:location group by p.patient_id 		"
            + "       	) transferido group by patient_id 																															"
            + "     ) transferidopara 																																				"
            + "     inner join 																																						"
            + "     ( 																																								"
            + "     	select max_consulta.patient_id, max(max_consulta.encounter_datetime) encounter_datetime from 																"
            + "     	( 																																							"
            + "			select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p 																			"
            + "				inner join encounter e on e.patient_id=p.patient_id 																									"
            + "			where p.voided=0 and e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location and e.encounter_type in (18,6,9) group by p.patient_id 		"
            + "			union 																																						"
            + "			select p.patient_id,max(value_datetime) encounter_datetime from patient p 																					"
            + "				inner join encounter e on p.patient_id=e.patient_id 																									"
            + "				inner join obs o on e.encounter_id=o.encounter_id 																										"
            + "			where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 and o.value_datetime is not null 								"
            + "				and o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id 																		"
            + "	 	) max_consulta group by max_consulta.patient_id 																												"
            + "	) consultaOuARV on transferidopara.patient_id=consultaOuARV.patient_id 																								"
            + "    where consultaOuARV.encounter_datetime <= transferidopara.data_transferidopara																					";
  }
}
