/** */
package org.openmrs.module.eptsreports.reporting.calculation.rtt;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.library.queries.TxRttQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class EncounterCalculation extends BaseFghCalculation {

  private static final int RTT_DAYS = 28;

  @Autowired private EvaluationService evaluationService;

  @Override
  public CalculationResultMap evaluate(
      final Collection<Integer> cohort,
      final Map<String, Object> parameterValues,
      final EvaluationContext context) {

    final CalculationResultMap resultMap = new CalculationResultMap();

    for (final Integer patientId : cohort) {

      final SqlQueryBuilder queryBuilder =
          new SqlQueryBuilder(
              TxRttQueries.QUERY.findEncountersByPatient, context.getParameterValues());
      queryBuilder.addParameter("patientId", patientId);

      final List<Date> encounters =
          this.evaluationService.evaluateToList(queryBuilder, Date.class, context);

      for (final Date encounterDate : encounters) {

        final List<Date> followUpResult =
            this.findResults(
                context,
                patientId,
                encounterDate,
                TxRttQueries.QUERY
                    .findLastNextEncounterScheduledDateOnThePreviousFolloupEncounterByPatient);

        if (followUpResult.isEmpty()) {
          break;
        }

        final List<Date> pickUpResult =
            this.findResults(
                context,
                patientId,
                encounterDate,
                TxRttQueries.QUERY.findLastNextPickupDateOnThePreviousPickupEncounterByPatient);

        if (pickUpResult.isEmpty()) {
          break;
        }

        final Date followUpDate = followUpResult.get(0);
        final Date pickUpDate = pickUpResult.get(0);

        final long followUpDays =
            EptsReportUtils.getDifferenceInDaysBetweenDates(encounterDate, followUpDate);
        final long pickUpDays =
            EptsReportUtils.getDifferenceInDaysBetweenDates(encounterDate, pickUpDate);

        if (this.isOnRTT(followUpDays, pickUpDays)) {
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          break;
        }
      }
    }

    return resultMap;
  }

  private List<Date> findResults(
      final EvaluationContext context,
      final Integer patientId,
      final Date encounterDate,
      final String query) {
    final SqlQueryBuilder queryBuilder = new SqlQueryBuilder(query, context.getParameterValues());

    queryBuilder.addParameter("patientId", patientId).addParameter("encounterDate", encounterDate);

    final List<Date> result =
        this.evaluationService.evaluateToList(queryBuilder, Date.class, context);
    return result;
  }

  private boolean isOnRTT(final long followUpDays, final long pickUpDays) {
    return (followUpDays > RTT_DAYS) && (pickUpDays > RTT_DAYS);
  }
}
