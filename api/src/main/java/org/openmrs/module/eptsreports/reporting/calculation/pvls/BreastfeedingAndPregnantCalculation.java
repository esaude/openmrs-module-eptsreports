package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
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
        Concept viralLoadConcept = hivMetadata.getHivViralLoadConcept();
        Date latestVlStartDate = EptsCalculationUtils.addMonths(context.getNow(), -12);
        //get female patients only
        Set<Integer> female = EptsCalculationUtils.female(cohort, context);
        //get the last VL results for patients
        CalculationResultMap lastVlObsMap = EptsCalculations.getObs(viralLoadConcept, female, Arrays.asList(location),
                null, TimeQualifier.ANY, latestVlStartDate, context);
        //get results maps for pregnancy

        for(Integer pId: cohort){
            boolean pass = false;
            Obs lastVlObs = EptsCalculationUtils.obsResultForPatient(lastVlObsMap, pId);
            if(female.contains(pId) && lastVlObs != null && lastVlObs.getObsDatetime() != null) {
                Date pregnancyStartDate = EptsCalculationUtils.addMonths(lastVlObs.getObsDatetime(), -9);
                Date breastfeedingStartDate = EptsCalculationUtils.addMonths(lastVlObs.getObsDatetime(), -18);
                Date endDate = lastVlObs.getObsDatetime();

                pass = true;
            }
            ret.put(pId, new BooleanResult(pass, this));
        }
        return ret;
    }
}
