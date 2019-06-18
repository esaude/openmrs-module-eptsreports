package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXRetCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class TxRetCohortDefinitionsFGHLiveTest extends DefinitionsFGHLiveTest {
  @Autowired private TXRetCohortQueries txRetCohortQueries;

  @Autowired private TestsHelper testsHelper;

  public static int NUMERATOR_VALUE_COUNT = 568;

  public static int DENOMINATOR_VALUE_COUNT = 1082;

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "Admin123";
  }

  @Override
  protected Date getStartDate() {
    return testsHelper.getDate("2016-05-16 00:00:00.0");
  }

  @Override
  protected Date getEndDate() {
    return testsHelper.getDate("2018-05-16 00:00:00.0");
  }

  protected Location getLocation() {
    return Context.getLocationService().getLocation(6);
  }

  // numerator
  @Test
  public void inCourtForTwelveMonths() throws EvaluationException {
    Map<Parameter, Object> parameters = getTxRetParameterMappings();

    EvaluatedCohort result =
        evaluateCohortDefinition(txRetCohortQueries.inCourtForTwelveMonths(), parameters);
    Assert.assertEquals(NUMERATOR_VALUE_COUNT, result.size());
  }

  // denominator
  @Test
  public void courtNotTransferredTwelveMonths() throws EvaluationException {
    Map<Parameter, Object> parameters = getTxRetParameterMappings();

    EvaluatedCohort result =
        evaluateCohortDefinition(txRetCohortQueries.courtNotTransferredTwelveMonths(), parameters);
    Assert.assertEquals(DENOMINATOR_VALUE_COUNT, result.size());
  }

  private Map<Parameter, Object> getTxRetParameterMappings() {
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());
    parameters.put(new Parameter("months", "Months", Integer.class), 12);
    return parameters;
  }
}
