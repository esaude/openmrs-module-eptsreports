package org.openmrs.module.eptsreports.reporting.calculation;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResultMap;

@Ignore
public class OnArtForMoreThanXmonthsCalcultionTest extends BasePatientCalculationTest {
	
	@Override
	public PatientCalculation getCalculation() {
		return null;
	}
	
	@Override
	public Collection<Integer> getCohort() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CalculationResultMap getResult() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Before
	public void initialise() {
		// 01/30/2019 @ 12:00am (UTC)
		Date now = new Date(1548806400);
		Map<String, Object> cacheEntries = new HashMap<String, Object>();
		cacheEntries.put("location", Context.getLocationService().getLocation(1));
		
		setEvaluationContext(now);
		setEvaluationContext(cacheEntries);
	}
	
}
