package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

import java.util.Date;
import java.util.List;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.library.queries.TxRttQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsListUtils;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

@Component
public class TxRttNextSeguimentoUntilEndDateProcessor {

  private static final int CHUNK_SIZE = 1000;

  public ListMap<Integer, Date[]> getResutls(List<Integer> cohort, EvaluationContext context) {

    if (cohort == null || cohort.isEmpty()) {
      return new ListMap<>();
    }

    return getNextScheduledConsultation(context, cohort);
  }

  private ListMap<Integer, Date[]> getNextScheduledConsultation(
      EvaluationContext context, List<Integer> cohort) {

    ListMap<Integer, Date[]> listPatientDates = new ListMap<>();
    final int slices = EptsListUtils.listSlices(cohort, CHUNK_SIZE);
    for (int position = 0; position < slices; position++) {

      final List<Integer> subList =
          cohort.subList(
              position * CHUNK_SIZE,
              (((position * CHUNK_SIZE) + CHUNK_SIZE) < cohort.size()
                  ? (position * CHUNK_SIZE) + CHUNK_SIZE
                  : cohort.size()));
      final SqlQueryBuilder queryBuilder =
          new SqlQueryBuilder(
              TxRttQueries.QUERY.findNextScheduledConsultationEncountersByPatientsIdAndLocation,
              context.getParameterValues());
      queryBuilder.addParameter("patientIds", subList);

      List<Object[]> queryResult =
          Context.getRegisteredComponents(EvaluationService.class)
              .get(0)
              .evaluateToList(queryBuilder, context);

      for (Object[] row : queryResult) {
        listPatientDates.putInList((Integer) row[0], new Date[] {(Date) row[1], (Date) row[2]});
      }
    }
    return listPatientDates;
  }
}
