package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
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
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.BreastfeedingAndPregnant;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Calculates for patient eligibility to be breastfeeding or pregnant
 * 
 * @return CalculationResultMap
 */
@Component
public class BreastfeedingAndPregnantCalculation extends AbstractPatientCalculation {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		CalculationResultMap ret = new CalculationResultMap();
		Location location = (Location) context.getFromCache("location");
		BreastfeedingAndPregnant criteria = (BreastfeedingAndPregnant) params.get("criteria");
		Concept viralLoadConcept = hivMetadata.getHivViralLoadConcept();
		Date latestVlStartDate = EptsCalculationUtils.addMonths(context.getNow(), -12);
		EncounterType labEncounterType = hivMetadata.getMisauLaboratorioEncounterType();
		EncounterType adultFollowup = hivMetadata.getAdultoSeguimentoEncounterType();
		EncounterType adultInitial = hivMetadata.getARVAdultInitialEncounterType();
		
		Concept pregnant = hivMetadata.getPregnantConcept();
		Concept gestation = hivMetadata.getGestationConcept();
		Concept pregnantBasedOnWeeks = hivMetadata.getNumberOfWeeksPregnant();
		Concept edd = hivMetadata.getPregnancyDueDate();
		Concept breastfeedingConcept = hivMetadata.getBreastfeeding();
		Concept priorDeliveryDate = hivMetadata.getPriorDeliveryDateConcept();
		Concept yes = hivMetadata.getYesConcept();
		Concept criteriaForHivStart = hivMetadata.getCriteriaForArtStart();
		
		Program ptv = hivMetadata.getPtvEtvProgram();
		
		// get female patients only
		Set<Integer> female = EptsCalculationUtils.female(cohort, context);
		// get the last VL results for patients
		CalculationResultMap lastVlObsMap = EptsCalculations.lastObs(Arrays.asList(labEncounterType, adultFollowup, adultInitial),
		    viralLoadConcept, location, latestVlStartDate, context.getNow(), cohort, context);
		// get results maps for pregnant women
		CalculationResultMap markedPregnant = EptsCalculations.getObs(pregnant, female, Arrays.asList(location),
		    Arrays.asList(gestation), TimeQualifier.ANY, null, context);
		CalculationResultMap markedPregnantByWeeks = EptsCalculations.getObs(pregnantBasedOnWeeks, female, Arrays.asList(location),
		    null, TimeQualifier.ANY, null, context);
		CalculationResultMap markedPregnantOnEdd = EptsCalculations.getObs(edd, female, Arrays.asList(location), null,
		    TimeQualifier.ANY, null, context);
		CalculationResultMap markedPregnantInProgram = EptsCalculations.lastProgramEnrollment(ptv, female, context);
		
		// get results maps for the breastfeeding women
		CalculationResultMap deliveryDateMap = EptsCalculations.getObs(priorDeliveryDate, female, Arrays.asList(location), null,
		    TimeQualifier.ANY, null, context);
		CalculationResultMap criteriaHivStartMap = EptsCalculations.getObs(criteriaForHivStart, female, Arrays.asList(location),
		    Arrays.asList(breastfeedingConcept), TimeQualifier.ANY, null, context);
		CalculationResultMap lactatingMap = EptsCalculations.getObs(breastfeedingConcept, female, Arrays.asList(location),
		    Arrays.asList(yes), TimeQualifier.ANY, null, context);
		// get patients who have been on ART for more than 3 months
		Set<Integer> onArtForMoreThan3Months = EptsCalculationUtils.patientsThatPass(
		    calculate(Context.getRegisteredComponents(OnArtForMoreThanXmonthsCalcultion.class).get(0), cohort, context));
		
