package org.openmrs.module.eptsreports.reporting.library.queries.data.quality.duplicate;

public interface EC2DuplicateQueries {
  class QUERY {

    public static String findPatiendsWithDuplicatedNID =
        "                                                                                                               			"
            + "select distinct  																																					"
            + "	patient.patient_id, 																													 							"
            + "     location.name locationName,  																																	"
            + "     patient_identifier.patient_identifier_id,   																										 			"
            + "     patient_identifier.identifier,    	 																											 	 			"
            + "     concat(ifnull(person_name.given_name,''),' ',ifnull(person_name.middle_name,''),' ',ifnull(person_name.family_name,'')) as nomeCompleto, 	 	 				"
            + "     person.birthdate, 							 																								 	 				"
            + "     if(person.birthdate_estimated = true, 'Sim','Não') birthdate_estimated,   															 		 	 				"
            + "     person.gender , 																 															 	 				"
            + "    	patient.date_created, 																 														 	 				"
            + "     patient.date_changed, 																														 	 				"
            + "    	patient_identifier.date_created dataCriacaoNID, 																							 	 				"
            + "     if(patient_identifier.preferred = true, 'Sim','Não') nidPreferido, 																			 	 				"
            + "     patient_program.date_enrolled	  																																"
            + "from 																																				 	 			"
            + "(	select patient.patient_id from patient 																												 			"
            + "		join patient_identifier on patient_identifier.patient_id = patient.patient_id 																		 			"
            + "	where patient.voided =0 and patient_identifier.voided =0 and patient_identifier.identifier_type =2 													 				"
            + "		group by patient.patient_id having count(patient_identifier.identifier) >=2																			 			"
            + ") paientID																																		 	 				"
            + "	inner join patient_identifier on patient_identifier.patient_id = paientID.patient_id 																 				"
            + "	inner join patient on patient.patient_id =patient_identifier.patient_id   																							"
            + "	inner join person  on (person.person_id =patient.patient_id and person.voided =0)  																					"
            + "	inner join person_name on (person_name.person_id = patient.patient_id and person_name.voided =0)		 															"
            + "	left join location on (location.location_id =patient_identifier.location_id and location.retired =0) 																"
            + "	left join patient_program on (patient_program.patient_id = patient.patient_id and patient_program.program_id =2 and patient_program.voided =0) 						"
            + "group by patient_identifier.patient_identifier_id order by patient.patient_id, patient_identifier.date_created desc  																						";

    public static String getEc2Total =
        "                                                 								"
            + "select distinct patient.patient_id																    "
            + "from 																								    "
            + "(select patient.patient_id from patient 															    "
            + " 	join patient_identifier on patient_identifier.patient_id = patient.patient_id 						"
            + " 	where patient.voided =0 and patient_identifier.voided =0 and patient_identifier.identifier_type =2  "
            + " 	group by patient.patient_id having count(patient_identifier.identifier) >=2							"
            + " ) paientID																							"
            + " inner join patient_identifier on patient_identifier.patient_id = paientID.patient_id 				"
            + " inner join patient on patient.patient_id =patient_identifier.patient_id 								";
  }
}
