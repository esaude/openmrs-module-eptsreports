package org.openmrs.module.eptsreports.reporting.utils.queries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class ColumnFinderImpl implements ColumnsFinder {

  private List<String> columnsNames;

  @Override
  public List<String> tokenizer(String query) {
    String tokens = getSelectedColumnsFrom(query);
    columnsNames = new ArrayList<>();
    List<String> columns =
        Collections.list(new StringTokenizer(tokens, ",")).stream()
            .map(token -> (String) token)
            .collect(Collectors.toList());

    for (String column : columns) {
      String[] split = column.trim().split(" ");
      String lastToken = split[split.length - 1];
      String columnName;
      if (lastToken.contains("(") && lastToken.contains(")")) {
        addColumnNameToTheList(lastToken);
      } else if (lastToken.contains(".")) {
        columnName = lastToken.substring(lastToken.lastIndexOf(".") + 1);
        addColumnNameToTheList(columnName);
      } else {
        addColumnNameToTheList(lastToken);
      }
    }

    return columnsNames;
  }

  private void addColumnNameToTheList(String columnName) {

    if (columnName.contains("`")) {
      columnsNames.add(columnName);
    } else {
      columnsNames.add("`" + columnName + "`");
    }
  }

  private String getSelectedColumnsFrom(String query) {
    String lowerCaseQuery = query.toLowerCase();
    int firstSelect = lowerCaseQuery.indexOf("select") + 6;
    int firstFrom = lowerCaseQuery.indexOf("from");
    String columns = query.substring(firstSelect, firstFrom);

    return columns;
  }
}
