package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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
  public void getDeadPatientsShouldReturn() throws EvaluationException {

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
  public void getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthA2()
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
  public void getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1()
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
  public void getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(2, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1003));
  }

  @Test
  public void getNumberOfPatientInitiedPreArtDuringCurrentMothA2() throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getNumberOfPatientInitiedPreArtDuringCurrentMothA2();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(2, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1003));
  }

  @Test
  public void getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthC1()
      throws EvaluationException {
    CohortDefinition cd =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthC1();

    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);
    assertEquals(2, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(1003));
  }
}
