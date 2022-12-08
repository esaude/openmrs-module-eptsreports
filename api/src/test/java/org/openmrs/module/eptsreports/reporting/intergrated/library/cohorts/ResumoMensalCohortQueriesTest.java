package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.*;

import java.util.*;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.CohortMembership;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class ResumoMensalCohortQueriesTest extends DefinitionsTest {

  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Before
  public void setup() throws Exception {
    // executeDataSet("metadata.xml");
    executeDataSet("ResumoMensalTest.xml");
  }

  @Test
  public void Test() {}

  @Override
  protected Date getStartDate() {
    return DateUtil.getDateTime(2019, 9, 21);
  }

  @Override
  protected Date getEndDate() {
    return DateUtil.getDateTime(2019, 12, 20);
  }

  @Override
  protected Location getLocation() {
    return Context.getLocationService().getLocation(21);
  }

  @Override
  protected void setParameters(
      Date startDate, Date endDate, Location location, EvaluationContext context) {
    context.addParameterValue("startDate", startDate);
    context.addParameterValue("onOrAfter", startDate);
    context.addParameterValue("onOrBefore", endDate);
    context.addParameterValue("location", location);
  }

  @Test
  public void getPatientsWhoDiedShouldReturn() throws EvaluationException {

    CohortDefinition cohort = resumoMensalCohortQueries.getPatientsWhoDied(true);

    Map<Parameter, Object> parameters = new HashMap<>();

    parameters.put(new Parameter("onOrAfter", "onOrAfter", Date.class), this.getStartDate());
    parameters.put(new Parameter("onOrBefore", "onOrBefore", Date.class), this.getEndDate());
    parameters.put(new Parameter("locationList", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);

    assertEquals(3, evaluatedCohort.getMemberIds().size());

    // DEAD IN DEMOGRAPHIC
    assertTrue(evaluatedCohort.getMemberIds().contains(1020));

    // DEAD DEMOGRAPHIC WITH ENCOUNTER POST DEAD DATE
    assertFalse(evaluatedCohort.getMemberIds().contains(1021));

    // DEAD IN ART
    assertTrue(evaluatedCohort.getMemberIds().contains(1022));

    // DEAD IN PRE ART
    assertTrue(evaluatedCohort.getMemberIds().contains(1023));
  }

  @Ignore
  public void
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthA2ShouldPass()
          throws EvaluationException {
    CohortDefinition cohort =
        resumoMensalCohortQueries
            .getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthA2();
    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrAfter", "", Date.class), getStartDate());
    parameters.put(new Parameter("onOrBefore", "", Date.class), getEndDate());
    parameters.put(new Parameter("location", "", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);
    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1010));
  }

  @Test
  public void getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1ShouldPass()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1933));
  }

  @Test
  public void getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2ShouldPass()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(8, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1003));
  }

  @Test
  public void getNumberOfPatientInitiedPreArtDuringCurrentMothA2ShouldPass()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getNumberOfPatientInitiedPreArtDuringCurrentMothA2();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(8, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1003));
  }

  @Test
  public void getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthC1ShouldPass()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthC1();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);
    assertEquals(8, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1003));
  }

  @Test
  public void
      getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1ShouldReturnPatientsWhoInitiatedTarvShouldPass()
          throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1021));
  }

  @Test
  public void getSumOfA1AndA2ShouldReturnSumOfA1AndA2ShouldPass() throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getSumOfA1AndA2();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(9, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1933));
  }

  @Test
  public void getPatientsStartedArtOnFilaOrArvPickupShouldPass() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsStartedArtOnFilaOrArvPickupDuringThePeriod();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(2, evaluatedCohort.getMemberships().size());
    long patientId =
        evaluatedCohort.getMemberships().stream()
            .filter(member -> member.getPatientId() == 12475)
            .findFirst()
            .get()
            .getPatientId();

    assertEquals(patientId, 12475);
  }

  @Test
  public void getPatientsRestartedArtOnFilaOrArvPickupShouldNotGetWhoStartedBeforeStartDate()
      throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWhoRestartedArtOnFilaOrArvPickup();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    List<CohortMembership> memberships =
        evaluatedCohort.getMemberships().stream()
            .filter(
                member -> {
                  if (member.getPatientId() == 1001 || member.getPatientId() == 1009) return true;
                  return false;
                })
            .collect(Collectors.toList());

    assertTrue(memberships.isEmpty());
  }

  @Test
  public void getPatientsRestartedArtOnFilaOrArvPickupShouldPass() throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWhoRestartedArtOnFilaOrArvPickup();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);
    assertEquals(2, evaluatedCohort.getMemberships().size());

    List<Integer> members = new ArrayList<>();
    evaluatedCohort.getMemberships().stream().forEach(member -> members.add(member.getPatientId()));

    assertTrue(members.contains(1021));
    assertTrue(members.contains(12475));
    assertFalse(members.contains(1009));
  }

  @Test
  public void getPatientsStartedArtOnAnyFacilityBeforeTheEndOfMonth() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoStartedArtOnAnyHeathFacilityRf33();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);
    assertEquals(5, evaluatedCohort.getMemberships().size());

    List<Integer> members =
        evaluatedCohort.getMemberships().stream()
            .map(member -> member.getPatientId())
            .collect(Collectors.toList());

    assertTrue(members.contains(1021));
    assertTrue(members.contains(12475));
    assertTrue(members.contains(1009));
    assertTrue(members.contains(1001));
  }

  @Test
  public void getPatientsStartedArtOnAnyFacilityBeforeStartDateOnSpecifiedFacility()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsStartedArtOnFilaOrArvPickupBeforeDate();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);
    assertEquals(3, evaluatedCohort.getMemberships().size());

    List<Integer> members =
        evaluatedCohort.getMemberships().stream()
            .map(member -> member.getPatientId())
            .collect(Collectors.toList());

    System.out.println(members);
    assertTrue(members.contains(5642));
    assertTrue(members.contains(1021));
    assertTrue(members.contains(12475));
  }

  @Test
  public void getPatientsTransferredOutB5ShouldReturnPatientsTransferredOut()
      throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsTransferredOutB5(true);

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrAfter", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("onOrBefore", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberships().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(12304));
  }

  @Test
  public void getPatientsWithStartDrugsShouldReturnPatientsWithStartDrugs()
      throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWithStartDrugs();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrAfter", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("onOrBefore", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("locationList", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(12580));
  }

  @Ignore("Query not supported in H2 database")
  public void getPatientsWhoSuspendedTreatmentB6ShouldReturnPatientsWhoSuspendedTreatment()
      throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWhoSuspendedTreatmentB6(true);

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrAfter", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("onOrBefore", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("locationList", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1933));
  }

  @Test
  public void getPatientsWhoStartedArtByEndOfPreviousMonthB10ShouldReturnWhoStartedArtByEnd()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoStartedArtByEndOfPreviousMonthB10();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberships().size());
    List<Integer> members =
        evaluatedCohort.getMemberships().stream()
            .map(member -> member.getPatientId())
            .collect(Collectors.toList());
    assertFalse(members.contains(1021));
    assertTrue(members.contains(5642));
  }

  @Ignore("Query using DATE_ADD not available in H2")
  @Test
  public void getPatientsWhoWereActiveByEndOfPreviousMonthB12ShouldReturnWhoWereActiveByEnd()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoWereActiveByEndOfPreviousMonthB12();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(3, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1933));
  }

  @Test
  @Ignore
  public void getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndDiagnosedForActiveTBC3ShouldPass()
      throws EvaluationException {

    CohortDefinition cd =
        resumoMensalCohortQueries
            .getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndDiagnosedForActiveTBC3();
    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertFalse(evaluatedCohort.getMemberIds().isEmpty());
    assertTrue(evaluatedCohort.getMemberIds().contains(6000001));
  }

  @Test
  @Ignore
  public void getPatientsWhoStartedTPIShouldPass() throws EvaluationException {

    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndStartedTpiC2();
    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertFalse(evaluatedCohort.getMemberIds().isEmpty());
    assertTrue(evaluatedCohort.getMemberIds().contains(6000002));
  }

  @Test
  public void getPatientScreenedForTbShouldPass() throws EvaluationException {

    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndScreenedTB();
    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertFalse(evaluatedCohort.getMemberIds().isEmpty());
    assertTrue(evaluatedCohort.getMemberIds().contains(6000003));
  }

  @Test
  public void getPatientsWhoHadAtLeastDrugPickUpShouldReturnPatientsWhoHadAtLeastDrugPickUp()
      throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWhoHadAtLeastDrugPickUp();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(5642));
  }

  @Test
  public void
      getNumberOfPatientsTransferredFromByEndOfPreviousMonthB12ShouldReturnPatientsTransferredFrom()
          throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries
            .getNumberOfPatientsTransferredFromAnotherHealthFacilityByEndOfPreviousMonthB12();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrBefore", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(3, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1563));
  }

  @Test
  public void getPatientsPreTarvScreenedTBShouldReturnPatientsPreTarvTB()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndScreenedTB();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(3, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(86526));
  }

  @Test
  public void getPatientsPreTarvDuringCurrentMonthAndScreenedTbC1ShouldReturnPatientsPreTarvTbC1()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndScreenedTbC1();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(2, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(6254));
  }

  @Test
  public void
      getNumberOfPatientTbScreenedInFirstEncounterShouldReturnPatientsTbScreenedInFirstEncounter()
          throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getNumberOfPatientTbScreenedInFirstEncounter();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(4, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(6254));
  }

  @Test
  @Ignore
  public void
      getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndStartedTpiC2ShouldReturnPatientsTpiC2()
          throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndStartedTpiC2();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberships().size());
    // assertTrue(evaluatedCohort.getMemberIds().contains(15236));
  }

  @Test
  public void getPatientsWhoHavePickedUpDrugsMasterCardByEndReporingPeriodShouldPass()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoHavePickedUpDrugsMasterCardByEndReporingPeriod();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrBefore", "onOrBefore", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);
    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1004));
  }

  @Test
  public void getPatientsWithFILAEncounterAndNextPickupDateShouldPass() throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWithFILAEncounterAndNextPickupDate();
    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrBefore", "onOrBefore", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(5642));
  }

  @Test
  public void getPatientsWithTBScreening() throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWithTBScreening();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(4, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(6000003));
  }

  @Ignore("Query using DATE_ADD function not supported in H2 database")
  public void getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries
            .getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(3, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(25000));
  }

  @Test
  public void getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthF1()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries
            .getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthF1();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(11, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(25000));
  }

  @Test
  public void
      getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthAndScreenedForTbF2()
          throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries
            .getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthAndScreenedForTbF2();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(4, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(25000));
  }

  @Ignore("Query using IF function not supported in H2 database")
  public void getNumberOfPatientsWithAtLeastOneClinicalAppointmentDuringTheYearF3()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries
            .getNumberOfPatientsWithAtLeastOneClinicalAppointmentDuringTheYearF3();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(4, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(25000));
  }

  @Ignore("Query using IF function not supported in H2 database")
  public void getPatientsRegisteredAsTransferredInDuringTheStatisticalYear()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsRegisteredAsTransferredInDuringTheStatisticalYear();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(4, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(25000));
  }

  @Ignore("Query using IF function not supported in H2 database")
  public void getExclusionForF3() throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getExclusionForF3();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(4, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(25000));
  }

  @Ignore("Query using IF function not supported in H2 database")
  public void getNumberOfPatientsWhoAbandonedArtDuringCurrentMonthForB7()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getNumberOfPatientsWhoAbandonedArtDuringCurrentMonthForB7();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(4, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(25000));
  }

  @Ignore("Query using IF function not supported in H2 database")
  public void getActivePatientsInARTByEndOfCurrentMonth() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getActivePatientsInARTByEndOfCurrentMonth(false);

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(4, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(25000));
  }

  @Ignore("Query using IF function not supported in H2 database")
  public void getActivePatientsOnArtWhoReceivedVldSuppressionResults() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getActivePatientsOnArtWhoReceivedVldSuppressionResults();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(3, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(25000));
  }

  @Test
  public void getNumberOfPatientsTransferredInOnPeriodShouldReturn() throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getTransferredInPatients(false);

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrAfter", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("onOrBefore", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(12475));
  }
}
