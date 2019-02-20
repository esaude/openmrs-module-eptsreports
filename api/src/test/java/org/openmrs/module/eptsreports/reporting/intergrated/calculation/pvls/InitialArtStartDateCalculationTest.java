package org.openmrs.module.eptsreports.reporting.intergrated.calculation.pvls;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class InitialArtStartDateCalculationTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {2, 6, 7, 8, 999, 432});
  }

  @Override
  public CalculationResultMap getResult() {
    PatientCalculation calculation = getCalculation();
    CalculationResultMap map = new CalculationResultMap();

    // initiated ART by hiv enrolment
    PatientCalculationContext evaluationContext = getEvaluationContext();
    map.put(
        2,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2008-08-01 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    // initiated ART by starting ARV plan observation
    map.put(
        6,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2018-06-21 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    // initiated ART by historical start date observation
    map.put(
        7,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2019-01-18 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    // initiated ART by first phamarcy encounter observation
    map.put(
        8,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2019-01-21 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    // initiated ART by ARV transfer in observation
    map.put(
        999,
        new SimpleResult(
            new Timestamp(testsHelper.getDate("2019-01-20 00:00:00.0").getTime()),
            calculation,
            evaluationContext));
    map.put(432, new SimpleResult(null, calculation, evaluationContext));
    return map;
  }

  @Test
  public void evaluateShouldConsiderLeastInitiationDateForMultipleEntryPoints() {
    // initiating ART by starting ARV plan observation in addition to HIV
    // enrollment
    PatientCalculationContext evaluationContext = getEvaluationContext();
    openmrsTestHelper.createBasicObs(
        Context.getPatientService().getPatient(2),
        Context.getConceptService().getConcept(7777002),
        Context.getEncounterService().getEncounter(3),
        testsHelper.getDate("2008-07-01 00:00:00.0"),
        (Location) evaluationContext.getFromCache("location"),
        Context.getConceptService().getConcept(7777003));
    CalculationResultMap evaluatedResult =
        service.evaluate(getCohort(), getCalculation(), evaluationContext);
    Assert.assertEquals(
        testsHelper.getDate("2008-07-01 00:00:00.0"), evaluatedResult.get(2).getValue());
    // the rest should not be touched
    matchOtherResultsExcept(evaluatedResult, 2);

    // initiating ART by historical start date in addition to HIV enrollment
    openmrsTestHelper.createBasicObs(
        Context.getPatientService().getPatient(2),
        Context.getConceptService().getConcept(7777005),
        Context.getEncounterService().getEncounter(3),
        testsHelper.getDate("2019-01-01 00:00:00.0"),
        (Location) evaluationContext.getFromCache("location"),
        testsHelper.getDate("2008-06-01 00:00:00.0"));
    evaluatedResult = service.evaluate(getCohort(), getCalculation(), evaluationContext);
    Assert.assertEquals(
        testsHelper.getDate("2008-06-01 00:00:00.0"), evaluatedResult.get(2).getValue());
    // the rest should not be touched
    matchOtherResultsExcept(evaluatedResult, 2);

    // initiating ART enrolling on ARV plan and transferred in
    openmrsTestHelper.createBasicObs(
        Context.getPatientService().getPatient(2),
        Context.getConceptService().getConcept(7777002),
        Context.getEncounterService().getEncounter(3),
        testsHelper.getDate("2019-01-02 00:00:00.0"),
        (Location) evaluationContext.getFromCache("location"),
        Context.getConceptService().getConcept(7777004));
    evaluatedResult = service.evaluate(getCohort(), getCalculation(), evaluationContext);
    Assert.assertEquals(
        testsHelper.getDate("2008-06-01 00:00:00.0"), evaluatedResult.get(2).getValue());
    // the rest should not be touched
    matchOtherResultsExcept(evaluatedResult, 2);

    // TODO does the results have to contain every patient from the cohort
    // received with empty values!
    Assert.assertEquals(getCohort().size(), evaluatedResult.size());
  }
}
