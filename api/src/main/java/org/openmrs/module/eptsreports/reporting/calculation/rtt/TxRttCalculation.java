/** */
package org.openmrs.module.eptsreports.reporting.calculation.rtt;

import java.util.ArrayList;
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

    final ArtPickUpCalculation artPickUpCalculation =
        Context.getRegisteredComponents(ArtPickUpCalculation.class).get(0);

    final FollowUpCalculation followUpCalculation =
        Context.getRegisteredComponents(FollowUpCalculation.class).get(0);

    final CalculationResultMap artPickUpResultMap =
        artPickUpCalculation.evaluate(patients, parameterValues, context);

    patients.removeAll(this.getPatientIds(artPickUpResultMap));

    final CalculationResultMap followUpResultMap =
        followUpCalculation.evaluate(patients, parameterValues, context);

    resultMap.putAll(artPickUpResultMap);
    resultMap.putAll(followUpResultMap);

    return resultMap;
  }

  private List<Integer> getPatientIds(final CalculationResultMap resultMap) {
    final List<Integer> patients = new ArrayList<>();

    for (final Integer patientId : resultMap.keySet()) {
      patients.add(patientId);
    }

    return patients;
  }
}
