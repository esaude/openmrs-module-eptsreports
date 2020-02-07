package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.NextFilaDateProcessor;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class NextFilaDateCalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> parameterValues, EvaluationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    CalculationResultMap lastFilaCalculationResult =
        Context.getRegisteredComponents(LastFilaCalculation.class)
            .get(0)
            .evaluate(cohort, parameterValues, context);
    NextFilaDateProcessor nextFilaProcessor =
        Context.getRegisteredComponents(NextFilaDateProcessor.class).get(0);

    ListMap<Integer, Obs> resutls =
        nextFilaProcessor.getResutls(new ArrayList<>(lastFilaCalculationResult.keySet()), context);

    for (Integer patientId : lastFilaCalculationResult.keySet()) {
      CalculationResult calculationResult = lastFilaCalculationResult.get(patientId);
      Date lastDateFila = (Date) (calculationResult != null ? calculationResult.getValue() : null);
      List<Obs> allObsFila = resutls.get(patientId);
      this.setMaxValueDateTime(patientId, lastDateFila, allObsFila, resultMap);
    }

    return resultMap;
  }

  private void setMaxValueDateTime(
      Integer pId, Date lastDateFila, List<Obs> allObsFila, CalculationResultMap resultMap) {

    Date finalComparisonDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);
    Date maxDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);

    if (lastDateFila != null) {
      if (allObsFila != null) {
        for (Obs obs : allObsFila) {
          if (obs != null && obs.getObsDatetime() != null) {
            if (obs.getObsDatetime().compareTo(lastDateFila) == 0) {
              Date valueDatetime = obs.getValueDatetime();
              if (valueDatetime != null && valueDatetime.compareTo(maxDate) > 0) {
                maxDate = valueDatetime;
              }
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
