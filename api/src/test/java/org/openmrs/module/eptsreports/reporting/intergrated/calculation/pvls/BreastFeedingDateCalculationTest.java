package org.openmrs.module.eptsreports.reporting.intergrated.calculation.pvls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.BreastfeedingDateCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

@Ignore
public class BreastFeedingDateCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(BreastfeedingDateCalculation.class).get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {7, 90, 91});
  }

  @Override
  public CalculationResultMap getResult() {

    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();

    PatientCalculationContext evaluationContext = getEvaluationContext();
    evaluationContext.setNow(testsHelper.getDate("2019-02-28 00:00:00.0"));

    map.put(
        7,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2018-06-20 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    map.put(
        91,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2018-07-01 00:00:00.0").getTime()),
            calculation,
            evaluationContext));

    map.put(
        90,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2018-08-30 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    return map;
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("metadata.xml");
    executeDataSet("pvlsTest.xml");
  }

  @Test
  public void evaluateShouldGetAtLeastOnePatientInBreastFeedingDate() throws ParseException {

    List<Integer> patientsIDs = Arrays.asList(7);

    CalculationResultMap calculationResultMap =
        service.evaluate(patientsIDs, this.getCalculation(), getEvaluationContext());

    SimpleResult result = (SimpleResult) calculationResultMap.get(7);

    assertNotNull(result);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    Date expeted = sdf.parse("2018-06-20 00:00:00");
    Date resultDate = (Date) result.getValue();
    assertEquals(expeted, resultDate);
  }

  @Test
  public void evaluateShouldNotGetAnyDateForBreastFeedingDate() {

    List<Integer> patientsIDs = Arrays.asList(501);

    CalculationResultMap calculationResultMap =
        service.evaluate(patientsIDs, this.getCalculation(), getEvaluationContext());

    SimpleResult result = (SimpleResult) calculationResultMap.get(501);

    assertNotNull(result);
    assertNull(result.getValue());
  }

  @Test(expected = Exception.class)
  public void evaluateShouldFailWithNullListOfPatientIds() {

    List<Integer> patientsIDs = null;

    CalculationResultMap calculationResultMap =
        service.evaluate(patientsIDs, this.getCalculation(), getEvaluationContext());

    SimpleResult result = (SimpleResult) calculationResultMap.get(7);

    assertNotNull(result);
    assertNull(result.getValue());
  }
}
