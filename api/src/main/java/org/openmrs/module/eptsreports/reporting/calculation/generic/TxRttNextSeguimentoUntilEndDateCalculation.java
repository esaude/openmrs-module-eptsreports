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
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.TxRttNextSeguimentoUntilEndDateProcessor;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class TxRttNextSeguimentoUntilEndDateCalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> parameterValues, EvaluationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    CalculationResultMap lastSeguimentoResult =
        Context.getRegisteredComponents(LastSeguimentoCalculation.class)
            .get(0)
            .evaluate(cohort, parameterValues, context);

    TxRttNextSeguimentoUntilEndDateProcessor nextSeguimentoProcessor =
        Context.getRegisteredComponents(TxRttNextSeguimentoUntilEndDateProcessor.class).get(0);

    ListMap<Integer, Date[]> patientsByObs =
        nextSeguimentoProcessor.getResutls(new ArrayList<>(lastSeguimentoResult.keySet()), context);

    for (Integer patientId : lastSeguimentoResult.keySet()) {

      CalculationResult calculationResult = lastSeguimentoResult.get(patientId);

      Date lastDateSeguimento =
          (Date) (calculationResult != null ? calculationResult.getValue() : null);

      List<Date[]> allObsSeguimento = patientsByObs.get(patientId);

      this.setMaxValueDateTime(patientId, lastDateSeguimento, allObsSeguimento, resultMap);
    }
    return resultMap;
  }

  private void setMaxValueDateTime(
      Integer pId,
      Date lastDateSeguimento,
      List<Date[]> allObsSeguimento,
      CalculationResultMap resultMap) {

    Date finalComparisonDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);
    Date maxDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);

    if (lastDateSeguimento != null) {
      if (allObsSeguimento != null) {
        for (Date[] dates : allObsSeguimento) {
          Date proposedLastConsultationDate = dates[0];
          if (proposedLastConsultationDate != null) {
            if (proposedLastConsultationDate.compareTo(lastDateSeguimento) == 0) {
              Date nextscheduledConsultationDate = dates[1];
              if (nextscheduledConsultationDate != null
                  && nextscheduledConsultationDate.compareTo(maxDate) > 0) {
                maxDate = nextscheduledConsultationDate;
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
