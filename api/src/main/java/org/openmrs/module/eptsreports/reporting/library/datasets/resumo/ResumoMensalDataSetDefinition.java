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
    // B3 indicators
    addRow(
        dsd,
        "B3TC",
        "Patients under 15 years",
        getSumPatientsB3(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B3TA",
        "Patients over 15 years - adults",
        getSumPatientsB3(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn("B3TP", "Total patients - Total Geral", getSumPatientsB3(), "");

    addRow(
        dsd,
        "B3TAD",
        "Adolescentes patients",
        getSumPatientsB3(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B5 indicators
    addRow(
        dsd,
        "B5TC",
        "Patients under 15 years",
        getNumberOfPatientsTransferredOutFromOtherHealthFacilitiesDuringCurrentMonthB5(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B5TA",
        "Patients over 15 years - adults",
        getNumberOfPatientsTransferredOutFromOtherHealthFacilitiesDuringCurrentMonthB5(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B5TP",
        "Total patients - Total Geral",
        getNumberOfPatientsTransferredOutFromOtherHealthFacilitiesDuringCurrentMonthB5(),
        "");

    addRow(
        dsd,
        "B5TAD",
        "Adolescentes patients",
        getNumberOfPatientsTransferredOutFromOtherHealthFacilitiesDuringCurrentMonthB5(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B6 indicators
    addRow(
        dsd,
        "B6TC",
        "Patients under 15 years",
        getPatientsWhoSuspendTratmentB6(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B6TA",
        "Patients over 15 years - adults",
        getPatientsWhoSuspendTratmentB6(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn("B6TP", "Total patients - Total Geral", getPatientsWhoSuspendTratmentB6(), "");

    addRow(
        dsd,
        "B6TAD",
        "Adolescentes patients",
        getPatientsWhoSuspendTratmentB6(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B7 indicators
    addRow(
        dsd,
        "B7TC",
        "Patients under 15 years",
        getPatientsWhoAbandonedTratmentUpB7(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B7TA",
        "Patients over 15 years - adults",
        getPatientsWhoAbandonedTratmentUpB7(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B7TP", "Total patients - Total Geral", getPatientsWhoAbandonedTratmentUpB7(), "");

    addRow(
        dsd,
        "B7TAD",
        "Adolescentes patients",
        getPatientsWhoAbandonedTratmentUpB7(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B8 indicators
    addRow(
        dsd,
        "B8TC",
        "Patients under 15 years",
        getPatientsWhoDiedTratmentB8(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B8TA",
        "Patients over 15 years - adults",
        getPatientsWhoDiedTratmentB8(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn("B8TP", "Total patients - Total Geral", getPatientsWhoDiedTratmentB8(), "");

    addRow(
        dsd,
        "B8TAD",
        "Adolescentes patients",
        getPatientsWhoDiedTratmentB8(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B10 indicators
    addRow(
        dsd,
        "B10TC",
        "Patients under 15 years",
        getTxNewEndDateB10(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B10TA",
        "Patients over 15 years - adults",
        getTxNewEndDateB10(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn("B10TP", "Total patients - Total Geral", getTxNewEndDateB10(), "");

    addRow(
        dsd,
        "B10TAD",
        "Adolescentes patients",
        getTxNewEndDateB10(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B12 indicators
    addRow(
        dsd,
        "B12TC",
        "Patients under 15 years",
        getPatientsWhoAreCurrentlyEnrolledOnARTB12(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B12TA",
        "Patients over 15 years - adults",
        getPatientsWhoAreCurrentlyEnrolledOnARTB12(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B12TP", "Total patients - Total Geral", getPatientsWhoAreCurrentlyEnrolledOnARTB12(), "");

    addRow(
        dsd,
        "B12TAD",
        "Adolescentes patients",
        getPatientsWhoAreCurrentlyEnrolledOnARTB12(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B13 indicators
    addRow(
        dsd,
        "B13TC",
        "Patients under 15 years",
        getPatientsWhoAreCurrentlyEnrolledOnARTB13(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "B13TA",
        "Patients over 15 years - adults",
        getPatientsWhoAreCurrentlyEnrolledOnARTB13(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "B13TP", "Total patients - Total Geral", getPatientsWhoAreCurrentlyEnrolledOnARTB13(), "");

    addRow(
        dsd,
        "B13TAD",
        "Adolescentes patients",
        getPatientsWhoAreCurrentlyEnrolledOnARTB13(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // C1, C2, C3 indicators
    dsd.addColumn("C1TC", "Total patients - Total Geral", findPatientWhoHaveTbSymthomsC1(), "");
    dsd.addColumn("C2TC", "Total patients - Total Geral", getPatientsWhoMarkedINHC2A2(), "");
    dsd.addColumn("C3TC", "Total patients - Total Geral", getPatientsWhoMarkedTbActiveC3A2(), "");


    // // E1 indicators
    //
    // addRow(
    // dsd,
    // "E1",
    // "Annual Notification",
    // map(
    // eptsGeneralIndicator.getIndicator(
    // "E1",
    // map(
    // resumoMensalCohortQueries
    // .getNumberOfActivePatientsInArtAtEndOfCurrentMonthWithVlPerformed(),
    // mappings)),
    // mappings),
    // resumoMensalAandBdisaggregations.disAggForE());
    // // E2
    // addRow(
    // dsd,
    // "E2",
    // "Annual Notification",
    // map(
    // eptsGeneralIndicator.getIndicator(
    // "E2",
    // map(
    // resumoMensalCohortQueries
    //
    // .getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults(),
    // mappings)),
    // mappings),
    // resumoMensalAandBdisaggregations.disAggForE());
    //
    // // E3
    // addRow(
    // dsd,
    // "E3",
    // "Annual Notification",
    // map(
    // eptsGeneralIndicator.getIndicator(
    // "E3",
    // map(
    // resumoMensalCohortQueries
    // .getActivePatientsOnArtWhoRecievedVldSuppressionResults(),
    // mappings)),
    // mappings),
    // resumoMensalAandBdisaggregations.disAggForE());
    //
    // // F1
    // dsd.addColumn(
    // "F1",
    // "Number of patients who had clinical appointment during the reporting month",
    // map(
    // eptsGeneralIndicator.getIndicator(
    // "F1",
    // map(
    // resumoMensalCohortQueries
    //
    // .getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonth(),
    // mappings)),
    // mappings),
    // "");
    // // F2
    // dsd.addColumn(
    // "F2",
    // "Number of patients who had clinical appointment during the reporting month
    // and were
    // screened for TB",
    // map(
    // eptsGeneralIndicator.getIndicator(
    // "F2",
    // map(
    // resumoMensalCohortQueries
    //
    // .getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthAndScreenedFoTb(),
    // mappings)),
    // mappings),
    // "");
    // // F3
    // dsd.addColumn(
    // "F3",
    // "Number of patients who had at least one clinical appointment during the
    // year",
    // map(
    // eptsGeneralIndicator.getIndicator(
    // "F3",
    // map(
    // resumoMensalCohortQueries
    // .getNumberOfPatientsWithAtLeastOneClinicalAppointmentDuringTheYear(),
    // mappings)),
    // mappings),
    // "");

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
            "Patients under 15 years",
            mapStraightThrough(
                resumoMensalCohortQueries
                    .getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1())));
  }

  private Mapped<CohortIndicator> getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Patients under 15 years",
            mapStraightThrough(
                resumoMensalCohortQueries
                    .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1())));
  }

  private Mapped<CohortIndicator>
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Patients under 15 years",
            mapStraightThrough(
                resumoMensalCohortQueries
                    .getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2())));
  }

  private Mapped<CohortIndicator> getSumPatientsB3() {

    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Patients under 15 years",
            mapStraightThrough(resumoMensalCohortQueries.getSumPatientsB3())));
  }

  private Mapped<CohortIndicator>
      getNumberOfPatientsTransferredOutFromOtherHealthFacilitiesDuringCurrentMonthB5() {
    String name = "Patients transferred out during the current month";
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    CohortDefinition cohort =
        resumoMensalCohortQueries
            .getNumberOfPatientsTransferredOutFromOtherHealthFacilitiesDuringCurrentMonthB5();
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getPatientsWhoSuspendTratmentB6() {
    String name = "Patients transferred out during the current month";
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    CohortDefinition cohort = resumoMensalCohortQueries.getPatientsWhoSuspendTratmentB6();
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, map(cohort, mappings)));
  }

  private Mapped<CohortIndicator> getPatientsWhoAbandonedTratmentUpB7() {
    String name = "Patients who abandoned the ART during the current month";
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    Mapped<CohortDefinition> cohort =
        map(resumoMensalCohortQueries.getPatientsWhoAbandonedTratmentUpB7(), mappings);
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getPatientsWhoDiedTratmentB8() {
    String name = "Patients who abandoned the ART during the current month";
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    Mapped<CohortDefinition> cohort =
        map(resumoMensalCohortQueries.getPatientsWhoDiedTratmentB8(), mappings);
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getTxNewEndDateB10() {
    String name = "Patients who abandoned the ART during the current month";
    String mappings = "startDate=${startDate},location=${location}";

    Mapped<CohortDefinition> cohort = map(resumoMensalCohortQueries.getTxNewEndDateB10(), mappings);
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getPatientsWhoAreCurrentlyEnrolledOnARTB12() {
    String name = "Patients who abandoned the ART during the current month";
    String mappings = "startDate=${startDate-1d},location=${location}";

    Mapped<CohortDefinition> cohort =
        map(resumoMensalCohortQueries.getPatientsWhoAreCurrentlyEnrolledOnARTB12(), mappings);
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getPatientsWhoAreCurrentlyEnrolledOnARTB13() {
    String name = "Patients who abandoned the ART during the current month";
    final String mappings = "endDate=${endDate},location=${location}";

    Mapped<CohortDefinition> cohort =
        map(resumoMensalCohortQueries.getPatientsWhoAreCurrentlyEnrolledOnARTB13(), mappings);
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> findPatientWhoHaveTbSymthomsC1() {
    String name = "Patients who abandoned the ART during the current month";
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    Mapped<CohortDefinition> cohort =
        map(resumoMensalCohortQueries.findPatientWhoHaveTbSymthomsC1(), mappings);
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getPatientsWhoMarkedINHC2A2() {
    String name = "Patients who abandoned the ART during the current month";
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    Mapped<CohortDefinition> cohort =
        map(resumoMensalCohortQueries.getPatientsWhoMarkedINHC2A2(), mappings);
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getPatientsWhoMarkedTbActiveC3A2() {
    String name = "Patients who abandoned the ART during the current month";
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    Mapped<CohortDefinition> cohort =
        map(resumoMensalCohortQueries.getPatientsWhoMarkedTbActiveC3A2(), mappings);
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }
}
