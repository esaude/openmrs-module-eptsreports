package org.openmrs.module.eptsreports.reporting.utils.queries;

import java.util.List;

public interface ColumnsFinder {

  List<String> tokenizer(String query);
}
