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
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
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
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.PatientsOnRoutineEnum;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoutineCalculation extends AbstractPatientCalculation {
	
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
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params,
	        PatientCalculationContext context) {
		
		CalculationResultMap map = new CalculationResultMap();
		Location location = (Location) context.getFromCache("location");
		Concept viralLoadConcept = hivMetadata.getHivViralLoadConcept();
		Concept regimeConcept = hivMetadata.getRegimeConcept();
		Date latestVlLowerDateLimit = EptsCalculationUtils.addMonths(context.getNow(), -12);
		EncounterType labEncounterType = hivMetadata.getMisauLaboratorioEncounterType();
		PatientsOnRoutineEnum criteria = (PatientsOnRoutineEnum) params.get("criteria");
		EncounterType adultFollowup = hivMetadata.getAdultoSeguimentoEncounterType();
		EncounterType childFollowup = hivMetadata.getARVPediatriaSeguimentoEncounterType();
		
		// lookups
		CalculationResultMap patientHavingVL = EptsCalculations.getObs(viralLoadConcept, cohort, Arrays.asList(location),
		    null, TimeQualifier.ANY, latestVlLowerDateLimit, context);
		
		CalculationResultMap changingRegimenLines = EptsCalculations.getObs(regimeConcept, cohort, Arrays.asList(location),
		    getSecondLineTreatmentArvs(), TimeQualifier.FIRST, null, context);
		
		// get the ART initiation date
		CalculationResultMap arvsInitiationDateMap = calculate(
		    Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0), cohort, context);
		CalculationResultMap lastVl = EptsCalculations.lastObs(Arrays.asList(labEncounterType, adultFollowup, childFollowup),
		    viralLoadConcept, location, latestVlLowerDateLimit, context.getNow(), cohort, context);
		// get patients who have been on ART for more than 3 months
		Set<Integer> onArtForMoreThan3Months = EptsCalculationUtils.patientsThatPass(
		    calculate(Context.getRegisteredComponents(OnArtForMoreThanXmonthsCalcultion.class).get(0), cohort, context));
		
		for (Integer pId : cohort) {
			boolean isOnRoutine = false;
			Date artInitiationDate = null;
			SimpleResult artStartDateResult = (SimpleResult) arvsInitiationDateMap.get(pId);
			Obs lastVlObs = EptsCalculationUtils.obsResultForPatient(lastVl, pId);
			
			if (artStartDateResult != null) {
				artInitiationDate = (Date) artStartDateResult.getValue();
			}
			// check that this patient should be on ART for more than six months
			if (artInitiationDate != null && lastVlObs != null && lastVlObs.getObsDatetime() != null && criteria != null
			        && onArtForMoreThan3Months.contains(pId)) {
				
				// we do not consider if the patient's last VL obs is not within window
				if (lastVlObs.getObsDatetime().after(latestVlLowerDateLimit)
				        && lastVlObs.getObsDatetime().before(context.getNow())) {
					
					// get all the VL results for each patient in the last 12 months only if the
					// last VL Obs is within the 12month window
					
					ListResult vlObsResult = (ListResult) patientHavingVL.get(pId);
					
					List<Obs> viralLoadForPatientTakenWithin12Months = new ArrayList<Obs>();
					
					List<Obs> vLoadList = getViralLoadForPatientTakenWithin12Months(context, latestVlLowerDateLimit,
					    vlObsResult, viralLoadForPatientTakenWithin12Months);
					
					// find out for criteria 1 a
					// the patients should be 6 to 9 months after ART initiation
					// get the obs date for this VL and compare that with the provided dates
					if (isOnRoutineCriteria1(criteria, artInitiationDate, viralLoadForPatientTakenWithin12Months)) {
						isOnRoutine = true;
					}
					
					// find out criteria 2
					if (viralLoadForPatientTakenWithin12Months.size() > 1
					        && isOnRoutineCriteria2(criteria, viralLoadForPatientTakenWithin12Months)) {
						isOnRoutine = true;
					}
					
					// find out criteria 3
					if (!isOnRoutine && viralLoadForPatientTakenWithin12Months.size() > 0) {
						// get when a patient switch between lines from first to second
						// Date when started on second line will be considered the changing date
						isOnRoutine = isOnRoutineCriteria3(changingRegimenLines, pId, lastVlObs, vLoadList);
					}
				}
			}
			map.put(pId, new BooleanResult(isOnRoutine, this));
		}
		
		return map;
	}
	
	private boolean isOnRoutineCriteria3(CalculationResultMap changingRegimenLines, Integer pId, Obs lastVlObs,
	        List<Obs> vLoadList) {
		boolean isOnRoutine = false;
		Obs obs = EptsCalculationUtils.obsResultForPatient(changingRegimenLines, pId);
		
		Date latestVlDate = lastVlObs.getObsDatetime();
		if (obs != null && latestVlDate != null) {
			Date startRegimeDate = obs.getObsDatetime();
			
			if (startRegimeDate != null && startRegimeDate.before(latestVlDate)) {
				isOnRoutine = true;
				// check that there is no other VL registered between first encounter_date and
				// vl_registered_date
				// loop through the vls and exclude the patient if they have an obs falling
				// between the 2 dates
				for (Obs obs1 : vLoadList) {
					if (obs1.getObsDatetime() != null
					        && (obs1.getObsDatetime().after(startRegimeDate)
					                || obs1.getObsDatetime().equals(startRegimeDate))
					        && obs1.getObsDatetime().before(latestVlDate)) {
						isOnRoutine = false;
						break;
					}
				}
			}
		}
		return isOnRoutine;
	}
	
	private boolean isOnRoutineCriteria2(PatientsOnRoutineEnum criteria, List<Obs> viralLoadForPatientTakenWithin12Months) {
		boolean isOnRoutine = false;
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
		
		if (currentObs != null && previousObs != null && previousObs.getValueNumeric() != null
		        && previousObs.getObsDatetime() != null && previousObs.getValueNumeric() < 1000
		        && currentObs.getObsDatetime() != null && previousObs.getObsDatetime().before(currentObs.getObsDatetime())) {
			
			if (criteria.equals(PatientsOnRoutineEnum.ADULTCHILDREN)) {
				if (EptsCalculationUtils.monthsSince(previousObs.getObsDatetime(), currentObs.getObsDatetime()) >= 12
				        && EptsCalculationUtils.monthsSince(previousObs.getObsDatetime(),
				            currentObs.getObsDatetime()) <= 15) {
					isOnRoutine = true;
				}
			} else if (criteria.equals(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT)) {
				isOnRoutine = true;
			}
		}
		return isOnRoutine;
	}
	
	private boolean isOnRoutineCriteria1(PatientsOnRoutineEnum criteria, Date artInitiationDate,
	        List<Obs> viralLoadForPatientTakenWithin12Months) {
		boolean isOnRoutine = false;
		for (Obs vlObs : viralLoadForPatientTakenWithin12Months) {
			if (vlObs != null && vlObs.getObsDatetime() != null) {
				Date vlDate = vlObs.getObsDatetime();
				if (criteria.equals(PatientsOnRoutineEnum.ADULTCHILDREN)) {
					if (EptsCalculationUtils.monthsSince(artInitiationDate, vlDate) > 6
					        && EptsCalculationUtils.monthsSince(artInitiationDate, vlDate) <= 9) {
						isOnRoutine = true;
						break;
					}
				} else if (criteria.equals(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT)) {
					if (EptsCalculationUtils.monthsSince(vlDate, artInitiationDate) > 3
					        && EptsCalculationUtils.monthsSince(vlDate, artInitiationDate) <= 6) {
						isOnRoutine = true;
						break;
					}
				}
			}
		}
		return isOnRoutine;
	}
	
	private List<Obs> getViralLoadForPatientTakenWithin12Months(PatientCalculationContext context,
	        Date latestVlLowerDateLimit, ListResult vlObsResult, List<Obs> viralLoadForPatientTakenWithin12Months) {
		List<Obs> vLoadList = Collections.emptyList();
		if (vlObsResult != null && !vlObsResult.isEmpty()) {
			vLoadList = EptsCalculationUtils.extractResultValues(vlObsResult);
			
			// populate viralLoadForPatientTakenWithin12Months with obs which fall within
			// the 12month window
			if (vLoadList.size() > 0) {
				for (Obs obs : vLoadList) {
					if (obs != null && obs.getObsDatetime().after(latestVlLowerDateLimit)
					        && obs.getObsDatetime().before(context.getNow())) {
						viralLoadForPatientTakenWithin12Months.add(obs);
					}
				}
			}
		}
		return vLoadList;
	}
	
	private List<Concept> getSecondLineTreatmentArvs() {
		List<Concept> secondLineArvs = new ArrayList<Concept>();
		secondLineArvs.add(hivMetadata.getAzt3tcAbcEfvConcept());
		secondLineArvs.add(hivMetadata.getD4t3tcAbcEfvConcept());
		secondLineArvs.add(hivMetadata.getAzt3tcAbcLpvConcept());
		secondLineArvs.add(hivMetadata.getD4t3tcAbcLpvConcept());
		secondLineArvs.add(hivMetadata.getAztDdiLpvConcept());
		secondLineArvs.add(hivMetadata.getTdf3tcEfvConcept());
		secondLineArvs.add(hivMetadata.getAzt3tcLpvConcept());
		secondLineArvs.add(hivMetadata.getAbc3tcEfvConcept());
		secondLineArvs.add(hivMetadata.getAbc3tcNvpConcept());
		secondLineArvs.add(hivMetadata.getAbc3tcLpvConcept());
		secondLineArvs.add(hivMetadata.getTdf3tcLpvConcept());
		return secondLineArvs;
	}
}
