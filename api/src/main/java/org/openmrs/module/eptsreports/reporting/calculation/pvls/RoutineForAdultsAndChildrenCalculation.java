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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoutineForAdultsAndChildrenCalculation extends AbstractPatientCalculation {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	/**
	 * Patients on ART for the last X months with one VL result registered in the 12 month period
	 * Between Y to Z months after ART initiation TODO: merge with
	 * RoutineForBreastfeedingAndPregnantWomenCalculation definition
	 * 
	 * @param cohort
	 * @param params
	 * @param context
	 * @return CalculationResultMap
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		
		CalculationResultMap map = new CalculationResultMap();
		
		Concept viralLoad = this.hivMetadata.getHivViralLoadConcept();
		Concept regime = this.hivMetadata.getRegimeConcept();
		
		EncounterType arvAdultoEncounterType = this.hivMetadata.getAdultoSeguimentoEncounterType(); // encounter 6
		EncounterType arvPediatriaEncounterType = this.hivMetadata.getARVPediatriaSeguimentoEncounterType(); // encounter
		// 9
		
		// get the ART initiation date
		CalculationResultMap changingRegimenLines = EptsCalculations.firstObs(regime, cohort, context);
		CalculationResultMap firstAdultoEncounter = EptsCalculations.firstEncounter(arvAdultoEncounterType, cohort, context);
		CalculationResultMap firstPediatriaEncounter = EptsCalculations.firstEncounter(arvPediatriaEncounterType, cohort, context);
		
		CalculationResultMap arvsInitiationDateMap = calculate(
		    Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0), cohort, context);
		CalculationResultMap lastVl = EptsCalculations.lastObs(viralLoad, cohort, context);
		CalculationResultMap patientHavingVL = EptsCalculations.allObs(viralLoad, cohort, context);
		
		for (Integer pId : cohort) {
			
			boolean isOnRoutine = false;
			Date artInitiationDate = null;
			SimpleResult artStartDateResult = (SimpleResult) arvsInitiationDateMap.get(pId);
			Obs lastVlObs = EptsCalculationUtils.obsResultForPatient(lastVl, pId);
			
			if (isNotNull(artStartDateResult)) {
				artInitiationDate = (Date) artStartDateResult.getValue();
			}
			
			Date latestVlLowerDateLimit = this.getLatestVlLowerDateLimit(context);
			
			if (this.isPatientOnArtForMoreThanSixMonths(context, artInitiationDate, lastVlObs, latestVlLowerDateLimit)) {
				
				// get all the VL results for each patient in the last 12 months only if the
				// last VL Obs is within the 12month window
				ListResult vlObsResult = (ListResult) patientHavingVL.get(pId);
				
				List<Obs> viralLoadForPatientTakenWithin12Months = this.getViralLoadForPatientTakenWithin12Months(context, vlObsResult,
				    latestVlLowerDateLimit);
				
				// find out for criteria 1 a
				isOnRoutine = this.isRoutineForCriteria1(viralLoadForPatientTakenWithin12Months, artInitiationDate);
				
				// find out criteria 2
				isOnRoutine = this.isRoutineForCriteria2(viralLoadForPatientTakenWithin12Months);
				
				// find out criteria 3
				isOnRoutine = this.isRoutineForCriteria3(context, pId, viralLoadForPatientTakenWithin12Months, isOnRoutine,
				    changingRegimenLines, firstAdultoEncounter, firstPediatriaEncounter, lastVlObs, vlObsResult);
			}
			map.put(pId, new BooleanResult(isOnRoutine, this));
		}
		
		return map;
	}
	
	// check that this patient should be on ART for more than six months
	private boolean isPatientOnArtForMoreThanSixMonths(PatientCalculationContext context, Date artInitiationDate, Obs lastVlObs,
	        Date latestVlLowerDateLimit) {
		
		if (isNotNull(artInitiationDate) && isNotNull(lastVlObs) && isNotNull(lastVlObs.getObsDatetime())) {
			
			if (this.isLastObsAfterLowerDateLimitAndBeforeCurrentTime(context, lastVlObs, latestVlLowerDateLimit, context.getNow())) {
				return true;
			}
		}
		return false;
	}
	
	// we do not consider if the patient's last VL obs is not within window
	private boolean isLastObsAfterLowerDateLimitAndBeforeCurrentTime(PatientCalculationContext context, Obs lastVlObs,
	        Date latestVlLowerDateLimit, Date currentDate) {
		if (lastVlObs.getObsDatetime().after(latestVlLowerDateLimit) && lastVlObs.getObsDatetime().before(currentDate)) {
			return true;
		}
		return false;
	}
	
	// we do not consider if the patient's last VL obs is not within window
	private Date getLatestVlLowerDateLimit(PatientCalculationContext context) {
		return EptsCalculationUtils.addMonths(context.getNow(), -12);
	}
	
	private List<Obs> getViralLoadForPatientTakenWithin12Months(PatientCalculationContext context, ListResult vlObsResult,
	        Date latestVlLowerDateLimit) {
		
		List<Obs> viralLoadForPatientTakenWithin12Months = new ArrayList<Obs>();
		
		if (vlObsResult != null && !vlObsResult.isEmpty()) {
			List<Obs> vLoadList = EptsCalculationUtils.extractResultValues(vlObsResult);
			
			// populate viralLoadForPatientTakenWithin12Months with obs which fall within
			// the 12month window
			if (vLoadList.size() > 0) {
				for (Obs obs : vLoadList) {
					if (obs != null && this.isLastObsAfterLowerDateLimitAndBeforeCurrentTime(context, obs, latestVlLowerDateLimit,
					    context.getNow())) {
						viralLoadForPatientTakenWithin12Months.add(obs);
					}
				}
			}
		}
		return viralLoadForPatientTakenWithin12Months;
	}
	
	/**
	 * find out for criteria 1 a the patients should be 6 to 9 months after ART initiation get the obs
	 * date for this VL and compare that with the provided dates
	 * 
	 * @param viralLoadForPatientTakenWithin12Months
	 * @param artInitiationDate
	 * @return
	 */
	private boolean isRoutineForCriteria1(List<Obs> viralLoadForPatientTakenWithin12Months, Date artInitiationDate) {
		
		if (viralLoadForPatientTakenWithin12Months.size() > 1) {
			for (Obs vlObs : viralLoadForPatientTakenWithin12Months) {
				if (vlObs != null && vlObs.getObsDatetime() != null) {
					Date vlDate = vlObs.getObsDatetime();
					if (EptsCalculationUtils.monthsSince(artInitiationDate, vlDate) > 6
					        && EptsCalculationUtils.monthsSince(artInitiationDate, vlDate) <= 9) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * find out criteria 2
	 * 
	 * @param viralLoadForPatientTakenWithin12Months
	 * @return
	 */
	private boolean isRoutineForCriteria2(List<Obs> viralLoadForPatientTakenWithin12Months) {
		
		// find out criteria 2
		if (viralLoadForPatientTakenWithin12Months.size() > 1) {
			
			// Sort list of VL obs
			Collections.sort(viralLoadForPatientTakenWithin12Months, new Comparator<Obs>() {
				
				@Override
				public int compare(Obs obs1, Obs obs2) {
					return obs1.getObsId().compareTo(obs2.getObsId());
				}
			});
			Obs currentObs = viralLoadForPatientTakenWithin12Months.get(viralLoadForPatientTakenWithin12Months.size() - 1);
			
			// find previous obs from entire list not just the obs in the 12month window
			Obs previousObs = viralLoadForPatientTakenWithin12Months.get(viralLoadForPatientTakenWithin12Months.size() - 2);
			
			if (isNotNull(previousObs, currentObs)
			        && isNotNull(previousObs.getValueNumeric(), previousObs.getObsDatetime(), currentObs.getObsDatetime())
			        && previousObs.getValueNumeric() < 1000 && previousObs.getObsDatetime().before(currentObs.getObsDatetime())) {
				
				if (EptsCalculationUtils.monthsSince(previousObs.getObsDatetime(), currentObs.getObsDatetime()) >= 12
				        && EptsCalculationUtils.monthsSince(previousObs.getObsDatetime(), currentObs.getObsDatetime()) <= 15) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * find out criteria 3
	 * 
	 * @param pId
	 * @param viralLoadForPatientTakenWithin12Months
	 * @param isOnRoutine
	 * @param changingRegimenLines
	 * @param firstAdultoEncounter
	 * @param firstPediatriaEncounter
	 * @param lastVlObs
	 * @param vlObsResult
	 * @return
	 */
	private boolean isRoutineForCriteria3(PatientCalculationContext context, Integer pId,
	        List<Obs> viralLoadForPatientTakenWithin12Months, boolean isOnRoutine, CalculationResultMap changingRegimenLines,
	        CalculationResultMap firstAdultoEncounter, CalculationResultMap firstPediatriaEncounter, Obs lastVlObs,
	        ListResult vlObsResult) {
		
		if (!isOnRoutine && viralLoadForPatientTakenWithin12Months.size() > 0) {
			// get when a patient switch between lines from first to second
			// Date when started on second line will be considered the changing date
			Obs obs = EptsCalculationUtils.obsResultForPatient(changingRegimenLines, pId);
			Encounter adultoEncounter = EptsCalculationUtils.encounterResultForPatient(firstAdultoEncounter, pId);
			Encounter pediatriaEncounter = EptsCalculationUtils.encounterResultForPatient(firstPediatriaEncounter, pId);
			// loop through the viral load list and find one that is after the second line
			// option
			Date finalDate = null;
			if (isNotNull(adultoEncounter)) {
				finalDate = adultoEncounter.getEncounterDatetime();
			}
			if (isNotNull(finalDate, pediatriaEncounter)) {
				finalDate = pediatriaEncounter.getEncounterDatetime();
			}
			List<Integer> codedObsValues = Arrays.asList(6108, 1311, 1312, 1313, 1314, 1315, 6109, 6325, 6326, 6327, 6328);
			
			Date latestVlDate = lastVlObs.getObsDatetime();
			if (isNotNull(obs, latestVlDate, finalDate)) {
				if (obs.getObsDatetime().before(latestVlDate) && codedObsValues.contains(obs.getValueCoded().getConceptId())) {
					isOnRoutine = true;
					// check that there is no other VL registered between first encounter_date and
					// vl_registered_date
					// get list of all vls for this patient
					List<Obs> allVlsForPatient = EptsCalculationUtils.extractResultValues(vlObsResult);
					// loop through the vls and exclude the patient if they have an obs falling
					// between the 2 dates
					for (Obs obs1 : allVlsForPatient) {
						
						if (this.isLastObsAfterLowerDateLimitAndBeforeCurrentTime(context, obs1, finalDate, context.getNow())) {
							return false;
						}
					}
				}
			}
		}
		return isOnRoutine;
	}
	
}
