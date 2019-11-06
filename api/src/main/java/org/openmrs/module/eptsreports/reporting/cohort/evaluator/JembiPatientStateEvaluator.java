package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.List;
import org.openmrs.PatientState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiPatientStateDefinition;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = JembiPatientStateDefinition.class, order = 50)
public class JembiPatientStateEvaluator implements PatientDataEvaluator {

  @Autowired private EvaluationService evaluationService;

  @Override
  public EvaluatedPatientData evaluate(
      PatientDataDefinition cohortDefinition, EvaluationContext context)
      throws EvaluationException {

    JembiPatientStateDefinition def = (JembiPatientStateDefinition) cohortDefinition;

    EvaluatedPatientData c = new EvaluatedPatientData(def, context);

    if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
      return c;
    }

    HqlQueryBuilder qb = new HqlQueryBuilder();
    qb.select("patientState.patientProgram.patient.patientId", "patientState");
    qb.from(PatientState.class, "patientState");
    qb.whereIn("patientState.state", def.getStates());
    qb.whereNotNull("patientState.startDate");
    qb.whereNull("patientState.endDate");
    if (def.getStartedOnOrAfter() != null) {
      qb.whereGreaterOrEqualTo("patientState.startDate", def.getStartedOnOrAfter());
    }
    qb.whereLessOrEqualTo("patientState.startDate", def.getStartedOnOrBefore());

    qb.whereEqual("patientState.patientProgram.location", def.getLocation());
    qb.whereEqual("patientState.voided", false);
    qb.whereEqual("patientState.patientProgram.voided", false);
    qb.whereEqual("patientState.patientProgram.patient.voided", false);
    qb.wherePatientIn("patientState.patientProgram.patient.patientId", context);

    if (def.getWhich() == TimeQualifier.LAST) {
      qb.orderDesc("patientState.startDate");
    } else {
      qb.orderAsc("patientState.startDate");
    }

    List<Object[]> queryResult = evaluationService.evaluateToList(qb, context);

    ListMap<Integer, PatientState> obsForPatients = new ListMap<>();
    for (Object[] row : queryResult) {
      obsForPatients.putInList((Integer) row[0], (PatientState) row[1]);
    }

    for (Integer pId : obsForPatients.keySet()) {
      List<PatientState> l = obsForPatients.get(pId);
      if (def.getWhich() == TimeQualifier.LAST || def.getWhich() == TimeQualifier.FIRST) {
        c.addData(pId, l.get(0));
      } else {
        c.addData(pId, l);
      }
    }
    return c;
  }
}
