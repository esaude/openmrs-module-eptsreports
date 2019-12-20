package org.openmrs.module.eptsreports.reporting.unit.calculation.txml;

import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.patient.PatientCalculationServiceImpl;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.calculation.txml.StartedArtOnLastClinicalContactCalculation;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.module.eptsreports.reporting.unit.PowerMockBaseContextTest;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest(EptsCalculationUtils.class)
public class StartedArtOnLastClinicalContactCalculationTest extends PowerMockBaseContextTest {
  @Mock private HivMetadata hivMetadata;
  @Mock private EPTSCalculationService eptsCalculationService;

  @Spy
  private PatientCalculationService patientCalculationService = new PatientCalculationServiceImpl();

  private StartedArtOnLastClinicalContactCalculation startedArtOnLastClinicalContactCalculation;

  private TestsHelper testsHelper;

  @Before
  public void setUp() {
    PowerMockito.mockStatic(Context.class);
    PowerMockito.mockStatic(EptsCalculationUtils.class);
    when(Context.getRegisteredComponents(HivMetadata.class))
        .thenReturn(Collections.singletonList(hivMetadata));
    when(Context.getRegisteredComponents(EPTSCalculationService.class))
        .thenReturn(Collections.singletonList(eptsCalculationService));
    when(Context.getService(PatientCalculationService.class)).thenReturn(patientCalculationService);
    startedArtOnLastClinicalContactCalculation = new StartedArtOnLastClinicalContactCalculation();
    testsHelper = new TestsHelper();
  }

  @Test
  public void evaluateShouldReturnPatientsOnArtForLessThan90Days() {
    Date artStartDate = testsHelper.getDate("2018-05-01 00:00:00.0");
    Date lastClinicalContactDate = testsHelper.getDate("2018-08-01 00:00:00.0");

    Location location = new Location(1);
    Person person = new Person(1);
  }
}
