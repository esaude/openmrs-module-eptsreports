package org.openmrs.module.eptsreports.reporting.calculation.txnew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
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
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiPatientStateDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

@Component
public class TxNewBreastfeedingDateCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap deliveryDateMap = getDeliveryDateInAdultInitialOrFollowUp(cohort, context);
    CalculationResultMap breastfeedingMap = getLBreastfeedingInAdultFollowUp(cohort, context);
    CalculationResultMap criteriaHivStartMap = getLactatingCriteriaForARTStart(cohort, context);
    CalculationResultMap patientStateMap = getPatientGaveBirthState(cohort, context);
    CalculationResultMap mastercardMap = getBreastfeedingInMastercardMap(cohort, context);

    CalculationResultMap resultMap = new CalculationResultMap();
    for (Integer pId : cohort) {
      Date resultantDate =
          getResultantDate(
              deliveryDateMap,
              breastfeedingMap,
              criteriaHivStartMap,
              patientStateMap,
              mastercardMap,
              pId);
      resultMap.put(pId, new SimpleResult(resultantDate, this));
    }
    return resultMap;
  }

  private Date getResultantDate(
      CalculationResultMap deliveryDateMap,
      CalculationResultMap lactatingMap,
      CalculationResultMap criteriaHivStartMap,
      CalculationResultMap patientStateMap,
      CalculationResultMap mastercardMap,
      Integer pId) {

    List<Date> allEligibleDates = new ArrayList<>();

    Obs deliveryDate = EptsCalculationUtils.resultForPatient(deliveryDateMap, pId);
    if (deliveryDate != null) {
      allEligibleDates.add(deliveryDate.getValueDatetime());
    }

    Obs lactatingObs = EptsCalculationUtils.resultForPatient(lactatingMap, pId);
    if (lactatingObs != null) {
      allEligibleDates.add(lactatingObs.getEncounter().getEncounterDatetime());
    }

    Obs criteriaForART = EptsCalculationUtils.resultForPatient(criteriaHivStartMap, pId);
    if (criteriaForART != null) {
      allEligibleDates.add(criteriaForART.getEncounter().getEncounterDatetime());
    }

    PatientState gaveBirth = EptsCalculationUtils.resultForPatient(patientStateMap, pId);
    if (gaveBirth != null) {
      allEligibleDates.add(gaveBirth.getStartDate());
    }

    Obs mastercardBreastfeedingObs = EptsCalculationUtils.resultForPatient(mastercardMap, pId);
    if (mastercardBreastfeedingObs != null) {
      allEligibleDates.add(mastercardBreastfeedingObs.getEncounter().getEncounterDatetime());
    }

    allEligibleDates.removeAll(Collections.singleton(null));

    Date resultantDate = null;
    if (allEligibleDates.size() > 0) {
      Collections.sort(allEligibleDates);
      resultantDate = allEligibleDates.get(allEligibleDates.size() - 1);
    }
    return resultantDate;
  }

  private CalculationResultMap getPatientGaveBirthState(
      Collection<Integer> cohort, PatientCalculationContext context) {
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    Date onOrAfter = (Date) context.getFromCache("onOrAfter");
    Location location = (Location) context.getFromCache("location");
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    ProgramWorkflowState gaveBirthWorkflowState = hivMetadata.getPatientGaveBirthWorkflowState();
    JembiPatientStateDefinition def = new JembiPatientStateDefinition();
    def.setLocation(location);
    def.setStartedOnOrAfter(onOrAfter);
    def.setStartedOnOrBefore(onOrBefore);
    def.setStates(Collections.singletonList(gaveBirthWorkflowState));
    def.setWhich(TimeQualifier.LAST);
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  private CalculationResultMap getDeliveryDateInAdultInitialOrFollowUp(
      Collection<Integer> cohort, PatientCalculationContext context) {

    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    Location location = (Location) context.getFromCache("location");
    Date onOrAfter = (Date) context.getFromCache("onOrAfter");

    Concept priorDeliveryDate = hivMetadata.getPriorDeliveryDateConcept();
    EncounterType adultFollowUp = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType adultInitial = hivMetadata.getARVAdultInitialEncounterType();

    return ePTSCalculationService.getObs(
        priorDeliveryDate,
        Arrays.asList(adultInitial, adultFollowUp),
        cohort,
        Collections.singletonList(location),
        null,
        TimeQualifier.LAST,
        onOrAfter,
        context);
  }

  private CalculationResultMap getLBreastfeedingInAdultFollowUp(
      Collection<Integer> cohort, PatientCalculationContext context) {

    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    Date onOrAfter = (Date) context.getFromCache("onOrAfter");
    Location location = (Location) context.getFromCache("location");
    Concept breastfeedingConcept = hivMetadata.getBreastfeeding();
    Concept yes = hivMetadata.getYesConcept();
    EncounterType adultFollowUp = hivMetadata.getAdultoSeguimentoEncounterType();

    return ePTSCalculationService.getObs(
        breastfeedingConcept,
        Collections.singletonList(adultFollowUp),
        cohort,
        Collections.singletonList(location),
        Collections.singletonList(yes),
        TimeQualifier.LAST,
        onOrAfter,
        context);
  }

  private CalculationResultMap getLactatingCriteriaForARTStart(
      Collection<Integer> cohort, PatientCalculationContext context) {

    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    Location location = (Location) context.getFromCache("location");
    Date onOrAfter = (Date) context.getFromCache("onOrAfter");

    Concept criteriaForHivStart = hivMetadata.getCriteriaForArtStart();
    Concept breastfeedingConcept = hivMetadata.getBreastfeeding();
    EncounterType adultFollowUp = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType adultInitial = hivMetadata.getARVAdultInitialEncounterType();

    return ePTSCalculationService.getObs(
        criteriaForHivStart,
        Arrays.asList(adultInitial, adultFollowUp),
        cohort,
        Collections.singletonList(location),
        Collections.singletonList(breastfeedingConcept),
        TimeQualifier.LAST,
        onOrAfter,
        context);
  }

  private CalculationResultMap getBreastfeedingInMastercardMap(
      Collection<Integer> cohort, PatientCalculationContext context) {
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    Date onOrAfter = (Date) context.getFromCache("onOrAfter");
    Location location = (Location) context.getFromCache("location");
    Concept breastfeedingConcept = hivMetadata.getBreastfeeding();
    Concept yes = hivMetadata.getYesConcept();
    EncounterType mastercardFichaResumo = hivMetadata.getMasterCardEncounterType();

    return ePTSCalculationService.getObs(
        breastfeedingConcept,
        Collections.singletonList(mastercardFichaResumo),
        cohort,
        Collections.singletonList(location),
        Collections.singletonList(yes),
        TimeQualifier.LAST,
        onOrAfter,
        context);
  }
}
