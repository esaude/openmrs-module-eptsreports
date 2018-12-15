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

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class BaseQueries {
	
	// State ids are left as hard coded for now because all reference same concept
	// they map to concept_id=1369 - TRANSFER FROM OTHER FACILITY
	// TODO: Query needs to be refactored
	public static String getBaseCohortQuery(Map<String, String> parameters) {
		String query = "select patient_id from "
		        + "(select p.patient_id from patient p join encounter e on e.patient_id=p.patient_id join person pr on pr.person_id = p.patient_id "
		        + "where e.voided=0 and p.voided=0 and e.encounter_type in (%s) and e.encounter_datetime<=:endDate and e.location_id = :location and pr.birthdate is not null "
		        + "union "
		        + "select pg.patient_id from patient p join patient_program pg on p.patient_id=pg.patient_id where pg.voided=0 and p.voided=0 and program_id=%s and date_enrolled<=:endDate and location_id=:location "
		        + "union "
		        + "select pg.patient_id from patient p join patient_program pg on p.patient_id=pg.patient_id join patient_state ps on pg.patient_program_id=ps.patient_program_id "
		        + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%s and ps.state=28 and ps.start_date=pg.date_enrolled and ps.start_date<=:endDate and location_id=:location "
		        + "union "
		        + "select pg.patient_id from patient p join patient_program pg on p.patient_id=pg.patient_id join patient_state ps on pg.patient_program_id=ps.patient_program_id "
		        + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%s and ps.state=29 and ps.start_date<=:endDate and location_id=:location) t "
		        + "join person pr on pr.person_id = t.patient_id where pr.birthdate is not null ";
		String encounterTypes = StringUtils.join(
		    Arrays.asList(parameters.get("arvAdultInitialEncounterTypeId"),
		        parameters.get("arvPediatriaInitialEncounterTypeId")), ',');
		return String.format(query, encounterTypes, parameters.get("hivCareProgramId"), parameters.get("hivCareProgramId"),
		    parameters.get("artProgramId"));
	}
}
