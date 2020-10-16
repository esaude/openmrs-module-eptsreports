package org.openmrs.module.eptsreports.reporting.intergrated.calculation;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.helper.OpenMRSTestHelper;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BasePatientCalculationTest extends BaseModuleContextSensitiveTest {

  @Autowired protected TestsHelper testsHelper;

  @Autowired protected OpenMRSTestHelper openmrsTestHelper;

  protected PatientCalculationService service;

  private PatientCalculationContext evaluationContext;

  protected Map<String, Object> params = new HashMap<String, Object>();

  public abstract PatientCalculation getCalculation();

  public abstract Collection<Integer> getCohort();

  public abstract CalculationResultMap getResult();

  @Before
  public void setUp() throws Exception {
    service = Context.getService(PatientCalculationService.class);
    evaluationContext = service.createCalculationContext();

    Map<String, Object> cacheEntries = new HashMap<String, Object>();
    cacheEntries.put("location", Context.getLocationService().getLocation(1));
    cacheEntries.put("onOrBefore", testsHelper.getDate("2019-05-30 00:00:00.0"));
    setEvaluationContext(cacheEntries);

    executeDataSet("metadata.xml");
  }

  /** This test runs for all classes that extend this class, it's the basic calculation unit test */
  @Test
  public void evaluateShouldReturnMatchedResultMapGivenCalculationCohort() {
    CalculationResultMap result = getResult();
    PatientCalculation calculation = getCalculation();
    Collection<Integer> cohort = getCohort();
    Assert.assertNotNull(calculation);
    Assert.assertNotNull(cohort);
    Assert.assertNotNull(result);
    CalculationResultMap evaluatedResult =
        service.evaluate(cohort, calculation, params, getEvaluationContext());
    Assert.assertTrue(matchCalculationResults(result, evaluatedResult));
  }

  protected void setEvaluationContext(Date now) {
    evaluationContext.setNow(now == null ? new Date() : now);
  }

  @SuppressWarnings("rawtypes")
  protected void setEvaluationContext(Map<String, Object> cacheEntries) {
    if (cacheEntries != null && !cacheEntries.isEmpty()) {
      for (Map.Entry e : cacheEntries.entrySet()) {
        evaluationContext.addToCache((String) e.getKey(), e.getValue());
      }
    }
  }

  public PatientCalculationContext getEvaluationContext() {
    return evaluationContext;
  }

  protected void matchOtherResultsExcept(
      CalculationResultMap evaluatedResult, Integer... patientsIdNotToMatch) {
    CalculationResultMap otherResult = (CalculationResultMap) evaluatedResult.clone();
    CalculationResultMap initialResult = (CalculationResultMap) getResult().clone();
    if (patientsIdNotToMatch != null) {
      for (int i : patientsIdNotToMatch) {
        initialResult.remove(i);
        otherResult.remove(i);
      }
    }
    Assert.assertTrue(matchCalculationResults(initialResult, otherResult));
  }

  protected boolean matchCalculationResults(
      CalculationResultMap results1, CalculationResultMap results2) {
    boolean matched = true;
    if (results1.size() != results2.size() || !results1.keySet().equals(results2.keySet())) {
      matched = false;
    } else {
      Iterator<Integer> results1Iterator = results1.keySet().iterator();
      while (results1Iterator.hasNext()) {
        Integer key = results1Iterator.next();
        if (results1.get(key).getValue() == null) {
          matched = results2.get(key).getValue() == null;
        } else if (results2.get(key).getValue() == null) {
          matched = results1.get(key).getValue() == null;
        } else if (!results1.get(key).getValue().equals(results2.get(key).getValue())) {
          matched = false;
          break;
        }
      }
    }
    return matched;
  }
}
