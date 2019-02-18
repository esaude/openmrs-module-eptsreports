package org.openmrs.module.eptsreports.reporting.unit.utils;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.patient.PatientCalculationServiceImpl;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.test.BaseContextMockTest;

public class EptsCalculationUtilsTest extends BaseContextMockTest {

  @Mock PatientCalculationService service;

  @Spy protected TestsHelper testsHelper;

  PatientCalculationContext calculationContext;

  @Before
  public void init() {
    when(service.createCalculationContext())
        .thenReturn(new PatientCalculationServiceImpl().new SimplePatientCalculationContext());
    calculationContext = service.createCalculationContext();
    calculationContext.setNow(testsHelper.getDate("2019-05-30 00:00:00.0"));
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

  public void evaluateWithReporting() {}

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

  /** unit tests {@link EptsCalculationUtils#obsResultForPatient(CalculationResultMap, Integer)} */
  @Test
  public void obsResultForPatientShouldReturnRightPatientResultValue() {
    CalculationResultMap map = new CalculationResultMap();
    Obs obs = new Obs(1);
    map.put(7, new ObsResult(obs, null));
    map.put(9, new ObsResult(null, null));
    // returns obs for Obsresult
    Assert.assertEquals(obs, EptsCalculationUtils.obsResultForPatient(map, 7));
    // retuns null for null obsresult
    Assert.assertNull(EptsCalculationUtils.obsResultForPatient(map, 9));
  }

  /**
   * unit tests {@link EptsCalculationUtils#encounterResultForPatient(CalculationResultMap,
   * Integer)}
   */
  @Test
  public void encounterResultForPatientShouldReturnRightPatientResultValue() {
    CalculationResultMap map = new CalculationResultMap();

    Encounter enc = new Encounter(2);
    map.put(10, new SimpleResult(enc, null, calculationContext));
    map.put(11, new SimpleResult(null, null, calculationContext));

    // returns encounter for Encounter simple result
    Assert.assertEquals(enc, EptsCalculationUtils.encounterResultForPatient(map, 10));
    // returns encounter for Encounter simple result
    Assert.assertNull(EptsCalculationUtils.encounterResultForPatient(map, 11));
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
        new HashSet<Integer>(Arrays.asList(10, 15)),
        EptsCalculationUtils.patientsThatPass(map, enc));
    Assert.assertEquals(
        new HashSet<Integer>(Arrays.asList(1)), EptsCalculationUtils.patientsThatPass(map, 1));
    Assert.assertEquals(
        new HashSet<Integer>(Arrays.asList(3, 16)),
        EptsCalculationUtils.patientsThatPass(map, Arrays.asList(r10, r11)));
  }

  @Test
  public void patientsThatPassShouldRetrieveMatchedResultsCohortWithNonNullOrUnEmptyResult() {
    CalculationResultMap map = new CalculationResultMap();
    map.put(1, new SimpleResult(1, null, calculationContext));
    map.put(2, new BooleanResult(false, null, calculationContext));
    map.put(3, new BooleanResult(true, null, calculationContext));
    map.put(4, new SimpleResult("", null, calculationContext));

    Assert.assertEquals(
        new HashSet<Integer>(Arrays.asList(1, 3)), EptsCalculationUtils.patientsThatPass(map));
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
            testsHelper.getDate("2019-02-28 00:00:00.0")));
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
}
