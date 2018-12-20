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

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OnArtForMoreThanXmonthsCalcultion extends AbstractPatientCalculation {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		
		CalculationResultMap map = new CalculationResultMap();
		Concept viralLoadConcept = hivMetadata.getHivViralLoadConcept();
		
		// get data inicio TARV
		CalculationResultMap arvsInitiationDateMap = calculate(
		    Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0), cohort, context);
		CalculationResultMap lastVl = EptsCalculations.lastObs(viralLoadConcept, cohort, context);
		
		for (Integer ptId : cohort) {
			boolean isOnArtForMoreThan3Months = false;
			SimpleResult artStartDateResult = (SimpleResult) arvsInitiationDateMap.get(ptId);
			Obs lastVlObs = EptsCalculationUtils.obsResultForPatient(lastVl, ptId);
			// only include a live patients
			if (checkNotNull(artStartDateResult, lastVlObs)) {
				Date artStartDate = (Date) artStartDateResult.getValue();
				Date lastVlDate = lastVlObs.getObsDatetime();
				if (checkNotNull(artStartDate, lastVlDate) && EptsCalculationUtils.monthsSince(artStartDate, lastVlDate) > 3) {
					isOnArtForMoreThan3Months = true;
				}
			}
			map.put(ptId, new BooleanResult(isOnArtForMoreThan3Months, this));
		}
		
		return map;
	}
	
	private boolean checkNotNull(Object... objects) {
		for (Object object : objects) {
			if (object == null)
				return false;
		}
		return true;
	}
}
