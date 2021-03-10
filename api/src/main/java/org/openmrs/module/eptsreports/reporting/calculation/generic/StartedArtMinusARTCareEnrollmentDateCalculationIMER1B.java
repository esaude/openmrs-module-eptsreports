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
import org.joda.time.Days;
import org.joda.time.Interval;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** Returns patients who initiated ART within 15 days from the ART Care Enrollment */
@Component
public class StartedArtMinusARTCareEnrollmentDateCalculationIMER1B
    extends AbstractPatientCalculation {

  private static final String ON_OR_BEFORE = "onOrBefore";
  private static final int INTERVAL_BETWEEN_ART_START_DATE_MINUS_PATIENT_ART_ENROLLMENT_DATE = 15;

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();
    CalculationResultMap artStartDates =
        calculate(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0),
            cohort,
            parameterValues,
            context);
    CalculationResultMap preARTCareEnrollmentMap = getARTCareEnrollment(cohort, context);

    Date endDate = (Date) context.getFromCache(ON_OR_BEFORE);

    if (endDate != null) {
      for (Integer patientId : cohort) {
        boolean match = false;
        Date artStartDate =
            InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);

        SimpleResult preARTCareEnrollmentResult =
            (SimpleResult) preARTCareEnrollmentMap.get(patientId);

        if (preARTCareEnrollmentResult != null) {
          Date preARTCareEnrollmentDate = (Date) preARTCareEnrollmentResult.getValue();

          if (artStartDate != null && preARTCareEnrollmentDate.compareTo(artStartDate) <= 0) {
            int days =
                Days.daysIn(
                        new Interval(preARTCareEnrollmentDate.getTime(), artStartDate.getTime()))
                    .getDays();

            if (days <= INTERVAL_BETWEEN_ART_START_DATE_MINUS_PATIENT_ART_ENROLLMENT_DATE) {
              match = true;
            }
          }
        }

        if (match) {
          map.put(patientId, new BooleanResult(match, this));
        }
      }
      return map;
    } else {
      throw new IllegalArgumentException(String.format("Parameter %s must be set", ON_OR_BEFORE));
    }
  }

  private CalculationResultMap getARTCareEnrollment(
      Collection<Integer> cohort, PatientCalculationContext context) {
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    SqlPatientDataDefinition def = new SqlPatientDataDefinition();
    def.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    def.addParameter(new Parameter("location", "location", Location.class));

    String sql =
        " SELECT final.patient_id, final.mindate AS value_datetime "
            + "    FROM  "
            + "        (  "
            + "        SELECT earliest_date.patient_id, MIN(earliest_date.min_date) AS mindate "
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
            + "                    AND pp.date_enrolled <= DATE_SUB(:onOrBefore,INTERVAL 1 MONTH) "
            + "                    AND pg.program_id = %d  "
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
            + "                    AND e.encounter_type = %d  "
            + "                    AND e.location_id = :location  "
            + "                    AND o.concept_id = %d "
            + "                    AND o.value_datetime <= DATE_SUB(:onOrBefore,INTERVAL 1 MONTH) "
            + "                GROUP BY p.patient_id  "
            + "            ) AS earliest_date  "
            + "        GROUP BY earliest_date.patient_id  "
            + "        ) AS final   "
            + "    WHERE final.mindate <= DATE_SUB(:onOrBefore,INTERVAL 1 MONTH) ";

    def.setSql(
        String.format(
            sql,
            hivMetadata.getHIVCareProgram().getId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getPreArtStartDate().getConceptId()));

    Map<String, Object> params = new HashMap<>();
    params.put("location", context.getFromCache("location"));
    params.put("onOrBefore", context.getFromCache("onOrBefore"));
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, params, null, context);
  }
}
