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
import static org.openmrs.module.eptsreports.reporting.utils.EptsCommonUtils.getPatientProgramEnrollment;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.BreastfeedingCriteriaCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationDataDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.converter.CalculationResultDataConverter;
import org.openmrs.module.eptsreports.reporting.library.converter.PatientProgramDataConverter;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Display men who are breastfeeding based on patient states
 *
 * @return DataSet
 */
@Component
public class Ec2PatientListDataset extends BaseDataSet {

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  private HivMetadata hivMetadata;

  @Autowired
  public Ec2PatientListDataset(
      SummaryDataQualityCohorts summaryDataQualityCohorts, HivMetadata hivMetadata) {
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition ec2DataSetDefinition() {
    PatientDataSetDefinition dsd = new PatientDataSetDefinition();
    dsd.setName("EC2");
    dsd.addParameters(getDataQualityParameters());
    dsd.addRowFilter(
        summaryDataQualityCohorts.getBreastfeedingMalePatients(), "location=${location}");

    // add standard column
    addStandardColumns(dsd);
    dsd.addColumn(
        "Breastfeeding Criteria",
        getBreastfeedingCriteria(),
        "",
        new CalculationResultDataConverter("PC"));
    dsd.addColumn(
        "Encounter Date Breastfeeding Criteria",
        getBreastfeedingCriteria(),
        "",
        new CalculationResultDataConverter("PD"));
    dsd.addColumn(
        "PTV/ETC Enrollment Date",
        getBreastfeedingCriteria(),
        "",
        new CalculationResultDataConverter("PTVD"));
    dsd.addColumn(
        "PTV/ETC Enrollment Status",
        getPatientProgramEnrollment(hivMetadata.getPtvEtvProgram(), TimeQualifier.LAST),
        "enrolledOnOrBefore=${endDate}",
        new PatientProgramDataConverter("lastStatus"));

    return dsd;
  }

  private DataDefinition getBreastfeedingCriteria() {
    CalculationDataDefinition pCriteria =
        new CalculationDataDefinition(
            "Breastfeeding Criteria",
            Context.getRegisteredComponents(BreastfeedingCriteriaCalculation.class).get(0));
    pCriteria.addParameter(new Parameter("location", "Location", Location.class));
    pCriteria.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
    return pCriteria;
  }
}
