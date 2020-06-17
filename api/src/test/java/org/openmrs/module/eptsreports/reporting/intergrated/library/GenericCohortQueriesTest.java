package org.openmrs.module.eptsreports.reporting.intergrated.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class GenericCohortQueriesTest extends DefinitionsTest {
  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TestsHelper testsHelper;

  private ConceptService conceptService;

  private EncounterService encounterService;

  private ProgramWorkflowService programWorkflowService;

  private LocationService locationService;

  @Before
  public void init() throws Exception {
    executeDataSet("metadata.xml");
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
                genericCohortQueries.hasCodedObs(conceptService.getConcept(18), null),
                Collections.<Parameter, Object>emptyMap())
            .getMemberIds());
  }

  @Test
  public void hasCodedObsShouldMatchAnswers() throws EvaluationException {
    assertEquals(
        new HashSet<>(Arrays.asList(7)),
        evaluateCohortDefinition(
                genericCohortQueries.hasCodedObs(
                    conceptService.getConcept(18), Arrays.asList(conceptService.getConcept(7))),
                Collections.<Parameter, Object>emptyMap())
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
                Collections.<Parameter, Object>emptyMap())
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
                Collections.<Parameter, Object>emptyMap())
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
                Collections.<Parameter, Object>emptyMap())
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(2, 6, 7, 8, 100, 101)),
        evaluateCohortDefinition(
                genericCohortQueries.generalSql(
                    "nonVoidedPatients", "select patient_id from patient where voided = false"),
                Collections.<Parameter, Object>emptyMap())
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
                /* in hiv care program at 28 state: */ 100,
                /* in art program at 29 state: */ 101)),
        evaluateCohortDefinition(genericCohortQueries.getBaseCohort(), parameters).getMemberIds());
  }

  @Test
  public void getPatientsBasedOnPatientStatesShouldRetrieveAllMatchingPatients()
      throws EvaluationException {
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("startDate", "start date", Date.class),
        testsHelper.getDate("2016-05-06 12:26:00.0"));
    parameters.put(
        new Parameter("endDate", "end date", Date.class),
        testsHelper.getDate("2019-05-06 12:26:00.0"));
    parameters.put(
        new Parameter("location", "Location", Location.class), locationService.getLocation(1));

    assertEquals(
        new HashSet<>(Arrays.asList(100)),
        evaluateCohortDefinition(
                genericCohortQueries.getPatientsBasedOnPatientStates(10, 28), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(101)),
        evaluateCohortDefinition(
                genericCohortQueries.getPatientsBasedOnPatientStates(11, 29), parameters)
            .getMemberIds());
  }

  @Test
  public void getPatientsBasedOnPatientStatesBeforeDateShouldRetrieveAllMatchingPatients()
      throws EvaluationException {
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("endDate", "end date", Date.class),
        testsHelper.getDate("2019-05-06 12:26:00.0"));
    parameters.put(
        new Parameter("location", "Location", Location.class), locationService.getLocation(1));

    assertEquals(
        new HashSet<>(Arrays.asList(100)),
        evaluateCohortDefinition(
                genericCohortQueries.getPatientsBasedOnPatientStatesBeforeDate(10, 28), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(101)),
        evaluateCohortDefinition(
                genericCohortQueries.getPatientsBasedOnPatientStatesBeforeDate(11, 29), parameters)
            .getMemberIds());
  }

  private void testDeathCohort(CohortDefinition cohortDefinition) throws EvaluationException {
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("onOrBefore", "end date", Date.class),
        testsHelper.getDate("2019-05-06 12:26:00.0"));
    parameters.put(
        new Parameter("endDate", "end date", Date.class),
        testsHelper.getDate("2019-05-06 12:26:00.0"));
    parameters.put(
        new Parameter("location", "Location", Location.class), locationService.getLocation(1));
    assertEquals(
        new HashSet<>(Arrays.asList(/* person dead: */ 100, /* exited by death */ 101)),
        evaluateCohortDefinition(cohortDefinition, parameters).getMemberIds());
  }

  @Test
  public void getDeceasedPatientsShouldRetrieveAllMatchingPatients() throws EvaluationException {
    CohortDefinition deceased = genericCohortQueries.getDeceasedPatients();
    assertNotNull(deceased);
    testDeathCohort(deceased);
  }

  @Test
  public void getDeceasedPatientsBeforeDateShouldRetrieveAllMatchingPatients()
      throws EvaluationException {
    CohortDefinition deceased = genericCohortQueries.getDeceasedPatientsBeforeDate();
    assertNotNull(deceased);
    testDeathCohort(deceased);
  }

  @Test
  public void hasNumericObsShouldMatchAllArguments() throws EvaluationException {
    assertEquals(
        new HashSet<>(Arrays.asList(7)),
        evaluateCohortDefinition(
                genericCohortQueries.hasNumericObs(
                    conceptService.getConcept(5089),
                    TimeModifier.ANY,
                    RangeComparator.GREATER_THAN,
                    49.0,
                    null,
                    null,
                    Arrays.asList(encounterService.getEncounterType(1))),
                Collections.<Parameter, Object>emptyMap())
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList()),
        evaluateCohortDefinition(
                genericCohortQueries.hasNumericObs(
                    conceptService.getConcept(5089),
                    TimeModifier.ANY,
                    RangeComparator.GREATER_THAN,
                    62.0,
                    null,
                    null,
                    Arrays.asList(encounterService.getEncounterType(1))),
                Collections.<Parameter, Object>emptyMap())
            .getMemberIds());
  }

  @Test
  public void hasNumericObsShouldMatchAllArgumentsAndParameters() throws EvaluationException {
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("onOrAfter", "start date", Date.class),
        testsHelper.getDate("2007-05-06 12:26:00.0"));
    parameters.put(
        new Parameter("onOrBefore", "end date", Date.class),
        testsHelper.getDate("2019-05-06 12:26:00.0"));
    parameters.put(
        new Parameter("locationList", "Location", Location.class), locationService.getLocation(2));
    assertEquals(
        new HashSet<>(Arrays.asList(7)),
        evaluateCohortDefinition(
                genericCohortQueries.hasNumericObs(
                    conceptService.getConcept(5089),
                    TimeModifier.ANY,
                    RangeComparator.GREATER_THAN,
                    60.0,
                    null,
                    null,
                    Arrays.asList(encounterService.getEncounterType(1))),
                parameters)
            .getMemberIds());
  }
}
