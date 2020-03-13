package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.List;
import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiEncounterPatientDataDefinition;
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

@Handler(supports = JembiEncounterPatientDataDefinition.class, order = 50)
public class JembiEncounterPatientDataEvaluator implements PatientDataEvaluator {

  private @Autowired EvaluationService evaluationService;

  public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
      throws EvaluationException {

    JembiEncounterPatientDataDefinition def = (JembiEncounterPatientDataDefinition) definition;
    EvaluatedPatientData c = new EvaluatedPatientData(def, context);

    if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
      return c;
    }

    HqlQueryBuilder q = new HqlQueryBuilder();
    q.select("e.patient.patientId", "e");
    q.from(Encounter.class, "e");
    q.wherePatientIn("e.patient.patientId", context);
    q.whereIn("e.encounterType", def.getTypes());
    q.whereIn("e.location", def.getLocationList());
    q.whereGreaterOrEqualTo("e.encounterDatetime", def.getOnOrAfter());
    q.whereLess("e.encounterDatetime", def.getOnOrBefore());

    if (def.getOnlyInActiveVisit()) {
      q.whereNull("e.visit.stopDatetime");
    }

    if (def.getWhich() == TimeQualifier.LAST) {
      q.orderDesc("e.encounterDatetime");
    } else {
      q.orderAsc("e.encounterDatetime");
    }

    List<Object[]> queryResult = evaluationService.evaluateToList(q, context);

    ListMap<Integer, Encounter> encountersForPatients = new ListMap<Integer, Encounter>();
    for (Object[] row : queryResult) {
      encountersForPatients.putInList((Integer) row[0], (Encounter) row[1]);
    }

    for (Integer pId : encountersForPatients.keySet()) {
      List<Encounter> l = encountersForPatients.get(pId);
      if (def.getWhich() == TimeQualifier.LAST || def.getWhich() == TimeQualifier.FIRST) {
        c.addData(pId, l.get(0));
      } else {
        c.addData(pId, l);
      }
    }

    return c;
  }
}
