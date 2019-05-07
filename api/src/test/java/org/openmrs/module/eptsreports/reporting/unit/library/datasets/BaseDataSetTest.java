package org.openmrs.module.eptsreports.reporting.unit.library.datasets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet.ColumnParameters;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.util.OpenmrsUtil;

public class BaseDataSetTest {
  private class SampleDataset extends BaseDataSet {}

  private BaseDataSet baseDataSet = new SampleDataset();

  @Test
  public void addRowShouldAddDisaggregationsAsColumns() {
    ColumnParameters uf =
        new ColumnParameters("UKFemales", "Unknown anos Feminino", "gender=F|age=UK", "UKF");
    ColumnParameters um =
        new ColumnParameters("UKMales", "Unknown anos Masculino", "gender=M|age=UK", "UKM");
    List<ColumnParameters> disaggregations = Arrays.asList(uf, um);
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    CohortIndicator ci = new CohortIndicator();
    assertTrue(dataSetDefinition.getColumns().isEmpty());
    baseDataSet.addRow(
        dataSetDefinition, "test", "TestWithDisaggs", EptsReportUtils.map(ci, ""), disaggregations);
    assertEquals(
        OpenmrsUtil.parseParameterList(uf.getDimensions()),
        dataSetDefinition.getColumns().get(0).getDimensionOptions());
    assertEquals("test-UKF", dataSetDefinition.getColumns().get(0).getName());
    assertEquals(
        "TestWithDisaggs (Unknown anos Feminino)",
        dataSetDefinition.getColumns().get(0).getLabel());

    assertEquals(
        OpenmrsUtil.parseParameterList(um.getDimensions()),
        dataSetDefinition.getColumns().get(1).getDimensionOptions());
    assertEquals("test-UKM", dataSetDefinition.getColumns().get(1).getName());
    assertEquals(
        "TestWithDisaggs (Unknown anos Masculino)",
        dataSetDefinition.getColumns().get(1).getLabel());
  }

  @Test
  public void getParametersShouldReturnStartEndAndLocationParameters() {
    assertEquals(3, baseDataSet.getParameters().size());
    for (Parameter parameter : baseDataSet.getParameters()) {
      assertTrue(parameter.getName().matches("startDate|endDate|location"));
    }
  }
}
