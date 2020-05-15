package org.openmrs.module.eptsreports.reporting.calculation.quarterly.indicator;

import java.util.List;
import java.util.Map;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralMonthPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.query.ResumoTrimestralQueries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

public abstract class ResumoTrimestralIndicatorCalculationB
    extends ResumoTrimestralMonthPeriodCalculation {

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    CalculationResultMap evaluated = super.evaluate(parameterValues, context);
    MonthlyDateRange monthlExecutionPeriod =
        (MonthlyDateRange) evaluated.get(MONTHLY_EXCUTION_PERIOD).getValue();

    if (monthlExecutionPeriod != null) {

      List<Integer> patientIds =
          ResumoTrimestralQueries.findPatientsWhoAreNewlyEnrolledOnART(
              context, monthlExecutionPeriod);

      List<Integer> inclusions =
          ResumoTrimestralQueries.findPatientsWhoWhereMarkedAsTransferreInByMinTransferredDate(
              context, monthlExecutionPeriod);

      patientIds.retainAll(inclusions);

      patientIds.retainAll(ResumoTrimestralQueries.getBaseCohort(context, monthlExecutionPeriod));

      for (Integer patientId : patientIds) {
        resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
      }
    }
    return resultMap;
  }
}
