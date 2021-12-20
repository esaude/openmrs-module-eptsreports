package org.openmrs.module.eptsreports.reporting.calculation.rtt;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRTTCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxRttQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsDateUtil;
import org.openmrs.module.eptsreports.reporting.utils.EptsListUtils;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxRTTPLHIVLess12MonthCalculation extends BaseFghCalculation {

  private static final int CHUNK_SIZE = 1000;

  private static int DAYS_OF_YEAR = 365;

  @Autowired private TxRTTCohortQueries txRTTCohortQueries;

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    try {
      Set<Integer> cohort =
          Context.getService(CohortDefinitionService.class)
              .evaluate(txRTTCohortQueries.getPatientsOnRTT(), context)
              .getMemberIds();

      Location location = (Location) context.getParameterValues().get("location");
      Date startDate = (Date) context.getParameterValues().get("startDate");
      Date endDate = (Date) context.getParameterValues().get("endDate");
      Map<String, Object> parameters = new HashMap<>();

      parameters.put("startDate", DateUtil.adjustDate(startDate, -1, DurationUnit.DAYS));
      parameters.put("endDate", DateUtil.adjustDate(startDate, -1, DurationUnit.DAYS));
      parameters.put("realEndDate", endDate);
      parameters.put("location", location);

      CalculationResultMap iiTPatients =
          Context.getRegisteredComponents(TxRTTPatientsWhoExperiencedIITCalculation.class)
              .get(0)
              .evaluate(parameters, this.getNewEvaluationContext(parameters));

      this.calculateLessThan12MonthsDates(context, new ArrayList<>(cohort), iiTPatients, resultMap);

    } catch (EvaluationException e) {
      throw new APIException(e);
    }
    return resultMap;
  }

  private void calculateLessThan12MonthsDates(
      EvaluationContext context,
      List<Integer> cohort,
      CalculationResultMap iiTPatients,
      CalculationResultMap resultMap) {

    Map<Integer, Date> mapTxCurrMinDate =
        processPatientsByMinTxCurrDateInReportingPeriod(context, cohort);
    for (Entry<Integer, Date> entry : mapTxCurrMinDate.entrySet()) {

      Integer patientId = entry.getKey();
      Date minTxCurrDate = entry.getValue();

      CalculationResult calculationResult = iiTPatients.get(patientId);

      if (calculationResult != null && calculationResult.getValue() != null) {
        if (calculationResult.getValue() instanceof Date) {
          Date iitDate = (Date) calculationResult.getValue();
          // Date iiDatePlus29 = CalculationProcessorUtils.adjustDaysInDate(iitDate, 1);
          if (EptsDateUtil.getDaysBetween(iitDate, minTxCurrDate) < DAYS_OF_YEAR) {
            resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          }
        }
      }
    }

    cohort.removeAll(mapTxCurrMinDate.keySet());
    Map<Integer, Date> mapTxCurrMaxDate =
        processPatientsByMaxTxCurrDateInPreviousReportingPeriod(context, cohort);

    for (Entry<Integer, Date> entry : mapTxCurrMaxDate.entrySet()) {

      Integer patientId = entry.getKey();
      Date maxTxCurrDate = entry.getValue();

      CalculationResult calculationResult = iiTPatients.get(patientId);

      if (calculationResult != null && calculationResult.getValue() != null) {
        if (calculationResult.getValue() instanceof Date) {
          Date iitDate = (Date) calculationResult.getValue();
          // Date iiDatePlus29 = CalculationProcessorUtils.adjustDaysInDate(iitDate, 1);
          if (EptsDateUtil.getDaysBetween(iitDate, maxTxCurrDate) < DAYS_OF_YEAR) {
            resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
          }
        }
      }
    }
  }

  private Map<Integer, Date> processPatientsByMinTxCurrDateInReportingPeriod(
      EvaluationContext context, List<Integer> cohort) {
    Map<Integer, Date> mapTxCurrMinDate = new HashMap<>();
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
              TxRttQueries.QUERY.findMinEncounterDateByPatientInReportingPeriod,
              context.getParameterValues());
      queryBuilder.addParameter("patientIds", subList);

      Map<Integer, Date> mapIter =
          Context.getRegisteredComponents(EvaluationService.class)
              .get(0)
              .evaluateToMap(queryBuilder, Integer.class, Date.class, context);
      mapTxCurrMinDate.putAll(mapIter);
    }
    return mapTxCurrMinDate;
  }

  private Map<Integer, Date> processPatientsByMaxTxCurrDateInPreviousReportingPeriod(
      EvaluationContext context, List<Integer> cohort) {

    Map<Integer, Date> mapTxCurrMinDate = new HashMap<>();

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
              TxRttQueries.QUERY.findMaxEncounterDateByPatientInReportingPeriod,
              context.getParameterValues());
      queryBuilder.addParameter("patientIds", subList);

      Map<Integer, Date> mapIter =
          Context.getRegisteredComponents(EvaluationService.class)
              .get(0)
              .evaluateToMap(queryBuilder, Integer.class, Date.class, context);
      mapTxCurrMinDate.putAll(mapIter);
    }
    return mapTxCurrMinDate;
  }

  private EvaluationContext getNewEvaluationContext(Map<String, Object> parameters) {
    EvaluationContext context = new EvaluationContext();
    for (Entry<String, Object> entry : parameters.entrySet()) {
      context.addParameterValue(entry.getKey(), entry.getValue());
    }
    return context;
  }
}
