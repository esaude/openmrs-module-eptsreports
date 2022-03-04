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
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXTBDenominatorForTBMontlyCascadeQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXTBMontlyCascadeReporCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.TXTBMontlyCascadeReportDimensions;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TXTBMontlyCascadeReportDataSet extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired private TXTBDenominatorForTBMontlyCascadeQueries txTBDenominatorForMontlyCascadeQuery;

  @Autowired private TXTBMontlyCascadeReporCohortQueries txtbMontlyCascadeReporCohortQueries;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private TXTBMontlyCascadeReportDimensions montlyCascadeReportDimensions;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructDatset(List<Parameter> parameters) {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    String mappings = "endDate=${endDate},location=${location}";
    dataSetDefinition.setName("TX TB Montly Cascade Data Set");
    dataSetDefinition.addParameters(parameters);

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension(
        "artStartState",
        EptsReportUtils.map(
            this.montlyCascadeReportDimensions.getArtStartRangeDimension(), mappings));

    dataSetDefinition.addDimension(
        "clinicalConsultation",
        EptsReportUtils.map(
            this.montlyCascadeReportDimensions.getClinicalConsultationDimension(), mappings));

    dataSetDefinition.addDimension(
        "diagnostictest",
        EptsReportUtils.map(this.montlyCascadeReportDimensions.DiagnosticTest(), mappings));

    dataSetDefinition.addDimension(
        "posetiveTestResult",
        EptsReportUtils.map(this.montlyCascadeReportDimensions.getPositiveTestResults(), mappings));

    dataSetDefinition.addDimension(
        "negativeTestResult",
        EptsReportUtils.map(this.montlyCascadeReportDimensions.getNegativeTestResults(), mappings));

    this.addSection1And2(dataSetDefinition, mappings);
    this.addSection3(dataSetDefinition, mappings);
    this.addSEction4(dataSetDefinition, mappings);
    this.addSection5(dataSetDefinition, mappings);
    this.addSection6(dataSetDefinition, mappings);
    this.addSection7(dataSetDefinition, mappings);
    this.addSection8(dataSetDefinition, mappings);
    return dataSetDefinition;
  }

  private void addSection1And2(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    final CohortIndicator txCurrIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "findPatientsWhoAreActiveOnART",
            EptsReportUtils.map(txCurrCohortQueries.findPatientsWhoAreActiveOnART(), mappings));

    dataSetDefinition.addColumn(
        "TC",
        "1. TX_CURR: Number of patients currently receiving ART - Total",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "");

    this.addRow(
        dataSetDefinition,
        "TCN",
        "1. TX_CURR: Number of patients currently receiving ART (New on ART)",
        EptsReportUtils.map(txCurrIndicator, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrNewlyOnArt");

    this.addRow(
        dataSetDefinition,
        "TCP",
        "1. TX_CURR: Number of patients currently receiving ART (Previously on ART)",
        EptsReportUtils.map(txCurrIndicator, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrPreviouslyOnArt");

    dataSetDefinition.addColumn(
        "TC-CC",
        "2. TX_CURR with clinical consultation in last 6 months - Total",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "clinicalConsultation=clinicalConsultationNewly");

    this.addRow(
        dataSetDefinition,
        "TC-CC-N",
        "2. TX_CURR with clinical consultation in last 6 months (New on ART) ",
        EptsReportUtils.map(txCurrIndicator, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrNewlyOnArt|clinicalConsultation=clinicalConsultationNewly");

    this.addRow(
        dataSetDefinition,
        "TC-CC-P",
        "2. TX_CURR with clinical consultation in last 6 months (Previously on ART) ",
        EptsReportUtils.map(txCurrIndicator, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrPreviouslyOnArt|clinicalConsultation=clinicalConsultationNewly");
  }

  private void addSection3(CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    CohortIndicator txTBDenominatorPreviousPeriod =
        this.getIndicator(this.txTBDenominatorForMontlyCascadeQuery.getTxTBDenominator());

    dataSetDefinition.addColumn(
        "TC-TB",
        "3.a TX_TB denominator - Total",
        EptsReportUtils.map(txTBDenominatorPreviousPeriod, mappings),
        "");

    this.addRow(
        dataSetDefinition,
        "TC-TB-N",
        "3.a TX_TB denominator (New On ART)",
        EptsReportUtils.map(txTBDenominatorPreviousPeriod, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrNewlyOnArt");

    this.addRow(
        dataSetDefinition,
        "TC-TB-P",
        "3.a TX_TB denominator (Previously on ART)",
        EptsReportUtils.map(txTBDenominatorPreviousPeriod, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrPreviouslyOnArt");

    CohortIndicator tbDenominatorAndTxCurr =
        this.getIndicator(this.txtbMontlyCascadeReporCohortQueries.getTxBTDenominatorAndTxCurr());
    dataSetDefinition.addColumn(
        "TBD-TC",
        "3.b.TX_TB denominator not died not transferred-out - Total",
        EptsReportUtils.map(tbDenominatorAndTxCurr, mappings),
        "");

    this.addRow(
        dataSetDefinition,
        "TBD-TC-N",
        "3.b.TX_TB denominator not died not transferred-out (New On ART)",
        EptsReportUtils.map(tbDenominatorAndTxCurr, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrNewlyOnArt");

    this.addRow(
        dataSetDefinition,
        "TBD-TC-P",
        "3.b.TX_TB denominator not died not transferred-out (Previously on ART)",
        EptsReportUtils.map(tbDenominatorAndTxCurr, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrPreviouslyOnArt");
  }

  private void addSEction4(CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    CohortIndicator denominatorAndPosetiveScreening =
        getIndicator(
            this.txTBDenominatorForMontlyCascadeQuery.getTxTBDenominatorAndPositiveScreening());

    dataSetDefinition.addColumn(
        "TBD-PS",
        "4a. Positive TB screening or specimen sent or notified with TB - Total",
        EptsReportUtils.map(denominatorAndPosetiveScreening, mappings),
        "");

    this.addRow(
        dataSetDefinition,
        "TBD-PS-N",
        "4a. Positive TB screening or specimen sent or notified with TB (New On ART)",
        EptsReportUtils.map(denominatorAndPosetiveScreening, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrNewlyOnArt");

    this.addRow(
        dataSetDefinition,
        "TBD-PS-P",
        "4a. Positive TB screening or specimen sent or notified with TB (Previously on ART)",
        EptsReportUtils.map(denominatorAndPosetiveScreening, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrPreviouslyOnArt");

    CohortIndicator denominatorAndNegativeScreening =
        this.getIndicator(
            this.txTBDenominatorForMontlyCascadeQuery.getTxTBDenominatorAndNegativeScreening());

    dataSetDefinition.addColumn(
        "TBD-NS",
        "4b. Negative TB screening or specimen sent or notified with TB - Total",
        EptsReportUtils.map(denominatorAndNegativeScreening, mappings),
        "");

    this.addRow(
        dataSetDefinition,
        "TBD-NS-N",
        "4b. Negative TB screening or specimen sent or notified with TB (New On ART)",
        EptsReportUtils.map(denominatorAndNegativeScreening, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrNewlyOnArt");

    this.addRow(
        dataSetDefinition,
        "TBD-NS-P",
        "4b. Negative TB screening or specimen sent or notified with TB (Previously on ART)",
        EptsReportUtils.map(denominatorAndNegativeScreening, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrPreviouslyOnArt");
  }

  private void addSection5(CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    CohortIndicator specimenSet =
        this.getIndicator(
            this.txTBDenominatorForMontlyCascadeQuery.getSpecimenSentCohortDefinition());

    dataSetDefinition.addColumn(
        "TBD-SP",
        "5. Screened patients with specimen sent -Total",
        EptsReportUtils.map(specimenSet, mappings),
        "");

    dataSetDefinition.addColumn(
        "TBD-SP-baciloscopia",
        "5. Screened patients with specimen sent -Total",
        EptsReportUtils.map(specimenSet, mappings),
        "diagnostictest=baciloscopia");

    dataSetDefinition.addColumn(
        "TBD-SP-genexpert",
        " 5. Screened patients with GeneXpert MTB/RIF",
        EptsReportUtils.map(specimenSet, mappings),
        "diagnostictest=genexpert");

    dataSetDefinition.addColumn(
        "TBD-SP-tblam",
        " 5. Screened patients with TB LAM",
        EptsReportUtils.map(specimenSet, mappings),
        "diagnostictest=tblam");

    dataSetDefinition.addColumn(
        "TBD-SP-other",
        " 5. Screened patients with Additional test other",
        EptsReportUtils.map(specimenSet, mappings),
        "diagnostictest=additonalDiagnostic");
  }

  private void addSection6(CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    CohortIndicator positiveResults =
        this.getIndicator(
            this.txTBDenominatorForMontlyCascadeQuery.getPositiveResultCohortDefinition());

    dataSetDefinition.addColumn(
        "TBD-PR",
        "6a.Screened patients with positive TB testing result -Total",
        EptsReportUtils.map(positiveResults, mappings),
        "");

    dataSetDefinition.addColumn(
        "TBD-PR-baciloscopia",
        "6a.Screened patients with positive TB testing result - Baciloscopia",
        EptsReportUtils.map(positiveResults, mappings),
        "diagnostictest=baciloscopia|posetiveTestResult=positiveBaciloscopia");

    dataSetDefinition.addColumn(
        "TBD-PR-genexpert",
        " 6a.Screened patients with positive TB testing result - GeneXpert MTB/RIF",
        EptsReportUtils.map(positiveResults, mappings),
        "diagnostictest=genexpert|posetiveTestResult=positiveGenexpert");

    dataSetDefinition.addColumn(
        "TBD-PR-tblam",
        " 6a.Screened patients with positive TB testing result - TB LAM",
        EptsReportUtils.map(positiveResults, mappings),
        "diagnostictest=tblam|posetiveTestResult=positiveTblam");

    dataSetDefinition.addColumn(
        "TBD-PR-other",
        "6a.Screened patients with positive TB testing result - Additional test other",
        EptsReportUtils.map(positiveResults, mappings),
        "diagnostictest=additonalDiagnostic|posetiveTestResult=positiveAdditonalDiagnostic");

    CohortIndicator negativeResults =
        this.getIndicator(
            this.txTBDenominatorForMontlyCascadeQuery.getNegativeResultCohortDefinition());

    dataSetDefinition.addColumn(
        "TBD-NR",
        "6b.Screened patients with negative TB testing result -Total",
        EptsReportUtils.map(negativeResults, mappings),
        "");

    dataSetDefinition.addColumn(
        "TBD-NR-baciloscopia",
        "6b.Screened patients with negative TB testing result - Baciloscopia",
        EptsReportUtils.map(negativeResults, mappings),
        "diagnostictest=baciloscopia|negativeTestResult=negativeBaciloscopia");

    dataSetDefinition.addColumn(
        "TBD-NR-genexpert",
        " 6b.Screened patients with negative TB testing result - GeneXpert MTB/RIF",
        EptsReportUtils.map(negativeResults, mappings),
        "diagnostictest=genexpert|negativeTestResult=negativeGenexpert");

    dataSetDefinition.addColumn(
        "TBD-NR-tblam",
        "6b.Screened patients with negative TB testing result - TB LAM",
        EptsReportUtils.map(negativeResults, mappings),
        "diagnostictest=tblam|negativeTestResult=negativeTblam");

    dataSetDefinition.addColumn(
        "TBD-NR-other",
        "6b.Screened patients with negative TB testing result - Additional test other",
        EptsReportUtils.map(negativeResults, mappings),
        "diagnostictest=additonalDiagnostic|negativeTestResult=negativeAdditonalDiagnostic");
  }

  private void addSection7(CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {
    CohortIndicator startedTBTreatment =
        this.getIndicator(
            this.txTBDenominatorForMontlyCascadeQuery.getPositiveResultAndTXTBNumerator());

    dataSetDefinition.addColumn(
        "TBD-STB",
        "7. Screened patients with pos TB testing result who initiated treatment -Total",
        EptsReportUtils.map(startedTBTreatment, mappings),
        "");
  }

  private void addSection8(CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    CohortIndicator screenedAndStartedTB =
        this.getIndicator(
            this.txTBDenominatorForMontlyCascadeQuery.getScreenedPatientsWhoStartedTBTreatment());

    dataSetDefinition.addColumn(
        "TBD-SSTB",
        "8a. TX_TB numerator (screened patients initiated TB treatment -Total",
        EptsReportUtils.map(screenedAndStartedTB, mappings),
        "");

    this.addRow(
        dataSetDefinition,
        "TBD-SSTB-N",
        "8a. TX_TB numerator (screened patients initiated TB treatment (New On ART)",
        EptsReportUtils.map(screenedAndStartedTB, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrNewlyOnArt");

    this.addRow(
        dataSetDefinition,
        "TBD-SSTB-P",
        "8a. TX_TB numerator (screened patients initiated TB treatment (Previously on ART)",
        EptsReportUtils.map(screenedAndStartedTB, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrPreviouslyOnArt");

    CohortIndicator txTBAndTXCurr =
        this.getIndicator(
            this.txTBDenominatorForMontlyCascadeQuery
                .getScreenedPatientsWhoStartedTBTreatmentAndTXCurr());

    dataSetDefinition.addColumn(
        "TBD-TBTC",
        "8b. TX_TB numerator not died not transferred-out (screened patients initiated TB treatment) -Total",
        EptsReportUtils.map(txTBAndTXCurr, mappings),
        "");

    this.addRow(
        dataSetDefinition,
        "TBD-TBTC-N",
        "8b. TX_TB numerator not died not transferred-out (screened patients initiated TB treatment) (New On ART)",
        EptsReportUtils.map(txTBAndTXCurr, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrNewlyOnArt");

    this.addRow(
        dataSetDefinition,
        "TBD-TBTC-P",
        "8b. TX_TB numerator not died not transferred-out (screened patients initiated TB treatment) (Previously on ART)",
        EptsReportUtils.map(txTBAndTXCurr, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings,
        "artStartState=txcurrPreviouslyOnArt");
  }

  private void addRow(
      CohortIndicatorDataSetDefinition dataSetDefinition,
      String indicatorPrefix,
      String baseLabel,
      Mapped<CohortIndicator> mappedIndicator,
      List<ColumnParameters> columns,
      String mappings,
      String dimensions) {

    dataSetDefinition.addColumn(
        indicatorPrefix + "-total",
        indicatorPrefix + ": " + baseLabel,
        mappedIndicator,
        dimensions);

    dataSetDefinition.addColumn(
        indicatorPrefix + "-TotalMale",
        indicatorPrefix + " - Age and Gender (Totals male) ",
        mappedIndicator,
        "gender=M|" + dimensions);
    dataSetDefinition.addColumn(
        indicatorPrefix + "-TotalFemale",
        indicatorPrefix + " - Age and Gender (Totals female) ",
        mappedIndicator,
        "gender=F|" + dimensions);

    for (ColumnParameters column : columns) {
      String name = indicatorPrefix + "-" + column.getName();
      String label = baseLabel + " (" + column.getLabel() + ")";
      dataSetDefinition.addColumn(
          name, label, mappedIndicator, column.getDimensions() + "|" + dimensions);
    }
  }

  private List<ColumnParameters> getColumnsForAgeDesaggregation() {

    ColumnParameters under15M =
        new ColumnParameters("under15M", "under 15 year male", "gender=M|age=<15", "01");
    ColumnParameters above15M =
        new ColumnParameters("above15M", "above 15 year male", "gender=M|age=15+", "02");
    ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "03");

    ColumnParameters under15F =
        new ColumnParameters("under15F", "under 15 year female", "gender=F|age=<15", "04");
    ColumnParameters above15F =
        new ColumnParameters("above15F", "above 15 year female", "gender=F|age=15+", "05");
    ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "06");

    return Arrays.asList(under15M, above15M, unknownM, under15F, above15F, unknownF);
  }

  private CohortIndicator getIndicator(CohortDefinition cohortDefinition) {
    String mappings = "endDate=${endDate},location=${location}";
    return this.eptsGeneralIndicator.getIndicator(
        "" + cohortDefinition, EptsReportUtils.map(cohortDefinition, mappings));
  }
}
