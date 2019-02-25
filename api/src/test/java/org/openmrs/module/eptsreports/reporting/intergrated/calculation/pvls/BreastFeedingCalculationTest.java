package org.openmrs.module.eptsreports.reporting.intergrated.calculation.pvls;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.BreastfeedingCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class BreastFeedingCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(BreastfeedingCalculation.class).get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {7, 8, 501, 90, 91});
  }

  @Override
  public CalculationResultMap getResult() {

    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();

    PatientCalculationContext evaluationContext = getEvaluationContext();

    map.put(91, new SimpleResult(true, calculation, evaluationContext));

    map.put(501, new SimpleResult(true, calculation, evaluationContext));

    map.put(7, new SimpleResult(true, calculation, evaluationContext));

    map.put(8, new SimpleResult(false, calculation, evaluationContext));

    map.put(90, new SimpleResult(true, calculation, evaluationContext));
    return map;
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("pvlsTest.xml");
  }

  /*
   *
   * Last VL Date: 2019-01-21
   *
   * Date marked as breastFeeding : 2018-01-21
   *
   *
   * rule tested: BreastfeedingCalculation.isLactating(Date, List<Obs>)
   */
  @Test
  public void shouldEvaluatePatientMarkedAsBreastFeedingByLactatinng() {

    setEvaluationContext(testsHelper.getDate("2019-01-30 00:00:00.0"));

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
    Assert.assertEquals(true, evaluatedResult.get(501).getValue());
    matchOtherResultsExcept(evaluatedResult, 7, 8, 90, 91);
  }

  /*
   * rule tested : BreastfeedingCalculation.hasHIVStartDate(Date, List<Obs>)
   */
  @Test
  public void shouldEvaluatePatientMarkedAsBreastFeedingByHIVStartDate() {

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
    evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
    Assert.assertEquals(true, evaluatedResult.get(7).getValue());
    matchOtherResultsExcept(evaluatedResult, 501, 8, 90, 91);
  }

  /*
   * Patient ID : 91
   *
   * Report end Period Date:
   *
   * Deliver date:
   *
   * Last Viral load date:
   *
   * rule tested: BreastfeedingCalculation.hasDeliveryDate(Date, List<Obs>)
   */
  @Test
  public void shouldEvaluatePatientMarkedAsBreastFeedingByDeliveryDate() {

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
    Assert.assertEquals(true, evaluatedResult.get(91).getValue());
    matchOtherResultsExcept(evaluatedResult, 501, 7, 90, 8);
  }

  /*
   * Patient ID : 8
   *
   * Report end Period Date: 2025-12-30 == 2019-04-30
   *
   * Patient State Start Date: 2018-08-30
   *
   * Last VL Date: 2025-12-30
   *
   * rule tested: BreastfeedingCalculation.isBreastFeedingInProgram(Date,
   * List<PatientState>)
   */
  @Test
  public void shouldEvaluatePatientMarkedAsBreastFeedingByPatientState() {

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
    evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
    Assert.assertEquals(true, evaluatedResult.get(90).getValue());
    matchOtherResultsExcept(evaluatedResult, 501, 7, 91, 8);
  }
}
