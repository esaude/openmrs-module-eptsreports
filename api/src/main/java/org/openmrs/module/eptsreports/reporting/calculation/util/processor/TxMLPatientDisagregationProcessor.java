package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

import java.util.Date;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;

public class TxMLPatientDisagregationProcessor {

  /**
   * Verifica se o paciente esta no map exclusionsToUse
   *
   * @param patientId
   * @param candidateDate
   * @param exclusionsToUse
   * @return
   */
  public static boolean hasDatesGreatherThanEvaluatedDateToExclude(
      Integer patientId, Date candidateDate, CalculationResultMap exclusionsToUse) {

    CalculationResult exclusionDateResult = exclusionsToUse.get(patientId);
    Date exclusionDate =
        (Date) ((exclusionDateResult != null) ? exclusionDateResult.getValue() : null);

    if (exclusionDate != null && exclusionDate.compareTo(candidateDate) > 0) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public static boolean hasPatientsFromOtherDisaggregationToExclude(
      Integer patientId, CalculationResultMap... resultMap) {
    for (CalculationResultMap resultMapItem : resultMap) {
      if (resultMapItem.get(patientId) != null) {
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }
}
