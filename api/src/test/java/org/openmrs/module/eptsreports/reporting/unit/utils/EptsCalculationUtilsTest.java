package org.openmrs.module.eptsreports.reporting.unit.utils;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.patient.PatientCalculationServiceImpl;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.CalculationWithResultFinder;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiObsDefinition;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.module.eptsreports.reporting.unit.PowerMockBaseContextTest;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.powermock.api.mockito.PowerMockito;

public class EptsCalculationUtilsTest extends PowerMockBaseContextTest {

  @Mock private PatientCalculationService service;

  private TestsHelper testsHelper;

  @Mock private PatientDataService patientDataService;

  @Mock private PersonDataService personDataService;

  private PatientCalculationContext calculationContext;

  @Before
  public void init() {
    testsHelper = new TestsHelper();
    when(service.createCalculationContext())
        .thenReturn(new PatientCalculationServiceImpl().new SimplePatientCalculationContext());
    calculationContext = service.createCalculationContext();
    calculationContext.setNow(testsHelper.getDate("2019-05-30 00:00:00.0"));
    PowerMockito.mockStatic(Context.class);
    when(Context.getService(PatientDataService.class)).thenReturn(patientDataService);
    when(Context.getService(PersonDataService.class)).thenReturn(personDataService);
  }

  /**
   * unit tests {@link EptsCalculationUtils#ensureEmptyListResults(CalculationResultMap,
   * java.util.Collection)}
   */
  @Test
  public void ensureEmptyListResultsShouldReplaceNullsWithEmptyList() {
    CalculationResultMap map = new CalculationResultMap();
    ListResult list = new ListResult();
    map.put(1, list);
    map.put(2, null);
    map.put(3, list);
    Assert.assertNull(map.get(2));
    CalculationResultMap replacedMap =
        EptsCalculationUtils.ensureEmptyListResults(map, Arrays.asList(1, 2, 3));
    Assert.assertEquals(map, replacedMap);
    Assert.assertEquals(list, replacedMap.get(3));
  }

  @Test(expected = RuntimeException.class)
  public void evaluateWithReportingShouldThrowExceptionWithNullDataDefition()
      throws RuntimeException {
    EptsCalculationUtils.evaluateWithReporting(
        null, Arrays.asList(1, 2, 3), null, null, calculationContext);
  }

  @Test(expected = RuntimeException.class)
  public void evaluateWithReportingShouldThrowExceptionWithNonPersonOrPatientDataDefition()
      throws RuntimeException {
    // EncounterDatetimeDataDefinition can be any datadefition besides
    // PersonDataDefinition and
    // PatientDataDefinition
    EptsCalculationUtils.evaluateWithReporting(
        new EncounterDatetimeDataDefinition(),
        Arrays.asList(1, 2, 3),
        null,
        null,
        calculationContext);
  }

  @SuppressWarnings({"unchecked"})
  @Test(expected = APIException.class)
  public void evaluateWithReportingShouldThrowAPIInsteadOfEvaluationException() throws Exception {
    when(patientDataService.evaluate(
            any(PatientDataDefinition.class), any(EvaluationContext.class)))
        .thenThrow(EvaluationException.class);
    EptsCalculationUtils.evaluateWithReporting(
        new JembiObsDefinition(), Arrays.asList(1, 2), null, null, calculationContext);
  }

  @Test
  public void evaluateWithReportingShouldRightlyEvaluatePatientAndPersonData()
      throws EvaluationException {
    EvaluatedPatientData patientData = new EvaluatedPatientData();
    EvaluatedPersonData personData = new EvaluatedPersonData();
    Map<Integer, Object> data = new HashMap<Integer, Object>();
    Obs obs = new Obs(1);
    // represents any other object
    Encounter encounter = new Encounter(1);
    List<? extends Object> list = Arrays.asList("hi", "there", 4);
    data.put(1, "");
    data.put(2, obs);
    data.put(3, false);
    data.put(4, true);
    data.put(5, encounter);
    data.put(6, null);
    data.put(7, list);
    data.put(8, new Object());
    patientData.setData(data);
    personData.setData(data);
    // evaluating patient data definition
    when(patientDataService.evaluate(
            any(PatientDataDefinition.class), any(EvaluationContext.class)))
        .thenReturn(patientData);
    CalculationResultMap patientResult =
        EptsCalculationUtils.evaluateWithReporting(
            new JembiObsDefinition(),
            Arrays.asList(1, 2, 3, 4, 5, 6, 7),
            null,
            null,
            calculationContext);
    Assert.assertNotNull(patientResult);
    evaluationTest(obs, encounter, list, patientResult);

    // evaluating persondata defition
    when(personDataService.evaluate(any(PersonDataDefinition.class), any(EvaluationContext.class)))
        .thenReturn(personData);
    CalculationResultMap personResult =
        EptsCalculationUtils.evaluateWithReporting(
            new ObsForPersonDataDefinition(),
            Arrays.asList(1, 2, 3, 4, 5, 6, 7),
            null,
            null,
            calculationContext);
    Assert.assertNotNull(personResult);
    evaluationTest(obs, encounter, list, personResult);
  }

