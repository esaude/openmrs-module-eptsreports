package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.LastFilaProcessor;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class LastFilaCalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> parameterValues, EvaluationContext context) {

    Map<Integer, Date> lastFilaProcessorResults =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getLastLevantamentoOnFila(context);

    CalculationResultMap resultMap = new CalculationResultMap();
    for (Integer patientId : cohort) {
      if (lastFilaProcessorResults.get(patientId) != null) {
        resultMap.put(patientId, new SimpleResult(lastFilaProcessorResults.get(patientId), this));
      }
    }
    return resultMap;
  }
}
