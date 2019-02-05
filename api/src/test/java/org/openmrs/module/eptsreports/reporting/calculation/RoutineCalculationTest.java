package org.openmrs.module.eptsreports.reporting.calculation;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.RoutineCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.PatientsOnRoutineEnum;

public class RoutineCalculationTest extends BasePatientCalculationTest {

  @Override
  PatientCalculation getCalculation() {
    return new RoutineCalculation();
  }

  @Override
  Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {2, 6, 7, 8, 999, 432});
  }

  @Override
  CalculationResultMap getResult() {
    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();

    // initiated ART on 2008-08-01 by hiv enrolment and received and vl
    // result on
    // 2018-12-12
    map.put(2, new SimpleResult(false, calculation, evaluationContext));
    // initiated ART on 2018-06-21 by starting ARV plan observation and vl
    // result on
    // 2019-04-02
    map.put(6, new SimpleResult(true, calculation, evaluationContext));
    // initiated ART on 2019-01-18 by historical start date observation but
    // not with
    // any vl result
    map.put(7, new SimpleResult(false, calculation, evaluationContext));
    // initiated ART on 2019-01-21 by first phamarcy encounter observation
    // but not
    // with any vl result
    map.put(8, new SimpleResult(false, calculation, evaluationContext));
    // initiated ART on 2019-01-20 by ARV transfer in observation but last
    // vl was in
    // last 3 months on 2018-12-12
    map.put(999, new SimpleResult(false, calculation, evaluationContext));
    // not initiated on ART but last vl is existing on 2018-12-12
    map.put(432, new SimpleResult(false, calculation, evaluationContext));

    return map;
  }

  @Before
  public void initialise() {
    // initially test with routine criteria1 > 6 && >= 9 months between ART
    // init & VL date
    params.put("criteria", PatientsOnRoutineEnum.ADULTCHILDREN);
  }

  @Test
  public void calculate_routineCriteria1WhenBreastFeeding_shouldMatchBetween3To6Months() {
    // test with routine criteria1 > 3 && >= 6 months between ART
    // init & VL date
    params.put("criteria", PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT);
    Obs obs = Context.getObsService().getObs(3777002);
    Assert.assertEquals(
        calculationsTestsCache.getDate("2018-06-21 00:00:00.0"), obs.getObsDatetime());
    // session gets auto updated without saving
    obs.setObsDatetime(calculationsTestsCache.getDate("2018-10-21 00:00:00.0"));
    Assert.assertEquals(
        getResult().toString(),
        service.evaluate(getCohort(), getCalculation(), params, getEvaluationContext()).toString());
  }

  @Ignore
  // TODO investigate.i hope routine criteria 2 is alright, i find it
  // strange; being on ART for atleast 12 months with 2 VL with
  // at-least 12 months away from each other when lookup is
  // constrained to only 12 months from context now!
  public void calculate_routineCriteria2() {
    // test with routine criteria2 > 12 && <= 16 months
    // change now from: 2019-05-30 00:00:00.0, needs to be on ART for
    // atleast 3 months
    setEvaluationContext(calculationsTestsCache.getDate("2019-01-01 00:00:00.0"));
    // initiate ART
    Obs obsInitArt = Context.getObsService().getObs(3777002);
    Assert.assertEquals(
        calculationsTestsCache.getDate("2018-06-21 00:00:00.0"), obsInitArt.getObsDatetime());
    obsInitArt.setObsDatetime(calculationsTestsCache.getDate("2018-01-01 00:00:00.0"));

    Obs obs = Context.getObsService().getObs(3777008);
    Assert.assertEquals(
        calculationsTestsCache.getDate("2019-04-02 00:00:00.0"), obs.getObsDatetime());
    // first VL obs should be within 12 months backwards from now
    obs.setObsDatetime(calculationsTestsCache.getDate("2018-01-01 00:00:00.0"));

    // second VL obs should be within 12 months backwards from now but
    // atleast 12 months from previous VL
    calculationsTestsCache.createBasicObs(
        Context.getPatientService().getPatient(6),
        Context.getConceptService().getConcept(7777001),
        Context.getEncounterService().getEncounter(2777006),
        calculationsTestsCache.getDate("2019-01-01 00:00:00.0"),
        (Location) getEvaluationContext().getFromCache("location"),
        132.3);
    Assert.assertEquals(
        getResult().toString(),
        service.evaluate(getCohort(), getCalculation(), params, getEvaluationContext()).toString());

    // TODO also test BreastFeeding criteria in here
  }

  @Ignore
  // same issue as on above test case
  public void calculate_routineCriteria3() {}
}
