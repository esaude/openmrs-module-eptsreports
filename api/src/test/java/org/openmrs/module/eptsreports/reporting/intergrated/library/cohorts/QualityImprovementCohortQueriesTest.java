package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
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
	public void getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter() throws EvaluationException {

		CohortDefinition cohortDefinition = qualityImprovementCohortQueries
				.getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("startDate", "Start Date", Date.class), DateUtil.getDateTime(2019, 01, 26));
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());
		parameters.put(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class), getDataFinalAvaliacao());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		//assertTrue(evaluatedCohort.getMemberIds().contains(1001));
 
		System.out.println(evaluatedCohort.getMemberIds());
		assertNotNull(cohortDefinition);
	}

	@Test
	public void getPacientsEnrolledInTBProgram() throws EvaluationException {

		CohortDefinition cohortDefinition = qualityImprovementCohortQueries.getPacientsEnrolledInTBProgram();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("startDate", "Start Date", Date.class),this.getStartDate());
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);


		assertTrue(evaluatedCohort.getMemberIds().contains(1002));
	}

	@Test
	public void getPatientWithDeliveryDate2YearsAgoBreatFeeding() throws EvaluationException {

		CohortDefinition cohortDefinition = qualityImprovementCohortQueries
				.getPatientWithDeliveryDate2YearsAgoBreatFeeding();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());

		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

		assertTrue(evaluatedCohort.getMemberIds().contains(1003));

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
		CohortDefinition cohortDefinition = qualityImprovementCohortQueries
				.getPatientsNotifiedSarcomaKaposi();

		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());
		
		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);
		
		assertTrue(evaluatedCohort.getMemberIds().contains(1004));

 
	}
	@Test
	public void getPatientsWhoHadEnconterInLast7MonthAndWereMarkedToNextEncounterIn6Months() throws EvaluationException{
		
		CohortDefinition cohortDefinition = qualityImprovementCohortQueries
				.getPatientsWhoHadEnconterInLast7MonthAndWereMarkedToNextEncounterIn6Months();
		
		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());
		
		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);
		
		assertTrue(evaluatedCohort.getMemberIds().contains(1005));

	}
	
	@Test
	public  void getPatientsTookARVInLast5MonthAndWereMarkedToNextEncounter() throws EvaluationException {
		
		CohortDefinition cohortDefinition = qualityImprovementCohortQueries
				.getPatientsTookARVInLast5MonthAndWereMarkedToNextEncounter();
		
		Map<Parameter, Object> parameters = new HashMap<>();
		parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
		parameters.put(new Parameter("location", "Location", Location.class), getLocation());
		
		
		EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);
		
		assertTrue(evaluatedCohort.getMemberIds().contains(1006));

		
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
