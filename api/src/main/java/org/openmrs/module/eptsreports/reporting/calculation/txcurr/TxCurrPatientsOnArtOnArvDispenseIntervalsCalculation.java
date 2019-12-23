package org.openmrs.module.eptsreports.reporting.calculation.txcurr;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.util.DisaggregationInterval;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.LastFilaProcessor;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class TxCurrPatientsOnArtOnArvDispenseIntervalsCalculation extends BaseFghCalculation {

  public static String DISAGREGRATION_INTERVAL = "disaggregation-interval";

  private static int DAYS_LESS_THAN_3_MONTHS = 83;
  private static int DAYS_BETWEEN_3_AND_5_MONTHS = 173;

  private static int CONCEPT_TYPE_OF_DISPENSATION = 23739;
  private static int CONCEPT_MONTHLY = 1098;
  private static int CONCEPT_QUARTERLY = 23720;
  private static int CONCEPT_QUARTERLY_DISPENSATION = 23730;
  private static int CONCEPT_SEMESTER_ARV_PICKUP = 23888;

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {

    DisaggregationInterval disagregationRange =
        DisaggregationInterval.valueOf(
            (String) context.getParameterValues().get(DISAGREGRATION_INTERVAL));

    List<Object[]> maxFilas =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getMaxFilaWithProximoLevantamento(context);

    if (DisaggregationInterval.LESS_THAN_3_MONTHS.equals(disagregationRange)) {
      return this.disagregationForLessThan3Months(context, maxFilas);
    }

    if (DisaggregationInterval.BETWEEN_3_AND_5_MONTHS.equals(disagregationRange)) {
      return this.disagregationBetween3And5Months(context, maxFilas);
    }

    if (DisaggregationInterval.FOR_6_OR_MORE_MONTHS.equals(disagregationRange)) {
      return this.disagregationFor6OrMoreMonths(context, maxFilas);
    }
    return new CalculationResultMap();
  }

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> parameterValues, EvaluationContext context) {
    return this.evaluate(parameterValues, context);
  }

  private CalculationResultMap disagregationForLessThan3Months(
      EvaluationContext context, List<Object[]> maxFilas) {
    CalculationResultMap resultMap = new CalculationResultMap();

    for (Object[] fila : maxFilas) {
      Integer patientId = (Integer) fila[0];
      Date lastFilaDate = (Date) fila[1];
      Date nextExpectedFila = (Date) fila[2];

      if (lastFilaDate != null && nextExpectedFila != null) {
        if (DateUtil.getDaysBetween(lastFilaDate, nextExpectedFila) < DAYS_LESS_THAN_3_MONTHS) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
        }
      }
    }

    Map<Integer, Date> tipoLevantamentoMensal =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getLastTipoDeLevantamentoOnFichaClinicaMasterCard(
                context,
                Integer.valueOf(CONCEPT_TYPE_OF_DISPENSATION),
                Integer.valueOf(CONCEPT_MONTHLY));
    for (Integer patientId : tipoLevantamentoMensal.keySet()) {
      resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
    }
    return resultMap;
  }

  private CalculationResultMap disagregationBetween3And5Months(
      EvaluationContext context, List<Object[]> maxFilas) {
    CalculationResultMap resultMap = new CalculationResultMap();

    for (Object[] fila : maxFilas) {
      Integer patientId = (Integer) fila[0];
      Date lastFilaDate = (Date) fila[1];
      Date nextExpectedFila = (Date) fila[2];

      if (lastFilaDate != null && nextExpectedFila != null) {
        int daysBetween = DateUtil.getDaysBetween(lastFilaDate, nextExpectedFila);
        if (daysBetween >= DAYS_LESS_THAN_3_MONTHS && daysBetween <= DAYS_BETWEEN_3_AND_5_MONTHS) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
        }
      }
    }

    Map<Integer, Date> tipoLevantamentoTrimestral =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getLastTipoDeLevantamentoOnFichaClinicaMasterCard(
                context,
                Integer.valueOf(CONCEPT_TYPE_OF_DISPENSATION),
                Integer.valueOf(CONCEPT_QUARTERLY));
    for (Integer patientId : tipoLevantamentoTrimestral.keySet()) {
      resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
    }

    Map<Integer, Date> modelosDiferenciadosTrimestral =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getLastMarkedInModelosDiferenciadosDeCuidadosOnFichaClinicaMasterCard(
                context, CONCEPT_QUARTERLY_DISPENSATION);
    for (Integer patientId : modelosDiferenciadosTrimestral.keySet()) {
      resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
    }
    return resultMap;
  }

  private CalculationResultMap disagregationFor6OrMoreMonths(
      EvaluationContext context, List<Object[]> maxFilas) {
    CalculationResultMap resultMap = new CalculationResultMap();

    for (Object[] fila : maxFilas) {
      Integer patientId = (Integer) fila[0];
      Date lastFilaDate = (Date) fila[1];
      Date nextExpectedFila = (Date) fila[2];

      if (lastFilaDate != null && nextExpectedFila != null) {
        if (DateUtil.getDaysBetween(lastFilaDate, nextExpectedFila) > DAYS_BETWEEN_3_AND_5_MONTHS) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
        }
      }
    }

    Map<Integer, Date> tipoLevantamentoSemestral =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getLastTipoDeLevantamentoOnFichaClinicaMasterCard(
                context,
                Integer.valueOf(CONCEPT_TYPE_OF_DISPENSATION),
                Integer.valueOf(CONCEPT_SEMESTER_ARV_PICKUP));
    for (Integer patientId : tipoLevantamentoSemestral.keySet()) {
      resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
    }

    Map<Integer, Date> modelosDiferenciadosSemestral =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getLastMarkedInModelosDiferenciadosDeCuidadosOnFichaClinicaMasterCard(
                context, CONCEPT_SEMESTER_ARV_PICKUP);
    for (Integer patientId : modelosDiferenciadosSemestral.keySet()) {
      resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
    }
    return resultMap;
  }
}
