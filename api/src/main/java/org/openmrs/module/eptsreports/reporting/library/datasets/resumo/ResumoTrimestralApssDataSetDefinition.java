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

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalAPSSCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.disaggregations.ResumoTrimestralAgeAndGenderDisaggregations;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralApssDataSetDefinition extends BaseDataSet {

  private EptsCommonDimension eptsCommonDimension;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private ResumoTrimestralAgeAndGenderDisaggregations resumoTrimestralAgeAndGenderDisaggregations;

  private ResumoMensalAPSSCohortQueries resumoMensalAPSSCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public ResumoTrimestralApssDataSetDefinition(
      EptsCommonDimension eptsCommonDimension,
      EptsGeneralIndicator eptsGeneralIndicator,
      ResumoTrimestralAgeAndGenderDisaggregations resumoMensalAandBdisaggregations,
      ResumoMensalAPSSCohortQueries resumoMensalAPSSCohortQueries) {
    this.eptsCommonDimension = eptsCommonDimension;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.resumoTrimestralAgeAndGenderDisaggregations = resumoMensalAandBdisaggregations;
    this.resumoMensalAPSSCohortQueries = resumoMensalAPSSCohortQueries;
  }

  public DataSetDefinition constructResumoTrimestralApssDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();

    dsd.setName("Resumo Trimestral APSS");
    dsd.addParameters(getParameters());

    dsd.addDimension("gender", map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age", map(eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    addRow(
        dsd,
        "A1Under15",
        "A1 - Patients Between 8  and 14 years",
        getIndicatorA1(),
        resumoTrimestralAgeAndGenderDisaggregations.get8To14YearsColumns());

    addRow(
        dsd,
        "B1Under15",
        "B1 - Patients under 15 years",
        getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
        resumoTrimestralAgeAndGenderDisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B1Over15",
        "B1 - Patients over 15 years",
        getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
        resumoTrimestralAgeAndGenderDisaggregations.getAdultPatients());

    dsd.addColumn(
        "B1Total",
        "B1 - Total patients - Total Geral",
        getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
        "");

    addRow(
        dsd,
        "C1Under15",
        "C1 - Patients under 15 years",
        getPatientsWhoAreCurrentlyEnrolledOnARTC1(),
        resumoTrimestralAgeAndGenderDisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "C1Over15",
        "C1 - Patients over 15 years - adults",
        getPatientsWhoAreCurrentlyEnrolledOnARTC1(),
        resumoTrimestralAgeAndGenderDisaggregations.getAdultPatients());

    dsd.addColumn(
        "C1Total",
        "C1 - Total patients - Total Geral",
        getPatientsWhoAreCurrentlyEnrolledOnARTC1(),
        "");

    addRow(
        dsd,
        "D1Over15",
        " D1 - Patients over 15 years - adults",
        getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthD1(),
        resumoTrimestralAgeAndGenderDisaggregations.getAdultPatients());

    dsd.addColumn(
        "D1Total",
        "D1 - Total patients - Total Geral",
        getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthD1(),
        "");

    return dsd;
  }

  private Mapped<CohortIndicator> getIndicatorA1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "A1",
            mapStraightThrough(
                this.resumoMensalAPSSCohortQueries
                    .getNumberOfPatientsWhoReceivedTotalDiagnosticRevelationInReportingPeriodA1())));
  }

  private Mapped<CohortIndicator> getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthD1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "D1",
            mapStraightThrough(
                resumoMensalAPSSCohortQueries
                    .findPatientsWhoAreCurrentlyEnrolledOnArtWithPrevencaoPosetivaD1())));
  }

  private Mapped<CohortIndicator> getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "B1",
            mapStraightThrough(
                resumoMensalAPSSCohortQueries
                    .getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthB1())));
  }

  private Mapped<CohortIndicator> getPatientsWhoAreCurrentlyEnrolledOnARTC1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "C1",
            mapStraightThrough(
                resumoMensalAPSSCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHC1())));
  }
}
