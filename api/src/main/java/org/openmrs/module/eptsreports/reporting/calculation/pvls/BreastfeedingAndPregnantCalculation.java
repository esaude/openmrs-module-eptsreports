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
import org.openmrs.Patient;
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
 * @return CalculationResultMap
 *
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
		EncounterType childFollowup = hivMetadata.getARVPediatriaSeguimentoEncounterType();
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

        //get female patients only
        Set<Integer> female = EptsCalculationUtils.female(cohort, context);
        //get the last VL results for patients
		CalculationResultMap lastVlObsMap = EptsCalculations.lastObs(
				Arrays.asList(labEncounterType, adultFollowup, childFollowup), viralLoadConcept, location,
				latestVlStartDate, context.getNow(), cohort, context);
        //get results maps for pregnant women
		CalculationResultMap markedPregnant = EptsCalculations.getObs(pregnant, female, Arrays.asList(location), Arrays.asList(gestation), TimeQualifier.ANY, null, context);
        CalculationResultMap markedPregnantByWeeks = EptsCalculations.getObs(pregnantBasedOnWeeks, female, Arrays.asList(location), null, TimeQualifier.ANY, null, context );
        CalculationResultMap markedPregnantOnEdd = EptsCalculations.getObs(edd, female, Arrays.asList(location), null, TimeQualifier.ANY, null, context));
        CalculationResultMap markedPregnantInProgram = EptsCalculations.lastProgramEnrollment(ptv, female, context);

        //get results maps for the breastfeeding women
		CalculationResultMap deliveryDateMap = EptsCalculations.lastObs(Arrays.asList(adultInitial, adultFollowup), priorDeliveryDate, location, null, context.getNow(), female, context);
		CalculationResultMap criteriaHivStartMap = EptsCalculations.lastObs(Arrays.asList(adultInitial, adultFollowup), criteriaForHivStart, location, null, context.getNow(), female, context);
		CalculationResultMap lactatingMap = EptsCalculations.lastObs(Arrays.asList(adultInitial, adultFollowup), breastfeedingConcept, location, null, context.getNow(), female, context);
		// get patients who have been on ART for more than 3 months
		Set<Integer> onArtForMoreThan3Months = EptsCalculationUtils.patientsThatPass(calculate(Context
				.getRegisteredComponents(OnArtForMoreThanXmonthsCalcultion.class).get(0), cohort, context));

        for(Integer pId: cohort){
            boolean pass = false;
			Obs lastVlObs = EptsCalculationUtils.obsResultForPatient(lastVlObsMap, pId);
            //evaluating pregnant maps
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

           // Obs markedEdd = EptsCalculationUtils.obsResultForPatient(markedPregnantOnEdd, pId);
			SimpleResult result = (SimpleResult) markedPregnantInProgram.get(pId);

			//evaluating breastfeeding maps
			Obs deliveryObs = EptsCalculationUtils.obsResultForPatient(deliveryDateMap, pId);
			Obs criteriaHivStartObs = EptsCalculationUtils.obsResultForPatient(criteriaHivStartMap, pId);
			Obs lactationObs = EptsCalculationUtils.obsResultForPatient(lactatingMap, pId);

			PatientProgram patientProgram = null;
			PatientState breastfeedingState = null;
			PatientState pregnantState = null;


            if(female.contains(pId) && lastVlObs != null && lastVlObs.getObsDatetime() != null && criteria != null && onArtForMoreThan3Months.contains(pId)) {
                Date pregnancyStartDate = EptsCalculationUtils.addMonths(lastVlObs.getObsDatetime(), -9);
                Date breastfeedingStartDate = EptsCalculationUtils.addMonths(lastVlObs.getObsDatetime(), -18);
                Date endDate = lastVlObs.getObsDatetime();

                if(result != null) {
                    patientProgram = (PatientProgram)result.getValue();
                }
				// get patient_state for pregnant and breastfeeding
				if (patientProgram != null) {
					for (PatientState patientState : patientProgram.getCurrentStates()) {
						if (patientState.getState().getProgramWorkflowStateId() == 27) {
							pregnantState = patientState;

						} if (patientState.getState().getProgramWorkflowStateId() == 25) {
							breastfeedingState = patientState;
						}
					}
				}
                
				if(criteria.equals(BreastfeedingAndPregnant.PREGNANT)) {
                	if(markedPregnantList.size() > 0) {
                		for(Obs obs:markedPregnantList) {
							if (obs != null && obs.getObsDatetime() != null
									&& (obs.getObsDatetime().equals(pregnancyStartDate) || obs.getObsDatetime().after(pregnancyStartDate))
									&& (obs.getObsDatetime().equals(endDate) || obs.getObsDatetime().before(endDate))) {
								pass = true;
								break;
							}
						}
					}
					if(markedPregnantByWeeksList.size() > 0) {
                		for(Obs obs:markedPregnantByWeeksList) {
							if (obs != null && obs.getValueNumeric() != null && obs.getObsDatetime() != null
									&& (obs.getObsDatetime().equals(pregnancyStartDate) || obs.getObsDatetime().after(pregnancyStartDate))
									&& (obs.getObsDatetime().equals(endDate) || obs.getObsDatetime().before(endDate))) {
								pass = true;
								break;
							}
						}
					}

					if(markedPregnantOnEddList.size() > 0) {
                		for(Obs obs:markedPregnantOnEddList) {
							if (obs != null && obs.getValueDatetime() != null && obs.getObsDatetime() != null
									&& (obs.getObsDatetime().equals(pregnancyStartDate) || obs.getObsDatetime().after(pregnancyStartDate))
									&& (obs.getObsDatetime().equals(endDate) || obs.getObsDatetime().before(endDate))) {
								pass = true;
								break;
							}
						}
					}
                    if(patientProgram != null && patientProgram.getDateEnrolled() != null
                            && (patientProgram.getDateEnrolled().equals(pregnancyStartDate) || patientProgram.getDateEnrolled().after(pregnancyStartDate))
                            && (patientProgram.getDateEnrolled().equals(endDate) || patientProgram.getDateEnrolled().before(endDate))) {
                        pass = true;
                    }
                    if(pregnantState != null && pregnantState.getStartDate() != null
							&& (pregnantState.getStartDate().equals(pregnancyStartDate) || pregnantState.getStartDate().after(pregnancyStartDate))
							&& (pregnantState.getStartDate().equals(endDate) || pregnantState.getStartDate().before(endDate))) {
                    	pass = true;
					}
					if(pregnantState != null && breastfeedingState != null && pregnantState.getStartDate() != null && breastfeedingState.getStartDate() != null) {
                    	if(breastfeedingState.getStartDate().after(pregnantState.getEndDate())) {
                    		pass = false;
						}
					}
                }
                else if(criteria.equals(BreastfeedingAndPregnant.BREASTFEEDING)) {
					if(breastfeedingState != null && breastfeedingState.getStartDate() != null
							&& (breastfeedingState.getStartDate().equals(breastfeedingStartDate) || breastfeedingState.getStartDate().after(breastfeedingStartDate))
							&& (breastfeedingState.getStartDate().equals(endDate) || breastfeedingState.getStartDate().before(endDate))) {
						pass = true;
					}
					if (deliveryObs != null && deliveryObs.getValueDatetime() != null
							&& (deliveryObs.getObsDatetime().equals(breastfeedingStartDate) || deliveryObs.getObsDatetime().after(breastfeedingStartDate))
							&& (deliveryObs.getObsDatetime().equals(endDate) || deliveryObs.getObsDatetime().before(endDate))) {
						pass = true;
					}
					if (criteriaHivStartObs != null && criteriaHivStartObs.getValueCoded() != null && criteriaHivStartObs.getValueCoded().equals(breastfeedingConcept)
							&& (criteriaHivStartObs.getObsDatetime().equals(breastfeedingStartDate) || criteriaHivStartObs.getObsDatetime().after(breastfeedingStartDate))
							&& (criteriaHivStartObs.getObsDatetime().equals(endDate) || criteriaHivStartObs.getObsDatetime().before(endDate))) {
						pass = true;
					}
					if (lactationObs != null && lactationObs.getValueCoded() != null && lactationObs.getValueCoded().equals(yes)
							&& (lactationObs.getObsDatetime().equals(breastfeedingStartDate) || lactationObs.getObsDatetime().after(breastfeedingStartDate))
							&& (lactationObs.getObsDatetime().equals(endDate) || lactationObs.getObsDatetime().before(endDate))) {
						pass = true;
					}
					if(pregnantState != null && breastfeedingState != null && pregnantState.getStartDate() != null && breastfeedingState.getStartDate() != null) {
						if(breastfeedingState.getStartDate().before(pregnantState.getEndDate())) {
							pass = false;
						}
					}
					
                }
            }
            ret.put(pId, new BooleanResult(pass, this));
        }
        return ret;
    }
}
