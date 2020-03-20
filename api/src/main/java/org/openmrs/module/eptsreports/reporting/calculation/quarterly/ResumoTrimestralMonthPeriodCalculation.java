package org.openmrs.module.eptsreports.reporting.calculation.quarterly;

import java.util.Map;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange.Month;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

public abstract class ResumoTrimestralMonthPeriodCalculation
    extends ResumoTrimestralDateRangesCalculation {

  @SuppressWarnings("unchecked")
  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    CalculationResultMap resultMap = new CalculationResultMap();

    CalculationResultMap evaluated = super.evaluate(parameterValues, context);
    Map<Month, MonthlyDateRange> mapRangesByMonth =
        (Map<Month, MonthlyDateRange>) evaluated.get(ALL_EXCUTIONS_PERIOD).getValue();

    resultMap.put(
        MONTHLY_EXCUTION_PERIOD,
        new SimpleResult(getMonthlExecutionPeriod(mapRangesByMonth), this));
    return resultMap;
  }

  public abstract MonthlyDateRange getMonthlExecutionPeriod(
      Map<Month, MonthlyDateRange> mapRangesByMonth);
}
