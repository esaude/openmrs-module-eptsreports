package org.openmrs.module.eptsreports.reporting.intergrated.calculation.cxcascrn;

import java.util.*;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.cxcascrn.CXCASCRNBBCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class CXCASCRNCalculationBBTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(CXCASCRNBBCalculation.class).get(0);
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
  public void setup() throws Exception {
    executeDataSet("cxcascrnBBDataset.xml");
  }

  @Test
  public void evaluate_ShouldGetPatientBetweenAA3AndBB() {

    PatientCalculationContext context = getEvaluationContext();

    Calendar endDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    Calendar startDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    endDate.set(2020, Calendar.JANUARY, 20);
    startDate.set(2019, Calendar.OCTOBER, 21);

    context.addToCache("onOrBefore", endDate.getTime());
    context.addToCache("onOrAfter", startDate.getTime());

    Map<String, Object> parameterValues = new HashMap<>();

    final int patientId = 1001;

    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);

    SimpleResult result = (SimpleResult) results.get(patientId);

    Assert.assertNotNull(result);
  }
}
