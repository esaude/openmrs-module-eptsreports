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
public class TxCurrPatientsOnArvDispenseBetween3And5MonthsCalculation
    extends TxCurrPatientsOnArtOnArvDispenseIntervalsCalculation {

  private static int DAYS_LESS_THAN_3_MONTHS = 83;
  private static int DAYS_BETWEEN_3_AND_5_MONTHS = 173;

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

    allPatientDisaggregated = super.getMaximumPatientDisaggregatedByDate(allPatientDisaggregated);

    if (allPatientDisaggregated.size() > 1) {

      for (PatientDisaggregated patientDisaggregated : allPatientDisaggregated) {
        if (DisaggregationSourceTypes.FILA.equals(
            patientDisaggregated.getDisaggregationSourceType())) {
          if (this.isInExpectedFilaDisaggregationInterval(
              (FilaPatientDisaggregated) patientDisaggregated)) {
            resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          }
          return;
        }
      }

      for (PatientDisaggregated patientDisaggregated : allPatientDisaggregated) {
        if (Arrays.asList(DisaggregationSourceTypes.DISPENSA_SEMESTRAL)
            .contains(patientDisaggregated.getDisaggregationSourceType())) {
          return;
        }
      }

      for (PatientDisaggregated patientDisaggregated : allPatientDisaggregated) {
        if (DisaggregationSourceTypes.DISPENSA_TRIMESTRAL.equals(
            patientDisaggregated.getDisaggregationSourceType())) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          return;
        }
      }

      for (PatientDisaggregated patientDisaggregated : allPatientDisaggregated) {
        if (Arrays.asList(
                DisaggregationSourceTypes.DISPENSA_MENSAL,
                DisaggregationSourceTypes.MODELO_DIFERENCIADO_SEMESTRAL)
            .contains(patientDisaggregated.getDisaggregationSourceType())) {
          return;
        }
      }

      for (PatientDisaggregated patientDisaggregated : allPatientDisaggregated) {

        if (DisaggregationSourceTypes.MODELO_DIFERENCIADO_TRIMESTRAL.equals(
            patientDisaggregated.getDisaggregationSourceType())) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          return;
        }
      }

    } else if (!allPatientDisaggregated.isEmpty()) {

      PatientDisaggregated maxPatientDisaggregated = allPatientDisaggregated.iterator().next();

      if (maxPatientDisaggregated != null) {
        if (DisaggregationSourceTypes.FILA.equals(
            maxPatientDisaggregated.getDisaggregationSourceType())) {
          if (this.isInExpectedFilaDisaggregationInterval(
              (FilaPatientDisaggregated) maxPatientDisaggregated)) {
            resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          }
          return;
        }
        if (DisaggregationSourceTypes.DISPENSA_SEMESTRAL.equals(
            maxPatientDisaggregated.getDisaggregationSourceType())) {
          return;
        }
        if (DisaggregationSourceTypes.DISPENSA_TRIMESTRAL.equals(
            maxPatientDisaggregated.getDisaggregationSourceType())) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
        }

        if (Arrays.asList(
                DisaggregationSourceTypes.DISPENSA_MENSAL,
                DisaggregationSourceTypes.MODELO_DIFERENCIADO_SEMESTRAL)
            .contains(maxPatientDisaggregated.getDisaggregationSourceType())) {
          return;
        }

        if (DisaggregationSourceTypes.MODELO_DIFERENCIADO_TRIMESTRAL.equals(
            maxPatientDisaggregated.getDisaggregationSourceType())) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
        }
      }
    }
  }

  private boolean isInExpectedFilaDisaggregationInterval(
      FilaPatientDisaggregated filaDisaggregation) {
    int daysBetween =
        DateUtil.getDaysBetween(filaDisaggregation.getDate(), filaDisaggregation.getNextFila());
    return daysBetween >= DAYS_LESS_THAN_3_MONTHS && daysBetween <= DAYS_BETWEEN_3_AND_5_MONTHS;
  }
}
