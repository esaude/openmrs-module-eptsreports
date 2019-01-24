package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.BreastfeedingAndPregnant;
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

        Program ptv = hivMetadata.getPtvEtvProgram();

        //get female patients only
        Set<Integer> female = EptsCalculationUtils.female(cohort, context);
        //get the last VL results for patients
		CalculationResultMap lastVlObsMap = EptsCalculations.lastObs(
				Arrays.asList(labEncounterType, adultFollowup, childFollowup), viralLoadConcept, location,
				latestVlStartDate, context.getNow(), cohort, context);
        //get results maps for pregnant women
		CalculationResultMap markedPregnant = EptsCalculations.lastObs(Arrays.asList(adultInitial, adultFollowup), pregnant, location, null, context.getNow(), female, context);
        CalculationResultMap markedPregnantByWeeks = EptsCalculations.lastObs(Arrays.asList(adultInitial, adultFollowup), pregnantBasedOnWeeks, location, null, context.getNow(), female, context);
        CalculationResultMap markedPregnantOnEdd = EptsCalculations.lastObs(Arrays.asList(adultInitial, adultFollowup), edd, location, null, context.getNow(), female, context);
        CalculationResultMap markedPregnantInProgram = EptsCalculations.firstPatientProgram(ptv, location, female, context);

        for(Integer pId: cohort){
            boolean pass = false;
            Obs lastVlObs = EptsCalculationUtils.obsResultForPatient(lastVlObsMap, pId);
            Obs weeksObs = EptsCalculationUtils.obsResultForPatient(markedPregnantByWeeks, pId);
            Obs markedPregnantObs = EptsCalculationUtils.obsResultForPatient(markedPregnant, pId);
            Obs markedEdd = EptsCalculationUtils.obsResultForPatient(markedPregnantOnEdd, pId);
			SimpleResult result = (SimpleResult) markedPregnantInProgram.get(pId);
			PatientProgram patientProgram = null;
			if(result != null) {
				patientProgram = (PatientProgram)result.getValue();
			}

            if(female.contains(pId) && lastVlObs != null && lastVlObs.getObsDatetime() != null && criteria != null) {
                Date pregnancyStartDate = EptsCalculationUtils.addMonths(lastVlObs.getObsDatetime(), -9);
                Date breastfeedingStartDate = EptsCalculationUtils.addMonths(lastVlObs.getObsDatetime(), -18);
                Date endDate = lastVlObs.getObsDatetime();
                
				if(criteria.equals(BreastfeedingAndPregnant.PREGNANT)) {
                    if (markedPregnantObs != null && markedPregnantObs.getValueCoded() != null && markedPregnantObs.getValueCoded().equals(gestation)
                            && (markedPregnantObs.getObsDatetime().equals(pregnancyStartDate) || markedPregnantObs.getObsDatetime().after(pregnancyStartDate))
                            && (markedPregnantObs.getObsDatetime().equals(endDate) || markedPregnantObs.getObsDatetime().before(endDate))) {
                        pass = true;
                    }
                    if (weeksObs != null && weeksObs.getValueNumeric() != null && weeksObs.getObsDatetime() != null
                            && (weeksObs.getObsDatetime().equals(pregnancyStartDate) || weeksObs.getObsDatetime().after(pregnancyStartDate))
                            && (weeksObs.getObsDatetime().equals(endDate) || weeksObs.getObsDatetime().before(endDate))) {
                        pass = true;
                    }
                    if (markedEdd != null && markedEdd.getValueDatetime() != null && markedEdd.getObsDatetime() != null
                            && (markedEdd.getObsDatetime().equals(pregnancyStartDate) || markedEdd.getObsDatetime().after(pregnancyStartDate))
                            && (markedEdd.getObsDatetime().equals(endDate) || markedEdd.getObsDatetime().before(endDate))) {
                        pass = true;
                    }
                    if(patientProgram != null && patientProgram.getDateEnrolled() != null
                            && (patientProgram.getDateEnrolled().equals(pregnancyStartDate) || patientProgram.getDateEnrolled().after(pregnancyStartDate))
                            && (patientProgram.getDateEnrolled().equals(endDate) || patientProgram.getDateEnrolled().before(endDate))) {
                        pass = true;
                    }
                }
                else if(criteria.equals(BreastfeedingAndPregnant.BREASTFEEDING)) {

                }
            }
            ret.put(pId, new BooleanResult(pass, this));
        }
        return ret;
    }
}
