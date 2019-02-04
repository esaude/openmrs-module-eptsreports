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
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
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

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  public DataSetDefinition constructEri4MonthsDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("ERI-4months Data Set");
    dsd.addParameters(getParameters());

    // apply disagregations here
    dsd.addDimension(
        "state", EptsReportUtils.map(eptsCommonDimension.getEri4MonthsDimension(), mappings));
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    // start forming the columns
    addRow(
        dsd,
        "All",
        "Total patients retained on ART for 4 months",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "all patients",
                EptsReportUtils.map(
                    eri4MonthsCohortQueries.getPatientsRetainedOnArtFor4MonthsFromArtInitiation(),
                    mappings)),
            mappings),
        genderColumns());
    dsd.addColumn(
        "Pregnant Women",
        "Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregnant Women",
                EptsReportUtils.map(
                    txNewCohortQueries.getPatientsPregnantEnrolledOnART(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "PW",
        "Total women retained on ART for 4 months who are pregnant ",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "all patients",
                EptsReportUtils.map(
                    eri4MonthsCohortQueries
                        .getPregnantWomenRetainedOnArtFor4MonthsFromArtInitiation(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "BW",
        "Total women retained on ART for 4 months who are breastfeeding ",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "all patients",
                EptsReportUtils.map(
                    eri4MonthsCohortQueries
                        .getPregnantWomenRetainedOnArtFor4MonthsFromArtInitiation(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "C",
        "Total children retained on ART for 4 months",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "all patients",
                EptsReportUtils.map(
                    eri4MonthsCohortQueries.getChildrenRetainedOnArtFor4MonthsFromArtInitiation(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "A",
        "Total Adults retained on ART for 4 months",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "all patients",
                EptsReportUtils.map(
                    eri4MonthsCohortQueries.getAdultsRetaineOnArtFor4MonthsFromArtInitiation(),
                    mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "I1",
        "All Patients retained on ART 4 months after ART initiation",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "all patients",
                EptsReportUtils.map(
                    eri4MonthsCohortQueries.getPatientsRetainedOnArtFor4MonthsFromArtInitiation(),
                    mappings)),
            mappings),
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
                    mappings)),
            mappings),
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
                    mappings)),
            mappings),
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
                    mappings)),
            mappings),
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
                    mappings)),
            mappings),
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
    return Arrays.asList(initiatedArt, aliveInTreatment, dead, lostTfu, transfers, stopped);
  }

  private List<ColumnParameters> genderColumns() {
    ColumnParameters male = new ColumnParameters("male", "Male", "gender=M", "01");
    ColumnParameters female = new ColumnParameters("female", "Female", "gender=F", "02");
    ColumnParameters femaleAdults =
        new ColumnParameters("femaleAdults", "Female adults", "gender=F|state=adults", "03");
    ColumnParameters total = new ColumnParameters("total", "Total", "", "04");
    return Arrays.asList(male, female, femaleAdults, total);
  }
}
