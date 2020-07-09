package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class TxCurrCohortQueriesTest extends DefinitionsTest {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Before
  public void setup() throws Exception {
    executeDataSet("txcurr-DataTest.xml");
  }

  @Test
  public void testGetPatientWithSTARTDRUGSObsBeforeOrOnEndDate() throws EvaluationException {

    CohortDefinition cd = txCurrCohortQueries.getPatientWithSTARTDRUGSObsBeforeOrOnEndDate();
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrBefore", "onOrBefore", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(new Integer(1001)));
  }

  @Test
  public void testGetPatientEnrolledInArtProgramByEndReportingPeriod() throws EvaluationException {
    CohortDefinition cd = txCurrCohortQueries.getPatientEnrolledInArtProgramByEndReportingPeriod();

    Map<Parameter, Object> parameters = new HashMap<>();

    parameters.put(new Parameter("onOrBefore", "onOrBefore", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(2, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(new Integer(1001)));
  }

  @Test
  public void testGetPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate()
      throws EvaluationException {
    CohortDefinition cd =
        txCurrCohortQueries.getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate();

    Map<Parameter, Object> parameters = new HashMap<>();

    parameters.put(new Parameter("onOrBefore", "onOrBefore", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(new Integer(1002)));
  }

  @Test
  public void testGetPatientsWhoHavePickedUpDrugsMasterCardByEndReporingPeriod()
      throws EvaluationException {
    CohortDefinition cd =
        txCurrCohortQueries.getPatientsWhoHavePickedUpDrugsMasterCardByEndReporingPeriod();
    Map<Parameter, Object> parameters = new HashMap<>();

    parameters.put(new Parameter("onOrBefore", "onOrBefore", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(new Integer(1002)));
  }

  @Test
  public void testGetPatientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate()
      throws EvaluationException {
    CohortDefinition cd =
        txCurrCohortQueries
            .getPatientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate();

    Map<Parameter, Object> parameters = new HashMap<>();

    parameters.put(new Parameter("onOrBefore", "onOrBefore", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(new Integer(1003)));
  }

  @Test
  public void testGetDeadPatientsInDemographiscByReportingEndDate() throws EvaluationException {
    CohortDefinition cd = txCurrCohortQueries.getDeadPatientsInDemographiscByReportingEndDate();

    Map<Parameter, Object> parameters = new HashMap<>();

    parameters.put(new Parameter("onOrBefore", "onOrBefore", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(new Integer(1004)));
  }

  @Test
  public void testGetPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate()
      throws EvaluationException {
    CohortDefinition cd =
        txCurrCohortQueries.getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate();

    Map<Parameter, Object> parameters = new HashMap<>();

    parameters.put(new Parameter("onOrBefore", "onOrBefore", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());
    assertTrue(evaluatedCohort.getMemberIds().contains(new Integer(12594)));
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
    return Context.getLocationService().getLocation(6);
  }

  @Override
  protected void setParameters(
      Date startDate, Date endDate, Location location, EvaluationContext context) {

    context.addParameterValue("startDate", startDate);
    context.addParameterValue("onOrBefore", endDate);
    context.addParameterValue("location", location);
  }
}
