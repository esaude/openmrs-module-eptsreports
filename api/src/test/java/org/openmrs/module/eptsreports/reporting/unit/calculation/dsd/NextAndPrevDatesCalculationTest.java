package org.openmrs.module.eptsreports.reporting.unit.calculation.dsd;

import static org.mockito.Mockito.when;

import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.patient.PatientCalculationServiceImpl;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.calculation.dsd.NextAndPrevDatesCalculation;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.module.eptsreports.reporting.unit.PowerMockBaseContextTest;
import org.powermock.api.mockito.PowerMockito;

public class NextAndPrevDatesCalculationTest extends PowerMockBaseContextTest {
  @Mock private HivMetadata hivMetadata;
  @Mock private EPTSCalculationService eptsCalculationService;

  @Spy
  private PatientCalculationService patientCalculationService = new PatientCalculationServiceImpl();

  private TestsHelper testsHelper;
  private NextAndPrevDatesCalculation nextAndPrevDatesCalculation;

  @Before
  public void init() {
    PowerMockito.mockStatic(Context.class);
    when(Context.getRegisteredComponents(HivMetadata.class))
        .thenReturn(Collections.singletonList(hivMetadata));
    when(Context.getService(PatientCalculationService.class)).thenReturn(patientCalculationService);
    when(Context.getRegisteredComponents(EPTSCalculationService.class))
        .thenReturn(Collections.singletonList(eptsCalculationService));
    testsHelper = new TestsHelper();
    nextAndPrevDatesCalculation = new NextAndPrevDatesCalculation();
  }

  @Test
  public void evaluateShouldReturnTrueForPatientsWhoAreScheduledForTheNextPickup() {
    Date lastDrugsPickupDate = testsHelper.getDate("2018-05-01 00:00:00.0");
    Date nextDrugsPickupDate = testsHelper.getDate("2018-09-19 00:00:00.0");

    Location location = new Location(1);
    Person person = new Person(1);
    Concept concept = new Concept(5096);
    EncounterType encounterTypeList = new EncounterType(18);

    Obs obs1 = new Obs(person, concept, lastDrugsPickupDate, location);
    obs1.setObsId(1);
    Obs obs2 = new Obs(person, concept, nextDrugsPickupDate, location);
    obs2.setObsId(2);
    List<Obs> obsList = Arrays.asList(obs1, obs2);
  }

  private CalculationResultMap getResultMap(Patient patient, Location location, List<Obs> obsList) {
    EncounterType farmacia = new EncounterType(18);

    when(hivMetadata.getARVPharmaciaEncounterType()).thenReturn(farmacia);

    PatientCalculationContext calculationContext =
        patientCalculationService.createCalculationContext();
    calculationContext.addToCache("location", location);
    Collection<Integer> cohort = Arrays.asList(patient.getId());

    CalculationResultMap map = new CalculationResultMap();

    return map;
  }
}
