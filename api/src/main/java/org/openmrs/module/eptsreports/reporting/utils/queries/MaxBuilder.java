package org.openmrs.module.eptsreports.reporting.utils.queries;

import java.util.List;

public class MaxBuilder {

  private String query;
  private List<String> columnsNames;
  private ColumnsFinder columnsFinder;

  public MaxBuilder(String query) {
    this.query = query;
    columnsFinder = new ColumnFinderImpl();
    columnsNames = tokenizer(query);
  }

  public String getQuery() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("SELECT ");
    stringBuilder.append(columnsNames.get(0));
    stringBuilder.append(",MAX( ");
    stringBuilder.append(columnsNames.get(1));
    stringBuilder.append(" ) FROM ( ");
    stringBuilder.append(query);
    stringBuilder.append(" ) most_recent ");
    stringBuilder.append(" GROUP BY most_recent.");
    stringBuilder.append(columnsNames.get(0));

    return stringBuilder.toString();
  }

  private List<String> tokenizer(String query) {

    List<String> columns = columnsFinder.tokenizer(query);

    if (columns.size() != 2) {
      throw new RuntimeException("Your query result must have two columns");
    }
    return columns;
  }
}
