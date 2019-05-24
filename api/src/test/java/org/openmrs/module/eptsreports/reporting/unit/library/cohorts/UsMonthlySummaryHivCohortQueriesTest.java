package org.openmrs.module.eptsreports.reporting.unit.library.cohorts;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.eptsreports.reporting.library.cohorts.HivCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.UsMonthlySummaryHivCohortQueries;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.test.BaseContextMockTest;

public class UsMonthlySummaryHivCohortQueriesTest extends BaseContextMockTest {

  @Mock private HivCohortQueries hivCohortQueries;

  @InjectMocks private UsMonthlySummaryHivCohortQueries usMonthlySummaryHivCohortQueries;

  private CohortDefinition allPatients = new AllPatientsCohortDefinition();

  @Test
  public void getAbandonedArtCareShouldCallHivCohortQueries() {
    when(hivCohortQueries.getPatientsInArtCareWhoAbandoned()).thenReturn(allPatients);
    usMonthlySummaryHivCohortQueries.getAbandonedArtCare();
    verify(hivCohortQueries).getPatientsInArtCareWhoAbandoned();
  }

  @Test
  public void getDeadDuringArtCareShouldCallHivCohortQueries() {
    when(hivCohortQueries.getPatientsInArtCareWhoDied()).thenReturn(allPatients);
    usMonthlySummaryHivCohortQueries.getDeadDuringArtCare();
    verify(hivCohortQueries).getPatientsInArtCareWhoDied();
  }

  @Test
  public void getInArtWhoSuspendedTreatmentShouldCallHivCohortQueries() {
    when(hivCohortQueries.getPatientsInArtWhoSuspendedTreatment()).thenReturn(allPatients);
    usMonthlySummaryHivCohortQueries.getInArtWhoSuspendedTreatment();
    verify(hivCohortQueries).getPatientsInArtWhoSuspendedTreatment();
  }

  @Test
  public void getInArtTransferredOutShouldCallHivCohortQueries() {
    when(hivCohortQueries.getPatientsInArtTransferredOutToAnotherHealthFacility())
        .thenReturn(allPatients);
    usMonthlySummaryHivCohortQueries.getInArtTransferredOut();
    verify(hivCohortQueries).getPatientsInArtTransferredOutToAnotherHealthFacility();
  }
}
