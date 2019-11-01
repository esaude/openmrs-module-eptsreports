package org.openmrs.module.eptsreports.reporting.intergrated.calculation.txnew;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.txnew.TxNewBreastfeedingDateCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class TxNewBreastfeedingDateCalculationTest extends BasePatientCalculationTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();
    executeDataSet("txNewBreastfeedingDateCalculationTest.xml");
  }

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(TxNewBreastfeedingDateCalculation.class).get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(10001, 10002);
  }

  @Override
  public CalculationResultMap getResult() {
    Date onOrAfter = testsHelper.getDate("2018-07-01 00:00:00.0");
    Date onOrBefore = testsHelper.getDate("2018-09-01 00:00:00.0");
    PatientCalculationContext evaluationContext = getEvaluationContext();
    evaluationContext.addToCache("onOrAfter", onOrAfter);
    evaluationContext.addToCache("onOrBefore", onOrBefore);

    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();

    // Patient with breastfeeding=yes in mastercard as most recent
    map.put(
        10001,
        new SimpleResult(
            testsHelper.getDate("2018-08-10 00:00:00.0"), calculation, evaluationContext));

    // Patient with delivery date in adult initial/followup as most recent
    map.put(
        10002,
        new SimpleResult(
            testsHelper.getDate("2018-08-10 00:00:00.0"), calculation, evaluationContext));

    return map;
  }
}
