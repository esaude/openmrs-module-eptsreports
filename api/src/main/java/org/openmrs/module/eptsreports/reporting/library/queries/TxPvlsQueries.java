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

public class TxPvlsQueries {
	
	/**
	 * Get patients who are aged between age brackets
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static String getPatientsBetweenAgeBracketsInYears(int min, int max) {
		return "SELECT person_id FROM person pe INNER JOIN patient pa ON pe.person_id=pa.patient_id"
		        + " INNER JOIN encounter e ON pa.patient_id=e.patient_id" + " WHERE TIMESTAMPDIFF(year, pe.birthdate, :endDate)>="
		        + min + " AND TIMESTAMPDIFF(year, pe.birthdate, :endDate) <=" + max
		        + " AND pe.voided=0 AND pa.voided=0 AND e.location_id=:location AND e.voided=0" + " AND pe.birthdate is not null";
	}
	
	/**
	 * Find patients who are aged below
	 * 
	 * @param age
	 * @return
	 */
	public static String getPatientsWhoAreBelowXyears(int age) {
		return "SELECT person_id FROM person pe INNER JOIN patient pa ON pe.person_id=pa.patient_id"
		        + " INNER JOIN encounter e ON pa.patient_id=e.patient_id" + " WHERE TIMESTAMPDIFF(year, pe.birthdate, :endDate)<" + age
		        + " AND pe.voided=0 AND pa.voided=0 AND e.location_id=:location AND e.voided=0" + " AND pe.birthdate is not null";
	}
}
