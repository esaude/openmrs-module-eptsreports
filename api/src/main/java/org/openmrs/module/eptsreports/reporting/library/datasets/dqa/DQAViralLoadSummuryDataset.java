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
package org.openmrs.module.eptsreports.reporting.library.datasets.dqa;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.DQACohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DQAViralLoadSummuryDataset extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;
  @Autowired private TxCurrCohortQueries txCurrCohortQueries;
  @Autowired private DQACohortQueries dQACohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructDatset() {

    final CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();

    final String mappingsTxCurr = "endDate=${endDate},location=${location}";
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.setName("DQA Data Set");
    dsd.addParameters(this.getParameters());

    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.ageDQA(ageDimensionCohort), "effectiveDate=${endDate}"));

    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    this.addRow(
        dsd,
        "DAQRM",
        "Number de Activos em TARV no fim do período de revisao (SESP - SISMA)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Number de Activos em TARV no fim do período de revisao (SESP - SISMA)",
                EptsReportUtils.map(
                    this.resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
                    mappingsTxCurr)),
            mappingsTxCurr),
        this.getDQAColumns());

    this.addRow(
        dsd,
        "DAQMER",
        "Número de Activos em TARV no fim do período de revisão (SESP - DATIM)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Number de Activos em TARV no fim do período de revisao (SESP - SISMA)",
                EptsReportUtils.map(
                    this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(), mappingsTxCurr)),
            mappingsTxCurr),
        this.getDQAColumns());

    this.addRow(
        dsd,
        "DAQRM2",
        "Número de Novos Inícios em TARV durante o período de revisão Resumo Mensal B1",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Number de Activos em TARV no fim do período de revisao (SESP - SISMA)",
                EptsReportUtils.map(
                    this.dQACohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1M3(),
                    mappings)),
            mappings),
        this.getDQAColumns());

    this.addRow(
        dsd,
        "DAQRM3",
        "Número de Novos Inícios em TARV durante o período de revisão Resumo Mensal B1",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Número de Novos Inícios em TARV durante o período de revisão Resumo Mensal B1",
                EptsReportUtils.map(
                    this.dQACohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1M2(),
                    mappings)),
            mappings),
        this.getDQAColumns());

    this.addRow(
        dsd,
        "DAQRM4",
        "Número de Novos Inícios em TARV durante o período de revisão Resumo Mensal B1",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Número de Novos Inícios em TARV durante o período de revisão Resumo Mensal B1",
                EptsReportUtils.map(
                    this.dQACohortQueries
                        .getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1M1(),
                    mappings)),
            mappings),
        this.getDQAColumns());

    dsd.addColumn(
        "DAQRM5",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral(CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral(CV) durante o mês (Notificação anual!)",
                EptsReportUtils.map(
                    dQACohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2B1M3(),
                    mappings)),
            mappings),
        "age=0-14");

    dsd.addColumn(
        "DAQRM6",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral(CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral(CV) durante o mês (Notificação anual!)",
                EptsReportUtils.map(
                    dQACohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2B1M3(),
                    mappings)),
            mappings),
        "age=15+");

    dsd.addColumn(
        "DAQRM7",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral(CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral(CV) durante o mês (Notificação anual!)",
                EptsReportUtils.map(
                    dQACohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2B1M2(),
                    mappings)),
            mappings),
        "age=0-14");

    dsd.addColumn(
        "DAQRM8",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral(CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral(CV) durante o mês (Notificação anual!)",
                EptsReportUtils.map(
                    dQACohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2B1M2(),
                    mappings)),
            mappings),
        "age=15+");

    dsd.addColumn(
        "DAQRM9",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral CV) durante o mês (Notificação anual!)",
                EptsReportUtils.map(
                    dQACohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2B1M1(),
                    mappings)),
            mappings),
        "age=0-14");

    dsd.addColumn(
        "DAQRM10",
        "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral(CV) durante o mês (Notificação anual!)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Dos activos em TARV no fim do mês, subgrupo que recebeu um resultado de Carga Viral CV) durante o mês (Notificação anual!)",
                EptsReportUtils.map(
                    dQACohortQueries
                        .findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2B1M1(),
                    mappings)),
            mappings),
        "age=15+");

    return dsd;
  }

  private List<ColumnParameters> getDQAColumns() {
    ColumnParameters dqa1 = new ColumnParameters("0-4", "0-4", "age=0-4", "01");
    ColumnParameters dqa2 = new ColumnParameters("5-9", "5-9", "age=5-9", "02");
    ColumnParameters dqa3 =
        new ColumnParameters("10-14", "10-14 years female", "gender=F|age=10-14", "03");
    ColumnParameters dqa4 =
        new ColumnParameters("10-14", "10-14 years mate", "gender=M|age=10-14", "04");
    ColumnParameters dqa5 =
        new ColumnParameters("15-19", "15-19 years female", "gender=F|age=15-19", "05");
    ColumnParameters dqa6 =
        new ColumnParameters("15-19", "15-19 years male", "gender=M|age=15-19", "06");
    ColumnParameters dqa7 =
        new ColumnParameters("20+", "20+ years female", "gender=F|age=20+", "07");
    ColumnParameters dqa8 = new ColumnParameters("20+", "20+ years male", "gender=M|age=20+", "08");
    return Arrays.asList(dqa1, dqa2, dqa3, dqa4, dqa5, dqa6, dqa7, dqa8);
  }
}
