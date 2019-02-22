package org.openmrs.module.eptsreports.reporting.unit.calculation;

import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class EptsCalculationsTest extends BaseModuleContextSensitiveTest {

  private List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);

  private PatientCalculationContext context;

  /** Setup each test */
  @Before
  public void setup() {
    context = Context.getService(PatientCalculationService.class).createCalculationContext();
    context.setNow(new Date());
  }
  /**
   * @see
   *     org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils#genders(java.util.Collection,
   *     org.openmrs.calculation.patient.PatientCalculationContext)
   */
  @Test
  public void genders() {
    CalculationResultMap results = EptsCalculationUtils.genders(cohort, context);

    Assert.assertThat(((String) results.get(6).getValue()), is("M"));
    Assert.assertThat(((String) results.get(7).getValue()), is("F"));
  }
}
