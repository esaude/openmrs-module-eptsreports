package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.library.queries.TRFINQueries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

@Component
public class TRFINQueryProcessor {

  public Map<Integer, Date> findPatientsWhoAreTransferredInWithinReportingPeriod(
      EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            TRFINQueries.QUERY.findPatientsWhoAreTransferredInWithinReportingPeriod,
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  public List<Integer> findPatientsWithoutConsultationOrDrugPickUpWithinReportingPeriod(
      EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            TRFINQueries.QUERY.findPatientsWIthoutConsultationOrDrugPickUpInReportingPeriod,
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToList(qb, Integer.class, context);
  }
}
