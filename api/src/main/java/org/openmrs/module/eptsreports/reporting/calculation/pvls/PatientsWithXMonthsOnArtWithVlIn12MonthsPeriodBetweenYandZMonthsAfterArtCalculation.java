package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class PatientsWithXMonthsOnArtWithVlIn12MonthsPeriodBetweenYandZMonthsAfterArtCalculation extends AbstractPatientCalculation {

    @Autowired
    private HivMetadata hivMetadata;
    /**
     * Patients on ART for the last X months with one VL result registered in the 12 month period
     * Between Y to Z months after ART initiation
     * @param cohort
     * @param parameterValues
     * @param context
     * @return
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
                                         PatientCalculationContext context) {
        CalculationResultMap map = new CalculationResultMap();
        Date startDate = addMoths(12, context.getNow());
        Date endDate = context.getNow();

        //get the ART initiation date
        CalculationResultMap arvsDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap patientHavingVL = EptsCalculations.allObs(hivMetadata.getHivViralLoadConcept(), cohort, context);
        for(Integer pId: cohort){
            Date artInitiationDate = null;
            SimpleResult artStartDateResult = (SimpleResult) arvsDateMap.get(pId);
            if(artStartDateResult != null){
                artInitiationDate = (Date) artStartDateResult.getValue();
            }
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
