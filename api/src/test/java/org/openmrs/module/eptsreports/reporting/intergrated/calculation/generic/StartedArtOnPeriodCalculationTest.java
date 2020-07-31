package org.openmrs.module.eptsreports.reporting.intergrated.calculation.generic;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.StartedArtOnPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class StartedArtOnPeriodCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(StartedArtOnPeriodCalculation.class).get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {});
  }

  @Override
  public CalculationResultMap getResult() {
    CalculationResultMap map = new CalculationResultMap();
    return map;
  }

  @Test
  public void evaluateShouldBeTrueIfPatientStartedOnFirstDayOfCohortPeriod() {
    Map<String, Object> parameterValues = new HashMap<String, Object>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    calendar.set(2008, Calendar.AUGUST, 1);
    context.addToCache("onOrAfter", calendar.getTime());
    calendar.set(2008, Calendar.AUGUST, 31);
    context.addToCache("onOrBefore", calendar.getTime());
    final int patientId = 1777001;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertNotNull(result);
    Assert.assertEquals(Boolean.TRUE, result.getValue());
  }

  @Test
  public void evaluateShouldBeFalseIfPatientStartedOnBeforeFirstDayOfCohortPeriod() {
    Map<String, Object> parameterValues = new HashMap<String, Object>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    calendar.set(2008, Calendar.AUGUST, 2);
    context.addToCache("onOrAfter", calendar.getTime());
    calendar.set(2008, Calendar.AUGUST, 31);
    context.addToCache("onOrBefore", calendar.getTime());
    final int patientId = 1777001;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertNotNull(result);
    Assert.assertEquals(Boolean.FALSE, result.getValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void evaluateShouldRaiseExceptionIfPeriodStartDateIsNotProvided() {
    Map<String, Object> parameterValues = new HashMap<String, Object>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    calendar.set(2008, Calendar.AUGUST, 31);
    context.addToCache("onOrBefore", calendar.getTime());
    final int patientId = 1777001;
    service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
  }

  @Test(expected = IllegalArgumentException.class)
  public void evaluateShouldRaiseExceptionIfPeriodEndDateIsNotProvided() {
    Map<String, Object> parameterValues = new HashMap<String, Object>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    calendar.set(2008, Calendar.AUGUST, 1);
    context.addToCache("onOrAfter", calendar.getTime());
    context.removeFromCache("onOrBefore");
    final int patientId = 1777001;
    service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("genericTest.xml");
  }

  @Test
  public void evaluateShouldBeNullIfPatientDidNotStartArt() {
    Map<String, Object> parameterValues = new HashMap<String, Object>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    calendar.set(2008, Calendar.AUGUST, 1);
    context.addToCache("onOrAfter", calendar.getTime());
    calendar.set(2008, Calendar.AUGUST, 31);
    context.addToCache("onOrBefore", calendar.getTime());
    final int patientId = 99999;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertNull(result);
  }
}
