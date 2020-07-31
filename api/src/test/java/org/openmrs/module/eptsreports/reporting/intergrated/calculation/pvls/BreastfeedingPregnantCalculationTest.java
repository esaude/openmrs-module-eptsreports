package org.openmrs.module.eptsreports.reporting.intergrated.calculation.pvls;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.BreastfeedingPregnantCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.PregnantOrBreastfeedingWomen;

@Ignore
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
    params.put("state", PregnantOrBreastfeedingWomen.PREGNANTWOMEN);
  }

  @Test
  public void evaluateShouldReturnPregnantIfMostRecent() {
    setEvaluationContext(testsHelper.getDate("2018-09-20 00:00:00.0"));
    params.put("state", PregnantOrBreastfeedingWomen.PREGNANTWOMEN);

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), params, getEvaluationContext());

    Assert.assertEquals(true, evaluatedResult.get(501).getValue());

    matchOtherResultsExcept(evaluatedResult, 8, 7);
  }

  @Test
  public void evaluateShouldReturnBreastfeedingIfMostRecent() {
    HashMap<String, Object> cacheEntries = new HashMap<>();
    cacheEntries.put("onOrBefore", testsHelper.getDate("2018-09-20 00:00:00.0"));
    setEvaluationContext(cacheEntries);
    params.put("state", PregnantOrBreastfeedingWomen.BREASTFEEDINGWOMEN);

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), params, getEvaluationContext());

    Assert.assertEquals(true, evaluatedResult.get(7).getValue());

    matchOtherResultsExcept(evaluatedResult, 8, 501);
  }
}
