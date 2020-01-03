package org.openmrs.module.eptsreports.reporting.unit.calculation.txml;

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
import org.openmrs.module.eptsreports.reporting.calculation.txml.StartedArtOnLastClinicalContactCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class StartedArtOnLastClinicalContactCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(StartedArtOnLastClinicalContactCalculation.class).get(0);
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

  @Before
  public void initialise() throws Exception {
    executeDataSet("genericTest.xml");
  }

  @Test
  public void evaluateShouldReturnPatientsOnArtForLessThan90Days() {
    Map<String, Object> parameterValues = new HashMap<String, Object>();
    PatientCalculationContext context = getEvaluationContext();
    Calendar calendar = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    calendar.set(2019, Calendar.OCTOBER, 20);
    context.addToCache("onOrBefore", calendar.getTime());
    final int patientId = 1777001;
    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);
    BooleanResult result = (BooleanResult) results.get(patientId);
    Assert.assertNotNull(result);
    Assert.assertEquals(Boolean.TRUE, result.getValue());
  }
}
