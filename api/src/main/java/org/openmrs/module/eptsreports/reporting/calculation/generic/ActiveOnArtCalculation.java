/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientState;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * If the patient is currently on ART
 *
 * @return a CulculationResultMap
 */
@Component
public class ActiveOnArtCalculation extends AbstractPatientCalculation {

  private static final int TOLERANCE_FOR_NEXT_CONSULTATION = 61;

  private static final int TOLERANCE_FOR_NEXT_PICKUP = 60;

  private static final String ON_OR_BEFORE = "onOrBefore";

  @Autowired private EPTSCalculationService eptsCalculationService;

  @Autowired private HivMetadata hivMetadata;

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();
    CalculationResultMap startedArtBeforeDate =
        calculate(
            Context.getRegisteredComponents(StartedArtBeforeDateCalculation.class).get(0),
            cohort,
            parameterValues,
            context);
    Location location = (Location) context.getFromCache("location");
    Date endDate = (Date) parameterValues.get(ON_OR_BEFORE);
    if (endDate == null) {
      endDate = (Date) context.getFromCache(ON_OR_BEFORE);
    }
    CalculationResultMap abandonments =
        eptsCalculationService.patientStatesBeforeDate(
            cohort,
            location,
            endDate,
            Arrays.asList(
                hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState(),
                hivMetadata.getSuspendedTreatmentWorkflowState(),
                hivMetadata.getPatientHasDiedWorkflowState(),
                hivMetadata.getAbandonedWorkflowState()),
            context);
    final List<EncounterType> pharmacyEncounterTypes =
        Arrays.asList(hivMetadata.getARVPharmaciaEncounterType());
    final List<EncounterType> consultationEncounterTypes =
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType());
    CalculationResultMap mostRecentPharmacyEncounter =
        eptsCalculationService.lastEncounterBeforeDate(
            pharmacyEncounterTypes, cohort, location, endDate, context);
    CalculationResultMap mostRecentConsultationEncounter =
        eptsCalculationService.lastEncounterBeforeDate(
            consultationEncounterTypes, cohort, location, endDate, context);
    final Concept nextPickupConcept = hivMetadata.getReturnVisitDateForArvDrugConcept();
    final Concept nextConsultationConcept = hivMetadata.getReturnVisitDateConcept();
    CalculationResultMap pharmacyObservations =
        eptsCalculationService.lastObs(
            pharmacyEncounterTypes, nextPickupConcept, location, null, endDate, cohort, context);
    CalculationResultMap consultationObservations =
        eptsCalculationService.lastObs(
            consultationEncounterTypes,
            nextConsultationConcept,
            location,
            null,
            endDate,
            cohort,
            context);
    for (Integer patientId : cohort) {
      List<PatientState> patientAbandonments =
          EptsCalculationUtils.extractResultValues((ListResult) abandonments.get(patientId));
      boolean startedArt =
          StartedArtBeforeDateCalculation.isStartedArt(patientId, startedArtBeforeDate);
      Encounter pharmacyEncounter =
          EptsCalculationUtils.resultForPatient(mostRecentPharmacyEncounter, patientId);
      Encounter consultationEncounter =
          EptsCalculationUtils.resultForPatient(mostRecentConsultationEncounter, patientId);
      Obs nextPickupObs = EptsCalculationUtils.resultForPatient(pharmacyObservations, patientId);
      Obs nextConsultationObs =
          EptsCalculationUtils.resultForPatient(consultationObservations, patientId);
      final Boolean missedPickup =
          isMissed(pharmacyEncounter, nextPickupObs, TOLERANCE_FOR_NEXT_PICKUP, endDate);
      final Boolean missedConsultation =
          isMissed(
              consultationEncounter, nextConsultationObs, TOLERANCE_FOR_NEXT_CONSULTATION, endDate);
      if (startedArt
          && patientAbandonments.isEmpty()
          && (!Boolean.TRUE.equals(missedPickup)
              || (Boolean.TRUE.equals(missedPickup) && Boolean.FALSE.equals(missedConsultation)))) {
        map.put(patientId, new BooleanResult(true, this));
      }
    }
    return map;
  }

  public Boolean isMissed(Encounter encounter, Obs obs, int days, Date endDate) {
    if (encounter != null
        && obs != null
        && encounter.getEncounterDatetime().equals(obs.getObsDatetime())) {
      Date date = obs.getValueDatetime();
      if (date != null) {
        if (date.compareTo(endDate) <= 0) {
          int difference = Days.daysIn(new Interval(date.getTime(), endDate.getTime())).getDays();
          return difference >= days;
        } else {
          return false;
        }
      }
    }
    return null;
  }

  public static boolean isActiveOnArt(
      Integer patientId, CalculationResultMap calculationResultMap) {
    BooleanResult result = (BooleanResult) calculationResultMap.get(patientId);
    if (result != null) {
      return result.getValue().equals(Boolean.TRUE);
    }
    return false;
  }
}
