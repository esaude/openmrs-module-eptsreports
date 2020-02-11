package org.openmrs.module.eptsreports.reporting.calculation.txcurr;

import java.util.Arrays;
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

      for (PatientDisaggregated patientDisaggregated : allPatientDisaggregated) {
        if (DisaggregationSourceTypes.FILA.equals(
            patientDisaggregated.getDisaggregationSourceType())) {
          FilaPatientDisaggregated filaDisaggregation =
              (FilaPatientDisaggregated) patientDisaggregated;
          if (DateUtil.getDaysBetween(
                  filaDisaggregation.getDate(), filaDisaggregation.getNextFila())
              < DAYS_LESS_THAN_3_MONTHS) {
            resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          }
          return;
        }
      }

      for (PatientDisaggregated patientDisaggregated : allPatientDisaggregated) {
        if (DisaggregationSourceTypes.DISPENSA_MENSAL.equals(
            patientDisaggregated.getDisaggregationSourceType())) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          return;
        }
      }
    } else {
      PatientDisaggregated maxPatientDisaggregated =
          this.getMaxPatientDisaggregated(allPatientDisaggregated);

      if (maxPatientDisaggregated != null) {

        if (DisaggregationSourceTypes.FILA.equals(
            maxPatientDisaggregated.getDisaggregationSourceType())) {
          FilaPatientDisaggregated filaDisaggregation =
              (FilaPatientDisaggregated) maxPatientDisaggregated;
          if (DateUtil.getDaysBetween(
                  filaDisaggregation.getDate(), filaDisaggregation.getNextFila())
              < DAYS_LESS_THAN_3_MONTHS) {
            resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          }
        } else {

          if (Arrays.asList(
                  DisaggregationSourceTypes.FILA, DisaggregationSourceTypes.DISPENSA_MENSAL)
              .contains((maxPatientDisaggregated.getDisaggregationSourceType()))) {
            resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          }
        }
      }
    }
  }
}
