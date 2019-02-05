package org.openmrs.module.eptsreports.reporting.calculation;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.api.EptsReportsService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

// TODO probably move this into calculation module
public abstract class BasePatientCalculationTest extends BaseModuleContextSensitiveTest {

  CalculationsTestsCache calculationsTestsCache;

  PatientCalculationService service;

  PatientCalculationContext evaluationContext;

  Map<String, Object> params = new HashMap<String, Object>();

  abstract PatientCalculation getCalculation();

  abstract Collection<Integer> getCohort();

  abstract CalculationResultMap getResult();

  @Before
  public void setUp() throws Exception {
    service = Context.getService(PatientCalculationService.class);
    evaluationContext = service.createCalculationContext();
    calculationsTestsCache = new CalculationsTestsCache();

    Map<String, Object> cacheEntries = new HashMap<String, Object>();
    cacheEntries.put("location", Context.getLocationService().getLocation(1));
    setEvaluationContext(calculationsTestsCache.getDate("2019-05-30 00:00:00.0"));
    setEvaluationContext(cacheEntries);

    executeDataSet("calculationsTest.xml");

    PatientCalculation calculation = getCalculation();
    calculationsTestsCache.stubEptsCalculations(calculation);
    calculationsTestsCache.stubEptsCalculationUtils(calculation);
  }

  /** This test runs for all classes that extend this class, it's the basic calculation unit test */
  @Test
  public void evaluate_shouldReturnMatchedResultMapBySizeAndPrintOutGivenCalculationCohort() {
    Assert.assertNotNull(Context.getService(EptsReportsService.class));
    CalculationResultMap result = getResult();
    PatientCalculation calculation = getCalculation();
    Collection<Integer> cohort = getCohort();
    Assert.assertNotNull(calculation);
    Assert.assertNotNull(cohort);
    Assert.assertNotNull(result);
    CalculationResultMap evaluatedResult =
        service.evaluate(cohort, calculation, params, getEvaluationContext());
    Assert.assertEquals(result.size(), evaluatedResult.size());
    Assert.assertEquals(result.toString(), evaluatedResult.toString());
  }

  void setEvaluationContext(Date now) {
    if (now == null) {
      now = new Date();
    }
    evaluationContext.setNow(now);
  }

  @SuppressWarnings("rawtypes")
  void setEvaluationContext(Map<String, Object> cacheEntries) {
    if (cacheEntries != null && !cacheEntries.isEmpty()) {
      for (Map.Entry e : cacheEntries.entrySet()) {
        evaluationContext.addToCache((String) e.getKey(), e.getValue());
      }
    }
  }

  PatientCalculationContext getEvaluationContext() {
    return evaluationContext;
  }

  void matchOtherResultsExcept(
      CalculationResultMap evaluatedResult, Integer... patientsIdNotToMatch) {
    CalculationResultMap otherResult = (CalculationResultMap) evaluatedResult.clone();
    CalculationResultMap initialResult = (CalculationResultMap) getResult().clone();
    if (patientsIdNotToMatch != null) {
      for (int i : patientsIdNotToMatch) {
        initialResult.remove(i);
        otherResult.remove(i);
      }
    }
    Assert.assertEquals(initialResult.toString(), otherResult.toString());
  }
}
