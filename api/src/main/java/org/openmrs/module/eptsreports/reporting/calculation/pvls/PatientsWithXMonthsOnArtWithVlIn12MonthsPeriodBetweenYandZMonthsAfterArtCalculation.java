package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PatientsWithXMonthsOnArtWithVlIn12MonthsPeriodBetweenYandZMonthsAfterArtCalculation extends AbstractPatientCalculation {
	
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
		
		Integer monthsOnArt = (params != null && params.containsKey("monthsOnArt")) ? (Integer) params.get("monthsOnArt") : null;
		Integer artlowerLimit1 = (params != null && params.containsKey("artLowerLimit1")) ? (Integer) params.get("artLowerLimit1")
		        : null;
		Integer artupperLimit1 = (params != null && params.containsKey("artUpperLimit2")) ? (Integer) params.get("artUpperLimit2")
		        : null;
		
		Integer artLowerLimit2 = (params != null && params.containsKey("artLowerLimit2")) ? (Integer) params.get("artLowerLimit2")
		        : null;
		Integer artUpperLimit2 = (params != null && params.containsKey("artUpperLimit2")) ? (Integer) params.get("artUpperLimit2")
		        : null;
		
		CalculationResultMap map = new CalculationResultMap();
		ConceptService conceptService = Context.getConceptService();
		Concept viralLoad = conceptService.getConceptByUuid("e1d6247e-1d5f-11e0-b929-000c29ad1d07");
		Concept secondLine = conceptService.getConceptByUuid("7f367983-9911-4f8c-bbfc-a85678801f64");
		
		// get the ART initiation date
		CalculationResultMap arvsInitiationDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap patientHavingVL = EptsCalculations.allObs(viralLoad, cohort, context);
		CalculationResultMap changingRegimenLines = EptsCalculations.lastObs(secondLine, cohort, context);

		Set<Integer> alivePatients = EptsCalculationUtils.patientsThatPass(EptsCalculations.alive(cohort, context));
		
		for (Integer pId : cohort) {
			boolean isOnRoutine = false;
			Date artInitiationDate = null;
			List<Obs> viralLoadForPatientTakenWithin12Months = new ArrayList<>();
			if(alivePatients.contains(pId)) {
			SimpleResult artStartDateResult = (SimpleResult) arvsInitiationDateMap.get(pId);
			// get all the VL results for each patient in the last 12 months
			ListResult vlObssResult = (ListResult) patientHavingVL.get(pId);

			if (artStartDateResult != null) {
				artInitiationDate = (Date) artStartDateResult.getValue();
			}
			// check that this patient should be on ART for more than six months
			if (artInitiationDate != null) {
				if (vlObssResult != null && !vlObssResult.isEmpty()) {
					List<Obs> viralLoadList = EptsCalculationUtils.extractResultValues(vlObssResult);
					// go through the list and only find those viral load that follow within 12
					// months
					if (viralLoadList.size() > 0) {
						for (Obs obs : viralLoadList) {
							if (EptsCalculationUtils.monthsSince(artInitiationDate, context) <= 12) {
								viralLoadForPatientTakenWithin12Months.add(obs);
							}
						}
					}
				}
				// find out for criteria 1
				if (artupperLimit1 != null && artlowerLimit1 != null && monthsOnArt != null
						&& EptsCalculationUtils.monthsSince(artInitiationDate, context) > monthsOnArt
						&& viralLoadForPatientTakenWithin12Months.size() == 1) {
					// the patients should be 6 to 9 months after ART initiation
					Date sixMonthsAfterArt = addMoths(artlowerLimit1, artInitiationDate);
					Date nineMonthsAfterArt = addMoths(artupperLimit1, artInitiationDate);
					// get the obs date for this VL and compare that with the provided dates
					Obs vlObs = viralLoadForPatientTakenWithin12Months.get(0);
					if (vlObs != null && vlObs.getObsDatetime().after(sixMonthsAfterArt)
							&& vlObs.getObsDatetime().before(nineMonthsAfterArt)) {
						isOnRoutine = true;
					}
				}
				// find out criteria 2
				if (artUpperLimit2 != null && artLowerLimit2 != null && viralLoadForPatientTakenWithin12Months.size() > 1) {
					Date twelveMonthsAfterArt = addMoths(artLowerLimit2, artInitiationDate);
					Date fifteenMonthsAfterArt = addMoths(artUpperLimit2, artInitiationDate);
					// pick the previous obs for this case
					Obs obs = viralLoadForPatientTakenWithin12Months.get(1);
					if (obs != null && obs.getValueNumeric() < 1000 && obs.getObsDatetime().after(twelveMonthsAfterArt)
							&& obs.getObsDatetime().before(fifteenMonthsAfterArt)) {
						isOnRoutine = true;
					}
				}

				// find out criteria 3
				if (viralLoadForPatientTakenWithin12Months.size() > 0) {
					// get when a patient switch between lines from first to second
					// Date when started on second line will be considered the changing date
					Obs obs = EptsCalculationUtils.obsResultForPatient(changingRegimenLines, pId);
					// loop through the viral load list and find one that is after the second line
					// option
					for (Obs obs1 : viralLoadForPatientTakenWithin12Months) {
						if (obs != null && obs1.getObsDatetime().after(obs.getObsDatetime())) {
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
	
	private Date addMoths(int months, Date endDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.MONTH, -months);
		return c.getTime();
	}
}
