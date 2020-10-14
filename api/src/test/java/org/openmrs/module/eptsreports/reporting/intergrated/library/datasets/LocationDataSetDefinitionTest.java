package org.openmrs.module.eptsreports.reporting.intergrated.library.datasets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.datasets.LocationDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class LocationDataSetDefinitionTest extends DefinitionsTest {

  private final LocationDataSetDefinition definition = new LocationDataSetDefinition();

  private final Map<Parameter, Object> parameters = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    executeDataSet("locationDataSetDefinitionTest.xml");
  }

  @Test
  public void evaluateShouldReturnDataSetWithStateProvinceColumn() throws EvaluationException {
    parameters.put(new Parameter("location", "", Location.class), new Location(207));
    SimpleDataSet dataSet = (SimpleDataSet) evaluateDatasetDefinition(definition, parameters);

    DataSetRow dataSetRow = dataSet.getRows().get(0);
    Map<String, Object> columnValuesByKey = dataSetRow.getColumnValuesByKey();
    assertTrue(columnValuesByKey.keySet().contains("STATEPROVINCE"));
    assertTrue(columnValuesByKey.keySet().contains("COUNTYDISTRICT"));

    assertEquals("Tete", columnValuesByKey.get("STATEPROVINCE"));
    assertEquals("Chiuta", columnValuesByKey.get("COUNTYDISTRICT"));
  }
}
