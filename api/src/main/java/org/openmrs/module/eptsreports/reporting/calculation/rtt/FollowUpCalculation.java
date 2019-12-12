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
public class FollowUpCalculation extends BaseFghCalculation {

  @Autowired private EvaluationService evaluationService;

  private static final int RTT_DAYS = 28;

  @Override
  public CalculationResultMap evaluate(
      final Collection<Integer> cohort,
      final Map<String, Object> parameterValues,
      final EvaluationContext context) {

    final CalculationResultMap resultMap = new CalculationResultMap();

    for (final Integer patientId : cohort) {

      final String query =
          String.format(TxRttQueries.QUERY.findFollowUpEncountersByPatient, patientId);

      final List<Object[]> encounters =
          this.evaluationService.evaluateToList(
              new SqlQueryBuilder(query, context.getParameterValues()), context);

      int position = 0;

      while (encounters.size() > position) {

        if ((encounters.size() - 1) == position) {
          final Object[] lastEncounter = encounters.get(position);
          final Date lastEncounterDate = (Date) lastEncounter[1];

          final String previousEncounterQuery =
              String.format(
                  TxRttQueries.QUERY.findLastFollowUpEncounterByPatientAndPeriod, patientId);

          final List<Object[]> evaluateToList =
              this.evaluationService.evaluateToList(
                  new SqlQueryBuilder(previousEncounterQuery, context.getParameterValues()),
                  context);

          if (evaluateToList.isEmpty()) {
            break;
          }

          final Object[] previousEncounter = evaluateToList.get(0);

          final Date previousScheduledDate = (Date) previousEncounter[2];

          if (previousScheduledDate != null) {

            final long days =
                EptsReportUtils.getDifferenceInDaysBetweenDates(
                    lastEncounterDate, previousScheduledDate);

            if (days > RTT_DAYS) {
              resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
              break;
            }
          }

        } else {
          final Object[] encounter = encounters.get(position);
          final Date encounterDate = (Date) encounter[1];
          final Object[] previousEncounter = encounters.get(position + 1);
          final Date previousScheduledDate = (Date) previousEncounter[2];

          if (previousScheduledDate != null) {

            final long days =
                EptsReportUtils.getDifferenceInDaysBetweenDates(
                    encounterDate, previousScheduledDate);

            if (days > RTT_DAYS) {
              resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
              break;
            }
          }
        }

        position++;
      }
    }

    return resultMap;
  }
}
