/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.Date;
import java.util.List;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiObsDefinition;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = JembiObsDefinition.class, order = 50)
public class JembiObsDefinitionEvaluator implements PatientDataEvaluator {

  @Autowired private EvaluationService evaluationService;

  @Override
  public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
      throws EvaluationException {
    JembiObsDefinition def = (JembiObsDefinition) definition;
    EvaluatedPatientData c = new EvaluatedPatientData(def, context);

    if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
      return c;
    }

    HqlQueryBuilder q = new HqlQueryBuilder();
    q.select("obs.person.personId", "obs");
    q.from(Obs.class, "obs");
    q.wherePatientIn("obs.person.personId", context);
    q.whereEqual("obs.concept", def.getQuestion());
    if (def.getAnswer() != null) {
      q.whereEqual("obs.valueCoded", def.getAnswer());
    }
    if (def.getEncounterTypeList() != null) {
      q.whereIn("obs.encounter.encounterType", def.getEncounterTypeList());
    }
    q.whereEqual("obs.location", def.getLocation());
    q.whereEqual("obs.voided", false);
    final Date valueDateTimeOnOrAfter = def.getValueDateTimeOnOrAfter();
    if (valueDateTimeOnOrAfter != null) {
      q.whereGreaterOrEqualTo("obs.valueDatetime", valueDateTimeOnOrAfter);
    }
    Date valueDateTimeOnOrBefore = def.getValueDateTimeOnOrBefore();
    if (valueDateTimeOnOrBefore != null) {
      q.whereLessOrEqualTo("obs.valueDatetime", valueDateTimeOnOrBefore);
    }
    if (def.isSortByDatetime()) {
      q.orderAsc("obs.obsDatetime");
    } else {
      q.orderAsc("obs.valueDatetime");
    }

    List<Object[]> queryResult = evaluationService.evaluateToList(q, context);

    ListMap<Integer, Obs> patientToObs = new ListMap<Integer, Obs>();
    for (Object[] row : queryResult) {
      patientToObs.putInList((Integer) row[0], (Obs) row[1]);
    }

    for (Integer pId : patientToObs.keySet()) {
      List<Obs> observations = patientToObs.get(pId);
      Obs obs;
      if (def.isFirst()) {
        obs = observations.get(0);
      } else {
        // last
        obs = observations.get(observations.size() - 1);
      }
      c.addData(pId, obs);
    }

    return c;
  }
}
