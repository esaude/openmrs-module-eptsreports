package org.openmrs.module.eptsreports.reporting.calculation.txml;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.LastRecepcaoLevantamentoCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.NextFilaDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.NextSeguimentoDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.OnArtInitiatedArvDrugsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.CalculationProcessorUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class TxMLPatientsWhoAreLTFUGreatherThan3MonthsCalculation extends BaseFghCalculation {

  private static int GREATHER_THAN_3_MONTHS = 90;

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    CalculationResultMap resultMap = new CalculationResultMap();

    Date startDate = (Date) context.getParameterValues().get("startDate");
    Date endDate = (Date) context.getParameterValues().get("endDate");
    CalculationResultMap inicioRealResult =
        Context.getRegisteredComponents(OnArtInitiatedArvDrugsCalculation.class)
            .get(0)
            .evaluate(parameterValues, context);

    Set<Integer> cohort = inicioRealResult.keySet();

    LastRecepcaoLevantamentoCalculation lastRecepcaoLevantamentoCalculation =
        Context.getRegisteredComponents(LastRecepcaoLevantamentoCalculation.class).get(0);
    CalculationResultMap lastRecepcaoLevantamentoResult =
        lastRecepcaoLevantamentoCalculation.evaluate(cohort, parameterValues, context);

    CalculationResultMap nextFilaResult =
        Context.getRegisteredComponents(NextFilaDateCalculation.class)
            .get(0)
            .evaluate(cohort, parameterValues, context);

    CalculationResultMap nextSeguimentoResult =
        Context.getRegisteredComponents(NextSeguimentoDateCalculation.class)
            .get(0)
            .evaluate(cohort, parameterValues, context);

    for (Integer patientId : cohort) {
      Date inicioRealDate = (Date) inicioRealResult.get(patientId).getValue();
      Date maxNextDate =
          CalculationProcessorUtils.getMaxDate(
              patientId,
              nextFilaResult,
              nextSeguimentoResult,
              TxMLPatientsWhoMissedNextApointmentCalculation.getLastRecepcaoLevantamentoPlus30(
                  patientId, lastRecepcaoLevantamentoResult, lastRecepcaoLevantamentoCalculation));
      if (maxNextDate != null
          && DateUtil.getDaysBetween(inicioRealDate, maxNextDate) >= GREATHER_THAN_3_MONTHS) {
        Date nextDatePlus28 = CalculationProcessorUtils.adjustDaysInDate(maxNextDate, 28);
        if (nextDatePlus28.compareTo(CalculationProcessorUtils.adjustDaysInDate(startDate, -1)) >= 0
            && nextDatePlus28.compareTo(endDate) < 0) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
        }
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
