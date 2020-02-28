package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.assertEquals;
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
    executeDataSet("ResumoMensalTest.xml");
  }

  public void Test() {}

  @Override
  protected Date getStartDate() {
    return DateUtil.getDateTime(2019, 9, 21);
  }

  @Override
  protected Date getEndDate() {
    return DateUtil.getDateTime(2019, 10, 20);
  }

  @Override
  protected Location getLocation() {
    return Context.getLocationService().getLocation(21);
  }

  @Override
  protected void setParameters(
      Date startDate, Date endDate, Location location, EvaluationContext context) {

    context.addParameterValue("onOrAfter", startDate);
    context.addParameterValue("onOrBefore", endDate);
    context.addParameterValue("location", location);
  }

  @Test
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

  @Ignore
  // TODO add some mappings on the dataset to properly test the whole query
  public void getPatientsWhoInitiatedPreArtDuringCurrentMonthWithConditions()
      throws EvaluationException {
    CohortDefinition cohort =
        resumoMensalCohortQueries.getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort);

    assertEquals(0, evaluatedCohort.getMemberIds().size());
  }

  @Ignore
  public void getPatientsWhoWereActiveByEndOfPreviousMonthB12() throws EvaluationException {
    CohortDefinition cohort =
        resumoMensalCohortQueries.getPatientsWhoWereActiveByEndOfPreviousMonthB12();
    Map<Parameter, Object> params = new HashMap<>();
    params.put(new Parameter("onOrAfter", "onOrAfter", Date.class), getStartDate());
    params.put(new Parameter("onOrBefore", "onOrBefore", Date.class), getEndDate());
    params.put(new Parameter("location", "location", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, params);

    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(1003));
  }
}
