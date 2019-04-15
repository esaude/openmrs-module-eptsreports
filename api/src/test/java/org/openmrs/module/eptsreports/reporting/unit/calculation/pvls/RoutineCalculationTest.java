package org.openmrs.module.eptsreports.reporting.unit.calculation.pvls;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.patient.PatientCalculationServiceImpl;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.OnArtForMoreThanXmonthsCalcultion;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.RoutineCalculation;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.PatientsOnRoutineEnum;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.test.BaseContextMockTest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class RoutineCalculationTest extends BaseContextMockTest {

  @Mock private HivMetadata hivMetadata;

  @Mock private InitialArtStartDateCalculation initialArtStartDateCalculation;

  @Mock private OnArtForMoreThanXmonthsCalcultion onArtForMoreThanXmonthsCalcultion;

  @Mock private EPTSCalculationService eptsCalculationService;

  @Spy
  private PatientCalculationService patientCalculationService = new PatientCalculationServiceImpl();

  private RoutineCalculation routineCalculation;

  private TestsHelper testsHelper;

  @Before
  public void init() {
    PowerMockito.mockStatic(Context.class);
    when(Context.getRegisteredComponents(HivMetadata.class))
        .thenReturn(Collections.singletonList(hivMetadata));
    when(Context.getRegisteredComponents(InitialArtStartDateCalculation.class))
        .thenReturn(Collections.singletonList(initialArtStartDateCalculation));
    when(Context.getRegisteredComponents(OnArtForMoreThanXmonthsCalcultion.class))
        .thenReturn(Collections.singletonList(onArtForMoreThanXmonthsCalcultion));
    when(Context.getService(PatientCalculationService.class)).thenReturn(patientCalculationService);
    when(Context.getRegisteredComponents(EPTSCalculationService.class))
        .thenReturn(Collections.singletonList(eptsCalculationService));
    routineCalculation = new RoutineCalculation();
    testsHelper = new TestsHelper();
  }

  // Routine criteria 2 test
  @Test
  public void
      evaluateShouldReturnTrueForAdultsAndChildrenWithVl12To15MonthsApartAndValueOfLessThan1000Copies() {
    Date artStartDate = testsHelper.getDate("2017-02-19 00:00:00.0");
    Date firstVlDate = testsHelper.getDate("2016-12-19 00:00:00.0");
    Date secondVlDate = testsHelper.getDate("2018-02-19 00:00:00.0");
    Date now = testsHelper.getDate("2019-02-18 00:00:00.0");
    double vlValueNumeric = 999.0;

    Location location = new Location(1);
    Person person = new Person(1);
    Concept viralLoadConcept = new Concept(856);

    Obs vl1 = new Obs(person, viralLoadConcept, firstVlDate, location);
    vl1.setObsId(1);
    vl1.setValueNumeric(vlValueNumeric);
    Obs vl2 = new Obs(person, viralLoadConcept, secondVlDate, location);
    vl2.setObsId(2);
    List<Obs> vlList = Arrays.asList(vl1, vl2);

    Concept regimeConcept = new Concept(1088);
    Obs regimeChange =
        new Obs(person, regimeConcept, testsHelper.getDate("2019-02-18 00:00:00.0"), location);

    Patient patient = new Patient(person);

    CalculationResultMap resultMap =
        getResultMap(
            patient,
            artStartDate,
            now,
            location,
            vlList,
            vl2,
            regimeChange,
            true,
            PatientsOnRoutineEnum.ADULTCHILDREN);

    Assert.assertEquals(true, resultMap.get(patient.getId()).getValue());
  }

  //   @Test
  //   public void
  // evaluateShouldReturnTrueForBreastfeedingAndPregnantWithVlValueOfLessThan1000Copies() {
  //     Date artStartDate = testsHelper.getDate("2017-02-19 00:00:00.0");
  //     Date firstVlDate = testsHelper.getDate("2018-01-19 00:00:00.0");
  //     Date secondVlDate = testsHelper.getDate("2018-02-19 00:00:00.0");
  //     Date now = testsHelper.getDate("2019-02-18 00:00:00.0");
  //     double vlValueNumeric = 999.0;

  //     Location location = new Location(1);
  //     Person person = new Person(1);
  //     Concept viralLoadConcept = new Concept(856);

  //     Obs vl1 = new Obs(person, viralLoadConcept, firstVlDate, location);
  //     vl1.setObsId(1);
  //     vl1.setValueNumeric(vlValueNumeric);
  //     Obs vl2 = new Obs(person, viralLoadConcept, secondVlDate, location);
  //     vl2.setObsId(2);
  //     List<Obs> vlList = Arrays.asList(vl1, vl2);

  //     Concept regimeConcept = new Concept(1088);
  //     Obs regimeChange =
  //         new Obs(person, regimeConcept, testsHelper.getDate("2019-02-18 00:00:00.0"), location);

  //     Patient patient = new Patient(person);

  //     CalculationResultMap resultMap =
  //         getResultMap(
  //             patient,
  //             artStartDate,
  //             now,
  //             location,
  //             vlList,
  //             vl2,
  //             regimeChange,
  //             true,
  //             PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT);

  //     Assert.assertEquals(true, resultMap.get(patient.getId()).getValue());
  //   }

  //   @Test
  //   public void evaluateShouldReturnFalseWhenCurrentVlIsTheFirstOne() {
  //     Date artStartDate = testsHelper.getDate("2018-02-19 00:00:00.0");
  //     Date firstVlDate = testsHelper.getDate("2018-02-19 00:00:00.0");
  //     Date now = testsHelper.getDate("2019-02-18 00:00:00.0");
  //     double vlValueNumeric = 999.0;

  //     Location location = new Location(1);
  //     Person person = new Person(1);
  //     Concept viralLoadConcept = new Concept(856);

  //     Obs vl1 = new Obs(person, viralLoadConcept, firstVlDate, location);
  //     vl1.setObsId(1);
  //     vl1.setValueNumeric(vlValueNumeric);
  //     List<Obs> vlList = Collections.singletonList(vl1);

  //     Concept regimeConcept = new Concept(1088);
  //     Obs regimeChange =
  //         new Obs(person, regimeConcept, testsHelper.getDate("2019-02-18 00:00:00.0"), location);

  //     Patient patient = new Patient(person);

  //     CalculationResultMap resultMap =
  //         getResultMap(
  //             patient,
  //             artStartDate,
  //             now,
  //             location,
  //             vlList,
  //             vl1,
  //             regimeChange,
  //             true,
  //             PatientsOnRoutineEnum.ADULTCHILDREN);

  //     Assert.assertEquals(false, resultMap.get(patient.getId()).getValue());
  //   }

  @Test
  public void evaluateShouldReturnFalseWhenThereIsNoViralLoadForPatientTakenWithin12Months() {
    Date artStartDate = testsHelper.getDate("2017-02-19 00:00:00.0");
    Date firstVlDate = testsHelper.getDate("2017-02-19 00:00:00.0");
    Date secondVlDate = testsHelper.getDate("2018-01-19 00:00:00.0");
    Date now = testsHelper.getDate("2019-02-18 00:00:00.0");
    double vlValueNumeric = 999.0;

    Location location = new Location(1);
    Person person = new Person(1);
    Concept viralLoadConcept = new Concept(856);

    Obs vl1 = new Obs(person, viralLoadConcept, firstVlDate, location);
    vl1.setObsId(1);
    vl1.setValueNumeric(vlValueNumeric);
    Obs vl2 = new Obs(person, viralLoadConcept, secondVlDate, location);
    vl2.setObsId(2);
    List<Obs> vlList = Arrays.asList(vl1, vl2);

    Concept regimeConcept = new Concept(1088);
    Obs regimeChange =
        new Obs(person, regimeConcept, testsHelper.getDate("2019-02-18 00:00:00.0"), location);

    Patient patient = new Patient(person);

    CalculationResultMap resultMap =
        getResultMap(
            patient,
            artStartDate,
            now,
            location,
            vlList,
            vl2,
            regimeChange,
            true,
            PatientsOnRoutineEnum.ADULTCHILDREN);

    Assert.assertEquals(false, resultMap.get(patient.getId()).getValue());
  }

  @SuppressWarnings("unchecked")
  private CalculationResultMap getResultMap(
      Patient patient,
      Date artStartDate,
      Date now,
      Location location,
      List<Obs> vlList,
      Obs lastVl,
      Obs regimeChange,
      boolean onArtForMoreThan3Months,
      PatientsOnRoutineEnum criteria) {

    Concept viralLoadConcept = new Concept(856);
    Concept regimeConcept = new Concept(1088);
    EncounterType labEncounterType = new EncounterType(13);
    EncounterType adultFollowup = new EncounterType(6);
    EncounterType childFollowup = new EncounterType(9);

    when(hivMetadata.getHivViralLoadConcept()).thenReturn(viralLoadConcept);
    when(hivMetadata.getRegimeConcept()).thenReturn(regimeConcept);
    when(hivMetadata.getMisauLaboratorioEncounterType()).thenReturn(labEncounterType);
    when(hivMetadata.getAdultoSeguimentoEncounterType()).thenReturn(adultFollowup);
    when(hivMetadata.getARVPediatriaSeguimentoEncounterType()).thenReturn(childFollowup);

    PatientCalculationContext calculationContext =
        patientCalculationService.createCalculationContext();
    calculationContext.addToCache("location", location);
    calculationContext.setNow(now);

    Collection<Integer> cohort = Arrays.asList(patient.getId());

    CalculationResultMap vlResultsMap = new CalculationResultMap();
    ListResult value = new ListResult();
    for (Obs obs : vlList) {
      value.add(new ObsResult(obs, null));
    }
    vlResultsMap.put(patient.getId(), value);
    when(eptsCalculationService.getObs(
            eq(viralLoadConcept),
            anyList(),
            eq(cohort),
            eq(Arrays.asList(location)),
            (List<Concept>) isNull(),
            eq(TimeQualifier.ANY),
            any(Date.class),
            eq(calculationContext)))
        .thenReturn(vlResultsMap);

    CalculationResultMap regimenChangeMap = new CalculationResultMap();
    regimenChangeMap.put(patient.getId(), new ObsResult(regimeChange, null));
    when(eptsCalculationService.getObs(
            eq(regimeConcept),
            anyList(),
            eq(cohort),
            eq(Arrays.asList(location)),
            (List<Concept>) anyList(),
            eq(TimeQualifier.FIRST),
            any(Date.class),
            eq(calculationContext)))
        .thenReturn(regimenChangeMap);

    CalculationResultMap artStartDateMap = new CalculationResultMap();
    artStartDateMap.put(patient.getId(), new SimpleResult(artStartDate, null));
    when(initialArtStartDateCalculation.evaluate(
            eq(cohort), anyMapOf(String.class, Object.class), any(PatientCalculationContext.class)))
        .thenReturn(artStartDateMap);

    CalculationResultMap lastVlMap = new CalculationResultMap();
    lastVlMap.put(patient.getId(), new ObsResult(lastVl, null));
    when(eptsCalculationService.lastObs(
            eq(Arrays.asList(labEncounterType, adultFollowup, childFollowup)),
            eq(viralLoadConcept),
            eq(location),
            any(Date.class),
            any(Date.class),
            eq(cohort),
            any(PatientCalculationContext.class)))
        .thenReturn(lastVlMap);

    CalculationResultMap onArtForMoreThan3MonthsResultMap = new CalculationResultMap();
    onArtForMoreThan3MonthsResultMap.put(
        patient.getId(), new BooleanResult(onArtForMoreThan3Months, null));
    when(onArtForMoreThanXmonthsCalcultion.evaluate(
            eq(cohort), anyMapOf(String.class, Object.class), any(PatientCalculationContext.class)))
        .thenReturn(onArtForMoreThan3MonthsResultMap);

    Map<String, Object> params = new HashMap<>();
    params.put("criteria", criteria);

    return routineCalculation.evaluate(cohort, params, calculationContext);
  }
}
