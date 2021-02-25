package org.openmrs.module.eptsreports.reporting.calculation.tbprev;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.library.queries.TxTbPrevQueriesInterface;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

@Component
public class TxTbPrev3HPCalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    Map<Integer, Date> map = new HashMap<>();

    Map<Integer, Date> resutls = this.getResutls(context);

    for (Integer patientId : resutls.keySet()) {

      Date date = resutls.get(patientId);

      resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));

      final SqlQueryBuilder queryBuilder =
          new SqlQueryBuilder(
              TxTbPrevQueriesInterface.QUERY
                  .findPatientsWhoStarted3HPPreventiveTreatmentBeforePreviousReportingPeriod,
              context.getParameterValues());

      queryBuilder.addParameter("patientId", patientId);
      queryBuilder.addParameter("encounterDate", date);

      Map<Integer, Date> mapItem =
          Context.getRegisteredComponents(EvaluationService.class)
              .get(0)
              .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

      map.putAll(mapItem);
    }
    if (!map.isEmpty()) {
      for (Entry<Integer, Date> entry : map.entrySet()) {
        Integer patientId = entry.getKey();
        Date date = entry.getValue();
        if (date != null) {
          resultMap.remove(patientId);
        }
      }
    }
    return resultMap;
  }

  public Map<Integer, Date> getResutls(EvaluationContext context) {

    Map<Integer, Date> map = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TxTbPrevQueriesInterface.QUERY
                .findPatientsWhoStarted3HPPreventiveTreatmentDuringPreviousReportingPeriod,
            context.getParameterValues());

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    map.putAll(mapIter);

    return map;
  }
}
