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
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.data.definition.DataDefinitions;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.DqQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsCommonUtils;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Ec11PatientListDataset extends BaseDataSet {

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  private HivMetadata hivMetadata;

  private EptsCommonUtils eptsCommonUtils;

  private DataDefinitions definitions;

  @Autowired
  public Ec11PatientListDataset(
      SummaryDataQualityCohorts summaryDataQualityCohorts,
      HivMetadata hivMetadata,
      EptsCommonUtils eptsCommonUtils,
      DataDefinitions definitions) {
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.hivMetadata = hivMetadata;
    this.eptsCommonUtils = eptsCommonUtils;
    this.definitions = definitions;
  }

  public DataSetDefinition ec11PatientListDataset(List<Parameter> parameterList) {
    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("EC11");
    dsd.setSqlQuery(
        DqQueries.getEc11CombinedQuery(
            hivMetadata.getNidServiceTarvIdentifierType().getPatientIdentifierTypeId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId()));
    dsd.addParameters(parameterList);
    return dsd;
  }
}
