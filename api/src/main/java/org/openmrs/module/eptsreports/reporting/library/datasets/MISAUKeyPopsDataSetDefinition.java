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

    // This returns total adult patients Started ART
    addRow(
        dataSetDefinition,
        "START",
        "Total Started ART",
        EptsReportUtils.map(getPatientsInARTIndicator, mappings),
        getColumnsForAdults());

    dataSetDefinition.addColumn(
        "STARTPID",
        "START: People who inject drugs",
        EptsReportUtils.map(getPatientsInARTIndicator, mappings),
        "keypop=PID");

    dataSetDefinition.addColumn(
        "STARTMSM",
        "START: Men who have sex with men",
        EptsReportUtils.map(getPatientsInARTIndicator, mappings),
        "keypop=MSM");

    dataSetDefinition.addColumn(
        "STARTCSW",
        "START: Female sex workers",
        EptsReportUtils.map(getPatientsInARTIndicator, mappings),
        "keypop=CSW");

    dataSetDefinition.addColumn(
        "STARTPRI",
        "START: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsInARTIndicator, mappings),
        "keypop=PRI");

    // This returns total adult patients currently ART
    addRow(
        dataSetDefinition,
        "CURRART",
        "Total Current ART",
        EptsReportUtils.map(getPatientsCurrentlyInARTIndicator, mappings),
        getColumnsForAdults());

    dataSetDefinition.addColumn(
        "CURRARTPID",
        "CURRART: People who inject drugs",
        EptsReportUtils.map(getPatientsCurrentlyInARTIndicator, mappings),
        "keypop=PID");

    dataSetDefinition.addColumn(
        "CURRARTMSM",
        "CURRART: Men who have sex with men",
        EptsReportUtils.map(getPatientsCurrentlyInARTIndicator, mappings),
        "keypop=MSM");

    dataSetDefinition.addColumn(
        "CURRARTCSW",
        "CURRART: Female sex workers",
        EptsReportUtils.map(getPatientsCurrentlyInARTIndicator, mappings),
        "keypop=CSW");

    dataSetDefinition.addColumn(
        "CURRARTPRI",
        "CURRART: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsCurrentlyInARTIndicator, mappings),
        "keypop=PRI");

    // This returns total adult patients with viral load test
    addRow(
        dataSetDefinition,
        "VL",
        "Total VL test patients",
        EptsReportUtils.map(getPatientsARTWithViralLoadTestIndicator, mappings),
        getColumnsForAdults());

    dataSetDefinition.addColumn(
        "VLPID",
        "VL: People who inject drugs",
        EptsReportUtils.map(getPatientsARTWithViralLoadTestIndicator, mappings),
        "keypop=PID");

    dataSetDefinition.addColumn(
        "VLMSM",
        "VL: Men who have sex with men",
        EptsReportUtils.map(getPatientsARTWithViralLoadTestIndicator, mappings),
        "keypop=MSM");

    dataSetDefinition.addColumn(
        "VLCSW",
        "VL: Female sex workers",
        EptsReportUtils.map(getPatientsARTWithViralLoadTestIndicator, mappings),
        "keypop=CSW");

    dataSetDefinition.addColumn(
        "VLPRI",
        "VL: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsARTWithViralLoadTestIndicator, mappings),
        "keypop=PRI");

    // This returns total adult patients with viral load supression
    addRow(
        dataSetDefinition,
        "VLSUP",
        "Total VL supression patients",
        EptsReportUtils.map(getPatientsARTWithVLSuppressionIndicator, mappings),
        getColumnsForAdults());

    dataSetDefinition.addColumn(
        "VLSUPPID",
        "VLSUP: People who inject drugs",
        EptsReportUtils.map(getPatientsARTWithVLSuppressionIndicator, mappings),
        "keypop=PID");

    dataSetDefinition.addColumn(
        "VLSUPMSM",
        "VLSUP: Men who have sex with men",
        EptsReportUtils.map(getPatientsARTWithVLSuppressionIndicator, mappings),
        "keypop=MSM");

    dataSetDefinition.addColumn(
        "VLSUPCSW",
        "VLSUP: Female sex workers",
        EptsReportUtils.map(getPatientsARTWithVLSuppressionIndicator, mappings),
        "keypop=CSW");

    dataSetDefinition.addColumn(
        "VLSUPPRI",
        "VLSUP: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsARTWithVLSuppressionIndicator, mappings),
        "keypop=PRI");

    // This returns total adult patients started ART in last 12 Months
    addRow(
        dataSetDefinition,
        "START12",
        "Total patients started ART in last 12 Months",
        EptsReportUtils.map(getPatientsStartedARTInLast12MonthsIndicator, mappings),
        getColumnsForAdults());

    dataSetDefinition.addColumn(
        "START12PID",
        "START12: People who inject drugs",
        EptsReportUtils.map(getPatientsStartedARTInLast12MonthsIndicator, mappings),
        "keypop=PID");

    dataSetDefinition.addColumn(
        "START12MSM",
        "START12: Men who have sex with men",
        EptsReportUtils.map(getPatientsStartedARTInLast12MonthsIndicator, mappings),
        "keypop=MSM");

    dataSetDefinition.addColumn(
        "START12CSW",
        "START12: Female sex workers",
        EptsReportUtils.map(getPatientsStartedARTInLast12MonthsIndicator, mappings),
        "keypop=CSW");

    dataSetDefinition.addColumn(
        "START12PRI",
        "START12: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsStartedARTInLast12MonthsIndicator, mappings),
        "keypop=PRI");

    // This returns total adult patients on ART in last 12 Months
    addRow(
        dataSetDefinition,
        "ARTLAST12",
        "Total patients on ART in last 12 Months",
        EptsReportUtils.map(getPatientsOnARTInLast12MonthsIndicator, mappings),
        getColumnsForAdults());

    dataSetDefinition.addColumn(
        "ARTLAST12PID",
        "ARTLAST12: People who inject drugs",
        EptsReportUtils.map(getPatientsOnARTInLast12MonthsIndicator, mappings),
        "keypop=PID");

    dataSetDefinition.addColumn(
        "ARTLAST12MSM",
        "ARTLAST12: Men who have sex with men",
        EptsReportUtils.map(getPatientsOnARTInLast12MonthsIndicator, mappings),
        "keypop=MSM");

    dataSetDefinition.addColumn(
        "ARTLAST12CSW",
        "ARTLAST12: Female sex workers",
        EptsReportUtils.map(getPatientsOnARTInLast12MonthsIndicator, mappings),
        "keypop=CSW");

    dataSetDefinition.addColumn(
        "ARTLAST12PRI",
        "ARTLAST12: People in prison and other closed settings",
        EptsReportUtils.map(getPatientsOnARTInLast12MonthsIndicator, mappings),
        "keypop=PRI");

    return dataSetDefinition;
  }

  private List<ColumnParameters> getColumnsForAdults() {
    ColumnParameters fifteenPlus =
        new ColumnParameters("fifteenPlus", "15 +", "age=15+", "adultos");

    return Arrays.asList(fifteenPlus);
  }
}
