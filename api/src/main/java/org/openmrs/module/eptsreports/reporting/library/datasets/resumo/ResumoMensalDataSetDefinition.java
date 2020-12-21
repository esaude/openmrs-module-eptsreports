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

import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.disaggregations.ResumoMensalAandBdisaggregations;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ResumoMensalDataSetDefinition extends BaseDataSet {

  private EptsCommonDimension eptsCommonDimension;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  private ResumoMensalAandBdisaggregations resumoMensalAandBdisaggregations;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public ResumoMensalDataSetDefinition(
      EptsCommonDimension eptsCommonDimension,
      EptsGeneralIndicator eptsGeneralIndicator,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      ResumoMensalAandBdisaggregations resumoMensalAandBdisaggregations) {
    this.eptsCommonDimension = eptsCommonDimension;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.resumoMensalAandBdisaggregations = resumoMensalAandBdisaggregations;
  }

  public DataSetDefinition constructResumoMensalDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("Resumo Mensal Data set B");
    dsd.addParameters(getParameters());

    dsd.addDimension("gender", map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age", map(eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    // indicators for section A1
    addRow(
        dsd,
        "A1TC",
        "Patients under 15 years",
        getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "A1TA",
        "Patients over 15 years - adults",
        getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "A1TP",
        "Total patients - Total Geral",
        getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(),
        "");

    addRow(
        dsd,
        "A1TAD",
        "Adolescentes patients",
        getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // Indicators for A2

    addRow(
        dsd,
        "A2TC",
        "Patients under 15 years",
        getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "A2TA",
        "Patients over 15 years - adults",
        getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "A2TP",
        "Total patients - Total Geral",
        getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
        "");

    addRow(
        dsd,
        "A2TAD",
        "Adolescentes patients",
        getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // Indicators for A3
    addRow(
        dsd,
        "A3TC",
        "Patients under 15 years",
        getSumOfA1AndA2(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "A3TA",
        "Patients over 15 years - adults",
        getSumOfA1AndA2(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn("A3TP", "Total patients - Total Geral", getSumOfA1AndA2(), "");

    addRow(
        dsd,
        "A3TAD",
        "Adolescentes patients",
        getSumOfA1AndA2(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // indicators for section B1
    addRow(
        dsd,
        "B1TC",
        "Patients under 15 years",
        getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B1TA",
        "Patients over 15 years - adults",
        getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B1TP",
        "Total patients - Total Geral",
        getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
        "");

    addRow(
        dsd,
        "B1TAD",
        "Adolescentes patients",
        getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());
    // B2 indicators start here
    addRow(
        dsd,
        "B2TC",
        "Patients under 15 years",
        getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B2TA",
        "Patients over 15 years - adults",
        getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B2TP",
        "Total patients - Total Geral",
        getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2(),
        "");

    addRow(
        dsd,
        "B2TAD",
        "Adolescentes patients",
        getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B5 indicators
    addRow(
        dsd,
        "B5TC",
        "Patients under 15 years",
        getPatientsTransferredOutDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B5TA",
        "Patients over 15 years - adults",
        getPatientsTransferredOutDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B5TP", "Total patients - Total Geral", getPatientsTransferredOutDuringCurrentMonth(), "");

    addRow(
        dsd,
        "B5TAD",
        "Adolescentes patients",
        getPatientsTransferredOutDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B6 indicators
    addRow(
        dsd,
        "B6TC",
        "Patients under 15 years",
        getPatientsWithArtSuspensionDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B6TA",
        "Patients over 15 years - adults",
        getPatientsWithArtSuspensionDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B6TP",
        "Total patients - Total Geral",
        getPatientsWithArtSuspensionDuringCurrentMonth(),
        "");

    addRow(
        dsd,
        "B6TAD",
        "Adolescentes patients",
        getPatientsWithArtSuspensionDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B7 indicators
    addRow(
        dsd,
        "B7TC",
        "Patients under 15 years",
        getPatientsWhoAbandonedArtDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B7TA",
        "Patients over 15 years - adults",
        getPatientsWhoAbandonedArtDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B7TP", "Total patients - Total Geral", getPatientsWhoAbandonedArtDuringCurrentMonth(), "");

    addRow(
        dsd,
        "B7TAD",
        "Adolescentes patients",
        getPatientsWhoAbandonedArtDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B8 indicators
    addRow(
        dsd,
        "B8TC",
        "Patients under 15 years",
        getPatientsWhoDiedDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B8TA",
        "Patients over 15 years - adults",
        getPatientsWhoDiedDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B8TP", "Total patients - Total Geral", getPatientsWhoDiedDuringCurrentMonth(), "");

    addRow(
        dsd,
        "B8TAD",
        "Adolescentes patients",
        getPatientsWhoDiedDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B10 indicators
    addRow(
        dsd,
        "B10TC",
        "Patients under 15 years",
        getPatientsWhoStartedArtByEndOfPreviousMonth(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B10TA",
        "Patients over 15 years - adults",
        getPatientsWhoStartedArtByEndOfPreviousMonth(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B10TP",
        "Total patients - Total Geral",
        getPatientsWhoStartedArtByEndOfPreviousMonth(),
        "");

    addRow(
        dsd,
        "B10TAD",
        "Adolescentes patients",
        getPatientsWhoStartedArtByEndOfPreviousMonth(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B12 indicators
    addRow(
        dsd,
        "B12TC",
        "Patients under 15 years",
        getPatientsWhoWereActiveByEndOfPreviousMonth(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B12TA",
        "Patients over 15 years - adults",
        getPatientsWhoWereActiveByEndOfPreviousMonth(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B12TP",
        "Total patients - Total Geral",
        getPatientsWhoWereActiveByEndOfPreviousMonth(),
        "");

    addRow(
        dsd,
        "B12TAD",
        "Adolescentes patients",
        getPatientsWhoWereActiveByEndOfPreviousMonth(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B13 Indicators
    dsd.addColumn(
        "B13TP", "Total patients - Total Geral", getActivePatientsInARTByEndOfCurrentMonth(), "");
    addRow(
        dsd,
        "B13TA",
        "Patients over 15 years - adults",
        getActivePatientsInARTByEndOfCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdultPatients());
    addRow(
        dsd,
        "B13TAD",
        "Adolescentes patients",
        getActivePatientsInARTByEndOfCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());
    addRow(
        dsd,
        "B13TC",
        "Patients under 15 years",
        getActivePatientsInARTByEndOfCurrentMonth(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    // C1 indicators
    dsd.addColumn(
        "C1TC",
        "Patients who initiated Pre-TARV during the current month and was screened for TB C1",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients who initiated Pre-TARV during the current month and was screened for TB C1",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndScreenedTbC1(),
                    mappings)),
            mappings),
        "");

    // C2 indicators
    dsd.addColumn(
        "C2TC",
        "Patients who initiated Pre-TARV during the current month and started TPI C2",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients who initiated Pre-TARV during the current month and started TPI C2",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndStartedTpiC2(),
                    mappings)),
            mappings),
        "");

    // C3 indicators
    dsd.addColumn(
        "C3TC",
        "Patientes who initiated Pre-TARV during the current month and were diagnosed for active TB",
        getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndDiagnosedForActiveTBC3(),
        "");

    // E1 indicators

    addRow(
        dsd,
        "E1",
        "Annual Notification",
        map(
            eptsGeneralIndicator.getIndicator(
                "E1",
                map(
                    resumoMensalCohortQueries
                        .getNumberOfActivePatientsInArtAtEndOfCurrentMonthWithVlPerformed(false),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.disAggForE());
    // E2
    addRow(
        dsd,
        "E2",
        "Annual Notification",
        map(
            eptsGeneralIndicator.getIndicator(
                "E2",
                map(
                    resumoMensalCohortQueries
                        .getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.disAggForE());

    // E3
    addRow(
        dsd,
        "E3",
        "Annual Notification",
        map(
            eptsGeneralIndicator.getIndicator(
                "E3",
                map(
                    resumoMensalCohortQueries
                        .getActivePatientsOnArtWhoReceivedVldSuppressionResults(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.disAggForE());

    // F1
    dsd.addColumn(
        "F1",
        "Number of patients who had clinical appointment during the reporting month",
        map(
            eptsGeneralIndicator.getIndicator(
                "F1",
                map(
                    resumoMensalCohortQueries
                        .getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthF1(),
                    mappings)),
            mappings),
        "");
    // F2
    dsd.addColumn(
        "F2",
        "Number of patients who had clinical appointment during the reporting month and were screened for TB",
        map(
            eptsGeneralIndicator.getIndicator(
                "F2",
                map(
                    resumoMensalCohortQueries
                        .getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthAndScreenedForTbF2(),
                    mappings)),
            mappings),
        "");
    // F3
    dsd.addColumn(
        "F3",
        "Number of patients who had at least one clinical appointment during the year",
        map(
            eptsGeneralIndicator.getIndicator(
                "F3",
                map(
                    resumoMensalCohortQueries
                        .getNumberOfPatientsWithAtLeastOneClinicalAppointmentDuringTheYearF3(),
                    mappings)),
            mappings),
        "");

    return dsd;
  }

  private Mapped<CohortIndicator> getSumOfA1AndA2() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Patients under 15 years",
            mapStraightThrough(resumoMensalCohortQueries.getSumOfA1AndA2())));
  }

  private Mapped<CohortIndicator> getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Patients under 15 years",
            mapStraightThrough(
                resumoMensalCohortQueries
                    .getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2())));
  }

  private Mapped<CohortIndicator> getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Total patients of A1",
            mapStraightThrough(
                resumoMensalCohortQueries
                    .getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1())));
  }

  private Mapped<CohortIndicator>
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Patients under 15 years",
            map(
                resumoMensalCohortQueries
                    .getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2(),
                "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")));
  }

  private Mapped<CohortIndicator> getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Patients under 15 years",
            mapStraightThrough(
                resumoMensalCohortQueries
                    .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1())));
  }

  private Mapped<CohortIndicator> getPatientsTransferredOutDuringCurrentMonth() {
    String name = "Patients transferred out during the current month";
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    CohortDefinition cohort = resumoMensalCohortQueries.getPatientsTransferredOutB5(true);
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getPatientsWithArtSuspensionDuringCurrentMonth() {
    String name = "Patients with ART suspension during the current month";
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    CohortDefinition cohort = resumoMensalCohortQueries.getPatientsWhoSuspendedTreatmentB6(true);
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, map(cohort, mappings)));
  }

  private Mapped<CohortIndicator> getPatientsWhoAbandonedArtDuringCurrentMonth() {
    String name = "Patients who abandoned the ART during the current month";
    Mapped<CohortDefinition> cohort =
        map(
            resumoMensalCohortQueries.getNumberOfPatientsWhoAbandonedArtDuringCurrentMonthForB7(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}");
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getPatientsWhoDiedDuringCurrentMonth() {
    String name = "Patients who died during the current month";
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    CohortDefinition cohort = resumoMensalCohortQueries.getPatientsWhoDied(true);
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, map(cohort, mappings)));
  }

  private Mapped<CohortIndicator> getPatientsWhoStartedArtByEndOfPreviousMonth() {
    String name = "Patients who started ART by end of current month";
    Mapped<CohortDefinition> cohort =
        mapStraightThrough(
            resumoMensalCohortQueries.getPatientsWhoStartedArtByEndOfPreviousMonthB10());
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getPatientsWhoWereActiveByEndOfPreviousMonth() {
    String name = "Patients active in ART by end of previous month";
    Mapped<CohortDefinition> cohort =
        mapStraightThrough(
            resumoMensalCohortQueries.getPatientsWhoWereActiveByEndOfPreviousMonthB12());
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator>
      getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndDiagnosedForActiveTBC3() {
    String name =
        "Patientes who initiated Pre-TARV during the current month and were diagnosed for active TB C3";
    Mapped<CohortDefinition> cohort =
        mapStraightThrough(
            resumoMensalCohortQueries
                .getPatientsWhoInitiatedPreTarvDuringCurrentMonthAndDiagnosedForActiveTBC3());
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getActivePatientsInARTByEndOfCurrentMonth() {
    String name = "";

    Mapped<CohortDefinition> cohort =
        mapStraightThrough(
            resumoMensalCohortQueries.getActivePatientsInARTByEndOfCurrentMonth(false));

    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }
}
