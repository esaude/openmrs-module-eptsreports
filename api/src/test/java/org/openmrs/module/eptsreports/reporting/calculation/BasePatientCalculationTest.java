package org.openmrs.module.eptsreports.reporting.calculation;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.api.EptsReportsService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import mockit.Mock;
import mockit.MockUp;

public abstract class BasePatientCalculationTest extends BaseModuleContextSensitiveTest {
	
	PatientCalculationService service;
	
	private PatientCalculationContext evaluationContext;
	
	abstract PatientCalculation getCalculation();
	
	abstract Collection<Integer> getCohort();
	
	abstract CalculationResultMap getResult();
	
	@Before
	public void setUp() throws Exception {
		service = Context.getService(PatientCalculationService.class);
		evaluationContext = service.createCalculationContext();
		executeDataSet("calculationsTest.xml");
	}
	
	@Test
	public void evaluate_shouldReturnMatchedResultMapBySizeAndPrintOutGivenCalculationCohort() {
		Assert.assertNotNull(Context.getService(EptsReportsService.class));
		CalculationResultMap result = getResult();
		Assert.assertNotNull(getCalculation());
		Assert.assertNotNull(getCohort());
		Assert.assertNotNull(result);
		Assert.assertEquals(result.size(), service.evaluate(getCohort(), getCalculation()).size());
		CalculationResultMap evaluatedResult = service.evaluate(getCohort(), getCalculation(), getEvaluationContext());
		Assert.assertEquals(result.toString(), evaluatedResult.toString());
	}
	
	void setEvaluationContext(Date now) {
		if (now == null) {
			now = new Date();
		}
		evaluationContext.setNow(now);
	}
	
	@SuppressWarnings("rawtypes")
	void setEvaluationContext(Map<String, Object> cacheEntries) {
		if (cacheEntries != null && !cacheEntries.isEmpty()) {
			for (Map.Entry e : cacheEntries.entrySet()) {
				evaluationContext.addToCache((String) e.getKey(), e.getValue());
			}
		}
	}
	
	PatientCalculationContext getEvaluationContext() {
		return evaluationContext;
	}
	
	/**
	 * Mocks {@link EptsCalculations} with org.jmockit
	 */
	void stubEptsCalculations(final PatientCalculation calculation) {
		new MockUp<EptsCalculations>() {
			
			@Mock
			public CalculationResultMap firstPatientProgram(Program program, Location location, Collection<Integer> cohort,
			        PatientCalculationContext context) {
				CalculationResultMap map = new CalculationResultMap();
				for (int i : cohort) {
					Patient p = Context.getPatientService().getPatient(i);
					if (p != null) {
						for (PatientProgram pp : Context.getProgramWorkflowService().getPatientPrograms(p, program, null,
						    null, null, null, false)) {
							map.put(i, new SimpleResult(pp, calculation, context));
						}
					}
				}
				return map;
			}
			
			@Mock
			public CalculationResultMap firstObs(Concept question, Concept answer, Location location,
			        boolean sortByDatetime, Collection<Integer> cohort, PatientCalculationContext context) {
				CalculationResultMap map = new CalculationResultMap();
				for (int i : cohort) {
					Patient p = Context.getPatientService().getPatient(i);
					if (p != null) {
						Obs matchedObs = null;
						for (Obs o : Context.getObsService().getObservationsByPersonAndConcept(p, question)) {
							// either with mached value or boolean
							if ((answer != null && answer.equals(o.getValueCoded()))
							        || question.getDatatype().getUuid().equals("8d4a5cca-c2cc-11de-8d13-0010c6dffd0f")) {
								matchedObs = o;
								break;
							}
						}
						if (matchedObs != null) {
							map.put(i, new SimpleResult(matchedObs, calculation, context));
						}
					}
				}
				return map;
			}
			
			@Mock
			public CalculationResultMap firstEncounter(EncounterType encounterType, Collection<Integer> cohort,
			        Location location, PatientCalculationContext context) {
				CalculationResultMap map = new CalculationResultMap();
				
				for (int i : cohort) {
					for (Encounter e : Context.getEncounterService().getEncountersByPatientId(i)) {
						if (encounterType.equals(e.getEncounterType())) {
							map.put(i, new SimpleResult(e, calculation, context));
						}
					}
				}
				return map;
			}
		};
	}
	
	/**
	 * Mocks {@link EptsCalculationUtils} with org.jmockit
	 */
	void stubEptsCalculationUtils(final PatientCalculation calculation) {
		new MockUp<EptsCalculationUtils>() {
			
			@Mock
			public Obs obsResultForPatient(CalculationResultMap results, Integer patientId) {
				return (results.isEmpty() || !results.containsKey(patientId)) ? null : (Obs) results.get(patientId)
				        .getValue();
			}
			
			@Mock
			public Encounter encounterResultForPatient(CalculationResultMap results, Integer patientId) {
				return (results.isEmpty() || !results.containsKey(patientId)) ? null : (Encounter) results.get(patientId)
				        .getValue();
			}
		};
	}
}
