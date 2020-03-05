package org.openmrs.module.eptsreports.reporting.calculation.quarterly.query;

import java.util.List;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;

public class ResumoTrimestralQueries {

  public static List<Integer> findPatientsWhoAreNewlyEnrolledOnART(
      EvaluationContext context, MonthlyDateRange monthlyDateRange) {
    SqlQueryBuilder qb = new SqlQueryBuilder();

    qb.append(IResumoTrimestralQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnART);
    qb.addParameter("startDate", monthlyDateRange.getStartDate());
    qb.addParameter("endDate", monthlyDateRange.getEndDate());
    qb.addParameter("location", (Location) context.getParameterValue("location"));

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToList(qb, Integer.class, context);
  }

  public static List<Integer> findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod(
      EvaluationContext context, MonthlyDateRange monthlyDateRange) {
    SqlQueryBuilder qb = new SqlQueryBuilder();
    qb.append(
        IResumoTrimestralQueries.QUERY.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod);
    qb.addParameter("startDate", monthlyDateRange.getStartDate());
    qb.addParameter("endDate", monthlyDateRange.getEndDate());
    qb.addParameter("location", (Location) context.getParameterValue("location"));

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToList(qb, Integer.class, context);
  }

  public static List<Integer>
      findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard(
          EvaluationContext context, MonthlyDateRange monthlyDateRange) {
    SqlQueryBuilder qb = new SqlQueryBuilder();
    qb.append(
        IResumoTrimestralQueries.QUERY
            .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard);
    qb.addParameter("startDate", monthlyDateRange.getStartDate());
    qb.addParameter("endDate", monthlyDateRange.getEndDate());
    qb.addParameter("location", (Location) context.getParameterValue("location"));

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToList(qb, Integer.class, context);
  }

  public static List<Integer> getBaseCohortQuery(
      EvaluationContext context, MonthlyDateRange monthlyDateRange) {
    SqlQueryBuilder qb = new SqlQueryBuilder();
    qb.append(BaseQueries.getBaseCohortQuery());
    qb.addParameter("startDate", monthlyDateRange.getStartDate());
    qb.addParameter("endDate", monthlyDateRange.getEndDate());
    qb.addParameter("location", (Location) context.getParameterValue("location"));

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToList(qb, Integer.class, context);
  }
}
