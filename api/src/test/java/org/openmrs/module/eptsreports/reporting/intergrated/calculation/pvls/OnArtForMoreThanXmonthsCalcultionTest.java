package org.openmrs.module.eptsreports.reporting.intergrated.calculation.pvls;

import java.util.Arrays;
import java.util.Collection;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.OnArtForMoreThanXmonthsCalcultion;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

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
}
