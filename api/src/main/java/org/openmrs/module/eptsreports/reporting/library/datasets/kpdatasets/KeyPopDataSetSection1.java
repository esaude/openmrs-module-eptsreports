package org.openmrs.module.eptsreports.reporting.library.datasets.kpdatasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.KeyPopCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
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
public class KeyPopDataSetSection1 extends KeyPopAbstractDataset {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private KeyPopCohortQueries keyPopCohortQueries;

  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructDataset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    // create cohort definition

    final CohortDefinition patientEnrolledInART =
        this.keyPopCohortQueries.findPatientsWhoAreNewlyEnrolledOnArtKeyPop();

    final CohortDefinition patientCurrentlyEnrolledOnArt =
        this.keyPopCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtIncludingTransferedIn();

    final CohortDefinition patientCurrentlyEnrolledOnArtHaveVLRequest =
        this.resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2();

    final CohortDefinition patientCurrentlyEnrolledOnArtHaveVLResult =
        this.resumoMensalCohortQueries.findPatientWithVlResulLessThan1000E3();

    final CohortDefinition patientWhoAreNewOnART12MonthsCohort =
        this.keyPopCohortQueries
            .findPatientsWhoAreNewlyEnrolledOnArtKeyPopPreviousPeriodCoorte12Months();

    final CohortDefinition patientWhoAreCurrentlyOnART12MonthsCohort =
        this.keyPopCohortQueries
            .findPatientsWhoAreCurrentlyEnrolledOnArtPreviousPeriodCoorte12Months();

    // create cohort indicator
    final CohortIndicator patientEnrolledInHIVStartedARTIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientEnrolledInART, mappings));

    final CohortIndicator patientCurrentlyEnrolledOnArtIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientCurrentlyEnrolledOnArt, mappings));

    final CohortIndicator patientCurrentlyEnrolledOnArtHaveVLRequestIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientCurrentlyEnrolledOnArtHaveVLRequest, mappings));

    final CohortIndicator patientCurrentlyEnrolledOnArtHaveVLResultIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientCurrentlyEnrolledOnArtHaveVLResult, mappings));

    final CohortIndicator patientWhoAreNewOnART12MonthsCohortIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientWhoAreNewOnART12MonthsCohort, mappings));

    final CohortIndicator patientWhoAreCurrentlyOnART12MonthsCohortIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientWhoAreCurrentlyOnART12MonthsCohort, mappings));

    dataSetDefinition.addColumn(
        "I1T",
        "I1T: New on ART",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "age=15+");

    addRow(
        dataSetDefinition,
        "KPI1",
        "Número adultos que iniciaram TARV durante o trimestre",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        this.getKeyPopColumns());

    dataSetDefinition.addColumn(
        "I2T",
        "I2T:Número adultos actualmente em TARV no fim do trimestre",
        EptsReportUtils.map(patientCurrentlyEnrolledOnArtIndicator, mappings),
        "age=15+");

    addRow(
        dataSetDefinition,
        "KPI2",
        "Número adultos actualmente em TARV no fim do trimestre",
        EptsReportUtils.map(patientCurrentlyEnrolledOnArtIndicator, mappings),
        this.getKeyPopColumns());

    dataSetDefinition.addColumn(
        "I3T",
        "I3T:Dos activos em TARV no fim do trimestre, subgrupo que recebeu um teste de Carga Viral "
            + "(CV)  durante o trimestre (Notificação anual!)",
        EptsReportUtils.map(patientCurrentlyEnrolledOnArtHaveVLRequestIndicator, mappings),
        "age=15+");

    addRow(
        dataSetDefinition,
        "KPI3",
        "Dos activos em TARV no fim do trimestre, subgrupo que recebeu um teste de Carga Viral "
            + "(CV)  durante o trimestre (Notificação anual!)",
        EptsReportUtils.map(patientCurrentlyEnrolledOnArtHaveVLRequestIndicator, mappings),
        this.getKeyPopColumns());

    dataSetDefinition.addColumn(
        "I4T",
        "I4T:Dos activos TARV no fim do trimestre, subgrupo que recebeu resultado de "
            + "CV com supressão virológica durante o trimestre  (<1000 cópias/mL) (Notificação anual!)",
        EptsReportUtils.map(patientCurrentlyEnrolledOnArtHaveVLResultIndicator, mappings),
        "age=15+");

    addRow(
        dataSetDefinition,
        "KPI4",
        "KPI3:Dos activos TARV no fim do trimestre, subgrupo que recebeu resultado de "
            + "CV com supressão virológica durante o trimestre  (<1000 cópias/mL) (Notificação anual!)",
        EptsReportUtils.map(patientCurrentlyEnrolledOnArtHaveVLResultIndicator, mappings),
        this.getKeyPopColumns());

    dataSetDefinition.addColumn(
        "I5T",
        "I5T:Número de adultos na coorte 12 meses - inicio de TARV",
        EptsReportUtils.map(patientWhoAreNewOnART12MonthsCohortIndicator, mappings),
        "age=15+");

    addRow(
        dataSetDefinition,
        "KPI5",
        "KPI5:Número de adultos na coorte 12 meses - inicio de TARV ",
        EptsReportUtils.map(patientWhoAreNewOnART12MonthsCohortIndicator, mappings),
        this.getKeyPopColumns());

    dataSetDefinition.addColumn(
        "I6T",
        "I6T:Número de adultos na coorte 12 meses - Activos em TARV ",
        EptsReportUtils.map(patientWhoAreCurrentlyOnART12MonthsCohortIndicator, mappings),
        "age=15+");

    addRow(
        dataSetDefinition,
        "KPI6",
        "KPI6:Número de adultos na coorte 12 meses - Activos em TARV ",
        EptsReportUtils.map(patientWhoAreCurrentlyOnART12MonthsCohortIndicator, mappings),
        this.getKeyPopColumns());

    return dataSetDefinition;
  }

  private List<ColumnParameters> getKeyPopColumns() {

    ColumnParameters k1 =
        new ColumnParameters("15-19", "15-19", "drug-user=drug-user|age=15-19", "01");
    ColumnParameters k2 =
        new ColumnParameters("20-24", "20-24", "drug-user=drug-user|age=20-24", "02");
    ColumnParameters k3 = new ColumnParameters("25+", "25+", "drug-user=drug-user|age=25+", "03");

    ColumnParameters k4 =
        new ColumnParameters("15-19", "15-19", "homosexual=homosexual|age=15-19", "04");
    ColumnParameters k5 =
        new ColumnParameters("20-24", "20-24", "homosexual=homosexual|age=20-24", "05");
    ColumnParameters k6 = new ColumnParameters("25+", "25+", "homosexual=homosexual|age=25+", "06");

    ColumnParameters k7 =
        new ColumnParameters("15-19", "15-19", "sex-worker=sex-worker|age=15-19", "07");
    ColumnParameters k8 =
        new ColumnParameters("20-24", "20-24", "sex-worker=sex-worker|age=20-24", "08");
    ColumnParameters k9 = new ColumnParameters("25+", "25+", "sex-worker=sex-worker|age=25+", "09");

    ColumnParameters k10 =
        new ColumnParameters("15-19", "15-19", "prisioner=prisioner|age=15-19", "010");
    ColumnParameters k11 =
        new ColumnParameters("20-24", "20-24", "prisioner=prisioner|age=20-24", "011");
    ColumnParameters k12 = new ColumnParameters("25+", "25+", "prisioner=prisioner|age=25+", "012");

    return Arrays.asList(k1, k2, k3, k4, k5, k6, k7, k8, k9, k10, k11, k12);
  }
}
