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
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.NextSeguimentoDateProcessor;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class NextSeguimentoDateCalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> parameterValues, EvaluationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    CalculationResultMap lastSeguimentoResult =
        Context.getRegisteredComponents(LastSeguimentoCalculation.class)
            .get(0)
            .evaluate(cohort, parameterValues, context);

    NextSeguimentoDateProcessor nextSeguimentoProcessor =
        Context.getRegisteredComponents(NextSeguimentoDateProcessor.class).get(0);

    ListMap<Integer, Obs> patientsByObs =
        nextSeguimentoProcessor.getResutls(new ArrayList<>(lastSeguimentoResult.keySet()), context);

    for (Integer patientId : lastSeguimentoResult.keySet()) {

      CalculationResult calculationResult = lastSeguimentoResult.get(patientId);

      Date lastDateSeguimento =
          (Date) (calculationResult != null ? calculationResult.getValue() : null);

      List<Obs> allObsSeguimento = patientsByObs.get(patientId);

      this.setMaxValueDateTime(patientId, lastDateSeguimento, allObsSeguimento, resultMap);
    }
    return resultMap;
  }

  private void setMaxValueDateTime(
      Integer pId,
      Date lastDateSeguimento,
      List<Obs> allObsSeguimento,
      CalculationResultMap resultMap) {

    Date finalComparisonDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);
    Date maxDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);

    if (lastDateSeguimento != null) {
      if (allObsSeguimento != null) {
        for (Obs obs : allObsSeguimento) {
          if (obs != null && obs.getObsDatetime() != null) {
            if (obs.getObsDatetime().compareTo(lastDateSeguimento) == 0) {
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

      if (!DateUtils.isSameDay(maxDate, finalComparisonDate)) {
        resultMap.put(pId, new SimpleResult(maxDate, this));
      }
    }
  }
}
