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
import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri4MonthsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Eri4MonthsDataset extends BaseDataSet {

  @Autowired private Eri4MonthsCohortQueries eri4MonthsCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  public DataSetDefinition constructEri4MonthsDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String cohortPeriodMappings =
        "startDate=${endDate-5m+1d},endDate=${endDate-4m},location=${location}";
    String dimensionMappings = cohortPeriodMappings + ",reportEndDate={endDate}";
    String reportPeriodMappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("ERI-4months Data Set");
    dsd.addParameters(getParameters());

    // apply disaggregations here
    dsd.addDimension(
        "state",
        EptsReportUtils.map(eptsCommonDimension.getEri4MonthsDimension(), dimensionMappings));

    // start forming the columns
    addRow(
        dsd,
        "I1",
        "All Patients retained on ART 4 months after ART initiation",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "all patients",
                EptsReportUtils.map(
                    eri4MonthsCohortQueries.getPatientsWhoInitiatedArtLessTransferIns(),
                    cohortPeriodMappings)),
            reportPeriodMappings),
        get4MonthsRetentionColumns());
    addRow(
        dsd,
        "I2",
        "Pregnant women retained on ART 4 months after ART initiation",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "pregnant women",
                EptsReportUtils.map(
                    eri4MonthsCohortQueries
                        .getPregnantWomenRetainedOnArtFor4MonthsFromArtInitiation(),
                    cohortPeriodMappings)),
            reportPeriodMappings),
        get4MonthsRetentionColumns());
    addRow(
        dsd,
        "I3",
        "Breastfeeding women retained on ART 3 months after ART initiation",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "breastfeeding women",
                EptsReportUtils.map(
                    eri4MonthsCohortQueries
                        .getBreastfeedingWomenRetainedOnArtFor4MonthsFromArtInitiation(),
                    cohortPeriodMappings)),
            reportPeriodMappings),
        get4MonthsRetentionColumns());
    addRow(
        dsd,
        "I4",
        "Children (0-14, excluding pregnant and breastfeeding women) retained on ART 3 months after ART initiation",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "children",
                EptsReportUtils.map(
                    eri4MonthsCohortQueries.getChildrenRetainedOnArtFor4MonthsFromArtInitiation(),
                    cohortPeriodMappings)),
            reportPeriodMappings),
        get4MonthsRetentionColumns());
    addRow(
        dsd,
        "I5",
        "Adults (15+, excluding pregnant and breastfeeding women)  retained on ART 3 months after ART initiation",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "adults",
                EptsReportUtils.map(
                    eri4MonthsCohortQueries.getAdultsRetaineOnArtFor4MonthsFromArtInitiation(),
                    cohortPeriodMappings)),
            reportPeriodMappings),
        get4MonthsRetentionColumns());
    return dsd;
  }

  private List<ColumnParameters> get4MonthsRetentionColumns() {
    ColumnParameters initiatedArt =
        new ColumnParameters("initiated ART", "Initiated ART", "state=IART", "01");
    ColumnParameters aliveInTreatment =
        new ColumnParameters("alive in Treatment", "Alive and In Treatment", "state=AIT", "02");
    ColumnParameters dead = new ColumnParameters("dead", "Dead", "state=DP", "03");
    ColumnParameters lostTfu =
        new ColumnParameters("ltfu", "Lost to follow up", "state=LTFU", "04");
    ColumnParameters transfers =
        new ColumnParameters("transfers", "Transferred Out", "state=TOP", "05");
    ColumnParameters stopped =
        new ColumnParameters("stopped", "Stopped treatment", "state=STP", "06");
    ColumnParameters aliveNotInTreatment =
        new ColumnParameters("Not in treatment", "Alive and NOT in treatment", "state=ANIT", "07");
    return Arrays.asList(
        initiatedArt, aliveInTreatment, dead, lostTfu, transfers, stopped, aliveNotInTreatment);
  }
}
