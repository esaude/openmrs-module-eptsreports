package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.Date;
import java.util.Map;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/** Evaluate {@link CalculationDataDefinition} */
@Handler(supports = CalculationDataDefinition.class, order = 50)
public class CalculationDataDefinitionEvaluator implements PatientDataEvaluator {

  /**
   * @see
   *     PatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition,
   *     org.openmrs.module.reporting.evaluation.EvaluationContext)
   */
  public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
      throws EvaluationException {

    CalculationDataDefinition def = (CalculationDataDefinition) definition;
    EvaluatedPatientData c = new EvaluatedPatientData(def, context);

    // return right away if there is nothing to evaluate
    if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
      return c;
    }

    // Use date from cohort definition, or from ${date} or ${endDate} or now
    Date onOrBefore = def.getOnOrBefore();
    if (onOrBefore == null) {
      onOrBefore = (Date) context.getParameterValue("date");
      if (onOrBefore == null) {
        onOrBefore = (Date) context.getParameterValue("endDate");
        if (onOrBefore == null) {
          onOrBefore = new Date();
        }
      }
    }
    // add the location in the cache

    // evaluate the calculation
    PatientCalculationService service = Context.getService(PatientCalculationService.class);
    PatientCalculationContext calcContext = service.createCalculationContext();
    calcContext.setNow(onOrBefore);
    calcContext.addToCache("location", def.getLocation());
    CalculationResultMap resultMap =
        service.evaluate(
            context.getBaseCohort().getMemberIds(),
            def.getCalculation(),
            def.getCalculationParameters(),
            calcContext);

    // move data into return object
    for (Map.Entry<Integer, CalculationResult> entry : resultMap.entrySet()) {
      c.addData(entry.getKey(), entry.getValue());
    }

    return c;
  }
}
