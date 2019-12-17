/** */
package org.openmrs.module.eptsreports.reporting.calculation.rtt;

import java.util.List;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.library.queries.TxRttQueries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class TxRttCalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      final Map<String, Object> parameterValues, final EvaluationContext context) {
    final CalculationResultMap resultMap = new CalculationResultMap();

    final EvaluationService evaluationService =
        Context.getRegisteredComponents(EvaluationService.class).get(0);

    final List<Integer> patients =
        evaluationService.evaluateToList(
            new SqlQueryBuilder(
                TxRttQueries.QUERY.findPatientsWithEncountersInASpecificPeriod,
                context.getParameterValues()),
            Integer.class,
            context);

    if (context.getBaseCohort() != null) {
      patients.retainAll(context.getBaseCohort().getMemberIds());
    }

    final EncounterCalculation encounterCalculation =
        Context.getRegisteredComponents(EncounterCalculation.class).get(0);

    final CalculationResultMap encounterResultMap =
        encounterCalculation.evaluate(patients, parameterValues, context);

    resultMap.putAll(encounterResultMap);

    return resultMap;
  }
}
