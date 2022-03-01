package org.openmrs.module.eptsreports.reporting.intergrated.cohort.evaluator;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientIdCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

@Ignore
public class CalculationCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

  @Test
  public void evaluateShouldReturnPatientIdsFromEvaluatedCalculation() throws EvaluationException {
    CalculationCohortDefinition calculationCohortDefinition =
        new CalculationCohortDefinition(new PatientIdCalculation());
    EvaluatedCohort evaluatedCohort = evaluate(calculationCohortDefinition);
    HashSet<Integer> ids = new HashSet<>(Arrays.asList(2, 6, 7, 8));
    Assert.assertEquals(ids, evaluatedCohort.getMemberIds());
  }

  private EvaluatedCohort evaluate(CalculationCohortDefinition calculationCohortDefinition)
      throws EvaluationException {
    return Context.getService(CohortDefinitionService.class)
        .evaluate(calculationCohortDefinition, null);
  }
}
