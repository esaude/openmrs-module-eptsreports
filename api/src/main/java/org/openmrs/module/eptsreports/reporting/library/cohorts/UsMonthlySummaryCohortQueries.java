package org.openmrs.module.eptsreports.reporting.library.cohorts;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.UsMonthlySummaryQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UsMonthlySummaryCohortQueries {
  @Autowired private GenericCohortQueries genericCohortQueries;

  public CohortDefinition getPdfFormatAssetAtFinalDate() {
    return generatlSql(
        "PDF FORMADOS E ACTIVOS ATE UMA DATA FINAL",
        UsMonthlySummaryQueries.pdfFormatAssetAtFinalDate());
  }

  public CohortDefinition getPdfFormatAssetWithinReportingPeriod() {
    return generatlSql(
        "PDFS FORMADOS NUM DETERMINADO PERIODO",
        UsMonthlySummaryQueries.pdfFormatAssetWithinReportingPeriod());
  }

  public CohortDefinition getDisaggregatedPdfFormatAssetWithinReportingPeriod() {
    return generatlSql(
        "PDFs DESINTEGRADOS NUM PERIODO",
        UsMonthlySummaryQueries.disaggregatedPdfFormatAssetWithinReportingPeriod());
  }

  public CohortDefinition getPdfFormatAssetAtFinalDateForChildren() {
    return generatlSql(
        "PDF FORMADOS E ACTIVOS COM CRIANÃ‡A ATE UMA DATA FINAL",
        UsMonthlySummaryQueries.pdfFormatAssetAtFinalDateForChildren());
  }

  public CohortDefinition getPdfFormatAssetWithinReportingPeriodForChildren() {
    return generatlSql(
        "PDFS FORMADOS NUM DETERMINADO PERIODO CONTENDO CRIANÃ‡A",
        UsMonthlySummaryQueries.pdfFormatAssetWithinReportingPeriodForChildren());
  }

  public CohortDefinition getDisaggregatedPdfFormatAssetWithinReportingPeriodForChildren() {
    return generatlSql(
        "PDFs DESINTEGRADOS NUM PERIODO CONTENDO CRIANÃ‡A",
        UsMonthlySummaryQueries.disaggregatedPdfFormatAssetWithinReportingPeriodForChildren());
  }

  /** @see UsMonthlySummaryQueries#activePatientsToEndDate() */
  public CohortDefinition getActiveToEndDate() {
    return generatlSql(
        "activePatientsToEndDate", UsMonthlySummaryQueries.activePatientsToEndDate());
  }

  public CohortDefinition getEnrolledInReportingPeriod() {
    return generatlSql(
        "PACIENTES INSCRITOS NO PDF NUM PERIODO",
        UsMonthlySummaryQueries.enrolledInReportingPeriod());
  }

  public CohortDefinition getReturnedToPDFInReportingPeriod() {
    return generatlSql(
        "PACIENTES QUE RETORNARAM AO PDF NUM PERIODO",
        UsMonthlySummaryQueries.returnedToPDFInReportingPeriod());
  }

  /** @see UsMonthlySummaryQueries#inARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDate() */
  private CohortDefinition getInARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDate() {
    return generatlSql(
        "inARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDate",
        UsMonthlySummaryQueries.inARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDate());
  }

  /** B4PDF */
  public CohortDefinition currentlyInARTWithoutPregnancy() {
      String mappings = "endDate=${endDate},location=${location}";
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("ACTUALMENTE EM TARV ATÃ‰ UM DETERMINADO PERIODO FINAL- PDF SEM INCLUIR GRAVIDAS");

    CohortDefinition TARVACTUAL = getInARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDate();
      addEndDateAndLocationParameters(TARVACTUAL);
    CohortDefinition PDF = getActiveToEndDate();
      addEndDateAndLocationParameters(PDF);

    cd.addSearch("TARVACTUAL", EptsReportUtils.map(TARVACTUAL, mappings));
    cd.addSearch("PDF", EptsReportUtils.map(PDF, mappings));
    cd.setCompositionString("TARVACTUAL AND PDF");
      addEndDateAndLocationParameters(cd);
    return cd;
  }

  public CohortDefinition getExitFromPdfTransferredTo() {
    return generatlSql(
        "PACIENTES QUE SAIRAM DO PDF - TRANSFERIDOS PARA",
        UsMonthlySummaryQueries.exitFromPdfTransferredTo());
  }

  public CohortDefinition getComeOutOfPdfRemoved() {
    return generatlSql(
        "PACIENTES QUE SAIRAM DO PDF - RETIRADOS", UsMonthlySummaryQueries.comeOutOfPdfRemoved());
  }

  public CohortDefinition getComeOutOfPdfObitos() {
    return generatlSql(
        "PACIENTES QUE SAIRAM DO PDF - OBITOS", UsMonthlySummaryQueries.comeOutOfPdfObitos());
  }

  public CohortDefinition getComeOutOfPdfSuspension() {
    return generatlSql(
        "PACIENTES QUE SAIRAM DO PDF - SUSPENSO", UsMonthlySummaryQueries.comeOutOfPdfSuspension());
  }

  public CohortDefinition getClinicalConsultationWithinReportingPeriod() {
    return generatlSql(
        "PACIENTES PDF QUE TIVERAM CONSULTA CLINICA NUM PERIODO",
        UsMonthlySummaryQueries.clinicalConsultationWithinReportingPeriod());
  }

  private CohortDefinition generatlSql(String name, String query) {
    CohortDefinition cd = genericCohortQueries.generalSql(name, query);
    addParameters(cd);
    return cd;
  }

  private void addParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
  }

    private void addEndDateAndLocationParameters(CohortDefinition cd) {
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addParameter(new Parameter("location", "location", Location.class));
    }
}
