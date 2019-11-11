package org.openmrs.module.eptsreports.reporting.calculation.eri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiPatientStateDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ERIDeadPatientsCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap deadInProgram = getDeadInProgram(cohort, context);
    CalculationResultMap deadInDemographics = getDeadInDemographics(cohort, context);
    CalculationResultMap deadInVisitCard = getDeadInVisitCard(cohort, context);
    CalculationResultMap deadInFollowUp = getDeadInFollowUp(cohort, context);
    CalculationResultMap deadInMastercard = getDeadInMastercard(cohort, context);
    CalculationResultMap followUpOrPharmacy = getFollowUpOrPharmacy(cohort, context);
    CalculationResultMap mastercardDrugPickup = getMastercardDrugPickup(cohort, context);

    CalculationResultMap resultMap = new CalculationResultMap();

    for (Integer pId : cohort) {
      boolean exclude = false;

      Date deathDate =
          getMostRecent(
              pId,
              deadInProgram,
              deadInDemographics,
              deadInVisitCard,
              deadInFollowUp,
              deadInMastercard);

      if (deathDate == null) {
        continue;
      }

      for (CalculationResultMap c : Arrays.asList(followUpOrPharmacy, mastercardDrugPickup)) {
        if (!c.isEmpty(pId)) {
          Date encounterOrPickup = c.get(pId).asType(Date.class);
          if (encounterOrPickup.after(deathDate)) {
            exclude = true;
            break;
          }
        }
      }

      if (!exclude) {
        resultMap.put(pId, new SimpleResult(deathDate, this));
      }
    }

    return resultMap;
  }

  private Date getMostRecent(Integer pId, CalculationResultMap... dateCalculations) {

    List<Date> dates = new ArrayList<>();

    for (CalculationResultMap c : dateCalculations) {
      if (!c.isEmpty(pId)) {
        dates.add(c.get(pId).asType(Date.class));
      }
    }

    return dates.size() > 0 ? Collections.max(dates) : null;
  }

  private CalculationResultMap getDeadInProgram(
      Collection<Integer> cohort, PatientCalculationContext context) {
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    Location location = (Location) context.getFromCache("location");
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    ProgramWorkflowState patientHasDiedWorkflowState = hivMetadata.getPatientHasDiedWorkflowState();
    JembiPatientStateDefinition def = new JembiPatientStateDefinition();
    def.setLocation(location);
    def.setStartedOnOrBefore(onOrBefore);
    def.setWhich(TimeQualifier.LAST);
    def.setStates(Collections.singletonList(patientHasDiedWorkflowState));
    PropertyConverter converter = new PropertyConverter(PatientState.class, "startDate");
    ConvertedPatientDataDefinition startDate = new ConvertedPatientDataDefinition(def, converter);
    return EptsCalculationUtils.evaluateWithReporting(startDate, cohort, null, null, context);
  }

  /**
   * @param cohort
   * @param context
   * @return A CalculationResultMap of {@link VitalStatus} for each dead patient
   */
  private CalculationResultMap getDeadInDemographics(
      Collection<Integer> cohort, PatientCalculationContext context) {
    VitalStatusDataDefinition def = new VitalStatusDataDefinition();

    CalculationResultMap vitalStatus =
        EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);

    CalculationResultMap deadMap = new CalculationResultMap();
    for (Integer pId : cohort) {
      VitalStatus v = EptsCalculationUtils.resultForPatient(vitalStatus, pId);
      if (v != null && v.getDead()) {
        deadMap.put(pId, new SimpleResult(v.getDeathDate(), null));
      }
    }

    return deadMap;
  }

  /**
   * @param cohort
   * @param context
   * @return Patients not found during last home visit card because they had passed away
   */
  private CalculationResultMap getDeadInVisitCard(
      Collection<Integer> cohort, PatientCalculationContext context) {
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EncounterType buscaActiva = hivMetadata.getBuscaActivaEncounterType();
    EncounterType visitaApoioReintegracaoParteA =
        hivMetadata.getVisitaApoioReintegracaoParteAEncounterType();
    EncounterType visitaApoioReintegracaoParteB =
        hivMetadata.getVisitaApoioReintegracaoParteBEncounterType();
    Concept patientFound = hivMetadata.getPatientFoundConcept();
    Concept no = hivMetadata.getNoConcept();
    Concept reasonPatientNotFound = hivMetadata.getReasonPatientNotFound();
    Concept dead = hivMetadata.getPatientIsDead();

    SqlPatientDataDefinition def = new SqlPatientDataDefinition();
    def.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    String sql =
        "SELECT p.patient_id, e.encounter_datetime "
            + "FROM   patient p "
            + "           JOIN encounter e "
            + "                ON p.patient_id = e.patient_id "
            + "           JOIN (SELECT p.patient_id, "
            + "                        Max(e.encounter_datetime) encounter_datetime "
            + "                 FROM   patient p "
            + "                            JOIN encounter e "
            + "                                 ON p.patient_id = e.patient_id "
            + "                 WHERE  p.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND e.encounter_type IN ( %d, %d, %d ) "
            + "                   AND e.encounter_datetime <= :onOrBefore "
            + "                 GROUP  BY p.patient_id) lst "
            + "                ON e.patient_id = lst.patient_id "
            + "                    AND e.encounter_datetime = lst.encounter_datetime "
            + "           JOIN obs notfound "
            + "                ON e.encounter_id = notfound.encounter_id "
            + "           JOIN obs reason "
            + "                ON e.encounter_id = reason.encounter_id "
            + "WHERE  notfound.voided = 0 "
            + "  AND notfound.concept_id = %d "
            + "  AND notfound.value_coded = %d "
            + "  AND reason.voided = 0 "
            + "  AND reason.concept_id = %d "
            + "  AND reason.value_coded = %d "
            + "  AND p.patient_id in (:patientIds)";

    def.setSql(
        String.format(
            sql,
            buscaActiva.getEncounterTypeId(),
            visitaApoioReintegracaoParteA.getEncounterTypeId(),
            visitaApoioReintegracaoParteB.getEncounterTypeId(),
            patientFound.getConceptId(),
            no.getConceptId(),
            reasonPatientNotFound.getConceptId(),
            dead.getConceptId()));

    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  private CalculationResultMap getDeadInFollowUp(
      Collection<Integer> cohort, PatientCalculationContext context) {
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    EncounterType adultoSeguimento = hivMetadata.getAdultoSeguimentoEncounterType();
    Concept stateOfStay = hivMetadata.getStateOfStayOfArtPatient();
    Concept died = hivMetadata.getPatientHasDiedConcept();

    return getBasedOnStateOfStay(cohort, context, adultoSeguimento, stateOfStay, died);
  }

  private CalculationResultMap getDeadInMastercard(
      Collection<Integer> cohort, PatientCalculationContext context) {
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    EncounterType masterCard = hivMetadata.getMasterCardEncounterType();
    Concept stateOfStay = hivMetadata.getStateOfStayOfArtPatient();
    Concept died = hivMetadata.getPatientHasDiedConcept();

    return getBasedOnStateOfStay(cohort, context, masterCard, stateOfStay, died);
  }

  private CalculationResultMap getBasedOnStateOfStay(
      Collection<Integer> cohort,
      PatientCalculationContext context,
      EncounterType encounterType,
      Concept stateOfStay,
      Concept valueCoded) {

    Location location = (Location) context.getFromCache("location");
    Date onOrAfter = (Date) context.getFromCache("onOrAfter");
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");

    ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
    def.setWhich(TimeQualifier.LAST);
    def.setOnOrBefore(onOrBefore);
    def.setOnOrAfter(onOrAfter);
    def.addEncounterType(encounterType);
    def.setQuestion(stateOfStay);
    def.addValueCoded(valueCoded);
    def.addLocation(location);

    PropertyConverter converter = new PropertyConverter(Obs.class, "encounter.encounterDatetime");
    ConvertedPersonDataDefinition encounterDatetime =
        new ConvertedPersonDataDefinition(null, def, converter);

    return EptsCalculationUtils.evaluateWithReporting(
        encounterDatetime, cohort, null, null, context);
  }

  private CalculationResultMap getFollowUpOrPharmacy(
      Collection<Integer> cohort, PatientCalculationContext context) {
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    Location location = (Location) context.getFromCache("location");

    EncounterType adultoSeguimento = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType arvPediatriaSeguimento = hivMetadata.getARVPediatriaSeguimentoEncounterType();
    EncounterType arvPharmacia = hivMetadata.getARVPharmaciaEncounterType();

    EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
    def.addType(adultoSeguimento);
    def.addType(arvPediatriaSeguimento);
    def.addType(arvPharmacia);
    def.setLocationList(Collections.singletonList(location));

    PropertyConverter converter = new PropertyConverter(Encounter.class, "encounterDatetime");
    ConvertedPatientDataDefinition encounterDatetime =
        new ConvertedPatientDataDefinition(def, converter);

    return EptsCalculationUtils.evaluateWithReporting(
        encounterDatetime, cohort, null, null, context);
  }

  private CalculationResultMap getMastercardDrugPickup(
      Collection<Integer> cohort, PatientCalculationContext context) {

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    Location location = (Location) context.getFromCache("location");

    EncounterType masterCard = hivMetadata.getMasterCardDrugPickupEncounterType();
    Concept pickUpDate = hivMetadata.getArtDatePickup();

    ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
    def.setEncounterTypeList(Collections.singletonList(masterCard));
    def.setWhich(TimeQualifier.LAST);
    def.setQuestion(pickUpDate);
    def.setLocationList(Collections.singletonList(location));

    PropertyConverter converter = new PropertyConverter(Obs.class, "valueDatetime");
    ConvertedPersonDataDefinition valueDatetime =
        new ConvertedPersonDataDefinition(null, def, converter);

    return EptsCalculationUtils.evaluateWithReporting(valueDatetime, cohort, null, null, context);
  }
}
