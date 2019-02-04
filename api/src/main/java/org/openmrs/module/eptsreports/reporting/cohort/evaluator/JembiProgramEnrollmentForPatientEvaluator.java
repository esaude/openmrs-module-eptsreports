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

import java.util.List;
import org.openmrs.PatientProgram;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiProgramEnrollmentForPatientDefinition;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = JembiProgramEnrollmentForPatientDefinition.class, order = 50)
public class JembiProgramEnrollmentForPatientEvaluator implements PatientDataEvaluator {

  @Autowired private EvaluationService evaluationService;

  @Override
  public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
      throws EvaluationException {
    JembiProgramEnrollmentForPatientDefinition def =
        (JembiProgramEnrollmentForPatientDefinition) definition;
    EvaluatedPatientData c = new EvaluatedPatientData(def, context);

    if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
      return c;
    }

    HqlQueryBuilder q = new HqlQueryBuilder();
    q.select("patientProgram.patient.patientId", "patientProgram");
    q.from(PatientProgram.class, "patientProgram");
    q.wherePatientIn("patientProgram.patient.patientId", context);
    q.whereEqual("patientProgram.program", def.getProgram());
    q.whereEqual("patientProgram.location", def.getLocation());
    q.whereEqual("patientProgram.voided", false);
    q.orderAsc("patientProgram.dateEnrolled");

    List<Object[]> queryResult = evaluationService.evaluateToList(q, context);

    ListMap<Integer, PatientProgram> enrollmentsForPatients =
        new ListMap<Integer, PatientProgram>();
    for (Object[] row : queryResult) {
      enrollmentsForPatients.putInList((Integer) row[0], (PatientProgram) row[1]);
    }

    for (Integer pId : enrollmentsForPatients.keySet()) {
      List<PatientProgram> l = enrollmentsForPatients.get(pId);
      c.addData(pId, l.get(0));
    }

    return c;
  }
}
