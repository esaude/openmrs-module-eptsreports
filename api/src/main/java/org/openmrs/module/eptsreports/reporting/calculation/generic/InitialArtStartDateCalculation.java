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

import static org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils.latestCalculationMapDateValuePerPatient;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.openmrs.Location;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.data.definition.InitialArtStartDateDataDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

/** Calculates the date on which a patient first started ART */
@Component
public class InitialArtStartDateCalculation extends AbstractPatientCalculation {

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
    Location location = (Location) context.getFromCache("location");

    if (onOrBefore == null) {
      throw new IllegalArgumentException(String.format("Parameter %s must be set", ON_OR_BEFORE));
    }

    CalculationResultMap map = new CalculationResultMap();

    InitialArtStartDateDataDefinition initialArtStartDateDefinition =
        new InitialArtStartDateDataDefinition();
    initialArtStartDateDefinition.setName("Patients with Initial date of ART initiation");
    initialArtStartDateDefinition.setOnOrBefore(onOrBefore);
    initialArtStartDateDefinition.setLocation(location);

    CalculationResultMap ret =
        EptsCalculationUtils.evaluateWithReporting(
            initialArtStartDateDefinition, cohort, parameterValues, null, context);

    CalculationResultMap latestArtStartDateMaps = latestCalculationMapDateValuePerPatient(ret);

    for (Integer pId : cohort) {
      Date actualArtStartDate = null;
      CalculationResult latestDateResult = latestArtStartDateMaps.get(pId);
      if (latestDateResult != null) {
        actualArtStartDate = (Date) latestDateResult.getValue();
      }
      map.put(pId, new SimpleResult(actualArtStartDate, this));
    }
    return map;
  }

  public static Date getArtStartDate(Integer patientId, CalculationResultMap artStartDates) {
    CalculationResult calculationResult = artStartDates.get(patientId);
    if (calculationResult != null) {
      return (Date) calculationResult.getValue();
    }
    return null;
  }
}
