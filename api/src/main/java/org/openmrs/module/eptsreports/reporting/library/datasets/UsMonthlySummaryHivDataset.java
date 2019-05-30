package org.openmrs.module.eptsreports.reporting.library.datasets;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.noMappings;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.UsMonthlySummaryHivCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UsMonthlySummaryHivDataset extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private UsMonthlySummaryHivCohortQueries usMonthlySummaryHivCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructUsMonthlySummaryHivDataset() {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("US Monthly Summary HIV Data Set");
    dataSetDefinition.addParameters(getParameters());

    dataSetDefinition.addDimension("gender", noMappings(eptsCommonDimension.gender()));
    dataSetDefinition.addDimension(
        "age", map(eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    addRow(
        dataSetDefinition,
        "A1",
        "Nº cumulativo de pacientes registados até o fim do mês anterior",
        getRegisteredPreArtByEndOfPreviousMonth(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "A2",
        "Nº de pacientes registados durante o mês",
        getRegisteredInPreArtByEndOfMonth(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "A3",
        "Nr cumulativo de pacientes registados nos Livros de Registo de Pré-TARV até o fim do mês",
        getRegisteredInPreArtBooksByEndOfMonth(),
        getColumnParameters());

    //    addRow(
    //        dataSetDefinition,
    //        "B1",
    //        "Nº mensal de novos inscritos",
    //        getNewlyEnrolled(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "B2",
    //        "Nº mensal de transferidos de outras US",
    //        getEnrolledByTransfer(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "C1",
    //        "Nº cumulativo de transferidos para outras US",
    //        getTransferredOut(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "C2",
    //        "Nº cumulativo de abandonos pre-tarv",
    //        getAbandoned(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "C3",
    //        "Nº cumulativo de óbitos pre-tarv",
    //        getDeceasedDuringPreArt(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "C4",
    //        "Nº cumulativo que iniciaram TARV",
    //        getInitiatedArt(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "E1",
    //        "Nº dos novos inscritos mensais no Livro de Registo Nº 1 de Pré-TARV rastreados para
    // TB",
    //        getScreenedForTb(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "E2",
    //        "Nº dos novos inscritos mensais no Livro de Registo Nº 1 de Pré-TARV rastreados para
    // ITS",
    //        getScreenedForSti(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "F1",
    //        "Nº dos novos inscritos mensais no Livro de Registo Nº 1 de Pré-TARV que iniciaram TPC
    // durante o mês",
    //        getStartedCotrimoxazoleProphylaxis(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "F2",
    //        "Nº dos novos inscritos mensais no  Livro de Registo Nº 1 de Pré-TARV que iniciaram
    // TPI durante o mês",
    //        getStartedIsoniazidProphylaxis(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "G1",
    //        "Nº cumulativo de pacientes registados até o fim do mês anterior TARV",
    //        getRegisteredInArtBooks1and2ByEndOfPreviousMonth(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "G2",
    //        "Nº de pacientes registados durante o mês TARV",
    //        getRegisteredInPArtBooks1and2DuringReportingPeriod(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "H1",
    //        "Nº mensal de novos inícios tarv",
    //        getNewlyEnrolledInArt(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "H2",
    //        "Nº mensal de transferidos de outras US tarv",
    //        getEnrolledInArtByTransfer(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "I1",
    //        "Nº cumulativo de suspensos tarv",
    //        getInArtWhoSuspendedTreatment(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "I2",
    //        "Nº cumulativo de transferidos para outras US tarv",
    //        getInArtTransferredOut(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "I3",
    //        "Nº cumulativo de abandonos tarv",
    //        getAbandonedArt(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "I4",
    //        "Nº cumulativo de óbitos tarv",
    //        getDeadDuringArt(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "K1",
    //        "Nº dos novos inícios mensais  no Livro de Registo Nº 1 de TARV rastreados para TB",
    //        getInArtScreenedForTb(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "K2",
    //        "Nº dos novos inícios mensais  no Livro de Registo Nº 1 de TARV rastreados para ITS",
    //        getInArtScreenedForSti(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "L1",
    //        "Nº dos novos inícios mensais no Livro de Registo Nº 1 de TARV que  iniciaram CTZ
    // durante o mês",
    //        getInArtStartedCotrimoxazoleProphylaxis(),
    //        getColumnParameters());
    //
    //    addRow(
    //        dataSetDefinition,
    //        "L2",
    //        "Nº dos novos inícios mensais no Livro de Registo Nº 1 de TARV que  iniciaram INH
    // durante o mês",
    //        getInArtStartedIsoniazidProphylaxis(),
    //        getColumnParameters());

    return dataSetDefinition;
  }

  //  private Mapped<CohortIndicator> getAbandonedArt() {
  //    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV QUE ABANDONARAM";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getAbandonedArt();
  //    String mappings = "onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getInArtStartedIsoniazidProphylaxis() {
  //    String name =
  //        "NUMERO DE NOVOS PACIENTES QUE INICIARAM TARV NUM PERIODO REGISTADOS NO LIVRO 1 TARV E
  // QUE INICIARAM PROFILAXIA COM INH NO MESMO PERIODO";
  //    CohortDefinition cohort =
  //        usMonthlySummaryHivCohortQueries.getInArtWhoStartedIsoniazidProphylaxis();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getInArtStartedCotrimoxazoleProphylaxis() {
  //    String name =
  //        "NUMERO DE NOVOS PACIENTES QUE INICIARAM TARV NUM PERIODO REGISTADOS NO LIVRO 1 TARV E
  // QUE INICIARAM PROFILAXIA COM CTZ NO MESMO PERIODO";
  //    CohortDefinition cohort =
  //        usMonthlySummaryHivCohortQueries.getArtWhoStartedCotrimoxazoleProphylaxis();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getInArtScreenedForSti() {
  //    String name =
  //        "NUMERO DE NOVOS PACIENTES QUE INICIARAM TARV NUM PERIODO REGISTADOS NO LIVRO 1 TARV E
  // RASTREADOS PARA ITS NO MESMO PERIODO";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtWhoScreenedForSti();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getInArtScreenedForTb() {
  //    String name =
  //        "NUMERO DE NOVOS PACIENTES QUE INICIARAM TARV NUM PERIODO REGISTADOS NO LIVRO 1 TARV E
  // RASTREADOS PARA TB NO MESMO PERIODO";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtWhoScreenedForTb();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getDeadDuringArt() {
  //    String name = "NUMERO CUMULATIVO DE PACIENTES TARV QUE OBITARAM";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getDeadDuringArt();
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name,
  // mapStraightThrough(cohort));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getInArtTransferredOut() {
  //    String name = "NUMERO CUMULATIVO DE PACIENTES TARV TRANSFERIDOS PARA";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtTransferredOut();
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name,
  // mapStraightThrough(cohort));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getInArtWhoSuspendedTreatment() {
  //    String name = "NUMERO CUMULATIVO DE PACIENTES TARV QUE SUSPENDERAM TARV";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtWhoSuspendedTreatment();
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name,
  // mapStraightThrough(cohort));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getEnrolledInArtByTransfer() {
  //    String name =
  //        "NUMERO DE PACIENTES TARV REGISTADOS NO LIVRO 1 E 2 TARV TRANSFERIDOS DE NUM PERIODO";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtEnrolledByTransfer();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getNewlyEnrolledInArt() {
  //    String name =
  //        "NUMERO DE NOVOS PACIENTES QUE INICIARAM TARV REGISTADOS NO LIVRO 1 TARV NUM PERIODO";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getNewlyEnrolledInArt();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getRegisteredInPArtBooks1and2DuringReportingPeriod() {
  //    String name = "NUMERO DE PACIENTES REGISTADOS NOS LIVROS 1 E 2 TARV NUM PERIODO";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getRegisteredInArtBooks();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getRegisteredInArtBooks1and2ByEndOfPreviousMonth() {
  //    String name =
  //        "NUMERO CUMULATIVO DE PACIENTES TARV REGISTADOS NOS LIVROS 1 E 2 ATE O FIM DE UM
  // PERIODO";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getRegisteredInArtBooks();
  //    String mappings = "onOrBefore=${startDate-1d},locationList=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getStartedIsoniazidProphylaxis() {
  //    String name =
  //        "NUMERO DE NOVOS PACIENTES REGISTADOS NO LIVRO 1 PRE-TARV NUM PERIODO E QUE INICIARAM
  // PROFILAXIA COM INH NO MESMO PERIODO";
  //    CohortDefinition cohort =
  //        usMonthlySummaryHivCohortQueries.getInPreArtWhoStartedIsoniazidProphylaxis();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getStartedCotrimoxazoleProphylaxis() {
  //    String name =
  //        "NUMERO DE NOVOS PACIENTES REGISTADOS NO LIVRO 1 PRE-TARV NUM PERIODO E QUE INICIARAM
  // PROFILAXIA COM CTZ NO MESMO PERIODO";
  //    CohortDefinition cohort =
  //        usMonthlySummaryHivCohortQueries.getInPreArtWhoStartedCotrimoxazoleProphylaxis();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getScreenedForSti() {
  //    String name =
  //        "NUMERO DE NOVOS PACIENTES REGISTADOS NO LIVRO 1 PRE-TARV NUM PERIODO E QUE FORAM
  // RASTREADOS PARA ITS NO MESMO PERIODO";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInPreArtWhoScreenedForSti();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getScreenedForTb() {
  //    String name =
  //        "NUMERO DE NOVOS PACIENTES REGISTADOS NO LIVRO 1 PRE-TARV E RASTREADOS PARA TB NUM
  // PERIODO";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInPreArtWhoScreenedForTb();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getInitiatedArt() {
  //    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV QUE INICIARAM TARV";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtCareWhoInitiatedArt();
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name,
  // mapStraightThrough(cohort));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getDeceasedDuringPreArt() {
  //    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV QUE OBITARAM";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getDeadDuringArtCare();
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name,
  // mapStraightThrough(cohort));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getAbandoned() {
  //    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV QUE ABANDONARAM";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getAbandonedArtCare();
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name,
  // mapStraightThrough(cohort));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getTransferredOut() {
  //    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV TRANSFERIDOS PARA";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getTransferredOut();
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name,
  // mapStraightThrough(cohort));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getEnrolledByTransfer() {
  //    String name =
  //        "NUMERO DE PACIENTES PRE-TARV REGISTADOS NO LIVRO 1 E 2 TRANSFERIDOS DE NUM PERIODO";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtCareEnrolledByTransfer();
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name,
  // mapStraightThrough(cohort));
  //    return mapStraightThrough(indicator);
  //  }
  //
  //  private Mapped<CohortIndicator> getNewlyEnrolled() {
  //    String name = "NUMERO DE NOVOS PACIENTES PRE-TARV REGISTADOS NO LIVRO 1 NUM PERIODO";
  //    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getNewlyEnrolledInPreArtBooks();
  //    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
  //    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
  //    return mapStraightThrough(indicator);
  //  }
  //

  private Mapped<CohortIndicator> getRegisteredInPreArtBooksByEndOfMonth() {
    String name = " NUMERO CUMULATIVO DE PACIENTES PRE-TARV REGISTADOS ATE O FIM DESTE MES";
    CohortDefinition cohort =
        usMonthlySummaryHivCohortQueries.getRegisteredInPreArtByEndOfPreviousMonth();
    String mappings = "endDate=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getRegisteredInPreArtByEndOfMonth() {
    String name = "NUMERO DE PACIENTES PRE-TARV REGISTADOS NO LIVRO 1 E 2 NUM PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getRegisteredInPreArtDuringMonth();
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, mapStraightThrough(cohort));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getRegisteredPreArtByEndOfPreviousMonth() {
    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV REGISTADOS ATE O FIM DO MES ANTERIOR";
    CohortDefinition cohort =
        usMonthlySummaryHivCohortQueries.getRegisteredInPreArtByEndOfPreviousMonth();
    String mappings = "endDate=${startDate-1d},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private List<ColumnParameters> getColumnParameters() {
    return Arrays.asList(
        new ColumnParameters("Female under 15", "Female under 15", "gender=F|age=<15", "F014"),
        new ColumnParameters("Female above 15", "Female 15 or above", "gender=F|age=15+", "F15"),
        new ColumnParameters("Male under 15", "Male under 15", "gender=M|age=<15", "M014"),
        new ColumnParameters("Male above 15", "Male 15 or above", "gender=M|age=15+", "M15"));
  }
}
