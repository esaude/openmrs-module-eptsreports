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
package org.openmrs.module.eptsreports.reporting.calculation.resumo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

@Component
public class ExcludeCriteriaForECalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    Location location = (Location) context.getFromCache("location");
    Date startDate = (Date) context.getFromCache("onOrAfter");
    Date endDate = (Date) context.getFromCache("onOrBefore");

    Date requiredDate;

    CalculationResultMap map = new CalculationResultMap();
    // External Dependencies
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);

    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH);
    Concept concept = (Concept) parameterValues.get("concept");
    EncounterType encounterType = (EncounterType) parameterValues.get("encounterType");
    String type = (String) parameterValues.get("type");
    String limit = (String) parameterValues.get("limit");
    String option = (String) parameterValues.get("option");

    if (!(day == 21 && month == 11)) {
      requiredDate = EptsCalculationUtils.addMonths(startDate, -12);
    } else {
      requiredDate = startDate;
    }
    CalculationResultMap calculationResultMap =
        ePTSCalculationService.getObs(
            concept,
            Arrays.asList(encounterType),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            requiredDate,
            context);
    CalculationResultMap encountersMap =
        ePTSCalculationService.allEncounters(
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
            cohort,
            location,
            requiredDate,
            endDate,
            context);
    for (Integer pId : cohort) {
      boolean toExclude = false;

      ListResult listResult = (ListResult) calculationResultMap.get(pId);
      List<Obs> obsList = EptsCalculationUtils.extractResultValues(listResult);
      if (option.equals("encounter") && encountersMap.containsKey(pId)) {
        toExclude = true;

      } else if (option.equals("obs")) {
        for (Obs obs : obsList) {
          if (type.equals("CODED")
              && obs.getValueCoded() != null
              && obs.getValueCoded().equals(hivMetadata.getHivViralLoadConcept())) {
            toExclude = true;
            break;
          } else if (type.equals("CODED") && obs.getValueCoded() != null) {
            toExclude = true;
            break;
          } else if (type.equals("NUMERIC")
              && obs.getValueNumeric() != null
              && limit.equals("YES")
              && obs.getValueNumeric() < 1000) {
            toExclude = true;
            break;
          } else if (type.equals("NUMERIC") && obs.getValueNumeric() != null) {
            toExclude = true;
            break;
          }
        }

        if (calculationResultMap.containsKey(pId)) {
          toExclude = true;
        }
      }
      map.put(pId, new BooleanResult(toExclude, this));
    }
    return map;
  }
}
