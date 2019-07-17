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

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.converter.EncounterDataConverter;
import org.openmrs.module.eptsreports.reporting.library.converter.PatientProgramDataConverter;
import org.openmrs.module.eptsreports.reporting.library.data.definition.DataDefinitions;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.DqQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsCommonUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Ec8PatientListDataset extends BaseDataSet {

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  private HivMetadata hivMetadata;

  private EptsCommonUtils eptsCommonUtils;

  private DataDefinitions definitions;

  @Autowired
  public Ec8PatientListDataset(
      SummaryDataQualityCohorts summaryDataQualityCohorts,
      HivMetadata hivMetadata,
      EptsCommonUtils eptsCommonUtils,
      DataDefinitions definitions) {
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.hivMetadata = hivMetadata;
    this.eptsCommonUtils = eptsCommonUtils;
    this.definitions = definitions;
  }

  public DataSetDefinition ec8PatientListDataset(List<Parameter> parameterList) {
    PatientDataSetDefinition dsd = new PatientDataSetDefinition();
    dsd.setName("EC8");
    dsd.addParameters(parameterList);
    dsd.addRowFilter(
        summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            Arrays.asList(hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId())),
        "location=${location}");

    // add standard column
    eptsCommonUtils.addStandardColumns(dsd);
    dsd.addColumn(
        "Patient Enrollment Date in TARV",
        definitions.getPatientProgramEnrollment(hivMetadata.getARTProgram(), TimeQualifier.FIRST),
        "enrolledOnOrBefore=${endDate}",
        new PatientProgramDataConverter("date"));
    dsd.addColumn(
        "Last Patient Status in Prog Enrollment",
        definitions.getPatientProgramEnrollment(hivMetadata.getARTProgram(), TimeQualifier.FIRST),
        "enrolledOnOrBefore=${endDate}",
        new PatientProgramDataConverter("lastStatus"));
    dsd.addColumn(
        "Date of Last Patient Status in Prog Enrollment",
        definitions.getPatientProgramEnrollment(hivMetadata.getARTProgram(), TimeQualifier.FIRST),
        "enrolledOnOrBefore=${endDate}",
        new PatientProgramDataConverter("lastStatusDate"));
    dsd.addColumn(
        "Date of Death in Demographics",
        definitions.getPatientDetails(
            "Date of Death in Demographics", DqQueries.getPatientDeathDate()),
        "");

    dsd.addColumn(
        "Laboratory Form Date",
        definitions.getEncounterForPatient(
            Arrays.asList(hivMetadata.getMisauLaboratorioEncounterType())),
        "",
        new EncounterDataConverter("encounterDate"));
    dsd.addColumn(
        "Lab Form Registration Date",
        definitions.getEncounterForPatient(
            Arrays.asList(hivMetadata.getMisauLaboratorioEncounterType())),
        "",
        new EncounterDataConverter("encounterCreatedDate"));
    dsd.addColumn(
        "Clinical Consultation Date",
        definitions.getEncounterForPatient(
            Arrays.asList(
                hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                hivMetadata.getAdultoSeguimentoEncounterType())),
        "",
        new EncounterDataConverter("encounterDate"));

    dsd.addColumn(
        "Clinical Consultation Registration Date",
        definitions.getEncounterForPatient(
            Arrays.asList(
                hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                hivMetadata.getAdultoSeguimentoEncounterType())),
        "",
        new EncounterDataConverter("encounterCreatedDate"));
    return dsd;
  }
}
