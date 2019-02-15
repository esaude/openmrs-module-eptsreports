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
import org.openmrs.module.eptsreports.reporting.calculation.pvls.PregnantCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class PregnantCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {

    return new PregnantCalculation();
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

    map.put(8, new SimpleResult(false, calculation, evaluationContext));

    return map;
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("breastfeedingAndPregnantCalculation-dataset.xml");
  }

  /*
   * Patients that are female and enrolled on PTV/ETC program during the
   * period range
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
   * Patients that are female and were marked as “PREGNANT” in the initial
   * consultation or follow-up consultation during the period range
   */
  @Test
  public void shouldEvaluateOnePatientMarkedAsPregrantByAdultFollowup() {

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());

    Assert.assertEquals(true, evaluatedResult.get(7).getValue());

    matchOtherResultsExcept(evaluatedResult, 8, 501);
  }
}
