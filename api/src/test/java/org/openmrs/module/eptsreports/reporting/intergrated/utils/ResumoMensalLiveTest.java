package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.library.cohorts.HivCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class ResumoMensalLiveTest extends DefinitionsFGHLiveTest {

  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Autowired private HivCohortQueries hivCohortQueries;

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
    assertEquals(93, evaluatedCohort.size());
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

  @Test
  public void B3() throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWithStartDrugs();
    Map<Parameter, Object> mappings = new HashMap<>();
    mappings.put(new Parameter("onOrAfter", "value1", Date.class), getStartDate());
    mappings.put(new Parameter("onOrBefore", "value2", Date.class), getEndDate());
    mappings.put(new Parameter("locationList", "locationList", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, mappings);
    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1004));
  }

  @Test
  public void B5() throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsTransferredOutB5(true);
    Map<Parameter, Object> mappings = new HashMap<>();
    mappings.put(new Parameter("onOrAfter", "value1", Date.class), getStartDate());
    mappings.put(new Parameter("onOrBefore", "value2", Date.class), getEndDate());
    mappings.put(new Parameter("locationList", "locationList", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, mappings);
    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1005));
  }

  @Test
  public void B6() throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWhoSuspendedTreatmentB6(true);
    Map<Parameter, Object> mappings = new HashMap<>();
    mappings.put(new Parameter("onOrAfter", "value1", Date.class), getStartDate());
    mappings.put(new Parameter("onOrBefore", "value2", Date.class), getEndDate());
    mappings.put(new Parameter("locationList", "locationList", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, mappings);
    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1006));
  }

  @Test
  public void B8() throws EvaluationException {
    CohortDefinition cd = resumoMensalCohortQueries.getPatientsWhoDied(true);
    CohortDefinition cf = resumoMensalCohortQueries.getPatientsWhoDied(false);

    Map<Parameter, Object> mappings = new HashMap<>();
    mappings.put(new Parameter("onOrAfter", "value1", Date.class), getStartDate());
    mappings.put(new Parameter("onOrBefore", "value2", Date.class), getEndDate());
    mappings.put(new Parameter("locationList", "locationList", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, mappings);
    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1008));

    EvaluatedCohort evaluatedCohort2 = evaluateCohortDefinition(cf, mappings);
    assertEquals(1, evaluatedCohort2.size());
    assertTrue(evaluatedCohort2.contains(1008));
  }

  @Test
  public void B10() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoStartedArtByEndOfPreviousMonthB10();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1009));
  }

  @Test
  public void B12() throws EvaluationException {
    List<Integer> patients = Arrays.asList(1009, 1010, 1011);
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoWereActiveByEndOfPreviousMonthB12();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertEquals(3, evaluatedCohort.size());
    assertTrue(evaluatedCohort.getMemberIds().containsAll(patients));
  }

  @Test
  public void C1() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndScreenedTB();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1012));
  }

  @Test
  public void C2() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndStartedTpiC2();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1013));
  }

  @Test
  public void C3() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries
            .getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndDiagnosedForActiveTBC3();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1014));
  }

  @Override
  protected Date getStartDate() {
    return DateUtil.getDateTime(2019, 11, 21);
  }

  @Override
  protected Date getEndDate() {
    return DateUtil.getDateTime(2019, 12, 20);
  }

  @Override
  protected Location getLocation() {
    return Context.getLocationService().getLocation(221);
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
