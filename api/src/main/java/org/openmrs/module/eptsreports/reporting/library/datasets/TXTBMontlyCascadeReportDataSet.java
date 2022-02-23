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
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXTBMontlyCascadeReporCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TXTBMontlyCascadeReportDataSet extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired private TXTBMontlyCascadeReporCohortQueries txtbMontlyCascadeReporCohortQueries;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructDatset() {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    String mappings = "endDate=${endDate},location=${location}";
    dataSetDefinition.setName("TX TB Montly Cascade Data Set");
    dataSetDefinition.addParameters(getParameters());

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    // Indicators
    final CohortIndicator txCurrIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "findPatientsWhoAreActiveOnART",
            EptsReportUtils.map(txCurrCohortQueries.findPatientsWhoAreActiveOnART(), mappings));

    final CohortIndicator txCurrLastSixMonthsIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "findTxCurrForTheLastSixMonths",
            EptsReportUtils.map(
                this.txtbMontlyCascadeReporCohortQueries.getTxCurrForTheLastSixMonths(), mappings));

    final CohortIndicator txCurrMoreThanSixMonthsIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "findTxCurrForMoreThanSixMonths",
            EptsReportUtils.map(
                this.txtbMontlyCascadeReporCohortQueries.getTxCurrForMoreThanSixMonths(),
                mappings));

    // Adding Columns
    dataSetDefinition.addColumn(
        "TC",
        "TX_CURR: Currently on ART - Total",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "");

    this.addRow(
        dataSetDefinition,
        "TCN",
        "Number of patients currently receiving ART In the Last 6 Months",
        EptsReportUtils.map(txCurrLastSixMonthsIndicator, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings);

    this.addRow(
        dataSetDefinition,
        "TCP",
        "Number of patients currently receiving ART For More than 6 Months",
        EptsReportUtils.map(txCurrMoreThanSixMonthsIndicator, mappings),
        this.getColumnsForAgeDesaggregation(),
        mappings);

    return dataSetDefinition;
  }

  private void addRow(
      CohortIndicatorDataSetDefinition dataSetDefinition,
      String indicatorPrefix,
      String baseLabel,
      Mapped<CohortIndicator> mappedIndicator,
      List<ColumnParameters> columns,
      String mappings) {

    dataSetDefinition.addColumn(
        indicatorPrefix + "-total", indicatorPrefix + ": " + baseLabel, mappedIndicator, "");

    dataSetDefinition.addColumn(
        indicatorPrefix + "-TotalMale",
        indicatorPrefix + " - Age and Gender (Totals male) ",
        mappedIndicator,
        "gender=M");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-TotalFemale",
        indicatorPrefix + " - Age and Gender (Totals female) ",
        mappedIndicator,
        "gender=F");

    for (ColumnParameters column : columns) {
      String name = indicatorPrefix + "-" + column.getName();
      String label = baseLabel + " (" + column.getLabel() + ")";
      dataSetDefinition.addColumn(name, label, mappedIndicator, column.getDimensions());
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
}
