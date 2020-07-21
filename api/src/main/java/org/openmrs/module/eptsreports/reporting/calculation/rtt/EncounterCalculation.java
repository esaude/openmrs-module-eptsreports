/** */
package org.openmrs.module.eptsreports.reporting.calculation.rtt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.library.queries.TxRttQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsListUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class EncounterCalculation extends BaseFghCalculation {

  private static final int CHUNK_SIZE = 1000;

  private static final int RTT_DAYS = 28;

  @Autowired private EvaluationService evaluationService;

  @Override
  public CalculationResultMap evaluate(
      final Collection<Integer> cohort,
      final Map<String, Object> parameterValues,
      final EvaluationContext context) {

    final CalculationResultMap resultMap = new CalculationResultMap();

    final List<Integer> list = new ArrayList<>(cohort);

    final int slices = EptsListUtils.listSlices(cohort, CHUNK_SIZE);

    for (int position = 0; position < slices; position++) {

      final List<Integer> subList =
          list.subList(
              position * CHUNK_SIZE,
              (((position * CHUNK_SIZE) + CHUNK_SIZE) < list.size()
                  ? (position * CHUNK_SIZE) + CHUNK_SIZE
                  : list.size()));

      final SqlQueryBuilder queryBuilder =
          new SqlQueryBuilder(
              TxRttQueries.QUERY.findEncountersByPatient, context.getParameterValues());
      queryBuilder.addParameter("patients", subList);

      final List<Object[]> evaluateToList =
          this.evaluationService.evaluateToList(queryBuilder, context);

      final Map<Integer, List<Date>> patients = this.getPatientsEncounterDates(evaluateToList);

      this.addIfOnRttPatient(context, resultMap, patients);
    }

    return resultMap;
  }

  private void addIfOnRttPatient(
      final EvaluationContext context,
      final CalculationResultMap resultMap,
      final Map<Integer, List<Date>> patients) {
    for (final Integer patientId : patients.keySet()) {

      final List<Date> encounters = patients.get(patientId);

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

        if (followUpDate != null && pickUpDate != null) {
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
    }
  }

  private Map<Integer, List<Date>> getPatientsEncounterDates(final List<Object[]> evaluateToList) {

    final Map<Integer, List<Date>> map = new HashMap<>();

    for (final Object[] object : evaluateToList) {

      final Integer patientId = (Integer) object[0];
      List<Date> dates = new ArrayList<>();

      if (map.get(patientId) != null) {
        dates = map.get(patientId);
      }

      dates.add((Date) object[1]);
      map.put(patientId, dates);
    }

    return map;
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
