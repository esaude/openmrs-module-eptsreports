package org.openmrs.module.eptsreports.reporting.intergrated.calculation.pvls;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.PregnantCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class PregnantCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {

    return new PregnantCalculation();
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {7, 8, 501});
  }

  @Override
  public CalculationResultMap getResult() {
    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();

    // initiated ART on 2008-08-01 by hiv enrolment and received and vl
    // result on
    // 2018-12-12
    PatientCalculationContext evaluationContext = getEvaluationContext();

    // Patient is is marked as pregnant(YES) in Adult initial Followup
    map.put(7, new SimpleResult(true, calculation, evaluationContext));

    map.put(8, new SimpleResult(false, calculation, evaluationContext));

    // Patient marked as Pregnant in PTV program
    map.put(501, new SimpleResult(true, calculation, evaluationContext));

    return map;
  }

  @Before
  public void initialise() throws Exception {
    executeDataSet("pregnantcalculation-dataset.xml");
  }

  @Test
  public void calculatePregnantCheckingLastVl() {

    evaluateMarkedAsPregnantInConsultation();
    evaluateMarkedAsPregnantInPTVProgram();
  }

  private void evaluateMarkedAsPregnantInConsultation() {

    Encounter adultInitialEncounter =
        openmrsTestHelper.createEncounter(
            Context.getPatientService().getPatient(7),
            Context.getEncounterService().getEncounterType(6777004),
            testsHelper.getDate("2019-01-21 00:00:00.0"),
            (Location) getEvaluationContext().getFromCache("location"));
    Encounter adultFollowUpEnconter =
        openmrsTestHelper.createEncounter(
            Context.getPatientService().getPatient(7),
            Context.getEncounterService().getEncounterType(6777002),
            testsHelper.getDate("2018-10-20 00:00:00.0"),
            (Location) getEvaluationContext().getFromCache("location"));

    // adding VL for Patient
    openmrsTestHelper.createBasicObs(
        Context.getPatientService().getPatient(7),
        Context.getConceptService().getConcept(7777001),
        adultInitialEncounter,
        testsHelper.getDate("2018-12-12 00:00:00.0"),
        (Location) getEvaluationContext().getFromCache("location"),
        100.0);

    openmrsTestHelper.createBasicObs(
        Context.getPatientService().getPatient(7),
        Context.getConceptService().getConcept(7777019),
        adultFollowUpEnconter,
        testsHelper.getDate("2018-10-20 00:00:00.0"),
        (Location) getEvaluationContext().getFromCache("location"),
        new Concept(7777020));

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());

    Assert.assertEquals(true, evaluatedResult.get(7).getValue());

    matchOtherResultsExcept(evaluatedResult, 8, 501);
  }

  private void evaluateMarkedAsPregnantInPTVProgram() {

    Encounter adultInitialEncounter =
        openmrsTestHelper.createEncounter(
            Context.getPatientService().getPatient(501),
            Context.getEncounterService().getEncounterType(6777004),
            testsHelper.getDate("2019-01-21 00:00:00.0"),
            (Location) getEvaluationContext().getFromCache("location"));

    // adding VL for Patient
    openmrsTestHelper.createBasicObs(
        Context.getPatientService().getPatient(501),
        Context.getConceptService().getConcept(7777001),
        adultInitialEncounter,
        testsHelper.getDate("2018-12-12 00:00:00.0"),
        (Location) getEvaluationContext().getFromCache("location"),
        110.0);

    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), getEvaluationContext());

    Assert.assertEquals(true, evaluatedResult.get(501).getValue());

    matchOtherResultsExcept(evaluatedResult, 7, 8);
  }
}