  private void evaluationTest(
      Obs obs, Encounter encounter, List<? extends Object> list, CalculationResultMap result) {
    Assert.assertEquals("", result.get(1).getValue());
    Assert.assertTrue(result.get(1) instanceof SimpleResult);
    Assert.assertEquals(obs, result.get(2).getValue());
    Assert.assertTrue(result.get(2) instanceof ObsResult);
    Assert.assertFalse((boolean) result.get(3).getValue());
    Assert.assertTrue(result.get(3) instanceof BooleanResult);
    Assert.assertTrue((boolean) result.get(4).getValue());
    Assert.assertTrue(result.get(4) instanceof BooleanResult);
    Assert.assertEquals(encounter, result.get(5).getValue());
    Assert.assertTrue(result.get(5) instanceof SimpleResult);
    Assert.assertNull(result.get(6));
    Assert.assertEquals(list.toString(), result.get(7).getValue().toString());
    Assert.assertTrue(result.get(7) instanceof ListResult);
    // should not include any objects outside the defined cohort
    Assert.assertFalse(result.containsKey(8));
  }

  /** unit tests {@link EptsCalculationUtils#resultForPatient(CalculationResultMap, Integer)} */
  @Test
  public void resultForPatientShouldReturnRightPatientResultValue() {
    CalculationResultMap map = new CalculationResultMap();
    ListResult list = new ListResult();
    SimpleResult r10 = new SimpleResult(10, null, calculationContext);
    SimpleResult r11 = new SimpleResult(11, null, calculationContext);
    list.add(r10);
    list.add(r11);
    map.put(1, new SimpleResult(1, null, calculationContext));
    map.put(2, null);
    map.put(3, list);
    map.put(4, new BooleanResult(false, null));
    map.put(5, new BooleanResult(true, null));
    map.put(6, new BooleanResult(null, null));
    Obs obs = new Obs(1);
    map.put(7, new ObsResult(obs, null));
    map.put(8, new SimpleResult(null, null, calculationContext));
    map.put(9, new ObsResult(null, null));
    Encounter enc = new Encounter(2);
    map.put(10, new SimpleResult(enc, null, calculationContext));
    map.put(11, new SimpleResult(null, null, calculationContext));
    // returns value for simple result
    Assert.assertEquals(1, EptsCalculationUtils.resultForPatient(map, 1));
    // returns value for null simple result
    Assert.assertNull(EptsCalculationUtils.resultForPatient(map, 8));
    // return null for null result
    Assert.assertNull(null, EptsCalculationUtils.resultForPatient(map, 2));
    // returns arraylist for listresult
    Assert.assertEquals(Arrays.asList(r10, r11), EptsCalculationUtils.resultForPatient(map, 3));
    // returns null for false booleanresult
    Assert.assertNull((Boolean) EptsCalculationUtils.resultForPatient(map, 4));
    // returns true for false booleanresult
    Assert.assertTrue((Boolean) EptsCalculationUtils.resultForPatient(map, 5));
    // returns null for null booleanresult
    Assert.assertNull((Boolean) EptsCalculationUtils.resultForPatient(map, 6));
    // returns obs for Obsresult
    Assert.assertEquals(obs, EptsCalculationUtils.resultForPatient(map, 7));
    // retuns null for null obsresult
    Assert.assertNull(EptsCalculationUtils.resultForPatient(map, 9));
    // returns encounter for Encounter simple result
    Assert.assertEquals(enc, EptsCalculationUtils.resultForPatient(map, 10));
    // returns encounter for null Encounter simple result
    Assert.assertNull(EptsCalculationUtils.resultForPatient(map, 11));
  }

