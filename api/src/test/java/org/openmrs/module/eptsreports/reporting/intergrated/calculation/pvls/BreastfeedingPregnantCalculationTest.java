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
import org.openmrs.module.eptsreports.reporting.calculation.pvls.BreastfeedingPregnantCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class BreastfeedingPregnantCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {

    return Context.getRegisteredComponents(BreastfeedingPregnantCalculation.class).get(0);
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

    // Patient marked as Pregnant in PTV program
    map.put(501, new SimpleResult(true, calculation, evaluationContext));

    // Patient is is marked as pregnant(YES) in Adult initial Followup
    map.put(7, new SimpleResult(true, calculation, evaluationContext));

    map.put(8, new SimpleResult(true, calculation, evaluationContext));

    return map;
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("pvlsTest.xml");
  }

  /*
   * Patients that are female and enrolled on PTV/ETC program during the
   * period range
   *
   * Patient Id : 501
   *
   * End Date Period: 2019-05-30
   *
   * Latest Viral Load Date: 2018-06-01
   *
   * Patient Program Enrolled Date: 2018-05-30
   *
   * rule tested : PregnantCalculation.isPregnantInProgram(Date, SimpleResult)
   */
  @Test
  public void shouldEvaluateOnePatientMarkedAsPregnantByEnrollInPTVProgram() {

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());

    evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
    Assert.assertEquals(true, evaluatedResult.get(501).getValue());

    matchOtherResultsExcept(evaluatedResult, 7, 8);
  }

  /*
   *
   * Patients that are female and were marked as “PREGNANT” in the initial
   * consultation or follow-up consultation during the period range
   *
   * Patient Id: 7
   *
   * End Date Period: 2019-06-30
   *
   * Latest Viral Load Date: 2019-01-02
   *
   * Pregnant Date: 2018-09-21
   *
   * rule tested : PregnantCalculation.isPregnant(Date, List<Obs>)
   */
  @Test
  public void shouldEvaluateOnePatientMarkedAsPregrantByAdultFollowup() {

    setEvaluationContext(testsHelper.getDate("2019-06-30 00:00:00.0"));

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());

    Assert.assertEquals(true, evaluatedResult.get(7).getValue());

    matchOtherResultsExcept(evaluatedResult, 8, 501);
  }

  /*
   * Patients that are female and have “Number of weeks Pregnant” registered
   * in the initial or follow-up consultation during the period range
   *
   * Patient Id: 8
   *
   * End Date Period: 2019-03-01
   *
   * Day registered nr of weeks of pregnant: 2018-10-15
   *
   * Latest Viral Load Date: 2019-02-28
   *
   * rule tested : PregnantCalculation.isPregnantByWeeks(Date, List<Obs>)
   */
  @Test
  public void shoudEvaluateOnePatientMarkedAsPreagnantByWeeks() {

    setEvaluationContext(testsHelper.getDate("2019-03-01 00:00:00.0"));

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());

    Assert.assertEquals(true, evaluatedResult.get(8).getValue());

    matchOtherResultsExcept(evaluatedResult, 7, 501);
  }

  /*
   * Patient ID: 7
   *
   * rule tested: PregnantCalculation.isPregnantDueDate(Date, List<Obs>)
   */
  @Test
  public void shouldEvaluatePatientMarkedAsPregnantByDueDate() {

    setEvaluationContext(testsHelper.getDate("1999-02-01 00:00:00.0"));

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());

    Assert.assertEquals(true, evaluatedResult.get(7).getValue());

    matchOtherResultsExcept(evaluatedResult, 8, 501);
  }
}
