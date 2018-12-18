package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import org.openmrs.Concept;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils.addMoths;

public class RoutineForBreastfeedingAndPregnantWomenCalculation extends AbstractPatientCalculation {
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		CalculationResultMap ret = new CalculationResultMap();
		
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
				if (artInitiationDate != null && lastVlObs != null && lastVlObs.getObsDatetime() != null) {
					Date latestVlLowerDateLimit = addMoths(context.getNow(), -12);
					isOnRoutine = true;
				}
			}
			ret.put(pId, new BooleanResult(isOnRoutine, this));
		}
		return ret;
	}
}
