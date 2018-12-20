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

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class OnArtForLessThanXmonthsCalcultion extends AbstractPatientCalculation {
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		
		CalculationResultMap map = new CalculationResultMap();
		Concept viralLoadConceptuuid = Context.getConceptService().getConceptByUuid("e1d6247e-1d5f-11e0-b929-000c29ad1d07");
		// get data inicio TARV
		CalculationResultMap arvsInitiationDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap lastVl = EptsCalculations.lastObs(viralLoadConceptuuid, cohort, context);
		// Set<Integer> alivePatients =
		// EptsCalculationUtils.patientsThatPass(EptsCalculations.alive(cohort,
		// context));
		
		for (Integer ptId : cohort) {
			boolean toExcludeFromList = false;
			SimpleResult artStartDateResult = (SimpleResult) arvsInitiationDateMap.get(ptId);
			Obs lastVlObs = EptsCalculationUtils.obsResultForPatient(lastVl, ptId);
			// only include a live patients
			if (artStartDateResult != null && lastVlObs != null) {
				Date artStartDate = (Date) artStartDateResult.getValue();
				Date lastVlDate = lastVlObs.getObsDatetime();
				if (artStartDate != null && lastVlDate != null && EptsCalculationUtils.monthsSince(artStartDate, lastVlDate) < 3) {
					toExcludeFromList = true;
				}
			}
			map.put(ptId, new BooleanResult(toExcludeFromList, this));
		}
		
		return map;
	}
	
}
