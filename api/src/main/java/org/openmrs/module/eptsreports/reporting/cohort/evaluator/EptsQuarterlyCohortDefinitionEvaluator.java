package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsQuarterlyCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = EptsQuarterlyCohortDefinition.class)
public class EptsQuarterlyCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

  private CohortDefinitionService cohortDefinitionService;

  @Autowired
  public EptsQuarterlyCohortDefinitionEvaluator(CohortDefinitionService cohortDefinitionService) {
    this.cohortDefinitionService = cohortDefinitionService;
  }

  @Override
  public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context)
      throws EvaluationException {
    EptsQuarterlyCohortDefinition cd = (EptsQuarterlyCohortDefinition) cohortDefinition;
    EvaluatedCohort ret = new EvaluatedCohort(cohortDefinition, context);
    Integer year = cd.getYear();
    EptsQuarterlyCohortDefinition.Quarter quarter = cd.getQuarter();
    EptsQuarterlyCohortDefinition.Month month = cd.getMonth();
    Map<String, Date> range = getRange(year, quarter, month);
    context.getParameterValues().putAll(range);
    Cohort c = cohortDefinitionService.evaluate(cd.getCohortDefinition(), context);
    ret.getMemberIds().addAll(c.getMemberIds());
    return ret;
  }

  private Map<String, Date> getRange(
      Integer year,
      EptsQuarterlyCohortDefinition.Quarter quarter,
      EptsQuarterlyCohortDefinition.Month month) {
    Map<String, Date> periodDates = DateUtil.getPeriodDates(year, quarter.ordinal() + 1, null);
    int monthAdjustment = 0;
    if (EptsQuarterlyCohortDefinition.Month.M2.equals(month)) {
      monthAdjustment = 1;
    } else if (EptsQuarterlyCohortDefinition.Month.M3.equals(month)) {
      monthAdjustment = 2;
    }
    HashMap<String, Date> range = new HashMap<>();
    Date start = DateUtil.getStartOfMonth(periodDates.get("startDate"), monthAdjustment);
    Date end = DateUtil.getEndOfMonth(start);
    range.put("startDate", start);
    range.put("onOrAfter", start);
    range.put("endDate", end);
    range.put("onOrBefore", end);
    return range;
  }
}
