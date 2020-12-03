package org.openmrs.module.eptsreports.reporting.intergrated.calculation.generic;

import java.util.*;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.AgeOnPreArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class AgeOnPreArtStartDateCalculationTest extends BasePatientCalculationTest {

  @Before
  public void init() throws Exception {
    executeDataSet("ageOnPreARTStartDateCalculation.xml");
  }

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(AgeOnPreArtStartDateCalculation.class).get(0);
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
  public void evaluateShouldBeInBirthDateAndPreArtStartedDate() {

    PatientCalculationContext context = getEvaluationContext();
    Calendar startDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    Calendar endDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    startDate.set(2007, Calendar.JULY, 1);
    endDate.set(2008, Calendar.JULY, 1);

    context.addToCache("onOrAfter", startDate.getTime());
    context.addToCache("onOrBefore", endDate.getTime());

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("minAge", 0);
    parameterValues.put("maxAge", 14);
    final int patientId = 1001;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertNotNull(result);
    Assert.assertEquals(Boolean.TRUE, result.getValue());
  }
}
