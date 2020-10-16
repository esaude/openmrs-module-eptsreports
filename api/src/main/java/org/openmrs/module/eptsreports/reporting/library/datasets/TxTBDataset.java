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

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXTBCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TxTBDataset extends BaseDataSet {
  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TXTBCohortQueries txTbCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructTxTBDataset() {
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TX_TB Data Set");
    dataSetDefinition.addParameters(getParameters());

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    addTXTBNumerator(mappings, dataSetDefinition);

    addTXTBDenominator(mappings, dataSetDefinition);

    return dataSetDefinition;
  }

  private void addTXTBNumerator(
      String mappings, CohortIndicatorDataSetDefinition dataSetDefinition) {
    CohortIndicator numerator =
        eptsGeneralIndicator.getIndicator(
            "NUMERATOR", EptsReportUtils.map(txTbCohortQueries.txTbNumerator(), mappings));
    CohortIndicator patientsPreviouslyOnARTNumerator =
        eptsGeneralIndicator.getIndicator(
            "patientsPreviouslyOnARTNumerator",
            EptsReportUtils.map(txTbCohortQueries.patientsPreviouslyOnARTNumerator(), mappings));
    CohortIndicator patientsNewOnARTNumerator =
        eptsGeneralIndicator.getIndicator(
            "patientsNewOnARTNumerator",
            EptsReportUtils.map(txTbCohortQueries.patientsNewOnARTNumerator(), mappings));

    dataSetDefinition.addColumn(
        "TXB_NUM", "TX_TB: Numerator total", EptsReportUtils.map(numerator, mappings), "");
    addRow(
        dataSetDefinition,
        "TXB_NUM_PREV",
        "Numerator (patientsPreviouslyOnARTNumerator)",
        EptsReportUtils.map(patientsPreviouslyOnARTNumerator, mappings),
        dissagregations());
    addRow(
        dataSetDefinition,
        "TXB_NUM_NEW",
        "Numerator (patientsNewOnARTNumerator)",
        EptsReportUtils.map(patientsNewOnARTNumerator, mappings),
        dissagregations());
  }

  private void addTXTBDenominator(
      String mappings, CohortIndicatorDataSetDefinition dataSetDefinition) {
    CohortIndicator previouslyOnARTPostiveScreening =
        eptsGeneralIndicator.getIndicator(
            "previouslyOnARTPositiveScreening",
            EptsReportUtils.map(txTbCohortQueries.previouslyOnARTPositiveScreening(), mappings));
    CohortIndicator previouslyOnARTNegativeScreening =
        eptsGeneralIndicator.getIndicator(
            "previouslyOnARTNegativeScreening",
            EptsReportUtils.map(txTbCohortQueries.previouslyOnARTNegativeScreening(), mappings));
    CohortIndicator newOnARTPositiveScreening =
        eptsGeneralIndicator.getIndicator(
            "newOnARTPositiveScreening",
            EptsReportUtils.map(txTbCohortQueries.newOnARTPositiveScreening(), mappings));
    CohortIndicator newOnARTNegativeScreening =
        eptsGeneralIndicator.getIndicator(
            "newOnARTNegativeScreening",
            EptsReportUtils.map(txTbCohortQueries.newOnARTNegativeScreening(), mappings));
    CohortIndicator specimenSent =
        eptsGeneralIndicator.getIndicator(
            "specimenSent", EptsReportUtils.map(txTbCohortQueries.specimenSent(), mappings));
    CohortIndicator smearMicroscopyOnly =
        eptsGeneralIndicator.getIndicator(
            "smearMicroscopyOnly",
            EptsReportUtils.map(txTbCohortQueries.smearMicroscopyOnly(), mappings));
    CohortIndicator genExpert =
        eptsGeneralIndicator.getIndicator(
            "genExpert", EptsReportUtils.map(txTbCohortQueries.genExpert(), mappings));
    CohortIndicator otherAdditionalTest =
        eptsGeneralIndicator.getIndicator(
            "otherAdditionalTest",
            EptsReportUtils.map(txTbCohortQueries.otherAdditionalTest(), mappings));
    CohortIndicator positiveResultsReturned =
        eptsGeneralIndicator.getIndicator(
            "positiveInvestigationResult",
            EptsReportUtils.map(txTbCohortQueries.positiveResultsReturned(), mappings));

    dataSetDefinition.addColumn(
        "TXB_DEN",
        "TX_TB: Denominator total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominator Total",
                EptsReportUtils.map(txTbCohortQueries.getDenominator(), mappings)),
            mappings),
        "");

    addRow(
        dataSetDefinition,
        "TXB_DEN_NEW_POS",
        "Denominator (newOnARTPositiveScreening)",
        EptsReportUtils.map(newOnARTPositiveScreening, mappings),
        dissagregations());
    addRow(
        dataSetDefinition,
        "TXB_DEN_NEW_NEG",
        "Denominator (newOnARTNegativeScreening)",
        EptsReportUtils.map(newOnARTNegativeScreening, mappings),
        dissagregations());
    addRow(
        dataSetDefinition,
        "TXB_DEN_PREV_POS",
        "Denominator (previouslyOnARTPositiveScreening)",
        EptsReportUtils.map(previouslyOnARTPostiveScreening, mappings),
        dissagregations());
    addRow(
        dataSetDefinition,
        "TXB_DEN_PREV_NEG",
        "Denominator (previouslyOnARTNegativeScreening)",
        EptsReportUtils.map(previouslyOnARTNegativeScreening, mappings),
        dissagregations());

    dataSetDefinition.addColumn(
        "TXB_DEN_SPEC_SENT",
        "TX_TB: Denominator (Specimen Sent)",
        EptsReportUtils.map(specimenSent, mappings),
        "");

    dataSetDefinition.addColumn(
        "TXB_DEN_SMEAR_ONLY",
        "TX_TB: Denominator (Diagnostic Test - Smear Only)",
        EptsReportUtils.map(smearMicroscopyOnly, mappings),
        "");

    dataSetDefinition.addColumn(
        "TXB_DEN_GENE_EXPERT",
        "TX_TB: Denominator (Diagnostic Test - Gene Xpert MTB/RIF Assay)",
        EptsReportUtils.map(genExpert, mappings),
        "");

    dataSetDefinition.addColumn(
        "TXB_DEN_OTHER",
        "TX_TB: Denominator (Diagnostic Test - Other No Xpert)",
        EptsReportUtils.map(otherAdditionalTest, mappings),
        "");

    dataSetDefinition.addColumn(
        "TXB_DEN_POS_RES_RET",
        "TX_TB: Denominator (Positive Result Returned)",
        EptsReportUtils.map(positiveResultsReturned, mappings),
        "");
  }

  private List<ColumnParameters> dissagregations() {
    return Arrays.asList(
        new ColumnParameters("<15Females", "<15 anos - Feminino", "gender=F|age=<15", "F1"),
        new ColumnParameters(">=15Females", "15+ anos Feminino", "gender=F|age=15+", "F2"),
        new ColumnParameters("UnknownFemales", "Unknown anos Feminino", "gender=F|age=UK", "F3"),
        new ColumnParameters("<15Males", "<15 anos - Masculino", "gender=M|age=<15", "M1"),
        new ColumnParameters(">=15Males", "15+ anos Masculino", "gender=M|age=15+", "M2"),
        new ColumnParameters("UnknownMales", "Unknown anos Masculino", "gender=M|age=UK", "M3"));
  }
}
