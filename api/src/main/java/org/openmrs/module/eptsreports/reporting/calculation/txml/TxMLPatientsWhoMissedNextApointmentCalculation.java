package org.openmrs.module.eptsreports.reporting.calculation.txml;

import java.util.Date;
import java.util.Set;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.LastRecepcaoLevantamentoCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.CalculationProcessorUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class TxMLPatientsWhoMissedNextApointmentCalculation extends TxMLPatientCalculation {

  protected CalculationResultMap evaluateUsingCalculationRules(
      EvaluationContext context,
      Set<Integer> cohort,
      Date startDate,
      Date endDate,
      CalculationResultMap resultMap,
      CalculationResultMap inicioRealResult,
      CalculationResultMap lastFilaCalculationResult,
      CalculationResultMap lastSeguimentoCalculationResult,
      CalculationResultMap nextFilaResult,
      CalculationResultMap nextSeguimentoResult,
      CalculationResultMap lastRecepcaoLevantamentoResult,
      LastRecepcaoLevantamentoCalculation lastRecepcaoLevantamentoCalculation) {

    for (Integer patientId : cohort) {

      Date maxNextDate =
          CalculationProcessorUtils.getMaxDate(
              patientId,
              nextFilaResult,
              nextSeguimentoResult,
              getLastRecepcaoLevantamentoPlus30(
                  patientId, lastRecepcaoLevantamentoResult, lastRecepcaoLevantamentoCalculation));
      if (maxNextDate != null) {
        Date nextDatePlus28 = CalculationProcessorUtils.adjustDaysInDate(maxNextDate, DAYS_TO_LTFU);
        if (nextDatePlus28.compareTo(CalculationProcessorUtils.adjustDaysInDate(startDate, -1)) >= 0
            && nextDatePlus28.compareTo(endDate) < 0) {
          resultMap.put(patientId, new SimpleResult(maxNextDate, this));
        }
      } else {
        this.checkConsultationsOrFilaWithoutNextConsultationDate(
            patientId, resultMap, endDate, lastFilaCalculationResult, nextFilaResult);
        this.checkConsultationsOrFilaWithoutNextConsultationDate(
            patientId, resultMap, endDate, lastSeguimentoCalculationResult, nextSeguimentoResult);
      }
    }
    return resultMap;
  }
}
