package org.openmrs.module.eptsreports.reporting.calculation;

import java.util.Arrays;
import java.util.Collection;
import org.openmrs.calculation.CountingCalculation;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;

//TODO probably move this into calculation module
public class CountingCalculationTest extends BasePatientCalculationTest {

  @Override
  PatientCalculation getCalculation() {
    return new CountingCalculation();
  }

  @Override
  Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {1, 2, 3});
  }

  @Override
  CalculationResultMap getResult() {
    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();
    map.put(1, new SimpleResult(1, calculation, getEvaluationContext()));
    map.put(2, new SimpleResult(2, calculation, getEvaluationContext()));
    map.put(3, new SimpleResult(3, calculation, getEvaluationContext()));
    return map;
  }
}
