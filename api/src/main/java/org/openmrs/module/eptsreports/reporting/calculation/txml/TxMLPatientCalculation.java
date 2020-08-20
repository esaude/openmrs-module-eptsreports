package org.openmrs.module.eptsreports.reporting.calculation.txml;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.LastFilaCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.LastRecepcaoLevantamentoCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.LastSeguimentoCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.NextFilaDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.NextSeguimentoDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.OnArtInitiatedArvDrugsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.CalculationProcessorUtils;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.QueryDisaggregationProcessor;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

public abstract class TxMLPatientCalculation extends BaseFghCalculation {

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
        Context.getRegisteredComponents(NextFilaDateCalculation.class)
            .get(0)
            .evaluate(lastFilaCalculationResult.keySet(), parameterValues, context);
    CalculationResultMap nextSeguimentoResult =
        Context.getRegisteredComponents(NextSeguimentoDateCalculation.class)
            .get(0)
            .evaluate(lastSeguimentoCalculationResult.keySet(), parameterValues, context);

    return this.evaluateUsingCalculationRules(
        context,
        cohort,
        startDate,
        endDate,
        resultMap,
        inicioRealResult,
        lastFilaCalculationResult,
        lastSeguimentoCalculationResult,
        nextFilaResult,
        nextSeguimentoResult,
        lastRecepcaoLevantamentoResult,
        lastRecepcaoLevantamentoCalculation);
  }

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> parameterValues, EvaluationContext context) {
    return this.evaluate(parameterValues, context);
  }

  protected abstract CalculationResultMap evaluateUsingCalculationRules(
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
      LastRecepcaoLevantamentoCalculation lastRecepcaoLevantamentoCalculation);

  protected void checkConsultationsOrFilaWithoutNextConsultationDate(
      Integer patientId,
      CalculationResultMap resultMap,
      Date endDate,
      CalculationResultMap lastResult,
      CalculationResultMap nextResult) {

    CalculationResult calculationLastResult = lastResult.get(patientId);
    CalculationResult calculationNextResult = nextResult.get(patientId);

    if (calculationNextResult != null && calculationNextResult.getValue() == null) {
      if (calculationLastResult != null) {
        Date lastDate = (Date) calculationLastResult.getValue();
        if (DateUtil.getDaysBetween(lastDate, endDate) >= 0) {
          resultMap.put(patientId, new SimpleResult(lastDate, this));
        }
      }
    }
  }

  public static CalculationResultMap getLastRecepcaoLevantamentoPlus30(
      Integer patientId,
      CalculationResultMap lastRecepcaoLevantamentoResult,
      LastRecepcaoLevantamentoCalculation lastRecepcaoLevantamentoCalculation) {

    CalculationResultMap lastRecepcaoLevantamentoPlus30 = new CalculationResultMap();
    CalculationResult maxRecepcao = lastRecepcaoLevantamentoResult.get(patientId);
    if (maxRecepcao != null) {
      lastRecepcaoLevantamentoPlus30.put(
          patientId,
          new SimpleResult(
              CalculationProcessorUtils.adjustDaysInDate((Date) maxRecepcao.getValue(), 30),
              lastRecepcaoLevantamentoCalculation));
    }
    return lastRecepcaoLevantamentoPlus30;
  }

  public static Map<Integer, Date> excludeEarlyHomeVisitDatesFromNextExpectedDateNumerator(
      CalculationResultMap numerator, Map<Integer, Date> deadInHomeVisitForm) {
    Map<Integer, Date> result = new HashMap<>();
    for (Integer patientId : numerator.keySet()) {
      CalculationResult numeratorResult = numerator.get(patientId);
      if (numeratorResult != null) {
        Date numeratorNextExpectedDate = (Date) numeratorResult.getValue();
        if (numeratorNextExpectedDate != null) {
          Date candidateDate = deadInHomeVisitForm.get(patientId);
          if (candidateDate != null) {
            if (candidateDate.compareTo(numeratorNextExpectedDate) > 0) {
              result.put(patientId, candidateDate);
            }
          }
        }
      }
    }
    return result;
  }

  protected CalculationResultMap filterUntracedAndTracedPatients(
      EvaluationContext context, CalculationResultMap resultMap) {
    CalculationResultMap returnMap = new CalculationResultMap();
    QueryDisaggregationProcessor queryDisaggregation =
        Context.getRegisteredComponents(QueryDisaggregationProcessor.class).get(0);

    Map<Integer, Date> criteriaOneData =
        queryDisaggregation.findUntracedPatientsWithinReportingPeriodCriteriaOne(context);
    ListMap<Integer, Date> criteriaTwoData =
        queryDisaggregation.findUntracedByNotHavefilledDataInVisitSectionCriteriaTwo(context);
    ListMap<Integer, Date> criteriaThreeData =
        queryDisaggregation.findTracedPatientsWithinReportingPeriodCriteriaThree(context);
    ListMap<Integer, Date> criteriaThreeNegationData =
        queryDisaggregation.findTracedPatientsWithinReportingPeriodCriteriaThreeNegation(context);

    for (Entry<Integer, CalculationResult> entry : resultMap.entrySet()) {
      Integer patientId = entry.getKey();
      Date maxNextDate = (Date) entry.getValue().getValue();

      if (this.matchCriteriaOne(criteriaOneData, patientId, maxNextDate)) {
        returnMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
        continue;
      }

      if (this.matchCriteriaTwo(criteriaTwoData, patientId, maxNextDate)) {
        returnMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
        continue;
      }

      if (this.matchCriteriaThree(
          criteriaThreeData, criteriaThreeNegationData, patientId, maxNextDate)) {
        returnMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
        continue;
      }
    }
    return returnMap;
  }

  private boolean matchCriteriaOne(
      Map<Integer, Date> criteriaOneData, Integer patientId, Date maxNextDate) {
    Date maxHomeCardVist = criteriaOneData.get(patientId);
    if (maxHomeCardVist == null
        || (maxHomeCardVist != null && maxHomeCardVist.compareTo(maxNextDate) < 0)) {
      return true;
    }
    return false;
  }

  private boolean matchCriteriaTwo(
      ListMap<Integer, Date> criteriaTwoData, Integer patientId, Date maxNextDate) {
    List<Date> homeCardVisitDates = criteriaTwoData.get(patientId);

    if (homeCardVisitDates == null || homeCardVisitDates.isEmpty()) {
      return false;
    }
    for (Date homeCardVisitDate : homeCardVisitDates) {
      if (homeCardVisitDate != null && homeCardVisitDate.compareTo(maxNextDate) >= 0) {
        return true;
      }
    }
    return false;
  }

  private boolean matchCriteriaThree(
      ListMap<Integer, Date> criteriaThreeData,
      ListMap<Integer, Date> criteriaThreeNegationData,
      Integer patientId,
      Date maxNextDate) {
    List<Date> homeCardVisitDates = criteriaThreeData.get(patientId);
    List<Date> homeCardVisitDatesNegation = criteriaThreeNegationData.get(patientId);

    if (homeCardVisitDates != null
        && !homeCardVisitDates.isEmpty()
        && homeCardVisitDatesNegation != null
        && !homeCardVisitDatesNegation.isEmpty()) {
      return false;
    }

    if (homeCardVisitDates != null && !homeCardVisitDates.isEmpty()) {
      for (Date homeCardVisitDate : homeCardVisitDates) {
        if (homeCardVisitDate != null && homeCardVisitDate.compareTo(maxNextDate) >= 0) {
          return true;
        }
      }
    }
    return false;
  }
}
