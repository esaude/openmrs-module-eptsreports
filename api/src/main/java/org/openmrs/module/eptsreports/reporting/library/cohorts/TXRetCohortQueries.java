package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.TXRetQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXRetCohortQueries {

  @Autowired private GenericCohortQueries genericCohortQueries;

  private String mappings =
      "startDate=${startDate},endDate=${endDate},location=${location},months=${months}";

  private void addParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Data Inicial", Date.class));
    cd.addParameter(new Parameter("endDate", "Data Final", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("months", "Número de Meses (12, 24, 36)", Integer.class));
  }

  private CohortDefinition cohortDefinition(CohortDefinition cohortDefinition) {
    addParameters(cohortDefinition);
    return cohortDefinition;
  }

  private CohortDefinition obitoTwelveMonths() {
    return cohortDefinition(
        genericCohortQueries.generalSql("obito", TXRetQueries.obitoTwelveMonths()));
  }

  private CohortDefinition suspensoTwelveMonths() {
    return cohortDefinition(
        genericCohortQueries.generalSql("suspenso", TXRetQueries.suspensoTwelveMonths()));
  }

  private CohortDefinition initiotArvTwelveMonths() {
    return cohortDefinition(
        genericCohortQueries.generalSql("initiotArv", TXRetQueries.initiotArvTwelveMonths()));
  }

  private CohortDefinition abandonoTwelveMonths() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("abandono");
    cd.addSearch(
        "NOTIFICADO",
        EptsReportUtils.map(
            cohortDefinition(
                genericCohortQueries.generalSql(
                    "notificado", TXRetQueries.notificadoTwelveMonths())),
            mappings));
    cd.addSearch(
        "NAONOTIFICADO",
        EptsReportUtils.map(
            cohortDefinition(
                genericCohortQueries.generalSql(
                    "naonotificado", TXRetQueries.naonotificadoTwelveMonths())),
            mappings));
    cd.setCompositionString("NOTIFICADO OR NAONOTIFICADO");
    addParameters(cd);
    return cd;
  }

  /** numerator */
  public CohortDefinition inCourtForTwelveMonths() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("inCourt12Months");
    cd.addSearch("OBITO", EptsReportUtils.map(cohortDefinition(obitoTwelveMonths()), mappings));
    cd.addSearch(
        "SUSPENSO", EptsReportUtils.map(cohortDefinition(suspensoTwelveMonths()), mappings));
    cd.addSearch(
        "INICIOTARV", EptsReportUtils.map(cohortDefinition(initiotArvTwelveMonths()), mappings));
    cd.addSearch(
        "ABANDONO", EptsReportUtils.map(cohortDefinition(abandonoTwelveMonths()), mappings));

    cd.setCompositionString("INICIOTARV NOT (OBITO OR SUSPENSO OR ABANDONO)");
    addParameters(cd);
    return cd;
  }

  /** denominator */
  public CohortDefinition courtNotTransferredTwelveMonths() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS PARA (SQL)",
            TXRetQueries.courtNotTransferredTwelveMonths()));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition under1YearIncreasedHARTAtARTStartDate() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "under1YearIncreasedHARTAtARTStartDate",
            TXRetQueries.under1YearIncreasedHARTAtARTStartDate()));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition oneTo19WhoStartedTargetAtARTInitiation() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "oneTo19WhoStartedTargetAtARTInitiation",
            TXRetQueries.oneTo19WhoStartedTargetAtARTInitiation()));
  }
}
