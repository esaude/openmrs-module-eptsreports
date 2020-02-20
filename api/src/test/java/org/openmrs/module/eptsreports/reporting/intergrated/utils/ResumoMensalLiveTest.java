package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class ResumoMensalLiveTest extends DefinitionsFGHLiveTest {

  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Test
  public void A1() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1000));
  }

  @Test
  public void A2() throws EvaluationException {
    List<Integer> patients = Arrays.asList(1001, 1012, 1013, 1014);
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertEquals(4, evaluatedCohort.size());
    assertTrue(evaluatedCohort.getMemberIds().containsAll(patients));
  }

  @Test
  public void A3() throws EvaluationException {
    List<Integer> patients = Arrays.asList(1000, 1001, 1012, 1013, 1014);
    CohortDefinition cd = resumoMensalCohortQueries.getSumOfA1AndA2();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertEquals(5, evaluatedCohort.size());
    assertTrue(evaluatedCohort.getMemberIds().containsAll(patients));
  }

  @Test
  public void B1() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1002));
  }

  @Test
  public void B2() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries
            .getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1003));
  }

  //  @Test
  //  public void B3() throws EvaluationException {
  //    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWithStartDrugs();
  //    Map<Parameter, Object> mappings = new HashMap<>();
  //    mappings.put(new Parameter("onOrAfter", "value1", Date.class), getStartDate());
  //    mappings.put(new Parameter("onOrBefore", "value2", Date.class), getEndDate());
  //    mappings.put(new Parameter("locationList", "locationList", Location.class), getLocation());
  //    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, mappings);
  //    assertEquals(1, evaluatedCohort.size());
  //    assertTrue(evaluatedCohort.contains(1004));
  //  }
  //
  //  @Test
  //  public void B5() throws EvaluationException {
  //    CohortDefinition cd = resumoMensalCohortQueries.getPatientsTransferredOut();
  //    Map<Parameter, Object> mappings = new HashMap<>();
  //    mappings.put(new Parameter("onOrAfter", "value1", Date.class), getStartDate());
  //    mappings.put(new Parameter("onOrBefore", "value2", Date.class), getEndDate());
  //    mappings.put(new Parameter("locationList", "locationList", Location.class), getLocation());
  //    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, mappings);
  //    assertEquals(1, evaluatedCohort.size());
  //    assertTrue(evaluatedCohort.contains(1005));
  //  }
  //
  //  @Test
  //  public void B6() throws EvaluationException {
  //    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWhoSuspendedTreatment();
  //    Map<Parameter, Object> mappings = new HashMap<>();
  //    mappings.put(new Parameter("onOrAfter", "value1", Date.class), getStartDate());
  //    mappings.put(new Parameter("onOrBefore", "value2", Date.class), getEndDate());
  //    mappings.put(new Parameter("locationList", "locationList", Location.class), getLocation());
  //    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, mappings);
  //    assertEquals(1, evaluatedCohort.size());
  //    assertTrue(evaluatedCohort.contains(1006));
  //  }
  //
  //  @Test
  //  public void B7() throws EvaluationException {
  //    CohortDefinition cd =
  //        resumoMensalCohortQueries.getNumberOfPatientsWhoAbandonedArtDuringCurrentMonthB7();
  //
  //    Calendar endDateMinus90Days = Calendar.getInstance();
  //    endDateMinus90Days.setTime(getEndDate());
  //    endDateMinus90Days.add(Calendar.DAY_OF_MONTH, -90);
  //    Map<Parameter, Object> mappings = new HashMap<>();
  //    mappings.put(new Parameter("value1", "value1", Date.class), endDateMinus90Days.getTime());
  //    mappings.put(new Parameter("value2", "value2", Date.class), getEndDate());
  //    mappings.put(new Parameter("locationList", "locationList", Location.class), getLocation());
  //    mappings.put(new Parameter("onOrBefore", "onOrBefore", Date.class), getEndDate());
  //    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, mappings);
  //    assertEquals(1, evaluatedCohort.size());
  //    assertTrue(evaluatedCohort.contains(1007));
  //  }
  //
  //  @Test
  //  public void B8() throws EvaluationException {
  //    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWhoDied();
  //    Map<Parameter, Object> mappings = new HashMap<>();
  //    mappings.put(new Parameter("onOrAfter", "value1", Date.class), getStartDate());
  //    mappings.put(new Parameter("onOrBefore", "value2", Date.class), getEndDate());
  //    mappings.put(new Parameter("locationList", "locationList", Location.class), getLocation());
  //    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, mappings);
  //    assertEquals(1, evaluatedCohort.size());
  //    assertTrue(evaluatedCohort.contains(1008));
  //  }
  //
  //  @Test
  //  public void B10() throws EvaluationException {
  //    CohortDefinition cd =
  //        resumoMensalCohortQueries.getPatientsWhoStartedArtByEndOfPreviousMonthB10();
  //    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
  //    assertEquals(1, evaluatedCohort.size());
  //    assertTrue(evaluatedCohort.contains(1009));
  //  }
  //
  //  @Test
  //  public void B12() throws EvaluationException {
  //    List<Integer> patients = Arrays.asList(1009, 1010, 1011);
  //    CohortDefinition cd =
  //        resumoMensalCohortQueries.getPatientsWhoWereActiveByEndOfPreviousMonthB12();
  //    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
  //    assertEquals(3, evaluatedCohort.size());
  //    assertTrue(evaluatedCohort.getMemberIds().containsAll(patients));
  //  }
  //
  //  @Test
  //  public void C1() throws EvaluationException {
  //    CohortDefinition cd =
  //
  // resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndScreenedTB();
  //    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
  //    assertEquals(1, evaluatedCohort.size());
  //    assertTrue(evaluatedCohort.contains(1012));
  //  }
  //
  //  @Test
  //  public void C2() throws EvaluationException {
  //    CohortDefinition cd =
  //
  // resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndStartedTPI();
  //    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
  //    assertEquals(1, evaluatedCohort.size());
  //    assertTrue(evaluatedCohort.contains(1013));
  //  }
  //
  //  @Test
  //  public void C3() throws EvaluationException {
  //    CohortDefinition cd =
  //        resumoMensalCohortQueries
  //            .getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndDiagnosedForActiveTB();
  //    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
  //    assertEquals(1, evaluatedCohort.size());
  //    assertTrue(evaluatedCohort.contains(1014));
  //  }

  @Override
  protected Date getStartDate() {
    return DateUtil.getDateTime(2018, 6, 21);
  }

  @Override
  protected Date getEndDate() {
    return DateUtil.getDateTime(2018, 7, 20);
  }

  @Override
  protected Location getLocation() {
    return Context.getLocationService().getLocation(540);
  }

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "eSaude123";
  }
}
