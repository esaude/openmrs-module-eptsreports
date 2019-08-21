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

import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.disaggregations.ResumoMensalAandBdisaggregations;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ResumoMensalAdataSetDefinition extends BaseDataSet {

  private EptsCommonDimension eptsCommonDimension;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  private ResumoMensalAandBdisaggregations resumoMensalAandBdisaggregations;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public ResumoMensalAdataSetDefinition(
      EptsCommonDimension eptsCommonDimension,
      EptsGeneralIndicator eptsGeneralIndicator,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      ResumoMensalAandBdisaggregations resumoMensalAandBdisaggregations) {
    this.eptsCommonDimension = eptsCommonDimension;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.resumoMensalAandBdisaggregations = resumoMensalAandBdisaggregations;
  }

  public DataSetDefinition constructResumoMensalDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("Resumo Mensal Data set");
    dsd.addParameters(getParameters());

    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    // start of the indicator definition for the main dataset A
    // indicators for section A1
    addRow(
        dsd,
        "A1TC",
        "Patients under 15 years",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients under 15 years",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "A1TA",
        "Patients over 15 years - adults",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients over 15 years - adults",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "A1TP",
        "Total patients - Total Geral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total patients - Total Geral",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(),
                    mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "A1TAD",
        "Adolescentes patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Adolescentes patients",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // Indicators for A2

    addRow(
        dsd,
        "A2TC",
        "Patients under 15 years",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients under 15 years",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "A2TA",
        "Patients over 15 years - adults",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients over 15 years - adults",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "A2TP",
        "Total patients - Total Geral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total patients - Total Geral",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
                    mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "A2TAD",
        "Adolescentes patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Adolescentes patients",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    return dsd;
  }
}
