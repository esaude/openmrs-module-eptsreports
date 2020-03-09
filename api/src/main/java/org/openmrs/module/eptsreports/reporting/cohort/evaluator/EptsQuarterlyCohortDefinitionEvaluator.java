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

    EptsQuarterlyCohortDefinition.Quarter quarter =
        (EptsQuarterlyCohortDefinition.Quarter) context.getParameterValue("quarter");
    Integer year = (Integer) context.getParameterValue("year");

    context.getParameterValues().putAll(getRange(year, quarter));

    Cohort c = cohortDefinitionService.evaluate(cd.getCohortDefinition(), context);
    ret.getMemberIds().addAll(c.getMemberIds());
    return ret;
  }

  //  TODO
  private Map<String, Date> getRange(Integer year, EptsQuarterlyCohortDefinition.Quarter quarter) {
    HashMap<String, Date> range = new HashMap<>();
    range.put("onOrAfter", DateUtil.getDateTime(2019, 9, 20));
    range.put("onOrBefore", DateUtil.getDateTime(2019, 10, 21));
    return range;
  }
}
