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

    dataSetDefinition.addDimension(
        "serodiscordants",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findPatientsWhoAreSeroDiscordants(), mappings));

    dataSetDefinition.addDimension(
        "trucker",
        EptsReportUtils.map(this.prepKeyPopulationDimension.findPatientsWhoAreTrucker(), mappings));

    dataSetDefinition.addDimension(
        "miner",
        EptsReportUtils.map(this.prepKeyPopulationDimension.findPatientsWhoAreMiner(), mappings));

    dataSetDefinition.addDimension(
        "military",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findPatientsWhoAreMilitary(), mappings));

    dataSetDefinition.addDimension(
        "breastfeeding",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension
                .findPatientsWhoAreBreastfeedingAgeGreaterOrEqualThan15(),
            mappings));

    dataSetDefinition.addDimension(
        "pregnants",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findPatientsWhoArePregnantAgeGreaterOrEqualThan15(),
            mappings));

    dataSetDefinition.addDimension(
        "youths",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findAdolescentsAndYouthsPatientsInRisk(), mappings));

    final CohortIndicator indicatorA1 =
        this.eptsGeneralIndicator.getIndicator(
            "Number of Clients Who Initiated Prep For the firts Time",
            EptsReportUtils.map(
                this.misauResumoMensalPrepCohortQueries.getIndicatorA1(), mappings));

    final CohortIndicator indicatorB1 =
        this.eptsGeneralIndicator.getIndicator(
            "N° de Utentes que iniciaram a PrEP pela 1ª vez durante o período de reporte",
            EptsReportUtils.map(
                this.misauResumoMensalPrepCohortQueries.getIndicatorB1(), mappings));

    final CohortIndicator indicatorB2 =
        this.eptsGeneralIndicator.getIndicator(
            "N° de Utentes que iniciaram a PrEP pela 1ª vez durante o período de reporte",
            EptsReportUtils.map(
                this.misauResumoMensalPrepCohortQueries.getIndicatorB2(), mappings));

    final CohortIndicator indicatorC1 =
        this.eptsGeneralIndicator.getIndicator(
            "N° de Utentes que receberam a PrEP durante o período de reporte",
            EptsReportUtils.map(
                this.misauResumoMensalPrepCohortQueries.getIndicatorC1(), mappings));

    final CohortIndicator indicatorD1 =
        this.eptsGeneralIndicator.getIndicator(
            "N° de Utentes em PrEP  por 3 meses consecutivos após terem iniciado a PrEP",
            EptsReportUtils.map(
                this.misauResumoMensalPrepCohortQueries.getIndicatorD1(), mappings));

    this.addRow(
        dataSetDefinition,
        "A1",
        "N° de Utentes elegíveis a PrEP durante o período de reporte",
        EptsReportUtils.map(indicatorA1, mappings),
        getColumnsForAgeAndGender(),
        mappings);

    this.addRow(
        dataSetDefinition,
        "B1",
        "N° de Utentes que iniciaram a PrEP pela 1ª vez durante o período de reporte",
        EptsReportUtils.map(indicatorB1, mappings),
        getColumnsForAgeAndGender(),
        mappings);

    this.addRow(
        dataSetDefinition,
        "B2",
        "N° de Novos inícios que retornaram a PrEP durante  o período de reporte",
        EptsReportUtils.map(indicatorB2, mappings),
        getColumnsForAgeAndGender(),
        mappings);

    this.addRow(
        dataSetDefinition,
        "C1",
        "N° de Utentes que receberam a PrEP durante o período de reporte",
        EptsReportUtils.map(indicatorC1, mappings),
        getColumnsForAgeAndGender(),
        mappings);

    this.addRow(
        dataSetDefinition,
        "D1",
        "N° de Utentes em PrEP  por 3 meses consecutivos após terem iniciado a PrEP",
        EptsReportUtils.map(indicatorD1, mappings),
        getColumnsForAgeAndGender(),
        mappings);

    return dataSetDefinition;
  }

  public void addRow(
      CohortIndicatorDataSetDefinition dataSetDefinition,
      String indicatorPrefix,
      String baseLabel,
      Mapped<CohortIndicator> mappedIndicator,
      List<ColumnParameters> columns,
      String mappings) {

    dataSetDefinition.addColumn(
        indicatorPrefix + "-total", indicatorPrefix + ": " + baseLabel, mappedIndicator, "");

    dataSetDefinition.addColumn(
        indicatorPrefix + "-TotalMale",
        indicatorPrefix + " - Age and Gender (Totals male) ",
        mappedIndicator,
        "gender=M");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-TotalFemale",
        indicatorPrefix + " - Age and Gender (Totals female) ",
        mappedIndicator,
        "gender=F");

    for (ColumnParameters column : columns) {
      String name = indicatorPrefix + "-" + column.getName();
      String label = baseLabel + " (" + column.getLabel() + ")";
      dataSetDefinition.addColumn(name, label, mappedIndicator, column.getDimensions());
    }

    this.addPregnantBreastFeedingColumns(
        dataSetDefinition, mappedIndicator, indicatorPrefix, mappings);
    this.addKeyPopColumns(dataSetDefinition, mappedIndicator, indicatorPrefix, mappings);
  }

  private void addPregnantBreastFeedingColumns(
      CohortIndicatorDataSetDefinition dataSetDefinition,
      Mapped<CohortIndicator> mappedIndicator,
      String indicatorPrefix,
      String mappings) {

    // Pregnant
    dataSetDefinition.addColumn(
        indicatorPrefix + "-pregnant-tenTo14",
        indicatorPrefix + " - Pregnant 10 - 14",
        mappedIndicator,
        "gender=F|age=10-14|pregnant-breastfeeding=PREGNANT");

    dataSetDefinition.addColumn(
        indicatorPrefix + "-pregnant-above15",
        indicatorPrefix + " - Pregnant 15+ ",
        mappedIndicator,
        "gender=F|age=15+|pregnant-breastfeeding=PREGNANT");

    dataSetDefinition.addColumn(
        indicatorPrefix + "-TotalPregnant",
        indicatorPrefix + " - Totals Pregnant",
        mappedIndicator,
        "gender=F|pregnant-breastfeeding=PREGNANT");

    // Breastfeeding
    dataSetDefinition.addColumn(
        indicatorPrefix + "-breastfeeding-tenTo14",
        indicatorPrefix + " - Breastfeeding 10 - 14",
        mappedIndicator,
        "gender=F|age=10-14|pregnant-breastfeeding=BREASTFEEDING");

    dataSetDefinition.addColumn(
        indicatorPrefix + "-breastfeeding-above15",
        indicatorPrefix + "- Breastfeeding 15+",
        mappedIndicator,
        "gender=F|age=15+|pregnant-breastfeeding=BREASTFEEDING");

    dataSetDefinition.addColumn(
        indicatorPrefix + "-TotalBreastfeeding",
        indicatorPrefix + "- Total Breastfeeding ",
        mappedIndicator,
        "gender=F|pregnant-breastfeeding=BREASTFEEDING");
  }

  private void addKeyPopColumns(
      CohortIndicatorDataSetDefinition dataSetDefinition,
      Mapped<CohortIndicator> mappedIndicator,
      String indicatorPrefix,
      String mappings) {
    dataSetDefinition.addColumn(
        indicatorPrefix + "-homosexual",
        indicatorPrefix + " - Homens que Fazem Sexo com Homens ",
        mappedIndicator,
        "homosexual=homosexual");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-drug-user",
        indicatorPrefix + " - Pessoas que Injectam Drogas",
        mappedIndicator,
        "drug-user=drug-user");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-sex-worker",
        indicatorPrefix + " - Trabalhador(a) de Sexo",
        mappedIndicator,
        "sex-worker=sex-worker");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-prisioner",
        indicatorPrefix + " - Reclusos",
        mappedIndicator,
        "prisioner=prisioner");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-transgender",
        indicatorPrefix + " - Transgéneros",
        mappedIndicator,
        "transgender=transgender");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-serodiscordants",
        indicatorPrefix + " - Casais serodiscordantes ",
        mappedIndicator,
        "serodiscordants=serodiscordants");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-trucker",
        indicatorPrefix + " - Camionista de Longo Curso",
        mappedIndicator,
        "trucker=trucker");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-miner", indicatorPrefix + " - Mineiro", mappedIndicator, "miner=miner");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-military",
        indicatorPrefix + " - Militar",
        mappedIndicator,
        "military=military");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-breastfeeding",
        indicatorPrefix + " - Mulheres Lactantes 15+ em Risco",
        mappedIndicator,
        "gender=F|age=15+|breastfeeding=breastfeeding");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-pregnants",
        indicatorPrefix + " - Mulheres Grávidas 15+ em Risco",
        mappedIndicator,
        "gender=F|age=15+|pregnants=pregnants");
    dataSetDefinition.addColumn(
        indicatorPrefix + "-youths",
        indicatorPrefix + " - Adolescentes e Jovens em Risco",
        mappedIndicator,
        "youths=youths");
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
