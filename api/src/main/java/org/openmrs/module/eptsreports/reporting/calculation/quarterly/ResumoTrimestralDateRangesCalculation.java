package org.openmrs.module.eptsreports.reporting.calculation.quarterly;

import java.util.Date;
import java.util.Map;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

public abstract class ResumoTrimestralDateRangesCalculation extends BaseFghCalculation {

  public static Integer ALL_EXCUTIONS_PERIOD = 1;
  public static Integer MONTHLY_EXCUTION_PERIOD = 2;

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    Date startDate = (Date) context.getParameterValues().get("startDate");
    Date endDate = (Date) context.getParameterValues().get("endDate");

    resultMap.put(
        ALL_EXCUTIONS_PERIOD,
        new SimpleResult(ResumoTrimestralUtil.getDisaggregatedDates(startDate, endDate), this));
    return resultMap;
  }
}
