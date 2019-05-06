package org.openmrs.module.eptsreports.reporting.intergrated.library;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GenericCohortQueriesTest extends DefinitionsTest {
  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TestsHelper testsHelper;

  private ConceptService conceptService;

  private EncounterService encounterService;

  private ProgramWorkflowService programWorkflowService;

  private LocationService locationService;

  @Before
  public void init() throws Exception {
    executeLargeDataSet("genericCohortQueriesTest.xml");
    conceptService = Context.getConceptService();
    encounterService = Context.getEncounterService();
    programWorkflowService = Context.getProgramWorkflowService();
    locationService = Context.getLocationService();
  }

  @Test
  public void hasCodedObsShouldMatchQuestion() throws EvaluationException {
    assertEquals(
        new HashSet<>(Arrays.asList(7)),
        evaluateCohortDefinition(
                genericCohortQueries.hasCodedObs(conceptService.getConcept(18), null), null)
            .getMemberIds());
  }

  @Test
  public void hasCodedObsShouldMatchAnswers() throws EvaluationException {
    assertEquals(
        new HashSet<>(Arrays.asList(7)),
        evaluateCohortDefinition(
                genericCohortQueries.hasCodedObs(
                    conceptService.getConcept(18), Arrays.asList(conceptService.getConcept(7))),
                null)
            .getMemberIds());
  }

  @Test
  public void hasCodedObsShouldMatchEncounterType() throws EvaluationException {
    assertEquals(
        new HashSet<>(Arrays.asList(7)),
        evaluateCohortDefinition(
                genericCohortQueries.hasCodedObs(
                    conceptService.getConcept(18),
                    null,
                    null,
                    Arrays.asList(encounterService.getEncounterType(1)),
                    null),
                null)
            .getMemberIds());
  }

  @Test
  public void hasCodedObsShouldMatchAnswersAndEncounterType() throws EvaluationException {
    assertEquals(
        new HashSet<>(Arrays.asList(7)),
        evaluateCohortDefinition(
                genericCohortQueries.hasCodedObs(
                    conceptService.getConcept(18),
                    null,
                    SetComparator.IN,
                    Arrays.asList(encounterService.getEncounterType(1)),
                    Arrays.asList(conceptService.getConcept(7))),
                null)
            .getMemberIds());
  }

  @Test
  public void createInProgramShouldReturnPatientsInAGivenProgram() throws EvaluationException {
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("onOrBefore", "OnOrBefore Date", Date.class),
        DateUtil.getDateTime(2019, 4, 30));
    assertEquals(
        new HashSet<>(Arrays.asList(2, 7)),
        evaluateCohortDefinition(
                genericCohortQueries.createInProgram("inTb", programWorkflowService.getProgram(2)),
                parameters)
            .getMemberIds());
    // filter by location
    parameters.put(
        new Parameter("locations", "Locations", Location.class), locationService.getLocation(1));
    assertEquals(
        new HashSet<>(Arrays.asList(7)),
        evaluateCohortDefinition(
                genericCohortQueries.createInProgram(
                    "inTbAtLocation", programWorkflowService.getProgram(2)),
                parameters)
            .getMemberIds());
    parameters.clear();
    parameters.put(
        new Parameter("onOrBefore", "OnOrBefore Date", Date.class),
        testsHelper.getDate("2008-08-01 00:00:00.0"));
    assertEquals(
        new HashSet<>(Arrays.asList(7)),
        evaluateCohortDefinition(
                genericCohortQueries.createInProgram(
                    "inTbOnOrBefore", programWorkflowService.getProgram(2)),
                parameters)
            .getMemberIds());
  }

  @Test
  public void generalSqlShouldMatchAGivenQuery() throws EvaluationException {
    assertEquals(
        new HashSet<>(Arrays.asList(2, 6, 7, 8, 999, 432, 100, 101)),
        evaluateCohortDefinition(
                genericCohortQueries.generalSql("allPatients", "select patient_id from patient"),
                null)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(2, 6, 7, 8, 100, 101)),
        evaluateCohortDefinition(
                genericCohortQueries.generalSql(
                    "nonVoidedPatients", "select patient_id from patient where voided = false"),
                null)
            .getMemberIds());
  }

  @Test
  public void generalSqlShouldMatchAGivenQueryWithParameters() throws EvaluationException {
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("startDate", "start date", Date.class),
        testsHelper.getDate("2008-08-18 12:25:00.0"));
    parameters.put(
        new Parameter("endDate", "end date", Date.class),
        testsHelper.getDate("2008-08-18 12:26:00.0"));
    assertEquals(
        new HashSet<>(Arrays.asList(6, 7)),
        evaluateCohortDefinition(
                genericCohortQueries.generalSql(
                    "patientsChangedBetween",
                    "select patient_id from patient where date_changed between :startDate and :endDate"),
                parameters)
            .getMemberIds());
    parameters.put(
        new Parameter("location", "Facility", Location.class), locationService.getLocation(1));
    assertEquals(
        new HashSet<>(Arrays.asList(7)),
        evaluateCohortDefinition(
                genericCohortQueries.generalSql(
                    "enrolledIntoTbAtLocation",
                    "select patient_id from patient_program where program_id = 2 and location_id = :location"),
                parameters)
            .getMemberIds());
  }

  @Test
  public void getBaseCohortShouldRetrieveAllMatchingPatients() throws EvaluationException {
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("endDate", "end date", Date.class),
        testsHelper.getDate("2019-05-06 12:26:00.0"));
    parameters.put(
        new Parameter("location", "Facility", Location.class), locationService.getLocation(1));

    assertEquals(
        new HashSet<>(
            Arrays.asList(
                /* with either adult on ped initial encounters: */ 2,
                6,
                /* in hiv care program: */ 7,
                /* in hiv care program at 58 state: */ 100,
                /* in art program at 29 state: */ 101)),
        evaluateCohortDefinition(genericCohortQueries.getBaseCohort(), parameters).getMemberIds());
  }
}
