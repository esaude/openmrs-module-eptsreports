package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

@Component
public class TXTbrevEndINHProcessor {
  public Map<Integer, Date> getResutls(EvaluationContext context) {

    Map<Integer, Date> map = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TxTbPrevQueriesInterface.QUERY.findPatientsWhoStartedTPIDuringPreviousReportingPeriod,
            context.getParameterValues());

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    map.putAll(mapIter);

    return map;
  }

  public Map<Integer, Date> processPatientsByMaxInFichaClinicaFichaResumoFichaSeguimento(
      EvaluationContext context, Map<Integer, Date> cohortToEvaluete) {

    final List<Integer> subList = new ArrayList<>();

    subList.addAll(cohortToEvaluete.keySet());

    Map<Integer, Date> mapxDate = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TXTbrevEndINHQueriesProcessor.QUERY
                .processPatientsByMaxInFichaClinicaFichaResumoFichaSeguimento,
            context.getParameterValues());

    queryBuilder.addParameter("patientIds", subList);

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    mapxDate.putAll(mapIter);

    return mapxDate;
  }

  public Map<Integer, Date> processPatientsByMaxReportingPeriodSixEncounters(
      EvaluationContext context, Map<Integer, Date> cohortToEvaluete) {

    final List<Integer> subList = new ArrayList<>();

    subList.addAll(cohortToEvaluete.keySet());

    Map<Integer, Date> mapxDate = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TXTbrevEndINHQueriesProcessor.QUERY.findMaxEncounterDateByPatientMin6Encounters,
            context.getParameterValues());

    queryBuilder.addParameter("patientIds", subList);

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    mapxDate.putAll(mapIter);

    return mapxDate;
  }

  public Map<Integer, Date> findMaxEncounterDateByPatientMin2FlitsEncounters(
      EvaluationContext context, Map<Integer, Date> cohortToEvaluete) {

    final List<Integer> subList = new ArrayList<>();

    subList.addAll(cohortToEvaluete.keySet());

    Map<Integer, Date> mapxDate = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TXTbrevEndINHQueriesProcessor.QUERY.findMaxEncounterDateByPatientMin2FlitsEncounters,
            context.getParameterValues());

    queryBuilder.addParameter("patientIds", subList);

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    mapxDate.putAll(mapIter);

    return mapxDate;
  }

  public Map<Integer, Date> findMaxEncounterDateByPatientMin6FlitsEncounters(
      EvaluationContext context, Map<Integer, Date> cohortToEvaluete) {

    final List<Integer> subList = new ArrayList<>();

    subList.addAll(cohortToEvaluete.keySet());

    Map<Integer, Date> mapxDate = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TXTbrevEndINHQueriesProcessor.QUERY.findMaxEncounterDateByPatientMin6FlitsEncounters,
            context.getParameterValues());

    queryBuilder.addParameter("patientIds", subList);

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    mapxDate.putAll(mapIter);

    return mapxDate;
  }

  public Map<Integer, Date> findMaxEncounterDateByPatientMin2Encounters(
      EvaluationContext context, Map<Integer, Date> cohortToEvaluete) {

    final List<Integer> subList = new ArrayList<>();

    subList.addAll(cohortToEvaluete.keySet());

    Map<Integer, Date> mapxDate = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TXTbrevEndINHQueriesProcessor.QUERY.findMaxEncounterDateByPatientMin2Encounters,
            context.getParameterValues());

    queryBuilder.addParameter("patientIds", subList);

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    mapxDate.putAll(mapIter);

    return mapxDate;
  }
}
