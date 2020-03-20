/** */
package org.openmrs.module.eptsreports.reporting.calculation;

import java.util.Collection;
import java.util.Map;
import org.openmrs.calculation.BaseCalculation;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/** @author Stélio Moiane */
public abstract class BaseFghCalculation extends BaseCalculation {

  public CalculationResultMap evaluate(
      final Map<String, Object> parameterValues, final EvaluationContext context) {
    throw new RuntimeException(
        String.format(
            "You must implement your evaluator method for the calculation '%s' ",
            this.getClass().getName()));
  };

  public CalculationResultMap evaluate(
      final Collection<Integer> cohort,
      final Map<String, Object> parameterValues,
      final EvaluationContext context) {
    throw new RuntimeException(
        String.format(
            "You must implement your evaluator method for the calculation '%s' ",
            this.getClass().getName()));
  };
}
