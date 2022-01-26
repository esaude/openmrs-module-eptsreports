package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.EvaluatableDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;

import java.util.List;

/**
 * Simple dataset of location_attribute properties. Actually supported:
 * location_attribute_type.name, location.name and location_attribute.value_reference => Datim Code
 */
public class DatimCodeDatasetDefinition extends EvaluatableDataSetDefinition {

  protected int typeId;

  protected String columnName;

  protected String getQuery(int typeId, String columnName) {

    return "SELECT lat.name as locationAttributeName, loc.name as locationName, la.value_reference as "
        + columnName
        + " FROM location_attribute la "
        + "       INNER JOIN location_attribute_type lat ON lat.location_attribute_type_id = la.attribute_type_id "
        + "       INNER JOIN location loc ON loc.location_id = la.location_id "
        + " WHERE lat.location_attribute_type_id = "
        + typeId;
  }

  public DatimCodeDatasetDefinition() {
    this.typeId = 2;
    this.columnName = "datimCode";
  }

  @Override
  public DataSet evaluate(EvaluationContext context) {
    SimpleDataSet dataSet = new SimpleDataSet(this, context);
    SqlQueryBuilder queryBuilder = new SqlQueryBuilder();

    queryBuilder.append(getQuery(typeId, columnName));
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
