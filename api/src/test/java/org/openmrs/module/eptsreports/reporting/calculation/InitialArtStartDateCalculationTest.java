package org.openmrs.module.eptsreports.reporting.calculation;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.InitialArtStartDateCalculation;

public class InitialArtStartDateCalculationTest extends BasePatientCalculationTest {
	
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
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");
			
			// initiated ART by hiv enrolment
			map.put(2, new SimpleResult(new Timestamp(sdf.parse("2008-08-01 00:00:00.0").getTime()), calculation,
			        getEvaluationContext()));
			// initiated ART by starting ARV plan observation
			map.put(6, new SimpleResult(new Timestamp(sdf.parse("2019-01-19 00:00:00.0").getTime()), calculation,
			        getEvaluationContext()));
			// initiated ART by historical start date observation
			map.put(7, new SimpleResult(new Timestamp(sdf.parse("2019-01-18 00:00:00.0").getTime()), calculation,
			        getEvaluationContext()));
			// initiated ART by first phamarcy encounter observation
			map.put(8, new SimpleResult(new Timestamp(sdf.parse("2019-01-21 00:00:00.0").getTime()), calculation,
			        getEvaluationContext()));
			// initiated ART by ARV transfer in observation
			map.put(999, new SimpleResult(new Timestamp(sdf.parse("2019-01-20 00:00:00.0").getTime()), calculation,
			        getEvaluationContext()));
			map.put(432, new SimpleResult("", calculation, getEvaluationContext()));
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		// TODO add other unit tests for scenarios where there are more than one
		// entry points into ART and non matched patients
		return map;
	}
	
	@Before
	public void initialise() {
		PatientCalculation calculation = getCalculation();
		
		stubEptsCalculations(calculation);
		stubEptsCalculationUtils(calculation);
	}
	
}