  @Test
  public void patientsThatPassShouldRetrieveMatchedResultsCohort() {
    CalculationResultMap map = new CalculationResultMap();
    ListResult list = new ListResult();
    SimpleResult r10 = new SimpleResult(10, null, calculationContext);
    SimpleResult r11 = new SimpleResult(11, null, calculationContext);
    list.add(r10);
    list.add(r11);
    map.put(1, new SimpleResult(1, null, calculationContext));
    map.put(3, list);
    map.put(4, new BooleanResult(false, null));
    map.put(5, new BooleanResult(true, null));
    Obs obs = new Obs(1);
    map.put(7, new ObsResult(obs, null));
    Encounter enc = new Encounter(2);
    map.put(10, new SimpleResult(enc, null, calculationContext));
    map.put(12, new BooleanResult(true, null));
    map.put(13, new ObsResult(obs, null));
    map.put(14, new SimpleResult("", null, calculationContext));
    map.put(15, new SimpleResult(enc, null, calculationContext));
    map.put(16, list);

    Assert.assertEquals(
        new HashSet<Integer>(Arrays.asList(7, 13)),
        EptsCalculationUtils.patientsThatPass(map, obs));
    Assert.assertEquals(
        new HashSet<Integer>(Arrays.asList(14)), EptsCalculationUtils.patientsThatPass(map, ""));
    Assert.assertEquals(
        new HashSet<Integer>(Arrays.asList(5, 12)),
        EptsCalculationUtils.patientsThatPass(map, true));
    Assert.assertEquals(
        new HashSet<Integer>(Arrays.asList(4)), EptsCalculationUtils.patientsThatPass(map, false));
    Assert.assertEquals(
        new HashSet<>(Arrays.asList(10, 15)), EptsCalculationUtils.patientsThatPass(map, enc));
    Assert.assertEquals(
        new HashSet<>(Arrays.asList(1)), EptsCalculationUtils.patientsThatPass(map, 1));
    Assert.assertEquals(
        new HashSet<>(Arrays.asList(3, 16)),
        EptsCalculationUtils.patientsThatPass(map, Arrays.asList(r10, r11)));
  }

  @Test
  public void patientsThatPassShouldRetrieveMatchedResultsCohortWithNullButNotFalseResult() {
    CalculationResultMap map = new CalculationResultMap();
    map.put(1, new SimpleResult(1, null, calculationContext));
    map.put(2, new BooleanResult(false, null, calculationContext));
    map.put(3, new BooleanResult(true, null, calculationContext));
    map.put(4, new SimpleResult("", null, calculationContext));
    map.put(5, new SimpleResult(null, null, calculationContext));

    Assert.assertEquals(
        new HashSet<>(Arrays.asList(1, 3, 5)), EptsCalculationUtils.patientsThatPass(map));
  }

  @Test
  public void patientsThatPassShouldRetrieveOnlyNullResultWhenRequested() {
    CalculationResultMap map = new CalculationResultMap();
    map.put(1, new SimpleResult(1, null, calculationContext));
    map.put(2, new BooleanResult(false, null, calculationContext));
    map.put(3, new BooleanResult(true, null, calculationContext));
    map.put(4, new SimpleResult("", null, calculationContext));
    map.put(5, new SimpleResult(null, null, calculationContext));

    Assert.assertEquals(
        new HashSet<>(Arrays.asList(5)),
        EptsCalculationUtils.patientsThatPass(map, null, CalculationWithResultFinder.NULL, null));
  }

  @Test
  public void monthsSinceShouldGetMonthsDifferenceBetween2Dates() {
    Assert.assertEquals(
        0,
        EptsCalculationUtils.monthsSince(
            testsHelper.getDate("2019-02-01 00:00:00.0"),
            testsHelper.getDate("2019-02-18 00:00:00.0")));
    Assert.assertEquals(
        1,
        EptsCalculationUtils.monthsSince(
            testsHelper.getDate("2019-01-21 00:00:00.0"),
            testsHelper.getDate("2019-02-21 00:00:00.0")));
    Assert.assertEquals(
        61,
        EptsCalculationUtils.monthsSince(
            testsHelper.getDate("2014-01-28 00:00:00.0"),
            testsHelper.getDate("2019-02-28 00:00:00.0")));

    // the arguments ordering doesn't matters with joda
    Assert.assertEquals(
        1,
        EptsCalculationUtils.monthsSince(
            (testsHelper.getDate("2019-02-28 00:00:00.0")),
            testsHelper.getDate("2019-01-21 00:00:00.0")));
  }

