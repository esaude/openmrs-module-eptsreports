package org.openmrs.module.eptsreports.reporting.calculation;

import java.util.Arrays;
import java.util.Collection;

import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.OnArtForMoreThanXmonthsCalcultion;

public class OnArtForMoreThanXmonthsCalcultionTest extends BasePatientCalculationTest {

    @Override
    public PatientCalculation getCalculation() {
        return new OnArtForMoreThanXmonthsCalcultion();
    }

    @Override
    public Collection<Integer> getCohort() {
        return Arrays.asList(new Integer[]{2, 6, 7, 8, 999, 432});
    }

    @Override
    public CalculationResultMap getResult() {
        return null;
    }

}
