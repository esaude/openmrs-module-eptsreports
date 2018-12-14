package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Calculates the date on which a patient first started ART
 * 
 * @return a CulculationResultMap
 */
public class InitialArtStartDateCalculation extends AbstractPatientCalculation {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should return null for patients who have not started ART
	 * @should return start date for patients who have started ART
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	        PatientCalculationContext context) {
		
		// Get calculation map date for the first program enrollment
		CalculationResultMap map = new CalculationResultMap();
		CalculationResultMap programMap = EptsCalculations.firstProgramEnrollment(hivMetadata.getARTProgram(), cohort, context);
		CalculationResultMap startDrugMap = EptsCalculations.firstObs(hivMetadata.getARVPlanConcept(), cohort, context);
		CalculationResultMap historicalMap = EptsCalculations.firstObs(hivMetadata.gethistoricalDrugStartDateConcept(), cohort,
		    context);
		CalculationResultMap pharmacyEncounterMap = EptsCalculations.firstEncounter(hivMetadata.getARVPharmaciaEncounterType(), cohort,
		    context);
		
		for (Integer pId : cohort) {
			Date dateEnrolledIntoProgram = null;
			Date dateStartedDrugs = null;
			Date historicalDate = null;
			Date pharmacyDate = null;
			
			CalculationResult programResults = EptsCalculationUtils.resultForPatient(programMap, pId);
			if (programResults != null) {
				PatientProgram patientProgram = (PatientProgram) programResults.getValue();
				if (patientProgram != null) {
					dateEnrolledIntoProgram = patientProgram.getDateEnrolled();
				}
			}
			
			Obs startDateObsResults = EptsCalculationUtils.obsResultForPatient(startDrugMap, pId);
			if (startDateObsResults != null && startDateObsResults.getValueCoded().equals(hivMetadata.getstartDrugsConcept())) {
				if (startDateObsResults.getEncounter().getEncounterType().equals(hivMetadata.getARVPharmaciaEncounterType())
				        || startDateObsResults.getEncounter().getEncounterType().equals(hivMetadata.getAdultoSeguimentoEncounterType())
				        || startDateObsResults.getEncounter().getEncounterType()
				                .equals(hivMetadata.getARVPediatriaSeguimentoEncounterType())) {
					dateStartedDrugs = startDateObsResults.getObsDatetime();
				}
			}
			Obs historicalDateValue = EptsCalculationUtils.obsResultForPatient(historicalMap, pId);
			if (historicalDateValue != null && historicalDateValue.getValueDatetime() != null) {
				if (historicalDateValue.getEncounter().getEncounterType().equals(hivMetadata.getARVPharmaciaEncounterType())
				        || historicalDateValue.getEncounter().getEncounterType().equals(hivMetadata.getAdultoSeguimentoEncounterType())
				        || historicalDateValue.getEncounter().getEncounterType()
				                .equals(hivMetadata.getARVPediatriaSeguimentoEncounterType())) {
					historicalDate = historicalDateValue.getObsDatetime();
				}
			}
			Encounter pharmacyEncounter = EptsCalculationUtils.encounterResultForPatient(pharmacyEncounterMap, pId);
			if (pharmacyEncounter != null) {
				pharmacyDate = pharmacyEncounter.getEncounterDatetime();
			}
			
			Date minDate = Collections.min(Arrays.asList(dateEnrolledIntoProgram, dateStartedDrugs, historicalDate, pharmacyDate));
			
			map.put(pId, new SimpleResult(minDate, this));
		}
		
		return map;
	}
}
