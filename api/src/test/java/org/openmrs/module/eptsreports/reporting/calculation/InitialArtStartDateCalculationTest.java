package org.openmrs.module.eptsreports.reporting.calculation;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.InitialArtStartDateCalculation;

public class InitialArtStartDateCalculationTest extends BasePatientCalculationTest {

    @Override
    public PatientCalculation getCalculation() {
        return new InitialArtStartDateCalculation();
    }

    @Override
    public Collection<Integer> getCohort() {
        return Arrays.asList(new Integer[]{2, 6, 7, 8, 999, 432});
    }

    @Override
    public CalculationResultMap getResult() {
        PatientCalculation calculation = getCalculation();
        return calculationsTestsCache.initialArtStartDateCalculationDefaultResult(calculation, getEvaluationContext(), getCohort());
    }

    @Test
    public void evaluate_shouldConsiderLeastInitiationDateForMultipleEntryPoints() {
        // initiating ART by starting ARV plan observation in addition to HIV
        // enrollment
        calculationsTestsCache.createBasicObs(Context.getPatientService().getPatient(2), Context.getConceptService().getConcept(7777002),
                        Context.getEncounterService().getEncounter(3),
                        calculationsTestsCache.getDate("2008-07-01 00:00:00.0", calculationsTestsCache.DATE_FORMAT),
                        (Location) getEvaluationContext().getFromCache("location"), Context.getConceptService().getConcept(7777003));
        CalculationResultMap evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
        Assert.assertEquals(calculationsTestsCache.getDate("2008-07-01 00:00:00.0", calculationsTestsCache.DATE_FORMAT),
                        evaluatedResult.get(2).getValue());
        // the rest should not be touched
        matchOtherResultsExcept(2, evaluatedResult);

        // initiating ART by historical start date in addition to HIV enrollment
        calculationsTestsCache.createBasicObs(Context.getPatientService().getPatient(2), Context.getConceptService().getConcept(7777005),
                        Context.getEncounterService().getEncounter(3),
                        calculationsTestsCache.getDate("2019-01-01 00:00:00.0", calculationsTestsCache.DATE_FORMAT),
                        (Location) getEvaluationContext().getFromCache("location"),
                        calculationsTestsCache.getDate("2008-06-01 00:00:00.0", calculationsTestsCache.DATE_FORMAT));
        evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
        Assert.assertEquals(calculationsTestsCache.getDate("2008-06-01 00:00:00.0", calculationsTestsCache.DATE_FORMAT),
                        evaluatedResult.get(2).getValue());
        // the rest should not be touched
        matchOtherResultsExcept(2, evaluatedResult);

        // initiating ART by historical start date in addition to HIV enrollment
        calculationsTestsCache.createBasicObs(Context.getPatientService().getPatient(2), Context.getConceptService().getConcept(7777002),
                        Context.getEncounterService().getEncounter(3),
                        calculationsTestsCache.getDate("2019-01-02 00:00:00.0", calculationsTestsCache.DATE_FORMAT),
                        (Location) getEvaluationContext().getFromCache("location"), Context.getConceptService().getConcept(7777004));
        evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
        Assert.assertEquals(calculationsTestsCache.getDate("2008-06-01 00:00:00.0", calculationsTestsCache.DATE_FORMAT),
                        evaluatedResult.get(2).getValue());
        // the rest should not be touched
        matchOtherResultsExcept(2, evaluatedResult);

        // TODO does the results have to contain every patient from the cohort
        // received with empty values!
        Assert.assertEquals(getCohort().size(), evaluatedResult.size());
    }
}