  @Test
  public void monthsSinceShouldGetMonthsIncludingDaysDiffBetween2Dates() {
    Assert.assertEquals(
        0,
        EptsCalculationUtils.monthsSince(
            testsHelper.getDate("2019-02-01 00:00:00.0"),
            testsHelper.getDate("2019-02-18 00:00:00.0")));
    Assert.assertEquals(
        1,
        EptsCalculationUtils.monthsSince(
            testsHelper.getDate("2019-01-21 00:00:00.0"),
            testsHelper.getDate("2019-02-21 00:00:00.0")));
    Assert.assertEquals(
        61,
        EptsCalculationUtils.monthsSince(
            testsHelper.getDate("2014-01-28 00:00:00.0"),
            testsHelper.getDate("2019-02-28 00:00:00.0")));

    // the arguments ordering doesn't matters with joda
    Assert.assertEquals(
        1,
        EptsCalculationUtils.monthsSince(
            (testsHelper.getDate("2019-02-28 00:00:00.0")),
            testsHelper.getDate("2019-01-21 00:00:00.0")));
  }

  @Test
  public void extractResultValuesShouldReturnListOfAllValuesFromAResultIncludingNulls() {
    ListResult results = new ListResult();
    Obs obs = new Obs(1);
    results.add(new ObsResult(obs, null));
    results.add(new ObsResult(null, null));
    results.add(new SimpleResult("", null, calculationContext));
    results.add(new SimpleResult(1, null, calculationContext));
    results.add(new BooleanResult(false, null, calculationContext));
    results.add(new BooleanResult(true, null, calculationContext));
    results.add(new ObsResult(obs, null));
    results.add(new BooleanResult(false, null, calculationContext));
    results.add(new BooleanResult(null, null, calculationContext));
    Assert.assertEquals(
        Arrays.asList(obs, null, "", 1, false, true, obs, false, null),
        EptsCalculationUtils.extractResultValues(results));
  }

  @Test
  public void earliestShouldReturnEarliestDateFrom2NullInclusive() {
    Assert.assertEquals(
        testsHelper.getDate("2019-02-01 00:00:00.0"),
        EptsCalculationUtils.earliest(
            testsHelper.getDate("2019-02-01 00:00:00.0"),
            testsHelper.getDate("2019-02-18 00:00:00.0")));
    Assert.assertEquals(
        testsHelper.getDate("2019-01-21 00:00:00.0"),
        EptsCalculationUtils.earliest(testsHelper.getDate("2019-01-21 00:00:00.0"), null));
    Assert.assertEquals(
        testsHelper.getDate("2019-01-21 00:00:00.0"),
        EptsCalculationUtils.earliest(null, testsHelper.getDate("2019-01-21 00:00:00.0")));
    Assert.assertEquals(
        testsHelper.getDate("2019-01-21 00:00:00.0"),
        EptsCalculationUtils.earliest(
            (testsHelper.getDate("2019-02-28 00:00:00.0")),
            testsHelper.getDate("2019-01-21 00:00:00.0")));
    Assert.assertEquals(null, EptsCalculationUtils.earliest(null, null));
  }

  @Test
  public void addMonthsShouldReturnDateWithAddedMonths() {
    Assert.assertEquals(
        testsHelper.getDate("2019-01-21 00:00:00.0"),
        EptsCalculationUtils.addMonths(testsHelper.getDate("2019-02-21 00:00:00.0"), -1));
    Assert.assertEquals(
        testsHelper.getDate("2019-02-21 00:00:00.0"),
        EptsCalculationUtils.addMonths(testsHelper.getDate("2019-02-21 00:00:00.0"), 0));
    Assert.assertEquals(
        testsHelper.getDate("2019-01-21 00:00:00.0"),
        EptsCalculationUtils.addMonths(testsHelper.getDate("2018-10-21 00:00:00.0"), 3));
  }

  @Test(expected = NullPointerException.class)
  public void addMonthsShouldThrowExceptionWithNullDate() throws NullPointerException {
    EptsCalculationUtils.addMonths(null, -1);
  }

