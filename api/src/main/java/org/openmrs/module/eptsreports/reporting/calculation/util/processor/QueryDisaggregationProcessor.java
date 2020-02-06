package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

@Component
public class QueryDisaggregationProcessor {

  public Map<Integer, Date> findMapMaxPatientStateDateByProgramAndPatientStateAndEndDate(
      EvaluationContext context, Program program, ProgramWorkflowState state) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            String.format(
                IQueryDisaggregationProcessor.QUERY
                    .findMaxPatientStateDateByProgramAndPatientStateAndEndDate,
                program.getProgramId(),
                state.getProgramWorkflowStateId()),
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  public Map<Integer, Date> findMapMaxObsDatetimeByEncounterAndQuestionsAndAnswersInPeriod(
      EvaluationContext context,
      EncounterType encounterType,
      List<Integer> questions,
      List<Integer> answers) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            String.format(
                IQueryDisaggregationProcessor.QUERY
                    .findMaxObsDatetimeByEncounterTypeAndConceptsAndAnsweresInPeriod,
                encounterType.getEncounterTypeId(),
                StringUtils.join(questions, ","),
                StringUtils.join(answers, ",")),
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  public Map<Integer, Date> findMapMaxObsDatetimeByEncountersAndQuestionsAndAnswerAndEndDate(
      EvaluationContext context,
      List<Integer> encounterIds,
      List<Integer> questions,
      Concept answer) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            String.format(
                IQueryDisaggregationProcessor.QUERY
                    .findMaxObsDateTimeByEncounterTypesAndConceptsAndAnswerAndEndDate,
                StringUtils.join(encounterIds, ","),
                StringUtils.join(questions, ","),
                answer.getConceptId()),
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  public Map<Integer, Date> findPatientAndDateInDemographicModule(EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            IQueryDisaggregationProcessor.QUERY.findPatientAndDateInDemographicModule,
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }
}
