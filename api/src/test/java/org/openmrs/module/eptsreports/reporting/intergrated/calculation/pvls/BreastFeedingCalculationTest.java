package org.openmrs.module.eptsreports.reporting.intergrated.calculation.pvls;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.BreastfeedingCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class BreastFeedingCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return new BreastfeedingCalculation();
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {7, 8, 501});
  }

  @Override
  public CalculationResultMap getResult() {

    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();

    PatientCalculationContext evaluationContext = getEvaluationContext();

    map.put(501, new SimpleResult(true, calculation, evaluationContext));

    map.put(7, new SimpleResult(true, calculation, evaluationContext));

    map.put(8, new SimpleResult(false, calculation, evaluationContext));

    return map;
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("breastfeedingAndPregnantCalculation-dataset.xml");
  }

  /*
   *
   * Last VL Date: 2019-01-21
   *
   * Date marked as breastFeeding : 2018-01-21
   *
   *
   * rule tested: org.openmrs.module.eptsreports.reporting.calculation.pvls.
   * BreastfeedingCalculation.isLactating(Date, List<Obs>)
   */

  public void shouldEvaluatePatientMarkedAsLactatinng() {

    setEvaluationContext(testsHelper.getDate("2019-01-30 00:00:00.0"));

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());

    evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
    Assert.assertEquals(true, evaluatedResult.get(501).getValue());

    matchOtherResultsExcept(evaluatedResult, 7, 8);
  }

  /*
   * rule tested : org.openmrs.module.eptsreports.reporting.calculation.pvls.
   * BreastfeedingCalculation.hasHIVStartDate(Date, List<Obs>)
   */
  @Test
  public void shouldEvaluatePatientMarkedAsLactatingByHIVStartDate() {

    setEvaluationContext(testsHelper.getDate("2015-06-30 00:00:00.0"));

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());

    evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
    Assert.assertEquals(true, evaluatedResult.get(7).getValue());
    matchOtherResultsExcept(evaluatedResult, 501, 8);
  }
}
