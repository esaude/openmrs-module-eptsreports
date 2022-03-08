/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

public interface TRFINQueries {

  public class QUERY {

    public static final String findPatientsWhoAreTransferredInWithinReportingPeriod =
        "																				"
            + "select patient_id, max(data_estado)  																												"
            + "from(  																									 											"
            + "	select distinct max_estado.patient_id, max_estado.data_estado  																						"
            + "	from (                                          						 		 																	"
            + "	  		select pg.patient_id,																											 		 	"
            + "	  			max(ps.start_date) data_estado																							 			 	"
            + "	  		from	patient p																												 		 	"
            + "	  			inner join patient_program pg on p.patient_id = pg.patient_id																 		 	"
            + "	  			inner join patient_state ps on pg.patient_program_id = ps.patient_program_id												 		 	"
            + "	  		where pg.voided=0 and ps.voided=0 and p.voided=0  and pg.program_id = 2 																				 		 	"
            + "	  			and ps.start_date>=:startDate and  ps.start_date<=:endDate and pg.location_id =:location group by pg.patient_id                         	"
            + "	  	) 																																		 		"
            + "	max_estado                                                                                                                         			 		"
            + "		inner join patient_program pp on pp.patient_id = max_estado.patient_id															 		 		"
            + "		inner join patient_state ps on ps.patient_program_id = pp.patient_program_id and ps.start_date = max_estado.data_estado	         		 		"
            + "	where pp.program_id = 2 and ps.state = 29 and pp.voided = 0 and ps.voided = 0 and pp.location_id = :location 								 		"
            + "	union                                              																						 			"
            + "	select transferido_de.patient_id, transferido_de.data_estado 																						"
            + "	from( 																																				"
            + "			select p.patient_id, max(obsOpenDate.value_datetime) data_estado from patient p  															"
            + "				inner join encounter e on p.patient_id=e.patient_id  																					"
            + "				inner join obs obsTransIn on e.encounter_id= obsTransIn.encounter_id  																	"
            + "				inner join obs obsOpenDate on e.encounter_id= obsOpenDate.encounter_id  																"
            + "				inner join obs obsInTarv on e.encounter_id= obsInTarv.encounter_id  																	"
            + "			 where e.voided=0 and obsTransIn.voided=0 and p.voided=0  and obsOpenDate.voided =0 and obsInTarv.voided =0  								"
            + "				and e.encounter_type=53 and obsTransIn.concept_id=1369 and obsTransIn.value_coded=1065  												"
            + "				and obsOpenDate.concept_id=23891 and obsOpenDate.value_datetime is not null  															"
            + "				and obsInTarv.concept_id=6300 and obsInTarv.value_coded=6276  																		 	"
            + "			 	and obsOpenDate.value_datetime >=:startDate and obsOpenDate.value_datetime <=:endDate and e.location_id=:location group by p.patient_id  "
            + "		) 																																				"
            + "	transferido_de 																																		"
            + "		inner join encounter e on e.patient_id = transferido_de.patient_id 																				"
            + "	where e.voided is false and e.encounter_type in (6, 9, 18) and e.encounter_datetime between :startDate and :endDate and e.location_id =:location 	"
            + "	) 																																		 			"
            + "result group by patient_id 											 																				";

    public static final String findPatientsWIthoutConsultationOrDrugPickUpInReportingPeriod =
        "select patient_id from patient p where p.patient_id not in ( select patient_id from ( "
            + "SELECT e.patient_id, max(e.encounter_datetime)  last_date "
            + "FROM patient p inner join encounter e on p.patient_id=e.patient_id "
            + "WHERE p.voided=0 and e.encounter_type in (6,9,18) AND e.voided=0 and e.encounter_datetime>=:startDate and e.encounter_datetime<=:endDate and e.location_id=:location GROUP BY p.patient_id "
            + "union Select p.patient_id,max(value_datetime) last_date from patient p "
            + " inner join encounter e on p.patient_id=e.patient_id "
            + " inner join obs o on e.encounter_id=o.encounter_id "
            + " where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 and o.value_datetime is not null "
            + " and o.value_datetime>=:startDate and o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id ) last_consulta_levantamento group by patient_id) ";
  }
}
