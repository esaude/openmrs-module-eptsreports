package org.openmrs.module.eptsreports.reporting.data.evaluator;

import java.util.Map;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.DatimExtractCalculation;
import org.openmrs.module.eptsreports.reporting.data.definition.CalculationDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a {@link
 * org.openmrs.module.eptsreports.reporting.data.definition.CalculationDataDefinition} to produce a
 * PatientData
 */
@Handler(supports = CalculationDataDefinition.class, order = 50)
public class CalculationDataEvaluator implements PatientDataEvaluator {
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

    // wrapper for evaluating the Datim Code in the MER Reports
    context.getBaseCohort().addMember(DatimExtractCalculation.PATIENT_DATIM_CODE_WRAPPER);

    // evaluate the calculation
    PatientCalculationService service = Context.getService(PatientCalculationService.class);
    PatientCalculationContext calcContext = service.createCalculationContext();
    calcContext.addToCache("location", def.getLocation());

    CalculationResultMap resultMap =
        Context.getRegisteredComponents(def.getCalculation().getClass())
            .get(0)
            .evaluate(def.getCalculationParameters(), this.getEvaluationContext(def.getLocation()));

    for (Map.Entry<Integer, CalculationResult> entry : resultMap.entrySet()) {
      c.addData(entry.getKey(), entry.getValue());
    }
    return c;
  }

  private EvaluationContext getEvaluationContext(Location location) {
    EvaluationContext context = new EvaluationContext();
    context.addParameterValue("location", location);
    return context;
  }
}