		for (Integer pId : cohort) {
			boolean pass = false;
			Obs lastVlObs = EptsCalculationUtils.obsResultForPatient(lastVlObsMap, pId);
			SimpleResult result = (SimpleResult) markedPregnantInProgram.get(pId);
			// evaluating pregnant maps
			ListResult markedPregnantObsResult = (ListResult) markedPregnant.get(pId);
			List<Obs> markedPregnantList = new ArrayList<>();
			if (markedPregnantObsResult != null && !markedPregnantObsResult.isEmpty()) {
				markedPregnantList = EptsCalculationUtils.extractResultValues(markedPregnantObsResult);
			}
			
			ListResult markedPregnantByWeeksObsResult = (ListResult) markedPregnantByWeeks.get(pId);
			List<Obs> markedPregnantByWeeksList = new ArrayList<>();
			if (markedPregnantByWeeksObsResult != null && !markedPregnantByWeeksObsResult.isEmpty()) {
				markedPregnantByWeeksList = EptsCalculationUtils.extractResultValues(markedPregnantByWeeksObsResult);
			}
			
			ListResult markedPregnantOnEddObsResult = (ListResult) markedPregnantOnEdd.get(pId);
			List<Obs> markedPregnantOnEddList = new ArrayList<>();
			if (markedPregnantOnEddObsResult != null && !markedPregnantOnEddObsResult.isEmpty()) {
				markedPregnantOnEddList = EptsCalculationUtils.extractResultValues(markedPregnantOnEddObsResult);
			}
			
			// evaluating breastfeeding maps
			ListResult deliveryDateMapObsResult = (ListResult) deliveryDateMap.get(pId);
			List<Obs> deliveryDateMapList = new ArrayList<>();
			if (deliveryDateMapObsResult != null && !deliveryDateMapObsResult.isEmpty()) {
				deliveryDateMapList = EptsCalculationUtils.extractResultValues(deliveryDateMapObsResult);
			}
			
			ListResult criteriaHivStartMapObsResult = (ListResult) criteriaHivStartMap.get(pId);
			List<Obs> criteriaHivStartMapList = new ArrayList<>();
			if (criteriaHivStartMapObsResult != null && !criteriaHivStartMapObsResult.isEmpty()) {
				criteriaHivStartMapList = EptsCalculationUtils.extractResultValues(criteriaHivStartMapObsResult);
			}
			
			ListResult lactatingMapObsResult = (ListResult) lactatingMap.get(pId);
			List<Obs> lactatingMapList = new ArrayList<>();
			if (lactatingMapObsResult != null && !lactatingMapObsResult.isEmpty()) {
				lactatingMapList = EptsCalculationUtils.extractResultValues(lactatingMapObsResult);
			}
			
			PatientProgram patientProgram = null;
			PatientState breastfeedingState = null;
			PatientState pregnantState = null;
			
			if (female.contains(pId) && lastVlObs != null && lastVlObs.getObsDatetime() != null && criteria != null
			        && onArtForMoreThan3Months.contains(pId)) {
				Date pregnancyStartDate = EptsCalculationUtils.addMonths(lastVlObs.getObsDatetime(), -9);
				Date breastfeedingStartDate = EptsCalculationUtils.addMonths(lastVlObs.getObsDatetime(), -18);
				Date endDate = lastVlObs.getObsDatetime();
				
				if (result != null) {
					patientProgram = (PatientProgram) result.getValue();
				}
				// get patient_state for pregnant and breastfeeding
				if (patientProgram != null) {
					for (PatientState patientState : patientProgram.getCurrentStates()) {
						if (patientState.getState().equals(hivMetadata.getPatientIsPregnantWorkflowState())) {
							pregnantState = patientState;
							
						}
						if (patientState.getState().equals(hivMetadata.getPatientIsBreastfeedingWorkflowState())) {
							breastfeedingState = patientState;
						}
					}
				}
				
				if (criteria.equals(BreastfeedingAndPregnant.PREGNANT)) {
					pass = pregnantCriteria(markedPregnantList, markedPregnantByWeeksList, markedPregnantOnEddList, patientProgram,
					    pregnantState, breastfeedingState, pregnancyStartDate, endDate);
				} else if (criteria.equals(BreastfeedingAndPregnant.BREASTFEEDING)) {
					
					pass = breastfeedingCriteria(deliveryDateMapList, criteriaHivStartMapList, breastfeedingState, pregnantState,
					    lactatingMapList, breastfeedingStartDate, endDate);
				}
			}
			ret.put(pId, new BooleanResult(pass, this));
		}
		return ret;
	}
	
	private boolean pregnantCriteria(List<Obs> markedPregnantList, List<Obs> markedPregnantByWeeksList,
	        List<Obs> markedPregnantOnEddList, PatientProgram patientProgram, PatientState pregnantState,
	        PatientState breastfeedingState, Date pregnancyStartDate, Date endDate) {
		boolean pass = false;
		if (!markedPregnantList.isEmpty()) {
			for (Obs obs : markedPregnantList) {
				if (obs != null && obs.getObsDatetime() != null && (obs.getObsDatetime().compareTo(pregnancyStartDate) >= 0)
				        && (obs.getObsDatetime().compareTo(endDate) <= 0)) {
					pass = true;
					break;
				}
			}
		}
		if (!markedPregnantByWeeksList.isEmpty()) {
			for (Obs obs : markedPregnantByWeeksList) {
				if (obs != null && obs.getValueNumeric() != null && obs.getObsDatetime() != null
				        && (obs.getObsDatetime().compareTo(pregnancyStartDate) >= 0)
				        && (obs.getObsDatetime().compareTo(endDate) <= 0)) {
					pass = true;
					break;
				}
			}
		}
		
		if (!markedPregnantOnEddList.isEmpty()) {
			for (Obs obs : markedPregnantOnEddList) {
				if (obs != null && obs.getValueDatetime() != null && obs.getObsDatetime() != null
				        && (obs.getObsDatetime().compareTo(pregnancyStartDate) >= 0)
				        && (obs.getObsDatetime().compareTo(endDate) <= 0)) {
					pass = true;
					break;
				}
			}
		}
		if (patientProgram != null && patientProgram.getDateEnrolled() != null
		        && (patientProgram.getDateEnrolled().compareTo(pregnancyStartDate) >= 0)
		        && (patientProgram.getDateEnrolled().compareTo(endDate) <= 0)) {
			pass = true;
		}
		if (pregnantState != null && pregnantState.getStartDate() != null
		        && (pregnantState.getStartDate().compareTo(pregnancyStartDate) >= 0)
		        && (pregnantState.getStartDate().compareTo(endDate) <= 0)) {
			pass = true;
		}
		if (pregnantState != null && breastfeedingState != null && pregnantState.getStartDate() != null
		        && breastfeedingState.getStartDate() != null
		        && breastfeedingState.getStartDate().after(pregnantState.getEndDate())) {
			pass = false;
		}
		return pass;
	}
	
	private boolean breastfeedingCriteria(List<Obs> deliveryDateMapList, List<Obs> criteriaHivStartMapList,
	        PatientState breastfeedingState, PatientState pregnantState, List<Obs> lactatingMapList,
	        Date breastfeedingStartDate, Date endDate) {
		boolean pass = false;
		if (breastfeedingState != null && breastfeedingState.getStartDate() != null
		        && (breastfeedingState.getStartDate().compareTo(breastfeedingStartDate) >= 0)
		        && (breastfeedingState.getStartDate().compareTo(endDate) <= 0)) {
			pass = true;
		}
		if (!deliveryDateMapList.isEmpty()) {
			for (Obs obs : deliveryDateMapList) {
				if (obs != null && obs.getValueDatetime() != null
				        && (obs.getObsDatetime().compareTo(breastfeedingStartDate) >= 0)
				        && (obs.getObsDatetime().compareTo(endDate) <= 0)) {
					pass = true;
					break;
				}
			}
		}
		if (!criteriaHivStartMapList.isEmpty()) {
			for (Obs obs : criteriaHivStartMapList) {
				if (obs != null && obs.getObsDatetime() != null
				        && (obs.getObsDatetime().compareTo(breastfeedingStartDate) >= 0)
				        && (obs.getObsDatetime().compareTo(endDate) <= 0)) {
					pass = true;
					break;
				}
			}
		}
		if (!lactatingMapList.isEmpty()) {
			for (Obs obs : lactatingMapList) {
				if (obs != null && obs.getObsDatetime() != null
				        && (obs.getObsDatetime().compareTo(breastfeedingStartDate) >= 0)
				        && (obs.getObsDatetime().compareTo(endDate) <= 0)) {
					pass = true;
					
				}
			}
		}
		if (pregnantState != null && breastfeedingState != null && pregnantState.getStartDate() != null
		        && breastfeedingState.getStartDate() != null
		        && breastfeedingState.getStartDate().before(pregnantState.getEndDate())) {
			pass = false;
		}
		return pass;
	}
}
