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
package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates whether patients are in the HIV program
 */
@Component
public class InHivProgramEnrollmentCalculation extends AbstractPatientCalculation {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		
		CalculationResultMap map = new CalculationResultMap();
		Location location = (Location) context.getFromCache("location");
		Program hivProgram = hivMetadata.getARTProgram();
		
		CalculationResultMap programMap = EptsCalculations.firstProgramEnrollment(hivProgram, cohort, context);
		
		for (Integer pId : cohort) {
			Date enrollmentDate = null;
			PatientProgram patientProgram = null;
			if (programMap != null) {
				patientProgram = EptsCalculationUtils.resultForPatient(programMap, pId);
			}
			
			if (patientProgram != null && patientProgram.getDateEnrolled() != null && location != null && patientProgram.getLocation().equals(location)) {
				enrollmentDate = patientProgram.getDateEnrolled();
			}
			map.put(pId, new SimpleResult(enrollmentDate, this));
		}
		return map;
	}
}
