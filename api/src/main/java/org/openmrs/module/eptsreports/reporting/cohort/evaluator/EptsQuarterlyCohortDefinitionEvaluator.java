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
    Date onOrAfter = (Date) context.getParameterValue("onOrAfter");
    Date onOrBefore = (Date) context.getParameterValue("onOrBefore");
    EptsQuarterlyCohortDefinition.Month month = cd.getMonth();
    context.getParameterValues().putAll(getRange(month, onOrAfter, onOrBefore));
    Cohort c = cohortDefinitionService.evaluate(cd.getCohortDefinition(), context);
    ret.getMemberIds().addAll(c.getMemberIds());
    return ret;
  }

  private Map<String, Date> getRange(
      EptsQuarterlyCohortDefinition.Month month, Date onOrAfter, Date onOrBefore) {
    Date startOfMonth1 = DateUtil.getStartOfMonth(onOrAfter);
    Date startOfMonth2 = DateUtil.getStartOfMonth(onOrBefore);
    int monthAdjustment = 0;
    int monthsBetween = DateUtil.monthsBetween(startOfMonth1, startOfMonth2);
    if (monthsBetween != 2) {
      throw new IllegalArgumentException(getMessage(onOrAfter, onOrBefore));
    } else if (EptsQuarterlyCohortDefinition.Month.M2.equals(month)) {
      monthAdjustment = 1;
    } else if (EptsQuarterlyCohortDefinition.Month.M3.equals(month)) {
      monthAdjustment = 2;
    }
    HashMap<String, Date> range = new HashMap<>();
    Date start = DateUtil.getStartOfMonth(onOrAfter, monthAdjustment);
    Date end = DateUtil.getEndOfMonth(start);
    range.put("startDate", start);
    range.put("onOrAfter", start);
    range.put("endDate", end);
    range.put("onOrBefore", end);
    return range;
  }

  private String getMessage(Date onOrAfter, Date onOrBefore) {
    return DateUtil.formatDate(onOrAfter, "dd MM yyyy")
        + " to "
        + DateUtil.formatDate(onOrBefore, "dd MM yyyy")
        + " is not a valid quarter";
  }
}
