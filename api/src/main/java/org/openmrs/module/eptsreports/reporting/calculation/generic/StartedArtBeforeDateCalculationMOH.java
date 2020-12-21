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

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.MohMQInitiatedARTDuringTheInclusionPeriodCalculation;
import org.springframework.stereotype.Component;

/** Returns the patients that have started ART before a specific date */
@Component
public class StartedArtBeforeDateCalculationMOH extends AbstractPatientCalculation {

  private static final String ON_OR_BEFORE = "onOrBefore";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();
    CalculationResultMap artStartDates =
        calculate(
            Context.getRegisteredComponents(
                    MohMQInitiatedARTDuringTheInclusionPeriodCalculation.class)
                .get(0),
            cohort,
            parameterValues,
            context);
    Date endDate = (Date) parameterValues.get(ON_OR_BEFORE);
    if (endDate == null) {
      endDate = (Date) context.getFromCache(ON_OR_BEFORE);
    }
    if (endDate != null) {
      for (Integer patientId : cohort) {
        Date artStartDate =
            InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
        if (artStartDate != null && artStartDate.compareTo(endDate) <= 0) {
          map.put(patientId, new BooleanResult(true, this));
        }
      }
      return map;
    } else {
      throw new IllegalArgumentException(String.format("Parameter %s must be set", ON_OR_BEFORE));
    }
  }
}
