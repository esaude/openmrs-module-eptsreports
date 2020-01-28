package org.openmrs.module.eptsreports.reporting.calculation.txcurr;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class TxCurrPatientsOnArvDispenseBetween3And5MonthsCalculation
		extends TxCurrPatientsOnArtOnArvDispenseIntervalsCalculation {

	private static int DAYS_LESS_THAN_3_MONTHS = 83;
	private static int DAYS_BETWEEN_3_AND_5_MONTHS = 173;

	@Override
	public CalculationResultMap evaluate(Map<String, Object> parameterValues, EvaluationContext context) {
		return super.evaluate(parameterValues, context);
	}

	@Override
	protected void evaluateDisaggregatedPatients(Integer patientId, CalculationResultMap resultMap,
			List<PatientDisaggregated> allPatientDisaggregated) {

		if (super.havePatientDisagregatedSameDates(allPatientDisaggregated)) {
			for (PatientDisaggregated pDisaggretated : allPatientDisaggregated) {
				if (Arrays
						.asList(DisaggregationSourceTypes.FILA, DisaggregationSourceTypes.DISPENSA_TRIMESTRAL,
								DisaggregationSourceTypes.MODELO_DIFERENCIADO_TRIMESTRAL)
						.contains((pDisaggretated.getDisaggregationSourceType()))) {
					resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
					break;
				}
			}
		} else {
			PatientDisaggregated maxPatientDisaggregated = super.getMaxPatientDisaggregated(allPatientDisaggregated);
			if (maxPatientDisaggregated != null) {

				if (Arrays
						.asList(DisaggregationSourceTypes.FILA, DisaggregationSourceTypes.DISPENSA_TRIMESTRAL,
								DisaggregationSourceTypes.MODELO_DIFERENCIADO_TRIMESTRAL)
						.contains((maxPatientDisaggregated.getDisaggregationSourceType()))) {
					resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
				}
			}
		}
	}

	@Override
	protected Map<Integer, PatientDisaggregated> getFilaDisaggregations(List<Object[]> maxFilas) {
		Map<Integer, PatientDisaggregated> filaDisaggregated = new HashMap<>();
		for (Object[] fila : maxFilas) {
			Integer patientId = (Integer) fila[0];
			Date lastFilaDate = (Date) fila[1];
			Date nextExpectedFila = (Date) fila[2];

			if (lastFilaDate != null && nextExpectedFila != null) {
				int daysBetween = DateUtil.getDaysBetween(lastFilaDate, nextExpectedFila);
				if (daysBetween >= DAYS_LESS_THAN_3_MONTHS && daysBetween <= DAYS_BETWEEN_3_AND_5_MONTHS) {
					filaDisaggregated.put(patientId, new FilaPatientDisaggregated(patientId, lastFilaDate));
				}
			}
		}
		return filaDisaggregated;
	}
}
