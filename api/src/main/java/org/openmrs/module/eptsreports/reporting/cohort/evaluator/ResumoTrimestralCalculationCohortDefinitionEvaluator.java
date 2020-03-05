/** */
package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.HashSet;
import org.openmrs.annotation.Handler;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.definition.ResumoTrimestralCalculationCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports = ResumoTrimestralCalculationCohortDefinition.class)
public class ResumoTrimestralCalculationCohortDefinitionEvaluator {

  public Object evaluate(final CohortDefinition cohortDefinition, final EvaluationContext context)
      throws EvaluationException {
    final EvaluatedCohort evaluatedCohort = new EvaluatedCohort(cohortDefinition, context);

    final BaseFghCalculationCohortDefinition calculationCohortDefinition =
        (BaseFghCalculationCohortDefinition) cohortDefinition;
    final BaseFghCalculation calculation = calculationCohortDefinition.getCalculation();

    final CalculationResultMap resultMap =
        calculation.evaluate(context.getParameterValues(), context);

    evaluatedCohort.setMemberIds(new HashSet<>(resultMap.keySet()));

    return evaluatedCohort;
  }
}