  @Test
  public void dateBetweenShouldBeFalseForAnyOrAllNulls() {
    Assert.assertFalse(EptsCalculationUtils.dateBetween(null, new Date(), new Date()));
    Assert.assertFalse(EptsCalculationUtils.dateBetween(new Date(), new Date(), null));
    Assert.assertFalse(EptsCalculationUtils.dateBetween(new Date(), null, new Date()));
    Assert.assertFalse(EptsCalculationUtils.dateBetween(null, new Date(), null));
    Assert.assertFalse(EptsCalculationUtils.dateBetween(new Date(), null, null));
    Assert.assertFalse(EptsCalculationUtils.dateBetween(null, null, new Date()));
    Assert.assertFalse(EptsCalculationUtils.dateBetween(null, null, null));
  }

  @Test
  public void dateBetweenShouldMatchDatesRightly() {
    Assert.assertTrue(
        EptsCalculationUtils.dateBetween(
            testsHelper.getDate("2019-01-19 00:00:00.0"),
            testsHelper.getDate("2019-01-21 00:00:00.0"),
            testsHelper.getDate("2019-01-20 00:00:00.0")));
    Assert.assertTrue(
        EptsCalculationUtils.dateBetween(
            testsHelper.getDate("2019-01-19 00:00:00.0"),
            testsHelper.getDate("2019-01-20 00:00:00.0"),
            testsHelper.getDate("2019-01-19 00:00:00.0")));
    Assert.assertTrue(
        EptsCalculationUtils.dateBetween(
            testsHelper.getDate("2019-01-19 00:00:00.0"),
            testsHelper.getDate("2019-01-20 00:00:00.0"),
            testsHelper.getDate("2019-01-20 00:00:00.0")));
    Assert.assertTrue(
        EptsCalculationUtils.dateBetween(
            testsHelper.getDate("2019-01-20 00:00:00.0"),
            testsHelper.getDate("2019-01-20 00:00:00.0"),
            testsHelper.getDate("2019-01-20 00:00:00.0")));
    Assert.assertFalse(
        EptsCalculationUtils.dateBetween(
            testsHelper.getDate("2019-01-19 00:00:00.0"),
            testsHelper.getDate("2019-01-20 00:00:00.0"),
            testsHelper.getDate("2019-01-20 00:00:00.1")));
  }

  @Test
  public void patientsThatPassShouldRetrieveOnlyOutsideMatchedDate() {
    CalculationResultMap map = new CalculationResultMap();
    map.put(
        1,
        new SimpleResult(testsHelper.getDate("2019-02-01 00:00:00.0"), null, calculationContext));
    map.put(
        2,
        new SimpleResult(testsHelper.getDate("2019-02-05 00:00:00.0"), null, calculationContext));
    map.put(3, new SimpleResult(true, null, calculationContext));
    map.put(4, new SimpleResult("", null, calculationContext));
    map.put(
        5,
        new SimpleResult(testsHelper.getDate("2019-02-09 00:00:00.0"), null, calculationContext));
    EvaluationContext context = new EvaluationContext();
    context.addParameterValue("startDate", testsHelper.getDate("2019-02-03 00:00:00.0"));
    context.addParameterValue("endDate", testsHelper.getDate("2019-02-06 00:00:00.0"));

    Assert.assertEquals(
        new HashSet<>(Arrays.asList(1, 5)),
        EptsCalculationUtils.patientsThatPass(
            map, null, CalculationWithResultFinder.DATE_OUTSIDE, context));

    Assert.assertEquals(
        new HashSet<Integer>(),
        EptsCalculationUtils.patientsThatPass(
            map, null, CalculationWithResultFinder.DATE_OUTSIDE, null));
  }

  @Test
  public void daysSinceShouldRightlyEvaluateDaysBetweenTwoDates() {
    Assert.assertEquals(
        2,
        EptsCalculationUtils.daysSince(
            testsHelper.getDate("2019-02-01 00:00:00.0"),
            testsHelper.getDate("2019-02-03 00:00:00.0")));
    Assert.assertEquals(
        0,
        EptsCalculationUtils.daysSince(
            testsHelper.getDate("2019-02-01 00:00:00.0"),
            testsHelper.getDate("2019-02-01 00:00:00.0")));
    // order arguments doesn't matter
    Assert.assertEquals(
        9,
        EptsCalculationUtils.daysSince(
            testsHelper.getDate("2019-02-10 00:00:00.0"),
            testsHelper.getDate("2019-02-01 00:00:00.0")));
  }
}
