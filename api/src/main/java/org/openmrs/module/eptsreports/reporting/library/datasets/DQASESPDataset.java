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
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.*;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DQASESPDataset extends BaseDataSet {

  private EptsCommonDimension eptsCommonDimension;
  private EptsGeneralIndicator eptsGeneralIndicator;
  private AgeDimensionCohortInterface ageDimensionCohortInterface;
  private ResumoMensalCohortQueries resumoMensalCohortQueries;
  private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired
  public DQASESPDataset(
      EptsCommonDimension eptsCommonDimension,
      EptsGeneralIndicator eptsGeneralIndicator,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      TxCurrCohortQueries txCurrCohortQueries,
      @Qualifier("commonAgeDimensionCohort")
          AgeDimensionCohortInterface ageDimensionCohortInterface) {
    this.eptsCommonDimension = eptsCommonDimension;
    this.ageDimensionCohortInterface = ageDimensionCohortInterface;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
  }

  public DataSetDefinition constructDQASESPDataset(boolean currentSpec) {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    String mappingM1 = "startDate=${startDate},endDate=${startDate+1m-1d},location=${location}";
    String mappingM2 = "startDate=${startDate+1m},endDate=${startDate+2m-1d},location=${location}";
    String mappingM3 = "startDate=${startDate+2m},endDate=${endDate},location=${location}";

    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohortInterface), "effectiveDate=${endDate}"));
    dsd.setName("SESP");
    dsd.addParameters(getParameters());

    addRow(
        dsd,
        "ART-SISMA",
        "Número de Activos em TARV no fim do período de revisão (SESP - SISMA)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Número de Activos em TARV no fim do período de revisão (SESP - SISMA)",
                EptsReportUtils.map(
                    resumoMensalCohortQueries.getActivePatientsInARTByEndOfCurrentMonth(false),
                    mappings)),
            mappings),
        getDisaggregations());

    addRow(
        dsd,
        "ART-DATIM",
        "Número de Activos em TARV no fim do período de revisão (SESP - DATIM)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Número de Activos em TARV no fim do período de revisão (SESP - DATIM)",
                EptsReportUtils.map(
                    txCurrCohortQueries.getTxCurrCompositionCohort(
                        "compositionCohort", currentSpec),
                    "onOrBefore=${endDate},location=${location}")),
            mappings),
        getDisaggregations());

    addRow(
        dsd,
        "ART-B1-M3",
        "Número de Novos Inícios em TARV durante o período de revisão (M3)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Número de Novos Inícios em TARV durante o período de revisão (M3)",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
                    mappingM3)),
            mappings),
        getDisaggregations());

    addRow(
        dsd,
        "ART-B1-M2",
        "Número de Novos Inícios em TARV durante o período de revisão (M2)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Número de Novos Inícios em TARV durante o período de revisão (M2)",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
                    mappingM2)),
            mappings),
        getDisaggregations());

    addRow(
        dsd,
        "ART-B1-M1",
        "Número de Novos Inícios em TARV durante o período de revisão (M1)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Número de Novos Inícios em TARV durante o período de revisão (M1)",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(),
                    mappingM1)),
            mappings),
        getDisaggregations());

    addRow(
        dsd,
        "ART-E2-M3",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!) (M3)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!) (M3)",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults(),
                    mappingM3)),
            mappings),
        disagForCargaViralM3());

    addRow(
        dsd,
        "ART-E2-M2",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!) (M2)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!) (M2)",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults(),
                    mappingM2)),
            mappings),
        disagForCargaViralM2());

    addRow(
        dsd,
        "ART-E2-M1",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!) (M1)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral (CV) durante o mês (Notificação anual!) (M1)",
                EptsReportUtils.map(
                    resumoMensalCohortQueries
                        .getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults(),
                    mappingM1)),
            mappings),
        disagForCargaViralM1());

    return dsd;
  }

  private List<ColumnParameters> getDisaggregations() {
    ColumnParameters betweenZeroAnd4Years =
        new ColumnParameters(
            "betweenZeroAnd4Years",
            "0-4",
            EptsCommonDimensionKey.of(DimensionKeyForAge.betweenZeroAnd4Years).getDimensions(),
            "01");
    ColumnParameters between5And9Years =
        new ColumnParameters(
            "between5And9Years",
            "5-9",
            EptsCommonDimensionKey.of(DimensionKeyForAge.between5And9Years).getDimensions(),
            "02");
    ColumnParameters between10And14YearsF =
        new ColumnParameters(
            "between10And14YearsF",
            "10-14 Female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between10And14Years)
                .getDimensions(),
            "03");
    ColumnParameters between10And14YearsM =
        new ColumnParameters(
            "between10And14YearsM",
            "10-14 Male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between10And14Years)
                .getDimensions(),
            "04");
    ColumnParameters between15And19YearsF =
        new ColumnParameters(
            "between15And19YearsF",
            "15-19 Female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between15And19Years)
                .getDimensions(),
            "05");
    ColumnParameters between15And19YearsM =
        new ColumnParameters(
            "between15And19YearsM",
            "15-19 Male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between15And19Years)
                .getDimensions(),
            "06");
    ColumnParameters overOrEqualTo20YearsF =
        new ColumnParameters(
            "overOrEqualTo20YearsF",
            "20+ Female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.overOrEqualTo20Years)
                .getDimensions(),
            "07");
    ColumnParameters overOrEqualTo20YearsM =
        new ColumnParameters(
            "overOrEqualTo20YearsM",
            "20+ Male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.overOrEqualTo20Years)
                .getDimensions(),
            "08");

    return Arrays.asList(
        betweenZeroAnd4Years,
        between5And9Years,
        between10And14YearsF,
        between10And14YearsM,
        between15And19YearsF,
        between15And19YearsM,
        overOrEqualTo20YearsF,
        overOrEqualTo20YearsM);
  }

  private List<ColumnParameters> disagForCargaViralM1() {
    ColumnParameters bellow15Years =
        new ColumnParameters(
            "bellow15Years",
            "<15",
            EptsCommonDimensionKey.of(DimensionKeyForAge.bellow15Years).getDimensions(),
            "01");
    ColumnParameters overOrEqualTo15Years =
        new ColumnParameters(
            "overOrEqualTo15Years",
            "15+",
            EptsCommonDimensionKey.of(DimensionKeyForAge.overOrEqualTo15Years).getDimensions(),
            "02");

    return Arrays.asList(bellow15Years, overOrEqualTo15Years);
  }

  private List<ColumnParameters> disagForCargaViralM2() {
    ColumnParameters bellow15Years =
        new ColumnParameters(
            "bellow15Years",
            "<15",
            EptsCommonDimensionKey.of(DimensionKeyForAge.bellow15Years).getDimensions(),
            "01");
    ColumnParameters overOrEqualTo15Years =
        new ColumnParameters(
            "overOrEqualTo15Years",
            "15+",
            EptsCommonDimensionKey.of(DimensionKeyForAge.overOrEqualTo15Years).getDimensions(),
            "02");

    return Arrays.asList(bellow15Years, overOrEqualTo15Years);
  }

  private List<ColumnParameters> disagForCargaViralM3() {
    ColumnParameters bellow15Years =
        new ColumnParameters(
            "bellow15Years",
            "<15",
            EptsCommonDimensionKey.of(DimensionKeyForAge.bellow15Years).getDimensions(),
            "01");
    ColumnParameters overOrEqualTo15Years =
        new ColumnParameters(
            "overOrEqualTo15Years",
            "15+",
            EptsCommonDimensionKey.of(DimensionKeyForAge.overOrEqualTo15Years).getDimensions(),
            "02");

    return Arrays.asList(bellow15Years, overOrEqualTo15Years);
  }
}
