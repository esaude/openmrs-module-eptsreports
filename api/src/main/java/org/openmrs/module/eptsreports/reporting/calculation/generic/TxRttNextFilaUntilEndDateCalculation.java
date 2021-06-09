package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.TxRttNextFilaUntilEndDateProcessor;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class TxRttNextFilaUntilEndDateCalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> parameterValues, EvaluationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    CalculationResultMap lastFilaCalculationResult =
        Context.getRegisteredComponents(LastFilaCalculation.class)
            .get(0)
            .evaluate(cohort, parameterValues, context);
    TxRttNextFilaUntilEndDateProcessor nextFilaProcessor =
        Context.getRegisteredComponents(TxRttNextFilaUntilEndDateProcessor.class).get(0);

    ListMap<Integer, Date[]> resutls =
        nextFilaProcessor.getResutls(new ArrayList<>(lastFilaCalculationResult.keySet()), context);

    for (Integer patientId : lastFilaCalculationResult.keySet()) {
      CalculationResult calculationResult = lastFilaCalculationResult.get(patientId);
      Date lastDateFila = (Date) (calculationResult != null ? calculationResult.getValue() : null);

      List<Date[]> allObsNextFila = resutls.get(patientId);

      this.setMaxValueDateTime(patientId, lastDateFila, allObsNextFila, resultMap);
    }
    return resultMap;
  }

  private void setMaxValueDateTime(
      Integer pId, Date lastDateFila, List<Date[]> allObsNextFila, CalculationResultMap resultMap) {

    Date finalComparisonDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);
    Date maxDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);

    if (lastDateFila != null) {
      if (allObsNextFila != null) {
        for (Date[] dates : allObsNextFila) {

          Date proposedLastFilaDate = dates[0];
          if (proposedLastFilaDate.compareTo(lastDateFila) == 0) {
            Date nextScheduledDate = dates[1];

            if (nextScheduledDate != null && nextScheduledDate.compareTo(maxDate) > 0) {
              maxDate = nextScheduledDate;
            }
          }
        }
      } else {
        resultMap.put(pId, null);
        return;
      }
    }
    if (!DateUtils.isSameDay(maxDate, finalComparisonDate)) {
      resultMap.put(pId, new SimpleResult(maxDate, this));
    }
  }
}
