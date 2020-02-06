package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.CalculationWithResultFinder;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXTBCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is a sample FGH database cohortDefinitions test, run FGH instance and then set
 * ~/.OpenMRS/openmrs-runtime.properties appropriately. Tested against PEPFAR MER 2.1 report results
 * with the same parameter values in here while running in eclipse
 */
@Ignore
public class TXTBCohortDefinitionsFGHLiveTest extends DefinitionsFGHLiveTest {

  @Autowired private TXTBCohortQueries txTbCohortQueries;

  @Override
  public String username() {
    return "admin";
  }

  @Override
  public String password() {
    return "eSaude123";
  }

  @Test
  public void tbTreatmentStartUsingEndDate() throws EvaluationException {
    // TODO remove startDate
    EvaluatedCohort result =
        evaluateCohortDefinition(
            txTbCohortQueries.getTbDrugTreatmentStartDateWithinReportingDate());
    Assert.assertEquals(3, result.size());
  }

  @Test
  public void getInTBProgram() throws EvaluationException {
    EvaluatedCohort result = evaluateCohortDefinition(txTbCohortQueries.getInTBProgram());
    Assert.assertEquals(7, result.size());
  }

  @Test
  public void codedNegativeTbScreening() throws EvaluationException {
    EvaluatedCohort result =
        evaluateCodedObsCohortDefinition(txTbCohortQueries.codedNoTbScreening());
    Assert.assertEquals(413, result.size());
  }

  @Test
  public void codedPositiveTbScreening() throws EvaluationException {
    EvaluatedCohort result =
        evaluateCodedObsCohortDefinition(txTbCohortQueries.codedYesTbScreening());
    Assert.assertEquals(18, result.size());
  }

  @Test
  public void patientsNotInitiationOnARTAtDate() throws EvaluationException {
    CalculationCohortDefinition artInit =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0));
    // all patients both initiated and not
    artInit.setWithResultFinder(CalculationWithResultFinder.DEFAULT);
    Assert.assertEquals(12328, evaluateCohortDefinition(artInit).size());
    // only non initiated patients
    artInit.setWithResultFinder(CalculationWithResultFinder.NULL);
    Assert.assertEquals(11906, evaluateCohortDefinition(artInit).size());

    // evaluate with onOrAfter and onOrBefore does no change
    Assert.assertEquals(11906, evaluateCalculationCohortDefinition(artInit).size());
  }

  @Test
  public void artList() throws EvaluationException {
    EvaluatedCohort result = evaluateCohortDefinition(txTbCohortQueries.artList());
    Assert.assertEquals(12328, result.size());
  }

  @Test
  public void txTbNumerator() throws EvaluationException {
    EvaluatedCohort result = evaluateCohortDefinition(txTbCohortQueries.txTbNumerator());
    Assert.assertEquals(9, result.size());
  }
}
