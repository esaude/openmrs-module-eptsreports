package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.PrepCtQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrepCtCohortQueries {

  private HivMetadata hivMetadata;
  private PrepNewCohortQueries prepNewCohortQueries;
  private CommonMetadata commonMetadata;

  @Autowired
  public PrepCtCohortQueries(
      HivMetadata hivMetadata,
      PrepNewCohortQueries prepNewCohortQueries,
      CommonMetadata commonMetadata) {
    this.hivMetadata = hivMetadata;
    this.prepNewCohortQueries = prepNewCohortQueries;
    this.commonMetadata = commonMetadata;
  }

  public CohortDefinition getPREPCTNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients on Prep CT Numerator");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            getPatientsListInitiatedOnPREP(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "B",
        EptsReportUtils.map(
            getPatientsTransferredInFromAnotherHFByEndOfPreviousReportingPeriod(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            getPatientsTransferredInFromAnotherHFDuringReportingPeriod(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            getPatientsReInitiatedToPrEP(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            getPatientsOnContinuedPrEP(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "F",
        EptsReportUtils.map(
            prepNewCohortQueries.getClientsWhoNewlyInitiatedPrep(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(A or B or C or D or E) AND NOT F");

    return cd;
  }

  public CohortDefinition getPatientsTransferredInFromAnotherHFByEndOfPreviousReportingPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients with Negative test Results on PrEP during the reporting period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "1",
        EptsReportUtils.map(
            getPatientsTransferredInFromAnotherHFByEndOfPreviousReportingPeriod1(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "2",
        EptsReportUtils.map(
            getPatientsTransferredInFromAnotherHFByEndOfPreviousReportingPeriod2(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("1 OR 2");

    return cd;
  }

  /**
   * <b>Description:</b> Number of patients who initiated PREP by end of previous reporting period
   * date as follows: All clients who have “O utente esta iniciar pela 1a vez a PrEP Data” (Concept
   * id 165296 from encounter type 80) and value coded equal to “Start drugs” (concept id 1256) and
   * value_datetime < start date;
   *
   * @return
   */
  public CohortDefinition getPatientsListInitiatedOnPREP() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Patients initiated to PREP by end of previous reporting period");

    definition.setQuery(
        PrepCtQueries.getPatientsListInitiatedOnPREP(
            hivMetadata.getInitialStatusPrepUserConcept().getConceptId(),
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getPrepStartDateConcept().getConceptId(),
            hivMetadata.getPrepSeguimentoEncounterType().getEncounterTypeId()));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  /**
   * <b>Description:</b> Number of patients who were transferred-in from another HF by end of the
   * previous reporting period: All clients who are enrolled in PrEP Program (PREP) (program id 25)
   * and have the first historical state as “Transferido de outra US” (patient state id= 76) in the
   * client chart by start of the reporting period;
   *
   * @return
   */
  public CohortDefinition getPatientsTransferredInFromAnotherHFByEndOfPreviousReportingPeriod1() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName(
        "Patients who were transferred-in from another HF by end of the previous reporting period part 1");

    definition.setQuery(
        PrepCtQueries.getPatientsTransferredInFromAnotherHFByEndOfPreviousReportingPeriod1(
            hivMetadata.getPrepProgram().getProgramId(),
            hivMetadata.getStateOfStayOnPrepProgram().getConceptId()));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  /**
   * <b>Description:</b> Number of patients who were transferred-in from another HF by end of the
   * previous reporting period: or All clients who have marked “Transferido de outra US”(concept id
   * 1594 value coded 1369) in the first Ficha de Consulta Inicial PrEP(encounter type 80,
   * Min(encounter datetime)) registered in the system by the start of the reporting period.
   *
   * @return
   */
  public CohortDefinition getPatientsTransferredInFromAnotherHFByEndOfPreviousReportingPeriod2() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName(
        "Patients who were transferred-in from another HF by end of the previous reporting period part 2");

    definition.setQuery(
        PrepCtQueries.getPatientsTransferredInFromAnotherHFByEndOfPreviousReportingPeriod2(
            hivMetadata.getReferalTypeConcept().getConceptId(),
            hivMetadata.getTransferredFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId()));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  /**
   * <b>Description:</b> Number of patients who Transferred in during the reporting period: All
   * clients who are enrolled in PrEP Program (PREP)(program id 25) and have the first historical
   * state as “Transferido de outra US” (patient state id= 76) in the client chart during the
   * reporting period; or All clients who have marked “Transferido de outra US”(concept id 1594
   * value coded 1369) in the first Ficha de Consulta Inicial PrEP(encounter type 80, Min(encounter
   * datetime)) registered in the system during the reporting period.
   *
   * @return
   */
  public CohortDefinition getPatientsTransferredInFromAnotherHFDuringReportingPeriod() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Patients who Transferred in during the reporting period");

    definition.setQuery(
        PrepCtQueries.getPatientsTransferredInFromAnotherHFDuringReportingPeriod(
            hivMetadata.getReferalTypeConcept().getConceptId(),
            hivMetadata.getTransferredFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getPrepProgram().getProgramId(),
            hivMetadata.getStateOfStayOnPrepProgram().getConceptId()));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  /**
   * <b>Description:</b> Re-initiated PrEP during the reporting period All clients who have “O
   * utente está a Retomar PrEP” (Concept id 165296 from encounter type 80) value coded “RESTART”
   * (concept id 1705) and value datetime between start date and end date;
   *
   * @return
   */
  public CohortDefinition getPatientsReInitiatedToPrEP() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Patients Re-initiated to PrEP during the reporting period");

    definition.setQuery(
        PrepCtQueries.getPatientsReInitiatedToPrEP(
            hivMetadata.getInitialStatusPrepUserConcept().getConceptId(),
            hivMetadata.getRestartConcept().getConceptId(),
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId()));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  /**
   * <b>Description:</b> Continued PrEP during the reporting period All clients who have “O utente
   * está a Continuar PrEP” (Concept id 165296 from encounter type 80) value coded “CONTINUE REGIMEN
   * ” (concept id 1257) and value datetime between start date and end date;
   *
   * @return
   */
  public CohortDefinition getPatientsOnContinuedPrEP() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Patients Continued on PrEP during the reporting period");

    definition.setQuery(
        PrepCtQueries.getPatientsOnContinuedPrEP(
            hivMetadata.getInitialStatusPrepUserConcept().getConceptId(),
            hivMetadata.getContinueRegimenConcept().getConceptId(),
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId()));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  /**
   * <b>Description:</b> Positive Test Results: Clients with the field “Resultado do Teste”(concept
   * id 1040) with value “Positivo” (concept id 703) on Ficha de Consulta de Seguimento
   * PrEP(encounter type 81) registered during the reporting period;
   *
   * @return
   */
  public CohortDefinition getPositiveTestResults() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Patients with Positive test Results on PrEP during the reporting period");

    definition.setQuery(
        PrepCtQueries.getPositiveTestResults(
            hivMetadata.getHivRapidTest1QualitativeConcept().getConceptId(),
            commonMetadata.getPositive().getConceptId(),
            hivMetadata.getPrepSeguimentoEncounterType().getEncounterTypeId()));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  /**
   * <b>Description:</b> Negative Test Results: Clients with the field “Resultado do Teste” (concept
   * id 1040) with value “Negativo” (concept id 664) on Ficha de Consulta de Seguimento PrEP
   * (encounter type 81) registered during the reporting period;
   *
   * @return
   */
  public CohortDefinition getNegativeTestResults() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Patients with Negative test Results on PrEP during the reporting period");

    definition.setQuery(
        PrepCtQueries.getNegativeTestResults(
            hivMetadata.getHivRapidTest1QualitativeConcept().getConceptId(),
            commonMetadata.getNegative().getConceptId(),
            hivMetadata.getPrepSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPrepSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getDateOfInitialHivTestConcept().getConceptId()));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  /**
   * <b>Description:</b> Other Test Results: Clients with the field “Resultado do Teste” (concept id
   * 1040) with value “Indeterminado” (concept id 1138) on Ficha de Consulta de Seguimento PrEP
   * (encounter type 81) registered during the reporting period;
   *
   * @return
   */
  public CohortDefinition getOtherTestResults() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Patients with Other test Results Part 1 Prep");

    definition.setQuery(
        PrepCtQueries.getOtherTestResults(
            hivMetadata.getHivRapidTest1QualitativeConcept().getConceptId(),
            commonMetadata.getIndeterminate().getConceptId(),
            hivMetadata.getPrepSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPrepInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getDateOfHivTestWithNegativeResultsPrepUuidConcept().getConceptId()));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }
}
