package org.openmrs.module.eptsreports.reporting.unit.calculation.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.patient.PatientCalculationServiceImpl;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.calculation.generic.KeyPopulationCalculation;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.module.eptsreports.reporting.unit.PowerMockBaseContextTest;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.data.DataDefinition;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest(EptsCalculationUtils.class)
public class KeyPopulationCalculationTest extends PowerMockBaseContextTest {

  @Mock private HivMetadata hivMetadata;

  @Mock private EPTSCalculationService eptsCalculationService;

  @Spy
  private PatientCalculationService patientCalculationService = new PatientCalculationServiceImpl();

  private KeyPopulationCalculation keyPopulationCalculation;

  private TestsHelper testsHelper;

  private Concept drugUseConcept;
  private Concept homosexualConcept;
  private Concept imprisonmentConcept;
  private Concept sexWorkerConcept;

  @Before
  public void setUp() {
    PowerMockito.mockStatic(Context.class);
    PowerMockito.mockStatic(EptsCalculationUtils.class);
    when(Context.getRegisteredComponents(HivMetadata.class))
        .thenReturn(Collections.singletonList(hivMetadata));
    when(Context.getRegisteredComponents(EPTSCalculationService.class))
        .thenReturn(Collections.singletonList(eptsCalculationService));
    when(Context.getService(PatientCalculationService.class)).thenReturn(patientCalculationService);
    keyPopulationCalculation = new KeyPopulationCalculation();
    testsHelper = new TestsHelper();
    drugUseConcept = new Concept(20454);
    homosexualConcept = new Concept(1377);
    imprisonmentConcept = new Concept(20426);
    sexWorkerConcept = new Concept(1901);
  }

  @Test
  public void evaluateShouldFollowKeyPopSourcePrecedenceWhenClassifiedOnSameDate() {
    Person person = new Person(1);
    Patient patient = new Patient(person);
    Date date = testsHelper.getDate("2019-11-03 00:00:00.0");

    PersonAttribute personAttribute = new PersonAttribute();
    personAttribute.setValue("Prisoner");
    personAttribute.setDateCreated(date);

    Obs adultoObs = new Obs();
    adultoObs.setValueCoded(sexWorkerConcept);
    Encounter encounter = new Encounter();
    encounter.setEncounterDatetime(date);
    adultoObs.setEncounter(encounter);

    Obs appsObs = new Obs();
    appsObs.setValueCoded(drugUseConcept);
    Encounter apssEncounter = new Encounter();
    apssEncounter.setEncounterDatetime(date);
    appsObs.setEncounter(apssEncounter);

    KeyPopulationCalculation.KeyPop expected = KeyPopulationCalculation.KeyPop.PRISONER;
    CalculationResultMap resultMap =
        getResultMap(patient, expected, personAttribute, adultoObs, appsObs);

    assertFalse(resultMap.isEmpty(patient.getId()));
    assertEquals(true, resultMap.get(patient.getId()).getValue());
  }

  @Test
  public void evaluateShouldShouldUseMostRecentClassification() {
    Person person = new Person(1);
    Patient patient = new Patient(person);
    Date date = testsHelper.getDate("2019-11-03 00:00:00.0");
    Date dayAfter = testsHelper.getDate("2019-11-04 00:00:00.0");

    PersonAttribute personAttribute = new PersonAttribute();
    personAttribute.setValue("Prisoner");
    personAttribute.setDateCreated(date);

    Obs adultoObs = new Obs();
    adultoObs.setValueCoded(sexWorkerConcept);
    Encounter encounter = new Encounter();
    encounter.setEncounterDatetime(date);
    adultoObs.setEncounter(encounter);

    Obs appsObs = new Obs();
    appsObs.setValueCoded(drugUseConcept);
    Encounter apssEncounter = new Encounter();
    apssEncounter.setEncounterDatetime(dayAfter);
    appsObs.setEncounter(apssEncounter);

    KeyPopulationCalculation.KeyPop expected = KeyPopulationCalculation.KeyPop.DRUG_USER;
    CalculationResultMap resultMap =
        getResultMap(patient, expected, personAttribute, adultoObs, appsObs);

    assertTrue(resultMap.isEmpty(patient.getId()));
    assertEquals(false, resultMap.get(patient.getId()).getValue());
  }

  private CalculationResultMap getResultMap(
      Patient patient,
      KeyPopulationCalculation.KeyPop expectedClassification,
      PersonAttribute personAttribute,
      Obs adultoObs,
      Obs appsObs) {

    when(hivMetadata.getDrugUseConcept()).thenReturn(drugUseConcept);
    when(hivMetadata.getHomosexualConcept()).thenReturn(homosexualConcept);
    when(hivMetadata.getImprisonmentConcept()).thenReturn(imprisonmentConcept);
    when(hivMetadata.getSexWorkerConcept()).thenReturn(sexWorkerConcept);

    Collection<Integer> cohort = Arrays.asList(patient.getId());
    PatientCalculationContext calculationContext =
        patientCalculationService.createCalculationContext();

    PersonAttributeType personAttributeType = new PersonAttributeType(1);
    when(hivMetadata.getIdentificadorDefinidoLocalmente01()).thenReturn(personAttributeType);
    CalculationResultMap personAttributeResultMap = new CalculationResultMap();
    personAttributeResultMap.put(patient.getId(), new SimpleResult(personAttribute, null));
    when(EptsCalculationUtils.evaluateWithReporting(
            any(DataDefinition.class),
            anyCollectionOf(Integer.class),
            anyMapOf(String.class, Object.class),
            any(PatientCalculation.class),
            any(PatientCalculationContext.class)))
        .thenReturn(personAttributeResultMap);

    EncounterType adultoSeguimento = new EncounterType(6);
    when(hivMetadata.getAdultoSeguimentoEncounterType()).thenReturn(adultoSeguimento);
    Concept keyPop = new Concept(23703);
    when(hivMetadata.getKeyPopulationConcept()).thenReturn(keyPop);
    CalculationResultMap adultoResultMap = new CalculationResultMap();
    adultoResultMap.put(patient.getId(), new ObsResult(adultoObs, null));
    when(eptsCalculationService.allObservations(
            eq(keyPop),
            eq(Arrays.asList(sexWorkerConcept)),
            eq(Arrays.asList(adultoSeguimento)),
            any(Location.class),
            eq(cohort),
            eq(calculationContext)))
        .thenReturn(adultoResultMap);

    EncounterType apss = new EncounterType(35);
    when(hivMetadata.getPrevencaoPositivaSeguimentoEncounterType()).thenReturn(apss);
    when(hivMetadata.getKeyPopulationConcept()).thenReturn(keyPop);
    CalculationResultMap apssResultMap = new CalculationResultMap();
    apssResultMap.put(patient.getId(), new ObsResult(appsObs, null));
    when(eptsCalculationService.allObservations(
            eq(keyPop),
            eq(Arrays.asList(sexWorkerConcept)),
            eq(Arrays.asList(adultoSeguimento)),
            any(Location.class),
            eq(cohort),
            eq(calculationContext)))
        .thenReturn(apssResultMap);

    Map<String, Object> params = new HashMap<>();
    params.put(KeyPopulationCalculation.TYPE, expectedClassification);

    return keyPopulationCalculation.evaluate(cohort, params, calculationContext);
  }
}
