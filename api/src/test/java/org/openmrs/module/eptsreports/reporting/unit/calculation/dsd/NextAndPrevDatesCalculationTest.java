package org.openmrs.module.eptsreports.reporting.unit.calculation.dsd;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.patient.PatientCalculationServiceImpl;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.calculation.dsd.NextAndPrevDatesCalculation;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.module.eptsreports.reporting.unit.PowerMockBaseContextTest;
import org.powermock.api.mockito.PowerMockito;

import java.util.Collections;

import static org.mockito.Mockito.when;

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
        when(Context.getRegisteredComponents(HivMetadata.class)).thenReturn(Collections.singletonList(hivMetadata));
        when(Context.getService(PatientCalculationService.class)).thenReturn(patientCalculationService);
        when(Context.getRegisteredComponents(EPTSCalculationService.class)).thenReturn(Collections.singletonList(eptsCalculationService));
        testsHelper = new TestsHelper();
        nextAndPrevDatesCalculation = new NextAndPrevDatesCalculation();
    }

    @Test
    public  void evaluateShouldReturnTrueForPatientsWhoAreScheduledForTheNextPickup(){

        Location location = new Location(1);
        Concept concept = new Concept(1234);

    }

}
