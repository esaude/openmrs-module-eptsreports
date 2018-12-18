package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils.addMoths;

@Component
public class RoutineForAdultsAndChildrenCalculation extends AbstractPatientCalculation {
	
	/**
	 * Patients on ART for the last X months with one VL result registered in the 12 month period
	 * Between Y to Z months after ART initiation
	 * 
	 * @param cohort
	 * @param params
	 * @param context
	 * @return
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		
		CalculationResultMap map = new CalculationResultMap();
		ConceptService conceptService = Context.getConceptService();
		EncounterService encounterService = Context.getEncounterService();
		
		Concept viralLoad = conceptService.getConceptByUuid("e1d6247e-1d5f-11e0-b929-000c29ad1d07");
		Concept secondLine = conceptService.getConceptByUuid("7f367983-9911-4f8c-bbfc-a85678801f64");
		EncounterType encounterType6 = encounterService.getEncounterTypeByUuid("e278f956-1d5f-11e0-b929-000c29ad1d07");
		EncounterType encounterType9 = encounterService.getEncounterTypeByUuid("e278fce4-1d5f-11e0-b929-000c29ad1d07");
		
		// get the ART initiation date
		CalculationResultMap arvsInitiationDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap patientHavingVL = EptsCalculations.allObs(viralLoad, cohort, context);
		CalculationResultMap changingRegimenLines = EptsCalculations.lastObs(secondLine, cohort, context);
		CalculationResultMap lastVl = EptsCalculations.lastObs(viralLoad, cohort, context);
		
		// get first encounter for the option c
		CalculationResultMap encounter6 = EptsCalculations.firstEncounter(encounterType6, cohort, context);
		CalculationResultMap encounter9 = EptsCalculations.firstEncounter(encounterType9, cohort, context);
		
		Set<Integer> alivePatients = EptsCalculationUtils.patientsThatPass(EptsCalculations.alive(cohort, context));
		
		for (Integer pId : cohort) {
			boolean isOnRoutine = false;
			Date artInitiationDate = null;
			List<Obs> viralLoadForPatientTakenWithin12Months = new ArrayList<Obs>();
			if (alivePatients.contains(pId)) {
				SimpleResult artStartDateResult = (SimpleResult) arvsInitiationDateMap.get(pId);
				// get all the VL results for each patient in the last 12 months
				ListResult vlObsResult = (ListResult) patientHavingVL.get(pId);
				Obs lastVlObs = EptsCalculationUtils.obsResultForPatient(lastVl, pId);
				
				if (artStartDateResult != null) {
					artInitiationDate = (Date) artStartDateResult.getValue();
				}
				// check that this patient should be on ART for more than six months
				
				if (artInitiationDate != null && lastVlObs != null && lastVlObs.getObsDatetime() != null) {
					Date latestVlLowerDateLimit = addMoths(context.getNow(), -12);
					if (lastVlObs.getObsDatetime().after(latestVlLowerDateLimit)
					        && lastVlObs.getObsDatetime().before(context.getNow())) {
						if (vlObsResult != null && !vlObsResult.isEmpty()) {
							List<Obs> vLoadList = EptsCalculationUtils.extractResultValues(vlObsResult);
							if (vLoadList.size() > 0) {
								for (Obs obs : vLoadList) {
									Date vlLowerDateLimitFromObs = addMoths(context.getNow(), -12);
									if (obs != null && obs.getObsDatetime().after(vlLowerDateLimitFromObs)
									        && obs.getObsDatetime().before(context.getNow())) {
										viralLoadForPatientTakenWithin12Months.add(obs);
									}
								}
							}
						}
					}
					// find out for criteria 1
					if (EptsCalculationUtils.monthsSince(artInitiationDate, context.getNow()) > 6
					        && viralLoadForPatientTakenWithin12Months.size() == 1) {
						// the patients should be 6 to 9 months after ART initiation
						// get the obs date for this VL and compare that with the provided dates
						Obs vlObs = viralLoadForPatientTakenWithin12Months.get(0);
						if (vlObs != null && vlObs.getObsDatetime() != null) {
							Date vlDate = vlObs.getObsDatetime();
							if (EptsCalculationUtils.monthsSince(vlDate, artInitiationDate) > 6
							        && EptsCalculationUtils.monthsSince(vlDate, artInitiationDate) <= 9) {
								isOnRoutine = true;
							}
						}
					}
					
					// find out criteria 2
					if (viralLoadForPatientTakenWithin12Months.size() > 1) {
						
						Collections.sort(viralLoadForPatientTakenWithin12Months, new Comparator<Obs>() {
							
							public int compare(Obs obs1, Obs obs2) {
								return obs1.getObsId().compareTo(obs2.getObsId());
							}
						});
						
						Obs previousObs = viralLoadForPatientTakenWithin12Months
						        .get(viralLoadForPatientTakenWithin12Months.size() - 2);
						Obs currentObs = viralLoadForPatientTakenWithin12Months.get(viralLoadForPatientTakenWithin12Months.size() - 1);
						if (currentObs != null && previousObs != null && previousObs.getValueNumeric() != null
						        && previousObs.getObsDatetime() != null && previousObs.getValueNumeric() < 1000
						        && currentObs.getObsDatetime() != null
						        && previousObs.getObsDatetime().before(currentObs.getObsDatetime())) {
							if (EptsCalculationUtils.monthsSince(previousObs.getObsDatetime(), currentObs.getObsDatetime()) >= 12
							        && EptsCalculationUtils.monthsSince(previousObs.getObsDatetime(),
							            currentObs.getObsDatetime()) <= 15) {
								isOnRoutine = true;
							}
						}
					}
					
					// find out criteria 3
					if (viralLoadForPatientTakenWithin12Months.size() > 0) {
						// get when a patient switch between lines from first to second
						// Date when started on second line will be considered the changing date
						Obs obs = EptsCalculationUtils.obsResultForPatient(changingRegimenLines, pId);
						Encounter encounter1 = EptsCalculationUtils.encounterResultForPatient(encounter6, pId);
						Encounter encounter2 = EptsCalculationUtils.encounterResultForPatient(encounter9, pId);
						// loop through the viral load list and find one that is after the second line
						// option
						Date finalDate = null;
						if (encounter1 != null) {
							finalDate = encounter1.getEncounterDatetime();
						}
						if (finalDate == null && encounter2 != null) {
							finalDate = encounter2.getEncounterDatetime();
						}
						Date latestVlDate = lastVlObs.getObsDatetime();
						
						if (obs != null && finalDate != null && latestVlDate != null) {
							if (finalDate.before(latestVlDate)) {
								isOnRoutine = true;
							}
						}
					}
					
				}
			}
			map.put(pId, new BooleanResult(isOnRoutine, this));
		}
		
		return map;
	}
}
