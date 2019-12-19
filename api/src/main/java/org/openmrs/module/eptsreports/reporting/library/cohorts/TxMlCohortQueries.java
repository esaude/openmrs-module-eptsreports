package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoAreDeadCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoAreLTFUGreatherThan3MonthsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoAreLTFULessThan3MonthsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoAreTransferedOutCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoMissedNextApointmentCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoRefusedOrStoppedTreatmentCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.TxMlQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** All queries needed for TxMl report needed for EPTS project */
@Component
public class TxMlCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  public CohortDefinition getAllPatientsWhoMissedNextAppointment() {
    return genericCohortQueries.generalSql(
        "Missed Next appointment",
        TxMlQueries.getPatientsWhoMissedAppointment(
            30,
            183,
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId()));
  }

  public CohortDefinition getTransferOutPatients() {
    return genericCohortQueries.generalSql(
        "Transfer out",
        TxMlQueries.getTransferredOutPatients(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));
  }

  public CohortDefinition getNonConsentedPatients() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Not Consented and Not dead");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "nonConsented",
        EptsReportUtils.map(
            genericCohortQueries.generalSql(
                "Non Consented patients",
                TxMlQueries.getNonConsentedPatients(
                    hivMetadata.getPrevencaoPositivaInicialEncounterType().getEncounterTypeId(),
                    hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getAcceptContactConcept().getConceptId(),
                    hivMetadata.getNoConcept().getConceptId())),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatientsBeforeDate(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "homeVisitCardDead",
        EptsReportUtils.map(
            getPatientsMarkedAsDeadInHomeVisitCard(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("nonConsented AND NOT (dead OR homeVisitCardDead)");
    return cd;
  }

  // a and b
  public CohortDefinition getPatientsWhoMissedNextAppointmentAndNotTransferredOut() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients who missed appointment and are NOT transferred out");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "missedAppointment",
        EptsReportUtils.map(
            getAllPatientsWhoMissedNextAppointment(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "transferOut",
        EptsReportUtils.map(getTransferOutPatients(), "endDate=${endDate},location=${location}"));
    cd.setCompositionString("missedAppointment AND NOT transferOut");
    return cd;
  }

  // a and b and died
  public CohortDefinition
      getPatientsWhoMissedNextAppointmentAndNotTransferredOutButDiedDuringReportingPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Get patients who missed appointment and are NOT transferred out, but died during reporting period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "missedAppointmentLessTransfers",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNotTransferredOut(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatientsBeforeDate(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "homeVisitCardDead",
        EptsReportUtils.map(
            getPatientsMarkedAsDeadInHomeVisitCard(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("missedAppointmentLessTransfers AND (dead OR homeVisitCardDead)");
    return cd;
  }

  // All Patients marked as dead in Patient Home Visit Card
  private CohortDefinition getPatientsMarkedAsDeadInHomeVisitCard() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("Get patients marked as dead in Patient Home Visit Card");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        TxMlQueries.getPatientsMarkedDeadInHomeVisitCard(
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getReasonPatientNotFound().getConceptId(),
            hivMetadata.getPatientIsDead().getConceptId()));

    return sqlCohortDefinition;
  }

  // a and b and Not Consented
  public CohortDefinition
      getPatientsWhoMissedNextAppointmentAndNotTransferredOutAndNotConsentedDuringReportingPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Get patients who missed appointment and are NOT transferred out, and NOT Consented during reporting period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "missedAppointmentLessTransfers",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNotTransferredOut(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "notConsented",
        EptsReportUtils.map(getNonConsentedPatients(), "endDate=${endDate},location=${location}"));
    cd.setCompositionString("missedAppointmentLessTransfers AND notConsented");
    return cd;
  }

  // Patients Traced Not Found
  private CohortDefinition getPatientsTracedAndNotFound() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("Get patients traced (Unable to locate) and Not Found");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        TxMlQueries.getPatientsTracedWithVisitCard(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getTypeOfVisitConcept().getConceptId(),
            hivMetadata.getBuscaConcept().getConceptId(),
            hivMetadata.getPatientFoundConcept().getConceptId(),
            hivMetadata.getNoConcept().getConceptId()));

    return sqlCohortDefinition;
  }

  // Patients Traced and Found.
  private CohortDefinition getPatientTracedAndFound() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("Get patients traced (Unable to locate) and Found");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        TxMlQueries.getPatientsTracedWithVisitCard(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getTypeOfVisitConcept().getConceptId(),
            hivMetadata.getBuscaConcept().getConceptId(),
            hivMetadata.getPatientFoundConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId()));

    return sqlCohortDefinition;
  }

  // a and b and Traced (Unable to locate)
  public CohortDefinition getPatientsWhoMissedNextAppointmentAndNotTransferredOutAndTraced() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "Get patients who missed next appointment, not transferred out and traced (Unable to locate)");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "missedAppointmentLessTransfers",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNotTransferredOut(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsNotFound",
        EptsReportUtils.map(
            getPatientsTracedAndNotFound(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsFound",
        EptsReportUtils.map(
            getPatientTracedAndFound(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatientsBeforeDate(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "homeVisitCardDead",
        EptsReportUtils.map(
            getPatientsMarkedAsDeadInHomeVisitCard(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "missedAppointmentLessTransfers AND (patientsNotFound AND NOT patientsFound) AND NOT (dead OR homeVisitCardDead)");

    return cd;
  }

  /*
   * a and b and Untraced Patients
   */
  public CohortDefinition getPatientsWhoMissedNextAppointmentAndNotTransferredOutAndUntraced() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Get patients who missed next appointment, not transferred out and untraced");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "missedAppointmentLessTransfers",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNotTransferredOut(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "withVisitCard",
        EptsReportUtils.map(
            getPatientsWithVisitCardRegisteredBtwnLastAppointmentOrDrugPickupAndEnddate(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "withVisitCardandWithoutObs",
        EptsReportUtils.map(
            getPatientsWithVisitCardAndWithoutObs(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatientsBeforeDate(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "homeVisitCardDead",
        EptsReportUtils.map(
            getPatientsMarkedAsDeadInHomeVisitCard(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "missedAppointmentLessTransfers AND (NOT withVisitCard OR withVisitCardandWithoutObs) AND NOT (dead OR homeVisitCardDead)");

    return cd;
  }

  /*
   * Untraced Patients Criteria 2 Patients with a set of observations
   */
  public CohortDefinition getPatientsWithVisitCardAndWithoutObs() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("Get patients without Visit Card but with a set of observations");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        TxMlQueries.getPatientsWithVisitCardAndWithoutObs(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getTypeOfVisitConcept().getConceptId(),
            hivMetadata.getBuscaConcept().getConceptId(),
            hivMetadata.getSecondAttemptConcept().getConceptId(),
            hivMetadata.getThirdAttemptConcept().getConceptId(),
            hivMetadata.getPatientFoundConcept().getConceptId(),
            hivMetadata.getDefaultingMotiveConcept().getConceptId(),
            hivMetadata.getReportOfVisitSupportConcept().getConceptId(),
            hivMetadata.getPatientHadDifficultyConcept().getConceptId(),
            hivMetadata.getPatientFoundForwardedConcept().getConceptId(),
            hivMetadata.getReasonPatientNotFound().getConceptId(),
            hivMetadata.getWhoGaveInformationConcept().getConceptId(),
            hivMetadata.getCardDeliveryDateConcept().getConceptId()));

    return sqlCohortDefinition;
  }

  /*
   * Untraced Patients Criteria 2 All Patients without “Patient Visit Card”
   * registered between the last scheduled appointment or drugs pick up by
   * reporting end date and the reporting end date
   */
  public CohortDefinition
      getPatientsWithVisitCardRegisteredBtwnLastAppointmentOrDrugPickupAndEnddate() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(
        "Get patients without Visit Card registered between the last scheduled appointment or drugs pick up by reporting end date and the reporting end date");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        TxMlQueries.getPatientsWithVisitCardRegisteredBtwnLastAppointmentOrDrugPickupAndEnddate(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId()));

    return sqlCohortDefinition;
  }

  public CohortDefinition getPatientsWhoAreLTFULessThan3Months() {
    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients who are LTFU less than 3 months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "missedAppointmentLess3Month",
        EptsReportUtils.map(this.getPatientsWhoAreLTFULessThan3MonthsCalculation(), mapping));
    cd.addSearch("dead", EptsReportUtils.map(this.getPatientsMarkedAsDead(), mapping));
    cd.addSearch(
        "transferedOut", EptsReportUtils.map(this.getPatientsWhoAreTransferedOut(), mapping));
    cd.addSearch(
        "refusedTreatment",
        EptsReportUtils.map(this.getPatientsWhoRefusedOrStoppedTreatment(), mapping));

    cd.setCompositionString(
        "missedAppointmentLess3Month NOT (dead OR transferedOut OR refusedTreatment)");
    return cd;
  }

  public CohortDefinition getPatientsWhoAreLTFUGreaterThan3Months() {
    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients who are LTFU less than 3 months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "missedAppointmentGreater3Month",
        EptsReportUtils.map(this.getPatientsWhoAreLTFUGreatherThan3MonthsCalculation(), mapping));
    cd.addSearch("dead", EptsReportUtils.map(this.getPatientsMarkedAsDead(), mapping));
    cd.addSearch(
        "transferedOut", EptsReportUtils.map(this.getPatientsWhoAreTransferedOut(), mapping));
    cd.addSearch(
        "refusedTreatment",
        EptsReportUtils.map(this.getPatientsWhoRefusedOrStoppedTreatment(), mapping));

    cd.setCompositionString(
        "missedAppointmentGreater3Month NOT (dead OR transferedOut OR refusedTreatment)");
    return cd;
  }

  @DocumentedDefinition(value = "patientsWhoMissedNextApointment")
  public CohortDefinition getPatientsWhoMissedNextApointment() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "txMLPatientsWhoMissedNextApointmentCalculation",
            Context.getRegisteredComponents(TxMLPatientsWhoMissedNextApointmentCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsMarkedAsDead")
  public CohortDefinition getPatientsMarkedAsDead() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsMarkedAsDeadCalculation",
            Context.getRegisteredComponents(TxMLPatientsWhoAreDeadCalculation.class).get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsWhoAreTransferedOut")
  public CohortDefinition getPatientsWhoAreTransferedOut() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsWhoAreTransferedOutCalculation",
            Context.getRegisteredComponents(TxMLPatientsWhoAreTransferedOutCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsWhoRefusedStoppedTreatmentCalculation")
  public CohortDefinition getPatientsWhoRefusedOrStoppedTreatment() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsWhoRefusedStoppedTreatmentCalculation",
            Context.getRegisteredComponents(
                    TxMLPatientsWhoRefusedOrStoppedTreatmentCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsWhoAreLTFULessThan3MonthsCalculation")
  private CohortDefinition getPatientsWhoAreLTFULessThan3MonthsCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsWhoAreLTFULessThan3MonthsCalculation",
            Context.getRegisteredComponents(TxMLPatientsWhoAreLTFULessThan3MonthsCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsWhoAreLTFUGreatherThan3MonthsCalculation")
  private CohortDefinition getPatientsWhoAreLTFUGreatherThan3MonthsCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsWhoAreLTFUGreatherThan3MonthsCalculation",
            Context.getRegisteredComponents(
                    TxMLPatientsWhoAreLTFUGreatherThan3MonthsCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }
}
