package org.openmrs.module.eptsreports.reporting.utils.queries;

import java.util.ArrayList;
import java.util.List;

public class UnionBuilder {

  List<String> queries = new ArrayList<>();

  public UnionBuilder(String query) {
    queries = new ArrayList<>();

    queries.add(query);
  }

  public UnionBuilder union(String query) {
    queries.add(query);
    return this;
  }

  public String buildQuery() {

    StringBuilder stringBuilder = new StringBuilder();
    for (String query : queries) {

      stringBuilder.append(query).append(" UNION ");
    }
    String unionQuery = stringBuilder.substring(0, stringBuilder.length() - 7);

    return unionQuery;
  }
}
