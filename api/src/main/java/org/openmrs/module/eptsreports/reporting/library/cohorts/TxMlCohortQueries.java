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

  private HivMetadata hivMetadata;

  private GenericCohortQueries genericCohortQueries;

  private TxCurrCohortQueries txCurrCohortQueries;

  private HivCohortQueries hivCohortQueries;

  private TxRttCohortQueries txRttCohortQueries;

  @Autowired
  public TxMlCohortQueries(
      HivMetadata hivMetadata,
      GenericCohortQueries genericCohortQueries,
      TxCurrCohortQueries txCurrCohortQueries,
      HivCohortQueries hivCohortQueries,
      TxRttCohortQueries txRttCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.genericCohortQueries = genericCohortQueries;
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.hivCohortQueries = hivCohortQueries;
    this.txRttCohortQueries = txRttCohortQueries;
  }

  /**
   * <b>Description:</b> Patients started ART and missed Next Appointment or Next Drug Pickup (A and
   * B)
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getPatientsWhoMissedNextAppointmentAndNoScheduledDrugPickupOrNextConsultation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients who missed appointment and are NOT transferred out");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition missedAppointment = getAllPatientsWhoMissedNextAppointment();
    CohortDefinition noScheduled = txRttCohortQueries.getSecondPartFromITT();
    CohortDefinition startedArt = genericCohortQueries.getStartedArtBeforeDate(false);

    CohortDefinition transferredOut = hivCohortQueries.getPatientsTransferredOut();

    CohortDefinition dead = getDeadPatientsComposition();

    cd.addSearch("missedAppointment", Mapped.mapStraightThrough(missedAppointment));

    String mappings = "onOrBefore=${endDate},location=${location}";
    String mappings2 = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    cd.addSearch("noScheduled", EptsReportUtils.map(noScheduled, mappings));
    cd.addSearch("startedArt", EptsReportUtils.map(startedArt, mappings));

    cd.addSearch(
        "transferredOut",
        EptsReportUtils.map(transferredOut, "onOrBefore=${startDate-1d},location=${location}"));
    cd.addSearch("dead", EptsReportUtils.map(dead, "endDate=${startDate-1d},location=${location}"));

    cd.setCompositionString(
        "((missedAppointment OR noScheduled) AND startedArt) AND NOT (dead OR transferredOut)");

    return cd;
  }
  /**
   * <b>Description:</b> All patients who do not have the next scheduled drug pick up date (Fila)
   * and next scheduled consultation date (Ficha de Seguimento or Ficha Clinica – Master Card) and
   * ART Pickup date (Recepção – Levantou ARV).
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <b>a.</b> the most recent Encounters of Type 6, 9 and 18 during the reporting period without
   * the following observations or itscontents is null:
   *
   * <ul>
   *   <li><b>i.</b> Next Clinical Appointment <b>(concept_id = 1410) -><b>encountersType_id = 6, or
   *       9</b>
   *   <li><b>ii.</b> Next Drugs Pick Up Appointment <b>(concept_id = 5096)</b> ->
   *       <b>encounterType_id = 18</b>
   * </ul>
   *
   * <p><b>b.</b> And none ART Pickup MasterCard date <b>(concept_id = 23866)</b> within reporting
   * period from <b>encounterType_id = 52</b>.
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> Patients Started Art and missed Next Appointment or Next Drug Pickup (a and
   * b) and Died during reporting period
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> (from A and B) Refused/Stopped treatment
   *
   * <p>Except patients identified in Dead or Transferred–out Disaggregation
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> A, B AND Transferred Out Except all patients who after the most recent date
   * from above criterias, have a drugs pick up or consultation: Except patients identified in Dead
   * Disaggregation
   *
   * @return {@link CohortDefinition}
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

  /**
   * <b>Description:</b> A and B and Traced (Unable to locate)
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description: </b> A and B and Untraced Patients
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> “Lost to Follow-Up After being on Treatment for <3 months” will have the
   * following combination: ((A OR B) AND C1) AND NOT DEAD AND NOT TRANSFERRED OUT AND NOT REFUSED
   * Lost to Follow-Up After being on Treatment for <3 months
   *
   * @return {@link CohortDefinition}
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
            getPatientsOnARTForLessOrMoreThan180Days(1),
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
   * <b>Description:</b> “Lost to Follow-Up After being on Treatment for >6 months” will have the
   * following combination:
   *
   * <ul>
   *   <li>((A OR B) AND C2) AND NOT Dead AND NOT Transferred-Out AND NOT Refused
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsLTFUMoreThan180DaysComposition() {
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
            getPatientsOnARTForLessOrMoreThan180Days(3),
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
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <b>10 –</b> All transferred-outs <b>(Patient_State.state = 7)</b> in ART Program <b>(program_id
   * =2)</b>
   *
   * <p>with Estado de Permanencia <b>(concept_id = 6272)</b> = Transferred-out <b>(concept_id =
   * 1706)</b>
   *
   * <p>with Busca activa <b>(encounterType_id = 21)</b> = Transferred-out <b>(concept_id =
   * 1706)</b> and Auto Transfer <b>(concept_id = 23863)</b>
   *
   * <p>And had no registered in Drug pickup <b>(encounterType_id = 53)</b> after The
   * Transferred-out Date within reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getTransferredOutPatientsComposition() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(
        "Patient Transferred Out With No Drug Pick After The Transferred out Date ");
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
            + " SELECT lastest.patient_id ,Max(lastest.last_date) as  last_date "
            + " FROM (  "
            + "    SELECT p.patient_id , Max(ps.start_date) AS last_date  "
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
            + "        AND ps.start_date <= :endDate    "
            + "        AND pg.location_id= :location   "
            + "    group by p.patient_id  "
            + "  "
            + "    UNION  "
            + "  "
            + "    SELECT  p.patient_id,  Max(o.obs_datetime) AS last_date  "
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
            + "        AND o.obs_datetime <= :endDate   "
            + "        AND e.location_id =  :location   "
            + "    GROUP BY p.patient_id  "
            + "    UNION   "
            + "    SELECT  p.patient_id , Max(e.encounter_datetime) AS last_date  "
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
            + "        AND e.encounter_datetime <= :endDate   "
            + "        AND e.location_id =  :location  "
            + "    GROUP BY p.patient_id   "
            + "  "
            + "    UNION  "
            + "  "
            + "    SELECT p.patient_id, Max(e.encounter_datetime) last_date   "
            + "    FROM patient p   "
            + "        INNER JOIN encounter e   "
            + "              ON p.patient_id = e.patient_id   "
            + "        INNER JOIN obs o   "
            + "              ON e.encounter_id = o.encounter_id   "
            + "    WHERE o.concept_id = ${defaultingMotiveConcept}  "
            + "    	   AND e.location_id = :location   "
            + "        AND e.encounter_type= ${buscaActivaEncounterType}   "
            + "        AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "		   AND o.value_coded IN (${transferredOutConcept} ,${autoTransferConcept})  "
            + "        AND e.voided=0   "
            + "        AND o.voided=0   "
            + "        AND p.voided=0   "
            + "    GROUP BY p.patient_id "
            + ") lastest   "
            + "WHERE lastest.patient_id NOT  IN("
            + " "
            + "  			     SELECT  p.patient_id    "
            + "	                 FROM patient p      "
            + "	                     INNER JOIN encounter e     "
            + "	                         ON e.patient_id=p.patient_id     "
            + "	                 WHERE  p.voided = 0     "
            + "	                     AND e.voided = 0     "
            + "	                     AND e.encounter_type IN (${adultoSeguimentoEncounterType},"
            + "${pediatriaSeguimentoEncounterType},"
            + "${pharmaciaEncounterType})    "
            + "	                     AND e.encounter_datetime > lastest.last_date "
            + " AND e.encounter_datetime <=  :endDate    "
            + "	                     AND e.location_id =  :location    "
            + "	                 GROUP BY p.patient_id "
            + " UNION "
            + "        			 SELECT  p.patient_id    "
            + "	                 FROM patient p       "
            + "	                      INNER JOIN encounter e      "
            + "	                          ON e.patient_id=p.patient_id      "
            + "	                      INNER JOIN obs o      "
            + "	                          ON o.encounter_id=e.encounter_id      "
            + "	                  WHERE  p.voided = 0      "
            + "	                      AND e.voided = 0      "
            + "	                      AND o.voided = 0      "
            + "	                      AND e.encounter_type = ${masterCardDrugPickupEncounterType}     "
            + "	                      AND o.concept_id = ${artDatePickup}     "
            + "	                      AND o.value_datetime > lastest.last_date  "
            + " AND o.value_datetime <= :endDate      "
            + "	                      AND e.location_id =  :location     "
            + "	                  GROUP BY p.patient_id   "
            + ")  "
            + " GROUP BY lastest.patient_id"
            + " )mostrecent "
            + " GROUP BY mostrecent.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String mappedQuery = stringSubstitutor.replace(query);

    sqlCohortDefinition.setQuery(mappedQuery);

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> Get all patients who after most recent Date have drug pickup or
   * consultation
   *
   * @return {@link CohortDefinition}
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
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <b>a.</b> All deaths registered in Patient Program State by reporting end date
   *
   * <p><b>
   *
   * <p>b.</b> All deaths registered in Patient Demographics by reporting end date
   *
   * <p><b>
   *
   * <p>c.</b> All deaths registered in Last Home Visit Card by reporting end date
   *
   * <p><b>
   *
   * <p>d.</b> All deaths registered in Ficha Resumo and Ficha Clinica of Master Card by reporting
   * end date
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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

    cd.addSearch(
        "exclusion",
        EptsReportUtils.map(getExcuisionDeadPatients(), "endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "(deadByPatientProgramState OR deadByPatientDemographics OR deadRegisteredInLastHomeVisitCard OR deadRegisteredInFichaResumoAndFichaClinicaMasterCard) AND NOT exclusion");

    return cd;
  }

  private CohortDefinition getExcuisionDeadPatients() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Excuision Dead patients");
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("2", hivMetadata.getARTProgram().getProgramId());
    map.put("10", hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId());
    map.put("21", hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId());
    map.put("36", hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId());
    map.put("37", hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId());
    map.put("2031", hivMetadata.getReasonPatientNotFound().getConceptId());
    map.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    map.put(
        "23944", hivMetadata.getReasonPatientNotFoundByActivist2ndVisitConcept().getConceptId());
    map.put(
        "23945", hivMetadata.getReasonPatientNotFoundByActivist3rdVisitConcept().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6272", hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        "SELECT outter.patient_id "
            + "FROM "
            + "    ( "
            + "    SELECT mostrecent.patient_id, MAX(mostrecent.last_date) as l_date "
            + "    FROM "
            + "        ( "
            + "        SELECT p.patient_id, MAX(ps.start_date) AS last_date "
            + "        FROM patient p  "
            + "            INNER JOIN patient_program pg ON p.patient_id=pg.patient_id  "
            + "            INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id  "
            + "        WHERE pg.voided=0  "
            + "            AND ps.voided=0  "
            + "            AND p.voided=0  "
            + "            AND pg.program_id= ${2}  "
            + "            AND ps.state = ${10}  "
            + "            AND ps.end_date IS NULL  "
            + "            AND ps.start_date<=:endDate  "
            + "            AND pg.location_id= :location  "
            + "        GROUP BY p.patient_id "
            + "        UNION "
            + "        SELECT  max_date.patient_id, max_date.last_date "
            + "        FROM   "
            + "            ( "
            + "                SELECT  p.patient_id,  MAX(e.encounter_datetime) last_date   "
            + "                FROM patient p  "
            + "                    INNER  JOIN encounter e ON e.patient_id=p.patient_id  "
            + "                WHERE   "
            + "                    e.encounter_datetime <= :endDate  "
            + "                    AND e.location_id =  :location  "
            + "                    AND e.encounter_type  in(${21},${36},${37})   "
            + "                    AND e.voided=0  "
            + "                    AND p.voided = 0  "
            + "                GROUP BY  p.patient_id   "
            + "            ) AS max_date  "
            + "            INNER  JOIN encounter ee ON ee.patient_id = max_date.patient_id  "
            + "            INNER  JOIN obs o ON ee.encounter_id = o.encounter_id   "
            + "        WHERE   "
            + "            (  "
            + "                (o.concept_id = ${2031} AND o.value_coded = ${1366}) OR  "
            + "                (o.concept_id = ${23944} AND o.value_coded = ${1366}) OR  "
            + "                (o.concept_id = ${23945} AND o.value_coded = ${1366})  "
            + "            )   "
            + "            AND o.voided=0  "
            + "            AND ee.voided = 0  "
            + "            GROUP BY  max_date.patient_id "
            + " "
            + "        UNION "
            + " "
            + "        SELECT  p.patient_id, MAX(e.encounter_datetime) last_date "
            + "        FROM patient p   "
            + "            INNER JOIN encounter e   "
            + "                ON e.patient_id=p.patient_id   "
            + "            INNER JOIN obs o   "
            + "                ON o.encounter_id=e.encounter_id   "
            + "        WHERE e.encounter_type = ${6}  "
            + "            AND e.encounter_datetime <= :endDate  "
            + "            AND o.concept_id = ${6273} "
            + "            AND o.value_coded= ${1366}   "
            + "            AND e.location_id =  :location   "
            + "            AND p.voided=0    "
            + "            AND e.voided=0   "
            + "            AND o.voided=0   "
            + "        GROUP BY p.patient_id  "
            + "        UNION  "
            + "        SELECT  p.patient_id, MAX(o.obs_datetime) AS last_date "
            + "        FROM patient p   "
            + "            INNER JOIN encounter e   "
            + "                ON e.patient_id=p.patient_id   "
            + "            INNER JOIN obs o   "
            + "                ON o.encounter_id=e.encounter_id   "
            + "        WHERE e.encounter_type = ${53}   "
            + "            AND o.obs_datetime <= :endDate  "
            + "            AND o.concept_id = ${6272}  "
            + "            AND o.value_coded=${1366}  "
            + "            AND e.location_id =  :location   "
            + "            AND p.voided=0    "
            + "            AND e.voided=0   "
            + "            AND o.voided=0   "
            + "        GROUP BY p.patient_id  "
            + " "
            + "        UNION "
            + " "
            + "        SELECT p.person_id AS patient_id, MAX(p.death_date) AS last_date   "
            + "        FROM person p  "
            + "        WHERE p.dead=1  "
            + "            AND p.death_date <= :endDate  "
            + "            AND p.voided=0 "
            + "        GROUP BY patient_id "
            + "        ) AS mostrecent "
            + "    GROUP BY mostrecent.patient_id "
            + "    ) AS outter "
            + "      INNER JOIN encounter e ON e.patient_id = outter.patient_id  "
            + "      INNER JOIN obs obss ON obss.encounter_id=e.encounter_id  "
            + "WHERE e.voided=0  "
            + "    AND obss.voided=0  "
            + "    AND   (( "
            + "                e.encounter_type IN (${6},${9},${18})  "
            + "                AND  e.encounter_datetime >  outter.l_date   "
            + "                AND e.encounter_datetime <= :endDate   "
            + "            )  "
            + "          OR  "
            + "            (  "
            + "                e.encounter_type = ${52}  "
            + "                AND obss.concept_id= ${23866}  "
            + "                AND  obss.value_datetime > outter.l_date  "
            + "                AND obss.value_datetime <= :endDate   "
            + "            ))    "
            + "    AND e.location_id =   :location  "
            + "GROUP BY outter.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * * <b>Description: A -</b> “Untraced” patients as following (Part II): * *
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>All Patients without “Patient Visit Card” <b>(encounterType_id 21 or 36 or 37)</b>
   * registered between most recent scheduled date (as below) and the reporting end date with the
   * following observations:
   *
   * <ul>
   *   <li>Type of Visit: <b>concept id= 1981 value = concept_id = 2160</b> (Busca) AND
   *   <li>Second Attempt: <b>concept_id = 6254</b> any value OR
   *   <li>Third Attempt: <b>concept_id = 6255</b> any value OR
   *   <li>Patient Found: <b>concept_id = 2003</b> any value OR
   *   <li>Defaulting Motive: <b>concept_id = 2016</b> any value OR
   *   <li>Report Visit: <b>concept_ids =  2158, 2157</b> any value OR
   *   <li>Patient Found Forwarded: <b>concept_id = 1272</b> any value OR
   *   <li>Reason of not finding: <b>concept_id =  2031</b> any value OR
   *   <li>Who gave the information: <b>concept_id = 2037</b> any value OR
   *   <li>Card Delivery Date: <b>concept_id = 2180</b> any value)
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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

  /**
   * * <b>Description: A-</b> “Untraced” patients as following (Part I): * *
   *
   * <p><b>Technical Specs:</b>
   *
   * <p><b>1 -</b> All Patients without “Patient Visit Card” (Encounter type 21 or 36 or 37)
   * registered between the most recent scheduled date (as below) and the reporting end date
   *
   * @return {@link CohortDefinition}
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

  /**
   * <b><b>Description:</b> All patients who missed (by 28 days) the last scheduled clinical
   * appointment or last drugs pick up-FILA or 30 days
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * All patients with the most recent date between the
   *
   * <p><b>(1)</b> last scheduled Drug pick up <b>(concept_id = 5096)</b> date from last Fila form
   * <b>(EncounterType_id = 18)</b> and the
   *
   * <p><b>(2)</b> last scheduled consultation date (concept_id = 1410) from last Ficha Seguimento
   * or Ficha Clinica Form <b>(encounterType_id = 6 or 9)</b> and
   *
   * <p><b>(3)</b> 30 days after the last ART pickup date <b>(concept_id = 23866)</b> from last
   * Recepcao – Levantou ARV Form <b>(encounterType_id = 52)</b>
   *
   * <p>Adding 28 days and this date is less than the reporting end Date and greater and equal than
   * start date minus 1 day
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
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
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Patients who have Reason Patient Missed Visit <b>(obs concept_id = 2016)</b>
   *
   * <ul>
   *   <li>As “Transferido para outra US <b>(concept_id = 1706)</b>” or “Auto-transferencia
   *       <b>(concept_id = 2363)</b>” marked last Home Visit Card <b>(EncounterType_id =21)</b>
   *       occurred during the reporting period.
   *       <p>Use the “data da visita” when the patient reason was marked on the home visit card as
   *       the relference date
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link SqlCohortDefinition}
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
   * <b>Description:</b> Patients Who have refused or Stopped Treatment Query
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Patients where Home Visit Card <b>(EncounterType_id =21)</b> where Reason Patient Missed Visit
   * <b>(obs concept_id = 2016)</b> in answers =
   *
   * <ul>
   *   <li>Patient Forgot Visit Date <b>(2005)</b>
   *   <li>Patient Is Bedridden At Home <b>(2006)</b>
   *   <li>Distance Or Money For Transport Is To Much For Patient <b>(2007)</b>
   *   <li>Patient Is Dissatisfied With Day Hospital Services <b>(2010)</b>
   *   <li>Fear Of The Provider (23915)absence Of Health Provider In Health Unit <b>(23946)</b>
   *   <li>Patient Does Not Like Arv Treatment Side Effects <b>(2015)</b>
   *   <li>Patient Is Treating Hiv With Traditional Medicine <b>(2013)</b>
   *   <li>Other Reason Why Patient Missed Visit <b>(2017)</b>
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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

  /**
   * <b>Description:</b> TRACED PATIENTS AND NOT FOUND
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * All Patients with “Patient Visit Card” <b>(encounterType 21 or 36 or 37)</b> with "NO" answer
   * <b>(concept_id 1066)</b> for Patient Found <b>(concept_id = 2003)</b>
   *
   * <p>Registered between the most recent scheduled date (as below) and the reporting end date with
   * the following information
   *
   * <p>With For Reason Not Found <b>(obs concept (id = 2031 or id = 23944 or id = 23945))</b> and
   * Answer <b>(id = 2024 or id = 2026 or id = 2011 or id = 2032)</b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsTracedAndNotFound() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("Get patients traced (Unable to locate) and Not Found");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        TxMlQueries.getPatientsTracedWithReasonNotFound(
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
            hivMetadata.getReasonPatientNotFound().getConceptId(),
            hivMetadata.getDefaultingMotiveConcept().getConceptId(),
            hivMetadata.getReasonForStoppedTakingArvDrugsDuringLast7DaysConcept().getConceptId(),
            hivMetadata.getReasonForStoppedTakingArvDrugsDuringLastMonthConcept().getConceptId(),
            hivMetadata.getMainReasonForDelayInTakingArvConcept().getConceptId(),
            hivMetadata.getPatientRecordHasWrongAddressConcept().getConceptId(),
            hivMetadata.getPatientMovedHousesConcept().getConceptId(),
            hivMetadata.getPatientTookATripConcept().getConceptId(),
            hivMetadata.getOtherReasonsWhyPatientWasNotLocatedByActivistConcept().getConceptId(),
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
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * All patients who have been on treatment for less than 90 days since the date initiated ARV
   * treatment to the date of their last scheduled clinical contact (the last scheduled clinical
   * appointment or last drugs pick up-FILA or 30 days after last drugs pick-up-MasterCard (the most
   * recent one from 3 sources) by reporting end date)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsOnARTForLessOrMoreThan180Days(Integer periodFlag) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "lessThanOrMoreThan90DaysPatients",
            Context.getRegisteredComponents(StartedArtOnLastClinicalContactCalculation.class)
                .get(0));

    cd.addCalculationParameter("periodFlag", periodFlag);
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <b>All patients who after the most recent date from below criterias:</b>
   *
   * <p><b>(Patient_program.program_id = 2)</b> = (SERVICO TARV-TRATAMENTO) and Patient_State.state
   * = 7 (Transferred-out) or Patient_State.start_date <= endDate Patient_state.end_date is null And
   *
   * <p><b>encounterType_id = 53</b>, Estado de Permanencia <b>(concept Id 6272)</b> =
   * Transferred-out <b>(concept_id = 1706)</b> obs_datetime <= endDate OR Encounter Type ID= 6
   * Estado de Permanencia <b>(concept_id 6273)</b> = Transferred-out <b>(concept_id 1706)</b>
   * Encounter_datetime <= endDate And
   *
   * <p><b>EncounterType_id = 21</b>, Last Encounter_datetime <=endDate Reason Patient Missed Visit
   * <b>(obs concept id = 2016)</b> Answers = "Transferred Out To Another Facility" <b>(id =
   * 1706)</b> OR "Auto Transfer" <b>(id = 23863)</b> have a drugs pick up or consultation
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * All deaths <b>(Patient_State.state = 10)</b> in ART Service Program
   * <b>(Patient_program.program_id = 2)</b> registered in Patient Program State by reporting end
   * date
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * All Transferred-out <b>(Patient_State.state = 7)</b> in ART Service Program
   * <b>(Patient_program.program_id = 2)</b> registered in Patient Program State by reporting end
   * date
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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

  /**
   * <b>Description:</b> “Interruption In Treatment for >6 months” will have the following
   * combination:
   *
   * <ul>
   *   <li>((A OR B) AND C3) AND NOT Dead AND NOT Transferred-Out AND NOT Refused
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsIITMoreThan180DaysComposition() {
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
        "C3",
        EptsReportUtils.map(
            getPatientsOnARTForLessOrMoreThan180Days(3),
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
        "(missedAppointment AND C3) AND NOT dead AND NOT transferredOut AND NOT refusedOrStopped");
    return cd;
  }

  /**
   * <b>Description:</b> “Interruption In Treatment for <3 months” will have the following
   * combination: ((A OR B) AND C1) AND NOT DEAD AND NOT TRANSFERRED OUT AND NOT REFUSED
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsIITLessThan90DaysComposition() {
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
        "C1",
        EptsReportUtils.map(
            getPatientsOnARTForLessOrMoreThan180Days(1),
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
        "(missedAppointment AND C1) AND NOT dead AND NOT transferredOut AND NOT refusedOrStopped");

    return cd;
  }

  /**
   * <b>Description:</b> “Interruption In Treatment between 3-5 months” will have the following
   * combination:
   *
   * <ul>
   *   <li>((A OR B) AND C2) AND NOT Dead AND NOT Transferred-Out AND NOT Refused
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsIITBetween90DaysAnd180DaysComposition() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Get patients who are Lost To Follow Up Composition between 3-5 months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "missedAppointment",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNoScheduledDrugPickupOrNextConsultation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "C2",
        EptsReportUtils.map(
            getPatientsOnARTForLessOrMoreThan180Days(2),
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
        "(missedAppointment AND C2) AND NOT dead AND NOT transferredOut AND NOT refusedOrStopped");
    return cd;
  }

  /**
   * <b>Description:</b> “Interruption In Treatment Total” will have the following combination:
   *
   * <ul>
   *   <li>(IIT<3month OR IITBetween3-5month OR IIT>6months)
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithIITComposition() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Get All patients who are Lost To Follow Up Composition");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "missedAppointment",
        EptsReportUtils.map(
            getPatientsWhoMissedNextAppointmentAndNoScheduledDrugPickupOrNextConsultation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "C1",
        EptsReportUtils.map(
            getPatientsOnARTForLessOrMoreThan180Days(1),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "C2",
        EptsReportUtils.map(
            getPatientsOnARTForLessOrMoreThan180Days(2),
            "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "C3",
        EptsReportUtils.map(
            getPatientsOnARTForLessOrMoreThan180Days(3),
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
        "(missedAppointment AND (C1 OR C2 OR C3)) AND NOT dead AND NOT transferredOut AND NOT refusedOrStopped");
    return cd;
  }
}
