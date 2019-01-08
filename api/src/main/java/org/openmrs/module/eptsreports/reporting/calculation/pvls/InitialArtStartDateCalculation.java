/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Calculates the date on which a patient first started ART
 * 
 * @return a CulculationResultMap
 */
@Component
public class InitialArtStartDateCalculation extends AbstractPatientCalculation {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private CommonMetadata commonMetadata;
	
	/**
	 * @should return null for patients who have not started ART
	 * @should return start date for patients who have started ART
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	        PatientCalculationContext context) {
		
		CalculationResultMap map = new CalculationResultMap();
		Location location = (Location) context.getFromCache("location");
		
		Program treatmentProgram = hivMetadata.getARTProgram();
		Concept arvPlan = hivMetadata.getARVPlanConcept();
		Concept startDrugsConcept = hivMetadata.getstartDrugsConcept();
		Concept transferInConcept = hivMetadata.getTransferFromOtherFacilityConcept();
		Concept hostoricalStartConcept = commonMetadata.gethistoricalDrugStartDateConcept();
		EncounterType encounterTypePharmacy = hivMetadata.getARVPharmaciaEncounterType();
		
		CalculationResultMap inProgramMap = EptsCalculations
		        .firstPatientProgram(treatmentProgram, location, cohort, context);
		CalculationResultMap startDrugMap = EptsCalculations.firstObs(arvPlan, startDrugsConcept, location, true, cohort,
		    context);
		CalculationResultMap historicalMap = EptsCalculations.firstObs(hostoricalStartConcept, null, location, false,
		    cohort, context);
		CalculationResultMap pharmacyEncounterMap = EptsCalculations.firstEncounter(encounterTypePharmacy, cohort, location,
		    context);
		CalculationResultMap transferInMap = EptsCalculations.firstObs(arvPlan, transferInConcept, location, true, cohort,
		    context);
		
		for (Integer pId : cohort) {
			Date requiredDate = null;
			List<Date> enrollmentDates = new ArrayList<Date>();
			SimpleResult result = (SimpleResult) inProgramMap.get(pId);
			if (result != null) {
				PatientProgram patientProgram = (PatientProgram) result.getValue();
				enrollmentDates.add(patientProgram.getDateEnrolled());
			}
			Obs startDrugsObs = EptsCalculationUtils.obsResultForPatient(startDrugMap, pId);
			if (startDrugsObs != null) {
				enrollmentDates.add(startDrugsObs.getObsDatetime());
			}
			Obs historicalDateObs = EptsCalculationUtils.obsResultForPatient(historicalMap, pId);
			if (historicalDateObs != null) {
				enrollmentDates.add(historicalDateObs.getValueDatetime());
			}
			Encounter pharmacyEncounter = EptsCalculationUtils.encounterResultForPatient(pharmacyEncounterMap, pId);
			if (pharmacyEncounter != null) {
				enrollmentDates.add(pharmacyEncounter.getEncounterDatetime());
			}
			Obs transferInObs = EptsCalculationUtils.obsResultForPatient(transferInMap, pId);
			if (transferInObs != null) {
				enrollmentDates.add(transferInObs.getObsDatetime());
			}
			enrollmentDates.removeAll(Collections.singleton(null));
			if (enrollmentDates.size() > 0) {
				Collections.sort(enrollmentDates);
				requiredDate = enrollmentDates.get(0);
			}
			map.put(pId, new SimpleResult(requiredDate, this));
		}
		return map;
	}
}
