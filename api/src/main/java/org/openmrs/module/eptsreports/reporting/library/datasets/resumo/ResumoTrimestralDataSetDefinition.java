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
package org.openmrs.module.eptsreports.reporting.library.datasets.resumo;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange.Month;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralMonthPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralQuarterlyTotalCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralQuarterlyTotalCalculation.QUARTERLIES;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryA;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryB;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoTrimestralCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralDataSetDefinition extends BaseDataSet {

  private EptsGeneralIndicator eptsGeneralIndicator;

  private ResumoTrimestralCohortQueries resumoTrimestralCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public ResumoTrimestralDataSetDefinition(
      EptsGeneralIndicator eptsGeneralIndicator,
      ResumoTrimestralCohortQueries resumoTrimestralCohortQueries) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.resumoTrimestralCohortQueries = resumoTrimestralCohortQueries;
  }

  public DataSetDefinition constructResumoTrimestralDataset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("Resumo Trimestral Data Set");
    dsd.addParameters(getParameters());

    ResumoTrimestralIndicatorFactoryA resumoTrimestralFactory_A =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryA.class).get(0);
    ResumoTrimestralIndicatorFactoryB resumoTrimestralFactory_B =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryB.class).get(0);
    int iteration = 1;
    for (Month month : Month.values()) {
      ResumoTrimestralMonthPeriodCalculation calculatorA =
          resumoTrimestralFactory_A.getResumoTrimestralCalculator(month);
      ResumoTrimestralMonthPeriodCalculation calculatorB =
          resumoTrimestralFactory_B.getResumoTrimestralCalculator(month);

      if (calculatorA != null && calculatorB != null) {

        CohortDefinition definitionForSectionA =
            resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(month, calculatorA);
        this.addColumnForMonth(
            dsd,
            definitionForSectionA,
            mappings,
            month,
            iteration,
            "A-month-",
            "A: Iniciaram TARV Cohort Original - ");

        CohortDefinition definitionForSectionB =
            resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(month, calculatorB);

        this.addColumnForMonth(
            dsd,
            definitionForSectionB,
            mappings,
            month,
            iteration,
            "B-month-",
            "B: Transferidos de - ");
      }
      iteration++;
    }

    iteration = 1;
    for (QUARTERLIES quarter : QUARTERLIES.values()) {
      ResumoTrimestralQuarterlyTotalCalculation totalQuarterly_A =
          resumoTrimestralFactory_A.getResumoTrimestralQuartelyCalculation(quarter);
      ResumoTrimestralQuarterlyTotalCalculation totalQuarterly_B =
          resumoTrimestralFactory_B.getResumoTrimestralQuartelyCalculation(quarter);

      if (totalQuarterly_A != null && totalQuarterly_B != null) {

        CohortDefinition definitionForSectionA =
            resumoTrimestralCohortQueries.getTotalPatientsQuarterly(quarter, totalQuarterly_A);
        this.addColumnForMonth(
            dsd,
            definitionForSectionA,
            mappings,
            quarter,
            iteration,
            "A-quarter-",
            "A: Total Quarter - ");

        CohortDefinition definitionForSectionB =
            resumoTrimestralCohortQueries.getTotalPatientsQuarterly(quarter, totalQuarterly_B);
        this.addColumnForMonth(
            dsd,
            definitionForSectionB,
            mappings,
            quarter,
            iteration,
            "B-quarter-",
            "B: Total Quarter - ");
      }
      iteration++;
    }
    return dsd;
  }

  private void addColumnForMonth(
      CohortIndicatorDataSetDefinition dsd,
      CohortDefinition definitionForSection,
      String mappings,
      Object period,
      int iteration,
      String prefixColumnName,
      String label) {

    final CohortIndicator indicatorForSection =
        this.eptsGeneralIndicator.getIndicator(
            label + period, EptsReportUtils.map(definitionForSection, mappings));

    dsd.addColumn(
        prefixColumnName + StringUtils.leftPad(StringUtils.EMPTY + iteration, 2, "0"),
        label + period,
        EptsReportUtils.map(indicatorForSection, mappings),
        StringUtils.EMPTY);
  }
}
