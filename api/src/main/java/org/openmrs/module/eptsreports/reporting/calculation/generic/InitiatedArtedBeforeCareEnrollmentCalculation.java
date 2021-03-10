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
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Returns the patients that have started ART before a specific date care enrollment */
@Component
public class InitiatedArtedBeforeCareEnrollmentCalculation extends AbstractPatientCalculation {

  private static final String ON_OR_AFTER = "onOrAfter";

  private static final String ON_OR_BEFORE = "onOrBefore";

  private static final String LOCATION = "location";

  @Autowired private HivMetadata hivMetadata;

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();

    PatientCalculationService service = Context.getService(PatientCalculationService.class);
    PatientCalculationContext artContext = service.createCalculationContext();

    Date onOrBefore = (Date) context.getFromCache(ON_OR_BEFORE);
    Location location = (Location) context.getFromCache(LOCATION);

    Date endDate = DateUtils.addMonths(onOrBefore, 1);

    CalculationResultMap initARTCareEnrollmentDateMap = getEarliestPreART(cohort, context);

    artContext.addToCache(ON_OR_BEFORE, endDate);
    artContext.addToCache(LOCATION, location);

    CalculationResultMap artStartDates =
        calculate(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0),
            cohort,
            parameterValues,
            artContext);

    for (Integer patientId : cohort) {
      Date artStartDate = InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
      Date preArtStartDate =
          EptsCalculationUtils.resultForPatient(initARTCareEnrollmentDateMap, patientId);
      if (artStartDate != null && preArtStartDate != null) {
        if (artStartDate.compareTo(preArtStartDate) < 0) {
          map.put(patientId, new BooleanResult(true, this));
        }
      }
    }
    return map;
  }

  public CalculationResultMap getEarliestPreART(
      Collection<Integer> cohort, PatientCalculationContext context) {
    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Get Earliest Pre-ART");
    sqlPatientDataDefinition.addParameter(new Parameter(ON_OR_AFTER, "onOrAfter", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter(ON_OR_BEFORE, "onOrBefore", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter(LOCATION, "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("1", hivMetadata.getHIVCareProgram().getId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23808", hivMetadata.getPreArtStartDate().getConceptId());

    String query =
        "    SELECT final.patient_id, final.mindate as  mdate "
            + "    FROM  "
            + "        (  "
            + "        SELECT earliest_date.patient_id ,MIN(earliest_date.min_date)  as  mindate "
            + "        FROM  "
            + "            (  "
            + "                SELECT p.patient_id, MIN(pp.date_enrolled) AS min_date  "
            + "                FROM patient p  "
            + "                    INNER JOIN patient_program pp  "
            + "                        ON pp.patient_id = p.patient_id  "
            + "                    INNER JOIN program pg  "
            + "                        ON pg.program_id = pp.program_id  "
            + "                WHERE  "
            + "                    p.voided = 0  "
            + "                    AND pp.voided = 0  "
            + "                    AND pp.date_enrolled <= :onOrBefore "
            + "                    AND pg.program_id = ${1}  "
            + "                    AND pp.location_id = :location  "
            + "                GROUP BY p.patient_id  "
            + "                UNION  "
            + "                SELECT p.patient_id, MIN(o.value_datetime) AS min_date  "
            + "                FROM patient p  "
            + "                    INNER JOIN encounter e  "
            + "                        ON e.patient_id = p.patient_id  "
            + "                    INNER JOIN obs o  "
            + "                        ON o.encounter_id = e.encounter_id  "
            + "                WHERE   "
            + "                    p.voided =0  "
            + "                    AND e.voided = 0  "
            + "                    AND o.voided = 0  "
            + "                    AND e.encounter_type = ${53}  "
            + "                    AND e.location_id = :location  "
            + "                    AND o.concept_id = ${23808} "
            + "                    AND o.value_datetime <= :onOrBefore  "
            + "                GROUP BY p.patient_id  "
            + "            ) as earliest_date  "
            + "        GROUP BY earliest_date.patient_id  "
            + "        ) as final   "
            + "    WHERE final.mindate   "
            + "        BETWEEN :onOrAfter AND :onOrBefore ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    Map<String, Object> param = new HashMap<>();
    param.put(ON_OR_AFTER, context.getFromCache(ON_OR_AFTER));
    param.put(ON_OR_BEFORE, context.getFromCache(ON_OR_BEFORE));
    param.put(LOCATION, context.getFromCache(LOCATION));

    return EptsCalculationUtils.evaluateWithReporting(
        sqlPatientDataDefinition, cohort, param, null, context);
  }
}
