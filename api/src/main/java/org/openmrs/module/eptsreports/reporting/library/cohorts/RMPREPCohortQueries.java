package org.openmrs.module.eptsreports.reporting.library.cohorts;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.RMPREPQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class RMPREPCohortQueries {

  private HivMetadata hivMetadata;

  private PrepNewCohortQueries prepNewCohortQueries;

  @Autowired
  public RMPREPCohortQueries(HivMetadata hivMetadata, PrepNewCohortQueries prepNewCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.prepNewCohortQueries = prepNewCohortQueries;
  }

    /**
   * <b>A1: Nº de Utentes elegíveis a PrEP durante o período de reporte </b>
   *
   * <p>Select all clients who are eligible for prep during the reporting period as follows: have
   * Ficha de Consulta Inicial PrEP (encounter type 80) and encounter datetime between start date
   * and end date; and
   *
   * <p>Have “Utente Elegível aceita iniciar a PrEP” (concept id 165289) value coded ‘Sim’ (concept
   * id 1065) on the same encounter;
   *
   * @return
   */
  public CohortDefinition getClientsEligibleForPrep() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("Numero de Utentes elegíveis a PrEP durante o período de reporte");
    sqlCohortDefinition.addParameter(new Parameter("startDate", " Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", " End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        RMPREPQueries.getA1(
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getAcceptStartMedicationConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    sqlCohortDefinition.setQuery(query);
    return sqlCohortDefinition;
  }

  /**
   * <b>B2: Nº de Novos Inícios que retornaram a PrEP durante o período de reporte </b>
   *
   * <p>All clients who have “O utente está a Retomar PrEP” (Concept id 165296 from encounter type
   * 80) value coded “RESTART” (concept id 1705) and obs_datetime between start date and end date;
   * and
   *
   * <p>Exclude all clients who have been transferred in (B1-Transferred-In)
   *
   * @return
   */
  public CohortDefinition getClientsReturnedToPrep() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Numero de Novos Inícios que retornaram a PrEP durante o período de reporte");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "reinitiated",
        EptsReportUtils.map(
            getClientsWhoReinitiatedPrep(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "transferredIn",
        EptsReportUtils.map(
            prepNewCohortQueries.getClientsWhoAreTransferredIn(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString("reinitiated AND NOT transferredIn");

    return cd;
  }

  /**
   * All clients who have “O utente está a Retomar PrEP” (Concept id 165296 from encounter type 80)
   * value coded “RESTART” (concept id 1705) and obs_datetime between start date and end date;
   *
   * @return
   */
  public CohortDefinition getClientsWhoReinitiatedPrep() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(
        "Numero de Novos Inícios que retornaram a PrEP durante o período de reporte");
    sqlCohortDefinition.addParameter(new Parameter("startDate", " Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", " End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        RMPREPQueries.getB2(
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getInitialStatusPrepUserConcept().getConceptId(),
            hivMetadata.getRestartConcept().getConceptId());

    sqlCohortDefinition.setQuery(query);
    return sqlCohortDefinition;
  }

  /**
   * <b>C1: Nº de Novos Inícios que retornaram a PrEP durante o período de reporte </b>
   *
   * <p>Have “Regime da PREP prescrita (ARVs)” (concept id 165213) value coded “TDF/3TC” or
   * “TDF/FTC” or “Outro” (concept id IN [165214, 165215, 165216]) registered on Ficha de Consulta
   * Inicial PrEP (encounter type 80) and encounter datetime between the reporting period start and
   * end dates; or
   *
   * <p>Have “Repetir a Prescrição da PrEP” (concept id 165213) value coded “TDF/3TC” ou “TDF/FTC”
   * (concept id IN [165214, 165215]) registered on Ficha de Consulta de Seguimento PrEP (encounter
   * type 81) and encounter datetime between the reporting period start and end dates.
   *
   * @return
   */
  public CohortDefinition getClientsWhoReceivedPrep() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(
        "Numero de Utentes que receberam a PrEP durante o período de reporte");
    sqlCohortDefinition.addParameter(new Parameter("startDate", " Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", " End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        RMPREPQueries.getC1(
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getPrepSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPrepRegimeConcept().getConceptId(),
            hivMetadata.getTdfAnd3tcConcept().getConceptId(),
            hivMetadata.getTdfAndFtcConcept().getConceptId(),
            hivMetadata.getOtherDrugForPrepConcept().getConceptId());

    sqlCohortDefinition.setQuery(query);
    return sqlCohortDefinition;
  }

  /**
   * <b>D1: Nº de Utentes em PrEP por 3 meses consecutivos após terem iniciado a PrEP </b>
   *
   * <p>Select all patients from B1 for the previous period (start date - 1 month and end date - 1
   * month) and filter:
   *
   * <p>All clients with at least 2 pickups (concept id 165217, value numeric >= 2) registered on
   * the Ficha de Seguimento PrEP (encounter type 81) during the reporting period and encounter
   * datetime should be between date of prep initial (earliest date from B1) and date of prep
   * initial (earliest date from B1) + 33 days
   *
   * @return
   */
  public CohortDefinition getClientsFromB1PreviousPeriod() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(
        "Numero de Utentes em PrEP por 3 meses consecutivos após terem iniciado a PrEP");
    sqlCohortDefinition.addParameter(new Parameter("startDate", " Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", " End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        RMPREPQueries.getD1(
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getPrepSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getNumberOfBottlesConcept().getConceptId(),
            hivMetadata.getInitialStatusPrepUserConcept().getConceptId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getPrepStartDateConcept().getConceptId(),
            hivMetadata.getPrepProgram().getProgramId(),
            hivMetadata.getStateOfStayOnPrepProgram().getConceptId(),
            hivMetadata.getReferalTypeConcept().getConceptId(),
            hivMetadata.getTransferredFromOtherFacilityConcept().getConceptId());

    sqlCohortDefinition.setQuery(query);
    return sqlCohortDefinition;
  }
}
