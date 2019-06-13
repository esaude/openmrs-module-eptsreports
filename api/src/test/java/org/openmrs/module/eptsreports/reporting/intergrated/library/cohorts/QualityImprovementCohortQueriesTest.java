package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
 
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.QualityImprovementCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class QualityImprovementCohortQueriesTest extends DefinitionsTest {

	@Autowired
	private QualityImprovementCohortQueries qualityImprovementCohortQueries;

	 

	@Before
	public void setup() throws Exception {
		executeDataSet("qualityImprovement1-patientDataTest.xml");
		executeDataSet("qualityImprovement2-globalPropertyDataTest.xml");
		executeDataSet("qualityImprovement3-conceptDataTest.xml");
		executeDataSet("qualityImprovement4-programDataTest.xml");
		executeDataSet("qualityImprovement5-encounterAndObsDataTest.xml");

	}

	@Test
	public void getPacientsEnrolledInTBProgram() throws EvaluationException {

		CohortDefinition cohortDefinition = qualityImprovementCohortQueries.getPacientsEnrolledInTBProgram();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		assertTrue(evaluatedCohort.getMemberIds().contains(1002));
	}

	@Test
	public void getPatientWithAtLeastOneEncounterInPeriod() throws EvaluationException {

		CohortDefinition cohortDefinition = qualityImprovementCohortQueries.getPatientWithAtLeastOneEncounterInPeriod();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		assertTrue(evaluatedCohort.getMemberIds().contains(1001));
	}

	@Test
	public void getPatientsNotifiedSarcomaKaposi() throws EvaluationException {
		CohortDefinition cohortDefinition = qualityImprovementCohortQueries.getPatientsNotifiedSarcomaKaposi();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		assertTrue(evaluatedCohort.getMemberIds().contains(1004));
	}

	@Test
	public void getPatientWhoCameOutARTProgramFinalPeriod() throws EvaluationException {

		CohortDefinition cohortDefinition = qualityImprovementCohortQueries.getPatientWhoCameOutARTProgramFinalPeriod();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		assertTrue(evaluatedCohort.getMemberIds().contains(1001));
	}

	@Test
	public void getPatientEnrolledARTProgramFinalPeriod() throws EvaluationException {
		CohortDefinition cohortDefinition = qualityImprovementCohortQueries.getPatientEnrolledARTProgramFinalPeriod();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		assertTrue(evaluatedCohort.getMemberIds().contains(1001));
	}

	@Test
	public void getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriodAndCompleted() throws EvaluationException {
		CohortDefinition cohortDefinition = qualityImprovementCohortQueries
				.getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriodAndCompleted();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("startDate", "Start Date", Date.class), DateUtil.getDateTime(2019, 01, 10));
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());
		parameters.put(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class), getDataFinalAvaliacao());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		assertTrue(evaluatedCohort.getMemberIds().contains(1008));
	}

	@Test
	public void getPatientWhoWereInARVTreatmentFinalPeriod() throws EvaluationException {

		CohortDefinition cohortDefinition = qualityImprovementCohortQueries
				.getPatientWhoWereInARVTreatmentFinalPeriod();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		assertEquals(1, evaluatedCohort.getMemberIds().size());
		assertTrue(evaluatedCohort.getMemberIds().contains(1001));
	}

	@Test
	public void getInfantPatientsEnrolledInTarvSample() throws EvaluationException {

		CohortDefinition cohortDefinition = qualityImprovementCohortQueries.getInfantPatientsEnrolledInTarvSample();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("onOrAfter", "Start Date", Date.class), this.getStartDate());
		parameters.put(new Parameter("onOrBefore", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("locationList", "Location", Location.class), getLocation());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		 assertNotNull(evaluatedCohort.getMemberIds());
	}

	@Test
	public void getFemalePatients() throws EvaluationException {
		CohortDefinition cohortDefinition = qualityImprovementCohortQueries.getFemalePatients();
		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, null);

		assertEquals(8, evaluatedCohort.getMemberIds().size());
		assertTrue(evaluatedCohort.getMemberIds().contains(1001));
		assertTrue(evaluatedCohort.getMemberIds().contains(1003));
		assertTrue(evaluatedCohort.getMemberIds().contains(1006));
		assertTrue(evaluatedCohort.getMemberIds().contains(1007));
		assertTrue(evaluatedCohort.getMemberIds().contains(1009));
		assertTrue(evaluatedCohort.getMemberIds().contains(1010));
	}

	@Test
	public void getPregnantPatientEnrolledInTARVService() throws EvaluationException {
		CohortDefinition cohortDefinition = qualityImprovementCohortQueries.getPregnantPatientEnrolledInTARVService();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		assertEquals(1, evaluatedCohort.getMemberIds().size());
		assertTrue(evaluatedCohort.getMemberIds().contains(1003));
	}
	
	@Test
	public void getBaseCohort() throws EvaluationException {
		CohortDefinition cohortDefinition = qualityImprovementCohortQueries
				.getBaseCohort();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		assertEquals(3, evaluatedCohort.getMemberIds().size());
		assertTrue(evaluatedCohort.getMemberIds().contains(1001));
		assertTrue(evaluatedCohort.getMemberIds().contains(1003));
		assertTrue(evaluatedCohort.getMemberIds().contains(1007));
 		
	}
	
	  
	private Date getDataFinalAvaliacao() {
		return DateUtil.getDateTime(2019, 5, 26);
	}
 

	@Override
	public Date getStartDate() {
		return DateUtil.getDateTime(2010, 5, 10);
	}

	@Override
	public Date getEndDate() {
		return DateUtil.getDateTime(2019, 5, 26);
	}
}
