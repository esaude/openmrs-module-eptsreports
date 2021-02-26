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
 *
 */

package org.openmrs.module.eptsreports.reporting.library.datasets.resumo;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.APSSResumoTrimestralCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet.ColumnParameters;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class APSSResumoTrimestralDataSetDefinition extends BaseDataSet {

  private EptsCommonDimension eptsCommonDimension;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private APSSResumoTrimestralCohortQueries APSSResumoTrimestralCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public APSSResumoTrimestralDataSetDefinition(
      EptsCommonDimension eptsCommonDimension,
      EptsGeneralIndicator eptsGeneralIndicator,
      APSSResumoTrimestralCohortQueries APSSResumoTrimestralCohortQueries) {
    this.eptsCommonDimension = eptsCommonDimension;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.APSSResumoTrimestralCohortQueries = APSSResumoTrimestralCohortQueries;
  }

  public DataSetDefinition constructAPSSResumoTrimestralDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("APSS Resumo Trimestral Data set");
    dsd.addParameters(getParameters());

    dsd.addDimension("gender", map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age", map(eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    // indicators

    // A1
    addRow(
        dsd,
        "A1",
        "Nº de crianças e adolescentes que receberam revelação total do diagnóstico",
        getA1(),
        getAPSSPPDisagg());

    dsd.addColumn("A1TG", "Total patients - Total Geral", getA1(), "");

    // B1
    addRow(
        dsd,
        "B1",
        "Nº de pacientes que iniciou cuidados HIV e que receberam aconselhamento Pré-TARV",
        getB1(),
        getAPSSPPDisagg());

    dsd.addColumn("B1TG", "Total patients - Total Geral", getB1(), "");

    // C1
    addRow(
        dsd,
        "C1",
        "Nº total de pacientes activos em TARV que receberam seguimento de adesão",
        getC1(),
        getAPSSPPDisagg());

    dsd.addColumn("C1TG", "Total patients - Total Geral", getC1(), "");

    // D1
    addRow(
        dsd,
        "D1",
        " Número de pacientes que iniciaram TARV (15/+ anos) nesta unidade sanitária no trimestre anterior e que receberam o pacote completo de prevenção positiva até ao período de reporte",
        getD1(),
        getAPSSPPDisagg());

    dsd.addColumn("D1TG", "Total patients - Total Geral", getD1(), "");
    // E1
    addRow(
        dsd,
        "E1",
        "E1: Número de pacientes faltosos e abandonos referidos para chamadas e/ou visitas de reintegração durante o trimestre",
        getE1(),
        getAPSSPPDisagg());

    dsd.addColumn("E1TG", "Total patients - Total Geral", getE1(), "");

    // E2
    addRow(
        dsd,
        "E2",
        "E2: Número de pacientes faltosos e abandonos referidos para chamadas, visitas de reintegração e paciente contactado durante o trimestre",
        getE2(),
        getAPSSPPDisagg());

    dsd.addColumn("E2TG", "Total patients - Total Geral", getE2(), "");

    // E3
    addRow(
        dsd,
        "E3",
        "Nº de pacientes faltosos e abandonos que retornaram a unidade sanitária ",
        getE3(),
        getAPSSPPDisagg());

    dsd.addColumn("E3TG", "Total patients - Total Geral", getE3(), "");

    return dsd;
  }

  private Mapped<CohortIndicator> getA1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Total patients of A1", mapStraightThrough(APSSResumoTrimestralCohortQueries.getA1())));
  }

  private Mapped<CohortIndicator> getB1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Total patients of B1", mapStraightThrough(APSSResumoTrimestralCohortQueries.getB1())));
  }

  private Mapped<CohortIndicator> getC1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Total patients of C1", mapStraightThrough(APSSResumoTrimestralCohortQueries.getC1())));
  }

  private Mapped<CohortIndicator> getD1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Total patients of D1", mapStraightThrough(APSSResumoTrimestralCohortQueries.getD1())));
  }

  private Mapped<CohortIndicator> getE1() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Total patients of E1", mapStraightThrough(APSSResumoTrimestralCohortQueries.getE1())));
  }

  private Mapped<CohortIndicator> getE2() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Total patients of E2", mapStraightThrough(APSSResumoTrimestralCohortQueries.getE2())));
  }

  private Mapped<CohortIndicator> getE3() {
    return mapStraightThrough(
        eptsGeneralIndicator.getIndicator(
            "Total patients of E3", mapStraightThrough(APSSResumoTrimestralCohortQueries.getE3())));
  }

  public static List<ColumnParameters> getAPSSPPDisagg() {
    ColumnParameters zeroTo14yearsMale =
        new ColumnParameters(
            "zeroTo14yearsMale", "0 to  14 years male patients", "gender=M|age=0-14", "01");
    ColumnParameters zeroTo14yearsFemale =
        new ColumnParameters(
            "zeroTo14yearsFemale", "0 to  14 years female patients", "gender=F|age=0-14", "02");
    ColumnParameters zeroTo14yearsTotal =
        new ColumnParameters("zeroTo14yearsFemale", "0 to  14 years patients", "age=0-14", "03");
    ColumnParameters fifteenYearsPlusYearsM =
        new ColumnParameters(
            "fifteenYearsPlusYearsM", "15 years plus male patients", "gender=M|age=15+", "04");
    ColumnParameters fifteenYearsPlusYearsF =
        new ColumnParameters(
            "fifteenYearsPlusYearsF", "15 years plus female patients", "gender=F|age=15+", "05");
    ColumnParameters adultsTotal =
        new ColumnParameters("adultsTotal", "Adults patients - Totals", "age=15+", "06");

    return Arrays.asList(
        zeroTo14yearsMale,
        zeroTo14yearsFemale,
        zeroTo14yearsTotal,
        fifteenYearsPlusYearsM,
        fifteenYearsPlusYearsF,
        adultsTotal);
  }
}
