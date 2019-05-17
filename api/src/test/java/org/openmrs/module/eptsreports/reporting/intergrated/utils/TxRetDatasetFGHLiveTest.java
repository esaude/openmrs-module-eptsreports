package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxRetDataset;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Ignore
public class TxRetDatasetFGHLiveTest extends DefinitionsFGHLiveTest {
  @Autowired private TxRetDataset txRetDataset;

  @Autowired private TestsHelper testsHelper;

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
    return testsHelper.getDate("2017-05-16 00:00:00.0");
  }

  @Override
  protected Date getEndDate() {
    return testsHelper.getDate("2019-05-16 00:00:00.0");
  }

  protected Location getLocation() {
    return Context.getLocationService().getLocation(6);
  }

  private Map<Parameter, Object> getTxRetParameterMappings() {
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());
    parameters.put(new Parameter("months", "Months", Integer.class), 12);
    return parameters;
  }

  @Test
  public void constructTxRetDataset() throws EvaluationException {
    Map<Parameter, Object> parameters = getTxRetParameterMappings();

    DataSet result = evaluateCohortDefinition(txRetDataset.constructTxRetDataset(), parameters);
    DataSetRow data = ((MapDataSet) result).getData();
    Assert.assertEquals(
        TxRetCohortDefinitionsFGHLiveTest.DENOMINATOR_VALUE_COUNT,
        ((CohortIndicatorAndDimensionResult) data.getColumnValue("T04SI-ALL")).getValue());
    Assert.assertEquals(
        TxRetCohortDefinitionsFGHLiveTest.NUMERATOR_VALUE_COUNT,
        ((CohortIndicatorAndDimensionResult) data.getColumnValue("T04SA-ALL")).getValue());
  }
}
