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

	@Override
	public CalculationResultMap evaluate(Map<String, Object> parameterValues, EvaluationContext context) {

		DisaggregationInterval disagregationRange = DisaggregationInterval
				.valueOf((String) context.getParameterValues().get(DISAGREGRATION_INTERVAL));

		List<Object[]> maxFilas = Context.getRegisteredComponents(LastFilaProcessor.class).get(0)
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
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			EvaluationContext context) {
		return this.evaluate(parameterValues, context);
	}

	private CalculationResultMap disagregationForLessThan3Months(EvaluationContext context, List<Object[]> maxFilas) {
		CalculationResultMap resultMap = new CalculationResultMap();

		for (Object[] fila : maxFilas) {
			Integer patientId = (Integer) fila[0];
			Date lastFilaDate = (Date) fila[1];
			Date nextExpectedFila = (Date) fila[2];

			if (DateUtil.getDaysBetween(lastFilaDate, nextExpectedFila) < 83) {
				resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
			}
		}

		Map<Integer, Date> tipoLevantamentoMensal = Context.getRegisteredComponents(LastFilaProcessor.class).get(0)
				.getLastTipoDeLevantamentoOnFichaClinicaMasterCard(context, Integer.valueOf(23739),
						Integer.valueOf(1098));
		for (Integer patientId : tipoLevantamentoMensal.keySet()) {
			resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
		}
		return resultMap;
	}

	private CalculationResultMap disagregationBetween3And5Months(EvaluationContext context, List<Object[]> maxFilas) {
		CalculationResultMap resultMap = new CalculationResultMap();

		for (Object[] fila : maxFilas) {
			Integer patientId = (Integer) fila[0];
			Date lastFilaDate = (Date) fila[1];
			Date nextExpectedFila = (Date) fila[2];

			int daysBetween = DateUtil.getDaysBetween(lastFilaDate, nextExpectedFila);
			if (daysBetween >= 83 && daysBetween <= 173) {
				resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
			}
		}

		Map<Integer, Date> tipoLevantamentoTrimestral = Context.getRegisteredComponents(LastFilaProcessor.class).get(0)
				.getLastTipoDeLevantamentoOnFichaClinicaMasterCard(context, Integer.valueOf(23739),
						Integer.valueOf(23720));
		for (Integer patientId : tipoLevantamentoTrimestral.keySet()) {
			resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
		}

		Map<Integer, Date> modelosDiferenciadosTrimestral = Context.getRegisteredComponents(LastFilaProcessor.class)
				.get(0).getLastMarkedInModelosDiferenciadosDeCuidadosOnFichaClinicaMasterCard(context, 23730);
		for (Integer patientId : modelosDiferenciadosTrimestral.keySet()) {
			resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
		}
		return resultMap;
	}

	private CalculationResultMap disagregationFor6OrMoreMonths(EvaluationContext context, List<Object[]> maxFilas) {
		CalculationResultMap resultMap = new CalculationResultMap();

		for (Object[] fila : maxFilas) {
			Integer patientId = (Integer) fila[0];
			Date lastFilaDate = (Date) fila[1];
			Date nextExpectedFila = (Date) fila[2];

			if (DateUtil.getDaysBetween(lastFilaDate, nextExpectedFila) > 173) {
				resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
			}
		}

		Map<Integer, Date> tipoLevantamentoSemestral = Context.getRegisteredComponents(LastFilaProcessor.class).get(0)
				.getLastTipoDeLevantamentoOnFichaClinicaMasterCard(context, Integer.valueOf(23739),
						Integer.valueOf(23888));
		for (Integer patientId : tipoLevantamentoSemestral.keySet()) {
			resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
		}

		Map<Integer, Date> modelosDiferenciadosSemestral = Context.getRegisteredComponents(LastFilaProcessor.class)
				.get(0).getLastMarkedInModelosDiferenciadosDeCuidadosOnFichaClinicaMasterCard(context, 23888);
		for (Integer patientId : modelosDiferenciadosSemestral.keySet()) {
			resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
		}
		return resultMap;
	}
}
