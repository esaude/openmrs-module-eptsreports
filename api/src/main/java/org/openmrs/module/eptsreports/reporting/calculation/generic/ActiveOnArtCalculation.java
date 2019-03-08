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
import org.openmrs.Location;
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
    for (Integer patientId : cohort) {
      List<PatientState> patientAbandonments =
          EptsCalculationUtils.extractResultValues((ListResult) abandonments.get(patientId));
      boolean startedArt =
          StartedArtBeforeDateCalculation.isStartedArt(patientId, startedArtBeforeDate);
      if (startedArt && patientAbandonments.isEmpty()) {
        map.put(patientId, new BooleanResult(true, this));
      }
    }
    return map;
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
