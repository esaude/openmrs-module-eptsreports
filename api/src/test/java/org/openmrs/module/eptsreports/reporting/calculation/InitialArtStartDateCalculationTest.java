package org.openmrs.module.eptsreports.reporting.calculation;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.InitialArtStartDateCalculation;

public class InitialArtStartDateCalculationTest extends BasePatientCalculationTest {
	
	private SimpleDateFormat sdf;
	
	@Override
	public PatientCalculation getCalculation() {
		return new InitialArtStartDateCalculation();
	}
	
	@Override
	public Collection<Integer> getCohort() {
		return Arrays.asList(new Integer[] { 2, 6, 7, 8, 999, 432 });
	}
	
	@Override
	public CalculationResultMap getResult() {
		PatientCalculation calculation = getCalculation();
		CalculationResultMap map = new CalculationResultMap();
		
		// initiated ART by hiv enrolment
		map.put(2, new SimpleResult(new Timestamp(getDate("2008-08-01 00:00:00.0", sdf).getTime()), calculation,
		        getEvaluationContext()));
		// initiated ART by starting ARV plan observation
		map.put(6, new SimpleResult(new Timestamp(getDate("2019-01-19 00:00:00.0", sdf).getTime()), calculation,
		        getEvaluationContext()));
		// initiated ART by historical start date observation
		map.put(7, new SimpleResult(new Timestamp(getDate("2019-01-18 00:00:00.0", sdf).getTime()), calculation,
		        getEvaluationContext()));
		// initiated ART by first phamarcy encounter observation
		map.put(8, new SimpleResult(new Timestamp(getDate("2019-01-21 00:00:00.0", sdf).getTime()), calculation,
		        getEvaluationContext()));
		// initiated ART by ARV transfer in observation
		map.put(999, new SimpleResult(new Timestamp(getDate("2019-01-20 00:00:00.0", sdf).getTime()), calculation,
		        getEvaluationContext()));
		map.put(432, new SimpleResult("", calculation, getEvaluationContext()));
		
		return map;
	}
	
	@Before
	public void initialise() {
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");
		PatientCalculation calculation = getCalculation();
		Map<String, Object> cacheEntries = new HashMap<String, Object>();
		cacheEntries.put("location", Context.getLocationService().getLocation(1));
		
		setEvaluationContext(getDate("2019-01-30 00:00:00.0", sdf));
		setEvaluationContext(cacheEntries);
		stubEptsCalculations(calculation);
		stubEptsCalculationUtils(calculation);
	}
	
	@Test
	public void evaluate_shouldConsiderLeastInitiationDateForMultipleEntryPoints() {
		// initiating ART by starting ARV plan observation in addition to HIV
		// enrollment
		createBasicObs(Context.getPatientService().getPatient(2), Context.getConceptService().getConcept(7777002), Context
		        .getEncounterService().getEncounter(3), getDate("2008-07-01 00:00:00.0", sdf),
		    (Location) getEvaluationContext().getFromCache("location"), Context.getConceptService().getConcept(7777003));
		CalculationResultMap evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
		Assert.assertEquals(getDate("2008-07-01 00:00:00.0", sdf), evaluatedResult.get(2).getValue());
		// the rest should not be touched
		matchOtherResultsExcept(2, evaluatedResult);
		
		// initiating ART by historical start date in addition to HIV enrollment
		createBasicObs(Context.getPatientService().getPatient(2), Context.getConceptService().getConcept(7777005), Context
		        .getEncounterService().getEncounter(3), getDate("2019-01-01 00:00:00.0", sdf),
		    (Location) getEvaluationContext().getFromCache("location"), getDate("2008-06-01 00:00:00.0", sdf));
		evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
		Assert.assertEquals(getDate("2008-06-01 00:00:00.0", sdf), evaluatedResult.get(2).getValue());
		// the rest should not be touched
		matchOtherResultsExcept(2, evaluatedResult);
		
		// initiating ART by historical start date in addition to HIV enrollment
		createBasicObs(Context.getPatientService().getPatient(2), Context.getConceptService().getConcept(7777002), Context
		        .getEncounterService().getEncounter(3), getDate("2019-01-02 00:00:00.0", sdf),
		    (Location) getEvaluationContext().getFromCache("location"), Context.getConceptService().getConcept(7777004));
		evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
		Assert.assertEquals(getDate("2008-06-01 00:00:00.0", sdf), evaluatedResult.get(2).getValue());
		// the rest should not be touched
		matchOtherResultsExcept(2, evaluatedResult);
		
		// TODO does the results have to contain every patient from the cohort
		// received with empty values!
		Assert.assertEquals(getCohort().size(), evaluatedResult.size());
	}
}
