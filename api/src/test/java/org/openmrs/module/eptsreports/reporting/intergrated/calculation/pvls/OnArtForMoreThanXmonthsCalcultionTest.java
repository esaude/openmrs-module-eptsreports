package org.openmrs.module.eptsreports.reporting.intergrated.calculation.pvls;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.OnArtForMoreThanXmonthsCalcultion;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

@Ignore
public class OnArtForMoreThanXmonthsCalcultionTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(OnArtForMoreThanXmonthsCalcultion.class).get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {2, 6, 7, 8, 999, 432});
  }

  @Override
  public CalculationResultMap getResult() {
    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();

    // initiated ART on 2008-08-01 by hiv enrolment and received and vl result on
    // 2018-12-12
    PatientCalculationContext evaluationContext = getEvaluationContext();
    map.put(2, new SimpleResult(true, calculation, evaluationContext));
    // initiated ART on 2018-06-21 by starting ARV plan observation and vl result on
    // 2019-04-02
    map.put(6, new SimpleResult(false, calculation, evaluationContext));
    // initiated ART on 2019-01-18 by historical start date observation but not with
    // any vl result
    map.put(7, new SimpleResult(false, calculation, evaluationContext));
    // initiated ART on 2019-01-21 by first phamarcy encounter observation but not
    // with any vl result
    map.put(8, new SimpleResult(false, calculation, evaluationContext));
    // initiated ART on 2019-01-20 by ARV transfer in observation but last vl was in
    // last 3 months on 2018-12-12
    map.put(999, new SimpleResult(false, calculation, evaluationContext));
    // not initiated on ART but last vl is existing on 2018-12-12
    map.put(432, new SimpleResult(false, calculation, evaluationContext));

    return map;
  }

  @Test
  public void evaluateShouldCalculateLastVlBeingUsedWhenThereAreMoreThanOne() {
    // add next vl after 3 months on ART in addition to previous one before 3 months
    openmrsTestHelper.createBasicObs(
        Context.getPatientService().getPatient(999),
        Context.getConceptService().getConcept(7777001),
        Context.getEncounterService().getEncounter(2777003),
        testsHelper.getDate("2019-05-10 00:00:00.0"),
        (Location) getEvaluationContext().getFromCache("location"),
        140.0);
    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
    Assert.assertEquals(false, evaluatedResult.get(999).getValue());
    // the rest should not be touched
    matchOtherResultsExcept(evaluatedResult, 999);

    // add initiation by historical start date in addition to HIV enrollment on
    // lacking 432 at-least 3 months earlier
    openmrsTestHelper.createBasicObs(
        Context.getPatientService().getPatient(432),
        Context.getConceptService().getConcept(7777002),
        Context.getEncounterService().getEncounter(3),
        testsHelper.getDate("2018-08-12 00:00:00.0"),
        (Location) getEvaluationContext().getFromCache("location"),
        Context.getConceptService().getConcept(7777004));
    evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
    Assert.assertEquals(false, evaluatedResult.get(432).getValue());
    // the rest should not be touched
    matchOtherResultsExcept(evaluatedResult, 999, 432);
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("pvlsTest.xml");
  }
}
