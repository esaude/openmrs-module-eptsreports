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
import org.openmrs.module.eptsreports.reporting.library.cohorts.APSSResumoTrimestralCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class APSSResumoTrimestralCohortQueriesTest extends DefinitionsTest {

  @Autowired private APSSResumoTrimestralCohortQueries APSSResumoTrimestralCohortQueries;

  @Before
  public void setup() throws Exception {
    // executeDataSet("metadata.xml");
    executeDataSet("APSSResumoTrimestralTest.xml");
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

  @Ignore
  public void getA1ShouldReturn() throws EvaluationException {

    CohortDefinition cohort = APSSResumoTrimestralCohortQueries.getA1();

    Map<Parameter, Object> parameters = new HashMap<>();

    parameters.put(new Parameter("onOrAfter", "onOrAfter", Date.class), this.getStartDate());
    parameters.put(new Parameter("onOrBefore", "onOrBefore", Date.class), this.getEndDate());
    parameters.put(new Parameter("locationList", "Location", Location.class), this.getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);

    assertEquals(3, evaluatedCohort.getMemberIds().size());
  }

}
