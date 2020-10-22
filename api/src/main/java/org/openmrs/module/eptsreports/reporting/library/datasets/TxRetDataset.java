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
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXRetCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.TxRetDimensionCohort;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxRetDataset extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TXRetCohortQueries txRetCohortQueries;

  @Autowired private TxRetDimensionCohort txRetDimensionCohort;

  private CohortIndicator cohortIndicator(CohortDefinition cd, String mapping) {
    CohortIndicator cohortIndicator =
        eptsGeneralIndicator.getIndicator(cd.getName(), EptsReportUtils.map(cd, mapping));
    cohortIndicator.addParameter(new Parameter("months", "Months", Integer.class));
    return cohortIndicator;
  }

  /**
   * Construction of dataset definition to handle TX ret
   *
   * @return @{@link DataSetDefinition}
   */
  public DataSetDefinition constructTxRetDataset() {
    String mappings =
        "startDate=${startDate},endDate=${endDate},location=${location},months=${months}";
    String mappings12MonthsBefore =
        "startDate=${startDate-12m},endDate=${endDate-12m},location=${location},months=${months}";
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TX_Ret Data Set");
    dataSetDefinition.addParameters(getParameters());
    dataSetDefinition.addDimension(
        "onArtByGenderAndAge",
        EptsReportUtils.map(
            txRetDimensionCohort.genderOnArtByAge(), "endDate=${endDate},location=${location}"));

    dataSetDefinition.addDimension(
        "0009",
        EptsReportUtils.map(
            txRetDimensionCohort.startedTargetAtARTInitiation(),
            "endDate=${endDate},location=${location}"));
    dataSetDefinition.addDimension(
        "pregnantOrBreastFeeding",
        EptsReportUtils.map(
            txRetDimensionCohort.pregnantOrBreastFeeding(),
            "startDate=${endDate-24m+1d},endDate=${endDate-12m},location=${location}"));
    dataSetDefinition.addDimension(
        "pregnantOrBreastFeeding24",
        EptsReportUtils.map(
            txRetDimensionCohort.pregnantOrBreastFeeding(),
            "startDate=${endDate-27m+1d},endDate=${endDate-24m},location=${location}"));
    dataSetDefinition.addDimension(
        "pregnantOrBreastFeeding36",
        EptsReportUtils.map(
            txRetDimensionCohort.pregnantOrBreastFeeding(),
            "startDate=${endDate-39m+1d},endDate=${endDate-36m},location=${location}"));

    Mapped<CohortIndicator> numerator =
        EptsReportUtils.map(
            cohortIndicator(txRetCohortQueries.inCourtForTwelveMonths(), mappings),
            mappings12MonthsBefore);
    dataSetDefinition.addColumn("T04SA-ALL", "TX_RET: Numerator total", numerator, "");
    addRow(
        dataSetDefinition,
        "T04SA",
        "TX_RET Numerator (inCourtForTwelveMonths)",
        numerator,
        dissagregations());

    Mapped<CohortIndicator> denominator =
        EptsReportUtils.map(
            cohortIndicator(txRetCohortQueries.courtNotTransferredTwelveMonths(), mappings),
            mappings12MonthsBefore);
    dataSetDefinition.addColumn("T04SI-ALL", "TX_RET: Denominator total", denominator, "");
    addRow(
        dataSetDefinition,
        "T04SI",
        "TX_RET Denominator (courtNotTransferredTwelveMonths)",
        denominator,
        dissagregations());
    dataSetDefinition.addParameter(
        new Parameter("months", "Número de Meses (12, 24, 36)", Integer.class));
    return dataSetDefinition;
  }

  private List<ColumnParameters> dissagregations() {
    return Arrays.asList(
        new ColumnParameters("<1", "Children <1 anos", "0009=0001", "0001"),
        new ColumnParameters("1–9", "Children 1-9 anos", "0009=0109", "0109"),
        new ColumnParameters("pregnant", "Pregnant", "pregnantOrBreastFeeding=GRAVIDAS", "PREG"),
        new ColumnParameters(
            "pregnant24", "Pregnant", "pregnantOrBreastFeeding24=GRAVIDAS", "PREG24"),
        new ColumnParameters(
            "pregnant36", "Pregnant", "pregnantOrBreastFeeding36=GRAVIDAS", "PREG36"),
        new ColumnParameters(
            "breastFeeding", "Breast feeding", "pregnantOrBreastFeeding=LACTANTE", "BRSTFDG"),
        new ColumnParameters(
            "breastFeeding24", "Breast feeding", "pregnantOrBreastFeeding24=LACTANTE", "BRSTFDG24"),
        new ColumnParameters(
            "breastFeeding36", "Breast feeding", "pregnantOrBreastFeeding36=LACTANTE", "BRSTFDG36"),
        new ColumnParameters(
            "10-14Males", "10-14 anos - Masculino", "onArtByGenderAndAge=10-14M", "1014M"),
        new ColumnParameters(
            "10-14Females", "10-14 anos - Feminino", "onArtByGenderAndAge=10-14F", "1014F"),
        new ColumnParameters(
            "15-19Males", "15-19 anos - Masculino", "onArtByGenderAndAge=15-19M", "1519M"),
        new ColumnParameters(
            "15-19Females", "15-19 anos - Feminino", "onArtByGenderAndAge=15-19F", "1519F"),
        new ColumnParameters(
            "20-24Males", "20-24 anos - Masculino", "onArtByGenderAndAge=20-24M", "2024M"),
        new ColumnParameters(
            "20-24Females", "20-24 anos - Feminino", "onArtByGenderAndAge=20-24F", "2024F"),
        new ColumnParameters(
            "25-29Males", "25-29 anos - Masculino", "onArtByGenderAndAge=25-29M", "2529M"),
        new ColumnParameters(
            "25-29Females", "25-29 anos - Feminino", "onArtByGenderAndAge=25-29F", "2529F"),
        new ColumnParameters(
            "30-34Males", "30-34 anos - Masculino", "onArtByGenderAndAge=30-34M", "3034M"),
        new ColumnParameters(
            "30-34Females", "30-34 anos - Feminino", "onArtByGenderAndAge=30-34F", "3034F"),
        new ColumnParameters(
            "35-39Males", "35-39 anos - Masculino", "onArtByGenderAndAge=35-39M", "3539M"),
        new ColumnParameters(
            "35-39Females", "35-39 anos - Feminino", "onArtByGenderAndAge=35-39F", "3539F"),
        new ColumnParameters(
            "40-49Males", "40-49 anos - Masculino", "onArtByGenderAndAge=40-49M", "4049M"),
        new ColumnParameters(
            "40-49Females", "40-49 anos - Feminino", "onArtByGenderAndAge=40-49F", "4049F"),
        new ColumnParameters("50+Males", "50+ anos - Masculino", "onArtByGenderAndAge=50+M", "50M"),
        new ColumnParameters(
            "50+Females", "50+ anos - Feminino", "onArtByGenderAndAge=50+F", "50F"));
  }
}
