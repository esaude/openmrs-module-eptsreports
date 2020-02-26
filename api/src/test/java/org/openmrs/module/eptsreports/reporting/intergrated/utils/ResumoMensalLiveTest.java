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
