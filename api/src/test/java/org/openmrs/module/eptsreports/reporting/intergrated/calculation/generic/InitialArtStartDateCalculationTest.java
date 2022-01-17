package org.openmrs.module.eptsreports.reporting.intergrated.calculation.generic;

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
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

@Ignore
public class InitialArtStartDateCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(2, 6, 7, 8, 999, 432, 1777005, 1777007);
  }

  @Override
  public CalculationResultMap getResult() {
    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();

    // initiated ART by hiv enrolment
    PatientCalculationContext evaluationContext = getEvaluationContext();
    map.put(
        2,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2008-08-01 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    // initiated ART by starting ARV plan observation
    map.put(
        6,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("1998-09-01 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    // initiated ART by historical start date observation
    map.put(7, new SimpleResult(null, calculation, evaluationContext));
    // initiated ART by first phamarcy encounter observation
    map.put(
        8,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2019-01-21 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    // initiated ART by ARV transfer in observation
    map.put(
        999,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2019-01-20 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    map.put(432, new SimpleResult(null, calculation, evaluationContext));
    // initiated ART by hiv enrolment and also has first phamarcy encounter observation, ealrier is
    // considerd
    map.put(
        1777005,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2018-01-21 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    // initiated ART by mastercard drug pickup
    map.put(
        1777007,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("1998-10-01 01:02:00.0").getTime()),
            calculation,
            evaluationContext));
    return map;
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("genericTest.xml");
  }
}
