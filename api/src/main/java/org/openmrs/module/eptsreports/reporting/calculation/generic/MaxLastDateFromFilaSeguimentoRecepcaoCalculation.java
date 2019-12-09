package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.CalculationProcessorUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class MaxLastDateFromFilaSeguimentoRecepcaoCalculation extends BaseFghCalculation {

  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    CalculationResultMap inicioRealResult =
        Context.getRegisteredComponents(OnArtInitiatedArvDrugsCalculation.class)
            .get(0)
            .evaluate(parameterValues, context);

    Set<Integer> cohort = inicioRealResult.keySet();

    CalculationResultMap lastFilaCalculationResult =
        Context.getRegisteredComponents(LastFilaCalculation.class)
            .get(0)
            .evaluate(cohort, parameterValues, context);

    CalculationResultMap lastSeguimentoResult =
        Context.getRegisteredComponents(LastSeguimentoCalculation.class)
            .get(0)
            .evaluate(cohort, parameterValues, context);

    CalculationResultMap lastRecepcaoLevantamentoResult =
        Context.getRegisteredComponents(LastRecepcaoLevantamentoCalculation.class)
            .get(0)
            .evaluate(cohort, parameterValues, context);

    for (Integer patientId : cohort) {
      Date maxLastDate =
          CalculationProcessorUtils.getMaxDate(
              patientId,
              lastFilaCalculationResult,
              lastSeguimentoResult,
              lastRecepcaoLevantamentoResult);
      if (maxLastDate != null) {
        resultMap.put(patientId, new SimpleResult(maxLastDate, this));
      }
    }
    return resultMap;
  }

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> parameterValues, EvaluationContext context) {
    return this.evaluate(parameterValues, context);
  }
}
