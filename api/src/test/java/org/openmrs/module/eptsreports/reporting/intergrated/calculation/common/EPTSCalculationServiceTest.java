package org.openmrs.module.eptsreports.reporting.intergrated.calculation.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class EPTSCalculationServiceTest extends BaseModuleContextSensitiveTest {

  private EPTSCalculationService eptsCalculationService;

  private PatientCalculationContext context;

  @Before
  public void initialise() throws Exception {
    context = Context.getService(PatientCalculationService.class).createCalculationContext();

    eptsCalculationService = Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    context.setNow(new Date());

    executeDataSet("metadata.xml");
    executeDataSet("pvlsTest.xml");
  }

  /**
   * Using {@link TimeQualifier}3
   *
   * @see EPTSCalculationService#getObs(Concept, List, java.util.Collection, List, List,
   *     TimeQualifier, Date, PatientCalculationContext)
   */
  @Test
  public void shouldGetFirstObs() {

    Concept concept = new Concept(5089);
    List<Integer> cohort = Arrays.asList(7);
    List<Location> locationList = Arrays.asList(new Location(1));

    CalculationResultMap obsMap =
        this.eptsCalculationService.getObs(
            concept, null, cohort, locationList, null, TimeQualifier.FIRST, null, context);

    Obs obs = EptsCalculationUtils.resultForPatient(obsMap, 7);

    Assert.assertNotNull(obs);
    Assert.assertEquals(Integer.valueOf(7), obs.getId());
    Assert.assertEquals(concept.getId(), obs.getConcept().getId());
    Assert.assertEquals(locationList.get(0).getLocationId(), obs.getLocation().getId());
    Assert.assertEquals(Integer.valueOf(7), obs.getPerson().getId());
  }

  /**
   * @see EPTSCalculationService#getObs(Concept, List, Collection, List, List, TimeQualifier, Date,
   *     PatientCalculationContext)
   */
  @Test
  public void shouldGetLastObs() {

    Concept concept = new Concept(5089);
    List<Integer> cohort = Arrays.asList(7);
    List<Location> locationList = Arrays.asList(new Location(1));

    CalculationResultMap obsMap =
        this.eptsCalculationService.getObs(
            concept, null, cohort, locationList, null, TimeQualifier.LAST, null, context);

    Obs obs = EptsCalculationUtils.resultForPatient(obsMap, 7);

    Assert.assertNotNull(obs);
    Assert.assertEquals(Integer.valueOf(10), obs.getId());
    Assert.assertEquals(concept.getId(), obs.getConcept().getId());
    Assert.assertEquals(locationList.get(0).getLocationId(), obs.getLocation().getId());
    Assert.assertEquals(Integer.valueOf(7), obs.getPerson().getId());
  }

  /**
   * @see EPTSCalculationService#getObs(Concept, List, java.util.Collection, List, List,
   *     TimeQualifier, Date, PatientCalculationContext)
   */
  @SuppressWarnings("unchecked")
  @Test
  public void shouldGetAllObsByAnyTimeQualifier() {

    Concept concept = new Concept(5089);
    List<Integer> cohort = Arrays.asList(7);
    List<Location> locationList = Arrays.asList(new Location(1));

    CalculationResultMap obsMap =
        this.eptsCalculationService.getObs(
            concept, null, cohort, locationList, null, TimeQualifier.ANY, null, context);

    Assert.assertNotNull(obsMap);
    ListResult obsListResult = (ListResult) obsMap.get(7);
    Assert.assertNotNull(obsListResult);
    List<Obs> obsList = EptsCalculationUtils.extractResultValues(obsListResult);

    assertThat(obsList, notNullValue());
    assertThat(obsList, IsCollectionWithSize.hasSize(2));
    assertThat(
        obsList,
        contains(
            allOf(hasProperty("obsId", Matchers.equalTo(7))), allOf(hasProperty("obsId", is(10)))));
  }

  /**
   * @see EPTSCalculationService#allPatientStates(java.util.Collection, Location,
   *     ProgramWorkflowState, PatientCalculationContext)
   */
  @Test
  public void shouldGetAllPatientState() {
    List<Integer> cohort = Arrays.asList(2);

    ProgramWorkflowState programWorkflowState =
        Context.getProgramWorkflowService()
            .getProgramWorkflowStatesByConcept(new Concept(17))
            .get(0);
    CalculationResultMap patientStateMap =
        this.eptsCalculationService.allPatientStates(cohort, null, programWorkflowState, context);

    Assert.assertNotNull(patientStateMap);
    ListResult patientStateListResult = (ListResult) patientStateMap.get(2);

    Assert.assertNotNull(patientStateListResult);

    List<PatientState> patientStateList =
        EptsCalculationUtils.extractResultValues(patientStateListResult);

    Assert.assertNotNull(patientStateList);
    assertThat(patientStateList, IsCollectionWithSize.hasSize(1));
    assertThat(
        patientStateList,
        contains(
            allOf(
                hasProperty("patientStateId", Matchers.equalTo(1)),
                hasProperty("state", Matchers.equalTo(programWorkflowState)))));
  }

  /**
   * @see EPTSCalculationService#allProgramEnrollment(Program, java.util.Collection,
   *     PatientCalculationContext)
   */
  @Test
  public void shouldGetAllProgramEnrollment() {
    List<Integer> cohort = Arrays.asList(2, 4, 7, 8);

    Program program = new Program(2);
    program.setUuid("71779c39-d289-4dfe-91b5-e7cfaa27c78b");
    CalculationResultMap programEnrollmentResultMap =
        this.eptsCalculationService.allProgramEnrollment(program, cohort, context);

    Assert.assertNotNull(programEnrollmentResultMap);

    ListResult patientProgramListResult2 = (ListResult) programEnrollmentResultMap.get(2);
    ListResult patientProgramListResult4 = (ListResult) programEnrollmentResultMap.get(4);
    ListResult patientProgramListResult7 = (ListResult) programEnrollmentResultMap.get(7);
    ListResult patientProgramListResult8 = (ListResult) programEnrollmentResultMap.get(8);

    Assert.assertNotNull(patientProgramListResult2);
    Assert.assertNull(patientProgramListResult4);
    Assert.assertNotNull(patientProgramListResult7);
    Assert.assertNull(patientProgramListResult8);

    List<PatientProgram> patientProgramList2 =
        EptsCalculationUtils.extractResultValues(patientProgramListResult2);
    List<PatientProgram> patientProgramList7 =
        EptsCalculationUtils.extractResultValues(patientProgramListResult7);

    Assert.assertNotNull(patientProgramList2);
    Assert.assertNotNull(patientProgramList7);

    assertThat(patientProgramList2, IsCollectionWithSize.hasSize(1));
    assertThat(
        patientProgramList2,
        contains(
            allOf(
                hasProperty("patientProgramId", Matchers.equalTo(2)),
                hasProperty("program", Matchers.equalTo(program)))));

    assertThat(patientProgramList7, IsCollectionWithSize.hasSize(1));
    assertThat(
        patientProgramList7,
        contains(
            allOf(
                hasProperty("patientProgramId", Matchers.equalTo(4)),
                hasProperty("program", Matchers.equalTo(program)))));
  }

  /**
   * @see EPTSCalculationService#firstEncounter(List, Collection, Location,
   *     PatientCalculationContext)
   */
  @Test
  public void shouldGetFirstEncounter() {

    List<Integer> cohort = Arrays.asList(999);
    EncounterType encounterType = new EncounterType(6777002);
    Location location = new Location(1);
    CalculationResultMap encounterResultMap =
        this.eptsCalculationService.firstEncounter(
            Arrays.asList(encounterType), cohort, location, context);

    Assert.assertNotNull(encounterResultMap);

    Encounter firstEncounter999 = EptsCalculationUtils.resultForPatient(encounterResultMap, 999);

    Assert.assertNotNull(firstEncounter999);

    Assert.assertEquals(Integer.valueOf(2777023), firstEncounter999.getId());
    Assert.assertEquals(Integer.valueOf(999), firstEncounter999.getPatient().getId());
    Assert.assertEquals(Integer.valueOf(location.getId()), firstEncounter999.getLocation().getId());
    Assert.assertEquals(Integer.valueOf(2777023), firstEncounter999.getId());
  }

  /**
   * @see EPTSCalculationService#firstObs(Concept, Concept, Location, boolean, Date, Date, List,
   *     Collection, PatientCalculationContext)
   */
  @Test
  public void shouldGetFirstObsByMethodFirstObs() {

    Concept concept = new Concept(5089);
    List<Integer> cohort = Arrays.asList(7);
    List<Location> locationList = Arrays.asList(new Location(1));

    CalculationResultMap obsMap =
        this.eptsCalculationService.firstObs(
            concept, null, new Location(1), true, null, null, null, cohort, context);
    Obs obs = EptsCalculationUtils.resultForPatient(obsMap, 7);

    Assert.assertNotNull(obs);
    Assert.assertEquals(Integer.valueOf(7), obs.getId());
    Assert.assertEquals(concept.getId(), obs.getConcept().getId());
    Assert.assertEquals(locationList.get(0).getLocationId(), obs.getLocation().getId());
    Assert.assertEquals(Integer.valueOf(7), obs.getPerson().getId());
  }

  /**
   * @see EPTSCalculationService#firstPatientProgram(Program, Location, Collection,
   *     PatientCalculationContext)
   * @throws Exception
   */
  @Test
  public void shouldGetFirstPatientProgram() {

    Program program = new Program(2);
    Location location = new Location(1);
    Collection<Integer> cohort = Arrays.asList(7, 8, 999);

    CalculationResultMap patientProgramMap =
        this.eptsCalculationService.firstPatientProgram(program, location, cohort, context);

    Assert.assertNotNull(patientProgramMap);

    SimpleResult patientProgramResult7 = (SimpleResult) patientProgramMap.get(7);
    SimpleResult patientProgramResult8 = (SimpleResult) patientProgramMap.get(8);
    SimpleResult patientProgramResult999 = (SimpleResult) patientProgramMap.get(999);

    Assert.assertNull(patientProgramResult7);
    Assert.assertNull(patientProgramResult8);
    Assert.assertNotNull(patientProgramResult999);

    PatientProgram firstPatientProgram999 = (PatientProgram) patientProgramResult999.getValue();

    Assert.assertNotNull(firstPatientProgram999);
    Assert.assertEquals(Integer.valueOf(28077), firstPatientProgram999.getId());
    Assert.assertEquals(program.getProgramId(), firstPatientProgram999.getProgram().getId());
    Assert.assertEquals(location.getId(), firstPatientProgram999.getLocation().getId());
  }

  /**
   * @see EPTSCalculationService#lastObs(List, Concept, Location, Date, Date, Collection,
   *     PatientCalculationContext)
   * @throws Exception
   */
  @Test
  public void shouldGetLastObsByMethodLastObs() throws Exception {

    List<EncounterType> encounterTypes = Arrays.asList(new EncounterType(6777002));
    Concept concept = new Concept(7777001);
    Location location = new Location(1);
    Collection<Integer> cohort = Arrays.asList(7, 8, 999);

    CalculationResultMap resultMap =
        this.eptsCalculationService.lastObs(
            encounterTypes, concept, location, null, context.getNow(), cohort, context);

    Obs lastObs999 = EptsCalculationUtils.resultForPatient(resultMap, 999);

    Assert.assertNotNull(lastObs999);
    Assert.assertEquals(Integer.valueOf(3777029), lastObs999.getId());
    Assert.assertEquals(
        Integer.valueOf(6777002), lastObs999.getEncounter().getEncounterType().getId());
    Assert.assertEquals(concept.getId(), lastObs999.getConcept().getId());
    Assert.assertEquals(Integer.valueOf(999), lastObs999.getPersonId());
    Assert.assertEquals(location.getId(), lastObs999.getLocation().getId());
  }
}
