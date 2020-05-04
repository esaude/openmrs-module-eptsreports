package org.openmrs.module.eptsreports.reporting.intergrated.calculation.pvls;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Ignore;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.PregnantDateCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

@Ignore
public class PregnantDateCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {

    return Context.getRegisteredComponents(PregnantDateCalculation.class).get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {7, 8, 501});
  }

  @Override
  public CalculationResultMap getResult() {
    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();

    PatientCalculationContext evaluationContext = getEvaluationContext();
    evaluationContext.setNow(testsHelper.getDate("2019-03-01 00:00:00.0"));

    // PregnantCalculation.isPregnantInProgram(2018-05-30 00:00:00.0, SimpleResult)
    map.put(
        501,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2018-05-30 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    // PregnantCalculation.isPregnantByWeeks(2018-10-15 00:00:00.0, List<Obs>)

    map.put(
        8,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2018-10-15 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    // PregnantCalculation.isPregnantDueDate(2018-04-01 00:00:00.0, List<Obs>)
    map.put(
        7,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2018-09-21 00:00:00.0").getTime()),
            calculation,
            evaluationContext));

    return map;
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("pvlsTest.xml");
  }
}
