package org.openmrs.module.eptsreports.reporting.intergrated.calculation.generic;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.AgeOnArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class AgeOnArtStartDateCalculationTest extends BasePatientCalculationTest {
	
	@Override
	public PatientCalculation getCalculation() {
		return Context.getRegisteredComponents(AgeOnArtStartDateCalculation.class).get(0);
	}
	
	@Override
	public Collection<Integer> getCohort() {
		return Arrays.asList(new Integer[] {});
	}
	
	@Override
	public CalculationResultMap getResult() {
		CalculationResultMap map = new CalculationResultMap();
		return map;
	}
	
	@Test
	public void shouldBeTrueIfPatientIsWithingAgeBracket() {
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("minAge", 10);
		parameterValues.put("maxAge", 14);
		CalculationResultMap results = service.evaluate(Arrays.asList(1777001), getCalculation(), parameterValues,
		    getEvaluationContext());
		BooleanResult result = (BooleanResult) results.get(1777001);
		Assert.assertNotNull(result);
		Assert.assertEquals(Boolean.TRUE, result.getValue());
	}
	
	@Test
	public void shouldBeNullResultIfPatientHasNotStartedArt() {
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		final int patientId = 10;
		parameterValues.put("minAge", patientId);
		parameterValues.put("maxAge", 14);
		CalculationResultMap results = service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues,
		    getEvaluationContext());
		BooleanResult result = (BooleanResult) results.get(patientId);
		Assert.assertNull(result);
	}
	
	@Test
	public void shouldBeFalseIfPatientIsWithingAgeBracket() {
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("minAge", 11);
		parameterValues.put("maxAge", 14);
		final int patientId = 1777001;
		CalculationResultMap results = service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues,
		    getEvaluationContext());
		BooleanResult result = (BooleanResult) results.get(patientId);
		Assert.assertNotNull(result);
		Assert.assertEquals(Boolean.FALSE, result.getValue());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldRaiseExceptionIfMinAgeParameterIsNotSpecified() {
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("maxAge", 14);
		service.evaluate(Arrays.asList(1777001), getCalculation(), parameterValues, getEvaluationContext());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldRaiseExceptionIfMaxAgeParameterIsNotSpecified() {
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("minAge", 11);
		service.evaluate(Arrays.asList(1777001), getCalculation(), parameterValues, getEvaluationContext());
	}
	
	@Test
	public void shouldBeNullIfPatientStartedArtBeforeWasBorn() {
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("minAge", 10);
		parameterValues.put("maxAge", 14);
		final int patientId = 1777002;
		CalculationResultMap results = service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues,
		    getEvaluationContext());
		BooleanResult result = (BooleanResult) results.get(patientId);
		Assert.assertNull(result);
	}
	
	@Test
	public void shouldBeNullIfBirthdateIsMissing() {
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("minAge", 10);
		parameterValues.put("maxAge", 14);
		final int patientId = 1777003;
		CalculationResultMap results = service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues,
		    getEvaluationContext());
		BooleanResult result = (BooleanResult) results.get(patientId);
		Assert.assertNull(result);
	}
	
	@Test
	public void shouldBeNullIfPatientHasNotStartedArt() {
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("minAge", 10);
		parameterValues.put("maxAge", 14);
		final int patientId = 1777004;
		CalculationResultMap results = service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues,
		    getEvaluationContext());
		BooleanResult result = (BooleanResult) results.get(patientId);
		Assert.assertNull(result);
	}

	@Before
	public void initialise() throws Exception {
		executeDataSet("genericTest.xml");
	}
	
}
