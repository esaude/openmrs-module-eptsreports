package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.txml.StartedArtOnLastClinicalContactCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.TxMlQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** All queries needed for TxMl report needed for EPTS project */
@Component
public class TxMlCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  /**
   * a and b
   *
   * @return
   */
  public CohortDefinition
      getPatientsWhoMissedNextAppointmentAndNoScheduledDrugPickupOrNextConsultation() {
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
        "noScheduledDrugPickupOrNextConsultation",
        EptsReportUtils.map(
            txCurrCohortQueries.getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup(),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("missedAppointment OR noScheduledDrugPickupOrNextConsultation");
    return cd;
  }

  /**
   * a and b and died
   *
   * @return
   */
  public CohortDefinition getPatientsWhoMissedNextAppointmentAndDiedDuringReportingPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Get patients who missed appointment and are NOT transferred out, but died during reporting period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "missedAppointmentLessTransfers",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNoScheduledDrugPickupOrNextConsultation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            getDeadPatientsComposition(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientWhoAfterMostRecentDateHaveDrugPickupOrConsultation",
        EptsReportUtils.map(
            getPatientWhoAfterMostRecentDateHaveDrugPickupOrConsultation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "missedAppointmentLessTransfers AND dead AND NOT patientWhoAfterMostRecentDateHaveDrugPickupOrConsultation");

    return cd;
  }

  /**
   * (from a and b) Refused/Stopped treatment Except patients identified in Dead Disaggregation
   * Except patients identified in Transferred –out Disaggregation
   *
   * @return
   */
  public CohortDefinition getPatientsWhoMissedNextAppointmentAndRefusedOrStoppedTreatment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Get patients who missed appointment and are NOT dead and NOT transferred out during the reporting period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "missedAppointmentLessTransfers",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNoScheduledDrugPickupOrNextConsultation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "refusedOrStoppedTreatment",
        EptsReportUtils.map(
            getRefusedOrStoppedTreatmentQuery(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            getDeadPatientsComposition(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "transferOut",
        EptsReportUtils.map(
            getTransferredOutPatientsComposition(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "missedAppointmentLessTransfers AND refusedOrStoppedTreatment AND NOT (dead OR transferOut)");

    return cd;
  }

  /**
   * a, b AND Transferred Out Except all patients who after the most recent date from above
   * criterias, have a drugs pick up or consultation: Except patients identified in Dead
   * Disaggregation
   *
   * @return
   */
  public CohortDefinition getPatientsWhoMissedNextAppointmentAndTransferredOut() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Get patients who missed appointment and are transferred out, but died during reporting period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "missedAppointmentLessTransfers",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNoScheduledDrugPickupOrNextConsultation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "transferOut",
        EptsReportUtils.map(
            getTransferredOutPatientsComposition(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientWhoAfterMostRecentDateHaveDrugPickupOrConsultation",
        EptsReportUtils.map(
            getPatientWhoAfterMostRecentDateHaveDrugPickupOrConsultation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            getDeadPatientsComposition(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "missedAppointmentLessTransfers AND transferOut AND NOT (patientWhoAfterMostRecentDateHaveDrugPickupOrConsultation OR dead) ");

    return cd;
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
            getDeadPatientsComposition(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "missedAppointmentLessTransfers AND (patientsNotFound AND NOT patientsFound) AND NOT dead");

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
        "withoutVisitCard",
        EptsReportUtils.map(
            getPatientsWithoutVisitCardRegisteredBtwnLastAppointmentOrDrugPickupAndEnddate(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "withVisitCardandWithObs",
        EptsReportUtils.map(
            getPatientsWithVisitCardAndWithObs(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            getDeadPatientsComposition(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "missedAppointmentLessTransfers AND (withoutVisitCard OR NOT withVisitCardandWithObs) AND NOT dead");

    return cd;
  }

  /**
   * @return Disaggregation “Lost to Follow-Up After being on Treatment for <3 months” will have the
   *     following combination: ((A OR B) AND C1) AND NOT DEAD AND NOT TRANSFERRED OUT AND NOT
   *     REFUSED Lost to Follow-Up After being on Treatment for <3 months
   */
  public CohortDefinition getPatientsLTFULessThan90DaysComposition() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Get patients who are Lost To Follow Up Composition");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "missedAppointment",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNoScheduledDrugPickupOrNextConsultation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "untracedPatients",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNotTransferredOutAndUntraced(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "tracedPatients",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNotTransferredOutAndTraced(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C1",
        EptsReportUtils.map(
            getPatientsOnARTForLessOrMoreThan90Days(true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            getDeadPatientsComposition(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "transferredOut",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndTransferredOut(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "refusedOrStopped",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndRefusedOrStoppedTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "missedAppointment AND ((untracedPatients OR tracedPatients) AND C1) AND NOT dead AND NOT transferredOut AND NOT refusedOrStopped");

    return cd;
  }

  /**
   * @return Disaggregation “Lost to Follow-Up After being on Treatment for >3 months” will have the
   *     following combination: ((A OR B) AND C2) AND NOT DEAD AND NOT TRANSFERRED OUT AND NOT
   *     REFUSED
   */
  public CohortDefinition getPatientsLTFUMoreThan90DaysComposition() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Get patients who are Lost To Follow Up Composition");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "missedAppointment",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNoScheduledDrugPickupOrNextConsultation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "untracedPatients",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNotTransferredOutAndUntraced(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "tracedPatients",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNotTransferredOutAndTraced(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C2",
        EptsReportUtils.map(
            getPatientsOnARTForLessOrMoreThan90Days(false),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            getDeadPatientsComposition(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "transferredOut",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndTransferredOut(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "refusedOrStopped",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndRefusedOrStoppedTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "missedAppointment AND ((untracedPatients OR tracedPatients) AND C2) AND NOT dead AND NOT transferredOut AND NOT refusedOrStopped");

    return cd;
  }

  /**
   * Get Patients Transferred out Using the following criteria:
   * a:patientsWhoLeftARTProgramBeforeOrOnEndDate b:patientsWithMissedVisitOnMasterCard
   *
   * @return
   */
  public CohortDefinition getTransferredOutPatientsComposition() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get all patients who are Transferred Out");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Date.class));

    cd.addSearch(
        "patientsWhoLeftARTProgramBeforeOrOnEndDate",
        EptsReportUtils.map(
            txCurrCohortQueries.getPatientsWhoLeftARTProgramBeforeOrOnEndDate(),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "patientsWithMissedVisitOnMasterCard",
        EptsReportUtils.map(
            getPatientsWithMissedVisitOnMasterCardQuery(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "patientsWhoLeftARTProgramBeforeOrOnEndDate OR patientsWithMissedVisitOnMasterCard");

    return cd;
  }

  /**
   * Get all patients who after most recent Date have drug pickup or consultation
   *
   * @return
   */
  public CohortDefinition getPatientWhoAfterMostRecentDateHaveDrugPickupOrConsultation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get all patients who after most recent Date have drug pickup or consultation");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Date.class));

    cd.addSearch(
        "patientsWithDrugsPickupOrConsultation",
        EptsReportUtils.map(
            txCurrCohortQueries
                .getPatientWhoAfterMostRecentDateHaveDrugPickupOrConsultationComposition(),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("patientsWithDrugsPickupOrConsultation");

    return cd;
  }

  /**
   * a. All deaths registered in Patient Program State by reporting end date
   *
   * <p>b. All deaths registered in Patient Demographics by reporting end date
   *
   * <p>c. All deaths registered in Last Home Visit Card by reporting end date
   *
   * <p>d. All deaths registered in Ficha Resumo and Ficha Clinica of Master Card by reporting end
   * date
   *
   * @return
   */
  public CohortDefinition getDeadPatientsComposition() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients who are dead according to criteria a,b,c,d and e");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "deadByPatientProgramState",
        EptsReportUtils.map(
            txCurrCohortQueries
                .getPatientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate(),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "deadByPatientDemographics",
        EptsReportUtils.map(
            txCurrCohortQueries.getDeadPatientsInDemographiscByReportingEndDate(),
            "onOrBefore=${endDate}"));
    cd.addSearch(
        "deadRegisteredInLastHomeVisitCard",
        EptsReportUtils.map(
            txCurrCohortQueries.getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate(),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "deadRegisteredInFichaResumoAndFichaClinicaMasterCard",
        EptsReportUtils.map(
            txCurrCohortQueries.getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString(
        "deadByPatientProgramState OR deadByPatientDemographics OR deadRegisteredInLastHomeVisitCard OR deadRegisteredInFichaResumoAndFichaClinicaMasterCard");

    return cd;
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
            getDeadPatientsComposition(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("nonConsented AND NOT dead");
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

  /*
   * Untraced Patients Criteria 2 Patients with a visit card of type busca with certain set of observations
   */
  public CohortDefinition getPatientsWithVisitCardAndWithObs() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("Get patients without Visit Card but with a set of observations");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        TxMlQueries.getPatientsWithVisitCardAndWithObs(
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
      getPatientsWithoutVisitCardRegisteredBtwnLastAppointmentOrDrugPickupAndEnddate() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(
        "Get patients without Visit Card registered between the last scheduled appointment or drugs pick up by reporting end date and the reporting end date");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        TxMlQueries.getPatientsWithoutVisitCardRegisteredBtwnLastAppointmentOrDrugPickupAndEnddate(
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

  public CohortDefinition getTransferOutPatients() {
    return genericCohortQueries.generalSql(
        "Transfer out",
        TxMlQueries.getTransferredOutPatients(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));
  }

  // a
  public CohortDefinition getAllPatientsWhoMissedNextAppointment() {
    return genericCohortQueries.generalSql(
        "Missed Next appointment",
        TxMlQueries.getPatientsWhoMissedAppointment(
            28,
            183,
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId()));
  }

  /**
   * Patients who have REASON PATIENT MISSED VISIT (MOTIVOS DA FALTA) as “Transferido para outra US”
   * or “Auto-transferencia” marked last Home Visit Card occurred during the reporting period. Use
   * the “data da visita” when the patient reason was marked on the home visit card as the reference
   * date
   *
   * @return
   */
  public CohortDefinition getPatientsWithMissedVisitOnMasterCardQuery() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Patients With Missed Visit On Master Card Query");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "location", Location.class));

    sql.setQuery(
        TxMlQueries.getPatientsWithMissedVisitOnMasterCard(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getDefaultingMotiveConcept().getConceptId(),
            hivMetadata.getTransferredOutConcept().getConceptId(),
            hivMetadata.getAutoTransferConcept().getConceptId()));

    return sql;
  }

  /**
   * Patients Who have refused or Stopped Treatment Query
   *
   * @return
   */
  public CohortDefinition getRefusedOrStoppedTreatmentQuery() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Patients Who have refused or Stopped Treatment Query");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End date", Date.class));
    sql.addParameter(new Parameter("location", "location", Location.class));

    sql.setQuery(
        TxMlQueries.getRefusedOrStoppedTreatment(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getDefaultingMotiveConcept().getConceptId(),
            hivMetadata.getPatientForgotVisitDateConcept().getConceptId(),
            hivMetadata.getPatientIsBedriddenAtHomeConcept().getConceptId(),
            hivMetadata.getDistanceOrMoneyForTransportIsTooMuchForPatientConcept().getConceptId(),
            hivMetadata.getPatientIsDissatisfiedWithDayHospitalServicesConcept().getConceptId(),
            hivMetadata.getFearOfTheProviderConcept().getConceptId(),
            hivMetadata.getAbsenceOfHealthProviderInHealthUnitConcept().getConceptId(),
            hivMetadata.getAdverseReaction().getConceptId(),
            hivMetadata.getPatientIsTreatingHivWithTraditionalMedicineConcept().getConceptId(),
            hivMetadata.getOtherReasonWhyPatientMissedVisitConcept().getConceptId()));

    return sql;
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

  /**
   * All patients who have been on treatment for less than 90 days since the date initiated ARV
   * treatment to the date of their last scheduled clinical contact (the last scheduled clinical
   * appointment or last drugs pick up-FILA or 30 days after last drugs pick-up-MasterCard (the most
   * recent one from 3 sources) by reporting end date)
   *
   * @param lessThan90Days
   * @return
   */
  public CohortDefinition getPatientsOnARTForLessOrMoreThan90Days(Boolean lessThan90Days) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "lessThanOrMoreThan90DaysPatients",
            Context.getRegisteredComponents(StartedArtOnLastClinicalContactCalculation.class)
                .get(0));

    cd.addCalculationParameter("lessThan90Days", lessThan90Days);
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }
}
