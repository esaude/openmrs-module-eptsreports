package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.List;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiEncounterObsDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EPTSMetadataDatetimeQualifier;
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

@Handler(supports = JembiEncounterObsDefinition.class, order = 50)
public class JembiEncounterObsDefinitionEvaluator implements PatientDataEvaluator {

  @Autowired private EvaluationService evaluationService;

  @Override
  public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context)
      throws EvaluationException {

    JembiEncounterObsDefinition def = (JembiEncounterObsDefinition) definition;
    EvaluatedPatientData evaluatedPatientData = new EvaluatedPatientData(def, context);

    HqlQueryBuilder hqb = buildQueryBuilder(def);

    List<Object[]> queryResult = evaluationService.evaluateToList(hqb, context);

    ListMap<Integer, Obs> listMap = new ListMap<>();
    for (Object[] row : queryResult) {
      listMap.putInList((Integer) row[0], (Obs) row[1]);
    }

    for (Integer pId : listMap.keySet()) {
      List<Obs> obss = listMap.get(pId);

      if (def.getTimeQualifier() == TimeQualifier.FIRST
          && def.getEptsMetadataDatetimeQualifier() == EPTSMetadataDatetimeQualifier.OBS_DATETIME) {
        Obs obs = obss.get(0);

        evaluatedPatientData.addData(pId, obs);
        continue;
      }

      if (def.getTimeQualifier() == TimeQualifier.LAST
          && def.getEptsMetadataDatetimeQualifier() == EPTSMetadataDatetimeQualifier.OBS_DATETIME) {
        Obs obs = obss.get(0);

        evaluatedPatientData.addData(pId, obs);
        continue;
      }

      if (def.getTimeQualifier() == TimeQualifier.FIRST
          && def.getEptsMetadataDatetimeQualifier()
              == EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME) {
        Obs obs = obss.get(0);

        evaluatedPatientData.addData(pId, obs);
        continue;
      }

      if (def.getTimeQualifier() == TimeQualifier.LAST
          && def.getEptsMetadataDatetimeQualifier()
              == EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME) {
        Obs obs = obss.get(0);

        evaluatedPatientData.addData(pId, obs);
        continue;
      }

      if (def.getTimeQualifier() == TimeQualifier.LAST
          && def.getEptsMetadataDatetimeQualifier()
              == EPTSMetadataDatetimeQualifier.VALUE_DATETIME) {
        Obs obs = obss.get(0);

        evaluatedPatientData.addData(pId, obs);
        continue;
      }

      if (def.getTimeQualifier() == TimeQualifier.FIRST
          && def.getEptsMetadataDatetimeQualifier()
              == EPTSMetadataDatetimeQualifier.VALUE_DATETIME) {
        Obs obs = obss.get(0);

        evaluatedPatientData.addData(pId, obs);
        continue;
      }

      if (def.getTimeQualifier() == TimeQualifier.ANY) {

        evaluatedPatientData.addData(pId, obss);
      }
    }

    return evaluatedPatientData;
  }

  private HqlQueryBuilder buildQueryBuilder(JembiEncounterObsDefinition def) {
    HqlQueryBuilder hqb = new HqlQueryBuilder();
    hqb.select("o.personId", "o");
    hqb.from(Obs.class, "o");
    hqb.whereEqual("o.encounter.encounterType", def.getEncounterType());
    hqb.whereEqual("o.encounter.location", def.getLocation());
    hqb.whereEqual("o.concept", def.getQuestion());

    // onOrAfter
    if (def.getOnOrAfter() != null
        && def.getEptsMetadataDatetimeQualifier()
            == EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME) {
      hqb.whereGreaterOrEqualTo("o.encounter.encounterDatetime", def.getOnOrAfter());
    }
    if (def.getOnOrAfter() != null
        && def.getEptsMetadataDatetimeQualifier() == EPTSMetadataDatetimeQualifier.OBS_DATETIME) {
      hqb.whereGreaterOrEqualTo("o.obsDatetime", def.getOnOrAfter());
    }
    if (def.getOnOrAfter() != null
        && def.getEptsMetadataDatetimeQualifier() == EPTSMetadataDatetimeQualifier.VALUE_DATETIME) {
      hqb.whereGreaterOrEqualTo("o.valueDatetime", def.getOnOrAfter());
    }
    // onOrBefore
    if (def.getOnOrBefore() != null
        && def.getEptsMetadataDatetimeQualifier()
            == EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME) {
      hqb.whereLessOrEqualTo("o.encounter.encounterDatetime", def.getOnOrBefore());
    }
    if (def.getOnOrBefore() != null
        && def.getEptsMetadataDatetimeQualifier() == EPTSMetadataDatetimeQualifier.OBS_DATETIME) {
      hqb.whereLessOrEqualTo("o.obsDatetime", def.getOnOrBefore());
    }
    if (def.getOnOrBefore() != null
        && def.getEptsMetadataDatetimeQualifier() == EPTSMetadataDatetimeQualifier.VALUE_DATETIME) {
      hqb.whereLessOrEqualTo("o.valueDatetime", def.getOnOrBefore());
    }
    //  value coded
    if (def.getAnswers() != null && !def.getAnswers().isEmpty()) {
      hqb.whereIn("o.valueCoded", def.getAnswers());
    }
    // sorting by date
    if (def.getTimeQualifier() == TimeQualifier.FIRST
        && def.getEptsMetadataDatetimeQualifier() == EPTSMetadataDatetimeQualifier.VALUE_DATETIME) {
      hqb.orderAsc("o.valueDatetime");
    }
    if (def.getTimeQualifier() == TimeQualifier.FIRST
        && def.getEptsMetadataDatetimeQualifier() == EPTSMetadataDatetimeQualifier.OBS_DATETIME) {
      hqb.orderAsc("o.obsDatetime");
    }
    if (def.getTimeQualifier() == TimeQualifier.FIRST
        && def.getEptsMetadataDatetimeQualifier()
            == EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME) {
      hqb.orderAsc("o.encounter.encounterDatetime");
    }
    if (def.getTimeQualifier() == TimeQualifier.LAST
        && def.getEptsMetadataDatetimeQualifier() == EPTSMetadataDatetimeQualifier.VALUE_DATETIME) {
      hqb.orderDesc("o.valueDatetime");
    }
    if (def.getTimeQualifier() == TimeQualifier.LAST
        && def.getEptsMetadataDatetimeQualifier() == EPTSMetadataDatetimeQualifier.OBS_DATETIME) {
      hqb.orderDesc("o.obsDatetime");
    }
    if (def.getTimeQualifier() == TimeQualifier.LAST
        && def.getEptsMetadataDatetimeQualifier()
            == EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME) {
      hqb.orderDesc("o.encounter.encounterDatetime");
    }

    return hqb;
  }
}
