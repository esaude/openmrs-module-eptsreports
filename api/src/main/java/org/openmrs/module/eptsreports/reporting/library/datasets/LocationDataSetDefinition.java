package org.openmrs.module.eptsreports.reporting.library.datasets;

import static org.openmrs.module.reporting.ReportingConstants.LOCATION_PARAMETER;

import java.util.List;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.EvaluatableDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;

/**
 * Simple data set of location properties. Currently supported are name, stateProvince and
 * countyDistrict.
 */
public class LocationDataSetDefinition extends EvaluatableDataSetDefinition {

  public LocationDataSetDefinition() {
    addParameter(LOCATION_PARAMETER);
  }

  @Override
  public DataSet evaluate(EvaluationContext context) {
    SimpleDataSet dataSet = new SimpleDataSet(this, context);
    SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
    String sqlQuery =
        "SELECT name, state_province as stateProvince, county_district as countyDistrict, current_timestamp() AS report_date "
            + "FROM location "
            + "WHERE location_id=:location";
    queryBuilder.append(sqlQuery);
    queryBuilder.setParameters(context.getParameterValues());

    EvaluationService evaluationService = Context.getService(EvaluationService.class);
    List<DataSetColumn> columns = evaluationService.getColumns(queryBuilder);
    List<Object[]> results = evaluationService.evaluateToList(queryBuilder, context);

    for (Object[] row : results) {
      addRow(dataSet, row, columns);
    }

    return dataSet;
  }

  private void addRow(SimpleDataSet dataSet, Object[] row, List<DataSetColumn> columns) {
    DataSetRow dataSetRow = new DataSetRow();
    for (int i = 0; i < columns.size(); i++) {
      dataSetRow.addColumnValue(columns.get(i), row[i]);
    }
    dataSet.addRow(dataSetRow);
  }
}
