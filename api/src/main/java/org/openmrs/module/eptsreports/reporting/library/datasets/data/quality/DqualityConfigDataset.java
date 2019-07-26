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
package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.List;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.ConfigQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This dataset will conatin all the configuration setting needed by the data quality report This
 * will include the time the report was runned, the correct date range date format The names
 * associated with the work flow states
 */
@Component
public class DqualityConfigDataset extends BaseDataSet {

  private HivMetadata hivMetadata;

  @Autowired
  public DqualityConfigDataset(HivMetadata hivMetadata) {
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition configDataSetDefinition(List<Parameter> parameterList) {
    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("config");
    dsd.addParameters(parameterList);
    dsd.setSqlQuery(ConfigQueries.getConfigurations(hivMetadata.getARTProgram().getProgramId()));
    return dsd;
  }
}
