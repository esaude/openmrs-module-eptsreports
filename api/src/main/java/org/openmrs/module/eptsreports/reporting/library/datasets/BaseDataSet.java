/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;

public abstract class BaseDataSet {

  /**
   * Adds a row to a dataset based on an indicator and a list of column parameters
   *
   * @param cohortDsd the dataset
   * @param baseName the base columm name
   * @param baseLabel the base column label
   * @param indicator the indicator
   * @param columns the column parameters
   */
  public void addRow(
      CohortIndicatorDataSetDefinition cohortDsd,
      String baseName,
      String baseLabel,
      Mapped<CohortIndicator> indicator,
      List<ColumnParameters> columns) {

    for (ColumnParameters column : columns) {
      String name = baseName + "-" + column.getColumn();
      String label = baseLabel + " (" + column.getLabel() + ")";
      cohortDsd.addColumn(name, label, indicator, column.getDimensions());
    }
  }

  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }

  public static class ColumnParameters {

    private String name;

    private String label;

    private String dimensions;

    private String column;

    /**
     * Default constructor
     *
     * @param name the name
     * @param label the label
     * @param dimensions the dimension parameters
     */
    public ColumnParameters(String name, String label, String dimensions, String column) {
      this.name = name;
      this.label = label;
      this.dimensions = dimensions;
      this.column = column;
    }

    /**
     * Gets the name
     *
     * @return the name
     */
    public String getName() {
      return name;
    }

    /**
     * Gets the label
     *
     * @return the label
     */
    public String getLabel() {
      return label;
    }

    /**
     * Gets the dimension parameters
     *
     * @return the dimension parameters
     */
    public String getDimensions() {
      return dimensions;
    }

    /**
     * Gets the column
     *
     * @return the column
     */
    public String getColumn() {
      return column;
    }
  }
}
