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
import org.openmrs.module.eptsreports.reporting.calculation.generic.StartedArtBeforeDateCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class StartedArtBeforeDateCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(StartedArtBeforeDateCalculation.class).get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {});
  }

  @Override
  public CalculationResultMap getResult() {
    return new CalculationResultMap();
  }

  @Test
  public void evaluateShouldBeTrueIfStartDateIsTheSameAsBeforeDate() {
    Map<String, Object> parameterValues = new HashMap<>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    calendar.set(2008, Calendar.AUGUST, 1);
    context.addToCache("onOrBefore", calendar.getTime());
    final int patientId = 1777001;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertNotNull(result);
    Assert.assertEquals(Boolean.TRUE, result.getValue());
  }

  @Test
  public void evaluateShouldBeNullIfStartDateOneDayAfterBeforeDate() {
    Map<String, Object> parameterValues = new HashMap<>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    calendar.set(2008, Calendar.JULY, 31);
    context.addToCache("onOrBefore", calendar.getTime());
    final int patientId = 1777001;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertNull(result);
  }

  @Test
  public void evaluateShouldBeNullIfThereIsNoStartDate() {
    Map<String, Object> parameterValues = new HashMap<>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    calendar.set(2008, Calendar.JULY, 31);
    context.addToCache("onOrBefore", calendar.getTime());
    final int patientId = 1777006;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertNull(result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldRaiseExceptionIfBeforeDateIsNotSpecified() {
    Map<String, Object> parameterValues = new HashMap<>();
    PatientCalculationContext context = getEvaluationContext();
    context.removeFromCache("onOrBefore");
    final int patientId = 1777006;
    service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("genericTest.xml");
  }
}
