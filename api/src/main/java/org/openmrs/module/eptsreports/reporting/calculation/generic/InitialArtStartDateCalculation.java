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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Calculates the date on which a patient first started ART */
@Component
public class InitialArtStartDateCalculation extends AbstractPatientCalculation {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private EPTSCalculationService ePTSCalculationService;

  private static final String ON_OR_BEFORE = "onOrBefore";

  /**
   * should return null for patients who have not started ART should return start date for patients
   * who have started ART
   *
   * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
   *     java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
   */
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    Date onOrBefore = (Date) context.getFromCache(ON_OR_BEFORE);

    if (onOrBefore == null) {
      throw new IllegalArgumentException(String.format("Parameter %s must be set", ON_OR_BEFORE));
    }

    CalculationResultMap map = new CalculationResultMap();
    Location location = (Location) context.getFromCache("location");

    boolean considerTransferredIn = getBooleanParameter(parameterValues, "considerTransferredIn");
    boolean considerPharmacyEncounter =
        getBooleanParameter(parameterValues, "considerPharmacyEncounter");

    Program treatmentProgram = hivMetadata.getARTProgram();
    Concept arvPlan = hivMetadata.getARVPlanConcept();
    Concept startDrugsConcept = hivMetadata.getStartDrugsConcept();
    Concept transferInConcept = hivMetadata.getTransferFromOtherFacilityConcept();
    Concept hostoricalStartConcept = commonMetadata.getHistoricalDrugStartDateConcept();
    EncounterType encounterTypePharmacy = hivMetadata.getARVPharmaciaEncounterType();
    EncounterType masterCardFichaResumo = hivMetadata.getMasterCardEncounterType();
    EncounterType adultoSeguimento = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType arvPediatriaSeguimento = hivMetadata.getPediatriaSeguimentoEncounterType();

    List<EncounterType> encounterTypes =
        Arrays.asList(
            encounterTypePharmacy, adultoSeguimento, arvPediatriaSeguimento, masterCardFichaResumo);

    CalculationResultMap inProgramMap =
        ePTSCalculationService.firstPatientProgram(treatmentProgram, location, cohort, context);
    CalculationResultMap startDrugMap =
        ePTSCalculationService.firstObs(
            arvPlan,
            startDrugsConcept,
            location,
            true,
            null,
            null,
            encounterTypes,
            cohort,
            context);
    CalculationResultMap historicalMap =
        ePTSCalculationService.firstObs(
            hostoricalStartConcept,
            null,
            location,
            false,
            null,
            null,
            encounterTypes,
            cohort,
            context);
    CalculationResultMap pharmacyEncounterMap =
        ePTSCalculationService.firstEncounter(
            Arrays.asList(encounterTypePharmacy), cohort, location, context);
    CalculationResultMap transferInMap =
        ePTSCalculationService.firstObs(
            arvPlan, transferInConcept, location, true, null, null, null, cohort, context);

    CalculationResultMap drugPickupMap = getMastercardDrugPickupMap(cohort, context);

    for (Integer pId : cohort) {
      Date requiredDate = null;
      List<Date> enrollmentDates = new ArrayList<>();
      SimpleResult result = (SimpleResult) inProgramMap.get(pId);
      if (result != null) {
        PatientProgram patientProgram = (PatientProgram) result.getValue();
        enrollmentDates.add(patientProgram.getDateEnrolled());
      }
      Obs startDrugsObs = EptsCalculationUtils.resultForPatient(startDrugMap, pId);
      if (startDrugsObs != null) {
        enrollmentDates.add(startDrugsObs.getEncounter().getEncounterDatetime());
      }
      Obs historicalDateObs = EptsCalculationUtils.resultForPatient(historicalMap, pId);
      if (historicalDateObs != null) {
        enrollmentDates.add(historicalDateObs.getValueDatetime());
      }
      Date drugPickup = EptsCalculationUtils.resultForPatient(drugPickupMap, pId);
      if (drugPickup != null) {
        enrollmentDates.add(drugPickup);
      }
      if (considerPharmacyEncounter) {
        Encounter pharmacyEncounter =
            EptsCalculationUtils.resultForPatient(pharmacyEncounterMap, pId);
        if (pharmacyEncounter != null) {
          enrollmentDates.add(pharmacyEncounter.getEncounterDatetime());
        }
      }
      if (considerTransferredIn) {
        Obs transferInObs = EptsCalculationUtils.resultForPatient(transferInMap, pId);
        if (transferInObs != null) {
          enrollmentDates.add(transferInObs.getObsDatetime());
        }
      }
      enrollmentDates.removeAll(Collections.singleton(null));
      if (enrollmentDates.size() > 0) {
        Collections.sort(enrollmentDates);
        requiredDate = enrollmentDates.get(0);
      }
      map.put(pId, new SimpleResult(requiredDate, this));
    }
    return map;
  }

  private boolean getBooleanParameter(Map<String, Object> parameterValues, String parameterName) {
    Boolean parameterValue = null;
    if (parameterValues != null) {
      parameterValue = (Boolean) parameterValues.get(parameterName);
    }
    if (parameterValue == null) {
      parameterValue = true;
    }
    return parameterValue;
  }

  public static Date getArtStartDate(Integer patientId, CalculationResultMap artStartDates) {
    CalculationResult calculationResult = artStartDates.get(patientId);
    if (calculationResult != null) {
      return (Date) calculationResult.getValue();
    }
    return null;
  }

  private CalculationResultMap getMastercardDrugPickupMap(
      Collection<Integer> cohort, PatientCalculationContext context) {
    SqlPatientDataDefinition def = new SqlPatientDataDefinition();
    def.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    def.addParameter(new Parameter("location", "location", Location.class));
    String sql =
        "SELECT p.patient_id, "
            + "       Min(pickupdate.value_datetime) value_datetime "
            + "FROM patient p "
            + "         JOIN encounter e "
            + "              ON p.patient_id = e.patient_id "
            + "         JOIN obs pickupdate on e.encounter_id = pickupdate.encounter_id "
            + "         JOIN obs pickup on e.encounter_id = pickup.encounter_id "
            + "WHERE p.voided = 0 "
            + "  AND p.patient_id in (:patientIds)"
            + "  AND e.voided = 0 "
            + "  AND e.encounter_type = %d "
            + "  AND e.location_id = :location "
            + "  AND pickup.voided = 0 "
            + "  AND pickup.concept_id = %d "
            + "  AND pickup.value_coded = %d "
            + "  AND pickupdate.voided = 0 "
            + "  AND pickupdate.concept_id = %d "
            + "  AND pickupdate.value_datetime <= :onOrBefore "
            + "GROUP BY p.patient_id;";

    def.setSql(
        String.format(
            sql,
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtPickupConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

    Map<String, Object> params = new HashMap<>();
    params.put("location", context.getFromCache("location"));
    params.put("onOrBefore", context.getFromCache("onOrBefore"));
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, params, null, context);
  }
}
