package org.openmrs.module.eptsreports.reporting.intergrated.calculation.pvls;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.CalculationContext;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.RoutineCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.PatientsOnRoutineEnum;

public class RoutineCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(RoutineCalculation.class).get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {13, 6, 7, 8, 999, 432});
  }

  @Override
  public CalculationResultMap getResult() {
    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();

    CalculationContext evaluationContext = getEvaluationContext();
    // initiated ART on 2008-08-01 by hiv enrolment and received and vl
    // result on
    // 2018-12-12
    map.put(13, new SimpleResult(false, calculation, evaluationContext));
    // initiated ART on 2018-06-21 by starting ARV plan observation and vl
    // result on
    // 2019-04-02
    map.put(6, new SimpleResult(false, calculation, evaluationContext));
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
  public void initialise() throws Exception {
    executeDataSet("pvlsTest.xml");
    // initially test with routine criteria1 > 6 && >= 9 months between ART
    // init & VL date
    params.put("criteria", PatientsOnRoutineEnum.ADULTCHILDREN);
  }

  @Test
  public void calculateRoutineCriteria1WhenBreastFeedingShouldMatchBetween3To6Months() {
    // test with routine criteria1 > 3 && >= 6 months between ART
    // init & VL date
    params.put("criteria", PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT);
    Obs obs = Context.getObsService().getObs(3777002);
    Assert.assertEquals(testsHelper.getDate("2018-06-21 00:00:00.0"), obs.getObsDatetime());
    // session gets auto updated without saving
    obs.setObsDatetime(testsHelper.getDate("2018-10-21 00:00:00.0"));
    Assert.assertEquals(
        getResult().toString(),
        service.evaluate(getCohort(), getCalculation(), params, getEvaluationContext()).toString());
  }

  @Test
  public void
      calculateRoutineCriteria2WhenAdultsAndChildrenShouldMatchBetween12To15MonthsAndVlOfLessThan1000Copies() {
    List<Integer> cohort = Arrays.asList(10);
    PatientCalculationContext evaluationContext = getEvaluationContext();
    params.put("criteria", PatientsOnRoutineEnum.ADULTCHILDREN);
    Assert.assertTrue(
        service.evaluate(cohort, getCalculation(), params, evaluationContext).getAsBoolean(10));
  }

  //  @Test
  //  public void
  //      calculateRoutineCriteria2WhenBreastfeedingAndPregnantShouldMatchBetween12To15Months() {
  //    List<Integer> cohort = Arrays.asList(11);
  //    PatientCalculationContext evaluationContext = getEvaluationContext();
  //    params.put("criteria", PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT);
  //    Assert.assertTrue(
  //        service.evaluate(cohort, getCalculation(), params, evaluationContext).getAsBoolean(11));
  //  }
}
