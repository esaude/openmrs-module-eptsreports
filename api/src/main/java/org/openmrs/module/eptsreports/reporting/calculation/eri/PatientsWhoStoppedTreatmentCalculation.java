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
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.springframework.stereotype.Component;

@Component
public class PatientsWhoStoppedTreatmentCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap programMap = getTransferredOutViaProgram(cohort, context);
    CalculationResultMap followUpMap = getSuspendedInFollowUp(cohort, context);
    CalculationResultMap mastercardMap = getSuspendedInMastercard(cohort, context);
    CalculationResultMap followUpOrPharmacy = getFollowUpOrPharmacy(cohort, context);
    CalculationResultMap mastercardDrugPickup = getMastercardDrugPickup(cohort, context);

    CalculationResultMap resultMap = new CalculationResultMap();

    for (Integer pId : cohort) {
      boolean exclude = false;

      Date suspendedDate = getMostRecent(pId, programMap, followUpMap, mastercardMap);
      if (suspendedDate == null) {
        continue;
      }

      for (CalculationResultMap c : Arrays.asList(followUpOrPharmacy, mastercardDrugPickup)) {
        if (!c.isEmpty(pId)) {
          Date encounterOrPickup = c.get(pId).asType(Date.class);
          if (encounterOrPickup.after(suspendedDate)) {
            exclude = true;
            break;
          }
        }
      }

      if (!exclude) {
        resultMap.put(pId, new SimpleResult(suspendedDate, this));
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

  private CalculationResultMap getTransferredOutViaProgram(
      Collection<Integer> cohort, PatientCalculationContext context) {
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    ProgramWorkflowState transferredOut =
        hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState();
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    Location location = (Location) context.getFromCache("location");

    JembiPatientStateDefinition def = new JembiPatientStateDefinition();
    def.setLocation(location);
    def.setStartedOnOrBefore(onOrBefore);
    def.setStates(Collections.singletonList(transferredOut));
    def.setWhich(TimeQualifier.LAST);

    PropertyConverter converter = new PropertyConverter(PatientState.class, "startDate");
    ConvertedPatientDataDefinition startDate = new ConvertedPatientDataDefinition(def, converter);

    return EptsCalculationUtils.evaluateWithReporting(startDate, cohort, null, null, context);
  }

  private CalculationResultMap getSuspendedInFollowUp(
      Collection<Integer> cohort, PatientCalculationContext context) {

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    EncounterType adultoSeguimento = hivMetadata.getAdultoSeguimentoEncounterType();
    Concept stateOfStay = hivMetadata.getStateOfStayOfArtPatient();
    Concept suspended = hivMetadata.getSuspendedTreatmentConcept();

    return getBasedOnStateOfStay(cohort, context, adultoSeguimento, stateOfStay, suspended);
  }

  private CalculationResultMap getSuspendedInMastercard(
      Collection<Integer> cohort, PatientCalculationContext context) {
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    EncounterType adultoSeguimento = hivMetadata.getMasterCardEncounterType();
    Concept stateOfStay = hivMetadata.getStateOfStayOfPreArtPatient();
    Concept suspended = hivMetadata.getSuspendedTreatmentConcept();

    return getBasedOnStateOfStay(cohort, context, adultoSeguimento, stateOfStay, suspended);
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
