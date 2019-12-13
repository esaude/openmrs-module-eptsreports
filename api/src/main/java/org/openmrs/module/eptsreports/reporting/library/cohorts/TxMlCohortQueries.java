package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
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
            txCurrCohortQueries
                .getPatientWhoAfterMostRecentDateHaveDrugPickupOrConsultationComposition(),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("missedAppointment AND noScheduledDrugPickupOrNextConsultation");
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
        "missedAppointmentLessTransfers AND refusedOrStoppedTreatment AND NOT (dead transferOut)");

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
    sql.setName("Patients With Missed Visist On Master Card Query");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End date", Date.class));
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
            "startDate=${startDate}, endDate=${endDate},location=${location}"));

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
        "deadRegisteredInFichaResumoAndFichaClinicaMasterCard ",
        EptsReportUtils.map(
            txCurrCohortQueries.getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString(
        "deadByPatientProgramState OR deadByPatientDemographics OR deadRegisteredInLastHomeVisitCard OR deadRegisteredInFichaResumoAndFichaClinicaMasterCard");

    return cd;
  }
}
