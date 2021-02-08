package org.openmrs.module.eptsreports.reporting.calculation.rtt;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.LastFilaCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.LastRecepcaoLevantamentoCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.LastSeguimentoCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.OnArtInitiatedArvDrugsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.TxRttNextFilaUntilEndDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.TxRttNextSeguimentoUntilEndDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.CalculationProcessorUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsDateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class TxRTTPatientsWhoExperiencedIITCalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    CalculationResultMap resultMap = new CalculationResultMap();

    Date startDate = (Date) context.getParameterValues().get("startDate");

    CalculationResultMap inicioRealResult =
        Context.getRegisteredComponents(OnArtInitiatedArvDrugsCalculation.class)
            .get(0)
            .evaluate(parameterValues, context);

    // TODO: Este codigo vai entrar na release de Marco 2021
    // QueryDisaggregationProcessor queryDisaggregation =
    // Context.getRegisteredComponents(QueryDisaggregationProcessor.class).get(0);
    //
    // Map<Integer, Date> transferredInToInclude =
    // queryDisaggregation.findTransferredInPatientsUntilRerportEndingDate(context);
    //
    // for (Entry<Integer, Date> entry : transferredInToInclude.entrySet()) {
    // CalculationResult calculationResult = inicioRealResult.get(entry.getKey());
    // if (calculationResult != null && calculationResult.getValue() != null) {
    // Date inicioDate = (Date) calculationResult.getValue();
    // if (inicioDate != null) {
    // if (entry.getValue().compareTo(inicioDate) >= 0) {
    // inicioRealResult.put(entry.getKey(), new SimpleResult(entry.getValue(),
    // this));
    // }
    // // TODO: vai entrar na proxima release
    // // if (entry.getValue().compareTo(startDate) > 0) {
    // // inicioRealResult.remove(entry.getKey());
    // // }
    // }
    // }
    // }

    Set<Integer> cohort = inicioRealResult.keySet();
    CalculationResultMap lastFilaCalculationResult =
        Context.getRegisteredComponents(LastFilaCalculation.class)
            .get(0)
            .evaluate(cohort, parameterValues, context);
    CalculationResultMap lastSeguimentoCalculationResult =
        Context.getRegisteredComponents(LastSeguimentoCalculation.class)
            .get(0)
            .evaluate(cohort, parameterValues, context);
    LastRecepcaoLevantamentoCalculation lastRecepcaoLevantamentoCalculation =
        Context.getRegisteredComponents(LastRecepcaoLevantamentoCalculation.class).get(0);
    CalculationResultMap lastRecepcaoLevantamentoResult =
        lastRecepcaoLevantamentoCalculation.evaluate(cohort, parameterValues, context);

    CalculationResultMap nextFilaResult =
        Context.getRegisteredComponents(TxRttNextFilaUntilEndDateCalculation.class)
            .get(0)
            .evaluate(lastFilaCalculationResult.keySet(), parameterValues, context);
    CalculationResultMap nextSeguimentoResult =
        Context.getRegisteredComponents(TxRttNextSeguimentoUntilEndDateCalculation.class)
            .get(0)
            .evaluate(lastSeguimentoCalculationResult.keySet(), parameterValues, context);

    return this.evaluateUsingCalculationRules(
        cohort,
        startDate,
        resultMap,
        lastFilaCalculationResult,
        lastSeguimentoCalculationResult,
        nextFilaResult,
        nextSeguimentoResult,
        lastRecepcaoLevantamentoResult,
        lastRecepcaoLevantamentoCalculation);
  }

  protected CalculationResultMap evaluateUsingCalculationRules(
      Set<Integer> cohort,
      Date startDate,
      CalculationResultMap resultMap,
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
              TxMLPatientCalculation.getLastRecepcaoLevantamentoPlus30(
                  patientId, lastRecepcaoLevantamentoResult, lastRecepcaoLevantamentoCalculation));

      if (maxNextDate != null) {
        Date nextDatePlus28 = CalculationProcessorUtils.adjustDaysInDate(maxNextDate, 28);

        if (nextDatePlus28.compareTo(startDate) < 0) {
          resultMap.put(patientId, new SimpleResult(nextDatePlus28, this));
        }
      } else {
        this.checkConsultationsOrFilaWithoutNextConsultationDate(
            patientId, resultMap, startDate, lastFilaCalculationResult, nextFilaResult);
        this.checkConsultationsOrFilaWithoutNextConsultationDate(
            patientId, resultMap, startDate, lastSeguimentoCalculationResult, nextSeguimentoResult);
      }
    }

    //      if (maxNextDate == null) {
    //        resultMap.put(patientId, new SimpleResult(null, this));
    //      } else {
    //
    //        Date nextDatePlus28 = CalculationProcessorUtils.adjustDaysInDate(maxNextDate, 28);
    //
    //        if (nextDatePlus28.compareTo(startDate) < 0) {
    //          resultMap.put(patientId, new SimpleResult(nextDatePlus28, this));
    //        }
    //      }
    //    }
    //
    return resultMap;
  }

  protected void checkConsultationsOrFilaWithoutNextConsultationDate(
      Integer patientId,
      CalculationResultMap resultMap,
      Date startDate,
      CalculationResultMap lastResult,
      CalculationResultMap nextResult) {

    CalculationResult calculationLastResult = lastResult.get(patientId);
    CalculationResult calculationNextResult = nextResult.get(patientId);

    if (calculationNextResult != null && calculationNextResult.getValue() == null) {
      if (calculationLastResult != null) {
        Date lastDate = (Date) calculationLastResult.getValue();
        if (EptsDateUtil.getDaysBetween(lastDate, startDate) < 0) {
          resultMap.put(patientId, new SimpleResult(lastDate, this));
        }
      }
    }
  }
}
