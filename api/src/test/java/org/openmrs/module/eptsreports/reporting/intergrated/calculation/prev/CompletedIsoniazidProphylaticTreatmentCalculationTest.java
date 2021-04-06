package org.openmrs.module.eptsreports.reporting.intergrated.calculation.prev;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.prev.CompletedIsoniazidProphylaticTreatmentCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class CompletedIsoniazidProphylaticTreatmentCalculationTest
    extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(CompletedIsoniazidProphylaticTreatmentCalculation.class)
        .get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {});
  }

  @Override
  public CalculationResultMap getResult() {
    return new CalculationResultMap();
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("prevTest.xml");
  }

  @Ignore
  @Test
  public void evaluateShouldBeTrueIfTreatmentWasWithinPeriodWithEnoughDuration() {
    Map<String, Object> parameterValues = new HashMap<>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    calendar.set(2018, Calendar.JANUARY, 1);
    context.addToCache("onOrAfter", calendar.getTime());
    calendar.set(2019, Calendar.JULY, 2);
    context.addToCache("onOrBefore", calendar.getTime());

    final int patientId = 92;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertNotNull(result);
    Assert.assertEquals(Boolean.TRUE, result.getValue());
  }

  @Ignore
  @Test
  public void evaluateShouldBeNullIfStartDateIsOutsideThePeriod() {
    Map<String, Object> parameterValues = new HashMap<>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    calendar.set(2018, Calendar.JULY, 2);
    context.addToCache("onOrAfter", calendar.getTime());
    calendar.set(2018, Calendar.JULY, 2);
    context.addToCache("onOrBefore", calendar.getTime());

    final int patientId = 90;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertNull(result);
  }

  @Ignore
  @Test
  public void evaluateShouldBeNullIfEndDateIsOutsidePeriod() {
    Map<String, Object> parameterValues = new HashMap<>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    calendar.set(2018, Calendar.JULY, 1);
    context.addToCache("onOrAfter", calendar.getTime());
    context.addToCache("onOrBefore", calendar.getTime());

    final int patientId = 90;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertNull(result);
  }

  @Ignore
  @Test
  public void evaluateShouldBeTrueIfDoesNotHaveEndDateButAnsweredYesEnoughTimes() {
    Map<String, Object> parameterValues = new HashMap<>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    calendar.set(2018, Calendar.JULY, 1);
    context.addToCache("onOrAfter", calendar.getTime());
    context.addToCache("onOrBefore", calendar.getTime());

    final int patientId = 91;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertEquals(Boolean.TRUE, result.getValue());
  }
}
