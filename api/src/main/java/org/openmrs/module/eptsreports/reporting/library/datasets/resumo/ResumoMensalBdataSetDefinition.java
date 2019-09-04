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
public class ResumoMensalBdataSetDefinition extends BaseDataSet {

  private EptsCommonDimension eptsCommonDimension;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  private ResumoMensalAandBdisaggregations resumoMensalAandBdisaggregations;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public ResumoMensalBdataSetDefinition(
      EptsCommonDimension eptsCommonDimension,
      EptsGeneralIndicator eptsGeneralIndicator,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      ResumoMensalAandBdisaggregations resumoMensalAandBdisaggregations) {
    this.eptsCommonDimension = eptsCommonDimension;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.resumoMensalAandBdisaggregations = resumoMensalAandBdisaggregations;
  }

  public DataSetDefinition constructResumoBMensalDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("Resumo Mensal Data set B");
    dsd.addParameters(getParameters());

    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    // indicators for section B1
    addRow(
        dsd,
        "1TC",
        "Patients under 15 years",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients under 15 years",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "1TA",
        "Patients over 15 years - adults",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients over 15 years - adults",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "1TP",
        "Total patients - Total Geral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total patients - Total Geral",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
                    mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "1TAD",
        "Adolescentes patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Adolescentes patients",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());
    // B2 indicators start here
    addRow(
        dsd,
        "2TC",
        "Patients under 15 years",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients under 15 years",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "2TA",
        "Patients over 15 years - adults",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients over 15 years - adults",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "2TP",
        "Total patients - Total Geral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total patients - Total Geral",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2(),
                    mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "2TAD",
        "Adolescentes patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Adolescentes patients",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());
    // B3 indicators
    addRow(
        dsd,
        "3TC",
        "Patients under 15 years",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients under 15 years",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoRestartedTreatmentDuringCurrentMonthB3(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "3TA",
        "Patients over 15 years - adults",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients over 15 years - adults",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoRestartedTreatmentDuringCurrentMonthB3(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "3TP",
        "Total patients - Total Geral",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total patients - Total Geral",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoRestartedTreatmentDuringCurrentMonthB3(),
                    mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "3TAD",
        "Adolescentes patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Adolescentes patients",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoRestartedTreatmentDuringCurrentMonthB3(),
                    mappings)),
            mappings),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B5 indicators
    addRow(
        dsd,
        "5TC",
        "Patients under 15 years",
        getPatientsTransferredOutDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "5TA",
        "Patients over 15 years - adults",
        getPatientsTransferredOutDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "5TP", "Total patients - Total Geral", getPatientsTransferredOutDuringCurrentMonth(), "");

    addRow(
        dsd,
        "5TAD",
        "Adolescentes patients",
        getPatientsTransferredOutDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B6 indicators
    addRow(
        dsd,
        "6TC",
        "Patients under 15 years",
        getPatientsWithArtSuspensionDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "6TA",
        "Patients over 15 years - adults",
        getPatientsWithArtSuspensionDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "6TP",
        "Total patients - Total Geral",
        getPatientsWithArtSuspensionDuringCurrentMonth(),
        "");

    addRow(
        dsd,
        "6TAD",
        "Adolescentes patients",
        getPatientsWithArtSuspensionDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B7 indicators
    addRow(
        dsd,
        "7TC",
        "Patients under 15 years",
        getPatientsWhoAbandonedArtDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "7TA",
        "Patients over 15 years - adults",
        getPatientsWhoAbandonedArtDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "7TP", "Total patients - Total Geral", getPatientsWhoAbandonedArtDuringCurrentMonth(), "");

    addRow(
        dsd,
        "7TAD",
        "Adolescentes patients",
        getPatientsWhoAbandonedArtDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    // B8 indicators
    addRow(
        dsd,
        "8TC",
        "Patients under 15 years",
        getPatientsWhoDiedDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    addRow(
        dsd,
        "8TA",
        "Patients over 15 years - adults",
        getPatientsWhoDiedDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdultPatients());

    dsd.addColumn(
        "8TP", "Total patients - Total Geral", getPatientsWhoDiedDuringCurrentMonth(), "");

    addRow(
        dsd,
        "8TAD",
        "Adolescentes patients",
        getPatientsWhoDiedDuringCurrentMonth(),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    return dsd;
  }

  private Mapped<CohortIndicator> getPatientsTransferredOutDuringCurrentMonth() {
    String name = "Patients transferred out during the current month";
    Mapped<CohortDefinition> cohort =
        mapStraightThrough(
            resumoMensalCohortQueries.getNumberOfPatientsTransferredOutDuringCurrentMonthB5());
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getPatientsWithArtSuspensionDuringCurrentMonth() {
    String name = "Patients with ART suspension during the current month";
    Mapped<CohortDefinition> cohort =
        mapStraightThrough(
            resumoMensalCohortQueries.getNumberOfPatientsWithArtSuspensionDuringCurrentMonthB6());
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getPatientsWhoAbandonedArtDuringCurrentMonth() {
    String name = "Patients who abandoned the ART during the current month";
    Mapped<CohortDefinition> cohort =
        mapStraightThrough(
            resumoMensalCohortQueries.getNumberOfPatientsWhoAbandonedArtDuringCurrentMonthB7());
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }

  private Mapped<CohortIndicator> getPatientsWhoDiedDuringCurrentMonth() {
    String name = "Patients who died during the current month";
    Mapped<CohortDefinition> cohort =
        mapStraightThrough(
            resumoMensalCohortQueries.getNumberOfPatientsWhoDiedDuringCurrentMonthB8());
    return mapStraightThrough(eptsGeneralIndicator.getIndicator(name, cohort));
  }
}
