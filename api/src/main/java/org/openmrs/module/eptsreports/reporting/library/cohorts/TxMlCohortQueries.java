package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
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
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
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

    CohortDefinition missedAppointment = getAllPatientsWhoMissedNextAppointment();
    CohortDefinition noScheduled = getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup();
    CohortDefinition startedArt = genericCohortQueries.getStartedArtBeforeDate(false);

    cd.addSearch("missedAppointment", Mapped.mapStraightThrough(missedAppointment));

    String mappings = "onOrBefore=${endDate},location=${location}";
    String mappings2 = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    cd.addSearch("noScheduled", EptsReportUtils.map(noScheduled, mappings2));
    cd.addSearch("startedArt", EptsReportUtils.map(startedArt, mappings));

    cd.setCompositionString("(missedAppointment OR noScheduled) AND startedArt");
    return cd;
  }
  /**
   * 14. All patients who do not have the next scheduled drug pick up date (Fila) and next scheduled
   * consultation date (Ficha de Seguimento or Ficha Clinica – Master Card) and ART Pickup date
   * (Recepção – Levantou ARV).
   *
   * @return
   */
  public CohortDefinition getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup");

    definition.setQuery(
        TxMlQueries.getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

    definition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
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
        "(missedAppointmentLessTransfers AND dead) AND NOT patientWhoAfterMostRecentDateHaveDrugPickupOrConsultation");

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
        "(missedAppointmentLessTransfers AND refusedOrStoppedTreatment) AND NOT (dead OR transferOut)");

    return cd;
  }

  /**
   * a, b AND Transferred Out Except all patients who after the most recent date from above
   * criterias, have a drugs pick up or consultation: Except patients identified in Dead
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

    cd.setCompositionString("(missedAppointmentLessTransfers AND transferOut) AND NOT dead ");

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
        "patientsNotFound",
        EptsReportUtils.map(
            getPatientsTracedAndNotFound(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsFound",
        EptsReportUtils.map(
            getPatientTracedAndFound(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("patientsNotFound AND NOT patientsFound");

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
        "withoutVisitCard",
        EptsReportUtils.map(
            getPatientsWithoutVisitCardRegisteredBtwnLastAppointmentOrDrugPickupAndEnddate(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "withVisitCardandWithObs",
        EptsReportUtils.map(
            getPatientsWithVisitCardAndWithObs(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("withoutVisitCard OR NOT withVisitCardandWithObs");

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
        "(missedAppointment AND ((untracedPatients OR tracedPatients) AND C1)) AND NOT dead AND NOT transferredOut AND NOT refusedOrStopped");

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
            getPatientsWhoMissedNextAppointmentAndDiedDuringReportingPeriod(),
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
        "LeftARTProgramBeforeOrOnEndDate",
        EptsReportUtils.map(
            getPatientsTransferedOutInProgramBeforeOrOnEndDate(),
            "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "permanentStateTransferredOut",
        EptsReportUtils.map(
            txCurrCohortQueries
                .getTransferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(),
            "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "MissedVisitCard",
        EptsReportUtils.map(
            getPatientsWithMissedVisit(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "MostRecentDateHaveFilaOrConsultation",
        EptsReportUtils.map(
            getPatientWithFilaOrConsultationAfterTrasnferDiedMissed(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "(LeftARTProgramBeforeOrOnEndDate OR permanentStateTransferredOut OR MissedVisitCard) AND NOT  MostRecentDateHaveFilaOrConsultation ");

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
            getPatientsDeadInProgramStateByReportingEndDate(),
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
            getAllPatientsWhoMissedNextAppointment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
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
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
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
            hivMetadata.getCardDeliveryDateConcept().getConceptId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

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
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

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
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));
  }

  /**
   * Patients who have REASON PATIENT MISSED VISIT (MOTIVOS DA FALTA) as “Transferido para outra US”
   * or “Auto-transferencia” marked last Home Visit Card occurred during the reporting period. Use
   * the “data da visita” when the patient reason was marked on the home visit card as the reference
   * date
   *
   * @return
   */
  public CohortDefinition getPatientsWithMissedVisit() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Patients With Missed Visit On Master Card Query");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "location", Location.class));

    sql.setQuery(
        TxMlQueries.getPatientsWithMissedVisit(
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
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
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getDefaultingMotiveConcept().getConceptId(),
            hivMetadata.getPatientForgotVisitDateConcept().getConceptId(),
            hivMetadata.getPatientIsBedriddenAtHomeConcept().getConceptId(),
            hivMetadata.getDistanceOrMoneyForTransportIsTooMuchForPatientConcept().getConceptId(),
            hivMetadata.getPatientIsDissatisfiedWithDayHospitalServicesConcept().getConceptId(),
            hivMetadata.getFearOfTheProviderConcept().getConceptId(),
            hivMetadata.getAbsenceOfHealthProviderInHealthUnitConcept().getConceptId(),
            hivMetadata.getAdverseReaction().getConceptId(),
            hivMetadata.getPatientIsTreatingHivWithTraditionalMedicineConcept().getConceptId(),
            hivMetadata.getOtherReasonWhyPatientMissedVisitConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

    return sql;
  }

  // Patients Traced Not Found
  public CohortDefinition getPatientsTracedAndNotFound() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("Get patients traced (Unable to locate) and Not Found");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        TxMlQueries.getPatientsTracedWithVisitCard(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getTypeOfVisitConcept().getConceptId(),
            hivMetadata.getBuscaConcept().getConceptId(),
            hivMetadata.getPatientFoundConcept().getConceptId(),
            hivMetadata.getNoConcept().getConceptId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

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
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getTypeOfVisitConcept().getConceptId(),
            hivMetadata.getBuscaConcept().getConceptId(),
            hivMetadata.getPatientFoundConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

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
  /**
   * <b>All patients who after the most recent date from below criterias:</b>
   *
   * <p>Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 7
   * (Transferred-out) or  Patient_State.start_date <= endDate Patient_state.end_date is null and
   *
   * <p>Encounter Type ID= 53 Estado de Permanencia (Concept Id 6272) = Transferred-out (Concept ID
   * 1706) obs_datetime <= endDate OR Encounter Type ID= 6 Estado de Permanencia (Concept Id 6273) =
   * Transferred-out (Concept ID 1706) Encounter_datetime <= endDate and
   *
   * <p>Encounter Type ID= 21, Last Encounter_datetime <=endDate REASON PATIENT MISSED VISIT (Obs
   * concept id = 2016) Answers = TRANSFERRED OUT TO ANOTHER FACILITY (id=1706) OR AUTO TRANSFER
   * (id=23863) <b>have a drugs pick up or consultation</b>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientWithFilaOrConsultationAfterTrasnferDiedMissed() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("get Patients With Most Recent Date Have Fila or Consultation ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pharmaciaEncounterType", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put(
        "masterCardDrugPickupEncounterType",
        hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("artDatePickup", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put(
        "stateOfStayOfPreArtPatient", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    map.put("transferredOutConcept", hivMetadata.getTransferredOutConcept().getConceptId());
    map.put("autoTransferConcept", hivMetadata.getAutoTransferConcept().getConceptId());
    map.put("stateOfStayOfArtPatient", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("defaultingMotiveConcept", hivMetadata.getDefaultingMotiveConcept().getConceptId());
    map.put(
        "buscaActivaEncounterType", hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId());
    map.put("artProgram", hivMetadata.getARTProgram().getProgramId());
    map.put(
        "transferredOutToAnotherHealthFacilityWorkflowState",
        hivMetadata
            .getTransferredOutToAnotherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());

    String query =
        "  SELECT mostrecent.patient_id "
            + "FROM ("
            + " SELECT lastest.patient_id ,lastest.last_date  "
            + " FROM (  "
            + "    SELECT p.patient_id , MAX(ps.start_date) AS last_date  "
            + "    FROM patient p   "
            + "        INNER JOIN patient_program pg   "
            + "            ON p.patient_id=pg.patient_id   "
            + "        INNER JOIN patient_state ps   "
            + "            ON pg.patient_program_id=ps.patient_program_id   "
            + "    WHERE pg.voided=0   "
            + "        AND ps.voided=0   "
            + "        AND p.voided=0   "
            + "        AND pg.program_id= ${artProgram}  "
            + "        AND ps.state = ${transferredOutToAnotherHealthFacilityWorkflowState}   "
            + "        AND ps.end_date is null   "
            + "        AND ps.start_date<= :endDate    "
            + "        AND pg.location_id= :location   "
            + "    group by p.patient_id  "
            + "  "
            + "    UNION  "
            + "  "
            + "    SELECT  p.patient_id,  MAX(o.obs_datetime) AS last_date  "
            + "    FROM patient p    "
            + "        INNER JOIN encounter e   "
            + "            ON e.patient_id=p.patient_id   "
            + "        INNER JOIN obs o   "
            + "            ON o.encounter_id=e.encounter_id   "
            + "    WHERE  p.voided = 0   "
            + "        AND e.voided = 0   "
            + "        AND o.voided = 0   "
            + "        AND e.encounter_type = ${masterCardEncounterType}   "
            + "        AND o.concept_id = ${stateOfStayOfPreArtPatient}  "
            + "        AND o.value_coded =  ${transferredOutConcept}   "
            + "        AND o.obs_datetime <=  :endDate   "
            + "        AND e.location_id =  :location   "
            + "    GROUP BY p.patient_id  "
            + "    UNION   "
            + "    SELECT  p.patient_id ,MAX(e.encounter_datetime) AS last_date  "
            + "    FROM patient p    "
            + "        INNER JOIN encounter e   "
            + "            ON e.patient_id=p.patient_id   "
            + "        INNER JOIN obs o   "
            + "            ON o.encounter_id=e.encounter_id   "
            + "    WHERE  p.voided = 0   "
            + "        AND e.voided = 0   "
            + "        AND o.voided = 0   "
            + "        AND e.encounter_type = ${adultoSeguimentoEncounterType}  "
            + "        AND o.concept_id = ${stateOfStayOfArtPatient}  "
            + "        AND o.value_coded = ${transferredOutConcept}   "
            + "        AND e.encounter_datetime <=  :endDate   "
            + "        AND e.location_id =  :location  "
            + "    GROUP BY p.patient_id   "
            + "  "
            + "    UNION  "
            + "  "
            + "    SELECT p.patient_id, MAX(e.encounter_datetime) last_date   "
            + "    FROM patient p   "
            + "        INNER JOIN encounter e   "
            + "              ON p.patient_id = e.patient_id   "
            + "        INNER JOIN obs o   "
            + "              ON e.encounter_id = o.encounter_id   "
            + "    WHERE o.concept_id = ${defaultingMotiveConcept}  "
            + "    	   AND e.location_id = :location   "
            + "        AND e.encounter_type= ${buscaActivaEncounterType}   "
            + "        AND e.encounter_datetime BETWEEN :startDate AND :endDate AND p.voided=0  "
            + "		   AND o.value_coded IN (${transferredOutConcept} ,${autoTransferConcept})  "
            + "        AND e.voided=0   "
            + "        AND o.voided=0   "
            + "        AND p.voided=0   "
            + "    GROUP BY p.patient_id "
            + ") lastest   "
            + " INNER JOIN encounter e ON e.patient_id = lastest.patient_id   "
            + " INNER JOIN obs o ON o.encounter_id = e.encounter_id   "
            + " WHERE  e.voided = 0  "
            + "        AND o.voided = 0  "
            + "        AND (( e.encounter_type = ${masterCardDrugPickupEncounterType} "
            + "					AND o.concept_id = ${artDatePickup} "
            + "					AND o.value_datetime > lastest.last_date "
            + "					AND  o.value_datetime <= :endDate)  "
            + "        OR  ( e.encounter_type IN (${adultoSeguimentoEncounterType},${pediatriaSeguimentoEncounterType},${pharmaciaEncounterType})  "
            + "					AND e.encounter_datetime > lastest.last_date "
            + "					AND  e.encounter_datetime <= :endDate))  "
            + "        AND e.location_id = :location  "
            + " GROUP BY lastest.patient_id) mostrecent"
            + " GROUP BY mostrecent.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String mappedQuery = stringSubstitutor.replace(query);

    sqlCohortDefinition.setQuery(mappedQuery);

    return sqlCohortDefinition;
  }
  /**
   * All deaths registered in Patient Program State by reporting end date Patient_program.program_id
   * =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 10 (Died) and Patient_State.start_date
   * <= endDate Patient_state.end_date is null
   */
  @DocumentedDefinition(value = "patientsDeadInProgramStateByReportingEndDate")
  public CohortDefinition getPatientsDeadInProgramStateByReportingEndDate() {

    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsDeadInProgramStateByReportingEndDate");

    definition.setQuery(
        TxMlQueries.getPatientsListBasedOnProgramAndStateByReportingEndDate(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }
  /**
   * @return Cohort of patients who left ART program before or on end date(4). Includes: transferred
   *     to, (patient state 7)
   */
  @DocumentedDefinition(value = "leftARTProgramBeforeOrOnEndDate")
  public CohortDefinition getPatientsTransferedOutInProgramBeforeOrOnEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("leftARTProgramBeforeOrOnEndDate");

    definition.setQuery(
        TxMlQueries.getPatientsListBasedOnProgramAndStateByReportingEndDate(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }
}
