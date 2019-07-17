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
import org.openmrs.module.eptsreports.reporting.library.converter.PatientProgramDataConverter;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsCommonUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Ec14PatientListDataset extends BaseDataSet {

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  private HivMetadata hivMetadata;

  private EptsCommonUtils eptsCommonUtils;

  @Autowired
  public Ec14PatientListDataset(
      SummaryDataQualityCohorts summaryDataQualityCohorts,
      HivMetadata hivMetadata,
      EptsCommonUtils eptsCommonUtils) {
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.hivMetadata = hivMetadata;
    this.eptsCommonUtils = eptsCommonUtils;
  }

  public DataSetDefinition ec14PatientListDataset(List<Parameter> parameterList) {
    PatientDataSetDefinition dsd = new PatientDataSetDefinition();
    dsd.setName("EC14");
    dsd.addParameters(parameterList);
    dsd.addRowFilter(
        summaryDataQualityCohorts.getPatientsWithAgeHigherThanXyears(100), "endDate=${endDate}");

    // add standard column
    eptsCommonUtils.addStandardColumns(dsd);
    dsd.addColumn(
        "ART Program Enrollment Date",
        eptsCommonUtils.getPatientProgramEnrollment(
            hivMetadata.getARTProgram(), TimeQualifier.FIRST),
        "enrolledOnOrBefore=${endDate}",
        new PatientProgramDataConverter("date"));
    dsd.addColumn(
        "ART Program Enrollment Status",
        eptsCommonUtils.getPatientProgramEnrollment(
            hivMetadata.getARTProgram(), TimeQualifier.FIRST),
        "enrolledOnOrBefore=${endDate}",
        new PatientProgramDataConverter("lastStatus"));

    return dsd;
  }
}
