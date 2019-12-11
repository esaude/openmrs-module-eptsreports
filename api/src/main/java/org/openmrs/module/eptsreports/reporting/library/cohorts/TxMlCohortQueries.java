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

  public CohortDefinition getTransferOutPatients() {
    return genericCohortQueries.generalSql(
        "Transfer out",
        TxMlQueries.getTransferredOutPatients(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));
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

  /** @return */
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
        "patientsWithDrugsPickupOrConsultation",
        EptsReportUtils.map(
            txCurrCohortQueries
                .getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultationComposition(),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "patientsWithMissedVisitOnMasterCard",
        EptsReportUtils.map(
            getPatientsWithMissedVisitOnMasterCardQuery(),
            "startDate=${startDate}, endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "patientsWhoLeftARTProgramBeforeOrOnEndDate OR patientsWithDrugsPickupOrConsultation OR patientsWithMissedVisitOnMasterCard");

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
   * <p>e. Except all patients who after the most recent date from a to d, have a drugs pick up or
   * consultation
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
    cd.addSearch(
        "patientsWithDrugsPickupOrConsultation",
        EptsReportUtils.map(
            txCurrCohortQueries
                .getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultationComposition(),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString(
        "(deadByPatientProgramState OR deadByPatientDemographics OR deadRegisteredInLastHomeVisitCard OR deadRegisteredInFichaResumoAndFichaClinicaMasterCard) AND NOT patientsWithDrugsPickupOrConsultation");

    return cd;
  }

  // a and b
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
                .getPatientWhoAfterMostRecentDateHaveDrusPickupOrConsultationComposition(),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("missedAppointment AND noScheduledDrugPickupOrNextConsultation");
    return cd;
  }

  // a and b and died
  public CohortDefinition
  getPatientsWhoMissedNextAppointmentAndDiedDuringReportingPeriod() {
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

    cd.setCompositionString("missedAppointmentLessTransfers AND dead ");

    return cd;
  }

  // a, b AND Transferred Out
  public CohortDefinition
  getPatientsWhoMissedNextAppointmentAndTransferredOut() {
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
            "transferOut",
            EptsReportUtils.map(
                    getTransferredOutPatientsComposition(),
                    "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("missedAppointmentLessTransfers AND transferOut ");

    return cd;
  }
}
