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

  public Map<Integer, Date> findMaxEncounterDateByPatientMin6EncountersOnFichaClinicaWithINH(
      EvaluationContext context, Map<Integer, Date> cohortToEvaluete) {

    final List<Integer> subList = new ArrayList<>();

    subList.addAll(cohortToEvaluete.keySet());

    Map<Integer, Date> mapxDate = new HashMap<>();
    Map<Integer, Date> mapPosibleToExclude = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TXTbrevEndINHQueriesProcessor.QUERY
                .findMaxEncounterDateByPatientMin6EncountersOnFichaClinicaWithINH,
            context.getParameterValues());

    queryBuilder.addParameter("patientIds", subList);

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    mapxDate.putAll(mapIter);

    for (Integer patientId : mapxDate.keySet()) {

      Date initDate = getResutls(context).get(patientId);

      final SqlQueryBuilder qb =
          new SqlQueryBuilder(
              TxTbPrevExclusions.QUERY.excludeDTINH3HP7MonthUntilINHStartDate,
              context.getParameterValues());
      qb.addParameter("encounterDate", initDate);
      qb.addParameter("patientId", patientId);

      Map<Integer, Date> newMap =
          Context.getRegisteredComponents(EvaluationService.class)
              .get(0)
              .evaluateToMap(qb, Integer.class, Date.class, context);

      mapPosibleToExclude.putAll(newMap);
    }

    if (!mapPosibleToExclude.isEmpty()) {
      for (Integer patientId : mapPosibleToExclude.keySet()) {
        mapxDate.remove(patientId);
      }
    }

    return mapxDate;
  }

  public Map<Integer, Date>
      findMaxEncounterDateByPatientMin2FlitsEncountersWithQuartelyDespensation(
          EvaluationContext context, Map<Integer, Date> cohortToEvaluete) {

    final List<Integer> subList = new ArrayList<>();

    subList.addAll(cohortToEvaluete.keySet());

    Map<Integer, Date> mapxDate = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TXTbrevEndINHQueriesProcessor.QUERY
                .findMaxEncounterDateByPatientMin2FlitsEncountersWithQuartelyDespensation,
            context.getParameterValues());

    queryBuilder.addParameter("patientIds", subList);

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    mapxDate.putAll(mapIter);

    return mapxDate;
  }

  public Map<Integer, Date> findMaxEncounterDateByPatientMin6FlitsEncountersWithMontlyDespensation(
      EvaluationContext context, Map<Integer, Date> cohortToEvaluete) {

    final List<Integer> subList = new ArrayList<>();

    subList.addAll(cohortToEvaluete.keySet());

    Map<Integer, Date> mapxDate = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TXTbrevEndINHQueriesProcessor.QUERY
                .findMaxEncounterDateByPatientMin6FlitsEncountersWithMontlyDespensation,
            context.getParameterValues());

    queryBuilder.addParameter("patientIds", subList);

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    mapxDate.putAll(mapIter);

    return mapxDate;
  }

  public Map<Integer, Date> findMaxEncounterDateByPatientMin2EncountersWithINHandDTINH(
      EvaluationContext context, Map<Integer, Date> cohortToEvaluete) {

    final List<Integer> subList = new ArrayList<>();

    subList.addAll(cohortToEvaluete.keySet());

    Map<Integer, Date> mapxDate = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TXTbrevEndINHQueriesProcessor.QUERY
                .findMaxEncounterDateByPatientMin2EncountersWithINHandDTINH,
            context.getParameterValues());

    queryBuilder.addParameter("patientIds", subList);

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    mapxDate.putAll(mapIter);

    return mapxDate;
  }

  public Map<Integer, Date>
      findMaxEncounterDateByPatientMin2EncountersFichaClinicaWithINHandOneFichaClinicaWithDTINH(
          EvaluationContext context, Map<Integer, Date> cohortToEvaluete) {

    final List<Integer> subList = new ArrayList<>();

    subList.addAll(cohortToEvaluete.keySet());

    Map<Integer, Date> mapxDate = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TXTbrevEndINHQueriesProcessor.QUERY
                .findMaxEncounterDateByPatientMin2EncountersFichaClinicaWithINHandOneFichaClinicaWithDTINH,
            context.getParameterValues());

    queryBuilder.addParameter("patientIds", subList);

    Map<Integer, Date> mapIter =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(queryBuilder, Integer.class, Date.class, context);

    mapxDate.putAll(mapIter);

    return mapxDate;
  }

  public Map<Integer, Date>
      findMaxEncounterDateByPatientOnFlitWithINHMonthlyDespensationAndOneINHQuartelyDespensation(
          EvaluationContext context, Map<Integer, Date> cohortToEvaluete) {

    final List<Integer> subList = new ArrayList<>();

    subList.addAll(cohortToEvaluete.keySet());

    Map<Integer, Date> mapxDate = new HashMap<>();

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            TXTbrevEndINHQueriesProcessor.QUERY
                .findMaxEncounterDateByPatientOnFlitWithINHMonthlyDespensationAndOneINHQuartelyDespensation,
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
