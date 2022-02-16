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
import org.openmrs.module.eptsreports.reporting.library.cohorts.MisauResumoMensalPrepReportCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.PrepKeyPopulationDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.PrepPregnantBreastfeedingDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MisauResumoMensalPrepDataset extends BaseDataSet {

  @Autowired private MisauResumoMensalPrepReportCohortQueries misauResumoMensalPrepCohortQueries;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired private PrepKeyPopulationDimension prepKeyPopulationDimension;

  @Autowired private PrepPregnantBreastfeedingDimension prepPregnantBreastfeedingDimension;

  public DataSetDefinition constructDataset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("Misau Resumo Mensal Prep DataSet");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension(
        "pregnant-breastfeeding",
        EptsReportUtils.map(
            prepPregnantBreastfeedingDimension.getPregnantAndBreastfeedingDimensios(), mappings));

    final CohortIndicator indicatorA1 =
        this.eptsGeneralIndicator.getIndicator(
            "Number of Clients Who Initiated Prep For the firts Time",
            EptsReportUtils.map(
                this.misauResumoMensalPrepCohortQueries.getIndicatorA1(), mappings));

    dataSetDefinition.addColumn(
        "A1-total",
        "A1: N° de Utentes elegíveis a PrEP durante o período de reporte",
        EptsReportUtils.map(indicatorA1, mappings),
        "");

    this.addRow(
        dataSetDefinition,
        "A1",
        "Age and Gender",
        EptsReportUtils.map(indicatorA1, mappings),
        getColumnsForAgeAndGender());

    dataSetDefinition.addColumn(
        "A1-TotalMale",
        "A1 - Age and Gender (Totals male) ",
        EptsReportUtils.map(indicatorA1, mappings),
        "gender=M");
    dataSetDefinition.addColumn(
        "A1-TotalFemale",
        "A1 - Age and Gender (Totals female) ",
        EptsReportUtils.map(indicatorA1, mappings),
        "gender=F");

    dataSetDefinition.addDimension(
        "homosexual",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findPatientsWhoAreHomosexual(), mappings));

    dataSetDefinition.addDimension(
        "drug-user",
        EptsReportUtils.map(this.prepKeyPopulationDimension.findPatientsWhoUseDrugs(), mappings));

    dataSetDefinition.addDimension(
        "prisioner",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findPatientsWhoAreInPrison(), mappings));

    dataSetDefinition.addDimension(
        "sex-worker",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findPatientsWhoAreSexWorker(), mappings));

    dataSetDefinition.addDimension(
        "transgender",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findPatientsWhoAreTransGender(), mappings));

    dataSetDefinition.addColumn(
        "A1-TotalPregnant",
        "Age and Gender (Totals female) ",
        EptsReportUtils.map(indicatorA1, mappings),
        "gender=F|pregnant-breastfeeding=PREGNANT");

    dataSetDefinition.addColumn(
        "A1-TotalBreastfeeding",
        "Age and Gender (Totals female) ",
        EptsReportUtils.map(indicatorA1, mappings),
        "gender=F|pregnant-breastfeeding=BREASTFEEDING");

    return dataSetDefinition;
  }

  public void addRow(
      CohortIndicatorDataSetDefinition cohortDsd,
      String baseName,
      String baseLabel,
      Mapped<CohortIndicator> indicator,
      List<ColumnParameters> columns) {

    for (ColumnParameters column : columns) {
      String name = baseName + "-" + column.getName();
      String label = baseLabel + " (" + column.getLabel() + ")";
      cohortDsd.addColumn(name, label, indicator, column.getDimensions());
    }
  }

  private List<ColumnParameters> getColumnsForAgeAndGender() {

    ColumnParameters tenTo14M =
        new ColumnParameters("tenTo14M", "10 - 14 male", "gender=M|age=10-14", "01");
    ColumnParameters fifteenTo19M =
        new ColumnParameters("fifteenTo19M", "15 - 19 male", "gender=M|age=15-19", "02");
    ColumnParameters twentyTo24M =
        new ColumnParameters("twentyTo24M", "20 - 24 male", "gender=M|age=20-24", "03");
    ColumnParameters above25M =
        new ColumnParameters("above25M", "25+ male", "gender=M|age=25+", "04");

    ColumnParameters tenTo14F =
        new ColumnParameters("tenTo14F", "10 - 14 female", "gender=F|age=10-14", "10");
    ColumnParameters fifteenTo19F =
        new ColumnParameters("fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "11");
    ColumnParameters twentyTo24F =
        new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "12");
    ColumnParameters above25F =
        new ColumnParameters("above25F", "25+ female", "gender=F|age=25+", "13");

    return Arrays.asList(
        tenTo14M,
        fifteenTo19M,
        twentyTo24M,
        above25M,
        tenTo14F,
        fifteenTo19F,
        twentyTo24F,
        above25F);
  }
}
