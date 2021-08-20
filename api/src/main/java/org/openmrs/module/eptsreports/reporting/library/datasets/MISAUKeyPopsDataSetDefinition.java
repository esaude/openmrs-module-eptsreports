/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */

package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.MISAUKeyPopsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MISAUKeyPopsDataSetDefinition extends BaseDataSet {

  private MISAUKeyPopsCohortQueries mISAUKeyPopsCohortQueries;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public MISAUKeyPopsDataSetDefinition(
      MISAUKeyPopsCohortQueries mISAUKeyPopsCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator,
      EptsCommonDimension eptsCommonDimension) {
    this.mISAUKeyPopsCohortQueries = mISAUKeyPopsCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.eptsCommonDimension = eptsCommonDimension;
  }

  public CohortIndicatorDataSetDefinition constructMISAUKeyPopsDataset() {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("MISAU Key Population Data Set");
    dataSetDefinition.addParameters(getParameters());
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    String keyPopMappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    CohortDefinitionDimension keyPopsDimension = eptsCommonDimension.getKeyPopsDimension();
    dataSetDefinition.addDimension("keypop", EptsReportUtils.map(keyPopsDimension, keyPopMappings));

    dataSetDefinition.addDimension(
        "kp",
        EptsReportUtils.map(eptsCommonDimension.getKpPatientsInCombinedCategories(), mappings));

    CohortIndicator getPatientsInARTIndicator =
        eptsGeneralIndicator.getIndicator(
            "getPatientsInARTIndicator",
            EptsReportUtils.map(mISAUKeyPopsCohortQueries.getPatientsInART(), mappings));

    CohortIndicator getPatientsCurrentlyInARTIndicator =
        eptsGeneralIndicator.getIndicator(
            "getPatientsCurrentlyInARTIndicator",
            EptsReportUtils.map(mISAUKeyPopsCohortQueries.getPatientsCurrentlyInART(), mappings));

    CohortIndicator getPatientsARTWithViralLoadTestIndicator =
        eptsGeneralIndicator.getIndicator(
            "getPatientsARTWithViralLoadTestIndicator",
            EptsReportUtils.map(
                mISAUKeyPopsCohortQueries.getPatientsARTWithViralLoadTest(), mappings));

    CohortIndicator getPatientsARTWithVLSuppressionIndicator =
        eptsGeneralIndicator.getIndicator(
            "getPatientsARTWithVLSuppressionIndicator",
            EptsReportUtils.map(
                mISAUKeyPopsCohortQueries.getPatientsARTWithVLSuppression(), mappings));

    CohortIndicator getPatientsStartedARTInLast12MonthsIndicator =
        eptsGeneralIndicator.getIndicator(
            "getPatientsStartedARTInLast12MonthsIndicator",
            EptsReportUtils.map(
                mISAUKeyPopsCohortQueries.getPatientsStartedARTInLast12Months(), mappings));

    CohortIndicator getPatientsOnARTInLast12MonthsIndicator =
        eptsGeneralIndicator.getIndicator(
            "getPatientsOnARTInLast12MonthsIndicator",
            EptsReportUtils.map(
                mISAUKeyPopsCohortQueries.getPatientsOnARTInLast12Months(), mappings));

    // This returns total adult patients Started ART B1
    addRow(
        dataSetDefinition,
        "START",
        "Total Started ART",
        EptsReportUtils.map(getPatientsInARTIndicator, mappings),
        getColumnsForAdults());
    // Numero adultos que iniciaram TARV durante o trimestre PID
    addRow(
        dataSetDefinition,
        "STARTPID",
        "START: People who inject drugs",
        EptsReportUtils.map(getPatientsInARTIndicator, mappings),
        getColumnForPID());

    // Numero adultos que iniciaram TARV durante o trimestre MSM
    addRow(
        dataSetDefinition,
        "STARTMSM",
        "START: Men who have sex with men",
        EptsReportUtils.map(getPatientsInARTIndicator, mappings),
        getColumnForMSM());

    // Numero adultos que iniciaram TARV durante o trimestre CSW
    addRow(
        dataSetDefinition,
        "STARTCSW",
        "START: Female sex workers",
        EptsReportUtils.map(getPatientsInARTIndicator, mappings),
        getColumnForCSW());

    // Numero adultos que iniciaram TARV durante o trimestre PRI
    addRow(
        dataSetDefinition,
        "STARTPRI",
        "START: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsInARTIndicator, mappings),
        getColumnForPRI());

    // This returns total adult patients currently ART B13
    addRow(
        dataSetDefinition,
        "CURRART",
        "Total Current ART",
        EptsReportUtils.map(getPatientsCurrentlyInARTIndicator, mappings),
        getColumnsForAdults());
    // currently ART - PID age dissaggregations
    addRow(
        dataSetDefinition,
        "CURRARTPID",
        "CURRART: People who inject drugs",
        EptsReportUtils.map(getPatientsCurrentlyInARTIndicator, mappings),
        getColumnForPID());
    // currently ART - MSM age dissaggregations
    addRow(
        dataSetDefinition,
        "CURRARTMSM",
        "CURRART: Men who have sex with men",
        EptsReportUtils.map(getPatientsCurrentlyInARTIndicator, mappings),
        getColumnForMSM());

    // currently ART - CSW age dissaggregations
    addRow(
        dataSetDefinition,
        "CURRARTCSW",
        "CURRART: Female sex workers",
        EptsReportUtils.map(getPatientsCurrentlyInARTIndicator, mappings),
        getColumnForCSW());
    // currently ART - CSW age dissaggregations
    addRow(
        dataSetDefinition,
        "CURRARTPRI",
        "CURRART: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsCurrentlyInARTIndicator, mappings),
        getColumnForPRI());

    // This returns total adult patients with viral load test E2
    addRow(
        dataSetDefinition,
        "VL",
        "Total VL test patients",
        EptsReportUtils.map(getPatientsARTWithViralLoadTestIndicator, mappings),
        getColumnsForAdults());

    // Adults patients with viral load test PID
    addRow(
        dataSetDefinition,
        "VLPID",
        "VL: People who inject drugs",
        EptsReportUtils.map(getPatientsARTWithViralLoadTestIndicator, mappings),
        getColumnForPID());
    // Adults patients with viral load test MSM
    addRow(
        dataSetDefinition,
        "VLMSM",
        "VL: Men who have sex with men",
        EptsReportUtils.map(getPatientsARTWithViralLoadTestIndicator, mappings),
        getColumnForMSM());
    // Adults patients with viral load test CSW
    addRow(
        dataSetDefinition,
        "VLCSW",
        "VL: Female sex workers",
        EptsReportUtils.map(getPatientsARTWithViralLoadTestIndicator, mappings),
        getColumnForCSW());
    // Adults patients with viral load test PRI
    addRow(
        dataSetDefinition,
        "VLPRI",
        "VL: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsARTWithViralLoadTestIndicator, mappings),
        getColumnForPRI());

    // This returns total adult patients with viral load supression E3
    addRow(
        dataSetDefinition,
        "VLSUP",
        "Total VL supression patients",
        EptsReportUtils.map(getPatientsARTWithVLSuppressionIndicator, mappings),
        getColumnsForAdults());

    // Adult patients with viral load supression - PID
    addRow(
        dataSetDefinition,
        "VLSUPPID",
        "VLSUP: People who inject drugs",
        EptsReportUtils.map(getPatientsARTWithVLSuppressionIndicator, mappings),
        getColumnForPID());
    // Adult patients with viral load supression - MSM
    addRow(
        dataSetDefinition,
        "VLSUPMSM",
        "VLSUP: Men who have sex with men",
        EptsReportUtils.map(getPatientsARTWithVLSuppressionIndicator, mappings),
        getColumnForMSM());
    // Adult patients with viral load supression - CSW
    addRow(
        dataSetDefinition,
        "VLSUPCSW",
        "VLSUP: Female sex workers",
        EptsReportUtils.map(getPatientsARTWithVLSuppressionIndicator, mappings),
        getColumnForMSM());

    // Adult patients with viral load supression - PRI
    addRow(
        dataSetDefinition,
        "VLSUPPRI",
        "VLSUP: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsARTWithVLSuppressionIndicator, mappings),
        getColumnForPRI());

    // This returns total adult patients started ART in last 12 Months
    addRow(
        dataSetDefinition,
        "START12",
        "Total patients started ART in last 12 Months",
        EptsReportUtils.map(getPatientsStartedARTInLast12MonthsIndicator, mappings),
        getColumnsForAdults());

    // Adult patients started ART in last 12 Months - PID
    addRow(
        dataSetDefinition,
        "START12PID",
        "START12: People who inject drugs",
        EptsReportUtils.map(getPatientsStartedARTInLast12MonthsIndicator, mappings),
        getColumnForPID());
    // Adult patients started ART in last 12 Months - MSM
    addRow(
        dataSetDefinition,
        "START12MSM",
        "START12: Men who have sex with men",
        EptsReportUtils.map(getPatientsStartedARTInLast12MonthsIndicator, mappings),
        getColumnForMSM());
    // Adult patients started ART in last 12 Months - CSW
    addRow(
        dataSetDefinition,
        "START12CSW",
        "START12: Female sex workers",
        EptsReportUtils.map(getPatientsStartedARTInLast12MonthsIndicator, mappings),
        getColumnForCSW());
    // Adult patients started ART in last 12 Months - PRI
    addRow(
        dataSetDefinition,
        "START12PRI",
        "START12: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsStartedARTInLast12MonthsIndicator, mappings),
        getColumnForPRI());

    // This returns total adult patients on ART in last 12 Months
    addRow(
        dataSetDefinition,
        "ARTLAST12",
        "Total patients on ART in last 12 Months",
        EptsReportUtils.map(getPatientsOnARTInLast12MonthsIndicator, mappings),
        getColumnsForAdults());

    // Adult patients on ART in last 12 Months - PID
    addRow(
        dataSetDefinition,
        "ARTLAST12PID",
        "ARTLAST12: People who inject drugs",
        EptsReportUtils.map(getPatientsOnARTInLast12MonthsIndicator, mappings),
        getColumnForPID());

    // Adult patients on ART in last 12 Months - MSM
    addRow(
        dataSetDefinition,
        "ARTLAST12MSM",
        "ARTLAST12: Men who have sex with men",
        EptsReportUtils.map(getPatientsOnARTInLast12MonthsIndicator, mappings),
        getColumnForMSM());
    // Adult patients on ART in last 12 Months - CSW
    addRow(
        dataSetDefinition,
        "ARTLAST12CSW",
        "ARTLAST12: Female sex workers",
        EptsReportUtils.map(getPatientsOnARTInLast12MonthsIndicator, mappings),
        getColumnForCSW());
    // Adult patients on ART in last 12 Months - PRI
    addRow(
        dataSetDefinition,
        "ARTLAST12PRI",
        "ARTLAST12: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsOnARTInLast12MonthsIndicator, mappings),
        getColumnForPRI());

    // POP CHAVES PID- Coorte 6 meses ART start
    // totals
    // totals PID ART start
    dataSetDefinition.addColumn(
        "STARTPID6MT",
        "Total Started ART in 6 months cohort",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTPID6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=PID");
    // Totals split into ages - PID ART start
    addRow(
        dataSetDefinition,
        "STARTPID6MD",
        "Total Started ART in 6 months cohort disagg",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTPID6MD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForPID());
    // Total disagregated into PID e HSH ART start
    addRow(
        dataSetDefinition,
        "STARTPID6MPIDHSH",
        "Total Started ART in 6 months cohort disagg with PID e HSH disagregations",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTPID6MPIDHSH",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidHshAndAgeColumns());
    // Total disagregated into PID e MTS ART start
    addRow(
        dataSetDefinition,
        "STARTPID6MPIDMTS",
        "Total Started ART in 6 months cohort disagg with PID e MTS disagregations",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTPID6MPIDMTS",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidMtsAndAgeColumns());
    // Total disagregated into PID e Rec ART start
    addRow(
        dataSetDefinition,
        "STARTPID6MPIDREC",
        "Total Started ART in 6 months cohort disagg with PID e REC disagregations",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTPID6MPIDREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidRecAndAgeColumns());
    // POP CHAVES PID- Coorte 6 meses Currently on ART
    // PID totals Currently on ART
    dataSetDefinition.addColumn(
        "CURRPID6MT",
        "Total Current on ART in 6 months cohort",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRPID6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=PID");
    // Totals split into ages - PID Currently on ART
    addRow(
        dataSetDefinition,
        "CURRPID6MTD",
        "Total Currently on ART in 6 months cohort disagg",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRPID6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForPID());
    // Totals split into ages - PID e HSH Currently on ART
    addRow(
        dataSetDefinition,
        "CURRPID6MPIDHSH",
        "Total Currently on ART in 6 months cohort disagg - PID e HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRPID6MPIDHSH",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidHshAndAgeColumns());
    // Totals split into ages - PID e MTS Currently on ART
    addRow(
        dataSetDefinition,
        "CURRPID6MPIDMTS",
        "Total Currently on ART in 6 months cohort disagg - PID e MTS",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRPID6MPIDMTS",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidMtsAndAgeColumns());

    // Totals split into ages - PID e REC Currently on ART
    addRow(
        dataSetDefinition,
        "CURRPID6MPIDREC",
        "Total Currently on ART in 6 months cohort disagg - PID e REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRPID6MPIDREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidRecAndAgeColumns());

    // POP CHAVES PID- Coorte 6 meses With VL results
    // PID totals with VL results
    dataSetDefinition.addColumn(
        "VLPID6MT",
        "Total VL results in 6 months cohort",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLPID6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=PID");
    // Totals split into ages - with VL results
    addRow(
        dataSetDefinition,
        "VLPID6MTD",
        "Total VL results in 6 months cohort disagg for PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLPID6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForPID());
    // PID e HSH with VL results
    addRow(
        dataSetDefinition,
        "VLPID6MPIDHSH",
        "Total VL results in 6 months cohort disagg with PID e HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLPID6MPIDHSH",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidHshAndAgeColumns());
    // PID e MTS with VL results
    addRow(
        dataSetDefinition,
        "VLPID6MPIDMTS",
        "Total VL results in 6 months cohort disagg with PID e MTS",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLPID6MPIDMTS",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidMtsAndAgeColumns());
    // PID e REC with VL results
    addRow(
        dataSetDefinition,
        "VLPID6MPIDREC",
        "Total VL results in 6 months cohort disagg with PID e REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLPID6MPIDREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidRecAndAgeColumns());

    // POP CHAVES PID- Coorte 6 meses With VL suppression
    // PID totals with VL supression
    dataSetDefinition.addColumn(
        "VLSPID6MT",
        "Total VL suppression in 6 months cohort",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSPID6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=PID");
    // Totals split into ages - with VL suppression
    addRow(
        dataSetDefinition,
        "VLSPID6MTD",
        "Total VL suppresion in 6 months cohort disagg for PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSPID6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForPID());
    // VL suppresssion PID e HSH
    addRow(
        dataSetDefinition,
        "VLSPID6MPIDHSH",
        "Total VL suppresion in 6 months cohort disagg for PID e HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSPID6MPIDHSH",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidHshAndAgeColumns());
    // VL suppresssion PID e MTS
    addRow(
        dataSetDefinition,
        "VLSPID6MPIDMTS",
        "Total VL suppresion in 6 months cohort disagg for PID e MTS",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSPID6MPIDMTS",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidMtsAndAgeColumns());
    // VL suppresssion PID e REC
    addRow(
        dataSetDefinition,
        "VLSPID6MPIDREC",
        "Total VL suppresion in 6 months cohort disagg for PID e REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSPID6MPIDREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidRecAndAgeColumns());

    // POP CHAVES HSH - Coorte 6 meses - Start ART
    // HSH totals with Start ART
    dataSetDefinition.addColumn(
        "STARTARTHSH6MT",
        "Total Start ART in 6 months cohort - HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTHSH6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=MSM");
    // Totals split into ages - Start on ART - HSH
    addRow(
        dataSetDefinition,
        "STARTARTHSH6MTD",
        "Total Start ART in 6 months cohort disagg for HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTHSH6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForMSM());

    // Totals split into ages - Start on ART - HSH e PID
    addRow(
        dataSetDefinition,
        "STARTARTHSHPID",
        "Total Start ART in 6 months cohort disagg for HSH e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTHSHPID",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidHshAndAgeColumns());

    // Totals split into ages - Start on ART - HSH e REC
    addRow(
        dataSetDefinition,
        "STARTARTHSHREC",
        "Total Start ART in 6 months cohort disagg for HSH e REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTHSHREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getHshRecAndAgeColumns());

    // HSH totals with Currently on ART
    dataSetDefinition.addColumn(
        "CURRARTHSH6MT",
        "Total Current on ART in 6 months cohort - HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRARTHSH6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=MSM");
    // Totals split into ages - Current on ART - HSH
    addRow(
        dataSetDefinition,
        "CURRARTHSH6MTD",
        "Total Current on ART in 6 months cohort disagg for HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRARTHSH6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForMSM());

    // Totals split into ages - Start on ART - HSH e PID
    addRow(
        dataSetDefinition,
        "CURRARTHSHPID",
        "Total Current on ART in 6 months cohort disagg for HSH e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRARTHSHPID",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidHshAndAgeColumns());

    // Totals split into ages - Current on ART - HSH e REC
    addRow(
        dataSetDefinition,
        "CURRARTHSHREC",
        "Total Current on ART in 6 months cohort disagg for HSH e REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRARTHSHREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getHshRecAndAgeColumns());

    // HSH totals with VL results
    dataSetDefinition.addColumn(
        "VLRHSH6MT",
        "Total with VL results in 6 months cohort - HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRHSH6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=MSM");
    // Totals split into ages - VL results - HSH
    addRow(
        dataSetDefinition,
        "VLRHSH6MTD",
        "Total patients with VL results in 6 months cohort disagg for HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRHSH6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForMSM());

    // Totals split into ages - VL results - HSH e PID
    addRow(
        dataSetDefinition,
        "VLRHSHPID",
        "Total VL results in 6 months cohort disagg for HSH e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRHSHPID",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidHshAndAgeColumns());

    // Totals split into ages - VL results - HSH e REC
    addRow(
        dataSetDefinition,
        "VLRHSHREC",
        "Total VL results in 6 months cohort disagg for HSH e REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRHSHREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getHshRecAndAgeColumns());

    // HSH totals with VL Suppression
    dataSetDefinition.addColumn(
        "VLSHSH6MT",
        "Total with VL supression in 6 months cohort - HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSHSH6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=MSM");
    // Totals split into ages - VL results - HSH
    addRow(
        dataSetDefinition,
        "VLSHSH6MTD",
        "Total patients with VL suppression in 6 months cohort disagg for HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSHSH6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForMSM());

    // Totals split into ages - VL suppression - HSH e PID
    addRow(
        dataSetDefinition,
        "VLSHSHPID",
        "Total VL suppression in 6 months cohort disagg for HSH e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSHSHPID",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidHshAndAgeColumns());

    // Totals split into ages - VL suppression - HSH e REC
    addRow(
        dataSetDefinition,
        "VLSHSHREC",
        "Total VL suppression in 6 months cohort disagg for HSH e REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLsHSHREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getHshRecAndAgeColumns());

    // POP CHAVES MTS - Coorte 6 meses
    // MTS totals with Start ART
    dataSetDefinition.addColumn(
        "STARTARTMTS6MT",
        "Total with Start ART in 6 months cohort - MTS/CSW",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTMTS6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=CSW");
    // Totals split into ages - Start ART - MTS/CSW
    addRow(
        dataSetDefinition,
        "STARTARTMTS6MTD",
        "Total patients started ART in 6 months cohort disagg for MTS/CSW",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTMTS6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForCSW());

    // Totals split into ages - MTS Start on ART - MTS e PID
    addRow(
        dataSetDefinition,
        "STARTARTMTS6M",
        "Total Start ART in 6 months cohort disagg for MTS/CSW - MTS e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTMTS6M",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidMtsAndAgeColumns());

    // Totals split into ages - Start ART - MTS e REC
    addRow(
        dataSetDefinition,
        "STARTARTMTSREC",
        "Total Start ART in 6 months cohort disagg for MTS e REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTMTSREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getMtsRecAndAgeColumns());

    // POP CHAVES MTS - Coorte 6 meses
    // MTS totals with Currently on ART
    dataSetDefinition.addColumn(
        "CURRMTS6MT",
        "Total with Current on ART in 6 months cohort - MTS/CSW",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRMTS6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=CSW");
    // Totals split into ages - Currently - MTS
    addRow(
        dataSetDefinition,
        "CURRMTS6MTD",
        "Total patients Currently on ART in 6 months cohort disagg for MTS/CSW",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRMTS6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForCSW());

    // Totals split into ages - MTS Current on ART - MTS e PID
    addRow(
        dataSetDefinition,
        "CURRMTSPID6M",
        "Total Current on ART in 6 months cohort disagg for MTS/CSW - MTS e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRMTSPID6M",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidMtsAndAgeColumns());

    // Totals split into ages - Current on ART - MTS e REC
    addRow(
        dataSetDefinition,
        "CURRMTSREC",
        "Total Current on ART in 6 months cohort disagg for MTS e REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRMTSREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getMtsRecAndAgeColumns());

    // POP CHAVES MTS - Coorte 6 meses
    // MTS totals with Viral load results
    dataSetDefinition.addColumn(
        "VLRMTS6MT",
        "Total with VL results in 6 months cohort - MTS/CSW",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRMTS6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=CSW");
    // Totals split into ages - VL results - MTS
    addRow(
        dataSetDefinition,
        "VLRMTS6MTD",
        "Total patients VL results on ART in 6 months cohort disagg for MTS/CSW",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRMTS6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForCSW());

    // Totals split into ages - MTS VL results - MTS e PID
    addRow(
        dataSetDefinition,
        "VLRMTSPID6M",
        "Total VL resultson ART in 6 months cohort disagg for MTS/CSW - MTS e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRMTSPID6M",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidMtsAndAgeColumns());

    // Totals split into ages - VL results - MTS e REC
    addRow(
        dataSetDefinition,
        "VLRMTSREC",
        "Total VL results in 6 months cohort disagg for MTS e REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRMTSREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getMtsRecAndAgeColumns());

    // POP CHAVES MTS - Coorte 6 meses
    // MTS totals with Viral load  suppresion
    dataSetDefinition.addColumn(
        "VLSMTS6MT",
        "Total with VL suppression in 6 months cohort - MTS/CSW",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSMTS6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=CSW");
    // Totals split into ages - VL results - MTS
    addRow(
        dataSetDefinition,
        "VLSMTS6MTD",
        "Total patients VL suppression on ART in 6 months cohort disagg for MTS/CSW",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSMTS6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForCSW());

    // Totals split into ages - MTS VL suppresion - MTS e PID
    addRow(
        dataSetDefinition,
        "VLSMTSPID6M",
        "Total VL suppression ART in 6 months cohort disagg for MTS/CSW - MTS e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSMTSPID6M",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidMtsAndAgeColumns());

    // Totals split into ages - VL suppression - MTS e REC
    addRow(
        dataSetDefinition,
        "VLSMTSREC",
        "Total VL suppression in 6 months cohort disagg for MTS e REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSMTSREC",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getMtsRecAndAgeColumns());

    // POP CHAVES REC - Coorte 6 meses
    // REC totals with Start ART
    dataSetDefinition.addColumn(
        "STARTARTREC6MT",
        "Total with Start ART in 6 months cohort - REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTREC6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=PRI");
    // Totals split into ages - Start - REC
    addRow(
        dataSetDefinition,
        "STARTARTREC6MTD",
        "Total patients start on ART in 6 months cohort disagg for REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTREC6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForPRI());

    // Totals split into ages - Start - REC e PID
    addRow(
        dataSetDefinition,
        "STARTARTPIDREC6MTD",
        "Total patients start on ART in 6 months cohort disagg for REC e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTPIDREC6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidRecAndAgeColumns());

    // Totals split into ages - Start - REC e HSH
    addRow(
        dataSetDefinition,
        "STARTARTRECHSH6MTD",
        "Total patients start on ART in 6 months cohort disagg for REC e HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTRECHSH6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getHshRecAndAgeColumns());

    // Totals split into ages - Start - REC e MTS
    addRow(
        dataSetDefinition,
        "STARTARTRECMTS6MTD",
        "Total patients start on ART in 6 months cohort disagg for REC e MTS",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTRECMTS6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsWhoStartedArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getMtsRecAndAgeColumns());

    // POP CHAVES REC - Coorte 6 meses
    // REC totals with Currently on ART
    dataSetDefinition.addColumn(
        "CURRREC6MT",
        "Total Patient currently on  ART in 6 months cohort - REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRREC6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=PRI");
    // Totals split into ages - Current on ART - REC
    addRow(
        dataSetDefinition,
        "STARTARTREC6MTD",
        "Total patients currently on ART in 6 months cohort disagg for REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "STARTARTREC6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForPRI());

    // Totals split into ages - current on ART - REC e PID
    addRow(
        dataSetDefinition,
        "CURRPIDREC6MTD",
        "Total patients current on ART in 6 months cohort disagg for REC e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRPIDREC6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidRecAndAgeColumns());

    // Totals split into ages - current on  - REC e HSH
    addRow(
        dataSetDefinition,
        "CURRRECHSH6MTD",
        "Total patients current on ART in 6 months cohort disagg for REC e HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRRECHSH6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getHshRecAndAgeColumns());

    // Totals split into ages - current - REC e MTS
    addRow(
        dataSetDefinition,
        "CURRECMTS6MTD",
        "Total patients current on ART in 6 months cohort disagg for REC e MTS",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CURRECMTS6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries.getNumberOfAdultsCurrentlyOnArtInSixMonthsCohort(),
                    mappings)),
            mappings),
        getMtsRecAndAgeColumns());

    // POP CHAVES REC - Coorte 6 meses
    // REC totals with VL results
    dataSetDefinition.addColumn(
        "VLRREC6MT",
        "Total Patient with VL results in 6 months cohort - REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRREC6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=PRI");
    // Totals split into ages - VL results - REC
    addRow(
        dataSetDefinition,
        "VLREC6MTD",
        "Total patients VL results in 6 months cohort disagg for REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLREC6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForPRI());

    // Totals split into ages - VL results - REC e PID
    addRow(
        dataSetDefinition,
        "VLRPIDREC6MTD",
        "Total patients with VL results in 6 months cohort disagg for REC e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRPIDREC6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidRecAndAgeColumns());

    // Totals split into ages - VL results  - REC e HSH
    addRow(
        dataSetDefinition,
        "VLRECHSH6MTD",
        "Total patients with VL results in 6 months cohort disagg for REC e HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRECHSH6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getHshRecAndAgeColumns());

    // Totals split into ages - Vl results - REC e MTS
    addRow(
        dataSetDefinition,
        "VLRECMTS6MTD",
        "Total patients with VL results in 6 months cohort disagg for REC e MTS",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLRECMTS6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientsOnArtWithVlResultsInSixMonthsCohort(),
                    mappings)),
            mappings),
        getMtsRecAndAgeColumns());

    // POP CHAVES REC - Coorte 6 meses
    // REC totals with VL supression
    dataSetDefinition.addColumn(
        "VLSREC6MT",
        "Total Patient with VL suppression in 6 months cohort - REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSREC6MT",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        "keypop=PRI");
    // Totals split into ages - VL supression - REC
    addRow(
        dataSetDefinition,
        "VLSEC6MTD",
        "Total patients VL supression in 6 months cohort disagg for REC",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSEC6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getColumnForPRI());

    // Totals split into ages - VL supression - REC e PID
    addRow(
        dataSetDefinition,
        "VLSPIDREC6MTD",
        "Total patients with VL supression in 6 months cohort disagg for REC e PID",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSPIDREC6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getPidRecAndAgeColumns());

    // Totals split into ages - VL suppression  - REC e HSH
    addRow(
        dataSetDefinition,
        "VLSECHSH6MTD",
        "Total patients with VL supression in 6 months cohort disagg for REC e HSH",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSECHSH6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getHshRecAndAgeColumns());

    // Totals split into ages - Vl suppression - REC e MTS
    addRow(
        dataSetDefinition,
        "VLSRECMTS6MTD",
        "Total patients with VL suppression in 6 months cohort disagg for REC e MTS",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "VLSRECMTS6MTD",
                EptsReportUtils.map(
                    mISAUKeyPopsCohortQueries
                        .getActiveAdultPatientOnArtWithVlSuppressionInSixMonthsCohort(),
                    mappings)),
            mappings),
        getMtsRecAndAgeColumns());

    return dataSetDefinition;
  }

  private List<ColumnParameters> getColumnsForAdults() {
    ColumnParameters fifteenPlus =
        new ColumnParameters("fifteenPlus", "15 +", "age=15+", "adultos");

    return Arrays.asList(fifteenPlus);
  }

  private List<ColumnParameters> getColumnForPID() {
    ColumnParameters pidFifteenTo19 =
        new ColumnParameters("pidFifteenTo19", "PID 15-19 years", "age=15-19|keypop=PID", "01");
    ColumnParameters pidTwentyTo24 =
        new ColumnParameters("pidTwentyTo24", "PID 20-24 years", "age=20-24|keypop=PID", "02");
    ColumnParameters pidTwenty25Plus =
        new ColumnParameters("pidTwenty25Plus", "PID 25 years+", "age=25+|keypop=PID", "03");

    return Arrays.asList(pidFifteenTo19, pidTwentyTo24, pidTwenty25Plus);
  }

  private List<ColumnParameters> getColumnForMSM() {
    ColumnParameters msmFifteenTo19 =
        new ColumnParameters("msmFifteenTo19", "MSM 15-19 years", "age=15-19|keypop=MSM", "01");
    ColumnParameters msmTwentyTo24Art =
        new ColumnParameters("msmTwentyTo24Art", "MSM 20-24 years", "age=20-24|keypop=MSM", "02");
    ColumnParameters msmTwenty25Plus =
        new ColumnParameters("msmTwenty25Plus", "MSM 25 years+", "age=25+|keypop=MSM", "03");

    return Arrays.asList(msmFifteenTo19, msmTwentyTo24Art, msmTwenty25Plus);
  }

  private List<ColumnParameters> getColumnForCSW() {
    ColumnParameters cswFifteenTo19 =
        new ColumnParameters("cswFifteenTo19", "CSW 15-19 years", "age=15-19|keypop=CSW", "01");
    ColumnParameters cswTwentyTo24 =
        new ColumnParameters("cswTwentyTo24", "CSW 20-24 years", "age=20-24|keypop=CSW", "02");
    ColumnParameters cswTwenty25Plus =
        new ColumnParameters("cswTwenty25Plus", "CSW 25 years+", "age=25+|keypop=CSW", "03");

    return Arrays.asList(cswFifteenTo19, cswTwentyTo24, cswTwenty25Plus);
  }

  private List<ColumnParameters> getColumnForPRI() {
    ColumnParameters priFifteenTo19 =
        new ColumnParameters("priFifteenTo19", "CSW 15-19 years", "age=15-19|keypop=PRI", "01");
    ColumnParameters priTwentyTo24 =
        new ColumnParameters("priTwentyTo24", "CSW 20-24 years", "age=20-24|keypop=PRI", "02");
    ColumnParameters priTwenty25Plus =
        new ColumnParameters("priTwenty25Plus", "CSW 25 years+", "age=25+|keypop=PRI", "03");

    return Arrays.asList(priFifteenTo19, priTwentyTo24, priTwenty25Plus);
  }

  private List<ColumnParameters> getPidHshAndAgeColumns() {
    ColumnParameters pidHsh15To19 =
        new ColumnParameters("pidHsh15To19", "15-19 years", "age=15-19|kp=PIDeHSH", "01");
    ColumnParameters pidHsh20To24 =
        new ColumnParameters("pidHsh20To24", "20-24 years", "age=20-24|kp=PIDeHSH", "02");
    ColumnParameters pidHsh25Plus =
        new ColumnParameters("pidHsh25Plus", "25+ years", "age=25+|kp=PIDeHSH", "03");

    return Arrays.asList(pidHsh15To19, pidHsh20To24, pidHsh25Plus);
  }

  private List<ColumnParameters> getPidMtsAndAgeColumns() {
    ColumnParameters pidMts15To19 =
        new ColumnParameters("pidMts15To19", "15-19 years", "age=15-19|kp=PIDeMTS", "01");
    ColumnParameters pidMts20To24 =
        new ColumnParameters("pidMts20To24", "20-24 years", "age=20-24|kp=PIDeMTS", "02");
    ColumnParameters pidMts25Plus =
        new ColumnParameters("pidMts25Plus", "25+ years", "age=25+|kp=PIDeMTS", "03");

    return Arrays.asList(pidMts15To19, pidMts20To24, pidMts25Plus);
  }

  private List<ColumnParameters> getPidRecAndAgeColumns() {
    ColumnParameters pidRec15To19 =
        new ColumnParameters("pidRec15To19", "15-19 years", "age=15-19|kp=PIDeREC", "01");
    ColumnParameters pidrec20To24 =
        new ColumnParameters("pidrec20To24", "20-24 years", "age=20-24|kp=PIDeREC", "02");
    ColumnParameters pidRec25Plus =
        new ColumnParameters("pidRec25Plus", "25+ years", "age=25+|kp=PIDeREC", "03");

    return Arrays.asList(pidRec15To19, pidrec20To24, pidRec25Plus);
  }

  private List<ColumnParameters> getHshRecAndAgeColumns() {
    ColumnParameters hshRec15To19 =
        new ColumnParameters("hshRec15To19", "15-19 years", "age=15-19|kp=HSHeREC", "01");
    ColumnParameters hshrec20To24 =
        new ColumnParameters("hshrec20To24", "20-24 years", "age=20-24|kp=HSHeREC", "02");
    ColumnParameters hshRec25Plus =
        new ColumnParameters("hshRec25Plus", "25+ years", "age=25+|kp=HSHeREC", "03");

    return Arrays.asList(hshRec15To19, hshrec20To24, hshRec25Plus);
  }

  private List<ColumnParameters> getMtsRecAndAgeColumns() {
    ColumnParameters mtsRec15To19 =
        new ColumnParameters("mtsRec15To19", "15-19 years", "age=15-19|kp=MTSeREC", "01");
    ColumnParameters mtsrec20To24 =
        new ColumnParameters("mtsrec20To24", "20-24 years", "age=20-24|kp=MTSeREC", "02");
    ColumnParameters mtsRec25Plus =
        new ColumnParameters("mtsRec25Plus", "25+ years", "age=25+|kp=MTSeREC", "03");

    return Arrays.asList(mtsRec15To19, mtsrec20To24, mtsRec25Plus);
  }
}
