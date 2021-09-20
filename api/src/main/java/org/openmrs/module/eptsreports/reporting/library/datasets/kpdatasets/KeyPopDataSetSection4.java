package org.openmrs.module.eptsreports.reporting.library.datasets.kpdatasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.KeyPopCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class KeyPopDataSetSection4 extends KeyPopAbstractDataset {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private KeyPopCohortQueries keyPopCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructDataset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    addKeyPopDementions(dataSetDefinition, mappings);

    // create cohort definition

    final CohortDefinition patientNewEnrolledInART6MonthsCoohort =
        this.keyPopCohortQueries.findPatientsWhoAreNewlyEnrolledOnArtKeyPop6MonthsCoorte();

    final CohortDefinition patientActiveOnArt6MonthsCoohort =
        this.keyPopCohortQueries.findPatientsWhoActiveOnArtKeyPop6MonthsCoorte();

    final CohortDefinition patientActiveOnArt6MonthsCoohortWithVLResult =
        this.keyPopCohortQueries.findPatientsWhoActiveOnArtKeyPop6MonthsCoorteWithViralLoadResult();

    final CohortDefinition patientActiveOnArt6MonthsCoohortWithVLResultLessThan1000 =
        this.keyPopCohortQueries
            .findPatientsWhoActiveOnArtKeyPop6MonthsCoorteWithViralLoadResultLessThan1000();

    // create cohort indicator
    final CohortIndicator patientEnrolledInART6MonthsCoohortIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientNewEnrolledInART6MonthsCoohort, mappings));

    final CohortIndicator patientActiveOnArt6MonthsCoohortIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientActiveOnArt6MonthsCoohort, mappings));

    final CohortIndicator patientActiveOnArt6MonthsCoohortIndicatorWithVLResult =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientActiveOnArt6MonthsCoohortWithVLResult, mappings));

    final CohortIndicator patientActiveOnArt6MonthsCoohortIndicatorWithVLResultLessThan1000 =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(
                patientActiveOnArt6MonthsCoohortWithVLResultLessThan1000, mappings));

    dataSetDefinition.addColumn(
        "I15T",
        "I7T:Número de População Chave (KP) no Grupo de MTS, recém-iniciados em terapia antirretroviral há 6 meses – Coorte 6 meses",
        EptsReportUtils.map(patientEnrolledInART6MonthsCoohortIndicator, mappings),
        "age=15+|sex-worker=sex-worker");

    addRow(
        dataSetDefinition,
        "KPI15",
        "Número de População Chave (KP) no Grupo de MTS, recém-iniciados em terapia antirretroviral há 6 meses – Coorte 6 meses",
        EptsReportUtils.map(patientEnrolledInART6MonthsCoohortIndicator, mappings),
        getKeyPopColumns());

    dataSetDefinition.addColumn(
        "I16T",
        "I16T:Número de População Chave (KP) no Grupo de MTS, actualmente recebendo terapia antirretroviral (TARV) – Coorte 6 meses",
        EptsReportUtils.map(patientActiveOnArt6MonthsCoohortIndicator, mappings),
        "age=15+|sex-worker=sex-worker");

    addRow(
        dataSetDefinition,
        "KPI16",
        "Número de População Chave (KP) no Grupo de MTS, actualmente recebendo terapia antirretroviral (TARV) – Coorte 6 meses",
        EptsReportUtils.map(patientActiveOnArt6MonthsCoohortIndicator, mappings),
        getKeyPopColumns());

    dataSetDefinition.addColumn(
        "I17T",
        "I17T:Número adultos activos em TARV, no Grupo de MTS, que receberam um resultado de Carga Viral – Coorte 6 meses (Notificação anual!)",
        EptsReportUtils.map(patientActiveOnArt6MonthsCoohortIndicatorWithVLResult, mappings),
        "age=15+|sex-worker=sex-worker");

    addRow(
        dataSetDefinition,
        "KPI17",
        "Número adultos activos em TARV, no Grupo de MTS, que receberam um resultado de Carga Viral – Coorte 6 meses (Notificação anual!)",
        EptsReportUtils.map(patientActiveOnArt6MonthsCoohortIndicatorWithVLResult, mappings),
        getKeyPopColumns());

    dataSetDefinition.addColumn(
        "I18T",
        "I18T:Número de adultos activos em TARV, no Grupo de MTS, que receberam um "
            + "resultado de Carga Viral com supressão viral (<1000 cópias/ml) – Coorte de 6 meses (Notificação anual!)",
        EptsReportUtils.map(
            patientActiveOnArt6MonthsCoohortIndicatorWithVLResultLessThan1000, mappings),
        "age=15+|sex-worker=sex-worker");

    addRow(
        dataSetDefinition,
        "KPI18",
        "Número de adultos activos em TARV, no Grupo de MTS, que receberam um "
            + "resultado de Carga Viral com supressão viral (<1000 cópias/ml) – Coorte de 6 meses (Notificação anual!)",
        EptsReportUtils.map(
            patientActiveOnArt6MonthsCoohortIndicatorWithVLResultLessThan1000, mappings),
        getKeyPopColumns());

    return dataSetDefinition;
  }

  private List<ColumnParameters> getKeyPopColumns() {

    ColumnParameters k1 =
        new ColumnParameters("15-19", "15-19", "sex-worker=sex-worker|age=15-19", "01");
    ColumnParameters k2 =
        new ColumnParameters("20-24", "20-24", "sex-worker=sex-worker|age=20-24", "02");
    ColumnParameters k3 = new ColumnParameters("25+", "25+", "sex-worker=sex-worker|age=25+", "03");

    ColumnParameters k4 =
        new ColumnParameters(
            "15-19", "15-19", "sex-worker=sex-worker|drug-user=drug-user|age=15-19", "04");
    ColumnParameters k5 =
        new ColumnParameters(
            "20-24", "20-24", "sex-worker=sex-worker|drug-user=drug-user|age=20-24", "05");
    ColumnParameters k6 =
        new ColumnParameters(
            "25+", "25+", "sex-worker=sex-worker|drug-user=drug-user|age=25+", "06");

    ColumnParameters k7 =
        new ColumnParameters(
            "15-19", "15-19", "sex-worker=sex-worker|prisioner=prisioner|age=15-19", "07");
    ColumnParameters k8 =
        new ColumnParameters(
            "20-24", "20-24", "sex-worker=sex-worker|prisioner=prisioner|age=20-24", "08");
    ColumnParameters k9 =
        new ColumnParameters(
            "25+", "25+", "sex-worker=sex-worker|prisioner=prisioner|age=25+", "09");

    return Arrays.asList(k1, k2, k3, k4, k5, k6, k7, k8, k9);
  }
}
