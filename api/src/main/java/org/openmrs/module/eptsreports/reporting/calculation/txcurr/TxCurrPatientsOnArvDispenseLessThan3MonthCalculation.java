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
public class TxCurrPatientsOnArvDispenseLessThan3MonthCalculation
    extends TxCurrPatientsOnArtOnArvDispenseIntervalsCalculation {

  private static int DAYS_LESS_THAN_3_MONTHS = 83;

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    return super.evaluate(parameterValues, context);
  }

  @Override
  protected void evaluateDisaggregatedPatients(
      Integer patientId,
      CalculationResultMap resultMap,
      List<PatientDisaggregated> allPatientDisaggregated) {

    if (this.havePatientDisagregatedSameDates(allPatientDisaggregated)) {
      for (PatientDisaggregated pDisaggretated : allPatientDisaggregated) {
        if (Arrays.asList(DisaggregationSourceTypes.FILA, DisaggregationSourceTypes.DISPENSA_MENSAL)
            .contains((pDisaggretated.getDisaggregationSourceType()))) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          return;
        }
      }
    } else {
      PatientDisaggregated maxPatientDisaggregated =
          this.getMaxPatientDisaggregated(allPatientDisaggregated);

      if (maxPatientDisaggregated != null) {
        if (Arrays.asList(DisaggregationSourceTypes.FILA, DisaggregationSourceTypes.DISPENSA_MENSAL)
            .contains((maxPatientDisaggregated.getDisaggregationSourceType()))) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
        }
      }
    }
  }

  protected Map<Integer, PatientDisaggregated> getFilaDisaggregations(List<Object[]> maxFilas) {
    Map<Integer, PatientDisaggregated> monthlyFila = new HashMap<>();

    for (Object[] fila : maxFilas) {
      Integer patientId = (Integer) fila[0];
      Date lastFilaDate = (Date) fila[1];
      Date nextExpectedFila = (Date) fila[2];
      if (DateUtil.getDaysBetween(lastFilaDate, nextExpectedFila) < DAYS_LESS_THAN_3_MONTHS) {
        monthlyFila.put(patientId, new FilaPatientDisaggregated(patientId, lastFilaDate));
      }
    }
    return monthlyFila;
  }
}
