/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.queries;

public class TxNewQueries {
	
	/**
	 * TxNew Queries Looks for patients enrolled in ART program (program 2=SERVICO TARV - TRATAMENTO)
	 * before or on end date (Parameters: getARTProgram) UNION Looks for patients registered as START
	 * DRUGS (answer to question 1255 = ARV PLAN is 1256 = START DRUGS) in the first drug pickup
	 * (encounter type 18=S.TARV: FARMACIA) or follow up consultation for adults and children (encounter
	 * types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA SEGUIMENTO) before or on end date
	 * (Parameters: getARVPharmaciaEncounterType, getAdultoSeguimentoEncounterType,
	 * getARVPediatriaSeguimentoEncounterType, getARVPlanConcept, getstartDrugsConcept) UNION Looks for
	 * with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in drug pickup (encounter type
	 * 18=S.TARV: FARMACIA) or follow up consultation for adults and children (encounter types 6=S.TARV:
	 * ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end
	 * date (Parameters: ) UNION Looks for patients with first drug pickup (encounter type 18=S.TARV:
	 * FARMACIA) before or on end date
	 * 
	 * @param parameters
	 * @return TxNew Union Query
	 */
	public static String getTxNewUnionQueries(int... parameters) {
		return "select patient_id from (select patient_id, min(data_inicio) data_inicio from "
		        + "(select p.patient_id, date_enrolled data_inicio from patient p "
		        + "inner join patient_program pg on p.patient_id=pg.patient_id "
		        + "where pg.voided=0 and p.voided=0 and pg.program_id= " + parameters[0]
		        + " and pg.date_enrolled <= :onOrBefore and pg.location_id=:location" + " UNION "
		        + "select p.patient_id, min(e.encounter_datetime) data_inicio from patient p "
		        + "inner join encounter e on p.patient_id=e.patient_id " + "inner join obs o on o.encounter_id=e.encounter_id "
		        + "where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (" + parameters[1] + "," + parameters[2]
		        + "," + parameters[3] + ") and o.concept_id=" + parameters[4] + " and o.value_coded=" + parameters[5]
		        + " and e.encounter_datetime <= :onOrBefore and e.location_id=:location group by p.patient_id" + " UNION "
		        + "select p.patient_id, min(value_datetime) data_inicio from patient p "
		        + "inner join encounter e on p.patient_id=e.patient_id inner join obs o on e.encounter_id=o.encounter_id "
		        + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (" + parameters[1] + "," + parameters[2]
		        + "," + parameters[3] + ") and o.concept_id=" + parameters[6]
		        + " and o.value_datetime is not null and o.value_datetime <= :onOrBefore and e.location_id=:location "
		        + "group by p.patient_id" + " UNION " + "select e.patient_id, min(e.encounter_datetime) data_inicio from patient p "
		        + "inner join encounter e on p.patient_id=e.patient_id " + "where p.voided=0 and e.encounter_type= " + parameters[1]
		        + " and e.voided=0 and e.encounter_datetime <= :onOrBefore and e.location_id=:location "
		        + "group by p.patient_id) temp1" + "group by patient_id) temp2 where data_inicio between :onOrAfter and :onOrBefore";
	}
}
