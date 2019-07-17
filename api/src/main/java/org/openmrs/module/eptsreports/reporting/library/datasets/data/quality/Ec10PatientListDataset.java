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

import static org.openmrs.module.eptsreports.reporting.utils.EptsCommonUtils.addStandardColumns;
import static org.openmrs.module.eptsreports.reporting.utils.EptsCommonUtils.getEncounterForPatient;
import static org.openmrs.module.eptsreports.reporting.utils.EptsCommonUtils.getPatientProgramEnrollment;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.converter.EncounterDataConverter;
import org.openmrs.module.eptsreports.reporting.library.converter.PatientProgramDataConverter;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Ec10PatientListDataset extends BaseDataSet {

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  private HivMetadata hivMetadata;

  @Autowired
  public Ec10PatientListDataset(
      SummaryDataQualityCohorts summaryDataQualityCohorts, HivMetadata hivMetadata) {
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition ec10PatientListDataset(List<Parameter> parameterList) {
    PatientDataSetDefinition dsd = new PatientDataSetDefinition();
    dsd.setName("EC10");
    dsd.addParameters(parameterList);
    dsd.addRowFilter(
        summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId())),
        "location=${location}");

    // add standard column
    addStandardColumns(dsd);
    dsd.addColumn(
        "Patient Enrollment Date in TARV",
        getPatientProgramEnrollment(hivMetadata.getARTProgram(), TimeQualifier.FIRST),
        "enrolledOnOrBefore=${endDate}",
        new PatientProgramDataConverter("date"));
    dsd.addColumn(
        "Last Patient Status in Prog Enrollment",
        getPatientProgramEnrollment(hivMetadata.getARTProgram(), TimeQualifier.FIRST),
        "enrolledOnOrBefore=${endDate}",
        new PatientProgramDataConverter("lastStatus"));
    dsd.addColumn(
        "Date of Last Patient Status in Prog Enrollment",
        getPatientProgramEnrollment(hivMetadata.getARTProgram(), TimeQualifier.FIRST),
        "enrolledOnOrBefore=${endDate}",
        new PatientProgramDataConverter("lastStatusDate"));
    dsd.addColumn(
        "Clinical Consultation Date",
        getEncounterForPatient(
            Arrays.asList(
                hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                hivMetadata.getAdultoSeguimentoEncounterType())),
        "",
        new EncounterDataConverter("encounterDate"));

    dsd.addColumn(
        "Clinical Consultation Registration Date",
        getEncounterForPatient(
            Arrays.asList(
                hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                hivMetadata.getAdultoSeguimentoEncounterType())),
        "",
        new EncounterDataConverter("encounterCreatedDate"));

    return dsd;
  }
}
