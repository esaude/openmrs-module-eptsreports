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

  // a
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

  public CohortDefinition getNonConsistentPatients() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Not Consistent and Not dead");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "nonConsistent",
        EptsReportUtils.map(
            genericCohortQueries.generalSql(
                "Non consistent patients",
                TxMlQueries.getNonConsistentPatients(
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
    cd.setCompositionString("nonConsistent AND NOT dead");
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
            hivMetadata.getReasonPatientNotFound().getConceptId(),
            hivMetadata.getPatientIsDead().getConceptId()));

    return sqlCohortDefinition;
  }

  // a and b and Not consistent
  public CohortDefinition
      getPatientsWhoMissedNextAppointmentAndNotTransferredOutAndNotConsistentDuringReportingPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Get patients who missed appointment and are NOT transferred out, and NOT consistent during reporting period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "missedAppointmentLessTransfers",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNotTransferredOut(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "notConsistent",
        EptsReportUtils.map(getNonConsistentPatients(), "endDate=${endDate},location=${location}"));
    cd.setCompositionString("missedAppointmentLessTransfers AND notConsistent");
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
        TxMlQueries.getPatientsTracedUnableToLocate(
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getTypeOfVisitConcept().getConceptId(),
            hivMetadata.getPatientFoundConcept().getConceptId(),
            hivMetadata.getBuscaConcept().getConceptId(),
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
        TxMlQueries.getPatientsTracedUnableToLocate(
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getTypeOfVisitConcept().getConceptId(),
            hivMetadata.getPatientFoundConcept().getConceptId(),
            hivMetadata.getBuscaConcept().getConceptId(),
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

    cd.setCompositionString(
        "missedAppointmentLessTransfers AND (patientsNotFound AND NOT patientsFound )");

    return cd;
  }
}
