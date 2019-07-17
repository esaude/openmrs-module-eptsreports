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

import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.PregnantCriteriaCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationDataDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.converter.CalculationResultDataConverter;
import org.openmrs.module.eptsreports.reporting.library.converter.PatientProgramDataConverter;
import org.openmrs.module.eptsreports.reporting.library.data.definition.DataDefinitions;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsCommonUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** A dataset to display list of patients who are pregnant and male */
@Component
public class Ec1PatientListDataset extends BaseDataSet {

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  private HivMetadata hivMetadata;

  private EptsCommonUtils eptsCommonUtils;

  private DataDefinitions definitions;

  @Autowired
  public Ec1PatientListDataset(
      SummaryDataQualityCohorts summaryDataQualityCohorts,
      HivMetadata hivMetadata,
      EptsCommonUtils eptsCommonUtils,
      DataDefinitions definitions) {
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.hivMetadata = hivMetadata;
    this.eptsCommonUtils = eptsCommonUtils;
    this.definitions = definitions;
  }

  public DataSetDefinition ec1DataSetDefinition(List<Parameter> parameterList) {
    PatientDataSetDefinition dsd = new PatientDataSetDefinition();
    dsd.setName("EC1");
    dsd.addParameters(parameterList);
    dsd.addRowFilter(summaryDataQualityCohorts.getPregnantMalePatients(), "location=${location}");

    eptsCommonUtils.addStandardColumns(dsd);
    dsd.addColumn(
        "Pregnant Criteria", getPregnantCriteria(), "", new CalculationResultDataConverter("PC"));
    dsd.addColumn(
        "Encounter Date Pregnant Criteria",
        getPregnantCriteria(),
        "location=${location}",
        new CalculationResultDataConverter("PD"));
    dsd.addColumn(
        "PTV/ETC Enrollment Date",
        getPregnantCriteria(),
        "",
        new CalculationResultDataConverter("PTVD"));
    dsd.addColumn(
        "PTV/ETC Enrollment Status",
        definitions.getPatientProgramEnrollment(hivMetadata.getPtvEtvProgram(), TimeQualifier.LAST),
        "enrolledOnOrBefore=${endDate}",
        new PatientProgramDataConverter("lastStatus"));

    return dsd;
  }

  private DataDefinition getPregnantCriteria() {
    CalculationDataDefinition pCriteria =
        new CalculationDataDefinition(
            "Pregnant Criteria",
            Context.getRegisteredComponents(PregnantCriteriaCalculation.class).get(0));
    pCriteria.addParameter(new Parameter("location", "Location", Location.class));
    pCriteria.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
    return pCriteria;
  }
}
